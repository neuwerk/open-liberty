package soapendpoint.wsfvt.test;

import java.io.ByteArrayInputStream;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * Tests the JAXBPayload
 */
public class JAXBPayloadProviderTest extends FVTTestCase {

	private static QName serviceName = new QName("http://ws.apache.org/axis2",
			"StringNoWSDLPayloadProviderService");

	private static QName portName = new QName("http://ws.apache.org/axis2",
			"StringNoWSDLPayloadProviderServicePort");
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
			+ "/soapendpoint/JAXBPayloadProviderService";

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

	/**
	 * Verify that a SOAP 1.1 message is received for a SOAP 1.1 request using a
	 * <code>Dispatch&lt;String&gt;</code> with a Message mode client.
	 * 
	 * @throws Exception
	 */
	public void testSOAP11DispatchStringMessageModeRequest() throws Exception {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		Service svc = Service.create(serviceName);
		svc.addPort(portName, null, endpointUrl);

		Dispatch<String> dispatch = svc.createDispatch(portName, String.class,
				Service.Mode.MESSAGE);

		String xmlMessage = SOAP11_ENVELOPE_HEAD
				+ "<invokeOp>soap11 request</invokeOp>" + SOAP11_ENVELOPE_TAIL;
		String response = dispatch.invoke(xmlMessage);

		MessageFactory factory = MessageFactory.newInstance();
		SOAPMessage soapMessage = factory.createMessage(null,
				new ByteArrayInputStream(response.getBytes()));
		System.out.println(soapMessage.getSOAPBody());

		assertTrue(getVersionURI(soapMessage).equals(SOAP11_NS_URI));
	}

	/**
	 * Verify that a SOAP 1.2 message is received for a SOAP 1.2 request using a
	 * <code>Dispatch&lt;String&gt;</code> with a Message mode client.
	 * 
	 * @throws Exception
	 */
	public void testSOAP12DispatchStringMessageModeRequest() throws Exception {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		Service svc = Service.create(serviceName);
		svc.addPort(portName, SOAPBinding.SOAP12HTTP_BINDING, endpointUrl);

		Dispatch<String> dispatch = svc.createDispatch(portName, String.class,
				Service.Mode.MESSAGE);

		String xmlMessage = SOAP12_ENVELOPE_HEAD
				+ "<invokeOp>soap12 request</invokeOp>" + SOAP12_ENVELOPE_TAIL;
		String response = dispatch.invoke(xmlMessage);
		System.out.println(response);

		MessageFactory factory = MessageFactory
				.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		System.out.println(factory);
		SOAPMessage soapMessage = factory.createMessage(null,
				new ByteArrayInputStream(response.getBytes()));
		System.out.println(soapMessage.getSOAPBody());

		assertTrue(getVersionURI(soapMessage).equals(SOAP12_NS_URI));
	}

	private String getVersionURI(SOAPMessage soapMessage) throws SOAPException {
		SOAPPart sp = soapMessage.getSOAPPart();
		SOAPEnvelope envelope = sp.getEnvelope();
		return envelope.getNamespaceURI();
	}
}
