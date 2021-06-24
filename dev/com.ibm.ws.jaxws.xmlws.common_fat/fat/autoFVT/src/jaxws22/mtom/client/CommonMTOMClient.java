// IBM Confidential OCO Source Material
// 
//  @(#) 1.1 autoFVT/src/jaxws22/mtom/client/CommonMTOMClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/2/10 11:50:50 [8/8/12 06:57:49]
// 
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date           UserId      Defect           Description
// ----------------------------------------------------------------------------
// 3/01/2010      btiffany    641610           new file

package jaxws22.mtom.client;
import javax.xml.ws.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.soap.*;
import javax.xml.ws.http.*;
import javax.xml.ws.soap.MTOMFeature;


import javax.xml.ws.spi.*;
import javax.xml.ws.spi.Provider;


/**
 * A client to test the mtom feature in various containers.
 * 
 *
 */
public class CommonMTOMClient {    
    
    // the handler will set these so we can examine the message for correctness.
    public static SOAPMessage outboundmsg;
    public static SOAPMessage inboundmsg;
    
    // this can be reset by the testcase to something else.
    public static String hostAndPort = "http://localhost:8080";

     /**
     *  For debug use.  The tests should invoke the individual methods.
     */
    public static void main(String[] args) throws Exception {
                
        Object pr = javax.xml.ws.spi.Provider.provider();      
        Class theImpl = pr.getClass();        
        System.out.println(theImpl.getName());
        
        //byte [] b = "I am a byte array, so there.".getBytes();
        ManagedClientDriver driver = new ManagedClientDriver();
        byte [] b = driver.genByteArray(250);
       byte [] c = testProxy("MTOMonMultipleMethodsAnnotationOnlyService_echobyte64", b);
       
       //System.out.println("mtom enabled:" + driver.)
       
      
        System.out.println(new String(c));
        System.out.println("\n outbound:");
        outboundmsg.writeTo(System.out);
        System.out.println("\n inbound:");
        inboundmsg.writeTo(System.out);
        System.out.println("\n ");        

        
     
    }   
    
