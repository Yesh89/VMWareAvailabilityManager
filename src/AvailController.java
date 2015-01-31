

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Scanner;

import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.ServiceInstance;


public class AvailController {
	public static void main(String[] args) throws IOException {
		String host;
		String vcenter = "https://130.65.132.115/sdk";
		String uname = "administrator";
		String pwd = "12!@qwQW";
		String vmname ;
		Boolean pinging = false;
		
		ServiceInstance si = new ServiceInstance(new URL(vcenter), uname, pwd, true);
		Folder rootFolder = si.getRootFolder();
		Scanner keyboard = new Scanner(System.in);
		

		AvailManager server = new AvailManager();
		int choice;
		System.out.println("Enter choice");
		choice = keyboard.nextInt();
		String pingvMachine;
		switch (choice) {
		case 1:
			System.out.println("Enter IP of the VM");
			
			pingvMachine = keyboard.next();
			pinging = server.pingVM(pingvMachine);
			System.out.println(pinging);
			break;
			
		case 2:
				server.getVMs();
			break;
			
		case 3:
				server.getHosts();
			break;
			
		case 4:
				server.pingAllVMs();
			break;
			
		case 5:
			server.alarmVM();
		break;
		
		case 6:
			server.createSnVM();
		break;
		
		case 7:
			server.revertSnVM("T15-VM03-Proj");
		break;
		
		case 8:
			server.createSnHost();
			break;
		
		
		case 9:
			server.revertSnHost("T15-vHost01-cum4_IP=.133.31");
			break;
		}
		
			
		
		
		
}
}

