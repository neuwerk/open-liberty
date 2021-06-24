
package jaxws.attachment.server;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jaxws.attachment.server package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Request_QNAME = new QName("http://server.attachment.jaxws", "request");
    private final static QName _Response_QNAME = new QName("http://server.attachment.jaxws", "response");
    private final static QName _MimeAttachment_QNAME = new QName("http://server.attachment.jaxws", "mimeAttachment");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jaxws.attachment.server
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.attachment.jaxws", name = "request")
    public JAXBElement<String> createRequest(String value) {
        return new JAXBElement<String>(_Request_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.attachment.jaxws", name = "response")
    public JAXBElement<String> createResponse(String value) {
        return new JAXBElement<String>(_Response_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.attachment.jaxws", name = "mimeAttachment")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    public JAXBElement<byte[]> createMimeAttachment(byte[] value) {
        return new JAXBElement<byte[]>(_MimeAttachment_QNAME, byte[].class, null, ((byte[]) value));
    }

}
