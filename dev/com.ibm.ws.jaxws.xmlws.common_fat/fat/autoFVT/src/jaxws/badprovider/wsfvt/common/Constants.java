//
// @(#) 1.6 autoFVT/src/jaxws/badprovider/wsfvt/common/Constants.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/29/07 17:12:42 [8/8/12 06:55:20]
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
// 09/26/06 sedov       D393143         Added Admin security variables
// 11/15/06 sedov       D404343         Removed dependency on AdminCommandFactory
// 01/29/07 sedov       D417317         Added PROFILE_DIR
//

package jaxws.badprovider.wsfvt.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
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

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class Constants {

	// substitution variables
	
	public static final String WAS_HOST_NAME = "@HOST@";
	public static final String WAS_HTTP_PORT = "@HTTP_PORT@";
	public static final String WAS_SOAP_PORT = "@SOAP_PORT@";
	public static final String WAS_SERVER_NAME = "@SERVER_NAME@";
	
	public static final String PROFILE_DIR = "@PROFILE_DIR@";
	
	public static final String SSL_CERT_PATH = "@SEC_SSL_CERT_PATH@";
	public static final String SEC_USERNAME = "@SEC_USERNAME@";
	public static final String SEC_PASSWORD = "@SEC_PASSWORD@";
	public static final String SEC_ENABLED = "@SEC_ENABLED@";
	
	private static final String SERVER = WAS_HOST_NAME + ":" + WAS_HTTP_PORT;

	// some useful names
	public static final String WSDL_NAMESPACE = "http://common.wsfvt.badprovider.jaxws";

	public static final QName SERVICE_QNAME = new QName(WSDL_NAMESPACE,
			"ProviderService");
	public static final QName PORT_QNAME = new QName(WSDL_NAMESPACE,
			"ProviderPort");	
	
	// Bad providers
	public static final String DEFAULT_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.Defaults/services/DefaultsService";
	public static final String PRIVATE_CTOR_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.PrivateConstructor/services/PrivateConstructorService";
	public static final String NOANNOTATION_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.UntypedProvider/services/UntypedProviderService";
	public static final String UNTYPED_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.Defaults/services/DefaultsService";
	public static final String UNSUPPORTED_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.UnsupportedType/services/UnsupportedTypeService";
	public static final String BADBINDING_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.InvalidBinding/services/InvalidBindingService";
	public static final String NonProviderWSP_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.NonProviderWSP/services/NonProviderWSPService";
	public static final String WSPandWS_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.WSPandWS/services/WSPandWSService";
	public static final String WSProvider_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.WSProvider/services/WSProviderService";
	public static final String S11PayloadSoapMessage_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.SOAP11PayloadSoapMessage/services/SOAP11PayloadSoapMessageService";
	public static final String S12PayloadSoapMessage_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.SOAP12PayloadSoapMessage/services/SOAP12PayloadSoapMessageService";
	public static final String XMLSoapMessage_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.XMLMessageSoapMessage/services/XMLMessageSoapMessageService";
	public static final String S11MessageDataSource_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.SOAP11MessageDataSource/services/SOAP11MessageDataSourceService";
	public static final String S12PayloadDataSource_ADDRESS = "http://" + SERVER + "/jaxws.badprovider.SOAP12PayloadDataSource/SOAP12PayloadDataSourceService";
	
	// Simple server ping requests
	public static final String TWOWAY_MSG = "<ns1:twoWay xmlns:ns1=\"" + WSDL_NAMESPACE +  "\"><value>test_message</value></ns1:twoWay>";

	public static final String TWOWAY_MSG_RESPONSE = "<ns1:twoWayResponse xmlns:ns1=\"" + WSDL_NAMESPACE +  "\"><value>test_message</value></ns1:twoWayResponse>";

	//public static final String TWOWAY_MSG_EXCEPTION = "<ns5:twoWayException xmlns:ns5=\"" + WSDL_NAMESPACE +  "\">test_message</ns5:twoWayException>";

	//public static final String THE_RETURN_NULL_INT = "-41";

	// max amount of time to wait for log file to be
	// returned
	public static final int SOCKET_READ_TIMEOUT = 5000;
	
	/**
	 * Adapter method used to convert any type of Source to a String
	 * 
	 * @param input
	 * @return
	 */
	public static String toString(Source input) {

		if (input == null)
			return null;

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
	 * Adapter method used to convert any type of SOAPMessage to a String
	 * 
	 * @param input
	 * @return
	 */
	public static String toString(SOAPMessage input) {

		if (input == null)
			return null;

		Source result = null;
		try {
			result = input.getSOAPPart().getContent();
		} catch (SOAPException e) {
			e.printStackTrace();
		}

		return toString(result);
	}

	/**
	 * Build Stream Source object
	 * 
	 * @param src
	 * @return
	 */
	public static StreamSource toStreamSource(String src) {

		if (src == null)
			return null;

		StreamSource ret = null;
		InputStream stream = new ByteArrayInputStream(src.getBytes());
		ret = new StreamSource((InputStream) stream);

		return ret;
	}

	/**
	 * Build DOMSource object from a string
	 * 
	 * @param message
	 * @return
	 */
	public static DOMSource toDOMSource(String message) {
		DOMSource srcStream = null;

		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(message
					.getBytes());
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
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
	 * Method used to convert Strings to SOAPMessages
	 * 
	 * @param msgString
	 * @return
	 */
	public static SOAPMessage toSOAPMessage(String msgString) {

		if (msgString == null)
			return null;

		SOAPMessage message = null;
		try {
			MessageFactory factory = MessageFactory.newInstance();
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
	 * Build SAXSource object from a string
	 * 
	 * @param message
	 * @return
	 */
	public static SAXSource toSAXSource(String message) {

		ByteArrayInputStream stream = new ByteArrayInputStream(message
				.getBytes());
		SAXSource srcStream = new SAXSource(new InputSource(stream));

		return srcStream;
	}

	/**
	 * Extract a value from the input string such as #value#
	 * 
	 * @param message
	 * @return
	 */
	public static String getValueBetweenHashes(String message) {

		if (message == null || message.length() == 0)
			return null;

		int i = message.indexOf("#") + 1;
		int p = message.indexOf("#", i);

		if (i == -1 || p == -1)
			return null;

		return message.substring(i, p);
	}
	
}
