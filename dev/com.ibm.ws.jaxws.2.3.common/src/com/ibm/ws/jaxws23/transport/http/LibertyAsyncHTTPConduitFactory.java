/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.ibm.ws.jaxws23.transport.http;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import org.apache.cxf.Bus;
import org.apache.cxf.buslifecycle.BusLifeCycleListener;
import org.apache.cxf.buslifecycle.BusLifeCycleManager;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.common.util.SystemPropertyAction;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPConduitFactory;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduitFactory;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.addressing.EndpointReferenceType;


/**
 *
 */
@NoJSR250Annotations
public class LibertyAsyncHTTPConduitFactory extends AsyncHTTPConduitFactory {


    public LibertyAsyncHTTPConduitFactory() {
        super();
    }

    public LibertyAsyncHTTPConduitFactory(Map<String, Object> conf) {
        this();
        setProperties(conf);
    }

    public LibertyAsyncHTTPConduitFactory(Bus b) {
        this();
        addListener(b);
        setProperties(b.getProperties());
    }


    @Override
    public HTTPConduit createConduit(Bus bus,
                                     EndpointInfo localInfo,
                                     EndpointReferenceType target) throws IOException {
        if (isShutdown) {
            return null;
        }
        return new LibertyAysncHTTPConduit(bus, localInfo, target, this);
    }


}
