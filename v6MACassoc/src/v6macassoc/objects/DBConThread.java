package v6macassoc.objects;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Random;


public class DBConThread implements Runnable {
    DBConnection dbcon;
    boolean running;
    Random rand;
    ConcurrentLinkedQueue input;
    
    public DBConThread(DBConnection dbcon) {
        this.dbcon = dbcon;
        
        this.running = true;
        this.rand = new Random();
        input = new ConcurrentLinkedQueue();
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
        
    }
}