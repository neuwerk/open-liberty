//
// @(#) 1.8 autoFVT/src/jaxws/provider/common/Constants.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 10/8/09 14:52:17 [8/8/12 06:54:49]
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
// 10/02/06 sedov       394989          Updated toDOMSource and toSOAPMessage
// 12/06/06 sedov       408880          Refactored, changed endpoints
// 12/18/06 sedov       411678          toJAXBObject will now dynamically load ObjectFactory
// 04/23/07 sedov       433838          Added logging/tracing
package jaxws.provider.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class Constants {

	// substitution variables
	private static final String SERVER = "@HOST@:@PORT@";	
	
	public static final String WSDL_NAMESPACE = "http://common.wsfvt.provider.jaxws";

	public static final QName SERVICE_QNAME = new QName(WSDL_NAMESPACE,
			"ProviderService");
	public static final QName PORT_QNAME = new QName(WSDL_NAMESPACE,
			"ProviderPort");

	// substitution variables
	public static final String WSDL_SUFFIX = "?wsdl";
	
	// base location
	public static final String BASE_ENDPOINT = "http://" + SERVER;

	public static final String STRING_SOAP11_ADDRESS = BASE_ENDPOINT + "/jwpv.string/services/SOAP11StringService";
	public static final String STRING_SOAP11MESSAGE_ADDRESS = BASE_ENDPOINT + "/jwpv.string/services/SOAP11StringMessageService";
	public static final String STRING_SOAP12_ADDRESS = BASE_ENDPOINT + "/jwpv.string/services/SOAP12StringService";
	public static final String STRING_SOAP12MESSAGE_ADDRESS = BASE_ENDPOINT + "/jwpv.string/services/SOAP12StringMessageService";

	public static final String SOAPMESSAGE_SOAP11_ADDRESS = BASE_ENDPOINT + "/jwpv.soapmsg/services/SOAP11SOAPMessageService";
	public static final String SOAPMESSAGE_SOAP12_ADDRESS = BASE_ENDPOINT + "/jwpv.soapmsg/services/SOAP12SOAPMessageService";
	
	public static final String JAXBSOURCE_SOAP11_ADDRESS = BASE_ENDPOINT + "/jwpv.source/services/SOAP11JAXBSourceService";
	public static final String SAXSOURCE_SOAP11_ADDRESS = BASE_ENDPOINT + "/jwpv.source/services/SOAP11SAXSourceService";
	public static final String DOMSOURCE_SOAP11_ADDRESS = BASE_ENDPOINT + "/jwpv.source/services/SOAP11DOMSourceService";
	public static final String STREAMSOURCE_SOAP11_ADDRESS = BASE_ENDPOINT + "/jwpv.source/services/SOAP11StreamSourceService";
	public static final String SOURCE_SOAP11MESSAGE_ADDRESS = BASE_ENDPOINT + "/jwpv.source/services/SOAP11SourceMessageService";
	public static final String SOURCE_SOAP11_ADDRESS = STREAMSOURCE_SOAP11_ADDRESS;
	public static final String SOURCE_SOAP12_ADDRESS = BASE_ENDPOINT + "/jwpv.source/services/SOAP12SourceService";
	public static final String SOURCE_SOAP12MESSAGE_ADDRESS = BASE_ENDPOINT + "/jwpv.source/services/SOAP12SourceMessageService";
	
	public static final String RPCLIT_ENDPOINT_ADDRESS = BASE_ENDPOINT + "/jwpv.rpclit/services/SOAP11RpcLitService";	
	
	public static final String MESSAGECONTEXT_SOAP11_ADDRESS = BASE_ENDPOINT + "/jwpv.msgctxt/services/MessageContextService";;
	
		
	// HTTP Providers
	//public static final String STRING_HTTP_ADDRESS = BASE_ENDPOINT + "HTTPStringService";
	//public static final String SOURCE_HTTP_ADDRESS = BASE_ENDPOINT + "HTTPSourceService";
	//public static final String DATASOURCE_HTTP_ADDRESS = BASE_ENDPOINT + "HTTPDataSourceService";

	// Ping provider
	//public static final String PING_SOAP11_ADDRESS = BASE_ENDPOINT + "PingPortService";
	
	// MessageContext provider
	//public static final String MESSAGECONTEXT_SOAP11_ADDRESS = BASE_ENDPOINT + "MessageContextService";
	
	// SOAP 1.1 Envelope Header and Trailer
	public static final String SOAP11_HEADER = "<soap:Envelope  xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>";

	public static final String SOAP11_TRAILER = "</soap:Body></soap:Envelope>";

	// SOAP 1.2 Envelope Header and Trailer
	public static final String SOAP12_HEADER = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body>";

	public static final String SOAP12_TRAILER = "</soap:Body></soap:Envelope>";

	// envelope namespace
	public static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope";

	public static final String SOAP12_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";	
	
	// Simple server ping requests
	public static final String TWOWAY_MSG = "<ns1:twoWay xmlns:ns1=\"" + WSDL_NAMESPACE +  "\"><value>test_message</value></ns1:twoWay>";

	public static final String TWOWAY_MSG_RESPONSE = "<ns1:twoWayResponse xmlns:ns1=\"" + WSDL_NAMESPACE +  "\"><value>test_message</value></ns1:twoWayResponse>";

	public static final String TWOWAY_MSG_EXCEPTION = "<ns5:twoWayException xmlns:ns5=\"" + WSDL_NAMESPACE +  "\">test_message</ns5:twoWayException>";

	public static final String THE_RETURN_NULL_INT = "-41";
	
	/**
	 * Adapter method used to convert any type of Source to a String
	 * 
	 * @param input
	 * @return
	 */
	public static String toString(Source input) {

		System.out.println("Constants.toString (Source: " + input + ")");
		
		if (input == null)
			return null;

		StringWriter writer = new StringWriter();
		Transformer trasformer;
		try {
			trasformer = TransformerFactory.newInstance().newTransformer();
			Result result = new StreamResult(writer);
			trasformer.transform(input, result);
		} catch (Exception e) {
			System.out.println("Constants.toString Exception: " + e);
			e.printStackTrace(System.out);
			
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

		System.out.println("Constants.toString (SOAPMessage: " + input + ")");
		
		if (input == null)
			return null;

		Source result = null;
		try {
			result = input.getSOAPPart().getContent();
		} catch (SOAPException e) {
			System.out.println("Constants.toString Exception: " + e);
			e.printStackTrace(System.out);
			return null;
		}

		return toString(result);
	}

	/**
	 * Adapter method used to convert any type of JAXB Object to a String
	 * 
	 * @TODO uncomment this later 
	 * @param input
	 * @return
	 */
	public static String toString(Object input) {
		
		System.out.println("Constants.toString (Object: " + input + ")");
		
		if (input == null) return null;
		
		// this only applies to JABSource tests
		// so we dynamically detect the presense of the ObjectFatory
		Class objFactory = null;
		try {
			objFactory = ClassLoader.getSystemClassLoader().loadClass("jaxws.provider.wsfvt.common.ObjectFactory");
		} catch (ClassNotFoundException cnfe){
			objFactory = null;
			System.out.println("Constants.toString objFactory=null");
		}		
		
		String result = null;
		try {
			// Marshall message PAYLOAD
			StringWriter wr = new StringWriter();
			JAXBContext jc = JAXBContext.newInstance(objFactory);
			Marshaller m = jc.createMarshaller();
			m.marshal(input, wr);

			result = wr.toString();

		} catch (Exception e) {
			System.out.println("Constants.toString Exception: " + e);
			e.printStackTrace(System.out);
			return null;
		}

		return result;
		
		//*/ return null;
	}

	/**
	 * Build Stream Source object
	 * 
	 * @param src
	 * @return
	 */
	public static StreamSource toStreamSource(String src) {

		System.out.println("Constants.toStreamSource (String: " + src + ")");

		/*The following lines are added to pass jaxws.provider.source.wsfvt.test.SourceProviderTest.testSource_returnEmpty() and 
		 * jaxws.provider.source.wsfvt.test.SourceProviderTest.testSource_returnNull().
		 * 
		 * Per spec JSR224: An empty Source payload can be used in payload mode to send a response with no payload. An empty source
		 * can be constructed using zero-argument default constructors of DOMSource, SAXSource, and Stream-Source.		 
		 */
		
		/*Start of jaxws.provider.source.wsfvt.test.SourceProviderTest.testSource_returnEmpty() and 
		 * jaxws.provider.source.wsfvt.test.SourceProviderTest.testSource_returnNull()*/
		
//		if (src == null)
//			return null;
		
		if (src == null || src.isEmpty()) {
			return new StreamSource();
		}
		/*End of jaxws.provider.source.wsfvt.test.SourceProviderTest.testSource_returnEmpty() and 
		 * jaxws.provider.source.wsfvt.test.SourceProviderTest.testSource_returnNull()*/
		
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
		
		System.out.println("Constants.toDOMSource (String: " + message + ")");
		
		if (message == null) return null;

		DOMSource srcStream = null;

		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(message
					.getBytes());
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true);

			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			Document domTree = domBuilder.parse(stream);
			srcStream = new DOMSource(domTree);
		} catch (Exception e) {
			System.out.println("Constants.toDOMSource Exception: " + e);
			e.printStackTrace(System.out);
			return null;
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

		System.out.println("Constants.toSOAPMessage (String: " + msgString + ")");
		
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
			System.out.println("Constants.toSOAPMessage Exception: " + e);
			e.printStackTrace(System.out);
			return null;
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

		System.out.println("Constants.toSAXSource (String: " + message + ")");
		
		if (message == null) return null;
		
		ByteArrayInputStream stream = new ByteArrayInputStream(message
				.getBytes());
		SAXSource srcStream = new SAXSource(new InputSource(stream));

		return srcStream;
	}

	/**
	 * Build DOMSource object from a string
	 * 
	 * @TODO uncomment later
	 * @param message
	 * @return
	 */
	public static Object toJAXBObject(String message) {
		
		System.out.println("Constants.toJAXBObject (String: " + message + ")");
		
		if (message == null)
			return null;

		// this only applies to JABSource tests
		// so we dynamically detect the presense of the ObjectFatory
		Class objFactory = null;
		try {
			objFactory = ClassLoader.getSystemClassLoader().loadClass("jaxws.provider.wsfvt.common.ObjectFactory");
		} catch (ClassNotFoundException cnfe){
			objFactory = null;
			
			System.out.println("Constants.toJAXBObject objFactory=null");
		}
		
		Object jaxbObject = null;
		try {

			boolean hasEnvelope = message.indexOf(":Envelope") != -1;

			if (hasEnvelope)
				message = message.substring(SOAP11_HEADER.length(), message
						.length()
						- SOAP11_TRAILER.length());

			ByteArrayInputStream input = new ByteArrayInputStream(message
					.getBytes());
			JAXBContext jc = JAXBContext.newInstance(objFactory);
			Unmarshaller u = jc.createUnmarshaller();
			jaxbObject = u.unmarshal(input);
		} catch (Exception e) {
			System.out.println("Constants.toJAXBObject Exception: " + e);
			e.printStackTrace(System.out);
			return null;
		}

		return jaxbObject; //*/
		//return null;
	}

	/**
	 * Extract a value from the input string such as #value#
	 * 
	 * @param message
	 * @return
	 */
	public static String getValueBetweenHashes(String message) {

		System.out.println("Constants.getValueBetweenHashes (" + message + ")");
		
		if (message == null || message.length() == 0)
			return null;

		int i = message.indexOf("#") + 1;
		int p = message.indexOf("#", i);

		if (i == -1 || p == -1)
			return null;

		return message.substring(i, p);
	}	
}
