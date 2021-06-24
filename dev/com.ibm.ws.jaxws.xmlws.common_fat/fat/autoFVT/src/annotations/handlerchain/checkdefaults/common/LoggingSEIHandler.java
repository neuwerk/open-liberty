package annotations.handlerchain.checkdefaults.common;


import java.io.PrintStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/*
 * This simple SOAPHandler will output the contents of incoming
 * and outgoing messages, and change the first output child element to "208".
 */
public class LoggingSEIHandler implements SOAPHandler<SOAPMessageContext> {
   

    public Set<QName> getHeaders() {
        return null;
    }
    
    public boolean handleMessage(SOAPMessageContext smc) {
        System.out.println("LoggingSEIHandler handleMessage called");
        procMsg(smc);
        return true;
    }
    
    public boolean handleFault(SOAPMessageContext smc) {
        System.out.println("LoggingSEIHandler handleFault called");
        return true;
    }
    
    // nothing to clean up
    public void close(MessageContext messageContext) {
        System.out.println("LoggingSEIHandler close called");
    }
    
 
    private void procMsg(SOAPMessageContext smc) {
               Boolean outboundProperty = (Boolean)
            smc.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        
        if (outboundProperty.booleanValue()) {
			try {
                System.out.println("LoggingSEIHandler handleMessage processing outbound message");
				SOAPMessage message = smc.getMessage();
				SOAPPart part = message.getSOAPPart();
				SOAPEnvelope env = part.getEnvelope();
				SOAPBody body = env.getBody();
				// Get child elements of the SOAP Body
                
                SOAPElement sel = (SOAPBodyElement) body.getChildElements().next();
                
                // for rpc or dlw, we have to dig down one layer deeper
                Object nextchild = sel.getChildElements().next();
                if ( nextchild instanceof  SOAPElement ){
                    System.out.println("looks like rpc or dlw");
                    ((SOAPElement) nextchild).setValue("208");
                }
                else {
                    System.out.println("looks like dlb");
                    sel.setValue("208");
                }
								
                System.out.println("LoggingSEIHandler handleMessage modified the  outbound message");

				System.out.println("\nOutboundbound message:" + "\n");
				message.writeTo(System.out);
				System.out.println(""); // just to add a newline
			} catch (Exception e) {
				System.out.println("Caught exception in LoggingSEI handler: " + e);
                e.printStackTrace();
			}
		}
    }
}
