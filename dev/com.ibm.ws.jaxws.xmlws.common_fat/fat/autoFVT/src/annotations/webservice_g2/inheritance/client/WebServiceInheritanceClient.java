package annotations.webservice_g2.inheritance.client;
import annotations.webservice_g2.inheritance.server.*;

import java.net.*;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

/**
 * a class to check runtime handling of inheritance
 *
 * @author btiffany
 * @param - url to wsdl file, optional
 * 
 * 2.26.07 - add try-catch around all method invokes, so test can proceed.
 */
public class WebServiceInheritanceClient{
    public static void main (String [] args){
       String url=null;
       if (args.length > 0 ) url = args[0];
       System.out.println("\n Would return this string to testcase: "+runme(url));

    }

    public static String runme(String url){
        WebServiceInheritanceClient c = new WebServiceInheritanceClient();
        return(c.runtest(url));
    }

     String runtest(String urlString){

        // create the proxy object
        BottomImpl s = null;
        String result = "";

        if (urlString == null ){
            System.out.println("using default wsdl url");
            s = new BottomImplService().getBottomImplPort();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            
            QName qn = new QName("http://server.inheritance.webservice_g2.annotations/", "BottomImplService");
            s = new BottomImplService(url, qn).getBottomImplPort();
            
        }

        // invoke the methods  
        // note how names changed case from wsdl - def 400956
        String result1 = null;
        try{
            System.out.println("invoking impl1Echo");
            result1 = s.impl1Echo("hello");   // inherited from Impl1.
            System.out.println("received response: "+result1);
        }    
        catch (Exception e){
            System.out.println("error! - caught exception");
            e.printStackTrace();
            result1= "exception!";
        }
        
        String result2 = null;
        try{
            System.out.println("invoking impl1Echo2");
            // in impl1, overridden by and then inherited from Impl2.
            result2 = s.impl1Echo2("hello"); 
            System.out.println("received response: "+result2);
        } catch (Exception e){
            System.out.println("error! - caught exception");
            e.printStackTrace();
            result2= "exception!";            
        }
        
        String result3 = null;
        try{            
            System.out.println("invoking impl2Echo");
            result3 = s.impl2Echo("hello");  // inherited from Impl2
            System.out.println("received response: "+result3);
         } catch (Exception e){
             System.out.println("error! - caught exception");
             e.printStackTrace();
             result3= "exception!";            
         }

        String result4 = null;
        try{            
            System.out.println("invoking echoBottom");
            result4 = s.echoBottom("hello"); // not inherited.
            System.out.println("received response: "+result4);
        } catch (Exception e){
            System.out.println("error! - caught exception");
            e.printStackTrace();
            result4= "exception!";            
        }

        
        return result1 + " " + result2 + " " + result3 + " " +result4;


    }

}
;
