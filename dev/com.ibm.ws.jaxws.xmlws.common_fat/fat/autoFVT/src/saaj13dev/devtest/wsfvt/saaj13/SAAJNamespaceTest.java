//
// @(#) 1.2 autoFVT/src/saaj13dev/devtest/wsfvt/saaj13/SAAJNamespaceTest.java, WAS.websvcs.fvt, WASX.FVT 5/11/07 17:34:45 [7/23/07 09:49:05]
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
// 09/24/07 gkuo        438716          http://www.w3.org/TR/xml-exc-c14n/

package saaj13dev.devtest.wsfvt.saaj13;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;

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
 * This test was initially obtained from Axis, but it has been enormously enhanced to 
 * test SAAJ 1.2 methods and our engine code.
 * 
 * The setup*() methods use the SAAJ model to build a SOAPEnvelope.
 * In each case, the SOAPEnvelope should look like the following:
 *
 * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
 *                   xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" 
 *                   xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
 *                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
 *                   xmlns="http://default/"
 *                   xmlns:top="http://top/" 
 *                   xmlns:rep="http://rep/">
 *    <soapenv:Header>
 *       <BugReport rep:def="ghi" top:jkl="mno"
 *                   xmlns:rep="http://rep/"/>
 *    </soapenv:Header>
 *    <soapenv:Body xmlns:rep="http://rep/">
 *       <BugReport rep:def="ghi" top:jkl="mno"
 *                   xmlns:rep="http://rep/"/>
 *    </soapenv:Body>
 * </soapenv:Envelope>
 *
 * The key namespace declarations are top, rep and the default namespace.
 * 1) Each setup method only creates a "top" namespace declaration on the envelope.
 * The testTopNS tests make sure that the "top" namespace only occurs once in the
 * serialized xml string.  These tests are important to ensure that the "top" namespace
 * is not lost and is not repeated.
 *
 * 2) Each setup method creates a "rep" namespace on the envelope and on the elements
 * contained in the header and body.  The testRepNS tests make sure that the "rep"
 * namespace is repeated in the message 4 times.  These tests are important to ensure
 * that the "rep" namespace declarations are not "optimized" from the xml string.
 *
 * 3) Each setup method creates a default prefix on the envelope and uses it in the header
 * and body.  The tests ensure the default prefix is not lost or repeated.
 *
 * The setup methods use various construction patterns to build
 * the SAAJ model.  These construction patterns are designed to test the various and
 * subtle ways that clients or sp's could build a message.
 *
 * There are different ways to check if the constructed SAAJ model is correct.  For example,
 * the SOAPEnvelope can be serialialized (via toString) and the resulting xml string
 * can be queried to ensure the correct number of "top" (1) or "rep" (3) namespaces
 * occur in the string.
 *
 * The different combinations of setup and check result in a matix of tests.  
 * The individual tests are named to identify which setup/checking scheme is used.
 * Here are the current setup methods:
 *    setup1: Build using pure SAAJ 1.1 model
 *    setup2: Like setup1, but namespace decls added via DOM setAttributeNS methods
 *    setup3: Like setup1, but namespace decls added by accessing NamedNodeMap
 *    setup4: Deserialize xml string into SOAPEnvelope
 *    setup5: Like setup2, except extra attributes are added and then immediately removed.
 *    setup6: Like setup1, except header/body elements are DOM nodes added via DOM appendChild
 *    setup7: Like setup4, except xml string is attached to SOAPEnvelope as alternate content.      
 *    setup8: Like setup2, except setAttribute (non-NS) methods are used to add namespace decls.    
 *    setup9: Like setup7, except header and body set using InputSource
 *    setup10: Like setup6, except SOAPElements are attached via DOM appendChild.
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
public class SAAJNamespaceTest extends TestBase {

