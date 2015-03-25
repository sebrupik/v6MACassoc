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


public class DeviceRouterIOS extends DeviceRouter {
    public static int _ARGUMENTS = 4;
    public static String _TYPE = "ROUTER_IOS";
    public static String[] _IOS_COMMAND = new String[]{"sh ipv6 neigh"};
    
    private final String enable;
    
    private final String _class;
    
    public DeviceRouterIOS(String[] items) {
        super(items);
        
        this.enable = items[4];
        this._class = this.getClass().getName();
    }
    
    @Override public void processCommand(ChannelShell channel, Expect expect, Session session) throws JSchException, IOException {
        try {
            channel.connect();
            expect.expect(contains("password:"));
            expect.sendLine(getPassword());
            expect.expect(contains(">"));
            expect.sendLine("en");
            expect.expect(contains("Password:"));
            expect.sendLine(enable);
            expect.expect(contains("#"));
            // should we retireve the current terminal length before setting it?
            expect.sendLine("terminal length 0");
            expect.expect(contains("#"));
            
            for(String command : _IOS_COMMAND) {
                expect.sendLine(command);

                //read the channel inputStream to see all the good stuff.
                //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                //readAll(bufferedReader, result);
                String x = processInput(command, channel);
            
            }
            expect.expect(contains("#"));
            expect.sendLine("terminal length 24");
            expect.expect(contains("#"));
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
    
    public String getEnable() { return enable; }
}