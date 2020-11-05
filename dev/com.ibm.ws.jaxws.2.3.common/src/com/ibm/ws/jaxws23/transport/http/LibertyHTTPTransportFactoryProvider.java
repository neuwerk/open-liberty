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

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.extension.Extension;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.PropertyUtils;
import org.apache.cxf.configuration.ConfigurationException;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.transport.http.DestinationRegistry;
import org.apache.cxf.transport.http.DestinationRegistryImpl;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.apache.cxf.transport.http.HTTPConduitFactory;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;


import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxws.bus.ExtensionProvider;
import com.ibm.ws.jaxws.bus.LibertyApplicationBus;
import com.ibm.ws.jaxws.metadata.builder.EndpointInfoConfigurator;

/**
 * This class will provider LibertyHTTPTransportFactory extension, which will override the default HTTPTransportFactory extension
 * provided by CXF
 */
public class LibertyHTTPTransportFactoryProvider implements ExtensionProvider {
    
    //private ConduitConfigurer conduitConfigurer;

    private static final TraceComponent tc = Tr.register(LibertyHTTPTransportFactoryProvider.class);
    private static final String DISABLE_DEFAULT_HTTP_TRANSPORT = "org.apache.cxf.osgi.http.transport.disable";

    
    @Override
    public Extension getExtension(Bus bus) {

        Tr.info(tc, "@TJJ we in the LibertyHTTPTransportFactoryProvidery.getExtension() looking to get a copy of an Extensions ");
        return new Extension(LibertyAsyncHTTPTransportFactory.class, HTTPTransportFactory.class);
    }
    
//    public class ConduitConfigurer extends ServiceTracker<Bus, Bus> implements ManagedService {
//        private Map<String, Object> currentConfig;
//
//        public ConduitConfigurer(BundleContext context) {
//            super(context, Bus.class, null);
//        }
//
//        @Override
//        public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
//            this.currentConfig = toMap(properties);
//            LibertyApplicationBus[] buses = (LibertyApplicationBus[])getServices();
//            if (buses == null) {
//                return;
//            }
//            for (Bus bus : buses) {
//                configureConduitFactory(bus);
//            }
//        }
//
//        @Override
//        public Bus addingService(ServiceReference<Bus> reference) {
//            LibertyApplicationBus bus = (LibertyApplicationBus) super.addingService(reference);
//            configureConduitFactory(bus);
//            return bus;
//        }
//
//        private Map<String, Object> toMap(Dictionary<String, ?> properties) {
//            Map<String, Object> props = new HashMap<>();
//            if (properties == null) {
//                return props;
//            }
//            Enumeration<String> keys = properties.keys();
//            while (keys.hasMoreElements()) {
//                String key = keys.nextElement();
//                props.put(key, properties.get(key));
//            }
//            return props;
//        }
//
//        private void configureConduitFactory(Bus bus) {
//            AsyncHTTPConduitFactory conduitFactory = (AsyncHTTPConduitFactory) bus.getExtension(HTTPConduitFactory.class);
//            conduitFactory.update(this.currentConfig);
//        }
//
//
//    }
}
    


