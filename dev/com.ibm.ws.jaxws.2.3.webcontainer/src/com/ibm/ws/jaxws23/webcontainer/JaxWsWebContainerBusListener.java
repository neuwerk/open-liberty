/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jaxws23.webcontainer;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.Bus;
import org.apache.cxf.transport.DestinationFactoryManager;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxws.bus.LibertyApplicationBus;
import com.ibm.ws.jaxws.bus.LibertyApplicationBusListener;

public class JaxWsWebContainerBusListener implements LibertyApplicationBusListener {
    private static final TraceComponent tc = Tr.register(
                                                         JaxWsWebContainerBusListener.class, null);

    private final static Set<Bus> busSet = Collections.newSetFromMap(new ConcurrentHashMap<Bus, Boolean>());

    public static Set<Bus> getBusSet() {
        return busSet;
    }

    @Override
    public void preInit(Bus bus) {

    }

    @Override
    public void initComplete(Bus bus) {
        if (bus == null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "initComplete", "bus == NULL");
            return;
        }

        LibertyApplicationBus.Type busType = bus.getExtension(LibertyApplicationBus.Type.class);
        if (busType == null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "initComplete",
                         "Can not determine server type, not Liberty BUS?");
            return;
        } else {
            busSet.add(bus);
        }
        JAXWSHttpTransportFactory stf = bus.getExtension(JAXWSHttpTransportFactory.class);
        if (stf != null) {
            Tr.info(tc, "@TJJ stf is not null");
            DestinationFactoryManager dfm = bus.getExtension(DestinationFactoryManager.class);
            if (dfm != null) {
                Tr.info(tc, "@TJJ dfm is not null");
                dfm.registerDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/", stf);
                dfm.registerDestinationFactory("http://schemas.xmlsoap.org/soap/", stf);
                dfm.registerDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/http", stf);
                dfm.registerDestinationFactory("http://schemas.xmlsoap.org/soap/http", stf);
                bus.setExtension(dfm, DestinationFactoryManager.class);
            }

            bus.setExtension(stf, JAXWSHttpTransportFactory.class);
        }

    }

    @Override
    public void preShutdown(Bus bus) {

    }

    @Override
    public void postShutdown(Bus bus) {
        busSet.remove(bus);
    }

}
