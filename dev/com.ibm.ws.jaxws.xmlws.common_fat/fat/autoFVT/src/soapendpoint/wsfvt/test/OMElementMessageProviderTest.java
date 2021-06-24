package soapendpoint.wsfvt.test;

import java.io.ByteArrayInputStream;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * Tests that an <code>Provider&lt;OMElement&gt;</code> is able to send SOAP
 * 1.1 and SOAP 1.2 messages.
 */
public class OMElementMessageProviderTest extends FVTTestCase {
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
			+ "/soapendpoint/BothSOAPVerisonsExport_ServiceGatewayHttpService";

	private QName serviceName = new QName(
			"http://www.ibm.com/websphere/sibx/ServiceGateway/Binding",
			"BothSOAPVerisonsExport_ServiceGatewayHttpService");
	private QName portName = new QName(
			"http://www.ibm.com/websphere/sibx/ServiceGateway/Binding",
			"BothSOAPVerisonsExport_ServiceGatewayHttpPort");

	private static final String SOAP11_NS_URI = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String SOAP12_NS_URI = "http://www.w3.org/2003/05/soap-envelope";

	/**
	 * SOAP 1.1 header
	 */
	private static final String SOAP11_ENVELOPE_HEAD = "<?xml version='1.0' encoding='utf-8'?>"
			+ "<soapenv:Envelope xmlns:soapenv=\""
			+ SOAP11_NS_URI
			+ "\">"
			+ "<soapenv:Header />" + "<soapenv:Body>";

	/**
	 * SOAP 1.2 header
	 */
	private static final String SOAP12_ENVELOPE_HEAD = "<?xml version='1.0' encoding='utf-8'?>"
			+ "<soapenv:Envelope xmlns:soapenv=\""
			+ SOAP12_NS_URI
			+ "\">"
			+ "<soapenv:Header />" + "<soapenv:Body>";

	/**
	 * SOAP 1.1 footer
	 */
	private static final String SOAP11_ENVELOPE_TAIL = "</soapenv:Body>"
			+ "</soapenv:Envelope>";

	/**
	 * SOAP 1.2 footer
	 */
	private static final String SOAP12_ENVELOPE_TAIL = "</soapenv:Body>"
			+ "</soapenv:Envelope>";

	private static String request = "<invokeOp>Hello World</invokeOp>";

	/**
	 * Tests that a <code>Provider&lt;OMElement&gt;</code> SOAP 1.1 request
	 * can be processed.
	 * 
	 * @throws Exception
	 */
	public void testSoap11Request() throws Exception {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		System.out.println("---------------------------------------");
		System.out.println("test: " + getName());

		Dispatch<SOAPMessage> dispatch = getDispatch();
		String soapMessage = getSOAP11Message();
		MessageFactory factory = MessageFactory.newInstance();
		SOAPMessage message = factory.createMessage(null,
				new ByteArrayInputStream(soapMessage.getBytes()));
		Object obj = dispatch.invoke(message);
		assertTrue(obj != null && obj instanceof SOAPMessage);
		assertTrue(getVersionURI(message).equals(SOAP11_NS_URI));
		System.out.println(obj);
	}

	/**
	 * Tests that a <code>Provider&lt;OMElement&gt;</code> SOAP 1.2 request
	 * can be processed.
	 * 
	 * @throws Exception
	 */
	public void testSoap12Request() throws Exception {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		System.out.println("---------------------------------------");
		System.out.println("test: " + getName());

		Dispatch<SOAPMessage> dispatch = getDispatch();
		String soapMessage = getSOAP12Message();
		System.out.println("soap message =" + soapMessage);
		MessageFactory factory = MessageFactory
				.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		MimeHeaders header = new MimeHeaders();
		header.addHeader("Content-Type", "application/soap+xml");
		SOAPMessage message = factory.createMessage(header,
				new ByteArrayInputStream(soapMessage.getBytes()));
		Object obj = dispatch.invoke(message);
		assertTrue(obj != null && obj instanceof SOAPMessage);
		assertTrue(getVersionURI(message).equals(SOAP12_NS_URI));
		System.out.println(obj);
		System.out
				.println("Provider endpoint was able to receive both SOAP 11 and SOAP 12 request");
	}

	private Dispatch<SOAPMessage> getDispatch() {
		Service svc = Service.create(serviceName);
		svc.addPort(portName, null, endpointUrl);
		Dispatch<SOAPMessage> dispatch = svc.createDispatch(portName,
				SOAPMessage.class, Service.Mode.MESSAGE);
		BindingProvider p = dispatch;
		p.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				endpointUrl);
		return dispatch;
	}

	private String getSOAP11Message() {
		return SOAP11_ENVELOPE_HEAD + request + SOAP11_ENVELOPE_TAIL;
	}

	private String getSOAP12Message() {
		return SOAP12_ENVELOPE_HEAD + request + SOAP12_ENVELOPE_TAIL;
	}

	private String getVersionURI(SOAPMessage soapMessage) throws SOAPException {
		SOAPPart sp = soapMessage.getSOAPPart();
		SOAPEnvelope envelope = sp.getEnvelope();
		return envelope.getNamespaceURI();
	}
}
