/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import org.apache.cxf.Bus;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.URLConnectionHTTPConduit;
import org.apache.cxf.transport.http.URLConnectionHTTPConduit.URLConnectionWrappedOutputStream;
import org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduit;
import org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduitFactory;
import org.apache.cxf.ws.addressing.EndpointReferenceType;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.util.ThreadContextAccessor;

/**
 *
 */
public class  LibertyAysncHTTPConduit extends AsyncHTTPConduit {

    
    private static final TraceComponent tc = Tr.register(LibertyAysncHTTPConduit.class);

    private static final ThreadContextAccessor THREAD_CONTEXT_ACCESSOR = ThreadContextAccessor.getThreadContextAccessor();

    /**
     * @param bus
     * @param endpointInfo
     * @param target
     * @throws IOException 
     */
    public LibertyAysncHTTPConduit(Bus b, EndpointInfo endpointInfo, EndpointReferenceType target,
                                           AsyncHTTPConduitFactory factory) throws IOException {
        super(b, endpointInfo, target, factory);
        Tr.info(tc, "@TJJ we in the LibertyAysncHTTPConduit.getExtension() looking to get a copy of an Extensions ");
    }
    
    @Override
    public String getAddress() {
        return super.getAddress();
    }

    @Override
    public void finalizeConfig() {
        super.finalizeConfig();
    }

    
    @Override
    @FFDCIgnore(URISyntaxException.class)
    protected OutputStream createOutputStream(Message message, 
                                              boolean needToCacheRequest, 
                                              boolean isChunking,
                                              int chunkThreshold) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)message.get(KEY_HTTP_CONNECTION);
        
        if (isChunking && chunkThreshold <= 0) {
            chunkThreshold = 0;
            connection.setChunkedStreamingMode(-1);                    
        }
        try {
            return new LibertyWrappedOutputStream(message, connection,
                                           needToCacheRequest, 
                                           isChunking,
                                           chunkThreshold,
                                           getConduitName());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    protected class LibertyWrappedOutputStream extends URLConnectionWrappedOutputStream {

        protected LibertyWrappedOutputStream(Message outMessage, HttpURLConnection connection, boolean possibleRetransmit, boolean isChunking, int chunkThreshold,
                                             String conduitName) throws URISyntaxException {
            
            super(outMessage, connection, possibleRetransmit, isChunking, chunkThreshold, conduitName);
            Tr.info(tc, "@TJJ we in the LibertyHTTPTransportFactoryProvidery.getExtension() looking to get a copy of an Extensions ");
        }

//        //handleResponse will call handleResponseInternal either synchronously or asynchronously
//        //so if call asynchronously, we set the thread context classloader because liberty executor won't set anything when run the task.
//        @Override
//        protected void handleResponseInternal() throws IOException {
//            if (outMessage == null
//                   || outMessage.getExchange() == null
//                   || outMessage.getExchange().isSynchronous()) {
//                super.handleResponseInternal();
//            } else {
//                ClassLoader oldCl = THREAD_CONTEXT_ACCESSOR.getContextClassLoader(Thread.currentThread());
//                try {
//                    // get the classloader from bus
//                    ClassLoader cl = bus.getExtension(ClassLoader.class);
//                    if (cl != null) {
//                        THREAD_CONTEXT_ACCESSOR.setContextClassLoader(Thread.currentThread(), cl);
//                    }
//                    super.handleResponseInternal();
//                } finally {
//                    THREAD_CONTEXT_ACCESSOR.setContextClassLoader(Thread.currentThread(), oldCl);
//                }
//            }
//        }
//
    }
}
