// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006, 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// Refer to WautoFVT/src/wasintegration/jaxwsrpc/
// 06/05/07 jramos      440922          Integrate ACUTE
// 08/30/10 btiffany    665075.1.FVT    Update for changed message.
// 06/27/11 btiffany    PM37820.FVT     Added test for pm37820
//
//
// Note: This test needs the app built by the src/saaj/server tests 
package soap.test.trigger;

import java.io.*;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import java.net.URL;

import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import com.ibm.ws.webservices.engine.xmlsoap.PM37820Check;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

import saaj.client.util.SAAJHelper;

public class TriggerTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    // static String endpointUrlJaxws  = "http://@REPLACE_WITH_HOST_NAME@:@REPLACE_WITH_PORT_NUM@/saajfvt/SAAJService";
    // static String endpointUrlJaxws12 = "http://@REPLACE_WITH_HOST_NAME@:@REPLACE_WITH_PORT_NUM@/saajfvt/SAAJService1";
    // static String endpointUrlJaxws  = "http://@REPLACE_WITH_HOST_NAME@:19080/saajfvt/SAAJService"; // ChangeBack

    static IAppServer server = QueryDefaultNode.defaultAppServer;
    static String endpointUrlJaxws   = "http://" + server.getMachine().getHostname() + 
                                       ":" + server.getPortMap().get(Ports.WC_defaulthost) + 
                                       //":19080" +
                                       "/saajfvt/SAAJService";
    static String endpointUrlJaxws12   = "http://" + server.getMachine().getHostname() + 
                                       ":" + server.getPortMap().get(Ports.WC_defaulthost) + 
                                       //":19080" +
                                       "/saajfvt/SAAJService1";
    //static String endpointUrlJaxws = "http://" + server.getHostname() + ":19080" + 
    //                                 "/saajfvt/SAAJService"; 

    String xmlString = "#test input#";
    boolean bReviewMinor = SAAJHelper.reviewMinor();
    //boolean bReviewMinor = true;

    public TriggerTest(String name) {
        super(name);
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJaxwsTrigger11() throws Exception {
        System.out.println("" );
        System.out.println("---------testJaxwsTrigger11------------------------------");
        String          strNV           = SOAPConstants.SOAP_1_1_PROTOCOL;
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(strNV); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        // To create SOAP Element instances
        SOAPFactory     soapFactory     = SOAPFactory.newInstance( strNV );
        //Name            bodyName        = soapFactory.createName("invoke", "ns1", "http://ws.apache.org/saaj" );   
        //SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        //SOAPElement     soapElement     = soapBodyElement.addChildElement( "value"); 
        //soapElement.addTextNode( xmlString );
        Name            bodyName        = soapFactory.createName("invoke", "s11", "http://ws.apache.org/saaj" );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( xmlString );

        // SOAPConnection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection        soapConnection        = soapConnectionFactory.createConnection();
        System.out.println( ">> Sending SOAPMessage:" );
        soapMessage.writeTo( System.out );
        System.out.println( );

        System.out.println(">> Invoking Jaxws SimpleProvider from SOAPConnection to " + endpointUrlJaxws );
        java.net.URL endpoint = new URL( endpointUrlJaxws );
        SOAPMessage response = soapConnection.call( soapMessage, endpoint );

        System.out.println(">> Response [" + response + "]");
        response.writeTo( System.out );
        System.out.println( );
        soapConnection.close();
        String strMsg = getSMString( response );
        assertTrue( "Get a wrong SOAP version answer",
                   strMsg.indexOf( SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE ) >= 0 );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJaxwsTrigger12() throws Exception {
        System.out.println("" );
        System.out.println("---------testJaxwsTrigger12------------------------------");
        String          strNV           = SOAPConstants.SOAP_1_2_PROTOCOL;
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(strNV); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        // To create SOAP Element instances
        SOAPFactory     soapFactory     = SOAPFactory.newInstance( strNV );
        Name            bodyName        = soapFactory.createName("invoke", "s12", "http://ws.apache.org/saaj"  );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( xmlString );

        // SOAPConnection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection        soapConnection        = soapConnectionFactory.createConnection();
        System.out.println( ">> Sending SOAPMessage:" );
        soapMessage.writeTo( System.out );
        System.out.println( );

        System.out.println(">> Invoking Jaxws SimpleProvider from SOAPConnection to " + endpointUrlJaxws12 );
        java.net.URL endpoint = new URL( endpointUrlJaxws12 );
        SOAPMessage response = soapConnection.call( soapMessage, endpoint );

        System.out.println(">> Response [" + response + "]");
        response.writeTo( System.out );
        System.out.println( );
        soapConnection.close();
        String strMsg = getSMString( response );
        assertTrue( "Get a wrong SOAP version answer",
                   strMsg.indexOf( SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE ) >= 0 );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJaxwsTriggerError11() throws Exception {
        System.out.println("" );
        System.out.println("---------testJaxwsTriggerError11------------------------------");
        String          strNV           = SOAPConstants.SOAP_1_2_PROTOCOL;
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(strNV); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        // To create SOAP Element instances
        SOAPFactory     soapFactory     = SOAPFactory.newInstance( strNV );
        Name            bodyName        = soapFactory.createName("invoke", "s11", "http://ws.apache.org/saaj"  );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( xmlString );

        // SOAPConnection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection        soapConnection        = soapConnectionFactory.createConnection();
        System.out.println( ">> Sending SOAPMessage:" );
        soapMessage.writeTo( System.out );
        System.out.println( );

        System.out.println(">> Invoking Jaxws SimpleProvider from SOAPConnection to " + endpointUrlJaxws );
        java.net.URL endpoint = new URL( endpointUrlJaxws );
        SOAPMessage response = null;
        try{
            response = soapConnection.call( soapMessage, endpoint );
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return;
        } finally{
            soapConnection.close();
        }
        System.out.println(">> Response [" + response + "]");
        response.writeTo( System.out );
        System.out.println( );
        if( bReviewMinor ){
            fail( "Expecting Exception because the SOAP version is different but did not get it" );
        } else {
            System.out.println( "reviewMinor is false" );
            String strMsg = getSMString( response );
            System.out.println("expected response to contain \"VersionMismatch\"");
            System.out.println("actual rseponse: " + strMsg);
            assertTrue( "Get a wrong SOAP version answer",
                       strMsg.indexOf( "VersionMismatch" ) >= 0 );
           
        }
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJaxwsTriggerError12() throws Exception {
        System.out.println("" );
        System.out.println("---------testJaxwsTriggererror12------------------------------");
        String          strNV           = SOAPConstants.SOAP_1_1_PROTOCOL;
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(strNV); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        // To create SOAP Element instances
        SOAPFactory     soapFactory     = SOAPFactory.newInstance( strNV );
        // Name            bodyName        = soapFactory.createName("invoke", "ns1", "http://ws.apache.org/saaj" );   
        // SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        // SOAPElement     soapElement     = soapBodyElement.addChildElement( "value"); 
        // soapElement.addTextNode( xmlString );
        Name            bodyName        = soapFactory.createName("invoke", "s12", "http://ws.apache.org/saaj"  );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( xmlString );

        // SOAPConnection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection        soapConnection        = soapConnectionFactory.createConnection();
        System.out.println( ">> Sending SOAPMessage:" );
        soapMessage.writeTo( System.out );
        System.out.println( );

        System.out.println(">> Invoking Jaxws SimpleProvider from SOAPConnection to " + endpointUrlJaxws12 );
        java.net.URL endpoint = new URL( endpointUrlJaxws12 );
        SOAPMessage response = null;
        try{
            response = soapConnection.call( soapMessage, endpoint );
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return;
        } finally{
            soapConnection.close();
        }
        System.out.println(">> Response [" + response + "]");
        response.writeTo( System.out );
        System.out.println( );
        if( bReviewMinor ){
            fail( "Expecting Exception because the SOAP version is different but did not get it" );
        } else {
            System.out.println( "reviewMinor is false" );
            String strMsg = getSMString( response );
            assertTrue( "Get a wrong SOAP version answer",
                       strMsg.indexOf( SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE ) >= 0 );
        }
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJaxwsTriggerExtra11() throws Exception {
        System.out.println("" );
        System.out.println("---------testJaxwsTriggerExtra11------------------------------");
        String          strNV           = SOAPConstants.SOAP_1_1_PROTOCOL;
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(strNV); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        // To create SOAP Element instances
        SOAPFactory     soapFactory     = SOAPFactory.newInstance( strNV );
        Name            bodyName        = soapFactory.createName("invoke", "s11", "http://ws.apache.org/saaj"  );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( xmlString );

        // Creating extra parameter
        Name            bodyName1        = soapFactory.createName("extra", "s11", "http://ws.apache.org/saaj"  );
        SOAPBodyElement soapBodyElement1 = soapBody.addBodyElement( bodyName1 );
        soapBodyElement1.addTextNode( "This is an extra parameter" );

        // SOAPConnection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection        soapConnection        = soapConnectionFactory.createConnection();
        System.out.println( ">> Sending SOAPMessage:" );
        soapMessage.writeTo( System.out );
        System.out.println( );

        System.out.println(">> Invoking Jaxws SimpleProvider from SOAPConnection to " + endpointUrlJaxws );
        java.net.URL endpoint = new URL( endpointUrlJaxws );
        SOAPMessage response = null;
        try{
            response = soapConnection.call( soapMessage, endpoint );
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return;
        } finally{
            soapConnection.close();
        }
        System.out.println(">> Response [" + response + "]");
        response.writeTo( System.out );
        System.out.println( );
        if( bReviewMinor ){
            fail( "Expecting Exception because this has an extra prameter but did not get it" );
        } else {
            System.out.println( "reviewMinor is false" );
            String strMsg = getSMString( response );
            assertTrue( "Get a wrong SOAP version answer",
                       strMsg.indexOf( SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE ) >= 0 );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJaxwsTriggerExtra12() throws Exception {
        System.out.println("" );
        System.out.println("---------testJaxwsTriggerExtra12------------------------------");
        String          strNV           = SOAPConstants.SOAP_1_2_PROTOCOL;
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(strNV); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        // To create SOAP Element instances
        SOAPFactory     soapFactory     = SOAPFactory.newInstance( strNV );
        Name            bodyName        = soapFactory.createName("invoke", "s12", "http://ws.apache.org/saaj"  );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( xmlString );
        // Creating extra parameter
        Name            bodyName1        = soapFactory.createName("extra", "s12", "http://ws.apache.org/saaj"  );
        SOAPBodyElement soapBodyElement1 = soapBody.addBodyElement( bodyName1 );
        soapBodyElement1.addTextNode( "This is an extra parameter" );

        // SOAPConnection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection        soapConnection        = soapConnectionFactory.createConnection();
        System.out.println( ">> Sending SOAPMessage:" );
        soapMessage.writeTo( System.out );
        System.out.println( );

        System.out.println(">> Invoking Jaxws SimpleProvider from SOAPConnection to " + endpointUrlJaxws12 );
        java.net.URL endpoint = new URL( endpointUrlJaxws12 );
        SOAPMessage response = null;
        try{
            response = soapConnection.call( soapMessage, endpoint );
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return;
        } finally{
            soapConnection.close();
        }
        System.out.println(">> Response [" + response + "]");
        response.writeTo( System.out );
        System.out.println( );
        if( bReviewMinor ){
            fail( "Expecting Exception because this has an extra prameter but did not get it" );
        } else {
            System.out.println( "reviewMinor is false" );
            String strMsg = getSMString( response );
            assertTrue( "Get a wrong SOAP version answer",
                       strMsg.indexOf( SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE ) >= 0 );
        }
    }


    public String getSMString( SOAPMessage soapMessage ) throws IOException{
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            soapMessage.writeTo( baos );
            baos.flush();
            byte[] bytesMessage = baos.toByteArray();
            baos.close();
            return new String( bytesMessage );
        } catch( Exception e ){
            e.printStackTrace(System.out);
        }

        return "ERROR: Can not create a String ";
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJaxwsTrigger3() throws Exception {
        System.out.println("" );
        System.out.println("---------testJaxwsTrigger3(SOAP11)------------------------------");
        JaxwsClient client = new JaxwsClient();
        String strResponse = client.callJaxwsStringProvider();
        System.out.println( "Get back response as '"  + strResponse + "'"); 
        assertTrue( "Did not getback the expected String", 
                    strResponse.indexOf( "request processed SAAJ" ) >= 0 );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJaxwsTrigger4() throws Exception {
        System.out.println("" );
        System.out.println("---------testJaxwsTrigger4(SOAP12)------------------------------");
        JaxwsClient client = new JaxwsClient();
        String strResponse = client.callJaxwsString12Provider();
        System.out.println( "Get back response as '"  + strResponse + "'"); 
        assertTrue( "Did not getback the expected String", 
                    strResponse.indexOf( "request 12 processed SAAJ" ) >= 0 );
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJaxwsTriggerNoPrefix121() throws Exception {
        System.out.println("" );
        System.out.println("---------testJaxwsTriggerNoPrefix121-----(for defect 412984)-------------------------");
        // Defect 412984 is having trouble when the first BODYElement(the first child of SOAPBody)
        // has no prefix(NameSpace qualified). It had troubles in both client to server and server to client
        String          strNV           = SOAPConstants.SOAP_1_2_PROTOCOL;
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(strNV); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        // To create SOAP Element instances
        SOAPFactory     soapFactory     = SOAPFactory.newInstance( strNV );
        Name            bodyName        = soapFactory.createName("invoke", "s12", "http://s12.ibm.com/soap" );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( "This is a test expect the first BODYElement of response to have NoPrefix" );

        // SOAPConnection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection        soapConnection        = soapConnectionFactory.createConnection();
        System.out.println( ">> Sending SOAPMessage:" );
        soapMessage.writeTo( System.out );
        System.out.println( );

        System.out.println(">> Invoking Jaxws SimpleProvider from SOAPConnection to " + endpointUrlJaxws12 );
        java.net.URL endpoint = new URL( endpointUrlJaxws12 );
        SOAPMessage response = null;
        try{
            response = soapConnection.call( soapMessage, endpoint );
        } catch( Exception e ){
            e.printStackTrace(System.out);
            fail( "Hit Exception unexpectedly:'" + e.getMessage() + "'");
        } finally{
            soapConnection.close();
        }
        System.out.println(">> Response [" + response + "]");
        response.writeTo( System.out );
        System.out.println( );
        String strMsg = getSMString( response );
        assertTrue( "Expect to get a 'no prefix' but does not",
                       strMsg.indexOf( "NoPrefix" ) >= 0 );
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJaxwsTriggerNoPrefix12() throws Exception {
        System.out.println("" );
        System.out.println("---------testJaxwsTriggerNoPrefix12-----(for defect 412984)-------------------------");
        // Defect 412984 is having trouble when the first BODYElement(the first child of SOAPBody)
        // has no prefix(NameSpace qualified). It had troubles in both client to server and server to client
        String          strNV           = SOAPConstants.SOAP_1_2_PROTOCOL;
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(strNV); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        // To create SOAP Element instances
        SOAPFactory     soapFactory     = SOAPFactory.newInstance( strNV );
        Name            bodyName        = soapFactory.createName("invoke" );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( "This is a test for the first BODYElement to have NoPrefix" +
                                     " and expect the same style response" );

        // SOAPConnection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection        soapConnection        = soapConnectionFactory.createConnection();
        System.out.println( ">> Sending SOAPMessage:" );
        soapMessage.writeTo( System.out );
        System.out.println( );

        System.out.println(">> Invoking Jaxws SimpleProvider from SOAPConnection to " + endpointUrlJaxws12 );
        java.net.URL endpoint = new URL( endpointUrlJaxws12 );
        SOAPMessage response = null;
        try{
            response = soapConnection.call( soapMessage, endpoint );
        } catch( Exception e ){
            e.printStackTrace(System.out);
            fail( "Hit Exception unexpectedly:'" + e.getMessage() + "'");
        } finally{
            soapConnection.close();
        }
        System.out.println(">> Response [" + response + "]");
        response.writeTo( System.out );
        System.out.println( );
        String strMsg = getSMString( response );
        assertTrue( "Expect to get a 'no prefix' but does not",
                       strMsg.indexOf( "NoPrefix" ) >= 0 );
    }
    
    /**
       PM19534 introduced a regression whereby elements being copied
       from one document to another could inadverdently be deleted from
       the source document.  That was fixed by pm37820.  
       This tests verifies we don't regress that.  
       
       It performs the element
       copy that used to cause the erroneous deletion and checks that it does not 
       occur. 
     **/
    public void testPM7820_since_fixpack19_Element_Deletion() throws Exception{
       assertTrue("pm37820Test failed, see Systemout", PM37820Check.check());
    }
    


}
