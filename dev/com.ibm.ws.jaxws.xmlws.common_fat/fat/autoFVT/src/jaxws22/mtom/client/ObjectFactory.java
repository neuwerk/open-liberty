
package jaxws22.client.mtom;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jaxws22.client.mtom package. 
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

    private final static QName _Echobyte64_QNAME = new QName("http://test.com/", "echobyte64");
    private final static QName _Echobyte64Response_QNAME = new QName("http://test.com/", "echobyte64Response");
    private final static QName _Echobyte_QNAME = new QName("http://test.com/", "echobyte");
    private final static QName _EchobyteResponse_QNAME = new QName("http://test.com/", "echobyteResponse");
    private final static QName _EchobyteNoMTOM_QNAME = new QName("http://test.com/", "echobyteNoMTOM");
    private final static QName _EchobyteNoMTOMResponse_QNAME = new QName("http://test.com/", "echobyteNoMTOMResponse");
    private final static QName _Echobyte256_QNAME = new QName("http://test.com/", "echobyte256");
    private final static QName _Echobyte256Response_QNAME = new QName("http://test.com/", "echobyte256Response");
    private final static QName _Echobyte64Arg0_QNAME = new QName("", "arg0");
    private final static QName _EchobyteNoMTOMResponseReturn_QNAME = new QName("", "return");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jaxws22.client.mtom
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Echobyte64 }
     * 
     */
    public Echobyte64 createEchobyte64() {
        return new Echobyte64();
    }

    /**
     * Create an instance of {@link EchobyteNoMTOMResponse }
     * 
     */
    public EchobyteNoMTOMResponse createEchobyteNoMTOMResponse() {
        return new EchobyteNoMTOMResponse();
    }

    /**
     * Create an instance of {@link EchobyteNoMTOM }
     * 
     */
    public EchobyteNoMTOM createEchobyteNoMTOM() {
        return new EchobyteNoMTOM();
    }

    /**
     * Create an instance of {@link Echobyte256Response }
     * 
     */
    public Echobyte256Response createEchobyte256Response() {
        return new Echobyte256Response();
    }

    /**
     * Create an instance of {@link EchobyteResponse }
     * 
     */
    public EchobyteResponse createEchobyteResponse() {
        return new EchobyteResponse();
    }

    /**
     * Create an instance of {@link Echobyte }
     * 
     */
    public Echobyte createEchobyte() {
        return new Echobyte();
    }

    /**
     * Create an instance of {@link Echobyte256 }
     * 
     */
    public Echobyte256 createEchobyte256() {
        return new Echobyte256();
    }

    /**
     * Create an instance of {@link Echobyte64Response }
     * 
     */
    public Echobyte64Response createEchobyte64Response() {
        return new Echobyte64Response();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Echobyte64 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://test.com/", name = "echobyte64")
    public JAXBElement<Echobyte64> createEchobyte64(Echobyte64 value) {
        return new JAXBElement<Echobyte64>(_Echobyte64_QNAME, Echobyte64 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Echobyte64Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://test.com/", name = "echobyte64Response")
    public JAXBElement<Echobyte64Response> createEchobyte64Response(Echobyte64Response value) {
        return new JAXBElement<Echobyte64Response>(_Echobyte64Response_QNAME, Echobyte64Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Echobyte }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://test.com/", name = "echobyte")
    public JAXBElement<Echobyte> createEchobyte(Echobyte value) {
        return new JAXBElement<Echobyte>(_Echobyte_QNAME, Echobyte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EchobyteResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://test.com/", name = "echobyteResponse")
    public JAXBElement<EchobyteResponse> createEchobyteResponse(EchobyteResponse value) {
        return new JAXBElement<EchobyteResponse>(_EchobyteResponse_QNAME, EchobyteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EchobyteNoMTOM }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://test.com/", name = "echobyteNoMTOM")
    public JAXBElement<EchobyteNoMTOM> createEchobyteNoMTOM(EchobyteNoMTOM value) {
        return new JAXBElement<EchobyteNoMTOM>(_EchobyteNoMTOM_QNAME, EchobyteNoMTOM.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EchobyteNoMTOMResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://test.com/", name = "echobyteNoMTOMResponse")
    public JAXBElement<EchobyteNoMTOMResponse> createEchobyteNoMTOMResponse(EchobyteNoMTOMResponse value) {
        return new JAXBElement<EchobyteNoMTOMResponse>(_EchobyteNoMTOMResponse_QNAME, EchobyteNoMTOMResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Echobyte256 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://test.com/", name = "echobyte256")
    public JAXBElement<Echobyte256> createEchobyte256(Echobyte256 value) {
        return new JAXBElement<Echobyte256>(_Echobyte256_QNAME, Echobyte256 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Echobyte256Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://test.com/", name = "echobyte256Response")
    public JAXBElement<Echobyte256Response> createEchobyte256Response(Echobyte256Response value) {
        return new JAXBElement<Echobyte256Response>(_Echobyte256Response_QNAME, Echobyte256Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arg0", scope = Echobyte64 .class)
    public JAXBElement<byte[]> createEchobyte64Arg0(byte[] value) {
        return new JAXBElement<byte[]>(_Echobyte64Arg0_QNAME, byte[].class, Echobyte64 .class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = EchobyteNoMTOMResponse.class)
    public JAXBElement<byte[]> createEchobyteNoMTOMResponseReturn(byte[] value) {
        return new JAXBElement<byte[]>(_EchobyteNoMTOMResponseReturn_QNAME, byte[].class, EchobyteNoMTOMResponse.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arg0", scope = EchobyteNoMTOM.class)
    public JAXBElement<byte[]> createEchobyteNoMTOMArg0(byte[] value) {
        return new JAXBElement<byte[]>(_Echobyte64Arg0_QNAME, byte[].class, EchobyteNoMTOM.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = Echobyte256Response.class)
    public JAXBElement<byte[]> createEchobyte256ResponseReturn(byte[] value) {
        return new JAXBElement<byte[]>(_EchobyteNoMTOMResponseReturn_QNAME, byte[].class, Echobyte256Response.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = EchobyteResponse.class)
    public JAXBElement<byte[]> createEchobyteResponseReturn(byte[] value) {
        return new JAXBElement<byte[]>(_EchobyteNoMTOMResponseReturn_QNAME, byte[].class, EchobyteResponse.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arg0", scope = Echobyte256 .class)
    public JAXBElement<byte[]> createEchobyte256Arg0(byte[] value) {
        return new JAXBElement<byte[]>(_Echobyte64Arg0_QNAME, byte[].class, Echobyte256 .class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arg0", scope = Echobyte.class)
    public JAXBElement<byte[]> createEchobyteArg0(byte[] value) {
        return new JAXBElement<byte[]>(_Echobyte64Arg0_QNAME, byte[].class, Echobyte.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = Echobyte64Response.class)
    public JAXBElement<byte[]> createEchobyte64ResponseReturn(byte[] value) {
        return new JAXBElement<byte[]>(_EchobyteNoMTOMResponseReturn_QNAME, byte[].class, Echobyte64Response.class, ((byte[]) value));
    }

}
