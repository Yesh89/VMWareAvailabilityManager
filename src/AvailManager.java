import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Scanner;

import com.vmware.vim25.AlarmDescription;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;


public class AvailManager {
	String vcenter = "https://130.65.132.115/sdk";
	String uname = "administrator";
	String pwd = "12!@qwQW";
	String vmname ;
	String hostip;
	String snapname;
	Scanner input = new Scanner(System.in);
	
	ServiceInstance si;
	private VirtualMachine vm;
	
	
	AvailManager(){
		
		
		try {
			//this.vmname = vmname;
			//this.vmname = "130.65.133.208";
			this.si = new ServiceInstance(new URL(vcenter), uname, pwd, true);
			Folder rootFolder = si.getRootFolder();
			this.vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("CPU", this.vmname);
			
			
		} catch (Exception e) {
			//System.out.println(e.toString());
		}
		/*System.out.println(this.vm+ " here");
		if (this.vm == null) {
			System.out.println(vmname + " not found");
			if (this.si != null){
				
			}
		}*/
		
	}
	
	public Boolean pingVM(String vmname1) throws IOException    {
		vmname = vmname1;
		Boolean pinging=false;
		Folder rootFolder=si.getRootFolder();
		
		ManagedEntity[] hosts = new InventoryNavigator(rootFolder).searchManagedEntities(
				new String[][] { {"HostSystem", "name" }, }, true);
		hostip=hosts[0].getName();
		
		String newHostUrl="https://"+hosts[0].getName()+"/sdk";
		ServiceInstance sitemp = new ServiceInstance(new URL(vcenter), uname, pwd, true);
		Folder rf = sitemp.getRootFolder();
		ManagedEntity[] vms = new InventoryNavigator(rf).searchManagedEntities(
				new String[][] { {"VirtualMachine", "name" }, }, true);
		
		for(int i=0; i<vms.length; i++)
		{
			if(vms[i].getName().equalsIgnoreCase(vmname))
			{
				hostip=hosts[0].getName();
				break;
			}
			else{
				hostip=hosts[1].getName();
			}
			
		}
		
		String pingResult = " ";
		
		String pingCmd = "ping " + this.vmname;

		
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(pingCmd);		/*Ping host ip executes from this line*/
		

		BufferedReader in = new BufferedReader(new
		InputStreamReader(p.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
		pingResult += inputLine;
		}		
		in.close();
		if((pingResult.contains("(100% loss)")) || (pingResult.contains("timed out")))
		{
			System.out.println("Destination unreachable");
		 pinging=false;
		}
		else
		{
			System.out.println("Pinging on the host: "+hostip);	
			pinging=true;
		}
		
		return pinging;

	}
	
	
	public ManagedEntity[] getVMs() throws IOException    {
		
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] vms = new InventoryNavigator(rootFolder).searchManagedEntities(
				new String[][] { {"VirtualMachine", "name" }, }, true);
	
		for(int i=0; i<vms.length; i++)
		{
			
			//System.out.println("vm["+i+"]=" + vms[i].getName());
			
			
		}
		return vms;
	}
	
	public ManagedEntity[] getHosts() throws IOException    {
		
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] hosts = new InventoryNavigator(rootFolder).searchManagedEntities(
				new String[][] { {"HostSystem", "name" }, }, true);
	
		for(int i=0; i<hosts.length; i++)
		{
			VirtualMachine vHost;
			//System.out.println("Host["+i+"]=" + hosts[i].getName());
			
			
		}
		
