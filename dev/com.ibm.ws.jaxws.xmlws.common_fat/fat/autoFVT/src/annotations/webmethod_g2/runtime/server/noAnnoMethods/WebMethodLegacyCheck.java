/*
 * @(#) 1.1 autoFVT/src/annotations/webmethod_g2/runtime/server/noAnnoMethods/WebMethodLegacyCheck.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/4/10 15:24:09 [8/8/12 06:58:51]
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
 * 08/04/2010  jtnguyen    664197             new file for static and final methods
 */
 
package annotations.webmethod_g2.runtime.server;
import javax.jws.*;

// This service is used for generating wsdl and artifacts for client side.  Not an implementation.
// no WebMethod annotations in this class

@WebService
public class WebMethodLegacyCheck {
    
    public String echonoanno(String s){return s;}    
        
    public static String webMethodStatic(String s) {return s;}
        
    public final String webMethodFinal(String s){return s;} 
}
