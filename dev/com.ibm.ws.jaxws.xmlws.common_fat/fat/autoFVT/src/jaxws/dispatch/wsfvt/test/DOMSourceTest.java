//
// @(#) 1.3.1.6 autoFVT/src/jaxws/dispatch/wsfvt/test/DOMSourceTest.java, WAS.websvcs.fvt, WASX.FVT 2/7/07 14:23:37 [7/11/07 13:15:17]
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
// 02/07/07 sedov    419445             Dropped non-NS aware parser test

package jaxws.dispatch.wsfvt.test;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
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
 * Tests for JAX-WS Dispatch in DOMSource mode
 */
public class DOMSourceTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private static final String MSG_TYPE_MISMATCH = "When using DOMSource objects, returned types must also be DOMSource objects.";

	// monitor used for interrupting hung async invocations
	private KillerThread killer = null;

	public DOMSourceTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(DOMSourceTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy Test for invokeOneWay in MESSAGE mode over DOMSource
	 *               dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDOMSource_invokeOneWay_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String req = Constants.SOAP11_HEADER + Constants.ONEWAY_MSG
				+ Constants.SOAP11_TRAILER;
		Source message = Constants.toDOMSource(req);

		Constants.logRequest(req);
		dispatch.invokeOneWay(message);
	}

	/**
	 * @testStrategy Test for invokeOneWay in PAYLOAD mode over DOMSource
	 *               dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDOMSource_invokeOneWay_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String req = Constants.ONEWAY_MSG;
		Source payload = Constants.toDOMSource(req);

		Constants.logRequest(req);
		dispatch.invokeOneWay(payload);
	}

	/**
	 * @testStrategy Test for invoke in MESSAGE mode over DOMSource dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDOMSource_invoke_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// create the soapenvelope
		String req = Constants.SOAP11_HEADER + Constants.TWOWAY_MSG
				+ Constants.SOAP11_TRAILER;
		Source message = Constants.toDOMSource(req);

		// invoke
		Constants.logRequest(req);
		Source src = dispatch.invoke(message);

		// validate returned message
		String res = Constants.toString(src);
		Constants.testEnvelope(res, true);
		assertTrue("Invalid Message Returned",
				res.indexOf("test_message") != -1);

		// this "feature" may not be in final GA so I will just put a variable
		// here to enabled it if it becomes available
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			assertTrue(MSG_TYPE_MISMATCH, src instanceof DOMSource);
		}
	}

	/**
	 * @testStrategy Test for invoke in PAYLOAD mode over DOMSource dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDOMSource_invoke_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String req = Constants.TWOWAY_MSG;
		Source payload = Constants.toDOMSource(req);

		// invoke
		Constants.logRequest(req);
		Source src = dispatch.invoke(payload);

		// validate response message
		String res = Constants.toString(src);
		Constants.testEnvelope(res, false);
		assertTrue("Invalid Message Returned",
				res.indexOf("test_message") != -1);

		// this "feature" may not be in final GA so I will just put a variable
		// here to enabled it if it becomes available
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			assertTrue(MSG_TYPE_MISMATCH, src instanceof DOMSource);
		}
	}

	/**
	 * @testStrategy Test for invokeAsync-polling in PAYLOAD mode over DOMSource
	 *               dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDOMSource_invokeAsyncPoll_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String req = Constants.TWOWAY_MSG;
		Source payload = Constants.toDOMSource(req);

		// invoke and wait for response
		Constants.logRequest(req);
		Response<Source> response = dispatch.invokeAsync(payload);
		Source src = response.get();

		// validate resposne message
		String res = Constants.toString(src);
		Constants.testEnvelope(res, false);
		assertTrue("Invalid Message Returned",
				res.indexOf("test_message") != -1);

		// this "feature" may not be in final GA so I will just put a variable
		// here to enabled it if it becomes available
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			assertTrue(MSG_TYPE_MISMATCH, src instanceof DOMSource);
		}
	}

	/**
	 * @testStrategy Test for invokeAsync-polling in MESSAGE mode over DOMSource
	 *               dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDOMSource_invokeAsyncPoll_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String req = Constants.SOAP11_HEADER
			+ Constants.TWOWAY_MSG + Constants.SOAP11_TRAILER;
		Source message = Constants.toDOMSource(req);

		// invoke and wait for response
		Constants.logRequest(req);
		Response<Source> response = dispatch.invokeAsync(message);
		Source src = response.get();

		// validate response
		String res = Constants.toString(src);
		Constants.testEnvelope(res, true);
		assertTrue("Invalid Message Returned",
				res.indexOf("test_message") != -1);

		// this "feature" may not be in final GA so I will just put a variable
		// here to enabled it if it becomes available
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			assertTrue(MSG_TYPE_MISMATCH, src instanceof DOMSource);
		}
	}

	/**
	 * @testStrategy Test for invokeAsync-callback in PAYLOAD mode over
	 *               DOMSource dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDOMSource_invokeAsycCallback_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		CallbackHandler<Source> handler = new CallbackHandler<Source>();

		// compose message
		String req = Constants.TWOWAY_MSG;
		Source source = Constants.toDOMSource(req);

		// invoke and wait for response
		Constants.logRequest(req);
		Future<?> monitor = dispatch.invokeAsync(source, handler);
		handler.waitBlocking(monitor);

		try {
			// validate response
			Source src = handler.get();
			String res = Constants.toString(src);
			Constants.testEnvelope(res, false);
			assertTrue("Invalid Message Returned",
					res.indexOf("test_message") != -1);

			// this "feature" may not be in final GA so I will just put a
			// variable
			// here to enabled it if it becomes available
			if (Constants.ENABLE_RETURNTYPE_CHECKING) {
				assertTrue(MSG_TYPE_MISMATCH, src instanceof DOMSource);
			}
		} catch (WebServiceException e) {
			e.printStackTrace(System.out);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Test for invokeAsync-callback in MESSAGE mode over
	 *               DOMSource dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDOMSource_invokeAsycCallback_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		CallbackHandler<Source> handler = new CallbackHandler<Source>();

		// compose message
		String req = Constants.SOAP11_HEADER + Constants.TWOWAY_MSG + Constants.SOAP11_TRAILER;
		Source message = Constants.toDOMSource(req);

		// invoke and wait for response
		Constants.logRequest(req);
		Future<?> monitor = dispatch.invokeAsync(message, handler);
		handler.waitBlocking(monitor);

		try {
			// vaidate response
			Source src = handler.get();
			String res = Constants.toString(src);

			Constants.testEnvelope(res, true);
			assertTrue("Invalid Message Returned",
					res.indexOf("test_message") != -1);

			// this "feature" may not be in final GA so I will just put a
			// variable
			// here to enabled it if it becomes available
			if (Constants.ENABLE_RETURNTYPE_CHECKING) {
				assertTrue(MSG_TYPE_MISMATCH, src instanceof DOMSource);
			}
		} catch (Exception e) {
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Test for parser issues with sending a 1MB payload over
	 *               DOMSource dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDOMSource_invoke_largeMessage() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build a large message
		StringBuffer message = new StringBuffer();
		message.append("<ns1:twoWay xmlns:ns1=\""
				+ Constants.WSDL_NAMESPACE + "\">");
		for (int i = 0; i < 30000; i++) {
			message.append("<value>test_message_" + i + "</value>");
		}
		message.append("</ns1:twoWay>");

		// invoke and see what happens
		dispatch.invoke(Constants.toDOMSource(message.toString()));
	}

	/**
	 * @testStrategy Test for receiving of WebServiceException when endpoint
	 *               throws it in DOMSource Dispatch
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDOMSource_invoke_endpointException() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String message = Constants.TWOWAY_MSG_EXCEPTION.replace("test_message",
				"#service-specific#");

		try {
			dispatch.invoke(Constants.toDOMSource(message));

			fail("WSE expected when endpoint returns a soap:Fault.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			assertTrue(
					"WebServiceException should contain the exception text.",
					wse.getMessage().indexOf(Constants.FAULT_TEXT) != -1);

			assertTrue("Exception must be a SOAPFaultException",
					wse instanceof SOAPFaultException);
		}
	}

	/**
	 * @testStrategy Test for creating a dispatch explicitly typed with
	 *               DOMSource
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDOMSource_explicitlyTyped() throws Exception {

		// only invoke if return type checking is enabled
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			// create a dispatch explicitly typed with DOMSource
			Service service = Service.create(Constants.SERVICE_QNAME);
			service.addPort(Constants.PORT_QNAME,
					SOAPBinding.SOAP11HTTP_BINDING,
					Constants.SOAP11_ENDPOINT_ADDRESS);
			Dispatch<DOMSource> dispatch = null;
			dispatch = service.createDispatch(Constants.PORT_QNAME,
					DOMSource.class, Service.Mode.PAYLOAD);
			assertNotNull("Dispatch not null", dispatch);

			// invoke
			DOMSource message = Constants.toDOMSource(Constants.TWOWAY_MSG);

			DOMSource response = dispatch.invoke(message);

			// validate response
			String res = Constants.toString(response);
			assertTrue("Invalid Message Returned",
					res.indexOf("test_message") != -1);
		}
	}

	/**
	 * @testStrategy Test for invoking a Source typed dispatch with a message
	 *               that was built using a non-namespace aware parser
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
	/*public void testDOMSource_nonNamespaceAwareParser() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// create the full soap envelope + payload using a non
		// namespace aware DOM parser
		Source message = Constants.toDOMSource(Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG + Constants.SOAP11_TRAILER, false);

		Constants.logRequest(Constants.toString(message));
		try {
			Source reply = dispatch.invoke(message);
			
			String response = Constants.toString(reply);
			
			
			
			fail("WebServiceException expected when non NS aware parser is used");
		} catch (WebServiceException e) {
			e.printStackTrace(System.out);

		}
	}//*/

	/**
	 * @testStrategy Create a DOMSource by using the default constructor and
	 *               populating anything in
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Create a DOMSource by using the default constructor and populating anything in",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDOMSource_invoke_emptySource() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		try {
			dispatch.invoke(new DOMSource());
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
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
