//
// @(#) 1.3 autoFVT/src/jaxws/async/wsfvt/test/ResponseTest.java, WAS.websvcs.fvt, WASX.FVT 5/11/07 15:22:05 [7/11/07 13:14:39]
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
// 01/10/07 sedov       413290          Added more tests
// 05/11/07 sedov       438719          Added more trace
//
package jaxws.async.wsfvt.test;

import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeoutException;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

import jaxws.async.wsfvt.common.Constants;
import jaxws.async.wsfvt.common.doclitwr.CustomAsyncResponse;
import jaxws.async.wsfvt.common.doclitwr.SleepResponse;
import jaxws.async.wsfvt.common.doclitwr.client.AsyncPort;
import jaxws.async.wsfvt.common.doclitwr.client.AsyncService;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for Asynchrony in JAX-WS. This test suite will exercise the
 * Async Response object used in async-polling
 * 
 * Note that in polling case, Executor has no effect
 */
public class ResponseTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	
	private AsyncPort  port = null;
	
	public ResponseTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(ResponseTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
		this.port = AsyncClient.getPort(null);
		port.wakeUp();
	}
	
	/**
	 * @testStrategy Test that the service is up and running before running any
	 *               other tests
	 * @wsdl async.wsdl + async.xml
	 * @target AsyncPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_isAlive() throws Exception {
		final String MESSAGE = getName();

		// invoke the server with a request to sleep until we request
		// that it comes awake
		Response<SleepResponse> response = port.sleepAsync(MESSAGE);
		AsyncClient.isAsleepCheck(MESSAGE, port);

		// release the server, this method will block on the server until
		String release = port.wakeUp();
		assertTrue(MESSAGE.equals(release));

		// verify that the invoke is released
		AsyncClient.waitBlocking(response);
		String msg = response.get().getMessage();
		assertTrue(MESSAGE.equals(msg));
	}

	/**
	 * @testStrategy Test for Future.cancel() on async-polling invoke, we will
	 *               call cancel while the request is executing to make sure we
	 *               get back a CancellationException at Response.get. Also
	 *               testing that Future.isDone and isCancelled are true
	 * @wsdl async.wsdl + async.xml
	 * @target AsyncPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testResponse_cancel_withInterrupt_running() throws Exception {
		final String MESSAGE = getName();

		// start executing & make sure the request reaches the server
		Response<SleepResponse> resp = port.sleepAsync(MESSAGE);
		AsyncClient.isAsleepCheck(MESSAGE, port);
		
		resp.cancel(true);

		// save these params so that we make sure to get to unblock
		// the service
		boolean isCancelled = resp.isCancelled();
		boolean isDone = resp.isDone();

		System.out.println("Response.isCancelled=" + isCancelled + " Response.isDone=" + isDone);
		
		try {
			Object o = resp.get();
			
			System.out.println("Response.get=" + o);
			fail("InterruptedException expected");
		} catch (CancellationException ce) {
			Constants.logStack(ce);
		}

		// verify that isCancelled and isDone return true
		assertTrue("Response.isCancelled = false", isCancelled);
	}

	/**
	 * @testStrategy Test for Future.cancel() on async-polling invoke, we will
	 *               call cancel while the request executes andrequest that it
	 *               not be interrupted. Expecting that the operation will
	 *               succeed
	 * @wsdl async.wsdl + async.xml
	 * @target AsyncPortImpl
	 */
	/*public void testResponse_cancel_noInterrupt() throws Exception {
		final String MESSAGE = getName();

		// invoke async operation and make sure the request reaches the endpoint
		Response<SleepResponse> resp = port.sleepAsync(MESSAGE);
		AsyncClient.isAsleepCheck(MESSAGE, port);
		
		resp.cancel(false);

		// wake up the service
		port.wakeUp();
		AsyncClient.waitBlocking(resp);

		boolean isCancelled = resp.isCancelled();
		boolean isDone = resp.isDone();
		
		try {
			Object o = resp.get();
			System.out.println("Response.get = " + o);
		} catch (Exception e) {
			System.out.println(getName() + ": " + e);
			fail("Unexpected: " + e);
		}
		
		System.out.println("Response.isCancelled = " + isCancelled);
		System.out.println("Response.isDone = " + isDone);
		
		assertTrue("Response.isCancelled = true", isCancelled);
	}//*/

	/**
	 * @testStrategy Test for Response.get(timeout), we will set the timeout
	 *               much longer then the server timeout so that we can get an
	 *               actual response
	 * @wsdl async.wsdl + async.xml
	 * @target AsyncPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testResponse_get_requestInProgress() throws Exception {
		final String MESSAGE = getName();

		// invoke the endpoitn with a short request
		Response<CustomAsyncResponse> resp = port.remappedAsync(MESSAGE);

		try {
			// sleep for a short while to make sure the wait timeout
			CustomAsyncResponse car = resp.get(
					Constants.CLIENT_SHORT_SLEEP_SEC,
					java.util.concurrent.TimeUnit.SECONDS);

			assertNotNull("Response is null", car);
		} catch (TimeoutException te) {
			fail("Unexpected " + te);
		}
	}	
	
	/**
	 * @testStrategy Test for Response.get(timeout), we will set the timeout
	 *               much longer then the server timeout so that we can get an
	 *               actual response
	 * @wsdl async.wsdl + async.xml
	 * @target AsyncPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testResponse_get_withTimeout_long() throws Exception {
		final String MESSAGE = getName();

		// invoke the endpoitn with a short request
		Response<CustomAsyncResponse> resp = port.remappedAsync(MESSAGE);

		try {
			// sleep for a short while to make sure the wait timeout
			CustomAsyncResponse car = resp.get(
					Constants.CLIENT_SHORT_SLEEP_SEC,
					java.util.concurrent.TimeUnit.SECONDS);

			assertNotNull("Response is null", car);
		} catch (TimeoutException te) {
			fail("Unexpected " + te);
		}
	}

	/**
	 * @testStrategy Test for Response.get(timeout), we will set the timeout
	 *               much shorter then the server wit and expect to get a
	 *               TimeoutException
	 * @wsdl async.wsdl + async.xml
	 * @target AsyncPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testResponse_get_withTimeout_short() throws Exception {
		final String MESSAGE = getName();

		Response<?> resp = port.sleepAsync(MESSAGE);

		// wait until the server receives the invoke
		AsyncClient.isAsleepCheck(MESSAGE, port);

		long start = System.currentTimeMillis();
		try {
			System.out.println("<<get(" + Constants.CLIENT_SHORT_SLEEP_SEC + "sec)");
			resp.get(Constants.CLIENT_SHORT_SLEEP_SEC,
					java.util.concurrent.TimeUnit.SECONDS);
			System.out.println(">>get(10sec) diff=" + (System.currentTimeMillis() - start)/1000);
			
			fail("TimeoutException expected");
		} catch (TimeoutException te) {
			System.out.println(">>get(10sec) diff=" + (System.currentTimeMillis() - start)/1000);
			System.out.println(getName() + ": " + te);
			assertTrue("Response.isDone = false", resp.isDone() == false);
		} finally {
			//port.wakeUp();
		}
	}

	/**
	 * @testStrategy Test for Response.getContext, we will request the response
	 *               context while the request is still executing
	 * @wsdl async.wsdl + async.xml
	 * @target AsyncPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testResponse_getContext() throws Exception {
		final String MESSAGE = getName();

		// create service & proxy
		AsyncService service = new AsyncService();
		AsyncPort port = service.getAsyncPort();

		// patch endpoint address
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.DOCLITWR_ASYNC_ENDPOINT);		
		
		// invoke a long running operation and then
		// verify that it has reached the server before running this test
		Response<?> resp = port.sleepAsync(MESSAGE);
		AsyncClient.isAsleepCheck(MESSAGE, port);

		Map<String, Object> context = null;

		try {
			// get the context while the call is running
			context = resp.getContext();
		} catch (Exception e) {
			fail("Unexpected " + e);
		} finally {
			assertNull("Response.getContext must return null if response is not available", context);
			port.wakeUp();
		}
	}
	
}
