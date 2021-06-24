package soapendpoint.wsfvt.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

/**
 * Provider that has no WSDL and uses a SoapMessage. This Provider returns the
 * expected SOAP version back.
 */
@WebServiceProvider()
@ServiceMode(value = Service.Mode.MESSAGE)
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class SOAPBindingProvider implements Provider<SOAPMessage> {
	private static final String SOAP11_NS_URI = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String SOAP12_NS_URI = "http://www.w3.org/2003/05/soap-envelope";

	private static final String SOAP11_ENVELOPE_HEAD = "<?xml version='1.0' encoding='utf-8'?>"
			+ "<soapenv:Envelope xmlns:soapenv=\""
			+ SOAP11_NS_URI
			+ "\">"
			+ "<soapenv:Header />" + "<soapenv:Body>";

	private static final String SOAP12_ENVELOPE_HEAD = "<?xml version='1.0' encoding='utf-8'?>"
			+ "<soapenv:Envelope xmlns:soapenv=\""
			+ SOAP12_NS_URI
			+ "\">"
			+ "<soapenv:Header />" + "<soapenv:Body>";

	private static final String SOAP11_ENVELOPE_TAIL = "</soapenv:Body>"
			+ "</soapenv:Envelope>";

	private static final String SOAP12_ENVELOPE_TAIL = "</soapenv:Body>"
			+ "</soapenv:Envelope>";

	private String soap11ResponseStr = "SOAP11 request received";
	private String soap12ResponseStr = "SOAP12 request received";

	public SOAPMessage invoke(SOAPMessage soapMessage) {
		try {
			System.out.println("Start Invoke(SOAPMessage)");
			String namespaceURI = getSoapVersionURI(soapMessage);
			// return SOAP11 response if this is a SOAP11 Message
			if (namespaceURI.equals(SOAP11_NS_URI)) {
				System.out.println("SOAP11_NS_URI found in the request");
				return getSOAP11Response();
			}
			// return SOAP12 response if this is a SOAP11 Message
			if (namespaceURI.equals(SOAP12_NS_URI)) {
				System.out.println("SOAP12_NS_URI found in the request");
				return getSOAP12Response();
			}
		} catch (Exception e) {
			throw new WebServiceException(e);
		}
		throw new WebServiceException(
				"Request received but could not interpret the protocol");
	}

	private String getSoapVersionURI(SOAPMessage soapMessage)
			throws SOAPException {
		SOAPPart sp = soapMessage.getSOAPPart();
		SOAPEnvelope envelope = sp.getEnvelope();
		return envelope.getNamespaceURI();
	}

	private SOAPMessage getSOAP11Response() throws SOAPException, IOException {
		MessageFactory factory = MessageFactory.newInstance();
		String responseXML = SOAP11_ENVELOPE_HEAD + "<return>"
				+ soap11ResponseStr + "</return>" + SOAP11_ENVELOPE_TAIL;
		System.out.println("Creating SOAP11 Response");
		return factory.createMessage(null, new ByteArrayInputStream(responseXML
				.getBytes()));
	}

	private SOAPMessage getSOAP12Response() throws SOAPException, IOException {
		MessageFactory factory = MessageFactory
				.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		MimeHeaders header = new MimeHeaders();
		header.addHeader("Content-Type", "application/soap+xml");
		String responseXML = SOAP12_ENVELOPE_HEAD + "<return>"
				+ soap12ResponseStr + "</return>" + SOAP12_ENVELOPE_TAIL;
		System.out.println("Creating SOAP12 Response");
		return factory.createMessage(header, new ByteArrayInputStream(
				responseXML.getBytes()));
	}
}
