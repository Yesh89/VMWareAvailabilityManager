import org.tempuri.* ;

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




public class PingExample
{
   
    public static void ping( String host )
    {
       System.out.println( "Ping Host: " + host ) ;
       Service service = new Service();
       service.getPorts();
       //ServiceSoap port = service.getServiceSoap(); 
       //String result = port.pingHost( host ) ;
       //System.out.println( "Ping Result: " + result ) ;
    }
}
