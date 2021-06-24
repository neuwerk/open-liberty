//
// @(#) 1.7 autoFVT/src/jaxws/dispatch/wsfvt/server/SourceProvider.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/22/10 11:31:05 [8/8/12 06:54:46]
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
// 01/28/07 sedov    417317             Made error test a constant
// 02/14/07 sedov    420835             Use correct SOAPFactory for SOAPFaults
// 01/22/10 btiffany 635671             Don't log really large messages - logs overflow on z. 
//
package jaxws.dispatch.wsfvt.server;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.http.HTTPException;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import jaxws.dispatch.wsfvt.common.Constants;

/**
 * Provider delegate for invoke(Source). Actual providers are expected to remove
 * the envelope, convert the message to source and delegate processing of the
 * message to this class.
 */
public class SourceProvider {

	public boolean enableEnvelopeCheck = true;

	private String binding;

	private javax.xml.ws.Service.Mode mode;

	public SourceProvider(String binding, javax.xml.ws.Service.Mode mode) {
		this.binding = binding;
		this.mode = mode;
	}

	public Source invoke(Source input) throws WebServiceException {

		String message = Constants.toString(input);
		String operation = getOperationName(message);

		String ret = null;

		System.out.println("**************************");
		System.out.println("Date=" + (new java.util.Date()));
		System.out.println("Operation=" + operation);
        if (message.length() > 512 ){
            System.out.println("Message was large, logging truncated to 512 bytes");
            System.out.println("Message=" +message.substring(0,512));
        } else{            
            System.out.println("Message=" + message);
        }            		

		if ((ret = badMessageTests(message)) != null) {
			// we have a bad message, no further processing is needed
		} else if (operation.equals("oneWay")) {
			ret = oneWay(message);
		} else if (operation.equals("oneWayException")) {
			ret = oneWayException(message);
		} else if (operation.equals("twoWay")) {
			ret = twoWay(message);
		} else if (operation.equals("twoWayException")) {
			ret = twoWayException(message);
		} else {
			throw new WebServiceException("Invalid operation specified '"
					+ operation + "'.");
		}
        // 635671
        if (ret.length() > 512 ){
            System.out.println("Response was large, logging truncated to 512 bytes");
            System.out.println("Response=" +ret.substring(0,512));
        } else{
            System.out.println("Response=" + ret);
        }
		System.out.println("**************************");

		return Constants.toStreamSource(ret);
	}

	/**
	 * test payload for invalid messages (such as receiving an empty envelope)
	 * 
	 * @param message
	 * @return
	 */
	private String badMessageTests(String message) {

		if (message == null || message.length() == 0) {
			// received an empty mesage
			return Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
					"empty_message_received");
		} else {
			int one = message.indexOf(":oneWay");
			int two = message.indexOf(":twoWay");
			int p = Math.max(one, two);

			if (p == -1) {
				// envelope contains invalid payload
				return Constants.TWOWAY_MSG_RESPONSE.replace("test_message",
						"invalid_payload_received: " + message);
			} else {
				// envelope contains valid payload
				return null;
			}
		}
	}

	/**
	 * Throw an exception in 2 way mode. if client specifies #service-specific#
	 * then an exception of HTTPException or SOAPFaultException is thrown depending
	 * on the binding. Otherwise the generic WebServiceException is thrown
	 * 
	 * @param payload
	 * @return
	 * @throws WebServiceException
	 */
	private String twoWayException(String payload) throws WebServiceException {

		if (payload.indexOf("#service-specific#") != -1) {

			if (binding == HTTPBinding.HTTP_BINDING) {
				throw new HTTPException(500);
			} else {
				SOAPFactory fac;
				SOAPFault fault = null;
				QName qnTwoWayException = new QName(Constants.WSDL_NAMESPACE, "twoWayException");
				QName qnClientFault = new QName(Constants.getSoapNS(binding), "Client");
				
				try {
					if (binding == SOAPBinding.SOAP12HTTP_BINDING)
						fac = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
					else
						fac = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
					
					fault = fac.createFault();
					fault.setFaultActor(Constants.SOAP11_ENDPOINT_ADDRESS);
					fault.setFaultString(Constants.FAULT_TEXT);
					
					Detail detail = fault.addDetail();
					SOAPElement twoWayException = detail.addChildElement(qnTwoWayException);
					twoWayException.setTextContent(Constants.FAULT_TEXT);
					
				} catch (SOAPException e) {
					throw new WebServiceException("Unable to create fault");
				}				
				throw new SOAPFaultException(fault);
			}
		} else //@TODO uncomment when possible */
			throw new WebServiceException(Constants.FAULT_TEXT);
	}

	/**
	 * handle a twoWay message request
	 * 
	 * @param payload
	 * @return
	 * @throws WebServiceException
	 */
	private String twoWay(String payload) throws WebServiceException {
		// handle request to id self
		payload = payload.replace("#id#", this.getClass().getName());

		// handle request for server to delay the response
		if (payload.indexOf("#timeout#") != -1) {
			try {
				Thread.sleep(Constants.SERVER_SLEEP_SEC * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// change request envelope into a response
		payload = payload.replaceAll("twoWay", "twoWayResponse");
		return payload;
	}

	/**
	 * throw an exception in oneWaymode
	 * 
	 * @param payload
	 * @return
	 * @throws WebServiceException
	 */
	private String oneWayException(String payload) throws WebServiceException {
		throw new WebServiceException("OneWay Fault");
	}

	/**
	 * handle OneWay ping
	 * 
	 * @param payload
	 * @return
	 * @throws WebServiceException
	 */
	private String oneWay(String payload) throws WebServiceException {
		return null;
	}

	/**
	 * get operation name from the incoming message
	 * 
	 * @param payload
	 * @return
	 */
	private String getOperationName(String payload) {

		if (payload == null)
			return null;

		int one = payload.indexOf(":oneWay");
		int two = payload.indexOf(":twoWay");
		int p = Math.max(one, two);

		if (p == -1)
			return null;
		else
			return payload.substring(p + 1, payload.indexOf(" ", p));
	}

	/**
	 * Wrap the message in an appropriate envelope If mode is Message or Binding
	 * is HTTP then no envelope is provided
	 * 
	 * @param message
	 * @return
	 */
	private String envelope(String message) {
		String hdr = "";
		String trl = "";

		if (this.binding != HTTPBinding.HTTP_BINDING) {
			if (mode == Service.Mode.MESSAGE) {
				if (this.binding == SOAPBinding.SOAP11HTTP_BINDING) {
					hdr = Constants.SOAP11_TRAILER;
					trl = Constants.SOAP11_TRAILER;
				} else {
					hdr = Constants.SOAP12_TRAILER;
					trl = Constants.SOAP12_TRAILER;

				}
			}

		}

		return hdr + message + trl;
	}
}
