/*
 * @(#) 1.1 autoFVT/src/annotations/webfault/multipleexceptions/server/exceptions2/MultNegativeNumbersException.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 12:06:11 [8/8/12 06:56:05]
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
 * 08/14/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.webfault.multipleexceptions.server.exceptions2;

import javax.xml.ws.WebFault;

@WebFault(name="MultiplyNumbersException", targetNamespace="http://server.multipleexceptions.webfault.annotations/")
public class MultNegativeNumbersException extends Exception {
    String info;

    public MultNegativeNumbersException(String message, String detail) {
      super(message);
        this.info = detail;
    }

    public String getFaultInfo() {
        return info;
    }
}
