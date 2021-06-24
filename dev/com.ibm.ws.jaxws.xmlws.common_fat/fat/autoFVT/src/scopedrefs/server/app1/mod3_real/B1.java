//
// @(#) 1.2 autoFVT/src/scopedrefs/server/app1/mod3_real/B1.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/18/10 14:52:30 [8/8/12 06:57:58]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId      Defect          Description
// ----------------------------------------------------------------------------
// 10/14/10    btiffany                    new file

package scopedrefs.server.app1.mod3;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.xml.ws.WebServiceRef;

import java.io.*;
import scopedrefs.generatedclients.app1.mod3.b1.*;
import scopedrefs.generatedclients.app1.mod3.b2.*;

//@Stateless()   
@WebService()
public class B1 extends scopedrefs.server.app1.common.HelperMethods {
    // declare a reference for the application scope test.  It's unused here. 
    @WebServiceRef(name="java:app/env/service/a1m2b1", value=scopedrefs.generatedclients.app1.mod2.b1.B1Service.class )
    scopedrefs.generatedclients.app1.mod2.b1.B1Service b1ref;
    
    
    public String identify(){
        System.out.println("a1m3b1.identify called");
        System.err.println("a1m3b1.identify called");
        return "a1m3b1";
    }
    
    public String invokeapp1ModxEjb2(){
        System.out.println("app1mod3B1.invokeapp1ModxEjb2 called");
        System.err.println("app1mod3B1.invokeapp1ModxEjb2 called");
        // look up the ref to ejb2 that app b1 created with @webserviceref
        try{
             InitialContext ctx = new InitialContext();        
             B2Service b2  =                                                                                   
                    (B2Service)ctx.lookup("java:module/env/service/a1m3b2");
             
             if (b2==null){ return "lookup returned null";   }
             String result = b2.getB2Port().identify();
             System.out.println("a1m3b1's call to a1m3b2.identify returned: " +result);
             return result;
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
    
}
