package annotations.handlerchain.checkdefaults.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import annotations.handlerchain.checkdefaults.server.*;

public class AddNumbersClient {
    
	public AddNumbersClient(){
		//Empty body
	}
    static AddNumbersImpl myPort = null;

	public static void main(String[] args) {
		int result = 0;
		int number1 = 10,
		    number2 = 20;
        String url = null;
        
        if (args.length > 0 ) url = args[0];
        myPort = getMyPort(url);
        
		
		AddNumbersClient client = new AddNumbersClient();
		result = client.callAddService(number1, number2);
		System.out.println("The result of AddService" + number1 + " + " + number2 +
				           "is " + result);
        System.out.println("exiting with return code = "+result);
        System.exit(result);
		

    }
	
	/*
	 * @return A Test object containing tests to be run
	 * @param A Test object containing tests to be run
	 */
    public static int callAddService(int number1, int number2){    	
        try {		
            System.out.println("calling remote service");            
			int i = myPort.addNumbers(number1, number2);
            System.out.println("remote service returned");
            return i;
            
		} catch (AddNumbersException_Exception ex) {
            System.out.println("caught AddNumbersException");            
			return -1;
		}
    }
    
    public static AddNumbersImpl getMyPort(String urlString){
   
        AddNumbersImpl s = null;
        if (urlString == null ){
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
            QName qn = new QName("http://server.checkdefaults.handlerchain.annotations/", "AddNumbersImplService");
            s = new AddNumbersImplService(url, qn).getAddNumbersImplPort();
        }    
        return s;
    }
}
