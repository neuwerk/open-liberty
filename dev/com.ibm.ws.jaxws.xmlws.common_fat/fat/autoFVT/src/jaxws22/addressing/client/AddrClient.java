/**
 * This client is used by the service side test to exercise services and determine
 * their addressing configuration.  This can be done in a couple of ways, by 
 * sending messages that should be rejected, and/or by examining the server response
 * soap message to determine if the proper addressing headers are present.
 * 
 * Multiple services with various addressing configs have been deployed, the service
 * to invoke is specified by selecting the context root of the service.
 * 
 */

package jaxws22.addressing.client;
import jaxws22.addressing.server.*;

import java.io.*;


import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.soap.*;
import com.ibm.ws.wsfvt.build.tools.*;
import com.ibm.websphere.wsaddressing.EndpointReference;
import com.ibm.wsspi.wsaddressing.AttributedURI;
import com.ibm.wsspi.wsaddressing.EndpointReferenceManager;
import com.ibm.wsspi.wsaddressing.WSAConstants;
import com.ibm.wsspi.wsaddressing.WSAddressingFactory;
import javax.xml.ws.soap.*;

public class AddrClient {
    public static String hostAndPort = "you need to set host and port";
    static String wsdldir = AppConst.FVT_HOME + "/src/jaxws22/addressing/client/wsdl";
    
    // for debug use only
    public static void main( String [] args) throws java.lang.Exception {
        AddrClient c = new AddrClient();
        c.hostAndPort = "http://localhost:9080";
        System.out.println( c.invokeNoAddr("annoOnImplicitDDOff", "Mondays....")); 
       // System.out.println( c.invokeNoAddr("annoOffDDOn", "greetings") );
       // System.out.println("outbound:" + c.getOutboundMsgAsString());
       // System.out.println("inbound:" + c.getInboundMsgAsString());
       // System.out.println("\n\n\n\n\n");
      //  System.out.println( c.invokeAddrNonAnonymous("annoOnDDOff", "SEND_BACK_MY_HEADERS"));
      //  System.out.println( c.invokeNoAddr("example", "frabnoxxxxxxxxxcasteer"));
     //   System.out.println( c.invokeAddrNonAnonymous("example", "frabnoxxxxxxxxxcasteer"));
        System.out.println("outbound:" + c.getOutboundMsgAsString());
        System.out.println("inbound:" + c.getInboundMsgAsString());
    }
    
   
    
    public String invokeNoAddr(String contextroot, String in) throws java.lang.Exception {
        // TODO: throw exception if message not echoed intact        
        // EchoService s = new EchoService(new URL(hostAndPort+"/annoOffDDOn/EchoService?wsdl"));
        init();
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoService.wsdl"));
        Echo port = s.getEchoPort();
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));        
        addHandler((BindingProvider)port);  // install the monitoring handler
        return port.echo(in);
    }
    
    public String invokeAddrAnonymous(String contextroot, String in) throws java.lang.Exception {
        init(); 
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoServiceRequiredAnon.wsdl"));
        Echo port = s.getEchoPort();
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));        
        addHandler((BindingProvider)port);  // install the monitoring handler
        return port.echo(in);
    }
    
    public String invokeAddrAnonymousWithFeature(String contextroot, String in) throws java.lang.Exception {
        init(); 
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoServiceRequiredAnon.wsdl"));
        Echo port = s.getEchoPort(new AddressingFeature(true, false, AddressingFeature.Responses.ALL));
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));        
        addHandler((BindingProvider)port);  // install the monitoring handler
        return port.echo(in);
    }
    
    
    // removed due  to the complexity of implementing a non-anonymous request.
    // we can check another way, by setting service to non-anonymous and verifying that anonymous fails.
    private String _invokeAddrNonAnonymous(String contextroot, String in) throws java.lang.Exception {
        // TODO: throw exception if message not echoed intact        
        // EchoService s = new EchoService(new URL(hostAndPort+"/annoOffDDOn/EchoService?wsdl"));
        init();
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoServiceRequiredNonAnon.wsdl"));
        Echo port = s.getEchoPort();
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));        
        addHandler((BindingProvider)port);  // install the monitoring handler
        
        // it's not enough to just point at a wsdl, we have to add a ReplyTo header on the request. 
        // it turns out that's still not enough, this isn't legal on a 2-way mep.  Argh! 
        Map<String, Object>requestContext =((BindingProvider)port).getRequestContext();
        AttributedURI uri = WSAddressingFactory.createAttributedURI(new URI(hostAndPort));
        EndpointReference epr = EndpointReferenceManager.createEndpointReference(uri);
        requestContext.put(WSAConstants.WSADDRESSING_REPLYTO_EPR, epr);
        
        // now invoke it.
        return port.echo(in);
    }
    
    private void init(){
        // debug accomodation
        if ( wsdldir.contains("null")){
            wsdldir = "/test/wasx_kk1003.32/autoFVT/src/jaxws22/addressing/client/wsdl";
        }       
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
    
    
    public String getInboundMsgAsString(){
       return MessageCaptureHandler.getInboundMsgAsString();
    }
    
    // because addressing handler runs after application handlers, the 
    // client's addressing headers will NOT be retrievable here. 
    public String getOutboundMsgAsString(){
        return MessageCaptureHandler.getOutboundMsgAsString();        
    }
    
    /**
     * update the endpoint url
     */
    private static void updateEndpointURL(String serviceURL, BindingProvider port){        
        Map<String, Object> rc = ((BindingProvider) port)
            .getRequestContext();         
         rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL);        
    }
    
    public boolean wasAddressingRequiredException(java.lang.Exception e) {
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        e.printStackTrace(pw);
//        pw.flush();
//        System.out.println(sw.toString());
//        return (sw.toString().contains("A required header representing a Message Addressing Property is not present")
//                || sw.toString().contains("A header representing a Message Addressing Property is not valid")) ;
    	String soapresp=this.getInboundMsgAsString();
    	return (soapresp.contains("MessageAddressingHeaderRequired")||soapresp.contains("OnlyNonAnonymousAddressSupported"));
    }    
    
    public boolean wasAddressingUsedinResponse(){
        return this.getInboundMsgAsString().contains("http://www.w3.org/2005/08/addressing");
    }
    
    /**
     * examines the response message and determines if response addressing
     * information was anonymous or not. 
     * @return
     * @throws java.lang.Exception
     */
    public boolean wasResponseNotAnonymous() throws java.lang.Exception {
        if(true){ throw new RuntimeException("implement me");}
        return false;
    }    

}
