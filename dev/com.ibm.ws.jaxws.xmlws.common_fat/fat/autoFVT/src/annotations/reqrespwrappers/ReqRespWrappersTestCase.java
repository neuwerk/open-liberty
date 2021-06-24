/*
 * autoFVT/src/annotations/reqrespwrappers/ReqRespWrappersTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * see cmvc history        LIDB3296.31.01     new file
 * 8/14/2007  btiffany                        fix constructors for framework changes
 *
 *TODO: move wsdl/java generation into setup methods or ant
 */

package annotations.reqrespwrappers;
import java.io.File;
import java.lang.annotation.Annotation;

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
import annotations.support.WsdlFlattener;

/**
 * Tests that the RequestWrapper and ResponseWrapper annotations respond
 * properly in the wsdl2java and java2wsdl cases. 
 * 
 * You can get a real headache over this one. 
 * These annotations are strange in their usage differs depending on which way you are going.
 * In J2w, they define the names and namespaces (packages) of the jaxb beans.
 * 
 * In W2j, they are indicators that a method is being handled in "wrapper" style, but this could also be
 * inferred from the absence of an @SOAPBinding annotation on the method. 
 * 
 * The meaning of "wrapper" here is different from what it means in the parameterStyle
 * parameter of the @SoapBinding annotation, although they're intimately related.
 * 
 * In the simple doclitwrapped case, the Soapbinding anno, if present, would have
 * parameterSytle=wrapped,   in which case this request annotation must be present UNLESS all params
 * would be default. 
 * 
 * Wrapper style means the wrapped style object is unwrapped
 * by the runtime, and it's contents passed to the method as individual parameters.
 *  
 * Non-wrapper means that the entire wrapper object is passed to the method and the method must take
 * care of extracting the parameters. 
 * 
 * There are certain cases where wrapped style is disallowed by the standard, in those cases we 
 * expect to see methods with an @SoapBinding with parametersytle=bare, and then there probably won't be
 * these req/resp annotations present.  Because of the complexity of defaults here, the best way to force
 * presence of this annotation for testing was to force a non-default value in wsdl on a wrapped method. 
 * see jsr224 2.3.1 for an example.  
 * 
 * @author btiffany
 * 
 * 4.11.2007 - revised for minor changes in wsdl shape after new tooling came into use.
 *
 */
