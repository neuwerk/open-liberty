//
// @(#) 1.1 WautoFVT/src/saaj13dev/devtest/wsfvt/saaj13/SAAJNamespaceFaultSOAP12Test.java, WAS.websvcs.fvt, WSFP.WFVT, a0712.20 8/2/06 08:56:31 [4/2/07 12:10:07]
//
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W60, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 5/18/06  scheu       LIDB4238        Create 

package saaj13dev.devtest.wsfvt.saaj13;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.websphere.webservices.soap.IBMSOAPElement;
import com.ibm.websphere.webservices.soap.IBMSOAPEnvelope;
import com.ibm.websphere.webservices.soap.IBMSOAPFactory;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;

/**
 * The purpose of this test is to verify that the placement and construction
 * of namespaces in our SAAJ model/engine does not add or lose information.
 * This test is like the SAAJNamespaceFaultTests, but has been upgraded to test SAAJ 1.3 and SOAP 1.2.
 * 
 * The setup*() methods use the SAAJ model to build a SOAPEnvelope.
 * In each case, the SOAPEnvelope should look like the following:
 *
 * <env:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope" 
 *                   xmlns:top="http://top/" 
 *                   xmlns:rep="http://rep/">
 *    <senv:Body>
 *       <senv:Fault>
 *         <env:Code>
 *            <env:Value>env:Sender</env:Value>
 *            <env:SubCode>
 *               <env:Value>top:sample</env:Value>
 *            </env:Subcode>
 *         </env:Code>
 *         <env:Reason>
 *           <env:Text xml:lang="en">hello</env:Text>
 *         </env:Reason>
 *         <env:Detail>
 *           <BugReport rep:def="ghi" top:jkl="mno"
 *                       xmlns:rep="http://rep/" xmlns="http://default/"/>
 *         </env:Detail>
 *       <env:Fault>
 *    </env:Body>
 * </env:Envelope>
 *
 * The key namespace declarations are top, rep and the default namespace.
 * 1) Each setup method only creates a "top" namespace declaration on the envelope.
 * The testTopNS tests make sure that the "top" namespace only occurs once in the
 * serialized xml string.  These tests are important to ensure that the "top" namespace
 * is not lost and is not repeated.
 *
 * 2) Each setup method creates a "rep" namespace on the envelope and on the element
 * contained in the detail.  The testRepNS tests make sure that the "rep"
 * namespace is repeated in the message 2 times.  These tests are important to ensure
 * that the "rep" namespace declarations are not "optimized" from the xml string.
 *
 * 3) Each setup method creates a default prefix on the detail entry and uses it. 
 * The tests ensure the default prefix is not lost or repeated.
 *
 * The setup methods use various construction patterns to build
 * the SAAJ model.  These construction patterns are designed to test the various and
 * subtle ways that clients or sp's could build a message.
 *
 * There are different ways to check if the constructed SAAJ model is correct.  For example,
 * the SOAPEnvelope can be serialialized (via toString) and the resulting xml string
 * can be queried to ensure the correct number of "top" (1) or "rep" (2) namespaces
 * occur in the string.
 *
 * The different combinations of setup and check result in a matix of tests.  
 * The individual tests are named to identify which setup/checking scheme is used.
 * Here are the current setup methods:
 *    setup1: Build using pure SAAJ model
 *    setup2: Like setup1, but namespace decls added via DOM setAttributeNS methods
 *    setup3: Like setup1, but namespace decls added by accessing NamedNodeMap
 *    setup4: Deserialize xml string into SOAPEnvelope
 *    setup5: Like setup2, except extra attributes are added and then immediately removed.
 *    setup6: Like setup1, except header/body elements are DOM nodes added via DOM appendChild
 *    setup7: Like setup4, except xml string is attached to SOAPEnvelope as alternate content.      
 *    setup8: Like setup2, except setAttribute (non-NS) methods are used to add namespace decls.    
 *    setup9: Like setup6, except SOAPElements are attached via DOM appendChild.
 *
 * Here are the current check methods:
 *    checkA: Check by serializing into an xml string and counting namespace declares
 *    checkB: Check by using pure SAAJ 1.1 methods to count namespace declares
 *    checkC: Check by using pure SAAJ 1.2 DOM methods to access DOM model
 *
 * The individual testcase names identify the setup/check performed.  For
 * example, testTopNS_1A uses setup1 and checkA.
 *
 */
