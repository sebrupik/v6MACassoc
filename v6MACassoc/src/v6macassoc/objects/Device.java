package v6macassoc.objects;

import java.util.Date;

public class Device {
    String ip_addr;
    String device_type;
    String username;
    String password;
    Date lastrun;
    
    public Device(String[] items) {
        ip_addr = items[0];
        device_type = items[1];
        username = items[2];
        password = items[3];
        
        System.out.println("device object created - "+ip_addr);
    }
    
    
}
