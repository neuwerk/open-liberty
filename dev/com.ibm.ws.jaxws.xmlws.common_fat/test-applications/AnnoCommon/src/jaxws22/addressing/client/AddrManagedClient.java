package jaxws22.addressing.client;

import jaxws22.addressing.server.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.ws.*;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.*;

import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * This class will invoke a variety of clients configured
 * with various deployment descriptors and annotations, 
 * producing results for evaluation by a junit test.
 *
 * Evaluating proper behavior often involves inspecting the outbound message
 * to look at the Addressing headers.  However, an application handler cannot capture those,
 * because the Addressing handler runs later in the chain.  So, to see the headers,
 * we call the annoOnDDOff service with a special message, SEND_BACK_MY_HEADERS,
 * which will cause it to capture and return the client's headers for examination.
 * This is actually simpler than implementing some sort of tcpmon-like proxy system. 
 * 
 * Collecting the invocation and evaluation logic here rather than in a test case 
 * will allow it to be packaged in either a thin client jar, a war, or an
 * ejb ear with minimal changes. 
 * 
 * @author btiffany
 *
 */
public class AddrManagedClient {
    public static String hostAndPort = "you need to set this";
    
    // an inner class we use to specify test conditions
    public class TestConditions{
        boolean outboundRequired = false;  // are headers required in request? 
        boolean outboundAllowed = false;   // are headers allowed in request? 
        boolean exceptionExpected = false;  // is an exception expected? 
    }
    PrintStream os = null;
    
    
     
    // The only way to test if we can turn things off on the client side with 
    // a deployment descriptor is to turn them on with resource injection.
    // dd should win.     
    @Addressing(enabled=true, required = true)
    @WebServiceRef(name="service/ClientAddrOnDDOff")    
    public static jaxws22.addressing.server.Echo annoOnDDOffPort;
    
    @WebServiceRef(name="service/ClientAddrOnDDEnabled")
    @Addressing(enabled=true, required = true)
    public static jaxws22.addressing.server.Echo annoOnDDEnabledPort;
    
    // do a jndi lookup.
    // this service-ref contains no addressing configuration. 
    //@WebServiceRef(name="service/ClientAddrOnNoDD")
    // or, we can simulate jndi like this: 
    @WebServiceRef(wsdlLocation="META-INF/wsdl/EchoService.wsdl", value=jaxws22.addressing.server.EchoService.class)
    // add addressing configuration 
    @Addressing(enabled=true, required = true)
    public static jaxws22.addressing.server.Echo annoOnNoDDPort;
    
    // since the above one is busted, let's see if we can turn things on through resource injection.
    @WebServiceRef(name="service/ClientAddrOffDDOn")
    public static jaxws22.addressing.server.Echo annoOffDDOnPort;
    
    // required should get turned off by dd. 
    @Addressing(enabled=true, required = true)
    @WebServiceRef(name="service/ClientAddrOnImplicitDDOff")
    public static jaxws22.addressing.server.Echo annoOnImplicitDDOffPort;
    
    
    /**
     * @param args
     */
    public static void main(String[] args) throws java.lang.Exception  {
        if (args.length == 0){
            System.out.println("hostAndPort argument required.  Example: http://localhost:9080"); 
            return;
        }
        AddrManagedClient.hostAndPort = args[0];
        (new AddrManagedClient()).performTests(System.out);
    }
    
    public void performTests(PrintStream os) throws java.lang.Exception {
        this.os = os;
        
        // perform each test in sequence. 
        
        // no extension dd true, should invoke
        TestConditions tc = new TestConditions();
        // this first test, dd is turning addressing off, so there
        // should be no headers outbound. 
        lookupAndInvoke("ClientAddrOffDDOff_ServiceOff", tc );
        
        // no configuration through dd, this is just testing the webservice ref injection and @addressing annotation.
        tc.exceptionExpected = false;
        tc.outboundRequired = true;
        tc.outboundAllowed = true;
        lookupAndInvoke("ClientAddrOnNoDD_ServiceOn",tc, annoOnNoDDPort);
        
        // configuration through dd, this is just testing the webservice ref injection
        tc.exceptionExpected = false;
        tc.outboundRequired = true;
        tc.outboundAllowed = true;
        lookupAndInvoke("InjectedClientAddrOffDDOn_ServiceOn",tc, annoOffDDOnPort);
        
        // test dd can turn off required implicitly.
        // anno should be present on request but not required in response
        tc.exceptionExpected = false;
        tc.outboundRequired = true;
        tc.outboundAllowed = true;        
        lookupAndInvoke("ClientAddrOnImplicitDDOff_ServiceOff",tc, annoOnImplicitDDOffPort);
        
        // here there should be headers outbound 
        tc = new TestConditions();
        tc.outboundRequired = true;
        tc.outboundAllowed = true;
        tc.exceptionExpected = false;
        
        lookupAndInvoke("ClientAddrOffDDOn_ServiceOn", tc );
        
        // here we should get an exception because the service does not return headers
        // the exception prevents us from inspecting the outbound headers here. 
        tc.outboundRequired = true;
        tc.outboundAllowed = true;
        tc.exceptionExpected = true;
        lookupAndInvoke("ClientAddrOffDDOn_ServiceOff", tc);
        
        // how do we configure a client with addressing on?  We can't use the annotation
        // directly on the client.  The only way is to annotate an injected reference.
        
        tc.outboundRequired = false;
        tc.outboundAllowed = false;
        tc.exceptionExpected = false;
        lookupAndInvoke("ClientAddrOnDDOff_ServiceOff", tc, annoOnDDOffPort );
        
        tc.outboundRequired = true;
        tc.outboundAllowed = true;
        tc.exceptionExpected = false;
        lookupAndInvoke("ClientAddrOnDDEnabled_ServiceOff", tc, annoOnDDEnabledPort );
        
        
    } 
    
