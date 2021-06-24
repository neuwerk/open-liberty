/*
 * @(#) 1.6 autoFVT/src/annotations/webfault/nameclash/server2/WebFaultNameClashException2.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 10/3/06 13:49:49 [8/8/12 06:54:50]
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
 * 06/16/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.webfault.nameclash.server2;

import javax.xml.ws.WebFault;

@WebFault(name="forceNameClashing", targetNamespace="http://server2.nameclash.webfault.annotations/")
public class WebFaultNameClashException2 extends Exception {
    String info;

    public WebFaultNameClashException2(String message, String detail) {
      super(message);
        this.info = detail;
    }

    public String getFaultInfo() {
        return info;
    }
}