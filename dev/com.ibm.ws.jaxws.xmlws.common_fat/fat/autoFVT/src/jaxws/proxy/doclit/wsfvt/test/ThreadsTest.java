//
// @(#) 1.11 autoFVT/src/jaxws/proxy/doclit/wsfvt/test/ThreadsTest.java, WAS.websvcs.fvt, WASX.FVT 6/5/07 14:10:33 [7/11/07 13:16:18]
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
// 09/15/06 sedov       390173          New File/changed to doc/lit-wrapped from bare
// 12/18/06 sedov       409973          Added executor.shutdownNow
// 04/19/07 sedov       433386          default creates service/proxy in its own thread
// 06/05/07 sedov       443236          Fixed ClassCastException on zOS
//

package jaxws.proxy.doclit.wsfvt.test;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import jaxws.proxy.common.CallbackHandler;
import jaxws.proxy.common.Constants;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.ReturnType;
import jaxws.proxy.doclit.wsfvt.async.*;

/**
 * Test case for multithreading in sync and async proxies
 * -default - will allow each client thread to create its own
 *            proxy instances, testing to see if the proxies
 *            are cached per-thread or per-jvm
 * -caching - will create a proxy-per-thread within the main thread
 *            from a common Service object
 * -sharing - will create a new Service/proxy-per-thread from the main
 *            thread
 */
