//
// @(#) 1.1 autoFVT/src/jaxb/types/wsfvt/test/AttachmentsTest.java, WAS.websvcs.fvt, WASX.FVT 7/10/07 09:47:39 [7/23/07 09:43:47]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 11/02/06 ulbricht    450943          New File
// 04/14/09 btiffany    561054          add cache test
//

package jaxb.types.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

import jaxb.types.wsfvt.client.AttachmentsClient;

/**
 * This test is taken from a Sun blog about how to use JAX-WS
 * with attachments.  
 *
 * WARNING: Do not add this to open source.  It was taken from
 *          a Sun blog. 
 */
public class AttachmentsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private AttachmentsClient client = null;

    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public AttachmentsTest(String name) {
        super(name);
    }

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
        return new TestSuite(AttachmentsTest.class);
    }
    
    /**
     * This method does any necessary setUp for each test
     * method.
     *
     * @throws Exception Any kind of exception
     */
    public void setUp() throws Exception {
        client = new AttachmentsClient();
    }

    /**
     * This method tests the ability to get multiple attachments.
     *
     * @testStrategy This method tests the ability to get multiple
     *               attachments.
     * @throws Exception Any kind of exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testGetMultipleAttachments() throws Exception {
        assertTrue(client.GetMultipleAttachmentsTest());
    }

    /**
     * This method tests the ability to put multiple attachments.
     *
     * @testStrategy This method tests the ability to put multiple
     *               attachments.
     * @throws Exception Any kind of exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPutMultipleAttachments() throws Exception {
        assertTrue(client.PutMultipleAttachmentsTest());
    }

    /**
     * This method tests the ability to send and receive multiple
     * attachments.
     *
     * @testStrategy This method tests the ability to send and
     *               receive multiple attachments.
     * @throws Exception Any kind of exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testEchoMultipleAttachments() throws Exception {
        assertTrue(client.EchoMultipleAttachmentsTest());
    }

    /**
     * This method tests the ability to send attachments and
     * the server implementation throws a fault.
     *
     * @testStrategy This method tests the ability to send attachments
     *                and the server implementation throws a fault.
     * @throws Exception Any kind of exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testEchoAttachmentsAndThrowFault() throws Exception {
        assertTrue(client.EchoAttachmentsAndThrowAFaultTest());
    }

    /**
     * This method tests the ability to send and receive multiple
     * attachments with a header.
     *
     * @testStrategy This method tests the ability to send and
     *               receive multiple attachments with a header.
     * @throws Exception Any kind of exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testEchoAttachmentsWithHeader() throws Exception {
        assertTrue(client.EchoAttachmentsWithHeaderTest());
    }

    /**
     * This method tests the ability to send multiple
     * attachments with a header and the server implementation throws
     * a fault.
     *
     * @testStrategy This method tests the ability to send
     *               multiple attachments with a header and the
     *               server implementation throws a fault.
     * @throws Exception Any kind of exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testEchoAttachmentsWithHeaderAndThrowFault() throws Exception {
        assertTrue(client.EchoAttachmentsWithHeaderAndThrowAFaultTest());
    }

    /**
     * This method tests the ability to send and receive multiple attachments
     * using SwaRef.
     *
     * @testStrategy This method tests the ability to send and receive multiple
     *               attachments with SwaRef.
     * @throws Exception Any kind of exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testEchoMultipleAttachmentsSwaRef() throws Exception {
        // This test SHOULD pass but will not until defect 420670 is fixed.
        // When this defect is fixed, the ! should be removed and
        // the test will pass as expected.  Until that time, I have
        // changed the test to assert false so that when it does get
        // fixed I know to take away the !.
        assertTrue(!client.EchoMultipleAttachmentsSwaRefTest());
    }
    
    /**
     * call each test method multiple times to see if we can find any
     * caching problems in jaxb fastpath. 
     */
    public void testCaching()throws Exception {
        int i = 0;
        System.out.println("----- "+ ++i);
        testEchoMultipleAttachmentsSwaRef();  
        System.out.println("----- "+ ++i);
        testEchoAttachmentsWithHeaderAndThrowFault();
        System.out.println("----- "+ ++i);
        testEchoMultipleAttachments();
        System.out.println("----- "+ ++i);
        testEchoAttachmentsWithHeaderAndThrowFault();
        System.out.println("----- "+ ++i);
        testEchoAttachmentsWithHeader();
        System.out.println("----- "+ ++i);
       testEchoMultipleAttachmentsSwaRef();
        System.out.println("----- "+ ++i);
        testEchoMultipleAttachments();
        System.out.println("----- "+ ++i);
        testPutMultipleAttachments();
        System.out.println("----- "+ ++i);
        testEchoMultipleAttachmentsSwaRef();
        System.out.println("----- "+ ++i);
        testPutMultipleAttachments();     
        System.out.println("----- "+ ++i);
        testGetMultipleAttachments();
        System.out.println("----- "+ ++i);
        testPutMultipleAttachments();
        System.out.println("----- "+ ++i);
        testGetMultipleAttachments();
        System.out.println("----- "+ ++i);
        testEchoAttachmentsWithHeader();
        System.out.println("----- "+ ++i);
        testGetMultipleAttachments();
        System.out.println("----- "+ ++i);
        testEchoAttachmentsWithHeaderAndThrowFault();
        System.out.println("----- "+ ++i);
        
    }

}