    public static byte []  testProxy(String service, byte [] b, WebServiceFeature... features) throws Exception {
        URL u = null;
        System.out.println("===== service selected is: "+service);
        if(service == "MTOMDDOnly"){
            // service config: enabled, threshold=2048
            QName q = new QName("http://test.com/", "MTOMDDOnlyService");
            u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService?wsdl");
            // need this when jdk is readY: Service s = Service.create(u, q, features);
            Service s = Service.create(u, q, features);            
            MTOMDDOnlyIF port = s.getPort(MTOMDDOnlyIF.class);
            addHandler((BindingProvider)port);  // install the monitoring handler
            return (port.echobyte(b));
            
        } else if(service=="MTOMonMultipleMethodsDDOverideService") {                            
            // service config: disabled, method annotation=enabled 1024
            // actually only contains one method.  Oops. 
            QName q = new QName("http://test.com/", "MTOMonMultipleMethodsDDOverrideService");
            u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMonMultipleMethodsDDOverrideService?wsdl");
            Service s = Service.create(u, q);
            MTOMonMultipleMethodsDDOverrideIF port = s.getPort
                (MTOMonMultipleMethodsDDOverrideIF.class);
            addHandler((BindingProvider)port);  // install the monitoring handler
            return (port.echobyte(b));                   
            
            
        } else if(service.contains("MTOMonMultipleMethodsAnnotationOnlyService")){
                // this one has three methods to check overriding of method level annotations.
                // we'll control selection by passing servicename_method
                // mtom is enabled on the ...64 and ..256
                // that turned out to be an invalid test but we'll use this for
                // testing empty dd and empty annotation
                QName q = new QName("http://test.com/", "MTOMonMultipleMethodsAnnotationOnlyService");                                                         
                u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMonMultipleMethodsAnnotationOnlyService?wsdl");
                // need this when jdk is readY: Service s = Service.create(u, q, features);
                Service s = Service.create(u, q, features);
                MTOMonMultipleMethodsAnnotationOnlyIF port = s.getPort(MTOMonMultipleMethodsAnnotationOnlyIF.class);
                addHandler((BindingProvider)port);  // install the monitoring handler                
                    
                //if( service.contains("echobyteNoMTOM")){return (port.echobyteNoMTOM(b));}    
                //if( service.contains("echobyte64")){return (port.echobyte64(b));}    
                //if( service.contains("echobyte256")){return (port.echobyte256(b));}
                return port.echobyte(b);                
                
   
            
        } else if(service=="MTOMDDNoMTOM") {
            // service config: disabled, annotation=enabled, default values
            QName q = new QName("http://test.com/", "MTOMDDNoMTOMService");
            u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDNoMTOMService?wsdl");
            Service s = Service.create(u, q);
            MTOMDDNoMTOMIF port = s.getPort(MTOMDDNoMTOMIF.class);
            addHandler((BindingProvider)port);  // install the monitoring handler
            return (port.echobyte(b));
            
        } else if(service=="MTOMDDOverride") {
            // service config: true, 3072 (annotation is false, 1024)
            QName q = new QName("http://test.com/", "MTOMDDOverrideService");
            u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOverrideService?wsdl");
            Service s = Service.create(u, q);
            MTOMDDOverrideIF port = s.getPort(MTOMDDOverrideIF.class);
            addHandler((BindingProvider)port);  // install the monitoring handler
            return (port.echobyte(b));
                
            
        } else if(service=="MTOMAnnotationOnly") {
            // service config: enabled, 0
            QName q = new QName("http://test.com/", "MTOMAnnotationOnlyService");
            u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMAnnotationOnlyService?wsdl");
            Service s = Service.create(u, q);
            MTOMAnnotationOnlyIF port = s.getPort(MTOMAnnotationOnlyIF.class);
            addHandler((BindingProvider)port);  // install the monitoring handler
            return (port.echobyte(b));
            
        } else if(service=="MTOMAnnotationNoMTOM") {
            // service config: disabled
            QName q = new QName("http://test.com/", "MTOMAnnotationNoMTOMService");
            u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMAnnotationNoMTOMService?wsdl");
            Service s = Service.create(u, q);
            MTOMAnnotationNoMTOMIF port = s.getPort(MTOMAnnotationNoMTOMIF.class);
            addHandler((BindingProvider)port);  // install the monitoring handler
            return (port.echobyte(b));   
            
        } else if(service=="BindingTypeMTOMAnnotationOnly") {
            // service config: disabled
            QName q = new QName("http://test.com/", "BindingTypeMTOMAnnotationOnlyService");
            u = new URL( hostAndPort+"/"+ "webservicefeaturetests/BindingTypeMTOMAnnotationOnlyService?wsdl");
            Service s = Service.create(u, q);
            BindingTypeMTOMAnnotationOnlyIF port = s.getPort(BindingTypeMTOMAnnotationOnlyIF.class);
            addHandler((BindingProvider)port);  // install the monitoring handler
            return (port.echobyte(b));
            
        } else if(service=="BindingTypeMTOMAnnotationDDOverride") {
                       
            // service config: disabled
            QName q = new QName("http://test.com/", "BindingTypeMTOMAnnotationDDOverrideService");
            u = new URL( hostAndPort+"/"+ "webservicefeaturetests/BindingTypeMTOMAnnotationDDOverrideService?wsdl");
            Service s = Service.create(u, q);
            BindingTypeMTOMAnnotationDDOverrideIF port = s.getPort(BindingTypeMTOMAnnotationDDOverrideIF.class);
            addHandler((BindingProvider)port);  // install the monitoring handler
            return (port.echobyte(b));               
                
        } else {
            throw new RuntimeException("bad argument to CommonMTOMClient");
                       
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
        }

        handlerList.add(new MessageCaptureHandler());        

        binding.setHandlerChain(handlerList);
        
        // clear our static vars, prep for invoke
        inboundmsg = null;
        outboundmsg = null;
    }
    
    public void printMsg(SOAPMessage msg){
        try{           
                msg.writeTo(System.out);                   
            } catch( SOAPException e){
                System.out.println("exception writing soap mesage");                
            } catch (java.io.IOException e){
                System.out.println("ioexception writing soap mesage");                
            }
    }
    
    public String getInboundMsgAsString(){
        java.io.ByteArrayOutputStream baos = null;
        try{
            baos =new java.io.ByteArrayOutputStream(); 
            inboundmsg.writeTo(baos);
            
        }catch (Exception e){
            e.printStackTrace(System.out);
        }    
        return baos.toString();
    }
    
    public String getOutboundMsgAsString(){
        java.io.ByteArrayOutputStream baos = null;
        try{
            baos =new java.io.ByteArrayOutputStream(); 
            outboundmsg.writeTo(baos);
            
        }catch (Exception e){
            e.printStackTrace(System.out);
        }    
        return baos.toString();
    }

}
