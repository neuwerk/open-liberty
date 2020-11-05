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

import java.io.File;

import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.ibm.ws.jaxws.fat.util.ExplodedShrinkHelper;

import componenttest.annotation.SkipForRepeat;
import componenttest.custom.junit.runner.FATRunner;

@RunWith(FATRunner.class)
@SkipForRepeat("jaxws-2.3")
public class HttpConduitPropertiesTest22 extends AbstractHttpConduitPropertiesTest {

    @BeforeClass
    public static void setup() throws Exception {

        WebArchive app = ExplodedShrinkHelper.explodedDropinApp(server, "httpConduitProperties", "com.ibm.jaxws.properties.echo",
                                                                "com.ibm.jaxws.properties.echo.client",
                                                                "com.ibm.jaxws.properties.hello",
                                                                "com.ibm.jaxws.properties.hello.client",
                                                                "com.ibm.jaxws.properties.interceptor",
                                                                "com.ibm.jaxws.properties.servlet");

        // copy httpConduitProperties and make httpConduitProperties2
        String localLocation = "publish/servers/" + server.getServerName() + "/dropins/";
        File outputFile = new File(localLocation);
        outputFile.mkdirs();
        app.as(ExplodedExporter.class).exportExploded(outputFile, "httpConduitProperties2.war");
        ExplodedShrinkHelper.copyFileToDirectory(server, outputFile, "dropins");

        server.copyFileToLibertyInstallRoot("lib/features", "HttpConduitPropertiesTest/jaxwsTest-2.2.mf");

        server.setServerConfigurationFile("HttpConduitPropertiesTest/jaxwsTest-2.2_server.xml");

        defaultSimpleEchoServiceEndpointAddr = new StringBuilder().append("http://").append(server.getHostname()).append(":").append(server.getHttpDefaultPort()).append("/httpConduitProperties/SimpleEchoService").toString();

        defaultSimpleEchoServiceEndpointAddr2 = new StringBuilder().append("http://").append(server.getHostname()).append(":").append(server.getHttpDefaultPort()).append("/httpConduitProperties2/SimpleEchoService").toString();

        defaultHelloServiceEndpointAddr = new StringBuilder().append("http://").append(server.getHostname()).append(":").append(server.getHttpDefaultPort()).append("/httpConduitProperties/HelloService").toString();

        testServletURL = new StringBuilder().append("http://").append(server.getHostname()).append(":").append(server.getHttpDefaultPort()).append("/httpConduitProperties/TestServlet?target=SimpleEchoService").toString();

        testServletURL2 = new StringBuilder().append("http://").append(server.getHostname()).append(":").append(server.getHttpDefaultPort()).append("/httpConduitProperties2/TestServlet?target=SimpleEchoService").toString();

        testServletURLForHelloService = new StringBuilder().append("http://").append(server.getHostname()).append(":").append(server.getHttpDefaultPort()).append("/httpConduitProperties/TestServlet?target=HelloService").toString();

        receiveTimeoutTestServletURL = new StringBuilder().append("http://").append(server.getHostname()).append(":").append(server.getHttpDefaultPort()).append("/httpConduitProperties/ReceiveTimeoutTestServlet").toString();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        server.deleteFileFromLibertyInstallRoot("lib/features/jaxwsTest-2.2.mf");
    }

}
