//
// @(#) 1.3.1.6 autoFVT/src/jaxws/dispatch/wsfvt/test/ThreadingTest.java, WAS.websvcs.fvt, WASX.FVT 6/5/07 10:14:50 [7/11/07 13:15:21]
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
// 04/30/07 sedov    435450             Executor is reused to prevent thread-thrashing
// 05/25/07 jramos   440922             Integrate ACUTE
//
package jaxws.dispatch.wsfvt.test;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

import java.io.IOException;

import jaxws.dispatch.wsfvt.common.CallbackHandler;
import jaxws.dispatch.wsfvt.common.Constants;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Test cases for thread-safety of Dispatch
 */
public class ThreadingTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private static int NUM_ITERATIONS_PER_THREAD = 25;

	private static int NUM_THREADS = 10;

	public enum TestModeEnum {
		SYNC, ASYNC_POLL, ASYNC_CALLBACK
	}

	private int successCount = 0; // how many times a roundtrip was succesful

	private Throwable error = null; // the error that was detected
	
	private Vector<Throwable> brokenpipeErrors=new Vector<Throwable>();   // how many broken pipe occur
	
	private ExecutorService exec = null;
	
	public ThreadingTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(ThreadingTest.class);
		
		//String osName = System.getProperty("os.name");
		//if (osName.indexOf("OS/400") != -1 || osName.indexOf("z/OS") != -1){
		//if (Cell.getDefaultCell().isSecurityEnabled()){
			NUM_ITERATIONS_PER_THREAD = 5;
			NUM_THREADS = 5;
		//}
		
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
		successCount = 0; // reset success count
		error = null;
		brokenpipeErrors.clear(); 
		
		this.exec = Executors.newFixedThreadPool(NUM_THREADS);
	}

	
	public void tearDown(){
		this.exec.shutdownNow();
	}
	/**
	 * @testStrategy Multithreading test for issues with having many dispatches
	 *               created from the same Service. Each thread has its own
	 *               instance of dispatch, invoke (synchronous) method is used
	 * 
	 * WSDL: DispatchSOAP11.wsdl
	 * Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test for issues with having many dispatches created from the same Service. Each thread has its own instance of dispatch, invoke (synchronous) method is used  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testThreading_sync_noSharing() throws Throwable {

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {

			// Each thread will create its own service and dispatch
			threads[i] = new Thread(new TesterThread(i, TestModeEnum.SYNC));
			threads[i].start();
		}
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {
			}
		}
		System.out.println(getName() + ": " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");
		if (error != null) {
			throw error;
		}
		
		checkBrokenpipeException();
	}	
	
	/**
	 * @testStrategy Multithreading test for issues with having many dispatches
	 *               created from the same Service. Each thread has its own
	 *               instance of dispatch, invoke (synchronous) method is used
	 * 
	 * WSDL: DispatchSOAP11.wsdl
	 * Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test for issues with having many dispatches created from the same Service. Each thread has its own instance of dispatch, invoke (synchronous) method is used  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testThreading_sync_sharedService() throws Throwable {

		Service service = getService();

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {

			// create a new instance of dispatch per thread
			Dispatch<Source> dispatch;
			dispatch = service.createDispatch(Constants.PORT_QNAME,
					Source.class, Service.Mode.PAYLOAD);

			threads[i] = new Thread(new TesterThread(i, dispatch,
					TestModeEnum.SYNC));
			threads[i].start();
		}
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {
			}
		}
		System.out.println(getName() + ": " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");
		if (error != null) {
			throw error;
		}
		
		checkBrokenpipeException();
	}

	/**
	 * @testStrategy Multithreading test for issues with having many dispatches
	 *               created from the same Service. Each thread shares an
	 *               instance of dispatch, invoke (synchronous) method is used
	 * 
	 * WSDL: DispatchSOAP11.wsdl
	 * Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test for issues with having many dispatches created from the same Service. Each thread shares an instance of dispatch, invoke (synchronous) method is used  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testThreading_sync_sharedDispatch() throws Throwable {

		Service service = getService();
		Dispatch<Source> dispatch;
		dispatch = service.createDispatch(Constants.PORT_QNAME, Source.class,
				Service.Mode.PAYLOAD);

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {
			threads[i] = new Thread(new TesterThread(i, dispatch,
					TestModeEnum.SYNC));
			threads[i].start();
		}
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {
			}
		}
		System.out.println(getName() + ": " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");
		if (error != null) {
			throw error;
		}
		
		checkBrokenpipeException();
	}

	/**
	 * @testStrategy Multithreading test for issues with having many dispatches
	 *               created from the same Service. Each thread has its own
	 *               instance of dispatch, invokeAsync (polling) method is used
	 * 
	 * WSDL: DispatchSOAP11.wsdl
	 * Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test for issues with having many dispatches created from the same Service. Each thread has its own instance of dispatch, invokeAsync (polling) method is used  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testThreading_asyncPolling_noSharing() throws Throwable {
		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {

			// each thread creates its own dispatch & service
			threads[i] = new Thread(new TesterThread(i, TestModeEnum.ASYNC_POLL));
			threads[i].start();
		}
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {
			}
		}
		System.out.println(getName() + ": " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");
		if (error != null) {
			throw error;
		}
		
		checkBrokenpipeException();
	}	
	
	/**
	 * @testStrategy Multithreading test for issues with having many dispatches
	 *               created from the same Service. Each thread has its own
	 *               instance of dispatch, invokeAsync (polling) method is used
	 * 
	 * WSDL: DispatchSOAP11.wsdl
	 * Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test for issues with having many dispatches created from the same Service. Each thread has its own instance of dispatch, invokeAsync (polling) method is used  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testThreading_asyncPolling_sharedService() throws Throwable {

		Service service = getService();

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {

			// create a new instance of dispatch per thread
			Dispatch<Source> dispatch;
			dispatch = service.createDispatch(Constants.PORT_QNAME,
					Source.class, Service.Mode.PAYLOAD);

			threads[i] = new Thread(new TesterThread(i, dispatch,
					TestModeEnum.ASYNC_POLL));
			threads[i].start();
		}
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {
			}
		}
		System.out.println(getName() + ": " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");
		if (error != null) {
			throw error;
		}
		
		checkBrokenpipeException();
	}

	/**
	 * @testStrategy Multithreading test for issues with having many dispatches
	 *               created from the same Service. Each thread shares an
	 *               instance of dispatch, invokeAsync (polling) method is used
	 * 
	 * WSDL: DispatchSOAP11.wsdl
	 * Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test for issues with having many dispatches created from the same Service. Each thread shares an instance of dispatch, invokeAsync (polling) method is used  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testThreading_asyncPolling_sharedDispatch() throws Throwable {

		Service service = getService();
		Dispatch<Source> dispatch;
		dispatch = service.createDispatch(Constants.PORT_QNAME, Source.class,
				Service.Mode.PAYLOAD);

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {
			threads[i] = new Thread(new TesterThread(i, dispatch,
					TestModeEnum.ASYNC_POLL));
			threads[i].start();
		}
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {
			}
		}
		System.out.println(getName() + ": " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");
		if (error != null) {
			throw error;
		}
		
		checkBrokenpipeException();
	}

	/**
	 * @testStrategy Multithreading test for issues with having many dispatches
	 *               created from the same Service. Each thread has its own
	 *               instance of dispatch, invokeAsync (callback) method is used
	 * 
	 * WSDL: DispatchSOAP11.wsdl
	 * Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test for issues with having many dispatches created from the same Service. Each thread has its own instance of dispatch, invokeAsync (callback) method is used  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testThreading_asyncCallback_noSharing() throws Throwable {

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {

			// each thread creates itws ownn dispatch & service
			threads[i] = new Thread(new TesterThread(i, TestModeEnum.ASYNC_CALLBACK));
			threads[i].start();
		}
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {
			}
		}
		System.out.println(getName() + ": " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");
		if (error != null) {
			throw error;
		}
		
		checkBrokenpipeException();
	}	
	
	/**
	 * @testStrategy Multithreading test for issues with having many dispatches
	 *               created from the same Service. Each thread has its own
	 *               instance of dispatch, invokeAsync (callback) method is used
	 * 
	 * WSDL: DispatchSOAP11.wsdl
	 * Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test for issues with having many dispatches created from the same Service. Each thread has its own instance of dispatch, invokeAsync (callback) method is used  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testThreading_asyncCallback_sharedService() throws Throwable {

		Service service = getService();

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {

			// create a new instance of dispatch per thread
			Dispatch<Source> dispatch;
			dispatch = service.createDispatch(Constants.PORT_QNAME,
					Source.class, Service.Mode.PAYLOAD);

			threads[i] = new Thread(new TesterThread(i, dispatch,
					TestModeEnum.ASYNC_CALLBACK));
			threads[i].start();
		}
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {
			}
		}
		System.out.println(getName() + ": " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");
		if (error != null) {
			throw error;
		}
		
		checkBrokenpipeException();
	}

	/**
	 * @testStrategy Multithreading test for issues with having many dispatches
	 *               created from the same Service. Each thread shares an
	 *               instance of dispatch, invokeAsync (callback) method is used
	 * 
	 * WSDL: DispatchSOAP11.wsdl
	 * Target: SOAP11DispatchPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Multithreading test for issues with having many dispatches created from the same Service. Each thread shares an instance of dispatch, invokeAsync (callback) method is used  WSDL: DispatchSOAP11.wsdl Target: SOAP11DispatchPortImpl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testThreading_asyncCallback_sharedDispatch() throws Throwable {

		Service service = getService();
		Dispatch<Source> dispatch;
		dispatch = service.createDispatch(Constants.PORT_QNAME, Source.class,
				Service.Mode.PAYLOAD);

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; ++i) {
			threads[i] = new Thread(new TesterThread(i, dispatch,
					TestModeEnum.ASYNC_CALLBACK));
			threads[i].start();
		}
		for (int i = 0; i < NUM_THREADS; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {
			}
		}
		System.out.println(getName() + ": " + successCount
				+ " successes (of a possible "
				+ (NUM_THREADS * NUM_ITERATIONS_PER_THREAD) + ")");
		if (error != null) {
			throw error;
		}
		
		checkBrokenpipeException();
	}

	/**
	 * ThreadTest class is whats used to execute the tests
	 */
	public class TesterThread implements Runnable {

		private int thread_id;

		private Dispatch<Source> dispatch;

		private TestModeEnum mode;

		public TesterThread(int threadId, Dispatch<Source> dispatch,
				TestModeEnum mode) {
			this.thread_id = threadId;
			this.dispatch = dispatch;
			this.mode = mode;
		}
		
		public TesterThread(int threadId, TestModeEnum mode) {
			this.thread_id = threadId;
			this.dispatch = null;
			this.mode = mode;
		}		

		public void run() {
			// actual test iterations completed by this thread
			int iterations = 0;
			
			try {
				// create a dispatch if it wasn't provided
				if (this.dispatch == null){
					this.dispatch = getService().createDispatch(Constants.PORT_QNAME, Source.class, Service.Mode.PAYLOAD);
				}
				
				for (int i = 0; i < NUM_ITERATIONS_PER_THREAD; ++i) {
					String expect = getName() + "_" + thread_id + "_msg_" + i;
					String msg = Constants.TWOWAY_MSG.replace("test_message",
							expect);

					Source src = Constants.toStreamSource(msg);

					Source rpl = null;
					switch (mode) {
					case SYNC:
						rpl = doSync(src, dispatch);
						break;

					case ASYNC_POLL:
						rpl = doAsyncPoll(src, dispatch);
						break;

					case ASYNC_CALLBACK:
						rpl = doAsyncCallback(src, dispatch);
					}

					String reply = Constants.toString(rpl);

					// make sure the correct message came back
					if (reply.indexOf(expect) == -1) {
						throw new Exception(
								"Unexpected response received, expecting '"
										+ expect + "' got '" + reply + "'");
					} else {
						addSuccess();
					}
					
					iterations++;
				} // for
				
			} catch (Throwable t) {
				
				//(ALEX)defect 132271: ignore "Broken pipe" as operating system issue, and record the exception.
				if (t instanceof IOException && t.toString().indexOf("Broken pipe")!=-1) {
					brokenpipeErrors.add(t);
				}
				
				// all exceptions must be WSE's, however if its not then its
				// definitely of interest				
				boolean isWSE = t instanceof WebServiceException;
				Throwable cause = t.getCause();
				if ((isWSE && 
					!(t.toString().indexOf("java.net.ConnectException") != -1 || 
					t.toString().indexOf("java.net.SocketException") != -1))
					|| (!isWSE && 
					!(t instanceof IOException && t.toString().indexOf("Broken pipe")!=-1))) {
					setError(t);
				}
				
				
			}
			System.out.println("Thread " + this.thread_id + " completed " + iterations + " iterations");
		} // run

		private Source doAsyncPoll(Source message, Dispatch<Source> port)
				throws Exception {

			// incoke and wait for response
			CallbackHandler<Source> handler = new CallbackHandler<Source>();
			Future<Source> monitor = port.invokeAsync(message);
			
			handler.waitBlocking(monitor);
			Source reply = monitor.get();
			
			return reply;
		}

		private Source doAsyncCallback(Source message, Dispatch<Source> port)
				throws Exception {
			CallbackHandler<Source> handler = new CallbackHandler<Source>();

			// invoke and wait for response
			Future<?> monitor = port.invokeAsync(message, handler);
			handler.waitBlocking(monitor);

			return handler.get();
		}

		private Source doSync(Source message, Dispatch<Source> port)
				throws Exception {

			Source reply = port.invoke(message);

			return reply;
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
		successCount++;
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
	
	private void checkBrokenpipeException() throws IOException {
		if (brokenpipeErrors.size()>1) {
			Throwable t=brokenpipeErrors.get(0);
			throw new IOException(t);
		}
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Service getService() {
		System.out.println("getService using executor: " + this.exec);
		
		Service service = Service.create(new QName("http://example.com", getName()));
		service.setExecutor(this.exec);
		
		service.addPort(Constants.PORT_QNAME, SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		return service;
	}
}
