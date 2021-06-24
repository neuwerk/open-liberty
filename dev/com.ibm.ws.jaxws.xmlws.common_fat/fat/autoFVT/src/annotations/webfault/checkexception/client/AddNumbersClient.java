/*
 * @(#) 1.8 autoFVT/src/annotations/webfault/checkexception/client/AddNumbersClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/17/07 16:26:55 [8/8/12 06:54:49]
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
 * 01/03/2007  btiffany                       make url a command line parameter
 */
package annotations.webfault.checkexception.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import annotations.webfault.checkexception.server.*;

public class AddNumbersClient {
	
	public AddNumbersClient(){
		//Empty body
	}
    static AddNumbers s = null;        // reference to the proxy port
	
	public static void main(String[] args) {
        String urlString = null;
        
        if (args.length >0) urlString = args[0]; 
		int result = 0;
		int number1 = -20,
		    number2 = -20;
        
		result = callAddService(urlString, number1, number2);
		System.out.println("The result of " + number1 + " + " + number2 +
				           "is " + result);
		
		number2 = -10;
		result = callAddService(urlString, number1, number2);
		System.out.println("The result of " + number1 + " + " + number2 +
		                   " is " + result);
	}

    public static int callAddService(String url, int number1, int number2){
        System.out.println("addTwoNumbers invoked with "+Integer.toString(number1)+" "+Integer.toString(number2));
        if (s == null) s = getPort(url);
        try {			
			int result = s.addTwoNumbers(number1, number2);
            System.out.println("returning "+result);
            return result;
            
		} catch (AddNegativeNumbersException_Exception ex) {
            System.out.println("caught AddNegativeNumbersException");
            ex.printStackTrace((System.out));
			return -1;
		} 
    }
    
    private static AddNumbers getPort(String urlString){
        AddNumbers s = null;
        if (urlString.length()==0  ){
            System.out.println("using default wsdl url");
            s = new AddNumbersImplService().getAddNumbersImplPort();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            
            QName qn = new QName("http://server.checkexception.webfault.annotations/", "AddNumbersImplService");            
            s = new AddNumbersImplService(url, qn).getAddNumbersImplPort();
            
        }
        return s;
    }
}
