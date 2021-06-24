//
// @(#) 1.10 autoFVT/src/jaxws/proxy/doclit/wsfvt/test/RuntimeExceptionsTest.java, WAS.websvcs.fvt, WASX.FVT 2/7/07 14:19:45 [7/11/07 13:16:17]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect           Description
// ----------------------------------------------------------------------------
// 12/04/06 sedov       408880           Added stack walking ability
// 12/19/06 sedov       409973           Fixed endpoint references
// 01/10/07 sedov       413290           Changed exception test to use ENUM
// 01/23/07 sedov       415799           Enhanced twoWay verification routines
// 02/01/07 sedov       417716           Updated UncheckedException test
// 02/07/07 sedov       419445           Fix SFE with detail test
//
package jaxws.proxy.doclit.wsfvt.test;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.soap.Detail;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import jaxws.proxy.common.CallbackHandler;
import jaxws.proxy.common.Constants;
import jaxws.proxy.doclit.wsfvt.async.CustomException;
import jaxws.proxy.doclit.wsfvt.async.DocLitWrappedProxy;
import jaxws.proxy.doclit.wsfvt.async.ProxyDocLitWrappedService;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.ExceptionTypeEnum;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.MyComplexType;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.ReturnType;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.TwoWayMultiResponse;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for different exceptions. Async exceptions are covered in AsyncTest
 * 
 * @endpoint ProxyDocLitPortImpl
 * @wsdl proxy_doclit.wsdl
 */
