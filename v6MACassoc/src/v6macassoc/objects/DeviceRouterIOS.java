/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v6macassoc.objects;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

public class DeviceRouterIOS extends DeviceRouter {
    public static int _ARGUMENTS = 4;
    public static String _ROUTER = "ROUTER_IOS";
    public static String _IOS_COMMAND = "sh ipv6 neigh";
    
    private final String _class;
    
    public DeviceRouterIOS(String[] items) {
        super(items);
        
        this._class = this.getClass().getName();
    }
    
    @Override public String processOutput(String cmd, String output) throws java.io.IOException {
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
        } 
        return "";
    } 
}
