//
// @(#) 1.1 autoFVT/src/scopedrefs/client/CommonTestClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/3/10 15:38:33 [8/8/12 06:57:57]
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

package scopedrefs.client;
import scopedrefs.generatedclients.app1.mod1.b1.*;
import scopedrefs.generatedclients.app1.mod1.b2.*;

/**
 * This client contains methods to invoke services used in some of the tests.
 * 
 * An alternative is to use the "lookupandInvokeAnyJndiRef" method found
 * in server.mod3.B1 and possibly other places. 
 * @author btiffany
 *
 */
public class CommonTestClient {
    
  /****** app1 module 1 ************/  
    public static String app1mod1ejb2_invokeapp1mod1ejb1(){
        B2Service b2 = new B2Service();                              
        return b2.getB2Port().invokeapp1ModxEjb1();  // calls ejb1's identify method
    }
    
    public static String app1mod1ejb1_invokeapp1mod1ejb2(){
        B1Service b1 = new B1Service();
        return b1.getB1Port().invokeapp1ModxEjb2();  // calls ejb2's identify method
        
    }
    
    /****** app1 module 2 ************/    
    public static String app1mod2ejb2_invokeapp1mod2ejb1(){
        scopedrefs.generatedclients.app1.mod2.b2.B2Service b2 = new scopedrefs.generatedclients.app1.mod2.b2.B2Service();                              
        return b2.getB2Port().invokeapp1ModxEjb1();  // calls ejb1's identify method
    }
    
    public static String app1mod2ejb1_invokeapp1mod2ejb2(){
        scopedrefs.generatedclients.app1.mod2.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod2.b1.B1Service();
        return b1.getB1Port().invokeapp1ModxEjb2();  // calls ejb2's identify method
        
    }
    
    /****** app1 module 3 ************/ 
    
    public static String app1mod3ejb2_invokeapp1mod3ejb1(){  //ejb in a war invoking a servlet in a war  
        scopedrefs.generatedclients.app1.mod3.b2.B2Service b2 = new scopedrefs.generatedclients.app1.mod3.b2.B2Service();
        System.out.println("can we call b2 identify at all?: "+ b2.getB2Port().identify());
        return b2.getB2Port().invokeapp1ModxEjb1();  // calls ejb1's identify method
    }
    
    public static String app1mod3ejb1_invokeapp1mod3ejb2(){
        scopedrefs.generatedclients.app1.mod3.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod3.b1.B1Service();
        System.out.println("can we call b1 identify at all?: "+ b1.getB1Port().identify());
        return b1.getB1Port().invokeapp1ModxEjb2();  // calls ejb2's identify method
    }

}
