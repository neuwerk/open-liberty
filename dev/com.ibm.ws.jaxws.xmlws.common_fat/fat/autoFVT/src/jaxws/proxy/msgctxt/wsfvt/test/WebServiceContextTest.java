//
//  @(#) 1.6 WautoFVT/src/jaxws/proxy/msgctxt/wsfvt/test/WebServiceContextTest.java, WAS.websvcs.fvt, WSFP.WFVT, a0722.08 2/7/07 14:19:37 [6/6/07 18:21:55]
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
// 01/24/07   sedov       416630          Added more properties
// 01/31/07   sedov       417716          swapped assert actual/expected
// 07/06/12   sedov       442265          attachments_outbound no longer exposed
// 03/25/08   sedov       507437          not exposed is tolerated if expected value is null
// 04/16/08   sedov       513284          tolerate prop not exposed, take 2
//
package jaxws.proxy.msgctxt.wsfvt.test;


import java.net.URI;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.handler.MessageContext;

import jaxws.proxy.common.Constants;
import jaxws.proxy.msgctxt.wsfvt.msgctxt.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Test cases for WebServiceContext and LifeCycle (JSR 250) annotations
 */
public class WebServiceContextTest  extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	static final String MSGCTXT_ENDPOINT = Constants.MSGCTXT_BASE + "/services/ProxyMsgCtxtService";
	
	static final String SERVICE_TNS = MsgCtxtService.class.getAnnotation(WebServiceClient.class).targetNamespace();
	
	public WebServiceContextTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(WebServiceContextTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJSR250_Resource(){
		MessageContextProxy port = getPort();
		
		boolean avail = port.atResourceAvailable();
		
		assertTrue("@Resource annotation was not recognised", avail);
	}

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testJSR250_PostConstruct(){
		MessageContextProxy port = getPort();
		
		boolean avail = port.atPostConstructAvailable();
		
		assertTrue("@PostConstruct annotation was not recognised", avail);		
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_WSDL_SERVICE_read(){
		String type_expected = QName.class.getName();
		
		String value_expected = "{" + SERVICE_TNS + "}MsgCtxtService";
		
		runTest(MessageContext.WSDL_SERVICE, type_expected, value_expected, false);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_WSDL_PORT_read(){
		String type_expected = QName.class.getName();

		WebServiceClient wsc = MsgCtxtService.class.getAnnotation(WebServiceClient.class);
		String value_expected = "{" + wsc.targetNamespace() + "}MsgCtxtPort";
		
		runTest(MessageContext.WSDL_PORT, type_expected, value_expected, false);		
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_WSDL_OPERATION_read(){
		String type_expected = QName.class.getName();
		String value_expected = "wsContextPropRead";
		
		runTest(MessageContext.WSDL_OPERATION, type_expected, value_expected, false);	
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_WSDL_INTERFACE_read(){
		String type_expected = QName.class.getName();
		String value_expected = "{" + SERVICE_TNS + "}MessageContextProxy";
		
		runTest(MessageContext.WSDL_INTERFACE, type_expected, value_expected, false);			
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_WSDL_DESCRIPTION_read(){
		String type_expected = URI.class.getName();
		String value_expected = "WEB-INF/wsdl/proxy_msgctxt.wsdl";
		
		runTest(MessageContext.WSDL_DESCRIPTION, type_expected, value_expected, false);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_SERVLET_CONTEXT_read(){
		String type_expected = "ServletContext";
		String value_expected = null;
		
		runTest(MessageContext.SERVLET_CONTEXT, type_expected, value_expected, false);
	}	
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_SERVLET_REQUEST_read(){
		String type_expected = "ServletRequest";
		String value_expected = null;
		
		runTest(MessageContext.SERVLET_REQUEST, type_expected, value_expected, false);
	}	
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_SERVLET_RESPONSE_read(){
		String type_expected = "ServletResponse";
		String value_expected = null;
		
		runTest(MessageContext.SERVLET_RESPONSE, type_expected, value_expected, false);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_HTTP_REQUEST_QUERYSTRING_read(){
		String type_expected = null;
		String value_expected = null;
		
		runTest("javax.xml.ws.http.request.querystring", type_expected, value_expected, false);		
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_HTTP_REQUEST_METHOD_read(){
		String type_expected = String.class.getName();
		String value_expected = "POST";
		
		runTest(MessageContext.HTTP_REQUEST_METHOD, type_expected, value_expected, false);		
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_HTTP_REQUEST_PATHINFO_read(){
		String type_expected = null;
		String value_expected = null;
		
		runTest("javax.xml.ws.http.request.pathinfo", type_expected, value_expected, false);		
	}	
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_ATTACHMENTS_OUTBOUND_read(){
		String type_expected = null; //String.class.getName();
		String value_expected = null;
		
		runTest("javax.xml.ws.binding.attachments.outbound", type_expected, value_expected, false, false);		
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWSCtxt_ATTACHMENTS_INBOUND_read(){
		String type_expected = null; //String.class.getName();
		String value_expected = null;
		
		runTest("javax.xml.ws.binding.attachments.inbound", type_expected, value_expected, false);		
	}	
	
	private void runTest(String propName, String exType, String exValue, boolean isValueFullySpecified){
		runTest(propName, exType, exValue, isValueFullySpecified, true);
}
	private void runTest(String propName, String exType, String exValue, boolean isValueFullySpecified, boolean isExposed){
		MessageContextProxy port = getPort();
		
		Holder<String> type = new Holder<String>();
		Holder<String> value = new Holder<String>();
		Holder<Boolean> isFound = new Holder<Boolean>();
		Holder<String> propertyName = new Holder<String>(propName);

		port.wsContextPropRead(propertyName, value, type, isFound);
		
		System.out.println("Property = " + propName + " found=" + isFound.value);
		System.out.println("Value = " + value.value + "/" + exValue);
		System.out.println("Type = " + type.value + "/" + exType);
		
		if (isFound.value || exValue != null){
			// either we found it, or we were expecting a specific value
			assertTrue("WebServiceContext did not expose " + propertyName.value, isFound.value);		
			
			if (exType != null)
				assertTrue("Type of " + propertyName.value + " does not match [" + type.value + ", " + exType + "]",
						type.value != null && type.value.indexOf(exType) > -1);
			
			if (exValue != null){
				if (isValueFullySpecified){
					assertEquals("Value of " + propertyName.value + " does not match", exValue, value.value);
				} else {
					assertTrue("Value of " + propertyName.value + " does not contain " + exValue, value.value.indexOf(exValue) != -1);
				}
			}
		} else if (!isFound.value && exValue == null) {
			// we didn't find it, but a null value would have been fine
			System.out.println("Not exposed, with a null expected value");
		} else {
			// expecting the prop to not be exposed
			assertFalse("WebServiceContext expose " + propertyName.value, isFound.value);
		}
	}
	
	private MessageContextProxy getPort() {
		MsgCtxtService service = new MsgCtxtService();
		MessageContextProxy port = service.getMsgCtxtPort();
		assertNotNull("Port is null", port);

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, MSGCTXT_ENDPOINT);
		
		return port;
	}	
}
