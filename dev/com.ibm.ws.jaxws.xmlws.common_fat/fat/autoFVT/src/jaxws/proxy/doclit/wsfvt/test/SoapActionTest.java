//
// @(#) 1.3 autoFVT/src/jaxws/proxy/doclit/wsfvt/test/SoapActionTest.java, WAS.websvcs.fvt, WASX.FVT 1/23/07 15:59:22 [7/11/07 13:16:18]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date       UserId      Defect          Description
// ----------------------------------------------------------------------------
// 11/15/06   sedov       D404343         New File
// 01/19/07   sedov       D415799         Added wrapped tests

package jaxws.proxy.doclit.wsfvt.test;

import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import jaxws.proxy.common.Constants;
import jaxws.proxy.doclit.wsfvt.doclit.DocLitProxy;
import jaxws.proxy.doclit.wsfvt.doclit.Ping;
import jaxws.proxy.doclit.wsfvt.doclit.ProxyDocLitService;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.DocLitWrappedProxy;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.ProxyDocLitWrappedService;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Test case for SOAPAction being handled correctly by the server
 */
public class SoapActionTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public SoapActionTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(SoapActionTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy Send a request wiout specifying anything, default value is
	 *               used
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request wiout specifying anything, default value is used",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPAction_bare_default() {
		DocLitProxy port = getBarePort();

		Holder<Ping> ping = new Holder<Ping>(new Ping());
		ping.value.setMessage(Constants.THE_STRING);
		port.soapAction(ping);
	}

	/**
	 * @testStrategy Send a request with a valid SOAPAction set in the
	 *               MessageContext
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request with a valid SOAPAction set in the MessageContext",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPAction_bare_reqCtxt_valid() {
		DocLitProxy port = getBarePort();
		setSOAPAction(port,
				"soapAction");

		Holder<Ping> ping = new Holder<Ping>(new Ping());
		ping.value.setMessage(Constants.THE_STRING);
		port.soapAction(ping);
	}

	/**
	 * @testStrategy Send a request with an empty SOAPAction set in the
	 *               MessageContext
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request with an empty SOAPAction set in the MessageContext",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPAction_bare_reqCtxt_empty() {
		DocLitProxy port = getBarePort();
		setSOAPAction(port, "");

		Holder<Ping> ping = new Holder<Ping>(new Ping());
		ping.value.setMessage(Constants.THE_STRING);
		port.soapAction(ping);
	}

	/**
	 * @testStrategy Send a request with a operation name as SOAPAction set in
	 *               the MessageContext
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request with a operation name as SOAPAction set in the MessageContext",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPAction_bare_reqCtxt_operationName() {
		DocLitProxy port = getBarePort();
		setSOAPAction(port, "soapAction");

		Holder<Ping> ping = new Holder<Ping>(new Ping());
		ping.value.setMessage(Constants.THE_STRING);
		port.soapAction(ping);
	}

//	/**
//	 * @testStrategy Send a request with an invalid SOAPAction set in the
//	 *               MessageContext
//	 */
//    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request with an invalid SOAPAction set in the MessageContext",
//    expectedResult="",
//    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
//	public void testSOAPAction_bare_reqCtxt_invalid() {
//		DocLitProxy port = getBarePort();
//		setSOAPAction(port, "SoapActionDoesNotMatchWsdl");
//
//		Holder<Ping> ping = new Holder<Ping>(new Ping());
//		ping.value.setMessage(Constants.THE_STRING);
//		port.soapAction(ping);
//	}

	/**
	 * @testStrategy Send a request wiout specifying anything, default value is
	 *               used
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request wiout specifying anything, default value is used",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPAction_wrapped_default() {
		DocLitWrappedProxy port = getWrappedPort();

		port.twoWay(Constants.THE_STRING);
	}

	/**
	 * @testStrategy Send a request with a valid SOAPAction set in the
	 *               MessageContext
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request with a valid SOAPAction set in the MessageContext",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPAction_wrapped_reqCtxt_valid() {
		DocLitWrappedProxy port = getWrappedPort();
		setSOAPAction(port,
				"twoWay");

		port.twoWay(Constants.THE_STRING);
	}

	/**
	 * @testStrategy Send a request with an empty SOAPAction set in the
	 *               MessageContext
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request with an empty SOAPAction set in the MessageContext",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPAction_wrapped_reqCtxt_empty() {
		DocLitWrappedProxy port = getWrappedPort();
		setSOAPAction(port, "");

		port.twoWay(Constants.THE_STRING);
	}

	/**
	 * @testStrategy Send a request with a operation name as SOAPAction set in
	 *               the MessageContext
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request with a operation name as SOAPAction set in the MessageContext",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPAction_wrapped_reqCtxt_operationName() {
		DocLitWrappedProxy port = getWrappedPort();
		setSOAPAction(port, "twoWay");

		port.twoWay(Constants.THE_STRING);
	}

//	/**
//	 * @testStrategy Send a request with an invalid SOAPAction set in the
//	 *               MessageContext
//	 */
//    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request with an invalid SOAPAction set in the MessageContext",
//    expectedResult="",
//    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
//	public void testSOAPAction_wrapped_reqCtxt_invalid() {
//		DocLitWrappedProxy port = getWrappedPort();
//		setSOAPAction(port, "SoapActionDoesNotMatchWsdl");
//
//		port.twoWay(Constants.THE_STRING);
//	}

	/**
	 * @testStrategy Send a request to the server when 2 operations have the
	 *               same soapaction. This should disable routing based on
	 *               soapaction and instead resort to soap body based
	 *               dispatching
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request to the server when 2 operations have the same soapaction. This should disable routing based on soapaction and instead resort to soap body based dispatching",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPAction_wrapped_soapActionCollision1() {
		DocLitWrappedProxy port = getWrappedPort();

		String ret = port.soapAction1A(Constants.THE_STRING);

		assertEquals("Request routed to the wrong operation", ret,
				"soapAction1A");
	}

	/**
	 * @testStrategy Send a request to the server when 2 operations have the
	 *               same soapaction. This should disable routing based on
	 *               soapaction and instead resort to soap body based
	 *               dispatching
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request to the server when 2 operations have the same soapaction. This should disable routing based on soapaction and instead resort to soap body based dispatching",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPAction_wrapped_soapActionCollision2() {
		DocLitWrappedProxy port = getWrappedPort();

		String ret = port.soapAction1B(Constants.THE_STRING);

		assertEquals("Request routed to the wrong operation", ret,
				"soapAction1B");
	}

	/**
	 * @testStrategy Send a request to the server when 2 operations have empty
	 *               soap action. This should disable routing based on
	 *               soapaction and instead resort to soap body based
	 *               dispatching
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request to the server when 2 operations have empty soap action. This should disable routing based on soapaction and instead resort to soap body based dispatching",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPAction_wrapped_soapActionEmpty1() {
		DocLitWrappedProxy port = getWrappedPort();

		String ret = port.soapAction2A(Constants.THE_STRING);

		assertEquals("Request routed to the wrong operation", ret,
				"soapAction2A");
	}
	
	/**
	 * @testStrategy Send a request to the server when 2 operations have empty
	 *               soap action. This should disable routing based on
	 *               soapaction and instead resort to soap body based
	 *               dispatching
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a request to the server when 2 operations have empty soap action. This should disable routing based on soapaction and instead resort to soap body based dispatching",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPAction_wrapped_soapActionEmpty2() {
		DocLitWrappedProxy port = getWrappedPort();

		String ret = port.soapAction2B(Constants.THE_STRING);

		assertEquals("Request routed to the wrong operation", ret,
				"soapAction2B");
	}	

	private void setSOAPAction(Object port, String uri) {
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
		rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, uri);
	}

	private DocLitProxy getBarePort() {
		ProxyDocLitService service = new ProxyDocLitService();
		DocLitProxy port = service.getProxyDocLitPort();
		assertNotNull("Port is null", port);

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, Constants.DOCLIT_BASE
				+ "/services/ProxyDocLitService");

		return port;
	}

	private DocLitWrappedProxy getWrappedPort() {
		ProxyDocLitWrappedService service = new ProxyDocLitWrappedService();
		DocLitWrappedProxy port = service.getProxyDocLitWrappedPort();
		assertNotNull("Port is null", port);

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, Constants.DOCLIT_BASE
				+ "/services/ProxyDocLitWrappedService");

		return port;
	}

}
