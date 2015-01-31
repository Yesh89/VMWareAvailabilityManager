

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Scanner;


public class VMSelection {
	public static void main(String[] args) throws MalformedURLException, RemoteException {
		MyVMController vm = new MyVMController();
		PingExample vm1 = new PingExample(); 
        Scanner input = new Scanner(System.in);
        int choice;
        String vmName;
        System.out.println("Welcome to VM Manager!!! Please choose from available options.\n");

        do {
            System.out.println("\n\n");
            System.out.println("Press 1 to power off VM.\n");
            System.out.println("Press 2 to power on VM.\n");
            System.out.println("Press 3 to clone a VM.\n");
            System.out.println("Press 4 to migrate a VM.\n");
            System.out.println("Press 5 to exit. \n");
            
            System.out.print("Please enter your choice from the above given options: ");
            choice = input.nextInt();

            switch (choice) {

                case 1:
                    vm.powerOff();
                    break;
                    
                case 2:
                    vm.powerOn();
                    break;
                    
                case 3:
                    vm.cloneVM();
                    break;

                case 4:
                  vm.migrateVM();
                    break;
                
                case 6: 
                	vm1.ping("130.65.133.31");
                	break;
                case 5:
                    System.out.print("Thank you for using VM Controller.\nGoodbye!!! ");
                    break;

                default:
                    System.out.print("Wrong choice entered. Please enter correct value.");
                    break;
            }
        } while (choice != 5);
    }

}

