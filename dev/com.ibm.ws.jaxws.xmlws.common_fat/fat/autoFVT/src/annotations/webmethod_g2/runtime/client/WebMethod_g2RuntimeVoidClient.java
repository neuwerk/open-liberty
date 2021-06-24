package annotations.webmethod_g2.runtime.client;
import annotations.webmethod_g2.runtime.server2.*;
//import annotations.webmethod_g2.runtime.server.*;
import java.net.*;
import javax.xml.namespace.QName;

/**
 * a class to check runtime handling of @WebMethod parameters.
 * Note that it has to be in a differnt package from the other client
 * to prevent namespace collisions.
 *
 * @author btiffany
 * @param - url to wsdl file, optional
 */
public class WebMethod_g2RuntimeVoidClient{
    public static void main (String [] args){
       String url=null;
       if (args.length > 0 ) url = args[0];
       System.out.println("\n Would return this string to testcase: "+runme(url));
 
    }
    
    public static String runme(String url){
        WebMethod_g2RuntimeVoidClient c = new WebMethod_g2RuntimeVoidClient();
        return(c.runtest(url));    	
    }

     // returns a string listing names of methods that invoked ok.
     String runtest(String urlString){

        WebMethodRuntimeVoidCheck s = null;
        
        
        if (urlString == null ){
            System.out.println("using default wsdl url");
            s = new WebMethodRuntimeVoidCheckService().getWebMethodRuntimeVoidCheckPort();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // http://server2.runtime.webmethod_g2.annotations", "WebMethodRuntimeVoidCheckService"
            QName qn = new QName("http://server2.runtime.webmethod_g2.annotations", "WebMethodRuntimeVoidCheckService");
            s = new WebMethodRuntimeVoidCheckService(url,qn).getWebMethodRuntimeVoidCheckPort();
        }
        
        String result = "";
        try{
            String response;
            String expected;
            System.out.println("calling echo");
            response = s.echo("Hello echo, are you there");
            expected = "echo replies: Hello echo, are you there";
            System.out.println("Recieved response: "+response);
            System.out.println("Expected response: "+expected);
            if (response.compareTo(expected)!=0){
                System.out.println("ERROR: unexpected response");
                result +=" error on call to echo";
            }  
            
            System.out.println("calling method with annotation-modified opName,  echoModified");
            response = s.echoModified("Hello echo, are you there");
            expected = "echo2 replies: Hello echo, are you there";
            System.out.println("Recieved response: "+response);
            System.out.println("Expected response: "+expected);
            if (response.compareTo(expected)!=0){
                System.out.println("ERROR: unexpected response");
                result=" +error occured on call to echoModified";
            }  
                
        } catch (Exception e){
            System.out.println("caught exception invoking normal methods:\n");
            e.printStackTrace(System.out);     
            result += " invocation failed on a normal method ";
        }     
        
        try{
            System.out.println("calling overloaded method echoInt");
            int iresp = s.echoInt(33);
            System.out.println("Received response: "+String.valueOf(iresp));
            System.out.println("Expected response: 33");
            if(iresp != 33){
                System.out.println("ERROR: unexpected response");
                result += " error occured on call to echoInt";
            }  
        }catch (Exception e){
            System.out.println("caught exception invoking overloaded method:\n");
            e.printStackTrace(System.out);     
            result+= " received exception invoking overloaded method echoInt";
            
        }
            
       try{     
        	System.out.println("calling voidReturnMethod");
	        s.voidReturnMethod("calling voidReturnMethod");
	        System.out.println("voidReturnMethod returned without exception, that's good");	       
        } catch (Exception e){
        	System.out.println("caught exception invoking voidReturnMethod:\n");
        	e.printStackTrace(System.out);     
            result += " received exception invoking void return method ";
        }
        if (result.compareTo("")==0) result="success";
        return result;
    }

}
;