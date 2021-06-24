package annotations.webresult_g2.runtime.client;

import annotations.webresult_g2.runtime.server.*;
//import annotations.webresult_g2.runtime.server.jaxws.*;

import java.net.*;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

/**
 * a class to check runtime handling of @WebResult parameters.
 *
 * @author btiffany
 * @param - url to wsdl file, optional
 */
public class WRCBetaClient{
    public static void main (String [] args){
       String url=null;
       if (args.length > 0 ) url = args[0];
       System.out.println("\n Would return this string to testcase: "+runme(url));

    }

    public static String runme(String url){
        WRCBetaClient c = new WRCBetaClient();
        return(c.runtest(url));
    }

     String runtest(String urlString){

        // create the proxy object
        WRCBetaIf s = null;
        String result = "";

        if (urlString == null ){
            System.out.println("using default wsdl url");
            s = new WRCBetaImplService().getWRCBetaImplPort();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            
            QName qn = new QName("http://server.runtime.webresult_g2.annotations/",
                                 "WRCBetaImplService");
            s = new WRCBetaImplService(url,qn).getWRCBetaImplPort();
        }

        // invoke the methods        
        try{
            System.out.println("calling echo1");
            String resp=s.echo1("calling echo1");
            System.out.println("received: "+resp);
            result += resp;
        } catch (Exception e){
                System.out.println("caught exception invoking echo1:\n");
                e.printStackTrace(System.out);
        }


        try{
                System.out.println("calling echo2");
                String resp=s.echo2("calling echo2");                
                System.out.println("received: "+resp);
                result += resp;
                
        } catch (Exception e){
                System.out.println("caught exception invoking echo2:\n");
                e.printStackTrace(System.out);
        }
        
        try{
            System.out.println("calling void1");
            s.void1("calling void1");
            System.out.println("calling void2");
            s.void2("calling void2");
            
        } catch (Exception e){
                System.out.println("caught exception invoking void methods\n");
                e.printStackTrace(System.out);
                return "client caught exception invoking void methods - probably defect 391760";
        }
        
        System.out.println("returning: "+result);
        return result;

    }

}

