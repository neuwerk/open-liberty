//
// @(#) 1.1 WautoFVT/src/saaj13dev/devtest/wsfvt/saaj13/SAAJ13FaultTest.java, WAS.websvcs.fvt, WSFP.WFVT, a0639.05 8/2/06 08:56:02 [9/28/06 13:56:18]
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
import java.util.Locale;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import com.ibm.websphere.webservices.soap.IBMDetailEntry;
import com.ibm.websphere.webservices.soap.IBMSOAPFactory;
import com.ibm.ws.webservices.engine.xmlsoap.SOAPConstants;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;

/**
 * SOAP 1.2 substantially changed and improved the 
 * SOAPFault construct.  SAAJ 1.3 has substantial additions
 * to support the SOAP 1.3 Faults.  This includes the SAAJ 1.3
 * changes: C004, C010, C011, C013, C015, C018, C020, etc.
 * 
 * I am using a "matrix" test strategy.  This strategy has been helpful in the
 * past to force lots of code paths and force breakage.
 * 
 * The same message is loaded several ways and then checked in several ways
 * 
 * Load strategies:
 *   * Load A: Parse the envelope string
 *        - This is the normal load
 *   * Load B: Use Load A and then transform the underlying data model to a WebServicesFault
 *        - This stresses WebServicesFault building code (which is used in Maelstrom)
 *   * Load C: Use B and then reserialize and reparse the message
 *        - This stresses WebServicesFault serialization
 *   * Load D: Use Load A and then the message is serialized and reparsed
 *        - This stresses the normal serialization and deserialization of SOAPFault
 *   * Load E: The fault is built using the SOAP methods
 *        - Stresses the SOAPFault methods
 *   * Load F: Use Load E and then transform to a WebServicesFault
 *        - Stresses WebSerivcesFault/SAAJ processing
 *   * Load G: Use Load F and then serialize and deserialize to SOAPFault
 *        - Further serialization and deserialization testing
 *   * Load H: Create Fault using SOAPBody.addFault(...)
 *   * Load I: Create Fault using SAAJ 1.3 SOAPBody.addFault(...)
 *        
 *  Check strategies
 *   * Check 1: Use the SAAJ 1.2 methods to examine the SOAPFault contents
 *        - Normal stresses of SAAJ methods..also makes sure that the SAAJ 1.2 methods
 *          work on a SOAP 1.2 message.
 *   * Check 2: Use the DOM methods to examine the SOAPFault contents
 *        - These checks make sure that the SOAPFault contents are ordered correctly
 *          and are DOM-ready.  They also check that the namespaces and ordering comply
 *          with the protocol (the ordering/names are different with SOAP 1.1/1.2)
 *   * Check 3: Use SAAJ 1.3 methods to examine the SOAPFault contents
 *        - For a SOAP 1.1 message, this check ensures that the proper exception is thrown
 *        - For SOAP 1.2 messages, this check ensures that all of the values are correct.   
 *        
 * There are currently 9 Loads * 3 Checks * 2 Protocols = 54 Tests
 */
public class SAAJ13FaultTest extends TestBase {

	public SAAJ13FaultTest() {
		super();
	}

	public SAAJ13FaultTest(String arg0) {
		super(arg0);
	}
	
