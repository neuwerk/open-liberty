/*
 * Copyright 2006 International Business Machines Corp.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ibm.ws.saaj;

import java.io.*;
import java.util.Locale;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

// SAAJ SOAPMessage related
//import javax.xml.soap.SAAJMetaFactory;
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

import java.net.URL;

import org.apache.axis2.AxisFault;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;

import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;

import javax.xml.namespace.QName;

/**
 * This handle SAAJ1.2/SOAP1.1 SOAPFault
 *
 */

public class FaultData13 extends SAAJUtil{
    final static public String ENVPREFIX = SOAPConstants.SOAP_ENV_PREFIX;
    final static public String CLIENT    = "Client";          //SOAPConstants.SOAP_SENDER_FAULT;
    final static public String SERVER    = "Server";          //SOAPConstants.SOAP_RECEIVER_FAULT;
    final static public String MUST      = "MustUnderstand";  //SOAPConstants.SOAP_MUSTUNDERSTAND_FAULT;
    final static public String VERSION   = "VersionMismatch"; //SOAPConstants.SOAP_VERSIONMISMATCH_FAULT;
    final static public String ENCODING  = "DataEncodingUnknown"; //SOAPConstants.SOAP_DATAENCODINGUNKNOWN_FAULT;

    final static private String STRFAULT13 = "Fault13_";

    String faultCode    = "Client"; // faultcode env:Code
    String faultActor   = null;   // Role
    String faultReason  = "MustHaveAFaultString";  // FaultString
    String faultLocale  = "Big5";
    Detail faultDetail  = null;
    int    iCaseID      = -1;

    String faultSubCodeLocalPart = "";
    String faultSubCodeURI       = "";

    String strNode6 = "http://faultnode6.ibm.com/newnode6";
    // Initialize Data in client 
    public FaultData13( String code,  // Client, Server, Mustunderstand
                        String actor, 
                        String fault, 
                        String locale,
                        Detail detail ){
        setCode  ( code   );
        setActor ( actor  );
        setFault ( fault, locale );
        setDetail( detail );
    }

    // Initialize Data through the requesting SOAPMessage 
    public FaultData13( SOAPMessage soapMessage ) throws Exception{
        readSoapMessage( soapMessage );
    }


    public void setCode( String code ){
        if( code != null ){
            faultCode = code;
        }
    }

    public void setActor( String actor ){
        if( actor != null ){
            faultActor = actor;
        }
    }

    public void setFault( String fault, String locale ){
        if( fault != null ){
            faultReason = fault;
        }
        if( locale != null ){
            faultLocale = locale;
        }
    }

    public void setDetail( Detail detail ){ 
        if( detail != null ){
            faultDetail = detail;
        }
    }

    // This creates a SOAPMessage which load the data in the SOAPMessage
    public SOAPMessage createSimpleMessage( int iCase) throws Exception{
        iCaseID                          = iCase;
        MessageFactory  messageFactory   = getMessageFactory(); 
        SOAPFactory     soapFactory      = getSOAPFactory();
        String          strCaseID        = STRFAULT13 + iCase;

        SOAPMessage     soapMessage      = messageFactory.createMessage();

        Name            headerName       = soapFactory.createName(strCaseID, "Fvt", 
                                                         "http://www.austin.texas.com/ibm" );
        SOAPHeader      soapHeader       = soapMessage .getSOAPHeader();
        SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
        soapHeaderElem.setActor( strCaseID );

        SOAPBody        soapBody         = soapMessage .getSOAPBody();

        Name            bodyName         = soapFactory.createName( "Fault13", "f13", 
                                                                   "http://wsfp.austin.ibm.com/f13ns" );
        SOAPBodyElement soapBodyElement  = soapBody.addBodyElement( bodyName );

        // add faultCode 
        Name            codeName         = soapFactory.createName("Code" );
        SOAPElement     codeElement      = soapBodyElement.addChildElement( codeName );
        codeElement.addTextNode( faultCode );

        // add fault String
        if( faultReason != null ){
            Name        stringName       = soapFactory.createName("FaultReason" );
            SOAPElement stringElement    = soapBodyElement.addChildElement( stringName );
            if( faultLocale != null ){
                Name        localeName   = soapFactory.createName("locale" );
                stringElement.addAttribute( localeName, faultLocale );
            }
            stringElement.addTextNode( faultReason );
        }

        // add Actor
        if( faultActor != null ){
            Name        stringName      = soapFactory.createName("Actor" );
            SOAPElement stringElement   = soapBodyElement.addChildElement( stringName );
            stringElement.addTextNode( faultActor );
        }

        // Add Detail
        if( faultDetail != null ){
            SOAPElement detailElement   = soapBodyElement.addChildElement( (SOAPElement)faultDetail );
        }

        if( iCase == 5  ) { 
            faultSubCodeLocalPart = "subCode5";
            faultSubCodeURI       = "http://site56.austin.ibm.com/nd56";
        } else if( iCase == 6 ) {                //c004
                faultSubCodeLocalPart = "newCode6";
                faultSubCodeURI       = "http://new56.austin.ibm.com/new56";
        }
        System.out.println( "FaultSubCode " + faultSubCodeURI + " " + faultSubCodeLocalPart );

        System.out.println( "Created a SOAPMessage as:" );
        soapMessage.writeTo( System.out );
        System.out.println( );
        return soapMessage; 
    }

