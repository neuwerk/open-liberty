package soapendpoint.wsfvt.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.axis2.jaxws.Constants;
import javax.xml.ws.soap.SOAPBinding;

/**
 * A SOAP Message provider with a SOAP_HTTP_BINDING.
 */
@WebServiceProvider(serviceName = "SoapMessageProviderService", targetNamespace = "http://soapmsg.provider.jaxws.axis2.apache.org", portName = "SoapMessageProviderPort")
@BindingType(SOAPBinding.SOAP12HTTP_MTOM_BINDING)
@ServiceMode(value = Service.Mode.MESSAGE)
public class SoapMessageProvider implements Provider<SOAPMessage> {

	String responseMsgStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body>";
	String responseMsgEnd = "</soapenv:Body></soapenv:Envelope>";

	// Requests and Response values of invoke_str and return_str
	// These constants are referenced by the SoapMessageProviderTest and
	// SoapMessageProvider

	/**
	 * response
	 */
	public static String RESPONSE_NAME = "ReturnType";

	/**
	 * response element
	 */
	public static String RESPONSE_DATA_NAME = "return_str";
	private static String REQUEST_NAME = "invokeOp";
	private static String REQUEST_DATA_NAME = "invoke_str";

	/**
	 * simple XML request
	 */
	public static String XML_REQUEST = "xml request";

	/**
	 * simple XML response
	 */
	public static String XML_RESPONSE = "xml response";

	/**
	 * simple XML empty body
	 */
	public static String XML_EMPTYBODY_REQUEST = "xml empty body request";

	/**
	 * simple XML check headers request
	 */
	public static String XML_CHECKHEADERS_REQUEST = "xml check headers request";

	/**
	 * simple XML check headers response
	 */
	public static String XML_CHECKHEADERS_RESPONSE = "xml check headers response";

	/**
	 * xml attachment request
	 */
	public static String XML_ATTACHMENT_REQUEST = "xml and attachment request";

	/**
	 * xml attachment response
	 */
	public static String XML_ATTACHMENT_RESPONSE = "xml and attachment response";

	/**
	 * mtom request
	 */
	public static String XML_MTOM_REQUEST = "xml and mtom request";

	/**
	 * mtom SOAP 1.2 request
	 */
	public static String XML_MTOM_REQUEST_SOAP12 = "xml and mtom request12";

	/**
	 * mtom response
	 */
	public static String XML_MTOM_RESPONSE = "xml and mtom response";

	/**
	 * SwA request
	 */
	public static String XML_SWAREF_REQUEST = "xml and swaref request";

	/**
	 * SwA 1.2 request
	 */
	public static String XML_SWAREF12_REQUEST = "xml and swaref request12";

	/**
	 * SwA response
	 */
	public static String XML_SWAREF_RESPONSE = "xml and swaref response";

	/**
	 * SOAP Fault request
	 */
	public static String XML_FAULT_REQUEST = "xml fault";

	/**
	 * SOAP 1.2 fault request
	 */
	public static String XML_SOAP12_FAULT_REQUEST = "xml soap12 fault";

	/**
	 * WSE request
	 */
	public static String XML_WSE_REQUEST = "xml wse fault";

	/**
	 * Specific SOAP 1.2 response
	 */
	public static String XML_SOAP12_RESPONSE = "xml soap12 response";

