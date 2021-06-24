//
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date       UserId      Defect          Description
// ----------------------------------------------------------------------------
// 10/03/2011 jtnguyen    F012118.fvt     New File
// 10/29/2011 varadan     F012118.fvt.1   Add Checker class to enable the MTOM-FIS


package jaxws.provider.omelementmtom.wsfvt.test;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.attachments.utils.IOUtils;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.impl.builder.XOPAwareStAXOMBuilder;
import org.apache.axiom.om.util.OMHelper;

import jaxws.provider.omelementmtom.wsfvt.checker.OMProviderMTOMTestChecker;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import java.io.File;
import java.io.StringReader;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This test uses org.apache.axiom.om.util.OMHelper provided by BPM to control
 * the serialization style of MTOM attachments. 
 * - on client side: we create an
 * OMElement with MTOM attachment(s). The XML message is at low level.
 * The message is sent using dispatch<OMElement>. 
 * - on server side: Using of
 * OMHelper.setSerializeAttachmentOutline function will enable attachment
 * outlining on the specified OMElement tree. setSerializeAttachmentOutline
 * function needs to be used right after OMElment created or obtained.
 * 
 * Attachment outlining means all attachments corresponding to xop:Include
 * element in the message will behave as follows: 1) When the message is
 * serialized, the xop:Include will not be replaced with inline text
 * representing the attachments 2) The OMHelper.getAllAttachments method can be
 * used to retrieve the attachments 3) Attachments can be added, or the
 * datahandler replaced for existing attachments, using the
 * OMHelper.forceAddAttachments method.
 * 
 * Note: 
 * - we only support dispatch/provider model with OMElement 
 * - BPM is using the generic provider, which means the Provider endpoint should not have
 * any WSDL associated with it.
 * - Attachments can be added using OMHelper.forceAddAttachments if there are corresponding xop:include elements
 * in the xml document. 
 * - setSerializeAttachmentOutline function needs to be used right after OMElment created or obtained.
 */
