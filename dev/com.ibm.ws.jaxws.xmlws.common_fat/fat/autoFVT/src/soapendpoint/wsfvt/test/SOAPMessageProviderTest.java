package soapendpoint.wsfvt.test;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import soapendpoint.wsfvt.server.SoapMessageProvider;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * Tests ,code>Provider&lt;SOAPMessage&gt;</code> with the SOAP_HTTP_BINDING
 * BindingType. Tests with attachments, MTOM, and faults as well as normal
 * processing.
 */
public class SOAPMessageProviderTest extends FVTTestCase {
	private final static ApplicationServer server;
	private final static String hostName;
	private final static int port;

	static {
		try {
			server = TopologyDefaults.getDefaultAppServer();
			hostName = server.getNode().getMachine().getHostname();
			port = server.getPortNumber(PortType.WC_defaulthost).intValue();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	private static String endpointUrl = "http://" + hostName + ":" + port
			+ "/soapendpoint/SoapMessageProviderService";

	private QName serviceName = new QName(
			"http://soapmsg.soapbinding.provider.jaxws.axis2.apache.org",
			"SOAPBindingSoapMessageProviderService");
	private QName portName = new QName(
			"http://soapmsg.soapbinding.provider.jaxws.axis2.apache.org",
			"SoapMessageProviderPort");

	private String reqMsgStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>";

	private String reqMsgEnd = "</soap:Body></soap:Envelope>";

	private static final String SOAP12_NS_URI = "http://www.w3.org/2003/05/soap-envelope";

	private static final String SOAP12_ENVELOPE_HEAD = "<?xml version='1.0' encoding='utf-8'?>"
			+ "<soapenv:Envelope xmlns:soapenv=\""
			+ SOAP12_NS_URI
			+ "\">"
			+ "<soapenv:Header />" + "<soapenv:Body>";

	private static final String SOAP12_ENVELOPE_TAIL = "</soapenv:Body>"
			+ "</soapenv:Envelope>";

	private String ATTACHMENT_INVOKE = "<ns2:invokeOp xmlns:ns2=\"http://org.test.soapmessage\"><invoke_str>"
			+ SoapMessageProvider.XML_ATTACHMENT_REQUEST
			+ "</invoke_str></ns2:invokeOp>";
	private String MTOM_INVOKE = "<ns2:invokeOp xmlns:ns2=\"http://org.test.soapmessage\"><invoke_str>"
			+ SoapMessageProvider.XML_MTOM_REQUEST
			+ "</invoke_str>"
			+ SoapMessageProvider.MTOM_REF + "</ns2:invokeOp>";
	private String MTOM_INVOKE12 = "<ns2:invokeOp xmlns:ns2=\"http://org.test.soapmessage\"><invoke_str>"
			+ SoapMessageProvider.XML_MTOM_REQUEST_SOAP12
			+ "</invoke_str>"
			+ SoapMessageProvider.MTOM_REF + "</ns2:invokeOp>";
	private String SWAREF_INVOKE = "<ns2:invokeOp xmlns:ns2=\"http://org.test.soapmessage\"><invoke_str>"
			+ SoapMessageProvider.XML_SWAREF_REQUEST
			+ "</invoke_str>"
			+ SoapMessageProvider.SWAREF_REF + "</ns2:invokeOp>";
	private String SWAREF12_INVOKE = "<ns2:invokeOp xmlns:ns2=\"http://org.test.soapmessage\"><invoke_str>"
			+ SoapMessageProvider.XML_SWAREF12_REQUEST
			+ "</invoke_str>"
			+ SoapMessageProvider.SWAREF_REF + "</ns2:invokeOp>";
	private String XML_FAULT_INVOKE = "<ns2:invokeOp xmlns:ns2=\"http://org.test.soapmessage\"><invoke_str>"
			+ SoapMessageProvider.XML_FAULT_REQUEST
			+ "</invoke_str></ns2:invokeOp>";
	private String XML_WSE_INVOKE = "<ns2:invokeOp xmlns:ns2=\"http://org.test.soapmessage\"><invoke_str>"
			+ SoapMessageProvider.XML_WSE_REQUEST
			+ "</invoke_str></ns2:invokeOp>";
	private String XML_SOAP12_FAULT_INVOKE = "<ns2:invokeOp xmlns:ns2=\"http://org.test.soapmessage\"><invoke_str>"
			+ SoapMessageProvider.XML_SOAP12_FAULT_REQUEST
			+ "</invoke_str></ns2:invokeOp>";
	private String XML_SOAP12_RESPONSE_INVOKE = "<ns2:invokeOp xmlns:ns2=\"http://org.test.soapmessage\"><invoke_str>"
			+ SoapMessageProvider.XML_SOAP12_RESPONSE
			+ "</invoke_str></ns2:invokeOp>";

	/**
	 * Sends an SOAPMessage containing only xml data Provider will throw a Fault
	 * 
	 * @throws Exception
	 */
	public void testProviderSOAPMessageSOAPFault() throws Exception {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		// Create the dispatch
		Dispatch<SOAPMessage> dispatch = createDispatch();

		// Create the SOAPMessage
		String msg = reqMsgStart + XML_FAULT_INVOKE + reqMsgEnd;
		MessageFactory factory = MessageFactory.newInstance();
		SOAPMessage request = factory.createMessage(null,
				new ByteArrayInputStream(msg.getBytes()));

		// Test the transport headers by sending a content description
		request.setContentDescription(SoapMessageProvider.XML_FAULT_REQUEST);

		try {
			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			dispatch.invoke(request);
			fail("Expected failure");
		} catch (SOAPFaultException e) {
			// Okay
			SOAPFault fault = e.getFault();
			assertNotNull(fault);
			if (fault != null) {
				assertTrue(fault.getFaultString().equals("sample fault"));
				QName expectedFaultCode = new QName(
						SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Client");
				assertTrue(fault.getFaultCodeAsQName()
						.equals(expectedFaultCode));
				assertTrue(fault.getDetail() != null);
				DetailEntry de = (DetailEntry) fault.getDetail()
						.getDetailEntries().next();
				assertTrue(de != null);
				assertNotNull(de);
				if (de != null) {
					assertTrue(de.getLocalName().equals("detailEntry"));
					assertTrue(de.getValue().equals("sample detail"));
					assertTrue(fault.getFaultActor().equals("sample actor"));
				}
			}
		}

		// Try a second time
		try {
			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			dispatch.invoke(request);
			fail("Expected failure");
		} catch (SOAPFaultException e) {
			// Okay
			SOAPFault fault = e.getFault();
			assertNotNull(fault);
			if (fault != null) {
				assertTrue(fault.getFaultString().equals("sample fault"));
				QName expectedFaultCode = new QName(
						SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Client");
				assertTrue(fault.getFaultCodeAsQName()
						.equals(expectedFaultCode));
				assertTrue(fault.getDetail() != null);
				DetailEntry de = (DetailEntry) fault.getDetail()
						.getDetailEntries().next();
				assertNotNull(de);
				if (de != null) {
					assertTrue(de.getLocalName().equals("detailEntry"));
					assertTrue(de.getValue().equals("sample detail"));
					assertTrue(fault.getFaultActor().equals("sample actor"));
				}
			}
		}
	}

	/**
	 * Tests that a WebException can be returned in a SOAP 1.2 message.
	 * @throws Exception
	 */
	public void testProviderSOAPMessageWebServiceException12() throws Exception {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		// Create the dispatch
		Dispatch<SOAPMessage> dispatch = createDispatch();

		// Create the SOAPMessage
		String msg = SOAP12_ENVELOPE_HEAD + XML_WSE_INVOKE
				+ SOAP12_ENVELOPE_TAIL;
		MessageFactory factory = MessageFactory
				.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		SOAPMessage request = factory.createMessage(null,
				new ByteArrayInputStream(msg.getBytes()));

		// Test the transport headers by sending a content description
		request.setContentDescription(SoapMessageProvider.XML_WSE_REQUEST);

		try {
			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			dispatch.invoke(request);
			assertTrue("Expected failure", false);
		} catch (SOAPFaultException e) {
			// Okay...SOAPFaultException should be thrown
			SOAPFault fault = e.getFault();
			assertNotNull(fault);
			assertTrue(fault.getFaultString().equals("A WSE was thrown"));
		}
	}

	/**
	 * Tests that a SOAP 1.2 fault can be returned.
	 * @throws Exception
	 */
	public void testProviderSOAPMessageSOAPFault12() throws Exception {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		// Create the dispatch
		Dispatch<SOAPMessage> dispatch = createDispatch();

		// Create the SOAPMessage
		String msg = SOAP12_ENVELOPE_HEAD + XML_SOAP12_FAULT_INVOKE
				+ SOAP12_ENVELOPE_TAIL;
		MessageFactory factory = MessageFactory
				.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		SOAPMessage request = factory.createMessage(null,
				new ByteArrayInputStream(msg.getBytes()));

		// Test the transport headers by sending a content description
		request
				.setContentDescription(SoapMessageProvider.XML_SOAP12_FAULT_REQUEST);

		try {
			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			dispatch.invoke(request);
			fail("Expecting failure");
		} catch (SOAPFaultException e) {
			// Okay
			SOAPFault fault = e.getFault();
			assertNotNull(fault);
			assertTrue(fault.getFaultString().equals("sample fault"));
			QName expectedFaultCode = new QName(
					SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Sender");
			assertTrue(fault.getFaultCodeAsQName().equals(expectedFaultCode));
			assertTrue(fault.getDetail() != null);
			DetailEntry de = (DetailEntry) fault.getDetail().getDetailEntries()
					.next();
			assertNotNull(de);
			assertTrue(de.getLocalName().equals("detailEntry"));
			assertTrue(de.getValue().equals("sample detail"));
			assertTrue(fault.getFaultActor().equals("sample actor"));
		}
	}

	/**
	 * Sends an SOAPMessage containing only xml data Provider will throw a
	 * generic WebServicesException
	 * 
	 * @throws Exception
	 */
	public void testProviderSOAPMessageWebServiceException() throws Exception {
		if (!TestUtils.isPrereqMet()) {
			return;
		}

		// Create the dispatch
		Dispatch<SOAPMessage> dispatch = createDispatch();

		// Create the SOAPMessage
		String msg = reqMsgStart + XML_WSE_INVOKE + reqMsgEnd;
		MessageFactory factory = MessageFactory.newInstance();
		SOAPMessage request = factory.createMessage(null,
				new ByteArrayInputStream(msg.getBytes()));

		// Test the transport headers by sending a content description
		request.setContentDescription(SoapMessageProvider.XML_WSE_REQUEST);

		try {
			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			dispatch.invoke(request);
			fail("Expecting failure");
		} catch (SOAPFaultException e) {
			// Okay...SOAPFaultException should be thrown
			SOAPFault fault = e.getFault();
			assertNotNull(fault);
			assertTrue(fault.getFaultString().equals("A WSE was thrown"));
		}

		// Try a second time
		try {
			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			dispatch.invoke(request);
			fail("Expecting failure");
		} catch (SOAPFaultException e) {
			// Okay...SOAPFaultException should be thrown
			SOAPFault fault = e.getFault();
			assertNotNull(fault);
			assertTrue(fault.getFaultString().equals("A WSE was thrown"));
		}
	}

	/**
	 * Sends an SOAPMessage containing xml data and raw attachments to the web
	 * service. Receives a response containing xml data and the same raw
	 * attachments.
	 */

	public void testProviderSOAPMessageRawAttachment() {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		// Raw Attachments are attachments that are not referenced in the xml
		// with MTOM or SWARef.
		// Currently there is no support in Axis 2 for these kinds of
		// attachments.
		// The belief is that most customers will use MTOM. Some legacy
		// customers will use SWARef.
		// Raw Attachments may be so old that no customers need this behavior.
		try {
			// Create the dispatch
			Dispatch<SOAPMessage> dispatch = createDispatch();

			// Create the SOAPMessage
			String msg = reqMsgStart + ATTACHMENT_INVOKE + reqMsgEnd;
			MessageFactory factory = MessageFactory.newInstance();
			SOAPMessage request = factory.createMessage(null,
					new ByteArrayInputStream(msg.getBytes()));

			// Add the Attachment
			AttachmentPart ap = request.createAttachmentPart(
					SoapMessageProvider.TEXT_XML_ATTACHMENT, "text/xml");
			ap.setContentId(SoapMessageProvider.ID);
			request.addAttachmentPart(ap);

			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			SOAPMessage response = dispatch.invoke(request);

			// Check assertions and get the data element
			assertResponseXML(response,
					SoapMessageProvider.XML_ATTACHMENT_RESPONSE);
			assertTrue(countAttachments(response) == 1);

			// Get the Attachment
			AttachmentPart attachmentPart = (AttachmentPart) response
					.getAttachments().next();

			// Check the attachment
			StreamSource contentSS = (StreamSource) attachmentPart.getContent();
			String content = SoapMessageProvider.getAsString(contentSS);
			assertNotNull(content);
			assertTrue(content
					.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));

			// Print out the response
			System.out.println(">> Response [" + response.toString() + "]");

			// Try a second time
			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			response = dispatch.invoke(request);

			// Check assertions and get the data element
			assertResponseXML(response,
					SoapMessageProvider.XML_ATTACHMENT_RESPONSE);
			assertTrue(countAttachments(response) == 1);

			// Get the Attachment
			attachmentPart = (AttachmentPart) response.getAttachments().next();

			// Check the attachment
			contentSS = (StreamSource) attachmentPart.getContent();
			content = SoapMessageProvider.getAsString(contentSS);
			assertNotNull(content);
			assertTrue(content
					.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));

			// Print out the response
			System.out.println(">> Response [" + response.toString() + "]");

		} catch (Exception e) {
			e.printStackTrace();
			fail("Caught exception " + e);
		}

	}

	/**
	 * Tests that a SOAP 1.2 MTOM message can be processed.
	 */
	public void testProviderSOAP12MessageMTOM() {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		try {
			// Create the dispatch
			Dispatch<SOAPMessage> dispatch = createDispatch12();

			// MTOM should be automatically detected. There is no need to set it
			// Binding binding = dispatch.getBinding();
			// SOAPBinding soapBinding = (SOAPBinding) binding;
			// soapBinding.setMTOMEnabled(true);

			// Create the SOAPMessage
			String msg = SOAP12_ENVELOPE_HEAD + MTOM_INVOKE12
					+ SOAP12_ENVELOPE_TAIL;
			MessageFactory factory = MessageFactory
					.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
			SOAPMessage request = factory.createMessage(null,
					new ByteArrayInputStream(msg.getBytes()));

			// Add the Attachment
			AttachmentPart ap = request.createAttachmentPart(
					SoapMessageProvider.TEXT_XML_ATTACHMENT, "text/xml");
			ap.setContentId(SoapMessageProvider.ID);
			request.addAttachmentPart(ap);

			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			SOAPMessage response = dispatch.invoke(request);

			// Check assertions and get the data element
			assertResponseXML(response, SoapMessageProvider.XML_MTOM_RESPONSE);
			assertTrue(countAttachments(response) == 1);

			// Get the Attachment
			AttachmentPart attachmentPart = (AttachmentPart) response
					.getAttachments().next();

			// Check the attachment
			StreamSource contentSS = (StreamSource) attachmentPart.getContent();
			String content = SoapMessageProvider.getAsString(contentSS);
			assertNotNull(content);
			assertTrue(content
					.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));

			// Print out the response
			System.out.println(">> Response [" + response.toString() + "]");

			// Try a second time
			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			response = dispatch.invoke(request);

			// Check assertions and get the data element
			assertResponseXML(response, SoapMessageProvider.XML_MTOM_RESPONSE);
			assertTrue(countAttachments(response) == 1);

			// Get the Attachment
			attachmentPart = (AttachmentPart) response.getAttachments().next();

			// Check the attachment
			contentSS = (StreamSource) attachmentPart.getContent();
			content = SoapMessageProvider.getAsString(contentSS);
			assertNotNull(content);
			assertTrue(content
					.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));

			// Print out the response
			System.out.println(">> Response [" + response.toString() + "]");

		} catch (Exception e) {
			e.printStackTrace();
			fail("Caught exception " + e);
		}
	}

	/**
	 * Sends an SOAPMessage containing xml data and mtom attachment. Receives a
	 * response containing xml data and the mtom attachment.
	 */
	public void testProviderSOAPMessageMTOM() {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		try {
			// Create the dispatch
			Dispatch<SOAPMessage> dispatch = createDispatch();

			// MTOM should be automatically detected. There is no need to set it
			// Binding binding = dispatch.getBinding();
			// SOAPBinding soapBinding = (SOAPBinding) binding;
			// soapBinding.setMTOMEnabled(true);

			// Create the SOAPMessage
			String msg = reqMsgStart + MTOM_INVOKE + reqMsgEnd;
			MessageFactory factory = MessageFactory.newInstance();
			SOAPMessage request = factory.createMessage(null,
					new ByteArrayInputStream(msg.getBytes()));

			// Add the Attachment
			AttachmentPart ap = request.createAttachmentPart(
					SoapMessageProvider.TEXT_XML_ATTACHMENT, "text/xml");
			ap.setContentId(SoapMessageProvider.ID);
			request.addAttachmentPart(ap);

			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			SOAPMessage response = dispatch.invoke(request);

			// Check assertions and get the data element
			assertResponseXML(response, SoapMessageProvider.XML_MTOM_RESPONSE);
			assertTrue(countAttachments(response) == 1);

			// Get the Attachment
			AttachmentPart attachmentPart = (AttachmentPart) response
					.getAttachments().next();

			// Check the attachment
			StreamSource contentSS = (StreamSource) attachmentPart.getContent();
			String content = SoapMessageProvider.getAsString(contentSS);
			assertNotNull(content);
			assertTrue(content
					.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));

			// Print out the response
			System.out.println(">> Response [" + response.toString() + "]");

			// Try a second time
			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			response = dispatch.invoke(request);

			// Check assertions and get the data element
			assertResponseXML(response, SoapMessageProvider.XML_MTOM_RESPONSE);
			assertTrue(countAttachments(response) == 1);

			// Get the Attachment
			attachmentPart = (AttachmentPart) response.getAttachments().next();

			// Check the attachment
			contentSS = (StreamSource) attachmentPart.getContent();
			content = SoapMessageProvider.getAsString(contentSS);
			assertNotNull(content);
			assertTrue(content
					.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));

			// Print out the response
			System.out.println(">> Response [" + response.toString() + "]");

		} catch (Exception e) {
			e.printStackTrace();
			fail("Caught exception " + e);
		}

	}

	/**
	 * Sends an SOAPMessage containing xml data and a swaref attachment to the
	 * web service. Receives a response containing xml data and the swaref
	 * attachment attachment.
	 */
	public void testProviderSOAPMessageSWARef() {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		try {
			// Create the dispatch
			Dispatch<SOAPMessage> dispatch = createDispatch();

			// Create the SOAPMessage
			String msg = reqMsgStart + SWAREF_INVOKE + reqMsgEnd;
			MessageFactory factory = MessageFactory.newInstance();
			SOAPMessage request = factory.createMessage(null,
					new ByteArrayInputStream(msg.getBytes()));

			// Add the Attachment
			AttachmentPart ap = request.createAttachmentPart(
					SoapMessageProvider.TEXT_XML_ATTACHMENT, "text/xml");
			ap.setContentId(SoapMessageProvider.ID);
			request.addAttachmentPart(ap);

			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			SOAPMessage response = dispatch.invoke(request);

			// Check assertions and get the data element
			assertResponseXML(response, SoapMessageProvider.XML_SWAREF_RESPONSE);
			assertTrue(countAttachments(response) == 1);

			// Get the Attachment
			AttachmentPart attachmentPart = (AttachmentPart) response
					.getAttachments().next();

			// Check the attachment
			StreamSource contentSS = (StreamSource) attachmentPart.getContent();
			String content = SoapMessageProvider.getAsString(contentSS);
			assertNotNull(content);
			assertTrue(content
					.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));
			assertEquals(SoapMessageProvider.ID, attachmentPart.getContentId());

			// Print out the response
			System.out.println(">> Response [" + response.toString() + "]");

			// Try a second time
			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			response = dispatch.invoke(request);

			// Check assertions and get the data element
			assertResponseXML(response, SoapMessageProvider.XML_SWAREF_RESPONSE);
			assertTrue(countAttachments(response) == 1);

			// Get the Attachment
			attachmentPart = (AttachmentPart) response.getAttachments().next();

			// Check the attachment
			contentSS = (StreamSource) attachmentPart.getContent();
			content = SoapMessageProvider.getAsString(contentSS);
			assertNotNull(content);
			assertTrue(content
					.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));
			assertEquals(SoapMessageProvider.ID, attachmentPart.getContentId());

			// Print out the response
			System.out.println(">> Response [" + response.toString() + "]");

		} catch (Exception e) {
			e.printStackTrace();
			fail("Caught exception " + e);
		}

	}

	/**
	 * Sends an SOAPMessage containing xml data and a swaref attachment to the
	 * web service. Receives a response containing xml data and the swaref
	 * attachment attachment.
	 */
	public void testProviderSOAP12MessageSWARef() {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		try {
			// Create the dispatch
			Dispatch<SOAPMessage> dispatch = createDispatch12();

			// Create the SOAPMessage
			String msg = SOAP12_ENVELOPE_HEAD + SWAREF12_INVOKE
					+ SOAP12_ENVELOPE_TAIL;
			MessageFactory factory = MessageFactory
					.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
			SOAPMessage request = factory.createMessage(null,
					new ByteArrayInputStream(msg.getBytes()));

			// Add the Attachment
			AttachmentPart ap = request.createAttachmentPart(
					SoapMessageProvider.TEXT_XML_ATTACHMENT, "text/xml");
			ap.setContentId(SoapMessageProvider.ID);
			request.addAttachmentPart(ap);

			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			SOAPMessage response = dispatch.invoke(request);

			// Check assertions and get the data element
			assertResponseXML(response, SoapMessageProvider.XML_SWAREF_RESPONSE);
			assertTrue(countAttachments(response) == 1);

			// Get the Attachment
			AttachmentPart attachmentPart = (AttachmentPart) response
					.getAttachments().next();

			// Check the attachment
			StreamSource contentSS = (StreamSource) attachmentPart.getContent();
			String content = SoapMessageProvider.getAsString(contentSS);
			assertNotNull(content);
			assertTrue(content
					.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));
			assertEquals(SoapMessageProvider.ID, attachmentPart.getContentId());

			// Print out the response
			System.out.println(">> Response [" + response.toString() + "]");

			// Try a second time
			// Dispatch
			System.out.println(">> Invoking SOAPMessageProviderDispatch");
			response = dispatch.invoke(request);

			// Check assertions and get the data element
			assertResponseXML(response, SoapMessageProvider.XML_SWAREF_RESPONSE);
			assertTrue(countAttachments(response) == 1);

			// Get the Attachment
			attachmentPart = (AttachmentPart) response.getAttachments().next();

			// Check the attachment
			contentSS = (StreamSource) attachmentPart.getContent();
			content = SoapMessageProvider.getAsString(contentSS);
			assertNotNull(content);
			assertTrue(content
					.contains(SoapMessageProvider.TEXT_XML_ATTACHMENT));
			assertEquals(SoapMessageProvider.ID, attachmentPart.getContentId());

			// Print out the response
			System.out.println(">> Response [" + response.toString() + "]");

		} catch (Exception e) {
			e.printStackTrace();
			fail("Caught exception " + e);
		}

	}

	/**
	 * This is a negative test case for a Provider that has NO SOAPBinding
	 * restriction Dispatch will send a SOAP11 request and Provider will send a
	 * SOAP12 Response.
	 */
	/**
	 * This case is commented out because the expected fault only applies to 
	 * org.apache.axis2.jaxws.Constants.SOAP_HTTP_BINDING, which is not applicalbe 
	 * to liberty server. For details, please see 
	 * org.apache.axis2.jaxws.server.EndpointController.handleResponse()
	 
	public void testSoap11RequestWithSoap12Response() {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		SOAPMessage request = null;
		Dispatch<SOAPMessage> dispatch = null;
		try {
			// Create the dispatch
			dispatch = createDispatch();
			// Create the SOAPMessage
			String msg = reqMsgStart + XML_SOAP12_RESPONSE_INVOKE + reqMsgEnd;
			MessageFactory factory = MessageFactory.newInstance();
			request = factory.createMessage(null, new ByteArrayInputStream(msg
					.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Caught Exception " + e);
		}
		try {
			if (dispatch != null) {
				dispatch.invoke(request);
			}
			fail("Expecting failure");
		} catch (SOAPFaultException e) {
			SOAPFault fault = e.getFault();
			assertNotNull(fault);
			assertTrue(fault
					.getFaultString()
					.equals(
							"Request SOAP message protocol is version 1.1, but Response SOAP message is configured for SOAP 1.2.  This is not supported."));
		}

	}
	*/
	/**
	 * @return
	 * @throws Exception
	 */
	private Dispatch<SOAPMessage> createDispatch() throws Exception {

		Service svc = Service.create(serviceName);
		svc.addPort(portName, null, endpointUrl);
		Dispatch<SOAPMessage> dispatch = svc.createDispatch(portName,
				SOAPMessage.class, Service.Mode.MESSAGE);
		return dispatch;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	private Dispatch<SOAPMessage> createDispatch12() throws Exception {
		Service svc = Service.create(serviceName);
		svc.addPort(portName, SOAPBinding.SOAP12HTTP_BINDING, endpointUrl);
		Dispatch<SOAPMessage> dispatch = svc.createDispatch(portName,
				SOAPMessage.class, Service.Mode.MESSAGE);
		return dispatch;
	}

	/**
	 * Common assertion checking of the response
	 * 
	 * @param msg
	 * @param expectedText
	 * @return SOAPElement representing the data element
	 */
	private SOAPElement assertResponseXML(SOAPMessage msg, String expectedText)
			throws Exception {
		assertNotNull(msg);
		SOAPBody body = msg.getSOAPBody();

		assertNotNull(body);
		Node invokeElement = (Node) body.getFirstChild();
		assertTrue(invokeElement instanceof SOAPElement);
		assertEquals(SoapMessageProvider.RESPONSE_NAME, invokeElement
				.getLocalName());

		Node dataElement = (Node) invokeElement.getFirstChild();
		assertTrue(dataElement instanceof SOAPElement);
		assertEquals(SoapMessageProvider.RESPONSE_DATA_NAME, dataElement
				.getLocalName());

		// TODO AXIS2 SAAJ should (but does not) support the getTextContent();
		// String text = dataElement.getTextContent();
		String text = dataElement.getValue();
		assertEquals(
				"Found (" + text + ") but expected (" + expectedText + ")",
				expectedText, text);

		return (SOAPElement) dataElement;
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
		assertNotNull(it);
		if (it != null) {
			while (it.hasNext()) {
				it.next();
				count++;
			}
		}
		return count;
	}
}
