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
package com.ibm.ws.jaxws23.webcontainer;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.extension.Extension;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxws.bus.ExtensionProvider;

/**
 * This class will provider LibertyHTTPTransportFactory extension, which will override the default HTTPTransportFactory extension
 * provided by CXF
 */
public class JaxWsHttpTransportFactoryProvider implements ExtensionProvider {

    //private ConduitConfigurer conduitConfigurer;

    private static final TraceComponent tc = Tr.register(JaxWsHttpTransportFactoryProvider.class);

    @Override
    public Extension getExtension(Bus bus) {

        Tr.info(tc, "@TJJ we in the JaxWsHttpTransportFactoryProvider.getExtension() looking to get a copy of an Extensions ");
        return new Extension(JAXWSHttpTransportFactory.class, JAXWSHttpTransportFactory.class);
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
