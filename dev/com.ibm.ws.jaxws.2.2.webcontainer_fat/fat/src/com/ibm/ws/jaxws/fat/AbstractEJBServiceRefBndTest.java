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
package com.ibm.ws.jaxws.fat;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import componenttest.annotation.Server;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;
import componenttest.topology.utils.HttpUtils;

public class AbstractEJBServiceRefBndTest {
    private static final int CONN_TIMEOUT = 5;

    @Server("EJBServiceRefBndTestServer")
    public static LibertyServer server = LibertyServerFactory.getLibertyServer("EJBServiceRefBndTestServer");

    @Before
    public void start() throws Exception {
        if (server != null) {
            server.startServer();
        }
    }

    @After
    public void tearDown() throws Exception {
        if (server == null) {
            return;
        }

        if (server.isStarted()) {
            server.stopServer();
        }
    }

    @Test
    public void testEJBServiceRefProperties() throws Exception {

        Map<String, String> echoServicePropertyMap = getServletResponse(getEchoServletURL());
        Map<String, String> helloServicePropertyMap = getServletResponse(getHelloServletURL());

        String echoClientConnectionTimeOut = echoServicePropertyMap.get("client.ConnectionTimeout");
        String echoClientChunkingThreshold = echoServicePropertyMap.get("client.ChunkingThreshold");

        String helloClientConnectionTimeOut = helloServicePropertyMap.get("client.ConnectionTimeout");
        String helloClientChunkingThreshold = helloServicePropertyMap.get("client.ChunkingThreshold");

        assertTrue("The expected client.ConnectionTimeOut should be '1739' for EchoBean, but the actual is '" + echoClientConnectionTimeOut + "'",
                   "1739".equals(echoClientConnectionTimeOut));
        assertTrue("The expected client.ChunkingThreshold should be '2317' for EchoBean, but the actual is '" + echoClientChunkingThreshold + "'",
                   "2317".equals(echoClientChunkingThreshold));

        assertTrue("The expected client.ConnectionTimeOut should be '1122' for HelloBean, but the actual is '" + helloClientConnectionTimeOut + "'",
                   "1122".equals(helloClientConnectionTimeOut));
        assertTrue("The expected client.ChunkingThreshold should not be '3344' for HelloBean, but the actual is '" + helloClientChunkingThreshold + "'",
                   "3344".equals(helloClientChunkingThreshold));
    }

    private Map<String, String> getServletResponse(String servletUrl) throws Exception {
        URL url = new URL(servletUrl);
        HttpURLConnection con = HttpUtils.getHttpConnection(url, HttpURLConnection.HTTP_OK, CONN_TIMEOUT);
        BufferedReader br = HttpUtils.getConnectionStream(con);
        String result = br.readLine();
        String[] keyValues = result.split(",");

        Map<String, String> propertyMap = new HashMap<String, String>();
        for (String keyValue : keyValues) {
            String[] param = keyValue.trim().split("=");
            if (param.length == 2) {
                propertyMap.put(param[0].trim(), param[1].trim());
            }
        }

        return propertyMap;
    }

    protected String getEchoServletURL() {
        return new StringBuilder().append("http://").append(server.getHostname()).append(":").append(server.getHttpDefaultPort()).append("/ejbServiceRefBndClient/EchoServlet").toString();
    }

    protected String getHelloServletURL() {
        return new StringBuilder().append("http://").append(server.getHostname()).append(":").append(server.getHttpDefaultPort()).append("/ejbServiceRefBndClient/HelloServlet").toString();
    }
}
