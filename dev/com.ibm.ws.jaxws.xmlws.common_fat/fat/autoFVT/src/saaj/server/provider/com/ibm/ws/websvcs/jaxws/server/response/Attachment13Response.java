/*
 * Copyright 2006 International Business Machines Corp.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.ws.websvcs.jaxws.server.response;


import org.apache.axiom.attachments.IncomingAttachmentInputStream;

import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;

import javax.activation.DataHandler;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.util.Utils;


import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.AttachmentPart;

import java.io.*;
import java.util.Iterator;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Document;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
/**
 * Attachment13Response
 * 
 * response to the\ saaj.ient.test.Attachment13Test
 * This will only deal with Body Elements
 *
 */

public class Attachment13Response extends com.ibm.ws.saaj.SAAJUtil
{
    private static final boolean FORMAL = reviewMinor();

    /**
     *   msgCtx is null for now                                         
     */
    public static  SOAPMessage test( MessageContext msgCtx, SOAPMessage soapMessage ) 
           throws Exception {
        String       strBodyText = getBodyFirstChildStr( soapMessage );
        SOAPHeaderElement soapHeaderElem = getHeaderFirstChild( soapMessage );
        if( soapHeaderElem != null ){
            String strActor = soapHeaderElem.getActor();
            if( strActor.equals( "Attachment13_2" ) ){
                return answerAttachment13Message2( soapMessage );
            } else  if( strActor.equals( "Attachment13_3" ) ){
                return answerAttachment13Message3( soapMessage );
            } else  if( strActor.equals( "Attachment13_4" ) ){
                return answerAttachment13Message4( soapMessage );
            } else  if( strActor.equals( "Attachment13_5" ) ){
                return answerAttachment13Message5( soapMessage );
            } else  if( strActor.equals( "Attachment13_6" ) ){
                return answerAttachment13Message6( soapMessage );
            } else  if( strActor.equals( "Attachment13_7" ) ){
                return answerAttachment13Message7( soapMessage );
            } else  if( strActor.equals( "Attachment13_8" ) ){
                return answerAttachment13Message8( soapMessage );
            } else  if( strActor.equals( "Attachment13_9" ) ){
                return answerAttachment13Message9( soapMessage );
            } else  if( strActor.equals( "Attachment13_10" ) ){
                return answerAttachment13Message10( soapMessage );
            }
            // ToDo: test header here
        }
        // May dup with Attachment13_1
        if( strBodyText != null ){
            if( strBodyText.indexOf( "Attachment13_1" ) >= 0 ){
                return answerAttachment13Message1( soapMessage );
            }
        }
        return createError13Message( "The SoapMessage does not have a correcponding response"  );
    }

