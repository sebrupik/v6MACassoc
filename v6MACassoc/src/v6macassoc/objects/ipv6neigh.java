package v6macassoc.objects;

public class ipv6neigh {
    private final String _class;
    private String ipv6address, macaddress, state, interf, datasource;
    private int age;
    
    public ipv6neigh(String ipv6address, int age, String macaddress, String state, String interf, String datasource) {
        this.ipv6address = ipv6address;
        this.age = age;
        this.macaddress = macaddress;
        this.state = state;
        this.interf = interf;
        this.datasource = datasource;
        
        this._class = this.getClass().getName();
        
        //System.out.println(_class+" - created "+ipv6address);
    }
    
    public String getIpv6address() { return this.ipv6address; }
    public int getAge() { return this.age; }
    public String getMacaddress() { return this.macaddress; }
    public String getState() { return this.state; }
    public String getInterface() { return this.interf; }
    public String getDatasource() { return this.datasource; }
  
}
