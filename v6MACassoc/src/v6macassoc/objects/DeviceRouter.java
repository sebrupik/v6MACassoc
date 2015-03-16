package v6macassoc.objects;
    
import v6macassoc.interfaces.AuthenticatedDevice;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

public class DeviceRouter extends Device implements AuthenticatedDevice {
    public static int _ARGUMENTS = 4;
    public static String _ROUTER = "ROUTER";
    public static String _IOS_COMMAND = "sh ipv6 neigh";
    public static String _LINUX_COMMAND = "ip -6 neigh";
    
    private final String _class;
    private String username, password, enable;
    
    public DeviceRouter(String[] items) {
        super(new String[]{items[1],_ROUTER});
        username = items[2];
        password = items[3];
        enable = items[4];
        
        this._class = this.getClass().getName();
    }
    
    
    public String processOutput(String cmd, String output) throws java.io.IOException {
        BufferedReader buff = new BufferedReader(new StringReader(output));
        String line;
        String[] split;
       
        ArrayList al = new ArrayList();
        if(cmd.equals(_IOS_COMMAND)) {
            while((line = buff.readLine()) != null) {
                if(!line.startsWith("IPv6 Address")) {
                    al.add(new ipv6neigh(line.substring(0,42), 
                                         Integer.parseInt(line.substring(42,45)),
                                         line.substring(46,60),
                                         line.substring(62,67), 
                                         line.substring(68, line.length()),
                                         super.getIPAddr()));
                }
            }
        } else if(cmd.equals(_LINUX_COMMAND)) {
            while((line = buff.readLine()) != null) {
                split = line.split("\\s+");
                if(split[3].equals("INCOMPLETE")) {
                    al.add(new ipv6neigh(split[0], 0, "N/A", split[3], split[2], super.getIPAddr()));
                } else {
                    al.add(new ipv6neigh(split[0], 0, split[4], split[5], split[2], super.getIPAddr()));
                }
            }
        }
        return "";
    }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEnable() { return enable; }
    //public String getCommand() { return command; }
}
