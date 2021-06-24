package annotations.webmethod.testdata;

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
public class WebMethodRuntimeClient{
    public static void main (String [] args){
        String url=null; 
        if (args.length > 0 ) url = args[0];
        WebMethodRuntimeClient c = new WebMethodRuntimeClient();
        System.exit(c.runtest(url));
     }  

     int runtest(String urlString)  {
    	 WebMethodRuntime s = null;
         String response = null;
         boolean fail = false;
         try{
        	 if (urlString == null ){
                 System.out.println("using default wsdl url");
                 s = new WebMethodRuntimeService().getWebMethodRuntimePort();
             } else {
                 System.out.println("using supplied wsdl url: "+urlString);
                 URL url = null;
                 try {
                     url = new URL(urlString);
                 } catch (MalformedURLException e) {
                     e.printStackTrace();
                 }
                 QName qn = new QName("http://testdata.webmethod.annotations/", "WebMethodRuntimeService");
    
                 System.out.println("getting port...");
                 s = new WebMethodRuntimeService(url, qn).getWebMethodRuntimePort();
                 if (s == null) System.out.println ("Port is null!");
             }
        	
       
	        /*
            // jsr 250 2.1.2 excludes this. 
	        response = s.noAnno("hello");
	        System.out.println("received response: "+response);
	        if (!response.contains("noAnno responds")){
	        	System.out.println("unexpected response ");
	        	fail = true;        	
	        }
	        else System.out.println("good response");
	        */
            System.out.println("invoking defaultAnno");
	        response = s.defaultAnno("hello");
	        System.out.println("received response: "+response);
	        if (!response.contains("defaultAnno responds")){
	        	System.out.println("unexpected response ");
	        	fail = true;        	
	        }
	        else System.out.println("good response");
	        
	        // this should work, as the webmethod annotation should override the method name.
            System.out.println("invoking renamedAnno");
	        response = s.renamedAnno("hello");
	        System.out.println("received response: "+response);
	        if (!response.contains("renamedAnno responds")){
	        	System.out.println("unexpected response ");
	        	fail = true;        	
	        }
	        else System.out.println("good response");
            
            
	    // set different rc for webserviceexception, so we can confirm we rcvd. it.            
        } catch (WebServiceException e){
            System.out.println("client caught WebServiceException");
        	e.printStackTrace();
        	return 23;  
        } catch( Throwable e){
            System.out.println("client caught some other strange exception");
        	e.printStackTrace();
        	return 4;
        }

        if (fail) return 12;
        return 0;
    }

}
