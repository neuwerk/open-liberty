/*
 * @(#) 1.6 autoFVT/src/annotations/webfault/nameclash/server2/WebFaultNameClash2.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 10/3/06 13:49:48 [8/8/12 06:54:50]
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

import javax.jws.WebMethod;
import javax.jws.WebService;


@WebService
public class WebFaultNameClash2 {
	
    /**
     * @param number1
     * @param number2
     * @return The result
     * @throws WebFaultNameClashException
     *             if any of the numbers to be multiplied is negative.
     */
	@WebMethod(operationName="forceNameClashing")
	public int multiplyNumbers(int number1, int number2)
			throws WebFaultNameClashException2 {
		if (number1 < 0 || number2 < 0) {
			throw new WebFaultNameClashException2(
					"Negative number submitted !! ", "Numbers: " + number1
							+ ", " + number2);
		} 
		return number1 * number2;
	}
}