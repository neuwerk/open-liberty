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

import javax.activation.DataHandler;

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
import javax.xml.soap.AttachmentPart;

import javax.xml.namespace.QName;

// XML parser related
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.axiom.attachments.IncomingAttachmentInputStream;


import com.ibm.ws.webservices.engine.components.image.ImageIOFactory;
import com.ibm.ws.webservices.engine.components.image.ImageIO;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

import java.awt.Image;
/**
 * SAAJBase
 * 
 * The utility method common to SAAJ FVT Server and Client
 *
 */

public class SAAJUtil extends SAAJUtilAxis
{

    final static String TYPE_IMAGE1 = "image/gif";
    final static String TYPE_IMAGE2 = "image/jpeg";
    final static String TYPE_IMAGE3 = "image/bmp";
    final static String TYPE_TEXT   = "text/plain";
    final static String TYPE_XML    = "text/xml";
    public static boolean isSaaj13Message( SOAPMessage soapMessage ){
        int iVersion = getSaajVersion( soapMessage );
        return (iVersion == 13 ); //
    }

    public static SOAPMessage createSOAP13MessageFromBodyStr( String strElement )
                  throws Exception {
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        Document doc = newDocument( strElement );
        SOAPBodyElement soapBodyElement = soapBody.addDocument( doc );
        return soapMessage;
    }

    public static boolean isEqual13( SOAPElement soapElement, String strElement) throws Exception{
        SOAPMessage soapMessage        = createSOAP13MessageFromBodyStr( strElement ); 
        SOAPBodyElement soapBodyElement = getBodyFirstChild( soapMessage);
        // The following items can be compared in SAAJ1.2 compatible methods
        boolean retBoolean = isEqual12Elem( soapElement, (SOAPElement)soapBodyElement );
        if( ! retBoolean ){
            System.out.println( );
            System.out.println( "***SOAPMessage of the SOAPBodyElement is not equal" );
            soapMessage.writeTo( System.out );
            System.out.println( );
        }
        return retBoolean;
    }

    public static boolean isEqual13Elem( SOAPElement elem1, SOAPElement elem2 ) {
        return isEqual12Elem( elem1, elem2 );
    }

     public static java.awt.Image getImage( DataHandler dh ) throws Exception{
         return getImage( (Object)dh.getContent() );
     }

     public static java.awt.Image getImage( Object obj ) throws Exception{
         
         if( obj instanceof java.io.InputStream ){
             // //Image IO, need export from runtime
             // ImageIO imageIO = ImageIOFactory.getImageIO();
             // Image   image   = imageIO.loadImage((java.io.InputStream)obj );
             // if( image != null ){
             //     System.out.println( "Get a way to load the Image" );
             //     return image;
             // }

             // String str = getString((java.io.InputStream)obj);
             // System.out.println( "Getting Image from inputStream from '" + str + "'" );
             // getString(new ByteArrayInputStream(str.getBytes("utf8")));
             // // File file = new File( "C:\\WASX\\WFVT\\ws\\code\\websvcs.fvt\\src\\saaj\\client\\util\\greenBlue.GIF" );
             // return javax.imageio.ImageIO.read(new FileInputStream(file));
             java.io.InputStream inputStream = (java.io.InputStream)obj;
             try{
                 inputStream.reset();
             } catch( Exception e ){
                 System.out.println( "reset() not supported" );
             }
             return javax.imageio.ImageIO.read(inputStream);
         }
         if( obj instanceof java.awt.Image ){
             return (java.awt.Image) obj;
         }
         if( obj != null ){
             System.out.println( "SAAJUtil.getImage() has a instance of class: " + 
                                 obj.getClass().getName() );
         } else {
             System.out.println( "SAAJUtil.getImage() has null parameter" );
         }

         return null;
     }

     public static ByteArrayInputStream getByteArrayInputStream( InputStream is ) throws IOException{
         byte[] bytesInput = new byte[0];
         try{
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             int iRead = 0;
             while( (iRead = is.read()) != -1 ){
                 baos.write( iRead );
             }
             baos.flush();
             bytesInput = baos.toByteArray();
             baos.close();
             is.close();
         } catch( Exception e ){
             e.printStackTrace(System.out);
         }

         return new ByteArrayInputStream( bytesInput );
     }

