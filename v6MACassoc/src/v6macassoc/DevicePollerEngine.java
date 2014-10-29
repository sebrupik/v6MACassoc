package v6macassoc;

import v6macassoc.interfaces.RejectedExecutionHandlerImpl;
import v6macassoc.objects.Device;
import v6macassoc.objects.DeviceWorkerThread;


import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class DevicePollerEngine {
    private HashMap devices;
    RejectedExecutionHandlerImpl rejectionHandler;
    ThreadFactory threadFactory;
    ThreadPoolExecutor executorPool;
    
    
    public DevicePollerEngine(HashMap devices) {
        this.devices = devices;
        
        rejectionHandler = new RejectedExecutionHandlerImpl();
        threadFactory = Executors.defaultThreadFactory();
        executorPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), threadFactory, rejectionHandler);
    }
    
    public void execute() {
        Iterator it = devices.entrySet().iterator();
        while (it.hasNext()) {
           Device dev = (Device)it.next();
           executorPool.execute(new DeviceWorkerThread(dev.getUsername(), dev.getPassword(), dev.getEnable(), dev.getIPAddr(), dev.getPort(), dev.getCommand() )); 
        }
    }
}
