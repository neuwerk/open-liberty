/*
 * @(#) 1.6 autoFVT/src/annotations/webfault/customization/client/WebFaultCustomizationClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 2/26/07 10:33:30 [8/8/12 06:55:08]
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
package annotations.webfault.customization.client;

import annotations.webfault.customization.server.*;

public class WebFaultCustomizationClient {
	
	public WebFaultCustomizationClient(){
		//Empty body
	}
	
	public static void main(String[] args) {
		int result = 0;
		int number1 = -20,
		    number2 = -20;
		
		result = callAddService(number1, number2);
		System.out.println("The result of " + number1 + " + " + number2 +
				           "is " + result);
		
		number2 = -10;
		result = callAddService(number1, number2);
		System.out.println("The result of " + number1 + " + " + number2 +
		                   " is " + result);
	}
	
    public static int callAddService(int number1, int number2){    	
        try {
        	WebFaultCustomization port = 
        		new WebFaultCustomizationService().getWebFaultCustomizationPort();
			return port.addNumbers(number1, number2);
		} catch (WebFaultCustomizationException ex) {
            System.out.println("caught expected WebFaultCustomizationException");
			return -1;
            
		}
    }

    /*
     * remove until we get the base case working
    public static int callMultiplyService(int number1, int number2){    	
        try {
        	WebFaultCustomizationImpl port = 
        		new WebFaultCustomizationImplService().getWebFaultCustomizationImplPort();
			return port.multiplyNumbers(number1, number2);
		} catch (MultiplyNumbersException ex) {
			return -2;
		}
    }
    */
}
