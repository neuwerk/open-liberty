//
// @(#) 1.11 autoFVT/src/com/ibm/ws/wsfvt/test/framework/FVTTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/10/08 09:57:35 [8/8/12 06:56:43]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect           Description
// ----------------------------------------------------------------------------
// 05/18/07 sedov       440313           New File
// 07/24/07 jramos      453487           Make default constructor public
// 08/17/07 sedov       460302           Add warning() method
// 09/10/07 jramos      465935           Extend ACUTETestCase
// 09/21/07 sedov       466314           Updates to logXML
// 10/03/07 btiffany    471692           Allow construction outside of junit.
// 04/15/08 sedov       513031           Run suiteTeardown if suiteSetup fails

package com.ibm.ws.wsfvt.test.framework;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Protectable;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.LogNoExecution;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import java.lang.Thread;
import java.lang.InterruptedException;
import java.util.Date;

import com.ibm.websphere.simplicity.OperatingSystem;
import common.utils.topology.IMachine;
import common.utils.topology.TopologyActions;


/**
 * Common FVT test case that provides many customized & imporved methods
 * inclusing a suite-level setup and teardown
 */
public abstract class FVTTestCase extends TestCase {
    protected static Map<Class, TestCount> tests = new HashMap<Class, TestCount>();
    private static Map<Class, TestCondition> conditions = new HashMap<Class, TestCondition>();
    
	// skip all tests in suite
	private boolean skipTest = false;
	
	private Warning warning = null;
	
	public FVTTestCase(){
        this(null);
	}
	
	public FVTTestCase(String name){
		super(name);
        if (tests.containsKey(this.getClass())) {
            tests.get(this.getClass()).totalTestCase++;
        } else {
            tests.put(this.getClass(), new TestCount(1, 0));
        }
        if(!conditions.containsKey(this.getClass())) {
            try {
                conditions.put(this.getClass(), new TestCondition());
            } catch(Exception e) {
                e.printStackTrace();
                return;
            }
        }
        
        // if being constructed outside of junit, don't require formatter class
        try{
            
            // undo test count if this test or suite will be filtered
            if(FVTJUnitXMLFilteredFormatter.filterSuite(this.getClass())) {
                tests.remove(this.getClass());
            } else if(FVTJUnitXMLFilteredFormatter.filterTest(this)) {
                if(tests.containsKey(this.getClass())) {
                    tests.get(this.getClass()).totalTestCase--;
                }
            }
        } catch (java.lang.NoClassDefFoundError ncdf){
            System.out.println("test case constructed without formatter");
        }
	}
	
	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before the entire test suite is executed.
	 * 
	 * @param testSkipCondition -
	 *            conditions that will cause this test to be skipped (the test
	 *            will pass, but will not be executed). Examples: ws-security, zOS platform, etc
	 * @param testFailCondition -
	 *            conditions that will cause this test to be failed without
	 *            running. Examples: test app not installed
	 * @throws Exception
	 */
	protected void suiteSetup(ConfigRequirement testSkipCondition)
			throws Exception {
		if (TopologyDefaults.libServer != null) {
			TopologyDefaults.libServer.restartServer();
		}
		/*
		 * try { System.out.println(
		 * "Wait for 2 seconds to ensure server and applications restarted OK");
		 * Thread.sleep(2000); } catch (InterruptedException e) { // do nothing
		 * }
		 */
		// check if server started
		System.out
				.println("Start to check if server started: "
						+ new Date(System.currentTimeMillis()));
		System.out.println("Output="
				+ TopologyDefaults.libServer.waitForStringInLog("CWWKF0011I:"));
		System.out
				.println("End to check if server started: "
						+ new Date(System.currentTimeMillis()));
		// check if app started
		System.out
			.println("Start to check if the test app started: "
				+ new Date(System.currentTimeMillis()));
		System.out.println("Output="
				+ TopologyDefaults.libServer.waitForStringInLog("CWWKZ0001I:"));
		System.out
			.println("End to check if the test app started: "
				+ new Date(System.currentTimeMillis()));
		
		// what if there are multiple test apps to start??
		// so for safety reason, we have to wait another seconds here
		// of cause, it's ugly thing to do, but before we find a better solution
		// this is a quick way to let build break issue gone.
		try {
			System.out.println("Wait for 4 seconds to ensure all test apps restarted OK");
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// do nothing
		}
	}
	
	protected void restartLiberty() throws Exception {
		if (TopologyDefaults.libServer != null) {
			TopologyDefaults.libServer.restartServer();
		}
		return;
	}

