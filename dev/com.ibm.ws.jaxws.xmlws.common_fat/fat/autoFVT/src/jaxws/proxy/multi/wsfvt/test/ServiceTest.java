//
//  @(#) 1.2.1.8 autoFVT/src/jaxws/proxy/multi/wsfvt/test/ServiceTest.java, WAS.websvcs.fvt, WASX.FVT 5/22/07 09:06:13 [7/23/07 09:45:36]
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
// 12/07/06 sedov       409973          Added stack walking
// 12/18/06 sedov       409973          Added minor fixes
// 12/19/06 sedov       409973          Modified wsdlLoc test to use ?wsdl
// 05/22/07 sedov       440853          Comparison failure in listPorts
// 12/04/07 sedov       486125          WSDL4J no longer give HNF or FNF Exceptions as cause
//
package jaxws.proxy.multi.wsfvt.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import jaxws.proxy.common.Constants;
import jaxws.proxy.multi.wsfvt.multi.DocLitBarePort;
import jaxws.proxy.multi.wsfvt.multi.DocLitWrMultiPort;
import jaxws.proxy.multi.wsfvt.multi.Ping;
import jaxws.proxy.multi.wsfvt.multi.ProxyMultiPortService1;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.ws.wsfvt.test.framework.FVTTestCase;


/**
 * Test cases for Service class
 */
public class ServiceTest extends FVTTestCase {

	static final String INVALID_ENDPOINT = "http://this.endpoint.does.not.exist/nope";

	static final String MULTI_PORT1_ENDPOINT = Constants.MULTI_BASE + "/services/MultiPort1";
	static final String MULTI_PORT2_ENDPOINT = Constants.MULTI_BASE + "/services/MultiPort2";
	static final String MULTI_PORT3_ENDPOINT = Constants.MULTI_BASE + "/services/MultiPort3";
	
	static final String WSDL_LOCATION = "@WSDL_LOCATION@";
	
	public static final QName WRONG_PORT = new QName("http://some.port",
			"SomePort");

	public static final QName MULTI1_PORT = new QName(
			"http://multi.wsfvt.multi.proxy.jaxws",
			"MultiPort1");

	public static final QName MULTI1_SERVICE = new QName(
			"http://multi.wsfvt.multi.proxy.jaxws",
			"ProxyMultiPortService1");

