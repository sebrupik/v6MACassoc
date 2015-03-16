package v6macassoc;

import v6macassoc.interfaces.RejectedExecutionHandlerImpl;
import v6macassoc.objects.Device;
import v6macassoc.objects.DeviceRouter;
import v6macassoc.objects.DeviceWorkerThread;


import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class DevicePollerEngine {
    private final String _class;
    private HashMap devices;
    RejectedExecutionHandlerImpl rejectionHandler;
    ThreadFactory threadFactory;
    ThreadPoolExecutor executorPool;
    
    
    public DevicePollerEngine(HashMap devices) {
        this.devices = devices;
        this._class = this.getClass().getName();
        
        System.out.println(_class+"/DevicePollerEngine - "+devices.size()+" devices to be polled");
        
        rejectionHandler = new RejectedExecutionHandlerImpl();
        threadFactory = Executors.defaultThreadFactory();
        executorPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), threadFactory, rejectionHandler);
    }
    
    public void execute() {
        System.out.println(_class+"/execute - entered");
        Iterator it = devices.keySet().iterator();
        while (it.hasNext()) {
           if ( devices.get(it.next()) instanceof v6macassoc.objects.DeviceRouter) {
               DeviceRouter dr = (DeviceRouter)devices.get(it.next());
               executorPool.execute(new DeviceWorkerThread(dr.getUsername(), dr.getPassword(), dr.getEnable(), dr.getIPAddr(), dr.getPort(), dr.getCommand() )); 
           }
           //Device dev = (Device)devices.get(it.next()); 
           //executorPool.execute(new DeviceWorkerThread(dev.getUsername(), dev.getPassword(), dev.getEnable(), dev.getIPAddr(), dev.getPort(), dev.getCommand() )); 
        }
        System.out.println(_class+"/execute - exited");
    }
    
    public void shutdown() {
        executorPool.shutdown();
    }
}
