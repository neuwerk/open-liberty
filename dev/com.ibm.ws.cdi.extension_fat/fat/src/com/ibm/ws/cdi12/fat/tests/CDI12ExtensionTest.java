/*******************************************************************************
 * Copyright (c) 2014, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.cdi12.fat.tests;

import java.io.File;

import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.websphere.simplicity.log.Log;
import com.ibm.ws.fat.util.BuildShrinkWrap;

import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.RepeatTestFilter;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.HttpUtils;

/**
 * Test the runtime extension to function correctly
 */
@RunWith(FATRunner.class)
public class CDI12ExtensionTest {

    public static final String SERVER_NAME = "cdi12RuntimeExtensionServer";
    public static final String INSTALL_USERBUNDLE_JAVAX = "cdi.helloworld.extension";
    public static final String INSTALL_USERBUNDLE_JAKARTA = "cdi.helloworld.extension-jakarta";
    public static final String INSTALL_USERFEATURE_JAVAX = "cdi.helloworld.extension-1.0";
    public static final String INSTALL_USERFEATURE_JAKARTA = "cdi.helloworld.extension-3.0";

    public static final String EXPOSE_INTERNAL_CDI_EXTENSION_API_FEATURE_JAVAX = "cdi.internals-1.0";
    public static final String EXPOSE_INTERNAL_CDI_EXTENSION_API_FEATURE_JAKARTA = "cdi.internals-3.0";

    @Server(SERVER_NAME)
    public static LibertyServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        JavaArchive multipleWarEmbeddedJar = ShrinkWrap.create(JavaArchive.class,"multipleWarEmbeddedJar.jar")
                        .addClass("com.ibm.ws.cdi.lib.MyEjb")
                        .add(new FileAsset(new File("test-applications/multipleWarEmbeddedJar.jar/resources/META-INF/beans.xml")), "/META-INF/beans.xml");

        WebArchive multipleWarNoBeans = ShrinkWrap.create(WebArchive.class, "multipleWarNoBeans.war")
                        .addClass("test.multipleWarNoBeans.TestServlet");

        WebArchive multipleWar = ShrinkWrap.create(WebArchive.class, "multipleWar1.war")
                        .addClass("test.multipleWar1.TestServlet")
                        .addClass("test.multipleWar1.MyBean")
                        .add(new FileAsset(new File("test-applications/multipleWar1.war/resources/WEB-INF/ejb-jar.xml")), "/WEB-INF/ejb-jar.xml")
                        .add(new FileAsset(new File("test-applications/multipleWar1.war/resources/WEB-INF/beans.xml")), "/WEB-INF/beans.xml")
                        .addAsLibrary(multipleWarEmbeddedJar);

        EnterpriseArchive multipleWars = ShrinkWrap.create(EnterpriseArchive.class,"multipleWars2.ear")
                        .add(new FileAsset(new File("test-applications/multipleWars2.ear/resources/META-INF/application.xml")), "/META-INF/application.xml")
                        .addAsModule(multipleWar)
                        .addAsModule(multipleWarNoBeans);

        WebArchive helloWorldExtensionTest = ShrinkWrap.create(WebArchive.class, "helloWorldExtensionTest.war")
                        .addClass("cdi12.helloworld.extension.test.HelloWorldExtensionTestServlet")
                        .addClass("cdi12.helloworld.extension.test.HelloWorldExtensionBean")
                        .add(new FileAsset(new File("test-applications/helloWorldExtensionTest.war/resources/WEB-INF/beans.xml")), "/WEB-INF/beans.xml");

        EnterpriseArchive helloWorldExension = ShrinkWrap.create(EnterpriseArchive.class,"helloWorldExension.ear")
                        .add(new FileAsset(new File("test-applications/helloWorldExension.ear/resources/META-INF/application.xml")), "/META-INF/application.xml")
                        .addAsModule(helloWorldExtensionTest)
                        .addAsManifestResource(new FileAsset(new File("test-applications/helloWorldExtensionTest.war/resources/META-INF/permissions.xml")), "permissions.xml");

