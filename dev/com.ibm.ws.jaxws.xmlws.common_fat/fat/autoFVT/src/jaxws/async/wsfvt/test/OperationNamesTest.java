//
// @(#) 1.5 autoFVT/src/jaxws/async/wsfvt/test/OperationNamesTest.java, WAS.websvcs.fvt, WASX.FVT 2/6/07 18:25:49 [7/11/07 13:14:38]
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
// 09/18/06 sedov       LIDB3296.38     New File
//

package jaxws.async.wsfvt.test;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.ws.BindingProvider;

import jaxws.async.wsfvt.common.CallbackHandler;
import jaxws.async.wsfvt.common.Constants;
import jaxws.async.wsfvt.common.doclitwr.AnotherResponse;
import jaxws.async.wsfvt.common.doclitwr.CustomAsyncResponse;
import jaxws.async.wsfvt.common.doclitwr.InvokeAsyncResponse;
import jaxws.async.wsfvt.common.doclitwr.client.AsyncPort;
import jaxws.async.wsfvt.common.doclitwr.client.AsyncService;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for having wsdl:operations names ending with Async
 * 
 */
public class OperationNamesTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private static final String REQUEST = "roundtrip me!";

	public OperationNamesTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(OperationNamesTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}
	
	/************************* wsdl:operation named Async *************************/
	
	/**
	 * @testStrategy Test for wsdl:operation names ending in Async. Invoke an
	 *               operation in syncronous mode
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for wsdl:operation names ending in Async. Invoke an operation in syncronous mode",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_invokeAsyncOperation_sync() {
		AsyncPort port = getPort();

		String response = port.invokeAsync(REQUEST);

		assertNotNull("Response is null", response);
		assertTrue("Message was not roundtripped", response.equals(REQUEST));
	}

	/**
	 * @testStrategy Test for wsdl:operation names ending in Async. Invoke an
	 *               operation in AsyncPolling mode
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for wsdl:operation names ending in Async. Invoke an operation in AsyncPolling mode",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_invokeAsyncOperation_polling() throws Exception {
		AsyncPort port = getPort();

		Future<InvokeAsyncResponse> rsp = port.invokeAsyncAsync(REQUEST);

		// block until response comes back
		InvokeAsyncResponse response = rsp.get();

		assertNotNull("Response is null", response);
		assertTrue("Message was not roundtripped", response.getResponse()
				.equals(REQUEST));
	}

	/**
	 * @testStrategy Test for wsdl:operation names ending in Async. Invoke an
	 *               operation in AsyncCallback mode
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for wsdl:operation names ending in Async. Invoke an operation in AsyncCallback mode",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_invokeAsyncOperation_callback() throws Exception {
		AsyncPort port = getPort();

		CallbackHandler<InvokeAsyncResponse> cb = new CallbackHandler<InvokeAsyncResponse>();
		Future<?> rsp = port.invokeAsyncAsync(REQUEST, cb);

		// wait until response comes back
		waitBlocking(rsp);
		InvokeAsyncResponse response = cb.get();

		assertNotNull("Response is null", response);
		assertTrue("Message was not roundtripped", response.getResponse()
				.equals(REQUEST));
	}

	/************************* wsdl:operation mapped to Async *************************/	
	
	/**
	 * @testStrategy Test for wsdl:operation names ending in Async. WSDL defines
	 *               a customAsync operation which is mapped to a java name
	 *               remapped. Invoke this operation in syncronous mode
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for wsdl:operation names ending in Async. WSDL defines a customAsync operation which is mapped to a java name remapped. Invoke this operation in syncronous mode",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_remappedOperation_sync() {
		AsyncPort port = getPort();

		String response = port.remapped(REQUEST);

		assertNotNull("Response is null", response);
		assertTrue("Message was not roundtripped", response.equals(REQUEST));
	}

	/**
	 * @testStrategy Test for wsdl:operation names ending in Async. WSDL defines
	 *               a customAsync operation which is mapped to a java name
	 *               remapped. Invoke this operation in AsyncPolling mode
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for wsdl:operation names ending in Async. WSDL defines a customAsync operation which is mapped to a java name remapped. Invoke this operation in AsyncPolling mode",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_remappedOperation_polling() throws Exception {
		AsyncPort port = getPort();

		Future<CustomAsyncResponse> rsp = port.remappedAsync(REQUEST);

		// block until response comes back
		CustomAsyncResponse response = rsp.get();

		assertNotNull("Response is null", response);
		assertTrue("Message was not roundtripped", response.getResponse()
				.equals(REQUEST));
	}

	/**
	 * @testStrategy Test for wsdl:operation names ending in Async. WSDL defines
	 *               a customAsync operation which is mapped to a java name
	 *               remapped. Invoke this operation in AsyncCallback mode
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for wsdl:operation names ending in Async. WSDL defines a customAsync operation which is mapped to a java name remapped. Invoke this operation in AsyncCallback mode",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_remappedOperation_callback() throws Exception {
		AsyncPort port = getPort();

		CallbackHandler<CustomAsyncResponse> cb = new CallbackHandler<CustomAsyncResponse>();
		Future<?> rsp = port.remappedAsync(REQUEST, cb);

		// wait until response comes back
		waitBlocking(rsp);
		CustomAsyncResponse response = cb.get();

		assertNotNull("Response is null", response);
		assertTrue("Message was not roundtripped", response.getResponse()
				.equals(REQUEST));
	}

	/**************** non-Async mapped to Async ***************/
	
	/**
	 * @testStrategy Test for wsdl:operation names ending in Async. WSDL defines
	 *               a another operation which is mapped to a java name
	 *               anotherAsync. Invoke this operation in syncronous mode
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for wsdl:operation names ending in Async. WSDL defines a another operation which is mapped to a java name anotherAsync. Invoke this operation in syncronous mode",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_anotherOperation_sync() {
		AsyncPort port = getPort();

		String response = port.anotherAsync(REQUEST);

		assertNotNull("Response is null", response);
		assertTrue("Message was not roundtripped", response.equals(REQUEST));
	}

	/**
	 * @testStrategy Test for wsdl:operation names ending in Async. WSDL defines
	 *               a another operation which is mapped to a java name
	 *               anotherAsync. Invoke this operation in async-polling mode
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for wsdl:operation names ending in Async. WSDL defines a another operation which is mapped to a java name anotherAsync. Invoke this operation in async-polling mode",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_anotherOperation_polling() throws Exception {
		AsyncPort port = getPort();

		Future<AnotherResponse> rsp = port.anotherAsyncAsync(REQUEST);

		// block until response comes back
		AnotherResponse response = rsp.get();

		assertNotNull("Response is null", response);
		assertTrue("Message was not roundtripped", response.getResponse()
				.equals(REQUEST));
	}

	/**
	 * @testStrategy Test for wsdl:operation names ending in Async. WSDL defines
	 *               a another operation which is mapped to a java name
	 *               anotherAsync. Invoke this operation in async-callback mode
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for wsdl:operation names ending in Async. WSDL defines a another operation which is mapped to a java name anotherAsync. Invoke this operation in async-callback mode",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_anotherOperation_callback() throws Exception {
		AsyncPort port = getPort();

		CallbackHandler<AnotherResponse> cb = new CallbackHandler<AnotherResponse>();
		Future<?> rsp = port.anotherAsyncAsync(REQUEST, cb);

		// wait until response comes back
		waitBlocking(rsp);
		AnotherResponse response = cb.get();

		assertNotNull("Response is null", response);
		assertTrue("Message was not roundtripped", response.getResponse()
				.equals(REQUEST));
	}	
	
	/**
	 * Auxiliary method used to wait for a monitor for a certain amount of time
	 * before timing out
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

	//private KillerThread killer = null;
	
	private AsyncPort getPort() {
		AsyncService service = new AsyncService();
		service.setExecutor(Executors.newSingleThreadExecutor());

		AsyncPort port = service.getAsyncPort();

		assertNotNull("Port is null", port);

		// stop the killer thread if it is active
		//if (killer != null) killer.abort();

		// create a killer monitor thread that will make
		// sure to kill the executor after a while
		//killer = new KillerThread(service, Constants.CLIENT_MAX_SLEEP_SEC);
		//killer.start();

		// modify endpoint location...useful if testing from eclipse with TCPMON
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.DOCLITWR_ASYNC_ENDPOINT);

		return port;
	}	
}
