/*
 * @(#) 1.7 autoFVT/src/annotations/webfault/checkexception/server/AddNumbersImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/17/07 16:26:19 [8/8/12 06:54:50]
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
 * 01/17/2006  btiffany    414850             use dynamic wsdl
 */
package annotations.webfault.checkexception.server;

import javax.jws.WebService;

@WebService(endpointInterface="annotations.webfault.checkexception.server.AddNumbers")
public class AddNumbersImpl implements AddNumbers{
	
 	public int addTwoNumbers(int number1, int number2)
			throws AddNegativeNumbersException {
		if (number1 < 0 || number2 < 0) {
        	System.out.println("DEBUG: number(s) are negative...");
            /*
			throw new AddNegativeNumbersException("Negative number submitted !! ", "Numbers: " + 
					                      number1 + ", " + number2);
            */                              
            throw new AddNegativeNumbersException("Negative number submitted !! ");
		}
    	System.out.println("DEBUG: no negative number found...");
		return number1 + number2;
	}
}
