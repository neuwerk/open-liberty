/*
 * @(#) 1.1 autoFVT/src/annotations/partialwsdl/serverpartial1/AddNegativeNumbersException.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 11:37:08 [8/8/12 06:56:01]
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
package annotations.partialwsdl.serverpartial1;

import javax.xml.ws.WebFault;


@WebFault(name = "AddNegativeNumbersException", targetNamespace = "http://serverpartial1.partialwsdl.annotations/")
public class AddNegativeNumbersException
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private String faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public AddNegativeNumbersException(String message, String faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param cause
     * @param message
     * @param faultInfo
     */
    public AddNegativeNumbersException(String message, String faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: java.lang.String
     */
    public String getFaultInfo() {
        return faultInfo;
    }

}
