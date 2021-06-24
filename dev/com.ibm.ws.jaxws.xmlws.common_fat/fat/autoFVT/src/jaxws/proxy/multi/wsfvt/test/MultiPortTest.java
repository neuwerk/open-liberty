//
// @(#) 1.4 autoFVT/src/jaxws/proxy/multi/wsfvt/test/MultiPortTest.java, WAS.websvcs.fvt, WASX.FVT 12/19/06 14:16:26 [7/11/07 13:16:31]
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
// 12/18/06 sedov       409973           Updated wsdlLocation tests to use ?wsdl instead of local file

package jaxws.proxy.multi.wsfvt.test;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import jaxws.proxy.common.Constants;
import jaxws.proxy.multi.wsfvt.multi.DocLitBarePort;
import jaxws.proxy.multi.wsfvt.multi.DocLitWrMultiPort;
import jaxws.proxy.multi.wsfvt.multi.Ping;
import jaxws.proxy.multi.wsfvt.multi.ProxyMultiPortService1;
import jaxws.proxy.multi.wsfvt.multi.ProxyMultiPortService2;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for a wsdl with multiple ports/multiple services per port
 * @endpoint ProxyMultiP1PortImpl, ProxyMultiP2PortImpl, ProxyMultiP3PortImpl
 * @wsdl proxy_multi.wsdl
 */
