package saaj.client.header13;

import javax.xml.namespace.QName;
import junit.framework.TestCase;

import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.AttachmentPart;

import saaj.client.util.SAAJHelper;
import saaj.client.util.SOAPMessage13Helper;

import java.util.Iterator;
import org.w3c.dom.Document;

/**
  *  Test the Message created in SAAJ 1.2 instance
  *  The corresponding in the Server to respond is 
  *     saaj/server/com.ibm.wsfp.jaxws_1.0.0.jar/...../Header13Response.java
  *
  */
public class Header13Test extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
    
    public Header13Test(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
        super.setUp();
        SAAJHelper.setTestCase( this );
    }

    SOAPMessage directlySend( SOAPMessage soapMessage ) throws Exception{
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage  ); // SOAPConnection
        return SAAJHelper.removeAddressingHeaderElements( response );
    }

    SOAPMessage sendFault( SOAPMessage soapMessage ) throws Exception{
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage, 
                                                         javax.xml.ws.soap.SOAPFaultException.class,
                                                         "MustUnderstand"
                                                       ); // SOAPConnection
        return SAAJHelper.removeAddressingHeaderElements( response );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testHeader13_1() throws Exception {
        System.out.println( );
        System.out.println("------------testHeader13_1() No SOAPHeader--------");

        SOAPMessage soapMessage = createRequest1Msg();
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = directlySend( soapMessage  ); // SOAPConnection

        verifyHeader1Message( response );
    }

    private SOAPMessage createRequest1Msg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        Name bodyName           = soapFactory.createName("Header13_1",
                                                         "n13", "http://saaj.ibm.com/n13");
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( "testHeader13:Header13_1" );

        SOAPHeader      soapHeader  = soapMessage .getSOAPHeader();
        soapHeader.detachNode();   // Remove Header from SOAPMessage

        return soapMessage; 
    }

    private void verifyHeader1Message( SOAPMessage response ) throws Exception{
        SOAPHeaderElement soapHeaderElem = SAAJHelper.getHeaderFirstChild( response );
        Name name = soapHeaderElem.getElementName();
        String strLocalName = name.getLocalName();
        assertTrue( "localName expects 'Header13' but get '" +
                    strLocalName + "'",
                    strLocalName.equals( "Header13" ) );

        String strUri = name.getURI();
        assertTrue( "URI expects 'http://ibm.org/Texas' but get '" +
                    strUri + "'",
                    strUri.equals( "http://ibm.org/Texas" ) );

        SOAPFactory soapFactory  = SAAJHelper.getSOAPFactory13();
        Name attrName  = soapFactory.createName( "attrib13" );
        String strWS   = soapHeaderElem.getAttributeValue( attrName );
        assertTrue( "The Attribute Value of attrib13 expects 'WebServices' but get '"+
                    strWS + "'",
                    strWS.equals( "WebServices" )
                  );

        assertTrue( "MustUnderstand expects true but get false",
                    soapHeaderElem.getMustUnderstand() );
        String strActor = soapHeaderElem.getActor();
        assertTrue( "Actor expects 'Pink Panther' but get '" +
                    strActor + "'",
                    strActor.equals( "Pink Panther" ) );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testHeader13_2() throws Exception {
        System.out.println( );
        System.out.println("------------testHeader13_2() two SOAPHeaderElement --------");

        SOAPMessage soapMessage = createRequest2Msg();
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = sendFault( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyHeader2Message( response );
    }

    private SOAPMessage createRequest2Msg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        // Name must Namespace qualified
        Name        headerName  = soapFactory.createName("Header13_2", "home", "http://northernstar.org/h2");
        SOAPHeader  soapHeader  = soapMessage .getSOAPHeader();
        SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
        soapHeaderElem.setRole( "Header13_2" );  // setActor in 12
        soapHeaderElem.setMustUnderstand( false );
        soapHeaderElem.setRelay( false ); 
        soapHeaderElem.addTextNode( "ServerSocket gave me a lot of troubles on multi-ethernet-card until I knew it" );

        Name        headerName1  = soapFactory.createName("Header13_2_1", "mustHave", "http://lyqs.austin.ibm.com/saaj/headernn");
        SOAPHeaderElement soapHeaderElem1 = soapHeader.addHeaderElement( headerName1 );
        soapHeaderElem1.setActor( SOAPConstants.URI_SOAP_1_2_ROLE_NEXT );  //
        soapHeaderElem1.setMustUnderstand( true ); 
        soapHeaderElem1.addTextNode( "Header13 2 1" );

        Name bodyName           = soapFactory.createName("Header13_2",
                                                         "n13", "http://saaj.ibm.com/n13");
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( "headerBody13_2_text" );

        return soapMessage; 
    }

    private void verifyHeader2Message( SOAPMessage response ) throws Exception{
        // Check for MustUnderstand Fault
        SOAPBody  soapBody  = response.getSOAPBody();
        SOAPFault soapFault = soapBody.getFault();
        String    faultCode = soapFault.getFaultCode().toLowerCase();
        System.out.println( "Header test 2 expect a MutsUnderstand fault code :'" +
                            faultCode + "'" );
        assertTrue( "expecting a MutsUnderstand fault code but did not get it",
                    faultCode.indexOf( "mustunderstand" ) >= 0 );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testHeader13_3() throws Exception {
        System.out.println( );
        System.out.println("------------testHeader13_3() one SOAPHeaderElement --------");

        SOAPMessage soapMessage = createRequest3Msg();
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = directlySend( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyHeader3Message( response );
    }

    private SOAPMessage createRequest3Msg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name        headerName  = soapFactory.createName("Header13_3", "", "http://northernstar.org/h3");
        SOAPHeader  soapHeader  = soapMessage .getSOAPHeader();
        SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
        soapHeaderElem.setActor( "Header13_3" );
        soapHeaderElem.setMustUnderstand( false );
        soapHeaderElem.addTextNode( "Header13 3 3" );
        soapHeaderElem.setRelay( true );      // 1.3 new

        soapBody.addNamespaceDeclaration( "h13", "http://AirForceNumberOne.edu.us/Boeing747" );
        SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.addChildElement( "Header13_3", "h13" );
        soapBodyElement.addTextNode( "Boeing 747 is a giant airplane" );

        return soapMessage; 
    }

    private void verifyHeader3Message( SOAPMessage response ) throws Exception{
        SOAPHeaderElement soapHeaderElem = SAAJHelper.getHeaderFirstChild( response );
        Name name = soapHeaderElem.getElementName();
        String strLocalName = name.getLocalName();
        assertTrue( "localName expects 'Header13_3' but get '" +
                    strLocalName + "'",
                    strLocalName.equals( "Header13_3" ) );

        String strUri = name.getURI();
        assertTrue( "URI expects 'http://www.ibm.org/Texas' but get '" +
                    strUri + "'",
                    strUri.equals( "http://www.ibm.org/Texas" ) );

        SOAPFactory soapFactory  = SAAJHelper.getSOAPFactory13();
        Name attrName  = soapFactory.createName( "attrib13_3", "at133", "http://ibm.org/Austin" ); 
        String strWS   = soapHeaderElem.getAttributeValue( attrName );
        assertTrue( "The Attribute Value of attrib13_3 expects 'attribute 133' but get '" +
                    strWS + "'",
                    strWS.equals( "attribute 133" )
                  );

        assertTrue( "MustUnderstand expects false but get true",
                    !soapHeaderElem.getMustUnderstand() );

        String strActor = soapHeaderElem.getActor();
        assertTrue( "Actor expects 'John Weng' but get '" +
                    strActor + "'",
                    strActor.equals( "John Weng" ) );

        SOAPBodyElement soapBodyElement = SAAJHelper.getBodyFirstChild( response );
        String strValue = soapBodyElement.getValue();
        assertTrue( "Expected the text on SOAPBodyElement as " + 
                    "'It's very expensive for a private Boeing 737' but get '" +
                    strValue + "'",
                    strValue.equals( "It's very expensive for a private Boeing 737" ));
        name = soapBodyElement.getElementName();
        strLocalName = name.getLocalName();
        assertTrue( "localName expects 'boeing747_1' but get '" +
                    strLocalName + "'",
                    strLocalName.equals( "boeing747_1" ) );

        strUri = name.getURI();
        assertTrue( "URI expects 'http://privateJet.edu.us/Boeing737' but get '" +
                    strUri + "'",
                    strUri.equals( "http://privateJet.edu.us/Boeing737" ) );

    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testHeader13_4() throws Exception {
        System.out.println( );
        System.out.println("------------testHeader13_4() two SOAPHeaderElement --------");

        SOAPMessage soapMessage = createRequest4Msg();
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = directlySend( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyHeader4Message( response );
    }

    private SOAPMessage createRequest4Msg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        SOAPHeader  soapHeader  = soapMessage .getSOAPHeader();

        Name        headerName  = soapFactory.createName("Header13_4", 
                                                          "mn", 
                                                          "http://musthave.namespace.com/headerElement" );
        SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
        soapHeaderElem.addTextNode( "Header13_4" );

        Name        headerName1  = soapFactory.createName("Header13_4_1",
                                                          "mn", 
                                                          "http://musthave.namespace.com/headerElement" );
        SOAPHeaderElement soapHeaderElem1 = soapHeader.addHeaderElement( headerName1 );
        soapHeaderElem1.addTextNode( "Header13 4 1" );

        Name bodyName           = soapFactory.createName("Header13_4",
                                                          "mn", 
                                                          "http://musthave.namespace.com/headerElement" );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( "Header13_4_text" );

        return soapMessage; 
    }

    private void verifyHeader4Message( SOAPMessage response ) throws Exception{
        SOAPHeaderElement soapHeaderElem = SAAJHelper.getHeaderFirstChild( response );
        Name name = soapHeaderElem.getElementName();
        String strLocalName = name.getLocalName();
        assertTrue( "localName expects 'Header13' but get '" +
                    strLocalName + "'",
                    strLocalName.equals( "Header13" ) );

        String strUri = name.getURI();
        assertTrue( "URI expects 'http://ibm.org/Texas' but get '" +
                    strUri + "'",
                    strUri.equals( "http://ibm.org/Texas" ) );

    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testHeader13_5() throws Exception {
        System.out.println( );
        System.out.println("------------testHeader13_5() two SOAPHeaderElement --------");

        SOAPMessage soapMessage = createRequest5Msg();
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = directlySend( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyHeader5Message( response );
    }


    private SOAPMessage createRequest5Msg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        SOAPHeader  soapHeader  = soapMessage .getSOAPHeader();

        Name        headerName  = soapFactory.createName("Header13_5", 
                                                         "", 
                                                         "http://default.ibm.com/default" );
        SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
        soapHeaderElem.addTextNode( "Header13_5" );

        Name        headerName1  = soapFactory.createName("Header13_5_1",
                                                          "", 
                                                          "http://default.ibm.com/default" );
        SOAPHeaderElement soapHeaderElem1 = soapHeader.addHeaderElement( headerName1 );
        soapHeaderElem1.addTextNode( "Header13 5 1" );

        Name        headerName2  = soapFactory.createName("Header13_5_2",
                                                          "", 
                                                          "http://default.ibm.com/default" );
        SOAPHeaderElement soapHeaderElem2 = soapHeader.addHeaderElement( headerName2 );
        soapHeaderElem2.addTextNode( "Header13 5 2" );

        Name bodyName           = soapFactory.createName("Header13_5", "",
                                                         "http://default.ibm.com/default" );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( "Header13_5_text" );

        return soapMessage; 
    }

    private void verifyHeader5Message( SOAPMessage response ) throws Exception{
        SOAPHeaderElement soapHeaderElem = SAAJHelper.getHeaderFirstChild( response );
        Name name = soapHeaderElem.getElementName();
        String strLocalName = name.getLocalName();
        assertTrue( "localName expects 'Header13' but get '" +
                    strLocalName + "'",
                    strLocalName.equals( "Header13" ) );

        String strUri = name.getURI();
        assertTrue( "URI expects 'http://ibm.org/Texas' but get '" +
                    strUri + "'",
                    strUri.equals( "http://ibm.org/Texas" ) );

    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testHeader13_6() throws Exception {
        System.out.println( );
        System.out.println("------------testHeader13_6() one SOAPHeaderElement --------");

        SOAPMessage soapMessage = createRequest6Msg();
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = directlySend( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyHeader3Message( response );
    }

    private SOAPMessage createRequest6Msg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name        headerName  = soapFactory.createName("Header13_6", "envX", "http://www.austin.texas/ibm" );
        SOAPHeader  soapHeader  = soapMessage .getSOAPHeader();
        SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
        soapHeaderElem.setActor( "Header13_6" );
        soapHeaderElem.setMustUnderstand( false );
        soapHeaderElem.addTextNode( "Header13 6 6" );

        soapBody.addNamespaceDeclaration( "h13", "http://AirForceNumberOne.edu.us/Boeing747" );
        SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.addChildElement( "Header13_6", "h13" );
        soapBodyElement.addTextNode( "Boeing 747 is a giant airplane" );

        return soapMessage; 
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testHeader13_7() throws Exception {
        System.out.println( "------------- testHeader13_7------------" );
        String          strNV           = SOAPConstants.SOAP_1_2_PROTOCOL;
        MessageFactory  messageFactory  = MessageFactory.newInstance( strNV );
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPFactory     soapFactory     = SOAPFactory.newInstance( strNV );

        try{
            Name        headerName  = soapFactory.createName("Header13_7" );
            SOAPHeader  soapHeader  = soapMessage .getSOAPHeader();
            SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
            // Per 401077, we would allow more flexible to prevent backward
            // in-compatible issue. 
            //fail( "SOAPHeaderElement is expected to be Namespace specified." + 
            //      " But it is created without Exception." );
        } catch( Exception e ){
            System.out.println( "Get the expected exception" );
        }
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testHeader13_8() throws Exception {
        System.out.println( );
        System.out.println("------------testHeader13_8() a long String --------");

        SOAPMessage soapMessage = createRequest8Msg();
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = directlySend( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyHeader8Message( response );
    }

    private SOAPMessage createRequest8Msg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name        headerName  = soapFactory.createName("Header13_8", "envX", "http://www.austin.texas/ibm" );
        SOAPHeader  soapHeader  = soapMessage .getSOAPHeader();
        SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
        soapHeaderElem.setActor( "Header13_8" );
        soapHeaderElem.setMustUnderstand( false );
        soapHeaderElem.addTextNode( "Header13 8 8" );

        soapBody.addNamespaceDeclaration( "long", "http://ut.edu.us/longhorn" );
        SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.addChildElement( "Header13_8", "long" );
        soapBodyElement.addTextNode( SAAJHelper.get10KLongString() );

        return soapMessage; 
    }


    private void verifyHeader8Message( SOAPMessage response ) throws Exception{
        String     strLong         = SAAJHelper.getBodyFirstChildValue( response );
        String     strLong1        = SAAJHelper.get10KLongString();
        assertTrue( "Did not get back the expected long string but get '" +strLong + "'",
                    strLong1.equals( strLong ) );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testHeader13_9() throws Exception {
        System.out.println( );
        System.out.println("------------testHeader13_9()sending 50 attributes --------");

        SOAPMessage soapMessage = createRequest9Msg();
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = directlySend( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyHeader9Message( response );
    }

    private SOAPMessage createRequest9Msg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

        Name        headerName  = soapFactory.createName("Header13_9", "env50", "http://www.austin.texas/attibute50" );
        SOAPHeader  soapHeader  = soapMessage .getSOAPHeader();
        SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
        soapHeaderElem.setActor( "Header13_9" );
        soapHeaderElem.setMustUnderstand( false );
        soapHeaderElem.addTextNode( "testing 50 attributes" );

        soapBody.addNamespaceDeclaration( "lotsOfAttributes", "http://ut.edu.us/lotsof" );
        SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.addChildElement( "Header13_9", "lotsOfAttributes" );
        soapBodyElement.addTextNode( "51 attributes" );
        SAAJHelper.addAttribute( soapBodyElement, 13 );
        return soapMessage; 
    }


    private void verifyHeader9Message( SOAPMessage response ) throws Exception{
        SOAPBodyElement  soapBodyElement  = SAAJHelper.getBodyFirstChild( response );
        assertTrue( "Did not get back the expected 51 attributes ",
                    SAAJHelper.verifyAttribute( soapBodyElement, 13 ) );
    }

}
