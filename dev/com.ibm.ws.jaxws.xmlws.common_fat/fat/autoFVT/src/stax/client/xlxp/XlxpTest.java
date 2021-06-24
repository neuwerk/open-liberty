package stax.client.xlxp;

import javax.xml.namespace.QName;
import junit.framework.TestCase;

import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
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
import saaj.client.util.SOAPMessage12Helper;

import java.util.Iterator;
import org.w3c.dom.Document;

import javax.xml.stream.XMLInputFactory;

/**
  *  Test if StAX is calling IBM XLXP Parser
  *  The corresponding in the Server to respond is 
  *   WautoFVT/src/saaj/server
  *     /com.ibm.wsfp.jaxws_1.0.0.jar/com/ibm/ws/
  *     websvcs/jaxws/server/response/XlxpResponse.java
  * Please also see 
  *   WautoFVT/src/saaj/saaj_readme.ppt
  *
  */
public class XlxpTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
    
    public XlxpTest(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
        super.setUp();
        SAAJHelper.setTestCase( this );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testXlxp() throws Exception {
        System.out.println( );
        System.out.println("------------testXlxp() --------");

        Class cls = Class.forName( "javax.xml.stream.XMLInputFactory" );
        Class[] clses = new Class[]{};
        java.lang.reflect.Method method = cls.getMethod( "newInstance", clses );
        /* Let's see if it's IBM XLXP parser */
        Object factory = method.invoke( null, new Object[]{} ); 
        ///* Test the XML Input factory*/
        //XMLInputFactory factory = XMLInputFactory.newInstance(); 
        String strClsName = factory.getClass().getName();
        System.out.println( "class name is " + strClsName );
        if(  strClsName.indexOf("com.ibm.xml") < 0 ) {
            assertTrue( "expect the IBM xlxp XML parser but get '" +
                        strClsName + "'", false );
        }

        SOAPMessage soapMessage = createRequestMsg();
        // SOAPMessage response = SAAJHelper.socketSend( soapMessage  ); // go through Socket
        SOAPMessage response = SOAPMessage12Helper.send( soapMessage  ); // SOAPConnection

        verifyXlxpMessage( response );
    }

    private SOAPMessage createRequestMsg() throws Exception{
        MessageFactory  messageFactory  = SAAJHelper.getMessageFactory12(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // To create SOAP Element instances
        SOAPFactory soapFactory = SOAPFactory.newInstance();
        Name bodyName           = soapFactory.createName("xlxp", "s11", "http://xlxp.ibm.com/xlxp" );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( "xlxpClientParser is IBM Parser" );

        //SOAPHeader      soapHeader  = soapMessage .getSOAPHeader();
        //soapHeader.detachNode();   // Remove Header from SOAPMessage

        return soapMessage; 
    }

    private void verifyXlxpMessage( SOAPMessage response ) throws Exception{
        SOAPBodyElement soapBodyElem = SAAJHelper.getBodyFirstChild( response );
        Name name = soapBodyElem.getElementName();
        String strLocalName = name.getLocalName();
        assertTrue( "localName expects 'xlxp' but get '" +
                    strLocalName + "'",
                    strLocalName.equals( "xlxp" ) );
    }

}
