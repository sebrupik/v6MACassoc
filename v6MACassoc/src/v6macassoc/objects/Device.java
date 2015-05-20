package v6macassoc.objects;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import net.sf.expectit.Expect;


public abstract class Device {
    private final String _class, ip_addr, device_type;
    private final int port;
    
    
    Date lastrun;
    String colon=":";
    
    public Device(String[] items) {
        this.device_type = items[0];
        this.ip_addr = items[1].substring(0, items[1].lastIndexOf(":"));
        this.port = Integer.parseInt(items[1].substring(items[1].lastIndexOf(":")+1, items[1].length()));
        
        
        this._class = this.getClass().getName();
        
        System.out.println(_class+"/device object created - "+ip_addr+" : "+port);
    }
    
    abstract void processCommand(ChannelShell channel, Expect expect, Session session) throws JSchException, IOException;
    abstract ArrayList processInput(String[] cmd, BufferedReader buff) throws java.io.IOException;
    
    public String getIPAddr() { return ip_addr; }
    public int getPort() { return port; }
    public String getDeviceType() { return device_type; }
}
