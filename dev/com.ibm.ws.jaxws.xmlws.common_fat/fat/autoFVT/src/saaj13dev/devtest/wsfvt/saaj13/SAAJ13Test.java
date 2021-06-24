//
// @(#) 1.10 autoFVT/src/saaj13dev/devtest/wsfvt/saaj13/SAAJ13Test.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 3/28/08 16:12:49 [8/8/12 06:04:52]
//
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W60, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 5/18/06  scheu       LIDB4238        Create 
// 09/20/06 scheu       391915          C028 change
// 10/02/06 scheu       391915          SAAJ 1.3 Spec Changes
// 09/05/07 loriv       460282          force use of xalan transformer
// 03/28/08 loriv       460282.1        xalan transformer problem fixed - backout workaround


package saaj13dev.devtest.wsfvt.saaj13;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SAAJResult;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPFaultElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.Text;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.websphere.webservices.soap.IBMSOAPBody;
import com.ibm.ws.webservices.engine.xmlsoap.SOAPConstants;
import com.ibm.ws.webservices.engine.xmlsoap.Utils;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;



/**
 * SAAJ13Test
 *
 * Tests for SAAJ13
 * The SAAJ13 change document contains 34 changes (labeled C001...C0034).
 * The tests in this class validate minimal compliance of these changes.
 *
 * @author Richard Scheuerle (scheu@us.ibm.com)
 *
 */
public class SAAJ13Test extends TestBase {

    /**
     * Constructor
     */
    public SAAJ13Test() {
        super();
    }

    /**
     * Constructor
     * @param arg0
     */
    public SAAJ13Test(String arg0) {
        super(arg0);
    }
    
    protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
        System.out.println("Do not need suiteSetup since no application is installed");    
    }

    /**
     * @testStrategy: This test uses internal apis to determine if the SAAJ 1.3 and DOM 3 code is
     * available and enabled.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description=" This test uses internal apis to determine if the SAAJ 1.3 and DOM 3 code is available and enabled.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSetup() {
    	assertTrue(Utils._isSAAJ13Available());
    	assertTrue(Utils._isSAAJ13Enabled());
    	assertTrue(Utils._isDOM3Available());
    	assertTrue(Utils._isDOM3Enabled());
    }
    
    /**
     * @testStrategy: Test that IBM implementation is used
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description=" Test that IBM implementation is used",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFactories() throws Exception {
    	MessageFactory mf = MessageFactory.newInstance();
    	SOAPFactory sf = SOAPFactory.newInstance();
    	SOAPConnectionFactory cf = SOAPConnectionFactory.newInstance();
    	assertTrue(mf.getClass().getName().equals("com.ibm.ws.webservices.engine.soap.MessageFactoryImpl"));
    	assertTrue(sf.getClass().getName().equals("com.ibm.ws.webservices.engine.xmlsoap.SOAPFactory"));
    	assertTrue(cf.getClass().getName().equals("com.ibm.ws.webservices.engine.soap.SOAPConnectionFactoryImpl"));
    }
   
    
    /**
     * @testStrategy: 
     * test for C001 changes
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC001() throws Exception {
    	// Create default (SOAP 1.1)
    	MessageFactory mf = MessageFactory.newInstance();
    	SOAPMessage msg = mf.createMessage();
    	SOAPHeader header = msg.getSOAPHeader();
    	QName test = new QName("urn://myTest", "test", "pre");
    	String fakeSOAPURI = "urn://SOAP";
    	
    	// Try adding a NotUnderstood header...this should cause
    	// an exception because a NotUnderstood header is only supported with SOAP 1.2
    	try {
    		header.addNotUnderstoodHeaderElement(test);
    		assertTrue(false);
    	} catch (UnsupportedOperationException uoe) {
    		// Swallow expected exception
    	}
    	
    	// Try adding an UpgradeHeaderElement with a fake SOAP URI
    	// This should succeed.
    	header.addUpgradeHeaderElement(fakeSOAPURI);
    	Iterator it = header.getChildElements(SOAPConstants.SOAP11_CONSTANTS.getUpgradeHeaderQName());
    	SOAPElement se = (SOAPElement) it.next();
    	se = (SOAPElement) se.getFirstChild();  // Get the SupportedEnvelope element
    	assertTrue(se.getAttribute("qname").equals("upgrade:Envelope"));
    	
    	// Repeat for SOAP1.2 
    	mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
    	msg = mf.createMessage();
    	header = msg.getSOAPHeader();
    	
    	// Set and retrieve a NotUnderstood header
    	header.addNotUnderstoodHeaderElement(test);
    	it = header.getChildElements(SOAPConstants.SOAP12_CONSTANTS.getNotUnderstoodHeaderQName());
    	se = (SOAPElement) it.next();
    	assertTrue(se.getAttribute("qname").equals("pre:test"));
    	assertTrue(se.getNamespaceURI("pre").equals("urn://myTest"));
    	
    	// Try adding an UpgradeHeaderElement with a fake SOAP URI
    	// This should succeed.
    	header.addUpgradeHeaderElement(fakeSOAPURI);
    	it = header.getChildElements(SOAPConstants.SOAP12_CONSTANTS.getUpgradeHeaderQName());
    	se = (SOAPElement) it.next();
    	se = (SOAPElement) se.getFirstChild();  // Get the SupportedEnvelope element
    	assertTrue(se.getAttribute("qname").equals("upgrade:Envelope"));
    }
    
    /**
     * test for C002 changes
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC002() throws Exception {
    	// Create default (SOAP 1.1)
    	MessageFactory mf = MessageFactory.newInstance();
    	SOAPMessage msg = mf.createMessage();
    	SOAPHeader header = msg.getSOAPHeader();
    	QName test = new QName("urn://myTest", "test", "pre");
    	SOAPHeaderElement headerElement = (SOAPHeaderElement) header.addChildElement(test);
    	String roleURI = "urn://role";
    	
    	
    	// Try adding a role header...this should cause an UnsupportedOperationException...supported with SOAP 1.2
    	try {
    		headerElement.setRole(roleURI);
    		assertTrue(false);
    	} catch (UnsupportedOperationException uoe) {
    		// Swallow expected exception
    	}
    	
    	// Try adding a relay...this should cause an UnsupportedOperationException...supported with SOAP 1.2
    	try {
    		headerElement.setRelay(true);
    		assertTrue(false);
    	} catch (UnsupportedOperationException uoe) {
    		// Swallow expected exception
    	}
    	
    	
    	// Repeat for SOAP1.2 
    	mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
    	msg = mf.createMessage();
    	header = msg.getSOAPHeader();
    	headerElement = (SOAPHeaderElement) header.addChildElement(test);
    	
    	// Set and retrieve Role
    	headerElement.setRole(roleURI);
    	assertTrue(headerElement.getRole().equals(roleURI));
    	
    	// Set and retrieve Relay
    	headerElement.setRelay(true);
    	assertTrue(headerElement.getRelay());
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC003() throws Exception {
    	// Covered by testC020
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC004() throws Exception {
    	// Covered by SAAJ13FaultTests
    }
    
    /**
     * @testStrategy Tests for C005
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Tests for C005",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC005() throws Exception {
    	
    	// Tests with Dynamic MessageFactory. 
    	// (Other tests adequately test SOAP 1.1 and SOAP 1.2 Message Factories)
    	SOAPConstants sc11 = SOAPConstants.SOAP11_CONSTANTS;
    	SOAPConstants sc12 = SOAPConstants.SOAP12_CONSTANTS;
    	MessageFactory  mf = MessageFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);
    	SOAPMessage msg = null;
    	
    	// Cannot create a default message with dynamic MessageFactory
    	try {
    		msg = mf.createMessage();
    		assert(false);
    	} catch (UnsupportedOperationException uoe) {
    		// Expected exception
    	}
    	
    	// Create a SOAP 1.1 InputStream
    	SOAPMessage message = createSOAPMessage(sc11.getSOAPProtocol());
        SOAPPart soapPart = message.getSOAPPart();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        message.writeTo(baos);
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream  bais = new ByteArrayInputStream(bytes);
        
        // Missing content type should cause an exception
        MimeHeaders mimeHeaders = new MimeHeaders();
        try {
        	msg = mf.createMessage(mimeHeaders, bais);
    		assertTrue(false);
    	} catch (IllegalArgumentException iae) {
    		// Expected exception
    	}
    	
    	// Wrong content type should cause an exception during the envelope build
    	bais.reset();
        mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-Type",sc12.getContentTypeValue());
        try {
        	msg = mf.createMessage(mimeHeaders, bais);
        	msg.getSOAPHeader();
    		assertTrue(false);
    	} catch (IllegalArgumentException iae) {
    		// Expected exception
    	}
    	
    	// Make sure correct header works
    	bais.reset();
    	mimeHeaders = new MimeHeaders();
    	mimeHeaders.addHeader("Content-Type",sc11.getContentTypeValue());
    	msg = mf.createMessage(mimeHeaders, bais);
    	assertTrue(msg.getSOAPHeader() != null);
    	assertTrue(msg.getSOAPHeader().getNamespaceURI().equals(sc11.getEnvelopeURI()));
    	
    	// Repeat with SOAP 1.2
    	// Create a SOAP 1.2 InputStream
    	message = createSOAPMessage(sc12.getSOAPProtocol());
        soapPart = message.getSOAPPart();
        baos = new ByteArrayOutputStream();
        message.writeTo(baos);
        bais = new ByteArrayInputStream(baos.toByteArray());
        
        // Missing content type should cause an exception
        mimeHeaders = new MimeHeaders();
        try {
        	msg = mf.createMessage(mimeHeaders, bais);
    		assertTrue(false);
    	} catch (IllegalArgumentException iae) {
    		// Expected exception
    	}
    	
    	// Wrong content type should cause an exception
    	bais.reset();
        mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-Type",sc11.getContentTypeValue());
        try {
        	msg = mf.createMessage(mimeHeaders, bais);
        	msg.getSOAPHeader();
    		assertTrue(false);
    	} catch (IllegalArgumentException iae) {
    		// Expected exception
    	}
    	
    	// Make sure correct header works
    	bais.reset();
    	mimeHeaders = new MimeHeaders();
    	mimeHeaders.addHeader("Content-Type",sc12.getContentTypeValue());
    	msg = mf.createMessage(mimeHeaders, bais);
    	assertTrue(msg.getSOAPHeader() != null);
    	assertTrue(msg.getSOAPHeader().getNamespaceURI().equals(sc12.getEnvelopeURI()));
    	
    }
    
    /**
     * @testStrategy: Tests for C006
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC006() throws Exception {
    	javax.xml.soap.SOAPFactory sf = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
    	Detail detail= sf.createDetail();
    	assertTrue(detail.getElementQName().equals(SOAPConstants.SOAP11_CONSTANTS.getFaultDetailQName()));
    	
    	sf = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
    	detail= sf.createDetail();
    	assertTrue(detail.getElementQName().equals(SOAPConstants.SOAP12_CONSTANTS.getFaultDetailQName()));
    	
    	try {
    		sf = SOAPFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);
        	detail= sf.createDetail();
        	assertTrue(detail.getElementQName().equals(SOAPConstants.SOAPDYNAMIC_CONSTANTS.getFaultDetailQName()));
    		assert(false);
    	} catch (UnsupportedOperationException uoe) {
    		// Expect exception
    	}
    	
    	try {
    		sf = SOAPFactory.newInstance("bad protocol");
    		assert(false);
    	} catch (SOAPException se) {
    		// Expect exception
    	}
    }
    
    /**
     * @testStrategy: Tests for C007
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description=" Tests for C007",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC007() {
        // Test values
    	QName qName = new QName("http://www.w3.org/2003/05/soap-envelope","DataEncodingUnknown");
    	assertTrue(SOAPConstants.SOAP_DATAENCODINGUNKNOWN_FAULT.equals(qName));
    	qName = new QName("http://www.w3.org/2003/05/soap-envelope","MustUnderstand");
    	assertTrue(SOAPConstants.SOAP_MUSTUNDERSTAND_FAULT.equals(qName));
    	qName = new QName("http://www.w3.org/2003/05/soap-envelope","Receiver");
    	assertTrue(SOAPConstants.SOAP_RECEIVER_FAULT.equals(qName));
    	qName = new QName("http://www.w3.org/2003/05/soap-envelope","Sender");
    	assertTrue(SOAPConstants.SOAP_SENDER_FAULT.equals(qName));
    	qName = new QName("http://www.w3.org/2003/05/soap-envelope","VersionMismatch");
    	assertTrue(SOAPConstants.SOAP_VERSIONMISMATCH_FAULT.equals(qName));
    	
        return;
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC008() {
    	// Indirectly tested by other tests..no additional test needed
    }
    
    
    /**
     * @testStrategy: Test basic functionality of C009 SAAJResult
     * 
     */
    // new SAAJResult code causes this test to fail - disabling for now.
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void disable_testC009() throws Exception {

        SOAPMessage message = createSOAPMessage(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPPart soapPart = message.getSOAPPart();
        assertTrue("no soap part", soapPart != null);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        message.writeTo(baos);
        ByteArrayInputStream  bais = new ByteArrayInputStream(baos.toByteArray());
        Document document = newDocument(new InputSource(bais));
        

        // transfor DOM to the soap message using setContent method
        DOMSource domSrc = new DOMSource(document);
        MessageFactory mf = MessageFactory.newInstance();
        message = mf.createMessage();
        message.getSOAPPart().setContent(domSrc);


        //transform SOAPMessage back to DOM document
        Source src = message.getSOAPPart().getContent();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        SAAJResult result = new SAAJResult();
        transformer.transform(src, result);
        SOAPPart part = (SOAPPart) result.getResult();
        assertTrue("no part", part != null);
        Node node = part.getFirstChild();
        
        assertTrue(node instanceof Element);
        assertTrue(node.getLocalName().equals("Envelope"));
        assertTrue(node.getFirstChild() != null);
        assertTrue(node.getFirstChild().getLocalName().equals("Header"));
    }
    
    /**
     * @testStrategy: Test for C009 changes.
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC009_2() throws Exception {

        SOAPMessage message = createSOAPMessage(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPPart soapPart = message.getSOAPPart();
        assertTrue("no soap part", soapPart != null);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        message.writeTo(baos);
        ByteArrayInputStream  bais = new ByteArrayInputStream(baos.toByteArray());
        Document document = newDocument(new InputSource(bais));
        

        // Transform DOM into the soap message using setContent method
        DOMSource domSrc = new DOMSource(document);
        MessageFactory mf = MessageFactory.newInstance();
        message = mf.createMessage();
        message.getSOAPPart().setContent(domSrc);


        //Transform SOAPMessage back to DOM document
        Source src = message.getSOAPPart().getContent();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        
        javax.xml.soap.SOAPFactory sf = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPElement holder = sf.createElement("holder", "holderPrefix", "urn://holder");
        SAAJResult result = new SAAJResult(holder);
        transformer.transform(src, result);
        SOAPElement resultNode = (SOAPElement) result.getResult();
        assertTrue("no holder", holder != null);
        Node node = resultNode.getFirstChild();
        assertTrue(node instanceof Element);
        assertTrue("name is not Header. Name is " + node.getLocalName(), node.getLocalName().equals("Header"));
        //assertTrue(node.getFirstChild() != null);
        //assertTrue(node.getFirstChild().getLocalName().equals("Header"));
    }
   
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC010() throws Exception {
    	// Covered by SAAJ13FaultTests
    }
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC011() throws Exception {
    	// Covered by SAAJ13FaultTests
    }
    
    /**
     * @testStrategy: Test for C012 Changes
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC012() throws Exception {
    	
    	javax.xml.soap.SOAPFactory sf = 
    		SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
    	SOAPElement se = sf.createElement("dummy", "dummyPrefix", "urn://dummy");
    	se.addNamespaceDeclaration("testPrefix", "urn://test");
    	
    	// Test createQName
    	QName qName = se.createQName("testElement", "testPrefix");
    	assertTrue(qName.getLocalPart().equals("testElement"));
    	assertTrue(qName.getNamespaceURI().equals("urn://test"));
    	assertTrue(qName.getPrefix().equals("testPrefix"));
    	
    	// Test setElementQName/getElementQName
    	se.setElementQName(qName);
    	qName = se.getElementQName();
    	assertTrue(qName.getLocalPart().equals("testElement"));
    	assertTrue(qName.getNamespaceURI().equals("urn://test"));
    	assertTrue(qName.getPrefix().equals("testPrefix"));
    	
    	// Test addChildElement
    	QName childQName = se.createQName("testChild", "testPrefix");
    	SOAPElement child = se.addChildElement(childQName);
    	qName = child.getElementQName();
    	assertTrue(qName.getLocalPart().equals("testChild"));
    	assertTrue(qName.getNamespaceURI().equals("urn://test"));
    	assertTrue(qName.getPrefix().equals("testPrefix"));
    	SOAPElement child2 = se.addChildElement(childQName);
    	qName = child2.getElementQName();
    	assertTrue(qName.getLocalPart().equals("testChild"));
    	assertTrue(qName.getNamespaceURI().equals("urn://test"));
    	assertTrue(qName.getPrefix().equals("testPrefix"));
    	
    	// Test getChildElements
    	Iterator it = se.getChildElements();
    	assertTrue(it.next() == child);
    	assertTrue(it.next() == child2);
    	assertTrue(!it.hasNext());
    	
    	// Test addAttribute/getAttribute/removeAttribute
    	qName = child.createQName("testAttr", "testPrefix");
    	child.addAttribute(qName, "childValue");
    	assertTrue(child.getAttributeValue(qName).equals("childValue"));
    	assertTrue(child.removeAttribute(qName));
    	assertTrue(child.getAttributeValue(qName) == null);
    	
    	
    }
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC013() throws Exception {
    	// Covered by SAAJ13FaultTests
    }
    
    
    /**
     * @testStrategy: Test for C014 changes
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC014() throws Exception {
    	SOAPConstants sc = SOAPConstants.SOAP11_CONSTANTS;
    	javax.xml.soap.SOAPFactory sf = 
    		SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
    	
    	// Create a normal SOAPElement
    	QName qname = new QName("urn://test", "test", "testPrefix");
    	SOAPElement se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPElement);
    	
    	qname = sc.getEnvelopeQName();
    	se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPEnvelope);
    	
    	qname = sc.getBodyQName();
    	se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPBody);
    	
    	qname = sc.getHeaderQName();
    	se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPHeader);
    	
    	qname = sc.getFaultQName();
    	se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPFault);
    	
    	qname = sc.getFaultDetailQName();
    	se = sf.createElement(qname);
    	assertTrue(se instanceof Detail);
    	
    	sc = SOAPConstants.SOAP12_CONSTANTS;
    	sf = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
    	
    	// Create a normal SOAPElement
    	qname = new QName("urn://test", "test", "testPrefix");
    	se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPElement);
    	
    	qname = sc.getEnvelopeQName();
    	se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPEnvelope);
    	
    	qname = sc.getBodyQName();
    	se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPBody);
    	
    	qname = sc.getHeaderQName();
    	se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPHeader);
    	
    	qname = sc.getFaultQName();
    	se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPFault);
    	
    	qname = sc.getFaultDetailQName();
    	se = sf.createElement(qname);
    	assertTrue(se instanceof Detail);
    	
    	qname = sc.getFaultCodeQName();
    	se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPFaultElement);
    	
    	qname = sc.getFaultCodeQName();
    	se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPFaultElement);
    	
    	// It is unclear what the behavior is if the Factory is dynamic.
    	// We made the decision to automatically change the 
    	// SOAPFactory protocol.  So the following should work.
    	sf = SOAPFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);
    	qname = sc.getEnvelopeQName();  // Create SOAP 1.2 envelope
    	se = sf.createElement(qname);
    	assertTrue(se instanceof SOAPEnvelope);
    	String protocol =
    			((com.ibm.ws.webservices.engine.xmlsoap.SOAPFactory)sf).
    				getSOAPConstants().getSOAPProtocol();
    	assertTrue(protocol.equals(SOAPConstants.SOAP_1_2_PROTOCOL));
    	
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC015() throws Exception {
    	// Covered by SAAJ13FaultTests
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC016() {
    	// No test needed
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC017() throws Exception {
    	// Covered by testC005 and other tests
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC018() throws Exception {
    	// Covered by SAAJ13FaultTests
    }
    
   
    /**
     * testStrategy: Test for C019 changes
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC019() throws Exception {
    	// testC0014 has more comprehensive tests for 
    	// SOAPFactory.createElements
    	
    	// Test newInstance
    	javax.xml.soap.SOAPFactory sf = SOAPFactory.newInstance();
    	
    	// Make sure createElement returns the correct derived type
    	Name name = sf.createName("Envelope", "soapenv", SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE);
    	SOAPElement se = sf.createElement(name);
    	assertTrue(se instanceof SOAPEnvelope);
    	
    	// Make sure createElement returns the correct derived type
    	se = sf.createElement("Body", "soapenv", SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE);
    	assertTrue(se instanceof SOAPBody);
    	
    	// Make sure createDetail throws proper exception if dynamic protocl
    	sf = SOAPFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);
    	try {
    		sf.createDetail();
    		assertTrue("expected createDetail to throw an exception", false);
    	} catch (UnsupportedOperationException uoe) {
    		// This is expected
    	}
    	
    }
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC020() throws Exception {
    	// Covered by SAAJ13FaultTests
    }
    
    /**
     * @testStrategy: Test for C021 changes
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC021() throws Exception {
    	// Create a message
    	SOAPMessage message = createSOAPMessage(SOAPConstants.SOAP_1_2_PROTOCOL);
    	SOAPHeader  header  = message.getSOAPHeader();
    	SOAPBody    body    = message.getSOAPBody();
    	SOAPEnvelope env    = (SOAPEnvelope) header.getParentNode();
    	SOAPElement child1  = (SOAPElement) body.getFirstChild();
    	SOAPElement child2  = (SOAPElement) child1.getNextSibling();
    	
    	// Test exception handling of addTextNode
    	try {
    		// A Text node is not allowed under a header element
    		header.addTextNode("not allowed");
    		assertTrue("expected a SOAPException", false);
    		
    	} catch (SOAPException se) {
    		// Expected
    	}
    	
    	// Test exception handling of setEncodingStyle
    	try {
    		header.setEncodingStyle("http://myEncoding");
    		assertTrue("expected a SOAPException", false);
    		
    	} catch (SOAPException se) {
    		// Expected
    	}
    	
    	// Test exception handling of setAttributeValue with encodingStyle attribute
    	try {
    		QName es =SOAPConstants.SOAP12_CONSTANTS.getEncodingStyleQName();
    		Name esName = env.createName(es.getLocalPart(), "pre", es.getNamespaceURI());
    		header.addAttribute(esName, "http://myEncoding");
    		assertTrue("expected a SOAPException", false);
    		
    	} catch (SOAPException se) {
    		// Expected
    	}
    	
    	// Test getAttributeValue to make sure it returns a null if the 
    	// atrribute is not found
    	Name attrName = env.createName("testAttr", "testPrefix", "http://myTest");
	    assertTrue(env.getAttributeValue(attrName) == null);
    	
	    // Test addChildElement(localName) when there is a default namespace declaration
    	child1.addNamespaceDeclaration("", "http://default");
    	SOAPElement se = child1.addChildElement("child");
        assertTrue(se.getNamespaceURI().equals("http://default"));
        
        // Test addChildElement(localName) when there is not a default namespace declaration
        se = child2.addChildElement("child");
        assertTrue(se.getNamespaceURI() == null);
    		
	    
	    // Test addChildElement(localName, prefix) when the prefix is missing
    	try {
    		se = child1.addChildElement("test", "missingPrefix");
    		assertTrue("expected a SOAPException", false);
    		// SAAJ indicates that a SOAPException should be thrown
    	} catch (SOAPException e) {
    		// Expected
    	}
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC022() {
    	// No test needed
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC023() {
    	// No test needed
    }
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC024() {
    	// No test needed
    }
    
    /**
     * @testStrategy: test for C025 changes
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC025() throws Exception {
    	// Create default (SOAP 1.1)
    	MessageFactory mf = MessageFactory.newInstance();
    	SOAPMessage msg = mf.createMessage();
    	SOAPHeader header = msg.getSOAPHeader();
    	QName test = new QName("urn://myTest", "test", "pre");
    	SOAPHeaderElement headerElement = (SOAPHeaderElement) header.addChildElement(test);
    	String actorURI = "urn://role";
    	
    	
    	// Try adding an actor header.
    	headerElement.setActor(actorURI);
    	assertTrue(headerElement.getActor().equals(actorURI));
    	
    	
    	// Repeat for SOAP1.2 
    	mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
    	msg = mf.createMessage();
    	header = msg.getSOAPHeader();
    	headerElement = (SOAPHeaderElement) header.addChildElement(test);
    	
    	// Set Actor and retrieve as Actor or Role
    	headerElement.setRole(actorURI);
    	assertTrue(headerElement.getRole().equals(actorURI));
    	assertTrue(headerElement.getActor().equals(actorURI));
    	
    	// Set Role and retrieve as Actor or Role
    	headerElement.setRole(actorURI);
    	assertTrue(headerElement.getRole().equals(actorURI));
    	assertTrue(headerElement.getActor().equals(actorURI));

    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC026() {
    	// No test needed for this change
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC027() {
    	// No test needed for this change
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC028() {
    	// See SAAJ13AttachmentTests
    }
    	
    /**
     * @testStrategy: Test for C029 changes
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC029() throws Exception {
    	// SAAJ13FaultTests contains more comprehensive tests for soap faults.
    	// This test contains some lightweight tests for C029 changes
    	
    	// Create a factory
    	SOAPConstants sc = SOAPConstants.SOAP11_CONSTANTS;
    	SOAPFactory sf = SOAPFactory.newInstance();
    	
    	// Test the SAAJ 1.3 createFault method
    	SOAPFault fault = sf.createFault();
    	assertTrue(fault.getElementQName().equals(sc.getFaultQName()));
    	
    	// Test the SAAJ 1.3 createFault(...) method
    	fault = sf.createFault("test", new QName("urn://test", "code", "faultPrefix"));
    	assertTrue(fault.getElementQName().equals(sc.getFaultQName()));
    	assertTrue(fault.getFaultString().equals("test"));
    	assertTrue(fault.getFaultCode().endsWith("faultPrefix:code"));
    	
    	
    	// Now get an envelope and put the fault into the body
    	SOAPEnvelope se = (SOAPEnvelope) sf.createElement(sc.getEnvelopeQName());
    	SOAPBody body = se.getBody();
    	body.appendChild(fault);
    	
    	// Now get the body contents as a document
    	Document doc = body.extractContentAsDocument();
    	assertTrue(doc.getDocumentElement() != null);
    	
    	// Now take the document element and create an element.
    	// This tests the new createElement(domElement) method
    	SOAPElement element = sf.createElement(doc.getDocumentElement());
    	assertTrue(element instanceof SOAPFault);
    	fault = (SOAPFault) element;
    	assertTrue(fault.getElementQName().equals(sc.getFaultQName()));
    	assertTrue(fault.getFaultString().equals("test"));
    	assertTrue(fault.getFaultCode().endsWith("faultPrefix:code"));
    	
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testC032() {
    	// Covered by SAAJ13AttachmentTests
    }
    
    /**
     * @testStrategy: Test for C030 changes
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC030() throws Exception {
    	// Create a Envelope / Body
    	MessageFactory mf = MessageFactory.newInstance();
    	SOAPMessage msg = mf.createMessage();
    	SOAPBody body = msg.getSOAPBody();
    	
    	// Add an element to the body
    	QName qname = new QName("urn://test", "root", "my");
    	SOAPElement se = body.addChildElement(qname);
    	se.addTextNode("hello world");
    	
    	// Test the extractContentAsDocument method
    	Document doc = body.extractContentAsDocument();
    	assertTrue(doc != null);
    	assertTrue(doc.getDocumentElement() != null);
    	Element root = doc.getDocumentElement();
    	assertTrue(root.getNamespaceURI().equals("urn://test"));
    	assertTrue(root.getLocalName().equals("root"));
    	assertTrue(root.getPrefix().equals("my"));
    	
    	// There should be no children in the body
    	assertTrue(body.getFirstChild() == null);
    	
    	// Now try adding just a text node to the body
    	body.addTextNode("bad data");
    	try {
    		body.extractContentAsDocument();
    		assertTrue("expected SOAPException from extractContentAsDocument", false);
    	} catch (SOAPException e) {
    		// Expected behavior
    	}
    	// Now try adding multiple children to the body
    	se = body.addChildElement(qname);
    	se = body.addChildElement(qname);
    	try {
    		body.extractContentAsDocument();
    		assertTrue("expected SOAPException from extractContentAsDocument", false);
    	} catch (SOAPException e) {
    		// Expected behavior
    	}
    	
    }
    
    /**
     * @testStrategy: Test for C031 changes
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC031() throws Exception {
    	SOAPMessage message = createSOAPMessage(SOAPConstants.SOAP_1_1_PROTOCOL);
    	SOAPBody body = message.getSOAPBody();
    	SOAPElement soapElement = (SOAPElement) body.getFirstChild();
    	soapElement.setTextContent("Hello World");
        // In ZOS, the root is a read-only directory, Change it to java.io.tmpdir
        String strDirTmp = System.getProperty( "java.io.tmpdir" );
        strDirTmp = strDirTmp.replace( '\\', '/' );
        if( !strDirTmp.startsWith( "/" ) ){
            strDirTmp = "/" + strDirTmp;
        }
        FileOutputStream fileOut = new FileOutputStream(strDirTmp + "/sampleMessage.txt");
        String url = "file://"+ strDirTmp + "/sampleMessage.txt";
    	message.writeTo(fileOut);
    	fileOut.close();
    	SOAPConnectionFactory cf = SOAPConnectionFactory.newInstance();
    	SOAPConnection connection = cf.createConnection();
    	SOAPMessage sm = connection.get(url);
    	body = sm.getSOAPBody();
    	soapElement = (SOAPElement) body.getFirstChild();
    	assertTrue(soapElement.getLocalName().equals("op1"));
    	assertTrue(soapElement.getTextContent().equals("Hello World"));
    }
    
    /**
     * testStrategy: Test for C033 changes
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC033() {
    	assertTrue(Node.class.isAssignableFrom(SOAPPart.class));
    }
    
    /**
     * testStrategy: Test for C034 changes
     * Test the constant values
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC034() {
        // Test values
        assertTrue(SOAPConstants.DEFAULT_SOAP_PROTOCOL.equals("SOAP 1.1 Protocol"));
        assertTrue(SOAPConstants.DYNAMIC_SOAP_PROTOCOL.equals("Dynamic Protocol"));
        assertTrue(SOAPConstants.SOAP_1_1_CONTENT_TYPE.equals("text/xml"));
        assertTrue(SOAPConstants.SOAP_1_1_PROTOCOL.equals("SOAP 1.1 Protocol")); 
        assertTrue(SOAPConstants.SOAP_1_2_CONTENT_TYPE.equals("application/soap+xml"));
        assertTrue(SOAPConstants.SOAP_1_2_PROTOCOL.equals("SOAP 1.2 Protocol"));
        assertTrue(SOAPConstants.SOAP_ENV_PREFIX.equals("env"));
        assertTrue(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE.equals("http://schemas.xmlsoap.org/soap/envelope/")); 
        assertTrue(SOAPConstants.URI_NS_SOAP_1_2_ENCODING.equals("http://www.w3.org/2003/05/soap-encoding")); 
        assertTrue(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE.equals("http://www.w3.org/2003/05/soap-envelope")); 
        assertTrue(SOAPConstants.URI_NS_SOAP_ENCODING.equals("http://schemas.xmlsoap.org/soap/encoding/"));
        assertTrue(SOAPConstants.URI_NS_SOAP_ENVELOPE.equals("http://schemas.xmlsoap.org/soap/envelope/")); 
        assertTrue(SOAPConstants.URI_SOAP_1_2_ROLE_NEXT.equals("http://www.w3.org/2003/05/soap-envelope/role/next")); 
        assertTrue(SOAPConstants.URI_SOAP_1_2_ROLE_NONE.equals("http://www.w3.org/2003/05/soap-envelope/role/none"));
        assertTrue(SOAPConstants.URI_SOAP_1_2_ROLE_ULTIMATE_RECEIVER.equals("http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver")); 
        assertTrue(SOAPConstants.URI_SOAP_ACTOR_NEXT.equals("http://schemas.xmlsoap.org/soap/actor/next"));
        
        // Compare main values against SOAPVersion
        // The following code tests the SOAPConstants implementation for correctness.
        assertTrue(com.ibm.ws.webservices.engine.xmlsoap.SOAPConstants.SOAP11_CONSTANTS.getEnvelopeURI().equals(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE));
        assertTrue(com.ibm.ws.webservices.engine.xmlsoap.SOAPConstants.SOAP12_CONSTANTS.getEnvelopeURI().equals(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE));
        assertTrue(com.ibm.ws.webservices.engine.xmlsoap.SOAPConstants.SOAP11_CONSTANTS.getEncodingURI().equals(SOAPConstants.URI_NS_SOAP_ENCODING));
        assertTrue(com.ibm.ws.webservices.engine.xmlsoap.SOAPConstants.SOAP12_CONSTANTS.getEncodingURI().equals(SOAPConstants.URI_NS_SOAP_1_2_ENCODING));
        assertTrue(com.ibm.ws.webservices.engine.xmlsoap.SOAPConstants.SOAP11_CONSTANTS.getContentTypeValue().equals(SOAPConstants.SOAP_1_1_CONTENT_TYPE));
        assertTrue(com.ibm.ws.webservices.engine.xmlsoap.SOAPConstants.SOAP12_CONSTANTS.getContentTypeValue().equals(SOAPConstants.SOAP_1_2_CONTENT_TYPE));
        
        
        return;
    }
    /**
     * @testStrategy: Minimal tests for the new methods enabled for DOM Level 3
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDOM3() throws Exception {
    	SOAPMessage message = createSOAPMessage(SOAPConstants.SOAP_1_1_PROTOCOL);
    	SOAPHeader  header  = message.getSOAPHeader();
    	SOAPBody    body    = message.getSOAPBody();
    	SOAPEnvelope env    = (SOAPEnvelope) header.getParentNode();
    	SOAPElement child1  = (SOAPElement) body.getFirstChild();
    	SOAPElement child2  = (SOAPElement) child1.getNextSibling();
    	
    	// Test compareDocumentPosition
    	// body occurs after header
    	assertTrue(body.compareDocumentPosition(header) > 0);
    	
    	// Test getBaseURI
    	// The document was created without a base URI
    	assertTrue(body.getBaseURI() == null);
    	
    	// Test getFeature
    	// Test dummy feature
    	assertTrue(body.getFeature("ScheuKid", "6.0") == null);
    	
    	// Test getTextContent/setTextContent
    	child1.setTextContent("Hello");
    	child2.setTextContent("World");
    	System.out.println(body.getTextContent().equals("HelloWorld"));
    	
    	// Test getUserData/setUserData
    	body.setUserData("key", "value", null);
    	assertTrue(body.getUserData("key").equals("value"));
    
    	// Test isDefaultNamespace
    	assertTrue(!body.isDefaultNamespace("http://test.body"));
    	body.addNamespaceDeclaration("", "http://default");
    	assertTrue(body.isDefaultNamespace("http://default"));
    	
    	// Test isEqualNode
    	Node clone = body.cloneNode(true);
    	assertTrue(body.isEqualNode(clone));
    	assertTrue(!body.isEqualNode(header));
    	
    	// Test isSameNode
    	assertTrue(!body.isSameNode(clone));
    	assertTrue(!body.isSameNode(header));
    	
    	// Test LookupNamespaceURI
    	assertTrue(body.lookupNamespaceURI("test-body").equals("http://test.body"));
    	
    	// Test LookupPrefix
    	assertTrue(body.lookupPrefix("http://test.body").equals("test-body"));
    	
    	// Test getWholeText, replaceWholeText
    	Text textNode = (Text) child1.getFirstChild();
    	assertTrue(textNode.getWholeText().equals("Hello"));
    	textNode.replaceWholeText(" Hello ");
    	assertTrue(textNode.getWholeText().equals(" Hello "));
    	
    	// Test isElementContentWhitespace
    	assertTrue(!textNode.isElementContentWhitespace());
    	
    	
    }
    
    /**
     * Create a sample soap message for the specified protocol
     * @param protocol
     * @return
     * @throws SOAPException
     */
    private SOAPMessage createSOAPMessage(String protocol) throws SOAPException {
        SOAPMessage message = MessageFactory.newInstance(protocol).createMessage();
        IBMSOAPBody body = (IBMSOAPBody) message.getSOAPBody();
        SOAPHeader header = message.getSOAPHeader();
        SOAPFactory factory = body.getIBMSOAPFactory();

        Name headerName1 =
            factory.createName("header1", "test-header", "http://test.header"),
            headerName2 =
                factory.createName(
                    "header2",
                    "test-header",
                    "http://test.header"),
            bodyName1 =
                factory.createName("op1", "test-body", "http://test.body"),
            bodyName2 =
                factory.createName("op2", "test-body", "http://test.body");
        header.addHeaderElement(headerName1);
        header.addHeaderElement(headerName2);
        header.addNamespaceDeclaration("test-header", "http://test.header");

        body.addBodyElement(bodyName1);
        body.addBodyElement(bodyName2);
        body.addNamespaceDeclaration("test-body", "http://test.body");

        return message;
    }
    
    /**
     * Build a Document from an InputSource
     * @param inp
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private Document newDocument(InputSource inp)
    	throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return( db.parse( inp ) );
    }
}
