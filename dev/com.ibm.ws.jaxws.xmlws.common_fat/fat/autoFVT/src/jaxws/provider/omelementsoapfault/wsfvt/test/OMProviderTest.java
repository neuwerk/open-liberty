//
// @(#) 1.1 autoFVT/src/jaxws/provider/omelementsoapfault/wsfvt/test/OMProviderTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/13/10 15:20:42 [8/8/12 06:58:52]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date         UserId      Defect          Description
// ----------------------------------------------------------------------------
// 08/10/2010  sudiptam     625992.FVT      New file 
//
package jaxws.provider.omelementsoapfault.wsfvt.test;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.soap.SOAPBinding;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;

import com.ibm.websphere.simplicity.PortType;
import com.ibm.websphere.simplicity.Server;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;
import com.ibm.ws.wsfvt.test.framework.FvtTest;


/**
 * This test class checks whether a SOAPFault is returned to the client in case an exception occurs 
 * on the Provider side. Both the Dispatch and Provider in this case are of type OMElement.
 *
 */
public class OMProviderTest extends FVTTestCase {

    private String endpointUrl_1;
    private String endpointUrl_2;

    /**
     * Constructor to create a test case with a given name.
     * 
     * @param name
     *            The name of the test case
     */
    public OMProviderTest(String name) {
        super(name);
    }

