package v6macassoc.objects;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Date;

public abstract class DeviceRouterCisco extends DeviceRouter {
    
    protected final String ENABLE;
    private final String _CLASS;
    
    public DeviceRouterCisco(String[] items) {
        super(items);
        
        this.ENABLE = items[4];
        this._CLASS = this.getClass().getName();
    }
    
    @Override public ArrayList processInput(String[] cmd, BufferedReader buff) throws java.io.IOException {
        boolean[] mark = new boolean[]{false,false};
        String line;
        ipv6neigh ipv6n;
        ArrayList<ipv6neigh> al = new ArrayList<>();
        long timestamp = new Date().getTime();
        
        for(int i=0; i<cmd.length; i++) {
            while(true) {
                line = buff.readLine();
                if(line==null | line.trim().length()==0) {
                    System.out.println("big break!!! "+line);
                    break;
                }
                
                if(line.contains(cmd[i]))
                    mark[0] = true;
                
                if (i+1 != cmd.length) {
                    if(line.contains(cmd[i+1]))
                        mark[1] = true;
                }
                
                //System.out.println(mark[0]+" : "+mark[1]+" :: "+line);
                
                if(mark[0]==true & mark[1]==false) {
                    if(!this.thingsThatArentNeighs(line, cmd[i])) {
                        ipv6n = ipv6neighFactory.createObject(cmd[i], line, super.getIPAddr(), timestamp);
                        if(ipv6n !=null) {
                            al.add(ipv6n);
                        }
                    }
                }
              
            }
        }
        return al;
    }
    
    /**
     * 
     * @param input   a single line from the command output
     * @param command the command used to generate the contents of the input 
     *                string
     * @return        did the input String contain anything that would indicate 
     *                a line from an IPv6 neighbour table was not being parsed
     * @see boolean
     */
    private boolean thingsThatArentNeighs(String input, String command) {
        if(input.trim().equals(command))
            return true;
        else if(input.trim().startsWith("IPv6"))
            return true;
        else if(input.trim().startsWith("exit"))
            return true;
        
        return false;
    } 
    
    public String getEnable() { return ENABLE; }
}