/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jaxws.bus;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.BusFactory;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.binding.soap.SoapTransportFactory;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.bus.extension.Extension;
import org.apache.cxf.bus.extension.ExtensionManager;
import org.apache.cxf.bus.extension.ExtensionManagerImpl;
import org.apache.cxf.bus.managers.DestinationFactoryManagerImpl;
import org.apache.cxf.buslifecycle.BusLifeCycleListener;
import org.apache.cxf.buslifecycle.BusLifeCycleManager;
import org.apache.cxf.transport.ConduitInitiatorManager;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.transport.http.HTTPConduitFactory;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduitFactory;
import org.apache.cxf.transport.http.asyncclient.AsyncHttpTransportFactory;
import org.apache.cxf.transport.http_jaxws_spi.JAXWSHttpSpiTransportFactory;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.ws.jaxws.metadata.JaxWsModuleMetaData;
import com.ibm.ws.jaxws23.transport.http.LibertyAsyncHTTPConduitFactory;
import com.ibm.ws.jaxws23.transport.http.LibertyAsyncHTTPTransportFactory;
import com.ibm.ws.util.ThreadContextAccessor;

/**
 * LibertyApplicationBusFactory is used to create the bus instance for both server and client side in the server.
 */
public class LibertyApplicationBusFactory extends CXFBusFactory {

    private static final TraceComponent tc = Tr.register(LibertyApplicationBusFactory.class);

    private static final ThreadContextAccessor THREAD_CONTEXT_ACCESSOR = AccessController.doPrivileged(ThreadContextAccessor.getPrivilegedAction());

    private static final LibertyApplicationBusFactory INSTANCE = new LibertyApplicationBusFactory();

    private final List<LibertyApplicationBusListener> listeners = new CopyOnWriteArrayList<LibertyApplicationBusListener>();

    private final List<ExtensionProvider> extensionProviders = new CopyOnWriteArrayList<ExtensionProvider>();

    private final List<Bus> serverScopedBuses = Collections.synchronizedList(new ArrayList<Bus>());

    private final List<Bus> clientScopedBuses = Collections.synchronizedList(new ArrayList<Bus>());

    public static LibertyApplicationBusFactory getInstance() {
        return INSTANCE;
    }

    public LibertyApplicationBus createServerScopedBus(JaxWsModuleMetaData moduleMetaData) {
        ModuleInfo moduleInfo = moduleMetaData.getModuleInfo();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("org.apache.cxf.bus.id", moduleInfo.getName() + "-Server-Bus");
        Map<Class<?>, Object> extensions = new HashMap<Class<?>, Object>();
        extensions.put(ClassLoader.class, moduleMetaData.getAppContextClassLoader());
        extensions.put(JaxWsModuleMetaData.class, moduleMetaData);
        extensions.put(LibertyApplicationBus.Type.class, LibertyApplicationBus.Type.SERVER);

        final ClassLoader moduleClassLoader = moduleInfo.getClassLoader();
        Object origTccl = THREAD_CONTEXT_ACCESSOR.pushContextClassLoaderForUnprivileged(moduleClassLoader);
        try {
            return createBus(extensions, properties, moduleClassLoader);
        } finally {
            THREAD_CONTEXT_ACCESSOR.popContextClassLoaderForUnprivileged(origTccl);
        }
    }

    public LibertyApplicationBus createClientScopedBus(JaxWsModuleMetaData moduleMetaData) {
        ModuleInfo moduleInfo = moduleMetaData.getModuleInfo();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("org.apache.cxf.bus.id", moduleInfo.getName() + "-Client-Bus");
        Map<Class<?>, Object> extensions = new HashMap<Class<?>, Object>();
        extensions.put(ClassLoader.class, moduleMetaData.getAppContextClassLoader());
        extensions.put(JaxWsModuleMetaData.class, moduleMetaData);
        extensions.put(LibertyApplicationBus.Type.class, LibertyApplicationBus.Type.CLIENT);

        final ClassLoader moduleClassLoader = moduleInfo.getClassLoader();
        Object origTccl = THREAD_CONTEXT_ACCESSOR.pushContextClassLoaderForUnprivileged(moduleClassLoader);
        try {
            return createBus(extensions, properties, moduleClassLoader);
        } finally {
            THREAD_CONTEXT_ACCESSOR.popContextClassLoaderForUnprivileged(origTccl);
        }
    }

    @Override
    public Bus createBus(Map<Class<?>, Object> e, Map<String, Object> properties) {
        return createBus(e, properties, THREAD_CONTEXT_ACCESSOR.getContextClassLoader(Thread.currentThread()));
    }

    public LibertyApplicationBus createBus(Map<Class<?>, Object> e, Map<String, Object> properties, ClassLoader classLoader) {

        Bus originalBus = getThreadDefaultBus(false);

        try {
            LibertyApplicationBus bus = new LibertyApplicationBus(e, properties, classLoader);

            //Considering that we have set the default bus in JaxWsService, no need to set default bus
            //Also, it avoids polluting the thread bus.
            //possiblySetDefaultBus(bus);

            /* initialize the bus */
            initializeBus(bus);
            BusLifeCycleManager lifeCycleManager = bus.getExtension(BusLifeCycleManager.class);
            if (lifeCycleManager != null) {
                lifeCycleManager.registerLifeCycleListener(new BusLifeCycleListenerAdapter(bus));
            } else {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Unable to locate LifeCycleManager for the bus " + bus.getId()
                                 + ", postShutdown and preShutDown methods configured in LibertyApplicationBusListener will not be invoked");
                }
            }

