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


public class DeviceRouterIOSASA extends DeviceRouter {
    public static int _ARGUMENTS = 4;
    public static String _TYPE = "ROUTER_IOS_ASA";
    public static String[] _IOS_COMMAND = new String[]{"sh ipv6 neigh"};
    
    private final String enable;
    
    private final String _class;
    
    public DeviceRouterIOSASA(String[] items) {
        super(items);
        
        this.enable = items[4];
        this._class = this.getClass().getName();
    }
    
    @Override public void processCommand(ChannelShell channel, Expect expect, Session session) throws JSchException, IOException {
        try {
            channel.connect();
            expect.expect(contains(">"));
            expect.sendLine("en");
            expect.expect(contains("Password:"));
            expect.sendLine(enable);
            expect.expect(contains("#"));
            // should we retireve the current terminal length before setting it?
            expect.sendLine("terminal pager 0");
            //expect.expect(contains("#"));
            
            for(String command : _IOS_COMMAND) {
                expect.expect(contains("#"));
                expect.sendLine(command);
            }
            //expect.expect(contains("#"));
            //expect.sendLine("terminal pager 24");
            //expect.expect(contains("#"));
            expect.sendLine("exit");
            BufferedReader buff = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            neighAl = processInputNew(_IOS_COMMAND, buff);
            
            
        } finally {
            channel.disconnect();
            session.disconnect();
            expect.close();
        }
    }
    
    
    
    public ArrayList processInputNew(String[] cmd, BufferedReader buff) throws java.io.IOException {
        boolean[] mark = new boolean[]{false,false};
        String line;
        ipv6neigh ipv6n;
        ArrayList<ipv6neigh> al = new ArrayList<>();
        
        for(int i=0; i<cmd.length; i++) {
            while(true) {
                line = buff.readLine();
                if(line==null | line.trim().length()==0) {
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
                    if(!this.thingsThatArentNeighs(line, cmd[i])) {
                        ipv6n = ipv6neighFactory.createObject(cmd[i], line, super.getIPAddr());
                        if(ipv6n !=null) {
                            al.add(ipv6n);
                        }
                    }
                }
              
            }
        }
        return al;
    }
    
    private boolean thingsThatArentNeighs(String input, String command) {
        if(input.trim().equals(command))
            return true;
        else if(input.trim().startsWith("IPv6"))
            return true;
        else if(input.trim().startsWith("exit"))
            return true;
        
        return false;
    }
    
    @Override public String processInput(String cmd, ChannelShell channel) throws java.io.IOException {
        BufferedReader buff = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        String line;
       
        ArrayList<ipv6neigh> al = new ArrayList<>();
        
        while((line = buff.readLine()) != null) {
            al.add(ipv6neighFactory.createObject(cmd, line, super.getIPAddr()));
        }
        
        return "";
    } 
    
    public String getEnable() { return enable; }
}