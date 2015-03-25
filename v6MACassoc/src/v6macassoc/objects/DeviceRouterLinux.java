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
            for (String command : _LINUX_COMMAND) {
                expect.sendLine(command);
                String x = processInput(command, channel);
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
       
        ArrayList<ipv6neigh> al = new ArrayList<>();
        
        while((line = buff.readLine()) != null) {
            al.add(ipv6neighFactory.createObject(cmd, line, super.getIPAddr()));
        }
        
        return "";
    }
}
