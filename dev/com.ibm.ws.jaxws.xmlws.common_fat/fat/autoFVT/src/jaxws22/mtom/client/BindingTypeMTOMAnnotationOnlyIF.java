package jaxws22.mtom.client;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.MTOM;

@WebService(targetNamespace="http://test.com/", name="BindingTypeMTOMAnnotationOnly")
public interface BindingTypeMTOMAnnotationOnlyIF {
    public byte[] echobyte(byte[] b);
}