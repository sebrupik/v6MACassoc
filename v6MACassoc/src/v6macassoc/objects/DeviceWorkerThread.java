package v6macassoc.objects;

import expect4j.Expect4j;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
       
import java.io.IOException;
import java.lang.InterruptedException;
import java.util.Hashtable;

public class DeviceWorkerThread implements Runnable {
    private Expect4j expect;
    private String username, password, host, command;
    private int port;
    public DeviceWorkerThread(String username, String password, String host, int port, String command) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.command = command;
        
       
    }
    @Override public void run() {
        System.out.println(Thread.currentThread().getName()+" Start. Command = "+command);
        processCommand();

        System.out.println(Thread.currentThread().getName()+" End.");
    }
    
   private void connectSSH() throws Exception, IOException, JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);

        Hashtable<String,String> config = new Hashtable<String,String>();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect(60000);
        ChannelShell channel = (ChannelShell) session.openChannel("shell");
        expect = new Expect4j(channel.getInputStream(), channel.getOutputStream());
        channel.connect();
    }

    private void processCommand() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override public String toString(){
        return this.command;
    }
}
