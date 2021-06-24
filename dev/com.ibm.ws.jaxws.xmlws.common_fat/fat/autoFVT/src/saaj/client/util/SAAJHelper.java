/*
 * 1.16, 10/31/07
 * Copyright 2006 International Business Machines Corp.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package saaj.client.util;

import java.net.URL;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import junit.framework.TestCase;

import org.apache.axis2.AxisFault;

import com.ibm.ws.wsfvt.test.framework.FVTTestCase;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;


/**
 * SAAJHelper
 * 
 * The utility method common to SAAJ FVT Client
 *
 */

public class SAAJHelper extends com.ibm.ws.saaj.SAAJUtil {

    //static String endpointUrlJaxws = "http://@REPLACE_WITH_HOST_NAME@:@REPLACE_WITH_PORT_NUM@/saajfvt/EchoService";
    //static String endpointUrlSaaj  = "http://@REPLACE_WITH_HOST_NAME@:@REPLACE_WITH_PORT_NUM@/saajfvt/SAAJService";
    //static String endpointUrlSaaj  = "http://@REPLACE_WITH_HOST_NAME@:19080/saajfvt/SAAJService"; // ChangeBack

    static IAppServer server = QueryDefaultNode.defaultAppServer;
    static String endpointUrlJaxws = "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) +
                                     "/saajfvt/EchoService";  
    static String endpointUrlSaaj  = "http://" + server.getMachine().getHostname() +
                                     ":" + server.getPortMap().get(Ports.WC_defaulthost) + // Default port
                                     // ":19080" +                     // For tcpmon
                                     "/saajfvt/SAAJService";
                                     //"/saajfvt/services/SAAJService";

    public static FVTTestCase    testCase = null;
    public static boolean     bExpectException = false;

    public static void setTestCase( FVTTestCase testCase1, boolean bExpecExcep ){
        testCase = testCase1;
        bExpectException = bExpecExcep;
    }

    public static void setTestCase( FVTTestCase testCase1 ){
        setTestCase( testCase1, false );
    }

    //public static SOAPMessage directlySend( SOAPMessage soapMessage ) throws Exception {
    //    return directlySend( soapMessage, endpointUrlSaaj );
    //}

    public static SOAPMessage directlySend( SOAPMessage soapMessage,
                                            Class clsException,
                                            String strID
                                          ) throws Exception {
        try{
            return directlySend( soapMessage, endpointUrlSaaj );
        } catch( Exception e ) {
            if( clsException.isInstance(e ) ){
                String strStack = getStackTraceString( e );
                if( strStack.indexOf( strID ) >= 0){
                    return null;
                }
            }
            throw e;
        }
    }

    public static SOAPMessage directlySend( SOAPMessage soapMessage, 
                                            String strEndPoint ) throws Exception {
        int iReqSAAJVersion = getSaajVersion( soapMessage );

        System.out.println(">> SOAPMessageOut" );
        soapMessage.writeTo( System.out );
        System.out.println( );

        // SOAPConnection
        SOAPConnectionFactory soapConnectionFactory = getSOAPConnectionFactory();
        SOAPConnection        soapConnection        = soapConnectionFactory.createConnection();

        System.out.println( );
        System.out.println(">> Invoking SAAJ ");
        java.net.URL endpoint = new URL( strEndPoint );
        SOAPMessage response = null;
        try{
            response = soapConnection.call( soapMessage, endpoint );
        } catch( Exception e ){
            if( ! bExpectException ){
                e.printStackTrace( System.out );
                debugException( e );
                try{
                    String strExcept = getStackTraceString( e);
                    if( strExcept.indexOf("SAAJ Attachments are not supported yet." ) >= 0 ){
                        testCase.fail( "SAAJ does not support AttachmentPart, yet." );
                    }
                    if( testCase.getClass().getName().indexOf( "Attachment13Test" ) >= 0){
                        if( strExcept.indexOf( "WSWS3192E" ) >=0 ){
                            testCase.fail( "Attachment13Test failed. Very likely, " + 
                                           "the SOAPPart has a old 12 Content-Type." 
                                         );
                        }
                    }
                    if( strExcept.indexOf( "Transport level information does not match " + 
                                           "with SOAP Message namespace URI" ) >= 0){
                        testCase.fail( "A known defect. Have work-warounds for it" );
                    }
                } catch( Exception e1 ){
                    e1.printStackTrace( System.out );
                }
            }

            throw e; // unless expected, otherwise it's an Error in Junit
        } finally{
            soapConnection.close();
        }

        System.out.println(">> Response [" + response + "]");
        response.writeTo( System.out );
        System.out.println( );
        System.out.println( ">> Response end[]" );
        // soapConnection.close();

        // The requesting SOAPMessage must get the same version answer
        int iResSAAJVersion = getSaajVersion( response );
        testCase.assertTrue( "Expect SAAJ"    + iReqSAAJVersion + 
                             " but gets SAAJ" + iResSAAJVersion, 
                             iReqSAAJVersion == iResSAAJVersion );

        verifyErrorMessage( response ); // If reponse an Error Message, then terminate it

        return response;
    }

