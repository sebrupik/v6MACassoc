package v6macassoc.objects;

public class ipv6neigh {
    private String ipv6address, macaddress, state, interf, datasource;
    private int age;
    
    public ipv6neigh(String ipv6address, int age, String macaddress, String state, String interf, String datasource) {
        this.ipv6address = ipv6address;
        this.age = age;
        this.macaddress = macaddress;
        this.state = state;
        this.interf = interf;
        this.datasource = datasource;
    }
    
    public String getIpv6address() { return this.ipv6address; }
    public int getAge() { return this.age; }
    public String macaddress() { return this.macaddress; }
    public String state() { return this.state; }
    public String interf() { return this.interf; }
    public String datasource() { return this.datasource; }
  
}
