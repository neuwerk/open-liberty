package jaxws.attachment.test;

import jaxws.attachment.client.*;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.ws.Holder;

import com.ibm.ws.wsfvt.test.framework.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * This test checks how the server treats a request for attachments that are null or not_null
 *
 */
public class PK57557_NullAttachmentTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase{

		public static void main(String args[]) {
			TestRunner.run(suite());
		}
		
		public static Test suite() {
			TestSuite suite = new TestSuite(PK57557_NullAttachmentTestCase.class);
			return suite;
		}
		
		public void setUp() {
//			log = new LogCommands(Constants.WAS_HOST_NAME, Constants.WAS_HTTP_PORT,
//					"/jaxws.nullattachment.logger/logger", LogCommands.LogFile.sysout,
//					Constants.WAS_SERVER_NAME);
		}
		
		// Send the server a request for an attachment that is not null. The server should create a well-defined message with a MIME attachment.
		@com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send the server a request for an attachment that is not null. The server should create a well-defined message with a MIME attachment.",
		expectedResult="",
		since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
		public void testNotNullAttachment() {
			try
			{
				NullAttachmentTestCaseClient.testAttachment("not_null");	// Send a request for a message with a defined attachment
			}
			catch(Exception e)
			{
				System.out.println("====== FAILURE: testNotNullAttachment() ======");
				fail("Exception caught: " + e);		//  If an exception is caught, the test fails
			}
			System.out.println("====== SUCCESS: testNotNullAttachment() ======"); 
			System.out.println();
		}
		
		
		//Send the server a request for a null attachment.  The server should send back a reponse with a null attachment. It should not throw a NullPointerException.
		@com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send the server a request for a null attachment.  The server should send back a reponse with a null attachment. It should not throw a NullPointerException. ",
		expectedResult="",
		since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
		public void testNullAttachment() {
			try
			{
				NullAttachmentTestCaseClient.testAttachment("null");		// Send a request for a message with a null attachment
			}
			catch(Exception e)				//If an exception is caught, the test fails
			{
				System.out.println(e); 
				e.printStackTrace();
				System.out.println("====== FAILURE: testNullAttachment() ======");
				fail("Exception caught: " + e);
			}
			
			System.out.println("====== SUCCESS: testNullAttachment() ======");
			System.out.println();
		}
}
