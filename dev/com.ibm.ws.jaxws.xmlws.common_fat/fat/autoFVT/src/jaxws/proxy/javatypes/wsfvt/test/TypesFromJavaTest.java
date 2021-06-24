//
// @(#) 1.14 autoFVT/src/jaxws/proxy/javatypes/wsfvt/test/TypesFromJavaTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/26/10 15:28:39 [8/8/12 06:55:44]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 01/05/07 sedov       413205          Modified list/set/array comparison routines
// 01/22/07 sedov       415477          Added additional types
// 01/25/07 sedov       416630          Added polymorphic & vector test
// 11/29/07 sedov       486125          1dArray w/nulls matches new JAXB behavior
// 04/14/08             561054          add caching test
// 01/22/10 btiffany    635671          add some diag messages for date tests
// 01/26/10 btiffany    635671.1        turn on trace for tests failing on z. 

package jaxws.proxy.javatypes.wsfvt.test;

import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import jaxws.proxy.common.Constants;
import jaxws.proxy.javatypes.wsfvt.javatypes.ColorENUM;
import jaxws.proxy.javatypes.wsfvt.javatypes.CustomComplexType;
import jaxws.proxy.javatypes.wsfvt.javatypes.CustomDerrivedType;
import jaxws.proxy.javatypes.wsfvt.javatypes.JavaTypesPort;
import jaxws.proxy.javatypes.wsfvt.javatypes.ProxyJavaTypesService;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import net.java.dev.jaxb.array.StringArray;
import com.ibm.websphere.simplicity.config.TraceService;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.websphere.simplicity.runtime.TraceServiceMBean;



/**
 * JaxRpcTests for handling of varios java types and their conversion to/from XML
 */
