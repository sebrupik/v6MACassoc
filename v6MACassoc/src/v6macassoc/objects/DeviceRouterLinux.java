/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v6macassoc.objects;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

public class DeviceRouterLinux extends DeviceRouter {
    public static int _ARGUMENTS = 4;
    public static String _ROUTER = "ROUTER_LINUX";
    public static String _LINUX_COMMAND = "ip -6 neigh";
    
    private final String _class;
    
    public DeviceRouterLinux(String[] items) {
        super(items);
        
        this._class = this.getClass().getName();
    }
    
    @Override public String processOutput(String cmd, String output) throws java.io.IOException {
        BufferedReader buff = new BufferedReader(new StringReader(output));
        String line;
        String[] split;
       
        ArrayList al = new ArrayList();
        if(cmd.equals(_LINUX_COMMAND)) {
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
}
