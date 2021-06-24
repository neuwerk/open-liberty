package annotations.reqrespwrappers.client;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.ws.*;
import annotations.reqrespwrappers.server.*;
// these imports needed because we mutated the namespace with the annotations. 


//import server2req.reqrespwrappers.annotations.*;
//import server2resp.reqrespwrappers.annotations.*;


import javax.xml.namespace.QName;
/**
 * a class to check runtime handling of @RequestWrapper and @ResponseWrapper parameters.
 * 
 * @author btiffany
 *
 */
public class ReqRespRuntimeClient{
    public static void main (String [] args){
        String url=null; 
        if (args.length > 0 ) url = args[0];
        ReqRespRuntimeClient c = new ReqRespRuntimeClient();
        System.exit(c.runtest(url));
     }  

     int runtest(String urlString)  {
    	 ReqRespRuntimeCheck s = null;
         String response = null;
    	 if (urlString == null ){
             System.out.println("using default wsdl url");
             s = new ReqRespRuntimeCheckService().getReqRespRuntimeCheckPort();
         } else {
             System.out.println("using supplied wsdl url: "+urlString);
             URL url = null;
             try {
                 url = new URL(urlString);
             } catch (MalformedURLException e) {
                 e.printStackTrace();
             }
             QName qn = new QName("http://server.reqrespwrappers.annotations/", "ReqRespRuntimeCheckService");

             s = new ReqRespRuntimeCheckService(url, qn).getReqRespRuntimeCheckPort();
         }
    	boolean fail = false;
        
        try{
	        System.out.println("calling echo1 method");
	        response = s.echo1("hello");
	        System.out.println("received response: "+response);
	        if (!response.contains("you passed me a string: hello")){
	        	System.out.println("unexpected response ");
	        	fail = true;        	
	        }
	        else System.out.println("good response");
        } catch (WebServiceException e){
            System.out.println("caught exception");
            e.printStackTrace();
            fail = true;  
        } catch( Throwable e){
            System.out.println("caught exception");
            e.printStackTrace();
            fail = true;
        }
        
        try{
            System.out.println("calling echo1B method");
            response = s.echo1B("hello");
            System.out.println("received response: "+response);
            if (!response.contains("you passed me an Object")){
                System.out.println("unexpected response ");
                fail = true;            
            }
            else System.out.println("good response");
        } catch (WebServiceException e){
            System.out.println("caught exception");
            e.printStackTrace();
            fail = true;  
        } catch( Throwable e){
            System.out.println("caught exception");
            e.printStackTrace();
            fail = true;
        }

            
            
        
        try{
            System.out.println("calling echo3 method");            
            response = s.echo3(new String("hello there echo 3"));
    
            System.out.println("received response: "+response);
            if (!response.contains("you passed me an object: hello")){
                System.out.println("unexpected response ");
                fail = true;            
            }
            else System.out.println("good response");
        } catch (WebServiceException e){
            System.out.println("caught exception");
            e.printStackTrace();
            fail = true;  
        } catch( Throwable e){
            System.out.println("caught exception");
            e.printStackTrace();
            fail = true;
        }
        
        try{
            // because the method got unwrapped, the signature gets strange.
            Notarg0 n = new Notarg0();
            System.out.println("calling echo2 method ");
            System.out.println("We don't expect success, but we want to see failures logged.");
            n.setArg0( "echo2, are you out there");
            Notresponse  response2 = s.echo2(n);
            response = response2.getReturn();
    
            System.out.println("received response: "+response);
            // MIGRATION CHANGE: echo2 should work if calling like above, the expect response include
            // "you passed me an object: echo2"
            if (!response.contains("you passed me an object: echo2")){
                System.out.println("unexpected response ");
                fail = true;            
            }
            else System.out.println("good response");
        } catch (WebServiceException e){
            System.out.println("caught exception");
            e.printStackTrace();
            //fail = true;  
        } catch( Throwable e){
            System.out.println("caught exception");
            e.printStackTrace();
            //fail = true;
        }



        if (fail){
            System.out.println("one or more invocations failed.");
            return 12;
        }
        return 0;
    }

}
