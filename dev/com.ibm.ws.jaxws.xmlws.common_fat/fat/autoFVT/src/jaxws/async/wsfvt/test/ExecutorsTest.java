//
// @(#) 1.8 autoFVT/src/jaxws/async/wsfvt/test/ExecutorsTest.java, WAS.websvcs.fvt, WASX.FVT 5/11/07 15:22:08 [7/11/07 13:14:37]
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
// 12/20/06 sedov       D409973         Modified ExecutorService tests to use callback
//                                      Added stack walking 
// 05/11/07 sedov       D438719         Added trace & AsyncClient
//
package jaxws.async.wsfvt.test;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

import jaxws.async.wsfvt.common.Constants;
import jaxws.async.wsfvt.common.doclitwr.AnotherResponse;
import jaxws.async.wsfvt.common.doclitwr.SleepResponse;
import jaxws.async.wsfvt.common.doclitwr.client.AsyncPort;
import jaxws.async.wsfvt.common.doclitwr.client.AsyncService;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * Tests for working with varios executors provided by Executors factory class
 * and also verifies that varios executor methods work as expected and that
 * executor exceptions are propagated correctly
 */
public class ExecutorsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private static final int THREAD_POOL_SZ = 3;

	private static final int MAX_ASYNCREQUESTS = 10;

	public ExecutorsTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(ExecutorsTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}
	

	/**
	 * @testStrategy Test accessing async web services using a
	 *               CachedThreadPoolExecutor available from Executors object
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test accessing async web services using a CachedThreadPoolExecutor available from Executors object",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_setExecutor_CachedThreadPool() throws Exception  {
		doExecutorTest(Executors.newCachedThreadPool());
	}

	/**
	 * @testStrategy Test accessing async web services using a
	 *               FixedThreadPoolExecutor available from Executors object
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test accessing async web services using a FixedThreadPoolExecutor available from Executors object",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_setExecutor_FixedThreadPool() throws Exception  {
		doExecutorTest(Executors.newFixedThreadPool(5));
	}

	/**
	 * @testStrategy Test accessing async web services using a
	 *               ScheduledThreadPoolExecutor available from Executors object
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test accessing async web services using a ScheduledThreadPoolExecutor available from Executors object",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_setExecutor_ScheduledThreadPool() throws Exception  {
		doExecutorTest(Executors.newScheduledThreadPool(THREAD_POOL_SZ));
	}

	/**
	 * @testStrategy Test accessing async web services using a
	 *               SingleThreadPoolExecutor available from Executors object
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test accessing async web services using a SingleThreadPoolExecutor available from Executors object",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_setExecutor_SingleThreadPool() throws Exception {
		doExecutorTest(Executors.newSingleThreadExecutor());
	}

	/**
	 * Executors test, shared by all testExecutors_executor methods
	 */
	private void doExecutorTest(Executor ex) throws Exception {
		final String MESSAGE = getName();

		AsyncPort port = AsyncClient.getPort(ex);

		Response<?>[] response = new Response[MAX_ASYNCREQUESTS];
		Exception exception = null;
		
		// queue up a few requests
		System.out.println("doExecutorTest.sleep");
		
		response[0] = port.sleepAsync(MESSAGE);
		AsyncClient.isAsleepCheck(MESSAGE, port);
		for (int i = 1; i < MAX_ASYNCREQUESTS; i++) {
			// alternate requests
			if (i % 2 == 1){
				response[i] = port.invokeAsyncAsync(MESSAGE + "_" + i);
				System.out.println("doExecutorTest.invokeAsyncAsync " + i);
			}else{
				response[i] = port.anotherAsyncAsync(MESSAGE + "_" + i);
				System.out.println("doExecutorTest.anotherAsyncAsync " + i);
			}
		}

		// recover from the first invocation
		System.out.println("doExecutorTest.wakeup");
		
		String wkup= port.wakeUp();
		
		System.out.println("doExecutorTest.wakeup=" + wkup);
		
		try {

			// retrieve responses one by one as they becoem available
			for (int i = 0; i < MAX_ASYNCREQUESTS; i++) {
				System.out.println("doExecutorTest.get[" + i + "]");
				response[i].get();
			}

		} catch (Exception e) {
			// log the exception and fail the test
			System.out.println(MESSAGE);
			e.printStackTrace();
			exception = e;
		}

		if (exception != null) throw exception;
		
	}

	/**
	 * @testStrategy Test accessing swapping executors while several tests are
	 *               executing. Expecting that the request will finish executing
	 *               on the original executor
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test accessing swapping executors while several tests are executing. Expecting that the request will finish executing on the original executor",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testExecutorService_swapExecutor1() throws Exception {
		final String MESSAGE = getName();

		AsyncService service = new AsyncService();
		AsyncPort port = service.getAsyncPort();

		Executor ex = Executors.newSingleThreadExecutor();
		service.setExecutor(ex);

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.DOCLITWR_ASYNC_ENDPOINT);		
		
		// request for the endpoint to block...then make sure it
		// receives the request before continuing
		Response<SleepResponse> sr = port.sleepAsync(MESSAGE);
		AsyncClient.isAsleepCheck(MESSAGE, port);

		// switch executors while the request is still executing
		ex = Executors.newFixedThreadPool(5);
		service.setExecutor(ex);

		rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.DOCLITWR_ASYNC_ENDPOINT);		
		
		// ask the endpoint to wake up...hopefuly the connection is still alive
		port.wakeUp();

		try {
			// retrieve the response
			SleepResponse resp = sr.get();
			assertTrue("Unexpected message returned", MESSAGE.equals(resp
					.getMessage()));

		} catch (Exception e) {
			fail("Unexpected: " + e);
		}
	}

	/**
	 * @testStrategy Test accessing swapping executors while several tests are
	 *               executing and several waiting to be executed. The ones
	 *               waiting should execute on the new executor
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test accessing swapping executors while several tests are executing and several waiting to be executed. The ones waiting should execute on the new executor",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testExecutorService_swapExecutor2() throws Exception {
		final String MESSAGE = getName();

		AsyncService service = new AsyncService();
		AsyncPort port = service.getAsyncPort();

		Executor ex = Executors.newSingleThreadExecutor();
		service.setExecutor(ex);

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.DOCLITWR_ASYNC_ENDPOINT);			
		
		// request for the endpoint to block...then make sure it
		// receives the request before continuing
		Response<SleepResponse> sr = port.sleepAsync(MESSAGE);

		// queue up a few more requests
		Response<AnotherResponse>[] ar = new Response[5];
		for (int i = 0; i < 5; i++) {
			rc = ((BindingProvider) port).getRequestContext();
			rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					Constants.DOCLITWR_ASYNC_ENDPOINT);				
			
			ar[i] = port.anotherAsyncAsync(MESSAGE);
		}

		AsyncClient.isAsleepCheck(MESSAGE, port);

		// switch executors while the request is still executing
		ex = Executors.newFixedThreadPool(5);
		service.setExecutor(ex);

		// ask the endpoint to wake up...hopefuly the connection is still alive
		rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.DOCLITWR_ASYNC_ENDPOINT);	
		
		port.wakeUp();

		try {
			// retrieve the response
			SleepResponse resp = sr.get();
			assertTrue("Unexpected message returned", MESSAGE.equals(resp
					.getMessage()));

			// ensure the remaining requests complete
			for (int i = 0; i < 5; i++) {
				ar[i].get();
			}

		} catch (Exception e) {
			fail("Unexpected: " + e);
		}
	}

	/**
	 * @testStrategy Test for ordering an executor to shutdown while there
	 *               are tests executing
	 */
	/*public void testExecutorService_shutdown() throws Exception {
		final String MESSAGE = getName();

		// create service & port
		AsyncService service = new AsyncService();
		AsyncPort port = service.getAsyncPort();

		// set endpoint location
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.DOCLITWR_ASYNC_ENDPOINT);		
		
		// set singleThreadExecutoor so that only 1 request is active at a time
		ExecutorService ex = Executors.newSingleThreadExecutor();
		service.setExecutor(ex);

		// approximate long running transaction by asking endpoint
		// to block until wakeUp call is made
		CallbackHandler<SleepResponse> sleepHandler = new CallbackHandler<SleepResponse>();
		Future<?> sr = port.sleepAsync(MESSAGE, sleepHandler);
		
		// order executor to shutdown
		ex.shutdown();

		try {
			// try to submit a new task. This request should fail since
			// executor is shutdown
			CallbackHandler<InvokeAsyncResponse> asyncHandler = new CallbackHandler<InvokeAsyncResponse>();
			port.invokeAsyncAsync(MESSAGE, asyncHandler);
			
			assertTrue("EE->ExecutionRejectedException OR Future.isCanceleld expected", sr.isCancelled());
		} catch (WebServiceException wse) {
			port.wakeUp(); // order endpoint to unblock
			
			Constants.logStack(wse);
			assertTrue("Expecting WSE->RejectedExecutionException",
						Constants.checkStack(wse, RejectedExecutionException.class));
		}
	}//*/

	/**
	 * @testStrategy Test for ordering an executor to shutdownNow while there
	 *               are tests executing. Hopefully this will put the engine
	 *               in an inconsistent state. We will then set a new executor
	 *               and send another request through
	 */
	/*public void testExecutorService_shutdownNow() throws Exception {
		final String MESSAGE = getName();

		// create service & port
		AsyncService service = new AsyncService();
		AsyncPort port = service.getAsyncPort();

		// set endpoint location
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.DOCLITWR_ASYNC_ENDPOINT);		
		
		// set singleThreadExecutoor so that only 1 request is active at a time
		ExecutorService ex = Executors.newSingleThreadExecutor();
		service.setExecutor(ex);

		// approximate long running transaction by asking endpoint
		// to block until wakeUp call is made
		CallbackHandler<SleepResponse> sleepHandler = new CallbackHandler<SleepResponse>();
		Future<?> sr = port.sleepAsync(MESSAGE, sleepHandler);
		
		// order executor to shutdown immediately
		// this will force the executing task to fail as well
		ex.shutdownNow();

		try {
			port.wakeUp();
			sleepHandler.get(); // should throw EE->WSE->InterruptedExecutionException
			
			assertTrue("EE->ExecutionRejectedException OR Future.isCanceleld expected", sr.isCancelled());
		} catch (ExecutionException ee) {
			port.wakeUp(); // order endpoint to unblock
			
			Constants.logStack(ee);
			assertTrue("Expecting EE->WSE", ee.getCause() instanceof WebServiceException);
			assertTrue("Expecting EE->WSE->InterruptedException", Constants.checkStack(ee, InterruptedException.class));
		}
		
		port.wakeUp();
	}//*/

}
