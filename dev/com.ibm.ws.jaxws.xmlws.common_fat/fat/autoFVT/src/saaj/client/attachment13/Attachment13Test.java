//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 07.10.2008 btiffany  533957.1        remove attachment test 13_10, see notes in test.

package saaj.client.attachment13;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
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
import javax.activation.DataHandler;

/**
  *  Test the Message created in SAAJ 1.2 instance
  *  The corresponding in the Server to respond is 
  *     saaj/server/com.ibm.wsfp.jaxws_1.0.0.jar/...../Attachment13Response.java
  *
  */
public class Attachment13Test extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    boolean FORMAL = false;
    
    public Attachment13Test(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
        super.setUp();
        SAAJHelper.setTestCase( this );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAttachment13_1() throws Exception {
        System.out.println( );
        System.out.println("------------testAttachment13_1() No SOAPHeader -----");

        SOAPMessage soapMessage = createRequest1Msg();
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage  ); // SOAPConnection

        verifyAttachment1Message( response );
    }

    private SOAPMessage createRequest1Msg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage   .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        Name bodyName           = soapFactory.createName("Attachment13_1",
                                                         "n13", "http://saaj.ibm.com/n13");
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( "testAttachment13:Attachment13_1" );

        SOAPHeader      soapHeader  = soapMessage .getSOAPHeader();
        soapHeader.detachNode();   // Remove Header from SOAPMessage

        // Create a AttachmentPart
        // Add another AttachmentPart
        byte[] bArray = SAAJHelper.STR_BODY_ELEMENT_1.getBytes("utf8");
        ByteArrayInputStream bais     = new ByteArrayInputStream(bArray);
        // soapAttachPart.setContent  ( );
        AttachmentPart soapAttachPart = soapMessage.createAttachmentPart(bais, "text/xml" );
        soapAttachPart.setContentId( "Attachment13SOAPMessage" );
        soapAttachPart.addMimeHeader( "FVT-source", "STR_BODY_ELEMENT_1" );
        soapMessage.addAttachmentPart( soapAttachPart );

        return soapMessage; 
    }

    private void verifyAttachment1Message( SOAPMessage response ) throws Exception{
        SOAPHeaderElement soapHeaderElem = SAAJHelper.getHeaderFirstChild( response );
        Name name = soapHeaderElem.getElementName();
        String strLocalName = name.getLocalName();
        assertTrue( "localName expects 'Attachment13' but get '" +
                    strLocalName + "'",
                    strLocalName.equals( "Attachment13" ) );

        String strUri = name.getURI();
        assertTrue( "URI expects 'http://ibm.org/texas' but get '" +
                    strUri + "'",
                    strUri.equals( "http://ibm.org/texas" ) );

        SOAPFactory soapFactory  = SAAJHelper.getSOAPFactory13();
        Name attrName  = soapFactory.createName( "attrib13" );
        String strWS   = soapHeaderElem.getAttributeValue( attrName );
        assertTrue( "The Attribute Value of attrib13 expects 'carton' but get '"+
                    strWS + "'",
                    strWS.equals( "carton_show" )
                  );

        assertTrue( "MustUnderstand expects false but get true",
                    !soapHeaderElem.getMustUnderstand() );
        String strActor = soapHeaderElem.getActor();
        assertTrue( "Actor expects 'pink_panther' but get '" +
                    strActor + "'",
                    strActor.equals( "pink_panther" ) );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAttachment13_2() throws Exception {
        System.out.println( );
        System.out.println("------------testAttachment13_2() test multi-MimeHeader --------");

        SOAPMessage soapMessage = createRequest2Msg();
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyAttachment2Message( response );
    }

    private SOAPMessage createRequest2Msg() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name        headerName  = soapFactory.createName("Attachment13_2", "Attach", 
                                                         "http://need.default.namespace.com/aaa" );
        SOAPHeader  soapHeader  = soapMessage.getSOAPHeader();
        SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
        soapHeaderElem.setActor( "Attachment13_2" );
        soapHeaderElem.setMustUnderstand( false );
        soapHeaderElem.addTextNode( "test double-byte characters '¼w§JÂÄ´µ¦{' Texas" );

        addSOAPBody( soapMessage );

        return createAttachmentParts2( soapMessage ); 
    }

    private SOAPMessage createAttachmentParts2( SOAPMessage soapMessage ) throws Exception{
        AttachmentPart soapAttachPart1 = soapMessage.createAttachmentPart();
        String str0                    = "ABACAFFAHGAKJAKJAKAKJKKKKKKKKK";
        // create an AttachmentPart, then add it to the above soap message
        ByteArrayInputStream bais0     = new ByteArrayInputStream(str0.getBytes("utf8"));
        soapAttachPart1.setContent  ( bais0, "text/plain" );
        soapAttachPart1.setContentId( "anyString" );
        soapAttachPart1.addMimeHeader( "OughtToBeKept", "MimeHeader0" );
        soapMessage.addAttachmentPart( soapAttachPart1 );

        // Added the AttachmentPart into the SOAPMessage. 
        // Then change it. It ought be changed.
        soapAttachPart1.clearContent(); // Clear what has just done.
        String str1                    = "abcdefghijklmnopqrstuvwxyz";
        // create an AttachmentPart, then add it to the above soap message
        ByteArrayInputStream bais1     = new ByteArrayInputStream(str1.getBytes("utf8"));
        soapAttachPart1.setContent  ( bais1, "text/plain" );
        soapAttachPart1.setContentId( "lowercaseString" );
        soapAttachPart1.addMimeHeader( "changed", "change 1" );
        soapAttachPart1.addMimeHeader( "changed", "change 2" );
        soapAttachPart1.addMimeHeader( "changed", "change 3" );
        soapAttachPart1.addMimeHeader( "mustKeep", "MimeHeader" );

        Iterator iter = soapAttachPart1.getAllMimeHeaders();
        while( iter.hasNext() ){
            MimeHeader mime = (MimeHeader)iter.next();
            System.out.println( "Name:" + mime.getName() + " value:" + mime.getValue() );
        }

        String[] aStrValue = soapAttachPart1.getMimeHeader( "changed" );
        System.out.println( "Changed has " + aStrValue.length + " items ");
        for( int iI = 0; iI < aStrValue.length; iI ++ ){
            System.out.println( "   Changed: " + aStrValue[ iI ] );
        }


        // Add another AttachmentPart
        AttachmentPart soapAttachPart = soapMessage.createAttachmentPart();
        byte[] byteArray = SAAJHelper.STR_BODY_ELEMENT_2.getBytes("utf8"); 
        ByteArrayInputStream bais = new ByteArrayInputStream( byteArray );
        soapAttachPart.setContent  ( bais, "text/plain" );
        soapAttachPart.setContentId( "Attachment13SOAPMessage" );
        soapAttachPart.addMimeHeader( "mimeRemoved","Yes, They were removed" );
        soapAttachPart.removeAllMimeHeaders();  // remove all the MimeHeaders
        soapAttachPart.setContentId( "Attach13Message" );
        // soapAttachPart.addMimeHeader( "Content-Type","text/xml"); // change the type makes no sense
        // "content-transfer-endconding"="binary" ??
        soapAttachPart.addMimeHeader( "mimeNew","New Mime" );
        soapAttachPart.addMimeHeader( "HeadSize","SizeA" );
        soapAttachPart.addMimeHeader( "NotIncluded","getNonMatchingMimeHeaders" );
        soapMessage.addAttachmentPart( soapAttachPart );


        AttachmentPart soapAttachPart2 = soapMessage.createAttachmentPart();
        String str2                    = "1234567890";
        soapAttachPart2.setContent  ( str2, "text/plain" );
        soapAttachPart2.setContentId( "numericCharacters" );
        soapMessage.addAttachmentPart( soapAttachPart2 );
        soapAttachPart2.addMimeHeader( "NothingChanged","Hopefully!" );

        soapAttachPart2.addMimeHeader( "HeadSize","SizeA" );
        soapAttachPart2.removeMimeHeader( "HeadSize" );
        soapAttachPart2.addMimeHeader( "austin_TX","State Capital of Texas" );

        soapMessage.setContentDescription( "create AttachmentPart 2 ");
        if( soapMessage.saveRequired() ){
            soapMessage.saveChanges();
        }
        return soapMessage;
    }


    private void verifyAttachment2Message( SOAPMessage response ) throws Exception{

        // String expected: 
        // "size=4;
        SOAPHeaderElement soapHeaderElem = SAAJHelper.getHeaderFirstChild( response );
        Name name = soapHeaderElem.getElementName();
        String strLocalName = name.getLocalName();
        assertTrue( "localName expects 'Attachment13' but get '" +
                    strLocalName + "'",
                    strLocalName.equals( "Attachment13" ) );

        String strUri = name.getURI();
        assertTrue( "URI expects 'http://ibm.org/Texas' but get '" +
                    strUri + "'",
                    strUri.equals( "http://ibm.org/Texas" ) );

        SOAPFactory soapFactory  = SAAJHelper.getSOAPFactory13();
        Name attrName  = soapFactory.createName( "attrib13", "", "http://ibm.org/Austin" );
        String strWS   = soapHeaderElem.getAttributeValue( attrName );
        assertTrue( "The Attribute Value of attrib13 expects 'WebServices' but get null",
                    strWS != null
                  );
        assertTrue( "The Attribute Value of attrib13 expects 'WebServices' but get '" +
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
    public void testAttachment13_3() throws Exception {
        System.out.println( );
        System.out.println("------------testAttachment13_3() Count AttachmentPart --------");
        System.out.println("------------ also test image/bmp in DataHandler --------");

        SOAPMessage soapMessage = createRequestMsg("Attachment13_3");
        soapMessage             = createAttachmentParts3( soapMessage ); 
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyAttachmentMessage( response, "Attachment13_3" );
    }

    private SOAPMessage createRequestMsg( String strHeader) throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name        headerName  = soapFactory.createName(strHeader,
                                                         "allright", "http://www.ibm.com/pcsupport" );
        SOAPHeader  soapHeader  = soapMessage.getSOAPHeader();
        SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
        soapHeaderElem.setActor( strHeader );
        soapHeaderElem.setMustUnderstand( false );
        soapHeaderElem.addTextNode( "test multiple AttachmentPart" );
        addSOAPBody( soapMessage );

        return soapMessage; 
    }

    private SOAPMessage createAttachmentParts3( SOAPMessage soapMessage ) throws Exception{
        // Add an dummy AttachmentPart
        AttachmentPart soapAttachPart = soapMessage.createAttachmentPart();
        byte[] byteArray = SAAJHelper.STR_BODY_ELEMENT_2.getBytes("utf8"); 
        ByteArrayInputStream bais  = new ByteArrayInputStream( byteArray );
        soapAttachPart.setContent  ( bais, "text/xml" );
        soapAttachPart.setContentId( "Attach13_3_Message" );
        soapAttachPart.addMimeHeader( "content-type","text/xml");
        soapAttachPart.addMimeHeader( "remark","setContent as text/xml" );
        soapMessage.addAttachmentPart( soapAttachPart );

        // Add another dummy AttachmentPart
        AttachmentPart soapAttachPart1 = soapMessage.createAttachmentPart();
        byte[] byteArray1 = SAAJHelper.STR_BODY_ELEMENT_3.getBytes("Big5"); 
        ByteArrayInputStream bais1  = new ByteArrayInputStream( byteArray1 );
        soapAttachPart1.setContent  ( bais1, "text/xml" );
        soapAttachPart1.setContentId( "Attach13_3_Message" );
        soapAttachPart1.addMimeHeader( "content-type","text/plain");
        soapAttachPart1.addMimeHeader( "mimeRemoved","Yes, They were but added back" );
        soapMessage.addAttachmentPart( soapAttachPart1 );

        //Then remove them all
        soapMessage.removeAllAttachments(); // No AttachmentPart now

        AttachmentPart  soapAttachPart4 = soapMessage.createAttachmentPart();
        java.io.File    file4           = new java.io.File("./saaj/client/util/greenBlue.GIF");
        FileInputStream fis4            = new FileInputStream( file4 );
        soapAttachPart4.setContent  ( fis4, "image/gif" );
        soapAttachPart4.setContentId( "gif4a" );
        soapAttachPart4.addMimeHeader( "wXh4", "210X80" );
        // soapMessage.addAttachmentPart( soapAttachPart4 );

        DataHandler     dataHandler7    = soapAttachPart4.getDataHandler();
        System.out.println( "datahandler for image/gif class is " + dataHandler7.getClass().getName() );
        AttachmentPart  soapAttachPart7 = soapMessage.createAttachmentPart(dataHandler7);
        soapMessage.addAttachmentPart( soapAttachPart7 );
        int iSize7 = soapAttachPart7.getSize();
        System.out.println( "   gif4 size is " + iSize7 );
        soapAttachPart7.setContentId( "gif4" );
        soapAttachPart7.addMimeHeader( "wXh", "210X80" );  // Add GIF4 attachmentPart

        AttachmentPart  soapAttachPart5 = soapMessage.createAttachmentPart();
        java.io.File    file5           = new java.io.File("./saaj/client/util/karaoke.bmp");
        FileInputStream fis5            = new FileInputStream( file5 );
        soapAttachPart5.setContent  ( fis5, "image/bmp" );
        soapAttachPart5.setContentId( "bmpKaraoke6" );
        soapAttachPart5.addMimeHeader( "wXh6", "233X153" );
        // soapMessage.addAttachmentPart( soapAttachPart5 );
        // Do not add the soapAttachmentPart5

        DataHandler     dataHandler6    = soapAttachPart5.getDataHandler();
        System.out.println( "datahandler for image/bmp class is " + dataHandler6.getClass().getName() );
        AttachmentPart  soapAttachPart6 = soapMessage.createAttachmentPart(dataHandler6);
        soapMessage.addAttachmentPart( soapAttachPart6 );
        soapAttachPart6.setContentId( "bmpKaraoke" );
        soapAttachPart6.addMimeHeader( "wXh", "233X153" );
        int iSize6 = soapAttachPart6.getSize();
        System.out.println( "   bmpKaraoke size is " + iSize6 );

        soapMessage.setContentDescription( "create AttachmentPart 3");
        if( soapMessage.saveRequired() ){
            soapMessage.saveChanges();
        }
        return soapMessage;
    }



    private void verifyAttachmentMessage( SOAPMessage response, String strName ) throws Exception{

        // String expected: 
        // "size=7;
        SOAPHeaderElement soapHeaderElem = SAAJHelper.getHeaderFirstChild( response );
        Name name = soapHeaderElem.getElementName();
        String strLocalName = name.getLocalName();
        assertTrue( "localName expects '" + strName + "' but get '" +
                    strLocalName + "'",
                    strLocalName.equals( strName ) );

        String strUri = name.getURI();
        assertTrue( "URI expects 'http://www.ibm.org/Texas' but get '" +
                    strUri + "'",
                    strUri.equals( "http://www.ibm.org/Texas" ) );

        assertTrue( "MustUnderstand expects true but get false",
                    !soapHeaderElem.getMustUnderstand() );

        String strActor = soapHeaderElem.getActor();
        assertTrue( "Actor expects 'John Weng' but get '" +
                    strActor + "'",
                    strActor.equals( "John Weng" ) );
    }



    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAttachment13_4() throws Exception {
        System.out.println( );
        System.out.println("------------testAttachment13_4() test getContent() --------");

        SOAPMessage soapMessage = createRequest4Msg("Attachment13_4");
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyAttachment4Message( response );
        SAAJHelper.verifyResponseAttachments( response, this );
    }

    private SOAPMessage createRequest4Msg(String strAttach) throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory13(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name        headerName  = soapFactory.createName(strAttach, "Attach", 
                                                         "http://need.default.namespace.com/aaa" );
        SOAPHeader  soapHeader  = soapMessage.getSOAPHeader();
        SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
        soapHeaderElem.setActor( strAttach );
        soapHeaderElem.setMustUnderstand( false );
        soapHeaderElem.addTextNode( "testing in Austin, Texas" );
        addSOAPBody( soapMessage );

        return createAttachmentParts4( soapMessage ); 
    }

    private SOAPMessage createAttachmentParts4( SOAPMessage soapMessage ) throws Exception{
        AttachmentPart soapAttachPart1 = soapMessage.createAttachmentPart();
        soapMessage.addAttachmentPart( soapAttachPart1 );
        String str1                    = "abcdefghijklmnopqrstuvwxyz";
        // create an AttachmentPart, then add it to the above soap message
        ByteArrayInputStream bais1     = new ByteArrayInputStream(str1.getBytes("utf8"));
        soapAttachPart1.setContent  ( bais1, "text/plain" );
        soapAttachPart1.setContentId( "lowercaseString" );

        // Add another AttachmentPart
        AttachmentPart soapAttachPart = soapMessage.createAttachmentPart();
        byte[] byteArray = SAAJHelper.STR_BODY_ELEMENT_2.getBytes("utf8"); 
        ByteArrayInputStream bais = new ByteArrayInputStream( byteArray );
        soapAttachPart.setContent  ( bais, "text/xml" );
        soapAttachPart.setContentId( "Attachment13SOAPMessage" );
        soapMessage.addAttachmentPart( soapAttachPart );

        AttachmentPart soapAttachPart3 = soapMessage.createAttachmentPart();
        java.io.File   file3           = new java.io.File("./saaj/client/util/websvcs.JPG");
        //java.awt.Image image3          = javax.imageio.ImageIO.read(file3);
        FileInputStream fis3           = new FileInputStream( file3 );
        soapAttachPart3.setContent  ( fis3, "image/jpeg" );
        soapAttachPart3.setContentId( "image3" );
        soapAttachPart3.addMimeHeader( "wXh", "220X90" );
        soapMessage.addAttachmentPart( soapAttachPart3 );

        AttachmentPart  soapAttachPart4 = soapMessage.createAttachmentPart();
        java.io.File    file4           = new java.io.File("./saaj/client/util/greenBlue.GIF");
        FileInputStream fis4            = new FileInputStream( file4 );
        soapAttachPart4.setContent  ( fis4, "image/gif" );
        soapAttachPart4.setContentId( "gif4" );
        soapAttachPart4.addMimeHeader( "wXh", "210X80" );
        soapMessage.addAttachmentPart( soapAttachPart4 );

        AttachmentPart  soapAttachPart5 = soapMessage.createAttachmentPart();
        java.io.File    file5           = new java.io.File("./saaj/client/util/karaoke.bmp");
        FileInputStream fis5            = new FileInputStream( file5 );
        soapAttachPart5.setContent  ( fis5, "image/bmp" );
        soapAttachPart5.setContentId( "bmp4" );
        soapAttachPart5.addMimeHeader( "wXh", "233X153" );
        soapMessage.addAttachmentPart( soapAttachPart5 );

        soapMessage.setContentDescription( "create AttachmentParts ");
        if( soapMessage.saveRequired() ){
            soapMessage.saveChanges();
        }
        return soapMessage;
    }


    private void verifyAttachment4Message( SOAPMessage response ) throws Exception{

        verifyDefect431318_1( response );

        // String expected: 
        // "size=4;
        SOAPHeaderElement soapHeaderElem = SAAJHelper.getHeaderFirstChild( response );
        Name name = soapHeaderElem.getElementName();
        String strLocalName = name.getLocalName();
        assertTrue( "localName expects 'Attachment13' but get '" +
                    strLocalName + "'",
                    strLocalName.equals( "Attachment13" ) );

        String strUri = name.getURI();
        assertTrue( "URI expects 'http://ibm.org/Texas' but get '" +
                    strUri + "'",
                    strUri.equals( "http://ibm.org/Texas" ) );

        SOAPFactory soapFactory  = SAAJHelper.getSOAPFactory13();
        Name attrName  = soapFactory.createName( "attrib13", "", "http://ibm.org/Austin" );
        String strWS   = soapHeaderElem.getAttributeValue( attrName );
        assertTrue( "The Attribute Value of attrib13 expects 'WebServices' but get null",
                    strWS != null
                  );
        assertTrue( "The Attribute Value of attrib13 expects 'WebServices' but get '" +
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
    public void testAttachment13_5() throws Exception {
        System.out.println( );
        System.out.println("------------testAttachment13_5() test getDataHandler() --------");

        SOAPMessage soapMessage = createRequest4Msg("Attachment13_5"); // the same AttachmentParts as 4
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyAttachment5Message( response );
    }


    private void verifyAttachment5Message( SOAPMessage response ) throws Exception{

        verifyDefect431318_1( response );

        SOAPHeaderElement soapHeaderElem = SAAJHelper.getHeaderFirstChild( response );
        Name name = soapHeaderElem.getElementName();
        String strLocalName = name.getLocalName();
        assertTrue( "localName expects 'Attachment13' but get '" +
                    strLocalName + "'",
                    strLocalName.equals( "Attachment13" ) );

        String strUri = name.getURI();
        assertTrue( "URI expects 'http://ibm.org/Texas' but get '" +
                    strUri + "'",
                    strUri.equals( "http://ibm.org/Texas" ) );

        SOAPFactory soapFactory  = SAAJHelper.getSOAPFactory13();
        Name attrName  = soapFactory.createName( "attrib13", "", "http://ibm.org/Austin" );
        String strWS   = soapHeaderElem.getAttributeValue( attrName );
        assertTrue( "The Attribute Value of attrib13 expects 'WebServices' but get null",
                    strWS != null
                  );
        assertTrue( "The Attribute Value of attrib13 expects 'WebServices' but get '" +
                    strWS + "'",
                    strWS.equals( "WebServices" )
                  );

        assertTrue( "MustUnderstand expects true but get false",
                    soapHeaderElem.getMustUnderstand() );

        String strActor = soapHeaderElem.getActor();
        assertTrue( "Actor expects 'Pink Panther' but get '" +
                    strActor + "'",
                    strActor.equals( "Pink Panther" ) );

        SAAJHelper.verifyResponseAttachments( response, this );
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAttachment13_6() throws Exception {
        System.out.println( );
        System.out.println("------------testAttachment13_6() sending greenBlue.gif 1227 bytes  --------");

        SOAPMessage soapMessage = createRequestMsg("Attachment13_6");
        soapMessage             = createAttachmentParts6( soapMessage ); 
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyAttachmentMessage( response, "Attachment13_6" );
    }


    private SOAPMessage createAttachmentParts6( SOAPMessage soapMessage ) throws Exception{

        AttachmentPart  soapAttachPart4 = soapMessage.createAttachmentPart();
        java.io.File    file4           = new java.io.File("./saaj/client/util/greenBlue.GIF");
        FileInputStream fis4            = new FileInputStream( file4 );
        soapAttachPart4.setContent  ( fis4, "image/gif" );
        soapAttachPart4.setContentId( "gif6" );
        soapAttachPart4.addMimeHeader( "wXh", "210X80" );
        soapMessage.addAttachmentPart( soapAttachPart4 );

        return soapMessage;
    }



    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAttachment13_7() throws Exception {
        System.out.println( );
        System.out.println("------------testAttachment13_7() sending karaoke.bmp 4958 bytes --------");

        SOAPMessage soapMessage = createRequestMsg("Attachment13_7");
        soapMessage             = createAttachmentParts7( soapMessage ); 
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyAttachmentMessage( response, "Attachment13_7" );
    }

    private SOAPMessage createAttachmentParts7( SOAPMessage soapMessage ) throws Exception{

        AttachmentPart  soapAttachPart5 = soapMessage.createAttachmentPart();
        java.io.File    file5           = new java.io.File("./saaj/client/util/karaoke.bmp");
        FileInputStream fis5            = new FileInputStream( file5 );
        soapAttachPart5.setContent  ( fis5, "image/bmp" );
        soapAttachPart5.setContentId( "bmpKaraoke7" );
        soapAttachPart5.addMimeHeader( "wXh", "233X153" );
        soapMessage.addAttachmentPart( soapAttachPart5 );

        return soapMessage;
    }



    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAttachment13_8() throws Exception {
        System.out.println( );
        System.out.println("------------testAttachment13_8() same as 13_3 --------");
        System.out.println("------------  but change the sequence of GIF and BMP --------");

        SOAPMessage soapMessage = createRequestMsg("Attachment13_8");
        soapMessage             = createAttachmentParts8( soapMessage ); 
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyAttachmentMessage( response, "Attachment13_8" );
    }

    private SOAPMessage createAttachmentParts8( SOAPMessage soapMessage ) throws Exception{
        AttachmentPart  soapAttachPart5 = soapMessage.createAttachmentPart();
        java.io.File    file5           = new java.io.File("./saaj/client/util/karaoke.bmp");
        FileInputStream fis5            = new FileInputStream( file5 );
        soapAttachPart5.setContent  ( fis5, "image/bmp" );
        soapAttachPart5.setContentId( "bmpKaraoke" );
        soapAttachPart5.addMimeHeader( "wXh", "233X153" );
        soapMessage.addAttachmentPart( soapAttachPart5 );

        AttachmentPart  soapAttachPart4 = soapMessage.createAttachmentPart();
        java.io.File    file4           = new java.io.File("./saaj/client/util/greenBlue.GIF");
        FileInputStream fis4            = new FileInputStream( file4 );
        soapAttachPart4.setContent  ( fis4, "image/gif" );
        soapAttachPart4.setContentId( "gif8" );
        soapAttachPart4.addMimeHeader( "wXh", "210X80" );
        soapMessage.addAttachmentPart( soapAttachPart4 );

        return soapMessage;
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAttachment13_9() throws Exception {
        System.out.println( );
        System.out.println("------------testAttachment13_9() same as 13_3 --------");
        System.out.println("------------  but sending a BMP and a JPEG --------");

        SOAPMessage soapMessage = createRequestMsg("Attachment13_9");
        soapMessage             = createAttachmentParts9( soapMessage ); 
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = SOAPMessage13Helper.send( soapMessage  ); // SOAPConnection
        response.writeTo( System.out );
        System.out.println( );

        verifyAttachmentMessage( response, "Attachment13_9" );
    }

    private SOAPMessage createAttachmentParts9( SOAPMessage soapMessage ) throws Exception{
        AttachmentPart  soapAttachPart5 = soapMessage.createAttachmentPart();
        java.io.File    file5           = new java.io.File("./saaj/client/util/karaoke.bmp");
        FileInputStream fis5            = new FileInputStream( file5 );
        soapAttachPart5.setContent  ( fis5, "image/bmp" );
        soapAttachPart5.setContentId( "bmpKaraoke" );
        soapAttachPart5.addMimeHeader( "wXh", "233X153" );
        soapMessage.addAttachmentPart( soapAttachPart5 );

        AttachmentPart soapAttachPart3 = soapMessage.createAttachmentPart();
        java.io.File   file3           = new java.io.File("./saaj/client/util/websvcs.JPG");
        //java.awt.Image image3          = javax.imageio.ImageIO.read(file3);
        FileInputStream fis3           = new FileInputStream( file3 );
        soapAttachPart3.setContent  ( fis3, "image/jpeg" );
        soapAttachPart3.setContentId( "image9" );
        soapAttachPart3.addMimeHeader( "wXh", "220X90" );
        soapMessage.addAttachmentPart( soapAttachPart3 );

        return soapMessage;
    }

    /**
     * TODO:
     * 7.10.08 After 533957, 
     * Attachment temp files are removed by a shutdown hook at jvm shutdown.
     * In order for this test to work, WAS would have to be restarted and the 
     * client would have to run in a spawned JVM that terminates before the 
     * junit test.  That's a BIG change. 
     * 
     * We've checked that the shutdown hooks work by manually doing those things,
     * for now due to time constraints we're going to disable this test. 
     * 
     * To check it manually, run this test, then shutdown WAS
     * and manually examine the contents of the temp directory (as defined by
     * the windows TMP environment variable) and verify that there are no
     * recent files still there.  
     * 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testAttachment13_10() throws Exception {
        System.out.println( );
        System.out.println("------------testAttachment13_10() same as 13_3 --------");
        System.out.println("------------  Test BMP and a 1.6 Mega JPEG --------");

        SOAPMessage soapMessage = createRequestMsg("Attachment13_10");
        int iFile = 0;
        int iCount= 0;
        SOAPMessage response = null;
        try{
            // fep61 SERV1\ws\code\webservices\src\com\ibm\ws\webservices\engine\attachments\ManagedMemoryDataSource.java
            // created the temporary file but forgot to delete if temp file exist
            iFile = SAAJHelper.countTempFile( "WebServices", "webservices" );
            soapMessage             = createAttachmentParts10( soapMessage ); 
            // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
            response = SOAPMessage13Helper.send( soapMessage, false  ); // SOAPConnection
        } catch( Exception e ){
            throw e;
        } finally {
            iCount = SAAJHelper.countTempFile( "WebServices", "webservices" ); 
        }

        verifyDefect431318_1( response );

        System.out.println( "Before has temp files : " + iFile  );
        System.out.println( "After  has temp files : " + iCount );
        response.writeTo( System.out );
        System.out.println( );

        verifyAttachmentMessage( response, "Attachment13_10" );
        assertTrue( "After response return, WebServices####WebServices temporary files are not removed",
                    iFile == iCount
                  );

        SAAJHelper.verifyResponseAttachments( response, this );
    }

    private SOAPMessage createAttachmentParts10( SOAPMessage soapMessage ) throws Exception{
        AttachmentPart  soapAttachPart5 = soapMessage.createAttachmentPart();
        java.io.File    file5           = new java.io.File("./saaj/client/util/karaoke.bmp");
        FileInputStream fis5            = new FileInputStream( file5 );
        soapAttachPart5.setContent  ( fis5, "image/bmp" );
        soapAttachPart5.setContentId( "bmpKaraoke" );
        soapAttachPart5.addMimeHeader( "wXh", "233X153" );
        soapMessage.addAttachmentPart( soapAttachPart5 );

        AttachmentPart soapAttachPart3 = soapMessage.createAttachmentPart();
        java.io.File   file3           = new java.io.File("./saaj/client/util/DSC00789.JPG");
        //java.awt.Image image3          = javax.imageio.ImageIO.read(file3);
        FileInputStream fis3           = new FileInputStream( file3 );
        soapAttachPart3.setContent  ( fis3, "image/jpeg" );
        soapAttachPart3.setContentId( "image10" );
        soapAttachPart3.addMimeHeader( "wXh", "2304X1728" );
        soapMessage.addAttachmentPart( soapAttachPart3 );

        return soapMessage;
    }

    private void addSOAPBody( SOAPMessage soapMessage ) throws Exception{
        SOAPBody        soapBody        = soapMessage   .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        Name bodyName           = soapFactory.createName("AttachmentBody13",
                                                         "n13", "http://saaj.ibm.com/n13");
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( "dummySOAPBody13" );
    }

    private void verifyDefect431318_1( SOAPMessage response ) throws Exception {
        Iterator iter = response.getAttachments();
        if( !iter.hasNext() ){
            fail( "Can not find any AttachmentPart in response. This must be Defect 431318.1" );
        } 
    }

}