	 protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
	     System.out.println("Do not need suiteSetup since no application is installed");    
	 }

	// Sample SOAP 1.1 Fault Message
	private String soap11TextStart = 
	 	 " <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"  \n" +
	     "                   xmlns:top=\"http://top/\" > \n" +
	     "    <soapenv:Header>\n" +
	     "       <top:TestHeader> \n" +
	     "         <top:Content/> \n" +
	     "       </top:TestHeader> \n" +
	     "    </soapenv:Header>\n" +
	     "    <soapenv:Body>\n" +
	     "      <!-- Sample Comment -->\n";
	private String soap11TextFault =
	     "       <soapenv:Fault> \n" +
	     "          <faultcode xmlns=\"\">soapenv:Server</faultcode>\n" +
	     "          <faultstring xmlns=\"\">Test</faultstring>\n" +
	     "          <faultactor xmlns=\"\">urn://worker</faultactor>\n" +
	     "          <detail xmlns=\"\"><top:TestDetail><top:Content/></top:TestDetail></detail>\n" +
	     "       </soapenv:Fault> \n";
	private String soap11TextEnd =
	     "    </soapenv:Body>\n" +
	     " </soapenv:Envelope>\n";
	
	private String soap11Text = soap11TextStart + soap11TextFault + soap11TextEnd;
	private String soap11TextEnv = soap11TextStart + soap11TextEnd;
	
	// Sample SOAP 1.2 Fault Message
	
	private String soap12TextStart = 
	 	 " <soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"  \n" +
	     "                   xmlns:top=\"http://top/\" > \n" +
	     "    <soapenv:Header>\n" +
	     "       <top:TestHeader> \n" +
	     "         <top:Content/> \n" +
	     "       </top:TestHeader> \n" +
	     "    </soapenv:Header>\n" +
	     "    <soapenv:Body>\n" 
	// TODO LI4238 Problem with Sample Comment   +  "      "       <!-- Sample Comment -->\n" +
	     ;
	private String soap12TextFault =
	     "       <soapenv:Fault>\n" +
	     "          <soapenv:Code>\n" +
	     "            <soapenv:Value>soapenv:Sender</soapenv:Value>\n" +
	     "              <soapenv:Subcode>\n" +
	     "                <soapenv:Value>top:subcode1</soapenv:Value>\n" +
	     "                <soapenv:Subcode>\n" +
	     "                  <soapenv:Value>top:subcode2</soapenv:Value>\n" +
	     "                </soapenv:Subcode>\n" +
	     "              </soapenv:Subcode>\n" +
	     "          </soapenv:Code>\n" +
	     "          <soapenv:Reason>\n" +
	     "             <soapenv:Text xml:lang=\"en\">Test</soapenv:Text>\n" +
	     "             <soapenv:Text xml:lang=\"de\">Test (de)</soapenv:Text>\n" +
	     "             <soapenv:Text xml:lang=\"fr\">Test (fr)</soapenv:Text>\n" +
	     "          </soapenv:Reason>\n" +
	     "          <soapenv:Node>urn://testNode</soapenv:Node>\n" +
	     "          <soapenv:Role>urn://worker</soapenv:Role>\n" +
	     "          <soapenv:Detail><top:TestDetail><top:Content/></top:TestDetail></soapenv:Detail>\n" +
	     "       </soapenv:Fault>\n";
	private String soap12TextEnd =
	     "    </soapenv:Body>\n" +
	     " </soapenv:Envelope>\n";
		
	private String soap12Text = soap12TextStart + soap12TextFault + soap12TextEnd;
	private String soap12TextEnv = soap12TextStart + soap12TextEnd;
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadA_Check1() throws Exception {
		SOAPFault fault = loadA(soap11Text);
		checkSOAP11_1(fault);
	}
	
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadB_Check1() throws Exception {
		SOAPFault fault = loadB(soap11Text);
		checkSOAP11_1(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadC_Check1() throws Exception {
		SOAPFault fault = loadC(soap11Text);
		checkSOAP11_1(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadD_Check1() throws Exception {
		SOAPFault fault = loadD(soap11Text);
		checkSOAP11_1(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadE_Check1() throws Exception {
		SOAPFault fault = loadE_SOAP11();
		checkSOAP11_1(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadF_Check1() throws Exception {
		SOAPFault fault = loadF_SOAP11();
		checkSOAP11_1(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadG_Check1() throws Exception {
		SOAPFault fault = loadG_SOAP11();
		checkSOAP11_1(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadH_Check1() throws Exception {
		SOAPFault fault = loadH_SOAP11();
		checkSOAP11_1(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadI_Check1() throws Exception {
		SOAPFault fault = loadI_SOAP11();
		checkSOAP11_1(fault);
	}
	
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadA_Check2() throws Exception {
		SOAPFault fault = loadA(soap11Text);
		checkSOAP11_2(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadB_Check2() throws Exception {
		SOAPFault fault = loadB(soap11Text);
		checkSOAP11_2(fault);
	}	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadC_Check2() throws Exception {
		SOAPFault fault = loadC(soap11Text);
		checkSOAP11_2(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadD_Check2() throws Exception {
		SOAPFault fault = loadC(soap11Text);
		checkSOAP11_2(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadE_Check2() throws Exception {
		SOAPFault fault = loadE_SOAP11();
		checkSOAP11_2(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadF_Check2() throws Exception {
		SOAPFault fault = loadF_SOAP11();
		checkSOAP11_2(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadG_Check2() throws Exception {
		SOAPFault fault = loadG_SOAP11();
		checkSOAP11_2(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadH_Check2() throws Exception {
		SOAPFault fault = loadH_SOAP11();
		checkSOAP11_2(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadI_Check2() throws Exception {
		SOAPFault fault = loadI_SOAP11();
		checkSOAP11_2(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadA_Check3() throws Exception {
		SOAPFault fault = loadA(soap11Text);
		checkSOAP11_3(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadB_Check3() throws Exception {
		SOAPFault fault = loadB(soap11Text);
		checkSOAP11_3(fault);
	}	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadC_Check3() throws Exception {
		SOAPFault fault = loadC(soap11Text);
		checkSOAP11_3(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadD_Check3() throws Exception {
		SOAPFault fault = loadC(soap11Text);
		checkSOAP11_3(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadE_Check3() throws Exception {
		SOAPFault fault = loadE_SOAP11();
		checkSOAP11_3(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadF_Check3() throws Exception {
		SOAPFault fault = loadF_SOAP11();
		checkSOAP11_3(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadG_Check3() throws Exception {
		SOAPFault fault = loadG_SOAP11();
		checkSOAP11_3(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test11_LoadH_Check3() throws Exception {
		SOAPFault fault = loadH_SOAP11();
		checkSOAP11_3(fault);
	}public void test11_LoadI_Check3() throws Exception {
		SOAPFault fault = loadI_SOAP11();
		checkSOAP11_3(fault);
	}
	
	// SOAP 1.2 Tests
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadA_Check1() throws Exception {
		SOAPFault fault = loadA(soap12Text);
		checkSOAP12_1(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadB_Check1() throws Exception {
		SOAPFault fault = loadB(soap12Text);
		checkSOAP12_1(fault);
		
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadC_Check1() throws Exception {
		SOAPFault fault = loadC(soap12Text);
		checkSOAP12_1(fault);
		
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadD_Check1() throws Exception {
		SOAPFault fault = loadD(soap12Text);
		checkSOAP12_1(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadE_Check1() throws Exception {
		SOAPFault fault = loadE_SOAP12();
		checkSOAP12_1(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadF_Check1() throws Exception {
		SOAPFault fault = loadF_SOAP12();
		checkSOAP12_1(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadG_Check1() throws Exception {
		SOAPFault fault = loadG_SOAP12();
		checkSOAP12_1(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadH_Check1() throws Exception {
		SOAPFault fault = loadH_SOAP12();
		checkSOAP12_1(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadI_Check1() throws Exception {
		SOAPFault fault = loadI_SOAP12();
		checkSOAP12_1(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadA_Check2() throws Exception {
		SOAPFault fault = loadA(soap12Text);
		checkSOAP12_2(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadB_Check2() throws Exception {
		SOAPFault fault = loadB(soap12Text);
		checkSOAP12_2(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadC_Check2() throws Exception {
		SOAPFault fault = loadC(soap12Text);
		checkSOAP12_2(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadD_Check2() throws Exception {
		SOAPFault fault = loadD(soap12Text);
		checkSOAP12_2(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadE_Check2() throws Exception {
		SOAPFault fault = loadE_SOAP12();
		checkSOAP12_2(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadF_Check2() throws Exception {
		SOAPFault fault = loadF_SOAP12();
		checkSOAP12_2(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadG_Check2() throws Exception {
		SOAPFault fault = loadG_SOAP12();
		checkSOAP12_2(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadH_Check2() throws Exception {
		SOAPFault fault = loadH_SOAP12();
		checkSOAP12_2(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadI_Check2() throws Exception {
		SOAPFault fault = loadI_SOAP12();
		checkSOAP12_2(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadA_Check3() throws Exception {
		SOAPFault fault = loadA(soap12Text);
		checkSOAP12_3(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadB_Check3() throws Exception {
		SOAPFault fault = loadB(soap12Text);
		checkSOAP12_3(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadC_Check3() throws Exception {
		SOAPFault fault = loadC(soap12Text);
		checkSOAP12_3(fault);
	}
	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadD_Check3() throws Exception {
		SOAPFault fault = loadD(soap12Text);
		checkSOAP12_3(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadE_Check3() throws Exception {
		SOAPFault fault = loadE_SOAP12();
		checkSOAP12_3(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadF_Check3() throws Exception {
		SOAPFault fault = loadF_SOAP12();
		checkSOAP12_3(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadG_Check3() throws Exception {
		SOAPFault fault = loadG_SOAP12();
		checkSOAP12_3(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadH_Check3() throws Exception {
		SOAPFault fault = loadH_SOAP12();
		checkSOAP12_3(fault);
	}
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test12_LoadI_Check3() throws Exception {
		SOAPFault fault = loadI_SOAP12();
		checkSOAP12_3(fault);
	}
	// -------------------------------------------------------------------------
	/**
	 * Load A: Builds a SOAPFault from the intput Text
	 * @param xmlText
	 * @return
	 */
	private SOAPFault loadA(String xmlText) throws Exception {
		// Create SOAPEnvelope from Text
		SOAPEnvelope env = deserialize(xmlText);
		
		// Get the SOAPFault
		SOAPBody body = env.getBody();
		SOAPFault fault = body.getFault();
		return fault;
	}
	
	/**
	 * Load A: Like Load A except that the SOAPFault underlying data structure
	 * is demarshalled into a WebServicesFault.  This tests the WebServicesFault code
	 * @param xmlText
	 * @return
	 */
	private SOAPFault loadB(String xmlText) throws Exception {
		SOAPFault fault = loadA(xmlText);
		// Use implementation class to convert underlying data to WebServicesFault
		// Note that converting to WebServicesFault is a lossy transformation.
		// Some information (whitespace, prefixes, etc.) will not be preserved.
		((com.ibm.ws.webservices.engine.xmlsoap.SOAPFault)fault).getFault();
		return fault;
	}
	
	/**
	 * Load C: Like Load B except that the SOAPFault is serialized and reparsed.
	 * This tests the serialization/deserialization logic with WebServicesFault.
	 * @param xmlText
	 * @return
	 */
	private SOAPFault loadC(String xmlText) throws Exception {
		// Create SOAPEnvelope from Text
		SOAPEnvelope env = deserialize(xmlText);
		
		// Get the SOAPFault
		SOAPBody body = env.getBody();
		SOAPFault fault = body.getFault();
		
		// Use implementation class to convert underlying data to WebServicesFault
		// Note that converting to WebServicesFault is a lossy transformation.
		// Some information (whitespace, prefixes, etc.) will not be preserved.
		((com.ibm.ws.webservices.engine.xmlsoap.SOAPFault)fault).getFault();
		
		return loadA(env.toString());
	}
	
	/**
	 * Load D: Like Load A except that the SOAPFault is serialized and reparsed.
	 * This tests the serialization/deserialization logic.
	 * @param xmlText
	 * @return
	 */
	private SOAPFault loadD(String xmlText) throws Exception {
		// Create SOAPEnvelope from Text
		SOAPEnvelope env = deserialize(xmlText);
		
		// Get the SOAPFault...This forces a parse
		SOAPBody body = env.getBody();
		SOAPFault fault = body.getFault();
		
		return loadA(env.toString());
	}
	
	/**
	 * Load E: Build using SAAJ 1.2 methods
	 * @return SOAPFault
	 */
	private SOAPFault loadE_SOAP11() throws Exception {
		SOAPEnvelope env = deserialize(soap11TextEnv);
		SOAPBody body = env.getBody();
		
		// Create the fault
		SOAPFault fault = body.addFault();
		
		// faultCode
		QName faultCodeValue = new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Server", "soapenv");
		fault.setFaultCode(faultCodeValue);
		
		// faultString
		fault.setFaultString("Test", Locale.getDefault());
		
		// faultActor
		fault.setFaultActor("urn://worker");
		
		// detail
		Detail detail = fault.addDetail();
		// Test SAAJ1.3 addDetailEntry(QName)
		QName qname = fault.createQName("TestDetail", "top");
		DetailEntry detailEntry = detail.addDetailEntry(qname);
		
		detailEntry.addChildElement("Content", "top", "http://top/");
		return fault;
	}
	
	/**
	 * Like loadE except the message is serialized and rebuilt
	 * @return
	 * @throws Exception
	 */
	private SOAPFault loadF_SOAP11() throws Exception {
		SOAPEnvelope env = deserialize(soap11TextEnv);
		SOAPBody body = env.getBody();
		
		// Create the fault using addBodyElement (to test C0011)
		QName faultQName= body.createQName("Fault", "soapenv");
		SOAPFault fault = (SOAPFault) body.addBodyElement(faultQName);
		
		// faultCode
		QName faultCodeValue = new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Server", "soapenv");
		fault.setFaultCode(faultCodeValue);
		
		// faultString
		fault.setFaultString("Test", Locale.getDefault());
		
		// faultActor
		fault.setFaultActor("urn://worker");
		
		// detail
		Detail detail = fault.addDetail();
		// Test SAAJ1.3 addDetailEntry(QName)
		QName qname = fault.createQName("TestDetail", "top");
		DetailEntry detailEntry = detail.addDetailEntry(qname);
		
		detailEntry.addChildElement("Content", "top", "http://top/");

		env = (SOAPEnvelope) fault.getParentNode().getParentNode();
		return loadA(env.toString());
	}
	
	/**
	 * Like loadE except the message is transformed into a WebServicesFault
	 * @return
	 * @throws Exception
	 */
	private SOAPFault loadG_SOAP11() throws Exception {
		SOAPFault fault = loadE_SOAP11();
		// Use implementation class to convert underlying data to WebServicesFault
		// Note that converting to WebServicesFault is a lossy transformation.
		// Some information (whitespace, prefixes, etc.) will not be preserved.
		((com.ibm.ws.webservices.engine.xmlsoap.SOAPFault)fault).getFault();
		return fault;
	}
	
	/**
	 * Load H: Build using Body addFault
	 * @return SOAPFault
	 */
	private SOAPFault loadH_SOAP11() throws Exception {
		SOAPEnvelope env = deserialize(soap11TextEnv);
		SOAPBody body = env.getBody();
		
		// Create the fault
		Name faultCodeValue = env.createName("Server", "soapenv", SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE);
		SOAPFault fault = body.addFault(faultCodeValue, "Test", Locale.getDefault());
		
		// faultActor
		fault.setFaultActor("urn://worker");
		
		// detail
		Detail detail = fault.addDetail();
		DetailEntry detailEntry = (DetailEntry) detail.addChildElement("TestDetail", "top", "http://top/");
		detailEntry.addChildElement("Content", "top", "http://top/");
		return fault;
	}
	
	/**
	 * Load E: Build using SAAJ 1.3 Body addFault
	 * @return SOAPFault
	 */
	private SOAPFault loadI_SOAP11() throws Exception {
		SOAPEnvelope env = deserialize(soap11TextEnv);
		SOAPBody body = env.getBody();
		
		// Create the fault
		QName faultCodeValue = body.createQName("Server", "soapenv");
		SOAPFault fault = body.addFault(faultCodeValue, "Test", Locale.getDefault());
		
		// faultActor
		fault.setFaultActor("urn://worker");
		
		// detail
		Detail detail = fault.addDetail();
		DetailEntry detailEntry = (DetailEntry) detail.addChildElement("TestDetail", "top", "http://top/");
		detailEntry.addChildElement("Content", "top", "http://top/");
		return fault;
	}
	
	
	
	/**
	 * Load E: Build using SAAJ 1.3 methods
	 * @return SOAPFault
	 */
	private SOAPFault loadE_SOAP12() throws Exception {
		SOAPEnvelope env = deserialize(soap12TextEnv);
		SOAPBody body = env.getBody();
		
		// Create the fault
		SOAPFault fault = body.addFault();
		
		// Code
		QName faultCodeValue = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Sender", "soapenv");
		fault.setFaultCode(faultCodeValue);
		
		// Subcodes
		fault.appendFaultSubcode(new QName("http://top/", "subcode1", "top"));
		fault.appendFaultSubcode(new QName("http://top/", "subcode2", "top"));
		
		// Reason
		fault.addFaultReasonText("Test", Locale.getDefault());
		fault.addFaultReasonText("Test (de)", new Locale("de"));
		fault.addFaultReasonText("Test (fr)", new Locale("fr"));
		
		// Node
		fault.setFaultNode("urn://testNode");
		
		// Role
		fault.setFaultRole("urn://worker");
		
		// Detail
		Detail detail = fault.addDetail();
		// Test SAAJ1.3 addDetailEntry(QName)
		QName qname = fault.createQName("TestDetail", "top");
		DetailEntry detailEntry = detail.addDetailEntry(qname);
		
		detailEntry.addChildElement("Content", "top", "http://top/");
		return fault;
	}
	
	/**
	 * Like loadE except the message is serialized and rebuilt
	 * @return
	 * @throws Exception
	 */
	private SOAPFault loadF_SOAP12() throws Exception {
		SOAPEnvelope env = deserialize(soap12TextEnv);
		SOAPBody body = env.getBody();
		
		// Create the fault using addBodyElement (to test C0011)
		QName faultQName= body.createQName("Fault", "soapenv");
		SOAPFault fault = (SOAPFault) body.addBodyElement(faultQName);
		
		// Code
		QName faultCodeValue = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Sender", "soapenv");
		fault.setFaultCode(faultCodeValue);
		
		// Subcodes
		fault.appendFaultSubcode(new QName("http://top/", "subcode1", "top"));
		fault.appendFaultSubcode(new QName("http://top/", "subcode2", "top"));
		
		// Reason
		fault.addFaultReasonText("Test", Locale.getDefault());
		fault.addFaultReasonText("Test (de)", new Locale("de"));
		fault.addFaultReasonText("Test (fr)", new Locale("fr"));
		
		// Node
		fault.setFaultNode("urn://testNode");
		
		// Role
		fault.setFaultRole("urn://worker");
		
		// Detail
		Detail detail = fault.addDetail();
		// Test SAAJ1.3 addDetailEntry(QName)
		QName qname = fault.createQName("TestDetail", "top");
		DetailEntry detailEntry = detail.addDetailEntry(qname);
		
		detailEntry.addChildElement("Content", "top", "http://top/");
		
		env = (SOAPEnvelope) fault.getParentNode().getParentNode();
		return loadA(env.toString());
	}
	
	/**
	 * Like loadE except the message is transformed into a WebServicesFault
	 * @return
	 * @throws Exception
	 */
	private SOAPFault loadG_SOAP12() throws Exception {
		SOAPFault fault = loadE_SOAP12();
		// Use implementation class to convert underlying data to WebServicesFault
		// Note that converting to WebServicesFault is a lossy transformation.
		// Some information (whitespace, prefixes, etc.) will not be preserved.
		((com.ibm.ws.webservices.engine.xmlsoap.SOAPFault)fault).getFault();
		return fault;
	}
	
	/**
	 * Load H: Build using Body addFault
	 * @return SOAPFault
	 */
	private SOAPFault loadH_SOAP12() throws Exception {
		SOAPEnvelope env = deserialize(soap12TextEnv);
		SOAPBody body = env.getBody();
		
		// Create the fault
		Name faultCodeValue = env.createName("Sender", "soapenv", SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);
		SOAPFault fault = body.addFault(faultCodeValue, "Test", Locale.getDefault());
		
		
		// Subcodes
		fault.appendFaultSubcode(new QName("http://top/", "subcode1", "top"));
		fault.appendFaultSubcode(new QName("http://top/", "subcode2", "top"));
		
		// Reason
		fault.addFaultReasonText("Test (de)", new Locale("de"));
		fault.addFaultReasonText("Test (fr)", new Locale("fr"));
		
		// Node
		fault.setFaultNode("urn://testNode");
		
		// Role
		fault.setFaultRole("urn://worker");
		
		// Detail
		Detail detail = fault.addDetail();
		DetailEntry detailEntry = (DetailEntry) detail.addChildElement("TestDetail", "top", "http://top/");
		detailEntry.addChildElement("Content", "top", "http://top/");
		return fault;
	}
	
	/**
	 * Load I: Build using Body addFault
	 * @return SOAPFault
	 */
	private SOAPFault loadI_SOAP12() throws Exception {
		SOAPEnvelope env = deserialize(soap12TextEnv);
		SOAPBody body = env.getBody();
		
		// Create the fault
		QName faultCodeValue = body.createQName("Sender", "soapenv");
		SOAPFault fault = body.addFault(faultCodeValue, "Test", Locale.getDefault());
		
		
		// Subcodes
		fault.appendFaultSubcode(new QName("http://top/", "subcode1", "top"));
		fault.appendFaultSubcode(new QName("http://top/", "subcode2", "top"));
		
		// Reason
		fault.addFaultReasonText("Test (de)", new Locale("de"));
		fault.addFaultReasonText("Test (fr)", new Locale("fr"));
		
		// Node
		fault.setFaultNode("urn://testNode");
		
		// Role
		fault.setFaultRole("urn://worker");
		
		// Detail
		Detail detail = fault.addDetail();
		DetailEntry detailEntry = (DetailEntry) detail.addChildElement("TestDetail", "top", "http://top/");
		detailEntry.addChildElement("Content", "top", "http://top/");
		return fault;
	}
	
	/**
	 * Check the SOAP 1.1 Fault using the normal SAAJ 1.2 methods
	 * @param fault
	 */
	private void checkSOAP11_1(SOAPFault fault) throws Exception {
		// faultCode
		QName faultCode = fault.getFaultCodeAsQName();
		QName faultCodeExpected = new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Server");
		assertTrue("Expected Fault Code "+faultCodeExpected+" but found " + faultCode, 
					faultCode.equals(faultCodeExpected));
		
		// TODO LI4238 Problem With FaultCode
		/*
		String faultCodeString = fault.getFaultCode();
		String faultCodeStringExpected = "soapenv:Server";
		assertTrue("Expected Fault Code String "+faultCodeStringExpected+" but found " + faultCodeString, 
				faultCodeString.equals(faultCodeStringExpected));
	    */

		
		// faultString
		String faultString = fault.getFaultString();
		String faultStringExpected = "Test";
		assertTrue("Expected Fault String" + faultStringExpected + " but found " + faultString, 
				faultString.equals(faultStringExpected));
		
		Locale faultStringLocale = fault.getFaultStringLocale();
		assertTrue(faultStringLocale == null || faultStringLocale == Locale.getDefault());
		
		
		// faultActor
		String faultActor = fault.getFaultActor();
		String faultActorExpected = "urn://worker";
		assertTrue("Expected Fault Actor" + faultActorExpected + " but found " + faultActor, 
				faultActor.equals(faultActorExpected));
		
		// detail
		Detail detail = fault.getDetail();
		assertTrue(detail != null);
		
		DetailEntry detailEntry = (DetailEntry) detail.getFirstChild();
		assertTrue(detailEntry != null);
		String detailEntryString = ((IBMDetailEntry)detailEntry).toXMLString(false);
		String detailEntryStringExpected = "<top:TestDetail><top:Content/></top:TestDetail>";
		assertTrue("Expected DetailEntry (" + detailEntryStringExpected + ") but found (" + detailEntryString + ")", 
				detailEntryString.equals(detailEntryStringExpected));
	}
	
	
	/**
	 * Check the SOAP 1.1 Fault using the normal SAAJ 1.2 methods
	 * @param fault
	 */
	private void checkSOAP12_1(SOAPFault fault) throws Exception {
		// Code
		QName faultCode = fault.getFaultCodeAsQName();
		QName faultCodeExpected = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Sender");
		assertTrue("Expected Fault Code "+faultCodeExpected+" but found " + faultCode, 
					faultCode.equals(faultCodeExpected));
		
		// TODO LI4238 Problem with getFaultCode
		/*
		String faultCodeString = fault.getFaultCode();
		String faultCodeStringExpected = "soapenv:Sender";
		assertTrue("Expected Fault Code String "+faultCodeStringExpected+" but found " + faultCodeString, 
				faultCodeString.equals(faultCodeStringExpected));
		*/
		
		// Reason
		String faultString = fault.getFaultString();
		String faultStringExpected = "Test";
		assertTrue("Expected Fault String" + faultStringExpected + " but found " + faultString, 
				faultString.equals(faultStringExpected));
		
		String faultStringLang = fault.getFaultStringLocale().getLanguage();
		assertTrue("Found Locale= " + faultStringLang, faultStringLang.equals("en"));
		
		// Skip Fault Node...there is not a SOAP 1.2 equivalent
		
		// Role
		String faultActor = fault.getFaultActor();
		String faultActorExpected = "urn://worker";
		assertTrue("Expected Fault Actor" + faultActorExpected + " but found " + faultActor, 
				faultActor.equals(faultActorExpected));
		
		// Detail
		Detail detail = fault.getDetail();
		assertTrue(detail != null);
		
		DetailEntry detailEntry = (DetailEntry) detail.getFirstChild();
		assertTrue(detailEntry != null);
		String detailEntryString = ((IBMDetailEntry)detailEntry).toXMLString(false);
		String detailEntryStringExpected = "<top:TestDetail><top:Content/></top:TestDetail>";
		assertTrue("Expected DetailEntry (" + detailEntryStringExpected + ") but found (" + detailEntryString + ")", 
				detailEntryString.equals(detailEntryStringExpected));
	}
	
	/**
	 * Check the SOAP 1.1 Fault using DOM methods
	 * @param fault
	 */
	private void checkSOAP11_2(SOAPFault fault) throws Exception {
		SOAPConstants soapConstants = SOAPConstants.SOAP11_CONSTANTS;
		QName faultQNameExpected = soapConstants.getFaultQName();
		QName faultQName = new QName(fault.getNamespaceURI(), fault.getLocalName());
		
		assertTrue(faultQNameExpected.equals(faultQName));
		
		Iterator it = fault.getChildElements();
		SOAPElement se = null;
		
		// Find faultCode
		while (it.hasNext() && se == null) {
			Object obj = it.next();
			if (obj instanceof SOAPElement) {
				se = (SOAPElement) obj;
			}
		}
		assertTrue(se != null);
		compareQNames(soapConstants.getFaultCodeQName(), new QName(se.getNamespaceURI(), se.getLocalName()));
		
		// Find faultString
		se = null;
		while (it.hasNext()&& se == null) {
			Object obj = it.next();
			if (obj instanceof SOAPElement) {
				se = (SOAPElement) obj;
			}
		}
		assertTrue(se != null);
		compareQNames(soapConstants.getFaultStringOrReasonQName(), new QName(se.getNamespaceURI(), se.getLocalName()));
		
		// Find faultActor
		se = null;
		while (it.hasNext()&& se == null) {
			Object obj = it.next();
			if (obj instanceof SOAPElement) {
				se = (SOAPElement) obj;
			}
		}
		assertTrue(se != null);
		compareQNames(soapConstants.getFaultActorOrRoleQName(), new QName(se.getNamespaceURI(), se.getLocalName()));
		
		// Find detail
		se = null;
		while (it.hasNext()&& se == null) {
			Object obj = it.next();
			if (obj instanceof SOAPElement) {
				se = (SOAPElement) obj;
			}
		}
		assertTrue(se != null);
		compareQNames(soapConstants.getFaultDetailQName(), new QName(se.getNamespaceURI(), se.getLocalName()));
		
		// This should be the end
		se = null;
		while (it.hasNext()&& se == null) {
			Object obj = it.next();
			if (obj instanceof SOAPElement) {
				se = (SOAPElement) obj;
			}
		}
		assert(se==null);
	}
	
	/**
	 * Check the SOAP 1.2 Fault using DOM methods
	 * @param fault
	 */
	private void checkSOAP12_2(SOAPFault fault) throws Exception {
		SOAPConstants soapConstants = SOAPConstants.SOAP12_CONSTANTS;
		QName faultQNameExpected = soapConstants.getFaultQName();
		QName faultQName = new QName(fault.getNamespaceURI(), fault.getLocalName());
		
		assertTrue(faultQNameExpected.equals(faultQName));
		
		Iterator it = fault.getChildElements();
		SOAPElement se = null;
		
		// Find Code
		while (it.hasNext() && se == null) {
			Object obj = it.next();
			if (obj instanceof SOAPElement) {
				se = (SOAPElement) obj;
			}
		}
		assertTrue(se != null);
		compareQNames(soapConstants.getFaultCodeQName(), new QName(se.getNamespaceURI(), se.getLocalName()));
		
		// Find Reason
		se = null;
		while (it.hasNext()&& se == null) {
			Object obj = it.next();
			if (obj instanceof SOAPElement) {
				se = (SOAPElement) obj;
			}
		}
		assertTrue(se != null);
		compareQNames(soapConstants.getFaultStringOrReasonQName(), new QName(se.getNamespaceURI(), se.getLocalName()));
		
		// Find Node
		se = null;
		while (it.hasNext()&& se == null) {
			Object obj = it.next();
			if (obj instanceof SOAPElement) {
				se = (SOAPElement) obj;
			}
		}
		assertTrue(se != null);
		compareQNames(soapConstants.getFaultNodeQName(), new QName(se.getNamespaceURI(), se.getLocalName()));
		
		// Find Role
		se = null;
		while (it.hasNext()&& se == null) {
			Object obj = it.next();
			if (obj instanceof SOAPElement) {
				se = (SOAPElement) obj;
			}
		}
		assertTrue(se != null);
		compareQNames(soapConstants.getFaultActorOrRoleQName(), new QName(se.getNamespaceURI(), se.getLocalName()));
		
		// Find Detail
		se = null;
		while (it.hasNext()&& se == null) {
			Object obj = it.next();
			if (obj instanceof SOAPElement) {
				se = (SOAPElement) obj;
			}
		}
		assertTrue(se != null);
		compareQNames(soapConstants.getFaultDetailQName(), new QName(se.getNamespaceURI(), se.getLocalName()));
		
		// This should be the end
		se = null;
		while (it.hasNext()&& se == null) {
			Object obj = it.next();
			if (obj instanceof SOAPElement) {
				se = (SOAPElement) obj;
			}
		}
		assert(se==null);
	}
	
	/**
	 * Check the SOAP 1.1 Fault using SAAJ 1.3 methods
	 * @param fault
	 */
	private void checkSOAP11_3(SOAPFault fault) throws Exception {
		
		// Remove and add Header to test C015
		SOAPEnvelope env = (SOAPEnvelope) fault.getParentElement().getParentElement();
		SOAPHeader header = env.getHeader();
		QName qName = header.createQName("TestHeader", "top");
		Iterator it = header.getChildElements(qName);
		SOAPElement se = (SOAPElement) it.next();
		qName = se.getElementQName();
		se.detachNode();
		SOAPHeaderElement she = header.addHeaderElement(qName);
		it = header.getChildElements(qName);
		assertTrue(she == it.next());
		
		// Check subcodes
		try {
			it = fault.getFaultSubcodes();
			assertTrue("expected UnsupportedOperationException", false);
		} catch(UnsupportedOperationException e) {
			// Expected
		}
		
		// Check Reasons
		try {
			it = fault.getFaultReasonLocales();
			assertTrue("expected UnsupportedOperationException", false);
		} catch(UnsupportedOperationException e) {
			// Expected
		}
		try {
			String text = fault.getFaultReasonText(Locale.getDefault());
			assertTrue("expected UnsupportedOperationException", false);
		} catch(UnsupportedOperationException e) {
			// Expected
		}
		
		// Check Node
		try {
			String faultNode = fault.getFaultNode();
			assertTrue("expected UnsupportedOperationException", false);
		} catch(UnsupportedOperationException e) {
			// Expected
		}
		
	}
	/**
	 * Check the SOAP 1.1 Fault using SAAJ 1.3 methods
	 * @param fault
	 */
	private void checkSOAP12_3(SOAPFault fault) throws Exception {
		
		// Remove and add Header to test C015
		SOAPEnvelope env = (SOAPEnvelope) fault.getParentElement().getParentElement();
		SOAPHeader header = env.getHeader();
		QName qName = header.createQName("TestHeader", "top");
		Iterator it = header.getChildElements(qName);
		SOAPElement se = (SOAPElement) it.next();
		qName = se.getElementQName();
		se.detachNode();
		SOAPHeaderElement she = header.addHeaderElement(qName);
		it = header.getChildElements(qName);
		assertTrue(she == it.next());
		
		// Check subcodes
		it = fault.getFaultSubcodes();
		QName subcode1 = new QName("http://top/", "subcode1");
		QName subcode2 = new QName("http://top/", "subcode2");
		compareQNames(subcode1, (QName) it.next());
		compareQNames(subcode2, (QName) it.next());
		assertTrue(!it.hasNext());
		
		// Check Reasons
		it = fault.getFaultReasonLocales();
		Locale locale = (Locale) it.next();
		assertTrue(locale.getLanguage().equals("en"));
		assertTrue(fault.getFaultReasonText(locale).equals("Test"));
		locale = (Locale) it.next();
		assertTrue("Expected de but found:" + locale.getLanguage(), locale.getLanguage().equals("de"));
		assertTrue(fault.getFaultReasonText(locale).equals("Test (de)"));
		locale = (Locale) it.next();
		assertTrue(locale.getLanguage().equals("fr"));
		assertTrue(fault.getFaultReasonText(locale).equals("Test (fr)"));
		
		// Check Node
		String faultNode = fault.getFaultNode();
		assertTrue( "urn://testNode".equals(faultNode));
	}
	
	protected javax.xml.soap.SOAPEnvelope deserialize(String data)
    	throws Exception
    {
		IBMSOAPFactory sf = (IBMSOAPFactory) SOAPFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);
		return (SOAPEnvelope) sf.createElementFromXMLString(data, SOAPEnvelope.class);
    }
	
	private void compareQNames(QName expected, QName found) throws Exception {
		assertTrue("Expected " + expected + " but found " + found, expected.equals(found));
	}
}
