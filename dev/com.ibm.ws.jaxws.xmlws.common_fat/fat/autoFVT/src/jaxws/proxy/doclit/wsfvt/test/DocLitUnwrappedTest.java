//
// @(#) 1.1 WautoFVT/src/jaxws/proxy/wsfvt/test/DocLitUnwrappedTest.java, WAS.websvcs.fvt, WSFPB.WFVT 9/12/06 14:50:56 [9/12/06 14:56:54]
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
// 09/15/06 sedov       390173          removed non-Beta tests
//


package jaxws.proxy.doclit.wsfvt.test;

import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

import jaxws.proxy.common.Constants;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import jaxws.proxy.doclit.wsfvt.doclitunwrapped.DocLitUnwrappedProxy;
import jaxws.proxy.doclit.wsfvt.doclitunwrapped.ProxyDocLitUnwrappedService;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.ProxyDocLitWrappedService;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.ReturnType;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.TwoWay;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.TwoWayHolder;

/**
 * Tests to see if the engine can be confused by sending a wrapped parameters to
 * an unwrapped service and vice versa e.g., If wsdl defines a doc/lit-wrapped
 * operation op(Op p), and Op has x and y fields Then I am going to have a
 * client interface op(x, y) that sends to an endpoint that has wrapping
 * disabled - op(Op)
 * 
 * @endpoint ProxyDocLitWrappedPortImpl, ProxyDocLitUnwrappedPortImpl
 * @wsdl proxy_doclitwr.wsdl
 * @wsdl proxy_doclitwr.wsdl + proxy_unwrapped.xml
 */
public class DocLitUnwrappedTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {


	static final String DOCLIT_WRAPPED_ENDPOINT = Constants.DOCLIT_BASE + "/services/ProxyDocLitWrappedService";
	static final String DOCLIT_UNWRAPPED_ENDPOINT = Constants.DOCLIT_BASE + "/services/ProxyDocLitUnwrappedService";
	
	public DocLitUnwrappedTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(DocLitUnwrappedTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}	
	
	/**
	 * Invoke with a null wrapper, this should throw a WSE
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testNullWrapper() {

		try {
			getUnwrappedPort(DOCLIT_UNWRAPPED_ENDPOINT).twoWay(null);
			fail("WebServiceException expected when invoking with a null wrapper");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * Invoke with a null parameter in a wrapper, this param has a default so
	 * not setting it should work fine
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testNullParam_default() {

		try {
			TwoWayHolder twoWayHolder = new TwoWayHolder();
			// twoWayHolder.setTwoWayHolderStr(null); not set to force the
			// default
			twoWayHolder.setTwoWayHolderInt(Constants.THE_INT);

			getUnwrappedPort(DOCLIT_UNWRAPPED_ENDPOINT).twoWayHolder(
					new Holder<TwoWayHolder>(twoWayHolder));
			// fail("WebServiceException expected when invoking with a null
			// parameter");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * Send a message using an unwrapped interface to an endpoint that uses an
	 * unwrapped interface.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testUnwrappedToUnwrappedCall() {

		TwoWay twoWay = new TwoWay();
		twoWay.setTwowayStr(Constants.THE_ID_STRING);
		ReturnType rt = getUnwrappedPort(DOCLIT_UNWRAPPED_ENDPOINT)
				.twoWay(twoWay);

		System.out
				.println("testUnwrappedToUnwrappedCall: " + rt.getReturnStr());
		assertTrue("Unexpected return type", rt.getReturnStr().equals(
				Constants.REPLY_UNWRAPPED));
	}

	/**
	 * Send a message using an unwrapped interface to an endpoint that uses a
	 * wrapped interface. Expecting the call to be dispatched as (x, y) and so
	 * the endpoint should be confused
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testUnwrappedToWrappedCall() {

		TwoWay twoWay = new TwoWay();
		twoWay.setTwowayStr(Constants.THE_ID_STRING);
		ReturnType rt = getUnwrappedPort(DOCLIT_WRAPPED_ENDPOINT)
				.twoWay(twoWay);

		System.out.println(getName() + ": " + rt.getReturnStr());
		assertTrue("Unexpected return type", rt.getReturnStr().equals(
				Constants.REPLY_WRAPPED));
	}

	/**
	 * Send a message using an wrapped interface to an endpoint that uses an
	 * unwrapped interface. Expecting the endpoint to get confused
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWrappedToUnwrappedCall() {

		String rt = getWrappedPort(DOCLIT_UNWRAPPED_ENDPOINT).twoWay(
				Constants.THE_ID_STRING);

		System.out.println(getName() + ": " + rt);
		assertTrue("Unexpected return type", rt
				.equals(Constants.REPLY_UNWRAPPED));
	}

	/**
	 * Send a message using an wrapped interface to an endpoint that uses a
	 * wrapped interface. This should be fine
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWrappedToWrappedCall() {

		String rt = getWrappedPort(DOCLIT_WRAPPED_ENDPOINT).twoWay(
				Constants.THE_ID_STRING);

		System.out.println(getName() + ": " + rt);
		assertTrue("Unexpected return type", rt.equals(Constants.REPLY_WRAPPED));
	}

	/**
	 * Auxiliary method used to obtain the port object. For the purposes of this
	 * test, the method takes an endpoitn address as parameter
	 * 
	 * @return
	 */
	private jaxws.proxy.doclit.wsfvt.doclitwrapped.DocLitWrappedProxy getWrappedPort(
			String endpoint) {
		jaxws.proxy.doclit.wsfvt.doclitwrapped.DocLitWrappedProxy port;
		port = (new ProxyDocLitWrappedService()).getProxyDocLitWrappedPort();
		assertNotNull("Port is null", port);

		// make sure the port points at the right destination
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

		return port;
	}

	/**
	 * Auxiliary method used to obtain the port object. For the purposes of this
	 * test, the method takes an endpoitn address as parameter
	 * 
	 * @return
	 */
	private DocLitUnwrappedProxy getUnwrappedPort(
			String endpoint) {
		// get a port
		DocLitUnwrappedProxy port;
		port = (new ProxyDocLitUnwrappedService()).getProxyDocLitWrappedPort();
		assertNotNull("Port is null", port);

		// make sure the port points at the right destination
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

		return port;
	}
}
