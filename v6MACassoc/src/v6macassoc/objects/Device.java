package v6macassoc.objects;

import java.util.Date;

public abstract class Device {
    private final String _class;
    private String ip_addr, device_type, username, password, enable, command;
    private final int port;
    
    
    Date lastrun;
    String colon=":";
    
    public Device(String[] items) {
        this.device_type = items[0];
        this.ip_addr = items[1];
        this.port = Integer.parseInt(items[1].substring(items[1].lastIndexOf(":")+1, items[1].length()));
        
        
        this._class = this.getClass().getName();
        
        System.out.println(_class+"/device object created - "+ip_addr+" : "+port);
    }
    
    public String getIPAddr() { return ip_addr; }
    public int getPort() { return port; }
    public String getDeviceType() { return device_type; }
}
