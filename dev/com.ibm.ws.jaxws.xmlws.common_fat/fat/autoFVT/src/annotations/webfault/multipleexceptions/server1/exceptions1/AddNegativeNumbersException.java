/*
 * @(#) 1.1 autoFVT/src/annotations/webfault/multipleexceptions/server1/exceptions1/AddNegativeNumbersException.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 12:06:15 [8/8/12 06:56:05]
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
package annotations.webfault.multipleexceptions.server1.exceptions1;

import javax.xml.ws.WebFault;

public class AddNegativeNumbersException extends Exception {
    String info;

    public AddNegativeNumbersException(String message, String detail) {
      super(message);
        this.info = detail + " this is package exceptions1...";
    }

    public String getFaultInfo() {
        return info;
    }
}
