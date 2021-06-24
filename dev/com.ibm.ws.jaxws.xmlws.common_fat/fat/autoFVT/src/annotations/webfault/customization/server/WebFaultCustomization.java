/*
 * @(#) 1.6 autoFVT/src/annotations/webfault/customization/server/WebFaultCustomization.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 2/16/07 10:22:09 [8/8/12 06:55:08]
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
 * 08/25/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.webfault.customization.server;

import javax.jws.WebService;

@WebService
public class WebFaultCustomization{
	
	public int addNumbers(int number1, int number2)
			throws WebFaultCustomizationException {
		if (number1 < 0 || number2 < 0) {
			throw new WebFaultCustomizationException("Negative number submitted !! ",
					"Numbers: " + number1 + ", " + number2);
		}
		return number1 + number2;
	}

	public int multiplyNumbers(int number1, int number2)
			throws WebFaultCustomizationException {
		if (number1 < 0 || number2 < 0) {
			throw new WebFaultCustomizationException("Negative number submitted !! ",
					"Numbers: " + number1 + ", " + number2);
		} 
		return number1 * number2;
	}
}
