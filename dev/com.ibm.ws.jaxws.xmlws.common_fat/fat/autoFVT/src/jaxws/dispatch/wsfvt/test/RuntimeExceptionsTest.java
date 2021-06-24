//
// @(#) 1.1.1.6 autoFVT/src/jaxws/dispatch/wsfvt/test/RuntimeExceptionsTest.java, WAS.websvcs.fvt, WASX.FVT 1/20/07 16:14:47 [7/11/07 13:15:19]
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
// 12/19/06 sedov    409973             Added stack-walking and updated EPR references
// 01/20/07 sedov    415799             Added FAULT_TEXT constant
//
package jaxws.dispatch.wsfvt.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import jaxws.dispatch.wsfvt.common.CallbackHandler;
import jaxws.dispatch.wsfvt.common.Constants;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This will test run time exceptions that are generated when a service is
 * either unavailable or when the endpoint throws an exception. All tests expect
 * to receive a WebServiceException or ExecutionException with an appropriate
 * getCause which provides additional detail into why the invoke failed
 */
public class RuntimeExceptionsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	
	private static final Class HOST_NOT_FOUND_CLASS = java.nio.channels.UnresolvedAddressException.class;
	private static final Class CONNECT_404_CLASS = java.net.ConnectException.class;
	
	private static final String HOST_NOT_FOUND_ENDPOINT = "http://balh.blah.yatta.yatta.ibm.com";
	private static final String CONNECT_404_ENDPOINT = Constants.BASE_ENDPOINT + "services/ServiceDoesNotExist";
	
	public RuntimeExceptionsTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(RuntimeExceptionsTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}
	
	/** **************** disptach.invoke Exception tests ***************** */

	/**
	 * @testStrategy Test for WebServiceException on invokeOneWay - host does
	 *               not exist (server is down/invalid endpoint)
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_oneWay_UnknownHost() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				HOST_NOT_FOUND_ENDPOINT);

		try {
			dispatch.invokeOneWay(Constants
					.toStreamSource(Constants.ONEWAY_MSG_EXCEPTION));

			fail("WebServiceException expected when invokeOneWay is invoked against non-existing endpoint.");
		} catch (WebServiceException wse) {
			logStackTrace(wse);
			
			assertTrue("WSE.cause must be instance of UnknownHostException",
					checkStack(wse, HOST_NOT_FOUND_CLASS));
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on invokeOneWay - endpoing
	 *               gives 404/Not Found (Service unavilable/not started/etc)
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_oneWay_404NotFound() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				CONNECT_404_ENDPOINT);

		try {
			dispatch.invokeOneWay(Constants
					.toStreamSource(Constants.ONEWAY_MSG_EXCEPTION));

			fail("WebServiceException expected when invokeOneWay is invoked against non-existing endpoint.");
		} catch (WebServiceException wse) {
			logStackTrace(wse);

			assertTrue("WSE.cause must be instance of ConnectException", 
					checkStack(wse, CONNECT_404_CLASS));
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on invokeOneWay - endpoint
	 *               throws a WebServiceException
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_oneWay_WebServiceException() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		try {
			dispatch.invokeOneWay(Constants
					.toStreamSource(Constants.ONEWAY_MSG_EXCEPTION));

		} catch (WebServiceException wse) {
			logStackTrace(wse);

			fail("WebServiceException should not be thrown when endpoint throws it on invokeOneWay.");
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on invoke - host does not
	 *               exist (server is down/invalid endpoint)
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_twoWay_HostNotFound() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				HOST_NOT_FOUND_ENDPOINT);

		try {
			dispatch.invoke(Constants
					.toStreamSource(Constants.TWOWAY_MSG_EXCEPTION));
			fail("WebServiceException expected when invoke endpoint does not exist.");
		} catch (WebServiceException wse) {
			logStackTrace(wse);

			assertTrue("Exception must be instance of UnknownHostException",
					checkStack(wse, HOST_NOT_FOUND_CLASS));
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on invoke - 404/Not Found
	 *               (Service is unavailable/not started/etc)
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_twoWay_404NotFound() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				CONNECT_404_ENDPOINT);

		try {
			dispatch.invoke(Constants
					.toStreamSource(Constants.TWOWAY_MSG_EXCEPTION));
			fail("WebServiceException expected when invoke endpoint does not exist.");
		} catch (WebServiceException wse) {
			logStackTrace(wse);

			assertTrue("Exception must be instance of ConnectException", 
					checkStack(wse, CONNECT_404_CLASS));
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on invoke - endpoint throws a
	 *               WebServiceException
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_twoWay_WebServiceException() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		try {
			dispatch.invoke(Constants
					.toStreamSource(Constants.TWOWAY_MSG_EXCEPTION));
			
			fail("SOAPFaultException expected when invoke endpoint throws an exception.");
		} catch (WebServiceException wse) {
			logStackTrace(wse);
			
			assertTrue(
					"FAULT should contain the exception text.",
					wse.getMessage().indexOf(Constants.FAULT_TEXT) != -1);			
			
			assertTrue("Exception must be instance of SOAPFaultException",
					wse instanceof SOAPFaultException);
		}
	}

	/**
	 * @testStrategy Test for
	 *               ExecutionException/WebServiceException/UnknownHostException
	 *               on invokeAsync-polling - unknown host (server not started)
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_asyncPoll_UnknownHost() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				HOST_NOT_FOUND_ENDPOINT);

		try {
			Source msg = Constants
					.toStreamSource(Constants.TWOWAY_MSG_EXCEPTION);
			Response<Source> rsp = dispatch.invokeAsync(msg);

			rsp.get();

			fail("ExecutionException expected when invokeAsync is invoked with an invalid endpoint address");
		} catch (ExecutionException ee) {
			logStackTrace(ee);

			assertTrue(
					"Execution aborted due to Thread hanging",
					!(ee.getCause() instanceof InterruptedException));
			
			assertTrue(
					"Exception.cause must be instance of WebServiceException",
					ee.getCause() instanceof WebServiceException);
			
			assertTrue("WSE.cause must be instance of UnknownHostException", 
					checkStack(ee.getCause(), HOST_NOT_FOUND_CLASS));
		} catch (Exception e) {
			fail("Unexpected Exception: " + e);
		}
	}

	/**
	 * @testStrategy Test for
	 *               ExecutionException/WebServiceException/ConnectException on
	 *               invokeAsync-polling - 404/Not Found (service is not
	 *               started)
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_asyncPoll_404NotFound() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				CONNECT_404_ENDPOINT);

		try {
			Source msg = Constants
					.toStreamSource(Constants.TWOWAY_MSG_EXCEPTION);
			Response<Source> rsp = dispatch.invokeAsync(msg);

			rsp.get();

			fail("ExecutionException expected when invokeAsync is invoked against non-exisintg endpoint");
		} catch (ExecutionException ee) {
			logStackTrace(ee);

			assertTrue(
					"Execution aborted due to Thread hanging",
					!(ee.getCause() instanceof InterruptedException));			
			assertTrue(
					"ExecutionException.getCause must be instance of WebServiceException",
					ee.getCause() instanceof WebServiceException);

			assertTrue(
					"WebServiceException.getCause must be instance of ConnectException",
					checkStack(ee.getCause(), CONNECT_404_CLASS));

		} catch (Exception e) {
			fail("Unexpected Exception: " + e);
		}
	}

	/**
	 * @testStrategy Test for ExecutionException/SOAPFaultException on
	 *               invokeAsync-polling - endpoint throws a WebServiceException
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_asyncPoll_WebServiceException() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		Source msg = Constants.toStreamSource(Constants.TWOWAY_MSG_EXCEPTION);
		Response<Source> response = null;

		try {
			response = dispatch.invokeAsync(msg);
			response.get();
			fail("java.util.concurrent.ExecutionException expected when doing response.get() after invokeAsync() when the endpoint throws an exception");
		} catch (java.util.concurrent.ExecutionException ee) {
			logStackTrace(ee);	
			
			assertTrue("Exception.cause must be instance of SOAPFault", ee
					.getCause() instanceof SOAPFaultException);

			assertTrue(
					"WebServiceException should contain the exception text.",
					ee.getCause().getMessage().indexOf(Constants.FAULT_TEXT) != -1);
		} catch (Exception e) {
			fail("Unexpected Exception: " + e);
		}
	}

	/**
	 * @testStrategy Test for
	 *               ExecutionException/WebServiceException/UnknwonHostException
	 *               on invokeAsync-callback - host is not found (server is
	 *               unavailable)
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_asyncCallback_UnknownHost() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				HOST_NOT_FOUND_ENDPOINT);
		Source msg = Constants.toStreamSource(Constants.TWOWAY_MSG_EXCEPTION);
		CallbackHandler<Source> handler = new CallbackHandler<Source>();

		try {		
			Future<?> rsp = dispatch.invokeAsync(msg, handler);

			waitBlocking(rsp);
			handler.get();

			fail("ExecutionException expected when invokeAsync is invoked with an invalid endpoint address");
		} catch (ExecutionException ee) {
			logStackTrace(ee);
			
			assertTrue(
					"Execution aborted due to Thread hanging",
					!(ee.getCause() instanceof InterruptedException));			
			assertTrue("EE.getCause should be WebServiceException", ee
					.getCause() instanceof WebServiceException);
			assertTrue("WSE.getCause should be UnknownHostException", checkStack(ee.getCause(), HOST_NOT_FOUND_CLASS));
		} catch (Exception e) {
			fail("Unexpected Exception: " + e);
		}

	}

	/**
	 * @testStrategy Test for
	 *               ExecutionException/WebServiceException/ConnectException on
	 *               invokeAsync-callback - 404/Not Found (service is not
	 *               started)
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_asyncCallback_404NotFound() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				CONNECT_404_ENDPOINT);
		Source msg = Constants.toStreamSource(Constants.TWOWAY_MSG_EXCEPTION);
		CallbackHandler<Source> handler = new CallbackHandler<Source>();

		try {
			Future<?> rsp = dispatch.invokeAsync(msg, handler);

			waitBlocking(rsp);
			handler.get();

			fail("ExecutionException expected when invokeAsync is invoked with an invalid endpoint address");
		} catch (ExecutionException ee) {
			logStackTrace(ee);
			
			assertTrue(
					"Execution aborted due to Thread hanging",
					!(ee.getCause() instanceof InterruptedException));			
			assertTrue("EE.getCause should be WebServiceException", ee
					.getCause() instanceof WebServiceException);
			assertTrue("WSE.getCause should be ConnectException", checkStack(ee.getCause(), CONNECT_404_CLASS));
		} catch (Exception e) {
			fail("Unexpected Exception: " + e);
		}
	}

	/**
	 * @testStrategy Test for ExecutionException/SOAPFaultException on
	 *               invokeAsync-callback - endpoint throws a
	 *               WebServiceException
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_asyncCallback_WebServiceException() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		Source msg = Constants.toStreamSource(Constants.TWOWAY_MSG_EXCEPTION);
		CallbackHandler<Source> handler = new CallbackHandler<Source>();

		Future<?> future = null;

		try {
			future = dispatch.invokeAsync(msg, handler);
		} catch (WebServiceException wse) {
			fail("Unexpected WebServiceException when doing invokeAsync() when the endpoint throws an exception");
		}

		try {
			// wait for request to complete
			waitBlocking(future);
			handler.get();

			fail("java.util.concurrent.ExecutionException expected when doing response.get() after invokeAsync() when the endpoint throws an exception");
		} catch (java.util.concurrent.ExecutionException ee) {
			logStackTrace(ee);
			
			assertTrue(
					"Execution aborted due to Thread hanging",
					!(ee.getCause() instanceof InterruptedException));			
			assertTrue(
					"EE.getCause() must return instance of SOAPFaultException",
					ee.getCause() instanceof SOAPFaultException);
			assertTrue(
					"WebServiceException should contain the exception text.",
					ee.getCause().getMessage().indexOf(Constants.FAULT_TEXT) != -1);
		} catch (Exception e) {
			fail("Unexpected Exception: " + e);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on invokeAsync-callback -
	 *               invokeAsync(..., null)
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_asyncCallback_NoHandler() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);
		Source msg = Constants.toStreamSource(Constants.TWOWAY_MSG);

		try {
			dispatch.invokeAsync(msg, null);

			fail("WebServiceException expected when invokeAsync(.., null) is invoked");
		} catch (WebServiceException wse) {
			logStackTrace(wse);
		} catch (Exception e) {
			fail("Unexpected Exception: " + e);
		}
	}

	/**
	 * Auxiliary method used to wait for a monitor for a certain amount of time
	 * before timing out
	 * 
	 * @param monitor
	 */
	private void waitBlocking(Future<?> monitor) throws Exception {
		// wait for request to complete
		int sec = Constants.CLIENT_MAX_SLEEP_SEC;
		while (!monitor.isDone()) {
			Thread.sleep(1000);
			sec--;
			if (sec <= 0)
				break;
		}

		if (sec <= 0)
			fail("Stopped waiting for Async response after "
					+ Constants.CLIENT_MAX_SLEEP_SEC + " sec");
	}

	/** **************** dispatch.invoke Binding exceptions ************** */

	/**
	 * @testStrategy Test for WebServiceException on dispatch.invoke(...) - the
	 *               endpoint throws a WebServiceException over SOAP11
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWebServiceException_SOAP11() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP11HTTP_BINDING);

		String message = Constants.TWOWAY_MSG_EXCEPTION;

		try {
			dispatch.invoke(Constants.toStreamSource(message));
			fail("WebServiceException expected when endpoint returns a soap:Fault.");
		} catch (SOAPFaultException wse) {
			logStackTrace(wse);
			
			assertTrue("SOAPFaultException should contain the exception text.",
					wse.getMessage().indexOf(Constants.FAULT_TEXT) != -1);
		}
	}

	/**
	 * @testStrategy Test for SOAPFaultException on dispatch.invoke(...) - the
	 *               endpoint throws a SOAPFaultException with wsdl:fault detail
	 *               over SOAP11
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPFaultException_SOAP11() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP11HTTP_BINDING);

		String message = Constants.TWOWAY_MSG_EXCEPTION.replace("test_message",
				"#service-specific#");

		try {
			dispatch.invoke(Constants.toStreamSource(message));
			fail("WebServiceException expected when endpoint returns a soap:Fault.");
		} catch (SOAPFaultException sf) {
			logStackTrace(sf);
			
			assertTrue(
					"WebServiceException should contain the exception text.",
					sf.getMessage().indexOf(Constants.FAULT_TEXT) != -1);
			assertNotNull("SOAPFaultException.getFault() is null", sf
					.getFault());
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on dispatch.invoke(...) - the
	 *               endpoint throws a WebServiceException over SOAP12
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWebServiceException_SOAP12() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_BINDING);

		String message = Constants.TWOWAY_MSG_EXCEPTION;

		try {
			dispatch.invoke(Constants.toStreamSource(message));
			fail("WebServiceException expected when endpoint returns a soap:Fault.");
		} catch (SOAPFaultException wse) {
			logStackTrace(wse);
			assertTrue("SOAPFaultException should contain the exception text.",
					wse.getMessage().indexOf(Constants.FAULT_TEXT) != -1);
		}
	}

	/**
	 * @testStrategy Test for SOAPFaultException on dispatch.invoke(...) - the
	 *               endpoint throws a SOAPFaultException with wsdl:fault detail
	 *               over SOAP12
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPFaultException_SOAP12() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP12_ENDPOINT_ADDRESS,
				SOAPBinding.SOAP12HTTP_BINDING);

		String message = Constants.TWOWAY_MSG_EXCEPTION.replace("test_message",
				"#service-specific#");

		try {
			dispatch.invoke(Constants.toStreamSource(message));
			fail("WebServiceException expected when endpoint returns a soap:Fault.");
		} catch (SOAPFaultException sf) {
			logStackTrace(sf);
			
			assertTrue("SOAPFaultException should contain the exception text.",
					sf.getMessage().indexOf(Constants.FAULT_TEXT) != -1);
			assertNotNull("SOAPFaultException.getFault() is null", sf
					.getFault());
		}
	}

	private void logStackTrace(Throwable e){
		Throwable cur = e;
		do {
			System.out.println(getName() + ": " + cur.getClass().getName() + ": " + cur.getMessage());
			cur = cur.getCause();
		} while (cur != null);
	}
	
	/**
	 * Determine if the stacktrace of t (or t itself) contains an instance of find
	 * @param t
	 * @param find
	 * @return
	 */
	public static boolean checkStack(Throwable t, Class find){		
		Throwable cur = t;
		boolean found = false;
		do {
			found = cur.getClass().isAssignableFrom(find);
			cur = cur.getCause();
		} while (!found && cur != null);
		
		return found;
	}	
	
	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<Source> getDispatch(Service.Mode mode, String endpoint) {
		return getDispatch(mode, endpoint, SOAPBinding.SOAP11HTTP_BINDING);
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<Source> getDispatch(Service.Mode mode, String endpoint,
			String binding) {
		
		Service service = Service.create(Constants.SERVICE_QNAME);
		service.setExecutor(Executors.newSingleThreadExecutor());
		
		service.addPort(Constants.PORT_QNAME, binding, endpoint);
		javax.xml.ws.Dispatch<Source> dispatch = null;
		dispatch = service.createDispatch(Constants.PORT_QNAME, Source.class,
				mode);

		assertNotNull("Dispatch Object is null", dispatch);

		return dispatch;
	}
}
