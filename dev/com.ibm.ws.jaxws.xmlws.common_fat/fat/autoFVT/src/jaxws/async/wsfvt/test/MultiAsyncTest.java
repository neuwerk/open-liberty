//
// @(#) 1.5 autoFVT/src/jaxws/async/wsfvt/test/MultiAsyncTest.java, WAS.websvcs.fvt, WASX.FVT 5/11/07 15:22:07 [7/11/07 13:14:38]
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
// 11/17/06 sedov       D406253         New File
// 12/01/06 sedov       D408880         Added mismatch test
// 05/11/07 sedov       D438719         Added more trace
//

package jaxws.async.wsfvt.test;

import java.util.concurrent.Future;

import javax.xml.ws.Response;

import jaxws.async.wsfvt.common.CallbackHandler;
import jaxws.async.wsfvt.common.doclitwr.AnotherResponse;
import jaxws.async.wsfvt.common.doclitwr.CustomAsyncResponse;
import jaxws.async.wsfvt.common.doclitwr.SleepResponse;
import jaxws.async.wsfvt.common.doclitwr.client.AsyncPort;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Similar to multithreading tests in other packages, but tests for multiple
 * concurrent async requests to different operations to see if a mismatch
 * condition can be generated
 */
public class MultiAsyncTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private static final int ITERATIONS_MAX = 10;
	
	public MultiAsyncTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(MultiAsyncTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy A case where the request/response wrappers are identical -
	 *               with just1 parameter that would normally be mapped to a
	 *               holder in sync case. For some reason this tends t return the
	 *               parameter rather than the wrapper
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="A case where the request/response wrappers are identical - with just1 parameter that would normally be mapped to a holder in sync case. For some reason this tends t return the parameter rather than the wrapper",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMultipleAsyncRequests_polling_mismatch() throws Exception {
		AsyncPort port = AsyncClient.getPort(null);

		for (int i = 0; i < ITERATIONS_MAX; i++) {
			System.out.println("Iteration " + i );
			String req1 = "sleepAsync";
			String req2 = "remappedAsync";
			
			//Constants.setSoapAction(port, "sleep");
			Response<SleepResponse> resp1 = port.sleepAsync(req1);
			
			//Constants.setSoapAction(port, "customAsync");
			Response<CustomAsyncResponse> resp2 = port.remappedAsync(req2);

			AsyncClient.waitBlocking(resp2);
			port.wakeUp();
			AsyncClient.waitBlocking(resp1);

			assertEquals("anotherAsyncAsync did not return expected response ",
					req1, resp1.get().getMessage());
			assertEquals("remappedAsync did not return expected response",
					req2, resp2.get().getResponse());
		}
	}

	/**
	 * @testStrategy Tests for multiple concurrent async requests to different
	 *               operations to see if a mismatch condition can be generated
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Tests for multiple concurrent async requests to different operations to see if a mismatch condition can be generated",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMultipleAsyncRequests_polling() throws Exception {
		AsyncPort port = AsyncClient.getPort(null);

		String req1 = "anotherAsyncAsync";
		String req2 = "remappedAsync";

		for (int i = 0; i < ITERATIONS_MAX; i++) {
			System.out.println("Iteration " + i );
			
			Response<AnotherResponse> resp1 = port.anotherAsyncAsync(req1);
			Response<CustomAsyncResponse> resp2 = port.remappedAsync(req2);

			AsyncClient.waitBlocking(resp1);
			AsyncClient.waitBlocking(resp2);

			assertEquals("anotherAsyncAsync did not return expected response ",
					req1, resp1.get().getResponse());
			assertEquals("remappedAsync did not return expected response",
					req2, resp2.get().getResponse());
		}
	}

	/**
	 * @testStrategy Tests for multiple concurrent async requests to different
	 *               operations to see if a mismatch condition can be generated
	 *               CustomAsync blocks for 10 seconds, so that second invoke
	 *               suceeds first
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Tests for multiple concurrent async requests to different operations to see if a mismatch condition can be generated CustomAsync blocks for 10 seconds, so that second invoke suceeds first",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMultipleAsyncRequests_callback() throws Exception {
		AsyncPort port = AsyncClient.getPort(null);

		String req1 = "anotherAsyncAsync";
		String req2 = "remappedAsync";

		for (int i = 0; i < ITERATIONS_MAX; i++) {
			System.out.println("Iteration " + i );
			
			CallbackHandler<AnotherResponse> cb1 = new CallbackHandler<AnotherResponse>(req1);
			CallbackHandler<CustomAsyncResponse> cb2 = new CallbackHandler<CustomAsyncResponse>(req2);

			Future<?> resp1 = port.anotherAsyncAsync(req1, cb1);
			Future<?> resp2 = port.remappedAsync(req2, cb2);

			AsyncClient.waitBlocking(resp1);
			AsyncClient.waitBlocking(resp2);

			assertEquals("remappedAsync did not return expected response",
					req2, cb2.get().getResponse());
			assertEquals("anotherAsyncAsync did not return expected response ",
					req1, cb1.get().getResponse());
		}
	}

}
