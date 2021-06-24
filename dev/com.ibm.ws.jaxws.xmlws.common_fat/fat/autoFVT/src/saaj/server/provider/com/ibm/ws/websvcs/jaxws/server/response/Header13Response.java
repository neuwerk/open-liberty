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



import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;

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

import java.io.*;
import java.util.Iterator;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Document;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;

import javax.xml.namespace.QName;

/**
 * Header13Response
 * 
 * response to the\ saaj.ient.test.Header13Test
 * This will only deal with Body Elements
 *
 */

public class Header13Response extends com.ibm.ws.saaj.SAAJUtil
{
    /**
     *   msgCtx is null for now                                         
     */
    public static  SOAPMessage test( MessageContext msgCtx, SOAPMessage soapMessage ) 
           throws Exception {
        soapMessage = removeAddressingHeaderElements( soapMessage );
        String       strBodyText = getBodyFirstChildStr( soapMessage );
        if( strBodyText != null ){
            if( strBodyText.indexOf( "Header13_1" ) >= 0 ){
                return answerHeader13Message1( soapMessage );
            }
            if( strBodyText.indexOf( "Header13_4" ) >= 0 ){
                return answerHeader13Message4( soapMessage );
            }
            if( strBodyText.indexOf( "Header13_5" ) >= 0 ){
                return answerHeader13Message5( soapMessage );
            }
        }
        SOAPHeaderElement soapHeaderElem = getHeaderFirstChild( soapMessage );
        if( soapHeaderElem != null ){
            String strActor = soapHeaderElem.getActor();
            if( strActor.equals( "Header13_2" ) ){
                return answerHeader13Message2( soapMessage );
            } else  if( strActor.equals( "Header13_3" ) ){
                return answerHeader13Message3( soapMessage );
            } else  if( strActor.equals( "Header13_6" ) ){
                return answerHeader13Message6( soapMessage );
            } else  if( strActor.equals( "Header13_8" ) ){
                return answerHeader13Message8( soapMessage );
            } else  if( strActor.equals( "Header13_9" ) ){
                return answerHeader13Message9( soapMessage );
            }
            // ToDo: test header here
        }
        return createError13Message( "The SoapMessage does not have a correcponding response"  );
    }

    private static SOAPMessage answerHeader13Message1(SOAPMessage inMsg ) throws Exception{
        Name name = getBodyFirstChildName( inMsg );
        if( name.getLocalName().equals( "Header13_1" ) ){
            SOAPHeaderElement soapHeaderElement = getHeaderFirstChild( inMsg );
            if( soapHeaderElement == null ){ // No Header
                return createHeader13Answer1();
            } else{
                return createError13Message( "The SoapMessage has an Header"  );
            }
        } else{
            return createError13Message( "The SoapMessage does not have \"Header13\" BodyElement"  );
        }
    }

    private static SOAPMessage createHeader13Answer1() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();

        // get default SOAPHeader which is an empty one
        SOAPPart        soapPart        = soapMessage .getSOAPPart();
        SOAPEnvelope    soapEnvelope    = soapPart    .getEnvelope();
        SOAPHeader      soapHeader      = soapEnvelope.getHeader();

        SOAPFactory soapFactory         = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name headerName = soapFactory.createName( "Header13", "good", "http://ibm.org/Texas" );
        SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement( headerName );
        Name attrName   = soapFactory.createName( "attrib13" );
        soapHeaderElement.addAttribute( attrName, "WebServices" );
        soapHeaderElement.setMustUnderstand( true );
        soapHeaderElement.setActor( "Pink Panther" );
        // no BodyElement