     public static String get10KLongString(){
         StringBuffer st = new StringBuffer( "" );
         String str1     = "abcdefghijklmnopqrst0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-";
         for( int iI = 0; iI < 200; iI ++ ){
             // 200 * 63 > 12600
             st.append( str1 + iI + "-" );
         }
         return st.toString();
     }

     public static void addAttribute( SOAPElement soapElement, int iVersion )throws Exception {
         SOAPFactory soapFactory = SOAPFactory.newInstance( iVersion == 12 ? 
                                                            SOAPConstants.SOAP_1_1_PROTOCOL :
                                                            SOAPConstants.SOAP_1_2_PROTOCOL 
                                                          );
         for( int iI = 0; iI < 51; iI ++ ){
             String strZero = "";
             if( iI < 10 ) strZero ="0";
             String attrLocal  = "attrib" + strZero + iI;
             String attrPrefix = "prefix" + strZero + iI;
             String attrURI    = "http://us.ibm.com/uri" + strZero + iI;
             Name attrName = soapFactory.createName( attrLocal, attrPrefix, attrURI );
             String attrValue  = "value" + strZero + iI;
             soapElement.addAttribute( attrName, attrValue);
         }
     }

     public static boolean verifyAttribute( SOAPElement soapElement, int iVersion )throws Exception {
         SOAPFactory soapFactory = SOAPFactory.newInstance( iVersion == 12 ? 
                                                            SOAPConstants.SOAP_1_1_PROTOCOL :
                                                            SOAPConstants.SOAP_1_2_PROTOCOL 
                                                          );
         for( int iI = 0; iI < 51; iI ++ ){
             String strZero = "";
             if( iI < 10 ) strZero ="0";
             String attrLocal  = "attrib" + strZero + iI;
             String attrPrefix = "prefix" + strZero + iI;
             String attrURI    = "http://us.ibm.com/uri" + strZero +  iI;
             Name attrName = soapFactory.createName( attrLocal, attrPrefix, attrURI );
             String attrValue  = "value" + strZero + iI;
             String attrValue1 = soapElement.getAttributeValue( attrName );
             if( !attrValue.equals( attrValue1 ) ){
                 System.out.println( "Attribute " + attrLocal  + 
                                     " prefix   " + attrPrefix +
                                     " URI      " + attrURI    +
                                     " gets value '" + attrValue1 + 
                                     "' not expected '" + attrValue +
                                     "'"
                                   );
                 return false;
             }
         }
         return true;
     }

     public static int countTempFile( String prefix, String suffix ) throws Exception {
         String strTmpDir = System.getProperty( "java.io.tmpdir" );
         File dir = new File( strTmpDir );
         String[] astr = null;
         try
         {
            astr = dir.list();
         }
         catch( SecurityException e )
         {
            System.out.println( "The directory: " + dir.getAbsolutePath() +
                                " has a securityViolation" );
            e.printStackTrace();
            return 0;
         }

         int count = 0;
         for( int iI = 0; iI < astr.length; iI ++ )
         {
             if( prefix != null ){
                 if( astr[ iI ].startsWith( prefix ) ){
                     if( suffix != null ){
                         if( astr[ iI ].endsWith( suffix ) ){
                             count ++;
                         }
                     } else {
                         count ++;
                     }
                 } else { 
                     astr[ iI ] = "";
                 }

             } else {
                 if( suffix != null ){ 
                     if( astr[ iI ].endsWith( suffix ) ){
                         count ++;
                     }
                 } else {
                     astr [ iI ]= "";
                 }
             }
         }
         System.out.println("In the directory "+strTmpDir + " these matching files are found:");
         for(int i=0; i<astr.length; i++){
             if (astr[i].length() > 0 ){
                 System.out.println( astr[i]);
             }
         }
         return count;
     }

