package v6macassoc.objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Iterator;
import java.util.Random;


public class DBConThread implements Runnable {
    private final String _class;
    DBConnection dbcon;
    boolean running;
    Random rand;
    ConcurrentLinkedQueue<Object[]> input;
    PreparedStatement insertNeighPS;
    
    public DBConThread(DBConnection dbcon) {
        this.dbcon = dbcon;
        
        this.running = true;
        this.rand = new Random();
        this._class = this.getClass().getName();
        input = new ConcurrentLinkedQueue<>();
        
        insertNeighPS = dbcon.getPS("ps_insert_ipv6_neighbour_table");
    }
    
    @Override public void run() {
        while(running) {
            if(input.size()>0) {
                Object[] obj =(Object[]) input.poll();
                if(obj != null) {
                    switch (obj[0].toString()) {
                        case "ipv6neigh" : this.insertNeighs((ArrayList)obj[1]);
                    }
                }
            }
        }
    }
    
    public void insertArrayList(String type, ArrayList al) {
        input.add(new Object[]{type, al});
    }
    
    private void insertNeighs(ArrayList al) {
        int i = 0;
        int size = al.size();
        boolean printedAlready = false;
        boolean modulus = false;
        System.out.print("x/"+size+"[");
        
        ipv6neigh i6n;
        Iterator<ipv6neigh> it = al.iterator();
        
        while(it.hasNext()) {
            i6n = it.next();
            try {
                insertNeighPS.clearParameters();
                
                insertNeighPS.setString(1, i6n.getIpv6address());
                insertNeighPS.setInt(2, i6n.getAge());
                insertNeighPS.setString(3, i6n.getMacaddress());
                insertNeighPS.setString(4, i6n.getState());
                insertNeighPS.setString(5, i6n.getInterface());
                insertNeighPS.setString(6, i6n.getDatasource());
                insertNeighPS.setTimestamp(7, new java.sql.Timestamp(i6n.getTimestamp()));
                
                dbcon.executeUpdate(insertNeighPS);
                
            } catch(java.sql.SQLException sqle) { System.out.println(_class+"/insertNeighs - "+sqle); }
            
            i++;
            modulus = (((i*100)/size)%10==0);

            if(modulus & !printedAlready) {
                printedAlready = true;
                System.out.print(".");
            }
            if(!modulus) 
                printedAlready = false;
        }
        System.out.print("] 100%");
    }
}