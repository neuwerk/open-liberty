//
// @(#) 1.1.1.7 autoFVT/src/jaxws/dispatch/wsfvt/test/StreamSourceTest.java, WAS.websvcs.fvt, WASX.FVT 3/21/07 11:53:43 [7/11/07 13:15:20]
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
// 08/23/06 sedov    LIDB3296-42.02     Beta Drop
// 01/09/07 sedov    413290             Added MTOM binding tests
// 01/20/07 sedov    415799             Added FAULT_TEXT constant
//

package jaxws.dispatch.wsfvt.test;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
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
 * Tests for Source Dispatch Source messages can be sent in both MESSAGE and
 * PAYLOAD mode
 */
public class StreamSourceTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	// monitor used for terminating hanging async calls
	private KillerThread killer = null;

	public StreamSourceTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(StreamSourceTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy Test invokeOneWay on a StreamSource Dispatch in MESSAGE
	 *               mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invokeOneWay on a StreamSource Dispatch in MESSAGE mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invokeOneWay_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// compose message
		String msg = Constants.SOAP11_HEADER
				+ Constants.ONEWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;
		Source message = Constants.toStreamSource(msg);

		// invoke
		Constants.logRequest(msg);
		dispatch.invokeOneWay(message);
	}

	/**
	 * @testStrategy Test invokeOneWay on a StreamSource Dispatch in PAYLOAD
	 *               mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invokeOneWay on a StreamSource Dispatch in PAYLOAD mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invokeOneWay_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// compose message
		String msg = Constants.ONEWAY_MSG.replace("test_message", getName());

		Constants.logRequest(msg);
		Source payload = Constants.toStreamSource(msg);

		// invoke
		dispatch.invokeOneWay(payload);
	}

	/**
	 * @testStrategy Test invoke on a StreamSource Dispatch in MESSAGE mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invoke on a StreamSource Dispatch in MESSAGE mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_Message() throws Exception {

		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// compose message
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Source message = Constants.toStreamSource(Constants.SOAP11_HEADER + msg
				+ Constants.SOAP11_TRAILER);

		// invoke
		Source res = dispatch.invoke(message);

		// validate response
		assertTrue("Response is null", res != null);
		assertTrue("Unexpected Response", Constants.toString(res).indexOf(
				getName()) != -1);
	}

	/**
	 * @testStrategy Test invoke on a StreamSource Dispatch in PAYLOAD mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invoke on a StreamSource Dispatch in PAYLOAD mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// compose
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Source payload = Constants.toStreamSource(msg);

		// invoke
		Source res = dispatch.invoke(payload);

		// validate response
		assertTrue("Response is null", res != null);
		assertTrue("Unexpected Response", Constants.toString(res).indexOf(
				getName()) != -1);
	}

	/**
	 * @testStrategy Test invokeAsync (polling) on a StreamSource Dispatch in
	 *               MESSAGE mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invokeAsync (polling) on a StreamSource Dispatch in MESSAGE mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invokeAsycPoll_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// create message
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Source message = Constants.toStreamSource(Constants.SOAP11_HEADER + msg
				+ Constants.SOAP11_TRAILER);

		// invoke and wait for response
		Response<Source> response = dispatch.invokeAsync(message);
		Source res = response.get();

		// validate response
		assertTrue("Response is null", res != null);
		assertTrue("Unexpected Response", Constants.toString(res).indexOf(
				getName()) != -1);
	}

	/**
	 * @testStrategy Test invokeAsync (polling) on a StreamSource Dispatch in
	 *               PAYLAOD mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invokeAsync (polling) on a StreamSource Dispatch in PAYLAOD mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invokeAsycPoll_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// compose message
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Source payload = Constants.toStreamSource(msg);

		// invoke and wait for response
		Response<Source> response = dispatch.invokeAsync(payload);
		Source res = response.get();

		// validate response
		assertTrue("Response is null", res != null);
		assertTrue("Unexpected Response", Constants.toString(res).indexOf(
				getName()) != -1);
	}

	/**
	 * @testStrategy Test invokeAsync (callback) on a StreamSource Dispatch in
	 *               MESSAGE mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invokeAsync (callback) on a StreamSource Dispatch in MESSAGE mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invokeAsycCallback_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		CallbackHandler<Source> handler = new CallbackHandler<Source>();

		// compose message
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Source message = Constants.toStreamSource(Constants.SOAP11_HEADER + msg
				+ Constants.SOAP11_TRAILER);

		// invoke and wait for response to come back
		Future<?> monitor = dispatch.invokeAsync(message, handler);
		handler.waitBlocking(monitor);

		try {
			// receive and validate response
			Source res = handler.get();

			assertTrue("Response is null", res != null);
			assertTrue("Unexpected Response", Constants.toString(res).indexOf(
					getName()) != -1);

		} catch (WebServiceException e) {
			e.printStackTrace(System.out);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Test invokeAsync (callback) on a StreamSource Dispatch in
	 *               PAYLAOD mode
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test invokeAsync (callback) on a StreamSource Dispatch in PAYLAOD mode  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invokeAsycCallback_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		CallbackHandler<Source> handler = new CallbackHandler<Source>();
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Source payload = Constants.toStreamSource(msg);

		// invoke and wait for response to come back
		Future<?> monitor = dispatch.invokeAsync(payload, handler);
		handler.waitBlocking(monitor);

		try {
			// receive and validate response
			Source res = handler.get();

			assertTrue("Response is null", res != null);
			assertTrue("Unexpected Response", Constants.toString(res).indexOf(
					getName()) != -1);

		} catch (WebServiceException e) {
			e.printStackTrace(System.out);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Test sending a large payload (1MB) on StreamSource Dispatch
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a large payload (1MB) on StreamSource Dispatch  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_largeMessage() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build a large message
		StringBuffer message = new StringBuffer();
		message.append("<ns1:twoWay xmlns:ns1=\"" + Constants.WSDL_NAMESPACE
				+ "\">");
		for (int i = 0; i < 30000; i++) {
			message.append("<ns1:value>test_message_" + i + "</ns1:value>");
		}
		message.append("</ns1:twoWay>");

		// invoke and see what happens
		dispatch.invoke(Constants.toStreamSource(message.toString()));

	}

	/**
	 * @testStrategy Test sreceiving a WebServiceException as a result of
	 *               endpoint throwing it on StreamSource Dispatch
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sreceiving a WebServiceException as a result of endpoint throwing it on StreamSource Dispatch  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_endpointException() throws Exception {

		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String message = Constants.TWOWAY_MSG_EXCEPTION;

		try {
			dispatch.invoke(Constants.toStreamSource(message));
			fail("WebServiceException expected when endpoint returns a soap:Fault.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			assertTrue(
					"WebServiceException should contain the exception text.",
					wse.getMessage().indexOf(Constants.FAULT_TEXT) != -1);
		}
	}

	/**
	 * @testStrategy Test sending a null message on StreamSource Dispatch in
	 *               MESSAGE mdoe
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a null message on StreamSource Dispatch in MESSAGE mdoe  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_empty_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			dispatch.invoke(new StreamSource());

			fail("WebServiceException should be thrown when an empty message is sent (MESSAGE mode)");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test sending an empty object message on StreamSource
	 *               Dispatch in PAYLOAD mdoe
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending an empty object message on StreamSource Dispatch in PAYLOAD mdoe  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_empty_Payload() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		// try {
		dispatch.invoke(new StreamSource());

		// fail("WebServiceException should be thrown when an empty message is
		// sent (PAYLOAD mode)");
		// } catch (WebServiceException wse) {
		// System.out.println(getName() + ": " + wse);
		// }
	}

	/**
	 * @testStrategy Test sending a null message on StreamSource Dispatch in
	 *               MESSAGE mdoe
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a null message on StreamSource Dispatch in MESSAGE mdoe  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_null_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			Map<String, Object> rc = ((BindingProvider) dispatch)
					.getRequestContext();
			rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
			rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, "empty");

			dispatch.invoke(null);

			// fail("WebServiceException should be thrown when a null message is
			// sent (MESSAGE mode)");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test sending a null message on StreamSource Dispatch in
	 *               PAYLOAD mdoe
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a null message on StreamSource Dispatch in PAYLOAD mdoe  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_null_Payload() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);

		Map<String, Object> rc = ((BindingProvider) dispatch)
				.getRequestContext();
		rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
		rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, "empty");

		dispatch.invoke(null);

	}

	/** *********************** Binding tests ********************** */

	/**
	 * @testStrategy Test sending a SOAP1.2 message on StreamSource Dispatch in
	 *               MESSAGE mdoe
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a SOAP1.2 message on StreamSource Dispatch in MESSAGE mdoe  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_SOAP11_MTOM_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP11HTTP_MTOM_BINDING);
		try {
			String msg = Constants.TWOWAY_MSG
					.replace("test_message", getName());
			Source message = Constants.toStreamSource(Constants.SOAP11_HEADER
					+ msg + Constants.SOAP11_TRAILER);

			// invoke
			Source resp = dispatch.invoke(message);

			// validate response
			String response = Constants.toString(resp);

			if (response.indexOf(getName()) == -1)
				fail("Unexpected response " + response);

		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("unexpected WebServiceException :" + wse);
		}
	}

	/**
	 * @testStrategy Test sending a SOAP1.2 message on StreamSource Dispatch in
	 *               PAYLOAD mdoe
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a SOAP1.2 message on StreamSource Dispatch in PAYLOAD mdoe  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_SOAP11_MTOM_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP11HTTP_MTOM_BINDING);
		try {
			String msg = Constants.TWOWAY_MSG
					.replace("test_message", getName());
			Source payload = Constants.toStreamSource(msg);

			// invoke
			Source resp = dispatch.invoke(payload);

			// validate response
			String response = Constants.toString(resp);

			if (response.indexOf(getName()) == -1)
				fail("Unexpected response " + response);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("unexpected WebServiceException :" + wse);
		}
	}

	/**
	 * @testStrategy Test sending a SOAP1.2 message on StreamSource Dispatch in
	 *               MESSAGE mdoe
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a SOAP1.2 message on StreamSource Dispatch in MESSAGE mdoe  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_SOAP12_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_BINDING);
		try {
			String msg = Constants.TWOWAY_MSG
					.replace("test_message", getName());
			Source message = Constants.toStreamSource(Constants.SOAP12_HEADER
					+ msg + Constants.SOAP12_TRAILER);

			// invoke
			Source resp = dispatch.invoke(message);

			// validate response
			String response = Constants.toString(resp);

			if (response.indexOf(getName()) == -1)
				fail("Unexpected response " + response);

		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("unexpected WebServiceException :" + wse);
		}
	}

	/**
	 * @testStrategy Test sending a SOAP1.2 message on StreamSource Dispatch in
	 *               PAYLOAD mdoe
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a SOAP1.2 message on StreamSource Dispatch in PAYLOAD mdoe  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_SOAP12_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_BINDING);
		try {
			String msg = Constants.TWOWAY_MSG
					.replace("test_message", getName());
			Source payload = Constants.toStreamSource(msg);

			// invoke
			Source resp = dispatch.invoke(payload);

			// validate response
			String response = Constants.toString(resp);

			if (response.indexOf(getName()) == -1)
				fail("Unexpected response " + response);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("unexpected WebServiceException :" + wse);
		}
	}

	/**
	 * @testStrategy Test sending a SOAP1.2 message on StreamSource Dispatch in
	 *               MESSAGE mdoe
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a SOAP1.2 message on StreamSource Dispatch in MESSAGE mdoe  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_SOAP12_MTOM_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_MTOM_BINDING);
		try {
			String msg = Constants.TWOWAY_MSG
					.replace("test_message", getName());
			Source message = Constants.toStreamSource(Constants.SOAP12_HEADER
					+ msg + Constants.SOAP12_TRAILER);

			// invoke
			Source resp = dispatch.invoke(message);

			// validate response
			String response = Constants.toString(resp);

			if (response.indexOf(getName()) == -1)
				fail("Unexpected response " + response);

		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("unexpected WebServiceException :" + wse);
		}
	}

	/**
	 * @testStrategy Test sending a SOAP1.2 message on StreamSource Dispatch in
	 *               PAYLOAD mdoe
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a SOAP1.2 message on StreamSource Dispatch in PAYLOAD mdoe  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_invoke_SOAP12_MTOM_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_MTOM_BINDING);
		try {
			String msg = Constants.TWOWAY_MSG
					.replace("test_message", getName());
			Source payload = Constants.toStreamSource(msg);

			// invoke
			Source resp = dispatch.invoke(payload);

			// validate response
			String response = Constants.toString(resp);

			if (response.indexOf(getName()) == -1)
				fail("Unexpected response " + response);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("unexpected WebServiceException :" + wse);
		}
	}

	/**
	 * @testStrategy Test sending a HTTP message on StreamSource Dispatch
	 * 
	 * WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test sending a HTTP message on StreamSource Dispatch  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void _testStreamSource_invoke_HTTP() throws Exception {

		try {
			Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
					Constants.HTTP_ENDPOINT_ADDRESS, HTTPBinding.HTTP_BINDING);

			String msg = Constants.TWOWAY_MSG
					.replace("test_message", getName());
			Source resp = dispatch.invoke(Constants.toStreamSource(msg));
			String response = Constants.toString(resp);

			if (response.indexOf(getName()) == -1)
				fail("Unexpected response " + response);
		} catch (WebServiceException wse) {
			assertTrue("", wse.getMessage() != null
					&& wse.getMessage().indexOf("SOAP11HTTP_BINDING") > 0);
		} catch (UnsupportedOperationException uoe) {
			// may be descoped in WSFP, so catch it and log it...but ignore it
			uoe.printStackTrace(System.out);
		}
	}

	/**
	 * @testStrategy Test for creating a dispatch explicitly typed with
	 *               SAXSource
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_explicitlyTyped() throws Exception {

		if (Constants.ENABLE_RETURNTYPE_CHECKING) {

			// create a dispatch explicitly typed with DOMSource
			Service service = Service.create(Constants.SERVICE_QNAME);
			service.addPort(Constants.PORT_QNAME,
					SOAPBinding.SOAP11HTTP_BINDING,
					Constants.SOAP11_ENDPOINT_ADDRESS);
			Dispatch<StreamSource> dispatch = null;
			dispatch = service.createDispatch(Constants.PORT_QNAME,
					StreamSource.class, Service.Mode.PAYLOAD);
			assertNotNull("Dispatch not null", dispatch);

			String msg = Constants.TWOWAY_MSG
					.replace("test_message", getName());

			// only perform this test if type checking is enabled
			StreamSource response = dispatch.invoke(Constants
					.toStreamSource(msg));

			// validate response
			String res = Constants.toString(response);
			assertTrue("Invalid Message Returned", res.indexOf(getName()) != -1);
		}
	}

	/**
	 * @testStrategy Create a StreamSource by providing an InputSource via
	 *               setInputSource
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Create a StreamSource by providing an InputSource via setInputSource",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testStreamSource_InputStream() throws Exception {
		StreamSource ss = new StreamSource();
		ss.setInputStream(new ByteArrayInputStream(Constants.TWOWAY_MSG
				.getBytes()));

		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		Source reply = dispatch.invoke(ss);

		// print a logging statement about what has been returned
		if (reply instanceof SAXSource) {
			System.out.println(getName() + " SAXSource.getXMLReader="
					+ ((SAXSource) reply).getXMLReader());
			System.out.println(getName() + " SAXSource.getInputSource="
					+ ((SAXSource) reply).getInputSource());
		} else {
			System.out.println(getName() + " Returned a "
					+ reply.getClass().getName());
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
	private Dispatch<Source> getDispatch(Service.Mode mode, String endpoint) {
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
	private Dispatch<Source> getDispatch(Service.Mode mode, String endpoint,
			String binding) {
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
		javax.xml.ws.Dispatch<Source> dispatch = null;
		dispatch = service.createDispatch(Constants.PORT_QNAME, Source.class,
				mode);

		assertNotNull("Dispatch not null", dispatch);

		return dispatch;
	}

}