     public static SOAPMessage removeAddressingHeaderElements( SOAPMessage inMsg ){
         try{
             SOAPHeader soapHeader = inMsg.getSOAPHeader();
             if( soapHeader != null ){
                 java.util.Iterator soapHeaderElements = soapHeader.examineAllHeaderElements();
                 int iCount = 0;
                 while( soapHeaderElements.hasNext() ){
                     Object obj = soapHeaderElements.next();
                     if( obj instanceof SOAPHeaderElement ){
                         SOAPHeaderElement soapHeaderElement = (SOAPHeaderElement) obj;
                         Name name = soapHeaderElement.getElementName();
                         String strURI = name.getURI();
                         if( strURI.endsWith( "addressing" ) )
                         {
                             soapHeaderElement.detachNode(); // remove the node from the tree
                             soapHeaderElement.recycleNode();
                         } else{
                             iCount ++;
                         }
                     } else{
                         iCount ++;
                     }
                 } 
                 if( iCount == 0 ){
                     System.out.println( "removeAddressingHeaderElements() no Header Elements at all. removing SOAPHeader" );
                     soapHeader.detachNode(); // remove the node from the tree
                     soapHeader.recycleNode();
                 }
             }
         } catch( Exception e ){
             e.printStackTrace(System.out);
         }
         return inMsg;
     }

     // For dealing with AttachmentPart FVT

     static String strNamespaceURI = "http://austin.ibm.com/attachmentpart";
     static QName  qAttachBodyName = new QName( strNamespaceURI, "attachments",     "attach" );
     static QName  qTypeName       = new QName( strNamespaceURI, "attachment-type", "attach" );
     static QName  qSizeName       = new QName( strNamespaceURI, "wXh",             "attach" );
     static QName  qRealTypeName   = new QName( strNamespaceURI, "real-type",       "attach" );
     static QName  qContentIdName  = new QName( strNamespaceURI, "content-id",      "attach" );

     static int iAttachmentID = 0;

     static public SOAPMessage  addAttachmentParts( SOAPMessage response, SOAPMessage oldMsg ) throws Exception{
         System.out.println( "addResponseAttachmentParts()" );
         Iterator iter = oldMsg.getAttachments();
         while( iter.hasNext() ){
             AttachmentPart attp = (AttachmentPart)iter.next();
             addResponseAttachmentPart( response, attp );
         }
         return response;
     }

     static public SOAPMessage addResponseAttachmentPart( SOAPMessage response,
                                                   AttachmentPart attachmentPart ) throws Exception {
         System.out.println( "addResponseAttachmentPart()" );
         iAttachmentID ++;
         String strID = "AttachmentID_" + iAttachmentID;
         String strContentType = attachmentPart.getContentType();

         AttachmentPart attPart = response.createAttachmentPart( attachmentPart.getContent(),
                                                                 strContentType );
         attPart.setContentId( strID );
         response.addAttachmentPart( attPart );
         SOAPElement soapElement = addAttachElement( response, strID, strContentType );
         if( strContentType.equalsIgnoreCase( TYPE_IMAGE1 ) ||
             strContentType.equalsIgnoreCase( TYPE_IMAGE2 ) || 
             strContentType.equalsIgnoreCase( TYPE_IMAGE3 ) 
           ){
             String[] aStrWXH = attachmentPart.getMimeHeader( "wXh");
             soapElement.addAttribute( qSizeName, aStrWXH[0] );
         } else {
             Object obj           = attachmentPart.getContent();
             String strValue = null;
             if( obj instanceof String ) {
                 strValue = getAscii2CodedString( (String)obj );
             } else if( obj instanceof javax.xml.transform.stream.StreamSource ) {
                 strValue = getString( ((javax.xml.transform.stream.StreamSource)obj).getInputStream() );
             } else {
                 strValue = "ERROR: getContent() on AttachmentPart( text/xml) does not return String or StreamSource";
             }
             soapElement.setValue( strValue );
         }
         return response;
     }

     static SOAPElement addAttachElement( SOAPMessage soapMessage,
                                          String strID,
                                          String strType ) throws Exception{
         SOAPBody soapBody = soapMessage.getSOAPBody();
         Iterator iter = soapBody.getChildElements( qAttachBodyName ); 
         SOAPBodyElement soapBodyElement = null;
         if( iter.hasNext() ){
             soapBodyElement = (SOAPBodyElement)iter.next();
         } else{
             soapBodyElement= soapBody.addBodyElement( qAttachBodyName );
         }
         QName qElementName = new QName( strNamespaceURI, strID, "ibm" );
         SOAPElement soapElement = soapBodyElement.addChildElement( qElementName );
         soapElement.addAttribute( qContentIdName, strID   );
         soapElement.addAttribute( qTypeName     , strType );
         return soapElement;
     }


