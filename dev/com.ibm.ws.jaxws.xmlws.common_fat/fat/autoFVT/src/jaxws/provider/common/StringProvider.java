//
// @(#) 1.3 autoFVT/src/jaxws/provider/common/StringProvider.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/5/06 11:36:23 [8/8/12 06:54:49]
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
// 07/31/06 sedov       LIDB3296.42     New File
// 08/31/06 sedov       LIDB3296-42.03  Beta drop
// 12/06/06 sedov       408880          Refactored

package jaxws.provider.common;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;



/**
 * Provider delegate. All provider implementations process messages based on
 * their type and binding, convert data into Strings and pass it to this class
 * for actual processing.
 */
public class StringProvider {

	private String binding = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING;

	private Service.Mode mode = Service.Mode.MESSAGE;

	private Object caller = null;
	
	private static final boolean DEBUG = true;

	/**
	 * @param binding -
	 *            binding used by the provider
	 * @param mode -
	 *            mode used by the provider
	 */
	public StringProvider(String binding, Service.Mode mode, Object caller) {
		this.binding = binding;
		this.mode = mode;
		this.caller = caller;
	}

	/**
	 * Generic handler method used for dispatching requests
	 * 
	 * @param message
	 * @param ctxt
	 * @return
	 */
	public String invoke(String message, WebServiceContext ctxt)
			throws WebServiceException {

		// get command
		String command = Constants.getValueBetweenHashes(message);
		String ret = null;

		if (DEBUG) {
			System.out.println("********************************************");
			System.out.println("EndpointClass=" + caller.getClass().getName());
			System.out.println("Binding=" + binding);
			System.out.println("Mode=" + mode);
			System.out.println("Command=" + command);
			System.out.println("Message=" + message);
		}

		if ((ret = badMessageTests(message)) != null) {
			// ret is already set to a message
		} else if (command.equals("checkEnvelope")) {
			// client has requested to verify the envelope for validity
			ret = checkEnvelope(message, ctxt);
		} else if (command.equals("twoWay")) {
			// a ping twoWay message
			ret = twoWay(message, ctxt);
		} else if (command.equals("twoWayException")) {
			ret = twoWayException(message, ctxt);
		} else if (command.equals("twoWayException2")) {
			ret = twoWayException2(message, ctxt);			
		} else if (command.equals("sendReplyViaServletOutStream")) {
			// try an alternate way of sending back a response
			ret = sendReplyViaServletOutStream(message, ctxt);
		} else if (command.equals("unexpectedFailure")) {
			// throw an unexpected exception
			int x = 10 / 0;
		} else if (command.equals("oneWay")) {
			// a oneway operation, return a null
			ret = null;
		} else if (command.equals("returnEmpty")) {
			// test returning an empty object (implies nonWrapped nilable
			// objects)
			ret = returnEmpty(message, ctxt);
		} else {
			ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
					"#invalid_command#");
		}

		if (DEBUG) {
			System.out.println("Reply=" + ret);
			System.out.println("********************************************");
		}