    boolean oddCase( int iCase ){
        if( iCase > 7 ) return true;
        return ( iCase & 1 ) == 1 ;
    }

    // This creates a SOAPMessage which load the data in the SOAPMessage
    public SOAPMessage createFaultMessage( ) throws Exception{
        MessageFactory  messageFactory  = getMessageFactory(); 
        SOAPFactory     soapFactory     = getSOAPFactory();

        SOAPMessage     soapMessage     = messageFactory.createMessage();
        createFaultHeader( soapMessage ); // iCaseID 

        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // create a SOAPFault
        QName           nameCode        = getFaultCodeQName(faultCode );

        // handle faultCode, faultReason and faultLocale

        SOAPFault       soapFault       = null;
        if( faultLocale != null ){
            Locale locale = Locale.ENGLISH;
            if( faultLocale.equalsIgnoreCase( "Big5" ) ){
                locale = Locale.TAIWAN;
            }
            if( iCaseID == 5  ) { //c004
                soapFault       = soapBody.addFault();
                soapFault.setFaultCode( nameCode );
                soapFault.addFaultReasonText( faultReason, locale );
                QName qSubcode = new QName( "http://site56.austin.ibm.com/nd56",
                                            "subCode5",
                                            "s56"
                                          );
                soapFault.appendFaultSubcode( qSubcode );
                faultSubCodeLocalPart = "subCode5";
                faultSubCodeURI       = "http://site56.austin.ibm.com/nd56";
            } else if( iCaseID == 6 ) {                //c004
                    soapFault       = soapBody.addFault();
                    soapFault.setFaultCode( nameCode );
                    soapFault.addFaultReasonText( faultReason, locale );

                    soapFault.addNamespaceDeclaration( "new56", "http://new56.austin.ibm.com/new56" );
                    QName qSubcode = soapFault.createQName( "oldCode6", "new56" );
                    soapFault.appendFaultSubcode( qSubcode );

                    // remove all the subcode
                    soapFault.removeAllFaultSubcodes();

                    qSubcode        = soapFault.createQName( "newCode6", "new56" );
                    soapFault.appendFaultSubcode( qSubcode );

                    QName qSubcodeN = new QName( "http://new56.austin.ibm.com/new561",
                                                 "newCode61",
                                                 "new561"
                                               );
                    soapFault.appendFaultSubcode( qSubcodeN );

                    faultSubCodeLocalPart = "newCode6";
                    faultSubCodeURI       = "http://new56.austin.ibm.com/new56";
            } else {
                soapFault       = soapBody.addFault( nameCode, 
                                                     faultReason,
                                                     locale );
            }
        } else if( faultReason != null ){
            soapFault       = soapBody.addFault( nameCode, 
                                                 faultReason );
        } else {
            soapFault       = soapBody.addFault();
            soapFault.setFaultCode( nameCode );
        }


        if( faultActor != null ){
            if( oddCase(iCaseID) ){
                soapFault.setFaultActor( faultActor );
            } else {
                soapFault.setFaultRole( faultActor );
            }
        }

        if( iCaseID == 6 ) {                //c004
            soapFault.setFaultNode( strNode6 );
        }

        if( iCaseID < 0 ){
            // Something wron, adk user to see the SystemOut.log
            // Replace the FaultDetail
            faultDetail = null; // remove the normal FaultDetail
            addDetailEntry( "error", "errPrefix", "http://wsfvt.ibm.com/err",
                            "Please see SystemOut.log to see the error message ID=" + iCaseID );
        } 

        if( faultDetail != null ){ // in case error, the faultDetail had been replaced
            Detail detail = soapFault.addDetail(); // create a Detail first
            addDetail( soapMessage );
        }

        System.out.println( "Created a Fault SOAPMessage as:" );
        soapMessage.writeTo( System.out );
        System.out.println( );
        return soapMessage; 
    }


