//
// @(#) 1.7 autoFVT/src/jaxws/proxy/doclit/wsfvt/test/DocLitWrappedTest.java, WAS.websvcs.fvt, WASX.FVT 1/26/07 15:51:46 [7/11/07 13:16:16]
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
// 07/01/26 sedov       417171          added SOAPAction to dispatch tests

package jaxws.proxy.doclit.wsfvt.test;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Holder;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.proxy.common.Constants;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.DocLitWrappedProxy;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.ExceptionTypeEnum;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.MyComplexType;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.ProxyDocLitWrappedService;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for Document/Literal-wrapped style
 * Holder cases for Header are not considered in this suite
 * as Header params automaticallyforce java binding to be Bare
 * 
 * @endpoint ProxyDocLitWrappedPortImpl
 * @wsdl proxy_doclitwr.wsdl
 */
public class DocLitWrappedTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	
	static final String DOCLITWR_ENDPOINT = Constants.DOCLIT_BASE + "/services/ProxyDocLitWrappedService";
	static final String DOCLITWR_PROVIDER_ENDPOINT = Constants.DOCLIT_BASE + "/services/ProviderDocLitWrappedService";
	
	static final String DOCLITWR_NAMESPACE = "http://doclitwrapped.wsfvt.doclit.proxy.jaxws";
	static final QName DOCLITWR_PORT = new QName(DOCLITWR_NAMESPACE, "ProxyDocLitWrappedPort");
	static final QName DOCLITWR_SERVICE = new QName(DOCLITWR_NAMESPACE, "ProxyDocLitWrappedService");
	
	public DocLitWrappedTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(DocLitWrappedTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * Test sendng messages to a one-way void interface "void x()"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testEndpoint_DispatchToProxy_generatedService() {

		ProxyDocLitWrappedService service = new ProxyDocLitWrappedService();

		Dispatch<Source> dispatch = service.createDispatch(DOCLITWR_PORT,
				Source.class, Service.Mode.PAYLOAD);
		assertNotNull("Dispatch is null", dispatch);

		Map<String, Object> reqCtxt = ((BindingProvider) dispatch)
				.getRequestContext();
		reqCtxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				DOCLITWR_ENDPOINT);
		reqCtxt.put(BindingProvider.SOAPACTION_USE_PROPERTY,
				Boolean.TRUE);
		reqCtxt.put(BindingProvider.SOAPACTION_URI_PROPERTY,
				"twoWay");
	
		String message = "<ns1:twoWay xmlns:ns1='" + DOCLITWR_NAMESPACE + "'><twoway_str>My String</twoway_str></ns1:twoWay>";
		dispatch.invoke(new StreamSource(new ByteArrayInputStream(message
				.getBytes())));
	}

	/**
	 * Test sendng messages to a one-way void interface "void x()"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testEndpoint_DispatchToProxy_dynamicService() {

		Service service = Service.create(DOCLITWR_SERVICE);

		service.addPort(DOCLITWR_PORT, SOAPBinding.SOAP11HTTP_BINDING,
				DOCLITWR_ENDPOINT);
		javax.xml.ws.Dispatch<Source> dispatch = null;
		dispatch = service.createDispatch(DOCLITWR_PORT, Source.class,
				Service.Mode.PAYLOAD);
		assertNotNull("Dispatch is null", dispatch);

		String message = "<ns1:twoWay xmlns:ns1='" + DOCLITWR_NAMESPACE + "'><twoway_str>My String</twoway_str></ns1:twoWay>";

		Map<String, Object> rc = ((BindingProvider) dispatch)
				.getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, DOCLITWR_ENDPOINT);
		rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
		rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, "twoWay");
		
		dispatch.invoke(new StreamSource(new ByteArrayInputStream(message
				.getBytes())));
	}

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testEndpoint_ProxyToProvider() {

		DocLitWrappedProxy port = getPort();

		Map<String, Object> reqCtxt = ((BindingProvider) port)
				.getRequestContext();
		reqCtxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				DOCLITWR_PROVIDER_ENDPOINT);
		
		String ret = port.twoWay(Constants.THE_STRING);
		
		assertTrue("Unexpected Response", ret.equals(Constants.THE_STRING));
	}

	/**
	 * Test sendng messages to a one-way void interface "void x()"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testOneWay_Empty() {
		DocLitWrappedProxy port = getPort();

		port.oneWayVoid();
	}

	/**
	 * Test sendng messages to a one-way void interface "void x(in T)"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testOneWay() {
		DocLitWrappedProxy port = getPort();

		port.oneWay(Constants.THE_STRING);
	}

	/**
	 * Test sendng messages to a two-way interface "A x(in B)"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay() throws Exception {
		DocLitWrappedProxy port = getPort();

		String response = port.twoWay(Constants.THE_STRING);
		
		System.out.println("Response String = " + response);
		assertTrue("Unexpected response", response != null);
	}

	/**
	 * Test sendng messages to a two-way interface "void x(inout A)"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testHolder_INOUT() {
		DocLitWrappedProxy port = getPort();

		Holder<String> strHolder = new Holder<String>(Constants.THE_STRING);
		Holder<Integer> intHolder = new Holder<Integer>(Constants.THE_INT);

		port.twoWayHolder(strHolder, intHolder);
		assertTrue("Unexpected response",
				strHolder.value.equals(Constants.THE_STRING));
		assertTrue("Unexpected response",
				intHolder.value.equals(Constants.THE_INT));
	}

	/**
	 * Test for invoking with a null Hodler object. This is invalid and should
	 * never invoke
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testHolder_null() {
		DocLitWrappedProxy port = getPort();

		//Holder<String> strHolder = new Holder<String>(Constants.THE_STRING);
		Holder<Integer> intHolder = new Holder<Integer>(Constants.THE_INT);

		try {
			port.twoWayHolder(null, intHolder);
			//fail("WebServiceException expected when invoked with a null Holder");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * Test for invoking with a null parameter. This parameter has a default in
	 * the wsdl so settingit null should just force it to use the default
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testParam_null_default() {
		DocLitWrappedProxy port = getPort();

		Holder<String> strHolder = new Holder<String>(null);
		Holder<Integer> intHolder = new Holder<Integer>(Constants.THE_INT);

		try {
			port.twoWayHolder(strHolder, intHolder);
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			fail("Unexpected: " + wse);
		}
	}	

	/**
	 * Test for invoking with a null parameter. This parameter does not have a
	 * default and so this shoudl force a failure at jax-b level. Expecting a
	 * WSE with JAXBException as cause
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testParam_null_noDefault() {
		DocLitWrappedProxy port = getPort();

		Holder<String> strHolder = new Holder<String>(Constants.THE_STRING);
		Holder<Integer> intHolder = new Holder<Integer>(null);

		try {
			port.twoWayHolder(strHolder, intHolder);
			//fail("WebServiceException expected when invoked with a null parameter");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			assertTrue("", wse.getCause() instanceof JAXBException);
		}
	}

	/**
	 * Test sendng messages to a two-way interface "void x(inout A, in B)"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testHolder_INOUTandIN() {
		DocLitWrappedProxy port = getPort();

		Holder<String> strHolder = new Holder<String>(Constants.THE_STRING);

		port.twoWayInOut(strHolder, new BigInteger("10"));
		assertTrue("Unexpected response", strHolder.value
				.equals(Constants.THE_STRING));
	}

	/**
	 * Test sendng messages to a two-way interface "void x(in A, out B, out C)"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testHolder_OUT() {
		DocLitWrappedProxy port = getPort();
		
		Holder<String> strHolder = new Holder<String>();
		Holder<MyComplexType> mct = new Holder<MyComplexType>();
		
		port.twoWayOut(Constants.THE_STRING, strHolder, mct);
		
		assertEquals("OUT param1 invalid", strHolder.value,
				Constants.THE_STRING);
		
		assertNotNull("OUT param2 is null", mct.value);
		
		assertEquals("OUT param2 invalid", mct.value.getB(),
				Constants.THE_STRING);		
		
	}
	

	/**
	 * Test sendng messages to a two-way interface "void x(in A, in B, out C, out D, inout E, inout F)"
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testHolder_INOUTandOUT() {
		DocLitWrappedProxy port = getPort();
		
		// pupulate IN params
		int a = Constants.THE_INT;
		MyComplexType b = new MyComplexType();
		b.setA(42);
		b.setB(Constants.THE_STRING);
		
		// populate INOUT params
		Holder<String> c = new Holder<String>(Constants.THE_STRING);
		Holder<MyComplexType> d = new Holder<MyComplexType>(new MyComplexType());
		d.value.setA(42);
		d.value.setB(Constants.THE_STRING);
		
		// OUT params...do not populate
		Holder<Integer> e = new Holder<Integer>();
		Holder<MyComplexType> f = new Holder<MyComplexType>();
				
		// invoke
		port.twoWayMulti(a, c, b, d, e, f);
		
		// verify INOUT params 
		assertEquals("INOUT param C does not match", Constants.THE_STRING, c.value);
		assertEquals("INOUT param D does not match", Constants.THE_STRING, d.value.getB());
		
		// verify OUT params
		assertNotNull("OUT Param E is null", e.value);
		assertEquals("INOUT param C does not match", Integer.valueOf(Constants.THE_INT), e.value);
		
		assertNotNull("OUT Param F is null", f.value);
		assertEquals("INOUT param D does not match", Constants.THE_STRING, f.value.getB());
		
	}	
	
	/**
	 * Test sendng messages to a two-way interface "void x(void)". Testing to
	 * see if the impementation can handle empty wrapper objects
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTwoWay_Empty() {
		DocLitWrappedProxy port = getPort();
		port.twoWaySilly();
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

		Map<String, Object> reqCtxt = ((BindingProvider) port)
				.getRequestContext();
		reqCtxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				DOCLITWR_ENDPOINT);

		return port;
	}

}
