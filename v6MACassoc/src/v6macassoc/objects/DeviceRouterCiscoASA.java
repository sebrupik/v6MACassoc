package v6macassoc.objects;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import net.sf.expectit.Expect;
import static net.sf.expectit.matcher.Matchers.contains;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class DeviceRouterCiscoASA extends DeviceRouterCisco {
    public static int arguments = 4;
    public static String type = "ROUTER_IOS_ASA";
    public static String[] ios_command = new String[]{"sh ipv6 neigh"};
    
    private final String _CLASS;
    
    public DeviceRouterCiscoASA(String[] items) {
        super(items);
        
        this._CLASS = this.getClass().getName();
    }
    
    
    @Override public void processCommand(ChannelShell channel, Expect expect, Session session) throws JSchException, IOException {
        try {
            channel.connect();
            expect.expect(contains(">"));
            expect.sendLine("en");
            expect.expect(contains("Password:"));
            expect.sendLine(ENABLE); 
            expect.expect(contains("#"));
            // should we retireve the current terminal length before setting it?
            expect.sendLine("terminal pager 0");
            //expect.expect(contains("#"));
            
            for(String command : ios_command) {
                expect.expect(contains("#"));
                expect.sendLine(command);
            }
            //expect.expect(contains("#"));
            //expect.sendLine("terminal pager 24");
            //expect.expect(contains("#"));
            expect.sendLine("exit");
            BufferedReader buff = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            neighAl = processInput(ios_command, buff);
            
            
        } finally {
            channel.disconnect();
            session.disconnect();
            expect.close();
        }
    }
}