//
// @(#) 1.5 autoFVT/src/jaxws/dispatch/wsfvt/server/DispatchSimplePortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/22/10 11:31:36 [8/8/12 06:54:46]
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
// 05/31/06 sedov    LIDB3296.42        New File
// 08/23/06 sedov    LIDB3296-42.02     Beta Drop
// 01/22/10 btiffany 635671             Don't log really large messages - logs overflow on z. 
//

package jaxws.dispatch.wsfvt.server;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceException;

import jaxws.dispatch.wsfvt.common.Constants;

/**
 * This servlet is intended to catch all malformed messages that leave the
 * Dispatch. It will also send back malformed data to test for Dispatch
 * receiving it.
 */
public class DispatchSimplePortImpl extends HttpServlet {

	private static int HTTP_OK = 200; // response is not a fault
	private static int HTTP_RETURN_FAULT = 500; // sending back a Fault envelope
	private static int HTTP_MALFORMED_INPUT = 400; // 
	
	private int status = HTTP_OK;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// simulate a ?WSDL request, timeout when we receive a request
		if (req.getParameter("timeout").equals("true")) {
			try {
				Thread.sleep(Constants.SERVER_SLEEP_SEC * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		res.getOutputStream().print(
				"Simple Listener Servlet is alive and kickin'!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		InputStream stream = req.getInputStream();
		StringBuffer buffer = new StringBuffer();

		int b;
		while ((b = stream.read()) != -1) {
			buffer.append((char) b);
		}
		String message = buffer.toString();
		String payload = getPayload(message);
		String operation = getOperationName(payload);
		String ret = null;

		System.out.println("*********************************");
		System.out.println("Operation=" + operation);
        // 635671
        if (message.length() > 512 ){
            System.out.println("Message was large, logging truncated to 512 bytes");
            System.out.println("Message=" +message.substring(0,512));
        } else{            
            System.out.println("Message=" + message);
        }    

		res.setContentType(getContentType(message));
		res.setStatus(status);

		if ((ret = badMessageTests(message)) != null) {
			// we have a bad message, no further processing is needed
		} else if (operation.equals("id")) {
			ret = id(message);
		} else if (operation.equals("twoWay")) {
			ret = twoWay(message);
		} else if (operation.equals("sessionId")) {
			ret = sessionId(message, req);
		} else if (operation.equals("sendSoap11Envelope")) {
			ret = sendSoap11Envelope(message);
		} else if (operation.equals("sendSoap12Envelope")) {
			ret = sendSoap12Envelope(message);
		} else if (operation.equals("sendHTTPEnvelope")) {
			ret = sendHTTPEnvelope(message);
		} else if (operation.equals("sendException")) {
			ret = sendException(message);
		} else if (operation.equals("sendEmptyFault")) {
			ret = sendEmptyFault(message);
		} else if (operation.equals("sendFault")) {
			ret = sendFault(message);
		} else if (operation.equals("sendNonWSIFault")) {
			ret = sendNonWSIFault(message);
		} else if (operation.equals("sendFaultNoEnvelope")) {
			ret = sendFaultNoEnvelope(message);
		} else if (operation.equals("sendEmptyMessage")) {
			ret = sendEmptyMessage(message);
		} else if (operation.equals("sendNonXMLMessage")) {
			ret = sendNonXMLMessage(message);
		} else if (operation.equals("sendWrongSOAPNSMessage")) {
			ret = sendWrongSOAPNSMessage(message);
		} else if (operation.equals("sendNonNSQualifiedMessage")) {
			ret = sendNonNSQualifiedMessage(message);
		} else if (operation.equals("sendEmptyEnvelope")) {
			ret = sendEmptyEnvelope(message);
		} else if (operation.equals("sendNonWSIEnvelope")) {
			ret = sendNonWSIEnvelope(message);
		} else if (operation.equals("sendInvalidXMLPayload")) {
			ret = sendInvalidXMLPayload(message);
		} else if (operation.equals("sendNonXMLPayload")) {
			ret = sendNonXMLPayload(message);
		} else if (operation.equals("sendNonNSQualifiedPayload")) {
			ret = sendNonNSQualifiedPayload(message);
		} else if (operation.equals("sendWrongNSQualifiedPayload")) {
			ret = sendWrongNSQualifiedPayload(message);
		} else {
			throw new ServletException("Invalid operation specified '"
					+ operation + "'.");
		}
        // 635671
        if (ret.length() > 512 ){
            System.out.println("Response was large, logging truncated to 512 bytes");
            System.out.println("Response=" +ret.substring(0,512));
        } else{
            System.out.println("Response=" + ret);
        }
		System.out.println("*********************************");

		res.setStatus(status);
		res.getOutputStream().print(ret);
	}

	/**
	 * Deterine content type of the message based on namespace
	 * @param message
	 * @return
	 */
	private String getContentType(String message) {

		if (message.indexOf("http://www.w3.org/2003/05/soap-envelope") != -1) {
			// soap 1.2 envelope received
			return "application/soap+xml";
		} else {
			return "text/xml";
		}

	}

	/**
	 * Send a two way response without a soap envelope
	 * 
	 * @param message
	 * @return
	 */
	private String sendHTTPEnvelope(String message) {
		return Constants.TWOWAY_MSG_RESPONSE;
	}

	/**
	 * Send a two way resposne message in SOAP 1.2 Envelope
	 * 
	 * @param message
	 * @return
	 */
	private String sendSoap12Envelope(String message) {
		return Constants.SOAP12_HEADER + Constants.TWOWAY_MSG_RESPONSE
				+ Constants.SOAP12_TRAILER;
	}

	/**
	 * Return a twoWay reply message in a SOAP 1.1 Envelope
	 * 
	 * @param message
	 * @return
	 */
	private String sendSoap11Envelope(String message) {
		return Constants.SOAP11_HEADER + Constants.TWOWAY_MSG_RESPONSE
				+ Constants.SOAP11_TRAILER;
	}

	/**
	 * Return a session id, used for verifying session keep alive
	 * 
	 * @param message
	 * @param req
	 * @return
	 * @throws ServletException
	 */
	private String sessionId(String message, HttpServletRequest req)
			throws ServletException {

		return twoWay(message.replace("sessionCreationTime", "#"
				+ req.getSession().getId() + "#"));
	}

	/**
	 * handle request to identify self
	 * 
	 * @param payload
	 * @return
	 */
	private String id(String payload) {
		payload = payload.replace("#id#", this.getClass().getName());

		payload = payload.replaceAll("twoWay", "twoWayResponse");
		return payload;
	}

	/**
	 * TwoWay operation, similar to DispatchPortTypeImpl but...does not send
	 * back twoWayResponse. This is used to id which endpoint is being tested
	 * 
	 * @param payload
	 * @return
	 * @throws WebServiceException
	 */
	private String twoWay(String payload) throws ServletException {
		status = HTTP_OK;
		return payload;
	}

	/**
	 * Test sending back a non-web service exception
	 * 
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendException(String message) throws ServletException {
		status = HTTP_MALFORMED_INPUT;
		throw new NullPointerException("A Null Pointer Exception");
	}

	/**
	 * Send a SOAP Fault back, from wsi.org R1000 (Correct Example)
	 * 
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendFault(String message) throws ServletException {
		status = HTTP_RETURN_FAULT;
		String ret = "<soap:Fault xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/' >"
				+ "<faultcode>soap:Client</faultcode>"
				+ "<faultstring>fault_string</faultstring>"
				+ "<faultactor>"
				+ Constants.SOAP11_ENDPOINT_ADDRESS
				+ "</faultactor><detail>"
				+ "<ns2:twoWayExceptionFault xmlns:ns2='http://" + Constants.WSDL_NAMESPACE + "'>TwoWay Fault</ns2:twoWayExceptionFault>"
				+ "</detail></soap:Fault>";
		return Constants.SOAP11_HEADER + ret + Constants.SOAP11_TRAILER;
	}

	/**
	 * Send a SOAP Fault back without an evelope
	 * 
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendFaultNoEnvelope(String message) throws ServletException {
		status = HTTP_RETURN_FAULT;
		String ret = "<soap:Fault xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/' >"
				+ "<faultcode>soap:Client</faultcode>"
				+ "<faultstring>Invalid message format</faultstring>"
				+ "<faultactor>http://example.org/someactor</faultactor>"
				+ "<detail><m:msg xmlns:m='http://example.org/faults/exceptions'>"
				+ "This is a test</m:msg>"
				+ "<m:Exception xmlns:m='http://example.org/faults/exceptions'>"
				+ "<m:ExceptionType>Severe</m:ExceptionType>"
				+ "</m:Exception></detail></soap:Fault>";
		return ret;
	}

	private String sendEmptyFault(String message) throws ServletException {
		status = HTTP_RETURN_FAULT;
		String ret = "<soap:Fault xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/' />";
		return Constants.SOAP11_HEADER + ret + Constants.SOAP11_TRAILER;
	}

	/**
	 * Send a SOAP Fault back, from wsi.org R1000 (Incorrect Example)
	 * 
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendNonWSIFault(String message) throws ServletException {
		status = HTTP_RETURN_FAULT;
		String ret = "<soap:Fault xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/' >"
				+ "<faultcode>soap:Client</faultcode>"
				+ "<faultstring>Invalid message format</faultstring>"
				+ "<faultactor>http://example.org/someactor</faultactor>"
				+ "<detail>This fault does not comply with WS-I</detail>"
				+ "<m:Exception xmlns:m='http://example.org/faults/exceptions' >"
				+ "<m:ExceptionType>Severe</m:ExceptionType>"
				+ "</m:Exception></soap:Fault>";
		return Constants.SOAP11_HEADER + ret + Constants.SOAP11_TRAILER;
	}

	/**
	 * Send a message with no content
	 * 
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendEmptyMessage(String message) throws ServletException {
		status = HTTP_OK;
		return "";
	}

	/**
	 * Send a message with a correct SOAP Envelope but not XML payload
	 * 
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendNonXMLMessage(String message) throws ServletException {
		status = HTTP_OK;
		return "I wanna be an Oscar Meyer Weiner";
	}

	/**
	 * Send a SOAP envelope without a payload
	 * 
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendEmptyEnvelope(String message) throws ServletException {
		status = HTTP_OK;
		return Constants.SOAP11_HEADER + Constants.SOAP11_TRAILER;
	}

	/**
	 * Send a SOAP Envelope with non XML a payload
	 * 
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendNonXMLPayload(String message) throws ServletException {
		status = HTTP_OK;
		return Constants.SOAP11_HEADER + "I wanna be an Oscar Meyer Weiner"
				+ Constants.SOAP11_TRAILER;
	}

	/**
	 * Send a SOAP Envelope with an unclosed Tag
	 * 
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendInvalidXMLPayload(String message)
			throws ServletException {
		status = HTTP_OK;
		return Constants.SOAP11_HEADER
				+ "<ns1:UNCLOSED_TAG xmlns='http://example.com'>"
				+ Constants.SOAP11_TRAILER;
	}

	/**
	 * Send a SOAP Envelope that contains payload outside of the Body From
	 * WS-I.org Basic Profile R1011, Incorrect
	 * 
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendNonWSIEnvelope(String message) throws ServletException {
		status = HTTP_OK;
		String ret = "<soap:Envelope xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/' >"
				+ "<soap:Body><p:Process xmlns:p='http://example.org/Operations' /></soap:Body>"
				+ "<m:Data xmlns:m='http://example.org/information' >Here is some data with the message"
				+ "</m:Data></soap:Envelope>";

		return ret;
	}

	/**
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendNonNSQualifiedPayload(String message)
			throws ServletException {
		status = HTTP_OK;
		String payload = "<twoWay><value>test_message</value></twoWay>";
		return Constants.SOAP11_HEADER + payload + Constants.SOAP11_TRAILER;
	}

	/**
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendWrongNSQualifiedPayload(String message)
			throws ServletException {
		status = HTTP_OK;
		String payload = "<ns1:twoWayResponse xmlns:ns1=\"http://unexpected.namespace.ibm.com\"><value>test_message</value></ns1:twoWayResponse>";
		return Constants.SOAP11_HEADER + payload + Constants.SOAP11_TRAILER;
	}

	/**
	 * Send back a SOAP envelope that is not qualified with a SOAP Namespace
	 * 
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendNonNSQualifiedMessage(String message)
			throws ServletException {
		status = HTTP_OK;
		String header = "<Envelope><Body>";
		String trailer = "</Body></Envelope>";

		return header + Constants.ONEWAY_MSG + trailer;
	}

	/**
	 * Send back a SOAP envelope that is not qualified with a SOAP Namespace
	 * 
	 * @param message
	 * @return
	 * @throws ServletException
	 */
	private String sendWrongSOAPNSMessage(String message)
			throws ServletException {
		status = HTTP_OK;
		String header = "<soap:Envelope xmlns:soap=\"http://www.example.com/soap/no_envelope/\"><soap:Body>";
		String trailer = "</soap:Body></soap:Envelope>";

		return header + Constants.ONEWAY_MSG + trailer;
	}

	/**
	 * Determine Operation name from payload
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
	 * Extract Payload from SOAP Envelope
	 * 
	 * @param message
	 * @return
	 */
	private String getPayload(String message) {
		int bodyOpen = message.indexOf(":Body>") + 6;
		int bodyClose = message.lastIndexOf("<", message.indexOf(":Body>",
				bodyOpen));

		if (bodyOpen == -1 || bodyClose == -1)
			return null;

		return message.substring(bodyOpen, bodyClose);
	}

	/**
	 * Check for messages that should never reach the server
	 * 
	 * @param message
	 * @return
	 */
	private String badMessageTests(String message) {

		if (message == null || message.length() == 0) {
			// received an empty mesage
			return Constants.SOAP11_HEADER
					+ Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
							"empty_message_received")
					+ Constants.SOAP11_TRAILER;
		} else if (message.indexOf(":Body>") > 0) {
			// received an envelope
			int one = message.indexOf(":oneWay");
			int two = message.indexOf(":twoWay");
			int p = Math.max(one, two);

			if (p == -1) {
				// envelope contains invalid payload
				String payload = getPayload(message);
				return Constants.SOAP11_HEADER
						+ Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
								"invalid_payload_received: " + payload)
						+ Constants.SOAP11_TRAILER;
			} else {
				// envelope contains valid payload
				return null;
			}
		} else {
			return Constants.SOAP11_HEADER
					+ Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
							"empty_envelope_received")
					+ Constants.SOAP11_TRAILER;
		}

	}
}