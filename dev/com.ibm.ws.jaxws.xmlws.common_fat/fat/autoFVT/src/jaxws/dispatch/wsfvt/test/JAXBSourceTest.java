//
// @(#) 1.1.1.5 autoFVT/src/jaxws/dispatch/wsfvt/test/JAXBSourceTest.java, WAS.websvcs.fvt, WASX.FVT 1/22/07 11:46:24 [7/11/07 13:15:18]
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

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import jaxws.dispatch.wsfvt.common.CallbackHandler;
import jaxws.dispatch.wsfvt.common.Constants;
import jaxws.dispatch.wsfvt.common.KillerThread;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for JAX-WS Dispatch in JAXBSource mode. This is an alternative to
 * JAXBContext dispatch
 */
public class JAXBSourceTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private static final String MSG_TYPE_MISMATCH = "When using JAXBSource objects, returned types must be SAXSource objects.";

	// monitor used for interrupting hung async invocations
	private KillerThread killer = null;

	public JAXBSourceTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(JAXBSourceTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy Test for invokeOneWay in PAYLOAD mode over JAXBSource
	 *               dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBSource_invokeOneWay_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.ONEWAY_MSG;

		// create request
		Constants.logRequest(msg);
		JAXBSource payload = Constants.toJAXBSource(msg);

		// invoke
		dispatch.invokeOneWay(payload);
	}

	/**
	 * @testStrategy Test for invokeOneWay in MESSAGE mode over JAXBSource
	 *               dispatch. Message mode is implemented by compiling the
	 *               SOAP11-envelope schema and adding it to the JAXBContext
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBSource_invokeOneWay_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// create request
		String msg = Constants.SOAP11_HEADER + Constants.ONEWAY_MSG
				+ Constants.SOAP11_TRAILER;
		JAXBSource message = Constants.toJAXBSource(msg);

		// invoke
		Constants.logRequest(msg);
		dispatch.invokeOneWay(message);
	}

	/**
	 * @testStrategy Test for invoke in PAYLOAD mode over JAXBSource dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBSource_invoke_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.TWOWAY_MSG;
		JAXBSource payload = Constants.toJAXBSource(msg);

		// invoke
		Constants.logRequest(msg);
		Source src = dispatch.invoke(payload);

		// validate response
		String res = Constants.toString(src);
		
		Constants.testEnvelope(res, false);
		assertTrue("Invalid Message Returned",
				res.indexOf("test_message") != -1);

		// if return type checking is enabled, then make sure the
		// response was a SAXSource
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			assertTrue(MSG_TYPE_MISMATCH, src instanceof SAXSource);
		}
	}

	/**
	 * @testStrategy Test for invoke in MESSAGE mode over JAXBSource dispatch.
	 *               Message mode is implemented by compiling the
	 *               SOAP11-envelope schema and adding it to the JAXBContext
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBSource_invoke_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.SOAP11_HEADER + Constants.TWOWAY_MSG
				+ Constants.SOAP11_TRAILER;
		JAXBSource message = Constants.toJAXBSource(msg);

		// invoke
		Constants.logRequest(msg);
		Source src = dispatch.invoke(message);

		// validate response
		String res = Constants.toString(src);
		
		Constants.testEnvelope(res, true);
		assertTrue("Invalid Message Returned",
				res.indexOf("test_message") != -1);

		// if return type checking is enabled, then make sure the
		// response was a SAXSource
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			assertTrue(MSG_TYPE_MISMATCH, src instanceof SAXSource);
		}
	}

	/**
	 * @testStrategy Test for invokeAsync-polling in PAYLOAD mode over
	 *               JAXBSource dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBSource_invokeAsycPoll_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.TWOWAY_MSG;
		JAXBSource payload = Constants.toJAXBSource(msg);

		// invoke
		Constants.logRequest(msg);
		Response<Source> response = dispatch.invokeAsync(payload);

		// block until response comes back
		Source src = response.get();
		String res = Constants.toString(src);

		// validate response
		Constants.testEnvelope(res, false);
		assertTrue("Invalid Message Returned",
				res.indexOf("test_message") != -1);

		// if return type checking is enabled, then make sure the
		// response was a SAXSource
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			assertTrue(MSG_TYPE_MISMATCH, src instanceof SAXSource);
		}
	}

	/**
	 * @testStrategy Test for invokeAsync-polling in MESSAGE mode over
	 *               JAXBSource dispatch. Message mode is implemented by
	 *               compiling the SOAP11-envelope schema and adding it to the
	 *               JAXBContext
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBSource_invokeAsycPoll_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// icompose message
		String msg = Constants.SOAP11_HEADER + Constants.TWOWAY_MSG
				+ Constants.SOAP11_TRAILER;
		JAXBSource message = Constants.toJAXBSource(msg);

		// block until response comes back
		Constants.logRequest(msg);
		Response<Source> response = dispatch.invokeAsync(message);
		Source src = response.get();

		// validate response
		String res = Constants.toString(src);
		
		Constants.testEnvelope(res, true);
		assertTrue("Invalid Message Returned",
				res.indexOf("test_message") != -1);

		// if return type checking is enabled, then make sure the
		// response was a SAXSource
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			assertTrue(MSG_TYPE_MISMATCH, src instanceof SAXSource);
		}
	}

	/**
	 * @testStrategy Test for invokeAsync-callback in PAYLOAD mode over
	 *               JAXBSource dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBSource_invokeAsycCallback_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// invoke
		CallbackHandler<Source> handler = new CallbackHandler<Source>();
		String msg = Constants.TWOWAY_MSG;
		JAXBSource payload = Constants.toJAXBSource(msg);

		// invoke and wait for response
		Constants.logRequest(msg);
		Future<?> monitor = dispatch.invokeAsync(payload, handler);
		handler.waitBlocking(monitor);

		try {
			// validate response
			Source src = handler.get();
			String res = Constants.toString(src);
			
			Constants.testEnvelope(res, false);
			assertTrue("Invalid Message Returned",
					res.indexOf("test_message") != -1);

			// if return type checking is enabled, then make sure the
			// response was a SAXSource
			if (Constants.ENABLE_RETURNTYPE_CHECKING) {
				assertTrue(MSG_TYPE_MISMATCH, src instanceof SAXSource);
			}
		} catch (Exception e) {
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Test for invokeAsync-callback in MESSAGE mode over
	 *               JAXBSource dispatch. Message mode is implemented by
	 *               compiling the SOAP11-envelope schema and adding it to the
	 *               JAXBContext
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBSource_invokeAsycCallback_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// invoke
		CallbackHandler<Source> handler = new CallbackHandler<Source>();
		String msg = Constants.SOAP11_HEADER + Constants.TWOWAY_MSG
				+ Constants.SOAP11_TRAILER;
		JAXBSource message = Constants.toJAXBSource(msg);

		// invoke and wait for response
		Constants.logRequest(msg);
		Future<?> monitor = dispatch.invokeAsync(message, handler);

		handler.waitBlocking(monitor);

		try {
			// validate response
			Source src = handler.get();
			String res = Constants.toString(src);
			
			Constants.testEnvelope(res, true);
			assertTrue("Invalid Message Returned",
					res.indexOf("test_message") != -1);

			// if return type checking is enabled, then make sure the
			// response was a SAXSource
			if (Constants.ENABLE_RETURNTYPE_CHECKING) {
				assertTrue(MSG_TYPE_MISMATCH, src instanceof SAXSource);
			}
		} catch (Exception e) {
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Test for parser issues with sending a 1MB payload over
	 *               JAXBSource dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBSource_invoke_largeMessage() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build a large message
		StringBuffer message = new StringBuffer();
		message.append("<ns1:twoWay xmlns:ns1=\"");
		message.append(Constants.WSDL_NAMESPACE);
		message.append("\">");

		for (int i = 0; i < 30000; i++) {
			message.append("<value>test_message_");
			message.append(i);
			message.append("</value>");
		}
		message.append("</ns1:twoWay>");

		// invoke and see what happens
		JAXBSource request = Constants.toJAXBSource(message.toString());
		dispatch.invoke(request);
	}

	/**
	 * @testStrategy Test for receiving of WebServiceException when endpoint
	 *               throws it in JAXBSource Dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBSource_invoke_endpointException() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String message = Constants.TWOWAY_MSG_EXCEPTION.replace("test_message",
				"#service-specific#");

		try {
			dispatch.invoke(Constants.toJAXBSource(message));
			fail("WebServiceException expected when endpoint returns a soap:Fault.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);

			assertTrue("WSE should contain the exception text.", wse
					.getMessage() != null
					&& wse.getMessage().indexOf(Constants.FAULT_TEXT) != -1);

			assertTrue("Exception must be a SOAPFaultException",
					wse instanceof SOAPFaultException);
		}
	}

	/**
	 * @testStrategy Test for creating a dispatch explicitly typed as SAXSource.
	 *               Since JAXBSource is not directly supported ithis is tested
	 *               by typing a dispatch as SAXSource
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJAXBSource_explicitlyTyped() throws Exception {

		// invoke only if return type checking has been enabled
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {

			// create a JAXBSource/PAYLOAD Dispatch
			Service service = Service.create(Constants.SERVICE_QNAME);
			service.addPort(Constants.PORT_QNAME,
					SOAPBinding.SOAP11HTTP_BINDING,
					Constants.SOAP11_ENDPOINT_ADDRESS);
			javax.xml.ws.Dispatch<SAXSource> dispatch = null;
			dispatch = service.createDispatch(Constants.PORT_QNAME,
					SAXSource.class, Service.Mode.PAYLOAD);
			assertNotNull("Dispatch is null", dispatch);

			// create request message
			JAXBSource request = Constants.toJAXBSource(Constants.TWOWAY_MSG);

			SAXSource reply = dispatch.invoke(request);

			// validate response
			String res = Constants.toString(reply);

			assertNotNull("Response is null", reply);
			assertTrue("Invalid Message Returned",
					res.indexOf("test_message") != -1);
		}
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<Source> getDispatch(Service.Mode mode, String endpoint) {
		// stop the killer thread if it is active
		if (killer != null) killer.abort();
		killer = null;

		Service service = Service.create(Constants.SERVICE_QNAME);
		service.setExecutor(Executors.newSingleThreadExecutor());

		// create a killer monitor thread that will make
		// sure to kill the executor after a while
		killer = new KillerThread(service, Constants.CLIENT_MAX_SLEEP_SEC);
		killer.start();

		service.addPort(Constants.PORT_QNAME, SOAPBinding.SOAP11HTTP_BINDING,
				endpoint);
		javax.xml.ws.Dispatch<Source> dispatch = null;
		dispatch = service.createDispatch(Constants.PORT_QNAME, Source.class,
				mode);

		assertNotNull("Dispatch not null", dispatch);

		return dispatch;
	}

}
