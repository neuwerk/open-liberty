//
// @(#) 1.14 autoFVT/src/jaxws/dispatch/wsfvt/common/Constants.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 2/5/07 09:30:22 [8/8/12 06:54:46]
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
// 08/30/06 sedov    387768             Fixed issues with invokeOneWay and SOAPMessage
// 09/20/06 sedov    383139.1           Update toDOMSource to be namespace aware
// 12/01/06 sedov    408880             Fixed context root error
// 01/20/07 sedov    415799             Added FAULT_TEXT constant
// 01/25/07 sedov    416630             Added envelope checking
// 01/28/07 sedov    417317             Remove dependency on AssertionFailedError
// 01/30/07 sedov    417716             Fixed UnmarshalException for MSG JAXBCtxt dsp
// 02/05/07 sedov    418799             Added SERVER default value
//
package jaxws.dispatch.wsfvt.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.activation.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class Constants {

	// substitution variables
	private static final String SERVER = getServer("@HOST@:@PORT@");
	
	public static final String WSDL_NAMESPACE = "http://common.wsfvt.dispatch.jaxws";
	public static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope";
	public static final String SOAP12_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";

	public static final String FAULT_TEXT = "Sample Fault Text";
	
	public static final QName SERVICE_QNAME = new QName(WSDL_NAMESPACE,
			"DispatchSOAP11Service");

	public static final QName PORT_QNAME = new QName(WSDL_NAMESPACE,
			"DispatchSOAP11Port");
	
	public static final String BASE_ENDPOINT = "http://" + SERVER
			+ "/jw.dispatch/";

	// Different endpoint addresses
	public static final String INVALID_ENDPOINT_ADDRESS = "http://invalid.ibm.com:41/definitely_not";

	public static final String SOAP11_ENDPOINT_ADDRESS = BASE_ENDPOINT
			+ "services/DispatchSOAP11";

	public static final String SOAP12_ENDPOINT_ADDRESS = BASE_ENDPOINT
			+ "services/DispatchSOAP12";

	public static final String HTTP_ENDPOINT_ADDRESS = BASE_ENDPOINT
			+ "services/DispatchHTTP";

	public static final String MIME_ENDPOINT_ADDRESS = BASE_ENDPOINT
			+ "services/DispatchDataSource";

	public static final String SECURE_ENDPOINT_ADDRESS = BASE_ENDPOINT
			+ "services/DispatchSecure";

	public static final String SIMPLE_ENDPOINT_ADDRESS = BASE_ENDPOINT
			+ "services/DispatchSimple";

	public static final String WSDL_LOCATION = SOAP11_ENDPOINT_ADDRESS
			+ "?wsdl";

	// SOAP 1.1 Envelope Header and Trailer
	public static final String SOAP11_HEADER = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>";
	public static final String SOAP11_TRAILER = "</soap:Body></soap:Envelope>";

	// SOAP 1.2 Envelope Header and Trailer
	public static final String SOAP12_HEADER = "<soapenv:Envelope xmlns:soapenv='http://www.w3.org/2003/05/soap-envelope'><soapenv:Body>";
	public static final String SOAP12_TRAILER = "</soapenv:Body></soapenv:Envelope>";

	// Simple server ping requests
	public static final String ONEWAY_MSG = "<ns1:oneWay xmlns:ns1='"
			+ WSDL_NAMESPACE + "'><value>test_message</value></ns1:oneWay>";

	public static final String TWOWAY_MSG = "<ns1:twoWay xmlns:ns1='"
			+ WSDL_NAMESPACE + "'><value>test_message</value></ns1:twoWay>";

	// Requests for server to throw an exception
	public static final String ONEWAY_MSG_EXCEPTION = "<ns1:oneWayException xmlns:ns1='"
			+ WSDL_NAMESPACE
			+ "'><value>test_message</value></ns1:oneWayException>";

	public static final String TWOWAY_MSG_EXCEPTION = "<ns1:twoWayException xmlns:ns1='"
			+ WSDL_NAMESPACE + "'>test_message</ns1:twoWayException>";

	// Template response messages
	public static final String TWOWAY_MSG_RESPONSE = "<ns1:twoWayResponse xmlns:ns1='"
			+ WSDL_NAMESPACE
			+ "'><value>test_message</value></ns1:twoWayResponse>";

	// how long the server should wait before sending a response. Used for
	// timeout tests
	public static final long SERVER_SLEEP_SEC = 60 * 5; // 5 minutes

	// how long the client should wait for invokeAsync operations to
	// come back before killing the thread
	public static final int CLIENT_MAX_SLEEP_SEC = 120; // 2 minutes

	// debugging output to System.out
	//public static final boolean DEBUG = false;

	// enable return type checking. e.g., if invokeed with DOMSource verify that
	// returned type is also a DOMSource
	public static final boolean ENABLE_RETURNTYPE_CHECKING = false;

	/**
	 * Adapter method used to convert Source to a String
	 * 
	 * @param input
	 * @return
	 */
	public static String toString(Source input) {

		if (input == null) return null;

		StringWriter writer = new StringWriter();
		Transformer trasformer;
		try {
			trasformer = TransformerFactory.newInstance().newTransformer();
			Result result = new StreamResult(writer);
			trasformer.transform(input, result);
		} catch (Exception e) {
			return null;
		}

		return writer.getBuffer().toString();
	}

	/**
	 * Wrapper code in case we are running in eclipse
	 * @param server
	 * @return
	 */
	private static String getServer(String server) {
		if (server.indexOf('@') != -1)
			server="localhost:8080";
		
		System.out.println("Constants.getServer=" + server);
		
		return server;
	}

	/**
	 * Adapter method used to convert SOAPMessage to a String
	 * 
	 * @param input
	 * @return
	 */
	public static String toString(SOAPMessage input) {

		if (input == null) return null;

		Source result = null;
		try {
			result = input.getSOAPPart().getContent();
		} catch (SOAPException e) {
			e.printStackTrace();
		}

		return toString(result);
	}

	/**
	 * Adapter method used to convert any type of DataSource to a String
	 * 
	 * @param input
	 * @return
	 */
	public static String toString(DataSource input) {

		if (input == null) return null;

		StringBuffer buffer = new StringBuffer();
		InputStream is;

		try {
			is = input.getInputStream();
			int b;
			while ((b = is.read()) != -1) {
				buffer.append((char) b);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}

	/**
	 * Adapter method used to convert any type of JAXB Object to a String
	 * 
	 * @param input
	 * @return
	 */
	public static String toString(Object input) {

		if (input == null) return null;

		System.out.println("Constants.toString(" + input + ")");
		Object type = input;
		
		if (input instanceof javax.xml.bind.JAXBElement){
			type = ((javax.xml.bind.JAXBElement) input).getValue();
		}
		
		String result = null;
		try {
			// Marshall message PAYLOAD
			StringWriter wr = new StringWriter();
			JAXBContext jc = null;

			// handle the different possible types of SOAP Envelopes and
			// Service.MODEs
			if (type instanceof jaxws.dispatch.wsfvt.common.soap11.Envelope) {
				// message is a SOAP11/MESSAGE mode
				jc = getJAXBContextSoap11();
			} else if (type instanceof jaxws.dispatch.wsfvt.common.soap12.Envelope) {
				// message is a SOAP12/MESSAGE mode
				jc = getJAXBContextSoap12();
			} else {
				// message is a HTTP or SOAP1.x/PAYLOAD
				jc = getJAXBContextPayload();
			}

			Marshaller m = jc.createMarshaller();
			m.marshal(input, wr);

			result = wr.toString();

		} catch (Exception e) {
			throw new WebServiceException(e);
		}

		return result;

	}

	/**
	 * Build Stream Source object
	 * 
	 * @param src
	 * @return
	 */
	public static StreamSource toStreamSource(String src) {

		if (src == null) return null;

		StreamSource ret = null;
		InputStream stream = new ByteArrayInputStream(src.getBytes());
		ret = new StreamSource((InputStream) stream);

		return ret;
	}

	/**
	 * Method used to convert Strings to SOAPMessages. We will use a detection
	 * routine to see if SOAP 1.2 support is present. If it is we will enable
	 * dynamic protocol selection, otherwise we will default to SOAP 1.1 (SAAJ
	 * 1.2)
	 * 
	 * @param msgString
	 * @return
	 */
	public static SOAPMessage toSOAPMessage(String msgString) {

		if (msgString == null) return null;

		SOAPMessage message = null;
		try {

			MessageFactory factory = null;

			// Force the usage of specific MesasgeFactories
			if (msgString.indexOf(SOAP11_NAMESPACE) >= 0) {
				factory = MessageFactory
						.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			} else {
				factory = MessageFactory
						.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
			}

			message = factory.createMessage();
			message.getSOAPPart().setContent(
					(Source) new StreamSource(new StringReader(msgString)));
			message.saveChanges();
		} catch (SOAPException e) {
			System.out.println("toSOAPMessage Exception encountered: " + e);
			e.printStackTrace();
		}

		return message;
	}

	/**
	 * Build DOMSource object from a string
	 * 
	 * @param message
	 * @return
	 */
	public static DOMSource toDOMSource(String message, boolean isNSAware) {

		if (message == null) return null;

		DOMSource srcStream = null;

		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(message
					.getBytes());
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(isNSAware);

			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			Document domTree = domBuilder.parse(stream);
			srcStream = new DOMSource(domTree);
		} catch (Exception e) {
			System.out.println("toDOMSource Exception encountered: " + e);
			e.printStackTrace();
		}

		return srcStream;
	}

	/**
	 * Build DOMSource object from a string. This is a convenience method
	 * 
	 * @param message
	 * @return
	 */
	public static DOMSource toDOMSource(String message) {
		return toDOMSource(message, true);
	}

	/**
	 * Build DOMSource object from a string
	 * 
	 * @param message
	 * @return
	 */
	public static Object toJAXBObject(String message) {

		if (message == null) return null;

		Object jaxbObject = null;
		try {
			// make JAXBContext aware of SOAP 1.1 schema if we are
			// passing in a MESSAGE mode message
			JAXBContext jc = null;
			if (message.indexOf(":Envelope") > 0) {
				// handle SOAP1.1 vs 1.2 envelopes
				if (message.indexOf(SOAP11_NAMESPACE) > 0) {
					jc = getJAXBContextSoap11();
				} else {
					jc = getJAXBContextSoap12();
				}
			} else {
				jc = getJAXBContextPayload();
			}

			// UnMarshall message
			ByteArrayInputStream input = new ByteArrayInputStream(message
					.getBytes());

			Unmarshaller u = jc.createUnmarshaller();
			jaxbObject = u.unmarshal(input);
		} catch (Exception e) {
			throw new WebServiceException(e);
		}

		return jaxbObject;

	}

	/**
	 * Build SAXSource object from a string
	 * 
	 * @param message
	 * @return
	 */
	public static SAXSource toSAXSource(String message) {

		if (message == null) return null;

		ByteArrayInputStream stream = new ByteArrayInputStream(message
				.getBytes());
		SAXSource srcStream = new SAXSource(new InputSource(stream));

		return srcStream;
	}

	/**
	 * Resolve SOAP Binding into correct name space
	 * 
	 * @param binding
	 * @return
	 */
	public static String getSoapNS(String binding) {
		if (binding == SOAPBinding.SOAP11HTTP_BINDING) {
			return SOAP11_NAMESPACE;
		} else if (binding == SOAPBinding.SOAP12HTTP_BINDING) {
			return SOAP12_NAMESPACE;
		} else
			return null;
	}

	/**
	 * Build JAXBSource object from a string
	 * 
	 * @param message
	 * @return
	 */
	public static JAXBSource toJAXBSource(String message) {

		if (message == null) return null;

		JAXBSource jbsrc = null;
		try {
			JAXBContext jc = null;

			// make JAXBContext aware of SOAP 1.1 schema if we are
			// passing in a MESSAGE mode message
			if (message.indexOf(":Envelope") > 0) {

				// handle SOAP1.1 vs 1.2 envelopes
				if (message.indexOf(SOAP11_NAMESPACE) > 0) {
					jc = getJAXBContextSoap11();
				} else {
					jc = getJAXBContextSoap12();
				}
			} else {
				jc = getJAXBContextPayload();
			}

			// unmarshall request into JAXB Object
			InputStream is = new ByteArrayInputStream(message.getBytes());
			Unmarshaller u = jc.createUnmarshaller();
			Object obj = u.unmarshal(is);

			// convert object into JAXBSource
			jbsrc = new JAXBSource(jc.createMarshaller(), obj);
		} catch (Exception e) {
			throw new WebServiceException(e);
		}

		return jbsrc;
	}

	/**
	 * Create a JAXBContext useful for Payload mode messages or HTTP Binding
	 * 
	 * @return
	 * @throws Exception
	 */
	public static JAXBContext getJAXBContextPayload() throws Exception {
		System.out.println("getJAXBContextPayload()");
		return JAXBContext
				.newInstance(jaxws.dispatch.wsfvt.common.ObjectFactory.class);
	}

	/**
	 * Create a JAXBContext useful for SOAP11 messages in MESSAGE mode
	 * 
	 * @return
	 * @throws Exception
	 */
	public static JAXBContext getJAXBContextSoap11() throws Exception {
		System.out.println("getJAXBContextSoap11()");
		return JAXBContext.newInstance(new Class[] {
				jaxws.dispatch.wsfvt.common.soap11.ObjectFactory.class,
				jaxws.dispatch.wsfvt.common.ObjectFactory.class });
	}

	/**
	 * Create a JAXBContext useful for SOAP12 messages in MESSAGE mode
	 * 
	 * @return
	 * @throws Exception
	 */
	public static JAXBContext getJAXBContextSoap12() throws Exception {
		System.out.println("getJAXBContextSoap12()");
		return JAXBContext.newInstance(new Class[] {
				jaxws.dispatch.wsfvt.common.soap12.ObjectFactory.class,
				jaxws.dispatch.wsfvt.common.ObjectFactory.class });
	}

	public static void logRequest(String req) {
		System.out.println("------------Request");
		System.out.println(req);
	}

	public static void testEnvelope(String res, boolean isMessageMode) throws Exception {
		System.out.println("------------ Response");
		System.out.println(res);
		
		if (isMessageMode){
			boolean isEnvelope = res.indexOf(":Envelope") != -1;
			boolean isBody = res.indexOf(":Body") != -1;
			boolean isSoap11NS = res.indexOf(SOAP11_NAMESPACE) != -1;
			boolean isSoap12NS = res.indexOf(SOAP12_NAMESPACE) != -1;
			
			System.out.println("------------ Checking envelope");
			System.out.println("found :Envelope = " + isEnvelope);
			System.out.println("found :Body = " + isBody);
			System.out.println("found SOAP1x NS = " + (isSoap11NS || isSoap12NS));
			
			if (!isEnvelope)
				throw new Exception("AsserionFailed: ':Envelope' was not found in response");
			
			if (!isBody)
				throw new Exception("AsserionFailed: ':Body' was not found in response");
			
			if (!isSoap11NS && !isSoap12NS)
				throw new Exception("AsserionFailed: SOAP 1.x NS was not found in response");
		}
		
	}
	
}
