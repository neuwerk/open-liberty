package annotations.webmethod_g2.runtime_beta.client;
import annotations.webmethod_g2.runtime_beta.server.*;
import java.net.*;
import javax.xml.namespace.QName;

/**
 * a class to check runtime handling of @WebService parameters.
 *
 * @author btiffany
 * @param - url to wsdl file, optional
 */
public class WebMethodg2BetaClient{
    public static void main (String [] args){
       String url=null;
       if (args.length > 0 ) url = args[0];
       System.out.println("\n Would return this string to testcase: "+runme(url));
 
    }
    
    public static String runme(String url){
        WebMethodg2BetaClient c = new WebMethodg2BetaClient();
        return(c.runtest(url));    	
    }

     // returns a string listing names of methods that invoked ok.
     String runtest(String urlString){

        InterfaceTestIf s = null;
        String result = "";
        
        if (urlString == null ){
            System.out.println("using default wsdl url");
            s = new InterfaceTestImplService().getInterfaceTestImplPort();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            QName qn = new QName("http://server.runtime_beta.webmethod_g2.annotations/", "InterfaceTestImplService");
            s = new InterfaceTestImplService(url,qn).getInterfaceTestImplPort();
        }
        
        String response = null;
        try{        
        	System.out.println("calling methodOne");
	        response = s.methodOne("calling methodOne");
	        System.out.println("received response: "+response);
	        result += response;
        } catch (Exception e){
        	System.out.println("caught exception invoking methodOne:\n");
        	e.printStackTrace(System.out);        
        }

        try{
        	System.out.println("calling methodTwo");
	        response = s.methodTwo("calling methodTwo");
	        System.out.println("received response: "+response);
	        result += response;
     	} catch (Exception e){
     		System.out.println("caught exception invoking methodTwo:");
     		e.printStackTrace(System.out);
     	}
        
        // bonus point here
        try{
            //String s, boolean b, Double d, int i, float f, char c)
            System.out.println("calling mixedTypes method");
            response = s.mixTypes("a string",
                                    false,
                                    (double)123.456,
                                    45,
                                    (float)234.567,
                                    'c');
            System.out.println("received response: "+response);
            // we don't want this in the result.
           
            
        } catch (Exception e){
            System.out.println("caught exception invoking mixedTypes:");
            e.printStackTrace(System.out);
        }
        
        
        return result;

    }

}
;