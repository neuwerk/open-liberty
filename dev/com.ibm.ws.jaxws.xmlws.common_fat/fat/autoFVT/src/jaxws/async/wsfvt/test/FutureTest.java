//
// @(#) 1.5 WautoFVT/src/jaxws/async/wsfvt/test/FutureTest.java, WAS.websvcs.fvt, WSFP.WFVT, a0706.23 2/6/07 18:25:46 [2/16/07 17:57:04]
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
// 01/09/07 sedov       413290          New File 
//

package jaxws.async.wsfvt.test;

import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

import jaxws.async.wsfvt.common.CallbackHandler;
import jaxws.async.wsfvt.common.Constants;
import jaxws.async.wsfvt.common.PausableExecutor;
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
 * Async Future object used in async-callbacks
 */
public class FutureTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private AsyncPort port = null;
	
	public FutureTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(FutureTest.class);
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

		// do the isAsleep check until the Async thread invokes it
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
	 * @testStrategy Test for Future.cancel() on async-callback invoke after
	 *               it has completed. This should have no effect
	 * @wsdl async.wsdl + async.xml
	 * @target AsyncPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testFuture_cancel_completed() throws Exception {
		final String MESSAGE = getName();

		System.out.println(">> " + MESSAGE);
		
		
		CallbackHandler<SleepResponse> handler = new CallbackHandler<SleepResponse>();
		
		// start executing
		Future<?> resp = port.sleepAsync(MESSAGE, handler);
		System.out.println(MESSAGE + ".sleep");
		AsyncClient.isAsleepCheck(MESSAGE, port);
		
		
		String wokeUp = port.wakeUp();
		System.out.println(MESSAGE + ".wakeup=" + wokeUp);
		
		// now that request has completed cancel it...this should have
		// no effect, then we will retrieve the response
		AsyncClient.waitBlocking(resp);
		resp.cancel(true);
		
		assertNotNull("Handler did not receive a response", handler.getResponse());
		
		System.out.println(MESSAGE + ".response.get()");
		SleepResponse sr = handler.getResponse().get();
		
		System.out.println(MESSAGE + ".response.get()=" + sr.getMessage());
		
		System.out.println("<< " + MESSAGE);
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
	public void testFuture_cancel_interruptRunning() throws Exception {
		final String MESSAGE = getName();

		
		CallbackHandler<SleepResponse> handler = new CallbackHandler<SleepResponse>();
		
		// start executing
		Future<?> resp = port.sleepAsync(MESSAGE, handler);

		// wait until the server receives the invoke and
		// then cancell it
		AsyncClient.isAsleepCheck(MESSAGE, port);
		resp.cancel(true);
		
		port.wakeUp();
		AsyncClient.waitBlocking(resp);
		
		// save these params so that we make sure to get to unblock
		// the service
		boolean isCancelled = resp.isCancelled();
		boolean isDone = resp.isDone();		
		
		Object o = handler.get();
		
		System.out.println("Future.isDone=" + isDone + " Future.isCancelled=" + isCancelled);
		System.out.println("handler.get=" + o);
		
		// verify that isCancelled and isDone return true
		assertNull("AsyncHandler received a response!", o);
		assertTrue("Response.isCancelled != true", isCancelled);
		assertTrue("Response.isDone != true", isDone);
	}

	/**
	 * @testStrategy Test for Future.cancel() on async-polling invoke, we will
	 *               call cancel while the request before it is executed to make
	 *               sure we get back a InterruptedException at Response.get.
	 *               Also testing that Future.isDone and isCancelled are true
	 * @wsdl async.wsdl + async.xml
	 * @target AsyncPortImpl
	 */
	/*public void testFuture_cancel_interruptWaiting() throws Exception {
		final String MESSAGE = getName();

		PausableExecutor pe = new PausableExecutor(1);
		AsyncPort port = AsyncClient.getPort(pe);
		pe.pause();

		// start executing & cancell immediately...the request should
		// have never been dispatched
		CallbackHandler<CustomAsyncResponse> handler = new CallbackHandler<CustomAsyncResponse>();
		Future<?> resp = port.remappedAsync(MESSAGE, handler);

		assertNull("Request was dispatched & received!", MESSAGE.equals(port
				.isAsleep()));
		resp.cancel(true);

		// save these params so that we make sure to get to unblock
		// the service
		boolean isCancelled = resp.isCancelled();
		boolean isDone = resp.isDone();

		try {
			handler.get();
			fail("InterruptedException expected");
		} catch (InterruptedException ce) {
			System.out.println(getName() + ": " + ce);
		} finally {
			// cancell operation in case it was dispatched
			port.wakeUp();

			// verify that isCancelled and isDone return true
			assertTrue("Response.isCancelled = false", isCancelled);
			assertTrue("Response.isDone = false", isDone);
		}
	}//*/

	/**
	 * @testStrategy Test for Future.cancel() on async-polling invoke, we will
	 *               call cancel while the request executes andrequest that it
	 *               not be interrupted. Expecting that the operation will
	 *               succeed
	 * @wsdl async.wsdl + async.xml
	 * @target AsyncPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testFuture_cancel_noInterrupt() throws Exception {
		final String MESSAGE = getName();

		
		CallbackHandler<SleepResponse> handler = new CallbackHandler<SleepResponse>();
		
		// start executing
		Future<?> resp = port.sleepAsync(MESSAGE, handler);

		// wait until the server receives the invoke and
		// then cancell it...but do not interrupt it
		AsyncClient.isAsleepCheck(MESSAGE, port);
		resp.cancel(false);
	
		port.wakeUp();
		AsyncClient.waitBlocking(resp);
		
		// save these params so that we make sure to get to unblock
		// the service
		boolean isCancelled = resp.isCancelled();
		boolean isDone = resp.isDone();		
		
		Object o = handler.get();
		
		System.out.println("Future.isDone=" + isDone + " Future.isCancelled=" + isCancelled);
		System.out.println("handler.get=" + o);
		
		// verify that isCancelled and isDone return true
		assertNull("AsyncHandler received a response!", o);
		assertTrue("Response.isCancelled != true", isCancelled);
		assertTrue("Response.isDone != true", isDone);
	}


	/**
	 * @testStrategy Test for Future.isDone when request is still in progress
	 * @wsdl async.wsdl + async.xml
	 * @target AsyncPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testFuture_isDone_notDone() throws Exception {
		final String MESSAGE = getName();

		boolean isDone = false;
		
		
		CallbackHandler<SleepResponse> handler = new CallbackHandler<SleepResponse>();
		
		// start executing
		Future<?> resp = port.sleepAsync(MESSAGE, handler);

		// capture state
		isDone = resp.isDone();

		// wake up the service
		AsyncClient.isAsleepCheck(MESSAGE, port);
		port.wakeUp();
		AsyncClient.waitBlocking(resp);

		assertFalse("Response.isDone != false when request is in progress", isDone);
	}
	
	/**
	 * @testStrategy Test for Future.isDone when request is completed
	 * @wsdl async.wsdl + async.xml
	 * @target AsyncPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testFuture_isDone_complete() throws Exception {
		final String MESSAGE = getName();

		boolean isDone = false;
		
		CallbackHandler<SleepResponse> handler = new CallbackHandler<SleepResponse>();
		
		// start executing
		Future<?> resp = port.sleepAsync(MESSAGE, handler);
		AsyncClient.isAsleepCheck(MESSAGE, port);
		
		// wake up the service
		port.wakeUp();		
		
		// capture state
		AsyncClient.waitBlocking(resp);
		isDone = resp.isDone();

		assertTrue("Response.isDone != true when request is comeplete", isDone);
	}	
}
