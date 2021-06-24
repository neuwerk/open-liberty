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
public class WebServiceInterfacesClient{
    public static void main (String [] args){
       String url=null;
       if (args.length > 0 ) url = args[0];
       runme(url);
 
    }
    
    public static int runme(String url){
        WebServiceInterfacesClient c = new WebServiceInterfacesClient();
        return(c.runtest(url));    	
    }

     int runtest(String urlString){
        int finalrc = 0;

        InterfaceTestOne s = null;
        if (urlString == null ){
            System.out.println("using default wsdl url");
            s = new Ifimplsvc().getIftest();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            QName qn = new QName("server.interfaces.webservice_g2.annotations", 
                                 "ifimplsvc");

            s = new Ifimplsvc(url, qn).getIftest();
        }
        String input = "calling Mr. bareIfMethod";
        System.out.println("calling bareIfMethod with: "+input);
        String response = s.bareIfMethod(input);
        System.out.println("received response: "+response);
        if (response.compareTo(input)!=0){
            System.out.println("invalid response");
            finalrc +=1;
        }

        input = "calling Mr. annoIfMethod";
        System.out.println("calling annoIfMethod with: "+input);
        response = s.annoIfMethod(input);
        System.out.println("received response: "+response);
        if (response.compareTo(input)!=0){
            System.out.println("invalid response");
            finalrc +=2;
        }

        try{
            input = "calling Mr. annoExcludedMethod";
            System.out.println("calling annoIfExcluded with: "+input);
	        response = s.annoIfExcluded(input);
	        System.out.println("received response: "+response);
            
        } catch( Exception e){
        	System.out.println("caught exception on invoking excluded method");
            e.printStackTrace();
        	// we should get here and return 0 if method was really exluded on server side 
            System.out.println("returning: "+finalrc);
        	return finalrc;
        }

       // System.out.println("excluded method was invoked, bad.");
        finalrc += 12;
        System.out.println("returning rc= "+finalrc);
        return finalrc;
    }

}
