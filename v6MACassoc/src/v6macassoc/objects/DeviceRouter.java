package v6macassoc.objects;
    
import java.util.ArrayList;
import v6macassoc.interfaces.AuthenticatedDevice;

public abstract class DeviceRouter extends Device implements AuthenticatedDevice {
    
    ArrayList<ipv6neigh> neighAl;
    
    private final String _CLASS, _USERNAME, _PASSWORD;
    
    public DeviceRouter(String[] items) {
        super(new String[]{items[0],items[1]});
        _USERNAME = items[2];
        _PASSWORD = items[3];
        
        this._CLASS = this.getClass().getName();
    }
    
    public ArrayList getNeighborList() {
        System.out.println(_CLASS+"/getNeighbourList - size: "+neighAl.size());
        return this.neighAl;
    }
    
    @Override public String getUsername() { return _USERNAME; }
    @Override public String getPassword() { return _PASSWORD; }
}