    protected void createFaultHeader( SOAPMessage soapMessage ) throws SOAPException {
        //
        // According to the iCaseID to create a SOAPHeaderElement c001
        SOAPHeader  soapHeader  = soapMessage.getSOAPHeader();

        if( faultReason.equals( MUST ) ){    //addNotUnderstoodHeaderElement 
            QName qName = new QName( "http://webservices.ibm.com/NSTransation", "transaction", "uprefix" );
            soapHeader.addNotUnderstoodHeaderElement( qName );
        } else if( faultReason.equals( VERSION )){  // 
            String   str1  = SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE;
            String[] aStr2 = new String[]{
                                SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, // Most preferred
                                SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE  // less preferred
                             };
            if( iCaseID == 4){
                Vector<String> vec  = new Vector<String>( 4 );
                for( int iI = 0; iI < aStr2.length; iI ++ ){
                    vec.add( aStr2[ iI ] );
                }
                Iterator<String> iter = vec.iterator();
                soapHeader.addUpgradeHeaderElement( iter );
            } else if( iCaseID == 5){
                soapHeader.addUpgradeHeaderElement( str1 );
            } else if( iCaseID == 6){
                soapHeader.addUpgradeHeaderElement( aStr2 );
            }
        }
        // return soapMessage;
    }

    static public Detail createDetail() throws Exception{
        MessageFactory  messageFactory  = getMessageFactory(); 
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        SOAPFault       soapfault       = soapBody.addFault();
        Detail          detail          = soapfault.addDetail();
        return detail; 
    }

    public void addDetail( SOAPMessage soapMessage ) throws Exception{
        SOAPBody        soapBody        = soapMessage.getSOAPBody();
        SOAPFault       soapfault       = soapBody.getFault();
        Detail          detail          = soapfault.getDetail();

        Iterator<DetailEntry> iterator  = faultDetail.getDetailEntries();
        while( iterator.hasNext() ){
            DetailEntry detailEntry = (DetailEntry)iterator.next();
            detail.addChildElement( detailEntry ); // This supposed to add the DetailEntry
        }
    }

    public void addDetailEntry( String localName, 
                                String prefix, 
                                String strURI, 
                                String strValue) throws Exception{

        Name nameDetail = createName( localName, prefix, strURI );
        addDetailEntry( nameDetail, strValue );
    }

    public void addDetailEntry( String localName, 
                                String strValue) throws Exception{
        Name nameDetail = createName( localName );
        addDetailEntry( nameDetail, strValue );
    }

    public void addDetailEntry( Name name, 
                                String strValue) throws Exception{
        if( faultDetail == null ){
            faultDetail = createDetail();
        }
        DetailEntry detailEntry = faultDetail.addDetailEntry( name );
        detailEntry.addTextNode( strValue );
    }

