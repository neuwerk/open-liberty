package annotations.soapbinding.testdata;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.ws.*;

import javax.xml.namespace.QName;

import annotations.soapbinding.testdata.serverimpl.*;
/**
 * a class to check runtime handling of @WebMethod parameters.
 * 
 * goes with SoapBindRtImpl on the server side.
 * 
 * @author btiffany
 *
 */
public class SoapBindRtImplClient{
    String resultString = "failed";
    public static void main (String [] args){
        String url=null; 
        System.out.println("client is invoked");
        if (args.length > 0 ) url = args[0];
        SoapBindRtImplClient c = new SoapBindRtImplClient();
        System.exit(c.runtest(url));
     }  
    
    int runme(String urlString){
    	SoapBindRtImplClient c = new SoapBindRtImplClient();
    	return(c.runtest(urlString));
    }
    
    public String getResult(String urlString){        
        int rc = runtest(urlString);
        if (rc != 0) return "rc="+String.valueOf(rc);
        return resultString; 
    }

    public int runtest(String urlString)  {
         int rc = 0;
         annotations.soapbinding.testdata.serverimpl.SoapBindRtImpl s = null;
         String response = null;
    	 if (urlString == null ){
             System.out.println("using default wsdl url");
             s = new SoapBindRtImplService().getSoapBindRtImplPort();
         } else {
             System.out.println("using supplied wsdl url: "+urlString);
             URL url = null;
             try {
                 url = new URL(urlString);
             } catch (MalformedURLException e) {
                 e.printStackTrace();
             }
             QName qn = new QName("http://serverimpl.testdata.soapbinding.annotations/", "SoapBindRtImplService");
             s = new SoapBindRtImplService(url, qn).getSoapBindRtImplPort();
             //s = new SoapBindAnno3Service().getSoapBindAnno3Port();
             //s = new WebMethodRuntimeService(url, qn).getWebMethodRuntimePort();
         }

        try{            
            System.out.println("calling echodlb");
            response = s.echodlb("hello dlb");
            resultString = response;
            System.out.println("got response: "+response);
        } catch (WebServiceException e){
            e.printStackTrace();
            rc = 23;
        }
         catch (Throwable t){
            t.printStackTrace();
            rc=4;
        }

            
        try{
            System.out.println("calling echodlw");
	        response = s.echodlw("hello there dlw");
            resultString += response;
	        System.out.println("received response: "+response);
	    // set different rc for webserviceexception, so we can confirm we rcvd. it. 
        } catch (WebServiceException e){
        	e.printStackTrace();
        	rc=23;  
        } catch( Throwable e){
        	e.printStackTrace();
        	rc=4;
        }        
        System.out.println("runtest rc=" +rc);
        return rc;
    }

}
