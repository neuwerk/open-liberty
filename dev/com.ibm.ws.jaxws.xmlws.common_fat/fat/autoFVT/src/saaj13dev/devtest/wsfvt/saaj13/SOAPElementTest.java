//
// 1.2 10/31/06
//
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W60, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId      Defect          Description
// ----------------------------------------------------------------------------
// 10/24/2007  gkuo                        create

package saaj13dev.devtest.wsfvt.saaj13;

import java.util.Iterator;
import java.util.Locale;

import javax.xml.namespace.QName;

// SAAJ SOAPMessage related
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPException;

import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;

import java.net.URL;

/**
 * Checking the misc items of SOAPElement c012
 */
public class SOAPElementTest extends TestBase {

	public SOAPElementTest() {
		super();
	}

	public SOAPElementTest(String arg0) {
		super(arg0);
	}
	
	 protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
	     System.out.println("Do not need suiteSetup since no application is installed");    
	 }


        SOAPFactory  soapFactory = null;
        SOAPMessage  soapMessage = null; 
        SOAPBody     soapBody    = null; 
        SOAPFault    soapFault   = null;

        public void setUp() throws Exception {
            super.setUp();

            MessageFactory  messageFactory  = getMessageFactory(); 
            this.soapFactory  = getSOAPFactory();
            this.soapMessage  = messageFactory.createMessage();
            this.soapBody     = soapMessage .getSOAPBody();
        }

        public MessageFactory getMessageFactory() throws Exception{
            return MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
        }

        public SOAPFactory getSOAPFactory() throws Exception {
            soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            return soapFactory;
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testAttribURI() throws Exception {
            QName qName = new QName( "http://soapelemnt.tivoli.com/soapbody",
                                     "soapbodyelement",
                                     "sbe" );
            SOAPBodyElement soapBodyElement = soapBody.addBodyElement( qName );
            // Existing Prefix is "sbe"
            QName qName1 = new QName( "http://soapelemnt.tivoli.com/tivoli",
                                     "attrib" );
            soapBodyElement.addAttribute( qName1, "It's OK" );

            QName qName2 = null;
            try{
                qName2 = soapBodyElement.createQName( "newSBEName", "sbe" );
            } catch( Exception e ){
                e.printStackTrace();
                fail( "SOAPElement.createQName() fail");
            }

            String strValue = soapBodyElement.getAttributeValue( qName1 );
            assertTrue( "expect 'It's OK' but get '" + strValue + "'", 
                        strValue.equals("It's OK" ) ); 


            // remove the attrobute
            boolean bRemoved = soapBodyElement.removeAttribute(qName1);
            assertTrue( "Did not removeAttribute(Qname) ", bRemoved );

            String strValue1 = soapBodyElement.getAttributeValue( qName1 );
            assertTrue( "expect no attrobute but get '" + strValue + "'", 
                        (strValue1 == null) || (strValue1.length() == 0) ); 

            Iterator<SOAPBodyElement> iter1 = (Iterator<SOAPBodyElement> )soapBody.getChildElements( qName );
            // This ought to have 1 item
            int iCount = 0;
            while( iter1.hasNext() ){
                SOAPBodyElement soapBodyE = (SOAPBodyElement)iter1.next();
                QName bqName = soapBodyE.getElementQName();
                assertTrue( "QName is different", bqName.equals(qName ) );
                iCount ++;
            }
            assertTrue( "SOAPBodyElement counted but error :" + iCount, iCount == 1 );

            try{
                // soapBody can not change name
                soapBody.setElementQName( qName2 );
                soapMessage.writeTo( System.out );
                fail( "SOAPBody can not change its element name" );
            } catch( Exception e ){
            }

            soapBodyElement.setElementQName( qName2 );
            soapMessage.writeTo( System.out );

            Iterator<SOAPBodyElement> iter2 = 
                    (Iterator<SOAPBodyElement> )soapBody.getChildElements( qName );
            // This ought to have 0 item, since Element QName changed
            iCount = 0;
            while( iter2.hasNext() ){
                SOAPBodyElement soapBodyE = (SOAPBodyElement)iter2.next();
                QName bqName = soapBodyE.getElementQName();
                assertTrue( "QName is different", bqName.equals(qName2 ) );
                iCount ++;
            }
            assertTrue( "SOAPBodyElement counted but error :" + iCount, iCount == 0 );


            Iterator<SOAPBodyElement> iter3 = 
                   (Iterator<SOAPBodyElement> )soapBody.getChildElements( qName2 );
            // This ought to have 1 item
            iCount = 0;
            while( iter3.hasNext() ){
                SOAPBodyElement soapBodyE = (SOAPBodyElement)iter3.next();
                QName bqName = soapBodyE.getElementQName();
                assertTrue( "QName is different", bqName.equals(qName2 ) );
                iCount ++;
            }
            assertTrue( "SOAPBodyElement counted but error :" + iCount, iCount == 1 );

            // Once this piece test OK. We can not create a prefix without an URI accociates to it
            // Unless default qName
            // Also see the QName
            try{
                qName2 = soapBodyElement.createQName( "child", "nosuchprefix" );
                fail( "SOAPElement.createQName() must have an URI associated to its prefix but not");
            } catch( Exception e ){
            }

        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testSetElementQName() throws Exception {
            SOAPPart     soapPart   = soapMessage.getSOAPPart();
            SOAPEnvelope soapEnv    = soapPart.getEnvelope();
            SOAPHeader   soapHeader = soapMessage.getSOAPHeader();

            QName qName = new QName( "http://soapelemnt.tivoli.com/soapbody",
                                     "soapbodyelement",
                                     "sbe" );
            try{
                // soapBody can not change name
                soapEnv.setElementQName( qName );
                fail( "SOAPEnvelope ought not change its element name" );
            } catch( Exception e ){
            }

            try{
                // soapBody can not change name
                soapHeader.setElementQName( qName );
                fail( "SOAPBody can not change its element name" );
            } catch( Exception e ){
            }

            try{
                // soapBody can not change name
                soapBody.setElementQName( qName );
                fail( "SOAPBody can not change its element name" );
            } catch( Exception e ){
            }

        }

        // Dot not test c021 
        // The fragment rooted in element cannot contain elements named "Envelope", "Header" or "Body" and in the SOAP namespace
        // Per the discussion note from R J Scheuerle on 10/30/2006
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void nottestCreateElement1() throws Exception {

            QName qName = new QName( "http://www.w3.org/2003/05/soap-envelope",
                                     "Envelope",
                                     "soapenv" );
            SOAPElement soapElement = null; 
            try{
                soapElement = soapFactory.createElement( qName );
            } catch( Exception e ){
                e.printStackTrace( System.out );
            }
            assertTrue( "It's not an SOAPEnvelope as expected",
                        (soapElement instanceof SOAPEnvelope) );

            try{
                soapBody.addChildElement( soapElement );
                fail( "SOAPEnvelope, SOAPBody, SOAPHeader can not addChildElement() to soapBody" );
            } catch( Exception e ){
                // Exactly what we expected
            }
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void nottestCreateElement2() throws Exception {

            QName qName = new QName( "http://www.w3.org/2003/05/soap-envelope",
                                     "Body",
                                     "soapenv" );
            SOAPElement soapElement = null; 
            try{
                soapElement = soapFactory.createElement( qName );
            } catch( Exception e ){
                e.printStackTrace( System.out );
            }
            assertTrue( "It's not an SOAPBody as expected",
                        (soapElement instanceof SOAPBody) );

            try{
                soapBody.addChildElement( soapElement );
                fail( "SOAPBody, SOAPHeader, SOAPEnvelope can not addChildElement() to soapBody" );
            } catch( Exception e ){
                // Exactly what we expected
            }
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void nottestCreateElement3() throws Exception {

            QName qName = new QName( "http://www.w3.org/2003/05/soap-envelope",
                                     "Header",
                                     "soapenv" );
            SOAPElement soapElement = null; 
            try{
                soapElement = soapFactory.createElement( qName );
            } catch( Exception e ){
                e.printStackTrace( System.out );
            }

            assertTrue( "It's not an SOAPHeader as expected",
                        (soapElement instanceof SOAPHeader) );


            try{
                soapBody.addChildElement( soapElement );
                fail( "SOAPHeader, SOAPEnvelope, SOAPBody can not addChildElement() to soapBody" );
            } catch( Exception e ){
                // Exactly what we expected
            }
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testQName() throws Exception {

            QName qName = new QName( "http://wsfp.austin.ibm.com/f13ns",
                                     "Fault13",
                                     "f13" );

            try{
                soapBody.addChildElement( qName );
            } catch( Exception e ){
                // Exactly what we expected
            }

            Iterator iter = soapBody.getChildElements( qName );
            SOAPElement soapElement = (SOAPElement)iter.next();
            assertTrue( "soapElement not found", soapElement != null );
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testAddChildElement() throws Exception {
            // This testcase is not documented.
            // But it's nice to take care of SOAPBody, SOAPHeader and SOAPEnvelope. 
            try{
                soapBody.addChildElement( "Body", "soapenv" );
                fail( "SOAPHeader, SOAPEnvelope, SOAPBody can not addChildElement() to soapBody" );
            } catch( Exception e ){
                // Exactly what we expected
            }

            try{
                soapBody.addChildElement( "Envelope", "soapenv" );
                fail( "SOAPHeader, SOAPEnvelope, SOAPBody can not addChildElement() to soapBody" );
            } catch( Exception e ){
                // Exactly what we expected
            }

            try{
                soapBody.addChildElement( "Header", "soapenv" );
                fail( "SOAPHeader, SOAPEnvelope, SOAPBody can not addChildElement() to soapBody" );
            } catch( Exception e ){
                // Exactly what we expected
            }
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testGetFault() throws Exception {
            SOAPFault soapFault1 = soapBody.addFault();
            SOAPFault soapFault2 = soapBody.getFault();
            assertTrue( "SOAPFault are different in addFault() and getFault()",
                        soapFault1 == soapFault2 );  // Ought exact the same SOAPFault object
        }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testAddTextNode() throws Exception {

            QName qName = new QName( "http://www.ibm.com/test",
                                     "bodyelement",
                                     "test" );
            SOAPBodyElement bodyElement = soapBody.addBodyElement(qName); 
            String strNode1 = "This is a SOAPElement, too."; 
            SOAPElement textNode = bodyElement.addTextNode( strNode1 );
            String strNode2 = textNode.getValue();
            assertTrue( "addTextNode does not get back the right SOAPElement",
                        strNode1.equals( strNode2 ) );
        }


}
