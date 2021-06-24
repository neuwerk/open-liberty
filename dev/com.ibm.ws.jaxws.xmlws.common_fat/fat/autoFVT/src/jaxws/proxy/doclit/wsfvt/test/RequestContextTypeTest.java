//
// @(#) 1.1 autoFVT/src/jaxws/proxy/doclit/wsfvt/test/RequestContextTypeTest.java, WAS.websvcs.fvt, WASX.FVT 12/18/06 14:31:22 [7/11/07 13:16:17]
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
// 12/18/06 sedov       409973           New File

package jaxws.proxy.doclit.wsfvt.test;

import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import jaxws.proxy.common.Constants;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.DocLitWrappedProxy;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.ProxyDocLitWrappedService;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Verifies that setting a request context property to an incorrect type does
 * not result in NPEs or other unexpected errors
 */
public class RequestContextTypeTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public RequestContextTypeTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(RequestContextTypeTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReqCtxt_ENDPOINT_ADDRESS() throws Exception {
		DocLitWrappedProxy port = getPort();		
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		
		try {
			rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, new URL("http://bad.endpoint.ibm.com/nope"));
		} catch (WebServiceException e){
			Constants.logStack(e);
		}

	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReqCtxt_AUTH_USERNAME() throws Exception{
		DocLitWrappedProxy port = getPort();		
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		
		try {
			rc.put(BindingProvider.USERNAME_PROPERTY, new StringTokenizer("http://bad.endpoint.ibm.com/nope"));
		} catch (WebServiceException e){
			Constants.logStack(e);
		}		
	}	

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReqCtxt_AUTH_PASSWORD() throws Exception{
		DocLitWrappedProxy port = getPort();		
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		
		try {
			rc.put(BindingProvider.PASSWORD_PROPERTY, new StringTokenizer("http://bad.endpoint.ibm.com/nope"));
		} catch (WebServiceException e){
			Constants.logStack(e);
		}			
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReqCtxt_SESSION_MAINTAIN() throws Exception{
		DocLitWrappedProxy port = getPort();		
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		
		try {
			rc.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, Constants.class);
		} catch (WebServiceException e){
			Constants.logStack(e);
		}			
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReqCtxt_SOAPACTION_USE() throws Exception{
		DocLitWrappedProxy port = getPort();		
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		
		try {
			rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Constants.class);
		} catch (WebServiceException e){
			Constants.logStack(e);
		}		
	}	
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReqCtxt_SOAPACTION_URI() throws Exception{
		DocLitWrappedProxy port = getPort();		
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		
		try {
			rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, new URL("http://bad.endpoint.ibm.com/nope"));
		} catch (WebServiceException e){
			Constants.logStack(e);
		}		
	}	
	
	/**
	 * Auxiliary method used for obtaining the service and setting any
	 * properties on it
	 * 
	 * @return
	 */
	private DocLitWrappedProxy getPort() {
		ProxyDocLitWrappedService service = new ProxyDocLitWrappedService();
		DocLitWrappedProxy port = service.getProxyDocLitWrappedPort();
		assertNotNull("Port is null", port);

		return port;
	}
}
