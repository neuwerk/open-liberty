/**
 * Copyright 2006 International Business Machines Corp.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ibm.ws.websvcs.jaxws.server;

import com.ibm.ws.websvcs.jaxws.server.response.*;

import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;

import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
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

import java.io.*;
import java.net.*;
import java.util.Iterator;
import org.apache.axis2.context.MessageContext;

// import org.apache.axis2.jaxws.registry.FactoryRegistry;
/**
 * SOAPMessageFvt
 * 
 * Similate the MessageReceiver to receive MessageContext for testing the SOAPMessage
 * The SOAPMessage will be parsed, processed by the Handlers and set in the
 * MessageContext. It may have been modified by the handlers.  
 *
 * Currently, the SOAPMessageFvt will not be installed into the product code. Its purpose 
 * is for FVT testings.
 *
 */

public class SOAPMessageFvt extends com.ibm.ws.saaj.SAAJUtil {

    private static SOAPMessageFvt soapMessageFvt = null;

    public static SOAPMessageFvt newInstance(){
        if( soapMessageFvt == null ) soapMessageFvt = new SOAPMessageFvt();
        return soapMessageFvt;
    }

    public SOAPMessageFvt(){
    }


    /**
     * This method will handle SOAPMessage according to 
     * its content of SOAPEnvelope.
     * It will call other methods to handle each SOAPMessage in the future
     */
    public static SOAPMessage handleTests( SOAPMessage inSOAPMsg ) 
           throws Exception {

        MessageContext msgCtx = null;
        if( isSaaj12Message( inSOAPMsg )) {
            String       strBodyText = getBodyFirstChildStr( inSOAPMsg );
            if( strBodyText != null &&
                strBodyText.length() > 0 ) {
                if( strBodyText.indexOf( "Simple12" ) >= 0 ){
                    return Simple12Response.test( msgCtx, inSOAPMsg );
                }
                if( strBodyText.indexOf( "Header12" ) >= 0 ){
                    return Header12Response.test( msgCtx, inSOAPMsg );
                }
                if( strBodyText.indexOf( "Attachment12" ) >= 0 ){
                    return Attachment12Response.test( msgCtx, inSOAPMsg );
                }
                if( strBodyText.indexOf( "xlxp" ) >= 0 ){
                    return XlxpResponse.test( msgCtx, inSOAPMsg );
                }
                // ToDo Other tests
            } 
            SOAPHeaderElement soapHeaderElem = getHeaderFirstChild( inSOAPMsg );
            if( soapHeaderElem != null ){
                String strActor = soapHeaderElem.getActor();
                if( strActor.indexOf( "Header12" ) >= 0 ){
                    return Header12Response.test( msgCtx, inSOAPMsg );
                }
                if( strActor.indexOf( "Attachment12" ) >= 0 ){
                    return Attachment12Response.test( msgCtx, inSOAPMsg );
                }
                if( strActor.indexOf( "Fault12" ) >= 0 ){
                    return Fault12Response.test( msgCtx, inSOAPMsg );
                }
                // ToDo: test header here
            }

        } else  if( isSaaj13Message( inSOAPMsg )) {
            String       strBodyText = getBodyFirstChildStr( inSOAPMsg );
            System.out.println( "SAAJFvt(strBodyText)13:" + strBodyText );
            if( strBodyText != null &&
                strBodyText.length() > 0 ) {
                if( strBodyText.indexOf( "Simple13" ) >= 0 ){
                    return Simple13Response.test( msgCtx, inSOAPMsg );
                }
                if( strBodyText.indexOf( "Header13" ) >= 0 ){
                    return Header13Response.test( msgCtx, inSOAPMsg );
                }
                if( strBodyText.indexOf( "Attachment13" ) >= 0 ){
                    return Attachment13Response.test( msgCtx, inSOAPMsg );
                }
                // ToDo Other tests
            } 
            SOAPHeaderElement soapHeaderElem = getHeaderFirstChild( inSOAPMsg );
            if( soapHeaderElem != null ){
                String strActor = soapHeaderElem.getActor();
                if( strActor.indexOf( "Header13" ) >= 0 ){
                    return Header13Response.test( msgCtx, inSOAPMsg );
                }
                if( strActor.indexOf( "Attachment13" ) >= 0 ){
                    return Attachment13Response.test( msgCtx, inSOAPMsg );
                }
                if( strActor.indexOf( "Fault13" ) >= 0 ){
                    return Fault13Response.test( msgCtx, inSOAPMsg );
                }
                // ToDo: test header here
            }
            return createError13Message( "No corresponding server test to response this SOAPMessage(13)" );
        } else {
            return createError12Message( "ERROR: Can not find the SOAPMessage version. not 1.2 or 1.3(SAAJFvt)"  );
        }

        return createError12Message( "No corresponding server test to response this SOAPMessage(12)" );

    }

}
