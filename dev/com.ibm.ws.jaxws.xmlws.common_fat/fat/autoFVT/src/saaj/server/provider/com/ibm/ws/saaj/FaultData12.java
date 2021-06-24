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

public class FaultData12 extends SAAJUtil{
    final static public String ENVPREFIX = SOAPConstants.SOAP_ENV_PREFIX;
    final static public String CLIENT    = "Client";
    final static public String SERVER    = "Server";
    final static public String MUST      = "MustUnderstand";
    final static public String VERSION   = "VersionMismatch";

    String faultCode    = "Client"; // faultcode env:Code
    String faultActor   = null;   // Role
    String faultReason  = "MustHaveAFaultString";  // FaultString
    String faultLocale  = "Big5";
    Detail faultDetail  = null;

    // Initialize Data in client 
    public FaultData12( String code,  // Client, Server, Mustunderstand
                        String actor, 
                        String fault, 
                        String locale,
                        Detail detail ){
        setCode  ( code );
        setActor ( actor  );
        setFault ( fault, locale );
        setDetail( detail );
    }

    // Initialize Data through the requesting SOAPMessage 
    public FaultData12( SOAPMessage soapMessage ) throws Exception{
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
        MessageFactory  messageFactory   = getMessageFactory(); 
        SOAPFactory     soapFactory      = getSOAPFactory();
        String          strCaseID        = "Fault12_" + iCase;

        SOAPMessage     soapMessage      = messageFactory.createMessage();

        Name            headerName       = soapFactory.createName(strCaseID, "Fvt", 
                                                         "http://www.austin.texas.com/ibm" );
        SOAPHeader      soapHeader       = soapMessage .getSOAPHeader();
        SOAPHeaderElement soapHeaderElem = soapHeader.addHeaderElement( headerName );
        soapHeaderElem.setActor( strCaseID );

        SOAPBody        soapBody         = soapMessage .getSOAPBody();

        Name            bodyName         = soapFactory.createName("Fault12", "SAAJ12", "http://soap.ibm.com/soap"  );
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
                Name        localeName       = soapFactory.createName("locale" );
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

        System.out.println( "Created a SOAPMessage as:" );
        soapMessage.writeTo( System.out );
        System.out.println( );
        return soapMessage; 
    }

