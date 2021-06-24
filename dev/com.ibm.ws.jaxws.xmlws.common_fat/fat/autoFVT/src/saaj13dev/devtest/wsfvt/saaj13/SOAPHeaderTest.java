//
// 1.1 11/3/06
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
 * Checking the misc items of SOAPElement c024
 */
public class SOAPHeaderTest extends TestBase {

	public SOAPHeaderTest() {
		super();
	}

	public SOAPHeaderTest(String arg0) {
		super(arg0);
	}
	
	 protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
	     System.out.println("Do not need suiteSetup since no application is installed");    
	 }


        SOAPFactory  soapFactory = null;
        SOAPMessage  soapMessage = null; 
        SOAPBody     soapBody    = null; 
        SOAPHeader   soapHeader  = null; 

        public void setUp() throws Exception {
            super.setUp();

            MessageFactory  messageFactory  = getMessageFactory(); 
            this.soapFactory  = getSOAPFactory();
            this.soapMessage  = messageFactory.createMessage();
            this.soapBody     = soapMessage .getSOAPBody();
            this.soapHeader   = soapMessage .getSOAPHeader();
        }

        public MessageFactory getMessageFactory() throws Exception{
            return MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
        }

        public SOAPFactory getSOAPFactory() throws Exception {
            soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            return soapFactory;
        }

        protected void addHeaderElement( String localName, 
                                         String strActor, 
                                         boolean bRelay, 
                                         boolean bUnder) throws Exception{
            QName qName = new QName( "http://austin.ibm.com/fvt", localName, "he" );
            SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement( qName );
            if( strActor != null ){
                soapHeaderElement.setRole( strActor );
            }
            if( bRelay ){
                soapHeaderElement.setRelay( bRelay ); 
            }
            if( bUnder ){
                soapHeaderElement.setMustUnderstand( bUnder );
            }
        }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testNoUnder() throws Exception {
            Iterator<SOAPHeaderElement> iter = soapHeader.examineMustUnderstandHeaderElements(SOAPConstants.URI_SOAP_1_2_ROLE_NEXT);
            assertTrue( "No soapHeaderElement(mustUnderstand) to be found but find one",
                        !iter.hasNext() );
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testNoElement() throws Exception {
            Iterator<SOAPHeaderElement> iter = soapHeader.examineHeaderElements(SOAPConstants.URI_SOAP_1_2_ROLE_NONE);
            assertTrue( "No soapHeaderElement(generic) to be found but find one",
                        !iter.hasNext() );
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testNoExtract() throws Exception {
            Iterator<SOAPHeaderElement> iter = soapHeader.extractHeaderElements(SOAPConstants.URI_SOAP_1_2_ROLE_ULTIMATE_RECEIVER);
            assertTrue( "No soapHeaderElement(extract) to be found but find one",
                        !iter.hasNext() );
        }

        protected void addDefaultHeaderElements()throws Exception {
            addHeaderElement( "mustUnderstandNext", 
                              SOAPConstants.URI_SOAP_1_2_ROLE_NEXT,
                              true,
                              true );
            addHeaderElement( "mustUnderstandNone", 
                              SOAPConstants.URI_SOAP_1_2_ROLE_NONE,
                              false,
                              true );
            addHeaderElement( "mustUnderstandUltra", 
                              SOAPConstants.URI_SOAP_1_2_ROLE_ULTIMATE_RECEIVER,
                              true,
                              true );

            addHeaderElement( "Next", 
                              SOAPConstants.URI_SOAP_1_2_ROLE_NEXT,
                              true,
                              false );
            addHeaderElement( "None", 
                              SOAPConstants.URI_SOAP_1_2_ROLE_NONE,
                              false,
                              false );
            addHeaderElement( "Ultra", 
                              SOAPConstants.URI_SOAP_1_2_ROLE_ULTIMATE_RECEIVER,
                              false,
                              false );

            addHeaderElement( "Ultra1", 
                              "http://fvt.austin.ibm.com/fvtu",
                              true,
                              false );

        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testUnder() throws Exception {
            addDefaultHeaderElements();
            Iterator<SOAPHeaderElement> iter = soapHeader.examineMustUnderstandHeaderElements(SOAPConstants.URI_SOAP_1_2_ROLE_NEXT);
            int iCount = 0;
            while( iter.hasNext() ){
                SOAPHeaderElement soapHeaderElement = (SOAPHeaderElement)iter.next();
                QName qName = soapHeaderElement.getElementQName();
                String strLocal = qName.getLocalPart();
                assertTrue( "did not find 'mustUnderstandNext' but '" + strLocal + "'",
                            strLocal.equals( "mustUnderstandNext" )
                          );
                iCount ++;
            }
            assertTrue( "only one mustUnderstandNext but get " + iCount, iCount == 1 );
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testNext() throws Exception {
            addDefaultHeaderElements();
            Iterator<SOAPHeaderElement> iter = soapHeader.examineHeaderElements(SOAPConstants.URI_SOAP_1_2_ROLE_NEXT);
            int iCount = 0;
            while( iter.hasNext() ){
                SOAPHeaderElement soapHeaderElement = (SOAPHeaderElement)iter.next();
                QName qName = soapHeaderElement.getElementQName();
                String strLocal = qName.getLocalPart();
                assertTrue( "did not find Next Header but '" + strLocal + "'",
                            strLocal.equals( "mustUnderstandNext" ) ||
                            strLocal.equals( "Next" ) 
                          );
                iCount ++;
            }
            assertTrue( "only 2 ROLE_NEXT Header Element but get " + iCount, iCount == 2 );
        }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testExtractNone() throws Exception {
            addDefaultHeaderElements();
            Iterator<SOAPHeaderElement> iter = soapHeader.extractHeaderElements(SOAPConstants.URI_SOAP_1_2_ROLE_NONE);
            int iCount = 0;
            while( iter.hasNext() ){
                SOAPHeaderElement soapHeaderElement = (SOAPHeaderElement)iter.next();
                QName qName = soapHeaderElement.getElementQName();
                String strLocal = qName.getLocalPart();
                assertTrue( "did not find None Header but '" + strLocal + "'",
                            strLocal.equals( "mustUnderstandNone" ) ||
                            strLocal.equals( "None" ) 
                          );
                iCount ++;
            }
            assertTrue( "only 2 ROLE_NONE Header Element but get " + iCount, iCount == 2 );

            iter = soapHeader.examineHeaderElements(SOAPConstants.URI_SOAP_1_2_ROLE_NONE);
            iCount = 0;
            while( iter.hasNext() ){
                SOAPHeaderElement soapHeaderElement = (SOAPHeaderElement)iter.next();
                QName qName = soapHeaderElement.getElementQName();
                String strLocal = qName.getLocalPart();
                fail( "ought not get any none header element after extract but get '" + strLocal + "'"
                          );
                iCount ++;
            }

            iter = soapHeader.extractHeaderElements(SOAPConstants.URI_SOAP_1_2_ROLE_NONE);
            iCount = 0;
            while( iter.hasNext() ){
                SOAPHeaderElement soapHeaderElement = (SOAPHeaderElement)iter.next();
                QName qName = soapHeaderElement.getElementQName();
                String strLocal = qName.getLocalPart();
                fail( "ought not get any none header element after extract but get '" + strLocal + "'"
                          );
                iCount ++;
            }
        }


}
