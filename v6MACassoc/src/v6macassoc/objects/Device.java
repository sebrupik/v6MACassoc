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
    private final String _CLASS, _IP_ADDR, _DEVICE_TYPE;
    private final int _PORT;
    
    Date lastrun;
    String colon=":";
    
    public Device(String[] items) {
        this._DEVICE_TYPE = items[0];
        this._IP_ADDR = items[1].substring(0, items[1].lastIndexOf(":"));
        this._PORT = Integer.parseInt(items[1].substring(items[1].lastIndexOf(":")+1, items[1].length()));
        
        
        this._CLASS = this.getClass().getName();
        
        System.out.println(_CLASS+"/device object created - "+_IP_ADDR+" : "+_PORT);
    }
    
    abstract void processCommand(ChannelShell channel, Expect expect, Session session) throws JSchException, IOException;
    abstract ArrayList processInput(String[] cmd, BufferedReader buff) throws java.io.IOException;
    
    public String getIPAddr() { return _IP_ADDR; }
    public int getPort() { return _PORT; }
    public String getDeviceType() { return _DEVICE_TYPE; }
}
