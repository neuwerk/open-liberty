package saaj.client.simple13;

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
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;

import saaj.client.util.SAAJHelper;
import saaj.client.util.SOAPMessage13Helper;

import java.util.Locale;
import java.util.Iterator;
import org.w3c.dom.Document;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
  *  Test the Message created in SAAJ 1.2 instance
  *  The corresponding in the Server to respond is 
  *     saaj/server/com.ibm.wsfp.jaxws_1.0.0.jar/..../Simple13Response.java
  *
  */
public class Simple13Test extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    
    public Simple13Test(String name) {
        super(name);
    }
    
    boolean bTest3 = false;
    public void setUp() throws Exception {
        super.setUp();
        SAAJHelper.setTestCase( this );
        bTest3 = false;
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSimple13_1() throws Exception {
        System.out.println( );
        System.out.println("------------testSimple13_1() one SOAPBody--------");

        SOAPMessage soapMessage = createRequest1aMsg();
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage  );

        verifySimple1Message( response );
    }

    private SOAPMessage createRequest1aMsg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SAAJHelper.getSOAPFactory13();
        Name bodyName           = soapFactory.createName("Simple13",
                                                          "n13", "http://saaj.ibm.com/n13");
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( "testSimple13:simple13" );
        return soapMessage; 
    }

    private SOAPMessage createRequest1Msg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SAAJHelper.getSOAPFactory13();
        Name bodyName           = soapFactory.createName("Simple13", 
                                                         "Simple13Prefix", 
                                                         "http://soap.ibm.com/simple13");   
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( "testSimple13 2 bodies" );
        return soapMessage; 
    }

    private void verifySimple1Message( SOAPMessage soapMessage ){
        if( SAAJHelper.isSaaj13Message( soapMessage ) ){
            String strSoapString = SAAJHelper.getBodyFirstChildStr( soapMessage );
            assertTrue( "expect 'simple13:simple13response' but get back '" + strSoapString + "'",
                        strSoapString.equals( "simple13:simple13response" ) );

            String strUri = SAAJHelper.getBodyFirstChildUri( soapMessage );
            assertTrue( "expect \"http://www.ibm.com/simple13\" but get back '" + strSoapString + "'",
                        strUri.equals( "http://www.ibm.com/simple13" ) );

            if( ! bTest3 ){
                String strText = SAAJHelper.getBodyFirstChildValue( soapMessage );
                assertTrue( "expect 'simple13 text response' but get back '" + strText + "'" ,
                            strText.equals("simple13 text response") );
            }
        } else {                                 
            fail( "The responding SOAPMessage is supposed to be in SAAJ1.2(SOAP1.1) but not" );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSimple13_2() throws Exception {
        System.out.println( );
        System.out.println("--------------------testSimple13_2() two SOAPBodies-------------------");

        SOAPMessage soapMessage = createRequest2Msg();
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage );

        verifySimple2Message( response );
    }

    private SOAPMessage createRequest2Msg() throws Exception{
        SOAPMessage soapMessage = createRequest1Msg();
        SOAPBody    soapBody    = soapMessage .getSOAPBody();
        Document    doc1        = SAAJHelper.newDocument( SAAJHelper.STR_BODY_ELEMENT_1 );
        SOAPBodyElement soapBodyElement1 = soapBody.addDocument( doc1 );

        return soapMessage;
    }

   

    private void verifySimple2Message( SOAPMessage soapMessage ) throws Exception{
        verifySimple1Message( soapMessage );
        SOAPBodyElement secondBodyElem = SAAJHelper.getBodySecondChild( soapMessage );
        assertTrue( "The second SOAPBodyElement is not '" +
                    SAAJHelper.STR_BODY_ELEMENT_2 + "'",
                    SAAJHelper.isEqual13( (SOAPElement)secondBodyElem,
                                          SAAJHelper.STR_BODY_ELEMENT_2 ) );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSimple13_3() throws Exception {
        bTest3 = true;
        System.out.println( );
        System.out.println("--------------------testSimple13_3() 2 childreen + one Grand Child -------------------");
        SOAPMessage soapMessage = createRequest3Msg();
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage );
        verifySimple3Message( response );
    }

    private SOAPMessage createRequest3Msg() throws Exception{
        SOAPMessage soapMessage         = createRequest2Msg();
        SOAPBodyElement soapBodyElement = SAAJHelper.getBodyFirstChild( soapMessage );
        soapBodyElement.removeContents();
        // ToDo: Need to test this piece of code
        //Iterator grandChildren          = soapBodyElement.getChildElements();
        //System.out.println( "remove the grand children" );
        //while( grandChildren.hasNext() ){
        //    Node node = (Node)grandChildren.next();
        //    System.out.println( "remove the grand child:" + node.toString() );
        //    node.detachNode(); // remove the node from the tree
        //    node.recycleNode();
        //}

        // Single GrandChild
        SOAPFactory soapFactory = SAAJHelper.getSOAPFactory13();
        Name bodyName           = soapFactory.createName("Simple13GrandChild",
                                                         "grand",
                                                         "urn://checkGrandChild"
                                                        );
        SOAPElement soapElement = soapBodyElement.addChildElement( bodyName );
        soapElement.addTextNode( "The first Grand Child " );
        return soapMessage;
    }

    private void verifySimple3Message( SOAPMessage soapMessage ) throws Exception{
        verifySimple2Message( soapMessage );
        SOAPBodyElement firstChild = SAAJHelper.getBodyFirstChild( soapMessage );
        SOAPFactory soapFactory  = SAAJHelper.getSOAPFactory13();
        int iI = 0;
        for( ; iI < 8; iI ++ ){ //Add third dozen children
            Name     name = soapFactory.createName("Simple13GrandChild" + iI);
            Iterator iter = firstChild.getChildElements( name );
            int iCount = 0;
            while( iter.hasNext() ){
                iCount ++;
                SOAPElement soapElement = (SOAPElement)iter.next();
                String strValue = soapElement.getValue();
                assertTrue( "Expect 'Grand Child " + iI + "' but get '" + strValue + "'",
                            strValue.equals( "Grand Child " + iI )
                          );
            }
            assertTrue( "expect only one Simple13GrandChild" + iI +
                        " but get " + iCount + " items",
                        iCount == 1 );
        }

        // 9-28 With URI
        for( ; iI < 28; iI ++ ){ //Add a dozen children
            Name name  = soapFactory.createName("Simple13GrandChild" + iI,                     
                                                "grand" + iI,                                  
                                                "http://austin.ibm.com/checkGrandChild" + iI   
                                               );
            Iterator iter = firstChild.getChildElements( name );
            int iCount = 0;
            while( iter.hasNext() ){
                iCount ++;
                SOAPElement soapElement = (SOAPElement)iter.next();
                String strValue = soapElement.getValue();
                assertTrue( "Expect 'Grand Child " + iI + "' but get '" + strValue + "'",
                            strValue.equals( "Grand Child " + iI )
                          );
            }
            assertTrue( "expect only one Simple13GrandChild" + iI +
                        " but get " + iCount + " items",
                        iCount == 1 );
        }
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSimple13_4() throws Exception {
        System.out.println( );
        System.out.println("------------testSimple13_4() Using IBM SAAJ --------");

        SOAPMessage soapMessage = createRequest4Msg();
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage );

        verifySimple4Message( soapMessage, response );
    }

    private SOAPMessage createRequest4Msg() throws Exception{
        // SOAP 1.1
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = createRequest1aMsg();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        SOAPFactory soapFactory         = SAAJHelper.getSOAPFactory13();
        Name bodyName                   = soapFactory.createName("SOAPMessage", 
                                                                 "client", 
                                                                 "http://w3.ibm.com/austin" );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( soapMessage.getClass().getName() );

        bodyName                        = soapFactory.createName("SOAPBody",
                                                                 "client", 
                                                                 "http://w3.ibm.com/austin" );
        soapBodyElement                 = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( soapBody.getClass().getName() );

        bodyName                        = soapFactory.createName("SOAPBodyElement",
                                                                 "client", 
                                                                 "http://w3.ibm.com/austin" );
        soapBodyElement                 = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( soapBodyElement.getClass().getName() );

        bodyName                        = soapFactory.createName("SOAPFactory",
                                                                 "client", 
                                                                 "http://w3.ibm.com/austin" );
        soapBodyElement                 = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( soapFactory.getClass().getName() );

        return soapMessage; 
    }

    private void verifySimple4Message( SOAPMessage reqMsg, SOAPMessage ansMsg )
                   throws Exception {
        if( SAAJHelper.isSaaj13Message( ansMsg ) ){
            verifySimple1Message( ansMsg );
            SOAPBody soapBody = reqMsg.getSOAPBody();
            verifyIBMClasses( soapBody, "Client class is not IBM Class. " );
            soapBody          = ansMsg.getSOAPBody();
            //verifyIBMClasses( soapBody, "Server class is not IBM class. " );
        } else {                                 
            fail( "The responding SOAPMessage is supposed to be in SAAJ1.2(SOAP1.1) but not" );
        }
    }

    private void verifyIBMClasses( SOAPBody soapBody, String strMsg ){
        Iterator iter = soapBody.getChildElements();
        int iCount = 0;
        while( iter.hasNext() ){
            iCount ++;
            SOAPElement soapElement = (SOAPElement)iter.next();
            if( iCount < 2 ) continue; // The first 1 
            Name   name      = soapElement.getElementName();
            String localName = name.getLocalName();
            String strValue  = soapElement.getValue();
            assertTrue( strMsg + localName + " is " + strValue,
                        strValue.indexOf( "com.ibm." ) >= 0 
                      );
        }
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSimple13_5() throws Exception {
        System.out.println( );
        System.out.println("------------testSimple13_5() scomplex attachment --------");

        SOAPMessage soapMessage = createRequest5Msg();
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage );

        verifySimple5Message( response );
    }

    private SOAPMessage createRequest5Msg() throws Exception{
        // SOAP 1.1
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = createRequest1aMsg();
        ByteArrayOutputStream baos      = new ByteArrayOutputStream();
        soapMessage.writeTo( baos );

        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        SOAPFactory soapFactory         = SAAJHelper.getSOAPFactory13();
        Name hrefLowCaseName            = soapFactory.createName("hrefLowCase",
                                                          "n13", "http://saaj.ibm.com/n13");
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( hrefLowCaseName );
        Name hrefName                   = soapFactory.createName("href" );
        soapBodyElement.addAttribute( hrefName, "character_lowcase" );
        soapBodyElement.setEncodingStyle( "http://encodingsytle.newfashion.com/newstyle" );

        return soapMessage; 
    }

    private void verifySimple5Message( SOAPMessage ansMsg ) throws Exception{
        if( SAAJHelper.isSaaj13Message( ansMsg ) ){
            verifySimple1Message( ansMsg );
        } else {                                 
            fail( "The responding SOAPMessage is supposed to be in SAAJ1.2(SOAP1.1) but not" );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSimple13_6() throws Exception {
        System.out.println( );
        System.out.println("------------testSimple13_6() test fault--------");

        SOAPMessage soapMessage = createRequest6Msg();
        SOAPMessage response = null;
        try{
            response = SOAPMessage13Helper.send( soapMessage,
                                                  javax.xml.ws.WebServiceException.class,
                                                  "The SOAPMessage is in fault. John?",
                                                  "HTTP ( 400 ) Bad Request"
                                               );
        } catch( Exception e ){
            String strE = e.toString();
            System.out.println( "Hit Exception:" + strE );
            if( e instanceof javax.xml.stream.XMLStreamException ||
                e instanceof javax.xml.ws.WebServiceException    ||
                e instanceof org.apache.axiom.om.OMException      ){
                if( strE.indexOf( "must consist of well-formed character data or markup" ) >= 0 ){
                    fail( "Can not get a XML-well-formed SOAPFault in response. This must be Defect 431318" );
                }
            } else {
                throw e;
            }
        } 
       
        if( response != null ){   // If null, it's already translate to Exception
            verifySimple6Message( response );
        }
    }

    private SOAPMessage createRequest6Msg() throws Exception{
        SOAPMessage     soapMessage     = createRequest1aMsg();
        SOAPBodyElement soapBodyElement = SAAJHelper.getBodyFirstChild( soapMessage );
        soapBodyElement.removeContents();
        soapBodyElement.addTextNode( "testSimple13:fault" );
        return soapMessage; 
    }

    private void verifySimple6Message( SOAPMessage soapMessage ) throws Exception{
        if( SAAJHelper.isSaaj13Message( soapMessage ) ){
            SOAPBodyElement soapBodyElement = SAAJHelper.getBodyFirstChild( soapMessage );
            assertTrue( "Does not get an SOAPFault", soapBodyElement instanceof SOAPFault ); 
            SOAPFault soapFault = (SOAPFault)soapBodyElement;

            System.out.println( "Client Locale.getDefault():" + Locale.getDefault() );
            String    strFault  = soapFault.getFaultReasonText( Locale.US );
            assertTrue( "strFaultString is supposed to be '" +
                        "  The SOAPMessage is in fault. John?  ' but it is '" + 
                        strFault + "'",
                        strFault.equals( "  The SOAPMessage is in fault. John?  " ));

            String    strActor  = soapFault.getFaultActor();
            assertTrue( "Fault Actor is supposed to be '" +
                        "http://movie.disney.com/MickeyMouse' but it is '" + 
                        strActor + "'",
                        strActor.equals( "http://movie.disney.com/MickeyMouse" ));

            Name name = soapFault.getFaultCodeAsName();

            SAAJHelper.verifyName( name,               // name
                                   "Fault Code name",  // message
                                   "Sender",           // local name
                                   "",                 // prefix
                                   SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE // URI
                                 );

            // Testing Detail
            Detail detail = soapFault.getDetail();
            Iterator iter = detail.getDetailEntries(); // This is one item only
            int iCount = 0;
            while( iter.hasNext() ){
                iCount ++;
                DetailEntry detailEntry = (DetailEntry)iter.next();
                Name nameDetail         = detailEntry.getElementName();
                SAAJHelper.verifyName( nameDetail,                   // name
                                       "DetailEntry Element Name ",  // message
                                       "detailEntryIBM",             // local name
                                       "",                           // prefix
                                       "http://www.ibm.com/austin/WebServices"  // Prefix/URI got removed by removeNamespaceDeclaration
                                     );

                String strEnc = detailEntry.getEncodingStyle();
                assertTrue( "DetailEntry  Encoding Style expects 'TexasSize' but get '" +
                            strEnc + "'",
                            strEnc.equals( "http://TexasSize" )
                          );

                String strText = detailEntry.getValue();
                assertTrue( "DetailEntry expects '   Texas Size is giant usually   ' but gets '"+
                            strText + "'",
                            strText.equals( "   Texas Size is giant usually   " )
                          );
            }

            assertTrue( "This has 1 Detail Entry. But it gets " + iCount + " items",
                        iCount == 1 );

        } else {                                 
            fail( "The responding SOAPMessage is supposed to be in SAAJ1.2(SOAP1.1) but not" );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSimple13_7() throws Exception {
        System.out.println( );
        System.out.println("------------testSimple13_7() make sure it's IBM SAAJ not Axis2 --------");

        MessageFactory        messageFactory        = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPFactory           soapFactory           = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        String str1 = messageFactory.getClass().getName();
        System.out.println( "MessageFactory class is " + str1 );
        assertTrue( "MessageFactory is not IBM SAAJ:" + str1,
                    str1.indexOf( "com.ibm" ) >= 0 );
        str1 = soapConnectionFactory.getClass().getName();
        System.out.println( "SOAPConnectionFactory class is " + str1 );
        assertTrue( "SOAPConnectionFactory is not IBM SAAJ:" + str1,
                    str1.indexOf( "com.ibm" ) >= 0 );
        str1 = soapFactory.getClass().getName();
        System.out.println( "SOAPFactory class is " + str1 );
        assertTrue( "SOAPFactory is not IBM SAAJ:" + str1,
                    str1.indexOf( "com.ibm" ) >= 0 );

        SOAPMessage soapMessage = messageFactory.createMessage();
        MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();
        Iterator    iterMimes   = mimeHeaders.getAllHeaders();
        System.out.println( "iterMimes : " + iterMimes );
        while( iterMimes.hasNext() ){
            MimeHeader mimeHeader = (MimeHeader)iterMimes.next();
            System.out.println( "MimeHeader name:'" + mimeHeader.getName() + "' value:'" +
                                mimeHeader.getValue() + "'" );
        }
        String[]    aStr        = mimeHeaders.getHeader( "Content-Type" );
        if( aStr != null &&
            aStr.length > 0 ){ // currently we do not have the transport-specific MimeHeader(s) 
                               // It does not hurt anything
            assertTrue( "New SOAPMessage does not have MimeHeaders as Content-Type",
                        aStr != null && aStr.length > 0 ); 
            System.out.println( "Content-Type=" + aStr[0] );
            assertTrue( "Content-Type in SAAJ1.2 is not 'application/soap+xml' but '" + 
                        aStr[0] + "'",
                        aStr[0].equals( "application/soap+xml" ) );
        }
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSimple13_8() throws Exception {
        System.out.println( );
        System.out.println("------------testSimple13_8() test attribute with NameSpace--------");

        SOAPMessage soapMessage = createRequest8Msg();
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage );

        verifySimple8Message( response );
    }

    private SOAPMessage createRequest8Msg() throws Exception{
        SOAPMessage     soapMessage     = createRequest1aMsg();
        SOAPBodyElement soapBodyElement = SAAJHelper.getBodyFirstChild( soapMessage );
        SOAPFactory soapFactory = SAAJHelper.getSOAPFactory13();
        Name attrName           = soapFactory.createName("attr_simple13", 
                                                         "prefix_a", 
                                                         "http://austin.org/austin" );
        soapBodyElement.addAttribute( attrName, "austin,TX" );

        try{
            SOAPElement soapElement = soapBodyElement.addChildElement( "Simple13GrandChild1",
                                                                       "notDefinedYet"
                                                                     );
            fail( "notDefinedYet is not defined. It's supposed to throw an Exception" );
        } catch( Exception e ){
            // This is expected, since the "notDefinedYet" nameSpace is not defined yet
        }
        return soapMessage; 
    }

    private void verifySimple8Message( SOAPMessage soapMessage ) throws Exception{
        verifySimple1Message( soapMessage );
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSimple13_9() throws Exception {
        System.out.println( );
        System.out.println("------------testSimple13_9() Server Attribute--------");

        SOAPMessage soapMessage = createRequest9Msg();
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage  );

        verifySimple9Message( response );
    }

    private SOAPMessage createRequest9Msg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SAAJHelper.getSOAPFactory13();
        Name bodyName           = soapFactory.createName("Simple13",
                                                          "n13", "http://saaj.ibm.com/n13");
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( "testSimple13:serverAttribute" );
        soapMessage.setContentDescription( "test setContentDescription() string");
        if( soapMessage.saveRequired() ){
            soapMessage.saveChanges();
        }
        return soapMessage; 
    }


    private void verifySimple9Message( SOAPMessage soapMessage ) throws Exception{
        verifySimple1Message( soapMessage );
        // Verify if it get the
        SOAPBodyElement soapBodyElement = SAAJHelper.getBodyFirstChild( soapMessage );
        // To create SOAP Element instances
        SOAPFactory soapFactory = SAAJHelper.getSOAPFactory13();
        Name bodyName           = soapFactory.createName("serverAttribute" );
        String str              = soapBodyElement.getAttributeValue( bodyName );
        assertTrue( "Expect the serverAttribute as 'created Server Attribute' but get back '" +
                   str + "'",
                   str.equals( "created Server Attribute" ) );
        if( SAAJHelper.reviewMinor() ){
        //if( true ){
            String strContentDescription = soapMessage.getContentDescription();
            String str1 = "response setContentDescription() string";
            assertTrue( "Did not get the expected ContentDescription '" + 
                        strContentDescription  + "'" ,
                        str1.equals(strContentDescription)
                      );
        }
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSimple13_a() throws Exception {
        System.out.println( );
        System.out.println("------------testSimple13_a() verify createQName --------");

        MessageFactory        messageFactory        = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPFactory           soapFactory           = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        SOAPMessage soapMessage = messageFactory.createMessage();
        MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();
        Iterator    iterMimes   = mimeHeaders.getAllHeaders();
        System.out.println( "iterMimes : " + iterMimes );
        while( iterMimes.hasNext() ){
            MimeHeader mimeHeader = (MimeHeader)iterMimes.next();
            System.out.println( "MimeHeader name:'" + mimeHeader.getName() + "' value:'" +
                                mimeHeader.getValue() + "'" );
        }

        String strURI13a        = "s13a";
        Name bodyName           = soapFactory.createName("Simple13A", strURI13a, 
                                                         "http://www.ibm.com/ss13a"  );
        SOAPHeader soapHeader   = soapMessage.getSOAPHeader();
        SOAPHeaderElement shEl  = soapHeader.addHeaderElement( bodyName );

        // Make sure the URI exists
        try{
            String strURI  = shEl.getNamespaceURI( strURI13a );
        } catch( Exception e ){
            e.printStackTrace(System.out);
            fail( "SOAPElement getNamespaceURI() failed" );
        }

        try{
            QName qName = shEl.createQName( "crQName1", strURI13a );  // This ought to be OK
        } catch( Exception e ){
            e.printStackTrace(System.out);
            fail( "SOAPElement createQName() failed(1)" );
        }

        try{
            QName qName = shEl.createQName( "crQName2", "noSuchPrefix" );  // This ought to fail
            fail( "SOAPElement.createQName() ought not to create an QName without URI(2)" );
        } catch( Exception e ){
            // Get the Exception
        }
    }

}