	/**
	 * Tears down the fixture, for example, close a network connection. This
	 * method is called after the entire test suite is executed
	 */
	protected void suiteTeardown() throws Exception {
		if (TopologyDefaults.libServer != null) {
			TopologyDefaults.libServer.stopServer();
		}
	}

	/**
	 * Runs the bare test sequence.
	 * 
	 * @exception Throwable
	 *                if any exception is thrown
	 */
	public final void runBare() throws Throwable {
		Class cls = this.getClass();
        TestCondition condition = conditions.get(cls);
		
		if (!FVTJUnitXMLFilteredFormatter.filterSuite(cls)){
			
			// run suite initialization
			if (tests.containsKey(cls) && tests.get(cls).executedTestCases == 1) {
				System.out.println("suite.filter=" + FVTJUnitXMLFilteredFormatter.getFilter());
				System.out.println("runBare: " + this.getClass());
				
				System.out.println("=================== suiteSetup ===================");
				
				try {
					suiteSetup(condition.testSkipCondition);
				} catch (Throwable t){
					// if the setup failed, then count this test as execuetd anyway
					condition.setupFailed = true;
					t.printStackTrace(System.out);
					
					//-----begin 513031
					try {
						System.out.println("=================== suiteTeardown ===================");
						suiteTeardown();
					} catch (Throwable st){
						// ignore
						System.out.println("Error in suiteTeardown, it will be ignored: " + st);
					}
					//-----end 513031
					throw t;
				}
			}
			System.out.println("=================== " + getName() + " ===================");
	
			// setup failed
			if (condition.setupFailed){
				System.out.println("Test not executed, suiteSetup() failed");
				throw new Exception("Test not executed, suiteSetup() failed");
			}
			
			// determine if the test should be skipped
			if (tests.containsKey(cls)){
				skipTest = !condition.testSkipCondition.meetsRequirements();
			} else {
				skipTest = false;
			}
	
			Throwable th = null;
			if (!FVTJUnitXMLFilteredFormatter.filterTest(this)){		
				// run the test
				try {
                    // call test setup, if it isn't being skipped
                    if (!skipTest) {
                        setUp();
                    }
                    
					// run the test, capture any exceptions from either
					// the test or the tearDown...but do not throw it yet
					try {
						runTest();
					} finally {
						// call test tearDown
						if (!skipTest) {
							tearDown();
						}
					}
					System.out.println("Test Passed!");
				} catch (Throwable e) {
					th = e;
					System.out.println("Test Failed: " + th);
				}  finally {
                    // make sure status was logged to common criteria logger
                }
			} 
	
			// do a suite tearDown
			if (tests.containsKey(cls)
					&& tests.get(cls).executedTestCases == tests.get(cls).totalTestCase) {
				System.out.println("=================== suiteTeardown ===================");
				suiteTeardown();
			}
			
			// if we caught an exception now is the time to throw it
			if (th != null) throw th;
		}
	}
    
    /**
     * Runs the test case and collects the results in TestResult.
     */
    public void run(TestResult result) {
        Class cls = this.getClass();
        if (!FVTJUnitXMLFilteredFormatter.filterSuite(cls) && !FVTJUnitXMLFilteredFormatter.filterTest(this)) {
            tests.get(cls).executedTestCases++;
            try {
                result.startTest(this);
                Protectable p= new Protectable() {
                    public void protect() throws Throwable {
                        runBare();
                    }
                };
                result.runProtected(this, p);
        
                result.endTest(this);
            } finally {
//                logger.finer("TRACE: " + currentCCLogger);
//                logger.finer("TRACE: " + tests.get(cls));
//                System.out.println(tests.get(cls).executedTestCases);
//                System.out.println(tests.get(cls).totalTestCase);
                if(tests.get(cls).executedTestCases == tests.get(cls).totalTestCase) {
                    tests.remove(cls);
                }
            }
        }
    }
	
	/**
     * Override to run the test and assert its state.
     * 
     * @exception Throwable if any exception is thrown
     */
	protected final void runTest() throws Throwable {
		if (!skipTest) {
			super.runTest();
		} else {
			System.out.println("Test prereqs not satisfied, skipping.");
			
			if (tests.containsKey(this.getClass())
					&& tests.get(this.getClass()).executedTestCases == 0) {
				String clazz = this.getClass().getCanonicalName();
				LogNoExecution.logTestSuite(clazz, null, "Test skipped because prereqs not satisfied");
			}
            
			warning("This test was skipped because configuration prerequisite was not satisfied, see System.Out for more details.");
		}
	}
	
	/***************************************************
	 * Additional asserts and logs
	 ***************************************************/
	
