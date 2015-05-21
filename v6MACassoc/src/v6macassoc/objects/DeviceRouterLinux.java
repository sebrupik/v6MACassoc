package v6macassoc.objects;


import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import net.sf.expectit.Expect;
import static net.sf.expectit.matcher.Matchers.contains;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;


public class DeviceRouterLinux extends DeviceRouter {
    public static int arguments = 3;
    public static String type = "ROUTER_LINUX";
    public static String[] linux_command = new String[]{"ip -6 neigh"};
    
    private final String _CLASS;
    
    public DeviceRouterLinux(String[] items) {
        super(items);
        
        this._CLASS = this.getClass().getName();
    }
    
    @Override public void processCommand(ChannelShell channel, Expect expect, Session session) throws JSchException, IOException {
        System.out.println(_CLASS+"/processCommand - entering");
        try {
            channel.connect();
            //expect.expect(contains("$"));
            
            //BufferedReader buff = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            System.out.println(_CLASS+"/processCommand - entering command for-loop");
            for (String command : linux_command) {
                //expect.sendLine();
                expect.expect(contains("$"));
                
                expect.sendLine(command);
                //processInput(command, buff);
                //String x = processInput(command, channel);
            }
            System.out.println(_CLASS+"/processCommand - exited command for-loop");
            
            //expect.expect(contains("$"));
            expect.sendLine("exit");
            
            BufferedReader buff = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            neighAl = processInput(linux_command, buff);
            
        } finally {
            expect.close();
            channel.disconnect();
            session.disconnect();
        }
    } 
    
    @Override public ArrayList processInput(String[] cmd, BufferedReader buff) throws java.io.IOException {
        boolean[] mark = new boolean[]{false,false};
        String line;
        ipv6neigh ipv6n;
        ArrayList<ipv6neigh> al = new ArrayList<>();
        long timestamp = new Date().getTime();
        
        for(int i=0; i<cmd.length; i++) {
            while(true) {
                line = buff.readLine();
                if(line==null) {
                    System.out.println("big break!!! "+line);
                    break;
                }
                
                if(line.contains(cmd[i]))
                    mark[0] = true;
                
                if (i+1 != cmd.length) {
                    if(line.contains(cmd[i+1]))
                        mark[1] = true;
                }
                
                //System.out.println(mark[0]+" : "+mark[1]+" :: "+line);
                
                if(mark[0]==true & mark[1]==false) {
                    ipv6n = ipv6neighFactory.createObject(cmd[i], line, super.getIPAddr(), timestamp);
                    if(ipv6n !=null)
                        al.add(ipv6n);
                }
              
            }
        }
        System.out.println(_CLASS+"/processInput - return this many ipv6neighs : "+al.size());
        return al;
    }
}
