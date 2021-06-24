/*
 * @(#) 1.2 autoFVT/src/annotations/webmethod_g2/runtime/server/WebMethodLegacyCheck.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/4/10 17:06:20 [8/8/12 06:57:37]
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 03/10/2010  btiffany    595458.5           New File
 * ..
 * 08/04/2010  jtnguyen    664197             add tests for static and final methods
 */
 
package annotations.webmethod_g2.runtime.server;
import javax.jws.*;


@WebService
public class WebMethodLegacyCheck {
    
    @WebMethod()
    public String echoanno(String s){return s;}
    
    // under old jax-ws (default) behavior, legacywm=true, this unannotated method would not be exposed,
    // but under the new rules, it is exposed.
    //@WebMethod()
    public String echonoanno(String s){return s;}
    
    
    /*
     * add for story 13248
     * for Implicit SEI
     * static and final methods.  
     * Both without @WebMethod because it's not allowed in WAS V7 & V8.  Installation gives errors in SystemOut if we include @WebMethod for static or final 
     */
        
    public static String webMethodStatic(String s) {return s;}
        
    public final String webMethodFinal(String s){return s;} 

    //@WebMethod()
    //public static String webMethodAnnoStatic(String s) {return s;}
        
    //@WebMethod()       
    //public final String webMethodAnnoFinal(String s){return s;} 
        
}
