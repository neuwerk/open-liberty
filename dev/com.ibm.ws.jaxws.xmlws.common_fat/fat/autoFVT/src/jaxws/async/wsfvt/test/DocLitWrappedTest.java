//
// @(#) 1.5 autoFVT/src/jaxws/async/wsfvt/test/DocLitWrappedTest.java, WAS.websvcs.fvt, WASX.FVT 2/6/07 18:25:43 [7/11/07 13:14:37]
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
import javax.xml.ws.Response;

import jaxws.async.wsfvt.common.*;
import jaxws.async.wsfvt.common.doclitwr.PingResponse;
import jaxws.async.wsfvt.common.doclitwr.client.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for asyncronous operations in DocLit/Wrapped style
 */
public class DocLitWrappedTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public DocLitWrappedTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(DocLitWrappedTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy basic two-way synchronous invoke test to verify that
	 *               service is available
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="basic two-way synchronous invoke test to verify that service is available",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAsync_invoke() {
		AsyncPort port = getPort();

		String actual = port.ping(getName());

		assertEquals("Unxpected Response", getName(), actual);
	}

	/**
	 * @testStrategy DOC/LIT WRAPPED invokeAsync-polling request
	 */	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="DOC/LIT WRAPPED invokeAsync-polling request",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAsync_invokeAsyncPolling() throws Exception {
		AsyncPort port = getPort();

		Response<PingResponse> monitor = port.pingAsync(getName());
		
		String actual = monitor.get().getResponse();

		assertEquals("Unxpected Response", getName(), actual);
	}	
	
	/**
	 * @testStrategy DOC/LIT WRAPPED invokeAsync-callback request
	 */		
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="DOC/LIT WRAPPED invokeAsync-callback request",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAsync_invokeAsyncCallback() throws Exception {
		AsyncPort port = getPort();

		CallbackHandler<PingResponse> handler = new CallbackHandler<PingResponse>();
		Future<?> monitor = port.pingAsync(getName(), handler);

		// wait for response to come back
		AsyncClient.waitBlocking(monitor);
		String actual = handler.get().getResponse();

		// verify response
		assertEquals("Unxpected Response", getName(), actual);
	}

	private KillerThread killer = null;

	private AsyncPort getPort() {
		AsyncService service = new AsyncService();
		service.setExecutor(Executors.newSingleThreadExecutor());

		AsyncPort port = service.getAsyncPort();

		assertNotNull("Port is null", port);

		// stop the killer thread if it is active
		if (killer != null) killer.abort();

		// create a killer monitor thread that will make
		// sure to kill the executor after a while
		killer = new KillerThread(service, Constants.CLIENT_MAX_SLEEP_SEC);
		killer.start();

		// modify endpoint location...useful if testing from eclipse with TCPMON
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.DOCLITWR_ASYNC_ENDPOINT);
		
		return port;
	}
}