    // We do not need to look into the SOAPFault all the time.
    static boolean debugSOAPFault = false;
    protected static void debugException( Throwable e ){
        if( !debugSOAPFault ) return;
        Throwable t = e.getCause();
        if( t != null ){
            debugException( t );
            return;
        }
        if( e instanceof AxisFault ){
            AxisFault fault = (AxisFault)e;
            System.out.println( "Get bottom AxisFault " + fault.toString());
            System.out.println( "  reason        " + fault.getReason());
            System.out.println( "  detail        " + fault.getDetail() );
            System.out.println( "  faultCode     " + fault.getFaultCode() );
            System.out.println( "  faultCodeE    " + fault.getFaultCodeElement() );
            System.out.println( "  faultReasonE  " + fault.getFaultReasonElement() );
            System.out.println( "  faultNodeE    " + fault.getFaultNodeElement() );
            System.out.println( "  faultRoleE    " + fault.getFaultRoleElement() );
            System.out.println( "  faultDetailE  " + fault.getFaultDetailElement() );
            System.out.println( "  NodeURI       " + fault.getNodeURI() );
            // System.out.println( "  faultElements " + fault.getFaultElements() );
            System.out.println( "  faultNode     " + fault.getFaultNode() );
            System.out.println( "  faultRole     " + fault.getFaultRole() );
            System.out.println( "  message       " + fault.getMessage() );
        } else {
            System.out.println( "The bottom Exception is " + e.toString() );
        }
    }

    public static void verifyName( Name name, String strMsg, 
                              String strLocalName,
                              String strPrefix, // StrPrefix usually is not necessary
                              String strUri ) throws Exception {

        if( strLocalName == null ) strLocalName = "";
        if( strUri       == null ) strUri       = "";

        String strLName = name.getLocalName(); 
        if( strLName == null ) strLName = "";

        testCase.assertTrue( strMsg + " localname is supposed to be '" +
                    strLocalName + "' but it's '" +
                    strLName + "'",
                    strLocalName.equals( strLName ) );

        // Prefix does not matter as long as it get the right URI

        String strU = name.getURI();
        if( strU == null ) strU = "";
        testCase.assertTrue( strMsg + " URI is '" +
                    strUri + "' but it's '" +
                    strU + "'",
                    strUri.equals( strU ) );
    }

    public static void verifyName( Name name, String strMsg, 
                              String strLocalName,
                              String strPrefix, // StrPrefix usually is not necessary
                              String strUri,
                              String strUri1
                              ) throws Exception {

        if( strLocalName == null ) strLocalName = "";
        if( strUri       == null ) strUri       = "";
        if( strUri1      == null ) strUri1      = "";

        String strLName = name.getLocalName(); 
        if( strLName == null ) strLName = "";

        testCase.assertTrue( strMsg + " localname is supposed to be '" +
                    strLocalName + "' but it's '" +
                    strLName + "'",
                    strLocalName.equals( strLName ) );

        // Prefix does not matter as long as it get the right URI

        String strU = name.getURI();
        if( strU == null ) strU = "";
        testCase.assertTrue( strMsg + " URI is '" +
                    strUri + "' or '" + strUri1 + "' but it's '" +
                    strU + "'",
                    ( strUri .equals( strU ) || 
                      strUri1.equals( strU )  )
                    );
    }

    public static MessageFactory getMessageFactory12() throws Exception{
        return MessageFactory.newInstance(); 
    }

    public static MessageFactory getMessageFactory13() throws Exception{
        return MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
    }

    public static SOAPConnectionFactory getSOAPConnectionFactory() throws Exception{
        return SOAPConnectionFactory.newInstance();
    }


    public static SOAPFactory getSOAPFactory12() throws Exception {
        return SOAPFactory.newInstance();
    }

    public static SOAPFactory getSOAPFactory13() throws Exception {
        return SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
    }

    static SocketUtil socketUtil = null;

    public static SOAPMessage socketSend( SOAPMessage soapMessage ) throws Exception {
        if( socketUtil == null ) socketUtil = new SocketUtil();

        System.out.println(">> SOAPMessageOut" );
        soapMessage.writeTo( System.out );
        System.out.println( );

        int iReqSAAJVersion = getSaajVersion( soapMessage );
        String strSoapMessage = socketUtil.sendSOAPMessage( soapMessage );
        SOAPMessage response = getSOAPMessage( strSoapMessage );

        // The requesting SOAPMessage must get the same version answer
        int iResSAAJVersion = getSaajVersion( response );
        testCase.assertTrue( "Expect SAAJ"    + iReqSAAJVersion + 
                             " but gets SAAJ" + iResSAAJVersion, 
                             iReqSAAJVersion == iResSAAJVersion );

        verifyErrorMessage( response ); // If reponse an Error Message, then terminate it
        return response;
    }

    public static void verifyErrorMessage( SOAPMessage response ) throws Exception{
        SOAPBodyElement soapBodyElement = getBodyFirstChild( response );
        if( soapBodyElement != null ){
            Name name = soapBodyElement.getElementName();
            String strLocalName = name.getLocalName();
            if( strLocalName.equals( "answer_error" ) ){
                String strErrorValue = soapBodyElement.getValue();
                testCase.fail( "ServerAnswerError:" + strErrorValue );
            }
        }
    }

}

