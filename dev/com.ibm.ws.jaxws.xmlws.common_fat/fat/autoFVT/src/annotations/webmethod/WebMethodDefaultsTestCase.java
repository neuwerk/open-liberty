/*
 * autoFVT/src/annotations/webmethod/WebMethodDefaultsTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 05/30/2006  btiffany    LIDB3296.31.01     new file
 * 08/01/2006    "                            add no annotated methods case,
 *    "                  "                    add illegal when interface used case.
 * 07/26/2007  jramos      453487.1           add annotations to test methods
 * 08/14/2007  btiffany    459641             add constructor for framework
 * 08/21/2007   "          460555             fix improper directory removal
 *
 */

package annotations.webmethod;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import annotations.support.AnnotationHelper;
import annotations.support.ImplementationAdapter;
import annotations.support.Support;

/**
 * based on 224 sec 2.3, 2.8
 * Checks that when all params are set on an @WebMethod annotation, j2w passes
 * these through to the wsdl correctly.
 *
 * Then checks the reverse, when the parameters are in wsdl, that they map to java.
 *
 * Does not yet check renaming conflicting names, or the use of the annotation on interfaces
 *
 * @author btiffany
 *
 * 07.02.22 - call _testDefaults2 from inside testDefaults1 to make sure they run in sequence.
 * Somehow we were getting some slightly parallel execution in junit.
 *
 *
 */
