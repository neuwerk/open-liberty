/*
 * @(#) 1.3 autoFVT/src/annotations/resource/test/ResourceFieldInjectionTest.java, WAS.websvcs.fvt, WASX.FVT 1/10/07 13:53:17 [7/11/07 13:10:52]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * NOTE: This was a late add to the test bucket and will be claimed under
 * annotations.Webservice in TTT. 
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 10/23/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.resource.test;

import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.resource.conflict.conflictclient.ConflictClient;
import annotations.resource.injection.injectionclient.InjectionClient;
import annotations.resource.primitiveval.primitivevalclient.PrimitiveValClient;

public class ResourceFieldInjectionTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
   
    
	/*
	 * String arg constructor: This constructor allows for altering the test
	 * suite to include just one test.
	 */
	public ResourceFieldInjectionTest(String name) {
		super(name);
	}

    /*
	 * @testStrategy this test case will check if the method marked
	 * with @PostConstruct in the service implementation is executed and 
	 * also the webServiceContext injected (via field injection) into the
	 * container is available. If both are true, the service should return
	 * true for the case if postConstructor is visited or not, and it also 
	 * returns the servletContextName from the webServiceContext.
     * 
     * (@PreDestroy was not implemented in wsfp because it needs some
     * functionality from jsr109 in the container, which was6 doesn't have.)
	 * 
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_fieldInjection_pos() throws Exception {
        System.out.println("*********** running test_runtime_fieldInjection_pos()*******");
    	String servletContextName;
    	boolean postConstructVisited;
    	
    	InjectionClient client = new InjectionClient();
    	
    	servletContextName = client.getServletContextNameFromFieldInj();
    	postConstructVisited = client.getPCVisitedFromFieldInj();
        System.out.println("servletContextName="+servletContextName+"\n"+
                           "postConstructVisited="+postConstructVisited);
                            
        
        
    	
    	// check is the servletContextName is also the same in WAS...
 		assertTrue("servletContextName: " + servletContextName +
 				", isPostConstructVisited: " + postConstructVisited, 
 				postConstructVisited && servletContextName.equals("FieldInjectionImplService"));
        System.out.println("********** test passed **********");
	}

    /*
	 * @testStrategy this test case will check if the method marked
	 * with @PostConstruct in the service implementation is executed and 
	 * also the webServiceContext injected (via method) into the container is available.
	 * If both are true, the service should return true for the case if
	 * postConstructor is visited or not, and it also returns the 
	 * servletContextName from the webServiceContext.
	 * 
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_methodInjection_pos() throws Exception {
        System.out.println("*********** running test_runtime_methodInjection_pos()*******");
    	String servletContextName;
    	boolean postConstructVisited;
    	
    	InjectionClient client = new InjectionClient();
    	
    	servletContextName = client.getServletContextNameFromMethodInj();
    	postConstructVisited = client.getPCVisitedFromMethodInj();
    	
    	// check is the servletContextName is also the same in WAS...
 		assertTrue("servletContextName: " + servletContextName +
 				", isPostConstructVisited: " + postConstructVisited, 
 				postConstructVisited && servletContextName.equals("MethodInjectionImplService"));
        System.out.println("********** test passed **********");
	}

    /*
	 * @testStrategy this test case will check the type consistency in
	 * resource declaration.
	 * webServiceContext is injected (via field injection) into the
	 * container via @Resource and the annotation declares the type of the
	 * resource as WebServiceContext. The actual declaration specifies the type 
	 * as an Object. This is a positive test case and the service should return
	 * the servletContextName from the webServiceContext.
	 * 
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_fieldConflict_pos() throws Exception {
        System.out.println("*********** running test_runtime_fieldConflict_pos()*******");
    	String servletContextName;
    	ConflictClient client = new ConflictClient();
    	
    	servletContextName = client.getServletContextNameFromFieldConflict();
    	
    	// check if the servletContextName is also the same in WAS...
 		assertTrue("servletContextName: " + servletContextName, 
 				servletContextName.equals("FieldConflictTypeImplService"));
        System.out.println("********** test passed **********");
	}

    /*
	 * @testStrategy this test case will check the type consistency in
	 * resource declaration.
	 * webServiceContext is injected (via method injection) into the
	 * container via @Resource and the annotation declares the type of the
	 * resource as an Object. The actual declaration specifies the type 
	 * as an WebServiceContext. This is a positive test case and the service 
	 * should return the servletContextName from the webServiceContext.
	 * 
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_methodConflict_pos() throws Exception {
        System.out.println("*********** running test_runtime_methodConflict_pos()*******");
    	String servletContextName;   	
    	ConflictClient client = new ConflictClient();
    	
    	servletContextName = client.getServletContextNameFromMethodConflict();
    	
    	// check is the servletContextName is also the same in WAS...
 		assertTrue("servletContextName: " + servletContextName, 
 				servletContextName.equals("MethodConflictTypeImplService"));
        System.out.println("********** test passed **********");
	}
    
    //PK96899.fvt
    /*
     * @testStrategy this test case will check incorrect validation 
     * performed on @resource annotations involving primitive datatypes.
     * A <env-entry> element having type java.lang.Integer is injected
     * in a field or method associated with a primitive type, i.e int in this case.
     * It should not throw any error as as a java.lang.Integer type is 
     * compatible with the int field type. 
     * 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    	    expectedResult="",
    	    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_primitive_type_fieldvalidation_pos(){
    	System.out.println("**** running test_primitive_type_fieldvalidation_pos()****007");
    	
    	int port;
    	
    	PrimitiveValClient client = new PrimitiveValClient();
    	port = client.getInjectedNumber();
    	
    	System.out.println("****Port :"+port);
    	
    	//check is the number is same as present in web.xml
    	assertTrue("Test failed: 700 not equals "+port, port==700);
    	System.out.println("********** test passed **********");
    }
    
    public static junit.framework.Test suite() {
    	System.out.println(ResourceFieldInjectionTest.class.getName());
        return new TestSuite(ResourceFieldInjectionTest.class);
    }   
	
	public static void main(String[] args)throws Exception {	
		if(false){
            TestRunner.run(suite());
        } else {
            ResourceFieldInjectionTest t = new ResourceFieldInjectionTest("");
            t.test_runtime_fieldInjection_pos();
            t.test_runtime_methodInjection_pos();
            t.test_runtime_fieldConflict_pos();
            t.test_runtime_methodConflict_pos();
        }
	}
}
