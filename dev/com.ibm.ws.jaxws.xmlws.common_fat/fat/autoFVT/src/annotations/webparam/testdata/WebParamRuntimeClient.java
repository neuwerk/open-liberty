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
public class WebParamRuntimeClient{
    boolean hadTrouble=false;
    public static void main (String [] args){
        String url=null;       
       if (args.length > 0 ) url = args[0];
       WebParamRuntimeClient c = new WebParamRuntimeClient();
       System.exit(c.runtest(url));
    }
    
    // set state if we have problem
    void trouble(boolean weHaveAProblem){
        if( ! weHaveAProblem )return;
        System.out.println("****trouble detected****");
        hadTrouble = true;
    }

     int runtest(String urlString){        
        WebParamGeneralChecks s = null;
        if (urlString == null ){
            System.out.println("using default wsdl url");
            s =new WebParamGeneralChecksService().getWebParamGeneralChecksPort();
        } else {
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            QName qn= new QName("http://testdata.webparam.annotations/", "WebParamGeneralChecksService");

            s = new WebParamGeneralChecksService(url, qn).getWebParamGeneralChecksPort();
        }
        // service should just echo whatever we send it.
        String input = "hello Ms. Server, how are you";
        
        // check benign use of name param
        System.out.println("invoking echoObject2");
        String response = (String) s.echoObject2(input);
        System.out.println("received response: "+response);
        trouble(!response.contains(input)); 
        
        // check use of a HOLDER
        // holder should come back changed, containing "holder updated"
        System.out.println("invoking echoObject3");
        Holder<Object> h = new Holder( input);
        s.echoObject3(h);
        response = (String)h.value;
        System.out.println("holder method received response: "+response);
        trouble(!response.contains(input));
        trouble(!response.contains("Holder updated"));

        // check benign IN mode param.
        System.out.println("invoking echoObject4");
        response = (String) s.echoObject4(input);
        System.out.println("IN param received response: "+response);
        trouble(!response.contains(input));

        // check variable in changed namespace
        System.out.println("invoking echoObject6");
        response = (String) s.echoObject6(input);
        System.out.println("modified namespace received response: "+response);
        trouble(!response.contains(input));

        // check parameter that comes back in the soap header
        // look at the generated class after wsimport runs to get
        // the correct method signature, which is really weird.
        System.out.println("invoking echoObject7");
        EchoObject7Response r =  s.echoObject7( new EchoObject7(),(Object)input);
        response = (String)r.getReturn();
        System.out.println("header message received response: "+response);
        trouble(!response.contains(input));        

        // check param with changed name and partName
        // meaningless for doclitwrapped.
        try{
            System.out.println("invoking echoObject8");
            response = (String) s.echoObject8(input);
            System.out.println("partname received response: "+response);
            trouble(!response.contains(input));
        } catch (Exception e){
            trouble(true);
            System.out.println("Caught exception");
            e.printStackTrace(System.out);
        }
        
        // check param with changed partName only
        // meaningless for doclitwrapped.
        System.out.println("invoking echoObject9");
        response = (String) s.echoObject9(input);
        System.out.println("partname received response: "+response);
        trouble(!response.contains(input));

        if(!hadTrouble) return 0;
        System.out.println("One or more invocations did not work");
        return 12;
    }

}
