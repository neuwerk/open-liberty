//
// @(#) 1.1 WautoFVT/src/jaxws/proxy/wsfvt/test/DocLitTest.java, WAS.websvcs.fvt, WSFPB.WFVT 9/12/06 14:50:56 [9/12/06 14:56:54]
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
// 11/13/06 sedov       404343          Added anyType parameter

package jaxws.proxy.doclit.wsfvt.test;

import java.math.BigInteger;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

import jaxws.proxy.common.Constants;
import jaxws.proxy.doclit.wsfvt.doclit.Composite;
import jaxws.proxy.doclit.wsfvt.doclit.DocLitProxy;
import jaxws.proxy.doclit.wsfvt.doclit.FaultBeanWithWrapper;
import jaxws.proxy.doclit.wsfvt.doclit.Header;
import jaxws.proxy.doclit.wsfvt.doclit.HeaderPart0;
import jaxws.proxy.doclit.wsfvt.doclit.HeaderPart1;
import jaxws.proxy.doclit.wsfvt.doclit.HeaderResponse;
import jaxws.proxy.doclit.wsfvt.doclit.ObjectFactory;
import jaxws.proxy.doclit.wsfvt.doclit.ProxyDocLitService;
import jaxws.proxy.doclit.wsfvt.doclit.SimpleFault;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for Document/Literal style
 * 
 * @endpoint ProxyDocLitPortImpl
 * @wsdl proxy_doclit.wsdl
 */
public class DocLitTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	public DocLitTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(DocLitTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * Try sending
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testOneWay_Empty() {
		DocLitProxy port = getPort();
		setSOAPAction(port, "oneWayEmpty");
		
		port.oneWayEmpty();
	}

	/**
	 * Try sending a message
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testOneWay() {
		DocLitProxy port = getPort();
		setSOAPAction(port, "oneWay");
		
		port.oneWay(Constants.THE_STRING);
	}

	/**
	 * Test sending a twoWay message with a simple return "T x(in S)"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay() {
		DocLitProxy port = getPort();
		setSOAPAction(port, "twoWaySimple");
		
		String ret = port.twoWaySimple(Constants.THE_INT);
		assertTrue("Returned incorrect type", ret.equals(Constants.THE_STRING));
	}
	
	/**
	 * Test sending a twoWay message with xsd:anyType
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay_anyType() {
		DocLitProxy port = getPort();
		setSOAPAction(port, "anyType");
		
		Object ret = port.anyType(Constants.THE_STRING);
		
		assertTrue("Returned incorrect type", ret instanceof String);
	}	
	
	/**
	 * Test sending a twoWay message with a simple return "T x(in S)"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testHolder_Header_INOUT() {
		DocLitProxy port = getPort();
		setSOAPAction(port, "header");
		
		Header payload = new Header();
		HeaderPart0 h0 = new HeaderPart0();
		HeaderPart1 h1 = new HeaderPart1();
		Holder<HeaderPart0> holder = new Holder<HeaderPart0>(h0);
		
		payload.setIn(Constants.THE_STRING);
		payload.setOut(1234567890);
		payload.setInout(new BigInteger("42"));
		
		h0.setHeaderType("header0");
		h1.setHeaderType("header1");
		
		HeaderResponse hr = port.header(payload, holder, h1);
		
		assertEquals("Returned incorrect type", payload.getOut(), hr.getOut());
		assertEquals("Returned incorrect type", payload.getInout(), hr.getInout());
		assertEquals("Returned incorrect type", "header0", h0.getHeaderType());
	}	
	
	/**
	 * Test sending a twoWay message using a holder "void x(inout T)"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testHolder_INOUT() {
		DocLitProxy port = getPort();
		setSOAPAction(port, "twoWayHolder");
		
		Composite comp = new Composite();
		comp.setMyElement(Constants.THE_STRING);
		Holder<Composite> holder = new Holder<Composite>(comp);
		try {
			port.twoWayHolder(holder);
		} catch (SimpleFault sf) {
			sf.printStackTrace();
			fail("Unexpected exception: " + sf);
		} catch (FaultBeanWithWrapper fbwr) {
			fail("Unexpected exception: " + fbwr);
		}

		assertTrue("Returned incorrect type", comp.getMyElement().equals(
				Constants.THE_STRING));
	}

	/**
	 * Test sending a twoWay message using a holder "void x(inout T)" Request
	 * for endpoint to throw a service-specific fault
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay_WsdlFault() {
		DocLitProxy port = getPort();
		setSOAPAction(port, "twoWayHolder");
		
		Composite comp = new Composite();
		comp.setMyElement(Constants.THE_FAULT_STRING);
		Holder<Composite> holder = new Holder<Composite>(comp);
		try {
			port.twoWayHolder(holder);
			fail("SimpleFault expected. Returned "
					+ holder.value.getMyElement());
		} catch (SimpleFault sf) {
			System.out.println(getName() + ": " + sf);
			assertTrue("Unexpected message", sf.getFaultInfo().equals(
					Constants.THE_FAULT_STRING));
		} catch (FaultBeanWithWrapper fbwr) {
			fail("Unexpected exception: " + fbwr);
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			fail("Unexpected exception: " + wse);
		}
	}

	/**
	 * Test sending a twoWay message using a holder "void x(inout T)" Request
	 * for the endpoint to throw a WSE
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay_WebServiceException() {
		DocLitProxy port = getPort();
		setSOAPAction(port, "twoWayHolder");
		
		Composite comp = new Composite();
		comp.setMyElement(Constants.THE_WSE_STRING);
		Holder<Composite> holder = new Holder<Composite>(comp);
		try {
			port.twoWayHolder(holder);
			fail("WebServiceException expected. Returned "
					+ holder.value.getMyElement());
		} catch (SimpleFault sf) {
			System.out.println(getName() + ": " + sf);
			fail("Unexpected exception: " + sf);
		} catch (FaultBeanWithWrapper fbwr) {
			fail("Unexpected exception: " + fbwr);
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			assertTrue("Unexpected message: '" + wse.getMessage() + "'",
					wse.getMessage().indexOf(Constants.THE_WSE_STRING) != -1);
		}
	}

	// this is a workaround to SOAPAction being used to map
	// doc/lit-bare requests to operations.
	private void setSOAPAction(DocLitProxy port, String actionUri){
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
		rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, actionUri);
	}
	
	private DocLitProxy getPort() {
		ProxyDocLitService service = new ProxyDocLitService();
		DocLitProxy port = service.getProxyDocLitPort();
		assertNotNull("Port is null", port);

		Map<String, Object> rc = ((BindingProvider) port)
				.getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.DOCLIT_BASE + "/services/ProxyDocLitService");

		return port;
	}
}