		return hosts;
	}

	public void pingAllVMs() throws IOException    {
		ManagedEntity[] vms=this.getVMs();
		ManagedEntity[] hosts=this.getHosts();
		Boolean pinging = false;
		for (int i=0; i < vms.length;i++){
			System.out.println(vms[i].getName());
			VirtualMachine vMachine = (VirtualMachine) vms[i];
			pinging = false;
			
			pinging = pingVM(vMachine.getGuest().getIpAddress());
			System.out.println(pinging);
			
			
			
			if (pinging == true){
				System.out.println(vMachine.getGuest().getIpAddress()+" :Success \n");
			}
			
			else {
				System.out.println(vMachine.getGuest().getIpAddress()+" :Failure \n");
			}
			
		}
		
	}
	
	public void alarmVM() throws IOException {
		vmname="T15-VM01-Clone_Test3";
		InventoryNavigator inv = new InventoryNavigator(si.getRootFolder());
		    VirtualMachine vm;
			try {
				vm = (VirtualMachine)inv.searchManagedEntity(
				        "VirtualMachine", vmname);
		   //System.out.println(vm);
		    if(vm==null)
		    {
		      System.out.println("Cannot find the VM " + vmname + "\nExisting...");
		     // si.getServerConnection().logout();
		      return;
		    }
		    
		    Alarm alarms[] = si.getAlarmManager().getAlarm(vm);
		    //AlarmState alarms[]=alarmMgr.getAlarmState(vm);
		    AlarmState[] alarmStates=null;
		    alarmStates=vm.getTriggeredAlarmState();
		    System.out.println(alarmStates[0].key);
		    //System.out.println(alarmStates.length);
		    	/*
		    	if (alarmStates[0] != null){
		    		System.out.println(alarmStates[0].key);
		    	}
		    	*/
		    	//change this
		    
		    	//System.out.println(alarms[i].getAlarmInfo().getExpression());
		    
		  
			} catch (InvalidProperty e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
		
	
	public void revertSnVM(String vMachineName) throws IOException {
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] vms = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		for (int i=0; i<vms.length; i++){
			VirtualMachine vMachineSnapshot=(VirtualMachine) vms[i];
			//System.out.println(vMachineSnapshot.getName());
			//System.out.println(vMachineName);
			if (vMachineSnapshot.getName().equals(vMachineName)){				
				Task snapShot=vMachineSnapshot.revertToCurrentSnapshot_Task(null);
				if (snapShot.waitForMe().equals(Task.SUCCESS)){
					System.out.println("Snapshot reverted for all VMs");
					vMachineSnapshot.powerOnVM_Task(null); /*power on the VM after snapshot creation*/
				}
				else {
					vMachineSnapshot.powerOnVM_Task(null); /* power on the VM even if snapshot creation process failed*/
				}
			}
			}		
			
		
	
	}
	
	public void createSnVM() throws RemoteException, MalformedURLException {
		Folder rootFolder=si.getRootFolder();
		ManagedEntity[] vms = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		
		for (int i=0; i<vms.length; i++){
			VirtualMachine vMachineSnapshot=(VirtualMachine) vms[i];
			
			if (vMachineSnapshot.getSummary().runtime.powerState.toString().equals("poweredOn")){
				Task snapShot=vMachineSnapshot.removeAllSnapshots_Task();
				if (snapShot.waitForMe().equals(Task.SUCCESS)){
					System.out.println("All snapshot removed");
				}
				
				snapShot = vMachineSnapshot.createSnapshot_Task("newSnShot", "A new snapshot", false, false);
				if (snapShot.waitForMe().equals(Task.SUCCESS)){
					System.out.println("Snapshot created for all powered on VM");
				}
			}		
			
		}
		
	}
	
	public void createSnHost()throws RemoteException, MalformedURLException {
		this.si = new ServiceInstance(new URL("https://130.65.132.14/sdk"), uname, pwd, true);
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] hosts = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		for (int i=0; i<hosts.length; i++){
			VirtualMachine vHosts=(VirtualMachine) hosts[i];
			if(vHosts.getName().contains("T15-vHost")){
				//System.out.println(vHosts.getName());
				if (vHosts.getSummary().runtime.powerState.toString().equals("poweredOn")){
					Task snapShot=vHosts.removeAllSnapshots_Task();
					if (snapShot.waitForMe().equals(Task.SUCCESS)){
						System.out.println("All snapshots removed");
					}
					snapShot = vHosts.createSnapshot_Task("newHostSnShot", "A new snapshot", false, false);
					if (snapShot.waitForMe().equals(Task.SUCCESS)){
						System.out.println("Snapshot created for all powered on VM");
					}
				}
			}
		}
	}
	
	
	public void revertSnHost(String vMachineName) throws IOException {
		this.si = new ServiceInstance(new URL("https://130.65.132.14/sdk"), uname, pwd, true);
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] hosts = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		for (int i=0; i<hosts.length; i++){
			VirtualMachine vHosts=(VirtualMachine) hosts[i];
			
			if (vHosts.getName().equals(vMachineName)){				
				Task snapShot=vHosts.revertToCurrentSnapshot_Task(null);
				if (snapShot.waitForMe().equals(Task.SUCCESS)){
					System.out.println("Snapshot reverted for all VMs");
					
				}
				
					vHosts.powerOnVM_Task(null); /* power on the host */			
			}
		}
		
		
	}
	
}