/*
 * @(#) 1.1 autoFVT/src/annotations/partialwsdl/serverpartial1/AddNumbersImplPartial1.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 11:37:09 [8/8/12 06:56:01]
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

import javax.jws.WebService;

import annotations.partialwsdl.serverpartial1.AddNegativeNumbersException;

/*
 * This test method will verify that the server generates the wsdl correctly
 * when a partial wsdl is provided (JSR-224: 5.2.5.5 Application-specified PortType)
 * in the deployment package and the service throws a service-specific exception.
 * 
 * In this type of partial wsdl, the metadata document defines the portType only.
 * 
 */	

@WebService(wsdlLocation="WEB-INF/wsdl/AddNumbersImplPartial1Service.wsdl")
public class AddNumbersImplPartial1 {
	
	public int addTwoNumbers(int number1, int number2)
			throws AddNegativeNumbersException {
		if (number1 < 0 || number2 < 0) {
			System.out.println("Throwing AddNegativeNumbersException, because negative operand(s) detected...!");
			throw new AddNegativeNumbersException("Negative number submitted !! ", "Numbers: " + 
					                      number1 + ", " + number2);
		}
		return number1 + number2;
	}
}
