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
import java.util.*;

// SAAJ SOAPMessage related
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.Text;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.MimeHeader;


// XML parser related
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.axiom.attachments.IncomingAttachmentInputStream;

// SAAJConverter and its Factories
import org.apache.axis2.jaxws.message.util.SAAJConverter;
import org.apache.axis2.jaxws.registry.FactoryRegistry;
import org.apache.axis2.jaxws.message.factory.SAAJConverterFactory;

/**
 * SAAJBase
 * 
 * The utility method common to SAAJ FVT Server and Client
 *
 */

public class SAAJUtil12
{

    public static final String MESSAGE_BEGIN = "$$@BEGIN@$$";
    public static final String MESSAGE_END   = "$$@END@$$";
    public static final String SOCKET_END    = "$$@ENDSOCKET@$$";
    public static final int    SERVER_PORT   = 10504;  // A random number. 

    public static String getStackTraceString( Throwable t )
    {
        String retaStr = null;
        try
        {
            ByteArrayOutputStream baStream    = new ByteArrayOutputStream();
            PrintStream           printStream = new PrintStream(baStream);

            t.printStackTrace(printStream);

            printStream.flush();
            byte[] ba = baStream.toByteArray();
            printStream.close();

            retaStr = new String( ba );
        }
        catch( Exception e )
        {
            retaStr = t.toString();
        }

        return retaStr;
    }


    public static String getBodyFirstChildStr( SOAPMessage soapMessage ) {
        try{
            Name name = getBodyFirstChildName( soapMessage);
            if( name == null ){
                return null;
            } else {
                String strPrefix = name.getPrefix();
                String localName = name.getLocalName();
                return (strPrefix == null || strPrefix.length() == 0)? localName : strPrefix + ":" + localName;
            }
        } catch( Exception e ){
            e.printStackTrace( System.out );
            return null;
        }
    }

    public static Name getBodyFirstChildName( SOAPMessage soapMessage ) {
        try{
            SOAPBodyElement soapBodyElement = getBodyFirstChild( soapMessage );
            if( soapBodyElement == null ) return null;
            return soapBodyElement.getElementName();
        } catch( Exception e ){
            e.printStackTrace(System.out);
        }
        return null;
    }

    public static SOAPBodyElement getBodyFirstChild( SOAPMessage soapMessage ) {
        try{
            SOAPBody soapBody = soapMessage.getSOAPBody();
            java.util.Iterator soapBodyElements = soapBody.getChildElements();
            if( soapBodyElements.hasNext() ){
                Object obj = soapBodyElements.next();
                if( obj instanceof SOAPBodyElement ){
                    return (SOAPBodyElement) obj;
                } else {
                    return null;
                }
            } 
        } catch( Exception e ){
            e.printStackTrace(System.out);
        }
        return null;
    }

    public static String getBodyFirstChildUri( SOAPMessage soapMessage ) {
        try{
            Name name = getBodyFirstChildName( soapMessage);
            if( name == null ){
                return null;
            } else {
                return name.getURI();
            }
        } catch( Exception e ){
            e.printStackTrace( System.out );
            return null;
        }
    }

    public static String getBodyFirstChildValue( SOAPMessage soapMessage ) {
        try{
            SOAPBodyElement soapBodyElement = getBodyFirstChild( soapMessage );
            if( soapBodyElement != null ){
                return soapBodyElement.getValue();
            }
        } catch( Exception e ){
            e.printStackTrace( System.out );
            return null;
        }
        return null;
    }

    public static SOAPBodyElement getBodySecondChild( SOAPMessage soapMessage ){
        try{
            SOAPBody soapBody = soapMessage.getSOAPBody();
            java.util.Iterator soapBodyElements = soapBody.getChildElements();
            if( soapBodyElements.hasNext() ){ // 1st child
                Object obj = soapBodyElements.next();
                if( soapBodyElements.hasNext() ){ // second child
                    Object obj1 = soapBodyElements.next();
                    if( obj1 instanceof SOAPBodyElement){
                        return (SOAPBodyElement)obj1;
                    }
                }
            }
        } catch( Exception e ){
            e.printStackTrace(System.out);
        }
        return null;
    }


