/*
 * 1.3, 1/4/07
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
package saaj.client.util;


import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Binding;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

/**
 * Tests Dispatch<SOAPMessage> client and a Provider<SOAPMessage> service.
 * The client and service interaction tests various xml and attachment scenarios
 *
 */
public class SOAPMessage12Helper extends SAAJHelper {

    static IAppServer server = QueryDefaultNode.defaultAppServer;
    static String endpointUrl  = "http://" + server.getMachine().getHostname() +
                                 ":" + server.getPortMap().get(Ports.WC_defaulthost) + // Default port
                                 // ":19080" +                     // For tcpmon
                                 "/soapmessagefvt/SOAPMessage12Service";
                                // "/soapmessagefvt/services/SOAPMessage12Service";

    private static QName serviceQName = new QName("http://saaj.server/soapmessage", "SOAPMessage12Service");
    private static QName portQName    = new QName("http://saaj.server/soapmessage", "ProviderPortType");
    
    /**
     * Sends an SOAPMessage containing xml data and raw attachments to the web service.  
     * Receives a response containing xml data and the same raw attachments.
     */
    
    public static SOAPMessage send( SOAPMessage request) throws Exception{
        return send( request, true );
    }

    
    public static SOAPMessage send( SOAPMessage request, boolean bPrint) throws Exception{
        SOAPMessage response = null;
        try{       
            // Create the dispatch
            Dispatch<SOAPMessage> dispatch = createDispatch();
            
            if( bPrint){
                System.out.println("Request 12 Message:");
                request.writeTo(System.out);
                System.out.println( );
            } else {
                System.out.println("Request 12 Message");
                System.out.println( );
            }
            
            // Dispatch
            System.out.println(">> Invoking SOAPMessage12ProviderDispatch");
            response = dispatch.invoke(request);

            System.out.println(">> Response 12 [" + response.toString() + "]");
            response.writeTo(System.out);
            System.out.println( );
            
        }catch(Exception e){
            e.printStackTrace(System.out);
            throw e;
        }
        return response;
    }

    public static SOAPMessage send( SOAPMessage soapMessage,
                                            Class clsException,
                                            String strID
                                          ) throws Exception {
        try{
            return send( soapMessage );
        } catch( Exception e ) {
            if( e instanceof javax.xml.ws.soap.SOAPFaultException ){
                SOAPFault soapFault = ((javax.xml.ws.soap.SOAPFaultException )e ).getFault();
                MessageFactory  messageFactory  = SAAJHelper.getMessageFactory12(); 
                     // 1.2 need to specify the version
                SOAPMessage     soapMsg     = messageFactory.createMessage();
                SOAPBody        soapBody    = soapMsg .getSOAPBody();
                soapBody.addChildElement( (SOAPElement)soapFault);
                soapMsg.writeTo( System.out );
                System.out.println( );
                return soapMsg;
            }
            if( clsException.isInstance(e ) ){
                String strStack = getStackTraceString( e );
                if( strStack.indexOf( strID ) >= 0){
                    return null;
                }
            }
            throw e;
        }
    }
    
    /**
     * Sends an SOAPMessage containing xml data and mtom attachment.  
     * Receives a response containing xml data and the mtom attachment.
     */
    public static SOAPMessage sendAttachmentMTOM( SOAPMessage request ) throws Exception{
        // TODO: This needs to have special handling in SOAPMessag12Provider
        SOAPMessage response = null;
        try{       
            // Create the dispatch
            Dispatch<SOAPMessage> dispatch = createDispatch();
            
            // Must indicated that this is a JAX-WS MTOM Dispatch
            Binding binding = dispatch.getBinding();
            SOAPBinding soapBinding = (SOAPBinding) binding;
            soapBinding.setMTOMEnabled(true);
            
            System.out.println("Request 12 MTOM Message:");
            request.writeTo(System.out);
            
            // Dispatch
            System.out.println(">> Invoking SOAPMessage12ProviderDispatch");
            response = dispatch.invoke(request);

            System.out.println(">> Response 12 MTOM [" + response.toString() + "]");
            response.writeTo(System.out);
            
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
        return response;
    }
    
    /**
     * @return
     * @throws Exception
     */
    private static Dispatch<SOAPMessage> createDispatch() throws Exception {
        Service svc = Service.create(serviceQName);
        svc.addPort(portQName, null, endpointUrl); // Second one is the binding name
        Dispatch<SOAPMessage> dispatch = svc.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);
        return dispatch;
    }
    
}
