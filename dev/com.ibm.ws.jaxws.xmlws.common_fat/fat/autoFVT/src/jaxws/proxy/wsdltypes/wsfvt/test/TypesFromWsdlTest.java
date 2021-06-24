//
// @(#) 1.2.1.19 autoFVT/src/jaxws/proxy/wsdltypes/wsfvt/test/TypesFromWsdlTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 10/4/09 16:10:04 [8/8/12 06:55:16]
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
// 09/15/06 sedov       390173          New File/changed to doc/lit-wrapped from bare
// 09/15/06 sedov       390173          Commented out non-beta tests
// 12/01/06 sedov       408880          Added hex and list tests
// 01/05/07 sedov       413180          Fixed array comparisons
// 01/10/07 sedov       413290          Updated polymorphic tests
// 01/22/07 sedov       415799          Updated array comparisons for correct jax-b behavior
// 02/11/09 jramos      574405          Update testTypes_array_java
//   (note: prior change affected the signature wsimport generates for testJava1dArray)
// 04/14/09 btiffany    561054          add caching test
// 09/23/09 jramos      615311          Modify testTypes_array_java for HP
//
package jaxws.proxy.wsdltypes.wsfvt.test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import jaxws.proxy.common.Constants;
import jaxws.proxy.wsdltypes.wsfvt.wsdltypes.*;
import jaxws.proxy.wsdltypes.wsfvt.wsdltypes1.*;
import jaxws.proxy.wsdltypes.wsfvt.wsdltypes2.*;
import jaxws.proxy.wsdltypes.wsfvt.wsdltypes3.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Test case for sending/receiving wsdl primitive, complex and erroneous types
 */
