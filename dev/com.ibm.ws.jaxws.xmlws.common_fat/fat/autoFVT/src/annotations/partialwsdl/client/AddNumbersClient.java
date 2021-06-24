/*
 * @(#) 1.3 autoFVT/src/annotations/partialwsdl/client/AddNumbersClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/10/07 13:32:05 [8/8/12 06:55:59]
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
 * 11/17/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.partialwsdl.client;

import javax.xml.ws.WebServiceException;

import annotations.partialwsdl.servernowsdl.*;
import annotations.partialwsdl.serverpartial1.*;


public class AddNumbersClient {
	
	public AddNumbersClient(){
		//Empty body
	}

	public static void main(String[] args) {
		int result = 0;
		int number1 = -20,
		    number2 = -20;
		
		result = addNumbers_Partial1(number1, number2);
		System.out.println("The result of " + number1 + " + " + number2 +
				           "is " + result);
		
		number2 = -10;
		result = addNumbers_Partial1(number1, number2);
		System.out.println("The result of " + number1 + " + " + number2 +
		                   " is " + result);
	}

    public static int addNumbers_NoWsdl(int number1, int number2){    	
        try {
        	AddNumbersImplNoWsdl port = 
				new AddNumbersImplNoWsdlService().getAddNumbersImplNoWsdlPort();
			return port.addTwoNumbers(number1, number2);
		} catch (WebServiceException ex) {
			return -1;
		}
    }
	
 	
    public static int addNumbers_Partial1(int number1, int number2){    	
        try {
        	AddNumbersImplPartial1 port = 
				new AddNumbersImplPartial1Service().getAddNumbersImplPartial1Port();
			return port.addTwoNumbers(number1, number2);
		} catch (AddNegativeNumbersException ex) {
            System.out.println("caught AddNegatvieNumbersException");
			ex.printStackTrace();
			return -1;
		}catch (WebServiceException ex) {
            System.out.println("caught WebServiceException");
			ex.printStackTrace();
			return -2;
		}
    }
	
 
}
