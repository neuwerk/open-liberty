package annotations.soapbinding.testdata;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.ws.*;

import javax.xml.namespace.QName;

import annotations.soapbinding.testdata.server.*;
/**
 * a class to check runtime handling of @WebMethod parameters.
 * 
 * @author btiffany
 *
 */
public class SoapBindAnno3Client{
    String resultString = "failed";
    public static void main (String [] args){
        String url=null; 
        System.out.println("client is invoked");
        if (args.length > 0 ) url = args[0];
        SoapBindAnno3Client c = new SoapBindAnno3Client();
        System.exit(c.runtest(url));
     }  
    
    int runme(String urlString){
    	SoapBindAnno3Client c = new SoapBindAnno3Client();
    	return(c.runtest(urlString));
    }
    
    public String getResult(String urlString){        
        int rc = runtest(urlString);
        if (rc != 0) return "rc="+String.valueOf(rc);
        return resultString; 
    }

    public int runtest(String urlString)  {
    	 SoapBindAnno3 s = null;
         String response = null;
    	 if (urlString == null ){
             System.out.println("using default wsdl url");
             s = new SoapBindAnno3ImplService().getSoapBindAnno3ImplPort();
         } else {
             System.out.println("using supplied wsdl url: "+urlString);
             URL url = null;
             try {
                 url = new URL(urlString);
             } catch (MalformedURLException e) {
                 e.printStackTrace();
             }
             QName qn = new QName("http://server.testdata.soapbinding.annotations/", "SoapBindAnno3ImplService");
             s = new SoapBindAnno3ImplService(url, qn).getSoapBindAnno3ImplPort();
             //s = new SoapBindAnno3Service().getSoapBindAnno3Port();
             //s = new WebMethodRuntimeService(url, qn).getWebMethodRuntimePort();
         }

        try{

            System.out.println("calling echodlw");
	        response = s.echodlw("hello there dlw");
            resultString = response;
	        System.out.println("received response: "+response);	        

            System.out.println("calling echodlb");
	        response = s.echodlb("hello dlb");
            resultString += response;
	        System.out.println("got response: "+response);
	        
	    // set different rc for webserviceexception, so we can confirm we rcvd. it. 
        } catch (WebServiceException e){
        	e.printStackTrace();
        	return 23;  
        } catch( Throwable e){
        	e.printStackTrace();
        	return 4;
        }
        return 0;
    }

}
