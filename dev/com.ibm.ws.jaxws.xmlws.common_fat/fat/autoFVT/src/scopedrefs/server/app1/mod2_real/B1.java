package scopedrefs.server.app1.mod2;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.xml.ws.WebServiceRef;

import java.io.*;
import scopedrefs.generatedclients.app1.mod2.b1.*;
import scopedrefs.generatedclients.app1.mod2.b2.*;
@Stateless
@WebService()
public class B1 extends scopedrefs.server.app1.common.HelperMethods {
    
    // declare a reference for the application scope test.  It's unused here. 
    @WebServiceRef(name="java:app/env/service/a1m2b2", value=scopedrefs.generatedclients.app1.mod2.b2.B2Service.class )
    scopedrefs.generatedclients.app1.mod2.b2.B2Service b2ref;
    
    public String identify(){ return "a1m2b1";}
    
    public String invokeapp1ModxEjb2(){
        // look up the ref to ejb2 that app b1 created with @webserviceref
        try{
             InitialContext ctx = new InitialContext();        
             B2Service b2  =                                                                                   
                    (B2Service)ctx.lookup("java:module/env/service/a1m2b2");
             
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
    
    public String lookupAndInvokeAnyJndiRef(String jndiref){
        return super.lookupAndInvokeAnyJndiRef(jndiref);
    }
    
    // statically set http port for app2 endpoints.  Only used when app2 is on a non-default server. 
    public void setStaticApp2HttpPort(String port){
       app2HttpPort = port;
    }
    
    
}
