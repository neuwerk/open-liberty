//
// @(#) 1.1 autoFVT/src/saaj13dev/devtest/wsfvt/saaj13/OMConversionTest.java, WAS.websvcs.fvt, WASX.FVT 8/2/06 08:55:37 [7/23/07 09:49:02]
//
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W60, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 5/18/06  scheu       LIDB4238        Create 

package saaj13dev.devtest.wsfvt.saaj13;

import java.io.StringReader;

import javax.xml.soap.SOAPEnvelope;

import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;



/**
 * Test conversions between OM <-> SAAJ
 * Currently stubbed out.  Functions will be provided 
 * using conversion function delivered in Axis2 JAX-WS
 * @author scheu
 *1
 */
/**
 * OMConversionTest
 *
 */
/**
 * OMConversionTest
 *
 */
public class OMConversionTest extends TestBase {

	public String xmlStringRaw =
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
        " <soapenv:Header>\n" +
        "  <shw:Hello xmlns:shw=\"http://www.jcommerce.net/soap/ns/SOAPHelloWorld\">\n" +
        "    <shw:Myname>Tony</shw:Myname>\n" +
        "  </shw:Hello>\n" +
        "  <pre1:First xmlns:pre1=\"http://firstNamespace\">\n" +
        "     <pre1:name>first</pre1:name>\n" +
        "     <pre1:name2>first</pre1:name2>\n" +
        "  </pre1:First>\n" +
        "  <pre2:Second xmlns:pre2=\"http://secondNamespace\">\n" +
        "     <pre2:name>second</pre2:name>\n" +
        "     <pre2:name2>second</pre2:name2>\n" +
        "  </pre2:Second>\n" +
        " </soapenv:Header>\n" +
        " <soapenv:Body>\n" +
        "  <shw:Address xmlns:shw=\"http://www.jcommerce.net/soap/ns/SOAPHelloWorld\">\n" +
        "    <shw:City>GENT</shw:City>\n" +
        "  </shw:Address>\n" +
        " </soapenv:Body>\n" +
        "</soapenv:Envelope>";
	
	
	public OMConversionTest(String arg0) {
		super(arg0);
	}
	
