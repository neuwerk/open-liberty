package annotations.handlerchain.checkdefaults.common;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/*
 * This handler intercepts soap messages.
 * To enable testing, it also changes the value of the first child element
 * of an outbound message to "500".  This effectively changes the response
 * of a service, and that is what the test looks for.
 */
public class LoggingImplHandler implements SOAPHandler<SOAPMessageContext> {

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("LoggingImplHandler handleMessage is called");
		procMsg(smc);
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("LoggingImplHandler handleFault is called");
		return true;
	}

	// nothing to clean up
	public void close(MessageContext messageContext) {
		System.out.println("LoggingImplHandler close is called");
	}

	private void procMsg(SOAPMessageContext smc) {
		Boolean outboundProperty = (Boolean) smc
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outboundProperty.booleanValue()) {
			try {
				System.out
						.println("\nLoggingImplHandler extracting outbound message\n");
				SOAPMessage message = smc.getMessage();
				System.out.println("Message before handler processing: ");
				message.writeTo(System.out);
				// SOAPPart part = message.getSOAPPart();
				// SOAPEnvelope env = part.getEnvelope();
				SOAPBody body = message.getSOAPBody();
				// Get child elements of the SOAP Body

				SOAPElement sel = (SOAPBodyElement) body.getChildElements()
						.next();

				// for rpc or dlw, we have to dig down one layer deeper
				Object nextchild = sel.getChildElements().next();
				if (nextchild instanceof SOAPElement) {
					System.out.println("looks like rpc or dlw");
					((SOAPElement) nextchild).setValue("50");
				} else {
					System.out.println("looks like dlb");
					sel.setValue("50");
				}

				((SOAPElement) (sel.getChildElements().next())).setValue("50");

				System.out
						.println("\nLoggingImplHandler modified an outbound message:");
				message.writeTo(System.out);
			} catch (Exception e) {
				System.out
						.println("LoggingImplHandler: Caught exception in handler: ");
				e.printStackTrace();
			}
		}
	}
}
