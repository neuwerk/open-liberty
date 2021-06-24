package annotations.handlerchain.provider.client;

//import annotations.handlerchain.normal.server.AddNumbersImpl;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import annotations.handlerchain.provider.server.*;

/* client for trivial service that adds two ints */
public class AddNumbersClient {
        
	public static void main (String[] args) {
        
        int number1 = 10;
        int number2 = 20;
        String url = null;
        if (args.length > 0 ) url = args[0];
        int result = callAddService(url, number1, number2);
        System.out.println("exiting with return code = result = "+result);
        System.exit(result);

    }
	
    public static int callAddService(String url, int number1, int number2){    	
        try {            
            AddNumbersImpl port = getMyPort(url);
        	return port.addNumbers(number1, number2);
		} catch (Exception ex) {
			ex.printStackTrace();
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
            QName qn = new QName("http://server.provider.handlerchain.annotations/", "AddNumbersImplService");
            s = new AddNumbersImplService(url, qn).getAddNumbersImplPort();
        }    
        return s;
    }
}