public class TypesFromWsdlTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	static final String WSDL_TYPES_ENDPOINT = Constants.WSDLTYPES_BASE + "/services/ProxyWsdlTypesService";
	
	// some constants for Date/Time tests
	final static int YEAR = 2006;

	final static int MONTH = 8;

	final static int DATE = 15;

	final static int HOUR = 15;

	final static int MIN = 37;

	final static int SEC = 30;

	// some constants for array elements
	final static String ELEM0 = "element_0";

	final static String ELEM1 = "element_1";

	final static String ELEM2 = "element_2";

	public TypesFromWsdlTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(TypesFromWsdlTest.class);
		//suite.addTest(new TypesFromWsdlTest("testTypes_xsdHexBinary"));
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy Test for transmitting an xsd:int
	 */

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an xsd:int",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_int() {
		WsdlTypesPort port = getPort();

		int expected = Constants.THE_INT;
		int actual = -1;

		actual = port.typesInt(expected);

		assertEquals("expected != actual", expected, actual);
	}

	/**
	 * @testStrategy Test for transmitting an xsd:integer
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an xsd:integer",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_integer() {

		WsdlTypesPort port = getPort();

		BigInteger expected = new BigInteger("" + Constants.THE_INT);
		BigInteger actual = null;

		actual = port.typesInteger(expected);

		assertEquals("expected != actual", expected, actual);
	}

	/**
	 * @testStrategy Test for transmitting an xsd:unsignedShort
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an xsd:unsignedShort",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_unsignedShort() {

		WsdlTypesPort port = getPort();

		int expected = Constants.THE_INT;
		int actual = 0;

		actual = port.typesChar(expected);

		assertEquals("expected != actual", expected, actual);
	}

	/**
	 * @testStrategy Test for transmitting an xsd:unsignedShort
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an xsd:unsignedShort",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_elementRef() {

		WsdlTypesPort port = getPort();

		Holder<String> hs = new Holder<String>("hello");
		Holder<TypesQName> hq = new Holder<TypesQName>(new TypesQName());
		Holder<Types1DArray> hl = new Holder<Types1DArray>(new Types1DArray());
		
		hq.value.setInQName(new QName("http://hello", "world", "hw"));
		hl.value.setMyArray(new MyStringArray());
		hl.value.getMyArray().getArray().add("hello");
		
		port.typesElementRef(hs, hq, hl);

		//assertEquals("expected != actual", expected, actual);
	}
	
	/**
	 * @testStrategy Test for transmitting an xsd:QName
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an xsd:QName",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_QName() {

		WsdlTypesPort port = getPort();

		QName expected = Constants.DOCLITWR_SERVICE;
		QName actual = null;

		actual = port.typesQName(expected);

		assertEquals("expected != actual", expected, actual);
	}

	/**
	 * @testStrategy Test for transmitting an xsd:anyType with a null value
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an xsd:anyType with a null value",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_anyType_null() {

		WsdlTypesPort port = getPort();

		Object expected = null;
		Object actual = null;

		Holder<Object> any = new Holder<Object>(expected);
		Holder<String> type = new Holder<String>();
		port.typesAny(any, type);
		actual = any.value;

		System.out.println(getName() + ": server received " + type.value);

		assertEquals("expected != actual", expected, actual);
	}

	/**
	 * @testStrategy Test for transmitting an xsd:anyType with a primitive type
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an xsd:anyType with a primitive type",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_anyType_primitive() {

		WsdlTypesPort port = getPort();

		Object expected = Constants.THE_STRING;
		Object actual = null;

		Holder<Object> any = new Holder<Object>(expected);
		Holder<String> type = new Holder<String>();
		port.typesAny(any, type);
		actual = any.value;

		System.out.println(getName() + ": server received " + type.value);

		assertEquals("expected != actual", expected, actual);
	}

	/**
	 * @testStrategy Test for transmitting an xsd:anyType with a jaxb object
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an xsd:anyType with a jaxb object",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_anyType_jaxbComplex() {

		WsdlTypesPort port = getPort();

		MyBaseType mbt = new MyBaseType();
		mbt.setMyInt(Constants.THE_INT);
		mbt.setMyString(Constants.THE_STRING);

		Object expected = mbt;
		Object actual = null;

		Holder<Object> any = new Holder<Object>(expected);
		Holder<String> type = new Holder<String>();
		port.typesAny(any, type);
		actual = any.value;

		System.out.println(getName() + ": server received " + type.value);

		assertTrue("expected != actual", actual instanceof MyBaseType);
	}

	/**
	 * @testStrategy Test for transmitting an xsd:anyType with an array.
	 * 				 WSE is the expected outcome
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an xsd:anyType with an array. WSE is the expected outcome",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_anyType_array() {

		WsdlTypesPort port = getPort();

		Object expected = new String[] { "A", "B", Constants.THE_STRING };
		Object actual = null;

		Holder<Object> any = new Holder<Object>(expected);
		Holder<String> type = new Holder<String>();
		
		try {
			port.typesAny(any, type);
		} catch (WebServiceException wse){
			Constants.logStack(wse);
		}
	}

	/**
	 * @testStrategy Test for transmitting a type with restriction by
	 *   pattern (regular expression)
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a type with restriction by pattern (regular expression)",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_restriction_pattern() throws Exception {
		WsdlTypesPort port = getPort();
		
		String expected = "Hello World!";
		String actual = null;
		
		actual = port.typesRestrictionPattern(expected);
		
		assertEquals("Expected != actual", expected, actual);
	}
	
	/**
	 * @testStrategy Test for transmitting a type with restriction by
	 *   minInclusive/maxInclusive
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a type with restriction by minInclusive/maxInclusive",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_restriction_minMax() throws Exception {
		WsdlTypesPort port = getPort();
		
		int expected = 500;
		int actual = 0;
		
		actual = port.typesRestrictionMinMax(expected);
		
		assertEquals("Expected != actual", expected, actual);
	}
	
	/**
	 * @testStrategy Test for transmitting a type with restriction by
	 *  an enumeration
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a type with restriction by an enumeration",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_restriction_enumeration() throws Exception {
		WsdlTypesPort port = getPort();
		
		int expected = 5; // primes: 2 3 5 7
		int actual = 0;
		
		actual = port.typesRestrictionEnum(expected);
		
		assertEquals("Expected != actual", expected, actual);
	}
	
	
	/**
	 * @testStrategy Test for transmitting a xsd:list of string
	 **/
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a xsd:list of string",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_list_string() throws Exception {
		WsdlTypesPort port = getPort();
		
		String[] expected = {"string1", "string2", "string3 string4"};
		String[] actual = null;
		
		actual = port.typesStringList(expected);
		
		assertEquals("Array Sizes do not match", expected.length + 1, actual.length);
		for (int i=0; i < expected.length - 1; i++){
			assertEquals("Expected != actual", expected[i], actual[i]);
		}
	}
	
	/**
	 * @testStrategy Test for transmitting a xsd:list of integer
	 **/
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a xsd:list of integer",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_list_integer() throws Exception {
		WsdlTypesPort port = getPort();
		
		BigInteger[] expected = {new BigInteger("10"), new BigInteger("500000"), new BigInteger("1234567890")};
		BigInteger[] actual = null;
		
		actual = port.typesIntegerList(expected);
		
		assertEquals("Array Sizes do not match", expected.length, actual.length);
		for (int i=0; i < expected.length; i++){
			assertEquals("Expected != actual", expected[i], actual[i]);
		}
	}
	
	/**
	 * @testStrategy Test for transmitting a xsd:list of int
	 **/
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a xsd:list of int",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_list_int() throws Exception {
		WsdlTypesPort port = getPort();
		
		Integer[] expected = {10, 20, 30, 32000};
		Integer[] actual = null;
		
		actual = port.typesIntList(expected);
		
		assertEquals("Array Sizes do not match", expected.length, actual.length);
		for (int i=0; i < expected.length; i++){
			assertEquals("Expected != actual", expected[i], actual[i]);
		}
	}
	
	/**
	 * @testStrategy Test for transmitting a xsd:list of decimal
	 **/
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a xsd:list of decimal",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_list_decimal() throws Exception {
		WsdlTypesPort port = getPort();
		
		BigDecimal[] expected = {new BigDecimal("3.1428"), new BigDecimal("7"),  new BigDecimal("2.7182818284590452353603")};
		BigDecimal[] actual = null;
		
		actual = port.typesDecimalList(expected);
		
		assertEquals("Array Sizes do not match", expected.length, actual.length);
		for (int i=0; i < expected.length; i++){
			assertEquals("Expected != actual", expected[i], actual[i]);
		}
	}
	
	/**
	 * @testStrategy Test for transmitting a xsd:list of byte
	 **/
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a xsd:list of byte",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_list_byte() throws Exception {
		WsdlTypesPort port = getPort();
		
		Byte[] expected = {0, -1, 64, 127, -128};
		Byte[] actual = null;
		
		actual = port.typesByteList(expected);
		
		assertEquals("Array Sizes do not match", expected.length, actual.length);
		for (int i=0; i < expected.length; i++){
			assertEquals("Expected != actual", expected[i], actual[i]);
		}
	}
	
	/**
	 * @testStrategy Test for transmitting a xsd:list of byte
	 **/
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a xsd:list of byte",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_attribute() throws Exception {
		WsdlTypesPort port = getPort();
		
		TypesAttributeType expected = new TypesAttributeType();
		TypesAttributeType actual = null;
		
		expected.setRequired("hello");
		actual = port.typesAttribute(expected);
		
		assertEquals("", expected.getDefault(), actual.getDefault());
		assertEquals("", expected.getRequired(), actual.getRequired());
		assertEquals("", expected.getFixed(), actual.getFixed());
		assertEquals("", expected.getOptional(), actual.getOptional());
	}
	
	
	/**
	 * @testStrategy Test for transmitting an xsd:date and xsd:time
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an xsd:date and xsd:time",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_date() throws Exception {
		WsdlTypesPort port = getPort();

		DatatypeFactory fac = DatatypeFactory.newInstance();
		XMLGregorianCalendar expected1 = fac.newXMLGregorianCalendarDate(YEAR,
				MONTH, DATE, DatatypeConstants.FIELD_UNDEFINED);
		XMLGregorianCalendar expected2 = fac.newXMLGregorianCalendarTime(HOUR,
				MIN, SEC, DatatypeConstants.FIELD_UNDEFINED);

		XMLGregorianCalendar actual1 = null;
		XMLGregorianCalendar actual2 = null;

		Holder<XMLGregorianCalendar> date = new Holder<XMLGregorianCalendar>(
				expected1);
		Holder<XMLGregorianCalendar> time = new Holder<XMLGregorianCalendar>(
				expected2);

		port.typesDate(expected1, expected2, date, time);
		actual1 = date.value;
		actual2 = time.value;

		assertEquals("expected1 != actual1", expected1, actual1);
		assertEquals("expected2 != actual2", expected2, actual2);
	}

	/**
	 * @testStrategy Test for transmitting an xsd:dateTime
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an xsd:dateTime",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_dateTime() throws Exception {
		WsdlTypesPort port = getPort();

		DatatypeFactory fac = DatatypeFactory.newInstance();
		XMLGregorianCalendar expected = fac.newXMLGregorianCalendar(YEAR,
				MONTH, DATE, HOUR, MIN, SEC, DatatypeConstants.FIELD_UNDEFINED,
				DatatypeConstants.FIELD_UNDEFINED);
		XMLGregorianCalendar actual = null;

		actual = port.typesDateTime(expected);

		assertEquals("expected != actual", expected, actual);
	}

	/**
	 * @testStrategy Test for transmitting an xsd:gDay xsd:gMonth, xsd:gYear
	 */
    /*@com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an xsd:gDay xsd:gMonth, xsd:gYear",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_gDate() throws Exception {
		WsdlTypesPort port = getPort();

		DatatypeFactory fac = DatatypeFactory.newInstance();
		XMLGregorianCalendar expected1 = fac.newXMLGregorianCalendarDate(
				DatatypeConstants.FIELD_UNDEFINED,
				DatatypeConstants.FIELD_UNDEFINED, DATE,
				DatatypeConstants.FIELD_UNDEFINED);
		//as there is a issue for gMonth format "--MM--" old standard and "--MM" new standard
		XMLGregorianCalendar expected2 = fac.newXMLGregorianCalendarDate(
				DatatypeConstants.FIELD_UNDEFINED,
				MONTH,
				DatatypeConstants.FIELD_UNDEFINED,
				DatatypeConstants.FIELD_UNDEFINED);

		XMLGregorianCalendar expected3 = fac.newXMLGregorianCalendarDate(YEAR,
				DatatypeConstants.FIELD_UNDEFINED,
				DatatypeConstants.FIELD_UNDEFINED,
				DatatypeConstants.FIELD_UNDEFINED);

		XMLGregorianCalendar actual1 = null;
		XMLGregorianCalendar actual2 = null;
		XMLGregorianCalendar actual3 = null;

		// build holders of OUT parameters
		Holder<XMLGregorianCalendar> holder1 = new Holder<XMLGregorianCalendar>(
				null);
		Holder<XMLGregorianCalendar> holder2 = new Holder<XMLGregorianCalendar>(
				null);
		Holder<XMLGregorianCalendar> holder3 = new Holder<XMLGregorianCalendar>(
				null);

		// invoke
		port.typesGDate(expected1, expected2, expected3, holder1, holder2,
				holder3);

		// extract actual results
		actual1 = holder1.value;
		actual2 = holder2.value;
		actual3 = holder3.value;

		// compare values
		assertEquals("expected1 != actual1", expected1, actual1);
		assertEquals("expected2 != actual2", expected2, actual2);
		assertEquals("expected3 != actual3", expected3, actual3);
	}
   */

	/**
	 * @testStrategy Test for transmitting a simple 1D array
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a simple 1D array",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_array() {
		WsdlTypesPort port = getPort();

		MyStringArray expected = new MyStringArray();
		MyStringArray actual = null;

		expected.getArray().add(ELEM0);
		expected.getArray().add(ELEM1);
		expected.getArray().add(ELEM2);

		actual = port.types1DArray(expected);

		assertEquals("expected != actual", expected.getArray(), actual
				.getArray());
	}

	/**
	 * @testStrategy Test for transmitting a simple 1D array with some values
	 *               set to null, this should fail in jax-b
	 *               
	 *               Note: in this version of jax-b, validation is disabled, so
	 *               the test case should pass eventhough the server returns array
	 *               of 4 elements, where the null element is now an empty object
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a simple 1D array with some values set to null, this should fail in jax-b  Note: in this version of jax-b, validation is disabled, so the test case should pass eventhough the server returns array of 4 elements, where the null element is now an empty object",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_array_withNulls_nilableFalse() {
		WsdlTypesPort port = getPort();

		MyStringArray expected = new MyStringArray();
		MyStringArray actual = null;

		expected.getArray().add(ELEM0);
		expected.getArray().add("");
		expected.getArray().add(null);
		expected.getArray().add(ELEM2);

		actual = port.types1DArray(expected);

		System.out.println("Expected: " + expected.getArray());
		System.out.println("Actual: " + actual.getArray());		
		
		// force the comparison to pass eventhough the 2 are not
		// identical
		//expected.getArray().remove(2);
		
		assertEquals("Size should be 4", expected.getArray(), actual.getArray());
	}

	/**
	 * @testStrategy Test for transmitting a simple 1D array where elements can
	 *               be null, this should pass
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a simple 1D array where elements can be null, this should pass",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_array_withNulls_nilableTrue() {
		WsdlTypesPort port = getPort();

		MyNillableStringArray expected = new MyNillableStringArray();
		expected.getArray().add(ELEM0);
		expected.getArray().add(null);
		expected.getArray().add(null);
		expected.getArray().add(ELEM1);

		MyNillableStringArray actual = null;

		actual = port.typesArrayWithNulls(expected);

		System.out.println("Expected: " + expected.getArray());
		System.out.println("Actual: " + actual.getArray());
		
		assertTrue("Array size is not 4", actual.getArray().size() == 4);
		assertEquals("expected != actual", expected.getArray(), actual
				.getArray());
	}

	/**
	 * @testStrategy Test for transmitting a simple 2D array
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a simple 2D array",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_array2D() {
		WsdlTypesPort port = getPort();

		List<MyStringArray> expected = new LinkedList<MyStringArray>();
		List<MyStringArray> actual = null;

		MyStringArray expected_r0 = new MyStringArray();
		expected_r0.getArray().add(ELEM0);
		expected_r0.getArray().add(ELEM1);
		expected_r0.getArray().add(ELEM2);

		MyStringArray expected_r1 = new MyStringArray();
		expected_r1.getArray().add(ELEM2);
		expected_r1.getArray().add(ELEM1);
		expected_r1.getArray().add(ELEM0);

		MyStringArray expected_r2 = new MyStringArray();
		expected_r2.getArray().add(ELEM2);
		expected_r2.getArray().add(ELEM0);
		expected_r2.getArray().add(ELEM1);

		expected.add(expected_r0);
		expected.add(expected_r1);
		expected.add(expected_r2);

		actual = port.types2DArray(expected);

		assertTrue("Array size is not 3", actual.size() == 3);

		List actual_r0 = actual.get(0).getArray();
		List actual_r1 = actual.get(1).getArray();
		List actual_r2 = actual.get(2).getArray();

		assertEquals("expected != actual for row0", expected_r0.getArray(), actual_r0);
		assertEquals("expected != actual for row1", expected_r1.getArray(), actual_r1);
		assertEquals("expected != actual for row2", expected_r2.getArray(), actual_r2);
	}

	/**
	 * @testStrategy Test for underlying jax-b object having an array
	 *               representation
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for underlying jax-b object having an array representation",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_array_java() throws Exception {
		WsdlTypesPort port = getPort();

		/*
		 * NOTE: It is important to use reflection here since the interface
		 * method generated can be either taking an array or take a List as an
		 * argument (depending on which JVM you're using).
		 */
