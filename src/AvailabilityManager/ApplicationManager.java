package AvailabilityManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.mo.ServiceInstance;

public class ApplicationManager {
	
	public static void main( String[] args ) throws InterruptedException, IOException    {
	
		int SnShTime = Integer.parseInt(args[3]); 
		ServiceInstance si = new ServiceInstance(new URL(args[0]), args[1], args[2], true);
		
		CreateSnapshot SnSh = new CreateSnapshot(si, SnShTime);
		Thread SnapShTread = new Thread (SnSh);
		SnapShTread.start();
		
		
		
		int PingTime = 60000; //1 minute
		PingThread PingT = new PingThread(si,PingTime);
		Thread pingThd = new Thread (PingT);
		pingThd.start();
		
		SnapShTread.join();
		pingThd.join();
		
		
		
	}

}