		return ret;
	}

	/**
	 * Return an empty object
	 * 
	 * @param message
	 * @param ctxt
	 * @return
	 */
	private String returnEmpty(String message, WebServiceContext ctxt) {
		return envelope("");
	}

	/**
	 * Ping response
	 * 
	 * @param message
	 * @param ctxt
	 * @return
	 */
	private String twoWay(String message, WebServiceContext ctxt) {

		String msg = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
				"#twoWay# bind=" + this.binding + " mode=" + this.mode);
		return envelope(msg);
	}

	/**
	 * Try to return a service specific fault (instead of WebServiceException)
	 * Since we cannot throw an exception directly, we must also set the return
	 * code via MessageConext
	 * 
	 * @param message
	 * @param ctxt
	 * @return
	 */
	private String twoWayException(String message, WebServiceContext ctxt) {

		String f = "<soap:Fault>"
				+ "<faultcode>soap:Client</faultcode>"
				+ "<faultstring>Test TwoWayFault</faultstring>"
				+ "<faultactor>" + Constants.SOURCE_SOAP11MESSAGE_ADDRESS + "</faultactor>"
				+ "<detail>" + Constants.TWOWAY_MSG_EXCEPTION + "</detail>"
				+ "</soap:Fault>";

		// set HTTP REsponse code 500 - internal server error
		ctxt.getMessageContext().put(MessageContext.HTTP_RESPONSE_CODE, 500);

		return envelope(f);
	}
	
	/**
	 * Try to return a service specific fault (instead of WebServiceException)
	 * This approach throws the exception directly as SOAPFaultException
	 * 
	 * @param message
	 * @param ctxt
	 * @return
	 */
	private String twoWayException2(String message, WebServiceContext ctxt) {
		
		SOAPFactory fac;
		SOAPFault fault = null;
		QName qnTwoWayException = new QName(Constants.WSDL_NAMESPACE, "twoWayException");
		QName qnClientFault = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Client");
		
		try {
			fac = SOAPFactory.newInstance();
			
			fault = fac.createFault("Test TwoWayFault", qnClientFault);
			fault.setFaultActor(Constants.SOURCE_SOAP11MESSAGE_ADDRESS);
			
			Detail detail = fault.addDetail();
			SOAPElement twoWayException = detail.addChildElement(qnTwoWayException);
			twoWayException.setTextContent("Test TwoWayFault");
			
		} catch (SOAPException e) {
			throw new WebServiceException("Unable to create fault");
		}
		
		throw new SOAPFaultException(fault);
	}	

	/**
	 * Attempt to send a reply via Servlet.outputStream. This is not a normal
	 * usage scenario, the intent is to see if this will cause the server any
	 * problems
	 * 
	 * @param message
	 * @param ctxt
	 * @return
	 */
	private String sendReplyViaServletOutStream(String message,
			WebServiceContext ctxt) {

		String reply = envelope(Constants.TWOWAY_MSG_RESPONSE);
		
		// obtain ServletResponse object
		HttpServletResponse response = (HttpServletResponse) ctxt
				.getMessageContext().get(MessageContext.SERVLET_RESPONSE);

		try {
			response.getOutputStream().print(reply);
			response.getOutputStream().flush();
		} catch (IOException e) {
			throw new WebServiceException("Unable to write to Servlet.OutputStream", e);
		}

		return null;
	}

	/**
	 * Check the message and determine if it should have
	 * 
	 * @param message
	 * @return
	 */
	private String badMessageTests(String message) {

		String ret = null;
		if (mode == Service.Mode.MESSAGE && binding != HTTPBinding.HTTP_BINDING) {
			// when HTTPBinding is used the mode is irrelevant

			int b = message.indexOf(":Body");
			int a = message.lastIndexOf("<", b);

			if (b == -1) {
				// the envelope must contain Body tag, otherwise we did
				// not receive an envelope
				ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
						"#empty_envelope#");
			} else {
				// extract the soap envelope namespace
				String ns = message.substring(a + 1, b);

				// start and end of the payload
				int start = message.indexOf(">", b) + 1;
				int end = message.indexOf("</" + ns + ":Body", start);

				if (start == -1 || end == -1) {
					// this condition is not likely
					ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
							"#empty_payload#");
				} else {
					// extract the payload from the message
					String payload = message.substring(start, end);

					if (payload == null || payload.length() == 0) {
						// empty payload received
						ret = Constants.TWOWAY_MSG_RESPONSE.replace(
								"test_message", "#empty_payload#");

					} else if (payload.indexOf(":twoWay") == -1) {
						// payload does not contain a valid message
						ret = Constants.TWOWAY_MSG_RESPONSE.replace(
								"test_message", "#invalid_payload#");
					}
				} // if (start == -1 || end == -1)
			} // if (b == -1)

		} else if (mode == Service.Mode.PAYLOAD
				|| binding == HTTPBinding.HTTP_BINDING) {

			if (message == null || message.length() == 0) {
				// empty payload received
				ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
						"#empty_payload#");

			} else if (message.indexOf(":twoWay") == -1) {
				// payload does not contain a valid message
				ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
						"#invalid_payload#");
			}
		}

		return ret;
	}

	/**
	 * Verify that the correct envelope has been received
	 * 
	 * @param message
	 * @param ctxt
	 * @return
	 */
	private String checkEnvelope(String message, WebServiceContext ctxt) {

		String ret = null;
		if (mode == Service.Mode.MESSAGE) {
			if (binding == javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING) {
				int env = message.indexOf(":Envelope");
				int bnd = message
						.indexOf(javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING);
				int bod = message.indexOf(":Body");

				if (env > 0 && bnd > env && bod > bnd) {
					ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
							"#soap11_binding#");
				} else {
					ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
							"#incorrect_binding#");
				}

			} else if (binding == javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING) {
				int env = message.indexOf(":Envelope");
				int bnd = message
						.indexOf(javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING);
				int bod = message.indexOf(":Body");

				if (env > 0 && bnd > env && bod > bnd) {
					ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
							"#soap12_binding#");
				} else {
					ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
							"#incorrect_binding#");
				}
			} else if ((binding == HTTPBinding.HTTP_BINDING)) {
				int env = message.indexOf(":Envelope");
				int bod = message.indexOf(":Body");

				if (env == -1 && bod == -1) {
					ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
							"#http_binding#");
				} else {
					ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
							"#incorrect_binding#");
				}
			} else {
				ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
						"#incorrect_binding#");
			}
		} else {
			ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
					"#payload_mode_mesage#");
		}

		return ret;
	}

	/**
	 * Envelope the payload as necesasry. Based on binding and mode values.
	 * 
	 * @param message
	 *            payload to envelope
	 * @return
	 */
	private String envelope(String message) {
		String hdr = "";
		String trl = "";

		if (!binding.equals(HTTPBinding.HTTP_BINDING)) {
			if (mode == Service.Mode.MESSAGE) {
				if (binding.equals(SOAPBinding.SOAP11HTTP_BINDING)) {
					hdr = Constants.SOAP11_HEADER;
					trl = Constants.SOAP11_TRAILER;
				} else {
					hdr = Constants.SOAP12_HEADER;
					trl = Constants.SOAP12_TRAILER;
				}
			}
		}
		return hdr + message + trl;
	}

}
