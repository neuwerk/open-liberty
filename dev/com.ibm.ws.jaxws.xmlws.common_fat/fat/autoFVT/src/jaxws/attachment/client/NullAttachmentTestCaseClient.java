package jaxws.attachment.client;

import jaxws.attachment.server.*;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.*;
import javax.activation.DataHandler;

public class NullAttachmentTestCaseClient {

	/** 
	 * Sends a request to the server with a MIME attachment described as either null or not_null by the 
	 * string taken as a parameter
	 */
	public static void testAttachment(String attachmentDescription) throws Exception
	{
		QName qn = new QName("http://server.attachment.jaxws", "CheckAttachmentService");
		String url = "http://REPLACE_WITH_HOST_NAME:REPLACE_WITH_PORT_NUMBER/attachment/CheckAttachmentImpl?wsdl";
		String request = "hello " + attachmentDescription;	//request to be sent to the server
		
		Holder<String> resp = new Holder<String>("foo");
		Holder<DataHandler> mimeAttachment;
		URL u = new URL(url);
		CheckAttachmentService server = new CheckAttachmentService(u, qn);
		
		mimeAttachment = new Holder<DataHandler>(new DataHandler("test", "text/xml"));				
		System.out.println("Sending \"" + request + "\" to server");
		server.getCheckAttachmentServicePort().retrieve(request, resp, mimeAttachment);
		System.out.println("Server response" + resp.value);
	}
}