//		if (OperatingSystem.HP.equals(TopologyDefaults.getDefaultAppServer()
//				.getNode().getMachine().getOperatingSystem())) {
//			List<String> expected = new ArrayList<String>();
//			List<String> actual = null;
//
//			expected.add(ELEM0);
//			expected.add(ELEM1);
//			expected.add(ELEM2);
//
//			Method m = port.getClass().getMethod("typesJava1DArray", expected.getClass());
//			actual = (List) m.invoke(port, expected);
//			List<String> actualList = (List) actual;
//			assertEquals("expected != actual for row0", expected, actualList);
//		} else {
			String[] expected = new String[3];
			String[] actual = null;

			expected[0] = ELEM0;
			expected[1] = ELEM1;
			expected[2] = ELEM2;

			Method m = port.getClass().getMethod("typesJava1DArray",
					new String[0].getClass());
			actual = (String[]) m.invoke(port, new Object[] { expected });
			assertEquals("expected != actual for row0",
					Arrays.asList(expected), Arrays.asList(actual));
//		}
	}

	/**
	 * @testStrategy Test for transmitting an array with polymorphic types where
	 *               base type is concrete
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an array with polymorphic types where base type is concrete",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_array_polymorphic() {
		WsdlTypesPort port = getPort();

		List<MyBaseType> expected = new LinkedList<MyBaseType>();
		List<MyBaseType> actual = null;

		MyDerrivedType1 e0 = new MyDerrivedType1();
		e0.setMyBoolean(false);
		e0.setMyInt(Constants.THE_INT);
		e0.setMyString(Constants.THE_STRING);

		MyDerrivedType2 e1 = new MyDerrivedType2();
		e1.setMyDecimal(new BigDecimal("" + Constants.THE_INT));
		e1.setMyInt(Constants.THE_INT);
		e1.setMyLong(123456789);
		e1.setMyString(Constants.THE_STRING);

		MyBaseType e2 = new MyBaseType();
		e2.setMyInt(Constants.THE_INT);
		e2.setMyString(Constants.THE_STRING);

		expected.add(e0);
		expected.add(e1);
		expected.add(e2);

		actual = port.typesPolymorphicArray(expected);

		assertTrue("Array size is not 3", actual.size() == 3);
		assertTrue("Element 0 is not MyDerrivedType1",
				actual.get(0) instanceof MyDerrivedType1);
		assertTrue("Element 1 is not MyDerrivedType2",
				actual.get(1) instanceof MyDerrivedType2);
		assertTrue("Element 2 is not MyBaseType",
				actual.get(2) instanceof MyBaseType);
		
		assertTrue("", compareBean(actual.get(0), e0, MyDerrivedType1.class));
		assertTrue("", compareBean(actual.get(1), e1, MyDerrivedType2.class));
		assertTrue("", compareBean(actual.get(2), e2, MyBaseType.class));
	}

	/**
	 * @testStrategy Test for transmitting an array with polymorphic types where
	 *               the base type is abstract
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting an array with polymorphic types where the base type is abstract",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_array_abstractPolymorphic() {
		WsdlTypesPort port = getPort();
		List<MyAbstractBaseType> expected = new LinkedList<MyAbstractBaseType>();
		List<MyAbstractBaseType> actual = null;

		MyConcreteDerrivedType1 e0 = new MyConcreteDerrivedType1();
		e0.setMyBoolean(false);
		e0.setMyInt(Constants.THE_INT);
		e0.setMyString(Constants.THE_STRING);

		MyConcreteDerrivedType2 e1 = new MyConcreteDerrivedType2();
		e1.setMyDecimal(new BigDecimal("" + Constants.THE_INT));
		e1.setMyInt(Constants.THE_INT);
		e1.setMyLong(123456789);
		e1.setMyString(Constants.THE_STRING);

		expected.add(e0);
		expected.add(e1);

		actual = port.typesAbstractPolymorphicArray(expected);

		assertTrue("Array size is not 2", actual.size() == actual.size());
		assertTrue("Element 0 is not MyConcreteDerrivedType1",
				actual.get(0) instanceof MyConcreteDerrivedType1);
		assertTrue("Element 1 is not MyConcreteDerrivedType2",
				actual.get(1) instanceof MyConcreteDerrivedType2);

		assertTrue("", compareBean(actual.get(0), e0,
				MyConcreteDerrivedType1.class));
		assertTrue("", compareBean(actual.get(1), e1,
				MyConcreteDerrivedType2.class));
	}

	/**
	 * @testStrategy Test for transmitting a .NET-style wrapped array
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a .NET-style wrapped array",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_array_wrapped() {
		WsdlTypesPort port = getPort();

		ArrayOfString expected = new ArrayOfString();
		ArrayOfString actual = null;

		expected.getArray().add(ELEM0);
		expected.getArray().add(ELEM1);
		expected.getArray().add(ELEM2);

		actual = port.typesWrappedArray(expected);

		assertNotNull("null actual",actual);		
		assertNotNull("null array",actual.getArray());
		assertTrue("wrong size", expected.getArray().size() == actual.getArray().size());
	}

	/**
	 * @testStrategy Test for using simpleType xsd:list
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for using simpleType xsd:list",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_list_simpleType() {
		WsdlTypesPort port = getPort();

		List<Long> expected = new LinkedList<Long>();
		List<Long> actual = null;

		expected.add(Long.valueOf(-998765432));		
		expected.add(Long.valueOf(-11));
		expected.add(Long.valueOf(0));
		expected.add(Long.valueOf(43));
		expected.add(Long.valueOf(1000000123));

		actual = port.typesLongList(expected);

		assertEquals("array sizes do not match", expected.size(), actual.size());

		for (int i=0; i < expected.size(); i++){
			assertEquals("expected != actual for row" + i, expected.get(i), actual.get(i));
		}
	}
	
	/**
	 * @testStrategy Test for using xsd:base64Binary
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for using xsd:base64Binary",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_base64Binary() {
		WsdlTypesPort port = getPort();

		byte[] expected = "a quick brown fox jumped over the lazy brown dog".getBytes();
		byte[] actual = null;

		for (int i=0; i < 200; i ++)
		actual = port.typesBase64Binary(expected);

		assertEquals("array sizes do not match", expected.length, actual.length);

		for (int i=0; i < expected.length; i++){
			assertEquals("expected != actual for row" + i, expected[i], actual[i]);
		}
	}		
	
	/**
	 * @testStrategy Test for using xsd:hexBinary
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for using xsd:hexBinary",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_hexBinary() {
		WsdlTypesPort port = getPort();

		
		byte[] expected = "a quick brown fox jumped over the lazy brown dog".getBytes();
		byte[] actual = null;

		actual = port.typesHexBinary(expected);

		assertEquals("array sizes do not match", expected.length, actual.length);

		for (int i=0; i < expected.length; i++){
			assertEquals("expected != actual for row" + i, expected[i], actual[i]);
		}
	}	
	
	/**
	 * @testStrategy Test for transmitting a graph with loops. The loop is
	 *               between 2 adjacent elements in a binary tree. Expecting
	 *               this test to fail and looking for a graceful detection
	 *               (other than StackoverflowException)
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a graph with loops. The loop is between 2 adjacent elements in a binary tree. Expecting this test to fail and looking for a graceful detection (other than StackoverflowException)",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_graph_simple() {
		WsdlTypesPort port = getPort();

		BinaryTreeElement root = new BinaryTreeElement();
		BinaryTreeElement right = new BinaryTreeElement();
		BinaryTreeElement left = new BinaryTreeElement();

		BinaryTreeElement actual = new BinaryTreeElement();

		// root points to the right
		root.setRight(right);
		root.setLeft(left);

		// right loops back to the root
		right.setRight(root);
		right.setLeft(null);

		// left doesn't go anywhere
		left.setLeft(null);
		left.setRight(null);

		try {
			// expecting a StackOverflowException
			actual = port.typesGraph(root);
		} catch (java.lang.StackOverflowError soe) {
			System.out.println(getName() + ": " + soe);
			//soe.printStackTrace(System.out);

			fail("received a StackOverflowError while transmitting a graph. Expecting a WSE");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			//wse.printStackTrace(System.out);

		}
	}

	/**
	 * @testStrategy Test for transmitting a graph with loops. The loop is
	 *               between several non-adjacent elements in a binary tree.
	 *               Expecting this test to fail and looking for a graceful
	 *               detection (other than StackoverflowException)
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a graph with loops. The loop is between several non-adjacent elements in a binary tree. Expecting this test to fail and looking for a graceful detection (other than StackoverflowException)",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_graph_complex() {
		WsdlTypesPort port = getPort();

		/**
		 * Create the following binary tree structure
		 * 
		 * root / \ /-> r1 /-> l1 | / \ | / \ | r2 n| n l2 | n \--/ / \
		 * \-----------/ l3 n n
		 * 
		 */

		BinaryTreeElement root = new BinaryTreeElement();
		BinaryTreeElement r1 = new BinaryTreeElement();
		BinaryTreeElement r2 = new BinaryTreeElement();
		BinaryTreeElement l1 = new BinaryTreeElement();
		BinaryTreeElement l2 = new BinaryTreeElement();
		BinaryTreeElement l3 = new BinaryTreeElement();

		// root points to the right
		root.setRight(r1);
		root.setLeft(l1);

		// right loops back to the left
		r1.setRight(null);
		r1.setLeft(r2);

		r2.setRight(l1);
		r2.setLeft(null);

		// set 2 elements below
		l1.setRight(l2);
		l1.setLeft(null);

		// l2 has a leaf and loops back to r1
		l2.setLeft(l3);
		l2.setRight(r1);

		// l3 is a leaf
		l3.setLeft(null);
		l3.setRight(null);

		try {
			port.typesGraph(root);
		} catch (java.lang.StackOverflowError soe) {
			System.out.println(getName() + ": " + soe);
			soe.printStackTrace(System.out);

			fail("received a StackOverflowError while transmitting a graph. Expecting a WSE");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
			// wse.printStackTrace(System.out);
		}
	}

	/**
	 * @testStrategy Test for transmitting a complexType that is the type of an
	 *               element. E.g. element type="type" as opposed to being an
	 *               anonymous type
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a complexType that is the type of an element. E.g. element type=\"type\" as opposed to being an anonymous type",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_complexType() {
		// ObjectFactory of = new ObjectFactory();
		WsdlTypesPort port = getPort();

		final String expected1 = "TheString";
		final long expected2 = 2006;
		String actual1 = null;
		long actual2 = 0;

		// may throw a ClassCastException
		NamedComplexType1 ret = port.typesElementIsNamedComplexType(expected1,
				expected2);

		actual1 = ret.getXsdString();
		actual2 = ret.getXsdLong();

		assertEquals("Returned types do not equal", expected1, actual1);
		assertEquals("Returned types do not equal", expected2, actual2);
	}

	/**
	 * @testStrategy Test for transmitting a complexType that extends another
	 *               complexType. we will send the base type and request that it
	 *               is roundtripped
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a complexType that extends another complexType. we will send the base type and request that it is roundtripped",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_extension_client1() {
		// ObjectFactory of = new ObjectFactory();
		WsdlTypesPort port = getPort();

		BasePolyType actual = null;		
		BasePolyType expected = new BasePolyType();
		expected.setAmount(10);
		Holder<String> type = new Holder<String>();
		Holder<BasePolyType> response = new Holder<BasePolyType>();

		// may throw a ClassCastException
		port.typesExtension(expected, TypesMode.ROUNDTRIP, response, type);
		actual = response.value;
		
		System.out.println("sent: " + expected.getClass().getName());
		System.out.println("actual: " + actual.getClass().getName());

		assertTrue("Returned type is not BasePolyType",
				actual instanceof BasePolyType);
	}
	
	/**
	 * @testStrategy Test for transmitting a complexType that extends another
	 *               complexType. we will send the derrived type and request that it
	 *               is roundtripped
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a complexType that extends another complexType. we will send the derrived type and request that it is roundtripped",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_extension_client2() {
		// ObjectFactory of = new ObjectFactory();
		WsdlTypesPort port = getPort();

		BasePolyType actual = null;		
		PositivePolyType expected = new PositivePolyType();
		expected.setAmount(10);
		Holder<String> type = new Holder<String>();
		Holder<BasePolyType> response = new Holder<BasePolyType>();

		// may throw a ClassCastException
		port.typesExtension(expected, TypesMode.ROUNDTRIP, response, type);
		actual = response.value;
		
		System.out.println("sent: " + expected.getClass().getName());
		System.out.println("actual: " + actual.getClass().getName());

		assertTrue("Returned type is not PositivePolyType",
				actual instanceof PositivePolyType);	
	}
	
	/**
	 * @testStrategy Test for transmitting a complexType that extends another
	 *               complexType. we will send the derrived type that is in a different
	 *               package and request that it is roundtripped
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a complexType that extends another complexType. we will send the derrived type that is in a different package and request that it is roundtripped",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_extension_client3() {
		// ObjectFactory of = new ObjectFactory();
		WsdlTypesPort port = getPort();

		BasePolyType actual = null;		
		LargePositivePolyType expected = new LargePositivePolyType();
		expected.setAmount(1000000);
		Holder<String> type = new Holder<String>();
		Holder<BasePolyType> response = new Holder<BasePolyType>();

		// may throw a ClassCastException
		port.typesExtension(expected, TypesMode.ROUNDTRIP, response, type);
		actual = response.value;
		
		System.out.println("sent: " + expected.getClass().getName());
		System.out.println("actual: " + actual.getClass().getName());

		assertTrue("Returned type is not LargePositivePolyType",
				actual instanceof LargePositivePolyType);	
	}
	
	/**
	 * @testStrategy Test for transmitting a complexType that extends another
	 *               complexType. we will send the derrived type that is in a different
	 *               package and request that it is roundtripped
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a complexType that extends another complexType. we will send the derrived type that is in a different package and request that it is roundtripped",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_extension_client4() {
		// ObjectFactory of = new ObjectFactory();
		WsdlTypesPort port = getPort();

		BasePolyType actual = null;		
		LargeNegativePolyType expected = new LargeNegativePolyType();
		expected.setAmount(-1000000);
		Holder<String> type = new Holder<String>();
		Holder<BasePolyType> response = new Holder<BasePolyType>();

		// may throw a ClassCastException
		port.typesExtension(expected, TypesMode.ROUNDTRIP, response, type);
		actual = response.value;
		
		System.out.println("sent: " + expected.getClass().getName());
		System.out.println("actual: " + actual.getClass().getName());

		assertTrue("Returned type is not LargeNegativePolyType",
				actual instanceof LargeNegativePolyType);	
	}	
	
	/**
	 * @testStrategy Test for transmitting a complexType that extends another
	 *               complexType. we will send the base type and request that it
	 *               is roundtripped
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a complexType that extends another complexType. we will send the base type and request that it is roundtripped",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_extension_server1() {
		// ObjectFactory of = new ObjectFactory();
		WsdlTypesPort port = getPort();

		BasePolyType actual = null;		
		BasePolyType expected = new BasePolyType();
		expected.setAmount(10);
		Holder<String> type = new Holder<String>();
		Holder<BasePolyType> response = new Holder<BasePolyType>();

		// may throw a ClassCastException
		port.typesExtension(expected, TypesMode.BASE, response, type);
		actual = response.value;
		
		System.out.println("sent: " + expected.getClass().getName());
		System.out.println("actual: " + actual.getClass().getName());

		assertTrue("Returned type is not BasePolyType",
				actual instanceof BasePolyType);
	}
	
	/**
	 * @testStrategy Test for transmitting a complexType that extends another
	 *               complexType. we will send the derrived type and request that it
	 *               is roundtripped
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a complexType that extends another complexType. we will send the derrived type and request that it is roundtripped",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_extension_server2() {
		// ObjectFactory of = new ObjectFactory();
		WsdlTypesPort port = getPort();

		BasePolyType actual = null;		
		BasePolyType expected = new BasePolyType();
		expected.setAmount(10);
		Holder<String> type = new Holder<String>();
		Holder<BasePolyType> response = new Holder<BasePolyType>();

		// may throw a ClassCastException
		port.typesExtension(expected, TypesMode.DERRIVED_SAME_PACKAGE, response, type);
		actual = response.value;
		
		//System.out.println("sent: " + expected.getClass().getDeclaringClass().getName());
		//System.out.println("actual: " + actual.getClass().getDeclaringClass().getName());

		assertTrue("Returned type is not PositivePolyType",
				actual instanceof PositivePolyType);	
	}
	
	/**
	 * @testStrategy Test for transmitting a complexType that extends another
	 *               complexType. we will send the derrived type that is in a different
	 *               package and request that it is roundtripped
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a complexType that extends another complexType. we will send the derrived type that is in a different package and request that it is roundtripped",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_extension_server3() {
		// ObjectFactory of = new ObjectFactory();
		WsdlTypesPort port = getPort();

		BasePolyType actual = null;		
		BasePolyType expected = new BasePolyType();
		expected.setAmount(1000000);
		Holder<String> type = new Holder<String>();
		Holder<BasePolyType> response = new Holder<BasePolyType>();

		// may throw a ClassCastException
		port.typesExtension(expected, TypesMode.DERRIVED_DIFF_PACKAGE, response, type);
		actual = response.value;
		
		System.out.println("sent: " + expected.getClass().getName());
		System.out.println("actual: " + actual.getClass().getName());

		assertTrue("Returned type is not LargePositivePolyType",
				actual instanceof LargePositivePolyType);	
	}
	
	/**
	 * @testStrategy Test for transmitting a complexType that extends another
	 *               complexType. we will send the derrived type that is in a different
	 *               package and request that it is roundtripped
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Test for transmitting a complexType that extends another complexType. we will send the derrived type that is in a different package and request that it is roundtripped",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypes_xsd_extension_server4() {
		// ObjectFactory of = new ObjectFactory();
		WsdlTypesPort port = getPort();

		BasePolyType actual = null;		
		BasePolyType expected = new BasePolyType();
		expected.setAmount(-1000000);
		Holder<String> type = new Holder<String>();
		Holder<BasePolyType> response = new Holder<BasePolyType>();

		// may throw a ClassCastException
		port.typesExtension(expected, TypesMode.DERRIVED_DIFF_PACKAGE, response, type);
		actual = response.value;
		
		System.out.println("sent: " + expected.getClass().getName());
		System.out.println("actual: " + actual.getClass().getName());

		assertTrue("Returned type is not LargeNegativePolyType",
				actual instanceof LargeNegativePolyType);	
	}
    
    /**
     * call each test method multiple times to see if we can find any
     * caching problems in jaxb fastpath. 
     */
    public void testCaching()throws Exception {
        int i = 0;
      
        testTypes_xsd_extension_server1(); printcount(++i);
        testTypes_xsd_extension_server2(); printcount(++i);
        testTypes_xsd_extension_server3(); printcount(++i);
        testTypes_xsd_extension_server4(); printcount(++i);
        //testTypes_xsd_gDate(); printcount(++i);
        testTypes_xsd_hexBinary(); printcount(++i);
        testTypes_xsd_int(); printcount(++i);
        testTypes_xsd_integer(); printcount(++i);
        testTypes_xsd_list_byte(); printcount(++i);
        testTypes_xsd_list_decimal(); printcount(++i);
        testTypes_xsd_list_simpleType(); printcount(++i);
        testTypes_xsd_list_string(); printcount(++i);
        testTypes_xsd_QName(); printcount(++i);
        testTypes_xsd_restriction_enumeration(); printcount(++i);
        testTypes_xsd_restriction_minMax(); printcount(++i);
        testTypes_xsd_restriction_pattern(); printcount(++i);
        testTypes_xsd_unsignedShort(); printcount(++i);
        testTypes_array(); printcount(++i);
        testTypes_array2D(); printcount(++i);
        testTypes_array_abstractPolymorphic(); printcount(++i);
        testTypes_array_java(); printcount(++i);
        testTypes_array_polymorphic(); printcount(++i);
        testTypes_array_withNulls_nilableFalse(); printcount(++i);
        testTypes_array_withNulls_nilableTrue(); printcount(++i);
        testTypes_array_wrapped(); printcount(++i);
        testTypes_elementRef(); printcount(++i);
        testTypes_graph_complex(); printcount(++i);
        testTypes_graph_simple(); printcount(++i);
        testTypes_xsd_anyType_array(); printcount(++i);
        testTypes_xsd_anyType_jaxbComplex(); printcount(++i);
        testTypes_xsd_anyType_null(); printcount(++i);
        testTypes_xsd_anyType_primitive(); printcount(++i);
        testTypes_xsd_attribute(); printcount(++i);
        testTypes_xsd_base64Binary(); printcount(++i);
        testTypes_xsd_base64Binary(); printcount(++i);
        testTypes_xsd_complexType(); printcount(++i);
        testTypes_xsd_date(); printcount(++i);
        testTypes_xsd_dateTime(); printcount(++i);
        testTypes_xsd_extension_client1(); printcount(++i);
        testTypes_xsd_extension_client2(); printcount(++i);
        testTypes_xsd_extension_client3(); printcount(++i);
        testTypes_xsd_extension_client4(); printcount(++i);
    }    
    private void printcount(int i){
        System.out.println("------ "+ i);
    }

	/**
	 * Auxiliary method used for comparing beans. Cannot be used for testing
	 * nesting of beans
	 */
	private boolean compareBean(Object o1, Object o2, Class type) {

		// same object
		if (o1 == o2) return true;

		// one is null the other isn't
		if (o1 == null || o2 == null) return false;

		// beans are not of the specified type
		if (o1.getClass() != type || o2.getClass() != type) return false;

		Method[] methods = type.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];

			// read all getter methods
			if (m.getName().startsWith("get")) {
				try {
					Object r1 = m.invoke(o1, new Object[] {});
					Object r2 = m.invoke(o2, new Object[] {});

					if (!((r1 != null && r1.equals(r2)) || (r1 == null && r2 == null))) { return false; }

				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		return true;
	}

	// static ProxyWsdlTypesService service = new ProxyWsdlTypesService();
	// static WsdlTypesPort port = service.getWsdlTypesPort();

	private WsdlTypesPort getPort() {
		ProxyWsdlTypesService service = new ProxyWsdlTypesService();
		WsdlTypesPort port = service.getWsdlTypesPort();
		assertNotNull("Port is null", port);

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				WSDL_TYPES_ENDPOINT);

		System.out.println("Endpoint: " + WSDL_TYPES_ENDPOINT);
		
		return port;
	}
	
}
