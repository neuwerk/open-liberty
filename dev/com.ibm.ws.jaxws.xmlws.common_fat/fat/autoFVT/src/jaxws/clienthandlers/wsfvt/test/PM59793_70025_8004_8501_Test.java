//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2012
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date      UserId       Defect          Description
// ----------------------------------------------------------------------------

// 06/27/12  btiffany     PM59793.fvt     New test
//

package jaxws.clienthandlers.wsfvt.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPBinding;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

//public class PM59793_70025_8004_8501_Test extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
public class PM59793_70025_8004_8501_Test extends FVTTestCase {
   
    /*
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public PM59793_70025_8004_8501_Test(String name) {
        super(name);
    }


    /*
     * The main method.  Used for debugging. 
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        //TestRunner.run(suite());
    	PM59793_70025_8004_8501_Test Debugme = new PM59793_70025_8004_8501_Test("foo");
    	Debugme.testPM59793();
    }


    /**
     * This method controls which test methods to run.
     *
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(PM59793_70025_8004_8501_Test.class);
    }

    
    // static string to store message we capture from client. 
    public static String messageBuffer;
    
   
    
    /**
     * Tests that change for PM59793 works.  In that apar, under certain conditions
     * where a handler called getMessage and getKey multiple times, updates to the message 
     * by the handler could be lost.
     * 
     * A JVM prop can be set which will reduce the use of internal caching and avoid this 
     * problem.
     * 
     * We can see if the handler updates are being preserved by merely inspecting a message
     * outbound from a client, we don't need to set up a client, a service, etc.
     * 
     * To check the message we will spin of a thread which will listen on a port,
     * we will then invoke the client with the jvm prop set, then
     * inspect that message and confirm that the message change was not lost. 
     * 
     * Inner classes in this test function as the client and as the message listener.
     * 
     * 
     */    
    public void testPM59793() {
    	try{
	        System.out.println("====== In testPM59793 ======");
	        Thread t = new Thread(new MessageListener());
	        t.start();  // start the listener
	        
	        System.setProperty("jaxws.handler.reducecaching", "true");  // ENABLE pm59793
	        
	        /* creaete a dispatch client - this client is from the infocenter */
	        String endpointUrl =   "http://localhost:44444/fooservice";
			
	        QName serviceName = new QName("http://com/ibm/was/wssample/echo/",
	         "EchoService");
	        QName portName = new QName("http://com/ibm/was/wssample/echo/",
	         "EchoServicePort");
	        		
	        /** Create a service and add at least one port to it. **/ 
	        Service service = Service.create(serviceName);
	        service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointUrl);
	        		
	        /** Create a Dispatch instance from a service.**/ 
	        Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, 
	        SOAPMessage.class, Service.Mode.MESSAGE);
	        	
	        /** Create SOAPMessage request. **/
	        // compose a request message
	        MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

	        // Create a message.  This example works with the SOAPPART.
	        SOAPMessage request = mf.createMessage();
	        SOAPPart part = request.getSOAPPart();

	        // Obtain the SOAPEnvelope and header and body elements.
	        SOAPEnvelope env = part.getEnvelope();
	        SOAPHeader header = env.getHeader();
	        SOAPBody body = env.getBody();

	        // Construct the message payload.
	        SOAPElement operation = body.addChildElement("invoke", "ns1",
	         "http://com/ibm/was/wssample/echo/");
	        SOAPElement value = operation.addChildElement("arg0");
	        value.addTextNode("ping");
	        request.saveChanges();

