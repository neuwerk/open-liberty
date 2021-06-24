//
// @(#) 1.3 autoFVT/src/jaxws/proxy/rpclit/wsfvt/test/RpcLitTest.java, WAS.websvcs.fvt, WASX.FVT 1/19/07 11:44:39 [7/11/07 13:16:35]
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
// 12/19/06 sedov       409973          Removed async test, see#412298
// 01/19/07 sedov       415799          Removed null return test
//
package jaxws.proxy.rpclit.wsfvt.test;

import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

import jaxws.proxy.common.Constants;
import jaxws.proxy.rpclit.wsfvt.rpclit.*;
import jaxws.proxy.rpclit.wsfvt.soap12rpclit.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for RPC/Literal style
 * 
 * @endpoint ProxyRpcLitPortImpl
 * @wsdl proxy_rpclit.wsdl
 */
public class RpcLitTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public RpcLitTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(RpcLitTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy Test for invoking operations in "void x()" style
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for invoking operations in \"void x()\" style",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLitOneWayEmpty() {
		RpcLitProxy port = getPort();

		port.oneWayEmpty();
	}

	/**
	 * @testStrategy Test for operations in "void x(param)" style pass in a null
	 *               value
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for operations in \"void x(param)\" style pass in a null value",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLitNullOneWay() {
		RpcLitProxy port = getPort();

		try {
			port.oneWay(null);
			fail("invoking a rpc-lit operation with a null requires a WSE");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test for operations in "void x(param)" style pass in an
	 *               empty value
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for operations in \"void x(param)\" style pass in an empty value",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLitOneWay2() {
		RpcLitProxy port = getPort();

		port.oneWay("");
	}

	/**
	 * @testStrategy Test for operations in "void x(param)" style
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for operations in \"void x(param)\" style",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLitOneWay3() {
		RpcLitProxy port = getPort();

		port.oneWay(Constants.THE_STRING);
	}

	/**
	 * @testStrategy Test for operations in "param x(in param, in param)" style
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for operations in \"param x(in param, in param)\" style",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLitSimpleReturn() {
		RpcLitProxy port = getPort();

		String ret = port.twoWay(Constants.THE_STRING, Constants.THE_INT);
		assertTrue("Returned value is unexpected", ret
				.equals(Constants.THE_STRING));
	}

	/**
	 * @testStrategy Test for operations in "param x()" style
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for operations in \"param x()\" style",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLitOutOnly() {
		RpcLitProxy port = getPort();

		String ret = port.twoWaySimple();
		assertTrue("Returned value is unexpected", ret
				.equals(Constants.THE_STRING));
	}

	/**
	 * @testStrategy Test for operations in "void x(inout a, inout b)" style
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for operations in \"void x(inout a, inout b)\" style",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLitInOut() throws SimpleFault {
		RpcLitProxy port = getPort();

		Holder<String> inout_str = new Holder<String>(Constants.THE_STRING);
		Holder<Integer> inout_int = new Holder<Integer>(Constants.THE_INT);

		port.twoWayInOut(inout_str, inout_int);

		assertTrue("Returned value is unexpected", inout_str.value
				.equals(Constants.THE_STRING));
		assertTrue("Returned value is unexpected", inout_int.value
				.compareTo(Constants.THE_INT) == 0);
	}

	/**
	 * @testStrategy Test for operations in "void x(inout a, inout b)" style
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for operations in \"void x(inout a, inout b)\" style",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLitFault() {
		RpcLitProxy port = getPort();

		Holder<String> inout_str = new Holder<String>(
				Constants.THE_FAULT_STRING);
		Holder<Integer> inout_int = new Holder<Integer>(Constants.THE_INT);

		try {
			port.twoWayInOut(inout_str, inout_int);
			fail("Fault Expected");
		} catch (SimpleFault sf) {
			String fault = sf.getFaultInfo();
			assertTrue("Returned value is unexpected", fault
					.equals(Constants.THE_FAULT_STRING));
		}
	}
	
	/**
	 * @testStrategy Test for two-way operations in "void x(void)" style
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for two-way operations in \"void x(void)\" style",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLitEmptyTwoWay() {
		RpcLitProxy port = getPort();

		port.twoWayVoid();
	}	

	/**
	 * @testStrategy Test for invoking an rpc-literal operation with a null.
	 *               Conformance point 3.22 requires a WSE
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for invoking an rpc-literal operation with a null. Conformance point 3.22 requires a WSE",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLitNullInvoke() {
		RpcLitProxy port = getPort();

		try {
			port.twoWay(null, 5);
			fail("Conformance Point 5.22, WebServiceException required when RpcLit op is invoked with a null");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test for invoking an rpc-literal operation with a null
	 *               Holder. This is invalid and should never dispatch
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for invoking an rpc-literal operation with a null Holder. This is invalid and should never dispatch",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLitNullHolder() throws Exception {
		RpcLitProxy port = getPort();

		try {
			port.twoWayInOut(null, new Holder<Integer>(Constants.THE_INT));
			fail("WebServiceException expected when invoked with a null Holder");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);

		}
	}

	/**
	 * @testStrategy Test for operations in "param x()" style
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for operations in \"param x()\" style",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLitPolymorphism() {
		RpcLitProxy port = getPort();

		FinancialOperation finop = new FinancialOperation();

		// invoke witha negative amount to get back a Withdraw
		finop.setAmount(-15);
		FinancialOperation ret1 = port.finop(finop);
		assertTrue("Polymorphic request did not return expected subtype",
				ret1 instanceof Withdraw);

		// invoke with a positive amount to get back a Deposit
		finop.setAmount(10);
		FinancialOperation ret2 = port.finop(finop);
		assertTrue("Polymorphic request did not return expected subtype",
				ret2 instanceof Deposit);
	}

	/**
	 * @testStrategy Test for operations in "param x()" style
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for operations in \"param x()\" style",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testRpcLit_soap12() {
		ProxySOAP12Rpc port = (new ProxySOAP12RpcLitService()).getProxySOAP12RpcLitPort();

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.RPCLIT_BASE + "/services/ProxySOAP12RpcLitService");
		
		Message req1 = new Message();
		req1.setMessage(Constants.THE_STRING);
		
		Message req2 = new Message();
		req2.setMessage(Constants.THE_STRING);
		
		Message resp = port.ping(req1, req2);
		
		assertEquals("Messages do not equal", Constants.THE_STRING, resp.getMessage());
	}	
	
	/**
	 * Auxiliary method used for obtaining the port
	 * 
	 * @return
	 */
	private RpcLitProxy getPort() {
		RpcLitProxy port = (new ProxyRpcLitService()).getProxyRpcLitPort();
		assertNotNull("Port is null", port);

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.RPCLIT_BASE + "/services/ProxyRpcLitService");

		return port;
	}
}
