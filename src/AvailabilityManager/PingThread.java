package AvailabilityManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Arrays;

import com.vmware.vim25.AlarmSetting;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.DuplicateName;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;


public class PingThread implements Runnable{
	ServiceInstance si=null;
	
	int PingTime = 0;
	
	PingThread (ServiceInstance si, int PingTime) throws RemoteException, IOException{
		this.si= si;
		
		this.PingTime = PingTime;
		
		
	}
	
	@Override
	public void run() {
		boolean pinging=false;
		boolean pingingHost=false;
		boolean revertVM = false;
		boolean revertHost = false;
		boolean isSetAlarm = false;
		while(true){
			try {
				pingingHost=pinging=revertVM=revertHost=isSetAlarm=false;
				Folder rootFolder=si.getRootFolder();
				ManagedEntity[] hosts = new InventoryNavigator(rootFolder).searchManagedEntities(
						new String[][] { {"HostSystem", "name" }, }, true);
				
				for (int i=0;i<hosts.length;i++){
					HostSystem Host = (HostSystem) hosts[i];
					ManagedEntity[] vms = Host.getVms();
					
					for (int j=0;j<vms.length;j++){
						VirtualMachine vm = (VirtualMachine) vms[j];
						/*Remove alarms*/
						removeAlarm(vms[j]); /*Passing a managed entity*/
						
						/*Create Alarm Definitions*/
						createAlarm(vms[j],i,j);
						
						Thread.sleep(2000);
						/*Check alarm status*/
						isSetAlarm = checkAlarm(vm);
						//System.out.println(isSetAlarm);
						
						if (isSetAlarm != false){
							System.out.println(vm.getName()+" is under maintenance.");
						}
						
						
						/* Ping the VMs*/
						
						
						if ((vm.getSummary().runtime.powerState.toString().equals("poweredOn")) && vm.getGuest().toolsRunningStatus.equals("guestToolsRunning") && (isSetAlarm != true)){
							
							pinging = PingTest(vm.getSummary().getGuest().getIpAddress());
						
						if (pinging == true){
							System.out.println(vm.getName()+" is up with | CPU : "+ vm.getSummary().quickStats.overallCpuUsage+" |Memory : "+vm.getSummary().quickStats.getPrivateMemory()+" MB |");
						}
						else {
							System.out.println(vm.getName()+" is not pinging");
							System.out.println("Checking the host "+ Host.getName() + "..." );
							pingingHost=PingTest(Host.getName()); /*Since name is same as the IP*/
									if (pingingHost == true){
										
										System.out.println(Host.getName()+" is pinging.");
										System.out.println("Recovering the VM "+ vm.getName()+" from its snapshot...");
										
										revertVM = revertSnVM(vm.getName());
										/*if (PingTest(vm.getName())){
											System.out.println(vm.getName()+" is pinging now.");
										}
										
										else {
											System.out.println(vm.getName()+" is not pinging even after snapshot reversion.");
										}*/
										
									}
									
									
									
									else {
										System.out.println("Host is not pinging.");
										revertHost = revertSnHost(Host.getName());
										if (revertHost == true){
											if (Host.getVms() != null){
												ManagedEntity[] poweronVM1 = Host.getVms();
												System.out.println("Powering on the VM(s)...");
												
												for (int k=0; k<poweronVM1.length; k++){
													VirtualMachine poweronVM = (VirtualMachine) poweronVM1[k];
													Task task2 = poweronVM.powerOnVM_Task(null);
													if (task2.waitForMe().equals(Task.SUCCESS)){
														System.out.println(poweronVM.getName()+" has been powered on");
													}
												}
											}
											
											else {
												System.out.println("No VMs on the host.");
											}
										}
										
										else {
											System.out.println("Host reversion failed.");
										}
									}
						}
						
						
						}
                        
					}
					
				}
				
				
				
				
				
			} catch (Exception e) {
                System.out.println("Exception occured " + Arrays.toString(e.getStackTrace()));
            }
			
			try {
                Thread.sleep(this.PingTime);
            } catch (Exception e) {
                System.out.println("Could not interrupt the thread to sleep" + e.getMessage());

            }
			
		}
	}
	public boolean PingTest(String vmIP) throws IOException{
		boolean pinging;
		String command;
		String PingResult = "";
		command = vmIP;
        command = "ping "+command;
        Runtime r = Runtime.getRuntime();
        Process p = r.exec(command);		/*Ping host ip executes from this line*/
        BufferedReader in = new BufferedReader(new
                InputStreamReader(p.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                	PingResult += inputLine;
                		}		
                	in.close();
                		if((PingResult.contains("Reply from") ) )
                		{
                			/*Pinging*/
                			
                		 pinging=true;
                		}
                		else	{	
                			/*Not Pinging*/
                			pinging=false;
                			
                		}
                		return pinging;
	}	
	
	
	public boolean revertSnVM(String vMachineName) throws IOException {
		Folder rootFolder = si.getRootFolder();
		boolean revertVM = false;
		ManagedEntity[] vms = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		for (int i=0; i<vms.length; i++){
			VirtualMachine vMachineSnapshot=(VirtualMachine) vms[i];
			//System.out.println(vMachineSnapshot.getName());
			//System.out.println(vMachineName);
			if ((vMachineSnapshot.getName().equals(vMachineName)) && vMachineSnapshot.getSummary().runtime.powerState.toString().equals("poweredOn")){
				Task snapShot=vMachineSnapshot.revertToCurrentSnapshot_Task(null);
				if (snapShot.waitForMe().equals(Task.SUCCESS)){
					System.out.println("Snapshot reverted.");
					Task poweron = vMachineSnapshot.powerOnVM_Task(null);
					poweron.waitForMe();
					revertVM = true;
					return revertVM;
				}
				else {
					System.out.println("Snapshot reversion failed...");
					System.out.println("Trying to reboot the VM...");
					Task revSnap = vMachineSnapshot.resetVM_Task(); /* Try to reset the VM if snapshot creation fails*/
					revSnap.waitForMe();
					return revertVM;
					}
				
			}
		}
		return revertVM;		
			
		
	
	}
	
	
	public boolean revertSnHost(String vMachineIP) throws IOException, InterruptedException {
		String subIP = vMachineIP.substring(7);
		boolean revertHost = false;
		ServiceInstance sih = new ServiceInstance(new URL("https://130.65.132.14/sdk"), "administrator", "12!@qwQW", true);
		Folder rootFolder = sih.getRootFolder();
		ManagedEntity[] hosts = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		for (int i=0; i<hosts.length; i++){
			VirtualMachine vHosts=(VirtualMachine) hosts[i];
			if (vHosts.getName().contains(subIP)){
				
				if (vHosts.getSummary().runtime.powerState.toString().equals("poweredOn")){
					
			
				//System.out.println("Here");
				Task snapShot=vHosts.revertToCurrentSnapshot_Task(null);
				
				if (snapShot.waitForMe().equals(Task.SUCCESS)){
					System.out.println("Snapshot reverted.");
					revertHost = true;
					
				}
					System.out.println("Waiting for the host to power on..");
					snapShot = vHosts.powerOnVM_Task(null); /* power on the host	*/
					Thread.sleep(120000);
					System.out.println("Powered on");
				}
			}	
		}
		
		
		
		return revertHost;
	}
	
	
    public void removeAlarm(ManagedEntity me) throws RemoteException {

        Alarm[] alarm = si.getAlarmManager().getAlarm(me);
        for (Alarm alarm1 : alarm) {
            alarm1.removeAlarm();
        }
    }
    
    
    public void createAlarm(ManagedEntity me,int i,int j) throws DuplicateName, RuntimeFault, RemoteException {
        AlarmManager alarmMgr = si.getAlarmManager();

        AlarmSpec Spec = new AlarmSpec();

        StateAlarmExpression expression  = createStateAlarmExpression();

        
        Spec.setExpression(expression);
        Spec.setName("VmPowerStateAlarm"+i+j);
        Spec.setDescription("Alarm by Yesh");
        Spec.setEnabled(true);

        AlarmSetting as = new AlarmSetting();
        as.setReportingFrequency(0); 
        as.setToleranceRange(0);

        Spec.setSetting(as);

        alarmMgr.createAlarm(me, Spec);

    }
    
    static StateAlarmExpression createStateAlarmExpression() {
        StateAlarmExpression expression
                = new StateAlarmExpression();
        expression.setType("VirtualMachine");
        expression.setStatePath("runtime.powerState");
        expression.setOperator(StateAlarmOperator.isEqual);
        expression.setRed("poweredOff");
        return expression;
    }
    
    public boolean checkAlarm(VirtualMachine vm) throws RemoteException {
    	boolean isSetAlarm = false;
    	 AlarmState[] alarmStates=vm.getTriggeredAlarmState();
		    if (alarmStates != null){
		    	//System.out.println(alarmStates[0].key);
		    	isSetAlarm = true;
		    	
		    }
    	return isSetAlarm;
    }
    
    
    
}

