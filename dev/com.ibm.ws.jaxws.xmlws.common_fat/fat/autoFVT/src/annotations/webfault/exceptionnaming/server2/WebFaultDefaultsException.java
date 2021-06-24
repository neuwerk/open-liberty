/*
 * @(#) 1.3 autoFVT/src/annotations/webfault/exceptionnaming/server2/WebFaultDefaultsException.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 10/3/06 13:57:12 [8/8/12 06:55:13]
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
 * 08/02/2006  euzunca     LIDB3296.31.01     new file
 * 
 */
package annotations.webfault.exceptionnaming.server2;

import javax.xml.ws.WebFault;

@WebFault
public class WebFaultDefaultsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	String info = null;

    public WebFaultDefaultsException(String message, String detail) {
      super(message);
        this.info = detail;
    }

    public String getFaultInfo() {
        return info;
    }
}