     // If the string is null or length is 0
     // Then it passed. Otherwise, it's in error
     static public Vector<String> verifyResponseAttachments( SOAPMessage response ) throws Exception {
         Vector<String> vecStr = new Vector<String>();
         SOAPBody soapBody = response.getSOAPBody();
         Iterator iter = soapBody.getChildElements( qAttachBodyName ); 
         SOAPBodyElement soapBodyElement = null;
         if( iter.hasNext() ){
             soapBodyElement = (SOAPBodyElement)iter.next();
             Iterator iter1 = soapBodyElement.getChildElements();
             while( iter1.hasNext() ){
                 SOAPElement soapElement = (SOAPElement)iter1.next();
                 String strValue = soapElement.getAttributeValue( qTypeName );
                 if( strValue.equalsIgnoreCase( TYPE_IMAGE1 ) ||
                     strValue.equalsIgnoreCase( TYPE_IMAGE2 ) || 
                     strValue.equalsIgnoreCase( TYPE_IMAGE3 )  
                   ){
                     System.out.println("*****Verifying Image element");
                     vecStr.add( verifyResponseImage( response, soapElement ));
                 } else if( strValue.equalsIgnoreCase( TYPE_XML ) ){
                     System.out.println("*****Verifying xml element");
                     vecStr.add( verifyResponseXml( response, soapElement ));
                 } else {
                     System.out.println("*****Verifying text element");
                     vecStr.add( verifyResponseString( response, soapElement ));
                 }
             }
         } 

         for( int iI = 0; iI < vecStr.size(); iI ++ ){ 
             String str = vecStr.elementAt( iI );
             if( str != null && str.length() > 0 ){
                 System.out.println( "response AttachmentPart error:" + str );
             }
         }

         return vecStr;
     }

     static String getAscii2CodedString( String str ) throws Exception{
         byte[] bytes = str.getBytes( "UTF8" );
         StringBuffer strBuf = new StringBuffer();
         for( int iI = 0; iI < bytes.length; iI ++ ){
             int iJ = bytes[ iI ];
             if( iJ < 0 ) iJ += 256;
             String strChar = "&#" + iJ + ";";
             strBuf.append( strChar );     
         }
         System.out.println( "transfer '" + str + "' to '" + strBuf + "'" );
         return strBuf.toString();
     }

     static String getCoded2AsciiString( String str )throws Exception{
        // System.out.println("getCoded2AsciiString called on string: "+str);
         if(!str.contains("&#")){
       //    System.out.println("no &# found, skipping conversion ");
             return str;         
         }
         StringBuffer strBuf = new StringBuffer();
         StringTokenizer st = new StringTokenizer( str, ";" );
         while( st.hasMoreTokens() ){
             String strToken = st.nextToken();
             int iIndex = strToken.indexOf( "&#" );
             String strByte = strToken.substring( iIndex + 2 );
             int iByte = Integer.parseInt( strByte );
             strBuf.append( (char) iByte );
         }
         System.out.println( "transfer '" + str + "' to '" + strBuf + "'" );
         return strBuf.toString();
     }

     static String verifyResponseString( SOAPMessage soapMessage, 
                                        SOAPElement soapElement
                                      ) throws Exception{
         QName qElementName = soapElement.getElementQName();
         System.out.println( "Verify Element '" + qElementName.getNamespaceURI() + 
                             "':'" + qElementName.getLocalPart() + "'" );
         String strContentId  = soapElement.getAttributeValue( qContentIdName ); 
         String strTextValue  = soapElement.getValue();
         strTextValue         = getCoded2AsciiString( strTextValue );
         AttachmentPart attp  = getAttachmentPart( soapMessage, strContentId );
         if( attp == null ) return "ERROR: Can not find the AttachmentPart:'" + 
                                   strContentId + "'";
         Object obj           = attp.getContent();
         if( obj instanceof String ) {
             if( strTextValue.equals( (String)obj ) ) return "";
         }
         return "String Attachment not found. expected '" + strTextValue + 
                "' but get '" + obj + "'" ;
     }

