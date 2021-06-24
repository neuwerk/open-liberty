package annotations.webservice.testdata;
import java.net.*;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;



/**
 * a client class to check runtime handling of @WebService parameters.
 * had to change the service class name as the name parameter doesn't work right yet for interfaces
 * on the server side.
 * 
 * The annotation on an interface is confusing.
 * Some parameters come through from the interface, some from the class. Yeech!
 *
 * @author btiffany
 * @param - url to wsdl file, optional
 */
public class WebServiceRuntimeIFClient{
    public static void main (String [] args){
       String url=null;
       if (args.length > 0 ) url = args[0];
       WebServiceRuntimeIFClient c = new WebServiceRuntimeIFClient();
       System.exit(c.runtest(url));
    }

     int runtest(String urlString){

    	WebServiceRuntimeIfc s = null;
        if (urlString == null ){
            urlString="http://btiffany:9080/websvcannoIf/websvcannoIf";
            System.out.println("setting url via requestcontext to: "+ urlString);
            s = new WebServiceRuntimeImplService().getWebServiceRuntimeImplPort();
            // let's try Dan's approach
            
            
            //assertNotNull("Port is null", s);
            Map<String, Object> reqCtxt = ((BindingProvider) s)
                    .getRequestContext();
            reqCtxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                    urlString);

            
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            QName qn = new QName("http://testdata.webservice.annotations/", "WebServiceRuntimeImplService");

            s = new WebServiceRuntimeImplService(url, qn).getWebServiceRuntimeImplPort();
        }
        String response = s.echo("Hello Ms. Server, how are you");
        System.out.println("received response: "+response);
        if (response.contains("server says")) return 0;  // good response

        return 12;
    }

}
