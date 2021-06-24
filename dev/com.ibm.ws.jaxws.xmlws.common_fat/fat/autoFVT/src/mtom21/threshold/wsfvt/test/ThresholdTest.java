package mtom21.threshold.wsfvt.test;

import mtom21.threshold.wsfvt.client.ThresholdClient;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

public class ThresholdTest extends FVTTestCase {

    /** 
     * The main method.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    /** 
     * This method controls which test methods to run.
     *
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(ThresholdTest.class);
    }
    
    /**
     * This test verifies that MTOM is used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch client. This test covers
     * the image/jpeg MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch client. This test covers the image/jpeg MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchClientBreakThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDispatchClient(true, null, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was not used on the client side when the attachment was larger than the threshold: MTOMFeature(10)",
                ThresholdClient.positiveResult, result);
    }
    
    /**
     * This test verifies that MTOM is used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(boolean int) constructor with a true value for the boolean. The client
     * is a Dispatch client. This test covers the image/jpeg MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean int) constructor with a true value for the boolean. The client is a Dispatch client. This test covers the image/jpeg MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchClientBreakThresholdTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDispatchClient(true, true, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was not used on the client side when the attachment was larger than the threshold: MTOMFeature(true, 10)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(boolean int) constructor with a false value for the boolean. The client
     * is a Dispatch client. This test covers the image/jpeg MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean int) constructor with a false value for the boolean. The client is a Dispatch client. This test covers the image/jpeg MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchClientBreakThresholdFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDispatchClient(false, false, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was larger than the threshold and the boolean constructor value was false: MTOMFeature(false, 10)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch client. This test
     * covers the image/jpeg MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch client. This test covers the image/jpeg MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchClientYieldThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDispatchClient(false, null, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold: MTOMFeature(100000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of true. The
     * client is a Dispatch client. This test covers the image/jpeg MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of true. The client is a Dispatch client. This test covers the image/jpeg MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchClientYieldThresholdTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDispatchClient(false, true, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold and the boolean constructor value was true: MTOMFeature(true, 100000).",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of false. The
     * client is a Dispatch client. This test covers the image/jpeg MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of false. The client is a Dispatch client. This test covers the image/jpeg MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchClientYieldThresholdFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDispatchClient(false, false, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold: MTOMFeature(false, 100000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that an expection is thrown for a negative threshold value. The client is
     * a Dispatch client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that an expection is thrown for a negative threshold value. The client is a Dispatch client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchInvalidThreshold() {
        ThresholdClient client = new ThresholdClient();
        try {
            client.callMTOMDispatchClient(false, null, -1);
            fail("An Exception was not thrown for an invalid threshold.");
        } catch(Exception e) {
            // expected
        }
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch Source client which is not supported for MTOM.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch Source client which is not supported for MTOM.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSourceClientBreakThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDispatchSourceClient(false, null, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was larger than the threshold: MTOMFeature(10)",
                ThresholdClient.positiveResult, result);
    }
    
    /**
     * This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(boolean int) constructor with a true value for the boolean. The client
     * is a Dispatch Source client which is not supported for MTOM.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean int) constructor with a true value for the boolean. The client is a Dispatch Source client which is not supported for MTOM.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSourceClientBreakThresholdTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDispatchSourceClient(false, true, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was larger than the threshold: MTOMFeature(true, 10)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(boolean int) constructor with a false value for the boolean. The client
     * is a Dispatch Source client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean int) constructor with a false value for the boolean. The client is a Dispatch Source client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSourceClientBreakThresholdFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDispatchSourceClient(false, false, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was larger than the threshold and the boolean constructor value was false: MTOMFeature(false, 10)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch Source client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch Source client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSourceClientYieldThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDispatchSourceClient(false, null, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold: MTOMFeature(100000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of true. The
     * client is a Dispatch Source client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of true. The client is a Dispatch Source client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSourceClientYieldThresholdTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDispatchSourceClient(false, true, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold and the boolean constructor value was true: MTOMFeature(true, 100000).",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of false. The
     * client is a Dispatch Source client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of false. The client is a Dispatch Source client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSourceClientYieldThresholdFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDispatchSourceClient(false, false, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold: MTOMFeature(false, 100000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that an expection is thrown for a negative threshold value. The client is
     * a Dispatch Source client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that an expection is thrown for a negative threshold value. The client is a Dispatch Source client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSourceInvalidThreshold() {
        ThresholdClient client = new ThresholdClient();
        try {
            client.callMTOMDispatchSourceClient(false, null, -1);
            fail("An Exception was not thrown for an invalid threshold.");
        } catch(Exception e) {
            // expected
        }
    }
    
    /**
     * This test verifies that MTOM is used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Proxy DataHandler client. This test
     * covers the multipart/* MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Proxy DataHandler client. This test covers the multipart/* MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyDataHandlerClientBreakThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyDataHandlerClient(true, null, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was not used on the client side when the attachment was larger than the threshold: MTOMFeature(10)",
                ThresholdClient.positiveResult, result);
    }
    
    /**
     * This test verifies that MTOM is used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(boolean int) constructor with a true value for the boolean. The client
     * is a Proxy DataHandler. This test covers the multipart/* MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean int) constructor with a true value for the boolean. The client is a Proxy DataHandler client. This test covers the multipart/* MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyDataHandlerClientBreakThresholdTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyDataHandlerClient(true, true, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was not used on the client side when the attachment was larger than the threshold: MTOMFeature(true, 10)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(boolean int) constructor with a false value for the boolean. The client
     * is a Proxy DataHandler client. This test covers the multipart/* MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean int) constructor with a false value for the boolean. The client is a Proxy DataHandler client. This test covers the multipart/* MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyDataHandlerClientBreakThresholdFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyDataHandlerClient(false, false, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was larger than the threshold and the boolean constructor value was false: MTOMFeature(false, 10)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Proxy DataHandler client.
     * This test covers the multipart/* MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Proxy DataHandler client. This test covers the multipart/* MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDProxyDataHandlerClientYieldThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyDataHandlerClient(false, null, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold: MTOMFeature(100000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of true. The
     * client is a Proxy DataHandler client. This test covers the multipart/* MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of true. The client is a Proxy DataHandler client. This test covers the multipart/* MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyDataHandlerClientYieldThresholdTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyDataHandlerClient(false, true, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold and the boolean constructor value was true: MTOMFeature(true, 100000).",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of false. The
     * client is a Proxy DataHandler client. This test covers the multipart/* MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of false. The client is a Proxy DataHandler client. This test covers the multipart/* MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyDataHandlerClientYieldThresholdFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyDataHandlerClient(false, false, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold: MTOMFeature(false, 100000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that an expection is thrown for a negative threshold value. The client is
     * a Proxy DataHandler client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that an expection is thrown for a negative threshold value. The client is a Proxy Data Handler client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyDataHandlerInvalidThreshold() {
        ThresholdClient client = new ThresholdClient();
        try {
            client.callMTOMProxyDataHandlerClient(false, null, -1);
            fail("An Exception was not thrown for an invalid threshold.");
        } catch(Exception e) {
            // expected
        }
    }

    /**
     * This test verifies that MTOM is used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Proxy client. This test covers
     * the image/jpeg MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Proxy client. This test covers the image/jpeg MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyClientBreakThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyClient(true, null, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was not used on the client side when the attachment was larger than the threshold: MTOMFeature(10)",
                ThresholdClient.positiveResult, result);
    }
    
    /**
     * This test verifies that MTOM is used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(boolean int) constructor with a true value for the boolean. The client
     * is a Proxy client. This test covers the image/jpeg MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean int) constructor with a true value for the boolean. The client is a Proxy client. This test covers the image/jpeg MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyClientBreakThresholdTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyClient(true, true, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was not used on the client side when the attachment was larger than the threshold: MTOMFeature(true, 10)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(boolean int) constructor with a false value for the boolean. The client
     * is a Proxy client. This test covers the image/jpeg MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean int) constructor with a false value for the boolean. The client is a Proxy client. This test covers the image/jpeg MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyClientBreakThresholdFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyClient(false, false, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was larger than the threshold and the boolean constructor value was false: MTOMFeature(false, 10)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Proxy client. This test
     * covers the image/jpeg MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Proxy client. This test covers the image/jpeg MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyClientYieldThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyClient(false, null, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold: MTOMFeature(100000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of true. The
     * client is a Proxy client. This test covers the image/jpeg MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of true. The client is a Proxy client. This test covers the image/jpeg MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyClientYieldThresholdTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyClient(false, true, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold and the boolean constructor value was true: MTOMFeature(true, 100000).",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of false. The
     * client is a Proxy client. This test covers the image/jpeg MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of false. The client is a Proxy client. This test covers the image/jpeg MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyClientYieldThresholdFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyClient(false, false, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold: MTOMFeature(false, 100000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that an expection is thrown for a negative threshold value. The client is
     * a Proxy client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that an expection is thrown for a negative threshold value. The client is a Proxy client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyClientInvalidThreshold() {
        ThresholdClient client = new ThresholdClient();
        try {
            client.callMTOMProxyClient(false, null, -1);
            fail("An Exception was not thrown for an invalid threshold.");
        } catch(Exception e) {
            // expected
        }
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch client. This test covers
     * the text/plain MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch client. This test covers the text/plain MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchTextPlainClientBreakThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDipatchTextPlainClient(false, null, 5);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was not used on the client side when the attachment was larger than the threshold: MTOMFeature(10)",
                ThresholdClient.positiveResult, result);
    }
    
    /**
     * This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(boolean int) constructor with a true value for the boolean. The client
     * is a Dispatch client. This test covers the text/plain MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean int) constructor with a true value for the boolean. The client is a Dispatch client. This test covers the text/plain MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchTextPlainClientBreakThresholdTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDipatchTextPlainClient(false, true, 5);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was not used on the client side when the attachment was larger than the threshold: MTOMFeature(true, 10)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(boolean int) constructor with a false value for the boolean. The client
     * is a Dispatch client. This test covers the text/plain MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean int) constructor with a false value for the boolean. The client is a Dispatch client. This test covers the text/plain MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchTextPlainClientBreakThresholdFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDipatchTextPlainClient(false, false, 5);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was larger than the threshold and the boolean constructor value was false: MTOMFeature(false, 10)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dipsatch client. This test
     * covers the text/plain MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch client. This test covers the text/plain MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchTextPlainClientYieldThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDipatchTextPlainClient(false, null, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold: MTOMFeature(100000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of true. The
     * client is a Dispatch client. This test covers the text/plain MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of true. The client is a Dispatch client. This test covers the text/plain MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchTextPlainClientYieldThresholdTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDipatchTextPlainClient(false, true, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold and the boolean constructor value was true: MTOMFeature(true, 100000).",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of false. The
     * client is a Dispatch client. This test covers the text/plain MIME type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of false. The client is a Dispatch client. This test covers the text/plain MIME type.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchTextPlainClientYieldThresholdFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDipatchTextPlainClient(false, false, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold: MTOMFeature(false, 100000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that an expection is thrown for a negative threshold value. The client is
     * a Dispatch client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that an expection is thrown for a negative threshold value. The client is a Dispatch client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchTextPlainClientInvalidThreshold() {
        ThresholdClient client = new ThresholdClient();
        try {
            client.callMTOMDipatchTextPlainClient(false, null, -1);
            fail("An Exception was not thrown for an invalid threshold.");
        } catch(Exception e) {
            // expected
        }
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch SOAPMessage client which is not supported for MTOM.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch SOAPMessage client which is not supported for MTOM.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSOAPMessageClientBreakThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDipatchSOAPMessageClient(false, null, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was larger than the threshold: MTOMFeature(10)",
                ThresholdClient.positiveResult, result);
    }
    
    /**
     * This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(boolean int) constructor with a true value for the boolean. The client
     * is a Dispatch SOAPMessage client which is not supported for MTOM.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean int) constructor with a true value for the boolean. The client is a Dispatch SOAPMessage client which is not supported for MTOM.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSOAPMessageClientBreakThresholdTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDipatchSOAPMessageClient(false, true, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was larger than the threshold: MTOMFeature(true, 10)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the
     * JAX-WS 2.1 MTOMFeature(boolean int) constructor with a false value for the boolean. The client
     * is a Dispatch SOAPMessage client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent larger than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean int) constructor with a false value for the boolean. The client is a Dispatch SOAPMessage client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSOAPMessageClientBreakThresholdFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDipatchSOAPMessageClient(false, false, 10);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was larger than the threshold and the boolean constructor value was false: MTOMFeature(false, 10)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch SOAPMessage client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(int) constructor. The client is a Dispatch SOAPMessage client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSOAPMessageClientYieldThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDipatchSOAPMessageClient(false, null, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold: MTOMFeature(100000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of true. The
     * client is a Dispatch SOAPMessage client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of true. The client is a Dispatch SOAPMessage client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSOAPMessageClientYieldThresholdTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDipatchSOAPMessageClient(false, true, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold and the boolean constructor value was true: MTOMFeature(true, 100000).",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used when a file is sent smaller than the threshold,
     * using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of false. The
     * client is a Dispatch SOAPMessage client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used when a file is sent smaller than the threshold, using the JAX-WS 2.1 MTOMFeature(boolean, int) constructor with a boolean value of false. The client is a Dispatch SOAPMessage client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSOAPMessageClientYieldThresholdFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMDipatchSOAPMessageClient(false, false, 100000);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the client side when the attachment was smaller than the threshold: MTOMFeature(false, 100000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that an expection is thrown for a negative threshold value. The client is
     * a Dispatch SOAPMessage client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that an expection is thrown for a negative threshold value. The client is a Dispatch SOAPMessage client.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMDispatchSOAPMessageInvalidThreshold() {
        ThresholdClient client = new ThresholdClient();
        try {
            client.callMTOMDipatchSOAPMessageClient(false, null, -1);
            fail("An Exception was not thrown for an invalid threshold.");
        } catch(Exception e) {
            // expected
        }
    }

    /**
     * This test verifies that MTOM is used on the server side when a file is sent larger than the
     * threshold, using the JAX-WS 2.1 \@MTOM(threshold) annotation. The server is a Proxy server.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is used on the server side when a file is sent larger than the threshold, using the JAX-WS 2.1 @MTOM(threshold) annotation. The server is a Proxy server.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyServerBreakThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyServerThreshold(true, ThresholdClient.jpegFile1);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was NOT used on the server side when the attachment was larger than the server side threshold: @MTOM(threshold=20000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used on the server side when a file is sent smaller than the
     * threshold, using the JAX-WS 2.1 \@MTOM(threshold) annotation. The server is a Proxy server.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used on the server side when a file is sent smaller than the threshold, using the JAX-WS 2.1 @MTOM(threshold) annotation. The server is a Proxy server.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyServerYieldThreshold() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyServerThreshold(false, ThresholdClient.jpegFile2);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the server side when the attachment was smaller than the server side threshold: @MTOM(threshold=20000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is used on the server side when a file is sent larger than the
     * threshold, using the JAX-WS 2.1 \@MTOM(enabled, threshold) annotation with enabled equal to
     * true. The server is a Proxy server.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is used on the server side when a file is sent larger than the threshold, using the JAX-WS 2.1 @MTOM(enabled, threshold) annotation with enabled equal to true. The server is a Proxy server.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyServerBreakThresholdMTOMEnabledTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyServerThresholdEnabledTrue(true, ThresholdClient.jpegFile1);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was NOT used on the server side when the attachment was larger than the server side threshold: @MTOM(enabled=true, threshold=20000)",
                ThresholdClient.positiveResult, result);
    }
    
    /**
     * This test verifies that MTOM is NOT used on the server side when a file is sent smaller than the
     * threshold, using the JAX-WS 2.1 \@MTOM(enabled, threshold) annotation with enabled equal to
     * true. The server is a Proxy server.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used on the server side when a file is sent smaller than the threshold, using the JAX-WS 2.1 @MTOM(enabled, threshold) annotation with enabled equal to true. The server is a Proxy server.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyServerYieldThresholdMTOMEnabledTrue() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyServerThresholdEnabledTrue(false, ThresholdClient.jpegFile2);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the server side when the attachment was smaller than the server side threshold: @MTOM(enabled=true, threshold=20000)",
                ThresholdClient.positiveResult, result);
    }

    /**
     * This test verifies that MTOM is NOT used on the server side when a file is sent larger than the
     * threshold, using the JAX-WS 2.1 \@MTOM(enabled, threshold) annotation with enabled equal to
     * false. The server is a Proxy server.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used on the server side when a file is sent larger than the threshold, using the JAX-WS 2.1 @MTOM(enabled, threshold) annotation with enabled equal to false. The server is a Proxy server.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyServerBreakThresholdMTOMEnabledFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyServerThresholdEnabledFalse(false, ThresholdClient.jpegFile1);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the server side when the attachment was larger than the server side threshold: @MTOM(enabled=false, threshold=20000)",
                ThresholdClient.positiveResult, result);
    }
    
    /**
     * This test verifies that MTOM is NOT used on the server side when a file is sent smaller than the
     * threshold, using the JAX-WS 2.1 \@MTOM(enabled, threshold) annotation with enabled equal to
     * false. The server is a Proxy server.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that MTOM is NOT used on the server side when a file is sent smaller than the threshold, using the JAX-WS 2.1 @MTOM(enabled, threshold) annotation with enabled equal to false. The server is a Proxy server.",
            expectedResult="",
            since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testMTOMProxyServerYieldThresholdMTOMEnabledFalse() {
        ThresholdClient client = new ThresholdClient();
        String result = null;
        try {
            result = client.callMTOMProxyServerThresholdEnabledFalse(false, ThresholdClient.jpegFile2);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error calling client: " + e.getMessage());
        }
        assertEquals("MTOM was used on the server side when the attachment was smaller than the server side threshold: @MTOM(enabled=false, threshold=20000)",
                ThresholdClient.positiveResult, result);
    }

}