	        try{
		        /** attach the handler and invoke the client. **/
	        	/* since our listener class isn't working quite right, we won't get back a response */
		        System.out.println("invoking the client");
		        List<Handler> handlers = new ArrayList<Handler>();		        
		        handlers.add(new InnerHandler());
		        dispatch.getBinding().setHandlerChain(handlers);
		        
		        SOAPMessage response = dispatch.invoke(request);	      
	        	System.out.println("client has returned, received: ");
	        	response.writeTo(System.out);
	        } catch (Exception e2){
	        	System.out.println("An exception was caught in the client, communications exceptions are normal: "+ e2);
	        	e2.printStackTrace(System.out);
	        }	        
	       
	        
    	} catch(Exception ex){
    		fail("Caught an unexpected exception: "+ex );
    		ex.printStackTrace(System.out);
    	} finally{
    		System.clearProperty("jaxws.handler.reducecaching");
    	}
    	
    	// only now that we have cleaned up after ourselves, closed sockets, reset properties, can we 
    	// check the response.  That soap envelope should have the header in it. 
    	 
        System.out.println("The SOAP Message transmitted by the client and it's handlers was: ");
        System.out.println(messageBuffer);
        assertTrue("soap message transmitted  by the client did not contain a soap header.", 
        		messageBuffer.contains("<soapenv:Header>") && messageBuffer.contains("</soapenv:Header>"));        
    	System.out.println("====== SUCCESS: testSOAP11EndpointHandlerChain() ======\n");  	
        
       
    }
    
    /**
     * The handler class, taken from customer's application and sanitized, which could add a soap header and then lose it.
     * @author btiffany
     *
     */
    class InnerHandler implements  javax.xml.ws.handler.soap.SOAPHandler<SOAPMessageContext> {
    	// routing header constants
    	public static final String HEADER_ELEMENT = "resolve";
    	public static final String SUB_ELEMENT = "contextIdentifier";
    	public static final String CID_ELEMENT = "consumerIdentifier";
    	public static final String NS_PREFIX = "gep63";
    	public static final String NS_URI = "http://www.ibm.com/xmlns/prod/serviceregistry/profile/v6r3/GovernanceEnablementModel";

    	// WS-Addressing constants
    	public static final String WSA_REF_ELEM = "ReferenceParameters";
    	public static final String WSA_EPR_ELEM = "EndpointReference";
    	public static final String WSA_ADDR_ELEM = "Address";
    	public static final String WSA_NS_PREFIX = "wsa";
    	public static final String WSA_NS_URI = "http://www.w3.org/2005/08/addressing";
    	public static final String address = "http://peze.com/TemperatureConverterService/TemperatureConverterService";

		@Override
		public void close(MessageContext arg0) {
			// TODO Auto-generated method stub			
		}

		@Override
		public boolean handleFault(SOAPMessageContext arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		

		@Override
		public Set getHeaders() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public boolean handleMessage(SOAPMessageContext smc) {

			boolean result = true;		

			out("** handleMessage invoked from SOAPHandler");
			
			Boolean isOutbound = (Boolean) smc.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
			try{
				if (isOutbound.booleanValue())
				{
					out("Handler will handle this outbound soap message");
					dumpSOAPMessage(smc);
					
				}
				else
				{
					out("* exiting because this is an INBOUND message");
					return result;
				}
			
				
				// BT: this code run here causes header to disappear, they say. ---
				//smc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,res);
				//smc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:19080/wsfp_hello_svc/HelloService");		
			
			    //BT: if dumpsoapmessage is present, header is lost.  If it's commented out, header is added and things work. 
				// BT: it's not just the duplicate calls to getMessage, it's the duplicate calls to get(key) and then 
				// BT: Printstream.println on them.   Extremely strange.
				 dumpSOAPMessage(smc);
				 out("Building a new header");
				SOAPFactory sFactory = SOAPFactory.newInstance();

				// create the dependency element including its children
				SOAPElement idElem = sFactory.createElement(CID_ELEMENT, NS_PREFIX,NS_URI);
				idElem.addTextNode("TCC01");
				SOAPElement consumesElem = sFactory.createElement(SUB_ELEMENT,NS_PREFIX, NS_URI);
				consumesElem.addTextNode("http://ejbs");

				// create a WS-Addressing EPR and Address nodes 
				SOAPElement eprElem = sFactory.createElement(WSA_EPR_ELEM,WSA_NS_PREFIX, WSA_NS_URI);
				eprElem.addChildElement(sFactory.createElement(WSA_ADDR_ELEM,WSA_NS_PREFIX, WSA_NS_URI).addTextNode(address));

				// create the ReferenceParameters element to hold the
				// service dependency info
				SOAPElement refElem = sFactory.createElement(WSA_REF_ELEM,WSA_NS_PREFIX, WSA_NS_URI);

				// add the dependency element to the WSA element
				refElem.addChildElement(idElem);
				
				// add the dependency element to the WSA element
				refElem.addChildElement(consumesElem);

				// add the reference parameters element to the EPR element
				eprElem.addChildElement(refElem);			
				
				out("Get the header from the context");
				SOAPHeader header = smc.getMessage().getSOAPHeader();

				// only add a header section to the message if header not yet present
				if (header == null) {
					header = smc.getMessage().getSOAPPart().getEnvelope().addHeader();
				}
				header.addChildElement(eprElem);
				out("WS addressing header added to envelope");
				
				//BT: if we modify the endpoint here, the header gets added? - nope. 
				//smc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,res);
				//smc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:19080/wsfp_hello_svc/HelloService");
				
				// BT: I think this will clear it? 
				// YES!! that causes us to flush our soapmessage cache, I think.
				// however customer is trying to avoid changing their code. 
				// 	smc.setMessage(smc.getMessage());
				
			} catch (Exception e) {
				out("Error adding Soap header to message");
				throw new RuntimeException(e);
			}

			// dump the message
			dumpSOAPMessage(smc);

			out("** handleMessage completed without error");
			return result;
		}

		private void dumpSOAPMessage(SOAPMessageContext smc) {

			if (smc == null) {
				out("dumpSOAPMessage - SOAP Message is null");
				return;
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					//out("-------DUMP OF SOAP MESSAGE CONTEXT-------");
					for (Iterator<String> iterator = smc.keySet().iterator(); iterator
							.hasNext();) {
						String key = iterator.next();
						// bt: this is bizarre.  The following line must be present to surface the problem.
						// just accessing the key won't do it. 
						//out("    SMC value: " + key + " = " + smc.get(key));
						
						// we'll use the following because we want to recreate the problem without console output
						PrintStream ps = new PrintStream(baos);
						ps.println(smc.get(key));
						
						
					
					}
					//out(" --------- Message XML -------- ");
					smc.getMessage().writeTo(baos);
					//out(baos.toString(getMessageEncoding(smc.getMessage())));
					//out(" --------- END Message XML -------- ");
					baos.close();
				} catch (Exception e) {
					baos = null;
					out("Error dumping soap message");
				}
			}
		}

		private String getMessageEncoding(SOAPMessage msg) throws SOAPException {
			String encoding = "utf-16";
			if (msg.getProperty(SOAPMessage.CHARACTER_SET_ENCODING) != null) {
				encoding = msg.getProperty(SOAPMessage.CHARACTER_SET_ENCODING)
						.toString();
			}
			return encoding;
		}

		private void out(String message) {
			System.out.println(message);
		}

    	
    }
    
  
    /**
     * an inner class to listen on a soap port 44444 for a post message, and return it to us as a String.
     * For some reason it isn't closing of the input stream properly but it does what we need for this test. 
     * @author btiffany
     *
     */
    class MessageListener implements Runnable {
    	String responseHeaders =  
    		        "HTTP/1.1 200 OK\n" + 
    		        "Content-Type: text/xml; charset=UTF-8\n" +
    		        "Content-Language: en-US\n" +
    		        "Content-Length: 250\n" +                        
    		        "Date: Wed, 23 Feb 2011 00:59:12 GMT\n" + 
    		        "Server: WL3WSE Mock Server/7.0\n";
    	
    	String responseSoap = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
    			"<SOAP-ENV:Body><ns1:invoke xmlns:ns1=\"http://com/ibm/was/wssample/echo/\"><arg0>ping</arg0></ns1:invoke></SOAP-ENV:Body></SOAP-ENV:Envelope>";   		        
    	  
    	      	
    	public void run(){
	    	 ServerSocket serverSocket = null;	 
	    	 try{
		         try {
		             serverSocket = new ServerSocket(44444);
		         } catch (IOException e) {
		             System.err.println("Could not listen on port: 44444.");
		             throw e;
		         }
		         System.out.println(" waiting for a connection on port 44444");
		         Socket clientSocket = null;
		         try {
		             clientSocket = serverSocket.accept();
		             clientSocket.setSoTimeout(10000); // 10 sec timeout on reads
		         } catch (IOException e) {
		             System.err.println("Accept failed.");
		             throw e;
		         }
		         
		         System.out.println(" accepted a connection");	
		         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);		         
		         InputStream in = clientSocket.getInputStream();		        
		         byte[] ba = new byte[64000];
		         
		         
		         // never could figure out why we don't exit this cleanly.
		         int bytesRead = 0;
		         try{
		        	 int rc = 0;		        	 
		             while (rc > -1){
		                 rc=in.read(ba);
		                 System.out.println("reading into buffer, bytes read = " + rc);
		                 bytesRead += rc;
		             }
		         } catch (Exception e){
		             System.out.println("caught exception when reading, going on: " + e);
		         }
		         messageBuffer =  new String(ba).substring(0,bytesRead);  // this is a static field in the parent class		         
		        // System.out.println("\n Received this input: \n"+  messageBuffer);
		         // now return a dummy response.  We have to fix up the content-length. 
		         String outbuffer = responseHeaders.replace("####",  String.valueOf(responseSoap.length()));
		         outbuffer += responseSoap;
		        // System.out.println("\n" + " Sending this response:  " + outbuffer);
		         out.print(outbuffer);
		         out.flush();
		         
		         System.out.println("sleeping 3 sec before closing connections");
		         Thread.sleep(3000);  // close too soon and server will complain.	         
		         
		         out.close();
		         in.close();
		         clientSocket.close();
		         serverSocket.close();
		         System.out.println("run method ending, thread is done");
	    	 } catch (Exception ioe){
	    		 System.out.println("MessageListener caught unexpected Exception: "+ioe);
	    		 ioe.printStackTrace(System.out);
	    	 }
    	}
    }
    
   
}

