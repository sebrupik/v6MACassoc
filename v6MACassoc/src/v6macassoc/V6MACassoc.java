package v6macassoc;

import v6macassoc.objects.DBConnection;
import v6macassoc.objects.Device;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Properties;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public final class V6MACassoc {
    private final String _class;
    private Properties psProps, sysProps;
    private HashMap<String, Device> devices;
    private DevicePollerEngine dpEngine;
    
    DBConnection dbcon;
    
    public V6MACassoc(String rtrTxt, String settingsTxt, String psTxt, boolean daemon) {
        this._class = this.getClass().getName();
        
        try {
            sysProps = this.loadPropsFromFile(settingsTxt, true);
            psProps =  this.loadPropsFromFile(psTxt, false);
            
            assignSystemVariables();
            createDevices(rtrTxt);
            createDBConnection(this.getSysProperty("sql_server_ip_addr"), this.getSysProperty("sql_server_username"), this.getSysProperty("sql_server_password"));
            
            dpEngine = new DevicePollerEngine(devices);
            
            if(daemon) {
                this.runAsDaemon(30);
            } else {
                dpEngine.execute();
                dpEngine.shutdown();
            }
        } catch (IOException ioe) { System.out.println(_class+"/"+ioe); }
            
    }
    
    private void runAsDaemon(int epoch) {
        while(true) {
            dpEngine.execute();
            try {
                Thread.sleep(epoch);
            } catch(InterruptedException ie) {
                System.out.println(_class+"/runAsDaemon - exception");
                ie.printStackTrace();
            }
        }
    }
    
    private void createDBConnection(String ip, String u, String p) {
        dbcon = new DBConnection(ip, u, p, psProps);
    }
    
    private HashMap createDevices(String deviceList) {
        devices = new HashMap<>();
        try ( BufferedReader br = new BufferedReader(new FileReader(deviceList)) ) {
            String line;
            String[] items;
            while ((line = br.readLine()) != null) {
                items = line.split(";");
                devices.put(items[0], new Device(items));
            }
            br.close();
        } catch(java.io.FileNotFoundException fnfe) { System.out.println(_class+"/createDevices - "+fnfe);
        } catch(java.io.IOException ioe) { System.out.println(_class+"/createDevices - "+ioe); }
        
        return devices;
    }
    
    private void assignSystemVariables() throws IOException {
        
    }
    
    private Properties loadPropsFromFile(String p1, boolean external) {
        System.out.println(_class+"/loadPropsFromFile - attempting to load "+p1);
        Properties tmp_prop = new java.util.Properties();
        InputStream in = null;

        try {
            if(external) 
                in = new FileInputStream(p1);
            else
                in = this.getClass().getClassLoader().getResourceAsStream(p1);
                
            if (in == null) {
                System.out.println(_class+"/loadPropsFromFile - "+p1+ " not found!!!");
                tmp_prop = null;
            } else {
                tmp_prop.load(in);
            }
        } catch(IOException ioe) { System.out.println(_class+"/loadPropsFromFile - "+ioe); }

        return tmp_prop;
    }

    public String getSysProperty(String arg) throws IOException {
        System.out.println(_class+"/getSysProperty - "+arg);
        String s;
        if(sysProps==null) {
            throw new IOException(_class+"/getSysProperty - Props file not loaded!");
        } else {
            s = sysProps.getProperty(arg);
            if(s==null)
                throw new IOException(_class+"/getSysProperty - Null value. Does field exist??");
            
            System.out.println(_class+"/getSysProperty - value is "+s);
            return s;
        }
    }
    
    public Object saveSysProperty(String key, String value) { return sysProps.setProperty(key, value); }

    public static void main(String[] args) {
        boolean d = false;
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("--daemon"))
                d = true;
        }
        
        V6MACassoc v6MA = new V6MACassoc("routers.txt", "settings.properties", "v6macassoc/preparedstatements.properties", d);
    }
}