public class WebMethodDefaultsTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
        private static String workDir=null;
        private static ImplementationAdapter imp = null; 
        private static boolean setupRun = false;        // for junit

        // get an absolute path to this directory, for reliability.
        static{
                workDir = Support.getFvtBaseDir()+"/build/work/WebMethodDefaults";
				imp = Support.getImplementationAdapter("ibm",workDir);
        }

        // framework reqmt
        public WebMethodDefaultsTestCase( String s){
                super(s);
        }

        public WebMethodDefaultsTestCase(){
                super("nothing");
        }

        public static void main(String[] args) throws Exception {
                if (true){
                TestRunner.run(suite());
                } else {
                        WebMethodDefaultsTestCase t = new WebMethodDefaultsTestCase();
                        t.setUp();
                }

        }


    /**
     * The suite method returns the tests to run from this suite.
     * The Java2WsdlTestCaseCase class is specified which means
     * all methods starting with "test" will be be run.
     *
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
        System.out.println(WebMethodDefaultsTestCase.class.getName());
        return new TestSuite(WebMethodDefaultsTestCase.class);

    }

    // junit runs this before every test
    public void setUp(){
        // if standalone, junit runs this
        // in harness, annotations ant script will run this explicitly with Run_Setup set
        if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
        if (setupRun) return;   // we only want to run this once per suite
        //imp.cleanWorkingDir();
        setupRun = true;
    }

    /**
     * @testStrategy take a java file that sets all parameters on \@WebMethod and verify that wsdl is
     * generated properly given the parameters of the annotation.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="take a java file that sets all parameters on \\@WebMethod and verify that wsdl is generated properly given the parameters of the annotation. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaults1() throws Exception{
        System.out.println("*********** runing testDefaults1() ****************");
        /*  
        // moved this to buildTest.xml
        imp.j2w("", "src/annotations/webmethod/testdata/WebMethodDefaults1.java",
                        "annotations.webmethod.testdata.WebMethodDefaults1");
        */                 

                // now inspect wsdl
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", true);
        reader.setFeature("javax.wsdl.importDocuments", true);
        Definition def = reader.readWSDL(null, workDir+"/WebMethodDefaults1Service.wsdl");


        Map m = def.getPortTypes();
        Collection<PortType> c = m.values();
        assertTrue("no or too many portTypes found", c.size()== 1);
        PortType p= c.iterator().next();
                List<Operation> l = p.getOperations();

                for ( Operation o: l){
                        System.out.println(o.toString()+"\n\n");
                }

                // confirm method names mapped properly
                String [] opnames = {"defaultAnno", "annotatedfullAnno", "someAnno" };
                for (String opname : opnames ){
                        assertNotNull("could not find operation:"+opname, p.getOperation(opname,null,null));
                }

                // confirm action parm mapped correctly
		// wsdl4j doesn't seem to have a way to get the SOAPaction attribute out of the wsdl.
		// So, we have to convert it back to java and see if it's still there.
		// We assume that if it reappears, it was in the WSDL correctly.
		imp.w2j("",workDir+"/WebMethodDefaults1Service.wsdl");
		AnnotationHelper anh = new AnnotationHelper("annotations.webmethod.testdata.WebMethodDefaults1",workDir);
		String actual = anh.getMethodElement("someAnno","WebMethod","action");
		String expect = "idunno";
		assertTrue("action parameter not properly preserved",
					actual.compareTo(expect)==0);	

        System.out.println("********* test passed *********");
        _testDefaults2();
		
    }

    /**
     * @testStrategy take a wsdl file, generate java, check that parameters on the \@WebMethod annotation
     * are correct.
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testDefaults2() throws Exception {
    	System.out.println("*********** runing testDefaults2() ****************");
		imp.w2j("","src/annotations/webmethod/testdata/WebMethodDefaults2Service.wsdl");
		AnnotationHelper anh = new AnnotationHelper("annotations.webmethod.testdata.WebMethodDefaults2",workDir);
		
		String actual = anh.getMethodElement("someAnno","WebMethod","action");
		String expect = "idunno";
		assertTrue("parameter incorrect, expected:"+expect+" actual:"+actual,
					actual.compareTo(expect)==0);
		
		// on fullAnno, method name will have changed		 								
		actual = anh.getMethodElement("annotatedfullAnno","WebMethod","action");
		expect = "idunno";
		assertTrue("parameter incorrect, expected:"+expect+" actual:"+actual,
					actual.compareTo(expect)==0);
		
		// defaultAnnomethod should still exist.
		assertNotNull("missing method", anh.getMethodAnnotation("defaultAnno", "WebMethod"));
        System.out.println("********* test passed *********");
		
    }

    /**
     * @testStrategy - check that only public methods map to wsdl
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- check that only public methods map to wsdl ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testNoPublicMethods() throws Exception {
    	System.out.println("*********** runing testNoPublicMethods) ****************");
    	try{
	    	imp.j2w("", "src/annotations/webmethod/testdata/WebMethodNopub.java",
	    			"annotations.webmethod.testdata.WebMethodNopub");
    	} catch (RuntimeException e){
    		System.out.println("Caught exception from j2w");
    		return;
    	}
    	
    	// if the tooling didn't catch it, we have to check the wsdl.
	 	// now inspect wsdl
        File f = new File(workDir+"/WebMethoddefaultNopubService.wsdl");
        if( f.exists() ){
        	WSDLFactory factory = WSDLFactory.newInstance();
        	WSDLReader reader = factory.newWSDLReader();
        	reader.setFeature("javax.wsdl.verbose", true);
        	reader.setFeature("javax.wsdl.importDocuments", true);
        	Definition def = reader.readWSDL(null, workDir+"/WebMethoddefaultNopubService.wsdl");    	
        	
        		    	
        	Map m = def.getPortTypes();
        	Collection<PortType> c = m.values();	    	
        	assertTrue("no or too many portTypes found", c.size()== 1);
        	PortType p= c.iterator().next();    	
    		List<Operation> l = p.getOperations();
    		
    		// there shouldn't be any mapped operations		
    		for ( Operation o: l){
    			assertFalse("non-public method was mapped to wsdl", true);
    			System.out.println(o.toString()+"\n\n");
    		}
        }
    }	
		
    /**
     * @testStrategy - @WebMethod is illegal within an imp. class if it references
     * an interface (jsr181 3.1).  Confirm that this is caught.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- @WebMethod is illegal within an imp. class if it references an interface (jsr181 3.1).  Confirm that this is caught. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAnnoIllegalOnInterface(){
    	System.out.println("*********** runing testAnnoIllegalOnInterface) ****************");
    	try{
	    	imp.j2w("", "src/annotations/webmethod/testdata/WebMethodOnIf*.java",
	    			"annotations.webmethod.testdata.WebMethodOnIfImpl");
    	} catch (RuntimeException e){
    		System.out.println("Caught exception from j2w");
    		return;
    	}    	
    	fail("wsdl should not have been generated for annotated interface");
		
	}
	
	/**
	 * testStrategy - confirm that when there is no webmethod annotation within an
	 * implementing class, all public methods (and only public methods) are exposed.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testNoMethodAnnos() throws Exception{
		System.out.println("*********** runing testNoMethodAnnos ****************");
		
    	imp.j2w("", "src/annotations/webmethod/testdata/WebMethodNoAnno.java",
		"annotations.webmethod.testdata.WebMethodNoAnno");
    	
    	// inspect wsdl using xpath
    	// http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/xpath/package-summary.html
    	String infil = imp.getWorkingDir()+"/WebMethodNoAnnoService.wsdl";
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new File(infil));		
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = null;
		
		// look for methods in the wsdl
		int methods = 0;
		String expression = "definitions/portType/operation[@name='echo']";		
		nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		methods += nodes.getLength();
		
		expression = "definitions/portType/operation[@name='echo2']";		
		nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		methods += nodes.getLength();
		
		assertTrue("public methods not in wsdl", methods == 2);
		
		expression = "definitions/portType/operation[@name='echo3']";		
		nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		assertTrue("non public method appears in wsdl", nodes.getLength() ==0);
		
		
		//assertTrue("echo Method not found in wsdl(nodes.getLength());
	
		//System.out.println("xpath found: "+nodes.item(0).getAttributes().getNamedItem("name").getNodeValue());
		
	}
		



}
