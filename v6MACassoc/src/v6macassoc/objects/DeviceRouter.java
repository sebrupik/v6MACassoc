package v6macassoc.objects;
    
import v6macassoc.interfaces.AuthenticatedDevice;

public abstract class DeviceRouter extends Device implements AuthenticatedDevice {
    
    private final String _class;
    private String username, password;
    
    public DeviceRouter(String[] items) {
        super(new String[]{items[0],items[1]});
        username = items[2];
        password = items[3];
        
        this._class = this.getClass().getName();
    }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}