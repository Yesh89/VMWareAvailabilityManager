
import com.vmware.vim25.HostVMotionCompatibility;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSummary;
import com.vmware.vim25.mo.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Scanner;

public class MyVMController {
	String vcenter = "https://130.65.132.115/sdk";
	String uname = "administrator";
	String pwd = "12!@qwQW";
	String vmname ;

	
	/*function to power off a VM*/
	public void powerOff() throws MalformedURLException, RemoteException{
		ServiceInstance si = new ServiceInstance(new URL(vcenter), uname, pwd, true);
		Folder rootFolder = si.getRootFolder();
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter VM name that you would like to power off");
		vmname = keyboard.nextLine();
		
		ManagedEntity[] mes = rootFolder.getChildEntity();
		
		for(int i=0; i<mes.length; i++)
		{
			if(mes[i] instanceof Datacenter)
			{
				Datacenter dc = (Datacenter) mes[i];
				Folder vmFolder = dc.getVmFolder();
				ManagedEntity[] vms = vmFolder.getChildEntity();
				
				for(int j=0; j<vms.length; j++)
				{
					if(vms[j] instanceof VirtualMachine)
					{
						VirtualMachine vm = (VirtualMachine) vms[j];
						System.out.println((vm.getName()));
						VirtualMachineSummary summary = (VirtualMachineSummary) (vm.getSummary());
						System.out.println(summary.toString());
						VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
						
						
						if(vmri.getPowerState() == VirtualMachinePowerState.poweredOn
							&& vmname.equals(vm.getName()))
						{
							Task task = vm.powerOffVM_Task();
							task.waitForMe();
							System.out.println("vm:" + vm.getName() + " powered off.");
						}
					}
				}
			}
		}
		si.getServerConnection().logout();
	}
	
	/*Function to power on VM*/
	public void powerOn() throws MalformedURLException, RemoteException{
		ServiceInstance si = new ServiceInstance(new URL(vcenter), uname, pwd, true);
		Folder rootFolder = si.getRootFolder();
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter VM name that you would like to power on");
		vmname = keyboard.nextLine();
		
		ManagedEntity[] mes = rootFolder.getChildEntity();
		
		for(int i=0; i<mes.length; i++)
		{
			if(mes[i] instanceof Datacenter)
			{
				Datacenter dc = (Datacenter) mes[i];
				Folder vmFolder = dc.getVmFolder();
				ManagedEntity[] vms = vmFolder.getChildEntity();
				
				for(int j=0; j<vms.length; j++)
				{
					if(vms[j] instanceof VirtualMachine)
					{
						VirtualMachine vm = (VirtualMachine) vms[j];
						System.out.println((vm.getName()));
						VirtualMachineSummary summary = (VirtualMachineSummary) (vm.getSummary());
						System.out.println(summary.toString());
						VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
						if(vmri.getPowerState() == VirtualMachinePowerState.poweredOff
							&& vmname.equals(vm.getName()))
						{
							Task task = vm.powerOnVM_Task(null);
							task.waitForMe();
							System.out.println("vm:" + vm.getName() + " powered on.");
						}
					}
				}
			}
		}
		si.getServerConnection().logout();

		
	}
	
	/*Function to clone the VM*/
	public void cloneVM() throws MalformedURLException, RemoteException{
		ServiceInstance si = new ServiceInstance(new URL(vcenter), uname, pwd, true);
		Folder rootFolder = si.getRootFolder();
		Scanner keyboard = new Scanner(System.in);
		
        String cloneName, power_option, template_option;
        boolean power, template;
        
		System.out.println("Enter VM name that you would like to clone");
		vmname = keyboard.nextLine();
        
		System.out.print("Please enter name of the clone : ");
        cloneName = keyboard.nextLine();
        System.out.print("Would you like to power on the clone VM?  yes/ no");
        power_option = keyboard.nextLine();
        power = "yes".equalsIgnoreCase(power_option);
        System.out.print("Would you like to set template? yes/no ");
        template_option = keyboard.nextLine();
        template = "yes".equalsIgnoreCase(template_option);
        
        VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vmname);
        
        if (vm == null) {
            System.out.println("No VM " + vmname + " found");
            si.getServerConnection().logout();
            return;
        }

        VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
        cloneSpec.setLocation(new VirtualMachineRelocateSpec());
        cloneSpec.setPowerOn(power);
        cloneSpec.setTemplate(template);

        Task task = vm.cloneVM_Task((Folder) vm.getParent(), cloneName, cloneSpec);
        System.out.println("Launching the VM clone task. "
                + "Please wait ...");
        String status = task.waitForMe();
        if (status.equals(Task.SUCCESS)) {
            System.out.println("VM got cloned successfully.");
        } else {
            System.out.println("Failure -: VM cannot be cloned");
        }
        si.getServerConnection().logout();
	}
	
	
	
	/*Function to migrate VM*/
	public void migrateVM() throws MalformedURLException, RemoteException{
		ServiceInstance si = new ServiceInstance(new URL(vcenter), uname, pwd, true);
		Folder rootFolder = si.getRootFolder();
		Scanner keyboard = new Scanner(System.in);
		String targetHost;
        System.out.print("Please enter name of the VM to be migrated: ");
        vmname = keyboard.nextLine();
        
        System.out.print("Enter target host : ");
        targetHost = keyboard.nextLine();
        VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vmname);
        HostSystem newHost = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem", targetHost);
        ComputeResource cr = (ComputeResource) newHost.getParent();
     
        String[] checks = new String[]{"cpu", "software"};
        HostVMotionCompatibility[] vmcs
                = si.queryVMotionCompatibility(vm, new HostSystem[]{newHost}, checks);

        String[] comps = vmcs[0].getCompatibility();
        if (checks.length != comps.length) {
            System.out.println("CPU/software NOT compatible. Exit.");
            si.getServerConnection().logout();
            return;
        }

        Task task = vm.migrateVM_Task(cr.getResourcePool(), newHost,
                VirtualMachineMovePriority.highPriority,
                VirtualMachinePowerState.poweredOff);

        if (task.waitForMe().equals(Task.SUCCESS)) {
            System.out.println("VMotioned!");
        } else {
            System.out.println("VMotion failed!");
            TaskInfo info = task.getTaskInfo();
            System.out.println(info.getError().getFault());
        }
        si.getServerConnection().logout();
    }

        
        
}
