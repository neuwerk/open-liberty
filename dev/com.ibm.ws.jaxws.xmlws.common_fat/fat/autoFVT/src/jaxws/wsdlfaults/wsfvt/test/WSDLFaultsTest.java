//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId         Defect            Description
// ----------------------------------------------------------------------------
// 08/28/2006  mzheng         LIDB3296-46.01    New File
// 01/26/2007  mzheng         417209            Fixed testFaultBeanReturn()
//

package jaxws.wsdlfaults.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

import jaxws.wsdlfaults.wsfvt.client.*;


/**
 * This test suite test:
 * 
 * - wsdl:fault is mapped to Java exception 
 * 
 * - JAX-WS marshals and unmarshals service exceptions correctly
 * 
 * - Wrapper exception and its fault bean are mapped to SOAP fault
 *   message per JAX-WS spec
 * 
 * - The name conflict between fault bean and wrapper exception is 
 *   handled correctly
 *
 * - Equivalent wsdl faults are mapped to a single exception and are 
 *   unmarshaled correctly
 * 
 * - Fault bean can be returned via Holder<> class
 *
 * - Fault bean inheritance
 */

public class WSDLFaultsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    /*
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public WSDLFaultsTest(String name) {
        super(name);
    }


    /*
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
        return new TestSuite(WSDLFaultsTest.class);
    }


    /**
     * @testStrategy This test catches exception generated from a wsdl:fault 
     *               that refers to a wsdl:message and the message element 
     *               referred to is a primitive string type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test catches exception generated from a wsdl:fault  that refers to a wsdl:message and the message element referred to is a primitive string type.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultSimpleString() {
        System.err.println("====== In testFaultSimpleString() ====== \n");

        String symbol = new String("XYZ");
        float value = 0;

        try {
            FaultsServicePortType myProxy = (new FaultsService()).getFaultsPort();
            if (myProxy == null) {
                fail("Failed to create proxy");
                return;
            }

            value = myProxy.getQuote(symbol);

            fail("Should have caught an InvalidTickerFault"); 

        } catch (InvalidTickerFault e) {

            assertEquals("Incorrect InvalidTickerFault caught", symbol, e.getFaultInfo());

            assertEquals("Incorrect message for InvalidTickerFault", 
                         "Server throws InvalidTickerFault", 
                         e.getMessage());

            System.err.println("====== SUCCESS: testFaultSimpleString() ======\n");
        } catch (Exception e) {

            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());

        }
    }


    /**
     * @testStrategy This test catches exception generated from a wsdl:fault 
     *               that refers to a wsdl:message and the message element 
     *               referred to is a primitive int type.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test catches exception generated from a wsdl:fault  that refers to a wsdl:message and the message element referred to is a primitive int type.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultSimpleInt() {
        System.err.println("====== In testFaultSimpleInt() ======\n");

        String symbol = new String("ABC");
        float value = 0;

        try {
            FaultsServicePortType myProxy = (new FaultsService()).getFaultsPort();
            if (myProxy == null) {
                fail("Failed to create proxy");
                return;
            }

            value = myProxy.getQuote(symbol);
            fail("Should have caught a SimpleFault");

        } catch (SimpleFault e) {

            assertEquals("Incorrect SimpleFault caught", 100, e.getFaultInfo());

            assertEquals("Incorrect message for SimpleFault", 
                         "Server throws SimpleFault",
                         e.getMessage());

            System.err.println("====== SUCCESS: testFaultSimpleInt() ======\n");

        } catch (Exception e) {

            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());

        }
    }


    /**
     * @testStrategy This test catches exception generated from a wsdl:fault 
     *               that refers to a wsdl:message and the message element 
     *               referred to is a complexType.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test catches exception generated from a wsdl:fault  that refers to a wsdl:message and the message element referred to is a complexType.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultComplexType() {
        System.err.println("====== In testFaultComplexType() ======\n");

        String symbol = new String("xyz");
        float value = 0;

        try {
            FaultsServicePortType myProxy = (new FaultsService()).getFaultsPort();
            if (myProxy == null) {
                fail("Failed to create proxy");
                return;
            }

            value = myProxy.getQuote(symbol);
            fail("Should have caught a BaseFault_Exception");

        } catch(BaseFault_Exception e) {

            assertEquals("Incorrect BaseFault_Exception caught", 400, e.getFaultInfo().getA());

            assertEquals("Incorrect message for BaseFault_Exception", 
                         "Server throws BaseFault_Exception", 
                         e.getMessage());

            System.err.println("====== SUCCESS: testFaultComplexType() ======\n");

        } catch (Exception e) {

            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * @testStrategy This test catches and verifies wrapper exceptions for 
     *               derived fault beans.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test catches and verifies wrapper exceptions for  derived fault beans. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDerivedFaultBeans() {
        System.err.println("====== In testDerivedFaultBeans() ======\n");

        String symbol1 = new String("one");
        String symbol2 = new String("two");
        float value = 0;
        FaultsServicePortType myProxy = null;

        try {
            myProxy = (new FaultsService()).getFaultsPort();
       
            if (myProxy == null) {
                fail("Failed to create proxy");
                return;
            }
        } catch (Exception e) {

            e.printStackTrace();
            fail("Unexpected Exception caught when creating proxy: " + e.getClass().getName() + ": " + e.getMessage());
        }

        try {

            value = myProxy.getQuote(symbol1);
            fail("Should have caught a DerivedFault1_Exception");

        } catch(DerivedFault1_Exception e) {

            assertEquals("Incorrect DerivedFault1_Exception caught", 100, e.getFaultInfo().getA());

            assertEquals("Incorrect DerivedFault1_Exception caught", symbol1, e.getFaultInfo().getB());

            assertEquals("Incorrect message for DerivedFault1_Exception", 
                         "Server throws DerivedFault1_Exception", 
                         e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }

        try {

            value = myProxy.getQuote(symbol2);
            fail("Should have caught a DerivedFault2_Exception");

        } catch(DerivedFault2_Exception e) {

            assertEquals("Incorrect DerivedFault2_Exception caught", 200, e.getFaultInfo().getA());

            assertEquals("Incorrect DerivedFault2_Exception caught", symbol2, e.getFaultInfo().getB());

            assertEquals("Incorrect DerivedFault2_Exception caught", 80.0F, e.getFaultInfo().getC());

            assertEquals("Incorrect message for DerivedFault2_Exception", 
                         "Server throws DerivedFault2_Exception", 
                         e.getMessage());

            System.err.println("====== SUCCESS: testDerivedFaultBeans() ======\n");

        } catch (Exception e) {

            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * @testStrategy This test verifies that server can construct derived 
     *               fault bean with invoking parameters and throws wrapper 
     *               Exception.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that server can construct derived  fault bean with invoking parameters and throws wrapper Exception.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testComplexFaultBean() {
        System.err.println("====== In testComplexFaultBean ======\n");

        int a = 10;
        String b = "Complex";
        float c = 3.14F;
        int d = 5;
        float value = 0;

        try {
            FaultsServicePortType myProxy = (new FaultsService()).getFaultsPort();
            if (myProxy == null) {
                fail("Failed to create proxy");
                return;
            }

            value = myProxy.throwFault(a,b,c);
            fail("Should have caught a ComplexFault_Exception");

        } catch(ComplexFault_Exception e) {

            assertEquals("Incorrect ComplexFault_Exception", 
                         a, e.getFaultInfo().getA());

            assertEquals("Incorrect ComplexFault_Exception", 
                         b, e.getFaultInfo().getB());

            assertEquals("Incorrect ComplexFault_Exception", 
                         c, e.getFaultInfo().getC());

            assertEquals("Incorrect ComplexFault_Exception", 
                         d, e.getFaultInfo().getD());

            assertEquals("Incorrect message for ComplexFault_Exception",
                         "Server throws ComplexFault_Exception",
                         e.getMessage());

            System.err.println("====== SUCCESS: testComplexFaultBean() ======\n");

        } catch (Exception e) {

            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());

        }
    }


    /**
     * @testStrategy This test catches wrapper Exception for parent fault
     *               bean that's contructed from derived fault bean
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test catches wrapper Exception for parent fault bean that's contructed from derived fault bean",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testParentFaultBean() {
        System.err.println("====== In testParentFaultBean ======\n");

        int a = 7;
        String b = "Base";
        float c = 3.14F;
        float value = 0;

        try {
            FaultsServicePortType myProxy = (new FaultsService()).getFaultsPort();
            if (myProxy == null) {
                fail("Failed to create proxy");
                return;
            }

            value = myProxy.throwFault(a, b, c);
            fail("Should have caught a BaseFault_Exception"); 

        } catch(BaseFault_Exception e) {

            assertEquals("Incorrect BaseFault_Exception", 
                         a, e.getFaultInfo().getA());

            assertEquals("Incorrect message for BaseFault_Exception",
                         "Server throws BaseFault_Exception",
                         e.getMessage());

            System.err.println("====== SUCCESS: testParentFaultBean() ======\n");

        } catch (Exception e) {

            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());

        }
    }


    /**
     * @testStrategy This test verifies fault bean may be created and returned
     *               by server.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies fault bean may be created and returned by server.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testFaultBeanReturn() {
        System.err.println("====== In testFaultReturn ======\n");

        int a = 7;
        String b = "fault";
        float c = 3.14F;

        try {
            FaultsServicePortType myProxy = (new FaultsService()).getFaultsPort();
            if (myProxy == null) {
                fail("Failed to create proxy");
                return;
            }

            DerivedFault2 fault = new DerivedFault2();
            fault.setA(a);
            fault.setB(b);
            fault.setC(c);

            Holder<DerivedFault1> faultHolder = new Holder<DerivedFault1>(fault);

            myProxy.returnFault(a, b, c, faultHolder);

            assertEquals("Incorrect fault bean", a, faultHolder.value.getA());
            assertEquals("Incorrect fault bean", b, faultHolder.value.getB());

            b = "exception";
            fault.setB(b);
            faultHolder.value = fault;

            myProxy.returnFault(a, b, c, faultHolder);
            fail("Should have caught an EqualFault");

        } catch (EqualFault e) {

            assertEquals("Incorrect EqualFault caught", a, e.getFaultInfo().getA());

            assertEquals("Incorrect EqualFault caught", b, e.getFaultInfo().getB());

            assertEquals("Incorrect message for EqualFault", 
                         "Server throws EqualFault", 
                         e.getMessage());

            System.err.println("====== SUCCESS: testFaultBeanReturn() ======\n");

        } catch (DerivedFault1_Exception e) {

            assertEquals("Incorrect DerivedFault1_Exception caught", a, e.getFaultInfo().getA());

            assertEquals("Incorrect DerivedFault1_Exception caught", b, e.getFaultInfo().getB());

            assertEquals("Incorrect message for DerivedFault1_Exception", 
                         "Server throws EqualFault", 
                         e.getMessage());

            System.err.println("====== SUCCESS: testFaultBeanReturn() ======\n");

        } catch (Exception e) {

            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());

        }
    }


    /**
     * @testStrategy This test verifies that two wsdl:fault child elements 
     *               of same wsdl:operation that indirectly refer to the 
     *               same global element declaration are considered to be 
     *               equivalent.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies that two wsdl:fault child elements  of same wsdl:operation that indirectly refer to the same global element declaration are considered to be equivalent.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testEqualFaults() {
        System.err.println("====== In testEqualFaults ======\n");

        int a = 7;
        String b = "notfault";
        float c = 3.14F;

        try {
            FaultsServicePortType myProxy = (new FaultsService()).getFaultsPort();
            if (myProxy == null) {
                fail("Failed to create proxy");
                return;
            }

            DerivedFault2 fault = new DerivedFault2();
            fault.setA(a);
            fault.setB(b);
            fault.setC(c);

            Holder<DerivedFault1> faultHolder = new Holder<DerivedFault1>(fault);

            myProxy.returnFault(a, b, c, faultHolder);
            fail("Should have caught a DerivedFault1_Exception");

        } catch (EqualFault e) {

            /**
             * Server throws EqualFault that should be considered 
             * equivalent as DerivedFault1_Exception.
             * 
             * Client could catch EqualFault or DerivedFault1_Exception.
             */
            assertEquals("Incorrect EqualFault caught", a + 1, e.getFaultInfo().getA());

            assertEquals("Incorrect EqualFault caught", "Server: " + b, e.getFaultInfo().getB());

            assertEquals("Incorrect message for EqualFault", 
                         "Server throws EqualFault", 
                         e.getMessage());

            System.err.println("====== SUCCESS: testEqualFaults() ======\n");

        } catch (DerivedFault1_Exception e) {

            /**
             * Server throws EqualFault that should be considered 
             * equivalent as DerivedFault1_Exception.
             */
            assertEquals("Incorrect DerivedFault1_Exception caught", a + 1, e.getFaultInfo().getA());

            assertEquals("Incorrect DerivedFault1_Exception caught", "Server: " + b, e.getFaultInfo().getB());

            assertEquals("Incorrect message for DerivedFault1_Exception", 
                         "Server throws EqualFault", 
                         e.getMessage());

            System.err.println("====== SUCCESS: testEqualFaults() ======\n");

        } catch (Exception e) {

            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());

        }
    }
}
