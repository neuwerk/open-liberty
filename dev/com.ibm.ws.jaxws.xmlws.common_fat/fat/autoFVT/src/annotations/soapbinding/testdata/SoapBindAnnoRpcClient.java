package annotations.soapbinding.testdata;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.ws.*;

import javax.xml.namespace.QName;
/**
 * a class to check runtime handling of @WebMethod parameters.
 * 
 * @author btiffany
 *
 */
public class SoapBindAnnoRpcClient{
    public static void main (String [] args){
        String url=null; 
        System.out.println("client is invoked");
        if (args.length > 0 ) url = args[0];
        SoapBindAnnoRpcClient c = new SoapBindAnnoRpcClient();
        System.exit(c.runtest(url));
     }  

     int runtest(String urlString)  {
    	 SoapBindAnnoRpc s = null;
         String response = null;
    	 if (urlString == null ){
             System.out.println("using default wsdl url");
             s = new SoapBindAnnoRpcService().getSoapBindAnnoRpcPort();
         } else {
             System.out.println("using supplied wsdl url: "+urlString);
             URL url = null;
             try {
                 url = new URL(urlString);
             } catch (MalformedURLException e) {
                 e.printStackTrace();
             }
             QName qn = new QName("http://testdata.soapbinding.annotations/", "SoapBindAnnoRpc");
             SoapBindAnnoRpcService rs = new SoapBindAnnoRpcService(url, qn);
             s = rs.getSoapBindAnnoRpcPort();
             //s = new SoapBindAnno3Service().getSoapBindAnno3Port();
             //s = new WebMethodRuntimeService(url, qn).getWebMethodRuntimePort();
         }
    	boolean fail = false;
        
        try{

	        response = s.echorlw("hello there rlw");
	        System.out.println("received response: "+response);
            if(! response.contains("echorlw replies: hello there rlw")) fail = true;
	        
	    // set different rc for webserviceexception, so we can confirm we rcvd. it. 
        } catch (WebServiceException e){
        	e.printStackTrace();
        	return 23;  
        } catch( Throwable e){
        	e.printStackTrace();
        	return 4;
        }

        if (fail) return 12;
        return 0;
    }

}