    private static SOAPMessage answerAttachment13Message1(SOAPMessage inMsg ) throws Exception{
        Name name       = getBodyFirstChildName( inMsg );
        if( name.getLocalName().equals( "Attachment13_1" ) ){
            Iterator iter5     = inMsg.getAttachments( );
            System.out.println( "inMsg has " + inMsg.countAttachments() + " items" );
            if( iter5.hasNext() ){
                AttachmentPart attachment4 = (AttachmentPart)iter5.next();
                Iterator iterMime = attachment4.getAllMimeHeaders();
                while( iterMime.hasNext() ){
                    MimeHeader mime = (MimeHeader)iterMime.next();
                    System.out.println( "Name :'" + mime.getName() +
                                        "' value: '" + mime.getValue() + "'" );
                }
            } else {
                System.out.println( "Even getAttachments() does not get the AttachmentPart" );
            }

            MimeHeaders mimes1 = new MimeHeaders();
            mimes1.addHeader( "content-type", "text/xml" );
            Iterator iter1     = inMsg.getAttachments( mimes1 );

            MimeHeaders mimes2 = new MimeHeaders();
            mimes2.addHeader( "Content-Id", "<Attachment13SOAPMessage>" ); // IBM SAAJ default
            Iterator iter2     = inMsg.getAttachments( mimes2 );

            MimeHeaders mimes3 = new MimeHeaders();
            mimes3.addHeader( "content-type", "text/xml" );
            mimes3.addHeader( "content-id"  , "<Attachment13SOAPMessage>" );
            mimes3.addHeader( "fvt-source"  , "STR_BODY_ELEMENT_1" );
            Iterator iter3     = inMsg.getAttachments( mimes3 );

            Iterator iter4     = inMsg.getAttachments( );

            // Iter1=iter2=iter3=iter4
            if( iter2.hasNext() ){
				iter1.hasNext();
                AttachmentPart attachment1 = (AttachmentPart)iter1.next();
                AttachmentPart attachment2 = (AttachmentPart)iter2.next();
                //mh AttachmentPart attachment3 = (AttachmentPart)iter3.next();
                //mh AttachmentPart attachment4 = (AttachmentPart)iter4.next();

                boolean b1 = iter1.hasNext();
                boolean b2 = iter2.hasNext();
                //mh boolean b3 = iter3.hasNext();
                //mh boolean b4 = iter4.hasNext();
                if( b1 || b2 /* mh || b3 || b4 */ ){
                    String strError = "Some iterator has extra AttachmentPart: 1:" + b1 +
                                      " 2:" + b2 /* mh + " 3:" + b3 + " 4:" + b4 */;
                    return createError13Message( strError  );
                }
                // The AttachmentPart is supposed to be the same object
                if( (attachment1 != attachment2) /*mh ||
                    (attachment1 != attachment3) || 
                    (attachment1 != attachment4) */ ){ 
                    return createError13Message( "Somehow the AttachmentPart is not the same object"  );
                }
                // verify the AttachmentPart

                // getContent() will be verified in testcase 4 or 5
                Object content1 = attachment1.getContent();
                if( content1 != null ){
                    System.out.println( "getContent() object is supposed to be " + 
                                        "a String of DataContentHandler :'" +
                                        content1.getClass().getName() + "'" );
                } else {
                    System.out.println( "getContent() object is null" );
                }

                int iSize1 = attachment1.getSize();
                System.out.println( "AttachmentPart return size = " + iSize1 +
                                    " Original string length is "   + 
                                    STR_BODY_ELEMENT_1.length() );

                DataHandler dataHandler = attachment1.getDataHandler();
                System.out.println( "DataHandler is " + (dataHandler == null ?
                                     "Error NULL" : dataHandler.getClass().getName()) );

                if( dataHandler != null ){
                    Object obj = dataHandler.getContent();
                    System.out.println( "DataHandler return content = " +
                                        obj.getClass().getName() );
                    // DataHandler will be verified in TestCase4 or 5
                    if( obj instanceof String ){
                        if( !((String)obj).equals( STR_BODY_ELEMENT_1 ) ){
                            return createError13Message( "The string does not match " + 
                                                         "STR_BODY_ELEMENT_1'" + obj + "'"
                                                       );
                        }
                    }
                    System.out.println( "DataHandler write() output" );
                    dataHandler.writeTo( System.out );
                    System.out.println(  );
                }

                // clear the content
                attachment1.clearContent();
                int iSize2 = attachment1.getSize();  // It's supposed to be 0?
                System.out.println( "AttachmentPart return size = " + iSize2 +
                                    " after clearContent()" );

                if( iSize2 > 0 ){
                    Object content2 = attachment1.getContent();
                    System.out.println( "Content object is supposed to be " + 
                                        "null :'" + 
                                        (content2 == null ? "null" : content2.getClass().getName()) );
                }

                // Continue to next section
            } else{
                System.out.println( "inMsg before error" );
                inMsg.writeTo( System.out );
                System.out.println(  );
                return createError13Message( "Iter2 does not have any AttachmentPart"  );
            }


            SOAPHeaderElement soapHeaderElement = getHeaderFirstChild( inMsg );
            if( soapHeaderElement == null ){ // No Header
                return createAttachment13Answer1();
            } else{
                return createError13Message( "The SoapMessage has an Header"  );
            }
        } else{
            return createError13Message( "The SoapMessage does not have \"Attachment13\" BodyElement"  );
        }
    }

    private static SOAPMessage createAttachment13Answer1() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();

        // get default SOAPHeader which is an empty one
        SOAPPart        soapPart        = soapMessage .getSOAPPart();
        SOAPEnvelope    soapEnvelope    = soapPart    .getEnvelope();
        SOAPHeader      soapHeader      = soapEnvelope.getHeader();

