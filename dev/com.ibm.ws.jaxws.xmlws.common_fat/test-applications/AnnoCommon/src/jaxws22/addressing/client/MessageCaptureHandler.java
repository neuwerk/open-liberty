//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId       Defect          Description
// ----------------------------------------------------------------------------
// 04/20/2007  mzheng       LIDB3296-40.01  New File
// 07/09/2007  mzheng       449269          Enable test cases
// 02/24/2010  btiffany                     borrow WASSOAPHandler for jaxws22 tests
//

package jaxws22.addressing.client;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.axis2.Constants;


/**
 * A handler to capture the soap message so we can see if 
 * various features are present in the message, such as mtom and addressing.
 */
public class MessageCaptureHandler implements SOAPHandler <SOAPMessageContext> {
    
    // a place to store the message in a non-thread-safe way to avoid the 
    // hassle of putting them on the message context. 
    public static SOAPMessage inboundmsg = null;
    public static SOAPMessage outboundmsg = null;
    
    @PostConstruct
    public void init() { }

    @PreDestroy
    public void destroy() { }
    
 

    /**
     * @see javax.xml.ws.handler.soap.Handler#getHeaders()
     */
    
    public Set<QName> getHeaders() {
        //System.out.println("getHeaders called");
        QName qn = new QName("http://www.w3.org/2005/08/addressing","To" );
        HashSet<QName> hs = new HashSet();
        hs.add(qn);
        return  hs;
    }
    


    /**
     * @see javax.xml.rpc.handler.Handler#handleFault(MessageContext)
     */
    public boolean handleFault(SOAPMessageContext smc) {
        boolean isOutbound = ((Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();
        SOAPMessage msg = smc.getMessage();
        System.out.println("handleFault called");
        if (isOutbound) {
            outboundmsg = msg;
        } else {
            inboundmsg = msg;            
        }
        return true;
    }


    public void close(MessageContext mc) {

    }


    /**
     * @see javax.xml.ws.handler.Handler#handleMessage(MessageContext)
     */
    public boolean handleMessage(SOAPMessageContext smc) {
        boolean isOutbound = ((Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();        

        System.out.println("handlemessage called");
       
        SOAPMessage msg = smc.getMessage();
        
        // since this is just testing we will store the message in a Client static variable 
        // for retrieval and examination by the testcases. 
        // In production, this would be poor practice, we would want to use the messageContext.  
        
        if (isOutbound) {
            /*  // this is an alternative way to retrieve the headers. 
            Map<QName, List<String>> outboundHeaders = (Map<QName, List<String>>)smc.get(Constants.JAXWS_OUTBOUND_SOAP_HEADERS);
            Iterator it  = outboundHeaders.keySet().iterator();
            while (it.hasNext()){
                System.out.println("handler saw outbound header:" + outboundHeaders.get(it.next()));
            }
            */
            outboundmsg = msg;
        } else {
            /*
            Map<QName, List<String>> inboundHeaders = (Map<QName, List<String>>)smc.get(Constants.JAXWS_INBOUND_SOAP_HEADERS);            
            Iterator it  = inboundHeaders.keySet().iterator();
            while (it.hasNext()){
                System.out.println("handler saw inbound header:" + inboundHeaders.get(it.next()));
            }
            */
            inboundmsg = msg;
        }
        
        
        return true;
    }
    
    /**
     * convenience method for tests to retrieve last inbound msg as a string
     * @return
     */
    public static String getInboundMsgAsString(){
        java.io.ByteArrayOutputStream baos = null;
        try{
            baos =new java.io.ByteArrayOutputStream(); 
            inboundmsg.writeTo(baos);
            
        }catch (Exception e){
            e.printStackTrace(System.out);
        }    
        return baos.toString();
    }
    
    /**
     * convenience method for tests to retrieve last outbound msg as a string
     * @return
     */
    public static String getOutboundMsgAsString(){
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
