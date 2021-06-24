package annotations.soapbinding.testdata;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.ws.*;
import junit.framework.*;

import javax.xml.namespace.QName;

import annotations.soapbinding.testdata.serverrpc.*;
/**
 * a class to check runtime handling of @SoapBinding parameters.
 * 
 * goes with SoapBindRtImpl on the server side.
 * 
 * @author btiffany
 *
 */
public class SoapBindRpcIfClient{
    String resultString = "failed";
    public static void main (String [] args){
        String url=null; 
        System.out.println("client is invoked");
        if (args.length > 0 ) url = args[0];
        SoapBindRpcIfClient c = new SoapBindRpcIfClient();
        System.exit(c.runtest(url));
     }  
    
    int runme(String urlString){
    	SoapBindRpcIfClient c = new SoapBindRpcIfClient();
    	return(c.runtest(urlString));
    }
    
    public String getResult(String urlString){        
        int rc = runtest(urlString);
        if (rc != 0) return "rc="+String.valueOf(rc);
        return resultString; 
    }

    public int runtest(String urlString)  {
         annotations.soapbinding.testdata.serverrpc.SoapBindRpcIf s = null;
         String response = null;
    	 if (urlString == null ){
             System.out.println("using default wsdl url");
             s = new SoapBindRpcImplService().getSoapBindRpcImplPort();
         } else {
             System.out.println("using supplied wsdl url: "+urlString);
             URL url = null;
             try {
                 url = new URL(urlString);
             } catch (MalformedURLException e) {
                 e.printStackTrace();
             }
             QName qn = new QName("http://serverrpc.testdata.soapbinding.annotations/", "SoapBindRpcImplService");
             s = new SoapBindRpcImplService(url, qn).getSoapBindRpcImplPort();
             //s = new SoapBindAnno3Service().getSoapBindAnno3Port();
             //s = new WebMethodRuntimeService(url, qn).getWebMethodRuntimePort();
         }

        try{
            
            // check basic service
            System.out.println("calling echo");
            response = s.echo("hello there echo");
            
            // check use of a HOLDER
            // holder should come back changed, containing "holder updated"
            String input = "hello there holder";
            Holder<Object> h = new Holder( input);
            s.echoHolder(h);
            System.out.println("calling holder");
            response = (String)h.value;
            System.out.println("holder method received response: "+response);
            Assert.assertTrue("holder method returned wrong value",response.contains(input));
            Assert.assertTrue("holder method returned wrong value",response.contains("Holder updated"));
            
            boolean ok = false;
            try{
                System.out.println("invoking echo with null, expecting exception");
                s.echo(null);                
            } catch (WebServiceException e){
                System.out.println("caught expected WebserviceException");
                e.printStackTrace();
                System.out.flush(); System.err.flush();
                ok = true;
            }
            Assert.assertTrue("Wrong/No exception received for null invoke", ok);
            
            ok = false;
            try{
                System.out.flush(); System.err.flush();
                System.out.println("invoking method that returns null, expecting exception");                
                s.returnNull("hello there null");                
            } catch (WebServiceException e){
                System.out.println("caught expected WebserviceException");
                e.printStackTrace();
                System.out.flush(); System.err.flush();
                ok = true;
            }
            Assert.assertTrue("Wrong/No exception received for null return", ok);

  	    // set different rc for webserviceexception, so we can confirm we rcvd. it. 
        } catch (WebServiceException e){
        	e.printStackTrace();
        	return 23;  
        } catch( Throwable e){
        	e.printStackTrace();
        	return 4;
        }
        return 0;
    }

}
