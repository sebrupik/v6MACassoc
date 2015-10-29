package v6macassoc.objects;

public class ipv6neigh {
    private final String _CLASS, _IPV6ADDRESS, _MACADDRESS, _STATE, _INTERF, _DATASOURCE;
    private final int _AGE;
    private final long _TIMESTAMP;
    
    public ipv6neigh(String ipv6address, int age, String macaddress, String state, String interf, String datasource, long timestamp) {
        this._IPV6ADDRESS = ipv6address;
        this._AGE = age;
        this._MACADDRESS = macaddress;
        this._STATE = state;
        this._INTERF = interf;
        this._DATASOURCE = datasource;
        this._TIMESTAMP = timestamp;
        
        this._CLASS = this.getClass().getName();
    }
    
    public String getIpv6address() { return this._IPV6ADDRESS; }
    public int getAge() { return this._AGE; }
    public String getMacaddress() { return this._MACADDRESS; }
    public String getState() { return this._STATE; }
    public String getInterface() { return this._INTERF; }
    public String getDatasource() { return this._DATASOURCE; }
    public long getTimestamp() { return this._TIMESTAMP; }
}
