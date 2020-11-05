/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jaxws23.transport.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.DestinationRegistry;
import org.apache.cxf.transport.http.DestinationRegistryImpl;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.apache.cxf.transport.http.HTTPConduitFactory;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.transport.http.URLConnectionHTTPConduit;
import org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduitFactory;
import org.apache.cxf.transport.http.asyncclient.AsyncHttpTransportFactory;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.addressing.EndpointReferenceType;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxws.client.injection.ServiceRefObjectFactory;
import com.ibm.ws.jaxws.security.JaxWsSecurityConfigurationService;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

/**
 * LibertyHTTPTransportFactory provides Liberty extension for CXF internal HTTPTransportFactory, it provides the extra functions below :
 * a. Enable auto redirect function while loading WSDL file, as WebSphere full profile will send a redirect response while accessing WSDL with ?wsdl
 * b. create our LibertyHTTPConduit so that we can set the TCCL when run the handleResponseInternal asynchronously
 */
public class LibertyAsyncHTTPTransportFactory extends AsyncHttpTransportFactory {
    
    private static final TraceComponent tc = Tr.register(LibertyAsyncHTTPTransportFactory.class);

    private static final QName CXF_TRANSPORT_URI_RESOLVER_QNAME = new QName("http://cxf.apache.org", "TransportURIResolver");
    private static final AtomicReference<AtomicServiceReference<JaxWsSecurityConfigurationService>> securityConfigSR = new AtomicReference<AtomicServiceReference<JaxWsSecurityConfigurationService>>();
    /**
     * @param destinationRegistry
     */
    public LibertyAsyncHTTPTransportFactory() {
        super();
    }


    /**
     * set the auto-redirect to true
     */
    @Override
    public Conduit getConduit(
                              EndpointInfo endpointInfo,
                              EndpointReferenceType target,
                              Bus bus) throws IOException {

        //following are copied from the super class.
        //Spring configure the conduit.  
        
        Tr.info(tc, "@TJJ we in the LibertyHTTPTransportFactory.getConduit() looking to get a copy of the HTTPConduit ");
        
       
        LibertyAysncHTTPConduit conduit = new LibertyAysncHTTPConduit(bus, endpointInfo, target, bus.getExtension(AsyncHTTPConduitFactory.class));

        String address = conduit.getAddress();
        if (address != null && address.indexOf('?') != -1) {
            address = address.substring(0, address.indexOf('?'));
        }
        HTTPConduitConfigurer c1 = bus.getExtension(HTTPConduitConfigurer.class);
        if (c1 != null) {
            c1.configure(conduit.getBeanName(), address, conduit);
        }
        configure(bus, conduit, conduit.getBeanName(), address);
        conduit.finalizeConfig();
        //copy end.

        //open the auto redirect when load wsdl, and close auto redirect when process soap message in default.
        //users can open the auto redirect for soap message with ibm-ws-bnd.xml
        if (conduit != null) {
            HTTPClientPolicy clientPolicy = conduit.getClient();

            clientPolicy.setAutoRedirect(CXF_TRANSPORT_URI_RESOLVER_QNAME.equals(endpointInfo.getName()));
        }

        // Set defaultSSLConfig for this HTTP Conduit, in case it is needed when retrieve WSDL from HTTPS URL
        AtomicServiceReference<JaxWsSecurityConfigurationService> secConfigSR = securityConfigSR.get();
        JaxWsSecurityConfigurationService securityConfigService = secConfigSR == null ? null : secConfigSR.getService();
        if (null != securityConfigService) {
            // set null values for sslRef and certAlias so the default one will be used
            securityConfigService.configClientSSL(conduit, null, null);
        }

        if (c1 != null) {
            c1.configure(conduit.getBeanName(), address, conduit);
        }
        configure(bus, conduit, conduit.getBeanName(), address);
        conduit.finalizeConfig();
        
        return conduit;
    }

    /**
     * Set the security configuration service
     * 
     * @param securityConfigService the securityConfigService to set
     */
    public static void setSecurityConfigService(AtomicServiceReference<JaxWsSecurityConfigurationService> serviceRefer) {
        securityConfigSR.set(serviceRefer);
    }

}
