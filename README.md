# VMWareAvailabilityManager
VMWareAvailabilityManager

This is a disaster recovery java application for VMware virtual machines. 
A disaster recovery system monitors the health of the virtual machines running at a site and if that site should fail, determines the site/host that is to be used to restart each failed virtual machine. 

An availability manager that monitors the liveness of the virtual machines (VM) running on any one of the hosts and restarts any virtual machines that fail on alternate, healthy hosts, using an earlier cached version of the VM. (In practice, such caching is done to enable recovering from complete site failures.) 

The application is designed to work like this:

For the purpose of this project, a VM can be considered to be live if it responds to pings, and dead if it stops responding for a configurable amount of time (say 1 minute ping heartbeat) .

This solution detects that a VM has failed, it checks if the vHost is active. If the vHost has failed, check if an alternative vHost is available. If a second live vHost is found, select that vHost for the VM and restart the VM on that vHost using a VM image format that is suitable for that vHost. In case if no other vHost is found, your solution should add another vHost to the vCenter, and restart the VM on that vHost. The image format conversions must be done ahead of time and stored for later use. Your solution must provide a mechanism for refreshing this image cache from the current running instance of the virtual machine, for example every 10 minute update. The conversion and refreshing must be automated. That means that you need to write a program to automate this refreshing of the clone update. 

When selecting a candidate host, your availability manager must take into account whether the host itself is live. (i.e., responds to pings).

Your solution can make use of unique capabilities of each of the hypervisors/management servers.

The solution will provide a mechanism for adding or removing a host from the set of hosts in the resource pool (collection of hosts (say 2 vHosts) being monitored), and for configuring the VMs that are to be monitored by it.

This solution provides following features: 

1. Gather statistics (such as CPU, I/O, network etc) for a VM and display in a text format 2. Refresh the backup cache update every 10 minute.
3. When a VM fails with ping heartbeat, then failover to another VM host/resource pool using VMDK image format (Cold migration) 4. If the vHost is not alive, try to make it alive. If even after a fixed number of attempts, the vHost does not come up, remove the vHost from the list. 5. Add a vHost to the vCenter if there exists only one vHost and that vHost is not alive. 6. Setup alarm on VM power off. If a VM is powered off by a user, then it should be able to prevent a failover from occurring. (A VM is not failed in this case by powered off by a user) 
