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

import com.ibm.ws.saaj.FaultData13;

import javax.xml.namespace.QName;

/**
 * Fault13Response
 * 
 * response to the soap.test.fault12.SOAPFault12Test
 * This will only deal with Body Elements
 *
 */

public class Fault13Response extends com.ibm.ws.saaj.SAAJUtil
{
    /**
     *   msgCtx is null for now                                         
     */
    public static  SOAPMessage test( MessageContext msgCtx, SOAPMessage soapMessage ) 
           throws Exception {
        FaultData13 faultData = null;
        try{
            faultData = new FaultData13( soapMessage );
        } catch( Exception e ){
            e.printStackTrace( System.out );
            return createError13Message( "The SoapMessage can not be read by FaultData13" );
        }

        SOAPMessage response = null;
        try{
            response = faultData.createFaultMessage();
        } catch( Exception e ){
            e.printStackTrace( System.out );
            return createError13Message( "The SoapMessage can not create responding SOAPFault message." );
        }

        return response;
    }

}