    public static SOAPElement getBodyFirstGrandChild( SOAPMessage soapMessage ){
        try{
            SOAPBody soapBody = soapMessage.getSOAPBody();
            Iterator soapBodyElements = soapBody.getChildElements();
            if( soapBodyElements.hasNext() ){
                Object obj = soapBodyElements.next();
                if( obj instanceof SOAPBodyElement ){
                    SOAPBodyElement soapBodyElement    = (SOAPBodyElement)obj;
                    Iterator        soapGrandChildren  = soapBodyElement.getChildElements(); 
                    if( soapGrandChildren.hasNext() ){
                        Object obj1 = soapGrandChildren.next();
                        if( obj1 instanceof SOAPElement ){
                            return (SOAPElement) obj1;
                        } 
                    }
                }
            } 
        } catch( Exception e ){
            e.printStackTrace(System.out);
        }
        return null;
    }


    public static SOAPMessage createError12Message( String msg ) throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(); // 1.2 need to specify the version
        return createErrorMessage( msg, messageFactory);
    }

    public static SOAPMessage createError13Message( String msg ) throws Exception{
        // SOAP 1.2
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
             // SAAJ 1.3 need to specify the version
        return createErrorMessage( msg, messageFactory);
    }

    public static SOAPMessage createErrorMessage( String msg, 
                                                  MessageFactory  messageFactory  ) throws Exception{
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        SOAPFactory soapFactory  = SOAPFactory.newInstance();
        Name bodyName            = soapFactory.createName("answer_error", "err", "http://error.ibm.com/error" );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( msg );
        // Name bodyName1            = soapFactory.createName("WebServices_Version" );
        // SOAPBodyElement soapBodyElement1 = soapBody.addBodyElement( bodyName1 );
        // String buildDate          = com.ibm.ws.webservices.Version.getDateBuilt();
        // soapBodyElement1.addTextNode( com.ibm.ws.webservices.Version.getVersion() );
        return soapMessage;
    }


    public static boolean isSaaj12Message( SOAPMessage soapMessage ){
        int iVersion = getSaajVersion( soapMessage );
        
        return (iVersion == 12 ); //
    }

    // return 12 means SOAP 1.1 (SAAJ1.2 and 1.3
    // return 13 means SOAP 1.2/SAAJ1.3
    public static int getSaajVersion( SOAPMessage soapMessage ){
        try{
            SOAPPart     soapPart     = soapMessage.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart   .getEnvelope();
            return getSaajVersion( soapEnvelope );
        } catch( Exception e ){
            e.printStackTrace(System.out);
        }
        
        return 00; //
    }

    // return 12 means SOAP 1.1 (SAAJ1.2 and 1.3
    // return 13 means SOAP 1.2/SAAJ1.3
    public static int getSaajVersion( SOAPEnvelope soapEnvelope ){
        try{
            java.util.Iterator namespaces = soapEnvelope.getNamespacePrefixes();
            while( namespaces.hasNext() ){
                String prefix = (String)namespaces.next();
                String uri = soapEnvelope.getNamespaceURI( prefix );
                if( uri.equalsIgnoreCase( "http://schemas.xmlsoap.org/soap/envelope/" )){
                    // System.out.println( "SAAJ12/SOAP11(1)" );
                    return 12;
                }
                if( uri.equals("http://www.w3.org/2003/05/soap-envelope") ){
                    // System.out.println( "SAAJ13/SOAP12(1)" );
                    return 13;
                }
            }
        } catch( Exception e ){
            e.printStackTrace(System.out);
        }
        
        return 00; //
    }

    // return 12 means SOAP 1.1 (SAAJ1.2 and 1.3
    // return 13 means SOAP 1.2/SAAJ1.3
    public static int getSaajVersion( String soapMessage ){
        if( soapMessage.indexOf( "http://schemas.xmlsoap.org/soap/envelope/" ) >= 0){
            // System.out.println( "SAAJ12/SOAP11(2)" );
            return 12;
        }
        if( soapMessage.indexOf( "http://www.w3.org/2003/05/soap-envelope"   ) >= 0){
            // System.out.println( "SAAJ13/SOAP12(2)" );
            return 13;
        }
        
        return 00; //
    }

    public static final String STR_BODY_ELEMENT_1 =
          "<austin:power xmlns:austin=\"urn://movieAustinPower\"" +
          " xmlns:cp=\"http://potato.com/coachPotato\">" +
          "<cp:coachPotato>Chips, cakes, TV, No karaoke, no exercises" + 
          "</cp:coachPotato>" +
          "</austin:power>";

    public static final String STR_BODY_ELEMENT_2 =
          "<parameterpower xmlns:austin=\"http://movieAustinPower.com/movie\"" +
          " xmlns:food=\"http://potato/coachPotato\">" +
          "<food:TexasPotato><sizeGiant>Sweet, Juicy, Great, Giant Size</sizeGiant>" + 
          "</food:TexasPotato>" +
          "</parameterpower>";

    public static final String STR_BODY_ELEMENT_3 =  // some text are in Big-5 code
          "<dallas:austinpower xmlns:dallas=\"urn://電視影集\"" + // TV show 
          " xmlns:houston=\"http://馬鈴薯.com/馬鈴薯\" " + // 馬鈴薯=potato 
          "xmlns=\"http://default.com/default\">" +        // default name space
          "<houston:coachPotatos populaton=\"millions\">"+
          "餅乾,蛋糕,電視,不唱歌,不運動" + // 餅乾,蛋糕,電視,不唱歌,不運動 =
          "</houston:coachPotatos>" +      // cookies,cake,tv,NoKaraoke,noExercise
          "</dallas:austinpower>";


    public static Document newDocument( String strXml ) throws Exception{
        StringReader is = new StringReader(strXml);
        InputSource inpSource = new InputSource( is );
        return newDocument( inpSource );
    }

    public static  Document newDocument(InputSource inp)
                   throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return( db.parse( inp ) );
    }
                                                                                
    public static SOAPMessage createSOAP12MessageFromBodyStr( String strElement )
                  throws Exception {
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        // To create SOAP Element instances
        // SOAPFactory soapFactory = SOAPFactory.newInstance();
        // Name bodyName           = soapFactory.createName("Simple12", "Simple12Prefix",
        //                                                  "http://soap.ibm.com/simple12");   
        // SOAPBodyElement soapBodyElement1 = soapBody.addBodyElement( bodyName );
        // soapBodyElement1.addTextNode( "testSimple12 2 bodies" );

        Document doc = newDocument( strElement );
        SOAPBodyElement soapBodyElement = soapBody.addDocument( doc );
        return soapMessage;
    }

    public static boolean isEqual12( SOAPElement soapElement, String strElement) throws Exception{
        SOAPMessage soapMessage        = createSOAP12MessageFromBodyStr( strElement ); 
        SOAPBodyElement soapBodyElement = getBodyFirstChild( soapMessage);
        boolean retBoolean = isEqual12Elem( soapElement, (SOAPElement)soapBodyElement );
        if( ! retBoolean ){
            System.out.println( );
            System.out.println( "***SOAPMessage of the SOAPBodyElement is not equal" );
            soapMessage.writeTo( System.out );
            System.out.println( );
        }
        return retBoolean;
    }

    public static boolean isEqual12Elem( SOAPElement elem1, SOAPElement elem2 ) {
        if( elem1 == null && elem2 == null ) return true;
        if( elem1 == null || elem2 == null ){
            System.out.println( "One of the element is null" );  // dbg 
            return false; // unless both are null
        }

        try{
            System.out.println( "Compare element '" + elem1.toString() + "' && '" +  // dbg 
                                elem2.toString() + "'" );  // dbg 
            Name name1 = elem1.getElementName();
            Name name2 = elem2.getElementName();
            if( !isEqual12Name( name1, name2 ) ) {
                System.out.println( "Name not equal" );  // dbg 
                return false;
            }
            Iterator<Name> iterAttr1 = elem1.getAllAttributes();
            Iterator<Name> iterAttr2 = elem2.getAllAttributes();
            if( !isEqual12Attributes( iterAttr1, iterAttr2, elem1, elem2 ) ){
                System.out.println( "Attributess not equal" );  // dbg 
                return false;
            }
            Iterator<SOAPElement> iterKids1 = elem1.getChildElements();
            Iterator<SOAPElement> iterKids2 = elem2.getChildElements();
            if( !isEqual12Elements( iterKids1, iterKids2 ) ) {
                System.out.println( "Elements not equal" );  // dbg 
                return false;
            }
            String strValue1 = elem1.getValue();
            String strValue2 = elem2.getValue();
            return isEqualStr( strValue1, strValue2 );
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return false;
        }
    }

    public static boolean isEqual12Name( Name name1, Name name2 ) {
        if( name1 == null && name2 == null ) return true;
        if( name1 == null || name2 == null ){
            System.out.println( "One of the name is null" );  // dbg 
            return false; // unless both are null
        }

        try{
            System.out.println( "Compare name '" + name1.toString() + "' && '" +  // dbg 
                                name2.toString() + "'" );  // dbg 
            String strPrefix1 = name1.getPrefix(); 
            String strLocal1  = name1.getLocalName();
            String strQname1  = name1.getQualifiedName();
            String strUri1    = name1.getURI();

            String strPrefix2 = name2.getPrefix(); 
            String strLocal2  = name2.getLocalName();
            String strQname2  = name2.getQualifiedName();
            String strUri2    = name2.getURI();
            if( strPrefix1.indexOf( "axis2ns" ) >= 0 ||
                strPrefix2.indexOf( "axis2ns" ) >= 0  ){
                System.out.println( "axis2ns temporary prefix found. Skip it" );  // dbg 
                strPrefix1 = strPrefix2;
                strQname1  = strPrefix2 + ":" + strLocal1;
            }

            if ( // isEqualStr( strPrefix1, strPrefix2 ) &&
                 isEqualStr( strLocal1 , strLocal2  ) &&
                 // isEqualStr( strQname1 , strQname2  ) &&
                 isEqualStr( strUri1   , strUri2    ) ){
                return true;
            } else {
                System.out.println( "Name not Equals: '" +  // dbg 
                                    strQname1 + "' <> '" +  // dbg 
                                    strQname2 + "'"  // dbg 
                                    );  // dbg 
                return false;
            }
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return false;
        }
    }

    public static boolean isEqualStr( String str1, String str2 ){
        if( str1 == null && str2 == null ) return true;
        if( str1 == null || str2 == null ){
            if( !reviewMinor() ){
                if( str1 != null && str1.length() == 0  ){
                    return true;
                }
                if( str2 != null && str2.length() == 0 ){
                    return true;
                }
            }
            System.out.println( "String not Equals: '" +  // dbg 
                                str1 + "' <> '" +  // dbg 
                                str2 + "'"  // dbg 
                                );  // dbg 
            return false; // unless both are null
        }
        if( str1.equals( str2 ) ) {
            System.out.println( "String Equals: '" +  // dbg 
                                str1 + "' == '" +  // dbg 
                                str2 + "'"  // dbg 
                              );  // dbg 
            return true;
        } else {
            if( ! reviewMinor() ){
                if( str1.equalsIgnoreCase( str2 ) ) {
                    return true;
                }
            }
            System.out.println( "String not Equals: '" +  // dbg 
                                str1 + "' <> '" +  // dbg 
                                str2 + "'"  // dbg 
                                );  // dbg 
            return false;
        }
    }

    public static boolean isEqual12Attributes( Iterator<Name> iter1, 
                                               Iterator<Name> iter2,
                                               SOAPElement    elem1,
                                               SOAPElement    elem2
                                               ) {
        if( iter1 == null && iter2 == null ) return true;
        if( iter1 == null || iter2 == null ) {
            System.out.println( "one of the attributes is null" );  // dbg 
            return false; // unless both are null
        }

        try{
            System.out.println( "Compare attributes" );  // dbg 
            // translate the Name Iterator to Hashtable
            List list1 = new Vector();
            while(iter1.hasNext()) {
                Name name =iter1.next();
                if( !name.getPrefix()   .equals( "xmlns" ) &&
                    !name.getLocalName().equals( "xmlns" ) 
                ){ 
                    // We do not compare NameSpace
                    // NameSpace is active only when it define some name
                    list1.add( name );
                }
            }
            List list2 = new Vector();
            while(iter2.hasNext()) {
                Name name =iter2.next();
                if( !name.getPrefix()   .equals( "xmlns" ) &&
                    !name.getLocalName().equals( "xmlns" ) 
                ) { 
                    // We do not compare NameSpace definitions
                    // NameSpace is active only when it define some name
                    list2.add( name );
                }
            }
            int iSize = list1.size();
            if( iSize != list2.size() ) 
            {
                System.out.println( "Size does not match " + iSize + "<>" +   // dbg 
                                    list2.size() );  // dbg 
                return false;
            }
            for( int iI = 0; iI < iSize; iI ++ ){
                int iSize2 = list2.size();
                int iJ = 0;
                for( ; iJ < iSize2; iJ ++ ){
                    Name name1 = (Name)list1.get(iI);
                    Name name2 = (Name)list2.get(iJ);
                    if( isEqual12Name( name1, name2) ){
                        String strValue1 = elem1.getAttributeValue( name1 );
                        String strValue2 = elem2.getAttributeValue( name2 );
                        if( strValue1.equals( strValue2 ) ){
                            list2.remove( iJ );
                            break;
                        }
                    }
                }
                if( iJ >= iSize2 ) {
                    System.out.println( "Name not found " + iJ + " vs size" +   // dbg 
                                        iSize2  );  // dbg 
                    return false; // can not find a match Name
                }
            }
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return false;
        }
        return true;
    }

    public static boolean isEqual12Elements( Iterator<SOAPElement> iter1, 
                                             Iterator<SOAPElement> iter2 ) {
        if( iter1 == null && iter2 == null ) return true;
        if( iter1 == null || iter2 == null ){
            System.out.println( "one of the elemnts is null" );  // dbg 
            return false; // unless both are null
        }

        try{
            System.out.println( "Compare elements" );  // dbg 
            // translate the Name Iterator to Hashtable
            List list1 = new Vector<SOAPElement>();
            while(iter1.hasNext()) {
                Object obj1 = iter1.next();
                System.out.println( "Object1 : '" + obj1.toString() + "'" );  // dbg 
                list1.add( obj1 );
            }
            List list2 = new Vector<SOAPElement>();
            while(iter2.hasNext()) {
                Object obj2 = iter2.next();
                System.out.println( "Object2 : '" + obj2.toString() + "'" );  // dbg 
                list2.add( obj2 );
            }
            int iSize = list1.size();
            if( iSize != list2.size() ){
                System.out.println( "Size does not match " + iSize + "<>" +   // dbg 
                                    list2.size() );  // dbg 
                return false;
            }
            for( int iI = 0; iI < iSize; iI ++ ){
                int iSize2 = list2.size();
                int iJ = 0;
                for( ; iJ < iSize2; iJ ++ ){
                    Object obj1 = list1.get(iI);
                    Object obj2 = list2.get(iJ);
                    if( obj1 instanceof SOAPElement  &&
                        obj2 instanceof SOAPElement  &&
                        isEqual12Elem( (SOAPElement)obj1, (SOAPElement)obj2 ) ){
                        list2.remove( iJ );
                        break;
                    }
                    if( obj1 instanceof Text &&
                        obj2 instanceof Text &&
                        isEqualStr( ((Text)obj1).getValue(), ((Text)obj2).getValue() ) ){
                        list2.remove( iJ );
                        break;
                    }
                }
                if( iJ >= iSize2 ) {
                    System.out.println( "Elemenet not found " + iJ + " vs size " +   // dbg 
                                        iSize2 );  // dbg 
                    return false; // can not find a match Name
                }
            }
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return false;
        }
        return true;
    }

    public static void removeChildren( SOAPElement soapElement ){
        Iterator grandChildren  = soapElement.getChildElements();
        while( grandChildren.hasNext() ){
            Node node = (Node)grandChildren.next();
            node.detachNode(); // remove the node from the tree
            node.recycleNode();
        }
    }


    public static SOAPHeaderElement getHeaderFirstChild( SOAPMessage soapMessage ) {
        try{
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();
            if( soapHeader == null ) return null;

            java.util.Iterator soapHeaderElements = soapHeader.examineAllHeaderElements();
            if( soapHeaderElements.hasNext() ){
                Object obj = soapHeaderElements.next();
                if( obj instanceof SOAPHeaderElement ){
                    return (SOAPHeaderElement) obj;
                } else {
                    return null;
                }
            } 
        } catch( Exception e ){
            e.printStackTrace(System.out);
        }
        return null;
    }

    public static SOAPHeaderElement getHeaderSecondChild( SOAPMessage soapMessage ) {
        try{
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();
            if( soapHeader == null ) return null;

            java.util.Iterator soapHeaderElements = soapHeader.examineAllHeaderElements();
            if( soapHeaderElements.hasNext() ){
                Object obj = soapHeaderElements.next();
                if( soapHeaderElements.hasNext() ){
                    obj = soapHeaderElements.next();
                    if( obj instanceof SOAPHeaderElement ){
                        return (SOAPHeaderElement) obj;
                    } else {
                        return null;
                    }
                }
            } 
        } catch( Exception e ){
            e.printStackTrace(System.out);
        }
        return null;
    }

    public static SOAPHeaderElement getHeaderThirdChild( SOAPMessage soapMessage ) {
        try{
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();
            if( soapHeader == null ) return null;

            java.util.Iterator soapHeaderElements = soapHeader.examineAllHeaderElements();
            if( soapHeaderElements.hasNext() ){
                Object obj = soapHeaderElements.next();  // 1st
                if( soapHeaderElements.hasNext() ){
                    obj = soapHeaderElements.next();    // 2nd
                    if( soapHeaderElements.hasNext() ){
                        obj = soapHeaderElements.next();  // 3rd
                       if( obj instanceof SOAPHeaderElement ){
                           return (SOAPHeaderElement) obj;
                       } else {
                           return null;
                       }
                    }
                }
            } 
        } catch( Exception e ){
            e.printStackTrace(System.out);
        }
        return null;
    }

    /**
     *  This method will remove all the SOAPHeaderElement
     *  The first call, it count the number of SOAPHeaderElement
     *  Then, its SOAPHeaderElement will be 0
     */
    public static boolean countHeaderElements( SOAPMessage soapMessage, int iCount ) {
        int iCnt = 0;
        try{
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();
            if( soapHeader == null ){ return iCount == 0; }

            java.util.Iterator soapHeaderElements = soapHeader.extractAllHeaderElements();
            while( soapHeaderElements.hasNext() ){
                Object obj = soapHeaderElements.next();
                iCnt ++;
            } 
        } catch( Exception e ){
            e.printStackTrace(System.out);
        }

        return iCnt == iCount;
    }

    /**
     *   The method for converting SOAPEnvelope String into an SAAJ SOAPMessage
     *   The soap string was from Axis2 MessageContext
     *   Or directly from SocketUtil/socketSend()
     */
    public static SOAPMessage getSOAPMessage( String soap) throws Exception {
        MessageFactory mf = null;
        MimeHeaders mimeHeaders = new MimeHeaders();
        if( getSaajVersion( soap ) == 12 ){
            System.out.println( "SAAJ12/SOAP11 Message" );
            mf = MessageFactory.newInstance();
        } else{
            System.out.println( "SAAJ13/SOAP12 Message" );
            //mimeHeaders.addHeader( "Content-Type", "application/soap+xml" );
            mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            if( reviewMinor() ){
                // The MessageFactory is nice to handle the SOAP1.1 and SOAP1.2 according to
                // the input SOAPMessage they got
                mf = MessageFactory.newInstance();
            }
        }
        SOAPMessage smsg = mf.createMessage(new MimeHeaders(), 
                                  new ByteArrayInputStream(soap.getBytes("utf8")));
        return smsg;
    }

    /**
     *   The method for converting SOAPEnvelope(Axis2) into an SAAJ SOAPMessage
     *   The soap string was from Axis2 MessageContext
     */
    public static SOAPMessage getSOAPMessage( org.apache.axiom.soap.SOAPEnvelope axis2Envelope,
                                              boolean isSOAP11
                                            ) throws Exception {
        MessageFactory mf = null;
        if( false /* reviewMinor() */){
            String soap = axis2Envelope.toString();
            return getSOAPMessage( soap ); // We got trouble with mf.createMessage( MimeHeaders, InputStream)
        } else {
            SAAJConverter saajConverter =  getSAAJConverter();
            Object obj = saajConverter.toSAAJ(axis2Envelope);
            if( obj instanceof SOAPMessage ){
                // Unlikely
                System.out.println( "Getting an SOAPMessage instead of SOAPEnvelope" );
                return (SOAPMessage)obj;
            } else {
                SOAPEnvelope soapEnvelope = (SOAPEnvelope)obj; // If not SOAPEnvelope, throws Cast Exception

                if( isSOAP11 ){
                    System.out.println( "SAAJ12/SOAP11 Message(Axis2Envelope)" );
                    mf = MessageFactory.newInstance();
                } else{
                    System.out.println( "SAAJ13/SOAP12 Message(Axis2Envelope)" );
                    //mimeHeaders.addHeader( "Content-Type", "application/soap+xml" );
                    mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
                }
                SOAPMessage smsg = mf.createMessage();
                return replaceSoapEnvelope( smsg, soapEnvelope );
            }
        }

    }

    public static SOAPMessage replaceSoapEnvelope( SOAPMessage soapMessage, SOAPEnvelope newEnvelope ) throws Exception{
        SOAPPart     soapPart    = soapMessage.getSOAPPart();
        SOAPEnvelope oldEnvelope = soapPart.getEnvelope();

        // replacing SOAPHeader
        SOAPHeader   soapHeader  = newEnvelope.getHeader();
        if( soapHeader != null ){ // Does it have a SOAPHeader?
            oldEnvelope.addChildElement( soapHeader  );
        } else { // if not, we need to remove the SOAPHeader
            SOAPHeader   soapHeaderOld  = oldEnvelope.getHeader();
            soapHeaderOld.detachNode();
        }

        oldEnvelope.addChildElement( newEnvelope.getBody()   );

        return soapMessage;
    }

    public static SAAJConverter getSAAJConverter() throws Exception {
        SAAJConverterFactory saajConverterFactory = 
               (SAAJConverterFactory)FactoryRegistry.getFactory(SAAJConverterFactory.class);
        SAAJConverter saajConverter =  saajConverterFactory.getSAAJConverter(); 
        return saajConverter;
    }


    // This is not supposed to be called. Since getContent() of AttachmentPart 
    // is specificly definded the "text/plain" and "text/xml"
    public static String getString(java.io.InputStream iais) throws Exception{
        StringBuffer strBuf = new StringBuffer( "" );

        int iByte = 0;
        System.out.print("getString() get ");
        while( ( iByte = iais.read() ) != -1 ){
            printHex( (char)iByte );
            strBuf.append( (char)iByte);
        }
        System.out.println();
        iais.close();  // Do not know what will happen  when we close it()
        return strBuf.toString();
    }

    public static void printHex( char aChar ){
        int high = (aChar & 0x0f0) >> 4;
        int low  = (aChar & 0x00f);
        String[] aStrHex = {"0", "1", "2",
                            "3", "4", "5",
                            "6", "7", "8",
                            "9", "A", "B",
                            "C", "D", "E",
                            "F"
                           };
        System.out.print( aStrHex[ high] + aStrHex[low]+ "," );
    }

    // There are some minor issues in the SAAJ which
    // we may want to review them in Pyxis or when we are available.
    // Turn on this flag will make they appear for reviewing
    public static boolean reviewMinor(){
        return false;
    }
}

