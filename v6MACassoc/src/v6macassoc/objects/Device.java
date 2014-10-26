package v6macassoc.objects;

import java.util.Date;

public class Device {
    private final String _class;
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
        this._class = this.getClass().getName();
        
        System.out.println(_class+"/device object created - "+ip_addr);
    }
    
    
}
