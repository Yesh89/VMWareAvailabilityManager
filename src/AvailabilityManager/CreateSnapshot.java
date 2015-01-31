package AvailabilityManager;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Arrays;


import com.vmware.vim25.mo.ServiceInstance;

public class CreateSnapshot implements Runnable{

	ServiceInstance si=null;
	int SleepTime = 0;
	
	CreateSnapshot(ServiceInstance si, int sleepTime) {
		
		this.si= si;
		this.SleepTime = 600000; //10 minute interval
		
	}

	@Override
	public void run() {
		while(true){
			
			try {
				AvailManager server = new AvailManager();
				
				server.createSnVM();
				server.createSnHost();
			} catch (Exception e) {
                System.out.println("Exception occured " + Arrays.toString(e.getStackTrace()));
            }
			
			try {
                Thread.sleep(600000);//sleep for 2 seconds
            } catch (Exception e) {
                System.out.println("Could not interrupt the thread to sleep" + e.getMessage());

            }
		}
	}
}
