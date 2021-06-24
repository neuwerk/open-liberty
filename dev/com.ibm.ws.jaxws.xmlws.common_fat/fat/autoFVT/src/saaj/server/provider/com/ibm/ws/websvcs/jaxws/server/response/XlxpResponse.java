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
 * XlxpResponse
 * 
 * response to the\ saaj.ient.test.XlxpTest
 * This will only deal with Body Elements
 *
 */

public class XlxpResponse extends com.ibm.ws.saaj.SAAJUtil
{

    /**
     *                                            
     */
    public static  SOAPMessage test( MessageContext msgCtx, SOAPMessage soapMessage ) 
           throws Exception {
        if( isSaaj12Message( soapMessage )){
            return answerXlxpMessage( soapMessage );
        } else{
            return createError12Message( "The SoapMessage is not a SAAJ 1.2 message"  );
        }
        // return createError12Message( "The SoapMessage does not have a correcponding response"  );
    }
                                               
    private static SOAPMessage answerXlxpMessage(SOAPMessage inMsg ) throws Exception{
        Name name = getBodyFirstChildName( inMsg );
        if( ! (name.getLocalName().equals( "xlxp" )) ){
            return createError12Message( "The SoapMessage does not have " + 
                                         "\"xlxp\" BodyElement"  );
        }
        String strValue = getBodyFirstChildValue( inMsg );
        if( !strValue.equals( "xlxpClientParser is IBM Parser" ) ){
            return createError12Message( "The SoapMessage does not have" +
                                         " \"xlxpClientParser is IBM Parser\""+
                                         " Body Text"  );
        }
        String strMSG = "xlxp parser found";
        try{
            Class cls = Class.forName( "javax.xml.stream.XMLInputFactory" );
            Class[] clses = new Class[]{};
            java.lang.reflect.Method method = cls.getMethod( "newInstance", clses );
            /* Let's see if it's IBM XLXP parser */
            Object factory = method.invoke( null, new Object[]{} ); 
            String strClass = factory.getClass().getName();
            if( strClass.indexOf("com.ibm.xml" ) < 0 ){
                return createError12Message( "Server does not have IBM XLXP Parser." + 
                                            " It gets\"" + strClass + "\""  );
            }
        } catch( Exception e ){
            strMSG = "xlxp parser not found but it due to the WebService Application running environment";
        }
        return createXlxpAnswer( strMSG );
    }

    private static SOAPMessage createXlxpAnswer( String strMsg) throws Exception{
        // SOAP 1.1
        MessageFactory  messageFactory  = MessageFactory.newInstance(); 
             // 1.2 need to specify the version
        SOAPMessage     soapMessage     = messageFactory.createMessage();
        SOAPBody        soapBody        = soapMessage .getSOAPBody();

        SOAPFactory soapFactory  = SOAPFactory.newInstance();
        Name bodyName            = soapFactory.createName("xlxp", 
                                                          "IBMxlxp", 
                                                          "http://www.ibm.com/Xlxp" );   
        SOAPBodyElement soapBodyElement = soapBody.addBodyElement( bodyName );
        soapBodyElement.addTextNode( strMsg );
        return soapMessage;
    }

}
