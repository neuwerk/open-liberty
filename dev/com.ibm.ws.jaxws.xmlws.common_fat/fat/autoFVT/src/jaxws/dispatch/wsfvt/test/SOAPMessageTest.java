//
// @(#) 1.1.1.7 autoFVT/src/jaxws/dispatch/wsfvt/test/SOAPMessageTest.java, WAS.websvcs.fvt, WASX.FVT 1/22/07 11:46:38 [7/11/07 13:15:20]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 08/23/06 sedov    LIDB3296-42.02     New File/Beta Drop
// 12/19/06 sedov    409973             Fixed asyncPollMessage
// 01/09/07 sedov    413290             Added MTOM binding tests
// 01/20/07 sedov    415799             Added FAULT_TEXT constant
//

package jaxws.dispatch.wsfvt.test;

import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.dispatch.wsfvt.common.CallbackHandler;
import jaxws.dispatch.wsfvt.common.Constants;
import jaxws.dispatch.wsfvt.common.KillerThread;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for SOAPMessage Dispatch.
 * 
 * Valid combiantions are SOAP1.1 and SOAP1.2 in Message Mode Payload mode or
 * HTTP Bidning are not valid
 * 
 */
public class SOAPMessageTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private KillerThread killer = null;

	public SOAPMessageTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(SOAPMessageTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy Test invokeOneWay on SOAPMessage Dispatch in MESSAGE mode
	 * 
	 * @WSDL DispatchSOAP11.wsdl
	 * @Target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_invokeOneWay_Message() throws Exception {
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;
		SOAPMessage message = Constants.toSOAPMessage(msg);

		Constants.logRequest(msg);
		dispatch.invokeOneWay(message);
	}

	/**
	 * @testStrategy Test invoke on SOAPMessage Dispatch in MESSAGE mode
	 * 
	 * @WSDL DispatchSOAP11.wsdl
	 * @Target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_invoke_Message() throws Exception {
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;
		SOAPMessage message = Constants.toSOAPMessage(msg);

		Constants.logRequest(msg);
		SOAPMessage res = dispatch.invoke(message);

		String resp = Constants.toString(res);
		Constants.testEnvelope(resp, true);		
		
		assertTrue("Response is null", res != null);
		assertTrue("Unexpected Response", resp.indexOf(getName()) != -1);
	}

	/**
	 * @testStrategy Test invokeAsync (polling) on SOAPMessage Dispatch in
	 *               MESSAGE mode
	 * 
	 * @wsdl DispatchSOAP11.wsdl
	 * @Target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_invokeAsycPoll_Message() throws Exception {
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// create message
		String msg = Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;
		SOAPMessage message = Constants.toSOAPMessage(msg);

		// invoke and wait for response
		Constants.logRequest(msg);
		Response<SOAPMessage> response = dispatch.invokeAsync(message);
		SOAPMessage res = response.get();

		String resp = Constants.toString(res);
		Constants.testEnvelope(resp, true);
		
		// validate response
		assertTrue("Response is null", res != null);
		assertTrue("Unexpected Response", resp.indexOf(
				getName()) != -1);
	}

	/**
	 * Test sending a two way message using Message mode (AsyncCalback)
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_invokeAsycCallback_Message() throws Exception {
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		CallbackHandler<SOAPMessage> handler = new CallbackHandler<SOAPMessage>();

		// create message
		String msg = Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;
		SOAPMessage message = Constants.toSOAPMessage(msg);

		// invoke and wait for response
		Constants.logRequest(msg);
		Future<?> monitor = dispatch.invokeAsync(message, handler);
		handler.waitBlocking(monitor);

		try {
			// validate response
			SOAPMessage res = handler.getResponse().get();

			String resp = Constants.toString(res);
			Constants.testEnvelope(resp, true);
			
			assertTrue("Response is null", res != null);
			assertTrue("Unexpected Response", resp.indexOf(
					getName()) != -1);
		} catch (WebServiceException e) {
			e.printStackTrace(System.out);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * Try sending a 1MB+ message through
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_invoke_largeMessage() throws Exception {
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build a large message
		StringBuffer message = new StringBuffer();
		message.append(Constants.SOAP11_HEADER);
		message.append("<ns1:twoWay xmlns:ns1=\""
				+ Constants.WSDL_NAMESPACE + "\">");
		for (int i = 0; i < 30000; i++) {
			message.append("<ns1:value>test_message_" + i + "</ns1:value>");
		}
		message.append("</ns1:twoWay>");
		message.append(Constants.SOAP11_TRAILER);

		try {
			dispatch.invoke(Constants.toSOAPMessage(message.toString()));
		} catch (WebServiceException wse) {
			fail("Unexpected WebServiceException when sending 1MB+ message: "
					+ wse.getMessage());
		}
	}

	/**
	 * Test receiving an exception
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_invoke_endpointException() throws Exception {
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.TWOWAY_MSG_EXCEPTION.replace("test_message",
				getName());
		SOAPMessage src = Constants.toSOAPMessage(Constants.SOAP11_HEADER + msg
				+ Constants.SOAP11_TRAILER);

		try {
			dispatch.invoke(src);
			fail("WebServiceException expected when endpoint returns a soap:Fault.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			assertTrue(
					"WebServiceException should contain the exception text.",
					wse.getMessage() != null
							&& wse.getMessage().indexOf(Constants.FAULT_TEXT) != -1);
		}
	}

	/**
	 * Try to send a null message, this should throw an exception. This should
	 * test to see if the message ever leaves the Dispatch and that the WSE is
	 * thrown
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_invoke_null_Message() throws Exception {
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);
		try {
			dispatch.invoke(null);

			fail("WebServiceException should be thrown when a null message is sent");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * Try to send an empty SOAPMessage object, this should throw an exception.
	 * This should test to see if the message ever leaves the Dispatch and that
	 * the WSE is thrown
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_invoke_empty_Message() throws Exception {
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// get the SOAP11 MessageFactory
		MessageFactory mf = MessageFactory.newInstance();

		try {
			Map<String, Object> rc = ((BindingProvider) dispatch)
					.getRequestContext();
			rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
			rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, "empty");

			// create a message but we will not pupulate it
			dispatch.invoke(mf.createMessage());

			// fail("WebServiceException should be thrown when an empty message
			// is sent");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test creating a PAYLOAD mode SOAPMessage dispatch
	 * 
	 * @WSDL DispatchSOAP11.wsdl
	 * @Target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_create_SOAP11_Payload() throws Exception {

		try {
			Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.PAYLOAD,
					Constants.SOAP11_ENDPOINT_ADDRESS);

			dispatch.invoke(Constants.toSOAPMessage(Constants.TWOWAY_MSG));

			fail("WSE expected when creating Payload mode SOAPMessage dispatches");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}	

	/** *********************** Binding tests ********************** */

	/**
	 * Test Sending messages over SOAP 1.1/Message mode with MTOM binding
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_invoke_SOAP11_MTOM_Message() throws Exception {
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP11HTTP_MTOM_BINDING);

		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		String message = Constants.SOAP11_HEADER + msg
				+ Constants.SOAP11_TRAILER;

		try {
			Constants.logRequest(message);
			SOAPMessage res = dispatch.invoke(Constants.toSOAPMessage(message));

			String resp = Constants.toString(res);
			Constants.testEnvelope(resp, true);			
			
			assertTrue("Response is null", res != null);
			assertTrue("Unexpected Response", resp.indexOf(
					getName()) != -1);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected :" + wse);
		}
	}

	/**
	 * Test Sending messages over SOAP 1.2/Message mode
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_invoke_SOAP12_Message() throws Exception {
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_BINDING);

		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		String message = Constants.SOAP12_HEADER + msg
				+ Constants.SOAP12_TRAILER;

		try {
			Constants.logRequest(message);
			SOAPMessage res = dispatch.invoke(Constants.toSOAPMessage(message));

			String resp = Constants.toString(res);
			Constants.testEnvelope(resp, true);			
			
			assertTrue("Response is null", res != null);
			assertTrue("Unexpected Response", resp.indexOf(getName()) != -1);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected :" + wse);
		}
	}

	/**
	 * Test Sending messages over SOAP 1.2/Message mode with MTOM binding
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_invoke_SOAP12_MTOM_Message() throws Exception {
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_MTOM_BINDING);

		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		String message = Constants.SOAP12_HEADER + msg
				+ Constants.SOAP12_TRAILER;

		try {
			Constants.logRequest(message);
			SOAPMessage res = dispatch.invoke(Constants.toSOAPMessage(message));

			String resp = Constants.toString(res);
			Constants.testEnvelope(resp, true);			
			
			assertTrue("Response is null", res != null);
			assertTrue("Unexpected Response", resp.indexOf(
					getName()) != -1);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected :" + wse);
		}
	}

	/**
	 * @testStrategy Test for creating a SOAPMessage Dispatch over HTTP Binding,
	 *               expected to throw an exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for creating a SOAPMessage Dispatch over HTTP Binding, expected to throw an exception",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_invoke_HTTP() throws Exception {

		try {
			Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
					Constants.HTTP_ENDPOINT_ADDRESS, HTTPBinding.HTTP_BINDING);

			String message = Constants.TWOWAY_MSG.replace("test_message",
					getName());
			
			Constants.toString(message);
			dispatch.invoke(Constants.toSOAPMessage(message));

			fail("WebServiceException expected when sending SOAPMessages over HTTP");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		} catch (UnsupportedOperationException uoe) {
			uoe.printStackTrace(System.out);
		}
	}

	/** ***************** Misc tests ********************* */

	/**
	 * @testStartegy Create a SOAPMessage using the SAAJ APIs, this test does
	 *               not sent any attachments
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAAJ_soap11_MessageFactory() throws Exception {
		final String MESSAGE = getName();
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP11HTTP_BINDING);

		// create SOAP Factory instance via factory
		MessageFactory mf = null;
		mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

		// Create a message, we'll work with the Part
		SOAPMessage msg = mf.createMessage();
		SOAPPart part = msg.getSOAPPart();

		// obtain the SOAPEnvelope and Header/Body
		SOAPEnvelope env = part.getEnvelope();
		SOAPHeader header = env.getHeader();
		SOAPBody body = env.getBody();

		// construct the payload
		SOAPElement operation = body.addChildElement("twoWay", "ns1",
				Constants.WSDL_NAMESPACE);
		SOAPElement value = operation.addChildElement("value");
		value.addTextNode(getName());
		msg.saveChanges();

		// msg.writeTo(System.out);

		try {
			SOAPMessage resp = dispatch.invoke(msg);

			// Extract the response
			SOAPBody rsp_body = resp.getSOAPPart().getEnvelope().getBody();
			SOAPElement op = (SOAPElement) rsp_body.getFirstChild();
			SOAPElement val = (SOAPElement) op.getFirstChild();
			String response = val.getTextContent();

			// validate the response
			assertTrue("Received unexpected response", MESSAGE.equals(response));
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			fail("Unexpected: " + wse);
		}
	}

	/**
	 * @testStartegy Create a SOAPMessage using the SAAJ APIs, this test does
	 *               not sent any attachments. We will obtain MessageFactory
	 *               from the SOAPBinding associated with the dispatch
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAAJ_soap11_BindingProvider() throws Exception {
		final String MESSAGE = getName();
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP11HTTP_BINDING);

		// obtain Binding used by this dispatch
		BindingProvider bp = ((BindingProvider) dispatch);
		Binding bind = bp.getBinding();

		// verify that binding is SOAPBinding
		assertNotNull("BindingProvider.getBinding is null", bind);
		assertTrue("BindingProvider.getBinding is not SOAPBinding",
				bind instanceof SOAPBinding);

		SOAPBinding soapBind = ((SOAPBinding) bind);

		// obtan MessageFactory appropriate for this binding
		MessageFactory mf = soapBind.getMessageFactory();
		assertNotNull("SOAPBinding.getMessageFactory is null", mf);

		// Create a message, we'll work with the Part
		SOAPMessage msg = mf.createMessage();
		SOAPPart part = msg.getSOAPPart();

		// obtain the SOAPEnvelope and Header/Body
		SOAPEnvelope env = part.getEnvelope();
		SOAPHeader header = env.getHeader();
		SOAPBody body = env.getBody();

		// construct the payload
		SOAPElement operation = body.addChildElement("twoWay", "ns1",
				Constants.WSDL_NAMESPACE);
		SOAPElement value = operation.addChildElement("value");
		value.addTextNode(getName());
		msg.saveChanges();

		// msg.writeTo(System.out);

		try {
			SOAPMessage resp = dispatch.invoke(msg);

			// Extract the response
			SOAPBody rsp_body = resp.getSOAPPart().getEnvelope().getBody();
			SOAPElement op = (SOAPElement) rsp_body.getFirstChild();
			SOAPElement val = (SOAPElement) op.getFirstChild();
			String response = val.getTextContent();

			// validate the response
			assertTrue("Received unexpected response", MESSAGE.equals(response));
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			fail("Unexpected: " + wse);
		}
	}

	/**
	 * @testStartegy Create a SOAPMessage using the SAAJ APIs, and then send
	 *               using SOAPConnection
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAAJ_soap11_SoapConnection() throws Exception {
		final String MESSAGE = getName();

		MessageFactory mf = null;
		mf = MessageFactory.newInstance();

		// Create a message, we'll work with the Part
		SOAPMessage msg = mf.createMessage();
		SOAPPart part = msg.getSOAPPart();

		// obtain the SOAPEnvelope and Header/Body
		SOAPEnvelope env = part.getEnvelope();
		SOAPHeader header = env.getHeader();
		SOAPBody body = env.getBody();

		// construct the payload
		SOAPElement operation = body.addChildElement("twoWay", "ns1",
				Constants.WSDL_NAMESPACE);
		SOAPElement value = operation.addChildElement("value");
		value.addTextNode(getName());
		msg.saveChanges();

		// msg.writeTo(System.out);

		try {
			SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
			SOAPConnection sc = scf.createConnection();
			SOAPMessage resp = sc.call(msg, Constants.SOAP11_ENDPOINT_ADDRESS);

			// Extract the response
			SOAPBody rsp_body = resp.getSOAPPart().getEnvelope().getBody();
			SOAPElement op = (SOAPElement) rsp_body.getFirstChild();
			SOAPElement val = (SOAPElement) op.getFirstChild();
			String response = val.getTextContent();

			// validate the response
			assertTrue("Received unexpected response", MESSAGE.equals(response));
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected: " + wse);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			fail(e.toString());
		}
	}

	/**
	 * @testStartegy Create a SOAPMessage using the SAAJ APIs, this test does
	 *               not sent any attachments
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAAJ_soap12_MessageFactory() throws Exception {
		final String MESSAGE = getName();
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_BINDING);

		// create SOAP Factory instance via factory
		MessageFactory mf = null;
		mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

		// Create a message, we'll work with the Part
		SOAPMessage msg = mf.createMessage();
		SOAPPart part = msg.getSOAPPart();

		// obtain the SOAPEnvelope and Header/Body
		SOAPEnvelope env = part.getEnvelope();
		SOAPHeader header = env.getHeader();
		SOAPBody body = env.getBody();

		// construct the payload
		SOAPElement operation = body.addChildElement("twoWay", "ns1",
				Constants.WSDL_NAMESPACE);
		SOAPElement value = operation.addChildElement("value");
		value.addTextNode(getName());
		msg.saveChanges();

		// msg.writeTo(System.out);

		try {
			SOAPMessage resp = dispatch.invoke(msg);

			// Extract the response
			SOAPBody rsp_body = resp.getSOAPPart().getEnvelope().getBody();
			SOAPElement op = (SOAPElement) rsp_body.getFirstChild();
			SOAPElement val = (SOAPElement) op.getFirstChild();
			String response = val.getTextContent();

			// validate the response
			assertTrue("Received unexpected response", MESSAGE.equals(response));
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			fail("Unexpected: " + wse);
		}
	}

	/**
	 * @testStartegy Create a SOAPMessage using the SAAJ APIs, this test does
	 *               not sent any attachments. We will obtain MessageFactory
	 *               from the SOAPBinding associated with the dispatch
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAAJ_soap12_BindingProvider() throws Exception {
		final String MESSAGE = getName();
		Dispatch<SOAPMessage> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_BINDING);

		// obtain Binding used by this dispatch
		BindingProvider bp = ((BindingProvider) dispatch);
		Binding bind = bp.getBinding();

		// verify that binding is SOAPBinding
		assertNotNull("BindingProvider.getBinding is null", bind);
		assertTrue("BindingProvider.getBinding is not SOAPBinding",
				bind instanceof SOAPBinding);

		// obtan MessageFactory appropriate for this binding
		MessageFactory mf = ((SOAPBinding) bind).getMessageFactory();
		assertNotNull("SOAPBinding.getMessageFactory is null", mf);

		// Create a message, we'll work with the Part
		SOAPMessage msg = mf.createMessage();
		SOAPPart part = msg.getSOAPPart();

		// obtain the SOAPEnvelope and Header/Body
		SOAPEnvelope env = part.getEnvelope();
		SOAPHeader header = env.getHeader();
		SOAPBody body = env.getBody();

		// construct the payload
		SOAPElement operation = body.addChildElement("twoWay", "ns1",
				Constants.WSDL_NAMESPACE);
		SOAPElement value = operation.addChildElement("value");
		value.addTextNode(getName());
		msg.saveChanges();

		// msg.writeTo(System.out);

		try {
			SOAPMessage resp = dispatch.invoke(msg);

			// Extract the response
			SOAPBody rsp_body = resp.getSOAPPart().getEnvelope().getBody();
			SOAPElement op = (SOAPElement) rsp_body.getFirstChild();
			SOAPElement val = (SOAPElement) op.getFirstChild();
			String response = val.getTextContent();

			// validate the response
			assertTrue("Received unexpected response", MESSAGE.equals(response));
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			fail("Unexpected: " + wse);
		}
	}

	/**
	 * @testStartegy Create a SOAPMessage using the SAAJ APIs, and then send
	 *               using SOAPConnection
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAAJ_soap12_SoapConnection() throws Exception {
		final String MESSAGE = getName();

		MessageFactory mf = null;
		mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

		// Create a message, we'll work with the Part
		SOAPMessage msg = mf.createMessage();
		SOAPPart part = msg.getSOAPPart();

		// obtain the SOAPEnvelope and Header/Body
		SOAPEnvelope env = part.getEnvelope();
		SOAPHeader header = env.getHeader();
		SOAPBody body = env.getBody();

		// construct the payload
		SOAPElement operation = body.addChildElement("twoWay", "ns1",
				Constants.WSDL_NAMESPACE);
		SOAPElement value = operation.addChildElement("value");
		value.addTextNode(getName());
		msg.saveChanges();

		// msg.writeTo(System.out);

		try {
			SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
			SOAPConnection sc = scf.createConnection();
			SOAPMessage resp = sc.call(msg, Constants.SOAP12_ENDPOINT_ADDRESS);

			// Extract the response
			SOAPBody rsp_body = resp.getSOAPPart().getEnvelope().getBody();
			SOAPElement op = (SOAPElement) rsp_body.getFirstChild();
			SOAPElement val = (SOAPElement) op.getFirstChild();
			String response = val.getTextContent();

			// validate the response
			assertTrue("Received unexpected response", MESSAGE.equals(response));
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected: " + wse);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			fail(e.toString());
		}
	}

	/**
	 * @testStrategy Test that we are picking up SAAJ 1.3 instead of 1.2 by
	 *               checking for presense of SAAJResult object, which was added
	 *               for JAX-P transformer
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test that we are picking up SAAJ 1.3 instead of 1.2 by checking for presense of SAAJResult object, which was added for JAX-P transformer",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAAJ_v13() {
		final String CLASS1 = "javax.xml.soap.SAAJResult";
		final String CLASS2 = "javax.xml.soap.SAAJMetaFactory";

		// look up class1
		try {
			Class saaj_result = Class.forName(CLASS1);

			assertNotNull("Unexpected...Class.forName(" + CLASS1 + ") = null!",
					saaj_result);
			System.out.println(getName() + ": " + CLASS1 + " found");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail("SAAJ 1.3 API's are not available");
		}

		// lookup class2
		try {
			Class saaj_metafactory = Class.forName(CLASS2);
			assertNotNull("Unexpected...Class.forName(" + CLASS2 + ") = null!",
					saaj_metafactory);
			System.out.println(getName() + ": " + CLASS2 + " found");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail("SAAJ 1.3 API's are not available");
		}
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<SOAPMessage> getDispatch(Service.Mode mode, String endpoint) {

		return getDispatch(mode, endpoint, SOAPBinding.SOAP11HTTP_BINDING);
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<SOAPMessage> getDispatch(Service.Mode mode,
			String endpoint, String binding) {
		// stop the killer thread if it is active
		if (killer != null) killer.abort();
		killer = null;

		Service service = Service.create(Constants.SERVICE_QNAME);
		// service.setExecutor(Executors.newSingleThreadExecutor());

		// create a killer monitor thread that will make
		// sure to kill the executor after a while
		killer = new KillerThread(service, Constants.CLIENT_MAX_SLEEP_SEC);
		killer.start();

		service.addPort(Constants.PORT_QNAME, binding, endpoint);
		javax.xml.ws.Dispatch<SOAPMessage> dispatch = null;
		dispatch = service.createDispatch(Constants.PORT_QNAME,
				SOAPMessage.class, mode);

		assertNotNull("Dispatch not null", dispatch);

		return dispatch;
	}

}
