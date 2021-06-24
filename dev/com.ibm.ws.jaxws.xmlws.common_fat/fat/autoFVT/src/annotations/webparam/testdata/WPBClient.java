package annotations.webparam.testdata;
import java.net.*;
import javax.xml.namespace.QName;
import javax.xml.ws.*;

/**
 * a class to check runtime handling of @WebService parameters.
 *
 * @author btiffany
 * @param - url to wsdl file, optional
 */
public class WPBClient{
    boolean hadTrouble=false;
    public static void main (String [] args){
        String url=null;       
       if (args.length > 0 ) url = args[0];
       WPBClient c = new WPBClient();
       System.exit(c.runtest(url));
    }
    
    // set state if we have problem
    void trouble(boolean weHaveAProblem){
        if( ! weHaveAProblem )return;
        System.out.println("****trouble detected****");
        hadTrouble = true;
    }

     public int runtest(String urlString){        
        WPBIf s = null;
        if (urlString == null ){
            System.out.println("using default wsdl url");
            s =new WPBImplService().getWPBImplPort();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            QName qn= new QName("http://testdata.webparam.annotations/", "WPBImplService");

            s = new WPBImplService(url, qn).getWPBImplPort();
        }
        // service should just echo whatever we send it.
        String input = "hello Ms. Server, how are you";
        
        // method with no webparam customizations on server side
        String response = (String) s.echo(input);
        System.out.println("received response: "+response);
        trouble(!response.contains(input));
        
        //name customized
        response = (String) s.echo2(input);
        System.out.println("received response: "+response);
        trouble(!response.contains(input));
        
        //namespace customized.
        response = (String) s.echo3(input);
        System.out.println("received response: "+response);
        trouble(!response.contains(input));
        
        if(!hadTrouble) return 0;
        return 12;
    }

}