	public ServiceTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(ServiceTest.class);
		return suite;
	}

	/** *************** confirmance tests ****************************** */

	/**
	 * @testStrategy Confirmance point 2.46, generated Service class extends
	 *               javax.xml.ws.Service
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Confirmance point 2.46, generated Service class extends javax.xml.ws.Service",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testServiceSuperclass() {

		Class s = ProxyMultiPortService1.class.getSuperclass();
		assertTrue(
				"Generated Service class does not extend javax.xml.ws.Service",
				s == javax.xml.ws.Service.class);
	}

	/** *************** Service constructor tests ********************** */

	/**
	 * @testStrategy Test generated Service's second constructor (URL, QNAME).
	 *               Invoke it using a correct service name and wsdl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test generated Service's second constructor (URL, QNAME). Invoke it using a correct service name and wsdl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_ctor_roundtrip() throws Exception {
		URL url = new URL(MULTI_PORT1_ENDPOINT + "?wsdl");
		ProxyMultiPortService1 svc = new ProxyMultiPortService1(url,
				MULTI1_SERVICE);

		DocLitWrMultiPort port = svc.getMultiPort1();
		port.ping(Constants.THE_STRING);
	}

	/**
	 * @testStrategy Test generated Service's second constructor (URL, QNAME).
	 *               Service name is not found in the wsdl file
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test generated Service's second constructor (URL, QNAME). Service name is not found in the wsdl file",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_ctor_ServiceNotInWSDL() throws Exception {
		File actualLoc = new File(WSDL_LOCATION, "proxy_doclit.wsdl");

		try {
			ProxyMultiPortService1 svc = new ProxyMultiPortService1(
					actualLoc.toURL(), MULTI1_SERVICE);
			fail("WebServiceException is expected when Service.create is invoked with incompatible wsdl file");
		} catch (WebServiceException wse) {
			logException(wse);
		}
	}

	/**
	 * @testStrategy Test generated Service's second constructor (URL, QNAME).
	 *               WSDL_Location url cannot be resolved (invalid server name)
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test generated Service's second constructor (URL, QNAME). WSDL_Location url cannot be resolved (invalid server name)",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_ctor_HostNotFound() throws Exception {
		URL url = new URL(INVALID_ENDPOINT + "?wsdl");

		try {
			ProxyMultiPortService1 svc = new ProxyMultiPortService1(url,
					MULTI1_SERVICE);
			fail("WebServiceException is expected when Service.create is invoked but server cannot be found");
		} catch (WebServiceException wse) {
			//logException(wse);

			assertSubstring("", wse.getMessage(), "UnknownHostException");
			assertStack("WSE.getCause should be UnknownHostException",
					wse, javax.wsdl.WSDLException.class);
		}
	}

	/**
	 * @testStrategy Test generated Service's second constructor (URL, QNAME).
	 *               WSDL file cannot be found on the server (valid server name)
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_ctor_404NotFound() throws Exception {
		URL url = new URL(Constants.MULTI_BASE + "/ThisEndpointDoesNotExist?wsdl");

		try {
			ProxyMultiPortService1 svc = new ProxyMultiPortService1(url,
					MULTI1_SERVICE);
			fail("WebServiceException is expected when Service.create is invoked but wsdl file cannot be found on the server");
		} catch (WebServiceException wse) {
			wse.printStackTrace();
			
			assertSubstring("", wse.getMessage(), "FileNotFoundException");
			assertStack("WSE.getCause should be WSDLException",
					wse, javax.wsdl.WSDLException.class);
		}
	}

	/** *********************** ************************** */

	/**
	 * @testStrategy Verify that correct service name is reported via
	 *               Service.getServiceName(
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Verify that correct service name is reported via Service.getServiceName(",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_getName() {
		ProxyMultiPortService1 svc = new ProxyMultiPortService1();
		QName svcName = svc.getServiceName();

		assertNotNull("Service.getServiceName() is null", svcName);
		assertTrue("Service.getServiceName() does not match", svcName
				.equals(MULTI1_SERVICE));
	}

	/**
	 * @testStrategy Verify that wsdl location is reportted correctly via
	 *               Service.getWSDLDocumentLocation() when we use the defautl
	 *               constructor
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Verify that wsdl location is reportted correctly via Service.getWSDLDocumentLocation() when we use the defautl constructor",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_getWsdlDocLoc_default() throws Exception {
		ProxyMultiPortService1 svc = new ProxyMultiPortService1();

		URL wsdlLoc = svc.getWSDLDocumentLocation();
		URL actualLoc = new URL(MULTI_PORT1_ENDPOINT + "?wsdl");
		
		// System.out.println("actual=" + wsdlLoc);
		// System.out.println("expected=" + actualLoc.toURL());

		assertNotNull("Service.getWSDLDocumentLocation() is null", wsdlLoc);
		assertEquals("Service.getWSDLDocumentLocation() does not match", actualLoc, wsdlLoc);
	}

	/**
	 * @testStrategy Verify that wsdl location is reportted correctly via
	 *               Service.getWSDLDocumentLocation() when we specify a new
	 *               wsdl location via the constructor
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Verify that wsdl location is reportted correctly via Service.getWSDLDocumentLocation() when we specify a new wsdl location via the constructor",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_getWsdlDocLoc_updated() throws Exception {
		String expected = MULTI_PORT1_ENDPOINT + "?wsdl";

		ProxyMultiPortService1 svc = new ProxyMultiPortService1(new URL(
				expected), MULTI1_SERVICE);
		URL wsdlLoc = svc.getWSDLDocumentLocation();

		System.out.println("WSDLLocation=" + wsdlLoc);

		assertNotNull("Service.getWSDLDocumentLocation() is null", wsdlLoc);
		assertTrue("Service.getWSDLDocumentLocation() does not match", wsdlLoc
				.toString().equals(expected));
	}

	/**
	 * @testStrategy Verify that Service.getPorts reports the correct ports
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Verify that Service.getPorts reports the correct ports",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_listPorts() {
		ProxyMultiPortService1 svc = new ProxyMultiPortService1();
		Iterator<QName> ports = svc.getPorts();

		assertNotNull("Service.getPorts is null", ports);
		assertTrue("Service.getPorts does not contain any ports", ports
				.hasNext());
		
		boolean found = false;
		System.out.println("Expecting:" + MULTI1_PORT);
		System.out.println("Actual: ");
		while(ports.hasNext()){
			QName port = ports.next();
			System.out.println(port);
			found |=  (MULTI1_PORT.equals(port));
		}
		
		assertTrue("Service.getPorts does not match", found);
	}

	/** ********************* getPort tests ********************* */

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_getPort1() {
		ProxyMultiPortService1 svc = new ProxyMultiPortService1();

		DocLitWrMultiPort port = svc.getPort(MULTI1_PORT, DocLitWrMultiPort.class);
		setEndpointAddress(port, MULTI_PORT1_ENDPOINT);
		
		port.ping(Constants.THE_STRING);
	}

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_getPort1_badPortName() {
		ProxyMultiPortService1 svc = new ProxyMultiPortService1();

		try {
			DocLitWrMultiPort port = svc.getPort(MULTI1_SERVICE,
					DocLitWrMultiPort.class);
		} catch (WebServiceException wse) {
			logException(wse);
		}
	}

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_getPort1_wrongClass() {
		ProxyMultiPortService1 svc = new ProxyMultiPortService1();

		try {
			DocLitBarePort port = svc.getPort(MULTI1_PORT, DocLitBarePort.class);
		} catch (WebServiceException wse) {
			logException(wse);
		}
	}
	
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_getPort1_nullPortName() {
		ProxyMultiPortService1 svc = new ProxyMultiPortService1();

		try {
			ProxyMultiPortService1 port = svc.getPort(null, ProxyMultiPortService1.class);
		} catch (WebServiceException wse) {
			logException(wse);
		}
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_getPort2() {
		ProxyMultiPortService1 svc = new ProxyMultiPortService1();

		DocLitBarePort port = svc.getPort(DocLitBarePort.class);
		
		// workaround to 401723...not required for this test
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
		rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, "barePing");
		
		setEndpointAddress(port, MULTI_PORT2_ENDPOINT);
		
		port.barePing(new Ping());
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService_getPort2_wrongClass() {
		ProxyMultiPortService1 svc = new ProxyMultiPortService1();

		DocLitBarePort port = svc.getPort(DocLitBarePort.class);
	}
	
	private void setEndpointAddress(Object port, String address){
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
	}
}
