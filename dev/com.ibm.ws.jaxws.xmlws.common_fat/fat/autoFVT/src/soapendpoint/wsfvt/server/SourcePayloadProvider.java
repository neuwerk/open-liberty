package soapendpoint.wsfvt.server;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.BindingType;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.ws.soap.SOAPBinding;

/**
 * Tests that a Provider&lt;Source&gt; can process messages with a
 * SOAP_HTTP_BINDING.
 */
@WebServiceProvider
@ServiceMode(value = Service.Mode.PAYLOAD)
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class SourcePayloadProvider implements javax.xml.ws.Provider<Source> {

	public Source invoke(Source arg0) {
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element e = doc.createElement("abcd");
			e.setTextContent("hello");
			doc.appendChild(e);
			return new DOMSource(doc.getDocumentElement());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

}