    private static final String TOP_PREFIX = "top";
    private static final String TOP_URI = "http://top/";
    private static final String REP_PREFIX = "rep";
    private static final String REP_URI = "http://rep/";
    private static final String DEFAULT_PREFIX = "";
    private static final String DEFAULT_URI = "http://default/";

   

    public SAAJNamespaceTest(String name) {
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
    public void testTopNS_1A() throws Exception{ 
        int count = checkA(setup1(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_1A() throws Exception{ 
        int count = checkA(setup1(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_1A() throws Exception{ 
        int count = checkA(setup1(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_2A() throws Exception{ 
        int count = checkA(setup2(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_2A() throws Exception{ 
        int count = checkA(setup2(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_2A() throws Exception{ 
        int count = checkA(setup2(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_3A() throws Exception{ 
        int count = checkA(setup3(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_3A() throws Exception{ 
        int count = checkA(setup3(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }
 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_3A() throws Exception{ 
        int count = checkA(setup3(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_4A() throws Exception{ 
        int count = checkA(setup4(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_4A() throws Exception{ 
        int count = checkA(setup4(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_4A() throws Exception{ 
        int count = checkA(setup4(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_5A() throws Exception{ 
        int count = checkA(setup5(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_5A() throws Exception{ 
        int count = checkA(setup5(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_5A() throws Exception{ 
        int count = checkA(setup5(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_6A() throws Exception{ 
        int count = checkA(setup6(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_6A() throws Exception{ 
        int count = checkA(setup6(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_6A() throws Exception{ 
        int count = checkA(setup6(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_7A() throws Exception{ 
        int count = checkA(setup7(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_7A() throws Exception{ 
        int count = checkA(setup7(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_7A() throws Exception{ 
        int count = checkA(setup7(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_8A() throws Exception{ 
        int count = checkA(setup8(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_8A() throws Exception{ 
        int count = checkA(setup8(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_8A() throws Exception{ 
        int count = checkA(setup8(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_9A() throws Exception{ 
        int count = checkA(setup9(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_9A() throws Exception{ 
        int count = checkA(setup9(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_9A() throws Exception{ 
        int count = checkA(setup9(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_10A() throws Exception{ 
        int count = checkA(setup10(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_10A() throws Exception{ 
        int count = checkA(setup10(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_10A() throws Exception{ 
        int count = checkA(setup10(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("No occurence of " + DEFAULT_URI + " were found", count >= 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_1B() throws Exception{ 
        int count = checkB(setup1(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_1B() throws Exception{ 
        int count = checkB(setup1(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_1B() throws Exception{ 
        int count = checkB(setup1(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_2B() throws Exception{ 
        int count = checkB(setup2(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_2B() throws Exception{ 
        int count = checkB(setup2(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_2B() throws Exception{ 
        int count = checkB(setup2(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_3B() throws Exception{ 
        int count = checkB(setup3(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_3B() throws Exception{ 
        int count = checkB(setup3(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_3B() throws Exception{ 
        int count = checkB(setup3(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_4B() throws Exception{ 
        int count = checkB(setup4(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_4B() throws Exception{ 
        int count = checkB(setup4(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_4B() throws Exception{ 
        int count = checkB(setup4(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_5B() throws Exception{ 
        int count = checkB(setup5(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_5B() throws Exception{ 
        int count = checkB(setup5(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_5B() throws Exception{ 
        int count = checkB(setup5(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_6B() throws Exception{
        int count = checkB(setup6(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_6B() throws Exception{ 
        int count = checkB(setup6(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_6B() throws Exception{
        int count = checkB(setup6(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_7B() throws Exception{  
        int count = checkB(setup7(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_7B() throws Exception{ 
        int count = checkB(setup7(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_7B() throws Exception{ 
        int count = checkB(setup7(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_8B() throws Exception{  
        int count = checkB(setup8(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_8B() throws Exception{ 
        int count = checkB(setup8(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_8B() throws Exception{ 
        int count = checkB(setup8(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_9B() throws Exception{  
        int count = checkB(setup9(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_9B() throws Exception{ 
        int count = checkB(setup9(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_9B() throws Exception{ 
        int count = checkB(setup9(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_10B() throws Exception{  
        int count = checkB(setup10(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_10B() throws Exception{ 
        int count = checkB(setup10(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_10B() throws Exception{ 
        int count = checkB(setup10(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("No occurence of " + DEFAULT_URI + " were found", count >= 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_1C() throws Exception{ 
        int count = checkC(setup1(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_1C() throws Exception{ 
        int count = checkC(setup1(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_1C() throws Exception{ 
        int count = checkC(setup1(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_2C() throws Exception{ 
        int count = checkC(setup2(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_2C() throws Exception{ 
        int count = checkC(setup2(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_2C() throws Exception{ 
        int count = checkC(setup2(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_3C() throws Exception{ 
        int count = checkC(setup3(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_3C() throws Exception{ 
        int count = checkC(setup3(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_3C() throws Exception{ 
        int count = checkC(setup3(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_4C() throws Exception{ 
        int count = checkC(setup4(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_4C() throws Exception{ 
        int count = checkC(setup4(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_4C() throws Exception{ 
        int count = checkC(setup4(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_5C() throws Exception{ 
        int count = checkC(setup5(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_5C() throws Exception{ 
        int count = checkC(setup5(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_5C() throws Exception{ 
        int count = checkC(setup5(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_6C() throws Exception{ 
        int count = checkC(setup6(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_6C() throws Exception{ 
        int count = checkC(setup6(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_6C() throws Exception{ 
        int count = checkC(setup6(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_7C() throws Exception{ 
        int count = checkC(setup7(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_7C() throws Exception{ 
        int count = checkC(setup7(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_7C() throws Exception{ 
        int count = checkC(setup7(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    /*
      test*8C tests will always fail because they are setup with non-NS dom attribute methods
      and checked with dom NS attribute methods.  This is expected; therefore the tests are excluded.
      (scheu)

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_8C() throws Exception{ 
        int count = checkC(setup8(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_8C() throws Exception{ 
        int count = checkC(setup8(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_8C() throws Exception{ 
        int count = checkC(setup8(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }
    */

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_9C() throws Exception{ 
        int count = checkC(setup9(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_9C() throws Exception{ 
        int count = checkC(setup9(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_9C() throws Exception{ 
        int count = checkC(setup9(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + DEFAULT_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testTopNS_10C() throws Exception{ 
        int count = checkC(setup10(), TOP_PREFIX, TOP_URI);
        assertTrue("Serious Integrity Problem: There should be 1 occurence of " + TOP_URI + " but " + count + " were found", count == 1);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRepNS_10C() throws Exception{ 
        int count = checkC(setup10(), REP_PREFIX, REP_URI);
        assertTrue("Serious Integrity Problem: There should be 4 occurences of " + REP_URI + " but " + count + " were found", count == 4);
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultNS_10C() throws Exception{ 
        int count = checkC(setup10(), DEFAULT_PREFIX, DEFAULT_URI);
        assertTrue("No occurence of " + DEFAULT_URI + " were found", count >= 1);
    }

    // ***** END OF TESTS *********** //

    /**
     * This method constructs a SOAPEnvelope using the
     * only the SAAJ 1.1 methods.
     */
    private javax.xml.soap.SOAPEnvelope setup1() throws Exception{  
    javax.xml.soap.SOAPMessage message = 
            javax.xml.soap.MessageFactory.newInstance().createMessage();
        javax.xml.soap.SOAPEnvelope env = 
            message.getSOAPPart().getEnvelope();
        env.addNamespaceDeclaration(DEFAULT_PREFIX, DEFAULT_URI);
        env.addNamespaceDeclaration(TOP_PREFIX, TOP_URI);
        env.addNamespaceDeclaration(REP_PREFIX, REP_URI);

        javax.xml.soap.Name name =
            env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.SOAPHeader header = env.getHeader();
        javax.xml.soap.SOAPHeaderElement she = header.addHeaderElement(name);
        she.addNamespaceDeclaration(REP_PREFIX, REP_URI);
        name = env.createName("def", REP_PREFIX, REP_URI);
        she.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        she.addAttribute(name, "mno");

        javax.xml.soap.SOAPBody body = env.getBody();
        body.addNamespaceDeclaration(REP_PREFIX, REP_URI);
        name = env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.SOAPBodyElement sbe = body.addBodyElement(name);
        sbe.addNamespaceDeclaration(REP_PREFIX, REP_URI);
        name = env.createName("def", REP_PREFIX, REP_URI);
        sbe.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        sbe.addAttribute(name, "mno");

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
            javax.xml.soap.MessageFactory.newInstance().createMessage();
        javax.xml.soap.SOAPEnvelope env = 
            message.getSOAPPart().getEnvelope();
        env.setAttributeNS(URI_XMLNS, "xmlns", DEFAULT_URI);
        env.setAttributeNS(URI_XMLNS, "xmlns:" + TOP_PREFIX, TOP_URI);
        env.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);

        javax.xml.soap.Name name =
            env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.SOAPHeader header = env.getHeader();
        javax.xml.soap.SOAPHeaderElement she = header.addHeaderElement(name);
        she.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        name = env.createName("def", REP_PREFIX, REP_URI);
        she.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        she.addAttribute(name, "mno");

        javax.xml.soap.SOAPBody body = env.getBody();
        body.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);

        name = env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.SOAPBodyElement sbe = body.addBodyElement(name);
        sbe.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        name = env.createName("def", REP_PREFIX, REP_URI);
        sbe.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        sbe.addAttribute(name, "mno");

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
            javax.xml.soap.MessageFactory.newInstance().createMessage();
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
        attr = 
            env.getOwnerDocument().createAttributeNS(URI_XMLNS,  
                                              "xmlns");
        attr.setValue(DEFAULT_URI);
        attributes.setNamedItemNS(attr);

        javax.xml.soap.Name name =
            env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.SOAPHeader header = env.getHeader();
        javax.xml.soap.SOAPHeaderElement she = header.addHeaderElement(name);
        attributes = she.getAttributes();
        attr = 
            env.getOwnerDocument().createAttributeNS(URI_XMLNS,  
                                                     "xmlns:" + REP_PREFIX);
        attr.setValue(REP_URI);
        attributes.setNamedItemNS(attr);
        name = env.createName("def", REP_PREFIX, REP_URI);
        she.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        she.addAttribute(name, "mno");


        javax.xml.soap.SOAPBody body = env.getBody();
        attributes = body.getAttributes();
        attr = 
            env.getOwnerDocument().createAttributeNS(URI_XMLNS,  
                                                     "xmlns:" + REP_PREFIX);
        attr.setValue(REP_URI);
        attributes.setNamedItemNS(attr);

        name = env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.SOAPBodyElement sbe = body.addBodyElement(name);
        attributes = sbe.getAttributes();
        attr = 
            env.getOwnerDocument().createAttributeNS(URI_XMLNS,  
                                                     "xmlns:" + REP_PREFIX);
        attr.setValue(REP_URI);
        attributes.setNamedItemNS(attr);
        name = env.createName("def", REP_PREFIX, REP_URI);
        sbe.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        sbe.addAttribute(name, "mno");


    return env;
    }

    /**
     * Use Maelstrom engine to build SOAPEnvelope
     */
    private javax.xml.soap.SOAPEnvelope setup4() throws Exception {
        javax.xml.soap.SOAPEnvelope env = setup1();  // build envelope using setup1 (most stable)
        String xmlString = env.toString();  // convert into string
        env = deserialize(xmlString); // Deserialize into SOAPEnvelope
        SOAPBody body = (SOAPBody) env.getBody();
        assertTrue(body != null);
        return env;
    }

    /**
     * Like setup2, except extra namespace/prefix defintions are added and then immediately
     * removed.
     */
    private javax.xml.soap.SOAPEnvelope setup5() throws Exception{  
    javax.xml.soap.SOAPMessage message = 
            javax.xml.soap.MessageFactory.newInstance().createMessage();
        javax.xml.soap.SOAPEnvelope env = 
            message.getSOAPPart().getEnvelope();
        env.setAttributeNS(URI_XMLNS, "xmlns", DEFAULT_URI);
        env.setAttributeNS(URI_XMLNS, "xmlns:" + TOP_PREFIX, TOP_URI);
        env.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);

        javax.xml.soap.Name name =
            env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.SOAPHeader header = env.getHeader();
        javax.xml.soap.SOAPHeaderElement she = header.addHeaderElement(name);
        she.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        she.setAttributeNS(URI_XMLNS, "xmlns:" + TOP_PREFIX, TOP_URI);
        she.setAttributeNS(URI_XMLNS,"xmlns:" + TOP_PREFIX, TOP_URI);
        she.setAttributeNS(URI_XMLNS, "xmlns", DEFAULT_URI);
        she.removeAttributeNS(URI_XMLNS,TOP_PREFIX);
        she.removeAttributeNS(URI_XMLNS, "xmlns");
        name = env.createName("def", REP_PREFIX, REP_URI);
        she.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        she.addAttribute(name, "mno");


        javax.xml.soap.SOAPBody body = env.getBody();
        body.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        body.setAttributeNS(URI_XMLNS,"xmlns", DEFAULT_URI);
        body.removeAttributeNS(URI_XMLNS,"xmlns");

        name = env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.SOAPBodyElement sbe = body.addBodyElement(name);
        sbe.setAttributeNS(URI_XMLNS,"xmlns", DEFAULT_URI);
        sbe.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        sbe.setAttributeNS(URI_XMLNS, "xmlns:" + TOP_PREFIX, TOP_URI);
        sbe.removeAttributeNS(URI_XMLNS,TOP_PREFIX);
        sbe.removeAttributeNS(URI_XMLNS,"xmlns");
        name = env.createName("def", REP_PREFIX, REP_URI);
        sbe.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        sbe.addAttribute(name, "mno");

    return env;
    }

    /**
     * Similar to setup1, except the elements in the header/body are
     * DOM nodes that are added via DOM apis.
     */
    private javax.xml.soap.SOAPEnvelope setup6() throws Exception{  

        // Create SOAPEnvelope
    javax.xml.soap.SOAPMessage message = 
            javax.xml.soap.MessageFactory.newInstance().createMessage();
        javax.xml.soap.SOAPEnvelope env = 
            message.getSOAPPart().getEnvelope();
        env.addNamespaceDeclaration(DEFAULT_PREFIX, DEFAULT_URI);
        env.addNamespaceDeclaration(TOP_PREFIX, TOP_URI);
        env.addNamespaceDeclaration(REP_PREFIX, REP_URI);

        // Get the header and body
        javax.xml.soap.SOAPHeader header = env.getHeader();
        javax.xml.soap.SOAPBody body = env.getBody();
        body.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);

        // Create a DOM node for the header element
        Document document = env.getOwnerDocument();
        Element she = document.createElementNS(DEFAULT_URI, "BugReport");
        she.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        she.setAttributeNS(REP_URI, REP_PREFIX + ":def", "ghi");
        she.setAttributeNS(TOP_URI, TOP_PREFIX + ":jkl", "mno");
        header.appendChild(she);  // Append the node

        Element sbe = document.createElementNS(DEFAULT_URI, "BugReport");
        sbe.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        sbe.setAttributeNS(REP_URI, REP_PREFIX + ":def", "ghi");
        sbe.setAttributeNS(TOP_URI, TOP_PREFIX + ":jkl", "mno");
        body.appendChild(sbe);  // Append the node

    return env;
    }

    /**
     * Setup envelope using setAlternateContent(string) on envelope
     */
    private javax.xml.soap.SOAPEnvelope setup7() throws Exception {
        javax.xml.soap.SOAPEnvelope env = setup1();  // build envelope using setup1 (most stable)
        String xmlString = env.toString();  // convert into string

        // Use factory to create Element from XMLString
        IBMSOAPFactory sf = (IBMSOAPFactory) SOAPFactory.newInstance();
        env = (javax.xml.soap.SOAPEnvelope) 
            sf.createElementFromXMLString(xmlString, 
                                          javax.xml.soap.SOAPEnvelope.class);
        return env; 
    }

    /**
     * Similar to setup2, except the namespaces are added via
     * DOM setAttribute (non-NS) method.  These settings come in via
     * a "back-door" and we need to make sure that our SAAJ model
     * recognizes the namespace/prefix declarations.
     */
    private javax.xml.soap.SOAPEnvelope setup8() throws Exception{  
    javax.xml.soap.SOAPMessage message = 
            javax.xml.soap.MessageFactory.newInstance().createMessage();
        javax.xml.soap.SOAPEnvelope env = 
            message.getSOAPPart().getEnvelope();
        env.setAttribute("xmlns", DEFAULT_URI);
        env.setAttribute("xmlns:" + TOP_PREFIX, TOP_URI);
        env.setAttribute("xmlns:" + REP_PREFIX, REP_URI);

        javax.xml.soap.Name name =
            env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.SOAPHeader header = env.getHeader();
        javax.xml.soap.SOAPHeaderElement she = header.addHeaderElement(name);
        she.setAttribute("xmlns:" + REP_PREFIX, REP_URI);
        name = env.createName("def", REP_PREFIX, REP_URI);
        she.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        she.addAttribute(name, "mno");

        javax.xml.soap.SOAPBody body = env.getBody();
        body.setAttribute("xmlns:" + REP_PREFIX, REP_URI);
        name = env.createName("BugReport", DEFAULT_PREFIX, DEFAULT_URI);
        javax.xml.soap.SOAPBodyElement sbe = body.addBodyElement(name);
        sbe.setAttribute("xmlns:" + REP_PREFIX, REP_URI);
        name = env.createName("def", REP_PREFIX, REP_URI);
        sbe.addAttribute(name, "ghi");
        name = env.createName("jkl", TOP_PREFIX, TOP_URI);
        sbe.addAttribute(name, "mno");

    return env;
    }

    /**
     * Setup envelope using setAlternateContent(InputStream) on header and body
     */
    private javax.xml.soap.SOAPEnvelope setup9() throws Exception {
        IBMSOAPEnvelope env = (IBMSOAPEnvelope) setup1();  // build envelope using setup1 (most stable)
        SOAPHeader header = (SOAPHeader) env.getHeader();
        SOAPBody body = (SOAPBody) env.getBody();
        // Get header and body xml strings
        String headerString = "";
        String bodyString = "";
        Iterator it = header.getChildElements();
        while (it.hasNext()) {
            IBMSOAPElement se = (IBMSOAPElement) it.next();
            headerString += se.toXMLString(false);  // Append text
        }
        it = body.getChildElements();
        while(it.hasNext()) {
            IBMSOAPElement se = (IBMSOAPElement) it.next();
            bodyString += se.toXMLString(false);  // Append text
        }
        //Replace contents with InputStream
        header.removeContents();
        body.removeContents();
        ((com.ibm.ws.webservices.engine.xmlsoap.SOAPHeader)header).
        	setAlternateContent(new ByteArrayInputStream(headerString.getBytes()));
        ((com.ibm.ws.webservices.engine.xmlsoap.SOAPBody)body).
        	setAlternateContent(new ByteArrayInputStream(bodyString.getBytes()));
        return env; 
    }

    /**
     * Similar to setup6, except the elements in the header/body are
     * raw SOAPElements that are added via DOM apis.
     */
    private javax.xml.soap.SOAPEnvelope setup10() throws Exception{ 

        // Create SOAPEnvelope
    javax.xml.soap.SOAPMessage message = 
            javax.xml.soap.MessageFactory.newInstance().createMessage();
        SOAPEnvelope env = (SOAPEnvelope) 
            message.getSOAPPart().getEnvelope();
        env.addNamespaceDeclaration(DEFAULT_PREFIX, DEFAULT_URI);
        env.addNamespaceDeclaration(TOP_PREFIX, TOP_URI);
        env.addNamespaceDeclaration(REP_PREFIX, REP_URI);

        // Detach the header and the body
        javax.xml.soap.SOAPHeader header = env.getHeader();
        javax.xml.soap.SOAPBody body = env.getBody();
        header.detachNode();
        body.detachNode();

        // Create SOAPElements representing the header and body
        // Note that I am intentionally creating raw SOAPElements instead
        // of header and body elements.  The model should do the proper 
        // transformation.
        IBMSOAPFactory sf = (IBMSOAPFactory) ((IBMSOAPEnvelope) env).getIBMSOAPFactory();
        SOAPElement seHeader = (SOAPElement)
            sf.createElement("Header", env.getPrefix(), env.getNamespaceURI());
        SOAPElement seBody   = (SOAPElement) 
            sf.createElement("Body", env.getPrefix(), env.getNamespaceURI());
        seHeader = (SOAPElement) env.appendChild(seHeader);
        seBody = (SOAPElement) env.appendChild(seBody);

        assertTrue("Header must be a SOAPHeader", seHeader instanceof SOAPHeader);
        assertTrue("Body must be a SOAPBody", seBody instanceof SOAPBody);

        header = env.getHeader();
        body = env.getBody();
        body.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);

        // Create a SOAPElement for the header
        // Note that I am intentionally creating raw SOAPElements instead
        // of SOAPHeaderElement and SOAPBodyElement objects.  The model should do the proper 
        // transformation.
        SOAPElement she = (SOAPElement)
            sf.createElement("BugReport", "", DEFAULT_URI);
        she.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        she.setAttributeNS(REP_URI, REP_PREFIX + ":def", "ghi");
        she.setAttributeNS(TOP_URI, TOP_PREFIX + ":jkl", "mno");
        header.appendChild(she);  // Append the node

        // Create a SOAPElement for the body.
        SOAPElement sbe = (SOAPElement)
            sf.createElement("BugReport", "", DEFAULT_URI);

        // Test create from DOMElement on self
        sbe = (SOAPBodyElement) sf.createElementFromDOMElement(sbe, javax.xml.soap.SOAPBodyElement.class);

        sbe.setAttributeNS(URI_XMLNS, "xmlns:" + REP_PREFIX, REP_URI);
        sbe.setAttributeNS(REP_URI, REP_PREFIX + ":def", "ghi");
        sbe.setAttributeNS(TOP_URI, TOP_PREFIX + ":jkl", "mno");
        body.appendChild(sbe);  // Append the node

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
     * Use the WebSMO to deserialize xmlString into
     * a SOAPEnvelope
     */
    protected javax.xml.soap.SOAPEnvelope deserialize(String data)
       throws Exception
    {
    	IBMSOAPFactory sf = (IBMSOAPFactory) SOAPFactory.newInstance();
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
     * Counts the number of prefix defintitions of the 
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
        // According to the XML Namespaces specification, the 
        // xmlns:prefix attributes are obtained without a namespace
        String attrName;
        if (prefix.length() == 0) {
            attrName = "xmlns";
        } else {
            attrName = "xmlns:"+prefix;
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