public class RuntimeExceptionsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private static final Class HOST_NOT_FOUND_CLASS = java.nio.channels.UnresolvedAddressException.class;

	private static final Class CONNECT_404_CLASS = java.net.ConnectException.class;

	static final String CONNECT_404_ENDPOINT = Constants.DOCLIT_BASE
			+ "/DoesNotExist";

	static final String HOST_NOT_FOUND_ENDPOINT = "http://this.endpoint.does.not.exist/nope";

	static final String DOCLIT_WRAPPED_ENDPOINT = Constants.DOCLIT_BASE
			+ "/services/ProxyDocLitWrappedService";

	public RuntimeExceptionsTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(RuntimeExceptionsTest.class);

		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/** **************** oneway exceptions ***************** */

	/**
	 * @testStrategy Test for oneWay methods throwing exceptions as a result of
	 *               misconfiguration. This is a server not found case
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for oneWay methods throwing exceptions as a result of misconfiguration. This is a server not found case",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testOneWay_HostNotFound() {
		DocLitWrappedProxy port = getProxy();

		Map<String, Object> map = ((BindingProvider) port).getRequestContext();
		map.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				HOST_NOT_FOUND_ENDPOINT);

		try {
			port.oneWayException(Constants.THE_WSE_STRING);
			fail("WebServiceException is expected when a proxy is invoked on oneWay operation against an invalid endpoint address");
		} catch (WebServiceException wse) {
			Constants.logStack(wse);

			if (Constants.TYPE_CHECKING_ENABLED)
				assertTrue(
						"WSE should contain UnknownHostException in its stack trace when endpoint cannot be found",
						Constants.checkStack(wse, HOST_NOT_FOUND_CLASS));
		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Test for oneWay methods throwing exceptions as a result of
	 *               misconfiguration. This is a 404-not found case
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for oneWay methods throwing exceptions as a result of misconfiguration. This is a 404-not found case",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testOneWay_404NotFound() {
		DocLitWrappedProxy port = getProxy();

		Map<String, Object> map = ((BindingProvider) port).getRequestContext();
		map
				.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
						CONNECT_404_ENDPOINT);

		try {
			port.oneWayException(Constants.THE_WSE_STRING);
			fail("WebServiceException is expected when a proxy is invoked on oneWay operation against an invalid endpoint address");
		} catch (WebServiceException wse) {
			Constants.logStack(wse);

			if (Constants.TYPE_CHECKING_ENABLED)
				assertTrue(
						"WSE should contain java.net.ConnectException when endpoint cannot be found",
						Constants.checkStack(wse, CONNECT_404_CLASS));
		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Test for oneWay methods throwing exceptions as a result of
	 *               misconfiguration. This is a 404-not found case
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for oneWay methods throwing exceptions as a result of misconfiguration. This is a 404-not found case",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testOneWay_WebServiceException() {
		DocLitWrappedProxy port = getProxy();

		try {
			port.oneWayException(Constants.THE_WSE_STRING);
		} catch (WebServiceException wse) {
			Constants.logStack(wse);

			fail("WebServiceException thrown by the endpoint during oneWay invokes should not be propagated to the client");
		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}

	/** ********************** two way exceptions ********************** */

	/**
	 * @testStrategy Try invoking a proxy using a sync invocation expect the
	 *               message to make a sucesful round trip. This is to verify
	 *               that the endpoint is alive and kicking
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Try invoking a proxy using a sync invocation expect the message to make a sucesful round trip. This is to verify that the endpoint is alive and kicking",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay_roundtrip() {
		DocLitWrappedProxy port = getProxy();

		String ret = port.twoWay(Constants.THE_STRING);
		assertTrue("Unexpected Response", Constants.THE_STRING.equals(ret));
	}

	/**
	 * @testStrategy Try invoking a proxy using a sync invocation expect an
	 *               exception as a result of endpoint throwing a WSE
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Try invoking a proxy using a sync invocation expect an exception as a result of endpoint throwing a WSE",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay_WebServiceException() throws Exception{
		DocLitWrappedProxy port = getProxy();

		try {
			port.twoWayException(ExceptionTypeEnum.WSE);
			fail("WebServiceException expected when endpoint throws an exception");
		} catch (SOAPFaultException sf) {
			Constants.logStack(sf);

			assertTrue("SFE class name was found in message: " + sf.getMessage(),
					sf.getMessage().indexOf(WebServiceException.class.getName()) == -1);
			
			assertTrue("Exception does not contain expected message: " + sf.getMessage(), 
					sf.getMessage().indexOf(Constants.THE_WSE_STRING) != -1);					
		}
	}

	/**
	 * @testStrategy Try invoking a proxy using a sync invocation expect an
	 *               exception as a result of endpoint throwing a WSE
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Try invoking a proxy using a sync invocation expect an exception as a result of endpoint throwing a WSE",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay_SOAPFaultException_noDetail() throws Exception {
		DocLitWrappedProxy port = getProxy();

		try {
			port.twoWayException(ExceptionTypeEnum.SOAP_FAULT_NO_DETAIL);
			fail("WebServiceException expected when endpoint throws an exception");
		} catch (SOAPFaultException sf) {
			Constants.logStack(sf);
			
			assertTrue("SFE class name was found in message: " + sf.getMessage(),
					sf.getMessage().indexOf(SOAPFaultException.class.getName()) == -1);
			
			assertTrue("Exception does not contain expected message: " + sf.getMessage(), 
					sf.getMessage().indexOf(Constants.THE_FAULT_STRING) != -1);
		}
	}
	
	/**
	 * @testStrategy Try invoking a proxy using a sync invocation expect an
	 *               exception as a result of endpoint throwing a WSE
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Try invoking a proxy using a sync invocation expect an exception as a result of endpoint throwing a WSE",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay_SOAPFaultException_withDetail() throws Exception {
		DocLitWrappedProxy port = getProxy();

		try {
			port.twoWayException(ExceptionTypeEnum.SOAP_FAULT_WITH_DETAIL);
			fail("WebServiceException expected when endpoint throws an exception");
		} catch (SOAPFaultException sf) {
			Constants.logStack(sf);
			
			assertTrue("SFE class name was found in message: " + sf.getMessage(),
					sf.getMessage().indexOf(SOAPFaultException.class.getName()) == -1);
			
			assertNotNull("detail filed is not available", sf.getFault());			
			
			Detail detail = sf.getFault().getDetail();
			String detailContent = detail.getFirstChild().getLocalName();
			
			System.out.println("getDetail()=" + detail.getLocalName());
			System.out.println("detailContent=" + detailContent);
			assertEquals("Unexpected detail: " + detailContent, detailContent, "testFaultDetail");
		}
	}	

	/**
	 * @testStrategy Try invoking a proxy using a sync invocation expect an
	 *               exception as a result of endpoint throwing a SimpleFault
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Try invoking a proxy using a sync invocation expect an exception as a result of endpoint throwing a SimpleFault",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay_WsdlFault() throws Exception{
		DocLitWrappedProxy port = getProxy();

		try {
			port.twoWayException(ExceptionTypeEnum.WSDL_FAULT);
			fail("SimpleFault expected when endpoint throws an exception");
		} catch (CustomException sf) {
			Constants.logStack(sf);

			assertTrue("SFE class name was found in message: " + sf.getMessage(),
					sf.getMessage().indexOf(CustomException.class.getName()) == -1);
			
			assertTrue("Exception does not contain expected message: " + sf.getMessage(), 
					sf.getMessage().indexOf(Constants.THE_FAULT_STRING) != -1);	
		}
	}

	/**
	 * @testStrategy Try invoking a proxy using a sync invocation expect an
	 *               exception as a result of endpoint throwing a SimpleFault
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Try invoking a proxy using a sync invocation expect an exception as a result of endpoint throwing a SimpleFault",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay_UncheckedException() throws Exception {
		DocLitWrappedProxy port = getProxy();

		try {
			port.twoWayException(ExceptionTypeEnum.UNCHECKED);
			fail("WSE expected when endpoint throws a DivideByZero");
		} catch (SOAPFaultException sf) {
			Constants.logStack(sf);
			
			assertTrue("ArithmeticException was found in message: " + sf.getMessage(),
					sf.getMessage().indexOf(java.lang.ArithmeticException.class.getName()) == -1);			
		}
	}

	/**
	 * @testStrategy Try invoking a proxy using a sync invocation expect an
	 *               exception as a result of endpoint not existing
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Try invoking a proxy using a sync invocation expect an exception as a result of endpoint not existing",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay_HostNotFound() {
		DocLitWrappedProxy port = getProxy();

		// force the client to call a non existent host
		Map<String, Object> map = ((BindingProvider) port).getRequestContext();
		map.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				HOST_NOT_FOUND_ENDPOINT);

		try {
			port.twoWayException(ExceptionTypeEnum.WSE);
			fail("WebServiceException expected when endpoint throws an exception");
		} catch (WebServiceException wse) {
			Constants.logStack(wse);

			if (Constants.TYPE_CHECKING_ENABLED)
				assertTrue(
						"WSE should contain UnknownHostException in its stack",
						Constants.checkStack(wse, HOST_NOT_FOUND_CLASS));
		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Try invoking a proxy using a sync invocation expect an
	 *               exception as a result of service not existing on a server
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Try invoking a proxy using a sync invocation expect an exception as a result of service not existing on a server",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay_404NotFound() {
		DocLitWrappedProxy port = getProxy();

		// force the client to call non existent endpoint
		Map<String, Object> map = ((BindingProvider) port).getRequestContext();
		map
				.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
						CONNECT_404_ENDPOINT);

		try {
			port.twoWayException(ExceptionTypeEnum.WSE);
			fail("WebServiceException expected when endpoint throws an exception");
		} catch (WebServiceException wse) {
			Constants.logStack(wse);

			wse.printStackTrace(System.out);

			if (Constants.TYPE_CHECKING_ENABLED)
				assertTrue("WSE.getCause should be "
						+ CONNECT_404_CLASS.getName(), Constants.checkStack(
						wse, CONNECT_404_CLASS));
		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}

	/** **************** Async Polling tests ********************* */

	/**
	 * @testStrategy Invoke the proxy with async-polling method, the proxy is
	 *               configured against an endpoint which does not exist (this
	 *               is a server not found case). Expected to throw a
	 *               EE/WSE/UnknownHostException
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Invoke the proxy with async-polling method, the proxy is configured against an endpoint which does not exist (this is a server not found case). Expected to throw a EE/WSE/UnknownHostException",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAsyncPolling_UnknwonHost() throws Exception {
		DocLitWrappedProxy port = getProxy();

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				HOST_NOT_FOUND_ENDPOINT);

		try {
			Response<ReturnType> resp = port
					.twoWayExceptionAsync(ExceptionTypeEnum.WSE);
			resp.get();

			fail("ExecutionException expected at invoke time when an invalid endpoint address is specified");
		} catch (ExecutionException ee) {
			Constants.logStack(ee);

			assertTrue("EE.getCause must be WebServiceException",
					ee.getCause() instanceof WebServiceException);

			if (Constants.TYPE_CHECKING_ENABLED)
				assertTrue("WSE.getCause must be UnknownHostException",
						Constants.checkStack(ee, HOST_NOT_FOUND_CLASS));
		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Invoke the proxy with async-polling method, the proxy is
	 *               configured against an endpoint which does not exist (this
	 *               is a 404-Not Found case). Expected to throw a EE/WSE
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Invoke the proxy with async-polling method, the proxy is configured against an endpoint which does not exist (this is a 404-Not Found case). Expected to throw a EE/WSE",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAsyncPolling_404NotFound() throws Exception {
		DocLitWrappedProxy port = getProxy();

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, CONNECT_404_ENDPOINT);

		try {
			Response<ReturnType> resp = port
					.twoWayExceptionAsync(ExceptionTypeEnum.WSE);
			resp.get();

			fail("ExecutionException expected at invoke time when an invalid endpoint address is specified");
		} catch (ExecutionException ee) {
			Constants.logStack(ee);

			assertTrue("EE.getCause must be WebServiceException",
					ee.getCause() instanceof WebServiceException);

			if (Constants.TYPE_CHECKING_ENABLED)
				assertTrue("WSE.getCause must be 404", Constants.checkStack(ee,
						CONNECT_404_CLASS));
		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Invoke the proxy with async-polling method, the endpoint
	 *               will throw a WSE which should result in a
	 *               EE/SOAPFaultException
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Invoke the proxy with async-polling method, the endpoint will throw a WSE which should result in a EE/SOAPFaultException",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAsyncPolling_WebServiceException() {
		DocLitWrappedProxy port = getProxy();

		Response<ReturnType> resp = port
				.twoWayExceptionAsync(ExceptionTypeEnum.WSE);

		try {
			resp.get();
			fail("ExecutionException expected at Response.get when ednpoint throws an exception");
		} catch (ExecutionException ee) {
			Constants.logStack(ee);

			if (Constants.TYPE_CHECKING_ENABLED)
				assertTrue(
						"ExecutionException.getCause should be an instance of SOAPFaultException",
						ee.getCause() instanceof SOAPFaultException);
		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Invoke the proxy with async-polling method, the endpoint
	 *               will throw a wsdl:fault which should result in a
	 *               EE/SimpleFault
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Invoke the proxy with async-polling method, the endpoint will throw a wsdl:fault which should result in a EE/SimpleFault",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAsyncPolling_WsdlFault() {
		DocLitWrappedProxy port = getProxy();

		Response<ReturnType> resp = port
				.twoWayExceptionAsync(ExceptionTypeEnum.WSDL_FAULT);

		try {
			resp.get();
			fail("ExecutionException expected at Response.get when ednpoint throws an exception");
		} catch (ExecutionException ee) {
			Constants.logStack(ee);

			if (Constants.TYPE_CHECKING_ENABLED)
				assertTrue(
						"ExecutionException.getCause should be an instance of SimpleFault",
						ee.getCause() instanceof CustomException);
		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}

	/** ******************** Async Callback ******************* */

	/**
	 * @testStrategy Invoke the proxy with async-callback method, the proxy is
	 *               configured against an endpoint which does not exist (this
	 *               is a server not found case). Expected to throw a
	 *               EE/WSE/UnknownHostException
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Invoke the proxy with async-callback method, the proxy is configured against an endpoint which does not exist (this is a server not found case). Expected to throw a EE/WSE/UnknownHostException",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAsyncCallback_UnknownHost() throws Exception {
		DocLitWrappedProxy port = getProxy();

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				HOST_NOT_FOUND_ENDPOINT);

		try {
			CallbackHandler<ReturnType> handler = new CallbackHandler<ReturnType>();
			Future<?> resp = port.twoWayExceptionAsync(ExceptionTypeEnum.WSE,
					handler);

			handler.waitBlocking(resp);
			handler.get();

			fail("ExecutionException expected at invoke time when an invalid endpoint address is specified");
		} catch (ExecutionException ee) {
			Constants.logStack(ee);

			assertTrue(
					"ExecutionException.getCause should be an instance of WebServiceException",
					ee.getCause() instanceof WebServiceException);

			if (Constants.TYPE_CHECKING_ENABLED)
				assertTrue(
						"WSE.getCause should be an instance of UnknownHostException",
						Constants.checkStack(ee, HOST_NOT_FOUND_CLASS));
		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Invoke the proxy with async-callback method, the proxy is
	 *               configured against an endpoint which does not exist (this
	 *               is a 404 Not Found case). Expected to throw a
	 *               EE/WSE/UnknownHostException
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Invoke the proxy with async-callback method, the proxy is configured against an endpoint which does not exist (this is a 404 Not Found case). Expected to throw a EE/WSE/UnknownHostException",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAsyncCallback_404NotFound() throws Exception {
		DocLitWrappedProxy port = getProxy();

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, CONNECT_404_ENDPOINT);

		CallbackHandler<ReturnType> handler = new CallbackHandler<ReturnType>();
		Future<?> resp = port.twoWayExceptionAsync(ExceptionTypeEnum.WSE,
				handler);

		try {
			handler.waitBlocking(resp);
			handler.get();

			fail("ExecutionException expected at Response.get when ednpoint throws an exception");
		} catch (ExecutionException ee) {
			Constants.logStack(ee);

			assertTrue(
					"ExecutionException.getCause should be an instance of WebServiceException",
					ee.getCause() instanceof WebServiceException);

			if (Constants.TYPE_CHECKING_ENABLED)
				assertTrue(
						"WSE.getCause should be an instance of ConnectException",
						Constants.checkStack(ee, CONNECT_404_CLASS));
		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Invoke the proxy with async-callback method, the proxy
	 *               throws a generic WebServiceException
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Invoke the proxy with async-callback method, the proxy throws a generic WebServiceException",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAsyncCallback_WebServiceException() throws Exception {
		DocLitWrappedProxy port = getProxy();

		CallbackHandler<ReturnType> handler = new CallbackHandler<ReturnType>();
		Future<?> resp = port.twoWayExceptionAsync(ExceptionTypeEnum.WSE,
				handler);

		try {
			handler.waitBlocking(resp);
			handler.get();

			fail("ExecutionException expected at Response.get when ednpoint throws an exception");
		} catch (ExecutionException ee) {
			Constants.logStack(ee);

			if (Constants.TYPE_CHECKING_ENABLED)
				assertTrue(
						"ExecutionException.getCause should be an instance of WebServiceException",
						ee.getCause() instanceof SOAPFaultException);

		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * @testStrategy Invoke the proxy with async-callback method, the proxy
	 *               throws a wsdl:Fault
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Invoke the proxy with async-callback method, the proxy throws a wsdl:Fault",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAsyncCallback_WsdlFault() throws Exception {
		DocLitWrappedProxy port = getProxy();

		CallbackHandler<ReturnType> handler = new CallbackHandler<ReturnType>();
		Future<?> resp = port.twoWayExceptionAsync(
				ExceptionTypeEnum.WSDL_FAULT, handler);

		try {
			handler.waitBlocking(resp);
			handler.get();

			fail("ExecutionException expected at Response.get when ednpoint throws an exception");
		} catch (ExecutionException ee) {
			Constants.logStack(ee);

			if (Constants.TYPE_CHECKING_ENABLED)
				assertTrue("EE.getCause should be an instance of SimpleFault",
						ee.getCause() instanceof CustomException);
		} catch (Exception e) {
			Constants.logStack(e);
			fail("Unexpected exception: " + e);
		}
	}
	
	/**
	 * Auxiliary method used for obtaining a port
	 * 
	 * @return
	 */
	private DocLitWrappedProxy getProxy() {
		ProxyDocLitWrappedService service = new ProxyDocLitWrappedService();
		DocLitWrappedProxy port = service.getProxyDocLitWrappedPort();
		assertNotNull("Port is null", port);

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				DOCLIT_WRAPPED_ENDPOINT);

		return port;
	}
}
