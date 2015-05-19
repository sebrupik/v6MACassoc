package v6macassoc;

import v6macassoc.objects.DBConnection;
import v6macassoc.objects.DBConThread;

import java.util.ArrayList;

public class DatabaseEngine extends ThreadEngine {
    private final String _class;
    V6MACassoc owner;
    DBConnection dbcon;
    DBConThread dbct;
    
    public DatabaseEngine(V6MACassoc owner, DBConnection dbcon) {
        super(1,1,10);
        this.owner = owner;
        this.dbcon = dbcon;
        this._class = this.getClass().getName();
        
        this.dbct = new DBConThread(dbcon); 
    }
    
    @Override public void execute() {
        executorPool.execute(dbct);
    }
    
    public void insertArrayList(String type, ArrayList al) {
        if(dbct!=null) 
            dbct.insertArrayList(type, al);
    }
}

