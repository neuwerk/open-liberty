package annotations.webservice_g2.interfaces.client;
import java.net.*;
import annotations.webservice_g2.interfaces.server.*;
import javax.xml.namespace.QName;

/**
 * a class to check runtime handling of @WebService parameters.
 *
 * @author btiffany
 * @param - url to wsdl file, optional
 */
public class WebServiceInterfacesClientTwo{
    public static void main (String [] args){
       String url=null;
       if (args.length > 0 ) url = args[0];
       runme(url);
 
    }
    
    public static int runme(String url){
        WebServiceInterfacesClientTwo c = new WebServiceInterfacesClientTwo();
        return(c.runtest(url));    	
    }

     int runtest(String urlString){        
        InterfaceTestTwo s = null;
        if (urlString == null ){
            System.out.println("using default wsdl url");
            s = new InterfaceTestTwoImplService().getInterfaceTestTwoImplPort();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            QName qn = new QName("http://server.interfaces.webservice_g2.annotations/",
                                    "InterfaceTestTwoImplService");

            s = s = new InterfaceTestTwoImplService(url,qn).getInterfaceTestTwoImplPort();
        }
        String request = "calling Mr. bareIfMethod";
        String response = s.bareIfMethod(request);
        System.out.println("received response: "+response);
        if (!response.contains(request)) throw new RuntimeException("bad response");
        

        request = "calling Mr. annoIfMethod";
        response = s.annoIfMethod(request);
        System.out.println("received response: "+response);
        if (!response.contains(request)) throw new RuntimeException("bad response");

        try{
            request = "calling Mr. annoExcludedMethod";
	        response = s.annoIfExcluded(request);
	        System.out.println("received response: "+response);            
        } catch( Exception e){
        	System.out.println("caught exception on invoking excluded method");
        	// we should get here and get an exception if the method was excluded on server side.
        	return 0;
        }

        //System.out.println("excluded method was invoked, bad.");
        return 12;
    }

}
