package annotations.webserviceprovider.server;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XMLUtils {

	public static DOMSource createDOMSourceFromString(String str) {
		DOMSource response = new DOMSource();
		DocumentBuilderFactory documentFacory = DocumentBuilderFactory
				.newInstance();
		documentFacory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentFacory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		Document doc;
		try {
			doc = documentBuilder.parse(new InputSource(
					new ByteArrayInputStream(str.getBytes("UTF-8"))));
		} catch (Exception e) {
			throw new RuntimeException("error parse: " + str, e);
		}
		response.setNode(doc);
		return response;
	}

	public static String getContentFromDOMSource(Source source) {
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new RuntimeException("getContentFromDOMSource error ", e);
		}
		return writer.toString();
	}

}