	 protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
	     System.out.println("Do not need suiteSetup since no application is installed");    
	 }

	/**
	 * testStrategy: NOOP Test to avoid Eclipse warning
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testNOOP() {}
	
	/*
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test001() throws Exception{
		// Create an OMDocument
		OMDocument doc = getOMDocument(xmlStringRaw);
		
		// Convert OM to SAAJ
		SAAJConverter converter = SAAJConverter.newInstance();
		SOAPEnvelope env = converter.toSAAJ((org.apache.axiom.soap.SOAPEnvelope)doc.getOMDocumentElement());
		String saajString = env.toString();
		assertTrue("Failure: " + xmlStringRaw + " " + saajString, saajString.equals(xmlStringRaw));
		
		// Convert SAAJ back to OM
		org.apache.axiom.soap.SOAPEnvelope omEnvelope = converter.toOM(env);
		String omString = omEnvelope.toStringWithConsume();
		omString = omString.substring(omString.indexOf("<soapenv"));
		System.out.println(xmlStringRaw);
		System.out.println(omString);
		assertTrue("Failure: " + xmlStringRaw + " " + omString, omString.equals(xmlStringRaw));
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test002() throws Exception{
		// Create an OMDocument
		OMDocument doc = getOMDocument(xmlStringRaw);
		
		// Convert OM to SAAJ
		SAAJConverter converter =  new IBMSAAJConverterImpl();
		SOAPEnvelope env = converter.toSAAJ((org.apache.axiom.soap.SOAPEnvelope)doc.getOMDocumentElement());
		//String saajString = env.toString();
		//assertTrue("Failure: " + xmlStringRaw + " " + saajString, saajString.equals(xmlStringRaw));
		
		// Convert SAAJ back to OM
		org.apache.axiom.soap.SOAPEnvelope omEnvelope = converter.toOM(env);
		String omString = omEnvelope.toStringWithConsume();
		omString = omString.substring(omString.indexOf("<soapenv"));
		System.out.println(xmlStringRaw);
		System.out.println(omString);
		assertTrue("Failure: " + xmlStringRaw + " " + omString, omString.equals(xmlStringRaw));
		
		// Make sure the XMLStreamReader under the body is consumed.
		assertTrue(env.getBody().getFirstChild() == null);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test003() throws Exception{
		// Create an OMDocument
		OMDocument doc = getOMDocument(xmlStringRaw);
		
		// Convert OM to SAAJ
		SAAJConverter converter =  new IBMSAAJConverterImpl2();
		SOAPEnvelope env = converter.toSAAJ((org.apache.axiom.soap.SOAPEnvelope)doc.getOMDocumentElement());
		//String saajString = env.toString();
		//assertTrue("Failure: " + xmlStringRaw + " " + saajString, saajString.equals(xmlStringRaw));
		
		// Convert SAAJ back to OM
		org.apache.axiom.soap.SOAPEnvelope omEnvelope = converter.toOM(env);
		String omString = omEnvelope.toStringWithConsume();
		omString = omString.substring(omString.indexOf("<soapenv"));
		System.out.println(xmlStringRaw);
		System.out.println(omString);
		assertTrue("Failure: " + xmlStringRaw + " " + omString, omString.equals(xmlStringRaw));
		
		// Make sure the XMLStreamReader under the body is consumed.
		assertTrue(env.getBody().getFirstChild() == null);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test004() throws Exception{
		// Create an OMDocument
		OMDocument doc = getOMDocument(xmlStringRaw);
		
		// Convert OM to SAAJ
		SAAJConverter converter =  new IBMSAAJConverterImpl3();
		SOAPEnvelope env = converter.toSAAJ((org.apache.axiom.soap.SOAPEnvelope)doc.getOMDocumentElement());
		//String saajString = env.toString();
		//assertTrue("Failure: " + xmlStringRaw + " " + saajString, saajString.equals(xmlStringRaw));
		
		// Convert SAAJ back to OM
		org.apache.axiom.soap.SOAPEnvelope omEnvelope = converter.toOM(env);
		String omString = omEnvelope.toStringWithConsume();
		omString = omString.substring(omString.indexOf("<soapenv"));
		System.out.println(xmlStringRaw);
		System.out.println(omString);
		assertTrue("Failure: " + xmlStringRaw + " " + omString, omString.equals(xmlStringRaw));
		
		// Make sure the XMLStreamReader under the body is consumed.
		assertTrue(env.getBody().getFirstChild() == null);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test005() throws Exception{
		// Create an OMDocument
		OMDocument doc = getOMDocument(xmlStringRaw);
		
		// Convert OM to SAAJ
		IBMSAAJConverterImpl3 converter =  new IBMSAAJConverterImpl3();
		SOAPEnvelope env = converter.toSAAJ((org.apache.axiom.soap.SOAPEnvelope)doc.getOMDocumentElement());
		//String saajString = env.toString();
		//assertTrue("Failure: " + xmlStringRaw + " " + saajString, saajString.equals(xmlStringRaw));
		
		// Convert SAAJ back to OM using message proxy
		StAXProxy proxy = new SAAJSoapEnvelopeProxyImpl();
		proxy.createFrom(env);
		org.apache.axiom.soap.SOAPEnvelope omEnvelope = converter.toOM(proxy, false, true);
		String omString = omEnvelope.toStringWithConsume();
		omString = omString.substring(omString.indexOf("<soapenv"));
		System.out.println(xmlStringRaw);
		System.out.println(omString);
		assertTrue("Failure: " + xmlStringRaw + " " + omString, omString.equals(xmlStringRaw));
		
		// Make sure the XMLStreamReader under the body is consumed.
		assertTrue(env.getBody().getFirstChild() == null);
	}
	*/
	/**
	 * Utility method that returns an OMDocument representing the xml String
	 * @param xml
	 * @return
	 */
	/*
	private OMDocument getOMDocument(String xml) {
        try {
            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
            StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(xmlStreamReader, null);
            return builder.getDocument();
        } catch (XMLStreamException e) {
            throw new UnsupportedOperationException();
        }
    }
    */
	
}
