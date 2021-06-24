/*
 * @(#) 1.1 autoFVT/src/annotations/partialwsdl/serverzero/AddNumbersImplZero.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 11:37:11 [8/8/12 06:56:02]
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
package annotations.partialwsdl.serverzero;

import javax.jws.WebService;

/*
 * This test method will verify that the server generates the wsdl correctly
 * when a zero-byte (empty) wsdl file is provided in the deployment package.
 * 
 */	


@WebService(wsdlLocation="WEB-INF/wsdl/AddNumbersImplZeroService.wsdl")
public class AddNumbersImplZero {
	
	public int addTwoNumbers(int number1, int number2) {
		return number1 + number2;
	}
}
