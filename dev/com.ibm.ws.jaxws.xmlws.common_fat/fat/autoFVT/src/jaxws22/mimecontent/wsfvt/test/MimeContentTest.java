//
// @(#) 1.1 autoFVT/src/jaxws22/mimecontent/wsfvt/test/MimeContentTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/28/10 15:16:24 [8/8/12 06:58:56]
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
// 09/23/10 jtnguyen    672049          New File

package jaxws22.mimecontent.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.validation.constraints.AssertTrue;

import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;



/**
 * @author jtnguyen
 *  FAT for story 13242 Validates that mime:content used in proper scenarios (JAX-WS 2.2 spec conformance reqmt)
 *  The test involves the WSDLs with mime:content and a custom binding jaxws:enableMIMEContent.
 *  We verify that the wsimport tool works correctly with mime:content in the WSDL:
 *       - We expect the wsimport will produce an interface with type = byte[] when:
 *         - no customization - wsdl doesn't have "enableMIMEContent" (default behavior)
 *         - value of enableMIMEContent is set to false
 *       - We expect the wsimport will produce an interface with type = Image when:
 *         - customize with enableMIMEContent = true, base64Binary, mime type =  image/jpeg
 *         - customize with enableMIMEContent = true, hexBinary, mime type =  image/jpeg
 *       - We expect the wsimport will produce an interface with type = DataHandler when:
 *         - customize with enableMIMEContent = true, mime type =  multipart/* 
 *       - In addition to above cases which use inline custom binding, there is one case for external custom binding file.
 * 
 */
public class MimeContentTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    public static final String fileToSearch = "MimeContentService.java";
    public static final String defaultStr = "public byte[] echoImage";
    public static final String imageStr = "public Image echoImage";
    public static final String datahandlerStr = "public DataHandler echoImage";
    private static String workDir;
    private static String cmd;


    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public MimeContentTest(String name) {
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
        return new TestSuite(MimeContentTest.class);
    }
    
    public void suiteSetup(ConfigRequirement req) throws Exception {
        //super.suiteSetup(req);
    	System.out.println("Do not need suiteSetup since no application is installed");
        workDir = AppConst.FVT_HOME + "/build/classes/jaxws22/mimecontent";
        cmd = TopologyDefaults.getDefaultAppServer().getNode().getProfileDir() + "/bin" ;
    
    }

    
    // wsdl has enableMIMEContent = false
    public void testMimeContentDisabled() throws Exception {           
        System.out.println("-- cmd = " + cmd + ", workDir = " + workDir);        
        assertTrue(verifyFileContents(workDir + "/MimeContentDisabled/" + fileToSearch,defaultStr));
    }
        
    // wsdl has type="xsd:base64Binary"
    public void testMimeContentEnabled() throws Exception {   
        System.out.println("-- cmd = " + cmd + ", workDir = " + workDir);        
        assertTrue(verifyFileContents(workDir + "/MimeContentEnabled/" + fileToSearch,imageStr));

    }
    
    // by default, mime:content is disabled when there is no custom binding with MimeContentEnabled
    public void testMimeContentDefault() throws Exception {   
        System.out.println("-- cmd = " + cmd + ", workDir = " + workDir);        
        assertTrue(verifyFileContents(workDir + "/MimeContentDefault/" + fileToSearch,defaultStr));

    }
    
    
    // wsdl has type="xsd:hexBinary"
    public void testMimeContentEnabledHex() throws Exception {   
        System.out.println("-- cmd = " + cmd + ", workDir = " + workDir);        
        assertTrue(verifyFileContents(workDir + "/MimeContentEnabledHex/" + fileToSearch,imageStr));

    }
    
    
    // wsdl has mime type="multipart/*"
    public void testMimeContentEnabledMultipart() throws Exception {   
        System.out.println("-- cmd = " + cmd + ", workDir = " + workDir);        
        assertTrue(verifyFileContents(workDir + "/MimeContentEnabledMultipart/" + fileToSearch,datahandlerStr));

    }
    
    private boolean verifyFileContents( String fileName, String searchStr) {
     
        boolean foundStr = false;
        try {

           BufferedReader inputStream = new BufferedReader(new FileReader(fileName));
           String line = inputStream.readLine();          

           while (line != null) {
               System.out.println("Line = " + line);
                           
               if (line.indexOf(searchStr) != -1)  // found it
               {
                  foundStr = true;
                  break;
               }
                // read next line
               line = inputStream.readLine();
           }       
           
           inputStream.close();
           
        }catch (IOException ex) {
                 ex.printStackTrace();     
                 fail("Error reading file " + fileName);      
        }
        return foundStr;
    }


}
