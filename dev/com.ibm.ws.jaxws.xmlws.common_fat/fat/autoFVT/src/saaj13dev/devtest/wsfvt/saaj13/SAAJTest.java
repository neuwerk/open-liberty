//
// @(#) 1.1 autoFVT/src/saaj13dev/devtest/wsfvt/saaj13/SAAJTest.java, WAS.websvcs.fvt, WASX.FVT 8/2/06 08:57:08 [7/23/07 09:49:05]
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

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.xml.sax.InputSource;

import com.ibm.websphere.webservices.soap.IBMSOAPBody;
import com.ibm.websphere.webservices.soap.IBMSOAPElement;
import com.ibm.websphere.webservices.soap.IBMSOAPEnvelope;
import com.ibm.websphere.webservices.soap.IBMSOAPFactory;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;


/**
 * SAAJTest
 *
 * Sample SAAJ Tests ported from the V2 project
 *
 */
public class SAAJTest extends TestBase {

    /**
     * Constructor
     */
    public SAAJTest() {
        super();
    }

    /**
     * Constructor
     * @param arg0
     */
    public SAAJTest(String arg0) {
        super(arg0);
    }
    
    protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
        System.out.println("Do not need suiteSetup since no application is installed");    
    }

    
    /**
     * test2
     * @testStrategy: Test the creation of the SAAJ factories
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test2_FactoryCreation() throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        assertTrue("MessageFactory initialization Failed", mf != null);
        SOAPFactory sf = SOAPFactory.newInstance();
        assertTrue("SOAPFactory initialization Failed", sf != null);
        SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
        assertTrue("SOAPConnectinFactory initialization Failed", scf != null);
    }
    
    /**
     * test3
     * @testStrategy:Creation of SOAPEnvelope from MessageFactory
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test3_CreateSOAPEnvelopeFromMessageFactory() throws Exception {
        IBMSOAPEnvelope env = createEnvelopeFromMessageFactory();
        SOAPBody body = env.getBody();
        SOAPElement se = body.addChildElement("test");
        se.addTextNode("Hello World");
        String text = env.toXMLString(false);
        System.out.println(text);
        assertTrue("Did not find 'Hello World", text.contains("Hello World"));
    }
    
    /**
     * test4_CreateSOAPEnvelopeFromSOAPFactory
     * @testStrategy: Create SOAPEnvelope from SOAPFactory
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test4_CreateSOAPEnvelopeFromSOAPFactory() throws Exception {
        IBMSOAPEnvelope env = createEnvelopeFromMessageFactory();
        SOAPBody body = env.getBody();
        SOAPElement se = body.addChildElement("test");
        se.addTextNode("Hello World");
        String text = env.toXMLString(false);
        System.out.println(text);
        assertTrue("Did not find 'Hello World", text.contains("Hello World")); 
    }
    
    /**
     * test5
     * @testStrategy: Create and add XML Text directly into an SAAJ model.
     * The SAAJ data model should be serialized without affecting the
     * alternate content.  Querying the subtree should cause the 
     * data model to automatically expand the subtree.
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test5_XMLStringAlternateContentTest() throws Exception {
        IBMSOAPEnvelope env = createEnvelopeFromMessageFactory();
        IBMSOAPFactory sf = env.getIBMSOAPFactory();
        IBMSOAPBody body = (IBMSOAPBody) env.getBody();
        
        // Create an element with an input string
        String text = "<text><data>Hello World</data></text>";
        IBMSOAPElement se = (IBMSOAPElement) sf.createElementFromXMLString(text, SOAPBodyElement.class);
        
        // Add the element to the body
        se = (IBMSOAPElement) body.addChildElement(se);
        
        // Print it out and check that output text. 
        String outText1 = env.toXMLString(false);
        assertTrue("Did not find proper data: " + outText1, outText1.contains("<data>Hello World</data>")); 
        
        /* Adding se to the body causes implicit attribute checking, 
           Thus se will already be expanded at this point. (scheu 
         
        assertTrue("Should have alternate content", se.hasAlternateContent());
        */
        
        // Now get the child, which forces expansion of the tree
        SOAPElement child =  (SOAPElement) se.getFirstChild();  
        assertTrue("Child should not be null", child != null);
        String outText2 = env.toXMLString(false);
        System.out.println(outText2);
        System.out.println(outText1);
        assertTrue("Did not find proper data: \n" + outText2 + "\n" + outText1, outText2.equals(outText1)); 
        assertTrue("Should not have alternate content", !se.hasAlternateContent());
        
    }
    
    /**
     * test5
     * @testStrategy: Similar to test5 except this tests InputSource alternate content
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test6_InputSourceAlternateContentTest() throws Exception {
        IBMSOAPEnvelope env = createEnvelopeFromMessageFactory();
        IBMSOAPFactory sf = env.getIBMSOAPFactory();
        IBMSOAPBody body = (IBMSOAPBody) env.getBody();
        
        // Create an element with an input com.ibm.env.source
        String encoding = "utf-8";
        String text = "<text><data>Hello World</data></text>";
        ByteArrayInputStream bais = new ByteArrayInputStream(text.getBytes(encoding));
        InputSource is = new InputSource(bais);
        is.setEncoding(encoding);
        IBMSOAPElement se = (IBMSOAPElement) sf.createElementFromInputSource(is, SOAPBodyElement.class);
        
        // Add the element to the body
        se = (IBMSOAPElement) body.addChildElement(se);
        
        // Print it out and check that output text. 
        String outText1 = env.toXMLString(false);
        assertTrue("Did not find proper data: " + outText1, outText1.contains("<data>Hello World</data>")); 
        
        /* Adding se to the body causes implicit attribute checking, 
        Thus se will already be expanded at this point. (scheu)
      
        assertTrue("Should have alternate content", se.hasAlternateContent());
        */
        
        // Now get the child, which forces expansion of the tree
        SOAPElement child =  (SOAPElement) se.getFirstChild();  
        assertTrue("Child should not be null", child != null);
        String outText2 = env.toXMLString(false);
        assertTrue("Did not find proper data: \n" + outText2 + "\n" + outText1, outText2.equals(outText1)); 
        assertTrue("Should not have alternate content", !se.hasAlternateContent());
        
    }
    
    /**
     * createEnvelopeFromMessageFactory
     * @return IBMSOAPEnvelope
     * @throws Exception
     */
    protected IBMSOAPEnvelope createEnvelopeFromMessageFactory() throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage message = mf.createMessage();
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope env = soapPart.getEnvelope();
        return (IBMSOAPEnvelope) env;
    }
    
}
