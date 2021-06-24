/*
 * @(#) 1.3 autoFVT/src/annotations/servicemode/tooling/test/ServiceModeTest.java, WAS.websvcs.fvt, WASX.FVT 1/18/07 11:21:06 [7/11/07 13:10:54]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 12/22/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.servicemode.tooling.test;

import java.io.File;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/*
 * @ServiceMode annotation is tested in jaxws/provider package
 * in detail.  Also it is used in both forms in annotations/webserviceprovider tests. 
 * No additional test cases provided here... 
 * Below is a trivially true test case... * 
 * 
 */
public class ServiceModeTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
      
	private static String buildDir = null;
	private static String testDir  = null;
	static {
		buildDir = AppConst.FVT_HOME + File.separator +
		          "build";
		testDir = "annotations" + File.separator +
			      "servicemode" + File.separator +
			      "tooling";
	}

	public ServiceModeTest(String name) {
		super(name);
	}
	
	protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
	     System.out.println("Do not need suiteSetup since no application is installed");    
	 }
	
	/*
	 * 
	 * @testStrategy 
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_j2w_ServiceMode_value() throws Exception {
        System.out.println("***** test_j2w_ServiceMode_value()running *******");

		File f = new File(buildDir + "/work/" +
				testDir + "/server/ServiceModeToolingService.wsdl");

		assertTrue("wsdl not generated, something is wrong..!", f.exists());
        System.out.println("****** test passed *******");
	}

    public static junit.framework.Test suite() {
    	System.out.println(ServiceModeTest.class.getName());
        return new TestSuite(ServiceModeTest.class);
    }   
	
	public static void main(String[] args) {	
		TestRunner.run(suite());
	}
}
