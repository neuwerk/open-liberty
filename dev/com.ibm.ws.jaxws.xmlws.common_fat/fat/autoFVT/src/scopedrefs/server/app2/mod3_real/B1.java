//
// @(#) 1.1 autoFVT/src/scopedrefs/server/app2/mod3_real/B1.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/18/10 14:50:41 [8/8/12 06:57:59]
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

package scopedrefs.server.app2.mod3;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.xml.ws.WebServiceRef;

import java.io.*;

   
@WebService()
public class B1 extends scopedrefs.server.app2.common.HelperMethods {
    
    //  declare a reference for the global  scope test.  It's unused here. 
    @WebServiceRef(name="java:global/env/service/a2m1b1_injected_from_a2m3b1", value=scopedrefs.generatedclients.app2.mod1.b1.B1Service.class )
    scopedrefs.generatedclients.app2.mod1.b1.B1Service b1ref;  
    
    
    
    public String identify(){
        System.out.println("a2m3b1.identify called");
        System.err.println("a2m3b1.identify called");
        return "a2m3b1";
    }
    
    public String lookupAndInvokeAnyJndiRef(String jndiref){
        return super.lookupAndInvokeAnyJndiRef(jndiref);
    }
    
}
