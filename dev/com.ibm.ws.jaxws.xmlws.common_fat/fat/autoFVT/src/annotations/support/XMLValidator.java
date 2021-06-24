package annotations.support;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A JAX-P based XML validator with convenience methods for WSDL and XML validation 
 */
public class XMLValidator {

	private static final String WSDL11_SCHEMA = "http://schemas.xmlsoap.org/wsdl/";
	private static final String XML11_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	/**
	 * Validate a WSDL file based on WSDL 1.1 Schema. Note that this class
	 * relies on the publicly available schema. So this check will fail if used
	 * on a computer without internet access
	 * 
	 * @param file
	 *            file name of the WSDL document to be validated
	 * @return true if document is valid
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public boolean validateWSDL11(String file)
			throws ParserConfigurationException, SAXException, IOException {

		validateSchema(WSDL11_SCHEMA, (new java.io.File(file)).toURL().toString());

		return true;
	}

	/**
	 * Validate a WSDL file based on WSDL 1.1 Schema. Note that this class
	 * relies on the publicly available schema. So this check will fail if used
	 * on a computer without internet access
	 * 
	 * @param file
	 *            file handle to the WSDL document to be validated
	 * @return true if document is valid
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public boolean validateWSDL11(java.io.File file)
			throws ParserConfigurationException, SAXException, IOException {

		validateSchema(WSDL11_SCHEMA, file.toURL().toString());

		return true;
	}

	/**
	 * Validate a XML file based on XML 1.1 Schema. Note that this class
	 * relies on the publicly available schema. So this check will fail if used
	 * on a computer without internet access
	 * 
	 * @param file
	 *            file name of the XML document to be validated
	 * @return true if document is valid
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public boolean validateXML11(String file)
			throws ParserConfigurationException, SAXException, IOException {

		validateSchema(XML11_SCHEMA, (new java.io.File(file)).toURL().toString());

		return true;
	}

	/**
	 * Validate a XML file based on XML 1.1 Schema. Note that this class
	 * relies on the publicly available schema. So this check will fail if used
	 * on a computer without internet access
	 * 
	 * @param file
	 *            file handle to the XML document to be validated
	 * @return true if document is valid
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public boolean validateXML11(java.io.File file)
			throws ParserConfigurationException, SAXException, IOException {

		validateSchema(XML11_SCHEMA, file.toURL().toString());

		return true;
	}
	
	/**
	 * Validate any XML file based on a specified Schema. 
	 * 
	 * @param file
	 * @param schema
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public boolean validateAnyXML(URL file, URL schema)
		throws ParserConfigurationException, SAXException, IOException {
		
		validateSchema(schema.toString(), file.toString());
		return true;
	}
	
	/**
	 * A generic validator for XML files, uses a specified schema to check validity
	 * @param SchemaUrl
	 * @param XmlDocumentUrl
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void validateSchema(String SchemaUrl, String XmlDocumentUrl)
			throws ParserConfigurationException, SAXException, IOException {
		// System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
		// "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		factory.setAttribute(
				"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
				"http://www.w3.org/2001/XMLSchema");
		factory.setAttribute(
				"http://java.sun.com/xml/jaxp/properties/schemaSource",
				SchemaUrl);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Validator handler = new Validator();
		builder.setErrorHandler(handler);

		// parse the document...any exceptions will be reported to the handler
		// class, which will in turn make the exception available via
		// saxParseException property
		builder.parse(XmlDocumentUrl);

		// throw the parser exception if one was encountered
		if (handler.validationError == true)
			throw handler.saxParseException;
	}

	/**
	 * SAX Error event handler class
	 */
	private class Validator extends DefaultHandler {
		public boolean validationError = false;

		public SAXParseException saxParseException = null;

		public void error(SAXParseException exception) throws SAXException {
			validationError = true;
			saxParseException = exception;
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			validationError = true;
			saxParseException = exception;
		}

		public void warning(SAXParseException exception) throws SAXException {
		}
	}
}