        return soapMessage;
    }

    private static SOAPMessage answerHeader13Message2(SOAPMessage inMsg ) throws Exception{
        Name name13 = getBodyFirstChildName( inMsg );
        if( name13 == null ){
            return createError13Message( "The SoapMessage expects some body, " + 
                                         "but gets No SOAPBodyElement" );
        }

        SOAPHeaderElement soapHeaderElem = getHeaderFirstChild(inMsg);
        Name name = soapHeaderElem.getElementName();
        if( name.getLocalName().equals( "Header13_2" ) &&
            name.getURI().equals( "http://northernstar.org/h2")&&
            soapHeaderElem.getRole().equals( "Header13_2" )   &&    // getActor() in 12
            (!soapHeaderElem.getRelay() ) &&                        // getRelay() new
            soapHeaderElem.getValue().equals(
            "ServerSocket gave me a lot of troubles on multi-ethernet-card until I knew it" )
          ){
            SOAPHeaderElement soapHeaderElement = getHeaderSecondChild( inMsg );
            Name name2          = soapHeaderElement.getElementName();
            String strLocalName = name2.getLocalName();
            String strActor     = soapHeaderElement.getActor();
            String strValue     = soapHeaderElement.getValue();
            System.out.println( "HeaderMessage2: LocalName'"  + strLocalName +
                                "' Actor:'" + strActor +
                                "' value:'" + strValue + "'"
                              );
            if( soapHeaderElement.getMustUnderstand() && 
                strLocalName.equals( "Header13_2_1" ) &&
                strValue    .equals( "Header13 2 1" ) &&
                strActor    .equals( SOAPConstants.URI_SOAP_1_2_ROLE_NEXT )){
                return createHeader13Answer2();
            } else{
                return createError13Message( "The SoapMessage has only one Header"  );
            }
        } else{
            return createError13Message( "The SoapMessage does not have \"home:Header13\" HeaderElement"  );
        }
    }

    private static SOAPMessage createHeader13Answer2() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();

        // get default SOAPHeader which is an empty one
        SOAPPart        soapPart        = soapMessage .getSOAPPart();
        SOAPEnvelope    soapEnvelope    = soapPart    .getEnvelope();
        SOAPHeader      soapHeader      = soapEnvelope.getHeader();

        SOAPFactory soapFactory         = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        QName headerQName = new QName( "http://ibm.org/Texas", "Header13", "good" );
        SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement( headerQName );
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

    private static SOAPMessage answerHeader13Message3(SOAPMessage inMsg ) throws Exception{
        // verify SOAPBodyName
        Name name = getBodyFirstChildName( inMsg );
        String strLocalName = name.getLocalName();
        if( !strLocalName.equals( "Header13_3" ) ){
            return createError13Message( "The airplane is not Boeing 747 !(boeing747_1) " );
        }
        String strUri = name.getURI( );
        if( !strUri.equals( "http://AirForceNumberOne.edu.us/Boeing747" ) ){
            return createError13Message( "The airplane is not in web page !(http://AirForceNumberOne.edu.us/Boeing747) " );
        }

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

        name = soapHeaderElem.getElementName();
        if( name.getLocalName().equals( "Header13_3" ) &&
            name.getURI().equals( "http://northernstar.org/h3")&&
            soapHeaderElem.getActor().equals( "Header13_3"    )&&
            soapHeaderElem.getValue().equals( "Header13 3 3"  )&&
            soapHeaderElem.getRelay()                          && // getRelay() 1.3 new
            !soapHeaderElem.getMustUnderstand()
          ){
             return createHeader13Answer3();
        } else{
            return createError13Message( "The SoapMessage does not have a right \"h13:Header13_3\" HeaderElement"  );
        }
    }

    private static SOAPMessage createHeader13Answer3() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();

        // get default SOAPHeader which is an empty one
        SOAPPart        soapPart        = soapMessage .getSOAPPart();
        SOAPEnvelope    soapEnvelope    = soapPart    .getEnvelope();
        SOAPHeader      soapHeader      = soapEnvelope.getHeader();

        SOAPFactory soapFactory         = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name headerName = soapFactory.createName( "Header13_3", "ibmtexas", "http://www.ibm.org/Texas" );
        SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement( headerName );
        Name attrName   = soapFactory.createName( "attrib13_3", "at133", "http://ibm.org/Austin" );
        soapHeaderElement.addAttribute( attrName, "attribute 133" );
        soapHeaderElement.setMustUnderstand( false );
        soapHeaderElement.setActor( "John Weng" );

        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        soapBody.addNamespaceDeclaration( "r13", "http://privateJet.edu.us/Boeing737" );
        SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.addChildElement( "boeing747_1", "r13" );
        soapBodyElement.addTextNode( "It's very expensive for a private Boeing 737" );

        return soapMessage;
    }


    private static SOAPMessage answerHeader13Message4(SOAPMessage inMsg ) throws Exception{
        Name name13 = getBodyFirstChildName( inMsg );
        if( name13 == null ){
            return createError13Message( "The SoapMessage expects some body, " + 
                                         "but gets No SOAPBodyElement" );
        }

        SOAPHeaderElement soapHeaderElem = getHeaderFirstChild(inMsg);
        Name name = soapHeaderElem.getElementName();
        if( name.getLocalName().equals( "Header13_4" ) &&
            soapHeaderElem.getValue().equals( "Header13_4" )
          ){
            SOAPHeaderElement soapHeaderElement = getHeaderSecondChild( inMsg );
            Name name2          = soapHeaderElement.getElementName();
            String strLocalName = name2.getLocalName();
            String strValue     = soapHeaderElement.getValue();
            if( strLocalName.equals( "Header13_4_1" ) &&
                strValue    .equals( "Header13 4 1" ) ){
                return createHeader13Answer4();
            } else{
                return createError13Message( "The SoapMessage has only one Header"  );
            }
        } else{
            return createError13Message( "The SoapMessage does not have \"home:Header13\" HeaderElement"  );
        }
    }

    private static SOAPMessage createHeader13Answer4() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();

        // get default SOAPHeader which is an empty one
        SOAPPart        soapPart        = soapMessage .getSOAPPart();
        SOAPEnvelope    soapEnvelope    = soapPart    .getEnvelope();
        SOAPHeader      soapHeader      = soapEnvelope.getHeader();

        SOAPFactory soapFactory         = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name headerName = soapFactory.createName( "Header13", "good", "http://ibm.org/Texas" );
        SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement( headerName );
        // no BodyElement
        return soapMessage;
    }

    private static SOAPMessage answerHeader13Message5(SOAPMessage inMsg ) throws Exception{
        Name name13 = getBodyFirstChildName( inMsg );
        if( name13 == null ){
            return createError13Message( "The SoapMessage expects some body, " + 
                                         "but gets No SOAPBodyElement" );
        }

        String strURI  = "http://default.ibm.com/default"; // default URI for this testcase
        SOAPHeaderElement soapHeaderElem = getHeaderFirstChild(inMsg);
        Name name = soapHeaderElem.getElementName();
        String strURI1 = name.getURI();
        if( name.getLocalName().equals( "Header13_5" ) &&
            strURI1.equals( strURI )                   &&
            soapHeaderElem.getValue().equals( "Header13_5" )
          ){
            SOAPHeaderElement soapHeaderElement = getHeaderSecondChild( inMsg );
            Name name2          = soapHeaderElement.getElementName();
            String strLocalName = name2.getLocalName();
            String strURI2      = name2.getURI();
            String strValue     = soapHeaderElement.getValue();
            if( !(strLocalName.equals( "Header13_5_1" ) &&
                  strURI2.equals( strURI )              &&
                  strValue    .equals( "Header13 5 1" ) )  ){
                return createError13Message( "The SoapMessage has only one Header"  );
            }
            SOAPHeaderElement soapHeaderElement3 = getHeaderThirdChild( inMsg );
            Name name3           = soapHeaderElement3.getElementName();
            String strURI3       = name3.getURI();
            String strLocalName3 = name3.getLocalName();
            String strValue3     = soapHeaderElement3.getValue();
            if( !(strLocalName3.equals( "Header13_5_2" ) &&
                  strURI3.equals( strURI )              &&
                  strValue3    .equals( "Header13 5 2" ) )  ){
                return createError13Message( "The SoapMessage has only two Header"  );
            }
        } else{
            return createError13Message( "The SoapMessage does not have \"home:Header13\" HeaderElement"  );
        }
        return createHeader13Answer5();
    }

    private static SOAPMessage createHeader13Answer5() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();

        // get default SOAPHeader which is an empty one
        SOAPPart        soapPart        = soapMessage .getSOAPPart();
        SOAPEnvelope    soapEnvelope    = soapPart    .getEnvelope();
        SOAPHeader      soapHeader      = soapEnvelope.getHeader();

        SOAPFactory soapFactory         = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Name headerName = soapFactory.createName( "Header13", "good", "http://ibm.org/Texas" );
        SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement( headerName );
        // no BodyElement
        return soapMessage;
    }

    private static SOAPMessage answerHeader13Message6(SOAPMessage inMsg ) throws Exception{
        // verify SOAPBodyName
        Name name = getBodyFirstChildName( inMsg );
        String strLocalName = name.getLocalName();
        if( !strLocalName.equals( "Header13_6" ) ){
            return createError13Message( "The airplane is not Boeing 747 !(boeing747_1) " );
        }
        String strUri = name.getURI( );
        if( !strUri.equals( "http://AirForceNumberOne.edu.us/Boeing747" ) ){
            return createError13Message( "The airplane is not in web page !(http://AirForceNumberOne.edu.us/Boeing747) " );
        }

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

        name = soapHeaderElem.getElementName();
        if( name.getLocalName().equals( "Header13_6" ) &&
            name.getURI().equals( "http://www.austin.texas/ibm")&&
            soapHeaderElem.getActor().equals( "Header13_6"    )&&
            soapHeaderElem.getValue().equals( "Header13 6 6"  )&&
            !soapHeaderElem.getMustUnderstand()
          ){
             return createHeader13Answer3();
        } else{
            return createError13Message( "The SoapMessage does not have a right \"h13:Header13_6\" HeaderElement"  );
        }
    }

    private static SOAPMessage answerHeader13Message8(SOAPMessage inMsg ) throws Exception{
        // verify SOAPBodyName
        String strValue = getBodyFirstChildValue( inMsg );
        String strLong  = get10KLongString();
        if( strLong.equals( strValue ) ){
             return createHeader13Answer8();

        } else{
            System.out.println( "ERROR: Did not get the expected long string. but get '"  +
                                strValue + "'" );
            return createError13Message( "Did not get the expected long String" );
        }
    }

    private static SOAPMessage createHeader13Answer8() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();

        // get default SOAPHeader which is an empty one
        SOAPPart        soapPart        = soapMessage .getSOAPPart();
        SOAPEnvelope    soapEnvelope    = soapPart    .getEnvelope();

        SOAPFactory soapFactory         = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        soapBody.addNamespaceDeclaration( "long", "http://ut.edu.us/longhorn" );
        SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.addChildElement( "longstring", "long" );
        soapBodyElement.addTextNode( get10KLongString()  );

        return soapMessage;
    }


    private static SOAPMessage answerHeader13Message9(SOAPMessage inMsg ) throws Exception{
        // verify SOAPBodyName
        SOAPBodyElement soapBodyElement = getBodyFirstChild( inMsg );
        if( verifyAttribute( soapBodyElement, 13 ) ){
            return createHeader13Answer9();
        } else {
            return createError13Message( "Did not get the exact 51 attributes. See SystemOut.log"  );
        }
    }

    private static SOAPMessage createHeader13Answer9() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();

        // get default SOAPHeader which is an empty one
        SOAPPart        soapPart        = soapMessage .getSOAPPart();
        SOAPEnvelope    soapEnvelope    = soapPart    .getEnvelope();

        SOAPFactory soapFactory         = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        soapBody.addNamespaceDeclaration( "attribute51", "http://ut.edu.us/longhorn" );
        SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.addChildElement( "attribute51element", "attribute51" );
        soapBodyElement.addTextNode( "I get 51 attributes" );
        addAttribute( soapBodyElement, 13 );

        return soapMessage;
    }

}
