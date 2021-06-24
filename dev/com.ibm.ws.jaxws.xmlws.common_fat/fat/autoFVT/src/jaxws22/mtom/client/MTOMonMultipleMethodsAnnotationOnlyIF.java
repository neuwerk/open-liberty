package jaxws22.mtom.client;

import javax.xml.ws.soap.MTOM;
import javax.jws.WebService;


@WebService(targetNamespace="http://test.com/", name="MTOMonMultipleMethodsAnnotationOnly")
public interface MTOMonMultipleMethodsAnnotationOnlyIF {
    //on the service: @MTOM(enabled=true, threshold=64)
    public byte[] echobyte64(byte[] b);
    
    // on the service:  @MTOM(enabled=true, threshold=256)
    public byte[] echobyte256(byte[] b);
    
    //on the service: @MTOM(enabled=false)
    public byte[] echobyteNoMTOM(byte[] b);
    
//  on the service: nothing
    public byte[] echobyte(byte[] b);
}
