package v6macassoc.objects;

import v6macassoc.objects.Device;

import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import static net.sf.expectit.filter.Filters.removeColors;
import static net.sf.expectit.filter.Filters.removeNonPrintable;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;
import java.io.IOException;
import java.io.BufferedReader;


public class DeviceWorkerThread implements Runnable {
    private final String _class;
    private JSch jsch;
    private Session session;
    private Expect expect;
    private String username, password, enable, host, command;
    private int port;
    
    private DeviceRouter dev;
    private final String _type;
    
    StringBuffer result;
    
    public DeviceWorkerThread(DeviceRouter dev) {
        this.dev = dev;
    
        this._class = this.getClass().getName();
        
        _type = this.getDeviceType(dev);
        
        jsch = new JSch();
        result = new StringBuffer();
        System.out.println(_class+"/DeviceWorkerThread - created "+dev.getIPAddr());
        
    }
    @Override public void run() {
        System.out.println(Thread.currentThread().getName()+" Start. running a thread for a "+_type+", "+dev.getIPAddr());
        try {
           ChannelShell c = connectSSH(); 
           dev.processCommand(c, buildExpect(c), session);
           
           //now dump the command result to a DB...
           
           
        } catch(IOException ioe) {
            System.out.println(_class+"/run - "+ioe);
        } catch(JSchException jse) {
            System.out.println(_class+"/run - "+jse);
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
        session = jsch.getSession(dev.getUsername(), dev.getIPAddr(), dev.getPort());
        session.setPassword(dev.getPassword());
        

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
    
    private void readAll(BufferedReader buffR, StringBuffer stringB) throws IOException {
        String line;
        while( (line = buffR.readLine()) != null) {
           stringB.append(line);
        }
    }
    
    private String getDeviceType(Device dev) {
        if ( dev instanceof v6macassoc.objects.DeviceRouterIOS) 
            return v6macassoc.objects.DeviceRouterIOS._TYPE;
        else if ( dev instanceof v6macassoc.objects.DeviceRouterLinux) 
            return v6macassoc.objects.DeviceRouterLinux._TYPE;
        else
            return "UNKNOWN";
    }
    
    @Override public String toString(){
        return this.command;
    }
}