	private String XML_RETURN = "<ns2:ReturnType xmlns:ns2=\"http://test\"><return_str>"
			+ SoapMessageProvider.XML_RESPONSE
			+ "</return_str></ns2:ReturnType>";
	private String ATTACHMENT_RETURN = "<ns2:ReturnType xmlns:ns2=\"http://test\"><return_str>"
			+ SoapMessageProvider.XML_ATTACHMENT_RESPONSE
			+ "</return_str></ns2:ReturnType>";
	private String MTOM_RETURN = "<ns2:ReturnType xmlns:ns2=\"http://test\"><return_str>"
			+ SoapMessageProvider.XML_MTOM_RESPONSE
			+ "</return_str>"
			+ SoapMessageProvider.MTOM_REF + "</ns2:ReturnType>";
	private String SWAREF_RETURN = "<ns2:ReturnType xmlns:ns2=\"http://test\"><return_str>"
			+ SoapMessageProvider.XML_SWAREF_RESPONSE
			+ "</return_str>"
			+ SoapMessageProvider.SWAREF_REF + "</ns2:ReturnType>";
	private String CHECKHEADERS_RETURN = "<ns2:ReturnType xmlns:ns2=\"http://test\"><return_str>"
			+ SoapMessageProvider.XML_CHECKHEADERS_RESPONSE
			+ "</return_str>"
			+ "</ns2:ReturnType>";

	/**
	 * a text attachment
	 */
	public static String TEXT_XML_ATTACHMENT = "<myAttachment>Hello World</myAttachment>";

	/**
	 * text attachment id
	 */
	public static String ID = "helloWorld123";

	/**
	 * mtom ref
	 */
	public static String MTOM_REF = "<data>" + "<xop:Include href='cid:" + ID
			+ "' xmlns:xop='http://www.w3.org/2004/08/xop/include' />"
			+ "</data>";
	/**
	 * SwA ref
	 */
	public static String SWAREF_REF = "<data>" + "cid:" + ID + "</data>";

	private static final QName FOO_HEADER_QNAME = new QName("http://sample1",
			"foo", "pre1");
	private static final QName BAR_HEADER_QNAME = new QName("http://sample2",
			"bar", "pre2");
	private static final QName BAT_HEADER_QNAME = new QName("http://sample3",
			"bat", "pre3");

	private static final String FOO_HEADER_CONTENT = "foo content";
	private static final String BAR_HEADER_CONTENT1 = "bar content1";
	private static final String BAR_HEADER_CONTENT2 = "bar content2";
	private static final String BAT_HEADER_CONTENT = "bat content";

	private static final String SOAP12_NS_URI = "http://www.w3.org/2003/05/soap-envelope";

	private static final String SOAP12_ENVELOPE_HEAD = "<?xml version='1.0' encoding='utf-8'?>"
			+ "<soapenv:Envelope xmlns:soapenv=\""
			+ SOAP12_NS_URI
			+ "\">"
			+ "<soapenv:Header />" + "<soapenv:Body>";

	private static final String SOAP12_ENVELOPE_TAIL = "</soapenv:Body>"
			+ "</soapenv:Envelope>";

