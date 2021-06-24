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


import javax.xml.soap.SOAPElementFactory;

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

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
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
/**
 * Simple12Response
 * 
 * response to the\ saaj.ient.test.Simple12Test
 * This will only deal with Body Elements
 *
 */

public class Simple12Response extends com.ibm.ws.saaj.SAAJUtil
{
    /**
     *                                            
     */
    public static  SOAPMessage test( MessageContext msgCtx, SOAPMessage soapMessage ) 
           throws Exception {
        if( isSaaj12Message( soapMessage )){
            SOAPBodyElement secondChild     = getBodySecondChild( soapMessage );
            SOAPElement     firstGrandChild = getBodyFirstGrandChild( soapMessage );

            if( secondChild != null ){
                Name            secondName      = secondChild.getElementName();
                String          str2ndLocalName = secondName.getLocalName();
                if( str2ndLocalName.equals( "SOAPMessage" ) ){
                    return answerSimple12Message4( soapMessage );
                }
                if( str2ndLocalName.equals( "hrefLowCase" ) ){
                    SOAPMessage returnMessage = answerSimple12Message5( soapMessage );
                    return returnMessage;
                }
            }

            if( secondChild     == null &&
                firstGrandChild == null ){
                // answerSimple12Message1 also do answerSimple12Message6
                return answerSimple12Message1( soapMessage );
            } else {                                            
                if( firstGrandChild != null && secondChild != null ){
                    return answerSimple12Message3( soapMessage, secondChild, firstGrandChild ) ;
                }
                if( firstGrandChild == null && secondChild != null ){
                    return answerSimple12Message2( soapMessage, secondChild )  ;
                }
            }
        } else{
            return createError12Message( "The SoapMessage is not a SAAJ 1.2 message"  );
        }
        return createError12Message( "The SoapMessage does not have a correcponding response"  );
    }
                                               
    private static SOAPMessage answerSimple12Message1(SOAPMessage inMsg ) throws Exception{
        Name name = getBodyFirstChildName( inMsg );
        if( name.getLocalName().equals( "Simple12" ) ){
            String strPrefix = name.getPrefix();
            if( strPrefix == null || 
                strPrefix.length() == 0 || 
                strPrefix.indexOf( "axis2ns" ) == 0 || 
                strPrefix.equals( "n12" )){

                String strValue = getBodyFirstChildValue( inMsg );
                if( strValue.equals( "testSimple12:simple12" ) ){
                    return createSimple12Answer1();
                }else  if( strValue.equals( "testSimple12:fault" ) ){
                        return createSimple12Answer6();
                }else  if( strValue.equals( "testSimple12:serverAttribute" ) ){
                        if( reviewMinor() ){
                        //if( true ){
                            String strDescr = inMsg.getContentDescription();
                            String str1 = "test setContentDescription() string";
                            if( !str1.equals( strDescr ) ){
                                return createError12Message( "The soapMessage does not have the contentDescription as expected" +
                                                             "'" + strDescr + "'"
                                                           );
                            }
                        }
                        SOAPMessage soapMessage         = createSimple12Answer1();
                        SOAPBodyElement soapBodyElement = getBodyFirstChild( soapMessage );
                        SOAPFactory soapFactory         = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
                        Name bodyName                   = soapFactory.createName("serverAttribute" );
                        soapBodyElement.addAttribute( bodyName, "created Server Attribute" );

                        soapMessage.setContentDescription( "response setContentDescription() string" );
                        if( soapMessage.saveRequired() ){
                            soapMessage.saveChanges();
                        }
                        return soapMessage;
                } else {
                    return createError12Message( "The SoapMessage does not have 'testSimple12:XXXXX' value"  );
                }
            } else{
                return createError12Message( "The SoapMessage does not have right prefix value"  );
            }
        } else{
            return createError12Message( "The SoapMessage does not have \"Simple12\" BodyElement"  );
        }
    }

