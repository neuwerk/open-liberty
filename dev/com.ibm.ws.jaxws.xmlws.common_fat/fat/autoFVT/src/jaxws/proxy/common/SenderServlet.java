//
// @(#) 1.8 autoFVT/src/jaxws/proxy/common/SenderServlet.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/8/08 12:24:04 [8/8/12 06:55:12]
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
// 12/18/06 sedov       409973           Updated getNamespaceFromPayload
// 02/16/07 sedov       420835           Added contentType
// 11/29/07 sedov       486125           Improved payload detection
// 01/08/08 sedov       490688           Fixed payload bug
//
package jaxws.proxy.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Holder;


/**
 * A simple sender servlet. Requests are sent with the request in
 * between # marks (e.g., #doX#). For each request, a pre-packaged
 * response is sent back using the same envelope as the request
 */
public class SenderServlet  extends HttpServlet {

	private static int HTTP_OK = 200; // response is not a fault
	private static int HTTP_RETURN_FAULT = 500; // sending back a Fault envelope
	private static int HTTP_UNSUPPORTED_METHOD = 405; // used GET not a POST
	private static int HTTP_MALFORMED_INPUT = 400; // 
	
	private int status = HTTP_OK;	
	
	private String header = null;
	private String trailer = null;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{
		InputStream is = req.getInputStream();
		OutputStream os = res.getOutputStream();
		Holder<Integer> rc = new Holder<Integer>(200);
		Holder<String> type = new Holder<String>("text/xml; charset=UTF-8");
		
		process(is, os, rc, type);

	}

	public void process(InputStream is, OutputStream os, Holder<Integer> status, Holder<String> contentType) throws IOException, ServletException{
		int b;
		StringBuffer buffer = new StringBuffer();
		int i =0;
		while ((b = is.read()) != -1) {
			buffer.append((char) b);
		}
		
		String message = String.valueOf(buffer);
		String payload = getPayload(message);
		String operation = getOperationName(payload);
		String ret = null;

		System.out.println("*********************************");
		System.out.println("Operation=" + operation);
		System.out.println("Message=" + message);
		
		contentType.value = "text/xml; charset=UTF-8";
		if (operation == null){
			throw new ServletException("Null operation specified");	
		} else if (operation.equals("send_roundtrip")){
			ret = unexpectedMessage(message, payload);
		} else if (operation.equals("send_invalidMessage")){
			ret = invalidMessage(message, payload);
		} else if (operation.equals("send_wrongBeanFieldsMatch")){
			ret = invalidMessageFieldsMatch(message, payload);
		} else if (operation.equals("send_wrongBeanFieldsMismatch")){
			ret = invalidMessageFieldsMismatch(message, payload);
		} else if (operation.equals("send_missingParam")){
			ret = invalidMissingParam(message, payload);
		/*} else if (operation.equals("send_entityRef")){
			ret = invalidEntityRef(message, payload); //*/
		} else if (operation.equals("check_soapEnvelopeVersion")){
			ret = checkSOAP12Env(message, payload);		
			contentType.value = "application/soap+xml; charset=UTF-8";
		} else {
			throw new ServletException("Invalid operation specified '"
					+ operation + "'.");			
		}
			
		System.out.println("Response=" + ret);
		System.out.println("*********************************");

		status.value = this.status;
		os.write(ret.getBytes());		
	}
	
	/**
	 * Check SOAP envelope version to make sure the bindingType is being picked up correctly
	 * @param message
	 * @param payload
	 * @return
	 */
	private String checkSOAP12Env(String message, String payload) {
		String ret = null; 
		String ns = getNamespaceFromPayload(payload);
		
		if (message.indexOf(Constants.SOAP11_ENVELOPE) != -1){
			//soap envelope is 1.2
			ret = "<nsping:pingResponse xmlns:nsping='" + ns + "'>"
				+ "<message_out>soap1.2</message_out></nsping:pingResponse>";
		} else if (message.indexOf(Constants.SOAP12_ENVELOPE) != -1){
			// soap enevlope is soap 1.1
			ret = "<nsping:pingResponse xmlns:nsping='" + ns + "'>"
				+ "<message_out>soap1.1</message_out></nsping:pingResponse>";
		} else {
			// unknown envelope
			ret = "<nsping:pingResponse xmlns:nsping='" + ns + "'>"
				+ "<message_out>unknown</message_out></nsping:pingResponse>";
		}
		
		return header + ret + trailer;
	}