public class TypesFromJavaTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
    static String saved_tracespec=null;

	public TypesFromJavaTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(TypesFromJavaTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}
    
    public static void traceOn() throws Exception{
        System.out.println("turning on trace");
        com.ibm.websphere.simplicity.ApplicationServer server = TopologyDefaults.getDefaultAppServer();        
         TraceServiceMBean t = server.getRuntimeServices().getTraceService(); 
            //TraceService.getInstance(server);
        saved_tracespec = t.getTraceSpecification();
        System.out.println("the prior trace setting was: "+saved_tracespec);
        t.setTraceSpecification("com.ibm.ws.websvcs.*=all=enabled:org.apache.axis2.*=all=enabled");
        
        
        
       
    }
    
    public static void traceOff() throws Exception{
        System.out.println("turning off trace");
        com.ibm.websphere.simplicity.ApplicationServer server = TopologyDefaults.getDefaultAppServer();      
        //com.ibm.websphere.simplicity.config.TraceService t =  TraceService.getInstance(server);
        TraceServiceMBean t = server.getRuntimeServices().getTraceService(); 
        t.setTraceSpecification("*=info");
        if (saved_tracespec != null){
            // restore it to what it was before
            t.setTraceSpecification(saved_tracespec);            
        }
            
    }

	/**
	 * @testStrategy Send a simple int
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a simple int",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_java_int() {
		JavaTypesPort port = getPort();

		int expected = 41;
		int actual = 0;

		actual = port.pingInt(expected);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send an java.math.Integer
	 */

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send an java.math.Integer",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_java_integer() {
		JavaTypesPort port = getPort();

		Integer expected = 41;
		Integer actual = 0;

		actual = port.pingInteger(expected);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send a simple char
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a simple char",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_java_char() {
		JavaTypesPort port = getPort();

		Integer expected = 41;
		Integer actual = 0;

		actual = port.pingChar(expected);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send a simple long
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a simple long",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_java_long() {
		JavaTypesPort port = getPort();

		long expected = 123456789;
		long actual = 0;

		actual = port.pingLong(expected);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send a simple java enum
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a simple java enum",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_java_enum() {
		JavaTypesPort port = getPort();

		ColorENUM expected = ColorENUM.RED;
		ColorENUM actual = null;

		actual = port.pingEnum(expected);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send an array of java enum
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send an array of java enum",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_java_enumArray() {
		JavaTypesPort port = getPort();

		List<ColorENUM> expected = new LinkedList<ColorENUM>();
		;
		List<ColorENUM> actual = null;

		expected.add(ColorENUM.BLUE);
		expected.add(ColorENUM.RED);
		expected.add(ColorENUM.GREEN);
		expected.add(ColorENUM.BLUE);

		actual = port.pingEnumArray(expected);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send a java.util.Date
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a java.util.Date",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_java_date() throws Exception {
    	// modified for liberty testing
        //traceOn();
		JavaTypesPort port = getPort();

		DatatypeFactory fac = DatatypeFactory.newInstance();
		Date d = (new GregorianCalendar()).getTime();

		XMLGregorianCalendar expected = fac
				.newXMLGregorianCalendar(d.getYear() + 1900, d.getMonth() + 1,
						d.getDate(), d.getHours(), d.getMinutes(), d
								.getSeconds(), 0, d.getTimezoneOffset());
		XMLGregorianCalendar actual = null;

        System.out.println("expected.toString: "+expected.toString());
		actual = port.pingDate(expected);
        if (actual != null){
            System.out.println("actual.toString: "+actual.toString());
            System.out.println("result of expected.equals(actual): " + expected.equals(actual));
        } else{
            System.out.println("actual was null");
        } 
        // modified for liberty testing
        //traceOff();
		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send a java.util.Date
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a java.util.Date",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_java_gregorianCalendar() throws Exception {
    	// modified for liberty testing
        //traceOn();
		JavaTypesPort port = getPort();

		DatatypeFactory fac = DatatypeFactory.newInstance();
		Date d = (new GregorianCalendar()).getTime();

		XMLGregorianCalendar expected = fac
				.newXMLGregorianCalendar(d.getYear() + 1900, d.getMonth() + 1,
						d.getDate(), d.getHours(), d.getMinutes(), d
								.getSeconds(), 0, d.getTimezoneOffset());
		XMLGregorianCalendar actual = null;

        System.out.println("expected.toString: "+expected.toString());
		actual = port.pingGregorianCalendar(expected);
        if (actual != null){
            System.out.println("actual.toString: "+actual.toString());
            System.out.println("result of expected.equals(actual): " + expected.equals(actual));
        } else{
            System.out.println("actual was null");        
        } 
        // modified for liberty testing
        //traceOff();
		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send an unannotated java bean
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send an unannotated java bean",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_pojo_base() throws Exception {
		JavaTypesPort port = getPort();

		CustomComplexType expected = new CustomComplexType();
		CustomComplexType actual = null;

		expected.setFirstName("Joe");
		expected.setLastName("Bob");
		expected.setId(1234);

		actual = port.pingJavaBean(expected);

		assertEquals("Unexpected response", expected.getFirstName(), actual
				.getFirstName());
		assertEquals("Unexpected response", expected.getLastName(), actual
				.getLastName());
		assertEquals("Unexpected response", expected.getId(), actual.getId());
	}
	
	/**
	 * @testStrategy Send an unannotated java bean
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send an unannotated java bean",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_pojo_polymorphic() throws Exception {
		JavaTypesPort port = getPort();

		CustomDerrivedType expected = new CustomDerrivedType();
		CustomComplexType actual = null;

		expected.setFirstName("Joe");
		expected.setLastName("Bob");
		expected.setId(1234);

		actual = port.pingJavaBean(expected);

		assertTrue("Expecting CustomDerrivedType", actual instanceof CustomDerrivedType);
		assertEquals("Unexpected response", expected.getFirstName(), actual
				.getFirstName());
		assertEquals("Unexpected response", expected.getLastName(), actual
				.getLastName());
		assertEquals("Unexpected response", expected.getId(), actual.getId());
	}	

	/**
	 * @testStrategy Send a list (not a specific implementation)
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a list (not a specific implementation)",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_vector_string() throws Exception {
		JavaTypesPort port = getPort();

		List<String> expected = new LinkedList<String>();
		List<String> actual = null;

		expected.add("String1");
		expected.add("String2");
		expected.add("String3");

		actual = port.pingVector(expected);

		assertEquals("Unexpected response", expected, actual);
	}
	
	/**
	 * @testStrategy Send a list (not a specific implementation)
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a list (not a specific implementation)",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_vector_generic() throws Exception {
		JavaTypesPort port = getPort();

		List<Object> expected = new LinkedList<Object>();
		List<Object> actual = null;

		CustomComplexType cct1 = new CustomComplexType();
		cct1.setFirstName("first1");
		cct1.setLastName("last1");
		cct1.setId(1);
		expected.add(cct1);
		
		expected.add("String");

		actual = port.pingVectorGeneric(expected);

		if (actual != null && actual.size() != 0)
		for (Object obj: actual){
			System.out.println("actual=" + obj);
		}
		
		assertTrue("Unexpected response", actual.get(0) instanceof CustomComplexType);
	}	

	/**
	 * @testStrategy Send a list (not a specific implementation)
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a list (not a specific implementation)",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_vector_pojo() throws Exception {
		JavaTypesPort port = getPort();

		List<CustomComplexType> expected = new LinkedList<CustomComplexType>();
		List<CustomComplexType> actual = null;

		
		CustomComplexType cct1 = new CustomComplexType();
		cct1.setFirstName("first1");
		cct1.setLastName("last1");
		cct1.setId(1);
		expected.add(cct1);

		CustomComplexType cct2 = new CustomComplexType();
		cct2.setFirstName("first2");
		cct2.setLastName("last2");
		cct2.setId(2);
		expected.add(cct2);

		actual = port.pingVectorOfPojo(expected);

		assertEquals("Unexpected response", expected.size(), actual.size());
	}	
	
	/**
	 * @testStrategy Send a list (not a specific implementation)
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a list (not a specific implementation)",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_list() throws Exception {
		JavaTypesPort port = getPort();

		List<String> expected = new LinkedList<String>();
		List<String> actual = null;

		expected.add("String1");
		expected.add("String2");
		expected.add("String3");

		actual = port.pingList(expected);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send an ArrayList (not a generic interface)
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send an ArrayList (not a generic interface)",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_list_ArrayList() throws Exception {
		JavaTypesPort port = getPort();

		List<String> expected = new LinkedList<String>();
		List<String> actual = null;

		expected.add("String1");
		expected.add("String2");
		expected.add("String3");

		actual = port.pingArrayList(expected);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send an LinkedList (not a generic interface)
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send an LinkedList (not a generic interface)",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_list_LinkedList() throws Exception {
		JavaTypesPort port = getPort();

		List<String> expected = new LinkedList<String>();
		List<String> actual = null;

		expected.add("String1");
		expected.add("String2");
		expected.add("String3");

		actual = port.pingArrayList(expected);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send a 1D array
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a 1D array",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_array_1d() throws Exception {
		JavaTypesPort port = getPort();

		List<String> expected = new LinkedList<String>();
		List<String> actual = null;

		expected.add("String1");
		expected.add("String2");
		expected.add("String3");

		actual = port.pingArray(expected);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send a 1D array with null values. Schema does not permit
	 *               nilables
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a 1D array with null values. Schema does not permit nilables",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_array_1d_withNulls() throws Exception {
		JavaTypesPort port = getPort();

		List<String> expected = new LinkedList<String>();
		List<String> actual = null;

		expected.add("String1");
		expected.add(null);
		expected.add("String3");

		actual = port.pingArray(expected);
		//expected.remove(1); // (1); // until validation in jax-b is enabled, ""=null
		//expected.add(1, "");
		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send a 2D array
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a 2D array",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_array_2d() throws Exception {
		JavaTypesPort port = getPort();

		List<StringArray> expected = new LinkedList<StringArray>();
		List<StringArray> actual = null;

		StringArray row0 = new StringArray();
		StringArray row1 = new StringArray();
		StringArray row2 = new StringArray();

		row0.getItem().add("String_row0_col0");
		row0.getItem().add("String_row0_col1");
		row0.getItem().add("String_row0_col2");

		row1.getItem().add("String_row1_col0");
		row1.getItem().add("String_row1_col1");
		row1.getItem().add("String_row1_col2");

		row2.getItem().add("String_row2_col0");
		row2.getItem().add("String_row2_col1");
		row2.getItem().add("String_row2_col2");

		expected.add(row0);
		expected.add(row1);
		expected.add(row2);

		actual = port.ping2Darray(expected);

		assertEquals("row0 does not match", expected.get(0).getItem(), actual
				.get(0).getItem());
		assertEquals("row1 does not match", expected.get(1).getItem(), actual
				.get(1).getItem());
		assertEquals("row2 does not match", expected.get(2).getItem(), actual
				.get(2).getItem());
	}

	/**
	 * @testStrategy Send a 2D array with null values
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a 2D array with null values",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_array_2d_withNulls() throws Exception {
		JavaTypesPort port = getPort();

		List<StringArray> expected = new LinkedList<StringArray>();
		List<StringArray> actual = null;

		StringArray row0 = new StringArray();
		StringArray row1 = new StringArray();
		StringArray row2 = new StringArray();

		row0.getItem().add(null);
		row0.getItem().add("String_row0_col1");
		row0.getItem().add("String_row0_col2");

		row1.getItem().add("String_row1_col0");
		row1.getItem().add(null);
		row1.getItem().add("String_row1_col2");

		row2.getItem().add("String_row2_col0");
		row2.getItem().add("String_row2_col1");
		row2.getItem().add(null);

		expected.add(row0);
		expected.add(row1);
		expected.add(row2);

		actual = port.ping2Darray(expected);

		// see 1d_array with nulls case
		// assertEquals("Unexpected response", expected, actual);
	}

	/*
	 * public void testTypes_HashMap() throws Exception{ JavaTypesPort port =
	 * getPort();
	 * 
	 * HashMap expected = new HashMap(); HashMap actual = null;
	 * 
	 * expected.put("key0", "value0"); expected.put("key1", "value1");
	 * expected.put("key2", "value2");
	 * 
	 * actual = port.pingHashMap(expected);
	 * 
	 * assertEquals("Unexpected response", expected, actual); } //
	 */

	/**
	 * @testStrategy Test for working with java.util.Set. Sets do not care about
	 *               order of elements.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for working with java.util.Set. Sets do not care about order of elements.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_set() throws Exception {
		JavaTypesPort port = getPort();

		List<Integer> expected = new LinkedList<Integer>();
		List<Integer> actual = null;

		expected.add(10);
		expected.add(20);
		expected.add(30);
		expected.add(40);
		expected.add(50);

		actual = port.pingSet(expected);

		// since a Set of {a,b}={b,a}, but the client uses a list representation
		// we need to sort the list to obtain equivalence
		Collections.sort(expected);
		Collections.sort(actual);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Test for a method that has multiple IN and INOUT Holders
	 *               and returns a value E.g., String op(IN, INOUT, OUT)
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for a method that has multiple IN and INOUT Holders and returns a value E.g., String op(IN, INOUT, OUT)",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testNonVoidHolder() throws Exception {
		JavaTypesPort port = getPort();

		String expected = Constants.THE_STRING;
		String actual = null;

		actual = port.pingWeirdoMethod(expected, new Holder<String>(expected),
				new Holder<Integer>(Constants.THE_INT));

		assertEquals("Unexpected response", expected, actual);
	}
    
    /**
     * call each test method multiple times to see if we can find any
     * caching problems in jaxb fastpath. 
     */
    public void testCaching()throws Exception {
        int i = 0;
        int j = 0;
        while(j++ <3){
         //   testTypes_java_date(); printcount(i++);
            testTypes_java_enumArray(); printcount(i++);
            testTypes_java_enum(); printcount(i++);
         //   testTypes_java_gregorianCalendar(); printcount(i++);
            testTypes_java_int(); printcount(i++);
            testTypes_java_integer(); printcount(i++);
            testTypes_java_long(); printcount(i++);
            testTypes_list(); printcount(i++);
            testNonVoidHolder(); printcount(i++);
            testTypes_array_1d(); printcount(i++);
            testTypes_array_1d(); printcount(i++);
            testTypes_array_1d_withNulls(); printcount(i++);
            testTypes_array_2d(); printcount(i++);
            testTypes_array_2d_withNulls(); printcount(i++);
            testTypes_java_char(); printcount(i++);
            testTypes_list_ArrayList(); printcount(i++);
            testTypes_list_LinkedList(); printcount(i++);
            testTypes_pojo_base(); printcount(i++);
            testTypes_pojo_polymorphic(); printcount(i++);
            testTypes_set(); printcount(i++);
            testTypes_vector_generic(); printcount(i++);
            testTypes_vector_pojo(); printcount(i++);
            testTypes_vector_string(); printcount(i++);
        }   
        
    }
    private void printcount(int i){
        System.out.println("------ "+ ++i);
    }

	/**
	 * Utility method to obtain a proxy instance
	 * 
	 * @return
	 */
	private JavaTypesPort getPort() {
		ProxyJavaTypesService service = new ProxyJavaTypesService();
		JavaTypesPort port = service.getJavaTypesPort();
		assertNotNull("Port is null", port);

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.JAVATYPES_BASE + "/services/ProxyJavaTypesService");

        // note that this is also burned in in buildTest.xml.         
		System.out.println("Endpoint=" + Constants.JAVATYPES_BASE
				+ "/services/ProxyJavaTypesService");

		return port;
	}
}
