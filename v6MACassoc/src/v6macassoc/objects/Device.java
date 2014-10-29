package v6macassoc.objects;

import java.util.Date;

public class Device {
    
    public static String _ROUTER = "ROUTER";
    public static String _IOS_COMMAND = "sh ipv6 neigh";
    
    private final String _class;
    private String ip_addr, device_type, username, password, enable, command;
    private final int port;
    
    Date lastrun;
    
    public Device(String[] items) {
        ip_addr = items[0];
        port = Integer.parseInt(items[1]);
        device_type = items[2];
        username = items[3];
        password = items[4];
        
        if(device_type.equals(_ROUTER)) {
            enable = items[5];
            command = _IOS_COMMAND;
        }
        
        this._class = this.getClass().getName();
        
        System.out.println(_class+"/device object created - "+ip_addr);
    }
    
    public String getIPAddr() { return ip_addr; }
    public int getPort() { return port; }
    public String getDeviceType() { return device_type; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEnable() { return enable; }
    public String getCommand() { return command; }
}
