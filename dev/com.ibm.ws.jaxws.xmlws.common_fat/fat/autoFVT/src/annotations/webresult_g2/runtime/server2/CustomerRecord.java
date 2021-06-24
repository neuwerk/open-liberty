package annotations.webresult_g2.runtime.server2;
// a little data structure
public class CustomerRecord{
    // yes, it's bad practice to not use getters. 
    public String fname=null;
    public String lname=null;
    public String addr=null;
    
    public CustomerRecord(){}
    
    public CustomerRecord(String fname, String lname, String addr){
        this.fname = fname;
        this.lname = lname;
        this.addr  = addr;
    }
}
