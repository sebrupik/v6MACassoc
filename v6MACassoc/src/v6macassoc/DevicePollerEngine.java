package v6macassoc;

import v6macassoc.objects.Device;
import v6macassoc.objects.DeviceRouter;
import v6macassoc.objects.DeviceWorkerThread;

import java.util.HashMap;
import java.util.Iterator;

public class DevicePollerEngine extends ThreadEngine {
    private final String _class;
    private V6MACassoc owner;
    private HashMap devices;
    
    
    public DevicePollerEngine(V6MACassoc owner, HashMap devices) {
        super(2,4,10);
        this.owner = owner;
        this.devices = devices;
        this._class = this.getClass().getName();
        
        System.out.println(_class+"/DevicePollerEngine - "+devices.size()+" devices to be polled");
    }
    
    @Override public void execute() {
        System.out.println(_class+"/execute - entered");
        Device d;
        DeviceRouter dr; 
        Iterator it = devices.keySet().iterator();
        while (it.hasNext()) {
           d = (Device)devices.get(it.next());
           if ( d instanceof v6macassoc.objects.DeviceRouter) {
               dr = (DeviceRouter)d;
               executorPool.execute(new DeviceWorkerThread(owner, dr));
           } else {
               System.out.println(_class+"/execute - not sure what device type this was!!");
           }
        }
        System.out.println(_class+"/execute - exited");
    }
}