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
package com.ibm.ws.jaxws.fat;

import static componenttest.annotation.SkipForRepeat.NO_MODIFICATION;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.ws.jaxws.fat.util.ExplodedShrinkHelper;

import componenttest.annotation.SkipForRepeat;
import componenttest.custom.junit.runner.FATRunner;

/**
 *
 */
@RunWith(FATRunner.class)
@SkipForRepeat({ NO_MODIFICATION })
public class EJBServiceRefBndTest23 extends AbstractEJBServiceRefBndTest {

    @BeforeClass
    public static void setup() throws Exception {
        JavaArchive jar = ShrinkHelper.buildJavaArchive("ejbServiceRefBndApp", "com.ibm.sample.bean",
                                                        "com.ibm.sample.jaxws.echo.client",
                                                        "com.ibm.sample.jaxws.hello.client",
                                                        "com.ibm.sample.jaxws.hello.client.interceptor",
                                                        "com.ibm.sample.util");

        WebArchive war = ShrinkHelper.buildDefaultApp("ejbServiceRefBndClient", "com.ibm.sample.web.servlet");

        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "ejbServiceRefBndApp.ear");;

        ear.addAsModule(jar);
        ear.addAsModule(war);

        ExplodedShrinkHelper.explodedArchiveToDestination(server, ear, "dropins");

        server.copyFileToLibertyInstallRoot("lib/features", "EJBServiceRefBndTest/jaxwsTest-2.3.mf");

        server.setServerConfigurationFile("EJBServiceRefBndTest/jaxwsTest-2.3_server.xml");

    }

    @AfterClass
    public static void cleanup() throws Exception {
        server.deleteFileFromLibertyInstallRoot("lib/features/jaxwsTest-2.3.mf");
    }
}