	/**
	 * Assumes that the client does not expect a message to be roundtripped
	 * @param message
	 * @param payload
	 * @return
	 */
	private String unexpectedMessage(String message, String payload) {
		
		return message;
	}


	/**
	 * Assumes that the client will not be aware of this message. doNotReturn
	 * is not defined in a wsdl 
	 * @param message
	 * @param payload
	 * @return
	 */
	private String invalidMessage(String message, String payload) {
		String ret = "<ns1:ReturnType>"
				   + "<doNotReturn>DocLitUnwrappedEndpoint</doNotReturn>"
                   + "</ns1:ReturnType>";
		
		return header + ret + trailer;
	}

	/**
	 * Assumes that the client will not be aware of this message. Client expects back
	 * a ReturnType(int) gets back AnotherReturnType(String), testing for wrapper bean
	 * validation
	 *  
	 * @param message
	 * @param payload
	 * @return
	 */
	private String invalidMessageFieldsMatch(String message, String payload) {
		String ret = "<ns1:AnotherReturnType>"
				   + "<return_str>DocLitUnwrappedEndpoint</return_str>"
                   + "</ns1:AnotherReturnType>";
		
		return header + ret + trailer;
	}
	
	/**
	 * Assumes that the client will not be aware of this message
	 * @param message
	 * @param payload
	 * @return
	 */
	private String invalidMessageFieldsMismatch(String message, String payload) {
		String ret = "<ns1:YetAnotherReturnType>"
				   + "<return_str>41</return_str>"
                   + "</ns1:YetAnotherReturnType>";
		
		return header + ret + trailer;
	}	

	/**
	 * Send a message where one of the fields is missing
	 * @param message
	 * @param payload
	 * @return
	 */
	private String invalidMissingParam(String message, String payload) {
		
		String ns = getNamespaceFromPayload(payload);
		String ret = "<ans:twoWayInOutResponse xmlns:ans='" + ns + "'>"
                   + "<myString>TheString</myString>"
                   + "</ans:twoWayInOutResponse>";
		
		return header + ret + trailer;
	}	
	
	/**
	 * Send a message where a file on a remote system is referenced
	 * @param message
	 * @param payload
	 * @return
	 */
	/*private String invalidEntityRef(String message, String payload) {
		
		String ref = "<!DOCTYPE PAYLOAD [<!ENTITY payload SYSTEM \"file:@WSDL_LOCATION@/message.xml\">]>";
		String ret = "&payload;";//*/
		/*	       + "<ans:twoWayInOutResponse xmlns:ans='http://rpclit.common.wsfvt.proxy.jaxws'>"
                   + "<myString>TheString</myString>"
                   + "<myInt>41</myInt>"
                   + "</ans:twoWayInOutResponse>"; //*/
		/*
		String hdr = header.substring(header.lastIndexOf("?>") + 2);
		
		return ref + hdr + ret + trailer;
	}	//*/
	
	public String getNamespaceFromPayload(String payload){
		StringTokenizer st = new StringTokenizer(payload, " \t><");
		
		while (st.hasMoreTokens()){
			String tok = st.nextToken();
			
			if (tok.indexOf("proxy.jaxws") > 0){
				int s = tok.indexOf("=");
				char quote = tok.charAt(s + 1);
				
				String ns=tok.substring(s + 2, tok.indexOf(quote, s + 2));
				
				return ns;
			}
		}
		
		return null;
	}
	
	/**
	 * Auxiliary method used to determine operation name from payload
	 * 
	 * @param payload
	 * @return
	 */
	private String getOperationName(String payload) {

		if (payload == null)
			return null;

		int one = payload.indexOf("#") + 1;
		int two = payload.indexOf("#", one);

		if (one == -1 || two == -1)
			return null;
		else
			return payload.substring(one, two);
	}

	/**
	 * Auxiliary method used to Extract Payload from SOAP Envelope
	 * 
	 * @param message
	 * @return
	 */
	private String getPayload(String message) {
		int bodyOpen = message.indexOf(":Body");
		bodyOpen = message.indexOf('>', bodyOpen) + 1;
		int bodyClose = message.indexOf(":Body",
				bodyOpen) - 1;
		bodyClose = message.lastIndexOf('<', bodyClose);

		if (bodyOpen < 0 || bodyClose < 0)
			return null;

		// save SOAP header and trailer
		header = message.substring(0, bodyOpen);
		trailer = message.substring(bodyClose);
		
		return message.substring(bodyOpen, bodyClose);
	}
}