    public void readSoapMessage( SOAPMessage soapMessage ) throws Exception{
        SOAPHeader      soapHeader      = soapMessage.getSOAPHeader();
        Iterator<Node>  iterator0       = soapHeader.getChildElements();
        SOAPElement     soapElementH    = (SOAPElement)iterator0.next();
        QName           qName           = soapElementH.getElementQName(); // 1.3 
        String          strFault        = qName.getLocalPart(); //
        String          strFaultActor   = ((SOAPHeaderElement)soapElementH).getActor();
        if( strFault.indexOf( STRFAULT13 ) == 0 ){
            String strCaseID = strFault.substring( STRFAULT13.length() );
            iCaseID = Integer.parseInt( strCaseID );
            if( iCaseID >= 0 && iCaseID <= 7 ){
                if( !strFaultActor.equals( strFault ) ){ // strFault = strFaultActor from cases 1 to 7
                    iCaseID = -2;
                    System.out.println( "ERROR: SOAPHeaderElement Actor ought to be the same as the localname (-2)" );
                }
            }
        }  else {
            iCaseID = -1;
            System.out.println( "ERROR: (FaultData13) Did not read in the FaultMessage with " + 
                                STRFAULT13 + " but read " + strFault + "(-1)" );
        }

        //System.out.println( "Debugging on QName" );
        //soapMessage.writeTo( System.out );
        //System.out.println(  );

        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        QName           nameFault       = new QName( "http://wsfp.austin.ibm.com/f13ns", "Fault13", "f13" );
        Iterator<Node>  iterator1       = soapBody.getChildElements(nameFault);
        SOAPElement     soapElement     = (SOAPElement)iterator1.next();

        Iterator<Node>  iterator        = soapElement.getChildElements();
        while( iterator.hasNext() ){
            Node node = (Node)iterator.next();
            if( node instanceof SOAPElement ){
                SOAPElement childElement = (SOAPElement)node;
                Name        nameA        = childElement.getElementName();
                String      strName      = nameA.getLocalName();
                if( strName.equals( "Code"             ) ){
                    readCode( childElement );
                } else if( strName.equals( "FaultReason" ) ){
                    readFault( childElement );
                } else if( strName.equalsIgnoreCase( "Detail"      ) ){
                    readDetail( childElement );
                } else if( strName.equals( "Actor"      ) ){
                    readActor( childElement );
                } else {
                    System.out.println( "ERROR: Unknown childElement in FaultData13 '" + nameA + "'");
                }
            } else {
                System.out.println( "ERROR: Unknown node in FaultData13" + node );
            }
        }
        System.out.println( "Read in " + toString() );
    }


    protected void readCode( SOAPElement childElement ){
        String strCode   = childElement.getValue(); // code
        int    iColon    = strCode.indexOf( ":" ); // -1
        faultCode        = strCode.substring( iColon + 1 ); // in case -1 + 1 = 0
    }

    protected void readFault( SOAPElement childElement ) throws Exception{
        faultReason     = childElement.getValue(); // faultReason
        Name localeName = createName( "locale" );
        faultLocale     = childElement.getAttributeValue( localeName );
    }

    protected void readDetail( SOAPElement childElement ) throws Exception{
        Iterator<SOAPElement> iterator  = childElement.getChildElements();
        while( iterator.hasNext() ){
            SOAPElement detailEntry = (SOAPElement)iterator.next();
            addDetailEntry( detailEntry.getElementName(),
                            detailEntry.getValue()
                          ); 
        }
    }

    protected void readActor( SOAPElement childElement ){
        faultActor     = childElement.getValue(); // faultReason
    }

    static public MessageFactory getMessageFactory() throws Exception{
        //SAAJMetaFactory saajMeta  = SAAJMetaFactory.getInstance();
        //MessageFactory msgFactory = saajMeta.newMessageFactory( SOAPConstants.SOAP_1_2_PROTOCOL );
        //return msgFactory;
        return MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
    }

    static SOAPFactory soapFactory = null;
    static public SOAPFactory getSOAPFactory() throws Exception {
        if( soapFactory == null ){
            soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        }
        return soapFactory;
    }

    public Name createName( String strLocalName ) throws Exception{
        SOAPFactory     soapFactory     = getSOAPFactory();
        return soapFactory.createName( strLocalName );
    }

    public Name createName( String strLocalName, String prefix, String strURI ) throws Exception{
        SOAPFactory     soapFactory     = getSOAPFactory();
        return soapFactory.createName( strLocalName, prefix, strURI );
    }

