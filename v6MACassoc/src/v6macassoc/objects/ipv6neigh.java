package v6macassoc.objects;

public class ipv6neigh {
    private final String _CLASS, _IPV6ADDRESS, _MACADDRESS, _STATE, _INTERF, _DATASOURCE;
    private final int _age;
    private final long _timestamp;
    
    public ipv6neigh(String ipv6address, int age, String macaddress, String state, String interf, String datasource, long timestamp) {
        this._IPV6ADDRESS = ipv6address;
        this._age = age;
        this._MACADDRESS = macaddress;
        this._STATE = state;
        this._INTERF = interf;
        this._DATASOURCE = datasource;
        this._timestamp = timestamp;
        
        this._CLASS = this.getClass().getName();
    }
    
    /**
     * 
     * @return      _IPV6ADDRESS
     * @see         String
     */
    public String getIpv6address() { return this._IPV6ADDRESS; }
    public int getAge() { return this._age; }
    public String getMacaddress() { return this._MACADDRESS; }
    public String getState() { return this._STATE; }
    public String getInterface() { return this._INTERF; }
    public String getDatasource() { return this._DATASOURCE; }
    public long getTimestamp() { return this._timestamp; }
}
