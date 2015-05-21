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
import v6macassoc.V6MACassoc;


public class DeviceWorkerThread implements Runnable {
    private final String _CLASS;
    V6MACassoc owner;
    private JSch _jsch;
    private Session _session;
    private Expect _expect;
    private String _username, _password, _enable, _host, _command;
    private int _port;
    
    private DeviceRouter _dev;
    private final String _TYPE;
    
    StringBuffer result;
    
    public DeviceWorkerThread(V6MACassoc owner, DeviceRouter dev) {
        this._dev = dev;
        this.owner = owner;
        this._CLASS = this.getClass().getName();
        
        _TYPE = this.getDeviceType(dev);
        
        _jsch = new JSch();
        result = new StringBuffer();
        System.out.println(_CLASS+"/DeviceWorkerThread - created "+dev.getIPAddr());
        
    }
    @Override public void run() {
        System.out.println(Thread.currentThread().getName()+" Start. running a thread for a "+_TYPE+", "+_dev.getIPAddr());
        try {
           ChannelShell c = connectSSH(); 
           System.out.println("about to run processCommand");
           _dev.processCommand(c, buildExpect(c), _session);
           System.out.println("finsihed processCommand");
           
           //now dump the command result to a DB...
           if(_TYPE.equals(v6macassoc.objects.DeviceRouterIOS._TYPE) |
              _TYPE.equals(v6macassoc.objects.DeviceRouterIOSASA.type) |
              _TYPE.equals(v6macassoc.objects.DeviceRouterLinux.type) ) {
               System.out.println("Lets insert some ipv6neighs");
              owner.getDatabaseEngine().insertArrayList("ipv6neigh",((DeviceRouter)_dev).getNeighborList() );
           }
        
        } catch(IOException | JSchException ex) { 
            System.out.println(_CLASS+"/run - "+ex);
        } 
        System.out.println(Thread.currentThread().getName()+" End.");
    }
    
   private ChannelShell connectSSH() throws JSchException {
        if(_session !=null) {
            if(_session.isConnected()) {
                System.out.println(_CLASS+"/connectSSH - session already connnected....better disconnect it.");
                _session.disconnect();
            }
        } 
        _session = _jsch.getSession(_dev.getUsername(), _dev.getIPAddr(), _dev.getPort());
        _session.setPassword(_dev.getPassword());
        

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        _session.setConfig(config);
        _session.connect(60000);
        
        return (ChannelShell)_session.openChannel("shell");
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
        else if ( dev instanceof v6macassoc.objects.DeviceRouterIOSASA) 
            return v6macassoc.objects.DeviceRouterIOSASA.type;
        else if ( dev instanceof v6macassoc.objects.DeviceRouterLinux) 
            return v6macassoc.objects.DeviceRouterLinux.type;
        else
            return "UNKNOWN";
    }
    
    @Override public String toString(){
        return this._command;
    }
}