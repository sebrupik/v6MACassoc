package v6macassoc;

import v6macassoc.objects.Device;
import v6macassoc.objects.DeviceRouter;
import v6macassoc.objects.DeviceWorkerThread;

import java.util.HashMap;
import java.util.Iterator;

public class DevicePollerEngine extends ThreadEngine {
    private final String _CLASS;
    private final V6MACassoc _OWNER;
    private final HashMap _DEVICES;
    
    
    public DevicePollerEngine(V6MACassoc owner, HashMap devices) {
        super(2,4,10);
        this._OWNER = owner;
        this._DEVICES = devices;
        this._CLASS = this.getClass().getName();
        
        System.out.println(_CLASS+"/DevicePollerEngine - "+devices.size()+" devices to be polled");
    }
    
    @Override public void execute() {
        System.out.println(_CLASS+"/execute - entered");
        Device d;
        DeviceRouter dr; 
        Iterator it = _DEVICES.keySet().iterator();
        while (it.hasNext()) {
           d = (Device)_DEVICES.get(it.next());
           if ( d instanceof v6macassoc.objects.DeviceRouter) {
               dr = (DeviceRouter)d;
               executorPool.execute(new DeviceWorkerThread(_OWNER, dr));
           } else {
               System.out.println(_CLASS+"/execute - not sure what device type this was!!");
           }
        }
        System.out.println(_CLASS+"/execute - exited");
    }
}