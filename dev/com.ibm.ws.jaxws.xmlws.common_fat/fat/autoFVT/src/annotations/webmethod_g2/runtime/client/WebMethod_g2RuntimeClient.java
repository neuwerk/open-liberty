package annotations.webmethod_g2.runtime.client;
import annotations.webmethod_g2.runtime.server.*;
import java.net.*;
import javax.xml.namespace.QName;

/**
 * a class to check runtime handling of @WebService parameters.
 *
 * @author btiffany
 * @param - url to wsdl file, optional
 */
public class WebMethod_g2RuntimeClient{
    public static void main (String [] args){
       String url=null;
       if (args.length > 0 ) url = args[0];
       System.out.println("\n Would return this string to testcase: "+runme(url));
 
    }
    
    public static String runme(String url){
        WebMethod_g2RuntimeClient c = new WebMethod_g2RuntimeClient();
        return(c.runtest(url));    	
    }

     // returns a string listing names of methods that invoked ok.
     String runtest(String urlString){

        WebMethodRuntimeAnnotatedCheck s = null;
        String result = "";
        
        if (urlString == null ){
            System.out.println("using default wsdl url");
            s = new WebMethodRuntimeAnnotatedCheckService().getWebMethodRuntimeAnnotatedCheckPort();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            QName qn = new QName("http://server.runtime.webmethod_g2.annotations/", "WebMethodRuntimeAnnotatedCheckService");
            s = new WebMethodRuntimeAnnotatedCheckService(url,qn).getWebMethodRuntimeAnnotatedCheckPort();
        }
        
        String response = null;
        try{        
        	System.out.println("calling noAnnoMethod");
	        response = s.noAnnoMethod("calling noAnnoMethod");
	        System.out.println("received response: "+response);
	        //result += " noAnnoMethod";
	        result += response;
        } catch (Exception e){
        	System.out.println("caught exception invoking noAnnoMethod:\n");
        	e.printStackTrace(System.out);
        	
        
        }

        try{
        	System.out.println("calling annoMethod");
	        response = s.annoMethod("calling annoMethod");
	        System.out.println("received response: "+response);
	        //result += " annoMethod";
	        result += response;
     	} catch (Exception e){
     		System.out.println("caught exception invoking annoMethod");
     		e.printStackTrace(System.out);
     	}

        try{
        	System.out.println("calling annoMethod2");
	        response = s.annoMethod2("calling annoMethod2");
	        System.out.println("received response: "+response);
	        //result += " annoMethod2";
	        result += response; 
        } catch( Exception e){
        	System.out.println("caught exception on invoking annoMethod2");
        	e.printStackTrace(System.out);
        }
        
        return result;

    }

}
;