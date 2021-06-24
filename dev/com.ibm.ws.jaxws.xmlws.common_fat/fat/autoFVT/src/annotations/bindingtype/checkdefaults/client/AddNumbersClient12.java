/*
 * @(#) 1.2 autoFVT/src/annotations/bindingtype/checkdefaults/client/AddNumbersClient12.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/8/07 08:52:52 [8/8/12 06:55:56]
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
package annotations.bindingtype.checkdefaults.client;

import javax.xml.ws.WebServiceException;

import annotations.bindingtype.checkdefaults.server12.*;

public class AddNumbersClient12 {
	
	public AddNumbersClient12(){
		//Empty body
	}

	public static void main(String[] args) {
		int result = 0;
		int number1 = -20,
		    number2 = -20;
		
		result = addNumbers(number1, number2);
		System.out.println("The result of " + number1 + " + " + number2 +
				           "is " + result);	
	}

    public static int addNumbers(int number1, int number2){    	
        try {
        	AddNumbersImpl port = 
				new AddNumbersImplService().getAddNumbersImplPort();
			return port.addTwoNumbers(number1, number2);
		} catch (WebServiceException ex) {
            ex.printStackTrace();
			return -1;
		}
    }
	
}