public class ThreadsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	static final String DOCLIT_WRAPPED_ENDPOINT = Constants.DOCLIT_BASE + "/services/ProxyDocLitWrappedService";
	
	private static final int NUM_ITERATIONS_PER_THREAD = 10;

	private static final int NUM_THREADS = 10;

	public enum TestModeEnum {
		SYNC, ASYNC_POLL, ASYNC_CALLBACK
	}

	private int successCount = 0;
	
	private Throwable error = null;

	public ThreadsTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(ThreadsTest.class);
		return suite;
	}

	public void setUp() {

		System.out.println("==================== " + getName());

		successCount = 0; // reset success count
		error = null;
	}

	/**
	 * @testStrategy Multithreading test where many threads create proxies from
	 *               a common Service object. The service is assesed via
	 *               synchronous requests. This is assuming that proxies are
	 *               "cached"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test where many threads create proxies from a common Service object. The service is assesed via synchronous requests. This is assuming that proxies are \"cached\"",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMultithreading_Sync_SharedService() throws Throwable {

		ProxyDocLitWrappedService service = new ProxyDocLitWrappedService();

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {
			// create a new proxy pre thread
			DocLitWrappedProxy port = service.getProxyDocLitWrappedPort();

			patchEndpointAddress(port);

			threads[i] = new Thread(
					new TesterThread(i, port, TestModeEnum.SYNC));
			threads[i].start();
		}

		// wait for threads to complete
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {}
		}

		// shutdown executor
		if (service.getExecutor() instanceof ExecutorService){
			((ExecutorService)service.getExecutor()).shutdownNow();
		}
		
		// log outcome
		System.out.println(getName() + ": Had " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");

		// throw the exception if we got lucky
		if (error != null) { throw error; }
	}

	/**
	 * @testStrategy Multithreading test where many threads sharing a single
	 *               prox. The service is assesed via synchronous requests. This
	 *               is a test for thread safety of proxies.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test where many threads sharing a single prox. The service is assesed via synchronous requests. This is a test for thread safety of proxies.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMultithreading_Sync_SharedPort() throws Throwable {
		// create a shared proxy
		ProxyDocLitWrappedService service = new ProxyDocLitWrappedService();
		DocLitWrappedProxy port = service.getProxyDocLitWrappedPort();
		patchEndpointAddress(port);

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {
			threads[i] = new Thread(
					new TesterThread(i, port, TestModeEnum.SYNC));
			threads[i].start();
		}

		// wait for threads to finish executing
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {}
		}

		// shutdown executor
		if (service.getExecutor() instanceof ExecutorService){
			((ExecutorService)service.getExecutor()).shutdownNow();
		}	
		
		// log the outcome
		System.out.println(getName() + ": Had " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");

		// throw the exception if we got lucky
		if (error != null) { throw error; }
	}

	/**
	 * @testStrategy Multithreading test where each thread will have its own
	 *               instance of a service and proxy
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test where each thread will have its own instance of a service and proxy",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMultithreading_Sync() throws Throwable {

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {

			// the thread will create its own proxy instance
			threads[i] = new Thread(new TesterThread(i, null, TestModeEnum.SYNC));
			threads[i].start();
		}

		// wait for threads to finish executing
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {}
		}

		// log the outcome
		System.out.println(getName() + ": Had " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");

		// throw the exception if we got lucky
		if (error != null) { throw error; }
	}

	/**
	 * @testStrategy Multithreading test where many threads create proxies from
	 *               a common Service object. The service is assesed via
	 *               async-callback requests. This is assuming that proxies are
	 *               "cached"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test where many threads create proxies from a common Service object. The service is assesed via async-callback requests. This is assuming that proxies are \"cached\"",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMultithreading_AsyncCallback_SharedService() throws Throwable {
		ProxyDocLitWrappedService service = new ProxyDocLitWrappedService();

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {
			// create one proxy per thread
			DocLitWrappedProxy port = service.getProxyDocLitWrappedPort();
			patchEndpointAddress(port);

			threads[i] = new Thread(new TesterThread(i, port,
					TestModeEnum.ASYNC_CALLBACK));
			threads[i].start();
		}

		// wait for threads to finish
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {}
		}

		// shutdown executor
		if (service.getExecutor() instanceof ExecutorService){
			((ExecutorService)service.getExecutor()).shutdownNow();
		}
		
		// logoutcome
		System.out.println(getName() + ": Had " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");

		// report the exception if we got lucky
		if (error != null) { throw error; }
	}

	/**
	 * @testStrategy Multithreading test where many threads share a proxy. The
	 *               service is assesed via async-callback requests. This is a
	 *               test for thread safety of proxies.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test where many threads share a proxy. The service is assesed via async-callback requests. This is a test for thread safety of proxies.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMultithreading_AsyncCallback_SharedProxy() throws Throwable {
		// create a common proxy
		ProxyDocLitWrappedService service = new ProxyDocLitWrappedService();
		DocLitWrappedProxy port = service.getProxyDocLitWrappedPort();
		patchEndpointAddress(port);

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {
			threads[i] = new Thread(new TesterThread(i, port,
					TestModeEnum.ASYNC_CALLBACK));
			threads[i].start();
		}
		// wait for threads to finish
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {}
		}

		// shutdown executor
		if (service.getExecutor() instanceof ExecutorService){
			((ExecutorService)service.getExecutor()).shutdownNow();
		}
		
		// log outcome
		System.out.println(getName() + ": Had " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");

		// report exception if we got lucky
		if (error != null) { throw error; }
	}

	/**
	 * @testStrategy Multithreading test where each thread creates its own proxy
	 *               instance. The service is assesed via async-callback
	 *               requests. This is a test for thread safety of proxies.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test where each thread creates its own proxy instance. The service is assesed via async-callback requests. This is a test for thread safety of proxies.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMultithreading_AsyncCallback() throws Throwable {

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {
			// thread will create its own proxy
			threads[i] = new Thread(new TesterThread(i,
					null, TestModeEnum.ASYNC_CALLBACK));
			threads[i].start();
		}
		// wait for threads to finish
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {}
		}

		// log outcome
		System.out.println(getName() + ": Had " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");

		// report exception if we got lucky
		if (error != null) { throw error; }
	}

	/**
	 * @testStrategy Multithreading test where many threads create proxies from
	 *               a common Service object. The service is assesed via
	 *               async-polling requests. This is assuming that proxies are
	 *               "cached"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test where many threads create proxies from a common Service object. The service is assesed via async-polling requests. This is assuming that proxies are \"cached\"",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMultithreading_AsyncPolling_SharedService() throws Throwable {
		ProxyDocLitWrappedService service = new ProxyDocLitWrappedService();

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {
			// new proxy per thread
			DocLitWrappedProxy port = service.getProxyDocLitWrappedPort();
			patchEndpointAddress(port);

			threads[i] = new Thread(new TesterThread(i, port,
					TestModeEnum.ASYNC_POLL));
			threads[i].start();
		}

		// wait for threads to finish
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {}
		}

		// shutdown executor
		if (service.getExecutor() instanceof ExecutorService){
			((ExecutorService)service.getExecutor()).shutdownNow();
		}
		
		// log the outcome
		System.out.println(getName() + ": Had " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");

		// report the error if it was encountered
		if (error != null) { throw error; }
	}

	/**
	 * @testStrategy Multithreading test where many threads share a proxy. The
	 *               service is assesed via async-polling requests. This is a
	 *               test for thread safety of proxies
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test where many threads share a proxy. The service is assesed via async-polling requests. This is a test for thread safety of proxies",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMultithreading_AsyncPolling_SharedProxy() throws Throwable {
		ProxyDocLitWrappedService service = new ProxyDocLitWrappedService();
		DocLitWrappedProxy port = service.getProxyDocLitWrappedPort();
		patchEndpointAddress(port);

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {
			threads[i] = new Thread(new TesterThread(i, port,
					TestModeEnum.ASYNC_POLL));
			threads[i].start();
		}

		// wait for threads to finish
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {}
		}

		// shutdown executor
		if (service.getExecutor() instanceof ExecutorService){
			((ExecutorService)service.getExecutor()).shutdownNow();
		}	
		
		// log the outcome
		System.out.println(getName() + ": Had " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");

		// report the error
		if (error != null) { throw error; }
	}

	/**
	 * @testStrategy Multithreading test where each thread creates its own
	 *               proxy. The service is assesed via async-polling requests.
	 *               This is a test for thread safety of proxies
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test where each thread creates its own proxy. The service is assesed via async-polling requests. This is a test for thread safety of proxies",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMultithreading_AsyncPolling() throws Throwable {

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {
			// the thread will create its own proxy
			threads[i] = new Thread(new TesterThread(i, null, TestModeEnum.ASYNC_POLL));
			threads[i].start();
		}

		// wait for threads to finish
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {}
		}

		// log the outcome
		System.out.println(getName() + ": Had " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");

		// report the error
		if (error != null) { throw error; }
	}

	/**
	 * Force the ENDPOINT_ADDRESS property to be what I specified in the
	 * Constants.java, this is useful for testing with TCPMon
	 * @param port
	 */
	private void patchEndpointAddress(DocLitWrappedProxy port) {
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				DOCLIT_WRAPPED_ENDPOINT);
	}

	/**
	 * ThreadTest class is whats used to execute the testMultithreading_s. Makes
	 * sure that a unique message can be roundtripped
	 */
	private class TesterThread implements Runnable {

		private int thread_id;

		private DocLitWrappedProxy port;
		
		private ProxyDocLitWrappedService service = null;
		private TestModeEnum mode;

		/**
		 * This constructor should be used for the case where many threads will
		 * share ports
		 * 
		 * @param threadId
		 * @param mode
		 */
		public TesterThread(int threadId, DocLitWrappedProxy port,
				TestModeEnum mode) {
			this.thread_id = threadId;
			this.port = port;
			this.mode = mode;
		}

		public void run() {
			try {
				if (port == null){
					this.service = new ProxyDocLitWrappedService();
					DocLitWrappedProxy proxy = service.getProxyDocLitWrappedPort();
					patchEndpointAddress(proxy);

					this.port = proxy;					
				}
				
				for (int i = 0; i < NUM_ITERATIONS_PER_THREAD; ++i) {
					String expect = getName() + "_thread_" + thread_id
							+ "_msg_" + i;
					String reply = null;

					// do the actual invoke based on the specified mode
					switch (mode) {
					case SYNC:
						reply = doSync(expect, port);
						break;

					case ASYNC_POLL:
						reply = doAsyncPoll(expect, port);
						break;

					case ASYNC_CALLBACK:
						reply = doAsyncCallback(expect, port);
						break;
					}

					// make sure the correct message came back
					if (reply.indexOf(expect) == -1) {
						throw new Exception(
								"Unexpected response received, expecting '"
										+ expect + "' got '" + reply + "'");
					} else {
						addSuccess();
					}
					// } //synchronized
				} // for
				
			} catch (Throwable t) {

				// all exceptions must be WSE's, however if its not then its
				// definitely of interest
				boolean isWSE = t instanceof WebServiceException;
				// Throwable cause = t.getCause();
				if ((isWSE && !(t.toString().indexOf(
						"java.net.ConnectException") != -1 || t.toString()
						.indexOf("java.net.SocketException") != -1))
						|| !isWSE) {

					// log exception, just in case
					t.printStackTrace();

					// report it
					setError(t);
				}
			}
			
			// shutdown the executor
			if (this.service != null &&
					service.getExecutor() instanceof ExecutorService){
				((ExecutorService)service.getExecutor()).shutdownNow();
			}
			
		} // run

		// run a test with a async-polling invoke
		private String doAsyncPoll(String message, DocLitWrappedProxy port)
				throws Exception {

			Response<ReturnType> monitor = null;
			monitor = port.twoWayAsync(message);
			
			ReturnType rt = monitor.get();
			return rt.getReturnStr();
		}

		// run a test with a async callback invoke
		private String doAsyncCallback(String message, DocLitWrappedProxy port)
				throws Exception {
			CallbackHandler<ReturnType> handler = new CallbackHandler<ReturnType>();

			Future<?> monitor = port.twoWayAsync(message, handler);
			waitBlocking(monitor);

			ReturnType rt = handler.get();
			return rt.getReturnStr();
		}

		// run a test with a synchronos invoke
		private String doSync(String message, DocLitWrappedProxy port)
				throws Exception {
			String reply = port.twoWay(message);

			return reply;
		}

		/**
		 * Auxiliary method used to wait for a monitor for a certain amount of
		 * time before timing out
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
		
	} // class ThreadTest

	/**
	 * This method keeps track of the number of threads that were successful.
	 * Especially on non-server versions of Windows, sometimes with a large
	 * amount of threads there will be ConnectExceptions because the queue depth
	 * is not large enough. The server versions of Windows should not get this
	 * exception.
	 */
	private synchronized void addSuccess() {
		this.successCount++;
	}

	/**
	 * This method will hold an error (other than a ConnectException), which can
	 * later be thrown.
	 * 
	 * @param error
	 *            An Exception that occurred in a class outside the
	 *            MultithreadDocLitTestCase class
	 */
	private synchronized void setError(Throwable error) {
		if (this.error == null) {
			this.error = error;
		}
	}
}
