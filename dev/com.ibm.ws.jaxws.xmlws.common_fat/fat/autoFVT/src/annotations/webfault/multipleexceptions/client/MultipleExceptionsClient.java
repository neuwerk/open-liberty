/*
 * @(#) 1.1 autoFVT/src/annotations/webfault/multipleexceptions/client/MultipleExceptionsClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 12:06:06 [8/8/12 06:56:04]
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
package annotations.webfault.multipleexceptions.client;

import annotations.webfault.multipleexceptions.server.*;

public class MultipleExceptionsClient {
	
	public MultipleExceptionsClient(){
		//Empty body
	}
	
	public static void main(String[] args) {
		int result = 0;
		int number1 = -20,
		    number2 = -20;
		
		result = MultipleExceptionsClient.callAddService(number1, number2);
		System.out.println("The result of " + number1 + " + " + number2 +
				           "is " + result);
		
		number2 = -10;
		result = MultipleExceptionsClient.callAddService(number1, number2);
		System.out.println("The result of " + number1 + " + " + number2 +
		                   " is " + result);
	}
	
    public static int callAddService(int number1, int number2){    	
        try {
        	MultipleExceptionsImpl port = 
        		new MultipleExceptionsImplService().getMultipleExceptionsImplPort();
			return port.addTwoNumbers(number1, number2);
		} catch (AddNumbersException ex) {
			return -1;
		} 
    }

    public static int callMultService(int number1, int number2){    	
        try {
        	MultipleExceptionsImpl port = 
        		new MultipleExceptionsImplService().getMultipleExceptionsImplPort();
			return port.multiplyTwoNumbers(number1, number2);
		} catch (MultiplyNumbersException ex) {
			return -1;
		}
    }
}