    public boolean isExpectedFault( Throwable e, int iCase ) throws Exception{
        //todo: Verify SOAPHeaderElement if possible
        //System.out.println( "  isExppectedFault : " + e );
        //e.printStackTrace( System.out );

        Throwable t = e.getCause();
        if( t != null ){
            return isExpectedFault( t, iCase );
        }
        e.printStackTrace( System.out );
        if( e instanceof AxisFault ){
            System.out.println( "Compare FaultData13 " + toString() );
            AxisFault fault = (AxisFault)e;
            dumpAxisFault( fault );
            // check faultCode
            String strCode1    = getFaultCode( fault );
            if( strCode1 != null ){
                int       iIndex    = strCode1.indexOf( ":" ); // -1
                // System.out.println( "strCode1 = '" + strCode1 + "' " + iIndex  );
                String    strCode2  = strCode1.substring( iIndex + 1 );
                if( strCode2.equals( "Sender" ) &&   // saaj1.3
                    faultCode.equals( CLIENT ) ) return true;
                if( strCode2.equals( "Receiver" ) &&   // saaj1.3
                    faultCode.equals( SERVER ) ) return true;
                // The other are the same
                if( ! faultCode.equalsIgnoreCase( strCode2 ) ) {
                    System.out.println( "ERROR: code is '" + strCode2 +
                                        "' but faultCode is '" + 
                                        faultCode + "'" );
                    return false;
                }
            } else if( e instanceof javax.xml.ws.WebServiceException ){ // after o0643.16
                String strString = e.getMessage(); // This ought to be the String
                if( faultReason.equals( strString ) ) return true;
            } else{
                if( ! faultCode.equals( CLIENT ) ){
                    System.out.println( "ERROR: strCode1 is null " );
                    return false;
                } else {  // HTTP ( 400 ) Bad Request
                    String strErr = e.getMessage();
                    if( strErr.indexOf( "HTTP" ) >= 0 &&
                        strErr.indexOf( "400"  ) >= 0  ){
                        System.out.println( "Get an expected new SAAJ1.3 Sender(Client) Exception" );
                    }  else {
                        System.out.println( "ERROR: Client/Sender is supposed to get ERR code 400" );
                    }
                }
            }

             // Check reason
            String strReason = getFaultReason( fault, faultLocale );
            if( !(strReason == null && faultReason == null )){
                if( strReason == null ){
                    System.out.println( "ERROR: strReason is null but faultReason is '" + 
                                        faultReason + "'" );
                    return false;
                }
                if( ! strReason.equalsIgnoreCase( faultReason ) ) {
                    System.out.println( "ERROR: omReason is '" + strReason +
                                        "' but faultReason is '" + 
                                        faultReason + "'" );
                    return false;
                }
            }

             //check Actor
            String strActor  = getFaultRole( fault );
            if( !(strActor == null && faultActor == null )){
                if( strActor == null ){
                    System.out.println( "ERROR: strActor is null but faultActor is '" + 
                                        faultActor + "'" );
                    return false;
                }
                if( ! strActor.equals( faultActor ) ) {
                    System.out.println( "ERROR: omActor is '" + strActor +
                                        "' but faultActor is '" + 
                                        faultActor + "'" );
                    return false;
                }
            }

             // check Detail
             // OMElement detailOMElement = fault.getFaultDetailElement();
             // if( ! isEqual13Elem( detailOMElement, (SOAPElement)faultDetail ) ){
             //     System.out.println( "Not Expected SOAPFault:" + detailOMElement + " <-> " + faultDetail );
             //     return false;
             // }
        } else if( e instanceof javax.xml.ws.WebServiceException ){
            if( e instanceof javax.xml.ws.soap.SOAPFaultException ){
                SOAPFault soapFault = ((javax.xml.ws.soap.SOAPFaultException)e).getFault();
                return isExpectedFault( soapFault, true );
            } else {
                String strMsg = e.getMessage();
                if( strMsg.equals( faultReason ) ){
                    return true;
                } else {
                    System.out.println( "Expecting '"  + faultReason + "' but get '" +
                                        strMsg + "'" );
                    return false;
                }
            }
        } else {
            System.out.println( "The bottom exception is not AxisFault :" + e );
            return false;
        }
        return true;
    }

    private String getFaultCode(AxisFault fault ) throws Exception {
        SOAPFaultCode  soapCode  = fault.getFaultCodeElement(); 
        if( soapCode == null ) return null;
        SOAPFaultValue soapValue = soapCode.getValue();
        if( soapValue == null ) return null;
        return soapValue.getText();
    }

    private String getFaultReason(AxisFault fault, String locale ) throws Exception {
        SOAPFaultReason        soapReason = fault.getFaultReasonElement(); 
        System.out.println( "Reason " + soapReason );
        if( soapReason == null ) return null;
        // debug
        List                    list      = soapReason.getAllSoapTexts();
        Iterator<SOAPFaultText> iterator  = list.listIterator();
        String strT = null;
        while( iterator.hasNext() ){
            SOAPFaultText       soapText  = (SOAPFaultText)iterator.next();
            System.out.println( "  Language   :" + soapText.getLang() );
            strT = soapText.getText();
            System.out.println( "  faultString:" + strT );
        }

        if( strT != null ) return strT;

        SOAPFaultText           soapValue = soapReason.getSOAPFaultText( locale );
        System.out.println( "ReasonText " + soapValue );
        if( soapValue == null ){
            String strText = soapReason.getText();
            if( strText != null ) return strText;
            return null;
        }
        return soapValue.getText();
    }

