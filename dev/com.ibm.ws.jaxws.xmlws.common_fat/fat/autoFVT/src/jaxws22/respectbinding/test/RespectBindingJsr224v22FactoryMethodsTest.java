//
// @(#) 1.4 autoFVT/src/jaxws22/respectbinding/test/RespectBindingJsr224v22FactoryMethodsTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/19/10 11:10:49 [8/8/12 06:57:39]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 04/10/10 btiffany                    New File
//

package jaxws22.respectbinding.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.RespectBindingFeature;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import jaxws22.respectbinding.server.Echo;
import jaxws22.respectbinding.server.EchoService;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * This class will test that the respectbinding webservice feature can be
 * applied correctly using static and dynamic factory methods, and that
 * conformance statements are met.
 * 
 * We already exercised most of the factory methods with MtomFeature, so we'll
 * just hit the common static and dynamic ones here with RespectBindingFeature
 * and make sure things are Ok.
 * 
 * 
 * 
 * Since: version 8 RTC Story: 23303, task 26090
 * 
 */
public class RespectBindingJsr224v22FactoryMethodsTest extends FVTTestCase {
    private static String hostAndPort = null;

    private static String fvtdir = null;

    private static String wsdldir = null;
	
	static {
		try {
			String host = TopologyDefaults.getDefaultAppServer().getNode().getHostname();
			String port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost).toString();

			hostAndPort = "http://" + host + ":" + port;
			System.out.println("hostAndPort = " + hostAndPort);

			fvtdir = AppConst.FVT_HOME;
			// debug hack:
			if (fvtdir == null | fvtdir.contains("null")) {
				fvtdir = "/test/wasx_kk1003.32/autoFVT";
			}
			wsdldir = fvtdir + "/src/jaxws22/respectbinding/client/wsdl";
		} catch (Exception e) {
		// do nothing
		}
    }

    /**
     * A constructor to create a test case with a given name.
     * 
     * @param name
     *            The name of the test case to be created
     */
    public RespectBindingJsr224v22FactoryMethodsTest(String name) {
        super(name);
    }

    /**
     * The main method. Only used for debugging.
     * 
     * @param args
     *            The command line arguments
     */
    public static void main(String[] args) throws java.lang.Exception {
         TestRunner.run(suite());
        /*
        RespectBindingJsrr224v22FactoryMethodsTest t = new RespectBindingJsrr224v22FactoryMethodsTest(
                "x");
        t.suiteSetup2();
        */
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(RespectBindingJsr224v22FactoryMethodsTest.class);
    }

    /**
     * test 4.1.1.1 3 arg constructor produces a properly configured service.
     * 645812 - we conclude this should throw a webservice exception
     * 
     * This was added in the mtom testcase, so it's disabled here.
     * 
     */
    public void _testCreate3argWithFeature() throws java.lang.Exception {
        fail("this is in the mtom testcase");
    }

    /**
     * spec sec 4.2.3, check that these new getport methods work. We will
     * reference a wsdl containing a valid, required extension. The client
     * should attempt to invoke the service.
     * 
     */
    public void testValidRequired() throws java.lang.Exception {
        System.out.println("classpath is: "+ System.getProperty("java.class.path"));
        QName q = new QName("http://server.respectbinding.jaxws22/",
                "EchoService");
        URL u = new URL("file:" + wsdldir + "/ValidRequired.wsdl");
        Service s = Service.create(u, q);
        // add the feature on getport. Unlike service.create, which should not
        // work, this should.
        Echo port = s.getPort(Echo.class, new RespectBindingFeature(true));
        try {
            port.echo("hello"); // this should make it onto the wire.
        } catch (WebServiceException e) {
	   System.out.println("Caught exception: " + e.toString());
            assertTrue("client should have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }

    /**
     * spec sec 4.2.3, check that these new getport methods work. We will
     * reference a wsdl containing an valid, not required extension. The spec
     * doesn't cover this state, but we need a test to be consistent. There's no
     * reason we shouldn't invoke the service
     * 
     */
    public void testValidNotRequired() throws java.lang.Exception {
        QName q = new QName("http://server.respectbinding.jaxws22/",
                "EchoService");
        URL u = new URL("file:" + wsdldir + "/ValidRequired.wsdl");
        Service s = Service.create(u, q);
        // add the feature on getport. Unlike service.create, which should not
        // work, this should.
        Echo port = s.getPort(Echo.class, new RespectBindingFeature(false));
        try {
            port.echo("hello"); // this should make it onto the wire.
        } catch (WebServiceException e) {
            assertTrue("client should have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }

    /**
     * a valid but not required feature, no respectbinding feature applied. The
     * spec doesn't say what has to happen here, but we need a test so we
     * continue to behave consistently with whatever we decide to do.
     * 
     * There's no reason not to invoke it, so we'll test for that.
     * 
     * @throws java.lang.Exception
     */
    public void testValidNotRequiredNoFeature_using_DynamicGetPort2arg()
            throws java.lang.Exception {
        QName q = new QName("http://server.respectbinding.jaxws22/",
                "EchoService");
        URL u = new URL("file:" + wsdldir + "/ValidRequired.wsdl");
        Service s = Service.create(u, q);
        Echo port = s.getPort(Echo.class); // no feature
        try {
            port.echo("hello"); // this should make it onto the wire.
        } catch (WebServiceException e) {
            assertTrue("client should have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }

    /**
     * Check that when we apply a respectbinding feature to a wsdl that has NO
     * extension elements, it has not effect. Client should attempt to invoke
     * the service.
     * 
     * @throws java.lang.Exception
     */
    public void testNoExtension() throws java.lang.Exception {
        String wsdlurl = "file:" + wsdldir + "/NoExtension.wsdl";
        EchoService s = new EchoService(new URL(wsdlurl));
        Echo port = s.getEchoPort(new RespectBindingFeature(true));
        try {
            port.echo("hello"); // this should make it onto the wire.
        } catch (WebServiceException e) {
            assertTrue("client should have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }

    /**
     * spec sec 4.2.3, check that these new getport methods work. We reference a
     * wsdl containing invalid, required extension. Client should throw
     * meaningful exception on invoke.
     * 
     * It had better not contain a ConnectException, which would mean message
     * would have been put on the wire.
     * 
     */
    public void testInvalidRequiredonBinding() throws java.lang.Exception {
        String wsdlurl = "file:" + wsdldir + "/InvalidRequiredonBinding.wsdl";
        // System.out.println(wsdlurl);
        EchoService s = new EchoService(new URL(wsdlurl));
        Echo port = s.getEchoPort(new RespectBindingFeature());
        try {
            port.echo("hello"); // this should not make it onto the wire.
        } catch (WebServiceException e) {
            assertFalse("client should not have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }

    /**
     * 
     * We reference a wsdl containing invalid, required extension. Client should
     * throw meaningful exception on invoke.
     * 
     * It had better not contain a ConnectException, which would mean message
     * would have been put on the wire.
     * 
     */
    public void testInvalidRequiredonFault() throws java.lang.Exception {
        String wsdlurl = "file:" + wsdldir + "/InvalidRequiredOnFault.wsdl";
        // System.out.println(wsdlurl);
        EchoService s = new EchoService(new URL(wsdlurl));
        Echo port = s.getEchoPort(new RespectBindingFeature());
        try {
            port.echo("hello"); // this should not make it onto the wire.
        } catch (WebServiceException e) {
            assertFalse("client should not have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }

    /**
     * 
     * We reference a wsdl containing invalid, required extension. Client should
     * throw meaningful exception on invoke.
     * 
     * It had better not contain a ConnectException, which would mean message
     * would have been put on the wire.
     * 
     */
    public void testInvalidRequiredonOperation() throws java.lang.Exception {
        String wsdlurl = "file:" + wsdldir + "/InvalidRequiredOnOperation.wsdl";
        // System.out.println(wsdlurl);
        EchoService s = new EchoService(new URL(wsdlurl));
        Echo port = s.getEchoPort(new RespectBindingFeature());
        try {
            port.echo("hello"); // this should not make it onto the wire.
        } catch (WebServiceException e) {
            assertFalse("client should not have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }

    /**
     * 
     * We reference a wsdl containing invalid, required extension. Client should
     * throw meaningful exception on invoke.
     * 
     * It had better not contain a ConnectException, which would mean message
     * would have been put on the wire.
     * 
     */
    public void testInvalidRequiredonInput() throws java.lang.Exception {
        String wsdlurl = "file:" + wsdldir + "/InvalidRequiredOnInput.wsdl";
        // System.out.println(wsdlurl);
        EchoService s = new EchoService(new URL(wsdlurl));
        Echo port = s.getEchoPort(new RespectBindingFeature());
        try {
            port.echo("hello"); // this should not make it onto the wire.
        } catch (WebServiceException e) {
            assertFalse("client should not have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }

    /**
     * 
     * We reference a wsdl containing invalid, required extension. Client should
     * throw meaningful exception on invoke.
     * 
     * It had better not contain a ConnectException, which would mean message
     * would have been put on the wire.
     * 
     */
    public void testInvalidRequiredonOutput() throws java.lang.Exception {
        String wsdlurl = "file:" + wsdldir + "/InvalidRequiredOnInput.wsdl";
        EchoService s = new EchoService(new URL(wsdlurl));
        Echo port = s.getEchoPort(new RespectBindingFeature());
        try {
            port.echo("hello"); // this should not make it onto the wire.
        } catch (WebServiceException e) {
            assertFalse("client should not have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }

    /**
     * expect exception to see if it was due to connection refused.
     * 
     * @param e
     * @return
     */
    private boolean wasConnectionRefused(java.lang.Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        // System.out.println(sw.toString());
        if (sw.toString().contains("ConnectException")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Test that when a wsdl has an invalid, required, feature, but there is no
     * respectbinding feature in play, we do whatever our implementation is
     * going to do, fixpack after fixpack.
     * 
     * The spec does not say what has to happen here, but we need a test so that
     * we don't accidentally change whatever our behavior turns out to be.
     * 
     * I would think that since we don't have to respect the invalid binding, we
     * should go ahead and invoke.
     * 
     * 
     */
    public void testInvalidRequiredNoFeature() throws java.lang.Exception {
        String wsdlurl = "file:" + wsdldir + "/InvalidRequiredonBinding.wsdl";
        // System.out.println(wsdlurl);
        EchoService s = new EchoService(new URL(wsdlurl));
        Echo port = s.getEchoPort();
        try {
            port.echo("hello"); // this should not make it onto the wire.
        } catch (WebServiceException e) {
            assertTrue("client should have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }

    /**
     * a required feature that normally would not work has been disabled by a
     * respectbinding(false) feature. Client should attempt to invoke.
     * 
     * @throws java.lang.Exception
     */
    public void testInvalidRequiredFeatureFalse() throws java.lang.Exception {
        String wsdlurl = "file:" + wsdldir + "/InvalidRequiredonBinding.wsdl";
        // System.out.println(wsdlurl);
        EchoService s = new EchoService(new URL(wsdlurl));
        Echo port = s.getEchoPort(new RespectBindingFeature(false));
        try {
            port.echo("hello"); // this should not make it onto the wire.
        } catch (WebServiceException e) {
            assertTrue("client should have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }

    /**
     * Test that when a wsdl has an valid, required, feature, but there is no
     * respectbinding feature in play, we do whatever our implementation is
     * going to do consistenly, fixpack after fixpack.
     * 
     * The spec does not say what has to happen here, but we need a test so that
     * we don't accidentally change whatever our behavior turns out to be.
     * 
     * Since we're not turning on enforcement, we should probably invoke.
     */
    public void testValidRequiredNoFeature() throws java.lang.Exception {
        String wsdlurl = "file:" + wsdldir + "/ValidRequired.wsdl";
        // System.out.println(wsdlurl);
        EchoService s = new EchoService(new URL(wsdlurl));
        Echo port = s.getEchoPort();
        try {
            port.echo("hello"); // this should not make it onto the wire.
        } catch (WebServiceException e) {
            assertTrue("client should have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }

    /**
     * a valid required feature with enforcement turned off. We should attempt
     * to invoke.
     * 
     * @throws java.lang.Exception
     */
    public void testValidRequiredFalse() throws java.lang.Exception {
        String wsdlurl = "file:" + wsdldir + "/ValidRequired.wsdl";
        // System.out.println(wsdlurl);
        EchoService s = new EchoService(new URL(wsdlurl));
        Echo port = s.getEchoPort(new RespectBindingFeature(false));
        try {
            port.echo("hello"); // this should not make it onto the wire.
        } catch (WebServiceException e) {
            assertTrue("client should have invoked service",
                    wasConnectionRefused(e));
            return;
        }
        fail("did not catch expected exception");
    }



    // perform setup for the testcase - vanilla junit method
    protected void setUp() throws java.lang.Exception {
    }

    // make sure everything is running at the end of each test
    public void tearDown() throws java.lang.Exception {
    }

    // our nonportable test setup method
    protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
        super.suiteSetup(cr);
        System.out.println("suiteSetup() called");
    }

    // our nonportable test teardownp method
    protected void suiteTeardown() throws java.lang.Exception {
        super.suiteTeardown();
        System.out.println("suiteTeardown() called");
    }

}
