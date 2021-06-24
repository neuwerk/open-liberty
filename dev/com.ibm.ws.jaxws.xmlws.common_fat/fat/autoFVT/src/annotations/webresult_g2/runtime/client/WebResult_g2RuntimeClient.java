package annotations.webresult_g2.runtime.client;
import annotations.webresult_g2.runtime.server2.*;

import java.net.*;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

/**
 * a class to check runtime handling of @WebResult parameters.
 *
 * @author btiffany
 * @param - url to wsdl file, optional
 */
public class WebResult_g2RuntimeClient{
    public static void main (String [] args){
       String url=null;
       if (args.length > 0 ) url = args[0];
       System.out.println("\n Would return this string to testcase: "+runme(url));

    }

    public static String runme(String url){
        WebResult_g2RuntimeClient c = new WebResult_g2RuntimeClient();
        return(c.runtest(url));
    }

     String runtest(String urlString){

        // create the proxy object
        WebResultCheck s = null;
        String result = "";

        if (urlString == null ){
            System.out.println("using default wsdl url");
            s = new WebResultCheckService().getWebResultCheckPort();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            
            
            
            QName qn = new QName("http://server2.runtime.webresult_g2.annotations/", "WebResultCheckService");
            s = new WebResultCheckService(url,qn).getWebResultCheckPort();
        }

        // invoke the methods  
        /*  - maybe we can put this back in when/if bare style works better.
        try{
            System.out.println("calling echo, tests header parameter.");
            String input = "calling_echo_";
            
            MyString in = new MyString();
            in.setString(input);
            MyString m = s.echo( in );
            System.out.println("received response: "+m.getString());
            result += m.getString();
            
        }  catch (Exception e){
            System.out.println("caught exception invoking echo:\n");
            e.printStackTrace(System.out);
            
        }
        */
        try{
                System.out.println("calling locateCustomer");
                CustomerRecord c =  s.locateCustomer("barney", "rubble", "stoneage");
                
                // calling the wsimport generated CustomerRecord, not ours.
                String resp = c.getAddr()+ "_" + c.getFname() + "_" + c.getLname() +"_";
                System.out.println("received response: "+resp);
                result += resp;
                
                

        } catch (Exception e){
                System.out.println("caught exception invoking locateCustomer:\n");
                e.printStackTrace(System.out);
        }


        try{
                System.out.println("calling locateCustomer2");
                CustomerRecord c = s.locateCustomer2("barney");
                System.out.println("method call returned");                 
                
                String resp = c.getAddr() + "_" + c.getFname() + "_" + c.getLname() +"_";
                //String resp = c.addr+ "_" + c.fname+ "_" + c.lname+"_";
                System.out.println("received response: "+resp);
                result += resp;

                
        } catch (Exception e){
                System.out.println("caught exception invoking locateCustomer2:\n");
                e.printStackTrace(System.out);
        }
        
        try{
            System.out.println("calling echo");
            MyString m = new MyString();
            m.setString("testing");
            
            MyString r = s.echo(m);
            System.out.println("received response: "+ r.getString());
            
            result += "_"+ r.getString();

            
        } catch (Exception e){
            System.out.println("caught exception invoking echo:\n");
            e.printStackTrace(System.out);

            
        }
        return result;

    }

}
;
