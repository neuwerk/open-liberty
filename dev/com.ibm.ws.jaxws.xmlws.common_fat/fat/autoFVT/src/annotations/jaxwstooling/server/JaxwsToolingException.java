/*
 * @(#) 1.1 autoFVT/src/annotations/jaxwstooling/server/JaxwsToolingException.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 11:27:37 [8/8/12 06:55:59]
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
 * 09/12/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.jaxwstooling.server;

import javax.xml.ws.WebFault;

@WebFault
public class JaxwsToolingException extends Exception {
    String info;

    public JaxwsToolingException(String message, String detail) {
      super(message);
        this.info = detail;
    }

    public String getFaultInfo() {
        return info;
    }
}
