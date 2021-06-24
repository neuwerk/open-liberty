package annotations.webservice.testdata;
import java.net.*;
import javax.xml.namespace.QName;
import javax.xml.ws.*;
import javax.xml.ws.soap.SOAPBinding;

/**
 * a class to check runtime handling of @WebService parameters.
 *
 * @author btiffany
 * @param - url to wsdl file, optional
 */
public class WebServiceBasicRuntimeClient{
    public static void main (String [] args){
       String url=null;
       if (args.length > 0 ) url = args[0];
       WebServiceBasicRuntimeClient c = new WebServiceBasicRuntimeClient();
       System.exit(c.runtest(url));
    }

     int runtest(String urlString){
         System.setProperty("com.ibm.websphere.webservices.qos.wsaddressing.enable", "true");
        WebServiceBasicRuntime s = null;
         //Service s = null;
        if (urlString == null ){
            System.out.println("using default wsdl url");
            s = new WebServiceBasicRuntimeService().getWebServiceBasicRuntimePort();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            QName qn = new QName("http://testdata.webservice.annotations/", 
                    "WebServiceBasicRuntimeService");
            
            QName portQn = new QName("http://testdata.webservice.annotations/", 
            "WebServiceBasicRuntimePort");

            // try dynamic to get around addressing bug - no good.
            
            /*
            Service s2 =  WebServiceBasicRuntimeService.create( qn);
            s2.addPort(portQn, SOAPBinding.SOAP11HTTP_BINDING, urlString);
            s = s2.getPort(portQn, WebServiceBasicRuntime.class);
            */
            s = new WebServiceBasicRuntimeService(url, qn).getWebServiceBasicRuntimePort();
        }
        String response = s.echo("Hello Ms. Server, how are you");
        System.out.println("received response: "+response);
        if (response.contains("server says")) return 0;  // good response

        return 12;
    }

}