            bus.initialize();
            DestinationFactoryManager dfm = bus.getExtension(DestinationFactoryManager.class);
            ConduitInitiatorManager cim = bus.getExtension(org.apache.cxf.transport.ConduitInitiatorManager.class);
            BindingFactoryManager bfm = bus.getExtension(org.apache.cxf.binding.BindingFactoryManager.class);
            SoapTransportFactory stf = bus.getExtension(SoapTransportFactory.class);
  
            if (null == dfm) {
                dfm  = new org.apache.cxf.bus.managers.DestinationFactoryManagerImpl(bus);   
                
            }

            if (null == cim) {
                cim = new org.apache.cxf.bus.managers.ConduitInitiatorManagerImpl(bus);
            }

            if (null == bfm) {
                bfm = new org.apache.cxf.bus.managers.BindingFactoryManagerImpl(bus);
            }
            
            
            
            
            dfm.registerDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/", stf);
            dfm.registerDestinationFactory("http://schemas.xmlsoap.org/soap/", stf);
            dfm.registerDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/http", stf);
            dfm.registerDestinationFactory("http://schemas.xmlsoap.org/soap/http", stf);
            
            for(Iterator<String> i = dfm.getRegisteredDestinationFactoryNames().iterator(); i.hasNext();) {
                Tr.info(tc, "@TJJ dfm registeredDestinationFactoryName: "+ i.next());
                
            }
        
            bus.setExtension(dfm, DestinationFactoryManager.class);
            bus.setExtension(cim, ConduitInitiatorManager.class);
            bus.setExtension(bfm, BindingFactoryManager.class);
            bus.setExtension(stf, SoapTransportFactory.class);


            try {
                Tr.info(tc, "@TJJ getDestinationFactory: "   + dfm.getDestinationFactory("http://schemas.xmlsoap.org/soap/http"));
            } catch (BusException e1) {
                // TODO Auto-generated catch block
                // Do you need FFDC here? Remember FFDC instrumentation and @FFDCIgnore
                e1.printStackTrace();
            }
   
            Tr.info(tc, "@TJJ DFM = " + dfm + " CIM = " + cim + " BFM = " + bfm + " stf = " + stf + " registered DestinationFactory: " + dfm.getDestinationFactoryForUri("http://cxf.apache.org/transports/http") + " what is the HTTPTF" );
            
            return bus;

        } finally {
            setThreadDefaultBus(originalBus);
        }
     
    }

    @Override
    protected void initializeBus(Bus bus) {
        super.initializeBus(bus);

        /**
         * run initializers before bus.initialize()
         */
        for (LibertyApplicationBusListener listener : listeners) {
            listener.preInit(bus);
        }

        /**
         * Add Override Extension provided by ExtensionProvider
         */
        ExtensionManager extensionManager = bus.getExtension(ExtensionManager.class);
        if (extensionManager != null && (extensionManager instanceof ExtensionManagerImpl)) {
            ExtensionManagerImpl managerImpl = (ExtensionManagerImpl) extensionManager;
            for (ExtensionProvider extensionProvider : extensionProviders) {
                Extension extension = extensionProvider.getExtension(bus);
                if (extension != null) {
                    managerImpl.add(extension);
                }
            }
        } else {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, (extensionManager == null ? "Unable to locate extension manager " : "The extension manager is not of type ExtensionManagerImpl")
                             + ", all the extensions from ExtensionProvider are ignored");
            }
        }
    }

    /**
     * register LibertyApplicationBusListener to bus factory, those methods will be invoked with the bus lifecycle
     *
     * @param initializer
     */
    public void registerApplicationBusListener(LibertyApplicationBusListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * unregister LibertyApplicationBusListener from the bus factory
     */
    public void unregisterApplicationBusListener(LibertyApplicationBusListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public List<Bus> getServerScopedBuses() {
        return new ArrayList<Bus>(serverScopedBuses);
    }

    public List<Bus> getClientScopedBuses() {
        return new ArrayList<Bus>(clientScopedBuses);
    }

    /**
     * Provide a new implemented static method for setting the default Bus.
     * The static method from parent class BusFactory also tries to change the thread bus, which is not required.
     * Use BusFactory.class as the synchronized lock due to the signature of the BusFactory.setDefaultBus is
     * public static void synchronized setDefaultBus(Bus bus)
     *
     * @param bus
     */
    public static void setDefaultBus(Bus bus) {
        synchronized (BusFactory.class) {
            if (bus == null) {
                defaultBus = null;
            } else {
                defaultBus = bus;
            }
        }
    }

    public void registerExtensionProvider(ExtensionProvider extensionProvider) {
        if (extensionProvider != null) {
            extensionProviders.add(extensionProvider);
        }
    }

    public void unregisterExtensionProvider(ExtensionProvider extensionProvider) {
        if (extensionProvider != null) {
            extensionProviders.remove(extensionProvider);
        }
    }

    private class BusLifeCycleListenerAdapter implements BusLifeCycleListener {

        private final Bus bus;

        public BusLifeCycleListenerAdapter(Bus bus) {
            this.bus = bus;
        }

        @Override
        public void initComplete() {
            for (LibertyApplicationBusListener listener : listeners) {
                listener.initComplete(bus);
            }
        }

        @Override
        public void postShutdown() {
            try {
                for (LibertyApplicationBusListener listener : listeners) {
                    listener.postShutdown(bus);
                }
            } finally {
                if (!serverScopedBuses.remove(bus)) {
                    clientScopedBuses.remove(bus);
                }
            }
        }

        @Override
        public void preShutdown() {
            for (LibertyApplicationBusListener listener : listeners) {
                listener.preShutdown(bus);
            }
        }
    }
}
