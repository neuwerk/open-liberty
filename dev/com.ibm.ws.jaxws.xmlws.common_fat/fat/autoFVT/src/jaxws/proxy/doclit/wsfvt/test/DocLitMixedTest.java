package jaxws.proxy.doclit.wsfvt.test;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import jaxws.proxy.common.Constants;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import jaxws.proxy.doclit.wsfvt.doclitmixed.AnotherReturnType;
import jaxws.proxy.doclit.wsfvt.doclitmixed.DocLitMixedProxy;
import jaxws.proxy.doclit.wsfvt.doclitmixed.ProxyDocLitMixedService;
import jaxws.proxy.doclit.wsfvt.doclitmixed.YetAnotherReturnType;

/**
 * Tests for mixed doc/lit and doc/lit-wrapped styles in the same
 * binding.
 * @endpoint ProxyDocLitMixedPortImpl
 * @wsdl proxy_doclitmixed.wsdl
 */
public class DocLitMixedTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public DocLitMixedTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(DocLitMixedTest.class);
		return suite;
	}
	
	public void setUp() {
		System.out.println("==================== " + getName());
	}	
	
	/**
	 * Test sendng messages to a two-way interface "A x(in B)"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMixed_Wrapped(){
		//ProxyDocLitMixedService service = new ProxyDocLitMixedService();
		DocLitMixedProxy port = getPort() ; //service.getProxyDocLitMixedPort();
		assertNotNull("Port is null", port);
		
		String response = port.twoWay(Constants.THE_STRING);
		assertEquals("Unexpected response", Constants.THE_STRING, response);
	}
	
	
	/**
	 * Test sendng/receiving messages using a non-wrapped style doc/lit
	 * Lookoing for assumptions about a port being wrapepd or non wrapped
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testMixed_NonWrapped(){
		//ProxyDocLitMixedService service = new ProxyDocLitMixedService();
		DocLitMixedProxy port = getPort(); //service.getProxyDocLitMixedPort();
		
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
		rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, "another");
		
		AnotherReturnType art = new AnotherReturnType();
		art.setReturnStr(Constants.THE_STRING);
		YetAnotherReturnType yart = port.another(art);

		assertEquals("Unexpected response", Constants.THE_INT, yart.getReturnStr());
	}
	
	/**
	 * Test sendng an empty message. Should be valid for bare
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testDocLitBare_twoWayEmpty(){
		DocLitMixedProxy port = getPort();
		
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
		rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, "twoWayEmpty");		
		
		YetAnotherReturnType yart = port.twoWayEmpty();

		assertEquals("Unexpected response", Constants.THE_INT, yart.getReturnStr());
	}	
	
	private DocLitMixedProxy getPort(){
		ProxyDocLitMixedService service = new ProxyDocLitMixedService();
		DocLitMixedProxy port = service.getProxyDocLitMixedPort();
		assertNotNull("Port is null", port);
		
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.DOCLIT_BASE + "/services/ProxyDocLitMixedService");
		
		return port;
	}
}