        SOAPFactory soapFactory         = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name headerName = soapFactory.createName( "Attachment13", "texas", "http://ibm.org/texas" ); // 德州=texas
        SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement( headerName );
        Name attrName   = soapFactory.createName( "attrib13" );          // attrib13
        soapHeaderElement.addAttribute( attrName, "carton_show" ); // carton show
        soapHeaderElement.setMustUnderstand( false );
        soapHeaderElement.setActor( "pink_panther" ); // Pink Panther

        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        Name            bodyName        = soapFactory.createName( "Attachment13", "texas", "http://ibm.org/texas"); // 德州=texas
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );

        return soapMessage;
    }

    private static SOAPMessage answerAttachment13Message2(SOAPMessage inMsg ) throws Exception{

        SOAPHeaderElement soapHeaderElem = getHeaderFirstChild(inMsg);
        Name name = soapHeaderElem.getElementName();
        if( name.getLocalName()      .equals( "Attachment13_2" ) &&
            soapHeaderElem.getActor().equals( "Attachment13_2" )
          ){
            if( !soapHeaderElem.getValue().equals(
                                 "test double-byte characters '德克薩斯州' Texas" ) ){
                System.out.println( "Testing doouble-byte: \"" +
                                    "test double-byte characters '德克薩斯州' Texas"
                                    + "\"");
                System.out.println( "             But get: \"" +
                                    soapHeaderElem.getValue()
                                    + "\"");
            }
            return checkAttachmentParts2( inMsg);
        } else{
            return createError13Message( "The SoapMessage does not have \"home:Attachment13\" HeaderElement"  );
        }
    }

    private static SOAPMessage checkAttachmentParts2( SOAPMessage soapMessage) throws Exception{

        System.out.println( "**Trying the SOAPMessageWrite() again. See what has happened to IAIS" );
        soapMessage.writeTo( System.out );
        System.out.println( );
        // get All the MimeHeader (s)
        MimeHeaders mimesAll = soapMessage.getMimeHeaders();
        Iterator    iterAll  = mimesAll.getAllHeaders();
        System.out.println( "All Transpoart-specific MimeHeader(s):" );
        int iCountAll = 0;
        while( iterAll.hasNext() ){
           MimeHeader mimeA1 = (MimeHeader)iterAll.next();
           System.out.print( "M" + iCountAll ++ + ":" +
                             mimeA1.getName() + "=" +
                             mimeA1.getValue()+ ";" );
        }
        System.out.println();

        int iSize = soapMessage.countAttachments();

        Iterator iter1 = soapMessage.getAttachments();
        int iSize1 = 0;
        while( iter1.hasNext() ){
            Object obj = iter1.next();
            iSize1 ++;
        }
        if( (iSize != iSize1) || (iSize != 3 ) ){
            return createError13Message( "The size is supposed to be 3 but it's not. " +
                                         " size=" + iSize + 
                                         " size1=" + iSize1 );
        }

        // Debugging
        MimeHeaders mimes2a = new MimeHeaders();
        mimes2a.addHeader( "content-id", "<lowercaseString>"); //lowercaseString
        mimes2a.addHeader( "changed", "changed 1st" );
        mimes2a.addHeader( "changed", "changed 2nd" );
        mimes2a.addHeader( "changed", "changed 3rd" );
        Iterator    iter2All  = mimes2a.getAllHeaders();
        int iCount2 = 0;
        while( iter2All.hasNext() ){
            MimeHeader mime2 = (MimeHeader)iter2All.next();
            System.out.println( "name:" + mime2.getName() + "   Value:" + mime2.getValue() );
        }

        // First AttachmentPart
        MimeHeaders mimes2 = new MimeHeaders();
        mimes2.addHeader( "content-id", "<lowercaseString>"); //lowercaseString
        //mimes2.addHeader( "changed", "change 1" );
        //mimes2.addHeader( "changed", "change 2" );
        mimes2.addHeader( "changed", "change 3" );
        Iterator iter2 = soapMessage.getAttachments( mimes2 );
        if( iter2.hasNext() ){
            AttachmentPart attach2 = (AttachmentPart)iter2.next();
            String strId2 = attach2.getContentId();
            if( !strId2.equals( "<lowercaseString>" ) ){
                return createError13Message( "Supposed to get ID 'lowercaseString" +
                                             "' but get '" + strId2 + "'" );
            }
            //String[] aStrMatch2 = new String[] { "OughtToBeKept", "mustKeep"};
            String[] aStrMatch2 = new String[] { "<oughttobekept>", "<mustkeep>"};
            Iterator iter2a = attach2.getMatchingMimeHeaders( aStrMatch2 );
            boolean bOught = false;
            boolean bMust  = false;
            while( iter2a.hasNext() ){
                MimeHeader mime2 = (MimeHeader)iter2a.next();
                if( mime2.getName() .equalsIgnoreCase("OughtToBeKept") &&
                    mime2.getValue().equals          ("MimeHeader0") ){
                    bOught = true;
                }
                if( mime2.getName() .equalsIgnoreCase("mustKeep"  ) &&
                    mime2.getValue().equals          ("MimeHeader") ){
                    bMust = true;
                }
            }
            if( !( bOught && bMust ) ){
                return createError13Message( "Does not have BOTH OughtToBeKept and mustKeep MimeHeader(s)" );
            }
            // defect 382628
            String[] astr2 = attach2.getMimeHeader( "changed" );
            if( astr2.length != 3 ){
                String strErr2 = "Supposed to get 3 'changed' value but get " +
                                                 astr2.length;
                if( reviewMinor()) {
                    return createError12Message( strErr2 );
                } else {
                    System.out.println( strErr2 );
                }
            }
            if( reviewMinor() ){
                for( int iI = 0; iI < astr2.length; iI ++ ){
                    if( !(astr2[ iI ].equals( "change " + iI ) )){
                        return createError13Message( "Supposed to get 'change " + iI + 
                                                     "' but get '" + astr2[ iI ] + "'" );
                    }
                }
            }
        } else {
            /* mh
            return createError13Message( "Does not get the multi 'changed' Attachment(lowercaseString'" );
            */
        }

        MimeHeaders mimes3 = new MimeHeaders();
        mimes3.addHeader( "content-id", "<Attach13Message>");
        Iterator iter3 = soapMessage.getAttachments( mimes3 );
        if( iter3.hasNext() ){
            AttachmentPart attach3 = (AttachmentPart)iter3.next();
            String[] aStrKey3 = new String[]{  "content-id",
                                               "content-type",
                                               "Content-Id",
                                               "mimenew", // mimeNew
                                               "headsize"     // HeadSize
                                            };
            Iterator iter3b = attach3.getMatchingMimeHeaders( aStrKey3 );
            while( iter3b.hasNext() ){ 
                MimeHeader mime3 = (MimeHeader)iter3b.next();
                String strKey3   = mime3.getName();
                String strValue3 = mime3.getValue();
                boolean bRight = false;
                if( strKey3.equalsIgnoreCase( "Content-Id" )){
                    bRight = strValue3.equals( "<Attach13Message>" );
                } else 
                if( strKey3.equalsIgnoreCase( "Content-Type" )){
                    bRight = strValue3.equals( "text/plain"   );  
                } else 
                if( strKey3.equalsIgnoreCase( "mimeNew" )){
                    bRight = strValue3.equals( "New Mime" );
                } else 
                if( strKey3.equalsIgnoreCase( "HeadSize" )){
                    bRight = strValue3.equals( "SizeA" );
                }
                if( ! bRight ){ // something wrong with getMatchingMimeHeaders()
                    return createError13Message( "Error on getMatchingMimeHeaders(3). see name:'" +
                                                 strKey3 + "' value:'" + strValue3 + "'" );
                }
            }

            Iterator iter3a = attach3.getNonMatchingMimeHeaders( aStrKey3 );
            while( iter3a.hasNext() ){ 
                MimeHeader mime3 = (MimeHeader)iter3a.next();
                String strKey3   = mime3.getName();
                String strValue3 = mime3.getValue();
                boolean bRight = false;
                if( strKey3.equalsIgnoreCase( "NotIncluded" )){
                    bRight = strValue3.equals( "getNonMatchingMimeHeaders" );
                } else
                if( strKey3.equalsIgnoreCase( "Content-Transfer-Encoding" )){
                    bRight = strValue3.equals( "binary" );
                } 
                // for( int iI = 0; iI < aStrKey3.length; iI ++ ){
                //     if( strKey3.equalsIgnoreCase( aStrKet3[ iI ] ) ){
                //         bRight = false;
                //     }
                // }
                if( ! bRight ){ // something wrong with getMatchingMimeHeaders()
                    return createError13Message( "Error on getNonMatchingMimeHeaders(3). see name:'" +
                                                 strKey3 + "' value:'" + strValue3 + "'" );
                }
            }


            // String[] aStrKeyNull = new String[]{ "mimeremoved"};  // mimeRemoved

        } else {
            return createError13Message( "Does not get the 'Attach13Message'" );
        }

        MimeHeaders mimes4 = new MimeHeaders();
        mimes4.addHeader( "content-id", "<numericCharacters>");
        Iterator iter4 = soapMessage.getAttachments( mimes4 );
        if( iter4.hasNext() ){
            AttachmentPart attach4 = (AttachmentPart)iter4.next();
            String[] aStrKey4 = new String[]{  "content-id",
                                               "content-type",
                                               "Content-Id",
                                               "nothingchanged" // NothingChanged
                                            };
            Iterator iter4b = attach4.getMatchingMimeHeaders( aStrKey4 );
            while( iter4b.hasNext() ){ 
                MimeHeader mime4 = (MimeHeader)iter4b.next();
                String strKey4   = mime4.getName();
                String strValue4 = mime4.getValue();
                boolean bRight = false;
                if( strKey4.equalsIgnoreCase( "Content-Id" )){
                    bRight = strValue4.equals( "<numericCharacters>" );
                } else 
                if( strKey4.equalsIgnoreCase( "Content-Type" )){
                    bRight = strValue4.equals( "text/plain" );
                } else 
                if( strKey4.equalsIgnoreCase( "NothingChanged" )){
                    bRight = strValue4.equals( "Hopefully!" );
                }
                if( ! bRight ){ // something wrong with getMatchingMimeHeaders()
                    return createError13Message( "Error on getMatchingMimeHeaders(4). see name:'" +
                                                 strKey4 + "' value:'" + strValue4 + "'" );
                }
            }

            Iterator iter4a = attach4.getNonMatchingMimeHeaders( aStrKey4 );
            while( iter4a.hasNext() ){ 
                MimeHeader mime4 = (MimeHeader)iter4a.next();
                String strKey4   = mime4.getName();
                String strValue4 = mime4.getValue();
                boolean bRight = false;
                if( strKey4.equalsIgnoreCase( "austin_TX" )){
                    bRight = strValue4.equals( "State Capital of Texas" );
                } else
                if( strKey4.equalsIgnoreCase( "Content-Transfer-Encoding" )){
                    bRight = strValue4.equals( "binary" );
                } 
                // for( int iI = 0; iI < aStrKey4.length; iI ++ ){
                //     if( strKey4.equalsIgnoreCase( aStrKet4[ iI ] ) ){
                //         bRight = false;
                //     }
                // }
                if( ! bRight ){ // something wrong with getMatchingMimeHeaders()
                    return createError13Message( "Error on getNonMatchingMimeHeaders(4). see name:'" +
                                                 strKey4 + "' value:'" + strValue4 + "'" );
                }
            }

            // ToDo: Compare getContentDescription
        } else {
            return createError13Message( "Does not get the multi 'changed' Attachment(lowercaseString'" );
        }


        return createAttachment13Answer2( );
    }

    private static SOAPMessage createAttachment13Answer2() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();

        // get default SOAPHeader which is an empty one
        SOAPPart        soapPart        = soapMessage .getSOAPPart();
        SOAPEnvelope    soapEnvelope    = soapPart    .getEnvelope();
        SOAPHeader      soapHeader      = soapEnvelope.getHeader();

        SOAPFactory soapFactory         = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name headerName = soapFactory.createName( "Attachment13", "good", "http://ibm.org/Texas" );
        SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement( headerName );
        // Changing the Attribute according to defect 382965 
        Name attrName   = soapFactory.createName( "attrib13", "fix382965", "http://ibm.org/Austin" );
        if( reviewMinor() ){
            attrName = soapFactory.createName( "attrib13", "", "http://ibm.org/Austin" );
        }
        soapHeaderElement.addAttribute( attrName, "WebServices" );
        soapHeaderElement.setMustUnderstand( true );
        soapHeaderElement.setActor( "Pink Panther" );
        // no BodyElement

        return soapMessage;
    }

    private static SOAPMessage answerAttachment13Message3(SOAPMessage inMsg ) throws Exception{

        SOAPHeader soapHeader = inMsg.getSOAPHeader();
        Iterator iter      = soapHeader.extractAllHeaderElements(); // This ougth to have one items
        Iterator iterEmpty = soapHeader.extractAllHeaderElements(); // This has no item.

        if( iterEmpty.hasNext() ){
            return createError13Message( "After extractHeaderElements(), " + 
                                         "the SOAPHeader ought not to have any SOAPHeaderElement." );
        }

        SOAPHeaderElement soapHeaderElem = null;
        if( iter.hasNext()){
            soapHeaderElem = (SOAPHeaderElement)iter.next();
        } else {
            return createError13Message( "No SOAPHeaderElement at all?" );
        }

        if( iter.hasNext()){
            return createError13Message( "Two SOAPHeaderElement. Expect only one" );
        }

        Name name = soapHeaderElem.getElementName();
        if( name.getLocalName().equals( "Attachment13_3" ) &&
            name.getURI().equals( "http://www.ibm.com/pcsupport")&&
            soapHeaderElem.getActor().equals( "Attachment13_3"    )&&
            soapHeaderElem.getValue().equals( "test multiple AttachmentPart"  )&&
            !soapHeaderElem.getMustUnderstand()
          ){

            int iSize = inMsg.countAttachments();
            if(  iSize != 2 ){
                return createError13Message( "It supposed to have 2 AttacgmentParts but get" +
                                             iSize + " items" );
            }

            String[] aStrImages =  new String[] { "<gif4>", "<bmpKaraoke>" };
            SOAPMessage errorMsg = checkImages( inMsg.getAttachments(),
                                                aStrImages );
            if( errorMsg != null ) return errorMsg;

            return createAttachment13AnswerCommon("Attachment13_3", "attrib13_3");
        } else{
            return createError13Message( "The SoapMessage does not have a right Attachment13_3 HeaderElement"  );
        }
    }

    private static SOAPMessage createAttachment13AnswerCommon( String strHeader,
                                                               String strAttr
                                                             ) throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();

        // get default SOAPHeader which is an empty one
        SOAPPart        soapPart        = soapMessage .getSOAPPart();
        SOAPEnvelope    soapEnvelope    = soapPart    .getEnvelope();
        SOAPHeader      soapHeader      = soapEnvelope.getHeader();

        SOAPFactory soapFactory         = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name headerName = soapFactory.createName( strHeader, "ibmtexas", "http://www.ibm.org/Texas" );
        SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement( headerName );
        Name attrName   = soapFactory.createName( strAttr, "at123", "http://ibm.org/Austin" );
        soapHeaderElement.addAttribute( attrName, "attribute 123" );
        soapHeaderElement.setMustUnderstand( false );
        soapHeaderElement.setActor( "John Weng" );

        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        soapBody.addNamespaceDeclaration( "r13", "http://privateJet.edu.us/Boeing737" );
        SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.addChildElement( "boeing747_1", "r13" );
        soapBodyElement.addTextNode( "It's very expensive for a private Boeing 737" );

        return soapMessage;
    }


    // verify getContent()
    private static SOAPMessage answerAttachment13Message4(SOAPMessage inMsg ) throws Exception{

        SOAPHeaderElement soapHeaderElem = getHeaderFirstChild(inMsg);
        Name name = soapHeaderElem.getElementName();
        if( name.getLocalName()      .equals( "Attachment13_4"           ) &&
            soapHeaderElem.getActor().equals( "Attachment13_4"           ) &&
            soapHeaderElem.getValue().equals( "testing in Austin, Texas" ) ){
            SOAPMessage response = checkAttachmentParts4( inMsg);
            Iterator iter = inMsg.getAttachments();
            if( iter.hasNext() ){
                AttachmentPart attp = (AttachmentPart)iter.next();
                addResponseAttachmentPart( response, attp );
            }
            return response;
        } else{
            return createError13Message( "The SoapMessage does not have \"home:Attachment13\" HeaderElement"  );
        }
    }

    // verify getContent()
    private static SOAPMessage checkAttachmentParts4( SOAPMessage soapMessage) throws Exception{

        int iSize = soapMessage.countAttachments();

        Iterator iter1 = soapMessage.getAttachments();
        int iSize1 = 0;
        while( iter1.hasNext() ){
            Object obj = iter1.next();
            iSize1 ++;
        }
        if( (iSize != iSize1) || (iSize != 5 ) ){
            return createError13Message( "The size is supposed to be 5 but it's not. " +
                                         " size=" + iSize + 
                                         " size1=" + iSize1 );
        }

        MimeHeaders mimes2 = new MimeHeaders();
        mimes2.addHeader( "content-id", "<lowercaseString>"); //lowercaseString
        Iterator iter2 = soapMessage.getAttachments( mimes2 );
        if( iter2.hasNext() ){
            AttachmentPart attach2 = (AttachmentPart)iter2.next();
            Object obj = attach2.getContent();
            String str2 = "";
            if( obj instanceof String ){ // This is a must according to the Document
                str2 = (String) obj;
                if( !str2.equals( "abcdefghijklmnopqrstuvwxyz" ) ){
                    return createError13Message( "Does not get the string "+
                                                 "'abcdefghijklmnopqrstuvwxyz' but get '" +
                                                 str2 + "'" );
                }
            } else{
                String strErr = "ERROR: getContent() on lowercaseString is expected to get a String back but get back a " +
                                (obj == null ? "null" : obj.getClass().getName());
                //if( FORMAL){
                    return createError13Message( strErr );                                                 
                //} else{
                //    System.out.println( strErr );
                //}
            }

            String strId2 = attach2.getContentId();
            if( !strId2.equals( "<lowercaseString>" ) ){
                return createError13Message( "Supposed to get ID 'lowercaseString" +
                                             "' but get '" + strId2 + "'" );
            }

        } else {
            return createError13Message( "Does not get the Attachment'lowercaseString'" );
        }

        MimeHeaders mimes3 = new MimeHeaders();
        mimes3.addHeader( "content-id", "<Attachment13SOAPMessage>"); //XML file
        Iterator iter3 = soapMessage.getAttachments( mimes3 );
        if( iter3.hasNext() ){
            AttachmentPart attach3 = (AttachmentPart)iter3.next();
            Object obj = attach3.getContent();
            if( !(obj instanceof javax.xml.transform.stream.StreamSource) ){ // This is a must according to the Document
                if( FORMAL ){
                    System.out.println( "Attachment13SOAPMessage:" + obj );
                    String strErr = "ERROR: getContent() on Attachment13SOAPMessage is expected to get " +
                                    "a javax.xml.transform.stream.StreamSource. But gets back a " + 
                                    (obj == null ? "null" : obj.getClass().getName());
                    return createError13Message( strErr );                                                 
                } else if( !(obj instanceof String )){
                    String strErr = "ERROR: getContent() on Attachment13SOAPMessage is expected to get " +
                                    "a javax.xml.transform.stream.StreamSource. Or for now, a String. " + 
                                    "But gets back a " + 
                                    (obj == null ? "null" : obj.getClass().getName());
                    return createError13Message( strErr );                                                 
                }
            }

        } else {
            return createError13Message( "Does not get the Attachment'Attachment13SOAPMessage'" );
        }

        MimeHeaders mimes4 = new MimeHeaders();
        mimes4.addHeader( "content-id", "<image3>"); //XML file
        Iterator iter4 = soapMessage.getAttachments( mimes4 );
        if( iter4.hasNext() ){
            AttachmentPart attach4 = (AttachmentPart)iter4.next();
            Object obj = attach4.getContent();
            if( !(obj instanceof java.awt.Image) ){ // This is a must according to the Document
                String strErr = "ERROR: getContent() on image3 is expected to get " +
                                "a javax.awt.Image. But gets back a " + 
                                (obj == null ? "null" : obj.getClass().getName());
                if( FORMAL){
                    return createError13Message( strErr );                                                 
                } else{
                    System.out.println( strErr );
                }
            } 
            SOAPMessage soapErr = checkImage( attach4, "image3", null );
            if( soapErr != null ) return soapErr;

        } else {
            return createError13Message( "Does not get the Attachment 'image3'" );
        }

        MimeHeaders mimes5 = new MimeHeaders();
        mimes5.addHeader( "content-id", "<gif4>"); //XML file
        Iterator iter5 = soapMessage.getAttachments( mimes5 );
        if( iter5.hasNext() ){
            AttachmentPart attach5 = (AttachmentPart)iter5.next();
            Object obj = attach5.getContent();
            if( !(obj instanceof java.awt.Image )){ // This is a must according to the Document
                String strErr = "ERROR: getContent() on gif4 is expected to get " +
                                "a javax.awt.Image. But gets back a " + 
                                (obj == null ? "null" : obj.getClass().getName());
                if( FORMAL){
                    return createError13Message( strErr );                                                 
                } else{
                    System.out.println( strErr );
                }
            } 
            SOAPMessage soapErr = checkImage( attach5, "gif4", null );
            if( soapErr != null ) return soapErr;

        } else {
            return createError13Message( "Does not get the Attachment 'gif3'" );
        }

        MimeHeaders mimes6 = new MimeHeaders();
        mimes6.addHeader( "content-id", "<bmp4>"); //XML file
        Iterator iter6 = soapMessage.getAttachments( mimes6 );
        if( iter6.hasNext() ){
            AttachmentPart attach6 = (AttachmentPart)iter6.next();
            Object obj = attach6.getContent();
            if( !(obj instanceof java.awt.Image )){ // This is a must according to the Document
                String strErr = "ERROR: getContent() on bmp4 is expected to get " +
                                "a javax.awt.Image. But gets back a " + 
                                (obj == null ? "null" : obj.getClass().getName());
                if( FORMAL){
                    return createError13Message( strErr );                                                 
                } else{
                    System.out.println( strErr );
                }
            }
            SOAPMessage soapErr = checkImage( attach6, "bmp4", null );
            if( soapErr != null ) return soapErr;

        } else {
            return createError13Message( "Does not get the Attachment 'bmp4'" );
        }

        return createAttachment13Answer2( ); // This is on purpose
    }

    // verify getDataHandler
    private static SOAPMessage answerAttachment13Message5(SOAPMessage inMsg ) throws Exception{

        SOAPHeaderElement soapHeaderElem = getHeaderFirstChild(inMsg);
        Name name = soapHeaderElem.getElementName();
        if( name.getLocalName()      .equals( "Attachment13_5"           ) &&
            soapHeaderElem.getActor().equals( "Attachment13_5"           ) &&
            soapHeaderElem.getValue().equals( "testing in Austin, Texas" ) ){
            return checkAttachmentParts5( inMsg);
        } else{
            return createError13Message( "The SoapMessage does not have \"home:Attachment13\" HeaderElement"  );
        }
    }

    // verify getDataHandler
    private static SOAPMessage checkAttachmentParts5( SOAPMessage soapMessage) throws Exception{
        // trigger the lazy initialization
        System.out.println( "**Trying the SOAPMessageWrite() again. See what has happened to IAIS" );
        soapMessage.writeTo( System.out );
        System.out.println( );

        SOAPMessage response = checkAttachmentParts4( soapMessage );
        addAttachmentParts( response, soapMessage ); // it's OK even with Error Message
        return response;
    }


    private static SOAPMessage answerAttachment13Message6(SOAPMessage inMsg ) throws Exception{

        SOAPHeader soapHeader = inMsg.getSOAPHeader();

        int iSize = inMsg.countAttachments();
        if(  iSize != 1 ){
            // Test failed
            return createError13Message( "It supposed to have 1 AttacgmentParts but get" +
                                         iSize + " items" );
        }

        String[] aStrImages =  new String[] { "<gif6>" };
        SOAPMessage errorMsg = checkImages( inMsg.getAttachments(),
                                            aStrImages );
        if( errorMsg != null ) return errorMsg;

        return createAttachment13AnswerCommon("Attachment13_6", "attrib13_6" );
    }

    private static SOAPMessage answerAttachment13Message7(SOAPMessage inMsg ) throws Exception{

        SOAPHeader soapHeader = inMsg.getSOAPHeader();

        int iSize = inMsg.countAttachments();
        if(  iSize != 1 ){
            // Test failed
            return createError13Message( "It supposed to have 1 AttacgmentParts but get" +
                                         iSize + " items" );
        }

        String[] aStrImages =  new String[] { "<bmpKaraoke7>" };
        SOAPMessage errorMsg = checkImages( inMsg.getAttachments(),
                                            aStrImages );
        if( errorMsg != null ) return errorMsg;

        return createAttachment13AnswerCommon("Attachment13_7", "attrib13_7" );
    }


    private static SOAPMessage answerAttachment13Message8(SOAPMessage inMsg ) throws Exception{

        SOAPHeader soapHeader = inMsg.getSOAPHeader();
                                  
        int iSize = inMsg.countAttachments();
        if(  iSize != 2 ){
            // Test failed
            return createError13Message( "It supposed to have 2 AttacgmentParts but get" +
                                         iSize + " items" );
        }

        String[] aStrImages =  new String[] { "<gif8>", "<bmpKaraoke>" };
        SOAPMessage errorMsg = checkImages( inMsg.getAttachments(),
                                            aStrImages );
        if( errorMsg != null ) return errorMsg;

        return createAttachment13AnswerCommon("Attachment13_8", "attrib13_8" );
    }

    private static SOAPMessage answerAttachment13Message9(SOAPMessage inMsg ) throws Exception{

        SOAPHeader soapHeader = inMsg.getSOAPHeader();
                                  
        int iSize = inMsg.countAttachments();
        if(  iSize != 2 ){
            // Test failed
            return createError13Message( "It supposed to have 2 AttacgmentParts but get" +
                                         iSize + " items" );
        }

        String[] aStrImages =  new String[] { "<image9>", "<bmpKaraoke>" };
        SOAPMessage errorMsg = checkImages( inMsg.getAttachments(),
                                            aStrImages );
        if( errorMsg != null ) return errorMsg;

        return createAttachment13AnswerCommon("Attachment13_9", "attrib13_9" );
    }

    private static SOAPMessage answerAttachment13Message10(SOAPMessage inMsg ) throws Exception{

        SOAPHeader soapHeader = inMsg.getSOAPHeader();
                                  
        int iSize = inMsg.countAttachments();
        if(  iSize != 2 ){
            // Test failed
            return createError13Message( "It supposed to have 2 AttacgmentParts but get" +
                                         iSize + " items" );
        }

        String[] aStrImages =  new String[] { "<image10>", "<bmpKaraoke>" };
        SOAPMessage errorMsg = checkImages( inMsg.getAttachments(),
                                            aStrImages );
        if( errorMsg != null ) return errorMsg;

        SOAPMessage response = createAttachment13AnswerCommon("Attachment13_10", "attrib13_10" );
        addAttachmentParts( response, inMsg ); // it's OK even with Error Message
        return response;
    }

    static class MYCounter{
        int _iCount = 0;
        void setCount( int iCount ){
            _iCount = iCount;
        }
        void increaseCount( ){
            _iCount ++ ;
        }
        int getCount( ){
            return _iCount;
        }
    }

    static SOAPMessage checkImages(Iterator iter1,
                                   String[] aStr
                                  ) throws Exception{
        MYCounter myCounter = new MYCounter();
        while( iter1.hasNext() ){
            int iCount = myCounter.getCount();
            AttachmentPart attach1 = (AttachmentPart)iter1.next();
            String strID = attach1.getContentId();

            for( int iI = 0; iI < aStr.length; iI ++ ){
                if( strID.equals( aStr[ iI ] ) ){
                    SOAPMessage errorMsg = checkImage( attach1, strID, myCounter );
                    if( errorMsg != null ) return errorMsg;
                }
            }

            if( myCounter.getCount() <= iCount ){
                return createError13Message( "Did not get expected IDs but gets '" +
                                             strID + "'" );
            }
        }
        return null;
    }

    static SOAPMessage checkImage( AttachmentPart attach1, String strID,
                                   MYCounter counter ) throws Exception{
        int iSize = attach1.getSize();
        System.out.println( strID + "size is " + iSize );
        boolean bMimeHeaderWorking = true;
        String[] aStr1 = attach1.getMimeHeader( "wXh");
        if( aStr1 == null || aStr1.length == 0 ){  //MH specific
            bMimeHeaderWorking = false;
            aStr1 = new String[] { "210X80", "233X153",
                      "220X90", "2304X1728"
            };
        }
        DataHandler dataHandler1 = attach1.getDataHandler();
        if( dataHandler1 == null ){
            return createError13Message(  strID + " DataHandler is null unexpectedly" );
        }

        Object obj1 = dataHandler1.getContent();
        System.out.println( "DataHandler return content = " +
                            ( obj1 == null ? " NULL" :
                              obj1.getClass().getName() ) );
        java.awt.Image image = null;
        // This is supposed to be java.awt.Image
        if( obj1 instanceof java.awt.Image ){
            image = (java.awt.Image)obj1;
        } else {
            image = getImage( obj1 ); // do not get Image at this testcase
        }
        if( image == null ){
            String strMsg = "ERROR:" + strID + " can not convert back to an Image";
            return createError13Message( strMsg );
        } else {
            // Let's check if the Image is a right one
            int iWidth  = image.getWidth(null);
            int iHeight = image.getHeight( null );
            String str1 = "" + iWidth + "X" + iHeight;
            boolean bMatch = false;
            for( int iI = 0; iI < aStr1.length; iI ++ ){
                if( str1.equals( aStr1[ iI ] ) ){
                    if( !bMimeHeaderWorking ){
                        attach1.addMimeHeader( "wXh", str1 );
                    }
                    bMatch = true;
                    System.out.println( "Image match size " + str1);
                }
            }
            if( ! bMatch ){
                return createError13Message( strID + " wXh did not get expected size" +
                                             " but get " + str1 );
            }
        }
        if( iSize < 10000 ){
            System.out.println( "DataHandler " + strID + " write() output" );
            dataHandler1.writeTo( System.out );
            System.out.println( );
        } else {
            System.out.println( "DataHandler " + strID + " too large to write() output" );
        }
        if( counter != null  ) counter.increaseCount();
        return null;
    }

}

