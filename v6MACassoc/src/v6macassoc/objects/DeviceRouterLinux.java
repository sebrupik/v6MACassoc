/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v6macassoc.objects;

import v6macassoc.objects.ipv6neigh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import net.sf.expectit.Expect;
import static net.sf.expectit.matcher.Matchers.contains;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;


public class DeviceRouterLinux extends DeviceRouter {
    public static int _ARGUMENTS = 4;
    public static String _TYPE = "ROUTER_LINUX";
    public static String[] _LINUX_COMMAND = new String[]{"ip -6 neigh"};
    
    private final String _class;
    
    public DeviceRouterLinux(String[] items) {
        super(items);
        
        this._class = this.getClass().getName();
    }
    
    @Override public void processCommand(ChannelShell channel, Expect expect, Session session) throws JSchException, IOException {
        try {
            channel.connect();
            expect.expect(contains("password:"));
            expect.sendLine(getPassword());
            expect.expect(contains("$"));
            
            
            
            //read the channel inputStream to see all the good stuff.
            //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            //readAll(bufferedReader, result);
            for (int i=0;i<_LINUX_COMMAND.length; i++) {
                expect.sendLine(_LINUX_COMMAND[i]);
                String x = processInput(_LINUX_COMMAND[i], channel);
            }
            expect.expect(contains("$"));
            expect.sendLine("exit");
        } finally {
            channel.disconnect();
            session.disconnect();
            expect.close();
        }
    } 
    
    @Override public String processInput(String cmd, ChannelShell channel) throws java.io.IOException {
        BufferedReader buff = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        String line;
        String[] split;
       
        ArrayList<ipv6neigh> al = new ArrayList<>();
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
