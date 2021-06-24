package scopedrefs.server.app1.mod1;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.InitialContext;
import java.io.*;
import scopedrefs.generatedclients.app1.mod1.b1.*;
import scopedrefs.generatedclients.app1.mod1.b2.*;
@Stateless
@WebService()
public class B1 extends scopedrefs.server.app1.common.HelperMethods {
    public String identify(){ return "a1m1b1";}
    
    public String invokeapp1ModxEjb2(){
        // look up the ref to ejb2 that app b1 created with @webserviceref
        try{
             InitialContext ctx = new InitialContext();        
             B2Service b2  =                                                                                   
                    (B2Service)ctx.lookup("java:module/env/service/a1m1b2");
             
             if (b2==null){ return "lookup returned null";   }         
             return b2.getB2Port().identify();
        } catch (Exception e){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            e.printStackTrace(System.out);
            e.printStackTrace(ps);
            ps.close();
            return "Service impl got this exception: \n" + baos.toString();  // send stacktrace back to caller 
        }
    }
    
    @javax.annotation.PreDestroy
    void goaway(){
        System.out.println("scopedrefs.server.app1.mod1 predestroy method called");
        try{
            throw new Exception("heres an exception");
        } catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
    
    public String lookupAndInvokeAnyJndiRef(String jndiref){
        return super.lookupAndInvokeAnyJndiRef(jndiref);
    }
    
    // statically set http port for app2 endpoints.   
    public void setStaticApp2HttpPort(String port){
       app2HttpPort = port;
    }
    
}
