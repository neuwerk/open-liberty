package annotations.webservice.testdata;
import java.net.*;
import anno.wsfvt.*;
import javax.xml.namespace.QName;

/**
 * a class to check runtime handling of @WebService parameters.
 *
 * @author btiffany
 * @param - url to wsdl file, optional
 */
public class WebServiceRuntimeClient{
    public static void main (String [] args){
       String url=null;
       if (args.length > 0 ) url = args[0];
       WebServiceRuntimeClient c = new WebServiceRuntimeClient();
       System.exit(c.runtest(url));
    }

     int runtest(String urlString){

        MyService s = null;
        if (urlString == null ){
            System.out.println("using default wsdl url");
            s = new Valid1().getMyServicePort();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            QName qn = new QName("wsfvt.anno", "valid1");

            s = new Valid1(url, qn).getMyServicePort();
        }
        String response = s.echo("Hello Ms. Server, how are you");
        System.out.println("received response: "+response);
        if (response.contains("server says")) return 0;  // good response

        return 12;
    }

}
