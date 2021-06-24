package soap.test.fault11;

import javax.xml.namespace.QName;
import junit.framework.TestCase;
import java.io.File;
import java.io.FileInputStream;

import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;

import saaj.client.util.SAAJHelper;
import saaj.client.util.SOAPMessage12Helper;
import com.ibm.ws.saaj.FaultData12;

import java.util.Iterator;
import org.w3c.dom.Document;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

// SAAJConverter and its Factories
import org.apache.axis2.jaxws.message.util.SAAJConverter;
import org.apache.axis2.jaxws.registry.FactoryRegistry;
import org.apache.axis2.jaxws.message.factory.SAAJConverterFactory;

/**
  *  Test the Message created in SAAJ 1.2(SOAP1.1) instance
  *  The corresponding in the Server to respond is 
  *     saaj/server/com.ibm.wsfp.jaxws_1.0.0.jar/...../Fault12Response.java
  *
  */
public class SOAPFault11Test extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
    
    public SOAPFault11Test(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        SAAJHelper.setTestCase( this, true );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFault11_1() throws Exception {
        int iCase  = 1;
        System.out.println( );
        System.out.println("------------ testFault11_"+ iCase + "() only faultCode+faultString --------");

        // Initialize Data in client 
        FaultData12 faultData = new FaultData12( FaultData12.CLIENT,  // code
                                                 null,                // actor
                                                 "It has to have a FaultString",    // reason/faultstring
                                                 null,                // locale
                                                 null                 // Detail
                                               );
        doTest( faultData, iCase);
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFault11_2() throws Exception {
        int iCase  = 2;
        System.out.println( );
        System.out.println("------------ testFault11_"+ iCase + "() faultCode + String --------");

        // Initialize Data in client 
        FaultData12 faultData = new FaultData12( FaultData12.SERVER,  // code
                                                 null,                // actor
                                                 " try the server code ",                // reason/faultstring
                                                 null,                // locale
                                                 null                 // Detail
                                               );
        doTest( faultData, iCase);
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFault11_3() throws Exception {
        int iCase  = 3;
        System.out.println( );
        System.out.println("------------ testFault11_"+ iCase + "() faultCode + String + Actor --------");

        // Initialize Data in client 
        FaultData12 faultData = new FaultData12( FaultData12.MUST,               // code
                                                 "http://dentist.com/Michael",   // actor
                                                 "Have you made the dentist appoint beforehande?",  // reason/faultstring
                                                 null,                // locale
                                                 null                 // Detail
                                               );
        doTest( faultData, iCase);
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFault11_4() throws Exception {
        int iCase  = 4;
        System.out.println( );
        System.out.println("------------ testFault11_"+ iCase + "() faultCode + String/Locale + Actor --------");

        // Initialize Data in client 
        FaultData12 faultData = new FaultData12( FaultData12.VERSION,            // code
                                                 "http://kungfu.com/taichi",   // actor
                                                 "Have you learn ¤Ó·¥®± yet?",  // reason/faultstring
                                                 "Big5",                // locale
                                                 null                 // Detail
                                               );
        doTest( faultData, iCase);
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFault11_5() throws Exception {
        int iCase  = 5;
        System.out.println( );
        System.out.println("------------ testFault11_"+ iCase + "() all --------");

        // Initialize Data in client 
        FaultData12 faultData = new FaultData12( FaultData12.VERSION,            // code
                                                 "http://kungfu.com/taichi",     // actor
                                                 "Have he learned Taichi yet?", // reason/faultstring
                                                 "en",                // locale
                                                 null                 // Detail
                                               );
        faultData.addDetailEntry( "taichi_nothern", "The taichi of the nothern part");
        doTest( faultData, iCase);
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFault11_6() throws Exception {
        int iCase  = 6;
        System.out.println( );
        System.out.println("------------ testFault11_"+ iCase + "() Mixed --------");

        // Initialize Data in client 
        FaultData12 faultData = new FaultData12( FaultData12.CLIENT,            // code
                                                 null,                          // actor
                                                 "We love to learn yoga instead of Taichi.", // reason/faultstring
                                                 "en",                // locale
                                                 null                 // Detail
                                               );
        faultData.addDetailEntry( "taichi", 
                                  "kungfu",
                                  "http://exercise.com/aged/persons",
                                  "The taichi is a good exercise for aged people" );
        doTest( faultData, iCase);
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFault11_7() throws Exception {
        int iCase  = 7;
        System.out.println( );
        System.out.println("------------ testFault11_"+ iCase + "() Mixed --------");

        // Initialize Data in client 
        FaultData12 faultData = new FaultData12( FaultData12.CLIENT,                   // code
                                                 "http://movie.com/accouting",         // actor
                                                 "No receipt no refund no exception!", // reason/faultstring
                                                 null,                // locale
                                                 null                 // Detail
                                               );
        faultData.addDetailEntry( "taichi", 
                                  "kungfu",
                                  "http://exercise.com/aged/persons",
                                  "The taichi is a good exercise for aged persons" );
        faultData.addDetailEntry( "taichi_2", "They prectice taichi is the morning" );
        faultData.addDetailEntry( "Yoga", "Yoga is really good for health, too." );
        doTest( faultData, iCase);
    }
    
    private void doTest( FaultData12 faultData, int iCase ) throws Exception {
        SOAPMessage soapMessage = faultData.createSimpleMessage( iCase );
        SOAPMessage response    = null;
        try{
            response  = SOAPMessage12Helper.send( soapMessage  );
        } catch( Exception e ){
            assertTrue( "Did not get the excpected SOAPFault Exception",
                        faultData.isExpectedFault( e ) );
        }
        if( response != null ){
            assertTrue( "Did not get the excpected SOAPFault Message",
                        faultData.isExpectedMessage( response ) );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFault11_8() throws Exception {
        System.out.println( "------------- testFault11_8------------" );
        String          strNV           = SOAPConstants.SOAP_1_1_PROTOCOL;
        MessageFactory  messageFactory  = MessageFactory.newInstance(strNV); 
        //MessageFactory  messageFactory  = getMessageFactory(); 
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        SOAPFactory     soapFactory     = SOAPFactory.newInstance( strNV );
        Name            nameCode        = soapFactory.createName( "client", "soapenv", 
                                                      SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE );
        SOAPFault       soapfault       = soapBody.addFault( nameCode, "testing only");
        Detail          detail          = soapfault.addDetail();
        System.out.println( "SOAP fault detail:'" + detail + "'" ); 
        QName           name            = detail.getElementQName();
        String          strLocal        = name.getLocalPart();
        System.out.println( "detail tag:'" + strLocal + "'" ); 

        SOAPPart        soapPart        = soapMessage.getSOAPPart();
        SOAPEnvelope    soapEnvelope    = soapPart.getEnvelope();
		// Convert OM to SAAJ
        SAAJConverterFactory saajConverterFactory = 
               (SAAJConverterFactory)FactoryRegistry.getFactory(SAAJConverterFactory.class);
        SAAJConverter saajConverter =  saajConverterFactory.getSAAJConverter(); 
		org.apache.axiom.soap.SOAPEnvelope omEnvelope = saajConverter.toOM(soapEnvelope);
        org.apache.axiom.soap.SOAPFault soapFault = omEnvelope.getBody().getFault();
        //new AxisFault(soapFault.getCode(), soapFault.getReason(),
        //        soapFault.getNode(), soapFault.getRole(), soapFault.getDetail());
        org.apache.axiom.om.OMElement omElement   = soapFault.getDetail();
        System.out.println( "soapDetail(omElement):'" + omElement+  "'" );
        QName          qName           = omElement.getQName();
        System.out.println( "soapDetail tag:'" + qName.getLocalPart() +  "'" );
        String strAll = omElement.toString();
        // See defect 402122. This is an Axiom issue and will be OK 
        boolean bOK402122 = false;
        String strLocal1 = qName.getLocalPart();
        String strLocal2 = strLocal.toLowerCase();
        if( strAll.indexOf( strLocal1  ) >= 0 || 
            strAll.indexOf( strLocal2 ) >= 0  ){ // If not 402122, take away this condition
            bOK402122 = true;
        }
        assertTrue( "Detail tag is in different case", bOK402122 );
    }

}
