package v6macassoc.objects;

import java.util.Date;

public abstract class Device {
    private final String _class;
    private String ip_addr, device_type, username, password, enable, command;
    private final int port;
    
    
    Date lastrun;
    String colon=":";
    
    public Device(String[] items) {
        this.ip_addr = items[0];
        this.port = Integer.parseInt(items[0].substring(items[0].lastIndexOf(":")+1, items[0].length()));
        this.device_type = items[1];
        
        this._class = this.getClass().getName();
        
        System.out.println(_class+"/device object created - "+ip_addr+" : "+port);
    }
    
    public String getIPAddr() { return ip_addr; }
    public int getPort() { return port; }
    public String getDeviceType() { return device_type; }
}