public class ReqRespWrappersTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	private static String workDir = null;
	private static ImplementationAdapter imp = null; 
	private static boolean setupRun = false; 	// for junit
	
	static{
		// set the working directory to be <work area>/<classname>
		workDir = ReqRespWrappersTestCase.class.getName();
		int p =	workDir.lastIndexOf('.');
		workDir = Support.getFvtBaseDir()+"/build/work/"+workDir.substring(p+1)+"/w2j"; 
		imp = Support.getImplementationAdapter("ibm",workDir);
	}
	
	// framework reqmt.
	public ReqRespWrappersTestCase( String s){
		super(s);
	}
	
	public ReqRespWrappersTestCase(){
		super("nothing");
	}

	public static void main(String[] args) throws Exception {	
		if (true){
			TestRunner.run(suite());
		}
		else {
			ReqRespWrappersTestCase t = new ReqRespWrappersTestCase();
			//t.setUp();
			t.testj2w();
		}	
	}
	

    /**
     * junit needs this.  
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
            return new TestSuite(ReqRespWrappersTestCase.class); 
    }   
    
    // junit will run this before every test
    public void setUp(){
    	// if standalone, junit runs this
    	// in harness, annotations ant script will run this explicitly with Run_Setup set
    	if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
    	if (setupRun) return;	// we only want to run this once per suite    
    	imp.setWorkingDir(imp.getWorkingDir()+"/..");
    	// MIGRATION CHANGE: Liberty build ID cannot run ant command on build engines, so do not clean
    	// client working directory to let the artifacts generated during case compilation phase there.
    	// imp.cleanWorkingDir();   
    	imp.setWorkingDir(imp.getWorkingDir()+"/j2w");
    			
    	setupRun = true;
    }
    
    /**
     * @testStrategy - check that all parameters on annotation can be set in java and passed to wsdl
     * 
     * @throws Exception
     * 
     * stuck trying to figure out if wsdl4java is up to the task at hand here. 
     * Can't seem to get document elements out of wsdl4j. 
     * Decided to try xpath. 
     * 
     * We're just checking that certain pieces are as we expect, not the total integrity
     * of the wsdl - that will get done by inference in a runtime check. 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testj2w() throws Exception {
       	System.out.println("********** testj2w is running****************");
        
    	imp.setWorkingDir(imp.getWorkingDir()+"/../j2w");
        if(true){
        	imp.j2w("","src/annotations/reqrespwrappers/server/J2WWrapperCheck.java",
        			"annotations.reqrespwrappers.server.J2WWrapperCheck");
        }
        
    	// inspect wsdl using xpath
    	// http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/xpath/package-summary.html
    	
    	// get rid of the import statements in wsdl so xpath doesn't choke
        
    	String infil = WsdlFlattener.flatten(imp.getWorkingDir()+"/J2WWrapperCheckService.wsdl");
        
    	// create an xpath instance with the flattened wsdl
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new File(infil));		
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = null;	

		/*----------------
		  query xpath to see if what we expect is in the wsdl
		  for echo2 method, we made the following customizations that should appear in wsdl:
		  	@requestwrapper:
		  	 	localName="notarg0",
		  	 		should show up as element name in namespace annotations.reqrespwrappers.server2req
		  	 	targetNamespace="annotations.reqrespwrappers.server2req",
				className="echo2input" - this appears to have no effect in j2w. 
			@ResponseWrapper:
					localName="notresponse"
						should show up as element name in namespace annotations.reqrespwrappers.server2req
					targetNamespace="annotations.reqrespwrappers.server2resp",
					className="echo2output" - appears to have no effect
		------------------*/
		
		// lets' look at response first
		//String expression = "definitions/types/schema/complexType[@name='notresponse']/sequence/element[@name='return']";
		String expression = "definitions/types/schema/element[@name='notresponse']";
		
		nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);		
		
		//System.out.println(nodes.getLength());
		String actual = nodes.item(0).getAttributes().getNamedItem("name").getNodeValue();
		System.out.println("found: "+actual);
		String expected = "notresponse";
		assertTrue("expected wsdl element: "+expected+" not found, actual:"+actual,
					expected.compareTo(actual)==0);
		
		// you can't query on a namespace directly in xpath, get it this way:
		expression = "definitions/message[@name='echo2Response']/part[@name='parameters']";                
        nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        
        
        System.out.println(nodes.toString());
        System.out.println(nodes.getLength());
        //System.out.println(nodes.item(0).get
		actual = nodes.item(0).getAttributes().getNamedItem("xmlns:ns2").getNodeValue();
		expected = "annotations.reqrespwrappers.server2resp";
		System.out.println("found resp ns: "+actual);
		assertTrue("expected wsdl element: "+expected+" not found, actual:"+actual,
				expected.compareTo(actual)==0);
        
        
		
		/********* now do request **********/
		expression = "definitions/types/schema/element[@name='notarg0']";
		

		nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);		
		
		//System.out.println(nodes.getLength());
		actual = nodes.item(0).getAttributes().getNamedItem("name").getNodeValue();
		System.out.println("found: "+actual);
		expected = "notarg0";
		assertTrue("expected wsdl element: "+expected+" not found, actual:"+actual,
					expected.compareTo(actual)==0);
		
		// you can't query on a namespace directly in xpath, get it this way:
        expression = "definitions/message[@name='echo2']/part[@name='parameters']";                
        nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		actual = nodes.item(0).getAttributes().getNamedItem("xmlns:ns1").getNodeValue();
		expected = "annotations.reqrespwrappers.server2req";
		System.out.println("found req ns: "+actual);
		assertTrue("expected wsdl element: "+expected+" not found, actual:"+actual,
				expected.compareTo(actual)==0);		

    }
    
    /**
     * @testStrategy  - this annotation is counterintuitive. see jsr225 2.3.1.2
     * if wrapper style of parameter passing is used, then generated methods must 
     * be annotated with RequestWrapper and ResponseWrapper UNLESS all properties would
     * have default values.  So the way to test this is to write some wsdl that 
     * can  be mapped go wrapper style java, but that don't have default values. 
     * 
     * See if annotation is present when methods processed in the bare style are properly annotated after 
     * wsdl2java processing.  doclit bare style is used here to force annotation presence.  
     * One may have to enable wrapper style thru bindings (8.7.3).
     * 
     * This is a difficult one to test, apparently the only way you can tell if
     * it should be wrapper style or not is to look at the wsdl.  Which is sort of irrelevant
     * in the java runtime domain. 
     * 
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description=" - this annotation is counterintuitive. see jsr225 2.3.1.2 if wrapper style of parameter passing is used, then generated methods must be annotated with RequestWrapper and ResponseWrapper UNLESS all properties would have default values.  So the way to test this is to write some wsdl that can  be mapped go wrapper style java, but that don't have default values.  See if annotation is present when methods processed in the bare style are properly annotated after wsdl2java processing.  doclit bare style is used here to force annotation presence. One may have to enable wrapper style thru bindings (8.7.3).  This is a difficult one to test, apparently the only way you can tell if it should be wrapper style or not is to look at the wsdl.  Which is sort of irrelevant in the java runtime domain.  ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testw2j(){
    	System.out.println("********** testw2j is running****************");
    	imp.setWorkingDir(imp.getWorkingDir()+"/../w2j");
    	imp.w2j("","src/annotations/reqrespwrappers/server/W2JWrapperCheck.wsdl");
    	AnnotationHelper anh = new AnnotationHelper(
    			"annotations.reqrespwrappers.server.W2JWrapperCheck",
    			imp.getWorkingDir());
    	assertNotNull("can't find class", anh);
    	
    	// first, if w2j has decided that the method will use non=wrapper style, then there should be a
    	// SOAPBinding anno on the method and param style will be bare  
    	// If there's not one, we'll get an exception here.
    	String sb1 = null;
    	boolean bareStyle = true;
    	try{
    	sb1 = anh.getMethodElement("echo1","SOAPBinding", "parameterStyle");
    	} catch ( NullPointerException e ){
    		System.out.println("no SOAPBinding anno on echo1, may be wrapper style");
    		bareStyle=false;
    	}  
    	
    	// if we have the annotation, does it declare bare (non-wrapper) style in use? 
    	if ( (bareStyle && sb1.compareTo("BARE")==0)  ){
    		System.out.println("SOAPBbinding anno is present, but BARE is not declared" +
    				", assuming wrapped style");
    		bareStyle=false;
    	}
    	
    	// if not bare style, check the req/resp annotations 
    	// note that it's still ok for them to be missing if all values are default,
    	// and we are not programatically checking for that.
    	
    	if (bareStyle){
    		fail("Webreq/resp annotation checks cannot be done due to non-wrapper style of generated java");    		
    	} else{
    		System.out.println("echo1 is wrappered, checking req/resp annos");
	    	
	    	
	    	Annotation a1 = anh.getMethodAnnotation("echo1","ResponseWrapper");    	
	    	
	    	assertNotNull("can't find method", a1);
	    	String s1 = anh.getMethodElement("echo1","ResponseWrapper","localName");
	    	System.out.println(s1);
	    	String s2 = anh.getMethodElement("echo1", "ResponseWrapper", "targetNamespace");
	    	System.out.println(s2);
	    	String s3 = anh.getMethodElement("echo1", "ResponseWrapper", "className");
	    	System.out.println(s3);
	    	
	    	Annotation a2 = anh.getMethodAnnotation("echo1","RequestWrapper");    	
	    	assertNotNull("can't find class", anh);
	    	assertNotNull("can't find method", a1);
	    	String s4 = anh.getMethodElement("echo1","RequestWrapper","localName");
	    	System.out.println(s4);
	    	String s5 = anh.getMethodElement("echo1", "RequestWrapper", "targetNamespace");
	    	System.out.println(s5);
	    	String s6 = anh.getMethodElement("echo1", "RequestWrapper", "className");
	    	System.out.println(s6);
	    	
	    	// check for proper values
	    	checkString("echo1Response", s1);
	    	checkString("http://server.reqrespwrappers.annotations/", s2);
	    	checkString("annotations.reqrespwrappers.server.Echo1Response", s3);
	    	checkString("echo1", s4);
	    	checkString("http://server.reqrespwrappers.annotations/", s5);
	    	checkString("annotations.reqrespwrappers.server.Echo1",s6);    	
	    	
    	}  // end if barestyle
    	
    	/*
    	 * the echo2 method should not be wsdl2java'd in wrapper style, because the opname, echo2,
    	 * does not map to the localname, notarg0.  It should have a SOAPBinding anno on it to warn
    	 * the processor that parameters are bare.
    	 */
    	
    	String sb2 = anh.getMethodElement("echo2","SOAPBinding", "parameterStyle");
    	assertTrue("did not find expected soapbinding bare style declaration on echo2", 
    				sb2.compareTo("BARE")==0);    	
    	
    }    
    
    void checkString(String actual, String expect){
    	assertTrue("wrong param value: "+actual+"expected: "+expect, expect.compareTo(actual)==0);
    	
    }
    
    /**
     * @testStrategy  same as prior testcase, only this one
     * uses a nondefault namespace on the elements in wsdl, to force the presence of the annotation
     * with targetnamespace set to a nondefault value.
     * 
     *  If the prior testcase is failing for bare style use, just delete it and use this one. 
     * 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description=" same as prior testcase, only this one uses a nondefault namespace on the elements in wsdl, to force the presence of the annotation with targetnamespace set to a nondefault value.  If the prior testcase is failing for bare style use, just delete it and use this one. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testw2j2(){
    	System.out.println("********** testw2j2 is running****************");
    	imp.setWorkingDir(imp.getWorkingDir()+"/../w2j2");
    	imp.w2j("","src/annotations/reqrespwrappers/server/W2JWrapperCheck2.wsdl");
    	AnnotationHelper anh = new AnnotationHelper(
    			"annotations.reqrespwrappers.server.W2JWrapperCheck2",
    			imp.getWorkingDir());
    	assertNotNull("can't find class", anh);
    	
    	// first, if w2j has decided that the method will use non=wrapper style, then there should be a
    	// SOAPBinding anno on the method and param style will be bare  
    	// If there's not one, we'll get an exception here.
    	String sb1 = null;
    	boolean bareStyle = true;
    	try{
    	sb1 = anh.getMethodElement("echo1","SOAPBinding", "parameterStyle");
    	} catch ( NullPointerException e ){
    		System.out.println("no SOAPBinding anno on echo1, may be wrapper style");
    		bareStyle=false;
    	}  
    	
    	// if we have the annotation, does it declare bare (non wrapper) style in use? 
    	if ( (bareStyle && sb1.compareTo("BARE")==0)  ){
    		System.out.println("SOAPBbinding anno is present, but BARE is not declared" +
    				", assuming wrapped style");
    		bareStyle=false;
    	}
    	
    	// if not bare style, check the req/resp annotations 
    	// note that it's still ok for them to be missing if all values are default,
    	// and we are not programatically checking for that.
    	
    	if (bareStyle){
    		System.out.println("Webreq/resp annotatoin checks skipped due to bare style");    		
    	} else{
    		System.out.println("echo1 is wrappered, checking req/resp annos");
	    	
	    	
	    	Annotation a1 = anh.getMethodAnnotation("echo1","ResponseWrapper");    	
	    	
	    	assertNotNull("can't find method", a1);
	    	String s1 = anh.getMethodElement("echo1","ResponseWrapper","localName");
	    	System.out.println(s1);
	    	String s2 = anh.getMethodElement("echo1", "ResponseWrapper", "targetNamespace");
	    	System.out.println(s2);
	    	String s3 = anh.getMethodElement("echo1", "ResponseWrapper", "className");
	    	System.out.println(s3);
	    	
	    	Annotation a2 = anh.getMethodAnnotation("echo1","RequestWrapper");    	
	    	assertNotNull("can't find class", anh);
	    	assertNotNull("can't find method", a1);
	    	String s4 = anh.getMethodElement("echo1","RequestWrapper","localName");
	    	System.out.println(s4);
	    	String s5 = anh.getMethodElement("echo1", "RequestWrapper", "targetNamespace");
	    	System.out.println(s5);
	    	String s6 = anh.getMethodElement("echo1", "RequestWrapper", "className");
	    	System.out.println(s6);
	    	
	    	// check for proper values
	    	checkString("echo1Response", s1);
	    	checkString("http://echo1.server.reqrespwrappers.annotations/", s2);
	    	checkString("annotations.reqrespwrappers.server.echo1.Echo1Response", s3);
	    	checkString("echo1", s4);
	    	checkString("http://echo1.server.reqrespwrappers.annotations/", s5);
	    	checkString("annotations.reqrespwrappers.server.echo1.Echo1",s6);    	
	    	
    	}  // end if barestyle
    	
    	/*
    	 * the echo2 method should not be wsdl2java'd in wrapper style, because the opname, echo2,
    	 * does not map to the localname, notarg0.  It should have a SOAPBinding anno on it to warn
    	 * the processor that parameters are bare.
    	 */
    	
    	String sb2 = anh.getMethodElement("echo2","SOAPBinding", "parameterStyle");
    	assertTrue("did not find expected soapbinding bare style declaration on echo2", 
    				sb2.compareTo("BARE")==0);    	
    	
    } 
    
}
