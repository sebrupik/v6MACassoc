package v6macassoc;

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


public class V6MACassoc {
    private final String _class;
    private Properties rtrProps, sysProps;
    
    public V6MACassoc(String rtrTxt, String settingsTxt) {
        this._class = this.getClass().getName();
        
        try {
            //rtrProps = this.loadPropsFromFile(rtrTxt, true);
            sysProps = this.loadPropsFromFile(settingsTxt, false);
            
            assignSystemVariables();
            createDevices(rtrTxt);
        } catch (IOException ioe) { System.out.println(_class+"/"+ioe); }

    }
    
    private HashMap createDevices(String deviceList) {
        HashMap<String, Device> devices = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(deviceList));
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
                System.out.println(_class+"/loadProps - "+p1+ " not found!!!");
                tmp_prop = null;
            } else {
                tmp_prop.load(in);
            }
        } catch(IOException ioe) { System.out.println(_class+"/loadProps - "+ioe); }

        return tmp_prop;
    }

    public String getSysProperty(String arg) throws IOException {
        System.out.println(_class+"/getSysProperty - "+arg);
        String s;
        if(sysProps==null) {
            throw new IOException(_class+"/Props file not loaded!");
        } else {
            s = sysProps.getProperty(arg);
            if(s==null)
                throw new IOException(_class+"/Null value. Does field exist??");
            
            System.out.println(_class+"/getSysProperty - value is "+s);
            return s;
        }
    }
    
    public Object saveSysProperty(String key, String value) { return sysProps.setProperty(key, value); }

    public static void main(String[] args) {
        V6MACassoc v6MA = new V6MACassoc("routers.txt", "settings.properties");
    }
}