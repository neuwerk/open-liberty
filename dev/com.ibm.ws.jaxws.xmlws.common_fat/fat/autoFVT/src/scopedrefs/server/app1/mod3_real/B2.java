package scopedrefs.server.app1.mod3;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.xml.ws.WebServiceRef;
import scopedrefs.generatedclients.app1.mod3.b1.*;
import scopedrefs.generatedclients.app1.mod3.b2.*;
@Stateless
@WebService
public class B2 {
    
    //we're going to declare a ref to ourselves.  We won't use it here. 
    //Since it's using the module scope, it should be usable
    // by the b1 service, just the same as if we had entered the
    // info in ejb-jar.xml
    @WebServiceRef(name="java:module/env/service/a1m3b2", value=scopedrefs.generatedclients.app1.mod3.b2.B2Service.class )
    B2Service b2ref;
    
    // let's do another for the application scope test 
    @WebServiceRef(name="java:app/env/service/a1m1b1", value=scopedrefs.generatedclients.app1.mod1.b1.B1Service.class )
    scopedrefs.generatedclients.app1.mod1.b1.B1Service b1ref;
    
    public String identify(){
          System.out.println("a1m3b2.identify is invoked");
          System.err.println("a1m3b2.identify is invoked");
          return "a1m3b2";
    }
    
    public String invokeapp1ModxEjb1(){
        System.out.println("app1mod3B2.invokeapp1ModxEjb1 called");
        System.err.println("app1mod3B2.invokeapp1ModxEjb1 called");
        // look up the ref to ejb1 out of web.xml
        try{
             InitialContext ctx = new InitialContext();        
             B1Service b1  =                                                                                   
                    (B1Service)ctx.lookup("java:module/env/service/a1m3b1");
             
             if (b1==null){ return "lookup returned null";   }
             String result = b1.getB1Port().identify();
             System.out.println("a1m3b1.identify returned: " + result);
             return result;
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