    private String getFaultRole(AxisFault fault ) throws Exception {
        SOAPFaultRole  soapRole  = fault.getFaultRoleElement(); 
        if( soapRole == null ) return null;
        return soapRole.getRoleValue();
    }


    public boolean isExpectedMessage( SOAPMessage soapMessage ) throws Exception {
       System.out.println( "Compare FaultData13 " + toString() );
       soapMessage.writeTo( System.out );
       System.out.println( );

       //todo: Verify the  SOAPHeaderElement

       SOAPBody  soapBody  = soapMessage.getSOAPBody(); 
       if( ! soapBody.hasFault() ){
           System.out.println( "The SOAPMessage does not have a SOAPFault" );
           return false;
       }

       SOAPFault soapFault = soapBody.getFault();
       return isExpectedFault( soapFault );
    }

    public boolean isExpectedFault( SOAPFault soapFault ) throws Exception {

       QName     qCode1    = soapFault.getFaultCodeAsQName();
       QName     qName1    = getFaultCodeQName( faultCode );
       if( ! qName1.equals( qCode1 ) ) {
           System.out.println( "ERROR: code is '" + qCode1 +
                               "' but faultCode is '" + 
                               faultCode + "'" );
           return false;
       }


       // Check reason
       String    strReason = soapFault.getFaultString();
       if( ! strReason.equalsIgnoreCase( faultReason ) ) {
           if( faultLocale != null ){
               Locale locale = Locale.ENGLISH;
               if( faultLocale.equalsIgnoreCase( "Big5" ) ){
                   locale = Locale.TAIWAN;
               }
               strReason = soapFault.getFaultReasonText( locale );
               if( ! strReason.equalsIgnoreCase( faultReason ) ){
                   return false;
               }
           } else {
               System.out.println( "ERROR: faultReason is '" + strReason +
                                   "' but faultReason is '" + 
                                   faultReason + "'" );
               return false;
           }
       }

       String    strActor = soapFault.getFaultActor();
       if( strActor == null ){
           if( faultActor != null &&  faultActor.length() != 0 ){
               System.out.println( "ERROR: FaultActor is '" + faultActor +
                                   "' but get back '" + 
                                   strActor + "'" );
               return false;
           }
       } else  if( ! strActor.equals( faultActor ) ) {
           System.out.println( "ERROR: strActor is '" + strActor +
                               "' but faultActor is '" + 
                               faultActor + "'" );
           return false;
       }

        // check Detail
        SOAPElement detail = (SOAPElement)soapFault.getDetail();
        if( ! isEqual13Elem( detail, (SOAPElement)faultDetail ) ){
            System.out.println( "Not Expected SOAPFault:" + detail + " <-> " + faultDetail );
            return false;
        }

        Iterator<QName> qSubcodes = soapFault.getFaultSubcodes(); 
        boolean bOk1 = false;
        boolean bOk2 = false;
        boolean bHasQName = false;
        while( qSubcodes.hasNext() ){
            bHasQName = true;
            QName qName = (QName)qSubcodes.next();
            System.out.println( "subcode qname :" + qName +
                                "subcode :" + faultSubCodeLocalPart + " " + faultSubCodeURI);
            if( qName.getLocalPart().equals( faultSubCodeLocalPart ) ){
                bOk1 = true;
            }
            if( qName.getNamespaceURI().equals( faultSubCodeURI ) ){
                bOk2 = true;
            }
        }

        if( bHasQName ){
            return ( bOk1 && bOk2 );
        }

        return true;
    }

    public boolean isExpectedFault( SOAPFault soapFault, boolean bPrint ) throws Exception {
        if( bPrint ){
            SOAPElement p = soapFault;
            while (p.getParentElement() != null) {
                p = p.getParentElement();
            }
            if(p instanceof SOAPMessage){
               ((SOAPMessage)p).writeTo(System.out);
               System.out.println();
            }
        }
        return isExpectedFault( soapFault );
    }


