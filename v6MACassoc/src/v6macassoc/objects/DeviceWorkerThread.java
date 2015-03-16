package v6macassoc.objects;

import v6macassoc.objects.Device;

import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import static net.sf.expectit.filter.Filters.removeColors;
import static net.sf.expectit.filter.Filters.removeNonPrintable;
import static net.sf.expectit.matcher.Matchers.contains;
import static net.sf.expectit.matcher.Matchers.regexp;

//import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;
import java.io.IOException;
import java.lang.InterruptedException;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.StringBuilder;


public class DeviceWorkerThread implements Runnable {
    private final String _class;
    private JSch jsch;
    private Session session;
    private Expect expect;
    private String username, password, enable, host, command;
    private int port;
    
    private Device dev;
    private final String _type;
    
    StringBuffer result;
    
    public DeviceWorkerThread(Device dev) {
    //public DeviceWorkerThread(String username, String password, String enable, String host, int port, String command) {
        /*this.username = username;
        this.password = password;
        this.enable = enable;
        this.host = host;
        this.port = port;
        this.command = command;*/
        this.dev = dev;
        
        
        this._class = this.getClass().getName();
        
        if ( dev instanceof v6macassoc.objects.DeviceRouter) 
            _type = v6macassoc.objects.DeviceRouter._ROUTER;
        else
            _type = "UNKNOWN";
        
        jsch = new JSch();
        result = new StringBuffer();
        
    }
    @Override public void run() {
        System.out.println(Thread.currentThread().getName()+" Start. running a thread for a "+_type+", "+dev.getIPAddr());
        try {
           ChannelShell c = connectSSH();
           processCommand(c, buildExpect(c));
           
           //now dump the command result to a DB...
           
           
        } catch(IOException ioe) {
        } catch(JSchException jse) {
        }

        System.out.println(Thread.currentThread().getName()+" End.");
    }
    
   private ChannelShell connectSSH() throws JSchException {
        if(session !=null) {
            if(session.isConnected()) {
                System.out.println(_class+"/connectSSH - session already connnected....better disconnect it.");
                session.disconnect();
            }
        } 
        session = jsch.getSession(username, host, port);
        session.setPassword(password);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect(60000);
        
        return (ChannelShell)session.openChannel("shell");
    }
   
    private Expect buildExpect(ChannelShell channel) throws IOException {
        Expect expect = new ExpectBuilder()
                            .withOutput(channel.getOutputStream())
                            .withInputs(channel.getInputStream(), channel.getExtInputStream())
                            .withEchoInput(System.out)
                            .withEchoOutput(System.err)
                            .withInputFilters(removeColors(), removeNonPrintable())
                            .withExceptionOnFailure()
                            .build();
        
        return expect;
    }

    private void processCommand(ChannelShell channel, Expect expect) throws JSchException, IOException {
        try {
            channel.connect();
            expect.expect(contains(">"));
            expect.sendLine("en");
            expect.expect(contains("Password:"));
            expect.sendLine(enable);
            expect.expect(contains("#"));
            // should we retireve the current terminal length before setting it?
            expect.sendLine("terminal length 0");
            expect.expect(contains("#"));
            expect.sendLine(command);
            
            //read the channel inputStream to see all the good stuff.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            readAll(bufferedReader, result);
            
            
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
    
    private void readAll(BufferedReader buffR, StringBuffer stringB) throws IOException {
        String line;
        while( (line = buffR.readLine()) != null) {
           stringB.append(line);
        }
    }
    
    @Override public String toString(){
        return this.command;
    }
}