    private static SOAPMessage createSimple12Answer1() throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        SOAPFactory soapFactory  = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        Name bodyName            = soapFactory.createName("simple12response", 
                                                          "simple12", 
                                                          "http://www.ibm.com/simple12" );   
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        //Name attrName                   = soapFactory.createName("testattr", "attrprefix", "http://austin.org/austin" );
        //soapBodyElement.addAttribute( attrName, "character_uppercase" );
        soapBodyElement.addTextNode( "simple12 text response" );
        return soapMessage;
    }

    private static SOAPMessage answerSimple12Message4(SOAPMessage inMsg ) throws Exception{
        SOAPMessage ansMsg = answerSimple12Message1( inMsg );
        return createSimple12Answer4( ansMsg );
    }

    private static SOAPMessage createSimple12Answer4(SOAPMessage soapMessage) throws Exception{
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        SOAPFactory soapFactory         = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        Name bodyName                   = soapFactory.createName("SOAPMessage",
                                                                 "server", 
                                                                 "http://w3.ibm.com/austin" );
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( soapMessage.getClass().getName() );

        bodyName                        = soapFactory.createName("SOAPBody",
                                                                 "server", 
                                                                 "http://w3.ibm.com/austin" );
        soapBodyElement                 = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( soapBody.getClass().getName() );

        bodyName                        = soapFactory.createName("SOAPBodyElement",
                                                                 "server", 
                                                                 "http://w3.ibm.com/austin" );
        soapBodyElement                 = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( soapBodyElement.getClass().getName() );

        bodyName                        = soapFactory.createName("SOAPFactory",
                                                                 "server", 
                                                                 "http://w3.ibm.com/austin" );
        soapBodyElement                 = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( soapFactory.getClass().getName() );
        return soapMessage;
    }

    private static SOAPMessage verifySimple12Message2(SOAPMessage inMsg, 
                                                      SOAPBodyElement secondChild ) throws Exception{
        // verify the input SOAPMessage
        String strName = getBodyFirstChildStr( inMsg );
        if( !strName.equals( "Simple12Prefix:Simple12" ) ){
            return createError12Message( "The SoapMessage does not have " +
                                         "\"Simple12Prefix:Simple12\" Bodyelement" );
        }

        String strUri = getBodyFirstChildUri( inMsg );
        if( !strUri.equals( "http://soap.ibm.com/simple12" ) ){
            return createError12Message( "The SoapMessage does not have " +
                                         "\"http://soap.ibm.com/simple12\" BodyElementUti" );
        }

        String strValue = getBodyFirstChildValue( inMsg );
        if( !strValue.equals( "testSimple12 2 bodies" ) ){
            return createError12Message( "The SoapMessage does not have " +
                                         "\"testSimple12 2 bodies\" BodyElementText" );
        }

        if( isEqual12( (SOAPElement)secondChild,  STR_BODY_ELEMENT_1 ) ){
            return null;
        } else {
            return createError12Message( "The second input SOAPBodyElement does not match" );
        }
    }

    private static SOAPMessage answerSimple12Message2(SOAPMessage inMsg, 
                                                      SOAPBodyElement secondChild ) throws Exception{
        SOAPMessage soapMessage = verifySimple12Message2( inMsg, secondChild );
        if( soapMessage == null ){
            soapMessage     = createSimple12Answer2();
        }
        return soapMessage;
    }

    private static SOAPMessage createSimple12Answer2() throws Exception{
        SOAPMessage     soapMessage     = createSimple12Answer1();

        // adding 2nd element from STR_BODY_ELEMENT_2
        SOAPBody        soapBody     = soapMessage .getSOAPBody();
        Document        docElement   = newDocument( STR_BODY_ELEMENT_2 );
        SOAPBodyElement soapBodyElem = soapBody.addDocument( docElement );
        return soapMessage;
    }


    private static SOAPMessage verifySimple12Message3(SOAPMessage inMsg, 
                                                      SOAPBodyElement secondChild,
                                                      SOAPElement     grandChild
                                                      ) throws Exception{
        // verify the input SOAPMessage
        String strName = getBodyFirstChildStr( inMsg );
        if( !strName.equals( "Simple12Prefix:Simple12" ) ){
            return createError12Message( "The SoapMessage does not have " +
                                         "\"Simple12Prefix:Simple12\" Bodyelement" );
        }

        String strUri = getBodyFirstChildUri( inMsg );
        if( !strUri.equals( "http://soap.ibm.com/simple12" ) ){
            return createError12Message( "The SoapMessage does not have " +
                                         "\"http://soap.ibm.com/simple12\" BodyElementUti" );
        }
        // No String text in the first child

        if( isEqual12( (SOAPElement)secondChild,  STR_BODY_ELEMENT_1 ) ){
            // ToDo: vreify the first Grand Child
            Name name = grandChild.getElementName();
            if( name.getPrefix()   .equals( "grand"                 ) &&
                name.getLocalName().equals( "Simple12GrandChild"    ) &&
                name.getURI()      .equals( "urn://checkGrandChild" ) ){
                String strText =  grandChild.getValue();
                if( strText.equals( "The first Grand Child " ) ){
                    return null;
                } else {
                    return createError12Message( "The grand child do not have " + 
                                                 "'The first Grand Child ' it has '" +
                                                 strText + "'" );
                }
            } else{
                return createError12Message( "The grand child is not 'grand:Simple12GrandChild$urn://checkGrandChild'" );
            }
        } else {
            return createError12Message( "The second input SOAPBodyElement does not match" );
        }
    }


    private static SOAPMessage answerSimple12Message3(SOAPMessage inMsg, 
                                                      SOAPBodyElement secondChild,
                                                      SOAPElement     grandChild
                                                      ) throws Exception{
        SOAPMessage soapMessage = verifySimple12Message3( inMsg, secondChild, grandChild );

        if( soapMessage == null ){
            soapMessage     = createSimple12Answer3();
        }
        return soapMessage;
    }

    private static SOAPMessage createSimple12Answer3() throws Exception{
        System.out.println( "In createSimpleAnswer3()" );
        SOAPMessage     soapMessage     = createSimple12Answer2();
        SOAPBodyElement firstChild  = getBodyFirstChild( soapMessage );
        // ToDo: Need to test this piece
        removeChildren( (SOAPElement)firstChild );
        // firstChild.removeContents();

        SOAPElementFactory soapElementFactory = SOAPElementFactory.newInstance();
        SOAPFactory soapFactory  = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        int iI = 0;
        for( ; iI < 4; iI ++ ){ //Add third dozen children
            SOAPElement soapElement1 = soapElementFactory.create(
                                                "Simple12GrandChild" + iI                     
                                                );
            SOAPElement soapElement = firstChild.addChildElement( soapElement1 );
            soapElement.addTextNode( "Grand Child " + iI );
        }
        for( ; iI < 8; iI ++ ){ //Add second dozen children
            SOAPElement soapElement = firstChild.addChildElement( "Simple12GrandChild" + iI                    
                                                                );

            soapElement.addTextNode( "Grand Child " + iI );
        }

        // 0-8 Single
        for( ; iI < 12; iI ++ ){ //Add second dozen children
            // ToDo
            // A bug: If change the sequence. (Create the name and then create the Declaration
            //        The prefix disappear
            firstChild.addNamespaceDeclaration("grand" + iI,
                                               "http://austin.ibm.com/checkGrandChild" + iI  
                                               );

            SOAPElement soapElement = firstChild.addChildElement( "Simple12GrandChild" + iI,
                                                                  "grand" + iI 
                                                                );
            soapElement.addTextNode( "Grand Child " + iI );

        }

        // 9-12 With URI
        for( ; iI < 16; iI ++ ){ //Add a dozen children
            SOAPElement soapElement = firstChild.addChildElement( "Simple12GrandChild" + iI,                    
                                                                  "grand" + iI,                                 
                                                                  "http://austin.ibm.com/checkGrandChild" + iI  
                                                                );

            soapElement.addTextNode( "Grand Child " + iI );
        }
        for( ; iI < 20; iI ++ ){ //Add third dozen children
            Name name  = soapFactory.createName("Simple12GrandChild" + iI,                     
                                                "grand" + iI,                                  
                                                "http://austin.ibm.com/checkGrandChild" + iI   
                                               );
            SOAPElement soapElement = firstChild.addChildElement( name );
            soapElement.addTextNode( "Grand Child " + iI );
        }
        for( ; iI < 24; iI ++ ){ //Add third dozen children
            SOAPElement soapElement1 = soapElementFactory.create(
                                                "Simple12GrandChild" + iI,                     
                                                "grand" + iI,                                  
                                                "http://austin.ibm.com/checkGrandChild" + iI   
                                               );
            SOAPElement soapElement = firstChild.addChildElement( soapElement1 );
            soapElement.addTextNode( "Grand Child " + iI );
        }
        for( ; iI < 28; iI ++ ){ //Add third dozen children
            Name name  = soapFactory.createName("Simple12GrandChild" + iI,                     
                                                "grand" + iI,                                  
                                                "http://austin.ibm.com/checkGrandChild" + iI   
                                               );
            SOAPElement soapElement1 = soapElementFactory.create( name );
            SOAPElement soapElement = firstChild.addChildElement( soapElement1 );
            soapElement.addTextNode( "Grand Child " + iI );
        }
        // 13-28 With URI

        return soapMessage;
    }


    private static SOAPMessage createSimple12Answer6() throws Exception{
        SOAPMessage     soapMessage     = createSimple12Answer1();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();
        soapBody.removeContents();  // remove all the current contents

        // create actor
        SOAPFault soapFault = soapBody.addFault();
        soapFault.setFaultActor( "http://movie.disney.com/MickeyMouse" );

        // create Fault Code through SOAPEnvelope.createName()
        SOAPPart     soapPart     = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        Name         nameFault    = soapEnvelope.createName( "Client", 
                                                             "soapenv",  // an issue but not important
                                                             SOAPConstants.URI_NS_SOAP_ENVELOPE);
        if( reviewMinor() ){
            nameFault    = soapEnvelope.createName( "client", 
                                                    null,  // an issue but not important
                                                    SOAPConstants.URI_NS_SOAP_ENVELOPE);
        }
        soapFault.setFaultCode( nameFault );
        soapFault.setFaultString( "  The SOAPMessage is in fault. John?  " );

        // Add detail and DetailEntry
        Detail       detailFault  = soapFault.addDetail();
        Name         nameDetail   = soapEnvelope.createName( "detailEntryIBM", 
                                                             "prefixIBM",
                                                             "http://www.ibm.com/austin/WebServices");
        DetailEntry  detailEntry  = detailFault.addDetailEntry( nameDetail ); 

        if( detailEntry instanceof javax.xml.soap.SOAPElement ){
            detailEntry.removeNamespaceDeclaration( "prefixIBM" );
            detailEntry.setEncodingStyle( "http://TexasSize" );
            detailEntry.addTextNode( "   Texas Size is giant usually   " );
        } else {
            detailEntry.addTextNode( "Even faultEntry can be in fault" );
        }

        return soapMessage;
    }

    private static SOAPMessage answerSimple12Message5(SOAPMessage inMsg ) throws Exception{
        SOAPMessage soapMessage = answerSimple12Message1( inMsg );

        return soapMessage;
    }

}
