package v6macassoc;

import v6macassoc.objects.DBConnection;
import v6macassoc.objects.Device;
import v6macassoc.objects.DeviceRouterIOS;
import v6macassoc.objects.DeviceRouterIOSASA;
import v6macassoc.objects.DeviceRouterLinux;
import v6macassoc.objects.ShutdownThread; 

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Properties;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;

public final class V6MACassoc {
    private final String _CLASS;
    private Properties _psProps, _sysProps;
    private HashMap<String, Device> _devices;
    private DevicePollerEngine _dpEngine;
    private DatabaseEngine _dEngine;
    
    DBConnection dbcon;
    
    public V6MACassoc(String rtrTxt, String settingsTxt, String psTxt, boolean daemon) {
        this._CLASS = this.getClass().getName();
        
        Runtime.getRuntime().addShutdownHook(new ShutdownThread(this));
        
        try {
            _sysProps = this.loadPropsFromFile(settingsTxt, true);
            _psProps =  this.loadPropsFromFile(psTxt, false);
            
            assignSystemVariables();
            createDevices(rtrTxt);
            createDBConnection(this.getSysProperty("sql_server_ip_addr"), this.getSysProperty("sql_server_username"), this.getSysProperty("sql_server_password"));
            
            _dpEngine = new DevicePollerEngine(this, _devices);
            _dEngine = new DatabaseEngine(this, dbcon);
            
            if(daemon) {
                this.runAsDaemon(30);
            } else {
                _dEngine.execute();
                _dpEngine.execute();
                
                System.out.println("Will you shutdown?");
                _dpEngine.shutdown();
                _dEngine.shutdown();
                System.out.println("Now you have!");
            }
        } catch (IOException ioe) { System.out.println(_CLASS+"/"+ioe); }
    }
    
    private void runAsDaemon(int epoch) {
        while(true) {
            _dEngine.execute();
            _dpEngine.execute();
            try {
                Thread.sleep(epoch);
            } catch(InterruptedException ie) {
                System.out.println(_CLASS+"/runAsDaemon - exception");
                ie.printStackTrace();
            }
        }
    }
    
    public void shutdownThreads() {
        System.out.println(_CLASS+"/shutdownThreads - starting");
        _dpEngine.shutdown();
        _dEngine.shutdown();
    }
    
    private void createDBConnection(String ip, String u, String p) {
        dbcon = new DBConnection(ip, u, p, _psProps);
    }
    
    private HashMap createDevices(String deviceList) {
        _devices = new HashMap<>();
        try ( BufferedReader br = new BufferedReader(new FileReader(deviceList)) ) {
            String line;
            String[] items;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    items = line.split(";");
                    if(items[0].equals(v6macassoc.objects.DeviceRouterIOS._TYPE)) {
                        if((items.length-1)== v6macassoc.objects.DeviceRouterIOS._ARGUMENTS)
                            _devices.put(items[1], new DeviceRouterIOS(items));
                        else 
                            System.out.println(_CLASS+"/createDevices - you have the incorrect number of arguments ("+items.length+") to create a DeviceRouterIOS ("+v6macassoc.objects.DeviceRouterIOS._ARGUMENTS+")");
                    } else if(items[0].equals(v6macassoc.objects.DeviceRouterIOSASA.type)) {
                        if((items.length-1)== v6macassoc.objects.DeviceRouterIOSASA.arguments)
                            _devices.put(items[1], new DeviceRouterIOSASA(items));
                        else 
                            System.out.println(_CLASS+"/createDevices - you have the incorrect number of arguments ("+items.length+") to create a DeviceRouterIOSASA ("+v6macassoc.objects.DeviceRouterIOSASA.arguments+")");
                    } else if(items[0].equals(v6macassoc.objects.DeviceRouterLinux.type)) {
                        if((items.length-1)== v6macassoc.objects.DeviceRouterLinux.arguments)
                            _devices.put(items[1], new DeviceRouterLinux(items));
                        else 
                            System.out.println(_CLASS+"/createDevices - you have the incorrect number of arguments ("+items.length+") to create a DeviceRouterLinux ("+v6macassoc.objects.DeviceRouterLinux.arguments+")");
                    } 
                    //else if(items[0].equals("RADIUS"))
                }   
                      
            }
            br.close();
        } catch(java.io.FileNotFoundException fnfe) { System.out.println(_CLASS+"/createDevices - "+fnfe);
        } catch(java.io.IOException ioe) { System.out.println(_CLASS+"/createDevices - "+ioe); }
        
        return _devices;
    }
    
    private void assignSystemVariables() throws IOException {
        
    }
    
    private Properties loadPropsFromFile(String p1, boolean external) {
        System.out.println(_CLASS+"/loadPropsFromFile - attempting to load "+p1);
        Properties tmp_prop = new java.util.Properties();
        InputStream in = null;

        try {
            if(external) 
                in = new FileInputStream(p1);
            else
                in = this.getClass().getClassLoader().getResourceAsStream(p1);
                
            if (in == null) {
                System.out.println(_CLASS+"/loadPropsFromFile - "+p1+ " not found!!!");
                tmp_prop = null;
            } else {
                tmp_prop.load(in);
            }
        } catch(IOException ioe) { System.out.println(_CLASS+"/loadPropsFromFile - "+ioe); }

        return tmp_prop;
    }

    public String getSysProperty(String arg) throws IOException {
        System.out.println(_CLASS+"/getSysProperty - "+arg);
        String s;
        if(_sysProps==null) {
            throw new IOException(_CLASS+"/getSysProperty - Props file not loaded!");
        } else {
            s = _sysProps.getProperty(arg);
            if(s==null)
                throw new IOException(_CLASS+"/getSysProperty - Null value. Does field exist??");
            
            System.out.println(_CLASS+"/getSysProperty - value is "+s);
            return s;
        }
    }
    
    public Object saveSysProperty(String key, String value) { return _sysProps.setProperty(key, value); }

    public static void main(String[] args) {
        
        
        boolean d = false;
        //for(int i = 0; i < args.length; i++) {
        for(String arg : args) {
            if(arg.equals("--daemon"))
                d = true;
        }
        
        V6MACassoc v6MA = new V6MACassoc("routers.txt", "settings.properties", "v6macassoc/preparedstatements.properties", d);
    }
    
    public DatabaseEngine getDatabaseEngine() {
        return _dEngine;
    }
}