	/**
	 * Print out a message
	 * @param e
	 */
	protected void logMessage(String message){
		System.out.println("log: " + message);
	}
	
	/**
	 * Print out the test description
	 * @param e
	 */
	protected void logDescription(String description){
		System.out.println("Description: " + description);
	}
	
	/**
	 * Print out the exception to SystemOut, this method logs the exception class, message
	 * stack-trace CausedBy's (but not stack frames)
	 * @param e
	 */
	protected void logException(Throwable e){
		logException(null, e);
	}
	
	/**
	 * Print out the exception to SystemOut, this method logs the exception class, message
	 * stack-trace CausedBy's (but not stack frames)
	 * @param description
	 * @param wse
	 */
	protected void logException(String description, Throwable e){
		
		if (e == null) return;
		
		if (description == null) description = "logException";

		// print the exception class name, message and CausedBy stack trace
		System.out.println(description + ": " + e.getClass().getCanonicalName());
		System.out.println("  Message: " + e.getMessage());
		if (e.getCause() != null){
			System.out.println("  CausedBy:");
			logExceptionCausedByChain("  -> ", e.getCause());
		}
		
		// if it is a SOAPFaultException then print the detail bean
		if (e instanceof javax.xml.ws.soap.SOAPFaultException){
			javax.xml.ws.soap.SOAPFaultException sfe = (javax.xml.ws.soap.SOAPFaultException) e;
			System.out.println("  SOAPFault as XML:");
			logXML("    ", sfe.getFault());
		}
		
		// detect a bean with faultInfo and print that (e.g., this is a wsdl:fault bean)
		Method m = null;
		try {
			if ((m = e.getClass().getMethod("getFaultInfo", (Class[])null)) != null){
				Object faultInfo = m.invoke(e, (Object[])null);
				logBean("  ", "faultInfo", faultInfo);
			}
		} catch (Exception nsme){/* ignored */}
		
	}
	
	
	/**
	 * Log entire XML tree underneath this node
	 * @param e
	 */
	protected void logXML(Node n){
		logXMLWithSpacer("", "", n);
	}
	
	/**
	 * Log entire XML tree underneath this node
	 * @param description
	 * @param e
	 */
	protected void logXML(String description, Node n){
		logXMLWithSpacer(description, "", n);
	}
	
	/**
	 * Utility method for logXML*. Determines if node has a text subnode
	 * @param n
	 * @return
	 */
	private boolean isXMLTextNode(Node n){
		
		NodeList nl = n.getChildNodes();
		if (nl != null && nl.getLength() == 1 && nl.item(0).getNodeType() == Node.TEXT_NODE){
			return true;
		} else {
			return false;
		}
	}
	
	private void logXMLWithSpacer(String description, String spacer, Node n){
		if (n == null) return;
		
		if (description == null) description = "";
		
		if (isXMLTextNode(n)){
			System.out.println(description + spacer + n.getNodeName() + "=" + n.getTextContent());
		} else if (n.getNodeType() == 3){
			// do nothing...this is a #text node
		} else {
			System.out.println(description + spacer + n.getNodeName());
			
			NodeList nl = n.getChildNodes();
			for (int i=0; nl != null && i < nl.getLength(); i++){
				logXMLWithSpacer("", spacer + "  ", nl.item(i));
			}
		}
	}
	
	/**
	 * Log just the exception names in the stack trace. useful for exception checking tests
	 * @param t
	 */
	private void logExceptionCausedByChain(String header, Throwable t){
		Throwable cur = t;
		do {
			System.out.println(header + "Class: " + cur.getClass().getName() + ", Message: " + cur.getMessage());
			
			// detect a bean with faultInfo and print that (e.g., this is a wsdl:fault bean)
			Method m = null;
			try {
				if ((m = t.getClass().getMethod("getFaultInfo", (Class[])null)) != null){
					Object faultInfo = m.invoke(t, (Object[])null);
					logBean(header + "  ", "faultInfo", faultInfo);
				}
			} catch (Exception nsme){/* ignored */}
			
			cur = cur.getCause();
		} while (cur != null);
	}
	
	/**
	 * Log a bean and some of its publically available properties
	 * @param bean
	 */
	protected void logBean(Object bean){
		logBean(null, bean);
	}
	
	
	/**
	 * Log a bean and some of its publically available properties
	 * @param bean
	 */
	protected void logBean(String description, Object bean){
		logBean("", null, bean);
	}
	
