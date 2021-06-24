/*
 * 1.1, 12/7/06
 * Copyright 2004,2005 The Apache Software Foundation.
 * Copyright 2006 International Business Machines Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package saaj.server.provider;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import com.ibm.ws.websvcs.jaxws.server.SOAPMessageFvt;

@ServiceMode(value=Service.Mode.MESSAGE)
@BindingType(SOAPBinding.SOAP11HTTP_MTOM_BINDING)
@WebServiceProvider(targetNamespace="http://fvt.saaj",
                    serviceName="SOAPMessage12Service", portName="SOAPMessage12Port",
                    wsdlLocation="WEB-INF/wsdl/SOAPMessage12Service.wsdl")
public class SOAPMessage12Provider implements Provider<SOAPMessage> {
      
    // request for the context to be injected
    @Resource

    public SOAPMessage invoke(SOAPMessage soapMessage) {
        System.out.println("        " );
        System.out.println(">> SOAPMessage12Provider: Request received.");

        SOAPMessage response = null;
        try{
            soapMessage.writeTo( System.out );
            System.out.println();
            response = SOAPMessageFvt.handleTests( soapMessage );
            // Write out the Message
            System.out.println(">> Response being sent by Server:");
            response.writeTo(System.out);
            System.out.println();
            return response;
        }catch(Exception e){
            System.out.println("***ERROR: In SOAPMessage12Provider.invoke: Caught exception " + e);
            e.printStackTrace(System.out);

        }
        return null;
    }
    
    
    /**
     * Count Attachments
     * @param msg
     * @return
     */
    private int countAttachments(SOAPMessage msg) {
        Iterator it = msg.getAttachments();
        int count = 0;
        assert(it != null);
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }
}
