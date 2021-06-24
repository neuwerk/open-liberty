//
// @(#) 1.2.1.7 autoFVT/src/jaxws/dispatch/wsfvt/test/JAXBContextTest.java, WAS.websvcs.fvt, WASX.FVT 3/21/07 11:53:41 [7/11/07 13:15:18]
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
// 12/19/06 sedov    409973             Fixed asyncPollMessage
// 01/20/07 sedov    415799             Added FAULT_TEXT constant
//
package jaxws.dispatch.wsfvt.test;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import jaxws.dispatch.wsfvt.common.CallbackHandler;
import jaxws.dispatch.wsfvt.common.Constants;
import jaxws.dispatch.wsfvt.common.KillerThread;
import jaxws.dispatch.wsfvt.common.ObjectFactory;
import jaxws.dispatch.wsfvt.common.StringArray;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * JaxRpcTests for JAXB Context based Dispatch. JAXBContext messages can be sent in
 * both Message and Payload mode, all 3 bindings are supported
 */
public class JAXBContextTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	// monitor used for interrupting hung async invocations
	private KillerThread killer = null;

	public JAXBContextTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(JAXBContextTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy Test for invokeOneWay in JAXBContext/PAYLOAD/SOAP11
	 *               dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invokeOneWay_Payload() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build request object
		String msg = Constants.ONEWAY_MSG.replace("test_message", getName());
		Object payload = Constants.toJAXBObject(msg);

		// invoke
		Constants.logRequest(msg);
		dispatch.invokeOneWay(payload);
	}

	/**
	 * @testStrategy Test for invokeOneWay in JAXBContext/MESSAGE/SOAP11
	 *               dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invokeOneWay_Message() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build request object
		String msg = Constants.SOAP11_HEADER
				+ Constants.ONEWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;
		Object message = Constants.toJAXBObject(msg);

		// invoke
		Constants.logRequest(msg);
		dispatch.invokeOneWay(message);
	}

	/**
	 * @testStrategy Test for invoke in JAXBContext/PAYLOAD dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invoke_Payload() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build request object
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Object payload = Constants.toJAXBObject(msg);

		// invoke
		Constants.logRequest(msg);
		Object response = dispatch.invoke(payload);

		// validate response
		String res = Constants.toString(response);
		Constants.testEnvelope(res, false);
		assertNotNull("Response is null", response);
		assertTrue("unexpected response", res.indexOf(getName()) != -1);
	}

	/**
	 * @testStrategy Test for invoke in JAXBContext/MESSAGE dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invoke_Message() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build request object
		String msg = Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;
		Object message = Constants.toJAXBObject(msg);

		// invoke
		Constants.logRequest(msg);
		Object response = dispatch.invoke(message);

		// validate response
		String res = Constants.toString(response);
		Constants.testEnvelope(res, true);
		assertNotNull("Response is null", response);
		assertTrue("unexpected response", res.indexOf(getName()) != -1);
	}

	/**
	 * @testStrategy Test for invokeAsync-polling in JAXBContext dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invokeAsycPoll_Payload() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build request object
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Object payload = Constants.toJAXBObject(msg);

		// invoke & block until response is available
		Constants.logRequest(msg);
		Future<?> monitor = dispatch.invokeAsync(payload);
		Object response = monitor.get();

		// validate response
		String res = Constants.toString(response);
		Constants.testEnvelope(res, false);

		assertNotNull("Response is null", response);
		assertTrue("unexpected response", res.indexOf(getName()) != -1);
	}

	/**
	 * @testStrategy Test for invokeAsync-polling in JAXBContext/MESSAGE
	 *               dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invokeAsycPoll_Message() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build request object
		String msg = Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;
		Object message = Constants.toJAXBObject(msg);

		// invoke & block until response is available
		Constants.logRequest(msg);
		Future<?> monitor = dispatch.invokeAsync(message);
		Object response = monitor.get();

		// validate response
		String res = Constants.toString(response);
		Constants.testEnvelope(res, true);

		assertNotNull("Response is null", response);
		assertTrue("unexpected response", res.indexOf(getName()) != -1);
	}

	/**
	 * @testStrategy Test for invokeAsync-callback in JAXBContext/PAYLOAD
	 *               dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invokeAsycCallback_Payload() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		CallbackHandler<Object> handler = new CallbackHandler<Object>();

		// build request object
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Object message = Constants.toJAXBObject(msg);

		// invoke & block until response is available
		Constants.logRequest(msg);
		Future<?> monitor = dispatch.invokeAsync(message, handler);
		handler.waitBlocking(monitor);
		Object response = handler.get();

		// validate response
		String res = Constants.toString(response);
		Constants.testEnvelope(res, false);
		assertNotNull("Response is null", response);
		assertTrue("unexpected response", res.indexOf(getName()) != -1);
	}

	/**
	 * @testStrategy Test for invokeAsync-callback in JAXBContext/MESSAGE
	 *               dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invokeAsycCallback_Message() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		CallbackHandler<Object> handler = new CallbackHandler<Object>();
		// build request object
		String msg = Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;
		Object message = Constants.toJAXBObject(msg);

		// invoke & block until response is available
		Constants.logRequest(msg);
		Future<?> monitor = dispatch.invokeAsync(message, handler);
		handler.waitBlocking(monitor);
		Object response = handler.get();

		// validate response
		String res = Constants.toString(response);
		Constants.testEnvelope(res, true);
		assertNotNull("Response is null", response);
		assertTrue("unexpected response", res.indexOf(getName()) != -1);
	}

	/**
	 * @testStrategy Test for sending a large message through using a
	 *               JAXBContext Dispatch. A 1MB payload is dynamically
	 *               constructed and then passed through
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invoke_largeMessage() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build a large message
		// build request object
		ObjectFactory fac = new ObjectFactory();
		StringArray sa = fac.createStringArray();
		JAXBElement<StringArray> twoWay = fac.createTwoWay(sa);

		// build array elements
		for (int i = 0; i < 30000; i++) {
			sa.getValue().add("test_message_" + i);
		}

		// invoke and see what happens
		Object response = dispatch.invoke(twoWay);

		// validate response
		String res = Constants.toString(response);
		assertNotNull("Response is null", response);
		assertTrue("unexpected response", res.indexOf("test_message_0") != -1);
	}

	/**
	 * @testStrategy Test for getting a marshalling exception
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invoke_marshallException() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		try {
			// invoke with an unsupported type
			dispatch.invoke(new Exception());
			fail("WebServiceException expected when marshalling errors occur.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);

			// ensure that JABException is thrown
			assertTrue("WSE.getCause must be JAXBException (jax-ws 4.21)", wse
					.getCause() instanceof JAXBException);
		}
	}

	/**
	 * @testStrategy Test for receiving an exception in JAXBContext mode
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invoke_endpointException() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build request object
		ObjectFactory fac = new ObjectFactory();
		JAXBElement<String> twoWay = fac
				.createTwoWayException("#service-specific#");

		try {
			dispatch.invoke(twoWay);
			fail("WebServiceException expected when endpoint returns a generic soap:Fault.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);

			assertTrue("WSE should contain the exception text.", wse
					.getMessage().indexOf(Constants.FAULT_TEXT) != -1);

			assertTrue("WSE should be instance of SOAPFaultException",
					wse instanceof SOAPFaultException);
		}
	}

	/**
	 * @testStrategy Test for invoke(null) in MESSAGE mode on JAXBContext
	 *               Dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invoke_null_Message() throws Exception {
		javax.xml.ws.Dispatch<Object> dispatch = getDispatch(
				Service.Mode.MESSAGE, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			dispatch.invoke(null);

			fail("WebServiceException should be thrown when a null message is sent");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test for invoke(null) in PAYLAOD mode on JAXBContext
	 *               Dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invoke_null_Payload() throws Exception {
		javax.xml.ws.Dispatch<Object> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			dispatch.invoke(null);
			// 486125 null payload is ok
			//fail("WebServiceException should be thrown when a null message is sent");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/** ****************** Binding test cases ************************** */

	/**
	 * @testStrategy Test for sending a message over SOAP1.2 Binding using
	 *               JAXBContext Dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invoke_SOAP12_Payload() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_BINDING);

		// build request object
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Object payload = Constants.toJAXBObject(msg);

		Constants.logRequest(msg);
		Object response = dispatch.invoke(payload);

		// validate response
		String res = Constants.toString(response);
		Constants.testEnvelope(res, false);
		assertNotNull("Response is null", response);
		assertTrue("unexpected response", res.indexOf(getName()) != -1);

	}

	/**
	 * @testStrategy Test for sending a message over SOAP1.2 Binding using
	 *               JAXBContext Dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBContext_invoke_SOAP12_Message() throws Exception {
		Dispatch<Object> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_BINDING);

		// build request object
		String msg = Constants.SOAP12_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP12_TRAILER;
		Object message = Constants.toJAXBObject(msg);

		// invoke
		Constants.logRequest(msg);
		Object response = dispatch.invoke(message);

		// validate response
		String res = Constants.toString(response);
		Constants.testEnvelope(res, true);
		assertNotNull("Response is null", response);
		assertTrue("unexpected response", res.indexOf(getName()) != -1);
	}

	/**
	 * @testStrategy Test for sending a message over HTTP Binding using
	 *               JAXBContext Dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 */

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void _testJAXBContext_invoke_HTTP() throws Exception {
		try {
			Dispatch<Object> dispatch = getDispatch(Service.Mode.PAYLOAD,
					Constants.HTTP_ENDPOINT_ADDRESS, HTTPBinding.HTTP_BINDING);
			// build request object
			String msg = Constants.TWOWAY_MSG
					.replace("test_message", getName());
			Object request = Constants.toJAXBObject(msg);

			Constants.logRequest(msg);
			Object response = dispatch.invoke(request);

			// validate response
			String res = Constants.toString(response);
			assertNotNull("Response is null", response);
			assertTrue("unexpected response", res.indexOf(getName()) != -1);
		} catch (UnsupportedOperationException uoe) {
			uoe.printStackTrace(System.out);
		} catch (WebServiceException wse) {
			assertTrue("", wse.getMessage() != null
					&& wse.getMessage().indexOf("SOAP11HTTP_BINDING") > 0);
		}
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<Object> getDispatch(Service.Mode mode, String endpoint)
			throws Exception {

		return getDispatch(mode, endpoint, SOAPBinding.SOAP11HTTP_BINDING);
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<Object> getDispatch(Service.Mode mode, String endpoint,
			String binding) throws Exception {
		// stop the killer thread if it is active
		if (killer != null) killer.abort();
		killer = null;

		Service service = Service.create(Constants.SERVICE_QNAME);
		service.setExecutor(Executors.newSingleThreadExecutor());

		// create a killer monitor thread that will make
		// sure to kill the executor after a while
		killer = new KillerThread(service, Constants.CLIENT_MAX_SLEEP_SEC);
		killer.start();

		service.addPort(Constants.PORT_QNAME, binding, endpoint);
		Dispatch<Object> dispatch = null;
		JAXBContext jaxbContext = null;

		// add support for MESSAGE mode requests by adding SOAP1.1 Schema to
		// the JAXBContext. When HTTP Binding is used SOAP envelope makes no
		// sense
		if (mode == Service.Mode.MESSAGE && binding != HTTPBinding.HTTP_BINDING) {
			if (binding == SOAPBinding.SOAP11HTTP_BINDING) {
				// SOAP 1.1 Binding
				jaxbContext = Constants.getJAXBContextSoap11();
			} else {
				// SOAP 1.2 Binding
				jaxbContext = Constants.getJAXBContextSoap12();
			}
		} else {
			// HTTP or PAYLOAD mode
			jaxbContext = Constants.getJAXBContextPayload();
		}

		dispatch = service.createDispatch(Constants.PORT_QNAME, jaxbContext,
				mode);
		assertNotNull("Dispatch not null", dispatch);

		return dispatch;
	}
}
