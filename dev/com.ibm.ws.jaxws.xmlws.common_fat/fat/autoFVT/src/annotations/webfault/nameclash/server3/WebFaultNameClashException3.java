/*
 * @(#) 1.3 autoFVT/src/annotations/webfault/nameclash/server3/WebFaultNameClashException3.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/5/07 14:48:53 [8/8/12 06:55:13]
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
 * 09/01/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.webfault.nameclash.server3;

import javax.xml.ws.WebFault;

@WebFault
public class WebFaultNameClashException3 extends Exception {
    String info;

    public WebFaultNameClashException3(String message, String detail) {
      super(message);
        this.info = detail;
    }

    public String getFaultInfo() {
        return info;
    }
}