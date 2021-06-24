package soap.test.fault12;

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
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;

import saaj.client.util.SAAJHelper;
import saaj.client.util.SOAPMessage13Helper;
import com.ibm.ws.saaj.FaultData13;

import java.util.Iterator;
import org.w3c.dom.Document;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

// SAAJConverter and its Factories
import org.apache.axis2.jaxws.message.util.SAAJConverter;
import org.apache.axis2.jaxws.registry.FactoryRegistry;
import org.apache.axis2.jaxws.message.factory.SAAJConverterFactory;

/**
  *  Test the Message created in SAAJ 1.3(SOAP1.2) instance
  *  The corresponding in the Server to respond is 
  *     saaj/server/com.ibm.wsfp.jaxws_1.0.0.jar/...../Fault13Response.java
  *
  */
public class SOAPFault12Test extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
    
    public SOAPFault12Test(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        SAAJHelper.setTestCase( this, true );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFault12_1() throws Exception {
        int iCase  = 1;
        System.out.println( );
        System.out.println("------------ testFault12_"+ iCase + "() only faultCode+faultString --------");

        // Initialize Data in client 
        FaultData13 faultData = new FaultData13( FaultData13.CLIENT,  // code
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
    public void testFault12_2() throws Exception {
        int iCase  = 2;
        System.out.println( );
        System.out.println("------------ testFault12_"+ iCase + "() faultCode + String --------");

        // Initialize Data in client 
        FaultData13 faultData = new FaultData13( FaultData13.SERVER,  // code
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
    public void testFault12_3() throws Exception {
        int iCase  = 3;
        System.out.println( );
        System.out.println("------------ testFault12_"+ iCase + "() faultCode + String + Actor --------");

        // Initialize Data in client 
        FaultData13 faultData = new FaultData13( FaultData13.MUST,               // code
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
    public void testFault12_4() throws Exception {
        int iCase  = 4;
        System.out.println( );
        System.out.println("------------ testFault12_"+ iCase + "() faultCode + String/Locale + Actor --------");

        // Initialize Data in client 
        FaultData13 faultData = new FaultData13( FaultData13.VERSION,            // code
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
    public void testFault12_5() throws Exception {
        int iCase  = 5;
        System.out.println( );
        System.out.println("------------ testFault12_"+ iCase + "() all --------");

        // Initialize Data in client 
        FaultData13 faultData = new FaultData13( FaultData13.ENCODING,            // code
                                                 "http://kungfu.com/taichi",     // actor
                                                 "Have he learned Taichi yet?", // reason/faultstring
                                                 "en",                // locale
                                                 null                 // Detail
                                               );
        //faultData.addDetailEntry( "taichi_nothern", "The taichi of the nothern part");
        faultData.addDetailEntry( "taichi_nothern","kungfu","http://exercise.com/aged/persons", "The taichi of the nothern part");
        doTest( faultData, iCase);
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFault12_6() throws Exception {
        int iCase  = 6;
        System.out.println( );
        System.out.println("------------ testFault12_"+ iCase + "() Mixed --------");

        // Initialize Data in client 
        FaultData13 faultData = new FaultData13( FaultData13.VERSION,            // code
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
    public void testFault12_7() throws Exception {
        int iCase  = 7;
        System.out.println( );
        System.out.println("------------ testFault12_"+ iCase + "() Mixed --------");

        // Initialize Data in client 
        FaultData13 faultData = new FaultData13( FaultData13.CLIENT,                   // code
                                                 "http://movie.com/accouting",         // actor
                                                 "No receipt no refund no exception!", // reason/faultstring
                                                 null,                // locale
                                                 null                 // Detail
                                               );
        faultData.addDetailEntry( "taichi", 
                                  "kungfu",
                                  "http://exercise.com/aged/persons",
                                  "The taichi is a good exercise for aged persons" );
        faultData.addDetailEntry( "taichi_2","kungfu","http://exercise.com/aged/persons","They prectice taichi is the morning" );
        faultData.addDetailEntry( "Yoga","kungfu","http://exercise.com/aged/persons","Yoga is really good for health, too." );
        doTest( faultData, iCase);
    }
    
    private void doTest( FaultData13 faultData, int iCase ) throws Exception {
        SOAPMessage soapMessage = faultData.createSimpleMessage( iCase );
        SOAPMessage response    = null;
        try{
            response  = SOAPMessage13Helper.send( soapMessage  );
        } catch( Exception e ){
            if( e instanceof javax.xml.stream.XMLStreamException ||
                e instanceof javax.xml.ws.WebServiceException    ||
                e instanceof org.apache.axiom.om.OMException      ){
                String strE = e.toString();
                if( strE.indexOf( "must consist of well-formed character data or markup" ) >= 0 ){
                    fail( "Can not get a XML-well-formed SOAPFault in response. This must be Defect 431318" );
                }
            }
            assertTrue( "Did not get the excpected SOAPFault Exception",
                        faultData.isExpectedFault( e, iCase ) );
        }
        if( response != null ){
            assertTrue( "Did not get the excpected SOAPFault Message",
                        faultData.isExpectedMessage( response ) );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFault12_8() throws Exception {
        System.out.println( "------------- testFault12_8------------" );
        String          strNV           = SOAPConstants.SOAP_1_2_PROTOCOL;
        MessageFactory  messageFactory  = MessageFactory.newInstance( strNV );
        //MessageFactory  messageFactory  = getMessageFactory(); 
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        SOAPFactory     soapFactory     = SOAPFactory.newInstance( strNV );
        Name            nameCode        = soapFactory.createName( "Sender", "env", 
                                                      SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE );
        SOAPFault       soapfault       = soapBody.addFault( nameCode, "testing 13 only");
        Detail          detail          = soapfault.addDetail();
        System.out.println( "SOAP fault detail:'" + detail + "'" ); 
        QName           name            = detail.getElementQName();
        String          strLocal        = name.getLocalPart();
        System.out.println( "detail tag:'" + strLocal + "'" ); 
        System.out.println( "SOAPMessage:" );
        soapMessage.writeTo( System.out );
        System.out.println(  );

        String          strNamespace   = name.getNamespaceURI();
        assertTrue( "Detail does not specified its namespace URI",
                    !( strNamespace == null ||
                       strNamespace.length() == 0 ) );
        assertTrue( "Detail does not specified a right namespace URI",
                    strNamespace.equals( SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE) );
    }

}
