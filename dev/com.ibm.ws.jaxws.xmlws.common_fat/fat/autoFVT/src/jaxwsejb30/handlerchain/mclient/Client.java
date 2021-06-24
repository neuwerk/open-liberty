package jaxwsejb30.handlerchain.mclient;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import jaxwsejb30.handlerchain.server.*;

import javax.xml.ws.WebServiceRef;
import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.xml.ws.WebServiceException;
import javax.jws.*;

// in thin client, this annotation doesn't work on clients, but it does on managed.
// defect: no error if handler file does not exist.
@HandlerChain(file = "mchandlerchain.xml") 
public class Client {
    
    // perform resource injection to set the handler chain - might work, works on class as well.
    // ref http://java.sun.com/mailers/techtips/enterprise/2006/TechTips_Sept06.html
    //@HandlerChain(file = "mchandlerchain.xml") 
    
    // perform resource injection to get the service-ref.
    // try this in a thin client and nothing happens, but in the managed client - magic!
    // we'll bundle the wsdl with the app to avoid having to set the port.
    @WebServiceRef(name = "WarEchoAnnoImplService", wsdlLocation = "META-INF/wsdl/WarEchoAnnoImplService.wsdl")
    static  WarEchoAnnoImplService service;
    

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        (new Client()).run();
    }

    public void run(){
        java.net.URL url = null;     
        WarEchoAnnoImpl s = service.getWarEchoAnnoImplPort();
        System.out.println("invoking impl1Echo");
        String result = s.echo ("hello");  
        System.out.println("server returned: "+result);
        // if override did not work, we would get 
        //helloWASSOAPHandler_Inbound:WASSOAPHandler_Outbound
        //String expected = "helloAnotherHandler_Inbound:AnotherHandler_Outbound";
        //System.out.println("expected= "+ expected);
    }


}
