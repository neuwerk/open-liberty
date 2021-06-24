//
// @(#) 1.3 autoFVT/src/jaxws/provider/nullreturn/test/NullReturnLegacyModeTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/19/11 12:17:49 [8/8/12 06:57:43]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 06/24/2010 btiffany                    New File
// 03/09/2011 jtnguyen  690107.1          Change to make V7 and V8 work the same after 690107                
// 12/06/11 jtnguyen    724041          Change jar name from 8.0 to 8.5
//

package jaxws.provider.nullreturn.test;


import java.io.File;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.websphere.simplicity.WebSphereVersion;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;


/**
 * This test will check jaxws specification compliance for sec 5.1.1., regarding
 * provider impls not being required to return a response when the response is null.
 * 
 * (In that case, it behaves as a one-way and returns an ack, only unlike a one-way,
 * it runs inbound handlers first)
 * 
 * For compatibility with v7 users, we have a flag that forces the old behavior, 
 * (returning an empty soap body). We test that it works. 
 * 
 * For the case where a wsdl is packed with the app and the wsdl contains a response,
 * we adhere to the wsdl contract regardless of the compatibility flag setting.
 * 
 * TEST MECHANICS NOTE:
 * only files name testLegacy* are run in this testcase.
 *  
 * 
 * Since: version 8.   Also present in v7 at fp13+ , apar PM16015
 * RTC Story: 13351, task 23021, feature F743-23021
 *  
 * since 690107: now the logacy mode(false) is the default. so both V7 and V8 will work the same:  
 *   - when there is no WSDL, when the response is null, it will return an empty soap body and return code = 200
 *   - V8 script is no longer needed

 */
public class NullReturnLegacyModeTest extends NullReturnTest {
    
    
    
    private static int numtests = 0;     // work around framework limitation
    private static int numtestsrun = 0;   
    
    

    // our nonportable test setup method
    protected void suiteSetup(ConfigRequirement cr) throws Exception{
        super.suiteSetup(cr);
        System.out.println("suiteSetup called");
            
    }
    
    public void tearDown() throws Exception {
        // suiteTeardown isn't working because we are manually manipulating the 
        // number of tests in the suite, so trigger it this way
        if(++numtestsrun == numtests) {suiteTeardown();}
    }
     
    
    //   our nonportable test teardown method
    protected void suiteTeardown() throws Exception {
        super.suiteTeardown();
    }
    

    
    /** 
     * This method controls which test methods to run.
     * @return A suite of tests to run
     * 
     * This method looks at the names of the tests when deciding which ones to add.
     * If we are in legacy mode, only the legacy tests are added
     * Otherwise, only the non-legacy tests are added. 
     * Since this method is static, we have to duplicate it here and hide the one in the superclass.
     * 
     */
    public static Test suite() {    
        System.out.println("selecting which tests to run, legacy mode or the others");
        
        TestSuite ts = new TestSuite(NullReturnLegacyModeTest.class);
        TestSuite ts2 = new TestSuite();
        Enumeration<Test> e = ts.tests();
        while(e.hasMoreElements()){
            Test t = e.nextElement();
            // System.out.println(t.toString());
            if( t.toString().toLowerCase().contains("testlegacy")  ){
                System.out.println("adding legacy mode test " + t.toString());
                ts2.addTest(t);
                numtests++;  
            }
        }  
        return ts2;
        
    }    
    

 
}
