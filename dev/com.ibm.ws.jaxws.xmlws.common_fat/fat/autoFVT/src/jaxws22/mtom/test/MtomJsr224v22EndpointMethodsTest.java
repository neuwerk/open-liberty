//
// @(#) 1.1 autoFVT/src/jaxws22/mtom/test/MtomJsr224v22EndpointMethodsTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/2/10 11:50:32 [8/8/12 06:57:47]
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
// 03/03/10 btiffany                    New File
//

package jaxws22.mtom.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * Since: version 8
 * RTC Story: 18112, task 23604
 *  
 */
public class MtomJsr224v22EndpointMethodsTest extends FVTTestCase {

    /** 
     * A constructor to create a test case with a given name.
     * @param name The name of the test case to be created
     */
    public MtomJsr224v22EndpointMethodsTest(String name) {
        super(name);
    } 
    
    public void testNothing(){
        /*
         * 645966 - we don't need to cover 5.2 and 6.2 in jsr224 the spec.  We're excused
         * by jsr109 sec 5.3.3.
         */
    }
    
    
    /** 
     * This method controls which test methods to run.
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(MtomJsr224v22EndpointMethodsTest.class);   
    }
 
}