    /**
     *
     * 
     */
    private void lookupAndInvoke(String testname, TestConditions tc) throws java.lang.Exception {
        lookupAndInvoke(testname, tc , null);
    }
    
    /**
     * for a given client, look it up from  deployment descriptor file, attempt
     * to invoke a given service, and see if the request headers are consistent with the
     * deployment descriptors and annotations. 
     * 
     * Print results to the outoput stream. 
     * @param clientConfig_servicetoinvoke - name of test which matches (jndi name in deployment descriptor)_(servicename)
     * @param service - the service to invoke. 
     * @param tc  - what we should check for 
     * @param  port - port of service.  If null, we'll look it up, otherwise skip lookup and just use it.
     */
    private void lookupAndInvoke(String testname, TestConditions tc, Echo port) throws java.lang.Exception { 
        os.println("-------------------- begin invoke of "+ testname + " ------------------------------");    
       
        boolean caughtExc = false;
        int pos = testname.indexOf("_");
        String clientJNDI = testname.substring(0,pos); 
        String serviceURL =  serviceURL = hostAndPort + "/annoOnDDOff/EchoService";
        if (testname.contains("ServiceOn")){ serviceURL = hostAndPort + "/annoOnNoDD/EchoService"; }
        os.println("  client JNDI: "+ clientJNDI + " ServiceURL: "+ serviceURL);
        os.println("  outbound headers required: "+tc.outboundRequired + " expecting exception: "+ tc.exceptionExpected );
        
        if (port == null){
            InitialContext ctx = new InitialContext();
            port = (Echo)ctx.lookup("java:comp/env/service/"+ clientJNDI);
        } 
        updateEndpointURL(serviceURL, (BindingProvider)port );
        addHandler((BindingProvider)port);  // install the monitoring handler.  Too bad it can't see the outbound headers.
        
        String headers = null;
        try{
            headers = port.echo("SEND_BACK_MY_HEADERS "+ testname);
        } catch (SOAPFaultException sfe){  // a fault returned by the server will be one of these.
            caughtExc = true;
            os.println("caught a SOAPFAULT (server side) exception:");
            sfe.printStackTrace(os);            
        }catch (WebServiceException wse){  // a fault due to client side processing will be one of these. 
            caughtExc = true;
            os.println("caught an WEBSERVICE (client side) exception:");
            wse.printStackTrace(os);
        }    
        
        // if we were not expecting an exception, examine the headers.  But if we caught one, we can't do that.
        String detail = null;
        String result  = "*** FAILURE ***";
        if(!( tc.exceptionExpected && caughtExc)){
            os.println("--Outbound Headers:--\n"+ headers + "\n--end headers-- \n");
            os.println("--Outbound Message Body:-- \n" + MessageCaptureHandler.getOutboundMsgAsString() +"\n-- end body -- \n");
            String inboundMsg = MessageCaptureHandler.getInboundMsgAsString();
            os.println("--Inbound Message:--\n " + inboundMsg  +"\n-- end inboundmsg --\n");        
            detail = checkOutboundHeaders(headers, tc);        
           
            if (detail.compareTo("OK") ==0 ) result = "--- PASSED ---";
            // unless - we were supposed to catch an exception.
            if(tc.exceptionExpected){
                result = "*** FAILURE ***";
                detail = "Did not catch expected exception";
            }
            
        } else { // we caught an expected exception. 
            result = "--- PASSED ---";
            detail = "Caught expected exception";    
        }    
        os.println("\n" +testname + " result: " + result + " detail: "+detail +"\n");
        os.println("-------------------- end invoke ------------------------------\n");
        os.flush();
        return;
    }
    
    /**
     * install a handler on a port.  We'll use the handler to capture the soap message.
     * Much easier than traffic monitoring, etc.   
     * @param port
     */
    private static void addHandler(BindingProvider port){
        // set binding handler chain
        Binding binding = ((BindingProvider)port).getBinding();

        // can create new list or use existing one
        List<Handler> handlerList = binding.getHandlerChain();

        if (handlerList == null) {
            handlerList = new ArrayList<Handler>();
            System.out.println("new list");
        }

        handlerList.add(new MessageCaptureHandler());  
        binding.setHandlerChain(handlerList);
        //System.out.println("handler added");
    }
    
    
    /**
     * check the headers against the testcondition.  Return "OK"if things are ok,
     * or an explanatory message otherwise. 
     * @param headers
     * @param tc
     * @return
     */
    private String checkOutboundHeaders(String headers, TestConditions tc){
        String msg = "";
        if(headers == null || ( tc.outboundRequired && headers.length() <20) ){
            msg += "outbound headers not present but were required";
        }   
        
        if (headers!= null && headers.length() > 0 && tc.outboundAllowed == false) {
            msg += "outbound headers were found but not allowed";
        }
        
        if( msg.length()==0) { msg= "OK"; }
        return msg;
    }
    
  
    
    /**
     * update the endpoint url
     */
    private static void updateEndpointURL(String serviceURL, BindingProvider port){        
        Map<String, Object> rc = ((BindingProvider) port)
            .getRequestContext();         
         rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL);        
    }

}
