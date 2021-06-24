//
// 1.2 11/13/06
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
// 10/24/06  gkuo                       Create 

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
 * Checking the misc items of SOAPFault c004, c010
 */
public class SAAJ13Fault1Test extends TestBase {

	public SAAJ13Fault1Test() {
		super();
	}

	public SAAJ13Fault1Test(String arg0) {
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
            this.soapFault    = soapBody.addFault();
        }

        public MessageFactory getMessageFactory() throws Exception{
            return MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
        }

        public SOAPFactory getSOAPFactory() throws Exception {
            if( soapFactory == null ){
                soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            }
            return soapFactory;
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testRemoveAllSubcodes() throws Exception {
            soapFault.setFaultCode( SOAPConstants.SOAP_RECEIVER_FAULT ); 
                                                  //"Client";         
                                                  //"Server";         
                                                  //"MustUnderstand"; 
                                                  //"VersionMismatch";
            QName qName1 = new QName( "http://saaj13.ibm.com/qname",  // URI
                                      "faultSubcode1",                //Localpart
                                      "fsub" );                       // prefix
            QName qName2 = new QName( "http://saaj13.ibm.com/qname",  // URI
                                      "faultSubcode2",                //Localpart
                                      "fsub" );                       // prefix
            soapFault.appendFaultSubcode( qName1 );
            soapFault.appendFaultSubcode( qName2 );
            Iterator<QName> iter1 = (Iterator<QName>)soapFault.getFaultSubcodes();
            boolean bQ1 = false;
            boolean bQ2 = false;
            while( iter1.hasNext() ){
                QName qName = (QName)iter1.next();
                if( qName.equals( qName1 ) ) bQ1 = true;
                if( qName.equals( qName2 ) ) bQ2 = true;
            }
            assertTrue( "Did not get back all the Fault Subcode", bQ1 && bQ2 ); 

            // remove all the fault subcode
            soapFault.removeAllFaultSubcodes();

            boolean bNoSub = true;
            Iterator<QName> iter2 = (Iterator<QName>)soapFault.getFaultSubcodes();
            while( iter2.hasNext() ){  // Nothing
                QName qName = (QName)iter2.next();
                if( qName.equals( qName1 ) ) bQ1 = false;
                if( qName.equals( qName2 ) ) bQ2 = false;
                bNoSub = false;
            }
            assertTrue( "Did not remove all the Fault Subcode",  bQ1 && bQ2 && bNoSub ); 

            //Make sure the FaultCode is not removed
            QName qnFaultCode = soapFault.getFaultCodeAsQName();
            assertTrue( "The fault code changed", qnFaultCode.equals(SOAPConstants.SOAP_RECEIVER_FAULT) ); 
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testHasDetail() throws Exception {
            soapFault.setFaultCode( SOAPConstants.SOAP_SENDER_FAULT );   //"Client";         
                                                  //"Server";         
                                                  //"MustUnderstand"; 
                                                  //"VersionMismatch";
            soapFault.addFaultReasonText( "It's in fault. Please double check your request", 
                                          Locale.ENGLISH );
            assertTrue( "The fault ought not to have detail", !(soapFault.hasDetail()) );

            Detail detail = soapFault.addDetail();
            QName qName = new QName( "http://detailentry.austin.ibm.com/detailentry",
                                     "detailEntry",
                                     "de"
                                   );
            DetailEntry detailEntry = detail.addDetailEntry( qName ); // c010
            detailEntry.addTextNode( "This is a detail description of the Fault" );
            assertTrue( "The fault does not have detail as expected", soapFault.hasDetail() );
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testFaultRole() throws Exception {
            soapFault.setFaultCode( SOAPConstants.SOAP_RECEIVER_FAULT );   //"Client";         
                                                  //"Server";         
                                                  //"MustUnderstand"; 
                                                  //"VersionMismatch";

            String strRole1 = "http://role.ebay.com/seller";
            soapFault.setFaultRole( strRole1 );
            String strRole2 = soapFault.getFaultRole();
            String strRole3 = soapFault.getFaultActor();

            assertTrue( "getFaultRole() is wrong", strRole1.equals( strRole2) );
            assertTrue( "getFaultActor() is wrong", strRole1.equals( strRole3) );

            String strRole4 = "http://role.ebay.com/buyer";
            soapFault.setFaultRole( strRole4 );
            String strRole5 = soapFault.getFaultRole();
            String strRole6 = soapFault.getFaultActor();
            assertTrue( "getFaultRole() is wrong(New)" , strRole4.equals( strRole5) );
            assertTrue( "getFaultActor() is wrong(NEW)", strRole4.equals( strRole6) );
        }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testFaultReason() throws Exception {
            soapFault.setFaultCode( SOAPConstants.SOAP_SENDER_FAULT );   //"Client";         
                                                  //"Server";         
                                                  //"MustUnderstand"; 
                                                  //"VersionMismatch";
            String strReason1 = "It's in fault. Please double check your request";
            soapFault.addFaultReasonText( strReason1, 
                                          Locale.ENGLISH );
            String strReason2 = soapFault.getFaultString();

            assertTrue( "FaultString is different from strReason. strReason1 = " + strReason1 + " strReason2 = " + strReason2, 
                        strReason1.equals( strReason2 ) );

            Locale locale = soapFault.getFaultStringLocale( );
            assertTrue( "Locale is not ENGLISH", locale.equals( Locale.ENGLISH ) );

            String strReason4 = "Double check your request, please!";
            soapFault.setFaultString( strReason4, 
                                      Locale.TAIWAN );
            String strReason5 = soapFault.getFaultReasonText( Locale.TAIWAN );

            assertTrue( "FaultString is different from strReason. strReason4 = " + strReason4 + " strReason5 = " + strReason5, 
                        strReason4.equals( strReason5 ) );

        }

}
