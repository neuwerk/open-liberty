//
// @(#) 1.1 WautoFVT/src/saaj13dev/devtest/wsfvt/saaj13/EnvelopeTest.java, WAS.websvcs.fvt, WSFP.WFVT, a0712.20 8/2/06 08:55:19 [4/2/07 12:10:06]
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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import com.ibm.websphere.webservices.soap.IBMSOAPEnvelope;
import com.ibm.websphere.webservices.soap.IBMSOAPFault;
import com.ibm.websphere.webservices.soap.IBMSOAPHeaderElement;
import com.ibm.ws.webservices.engine.WebServicesFault;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;


/**
 * EnvelopeTest
 * Various Tests of SOAPEnvelope.  
 */
public class EnvelopeTest extends TestBase {

 public EnvelopeTest(String name) {
     super(name);
     xmlString = removeMixedContent(xmlStringRaw);
 }
 
 protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
     System.out.println("Do not need suiteSetup since no application is installed");    
 }

 String xmlString;
 String xmlStringRaw =
         "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
         "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
         "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
         "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
         " <soapenv:Header>\n" +
         "  <shw:Hello xmlns:shw=\"http://www.jcommerce.net/soap/ns/SOAPHelloWorld\">\n" +
         "    <shw:Myname>Tony</shw:Myname>\n" +
         "  </shw:Hello>\n" +
         "  <pre1:First xmlns:pre1=\"http://firstNamespace\">\n" +
         "     <pre1:name>first</pre1:name>\n" +
         "     <pre1:name2>first</pre1:name2>\n" +
         "  </pre1:First>\n" +
         "  <pre2:Second xmlns:pre2=\"http://secondNamespace\">n" +
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

 
 /**
  * @tesStrategy: Test simple creation of a soap envelope
 * @throws Exception
 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
public void testEnvelope() throws Exception {
     MessageFactory mf = MessageFactory.newInstance();
     SOAPMessage smsg =
             mf.createMessage(new MimeHeaders(), new ByteArrayInputStream(xmlString.getBytes()));
     SOAPPart sp = smsg.getSOAPPart();
     SOAPEnvelope se = (SOAPEnvelope)sp.getEnvelope();
     //smsg.writeTo(System.out);
     assertTrue(se != null);
 }

/**
 * @tesStrategy: Test whether namespaces are polluted.  This is an important
 * test for basic namespace function needed by C14N/WS-Security
* @throws Exception
*/
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
public void testHeaderNamespacePollution() throws Exception {
     MessageFactory mf = MessageFactory.newInstance();
     SOAPMessage smsg =
             mf.createMessage(new MimeHeaders(), new ByteArrayInputStream(xmlString.getBytes()));
     SOAPPart sp = smsg.getSOAPPart();
     IBMSOAPEnvelope se = (IBMSOAPEnvelope)sp.getEnvelope();
     SOAPHeader hdr = se.getHeader();
     assertTrue(hdr != null);

     Iterator it = hdr.getChildElements();
     while (it.hasNext()) {
         IBMSOAPHeaderElement she = (IBMSOAPHeaderElement) it.next();
         if (she.getElementName().getLocalName().equals("Second")) {
             Iterator it2 = she.getNamespacePrefixes();
             String prefix = null;
             int count = 0;
             while (it2.hasNext()) {
                 count++;
                 prefix = (String) it2.next();
             }

             // Make sure Second only has one prefix (pre2).
             assertTrue(count == 1 && prefix.equals("pre2"));
         } 
     }

     // Repeat with message created from DOM
     smsg = mf.createMessage(new MimeHeaders(), new ByteArrayInputStream(se.toString().getBytes()));
     sp = smsg.getSOAPPart();
     se = (IBMSOAPEnvelope)sp.getEnvelope();
     hdr = se.getHeader();
     assertTrue(hdr != null);

     it = hdr.getChildElements();
     while (it.hasNext()) {
         SOAPHeaderElement she = (SOAPHeaderElement) it.next();
         if (she.getElementName().getLocalName().equals("Second")) {
             Iterator it2 = she.getNamespacePrefixes();
             String prefix = null;
             int count = 0;
             while (it2.hasNext()) {
                 count++;
                 prefix = (String) it2.next();
             }

             // Make sure Second only has one prefix (pre2).
             assertTrue(count == 1 && prefix.equals("pre2"));
             return;
         }
     }
     // Shouldn't get here
     assertTrue(false);
 }

 private SOAPEnvelope getSOAPEnvelope() throws Exception {
     SOAPConnectionFactory scFactory = SOAPConnectionFactory.newInstance();
     SOAPConnection con = scFactory.createConnection();
     assertTrue("Expected connection", con != null);

     MessageFactory factory = MessageFactory.newInstance();
     SOAPMessage message = factory.createMessage();
     SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
     return envelope;
 }

 /**
  * @testStrategy:Test setting of attributes on the body.
 * @throws Exception
 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
public void testAttributes() throws Exception {
     SOAPEnvelope envelope = getSOAPEnvelope();
     SOAPBody body = envelope.getBody();

     Name name1 = envelope.createName("MyAttr1");
     String value1 = "MyValue1";
     Name name2 = envelope.createName("MyAttr2");
     String value2 = "MyValue2";
     Name name3 = envelope.createName("MyAttr3");
     String value3 = "MyValue3";
     body.addAttribute(name1, value1);
     body.addAttribute(name2, value2);
     body.addAttribute(name3, value3);
     java.util.Iterator iterator = body.getAllAttributes();
     assertTrue(getIteratorCount(iterator) == 3);
     iterator = body.getAllAttributes();
     boolean foundName1 = false;
     boolean foundName2 = false;
     boolean foundName3 = false;
     while (iterator.hasNext()) {
         Name name = (Name) iterator.next();
         if (name.equals(name1))
             foundName1 = true;
         else if (name.equals(name2))
             foundName2 = true;
         else if (name.equals(name3))
             foundName3 = true;
     }
     assertTrue(foundName1 && foundName2 && foundName3);
 }

 /**
  * testStrategy: Test basic SOAPFault function
 * @throws Exception
 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
public void testFaults() throws Exception {
     SOAPEnvelope envelope = getSOAPEnvelope();
     SOAPBody body = envelope.getBody();
     SOAPFault sf = body.addFault();
     sf.setFaultCode(SOAPConstants.SOAP_RECEIVER_FAULT);
     QName qnfc = sf.getFaultCodeAsQName();
     assertTrue(qnfc.equals(SOAPConstants.SOAP_RECEIVER_FAULT));

     sf.setFaultCode("env:Receiver");
     String fc = sf.getFaultCode();
     assertTrue(fc.equals("env:Receiver"));
     
     WebServicesFault wsFault = ((com.ibm.ws.webservices.engine.xmlsoap.SOAPFault) sf).getFault();
     assertTrue(!((com.ibm.websphere.webservices.soap.IBMSOAPFault)sf).isUserFault());
     QName actualQN = wsFault.getFaultCode();
     QName expectedQN = SOAPConstants.SOAP_RECEIVER_FAULT;
     assertEquals("Fault code should be " + expectedQN + ", instead it was " + actualQN, expectedQN, actualQN);
 }

 /**
  * @testStrategy: Test basic actor functionality
 * @throws Exception
 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
public void testHeaderElements() throws Exception {
     SOAPEnvelope envelope = getSOAPEnvelope();
     SOAPHeader hdr = envelope.getHeader();

     SOAPHeaderElement she1 = hdr.addHeaderElement(envelope.createName("foo1", "f1", "foo1-URI"));
     she1.setActor("actor-URI");
     java.util.Iterator iterator = hdr.extractHeaderElements("actor-URI");
     int cnt = 0;
     while (iterator.hasNext()) {
         cnt++;
         SOAPHeaderElement she = (SOAPHeaderElement) iterator.next();
         assertTrue(she.equals(she1));
     }
     assertTrue(cnt == 1);
     iterator = hdr.extractHeaderElements("actor-URI");
     assertTrue(!iterator.hasNext());
 }

 private int getIteratorCount(java.util.Iterator i) {
     int count = 0;
     while (i.hasNext()) {
         count++;
         i.next();
     }
     return count;
 }

 
 /**
 * testSOAPFault
 * Ported from TestSOAPFault.testQuick
 * @throws Exception
 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
public void testSOAPFault() throws Exception {
     MessageFactory msgfactory = MessageFactory.newInstance();
     SOAPFactory factory = SOAPFactory.newInstance();
     SOAPMessage outputmsg = msgfactory.createMessage();
     String valueCode = "faultcode";
     String valueString = "faultString";
     SOAPFault fault = outputmsg.getSOAPPart().getEnvelope().getBody().addFault();
     fault.setFaultCode(SOAPConstants.SOAP_RECEIVER_FAULT);
     fault.setFaultString(valueString);
     Detail d;
     d = fault.addDetail();
     assertTrue(((IBMSOAPFault)fault).isUserFault());
     d.addDetailEntry(factory.createName("Hello"));
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     if (outputmsg != null) {
         if (outputmsg.saveRequired()) {
             outputmsg.saveChanges();
         }
         outputmsg.writeTo(baos);
     }
     String xml = new String(baos.toByteArray());
     assertTrue(xml.indexOf("Hello")!=-1);
 }
 
 
 /**
  * removeMixedContent
  * brain dead code that can be used to remove mixed content
  * from xml messages.
  * @param input xml string
  * @return String with mixed content removed.
  */
 private String removeMixedContent(String input) {
     String[] parts = input.split(">\\s*<");
     StringBuffer result = new StringBuffer();
     for (int i=0; i < parts.length; i++) {
         result.append(parts[i]);
         if ((i+1) < parts.length) {
             result.append("><");
         }
     }
     return result.toString();
 }
 
 
}

