package scopedrefs.server.app2.mod3;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.WebServiceRef;

@Stateless
@WebService
public class B2  extends scopedrefs.server.app2.common.HelperMethods {
    // declare a reference for the global  scope test.  It's unused here. 
    @WebServiceRef(name="java:global/env/service/a2m3b1_injection_from_a2m3b2", value=scopedrefs.generatedclients.app2.mod3.b1.B1Service.class )
    scopedrefs.generatedclients.app2.mod3.b1.B1Service b2ref;
    
    public String identify(){
          System.out.println("a2m3b2.identify is invoked");
          System.err.println("a2m3b2.identify is invoked");
          return "a2m3b2";
    }
    
    public String lookupAndInvokeAnyJndiRef(String jndiref){
        return super.lookupAndInvokeAnyJndiRef(jndiref);
    }
}
