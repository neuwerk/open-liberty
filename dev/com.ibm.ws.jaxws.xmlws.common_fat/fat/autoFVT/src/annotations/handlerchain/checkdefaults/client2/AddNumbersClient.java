package annotations.handlerchain.checkdefaults.client2;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import annotations.handlerchain.checkdefaults.server.*;

/* we had to use a separate client for the doc-lit-bare case as doc-lit-bare
 * can't handle an input method with two arguements. 
 */
public class AddNumbersClient {
    
	public AddNumbersClient(){
		//Empty body
	}
    static AddNumbersImpl myPort = null;

	public static void main(String[] args) {
		int result = 0;
		int number1 = 10;
        String url = null;
        
        if (args.length > 0 ) url = args[0];
        myPort = getMyPort(url);
        
		
		AddNumbersClient client = new AddNumbersClient();
		result = client.callAddService(number1);
		System.out.println("The result of AddService" + number1 + 
				           "is " + result);
        System.out.println("exiting with return code = "+result);
        System.exit(result);
		

    }
	
    // have to use Strings for jaxb limitations
    public static int callAddService(int number1){    	
        System.out.println("calling remote service");            
		String i = myPort.addTwenty(Integer.toString(number1));
        System.out.println("remote service returned");
        return Integer.parseInt(i);
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