    // This creates a SOAPMessage which load the data in the SOAPMessage
    public SOAPMessage createFaultMessage( ) throws Exception{
        MessageFactory  messageFactory  = getMessageFactory(); 
        SOAPFactory     soapFactory     = getSOAPFactory();

        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        // create a SOAPFault
        Name            nameCode        = createName( faultCode,
                                                      ENVPREFIX,
                                                      SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE
                                                    );
        // handle faultCode, faultReason and faultLocale

        SOAPFault       soapFault       = null;
        if( faultLocale != null ){
            Locale locale = Locale.ENGLISH;
            if( faultCode.equalsIgnoreCase( "Big5" ) ){
                locale = Locale.TAIWAN;
            }
            soapFault       = soapBody.addFault( nameCode, 
                                                 faultReason,
                                                 locale );
        } else if( faultReason != null ){
            soapFault       = soapBody.addFault( nameCode, 
                                                 faultReason );
        } else {
            soapFault       = soapBody.addFault();
            soapFault.setFaultCode( nameCode );
        }

        if( faultActor != null ){
            soapFault.setFaultActor( faultActor );
        }

        if( faultDetail != null ){
            Detail detail = soapFault.addDetail(); // create a Detail first
            addDetail( soapMessage );
        }

        System.out.println( "Created a Fault SOAPMessage as:" );
        soapMessage.writeTo( System.out );
        System.out.println( );
        return soapMessage; 
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
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        Name            nameFault       = createName( "Fault12", "SAAJ12", "http://soap.ibm.com/soap"   );
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
                    System.out.println( "ERROR: Unknown childElement in FaultData12 '" + nameA + "'");
                }
            } else {
                System.out.println( "ERROR: Unknown node in FaultData12" + node );
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
        return MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL); 
    }

    static SOAPFactory soapFactory = null;
    static public SOAPFactory getSOAPFactory() throws Exception {
        if( soapFactory == null ){
            soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
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

    public boolean isExpectedFault( Throwable e ) throws Exception{
        // debugging
        // System.out.println( "  isExppectedFault : " + e );
        // e.printStackTrace( System.out );
        Throwable t = e.getCause();
        if( t != null ){
            return isExpectedFault( t );
        }
        if( e instanceof AxisFault ){  // original
            System.out.println( "Compare FaultData12 " + toString() );
            AxisFault fault = (AxisFault)e;
            dumpAxisFault( fault );
            // check faultCode
            String strCode1    = getFaultCode( fault );
            if( strCode1 != null ){
                int       iIndex    = strCode1.indexOf( ":" ); // -1
                // System.out.println( "strCode1 = '" + strCode1 + "' " + iIndex  );
                String    strCode2  = strCode1.substring( iIndex + 1 );
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
                System.out.println( "ERROR: strCode1 is null " );
                return false;
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

             //// check Detail
             //OMElement detailOMElement = fault.getFaultDetailElement();
             //if( ! isEqual12Elem( detailOMElement, (SOAPElement)faultDetail ) ){
             //    System.out.println( "Not Expected SOAPFault:" + detailOMElement + " <-> " + faultDetail );
             //    return false;
             //}
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
       System.out.println( "Compare FaultData12 " + toString() );
       soapMessage.writeTo( System.out );
       System.out.println( );

       SOAPBody  soapBody  = soapMessage.getSOAPBody(); 
       if( ! soapBody.hasFault() ){
           System.out.println( "The SOAPMessage does not have a SOAPFault" );
           return false;
       }

       SOAPFault soapFault = soapBody.getFault();
       return isExpectedFault( soapFault );
    }

    public boolean isExpectedFault( SOAPFault soapFault ) throws Exception {

       String    strCode1  = soapFault.getFaultCode();
       int       iIndex    = strCode1.indexOf( ":" ); // -1
       String    strCode2  = strCode1.substring( iIndex + 1 );
       if( ! faultCode.equalsIgnoreCase( strCode2 ) ) {
           System.out.println( "ERROR: code is '" + strCode2 +
                               "' but faultCode is '" + 
                               faultCode + "'" );
           return false;
       }

       // Check reason
       String    strReason = soapFault.getFaultString();
       if( ! strReason.equalsIgnoreCase( faultReason ) ) {
           System.out.println( "ERROR: omReason is '" + strReason +
                               "' but faultReason is '" + 
                               faultReason + "'" );
           return false;
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
        if( ! isEqual12Elem( detail, (SOAPElement)faultDetail ) ){
            System.out.println( "Not Expected SOAPFault:" + detail + " <-> " + faultDetail );
            return false;
        }

        return true;
    }
    public boolean isExpectedFault( SOAPFault soapFault, boolean bPrint ) throws Exception {
        if( bPrint ){
            MessageFactory  messageFactory  = MessageFactory.newInstance();
                 // 1.2 need to specify the version
            SOAPMessage     soapMsg         = messageFactory.createMessage();
            SOAPBody        soapBody        = soapMsg.getSOAPBody();
            soapBody.addChildElement( (SOAPElement)soapFault);
            soapMsg.writeTo( System.out );
            System.out.println();
        }
        return isExpectedFault( soapFault );
    }



    protected void dumpAxisFault( AxisFault fault )throws Exception{
        // System.out.println( "  reason        " + fault.getReason());
        // System.out.println( "  detail        " + fault.getDetail() );
        // System.out.println( "  faultCode     " + fault.getFaultCode() );
        // System.out.println( "  faultCodeE    " + fault.getFaultCodeElement() );
        // System.out.println( "  faultReasonE  " + fault.getFaultReasonElement() );
        // System.out.println( "  faultNodeE    " + fault.getFaultNodeElement() );
        // System.out.println( "  faultRoleE    " + fault.getFaultRoleElement() );
        // System.out.println( "  faultDetailE  " + fault.getFaultDetailElement() );
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
        return "FaultData12 : code '" + 
               faultCode   + "' Actor '"  +
               faultActor  + "' reason '" + 
               faultReason + "' locale '" +
               faultLocale + "' Detail '" +
               faultDetail + "'";
    }

}


