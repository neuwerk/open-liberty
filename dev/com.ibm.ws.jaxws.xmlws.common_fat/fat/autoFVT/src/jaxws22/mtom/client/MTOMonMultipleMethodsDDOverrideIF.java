package jaxws22.mtom.client;

import javax.xml.ws.soap.MTOM;
import javax.jws.WebService;

@WebService(targetNamespace="http://test.com/", name="MTOMonMultipleMethodsDDOverride")
//                                                    MTOMonMultipleMethodsDDOverrideService
public interface MTOMonMultipleMethodsDDOverrideIF {
      // on the service: @MTOM(enabled=true, threshold=1024), and dd overridden to false
    public byte[] echobyte(byte[] b);

}