public class MultiPortTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	static final String MULTI_PORT1_ENDPOINT = Constants.MULTI_BASE + "/services/MultiPort1";
	static final String MULTI_PORT2_ENDPOINT = Constants.MULTI_BASE + "/services/MultiPort2";
	static final String MULTI_PORT3_ENDPOINT = Constants.MULTI_BASE + "/services/MultiPort3";
	
	// namespace of the wsdl
	private static final String WSDL_NAMESPACE = "http://multi.wsfvt.multi.proxy.jaxws";
	
	// wsdl:services in the wsdl
	private static final QName SERVICE1 = new QName(WSDL_NAMESPACE, "ProxyMultiPortService1");
	private static final QName SERVICE2 = new QName(WSDL_NAMESPACE, "ProxyMultiPortService2");
	
	// wsdl:ports in the wsdl
	private static final QName PORT1 = new QName(WSDL_NAMESPACE, "MultiPort1");
	private static final QName PORT2 = new QName(WSDL_NAMESPACE, "MultiPort2");
	private static final QName PORT3 = new QName(WSDL_NAMESPACE, "MultiPort3");
	
	// local names of the PortImpl classes
	private static final String PORT1_CLASS = "ProxyMultiP1PortImpl";
	private static final String PORT2_CLASS = "ProxyMultiP2PortImpl";
	private static final String PORT3_CLASS = "ProxyMultiP3PortImpl";
	
	public MultiPortTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(MultiPortTest.class);
		return suite;
	}
	
	public void setUp() {
		System.out.println("==================== " + getName());
	}	
	
	/************************* service1 tests *************************/
	
	/**
	 * Test to ensure the service name is picked up correctly
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService1_getServiceName(){
		ProxyMultiPortService1 service = new ProxyMultiPortService1();
		
		assertEquals("Service.getServiceName does not match", SERVICE1, service.getServiceName());
	}
	
	/**
	 * Tests to ensure that the right ports are populated into a service
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService1_getPorts(){
		ProxyMultiPortService1 service = new ProxyMultiPortService1();
		
		List<QName> list =  iteratorToList(service.getPorts());
		
		assertEquals("Service.getPorts does not contain expected number of ports", 2, list.size());
		assertTrue("Service.getPorts does not contain Port1", list.contains(PORT1));
		assertTrue("Service.getPorts does not contain Port2", list.contains(PORT2));
	}	
	
	/**
	 * Test to ensure the wsdl location is populated correctly
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService1_getWSDLDocumentLocation() throws Exception{
		ProxyMultiPortService1 service = new ProxyMultiPortService1();
		URL wsdlLoc = service.getWSDLDocumentLocation();
		URL expectedLoc = new URL(MULTI_PORT1_ENDPOINT + "?wsdl");
		
		assertEquals("Service.getWSDLDocumentLocation is incorrect", expectedLoc, wsdlLoc);
	}
	
	/**
	 * Try invoking service1/port1
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService1_invokePort1_normal(){
		ProxyMultiPortService1 service = new ProxyMultiPortService1();
		DocLitWrMultiPort port =  service.getMultiPort1();
		patchEndpointURL(port, MULTI_PORT1_ENDPOINT, null);
		
		String endpoint_id = port.ping("ping");
		assertEquals("Request dispatched to the wrong endpoint", PORT1_CLASS, endpoint_id);
	}
	
	/**
	 * Try invoking service1/port1
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService1_invokePort1_getPortSEIOnly(){
		ProxyMultiPortService1 service = new ProxyMultiPortService1();
		DocLitWrMultiPort port =  service.getPort(DocLitWrMultiPort.class);
		patchEndpointURL(port, MULTI_PORT1_ENDPOINT, null);
		
		String endpoint_id = port.ping("ping");
		assertEquals("Request dispatched to the wrong endpoint", PORT1_CLASS, endpoint_id);
	}
	
	/**
	 * Try invoking service1/port1
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService1_invokePort1_getPortSEIandPortName(){
		ProxyMultiPortService1 service = new ProxyMultiPortService1();
		DocLitWrMultiPort port =  service.getPort(PORT1, DocLitWrMultiPort.class);
		patchEndpointURL(port, MULTI_PORT1_ENDPOINT, null);
		
		String endpoint_id = port.ping("ping");
		assertEquals("Request dispatched to the wrong endpoint", PORT1_CLASS, endpoint_id);
	}		
	
	/**
	 * Try invoking service1/port2
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService1_invokePort2_normal(){
		ProxyMultiPortService1 service = new ProxyMultiPortService1();
		DocLitBarePort port =  service.getMultiPort2();
		patchEndpointURL(port, MULTI_PORT2_ENDPOINT, "barePing");
		
		Ping ping = new Ping();
		ping.setMessageIn(Constants.THE_STRING);
		String endpoint_id = port.barePing(ping).getMessageOut();
		
		assertEquals("Request dispatched to the wrong endpoint", PORT2_CLASS, endpoint_id);
	}
	
	/**
	 * Try invoking service1/port2
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService1_invokePort2_getPortSEIOnly(){
		ProxyMultiPortService1 service = new ProxyMultiPortService1();
		DocLitBarePort port =  service.getPort(DocLitBarePort.class);
		patchEndpointURL(port, MULTI_PORT2_ENDPOINT, "barePing");
		
		Ping ping = new Ping();
		ping.setMessageIn(Constants.THE_STRING);
		String endpoint_id = port.barePing(ping).getMessageOut();
		
		assertEquals("Request dispatched to the wrong endpoint", PORT2_CLASS, endpoint_id);
	}
	
	/**
	 * Try invoking service1/port2
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService1_invokePort2_getPortSEIAndPortName(){
		ProxyMultiPortService1 service = new ProxyMultiPortService1();
		DocLitBarePort port =  service.getPort(PORT2, DocLitBarePort.class);
		patchEndpointURL(port, MULTI_PORT2_ENDPOINT, "barePing");
		
		Ping ping = new Ping();
		ping.setMessageIn(Constants.THE_STRING);
		String endpoint_id = port.barePing(ping).getMessageOut();
		
		assertEquals("Request dispatched to the wrong endpoint", PORT2_CLASS, endpoint_id);
	}		
	
	/************************* service2 tests *************************/
	
	/**
	 * Test to ensure the service name is picked up correctly
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService2_getServiceName(){
		ProxyMultiPortService2 service = new ProxyMultiPortService2();
		
		assertEquals("Service.getServiceName does not match", SERVICE2, service.getServiceName());
	}
	
	/**
	 * Tests to ensure that the right ports are populated into a service
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService2_getPorts(){
		ProxyMultiPortService2 service = new ProxyMultiPortService2();
		
		List<QName> list =  iteratorToList(service.getPorts());
		
		assertEquals("Service.getPorts does not contain expected number of ports",1,  list.size());
		assertTrue("Service.getPorts does not contain Port2", list.contains(PORT3));
	}	
	
	/**
	 * Test to ensure the wsdl location is populated correctly
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService2_getWSDLDocumentLocation() throws Exception{
		ProxyMultiPortService2 service = new ProxyMultiPortService2();
		URL wsdlLoc = service.getWSDLDocumentLocation();
		URL expectedLoc = new URL(MULTI_PORT1_ENDPOINT + "?wsdl");
		
		assertEquals("Service.getWSDLDocumentLocation is incorrect",expectedLoc,  wsdlLoc);
	}
	
	/**
	 * Try invoking service1/port1
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService2_invokePort3(){
		ProxyMultiPortService2 service = new ProxyMultiPortService2();
		DocLitWrMultiPort port =  service.getMultiPort3();
		patchEndpointURL(port, MULTI_PORT3_ENDPOINT, null);
		
		String endpoint_id = port.ping("ping");
		assertEquals("Request dispatched to the wrong endpoint", PORT3_CLASS, endpoint_id);
	}
		
	/**
	 * Chnage the default endpoint address to the one we specified in Constants
	 * @param port
	 * @param address
	 * @param string 
	 */
	private void patchEndpointURL(Object port, String address, String soapActionUri){
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
		
		if (soapActionUri != null){
			rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
			rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, soapActionUri);
		}
	}	
	
	/**
	 * Auxiliary method used to convert an iterator to a list. 
	 * @param it
	 * @return
	 */
	private List<QName> iteratorToList(Iterator<QName> it){
		
		List<QName> list = new LinkedList<QName>();
		while (it != null && it.hasNext()){
			list.add(it.next());
		}
		return list;
	}
}