    /**
     * The main method.
     * 
     * @param args
     *            The command line arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(OMProviderTest.class);
    }

    protected void suiteSetup(ConfigRequirement testSkipCondition) throws Exception {
        super.suiteSetup(testSkipCondition);
    }

    protected void suiteTeardown() throws Exception {
        super.suiteTeardown();
    }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        Server server = TopologyDefaults.getDefaultAppServer();
        String hostname = server.getNode().getHostname();
        Integer port = server.getPortNumber(PortType.WC_defaulthost);
              
        endpointUrl_1 = "http://" + hostname + ":" + port
        + "/omelementpayloadprovider/services/OMElementPayloadProviderService";
        
        endpointUrl_2 = "http://" + hostname + ":" + port
        + "/omelementmessageprovider/services/OMElementMessageProviderService";
    }


    /**
     * @testStrategy This method calls a Payload Provider. A message is sent such that no exception is thrown.
     * 				 The expected response is a message in Payload format.
     * @throws Exception
     */
    @FvtTest(description="This test will call a provider in payload mode.",
    	    expectedResult="message in Payload format",
    	    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testOMElement_Playload() throws Exception{
    	String messageToSend = "hello";
    	OMElement response = invokeService(Service.Mode.PAYLOAD, messageToSend, SOAPBinding.SOAP12HTTP_BINDING);
    	assertNotNull(response);
    	String responseText = response.toStringWithConsume();
    	System.out.println("response:==>>> "+ responseText);
    	assertTrue("test", responseText.contains("Echo"));
    }
    
    
    /**
     * @testStrategy This method calls a Message Provider. A message is sent such that no exception is thrown.
     * 				 The expected response is a message in SOAP format.
     * @throws Exception
     */
    @FvtTest(description="This test will call a provider in message mode.",
    	    expectedResult="message in Payload format",
    	    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testOMElement_Message() throws Exception{
    	String messageToSend = "hello";
    	OMElement response = invokeService(Service.Mode.MESSAGE, messageToSend, SOAPBinding.SOAP11HTTP_BINDING);
    	assertNotNull(response);
    	String responseText = response.toStringWithConsume();
    	System.out.println("response:==>>> "+ responseText);
    	assertTrue("test", responseText.contains("Echo"));
    }
    
    
    /**
     * @testStrategy This method calls a Payload Provider with SOAP 1.2. A message is sent such that an exception is thrown.
     * 				 The exception must be of type SOAPFaultException.
     */
    @FvtTest(description="This test will call a provider in payload mode with SOAP 1.2.",
    	    expectedResult="SOAPFaultException",
    	    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testOMElement_Playload_WithException_SOAP12(){
    	String messageToSend = "generate fault";
    	String exception = "";
    	OMElement response = null;
    	try{
    		response = invokeService(Service.Mode.PAYLOAD, messageToSend, SOAPBinding.SOAP12HTTP_BINDING);
    	}
    	catch(Exception ex){
    		String type = ex.getClass().toString();
    		System.out.println("The exception Class is: "+ type);
    		
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		ex.printStackTrace(pw);
    		
    		exception = sw.toString();
    	}
    	
    	System.out.println("Exception:==>>> "+ exception);
    	assertTrue("test", exception.contains("SOAPFaultException"));
    }
    
    
    /**
     * @testStrategy This method calls a Payload Provider with SOAP 1.1. A message is sent such that an exception is thrown.
     * 				 The exception must be of type SOAPFaultException.
     */
    @FvtTest(description="This test will call a provider in payload mode with SOAP 1.1.",
    	    expectedResult="SOAPFaultException",
    	    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testOMElement_Playload_WithException_SOAP11(){
    	String messageToSend = "generate fault";
    	String exception = "";
    	OMElement response = null;
    	try{
    		response = invokeService(Service.Mode.PAYLOAD, messageToSend, SOAPBinding.SOAP11HTTP_BINDING);
    	}
    	catch(Exception ex){
    		String type = ex.getClass().toString();
    		System.out.println("The exception Class is: "+ type);
    		
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		ex.printStackTrace(pw);
    		
    		exception = sw.toString();
    	}
    	
    	System.out.println("Exception:==>>> "+ exception);
    	assertTrue("test", exception.contains("SOAPFaultException"));
    }
    
    
    
    /**
     * @testStrategy This method calls a Message Provider with SOAP 1.2. A message is sent such that an exception is thrown.
     * 				 The exception must be of type SOAPFaultException.
     */
    @FvtTest(description="This test will call a provider in Message mode with SOAP 1.2.",
    	    expectedResult="SOAPFaultException",
    	    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testOMElement_Message_WithException_SOAP12(){
    	String messageToSend = "generate fault";
    	String exception = "";
    	OMElement response = null;
    	try{
    		response = invokeService(Service.Mode.MESSAGE, messageToSend, SOAPBinding.SOAP12HTTP_BINDING);
    	}
    	catch(Exception ex){
    		String type = ex.getClass().toString();
    		System.out.println("The exception Class is: "+ type);
    		
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		ex.printStackTrace(pw);
    		
    		exception = sw.toString();
    	}
    	
    	System.out.println("Exception:==>>> "+ exception);
    	assertTrue("test", exception.contains("SOAPFaultException"));
    }
    
    
    
    /**
     * @testStrategy This method calls a Message Provider with SOAP 1.1. A message is sent such that an exception is thrown.
     * 				 The exception must be of type SOAPFaultException.
     */
    @FvtTest(description="This test will call a provider in Message mode with SOAP 1.1.",
    	    expectedResult="SOAPFaultException",
    	    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testOMElement_Message_WithException_SOAP11(){
    	String messageToSend = "generate fault";
    	String exception = "";
    	OMElement response = null;
    	try{
    		response = invokeService(Service.Mode.MESSAGE, messageToSend, SOAPBinding.SOAP11HTTP_BINDING);
    	}
    	catch(Exception ex){
    		String type = ex.getClass().toString();
    		System.out.println("The exception Class is: "+ type);
    		
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		ex.printStackTrace(pw);
    		
    		exception = sw.toString();
    	}
    	
    	System.out.println("Exception:==>>> "+ exception);
    	assertTrue("test", exception.contains("SOAPFaultException"));
    }
    
    
    /**
     * This method is used for invoking  services.
     * @param serviceMode
     * @param messageToSend
     * @param soapBinding
     * @return
     */
    public OMElement invokeService(Mode serviceMode, String messageToSend, String soapBinding){
    	
    	OMElement message;
    	
    	//construct the message using OM API
    	OMFactory factory = OMAbstractFactory.getOMFactory();
    	OMNamespace omNS = factory.createOMNamespace("http://server.wsfvt.omelementsoapfault.provider.jaxws", "omReq");
    	message = factory.createOMElement("input", omNS);
    	message.setText(messageToSend);
    	
    	if(serviceMode == Service.Mode.MESSAGE ){
    		
    		SOAPFactory soapFactory;
    		if(soapBinding.equals(SOAPBinding.SOAP12HTTP_BINDING)){
    			soapFactory = OMAbstractFactory.getSOAP12Factory();
    		}else
    			soapFactory = OMAbstractFactory.getSOAP11Factory();
    		
        	SOAPEnvelope env = soapFactory.getDefaultEnvelope();
        	SOAPBody body = env.getBody();
        	body.addChild(message);
        	message=env;
    	}
    	
    	try {
			System.out.println("Message to send:==>>> "+ message.toStringWithConsume());
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
    	    	
    	//create a Dispatch in payload or message mode with OM message
    	System.out.println("Creating service...");
    	QName serviceName;
    	QName port;
    	Service service;
    	if(serviceMode == Service.Mode.PAYLOAD){
    		
    		serviceName = new QName("http://server.wsfvt.omelementsoapfault.provider.jaxws", "OMElementPayloadProviderService");
        	port = new QName("http://server.wsfvt.omelementsoapfault.provider.jaxws", "OMElementPayloadProviderPort");
        	
            service = Service.create(serviceName);
            service.addPort(port, soapBinding, endpointUrl_1);
    		
    	}else{
    		
    		serviceName = new QName("http://server.wsfvt.omelementsoapfault.provider.jaxws", "OMElementMessageProviderService");
        	port = new QName("http://server.wsfvt.omelementsoapfault.provider.jaxws", "OMElementMessageProviderPort");
        	
            service = Service.create(serviceName);
            service.addPort(port, soapBinding, endpointUrl_2);
    		
    	}
        
    	System.out.println("Creating dispatch");
        Dispatch<OMElement> dispatch = service.createDispatch(port, OMElement.class,
                                                              serviceMode);
    	//invoke the service        	
    	return dispatch.invoke(message);
    }
}
