/*
 * @(#) 1.1 autoFVT/src/annotations/jaxwstooling/server/JaxwsTooling.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 11:27:36 [8/8/12 06:55:59]
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

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

@WebService
@BindingType(value=SOAPBinding.SOAP11HTTP_BINDING)
public class JaxwsTooling {
	
    /**
     * @param number1
     * @param number2
     * @return The result
     * @throws NegativeNumbersException
     *             if any of the numbers to be multiplied is negative.
     */
	public int addTwoNumbers(int number1, int number2)
			throws JaxwsToolingException {
		if (number1 < 0 || number2 < 0) {
			throw new JaxwsToolingException("Negative number submitted !! ", "Numbers: " + 
					                      number1 + ", " + number2);
		} else if (number1 < 0 && number2 < 0) {
			throw new WebServiceException("both numbers are negative");
		}
		return number1 + number2;
	}
}
