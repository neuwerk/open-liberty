/*
 * @(#) 1.1 autoFVT/src/annotations/webfault/checkexception/server/GenericException.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 12:05:54 [8/8/12 06:56:04]
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
 * 06/26/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.webfault.checkexception.server;

import javax.xml.ws.WebFault;

public class GenericException extends Exception {
	private static final long serialVersionUID = 1L;
	String info;

    public GenericException(String message, String detail) {
      super(message);
        this.info = detail;
    }

    public String getFaultInfo1() {
        return info;
    }
    
}