	public SOAPMessage invoke(SOAPMessage soapMessage)
			throws SOAPFaultException {
		System.out.println(">> SoapMessageProvider: Request received.");

		try {
			// Look at the incoming request message
			// System.out.println(">> Request on Server:");
			// soapMessage.writeTo(System.out);
			// System.out.println("\n");

			// Get the discrimination element. This performs basic assertions on
			// the received message
			SOAPElement discElement = assertRequestXML(soapMessage);

			// Use the data element text to determine the type of response to
			// send
			SOAPMessage response = null;
			// TODO AXIS2 SAAJ should (but does not) support the
			// getTextContent();
			// String text = dataElement.getTextContent();
			String text = discElement.getValue();
			if (XML_REQUEST.equals(text)) {
				response = getXMLResponse(soapMessage, discElement);
			} else if (XML_EMPTYBODY_REQUEST.equals(text)) {
				response = getXMLEmptyBodyResponse(soapMessage, discElement);
			} else if (XML_CHECKHEADERS_REQUEST.equals(text)) {
				response = getXMLCheckHeadersResponse(soapMessage, discElement);
			} else if (XML_ATTACHMENT_REQUEST.equals(text)) {
				response = getXMLAttachmentResponse(soapMessage, discElement);
			} else if (XML_MTOM_REQUEST.equals(text)) {
				response = getXMLMTOMResponse(soapMessage, discElement);
			} else if (XML_MTOM_REQUEST_SOAP12.equals(text)) {
				response = getXMLMTOM12Response(soapMessage, discElement);
			} else if (XML_SWAREF_REQUEST.equals(text)) {
				response = getXMLSWARefResponse(soapMessage, discElement);
			} else if (XML_SWAREF12_REQUEST.equals(text)) {
				response = getXMLSWARef12Response(soapMessage, discElement);
			} else if (XML_FAULT_REQUEST.equals(text)) {
				throwSOAPFaultException();
			} else if (XML_WSE_REQUEST.equals(text)) {
				throwWebServiceException();
			} else if (XML_SOAP12_RESPONSE.equals(text)) {
				response = getSOAP12Response();
			} else if (XML_SOAP12_FAULT_REQUEST.equals(text)) {
				throwSOAPFault12Exception();
			} else {
				// We should not get here
				System.out.println("Unknown Type of Message");
				assertTrue(false);
			}

			// Write out the Message
			System.out.println(">> Response being sent by Server:");
			// response.writeTo(System.out);
			// System.out.println("\n");
			return response;
		} catch (WebServiceException wse) {
			throw wse;
		} catch (Exception e) {
			System.out
					.println("***ERROR: In SoapMessageProvider.invoke: Caught exception "
							+ e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Common assertion checking of the request
	 * 
	 * @param msg
	 * @return SOAPElement representing the data element
	 */
	private SOAPElement assertRequestXML(SOAPMessage msg) throws Exception {
		assertTrue(msg != null);
		if (msg == null) {
			return null;
		}
		SOAPBody body = msg.getSOAPBody();
		assertTrue(body != null);

		if (body == null) {
			return null;
		}
		Node invokeElement = (Node) body.getFirstChild();
		assertTrue(invokeElement instanceof SOAPElement);
		assertTrue(SoapMessageProvider.REQUEST_NAME.equals(invokeElement
				.getLocalName()));

		Node discElement = (Node) invokeElement.getFirstChild();
		assertTrue(discElement instanceof SOAPElement);
		assertTrue(SoapMessageProvider.REQUEST_DATA_NAME.equals(discElement
				.getLocalName()));

		String text = discElement.getValue();
		assertTrue(text != null);
		if (text == null) {
			return null;
		}
		assertTrue(text.length() > 0);
		System.out.println("Request Message Type is:" + text);

		return (SOAPElement) discElement;
	}

	/**
	 * Get the response for an XML only request
	 * 
	 * @param request
	 * @param dataElement
	 * @return SOAPMessage
	 */
	private SOAPMessage getXMLResponse(SOAPMessage request,
			SOAPElement dataElement) throws Exception {
		SOAPMessage response;

		// Transport header check
		assertTrue(request.getContentDescription() != null);
		assertTrue(request.getContentDescription().equals(
				SoapMessageProvider.XML_REQUEST));

		// Additional assertion checks
		assertTrue(countAttachments(request) == 0);

		// Build the Response
		MessageFactory factory = MessageFactory.newInstance();
		String responseXML = responseMsgStart + XML_RETURN + responseMsgEnd;
		response = factory.createMessage(null, new ByteArrayInputStream(
				responseXML.getBytes()));

		// Set a content description
		response.setContentDescription(SoapMessageProvider.XML_RESPONSE);
		return response;
	}

	/**
	 * Get the response for an XML only request
	 * 
	 * @param request
	 * @param dataElement
	 * @return SOAPMessage
	 */
	private SOAPMessage getXMLEmptyBodyResponse(SOAPMessage request,
			SOAPElement dataElement) throws Exception {
		SOAPMessage response;

		// Additional assertion checks
		assertTrue(countAttachments(request) == 0);

		// Build the Response
		MessageFactory factory = MessageFactory.newInstance();
		response = factory.createMessage();

		return response;
	}

	/**
	 * Get the response for an XML check headers request
	 * 
	 * @param request
	 * @param dataElement
	 * @return SOAPMessage
	 */
	private SOAPMessage getXMLCheckHeadersResponse(SOAPMessage request,
			SOAPElement dataElement) throws Exception {
		SOAPMessage response;

		// Additional assertion checks
		assertTrue(countAttachments(request) == 0);

		// Check for specific headers
		SOAPHeader sh = request.getSOAPHeader();
		assertTrue(sh != null);

		if (sh == null) {
			return null;
		}

		SOAPHeaderElement she = (SOAPHeaderElement) sh.getChildElements(
				FOO_HEADER_QNAME).next();
		assertTrue(she != null);
		if (she == null) {
			return null;
		}
		String text = she.getValue();
		assertTrue(FOO_HEADER_CONTENT.equals(text));

		Iterator<?> it = sh.getChildElements(BAR_HEADER_QNAME);
		she = (SOAPHeaderElement) it.next();
		assertTrue(she != null);
		if (she == null) {
			return null;
		}
		text = she.getValue();
		assertTrue(BAR_HEADER_CONTENT1.equals(text));
		she = (SOAPHeaderElement) it.next();
		assertTrue(she != null);
		if (she == null) {
			return null;
		}
		text = she.getValue();
		assertTrue(BAR_HEADER_CONTENT2.equals(text));

		assertTrue(!sh.getChildElements(BAT_HEADER_QNAME).hasNext());

		// Build the Response
		MessageFactory factory = MessageFactory.newInstance();
		String responseXML = responseMsgStart + CHECKHEADERS_RETURN
				+ responseMsgEnd;
		response = factory.createMessage(null, new ByteArrayInputStream(
				responseXML.getBytes()));
		response.getSOAPHeader().addHeaderElement(BAR_HEADER_QNAME)
				.addTextNode(BAR_HEADER_CONTENT1);
		response.getSOAPHeader().addHeaderElement(BAR_HEADER_QNAME)
				.addTextNode(BAR_HEADER_CONTENT2);
		response.getSOAPHeader().addHeaderElement(BAT_HEADER_QNAME)
				.addTextNode(BAT_HEADER_CONTENT);

		return response;
	}

	/**
	 * Get the response for an XML and an Attachment request
	 * 
	 * @param request
	 * @param dataElement
	 * @return SOAPMessage
	 */
	private SOAPMessage getXMLAttachmentResponse(SOAPMessage request,
			SOAPElement dataElement) throws Exception {
		SOAPMessage response;

		// Additional assertion checks
		assertTrue(countAttachments(request) == 1);
		AttachmentPart requestAP = (AttachmentPart) request.getAttachments()
				.next();
		StreamSource contentSS = (StreamSource) requestAP.getContent();
		String content = getAsString(contentSS);
		assertTrue(content.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));

		// Build the Response
		MessageFactory factory = MessageFactory.newInstance();
		String responseXML = responseMsgStart + ATTACHMENT_RETURN
				+ responseMsgEnd;
		response = factory.createMessage(null, new ByteArrayInputStream(
				responseXML.getBytes()));

		// Create and attach the attachment
		AttachmentPart ap = response.createAttachmentPart(
				SoapMessageProvider.TEXT_XML_ATTACHMENT, "text/xml");
		ap.setContentId(ID);
		response.addAttachmentPart(ap);

		return response;
	}

	/**
	 * Get the response for an XML and an MTOM Attachment request
	 * 
	 * @param request
	 * @param dataElement
	 * @return SOAPMessage
	 */
	private SOAPMessage getXMLMTOMResponse(SOAPMessage request,
			SOAPElement dataElement) throws Exception {
		SOAPMessage response;

		System.out.println("Received MTOM Message");
		// Additional assertion checks
		assertTrue(countAttachments(request) == 1);
		AttachmentPart requestAP = (AttachmentPart) request.getAttachments()
				.next();
		StreamSource contentSS = (StreamSource) requestAP.getContent();
		String content = getAsString(contentSS);
		assertTrue(content.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));

		System.out.println("The MTOM Request Message appears correct.");

		// Build the Response
		MessageFactory factory = MessageFactory.newInstance();
		String responseXML = responseMsgStart + MTOM_RETURN + responseMsgEnd;
		response = factory.createMessage(null, new ByteArrayInputStream(
				responseXML.getBytes()));

		// Create and attach the attachment
		AttachmentPart ap = response.createAttachmentPart(
				SoapMessageProvider.TEXT_XML_ATTACHMENT, "text/xml");
		ap.setContentId(ID);
		response.addAttachmentPart(ap);

		System.out.println("Returning the Response Message");
		return response;
	}

	/**
	 * Get the response for an XML and an MTOM Attachment request
	 * 
	 * @param request
	 * @param dataElement
	 * @return SOAPMessage
	 */
	private SOAPMessage getXMLMTOM12Response(SOAPMessage request,
			SOAPElement dataElement) throws Exception {
		SOAPMessage response;

		System.out.println("Received MTOM12 Message");
		// Additional assertion checks
		assertTrue(countAttachments(request) == 1);
		AttachmentPart requestAP = (AttachmentPart) request.getAttachments()
				.next();
		StreamSource contentSS = (StreamSource) requestAP.getContent();
		String content = getAsString(contentSS);
		assertTrue(content.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));

		System.out.println("The MTOM Request Message appears correct.");

		// Build the Response
		MessageFactory factory = MessageFactory
				.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		String responseXML = SOAP12_ENVELOPE_HEAD + MTOM_RETURN
				+ SOAP12_ENVELOPE_TAIL;
		response = factory.createMessage(null, new ByteArrayInputStream(
				responseXML.getBytes()));

		// Create and attach the attachment
		AttachmentPart ap = response.createAttachmentPart(
				SoapMessageProvider.TEXT_XML_ATTACHMENT, "text/xml");
		ap.setContentId(ID);
		response.addAttachmentPart(ap);

		System.out.println("Returning the Response Message");
		return response;
	}

