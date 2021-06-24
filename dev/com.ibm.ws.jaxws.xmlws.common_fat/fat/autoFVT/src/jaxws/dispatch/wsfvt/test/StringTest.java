//
// @(#) 1.3.1.6 autoFVT/src/jaxws/dispatch/wsfvt/test/StringTest.java, WAS.websvcs.fvt, WASX.FVT 3/21/07 11:53:45 [7/11/07 13:15:21]
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
// 05/31/06 sedov    LIDB3296.42        New File
// 08/23/06 sedov    LIDB3296-42.02     Beta Drop
// 01/20/07 sedov    415799             Added FAULT_TEXT constant
//

package jaxws.dispatch.wsfvt.test;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import jaxws.dispatch.wsfvt.common.CallbackHandler;
import jaxws.dispatch.wsfvt.common.Constants;
import jaxws.dispatch.wsfvt.common.KillerThread;

/**
 * Tests for JAX-WS String Dispatches
 */
public class StringTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private KillerThread killer = null;

	public StringTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(StringTest.class);

		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy Test invokeOneWay on a String Dispatch in MESSAGE mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invokeOneWay on a String Dispatch in MESSAGE mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invokeOneWay_Message() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.ONEWAY_MSG.replace("test_message", getName());
		String message = Constants.SOAP11_HEADER + msg
				+ Constants.SOAP11_TRAILER;

		Constants.logRequest(message);
		dispatch.invokeOneWay(message);
	}

	/**
	 * @testStrategy Test invokeOneWay on a String Dispatch in PAYLOAD mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invokeOneWay on a String Dispatch in PAYLOAD mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invokeOneWay_Payload() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String payload = Constants.ONEWAY_MSG
				.replace("test_message", getName());

		Constants.logRequest(payload);
		dispatch.invokeOneWay(payload);
	}

	/**
	 * @testStrategy Test invoke on a String Dispatch in MESSAGE mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invoke on a String Dispatch in MESSAGE mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invoke_Message() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String message = Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;

		Constants.logRequest(message);
		String res = dispatch.invoke(message);
		Constants.testEnvelope(res, true);
		
		assertTrue("Response is null", res != null);
		assertTrue("Invalid Message Returned", res.indexOf(getName()) != -1);
	}

	/**
	 * @testStrategy Test invoke on a String Dispatch in PAYLOAD mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invoke on a String Dispatch in PAYLOAD mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invoke_Payload() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String payload = Constants.TWOWAY_MSG
				.replace("test_message", getName());
		
		Constants.logRequest(payload);
		String res = dispatch.invoke(payload);
		Constants.testEnvelope(res, false);
		
		assertTrue("Response is null", res != null);
		assertTrue("Invalid Message Returned", res.indexOf(getName()) != -1);
	}

	/**
	 * @testStrategy Test invokeAsync (polling) on a String Dispatch in MESSAGE
	 *               mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invokeAsync (polling) on a String Dispatch in MESSAGE mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invokeAsycPoll_Message() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String message = Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;

		// invoke and wait for response
		Constants.logRequest(message);
		Response<String> response = dispatch.invokeAsync(message);
		String res = response.get();

		Constants.testEnvelope(res, true);
		assertTrue("Response is null", res != null);
		assertTrue("Invalid Message Returned", res.indexOf(getName()) != -1);
	}

	/**
	 * @testStrategy Test invokeAsync (polling) on a String Dispatch in PAYLAOD
	 *               mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invokeAsync (polling) on a String Dispatch in PAYLAOD mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invokeAsycPoll_Payload() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String payload = Constants.TWOWAY_MSG.replace("test_message", getName());
		
		// invoke and wait for response
		Constants.logRequest(payload);
		Response<String> response = dispatch.invokeAsync(payload);
		String res = response.get();

		Constants.testEnvelope(res, false);
		assertTrue("Response is null", res != null);
		assertTrue("Invalid Message Returned", res.indexOf(getName()) != -1);
	}

	/**
	 * @testStrategy Test invokeAsync (callback) on a String Dispatch in MESSAGE
	 *               mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invokeAsync (callback) on a String Dispatch in MESSAGE mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invokeAsycCallback_Message() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		CallbackHandler<String> handler = new CallbackHandler<String>();
		
		// compose message
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		String message = Constants.SOAP11_HEADER + msg
				+ Constants.SOAP11_TRAILER;
		
		// invoke and wait for response
		Constants.logRequest(message);
		Future<?> monitor = dispatch.invokeAsync(message, handler);
		handler.waitBlocking(monitor);
		
		try {
			// validate response
			String res = handler.get();
			Constants.testEnvelope(res, true);
			
			assertTrue("Response is null", res != null);
			assertTrue("Invalid Message Returned", res.indexOf(getName()) != -1);
		} catch (WebServiceException e) {
			e.printStackTrace(System.out);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Test invokeAsync (callback) on a String Dispatch in PAYLAOD
	 *               mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invokeAsync (callback) on a String Dispatch in PAYLAOD mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invokeAsycCallback_Payload() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// compose message
		CallbackHandler<String> handler = new CallbackHandler<String>();
		String payload = Constants.TWOWAY_MSG.replace("test_message", getName());
		
		// invoke and wait for response
		Constants.logRequest(payload);
		Future<?> monitor = dispatch.invokeAsync(payload, handler);
		handler.waitBlocking(monitor);
		
		try {
			// valdiate response
			String res = handler.get();
			Constants.testEnvelope(res, false);
			
			assertTrue("Response is null", res != null);
			assertTrue("Invalid Message Returned", res.indexOf(getName()) != -1);
		} catch (WebServiceException e) {
			e.printStackTrace(System.out);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Test sending 1MB message though. An string array is
	 *               dynamically built up until payload size reaches 1MB.
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending 1MB message though. An string array is dynamically built up until payload size reaches 1MB.  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invoke_LargeMessage() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build a large message
		StringBuffer message = new StringBuffer();
		message.append("<ns1:twoWay xmlns:ns1=\""
				+ Constants.WSDL_NAMESPACE + "\">");
		for (int i = 0; i < 30000; i++) {
			message.append("<ns1:value>test_message_" + i + "</ns1:value>");
		}
		message.append("</ns1:twoWay>");

		try {
			dispatch.invoke(message.toString());
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected WebServiceException when sending 1MB+ message: "
					+ wse.getMessage());
		}
	}

	/**
	 * @testStrategy Test receiving exceptions from endpoint. The endpoitn
	 *               throws a SOAPFaultException with a wsdl:fault detail.
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test receiving exceptions from endpoint. The endpoitn throws a SOAPFaultException with a wsdl:fault detail.  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invoke_endpointException() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String message = Constants.TWOWAY_MSG_EXCEPTION.replace("test_message",
				"#service-specific#");

		try {
			dispatch.invoke(message);
			fail("WSE expected when endpoint returns a soap:Fault.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			assertTrue(
					"WSE should contain the exception text.",
					wse.getMessage() != null
							&& wse.getMessage().indexOf(Constants.FAULT_TEXT) != -1);
			
			assertTrue("WSE should be instance of SOAPFaultException",
					wse instanceof SOAPFaultException);
		}
	}

	/**
	 * @testStrategy Test sending empty MESSAGE.
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending empty MESSAGE.  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invoke_empty_Message() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SIMPLE_ENDPOINT_ADDRESS);

		try {
			dispatch.invoke("");

			fail("WebServiceException should be thrown when an empty message is received from Endpoint");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test sending empty PAYLOAD.
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending empty PAYLOAD.  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invoke_empty_Payload() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SIMPLE_ENDPOINT_ADDRESS);

		Map<String, Object> rc = ((BindingProvider)dispatch).getRequestContext();
		rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
		rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, "oneWayEmpty");
		
		dispatch.invoke("");

	}

	/**
	 * @testStrategy Test sending null MESSAGE.
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending null MESSAGE.  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invoke_null_Message() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SIMPLE_ENDPOINT_ADDRESS);

		try {
			dispatch.invoke(null);

			fail("WebServiceException should be thrown when an empty message is received from Endpoint");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test sending null PAYLOAD.
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending null PAYLOAD.  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invoke_null_Payload() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SIMPLE_ENDPOINT_ADDRESS);

		Map<String, Object> rc = ((BindingProvider)dispatch).getRequestContext();
		rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
		rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, "oneWayEmpty");
		
		dispatch.invoke(null);

	}

	/** *************** Binding Tests ****************** */

	/**
	 * @testStrategy Test sending a SOAP1.2 message in MESSAGE
	 * 
	 * WSDL: DispatchSOAP12.wsdl Target: SOAP12DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a SOAP1.2 message in MESSAGE  WSDL: DispatchSOAP12.wsdl Target: SOAP12DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invoke_SOAP12_Message() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_BINDING);
		try {
			String msg = Constants.TWOWAY_MSG
					.replace("test_message", getName());
			String message = Constants.SOAP12_HEADER + msg
					+ Constants.SOAP12_TRAILER;
			
			Constants.logRequest(message);
			String response = dispatch.invoke(message);
			Constants.testEnvelope(response, true);
			
			if (response.indexOf("twoWayResponse") == -1)
				fail("Unexpected response " + response);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("unexpected WebServiceException :" + wse);
		}
	}

	/**
	 * @testStrategy Test sending a SOAP1.2 message in PAYLOAD
	 * 
	 * WSDL: DispatchSOAP12.wsdl Target: SOAP12DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a SOAP1.2 message in PAYLOAD  WSDL: DispatchSOAP12.wsdl Target: SOAP12DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testString_invoke_SOAP12_Payload() throws Exception {
		Dispatch<String> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_BINDING);
		try {

			String payload = Constants.TWOWAY_MSG
					.replace("test_message", getName());
			
			Constants.logRequest(payload);
			String response = dispatch.invoke(payload);
			Constants.testEnvelope(response, false);
			
			if (response.indexOf("twoWayResponse") == -1)
				fail("Unexpected response " + response);
		} catch (WebServiceException wse) {
			fail("unexpected WebServiceException :" + wse);
		}
	}

	/**
	 * @testStrategy Test sending a HTTP message
	 * 
	 * WSDL: DispatchHTTP.wsdl Target: HTTPDispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a HTTP message  WSDL: DispatchHTTP.wsdl Target: HTTPDispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void _testString_invoke_HTTP() throws Exception {

		try {
			Dispatch<String> dispatch = getDispatch(Service.Mode.MESSAGE,
					Constants.HTTP_ENDPOINT_ADDRESS, HTTPBinding.HTTP_BINDING);
			
			String message = Constants.TWOWAY_MSG;
			String response = dispatch.invoke(message);

			if (response.indexOf("twoWayResponse") == -1)
				fail("Unexpected response " + response);
		} catch (WebServiceException wse) {
			assertTrue("", wse.getMessage() != null && wse.getMessage().indexOf("SOAP11HTTP_BINDING") > 0);
		} catch (UnsupportedOperationException uoe){
			uoe.printStackTrace(System.out);
		}
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 *            Service.Mode
	 * @param endpoint
	 *            endpoint address
	 * @return
	 */
	private Dispatch<String> getDispatch(Service.Mode mode, String endpoint) {
		return getDispatch(mode, endpoint, SOAPBinding.SOAP11HTTP_BINDING);
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 *            Service.Mode
	 * @param endpoint
	 *            endpoint address
	 * @param binding
	 *            binding type
	 * @return
	 */
	private Dispatch<String> getDispatch(Service.Mode mode, String endpoint,
			String binding) {

		if (killer != null) killer.abort();

		Service service = Service.create(Constants.SERVICE_QNAME);

		// create a killer monitor thread that will make
		// sure to kill the executor after a while
		killer = new KillerThread(service, Constants.CLIENT_MAX_SLEEP_SEC);
		killer.start();

		service.addPort(Constants.PORT_QNAME, binding, endpoint);
		javax.xml.ws.Dispatch<String> dispatch = null;
		dispatch = service.createDispatch(Constants.PORT_QNAME, String.class,
				mode);

		assertNotNull("Dispatch not null", dispatch);

		return dispatch;
	}
	
}