        /**
         * Install the user feature and the bundle
         */
        ShrinkHelper.exportDropinAppToServer(server, multipleWars);
        ShrinkHelper.exportDropinAppToServer(server, helloWorldExension);
        System.out.println("Intall the user feature bundle... cdi.helloworld.extension");
        if (RepeatTestFilter.isRepeatActionActive("EE9_FEATURES")) {
            server.installUserBundle(INSTALL_USERBUNDLE_JAKARTA);
            server.installUserFeature(INSTALL_USERFEATURE_JAKARTA);
            server.installSystemFeature(EXPOSE_INTERNAL_CDI_EXTENSION_API_FEATURE_JAKARTA);
        } else {
            server.installUserBundle(INSTALL_USERBUNDLE_JAVAX);
            server.installUserFeature(INSTALL_USERFEATURE_JAVAX);
            server.installSystemFeature(EXPOSE_INTERNAL_CDI_EXTENSION_API_FEATURE_JAVAX);
        }
        server.startServer(true);
        server.waitForStringInLogUsingMark("CWWKZ0001I.*Application helloWorldExension started");
    }

    @Test
    public void testHelloWorldExtensionServlet() throws Exception {
        HttpUtils.findStringInUrl(server, "/helloWorldExtension/hello", "Hello World CDI 1.2!");

        Assert.assertFalse("Test for Requested scope destroyed",
                           server.findStringsInLogs("Stop Event request scope is happening").isEmpty());
    }

    @Test
    public void testExtensionLoaded() throws Exception {
        Assert.assertFalse("Test for extension loadded",
                           server.findStringsInLogs("Hello World! We are starting the container").isEmpty());
        Assert.assertFalse("Test for extension loadded",
                           server.findStringsInLogs("Hello World! scanning class").isEmpty());
        Assert.assertFalse("Test for extension loadded",
                           server.findStringsInLogs("Hello World! scanning class (javax|jakarta).validation.ValidatorFactory").isEmpty());
        Assert.assertFalse("Test for extension loadded",
                           server.findStringsInLogs("Hello World! scanning class (javax|jakarta).validation.Validator").isEmpty());
        Assert.assertFalse("Test for extension loadded",
                           server.findStringsInLogs("Hello World! We are almost finished with the CDI container boot now...").isEmpty());
    }

    @Test
    public void testCDINotEnabled() throws Exception {

        HttpUtils.findStringInUrl(server, "/multipleWar3", "MyEjb myWar1Bean");
        HttpUtils.findStringInUrl(server, "/multipleWarNoBeans", "ContextNotActiveException");
    }

    /**
     * Post test processing.
     *
     * @throws Exception
     */
    @AfterClass
    public static void cleanup() throws Exception {
        final String METHOD_NAME = "cleanup";
        Log.info(CDI12ExtensionTest.class, METHOD_NAME, "Stopping the server.");
        if (server.isStarted()) {
            server.stopServer();
        }
        Log.info(CDI12ExtensionTest.class, METHOD_NAME, "Removing cdi extension test user feature files.");
        if (RepeatTestFilter.isRepeatActionActive("EE9_FEATURES")) {
            server.uninstallUserBundle(INSTALL_USERBUNDLE_JAKARTA);
            server.uninstallUserFeature(INSTALL_USERFEATURE_JAKARTA);
            server.uninstallSystemFeature(EXPOSE_INTERNAL_CDI_EXTENSION_API_FEATURE_JAKARTA);
        } else {
            server.uninstallUserBundle(INSTALL_USERBUNDLE_JAVAX);
            server.uninstallUserFeature(INSTALL_USERFEATURE_JAVAX);
            server.uninstallSystemFeature(EXPOSE_INTERNAL_CDI_EXTENSION_API_FEATURE_JAVAX);
        }
    }
}