	/**
	 * Get the response for an XML and an MTOM Attachment request
	 * 
	 * @param request
	 * @param dataElement
	 * @return SOAPMessage
	 */
	private SOAPMessage getXMLSWARefResponse(SOAPMessage request,
			SOAPElement dataElement) throws Exception {
		SOAPMessage response;

		// Additional assertion checks
		assertTrue(countAttachments(request) == 1);
		AttachmentPart requestAP = (AttachmentPart) request.getAttachments()
				.next();
		assertTrue(requestAP.getContentId().equals("<" + ID + ">"));
		StreamSource contentSS = (StreamSource) requestAP.getContent();
		String content = getAsString(contentSS);
		assertTrue(content.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));

		// Build the Response
		MessageFactory factory = MessageFactory.newInstance();
		String responseXML = responseMsgStart + SWAREF_RETURN + responseMsgEnd;
		response = factory.createMessage(null, new ByteArrayInputStream(
				responseXML.getBytes()));

		// Create and attach the attachment
		AttachmentPart ap = response.createAttachmentPart(
				SoapMessageProvider.TEXT_XML_ATTACHMENT, "text/xml");
		ap.setContentId(ID);
		response.addAttachmentPart(ap);

		return response;
	}

	/**
	 * Get the response for an XML and an MTOM Attachment request
	 * 
	 * @param request
	 * @param dataElement
	 * @return SOAPMessage
	 */
	private SOAPMessage getXMLSWARef12Response(SOAPMessage request,
			SOAPElement dataElement) throws Exception {
		SOAPMessage response;

		// Additional assertion checks
		assertTrue(countAttachments(request) == 1);
		AttachmentPart requestAP = (AttachmentPart) request.getAttachments()
				.next();
		assertTrue(requestAP.getContentId().equals("<" + ID + ">"));
		StreamSource contentSS = (StreamSource) requestAP.getContent();
		String content = getAsString(contentSS);
		assertTrue(content.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));

		// Build the Response
		MessageFactory factory = MessageFactory
				.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		String responseXML = SOAP12_ENVELOPE_HEAD + SWAREF_RETURN
				+ SOAP12_ENVELOPE_TAIL;
		response = factory.createMessage(null, new ByteArrayInputStream(
				responseXML.getBytes()));

		// Create and attach the attachment
		AttachmentPart ap = response.createAttachmentPart(
				SoapMessageProvider.TEXT_XML_ATTACHMENT, "text/xml");
		ap.setContentId(ID);
		response.addAttachmentPart(ap);

		return response;
	}

	private void throwSOAPFaultException() throws SOAPFaultException {
		try {
			MessageFactory mf = MessageFactory.newInstance();
			SOAPFactory sf = SOAPFactory.newInstance();

			SOAPMessage m = mf.createMessage();
			SOAPBody body = m.getSOAPBody();
			SOAPFault fault = body.addFault();
			QName faultCode = new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE,
					"Client");
			fault.setFaultCode(faultCode);
			fault.setFaultString("sample fault");
			Detail detail = fault.addDetail();
			Name deName = sf.createName("detailEntry");
			SOAPElement detailEntry = detail.addDetailEntry(deName);
			detailEntry.addTextNode("sample detail");
			fault.setFaultActor("sample actor");

			SOAPFaultException sfe = new SOAPFaultException(fault);
			throw sfe;
		} catch (SOAPFaultException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void throwSOAPFault12Exception() throws SOAPFaultException {
		try {
			MessageFactory mf = MessageFactory
					.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
			SOAPFactory sf = SOAPFactory
					.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

			SOAPMessage m = mf.createMessage();
			SOAPBody body = m.getSOAPBody();
			SOAPFault fault = body.addFault();
			QName faultCode = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE,
					"Sender");
			fault.setFaultCode(faultCode);
			fault.setFaultString("sample fault");
			Detail detail = fault.addDetail();
			Name deName = sf.createName("detailEntry");
			SOAPElement detailEntry = detail.addDetailEntry(deName);
			detailEntry.addTextNode("sample detail");
			fault.setFaultActor("sample actor");

			SOAPFaultException sfe = new SOAPFaultException(fault);
			throw sfe;
		} catch (SOAPFaultException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void throwWebServiceException() throws WebServiceException {
		throw new WebServiceException("A WSE was thrown");
	}

	/**
	 * Count Attachments
	 * 
	 * @param msg
	 * @return
	 */
	private int countAttachments(SOAPMessage msg) {
		Iterator<?> it = msg.getAttachments();
		int count = 0;
		assertTrue(it != null);
		if (it != null) {
			while (it.hasNext()) {
				it.next();
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the stream as a string.
	 * 
	 * @param ss
	 * @return
	 * @throws Exception
	 */
	public static String getAsString(StreamSource ss) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Result result = new StreamResult(out);
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.transform(ss, result);
		String text = new String(out.toByteArray());
		return text;
	}

	private void assertTrue(boolean testAssertion) {
		if (!testAssertion) {
			throw new RuntimeException("Assertion false");
		}
	}

	private SOAPMessage getSOAP12Response() throws SOAPException, IOException {
		MessageFactory factory = MessageFactory
				.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		MimeHeaders header = new MimeHeaders();
		header.addHeader("Content-Type", "application/soap+xml");
		String responseXML = SOAP12_ENVELOPE_HEAD + "<return>"
				+ XML_SOAP12_RESPONSE + "</return>" + SOAP12_ENVELOPE_TAIL;
		System.out.println("Creating SOAP12 Response");
		return factory.createMessage(header, new ByteArrayInputStream(
				responseXML.getBytes()));
	}
}