package annotations.handlerchain.provider.common;


import java.io.PrintStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/*
 * This simple SOAPHandler will output the contents of incoming
 * and outgoing messages.
 */
public class ChangeHandler implements SOAPHandler<SOAPMessageContext> {
    
    // change this to redirect output if desired
    private static PrintStream out = System.out;
    
    public Set<QName> getHeaders() {
        return null;
    }
    
    public boolean handleMessage(SOAPMessageContext smc) {
    	changeMessage(smc);
        return true;
    }
    
    public boolean handleFault(SOAPMessageContext smc) {
    	changeMessage(smc);
        return true;
    }
    
    // nothing to clean up
    public void close(MessageContext messageContext) {
    }
    
    /*
     * Check the MESSAGE_OUTBOUND_PROPERTY in the context
     * to see if this is an outgoing or incoming message.
     * Write a brief message to the print stream and
     * output the message. The writeTo() method can throw
     * SOAPException or IOException
     * 
     * this handler changes the first argument of the  
     * INBOUND message to 50
     */
    private void changeMessage(SOAPMessageContext smc) {
        Boolean outboundProperty = (Boolean)
            smc.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        
        if (!outboundProperty.booleanValue()) {
			try {
				SOAPMessage message = smc.getMessage();
				SOAPPart part = message.getSOAPPart();
				SOAPEnvelope env = part.getEnvelope();
				SOAPBody body = env.getBody();
				// Get child elements of the SOAP Body
								
				// Change the value of the first number
				SOAPElement sel = (SOAPElement) body.getChildElements().next();
				((SOAPElement) (sel.getChildElements().next())).setValue("50");

				out.println("\nInbound message:" + "\n");
				message.writeTo(out);
				out.println(""); // just to add a newline
			} catch (Exception e) {
				out.println("Exception in handler: " + e);
			}
		}
    }
}