public class OMProviderMTOMTests extends
		com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private String endpointUrl = null;
	private QName serviceName = new QName(
			"http://server.wsfvt.omelementmtom.provider.jaxws",
			"OMProviderMTOMService");
	public static final QName portName = new QName(
			"http://server.wsfvt.omelementmtom.provider.jaxws",
			"OMProviderMTOMPort");
	//private static Cell cell = null;
	private static String hostAndPort = null;

	private static final String SOAP11_HEADER = "<soap:Envelope  xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>";
	private static final String SOAP11_TRAILER = "</soap:Body></soap:Envelope>";
	private static final String SOAP12_HEADER = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body>";
	private static final String SOAP12_TRAILER = "</soap:Body></soap:Envelope>";
	
	private static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope";
	//private static final String SOAP12_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";
	
	//private static final String attachment1 = "<Text1>A. What is WebSphere?In todays business environment, it is increasingly important for all enterprises to develop and implement intelligent on demand strategies. In recent years, IBM has established itself as the industry leader in providing state-of-the-art on demand business solutions. In order to meet the rapidly emerging real-time requirements of businesses everywhere, IBM has launched the WebSphere software suite. WebSphere is an IBM software brand used to distinguish a suite of more than 300 software products. The magnitude of WebSphere has led to the misperception that WebSphere is too complex and expensive for SMBs. In truth, WebSphere is tremendously flexible, and WebSphere products are ideally suited to the on demand needs of both SMBs and large enterprises.IBM WebSphere software provides businesses with an IT infrastructure that maximizes both flexibility and responsiveness. By integrating people, processes, and information, WebSphere enables businesses to respond to changing business conditions with improved flexibility and speed. By optimizing the application infrastructure, WebSphere creates a reliable, high-performance environment for deploying and running applicat fosters operational excellence. And by extending the reach of IT, WebSphere enables companies to maximize the use of their IT infrastructure to reach users in new ways and support new business models.Figure 1: Business AlignmentIBM WebSphere software, with its integration and infrastructure capabilities, is a key part of IBM s overall middleware platform. IBM s middleware platform extends beyond WebSphere to include infrastructure management capabilities including security, provisioning, and infrastructure orchestration. The middleware also extends to business-driven development capabilities for improving the software life cycle management process. Furthermore, IBM s middleware platform provides business performance management capabilities, which extend the process integration and management capability of WebSphere to include the monitoring and optimization of the IT resources, which support your business processes. The IBM middleware platform is delivered as a modular product portfolio built on open standards. While functionally rich, the platform can be adopted incrementally, and you can start by adopting only those capabilities that you need to solve your most pressing business challenges. It provides integrated, role-based tools for application development and administration, utilizing a common installation, administration, security, and programming model. In short, the IBM middleware platform makes your technology infrastructure simple to develop, deploy, and manage.The WebSphere software suite enables on demand flexibility through a set of integration and infrastructure capabilities. Consider the following illustration; it shows WebSpheres capabilities, from the top down, in six distinct categories: People Integration, Process Integration, Information Integration, Application Integration, Application Infrastructure, and Accelerators. This abstract description of the WebSphere suite is probably less exciting than descriptions of the actual products; nevertheless, the capability classification is essential to defining WebSphere, because it provides the clearest orientation to the different areas that the WebSphere suite addresses.Modeling, simulating, and optimizing business processes Enabling you to efficiently manage, integrate, and access information that is scattered throughout the enterprise, and even beyond the enterprise in other companies with which you do businessApplication IntegrationWebSphere middleware ensures reliable and flexible information flow among diverse applications and organizations. The middleware enables reliability and seamless exchange of data among multiple applications. WebSphere software helps you manage the differences between multiple applications and business partners, and it adopts an enterprise-wide, flexible, service-oriented approach to integration.Application InfrastructureWebSphere middleware allows you to build, deploy, integrate, and enhance new and existing applications. You can quickly Web-enable green-screen applications, and adapt legacy applications for use in new Java environments. WebSphere delivers operational efficiency and enterprise quality of service for a mixed-workload infrastructure.AcceleratorsWebSphere Business Integration Accelerators are prebuilt accelerators that reduce deployment costs, provide increased cost savings, speed time to market, and lower business risk when using the IBM Business Integration infrastructure to solve customers business problems. IBM provides a set of accelerators for specific vertical industry processes. We also provide accelerators for processes that are common to many industries, such as multi-channel commerce. WebSphere Commerce provides prebuilt processes for business-to-consumer selling, as well as business-to-business selling. IBM also provides a set of prebuilt, industry-specific middleware designed to accelerate business transformation initiatives. As an example, IBM provides a prebuilt accelerator for improving supply chain integration in the electronics industry. This solution features WebSphere Business Integration Connect with a Rosettanet Accelerator. The middleware enables companies to send and receive electronic documents over the Internet, using the Rosettanet industry standard.In terms of increased cost savings:WebSphere Business Integration customers can reduce their integration costs by 50 percent when using prebuilt accelerators, when compared to traditional methods. Customers using prebuilt accelerators can achieve a high degree of reuse, reducing integration project costs and lowering total cost of ownership (TCO). Accelerators can speed the time to market:Prepackaged, out-of-the-box accelerators mean that customers are not bogged down in customer coding, allowing you to get to market more quickly. Customers are freed to focus on customization, not building and testing. Customers can focus on achieving business results, not on development.</Text1>"
	//		+ "<Text2>A. What is WebSphere?In todays business environment, it is increasingly important for all enterprises to develop and implement intelligent on demand strategies. In recent years, IBM has established itself as the industry leader in providing state-of-the-art on demand business solutions. In order to meet the rapidly emerging real-time requirements of businesses everywhere, IBM has launched the WebSphere software suite. WebSphere is an IBM software brand used to distinguish a suite of more than 300 software products. The magnitude of WebSphere has led to the misperception that WebSphere is too complex and expensive for SMBs. In truth, WebSphere is tremendously flexible, and WebSphere products are ideally suited to the on demand needs of both SMBs and large enterprises.IBM WebSphere software provides businesses with an IT infrastructure that maximizes both flexibility and responsiveness. By integrating people, processes, and information, WebSphere enables businesses to respond to changing business conditions with improved flexibility and speed. By optimizing the application infrastructure, WebSphere creates a reliable, high-performance environment for deploying and running applicat fosters operational excellence. And by extending the reach of IT, WebSphere enables companies to maximize the use of their IT infrastructure to reach users in new ways and support new business models.Figure 1: Business AlignmentIBM WebSphere software, with its integration and infrastructure capabilities, is a key part of IBM s overall middleware platform. IBM s middleware platform extends beyond WebSphere to include infrastructure management capabilities including security, provisioning, and infrastructure orchestration. The middleware also extends to business-driven development capabilities for improving the software life cycle management process. Furthermore, IBM s middleware platform provides business performance management capabilities, which extend the process integration and management capability of WebSphere to include the monitoring and optimization of the IT resources, which support your business processes. The IBM middleware platform is delivered as a modular product portfolio built on open standards. While functionally rich, the platform can be adopted incrementally, and you can start by adopting only those capabilities that you need to solve your most pressing business challenges. It provides integrated, role-based tools for application development and administration, utilizing a common installation, administration, security, and programming model. In short, the IBM middleware platform makes your technology infrastructure simple to develop, deploy, and manage.The WebSphere software suite enables on demand flexibility through a set of integration and infrastructure capabilities. Consider the following illustration; it shows WebSpheres capabilities, from the top down, in six distinct categories: People Integration, Process Integration, Information Integration, Application Integration, Application Infrastructure, and Accelerators. This abstract description of the WebSphere suite is probably less exciting than descriptions of the actual products; nevertheless, the capability classification is essential to defining WebSphere, because it provides the clearest orientation to the different areas that the WebSphere suite addresses.Modeling, simulating, and optimizing business processes Enabling you to efficiently manage, integrate, and access information that is scattered throughout the enterprise, and even beyond the enterprise in other companies with which you do businessApplication IntegrationWebSphere middleware ensures reliable and flexible information flow among diverse applications and organizations. The middleware enables reliability and seamless exchange of data among multiple applications. WebSphere software helps you manage the differences between multiple applications and business partners, and it adopts an enterprise-wide, flexible, service-oriented approach to integration.Application InfrastructureWebSphere middleware allows you to build, deploy, integrate, and enhance new and existing applications. You can quickly Web-enable green-screen applications, and adapt legacy applications for use in new Java environments. WebSphere delivers operational efficiency and enterprise quality of service for a mixed-workload infrastructure.AcceleratorsWebSphere Business Integration Accelerators are prebuilt accelerators that reduce deployment costs, provide increased cost savings, speed time to market, and lower business risk when using the IBM Business Integration infrastructure to solve customers business problems. IBM provides a set of accelerators for specific vertical industry processes. We also provide accelerators for processes that are common to many industries, such as multi-channel commerce. WebSphere Commerce provides prebuilt processes for business-to-consumer selling, as well as business-to-business selling. IBM also provides a set of prebuilt, industry-specific middleware designed to accelerate business transformation initiatives. As an example, IBM provides a prebuilt accelerator for improving supply chain integration in the electronics industry. This solution features WebSphere Business Integration Connect with a Rosettanet Accelerator. The middleware enables companies to send and receive electronic documents over the Internet, using the Rosettanet industry standard.In terms of increased cost savings:WebSphere Business Integration customers can reduce their integration costs by 50 percent when using prebuilt accelerators, when compared to traditional methods. Customers using prebuilt accelerators can achieve a high degree of reuse, reducing integration project costs and lowering total cost of ownership (TCO). Accelerators can speed the time to market:Prepackaged, out-of-the-box accelerators mean that customers are not bogged down in customer coding, allowing you to get to market more quickly. Customers are freed to focus on customization, not building and testing. Customers can focus on achieving business results, not on development.</Text2>";

	private static String contentID1 = "urn:uuid:MyUniqueContentID1";
	private static String contentID2 = "urn:uuid:MyUniqueContentID2";

	private static String sampleRequest_OneAttachment = "<test:echoOMElement xmlns:test=\"http://org/apache/axis2/jaxws/test/OMELEMENT\">"
			+ "<test:input>"
			+ "<xop:Include xmlns:xop=\"http://www.w3.org/2004/08/xop/include\" href=\"cid:"
			+ contentID1 + "\"/>" + "</test:input>" + "</test:echoOMElement>";

	// 2 MTOM attachments with xop:include
	private static String sampleRequest_Attachments = "<test:echoOMElement xmlns:test=\"http://org/apache/axis2/jaxws/test/OMELEMENT\">"
			+ "<test:binaryContent>"
			+ "<xop:Include xmlns:xop=\"http://www.w3.org/2004/08/xop/include\" href=\"cid:"
			+ contentID1
			+ "\"/>"
			+ "</test:binaryContent>"
			+ "<test:secondAttachment>"
			+ "<xop:Include xmlns:xop=\"http://www.w3.org/2004/08/xop/include\" href=\"cid:"
			+ contentID2
			+ "\"/>"
			+ "</test:secondAttachment>"
			+ "<test:inlineAttachment>7f2d1da3342c4b9742e4e7742f7e8f04</test:inlineAttachment>"
			+ "<reserved>success</reserved>"
			+ "<size>1024</size>"
			+ "</test:echoOMElement>";

	private static String sampleRequest_noXOP = 
		"<test:echoOMElement xmlns:test=\"http://org/apache/axis2/jaxws/test/OMELEMENT\">"
		+ "<test:inlineAttachment>7f2d1da3342c4b9742e4e7742f7e8f04</test:inlineAttachment>"
		+ "<reserved>success</reserved>"
		+ "<size>1024</size>"
		+ "</test:echoOMElement>";

	private static String sampleRequest_contentType = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\""
			+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
			+ "xmlns:ns1=\"http://example.org/mtom/data\""
			+ "xmlns:ns2=\"http://www.w3.org/2005/05/xmlmime\">"
			+ "<S:Body>"
			+ "<ns1:TestMtomXmimeContentTypeResponse ns2:contentType=\"application/xml\">"
			+ "<xop:Include xmlns:xop=\"http://www.w3.org/2004/08/xop/include\""
			+ "href=\"cid:"
			+ contentID1
			+ "\"/>"
			+ "</ns1:TestMtomXmimeContentTypeResponse>"
			+ "</S:Body>"
			+ "</S:Envelope>";

      	private static boolean initialized = false;


	public static Test suite() {
		return new TestSuite(OMProviderMTOMTests.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void suiteSetup(ConfigRequirement req) throws Exception {
        super.suiteSetup(req);
		System.out.println("suiteSetup() called");

		String host = TopologyDefaults.getDefaultAppServer().getNode()
				.getHostname();
		String port = TopologyDefaults.getDefaultAppServer().getPortNumber(
				PortType.WC_defaulthost).toString();

		hostAndPort = "http://" + host + ":" + port;
                if (!initialized) {
        	     OMProviderMTOMTestChecker checker = new OMProviderMTOMTestChecker();
		     OMHelper.addChecker("OMProviderMTOMTests", checker);
		     initialized = true;
		}
	}

	/**
	 * This test validates OMHelper methods while using those methods to create a message.
	 * 
	 * Send: a dispatch<OMElement> message with a MTOM attachment in PAYLOAD mode.
	 * Expected results: 
	 * - The OMHelper functions work as expected.
	 * - The attachment in the response is a new attachment from the service.
	 */
	public void testOMElementDispatchPayloadMode_APITest() throws Exception {

		// Create the OMElement object with the payload contents
		Dispatch<OMElement> dispatch = createDispatch("OMProviderMTOMService",
				null, Mode.PAYLOAD);

		/*
		 * Use OMHelper APIs
		 */

		// Create the OMElement from the XML String. we're in PAYLOAD mode, we
		// don't have to worry about the envelope.

		OMElement om = createOMElement(sampleRequest_OneAttachment);
		assertFalse("OMElement created with outline enabled", OMHelper
				.isSerializeAttachmentOutline(om));

		// the OMElment om is created using XOPAwareStAXOMBuilder. If the
		// request has xop:Include,
		// running om.toString() before running
		// setSerializeAttachmentOutline(om, true) will get an OMException
		try {
			System.out.println("before_setSerializeAttachmentOutline:\n"
					+ om.toString());
		} catch (OMException ome) {
			System.out.println("Caught expected exception OMException");
		}

		// we only support setSerializeAttachmentOutline with true value
		OMHelper.setSerializeAttachmentOutline(om, true);
		assertTrue("OMElement outline was not enabled", OMHelper
				.isSerializeAttachmentOutline(om));

		// om.toString() now works
		System.out.println("after setSerializeAttachmentOutline:\n"
				+ om.toString());

		/*
		 * add an attachment with content id predefined in the xml document
		 * sampleRequest
		 */

		String addAttachment = "This is a new attachment added by the client";
		ByteArrayDataSource byteDS = new ByteArrayDataSource(addAttachment
				.getBytes(), "text/plain");

		DataHandler attachmentDH = new DataHandler(byteDS);
		String attachmentCID = OMHelper.forceAddAttachment(om, attachmentDH,
				null, contentID1);
		System.out.println("attachmentCID=" + attachmentCID);

		// Validate the add attachment and outline enablement worked as expected
		assertTrue("Returned CID from forceAddAttachment was not correct",
				attachmentCID.equals("cid:" + contentID1));

		// verify that the result should be outlining - attachments with
		// xop:include should not be changed to inline attachments.
		String outlineOE = om.toString();
		assertTrue("Outlined OE does not contain Include", outlineOE
				.contains("Include"));
		assertTrue("Outlined OE does not contain specified ContentID",
				outlineOE.contains(contentID1));

		Map requestAttachmentMap = OMHelper.getAllAttachments(om);
		assertTrue("Incorrect number of attachments on request",
				(requestAttachmentMap != null)
						&& (requestAttachmentMap.size() >= 1));

		String attachmentEntryString = getStringFromAttachment(om, contentID1);
		assertTrue("Added attachment string incorrect", addAttachment
				.equals(attachmentEntryString));

		/*
		 * send the message and verify the result
		 */

		OMElement response = null;
		try {
			response = dispatch.invoke(om);
		} catch (Throwable t) {
			System.out.println("Unexpected exception: " + t.toString());
			fail("Unexpected exception: " + t);
		}

		// Validate the response outline enablement worked as expected

		// run setSerializeAttachmentOutline right after receiving the OMElement
		OMHelper.setSerializeAttachmentOutline(response, true);
		String responseOutline = response.toString();

		System.out.println("response after_setSerializeAttachmentOutline:\n"
				+ responseOutline);

		assertTrue("Outlined response does not contain Include",
				responseOutline.contains("Include"));

		// Validate the response attachments are as expected.
		// the client's attachment was replaced by a different attachment using
		// the same content ID
		Map responseAttachmentsMap = OMHelper.getAllAttachments(response);
		assertTrue("Incorrect number of attachments on response",
				(responseAttachmentsMap != null)
						&& (responseAttachmentsMap.size() >= 1));
		String responseAttachmentString = getStringFromAttachment(response,
				contentID1);
		System.out.println("responseAttachmentString:\n"
				+ responseAttachmentString);
		assertTrue(
				"Response attachment string incorrect",
				"This is a new attachment added by the endpoint which should be added to the cloned OE"
						.equals(responseAttachmentString));
	}

	/**
     * This test uses SimpleOMProviderMTOMService. There is no validation of the
	 * client's attachments at the service side so we can send different attachments to it instead of
	 * a predefined text string.
	 * Send: a dispatch<OMElement> message with a MTOM attachment in PAYLOAD mode.
	 * The attachemt is read from a jpeg file using FileDataSource.
	 * Expected results: 
	 * - The OMHelper functions work as expected.
	 * - The attachment in the response is a new attachment from the service.
     */
	public void testOMElementDispatch_imageFile() throws Exception {

		Dispatch<OMElement> dispatch = createDispatch(
				"SimpleOMProviderMTOMService", null, Mode.PAYLOAD);

		// Create the OMElement object with the payload contents
		OMElement om = createOMElement(sampleRequest_OneAttachment);
		OMHelper.setSerializeAttachmentOutline(om, true);

		// debug
		System.out.println("after setSerializeAttachmentOutline:\n"
				+ om.toString());

		String jpegFilename = "OMTestImage.jpeg"; // about 60KB

		File file = new File(jpegFilename);
		System.out.println(">> Loading data from " + jpegFilename);
		FileDataSource fds = new FileDataSource(file);

		/*
		 * add an attachment with content id predefined in the xml document
		 */

		DataHandler attachmentDH = new DataHandler(fds);
		String attachmentCID = OMHelper.forceAddAttachment(om, attachmentDH,
				"image/jpeg", contentID1);
		System.out.println("attachmentCID=" + attachmentCID);

		Map requestAttachmentMap = OMHelper.getAllAttachments(om);
		assertTrue("Incorrect number of attachments on request",
				(requestAttachmentMap != null)
						&& (requestAttachmentMap.size() >= 1));

		/*
		 * send the message and verify the new attachment result
		 */

		OMElement response = null;
		try {
			response = dispatch.invoke(om);
		} catch (Throwable t) {
			System.out.println("Unexpected exception: " + t.toString());
			fail("Unexpected exception: " + t);
		}

		// Validate the response outline enablement worked as expected
		OMHelper.setSerializeAttachmentOutline(response, true);
		String responseOutline = response.toString();

		System.out.println("response_after_setSerializeAttachmentOutline="
				+ responseOutline);

		String responseAttachmentString = getStringFromAttachment(response,
				contentID1);
		System.out.println("responseAttachmentString="
				+ responseAttachmentString);
		assertTrue(
				"Response attachment string incorrect",
				"This is a new attachment added by the endpoint which should be added to the cloned OE"
						.equals(responseAttachmentString));
	}

	/**
     *
	 * Send: a dispatch<OMElement> message with a MTOM attachment in PAYLOAD mode, using SOAP12.
	 * Expected results: 
	 * - The OMHelper functions work as expected.
	 * - The attachment in the response is a new attachment from the service.
     */
	public void testOMElementDispatchPayloadModeSoap12() throws Exception {
		
		Dispatch<OMElement> dispatch = createDispatch(
				"OMProviderMTOMSoap12Service", SOAPBinding.SOAP12HTTP_BINDING, Mode.PAYLOAD);

		// Create the OMElement object with the payload contents
		OMElement om = createOMElement(sampleRequest_OneAttachment);
		OMHelper.setSerializeAttachmentOutline(om, true);

		// debug
		System.out.println("after setSerializeAttachmentOutline:\n"
				+ om.toString());

		OMHelper.setSerializeAttachmentOutline(om, true);
		assertTrue("OMElement outline was not enabled", OMHelper
				.isSerializeAttachmentOutline(om));

		// debug
		System.out.println("after_setSerializeAttachmentOutline"
				+ om.toString());

		String addAttachment = "This is a new attachment added by the client";
		ByteArrayDataSource byteDS = new ByteArrayDataSource(addAttachment
				.getBytes(), "text/plain");
		DataHandler attachmentDH = new DataHandler(byteDS);
		String attachmentCID = OMHelper.forceAddAttachment(om, attachmentDH,
				null, contentID1);
		System.out.println("attachmentCID=" + attachmentCID);

		// Validate the add attachment and outline enablement worked as expected
		assertTrue("Returned CID from forceAddAttachment was not correct",
				attachmentCID.equals("cid:" + contentID1));

		// verify that the result should be outlining - attachments with
		// xop:include should not be changed to inline attachments.
		String outlineOE = om.toString();
		System.out.println("after_attachment_outlineOE=" + outlineOE);

		assertTrue("Outlined OE does not contain Include", outlineOE
				.contains("Include"));
		assertTrue("Outlined OE does not contain specified ContentID",
				outlineOE.contains(contentID1));
		Map requestAttachmentMap = OMHelper.getAllAttachments(om);
		assertTrue("Incorrect number of attachments on request",
				(requestAttachmentMap != null)
						&& (requestAttachmentMap.size() >= 1));

		String attachmentEntryString = getStringFromAttachment(om, contentID1);
		assertTrue("Added attachment string incorrect", addAttachment
				.equals(attachmentEntryString));

		OMElement response = null;
		try {
			response = dispatch.invoke(om);
		} catch (Throwable t) {
			System.out.println("Unexpected exception: " + t.toString());
			fail("Unexpected exception: " + t);
		}

		// Validate the response outline enablement worked as expected
		OMHelper.setSerializeAttachmentOutline(response, true);
		String responseOutline = response.toString();

		System.out.println("response_after_setSerializeAttachmentOutline="
				+ responseOutline);

		assertTrue("Outlined response does not contain Include", responseOutline.contains("Include"));

		// Validate the response attachments are as expected.
		// ( attachment was replaced by the service with a different attachment
		// using the same content ID)
		String responseAttachmentString = getStringFromAttachment(response,
				contentID1);
		System.out.println("responseAttachmentString="
				+ responseAttachmentString);
		assertTrue(
				"Response attachment string incorrect",
				"This is a new attachment added by the endpoint which should be added to the cloned OE"
						.equals(responseAttachmentString));
	}

	/**
	 * Send: a dispatch<OMElement> message in MESSAGE mode with 2 MTOM attachments.
	 * Expected results: 
	 * - The OMHelper functions work as expected.
	 * - The number of MTOM attachments in the response is correct.  One attachment was replace by a new attachment from the service.
     */
	public void testOMSourcedElementDispatchMessageMode_MultipleAttachments()  throws Exception {
		
		Dispatch<OMElement> dispatch = createDispatch(
				"OMProviderMTOMSoapMessageModeService", SOAPBinding.SOAP11HTTP_BINDING, Mode.MESSAGE);

		// Create a soap message with OMAbstractFactory
		SOAPFactory soapFactory = OMAbstractFactory.getSOAP11Factory();
		SOAPEnvelope envelope = soapFactory.createSOAPEnvelope();
		if (envelope.getBody() == null) {
			soapFactory.createSOAPBody(envelope);
		}
		
		// run OMAbstractFactory on the envelope
		OMHelper.setSerializeAttachmentOutline(envelope, true);

		// Send multiple elements in the Body, similar to what BPM is doing
		String sampleRequest = sampleRequest_Attachments;
		String ENCODING = "utf-8";
		try {
			OMDataSource dataSource = new org.apache.axiom.om.ds.ByteArrayDataSource(
					sampleRequest.getBytes(ENCODING), ENCODING);
			QName qname = new QName(
					"http://org/apache/axis2/jaxws/test/OMELEMENT",
					"echoOMElement", "test");
			OMFactory omFactory = OMAbstractFactory.getOMFactory();
			OMNamespace omNS = omFactory.createOMNamespace(qname
					.getNamespaceURI(), qname.getPrefix());
			OMSourcedElement omElement = omFactory.createOMElement(dataSource,
					qname.getLocalPart(), omNS);

			// try toString()
			System.out
					.println("before setSerializeAttachmentOutline , omElement="
							+ omElement.toString());

			envelope.getBody().addChild(omElement);

			String addAttachment = "This is a new attachment added by the client";
			ByteArrayDataSource byteDS = new ByteArrayDataSource(addAttachment
					.getBytes(), "text/plain");
			DataHandler attachmentDH = new DataHandler(byteDS);
			String attachmentCID = OMHelper.forceAddAttachment(envelope,
					attachmentDH, null, contentID1);
			System.out.println("attachmentCID=" + attachmentCID);

			// add attachment #2
			String addAttachment2 = "This is the second attachment added by the client";
			ByteArrayDataSource byteDS2 = new ByteArrayDataSource(
					addAttachment2.getBytes(), "text/plain");
			DataHandler attachmentDH2 = new DataHandler(byteDS2);
			String attachmentCID2 = OMHelper.forceAddAttachment(envelope,
					attachmentDH2, null, contentID2);
			System.out.println("attachmentCID2=" + attachmentCID2);

			// verify number of MTOM attachments
			Map resquestAttachmentsMap = OMHelper.getAllAttachments(envelope);
			System.out.println("number of attachments in request="
					+ resquestAttachmentsMap.size());
	        assertTrue("Incorrect number of attachments on request", (resquestAttachmentsMap != null) && (resquestAttachmentsMap.size() >= 2));


	        // verify the attachments' content
			String resquestAttachment1 = getStringFromAttachment(envelope,
					contentID1);
			System.out.println("resquestAttachmentString="
					+ resquestAttachment1);
	        assertTrue("Request attachment string #1 incorrect", 
	        		"This is a new attachment added by the client".equals(resquestAttachment1));

			String resquestAttachment2 = getStringFromAttachment(envelope,
					contentID2);
			System.out.println("resquestAttachmentString="
					+ resquestAttachment2);
	        assertTrue("Request attachment string #2 incorrect", 
	        		"This is the second attachment added by the client".equals(resquestAttachment2));

	        
			OMElement response = null;
			try {
				response = dispatch.invoke(envelope);
			} catch (Throwable t) {
				System.out.println("Unexpected exception: " + t.toString());
				fail("Unexpected exception: " + t);
			}
			
			// Validate the response outline enablement worked as expected
			OMHelper.setSerializeAttachmentOutline(response, true);
			String responseOutline = response.toString();
			assertTrue("Outlined response does not contain Include",
					responseOutline.contains("Include"));

			// Validate the response attachments are as expected.
			Map responseAttachmentsMap = OMHelper.getAllAttachments(response);
			System.out.println("number of attachments in response="
					+ responseAttachmentsMap.size());

			assertTrue("Incorrect number of attachments on response",
			 (responseAttachmentsMap != null) &&
			 (responseAttachmentsMap.size() == 2));
			
			// attachment with contentID1 is new from service
			String responseAttachment1 = getStringFromAttachment(response,
					contentID1);
			System.out.println("responseAttachment1="
					+ responseAttachment1);
			assertTrue("Response attachment #1 incorrect",
					 "This is a new attachment added by the endpoint which should be added to the cloned OE".equals(responseAttachment1));
			
			// attachment with contentID2 is the same as the sending attachment
			String responseAttachment2 = getStringFromAttachment(
					response, contentID2);
			System.out.println("responseAttachment2="
					+ responseAttachment2);

			assertTrue("Response attachment string incorrect",
			 "This is the second attachment added by the client".equals(responseAttachment2));

		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Caught unexpected exception " + ex);
		}

	}

	/**
	 * Send: a dispatch<OMElement> message in PAYLOAD mode with a MTOM attachment.
	 * Both the client and service have MTOM enabled with SOAPBinding.SOAP11HTTP_MTOM_BINDING. 
	 * Expected results: 
	 * - The OMHelper functions work as expected.  Regardless of the MTOM threshold, when the message is
	 * serialized, the xop:Include will not be replaced with inline text representing the attachment.
     */

	public void testOMElementDispatch_serviceMTOMEnabled() throws Exception {
		
		Dispatch<OMElement> dispatch = createDispatch(
				"OMProviderMTOMEnabledService", SOAPBinding.SOAP11HTTP_MTOM_BINDING, Mode.PAYLOAD);

		// Create the OMElement object with the payload contents
		OMElement om = createOMElement(sampleRequest_OneAttachment);
		OMHelper.setSerializeAttachmentOutline(om, true);

		// debug
		System.out.println("after setSerializeAttachmentOutline:\n"
				+ om.toString());

		String addAttachment = "This is a new attachment added by the client";
		ByteArrayDataSource byteDS = new ByteArrayDataSource(addAttachment
				.getBytes(), "text/plain");
		DataHandler attachmentDH = new DataHandler(byteDS);
		String attachmentCID = OMHelper.forceAddAttachment(om, attachmentDH,
				null, contentID1);
		System.out.println("attachmentCID=" + attachmentCID);

		// Validate the add attachment and outline enablement worked as expected
		assertTrue("Returned CID from forceAddAttachment was not correct",
				attachmentCID.equals("cid:" + contentID1));

		// verify that the result should be outlining - attachment with
		// xop:include should not be changed to inline attachment.
		String outlineOE = om.toString();
		System.out.println("after_attachment_outlineOE=" + outlineOE);

		assertTrue("Outlined OE does not contain Include", outlineOE
				.contains("Include"));
		assertTrue("Outlined OE does not contain specified ContentID",
				outlineOE.contains(contentID1));
		Map requestAttachmentMap = OMHelper.getAllAttachments(om);
		assertTrue("Incorrect number of attachments on request",
				(requestAttachmentMap != null)
						&& (requestAttachmentMap.size() >= 1));

		String attachmentEntryString = getStringFromAttachment(om, contentID1);
		assertTrue("Added attachment string incorrect", addAttachment
				.equals(attachmentEntryString));

		OMElement response = null;
		try {
			response = dispatch.invoke(om);
		} catch (Throwable t) {
			System.out.println("Unexpected exception: " + t.toString());
			fail("Unexpected exception: " + t);
		}

		// Validate the response outline enablement worked as expected
		OMHelper.setSerializeAttachmentOutline(response, true);
		String responseOutline = response.toString();

		System.out.println("response_after_setSerializeAttachmentOutline="
				+ responseOutline);

		String responseAttachmentString = getStringFromAttachment(response,
				contentID1);
		System.out.println("responseAttachmentString="
				+ responseAttachmentString);
		assertTrue(
				"Response attachment string incorrect",
				"This is a new attachment added by the endpoint which should be added to the cloned OE"
						.equals(responseAttachmentString));
	}

	/**
	 * Send: a dispatch<OMElement> message in MESSAGE mode with SOAP12
	 * Expected results: 
	 * - The OMHelper functions work as expected.
	 * - The attachment in the response is a new attachment from the service.
     */
	public void testOMSourcedElementDispatchMessageModeSoap12() {
		
		Dispatch<OMElement> dispatch = createDispatch(
				"OMProviderMTOMSoapMessageModeSOAP12Service", SOAPBinding.SOAP12HTTP_BINDING, Mode.MESSAGE);

		SOAPFactory soapFactory = OMAbstractFactory.getSOAP12Factory();
		SOAPEnvelope envelope = soapFactory.createSOAPEnvelope();
		if (envelope.getBody() == null) {
			soapFactory.createSOAPBody(envelope);
		}
		OMHelper.setSerializeAttachmentOutline(envelope, true);

		// String contentID = contentID1;
		// Send multiple elements in the Body, similar to what BPM is doing
		String sampleRequest = sampleRequest_OneAttachment;
		String ENCODING = "utf-8";
		try {
			OMDataSource dataSource = new org.apache.axiom.om.ds.ByteArrayDataSource(
					sampleRequest.getBytes(ENCODING), ENCODING);
			QName qname = new QName(
					"http://org/apache/axis2/jaxws/test/OMELEMENT",
					"echoOMElement", "test");
			OMFactory omFactory = OMAbstractFactory.getOMFactory();
			OMNamespace omNS = omFactory.createOMNamespace(qname
					.getNamespaceURI(), qname.getPrefix());
			OMSourcedElement omElement = omFactory.createOMElement(dataSource,
					qname.getLocalPart(), omNS);
			envelope.getBody().addChild(omElement);

			String addAttachment = "This is a new attachment added by the client";
			ByteArrayDataSource byteDS = new ByteArrayDataSource(addAttachment
					.getBytes(), "text/plain");
			DataHandler attachmentDH = new DataHandler(byteDS);
			String attachmentCID = OMHelper.forceAddAttachment(envelope,
					attachmentDH, null, contentID1);

			OMElement response = null;
			try {
				response = dispatch.invoke(envelope);
			} catch (Throwable t) {
				System.out.println("Caught " + t.toString());
				t.printStackTrace();
				fail("Caught exception: " + t);
			}
			// Validate the response outline enablement worked as expected
			OMHelper.setSerializeAttachmentOutline(response, true);
			String responseAttachmentString = getStringFromAttachment(response,
					contentID1);
			System.out.println("responseAttachmentString="
					+ responseAttachmentString);

			assertTrue("Response attachment string incorrect",
			 "This is a new attachment added by the endpoint which should be added to the cloned OE".equals(responseAttachmentString));

		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Caught unexpected exception " + ex);
		}
	}


	/**
	 * Send: a dispatch<OMElement> message in PAYLOAD mode with no XOP:Include in the xml document.
	 * Expected results: 
	 * - The OMHelper functions don't change the message
     */
	public void testOMElementDispatchPayloadMode_noAttachment() throws Exception {
		
		// Create the OMElement object with the payload contents
		Dispatch<OMElement> dispatch = createDispatch("OMProviderMTOM_noAttachmentService",
				null, Mode.PAYLOAD);

		// Create the OMElement from the XML String
		OMElement om = createOMElement(sampleRequest_noXOP);

		// debug
		System.out.println("before_setSerializeAttachmentOutline"
				+ om.toString());

		// when there is no attachment with xop:include, we expect nothing changed after OMElement is created
		assertFalse("Create the OMElement changed the orginial message", om
				.toString().compareTo(sampleRequest_noXOP) != 0);
		OMHelper.setSerializeAttachmentOutline(om, true);
		
		// when there is no attachment with xop:include, we expect nothing changed after setSerializeAttachmentOutline
		System.out.println("after_setSerializeAttachmentOutline"
				+ om.toString());
		assertFalse(
				"setSerializeAttachmentOutline changed the orginial message",
				om.toString().compareTo(sampleRequest_noXOP) != 0);

		OMElement response = null;
		try {
			response = dispatch.invoke(om);
		} catch (Throwable t) {
			System.out.println("Unexpected exception: " + t.toString());
			fail("Unexpected exception: " + t);
		}

		// Validate the response is the same as the original message
		String responseOutline = response.toString();

		System.out.println("response_after_setSerializeAttachmentOutline="
				+ responseOutline);

		assertFalse("response is not matched with orginial message", om
				.toString().compareTo(sampleRequest_noXOP) != 0);
	}


	/**
	 * Attempt to generate a CID from OMHelper.forceAddAttachment
	 * Expected results: generated CID is not Null
     */

	public void testOMElementDispatch_nocid() throws Exception {
		

		// Create the OMElement object with the payload contents
		OMElement om = createOMElement(sampleRequest_OneAttachment);
		OMHelper.setSerializeAttachmentOutline(om, true);

		// debug
		System.out.println("after_setSerializeAttachmentOutline"
				+ om.toString());

		String addAttachment = "This is a new attachment added by the client";
		ByteArrayDataSource byteDS = new ByteArrayDataSource(addAttachment
				.getBytes(), "text/plain");
		DataHandler attachmentDH = new DataHandler(byteDS);
		
		// provide no content id to forceAddAttachment
		String attachmentCID = OMHelper.forceAddAttachment(om, attachmentDH,
				null, null);
		System.out.println("attachmentCID=" + attachmentCID);

		// Validate the content id is generated by forceAddAttachment
		assertFalse("Returned CID from forceAddAttachment is null", attachmentCID == null);
		
	}



	/*
	 * Utility methods
	 */

	/*
	 * Create the OMElement from the XML String, using
	 * org.apache.axiom.om.impl.builder.XOPAwareStAXOMBuilder
	 */
	public OMElement createOMElement(String request) throws XMLStreamException {

		// Create the OMElement from the XML String
		StringReader sr = new StringReader(request);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader inputReader = inputFactory.createXMLStreamReader(sr);
		Attachments attachments = new Attachments();
		XOPAwareStAXOMBuilder builder = new XOPAwareStAXOMBuilder(inputReader,
				attachments);
		OMElement om = builder.getDocumentElement();

		return om;
	}

	/*
	 * client creating dispatch with in-line attachment we refer to "inlining",
	 * that means replacing the <xop:Include> with base64 encoded text from the
	 * attachment, so it is contained within the XML message.
	 */
	public OMElement invokeService(String request, String endpointUrl,
			Mode serviceMode, boolean isSoap12) throws XMLStreamException {
		System.out.println("Creating service...");
		Service service = Service.create(serviceName);
		String requestMsg = null;

		try {
			if (isSoap12) {
				requestMsg = SOAP12_HEADER + request + SOAP12_TRAILER;
				service.addPort(portName, SOAPBinding.SOAP12HTTP_MTOM_BINDING,
						endpointUrl);
			} else {
				requestMsg = SOAP11_HEADER + request + SOAP11_TRAILER;
				service.addPort(portName, SOAPBinding.SOAP11HTTP_MTOM_BINDING,
						endpointUrl);
			}

			System.out
					.println("Creating dispatch<OMElement> with serviceMode = "
							+ serviceMode + ", requestMsg = " + requestMsg);

			Dispatch<OMElement> dispatch = service.createDispatch(portName,
					OMElement.class, serviceMode);

			// Create the OMElement from the XML String
			OMElement om = createOMElement(requestMsg);

			System.out.println("Invoking dispatch...");
			return dispatch.invoke(om);

		} catch (WebServiceException e) {
			e.printStackTrace();
			return null;
		} catch (XMLStreamException xe) {
			xe.printStackTrace();
			return null;
		}

	}

	private Dispatch<OMElement> createDispatch(String serviceEndpoint,
			String binding, Mode mode) {
		// set endpointUrl
		String endpointUrl = hostAndPort + "/omelementmtom/" + serviceEndpoint;
		System.out.println("endpointUrl = " + endpointUrl);

		Service service = Service.create(serviceName);
		service.addPort(portName, binding, endpointUrl);
		Dispatch<OMElement> dispatch = service.createDispatch(portName,
				OMElement.class, mode);
		return dispatch;
	}

	private String getStringFromAttachment(OMElement oe, String cid)
			throws Exception {
		Map attachments = OMHelper.getAllAttachments(oe);
		DataHandler checkDH = (DataHandler) attachments.get(cid);
		byte[] attachmentBytes = IOUtils.getStreamAsByteArray(checkDH
				.getInputStream());
		String attachmentString = new String(attachmentBytes);
		return attachmentString;
	}

	/**
	 * Method used to convert Strings to SOAPMessages
	 * 
	 * @param msgString
	 * @return
	 */
	public static SOAPMessage toSOAPMessage(String msgString) {

		if (msgString == null)
			return null;

		SOAPMessage message = null;
		try {

			MessageFactory factory = null;

			// Force the usage of specific MesasgeFactories
			if (msgString.indexOf(SOAP11_NAMESPACE) >= 0) {
				factory = MessageFactory
						.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			} else {
				factory = MessageFactory
						.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
			}

			message = factory.createMessage();
			message.getSOAPPart().setContent(
					(Source) new StreamSource(new StringReader(msgString)));
			message.saveChanges();
		} catch (SOAPException e) {
			System.out.println("toSOAPMessage Exception encountered: " + e);
			e.printStackTrace();
		}

		return message;
	}
}