     static String verifyResponseImage( SOAPMessage soapMessage, 
                                        SOAPElement soapElement
                                      ) throws Exception{
         QName qElementName = soapElement.getElementQName();
         System.out.println( "Verify Element '" + qElementName.getNamespaceURI() + 
                             "':'" + qElementName.getLocalPart() );
         String strContentId  = soapElement.getAttributeValue( qContentIdName ); 
         AttachmentPart attp  = getAttachmentPart( soapMessage, strContentId );
         if( attp == null ) return "ERROR: Can not find the AttachmentPart:'" + 
                                   strContentId + "'";

         String strSize       = soapElement.getAttributeValue( qSizeName ); 
         int iSize = attp.getSize();
         System.out.println( strContentId + "size is " + iSize + " wXh " + strSize );

         DataHandler dataHandler = attp.getDataHandler();
         if( dataHandler == null ){
             return "Error: " + strContentId + " DataHandler is null unexpectedly";
         }

         Object obj = dataHandler.getContent();
         System.out.println( "DataHandler return content = " +
                             ( obj == null ? " NULL" :
                               obj.getClass().getName() ) );

         java.awt.Image image = null;
         // This is supposed to be java.awt.Image
         if( obj instanceof java.awt.Image ){
             image = (java.awt.Image)obj;
         } else {
             image = getImage( obj ); // do not get Image at this testcase
         }
         if( image == null ){
             String strMsg = "ERROR:" + strContentId + " can not convert back to an Image";
             return strMsg;
         } else {
             // Let's verify if the Image is a right one
             int iWidth  = image.getWidth(null);
             int iHeight = image.getHeight( null );
             String str1 = "" + iWidth + "X" + iHeight;
             if( str1.equals( strSize) ){
                 System.out.println( "Image match size " + str1);
             } else {
                 return (strContentId + " wXh did not get expected size " +
                                              " but get " + str1 );
             }
         }
         return "";
     }

     static String verifyResponseXml( SOAPMessage soapMessage, 
                                      SOAPElement soapElement
                                    ) throws Exception{
         QName qElementName = soapElement.getElementQName();
         System.out.println( "Verify Element '" + qElementName.getNamespaceURI() + 
                             "':'" + qElementName.getLocalPart() );
         String strContentId  = soapElement.getAttributeValue( qContentIdName ); 

         String strTextValue  = soapElement.getValue();
         strTextValue         = getCoded2AsciiString( strTextValue );

         AttachmentPart attp  = getAttachmentPart( soapMessage, strContentId );
         if( attp == null ) return "ERROR: Can not find the AttachmentPart:'" + 
                                   strContentId + "'";

         Object obj           = attp.getContent();
         if( obj instanceof String ) {
             if( strTextValue.equals( (String)obj ) ) return "";
         } else if( obj instanceof javax.xml.transform.stream.StreamSource ) {
             String strValue = getString( ((javax.xml.transform.stream.StreamSource)obj).getInputStream() );
             if( strTextValue.equals( strValue )) return "";
         } else {
             System.out.println( "ERROR: getContent() on AttachmentPart( text/xml) does not return String or StreamSource" );
         }
         return "String Attachment not found. expected '" + strTextValue + 
                "' but get '" + obj + "'" ;
     }

     static AttachmentPart getAttachmentPart( SOAPMessage soapMessage, 
                                              String strContentId ) throws Exception{
         MimeHeaders mhs = new MimeHeaders();
         mhs.addHeader( "content-id", strContentId );
         Iterator iter = soapMessage.getAttachments( mhs );
         if( iter.hasNext() ){
             return (AttachmentPart) iter.next();
         } else{
             System.out.println( "Error: Can not find AttachmentPaet with content-id:'" + strContentId + "'" );
             return null;
         }
     }

     static public void verifyResponseAttachments( SOAPMessage response,
                                                  FVTTestCase testcase ) throws Exception{
         Vector<String> vec = verifyResponseAttachments( response );
         for( int iI = 0; iI < vec.size(); iI ++ ){ 
             String str = vec.elementAt( iI );
             if( str != null && str.length() > 0 ){
                 testcase.fail( "response AttachmentPart error:" + str );
             }
         }
     }
}
