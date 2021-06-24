package annotations.webserviceprovider.client;

import java.net.MalformedURLException;
import java.net.URL;
import fq.webserviceprovider.annotations.*;  // generated classes
import javax.xml.namespace.QName;

public class ProvFqProxyClient {
    public static void main (String [] args){
        String url=null;
        if (args.length > 0 ) url = args[0];
        runclient(url);
  
     }
     
     public static String runclient(String url){
         ProvFqProxyClient c = new ProvFqProxyClient();
         return(c.runtest(url));        
     }

      String runtest(String urlString){

         FqPort1 s = null;
         if (urlString == null ){
             System.out.println("using default wsdl url");
             s = new ProvFqService().getFqPort();
         } else {
             System.out.println("using supplied wsdl url: "+urlString);
             URL url = null;
             try {
                 url = new URL(urlString);
             } catch (MalformedURLException e) {
                 e.printStackTrace();
             }
             QName qn = new QName("annotations.webserviceprovider.fq", "ProvFqService");

             s = new ProvFqService(url, qn).getFqPort();
         }
         System.out.println("invoking remote service");
         String response = s.echo("calling Mr. echo");
         System.out.println("received response: "+response);
         return response;
     }


}
