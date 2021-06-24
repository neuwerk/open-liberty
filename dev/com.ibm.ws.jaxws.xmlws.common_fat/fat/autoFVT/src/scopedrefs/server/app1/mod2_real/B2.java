package scopedrefs.server.app1.mod2;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.xml.ws.WebServiceRef;
import scopedrefs.generatedclients.app1.mod2.b1.*;
import scopedrefs.generatedclients.app1.mod2.b2.*;
@Stateless
@WebService
public class B2 {
    
    //we're going to declare a ref to ourselves.  We won't use it here. 
    //Since it's using the module scope, it should be usable
    // by the b1 service, just the same as if we had entered the
    // info in ejb-jar.xml
    @WebServiceRef(name="java:module/env/service/a1m2b2", value=scopedrefs.generatedclients.app1.mod2.b2.B2Service.class )
    B2Service b2ref;
    
    public String identify(){ return "a1m2b2";}
    
    public String invokeapp1ModxEjb1(){
        // look up the ref to ejb2 out of ejb-jar.xml
        try{
             InitialContext ctx = new InitialContext();        
             B1Service b1  =                                                                                   
                    (B1Service)ctx.lookup("java:module/env/service/a1m2b1");
             
             if (b1==null){ return "lookup returned null";   } 
             return b1.getB1Port().identify();
        } catch (Exception e){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            e.printStackTrace(System.out);
            e.printStackTrace(ps);
            ps.close();
            return "Service impl got this exception: \n"+ baos.toString();  // send stacktrace back to caller 
        }
    }

}