	/**
	 * Log a bean and some of its publically available properties
	 * @param bean
	 */
	private void logBean(String prefix, String description, Object bean){
		if (bean == null) return;
		if (prefix == null) prefix = "";
		
		String leadin = null;
		if (description == null){
			leadin = "logBean";
		} else {
			leadin = description;
		}
		System.out.println(prefix + leadin + ": " + bean.getClass().getCanonicalName());
		
		if (bean.getClass().getCanonicalName().startsWith("java")){
			System.out.println(prefix + "  value = " + bean.toString());
		} else {
			// iterate over all methods
			for (Method m: bean.getClass().getMethods()){
				Class[] params = m.getParameterTypes();
				
				// for any method that does not have any paramters (like a getter)
				// that is not derrived from Object (such as equals, getClass, etc)
				if ((params == null || params.length == 0) &&
						(m.getName().startsWith("is") || m.getName().startsWith("get"))){
					try {
						System.out.println(prefix + "  " + m.getName() + " = " + m.invoke(bean, new Object[]{}));
					} catch (Exception e) {}
				}
			}
		}
	}
	
	
	/**********************************
	 * Asserts
	 **********************************/

	/**
	 * Verify that stacktrace "check" has a causeBy "lookFor" somewhere in the causedBy chain.
	 * E.g.,
	 * 	assertsTrue for assertStack(e, a) and assertStack(e, b), but not assertStack(e, c)
	 * 	When Exception e, is caused by a, caused by b
	 * @param check
	 * @param lookFor
	 */	
	protected void assertStack(Throwable check, Class<? extends Exception> lookFor){
		assertStack(null, check, lookFor);
	}
	
	/**
	 * Verify that stacktrace "check" has a causeBy "lookFor" somewhere in the causedBy chain.
	 * E.g.,
	 * 	assertsTrue for assertStack(e, a) and assertStack(e, b), but not assertStack(e, c)
	 * 	When Exception e, is caused by a, caused by b
	 * @param description
	 * @param check
	 * @param lookFor
	 */
	protected void assertStack(String description, Throwable check, Class<? extends Exception> lookFor){
		System.out.println("assertStack: looking for " + lookFor + " in stack trace causedBy");
		logException(check);
		
		Throwable cur = check;
		
		if (description == null) description = "Stack trace does not contain expected causedBy";
		
		boolean found = false;
		do {
			found = cur.getClass().isAssignableFrom(lookFor);
			cur = cur.getCause();
		} while (!found && cur != null);
		
		assertTrue(description + " [Expected: " + lookFor.getName() + " not found]", found);
	}
	
	protected void assertInstanceOf(String description, Object obj, Class t){
		System.out.println("assertInstanceOf: " + obj + " instanceof " + t);
		assertTrue(description, obj.getClass().isAssignableFrom(t));
	}
	
	
	/**
	 * Fail a test due to exception
	 * @param e
	 */
	protected void fail(Throwable e){
		logException(e);
		fail("Unexpected Exception " + e.getClass().getName() + ": " + e.getMessage());
	}
	
	/**
	 * Verify that "check" has a substring "checkFor", or fail the test case
	 * @param message
	 * @param check
	 * @param checkFor
	 */
	protected void assertSubstring(String message, String check, String checkFor){
		System.out.println("assertSubstring: check '" + check + "' for '" + checkFor + "'");
		
		if (message == null) message = "assertSubstring";
		if (check == null || checkFor == null) return;
		
		assertTrue(message + "[expecting: " + checkFor + ", in: " + check + "]", check.indexOf(checkFor) != -1);
	}
	
	/**
	 * Private structure used to count all test cases on a per-suite basis
	 */
	private static class TestCondition {
        public boolean setupFailed = false;
        public ConfigRequirement testSkipCondition = new ConfigRequirement();
		public TestCondition() throws Exception {
		}
	}

	/**
	 * Method for JUnitXMLFormatter to obtain the warning, if any
	 * @return
	 */
	public Warning getWarning() {
		return warning;
	}
	
	/**
	 * Set the tests's state to warning
	 * @param message
	 */
	public void warning(String message){
		System.out.println("Warning: " + message);
		
		this.warning = new Warning(message);
	}
    
    /**
     * Private structure used to count all test cases on a per-suite basis
     */
    protected static class TestCount {
        public TestCount(int totalTestCase, int executedTestCases) {
            this.totalTestCase = totalTestCase;
            this.executedTestCases = executedTestCases;
        }

        public int totalTestCase = 0;
        public int executedTestCases = 0;
    }
    
    public boolean isZOS(){
    	IMachine machine = TopologyActions.FVT_MACHINE;
    	System.out.println("FVTTestCase detected OS=" + machine.getOperatingSystem());
    	return machine.getOperatingSystem() == OperatingSystem.ZOS;
		//return machine.getOperatingSystem() == OperatingSystem.WINDOWS;
    }
}

