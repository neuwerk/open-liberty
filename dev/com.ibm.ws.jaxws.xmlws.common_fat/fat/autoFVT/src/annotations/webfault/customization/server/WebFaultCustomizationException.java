/*
 * @(#) 1.5 autoFVT/src/annotations/webfault/customization/server/WebFaultCustomizationException.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/10/07 13:53:42 [8/8/12 06:55:08]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 08/25/2006  euzunca     LIDB3296.31.01     new file
 * 01/10/2007  btiffany                       use non-default anno params
 */
package annotations.webfault.customization.server;

import javax.xml.ws.WebFault;

/*
 * We're testing jsr224 sec 3.7, seeing if we can customize the 
 * local name and namespace of the exception.
 *  
 * The standard doesn't say anything about mutating the classname,
 * so we're not going to try that. 
 */

@WebFault(name="WebFaultRenamedException", 
		targetNamespace="http://server_renamed.customization.webfault.annotations/")
public class WebFaultCustomizationException extends Exception {
    String info;

    public WebFaultCustomizationException(String message, String detail) {
      super(message);
        this.info = detail;
    }

    public String getFaultInfo() {
        return info;
    }
}
