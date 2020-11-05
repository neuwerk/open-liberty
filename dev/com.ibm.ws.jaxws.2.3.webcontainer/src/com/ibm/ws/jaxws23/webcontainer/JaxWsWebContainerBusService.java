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

import java.util.Map;

import org.osgi.service.component.ComponentContext;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxws.bus.LibertyApplicationBusFactory;

public class JaxWsWebContainerBusService {

    private static final TraceComponent tc = Tr.register(
                                                         JaxWsWebContainerBusService.class, null);

    private JaxWsWebContainerBusListener listener;

    protected void activate(ComponentContext cc) {
        // Keeping the Interceptor in bus because if we would like to check by using isWSATPresent()
        // We need consider both jaxws and wsat features dynamic enable and disable situation
        insertListeners();
    }

    protected void deactivate(ComponentContext cc) {
        removeListeners();
    }

    protected void modified(Map<String, Object> newProps) {
    }

    private void insertListeners() {
        if (listener == null) {
            listener = new JaxWsWebContainerBusListener();
        }
        if (LibertyApplicationBusFactory.getInstance() != null) {
            LibertyApplicationBusFactory.getInstance().registerApplicationBusListener(
                                                                                      listener);
        } else {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "goInsertListeners", "NO BUS FACTORY");
        }
    }

    private void removeListeners() {
        if (LibertyApplicationBusFactory.getInstance() != null && listener != null) {
            LibertyApplicationBusFactory.getInstance().unregisterApplicationBusListener(
                                                                                        listener);
        }
    }
}