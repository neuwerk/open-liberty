/*
 * @(#) 1.1 autoFVT/src/annotations/webfault/multipleexceptions/server1/MultipleExceptionsImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 12:06:17 [8/8/12 06:56:05]
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
package annotations.webfault.multipleexceptions.server1;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

import annotations.webfault.multipleexceptions.server1.exceptions2.MultNegativeNumbersException;
import annotations.webfault.multipleexceptions.server1.*;

@WebService
@BindingType(value=SOAPBinding.SOAP11HTTP_BINDING)
public class MultipleExceptionsImpl {
	
    /**
     * @param number1
     * @param number2
     * @return The result
     * @throws AddNegativeNumbersException 
     * @throws NegativeNumbersException
     *             if any of the numbers to be multiplied is negative.
     */
	public int addTwoNumbers(int number1, int number2)
			throws annotations.webfault.multipleexceptions.server1.exceptions1.AddNegativeNumbersException, 
			       annotations.webfault.multipleexceptions.server1.exceptions2.AddNegativeNumbersException {
		if (number1 < 0 && number2 < 0) {
			throw new annotations.webfault.multipleexceptions.server1.exceptions1.
				AddNegativeNumbersException("Negative number submitted !! ", 
						"Numbers: " + number1 + ", " + number2);
		} else if (number1 < 0 || number2 < 0) {
			throw new annotations.webfault.multipleexceptions.server1.exceptions2.
				AddNegativeNumbersException("Negative number submitted !! ", 
					"Numbers: " + number1 + ", " + number2);
		}
		return number1 + number2;
	}

	public int multiplyTwoNumbers(int number1, int number2)
			throws MultNegativeNumbersException {
		if (number1 < 0 || number2 < 0) {
			throw new MultNegativeNumbersException("Negative number submitted !! ",
					"Numbers: " + number1 + ", " + number2);
		}
		return number1 * number2;
	}

}