    protected void dumpAxisFault( AxisFault fault )throws Exception{
        // System.out.println( "  reason        " + fault.getReason());
        // System.out.println( "  detail        " + fault.getDetail() );
        // System.out.println( "  faultCode     " + fault.getFaultCode() );
        // System.out.println( "  faultCodeE    " + fault.getFaultCodeElement() );
        // System.out.println( "  faultReasonE  " + fault.getFaultReasonElement() );
        // dumpOMElement( fault.getFaultReasonElement() ); 
        // System.out.println( "  faultNodeE    " + fault.getFaultNodeElement() );
        // dumpOMElement( fault.getFaultNodeElement() ); 
        // System.out.println( "  faultRoleE    " + fault.getFaultRoleElement() );
        // dumpOMElement( fault.getFaultRoleElement() ); 
        // System.out.println( "  faultDetailE  " + fault.getFaultDetailElement() );
        // dumpOMElement( fault.getFaultDetailElement() ); 
        // System.out.println( "  NodeURI       " + fault.getNodeURI() );
        // System.out.println( "  faultElements " + fault.getFaultElements() );
        // System.out.println( "  faultNode     " + fault.getFaultNode() );
        // System.out.println( "  faultRole     " + fault.getFaultRole() );
        // System.out.println( "  message       " + fault.getMessage() );
    }

    // String      faultCode    = "Client"; // faultcode env:Code
    // String      faultActor   = null;   // Role
    // String      faultReason  = null;  // FaultString
    // String      faultLocale  = "Big5";
    // SOAPElement faultDetail  = null;
    public String toString(){
        return "FaultData13 : code '" + 
               faultCode   + "' Actor '"  +
               faultActor  + "' reason '" + 
               faultReason + "' locale '" +
               faultLocale + "' Detail '" +
               faultDetail + "'";
    }

    // private void dumpOMElement( OMElement omElem ) throws Exception{
    //     if( omElem != null ){
    //         dumpOMElement( omElem, 0 );
    //         System.out.println( );   
    //     }
    // }
    // 
    // private void dumpOMElement( OMElement omElem, int iLevel ) throws Exception{
    //     OMNode child = omElem.getFirstOMChild();
    //     for( int iI = 0; iI <  iLevel; iI ++ ){
    //         System.out.print( "  " );
    //     }
    //     System.out.println( "dumpOMElement :" + omElem );
    //     System.out.println( "        class :" + omElem.getClass().getName() );
    //     while (child != null) {
    //         if (child.getType() == OMNode.TEXT_NODE) {
    //             OMText textNode = (OMText) child;
    //             for( int iI = 0; iI <  iLevel; iI ++ ){
    //                 System.out.print( "  " );
    //             }
    //             System.out.println( "OMText : " + textNode.getText());
    //         } else if( child instanceof OMElement ){
    //             dumpOMElement( (OMElement)child, iLevel + 1  );
    //         } else {
    //             System.out.println( "**** terminated" );
    //         }
    //         child = child.getNextOMSibling();
    //     }
    // }

    //SOAPConstants.SOAP_SENDER_FAULT;          
    //SOAPConstants.SOAP_RECEIVER_FAULT;        
    //SOAPConstants.SOAP_MUSTUNDERSTAND_FAULT;  
    //SOAPConstants.SOAP_VERSIONMISMATCH_FAULT; 
    //SOAPConstants.SOAP_DATAENCODINGUNKNOWN_FAULT; 
    protected QName getFaultCodeQName( String strFaultCode ){
        QName qName = SOAPConstants.SOAP_SENDER_FAULT;
        if( strFaultCode.equalsIgnoreCase( CLIENT   ) ) qName = SOAPConstants.SOAP_SENDER_FAULT;          
        if( strFaultCode.equalsIgnoreCase( SERVER   ) ) qName = SOAPConstants.SOAP_RECEIVER_FAULT;        
        if( strFaultCode.equalsIgnoreCase( MUST     ) ) qName = SOAPConstants.SOAP_MUSTUNDERSTAND_FAULT;  
        if( strFaultCode.equalsIgnoreCase( VERSION  ) ) qName = SOAPConstants.SOAP_VERSIONMISMATCH_FAULT; 
        if( strFaultCode.equalsIgnoreCase( ENCODING ) ) qName = SOAPConstants.SOAP_DATAENCODINGUNKNOWN_FAULT; 
        return qName;
    }
}