public class SAAJNamespaceFaultSOAP12Test extends TestBase {

    private static final String TOP_PREFIX = "top";
    private static final String TOP_URI = "http://top/";
    private static final String REP_PREFIX = "rep";
    private static final String REP_URI = "http://rep/";
    private static final String DEFAULT_PREFIX = "";
    private static final String DEFAULT_URI = "http://default/";

    
    public SAAJNamespaceFaultSOAP12Test(String name) {
        super(name);
    }
    
    protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
        System.out.println("Do not need suiteSetup since no application is installed");    
    }

    //****** TESTS START HERE **************//

    // NOTE: Some of these tests currently don't work.
    // In such cases I have changed the test name to disable_<test>
    // to prevent them from being run.  This is a temporary solution
    // until all of the tests pass.

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_1A() throws Exception{ 
        int count = checkA(setup1(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_1A() throws Exception{ 
        int count = checkA(setup1(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_1A() throws Exception{ 
        int count = checkA(setup1(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_2A() throws Exception{ 
        int count = checkA(setup2(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_2A() throws Exception{ 
        int count = checkA(setup2(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_2A() throws Exception{ 
        int count = checkA(setup2(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_3A() throws Exception{ 
        int count = checkA(setup3(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_3A() throws Exception{ 
        int count = checkA(setup3(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }
 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_3A() throws Exception{ 
        int count = checkA(setup3(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_4A() throws Exception{ 
        int count = checkA(setup4(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_4A() throws Exception{ 
        int count = checkA(setup4(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_4A() throws Exception{ 
        int count = checkA(setup4(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_5A() throws Exception{ 
        int count = checkA(setup5(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_5A() throws Exception{ 
        int count = checkA(setup5(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_5A() throws Exception{ 
        int count = checkA(setup5(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_6A() throws Exception{ 
        int count = checkA(setup6(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_6A() throws Exception{ 
        int count = checkA(setup6(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_6A() throws Exception{ 
        int count = checkA(setup6(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_7A() throws Exception{ 
        int count = checkA(setup7(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_7A() throws Exception{ 
        int count = checkA(setup7(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_7A() throws Exception{ 
        int count = checkA(setup7(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_8A() throws Exception{ 
        int count = checkA(setup8(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_8A() throws Exception{ 
        int count = checkA(setup8(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_8A() throws Exception{ 
        int count = checkA(setup8(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_9A() throws Exception{ 
        int count = checkA(setup9(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_9A() throws Exception{ 
        int count = checkA(setup9(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_9A() throws Exception{ 
        int count = checkA(setup9(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_1B() throws Exception{ 
        int count = checkB(setup1(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_1B() throws Exception{ 
        int count = checkB(setup1(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_1B() throws Exception{ 
        int count = checkB(setup1(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_2B() throws Exception{ 
        int count = checkB(setup2(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_2B() throws Exception{ 
        int count = checkB(setup2(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_2B() throws Exception{ 
        int count = checkB(setup2(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_3B() throws Exception{ 
        int count = checkB(setup3(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_3B() throws Exception{ 
        int count = checkB(setup3(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_3B() throws Exception{ 
        int count = checkB(setup3(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_4B() throws Exception{ 
        int count = checkB(setup4(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_4B() throws Exception{ 
        int count = checkB(setup4(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_4B() throws Exception{ 
        int count = checkB(setup4(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_5B() throws Exception{ 
        int count = checkB(setup5(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_5B() throws Exception{ 
        int count = checkB(setup5(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_5B() throws Exception{ 
        int count = checkB(setup5(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_6B() throws Exception{
        int count = checkB(setup6(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_6B() throws Exception{ 
        int count = checkB(setup6(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_6B() throws Exception{ 
        int count = checkB(setup6(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_7B() throws Exception{ 
        int count = checkB(setup7(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_7B() throws Exception{ 
        int count = checkB(setup7(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_7B() throws Exception{ 
        int count = checkB(setup7(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_8B() throws Exception{ 
        int count = checkB(setup8(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_8B() throws Exception{ 
        int count = checkB(setup8(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_8B() throws Exception{ 
        int count = checkB(setup8(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_9B() throws Exception{ 
        int count = checkB(setup9(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_9B() throws Exception{ 
        int count = checkB(setup9(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_9B() throws Exception{ 
        int count = checkB(setup9(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_1C() throws Exception{ 
        int count = checkC(setup1(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_1C() throws Exception{ 
        int count = checkC(setup1(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_1C() throws Exception{ 
        int count = checkC(setup1(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_2C() throws Exception{ 
        int count = checkC(setup2(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_2C() throws Exception{ 
        int count = checkC(setup2(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_2C() throws Exception{ 
        int count = checkC(setup2(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_3C() throws Exception{ 
        int count = checkC(setup3(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_3C() throws Exception{ 
        int count = checkC(setup3(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_3C() throws Exception{ 
        int count = checkC(setup3(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_4C() throws Exception{ 
        int count = checkC(setup4(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_4C() throws Exception{ 
        int count = checkC(setup4(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_4C() throws Exception{ 
        int count = checkC(setup4(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_5C() throws Exception{ 
        int count = checkC(setup5(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_5C() throws Exception{ 
        int count = checkC(setup5(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_5C() throws Exception{ 
        int count = checkC(setup5(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_6C() throws Exception{ 
        int count = checkC(setup6(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_6C() throws Exception{ 
        int count = checkC(setup6(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_6C() throws Exception{ 
        int count = checkC(setup6(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_7C() throws Exception{ 
        int count = checkC(setup7(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_7C() throws Exception{ 
        int count = checkC(setup7(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultDefaultNS_7C() throws Exception{ 
        int count = checkC(setup7(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    /*
      test*8C tests will always fail because they are setup with non-NS dom attribute methods
      and checked with dom NS attribute methods.  This expected; therefore the tests are excluded.
      (scheu)

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_8C() throws Exception{ 
        int count = checkC(setup8(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_8C() throws Exception{ 
        int count = checkC(setup8(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testFaultDefaultNS_8C() throws Exception{ 
        int count = checkC(setup8(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }
    */

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultTopNS_9C() throws Exception{ 
        int count = checkC(setup9(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultRepNS_9C() throws Exception{ 
        int count = checkC(setup9(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 2 occurences of " + REP_URI + " but " + count + " were found", count == 2);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testFaultDefaultNS_9C() throws Exception{ 
        int count = checkC(setup9(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    // ***** END OF TESTS *********** //

    /**
     * This method constructs a SOAPEnvelope using the
     * only the SAAJ methods.
     */
    private javax.xml.soap.SOAPEnvelope setup1() throws Exception{  
    javax.xml.soap.SOAPMessage message = 
            javax.xml.soap.MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        javax.xml.soap.SOAPEnvelope env = 
            message.getSOAPPart().getEnvelope();
        env.addNamespaceDeclaration(TOP_PREFIX, TOP_URI);
        env.addNamespaceDeclaration(REP_PREFIX, REP_URI);

        javax.xml.soap.SOAPBody body = env.getBody();

        QName qName = new QName( TOP_URI, "sample",  TOP_PREFIX);
            env.createName("sample", TOP_PREFIX, TOP_URI);
        javax.xml.soap.SOAPFault fault = body.addFault(SOAPConstants.SOAP_RECEIVER_FAULT, "hello");
        javax.xml.soap.Detail detail = fault.addDetail();

        qName= new QName(DEFAULT_URI, "BugReport", DEFAULT_PREFIX);
        javax.xml.soap.DetailEntry de = detail.addDetailEntry(qName);
        de.addNamespaceDeclaration(DEFAULT_PREFIX, DEFAULT_URI);
        de.addNamespaceDeclaration(REP_PREFIX, REP_URI);
        qName = new QName(REP_URI, "def", REP_PREFIX);
        de.addAttribute(qName, "ghi");
        qName = new QName(TOP_URI, "jkl", TOP_PREFIX);
        de.addAttribute(qName, "mno");

    return env;
    }
    
    /**
     * Similar to setup1, except the namespaces are added via
     * DOM setAttributeNS method.  These settings come in via
     * a "back-door" and we need to make sure that our SAAJ model
     * recognizes the namespace/prefix declarations.
     */
    private javax.xml.soap.SOAPEnvelope setup2() throws Exception{  
    javax.xml.soap.SOAPMessage message = 
            javax.xml.soap.MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        javax.xml.soap.SOAPEnvelope env = 
            message.getSOAPPart().getEnvelope();
        env.setAttributeNS(URI_XMLNS, "xmlns:" + TOP_PREFIX, TOP_URI);
        env.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);

        javax.xml.soap.SOAPBody body = env.getBody();

        javax.xml.soap.Name name =
            env.createName("sample", TOP_PREFIX, TOP_URI);
        javax.xml.soap.SOAPFault fault = body.addFault(SOAPConstants.SOAP_RECEIVER_FAULT, "hello");
        javax.xml.soap.Detail detail = fault.addDetail();

        name = env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.DetailEntry de = detail.addDetailEntry(name);
        de.setAttributeNS(URI_XMLNS, "xmlns", DEFAULT_URI);
        de.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        name = env.createName("def", REP_PREFIX, REP_URI);
        de.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        de.addAttribute(name, "mno");
        return env;
    }

    /**
     * Similar to setup1, except the namespaces are added by
     * accessing all of the Attributes via the DOM getAttributes method
     * and manipulating the attributes directly.  This means
     * that our model has no interception point for seeing these
     * attributes.
     */
    private javax.xml.soap.SOAPEnvelope setup3() throws Exception{  
    javax.xml.soap.SOAPMessage message = 
            javax.xml.soap.MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        javax.xml.soap.SOAPEnvelope env = 
            message.getSOAPPart().getEnvelope();
        NamedNodeMap attributes = env.getAttributes();
        Attr attr = 
            env.getOwnerDocument().createAttributeNS(URI_XMLNS,  
                                              "xmlns:" + TOP_PREFIX);
        attr.setValue(TOP_URI);
        attributes.setNamedItemNS(attr);
        attr = 
            env.getOwnerDocument().createAttributeNS(URI_XMLNS,  
                                              "xmlns:" + REP_PREFIX);
        attr.setValue(REP_URI);
        attributes.setNamedItemNS(attr);

        javax.xml.soap.SOAPBody body = env.getBody();

        javax.xml.soap.Name name =
            env.createName("sample", TOP_PREFIX, TOP_URI);
        javax.xml.soap.SOAPFault fault = body.addFault(SOAPConstants.SOAP_RECEIVER_FAULT, "hello");
        javax.xml.soap.Detail detail = fault.addDetail();

        name = env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.DetailEntry de = detail.addDetailEntry(name);
        attributes = de.getAttributes();
        attr = 
            env.getOwnerDocument().createAttributeNS(URI_XMLNS,  
                                                     "xmlns:" + REP_PREFIX);
        attr.setValue(REP_URI);
        attributes.setNamedItemNS(attr);
        attr = 
            env.getOwnerDocument().createAttributeNS(URI_XMLNS,  
                                              "xmlns");
        attr.setValue(DEFAULT_URI);
        attributes.setNamedItemNS(attr);
        name = env.createName("def", REP_PREFIX, REP_URI);
        de.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        de.addAttribute(name, "mno");
        return env;
    }

    /**
     * Use Maelstrom engine to build SOAPEnvelope
     */
    private javax.xml.soap.SOAPEnvelope setup4() throws Exception {
        javax.xml.soap.SOAPEnvelope env = setup1();  // build envelope using setup1 (most stable)
        String xmlString = env.toString();  // convert into string
        return deserialize(xmlString); // Deserialize into SOAPEnvelope
    }

    /**
     * Like setup2, except extra namespace/prefix defintions are added and then immediately
     * removed.
     */
    private javax.xml.soap.SOAPEnvelope setup5() throws Exception{
    javax.xml.soap.SOAPMessage message = 
            javax.xml.soap.MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        javax.xml.soap.SOAPEnvelope env = 
            message.getSOAPPart().getEnvelope();
        env.setAttributeNS(URI_XMLNS, "xmlns:" + TOP_PREFIX, TOP_URI);
        env.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);

        env.setAttributeNS(URI_XMLNS, "xmlns", DEFAULT_URI);
        env.removeAttributeNS(URI_XMLNS,"xmlns");

        javax.xml.soap.SOAPBody body = env.getBody();

        javax.xml.soap.Name name =
            env.createName("sample", TOP_PREFIX, TOP_URI);
        javax.xml.soap.SOAPFault fault = body.addFault(SOAPConstants.SOAP_RECEIVER_FAULT, "hello");
        javax.xml.soap.Detail detail = fault.addDetail();

        name = env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.DetailEntry de = detail.addDetailEntry(name);
        de.setAttributeNS(URI_XMLNS, "xmlns", DEFAULT_URI);
        de.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        de.setAttributeNS(URI_XMLNS,"xmlns:" + TOP_PREFIX, TOP_URI);
        de.removeAttributeNS(URI_XMLNS,TOP_PREFIX);

        name = env.createName("def", REP_PREFIX, REP_URI);
        de.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        de.addAttribute(name, "mno");
        return env;
    }

    /**
     * Similar to setup1, except the detail element is a
     * DOM node that is added via DOM apis.
     */
    private javax.xml.soap.SOAPEnvelope setup6() throws Exception{  

    javax.xml.soap.SOAPMessage message = 
            javax.xml.soap.MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        javax.xml.soap.SOAPEnvelope env = 
            message.getSOAPPart().getEnvelope();
        env.addNamespaceDeclaration(TOP_PREFIX, TOP_URI);
        env.addNamespaceDeclaration(REP_PREFIX, REP_URI);

        javax.xml.soap.SOAPBody body = env.getBody();

        javax.xml.soap.Name name =
            env.createName("sample", TOP_PREFIX, TOP_URI);
        javax.xml.soap.SOAPFault fault = body.addFault(SOAPConstants.SOAP_RECEIVER_FAULT, "hello");
        javax.xml.soap.Detail detail = fault.addDetail();

        // Create a DOM node for the detail entry element
        Document document = env.getOwnerDocument();
        Element de = document.createElementNS(DEFAULT_URI, "BugReport");
        de.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        de.setAttributeNS(URI_XMLNS, "xmlns", DEFAULT_URI);
        de.setAttributeNS(REP_URI, REP_PREFIX + ":def", "ghi");
        de.setAttributeNS(TOP_URI, TOP_PREFIX + ":jkl", "mno");
        detail.appendChild(de);  // Append the node
    return env;
    }

    /**
     * Use Maelstrom engine to build SOAPEnvelope
     */
    private javax.xml.soap.SOAPEnvelope setup7() throws Exception {
        javax.xml.soap.SOAPEnvelope env = setup1();  // build envelope using setup1 (most stable)
        String xmlString = env.toString();  // convert into string

        // Use factory to create Element from XMLString
        IBMSOAPFactory sf = (IBMSOAPFactory) SOAPFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);
        env = (javax.xml.soap.SOAPEnvelope) 
            sf.createElementFromXMLString(xmlString, 
                                          javax.xml.soap.SOAPEnvelope.class);
        return env;  
    }

    /**
     * Similar to setup1, except the namespaces are added via
     * DOM setAttributeNS method.  These settings come in via
     * a "back-door" and we need to make sure that our SAAJ model
     * recognizes the namespace/prefix declarations.
     */
    private javax.xml.soap.SOAPEnvelope setup8() throws Exception{  
    javax.xml.soap.SOAPMessage message = 
            javax.xml.soap.MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        javax.xml.soap.SOAPEnvelope env = 
            message.getSOAPPart().getEnvelope();
        env.setAttribute("xmlns:" + TOP_PREFIX, TOP_URI);
        env.setAttribute("xmlns:" + REP_PREFIX, REP_URI);

        javax.xml.soap.SOAPBody body = env.getBody();

        javax.xml.soap.Name name =
            env.createName("sample", TOP_PREFIX, TOP_URI);
        javax.xml.soap.SOAPFault fault = body.addFault(SOAPConstants.SOAP_RECEIVER_FAULT, "hello");
        javax.xml.soap.Detail detail = fault.addDetail();

        name = env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.DetailEntry de = detail.addDetailEntry(name);
        de.setAttribute("xmlns", DEFAULT_URI);
        de.setAttribute("xmlns:" + REP_PREFIX, REP_URI);
        name = env.createName("def", REP_PREFIX, REP_URI);
        de.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        de.addAttribute(name, "mno");
        return env;
    }

    /**
     * Similar to setup6, except the elements in the header/body are
     * raw SOAPElements that are added via DOM apis.
     */
    private javax.xml.soap.SOAPEnvelope setup9() throws Exception{  

    javax.xml.soap.SOAPMessage message = 
            javax.xml.soap.MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        SOAPEnvelope env = (SOAPEnvelope) 
            message.getSOAPPart().getEnvelope();
        env.addNamespaceDeclaration(TOP_PREFIX, TOP_URI);
        env.addNamespaceDeclaration(REP_PREFIX, REP_URI);

        // Detach the body and recreate using a SOAPElement
        javax.xml.soap.SOAPBody body = env.getBody();
        body.detachNode();
        SOAPFactory sf = (SOAPFactory)((IBMSOAPElement) env).getIBMSOAPFactory();
        SOAPElement seBody   = (SOAPElement) 
            sf.createElement("Body", env.getPrefix(), env.getNamespaceURI());
        env.appendChild(seBody);
        body = env.getBody();

        // Create a SOAPElement representing the fault
        SOAPElement seFault = (SOAPElement)
            sf.createElement("Fault", env.getPrefix(), env.getNamespaceURI());
        
        seFault =(SOAPElement) body.appendChild(seFault);

        // Note that the returned delement should be a SOAPFault
        assertTrue("Transformation error, expected SOAPFault", 
                   seFault instanceof javax.xml.soap.SOAPFault);
        

        SOAPElement seFaultCode = (SOAPElement)
            sf.createElement("Code", env.getPrefix(), env.getNamespaceURI());
        SOAPElement se = (SOAPElement)
        	seFaultCode.addChildElement("Value", env.getPrefix());
        se = (SOAPElement)
    		seFaultCode.addChildElement("Subcode", env.getPrefix());
        se = (SOAPElement)
    		se.addChildElement("Value", env.getPrefix());
        se.addTextNode("top:sample");
        seFault.appendChild(seFaultCode);

        SOAPElement seFaultReason = (SOAPElement)
            sf.createElement("Reason", env.getPrefix(), env.getNamespaceURI());
        se = (SOAPElement)
    		seFaultCode.addChildElement("Text", env.getPrefix());
        se.setAttribute("xml:lang", "en");
        se.addTextNode("hello");
        seFault.appendChild(seFaultReason);

        SOAPElement seDetail = (SOAPElement)
            sf.createElement("Detail", env.getPrefix(), env.getNamespaceURI());

        seDetail = (SOAPElement) seFault.appendChild(seDetail); 

        // Note that the returned detail should be a Detail
        assertTrue("Transformation error, expected Detail", 
                   seDetail instanceof javax.xml.soap.Detail);
        
        SOAPElement de = (SOAPElement) 
            sf.createElement("BugReport", "", DEFAULT_URI);
        de.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        de.setAttributeNS(URI_XMLNS, "xmlns", DEFAULT_URI);
        de.setAttributeNS(REP_URI, REP_PREFIX + ":def", "ghi");
        de.setAttributeNS(TOP_URI, TOP_PREFIX + ":jkl", "mno");
        seDetail.appendChild(de);  // Append the node
        return env;
    }


    /**
     * Counts the number of namespace definitions by serializing 
     * envelope to a String
     * @param env SOAPEnvelope
     * @param prefix String
     * @param namespace String
     * @return number of xml definitions of xmlns:prefix=namespace
     */
    private int checkA(javax.xml.soap.SOAPEnvelope env, String prefix, String namespace) {
        String xmlString = ((IBMSOAPEnvelope)env).toXMLString(false);
        String attrName = "xmlns";
        if (prefix.length()>0) {
            attrName = "xmlns:" + prefix;
        }

        return countSubstrings(xmlString, attrName + "=" +"\"" + namespace + "\"");
    }

    /**
     * Counts the number of namespace definitions using the SAAJ methods
     * to find the declared prefixes
     * @param env SOAPEnvelope
     * @param prefix String
     * @param namespace String
     * @return number of xml definitions of xmlns:prefix=namespace
     */
    private int checkB(javax.xml.soap.SOAPEnvelope env, String prefix, String namespace) {
        return countPrefixes(env, prefix, namespace);
    }

    /**
     * Counts the number of namespace definitions using the SAAJ 1.2 DOM methods
     * to find the declared prefixes
     * @param env SOAPEnvelope
     * @param prefix String
     * @param namespace String
     * @return number of xml definitions of xmlns:prefix=namespace
     */
    private int checkC(javax.xml.soap.SOAPEnvelope env, String prefix, String namespace) {
        return countPrefixesDOM(env, prefix, namespace);
    }


    // *********** START UTILITY METHODS ************//

    /**
     * Use the Maelstrom engine to deserialize xmlString into
     * a SOAPEnvelope
     */

    protected javax.xml.soap.SOAPEnvelope deserialize(String data)
       throws Exception
    {
    	IBMSOAPFactory sf = (IBMSOAPFactory) SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
    	return (SOAPEnvelope) sf.createElementFromXMLString(data, SOAPEnvelope.class);
    }

    /**
     * Counts the number of the occurences of the find String
     * in text.
     * @param text String
     * @param find String
     * @return number of occurences
     */
    private int countSubstrings(String text, String find) {
        int count = 0;
        int index = text.indexOf(find);
        while (index >= 0) {
            count++;
            index = text.indexOf(find, index+1);
        }
        return count;
    }
    
    /**
     * Counts the number of prefix definitions of the 
     * supplied prefix/namespace using SAAJ 1.1 methods.
     * This method counts the occurences in the supplied SOAPElement
     * plus all occurences in the SOAPElement's descendents. 
     */
    private int countPrefixes(javax.xml.soap.SOAPElement se, 
                              String prefix, 
                              String namespace) {
        Iterator it = se.getNamespacePrefixes();
        int count = 0;
        while (it.hasNext()) {
            String tryPrefix = (String) it.next();
            if (tryPrefix.equals(prefix) &&
                namespace.equals(se.getNamespaceURI(tryPrefix))) {
                count++;
            }
        }
        // Now count the prefixes in the children
        it = se.getChildElements();
        while (it.hasNext()) {
            javax.xml.soap.Node node = (javax.xml.soap.Node) it.next();
            if (node instanceof javax.xml.soap.SOAPElement) {
                count += countPrefixes((javax.xml.soap.SOAPElement) node, 
                                       prefix, namespace);
            }
        }
        return count;
    }

    /**
     * Counts the number of prefix definitions of the 
     * supplied prefix/namespace using DOM methods.
     * This method counts the occurences in the supplied SOAPElement
     * plus all occureces in the SOAPElement's descendents. 
     */
    private int countPrefixesDOM(Element se, 
                                 String prefix, 
                                 String namespace) {
        int count = 0;
        // The xmlns attributes are queried via the getAttribute method per "XML for Namespaces"
        String attrName;
        if (prefix.length() == 0) {
            attrName = "xmlns";
        } else {
            attrName = "xmlns:" + prefix;
        }
        String value = se.getAttribute(attrName);
        if (namespace.equals(value)) {
            count++;
        }
        NodeList list = se.getChildNodes();
        for (int i=0; i<list.getLength(); i++) {
            // Now count the prefixes in the children
            Node node = list.item(i);
            if (node instanceof Element) {
                count += countPrefixesDOM((Element) node, 
                                          prefix, namespace);
            }
        }
        return count;
    }

    // *********** END UTILITY METHODS ************//

}

