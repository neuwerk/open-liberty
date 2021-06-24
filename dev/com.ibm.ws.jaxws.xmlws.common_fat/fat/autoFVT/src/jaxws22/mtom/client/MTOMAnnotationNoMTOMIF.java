/**
 * The interface used by the dynamic proxy client.
 */
package jaxws22.mtom.client;
import javax.jws.*;
import javax.xml.ws.soap.MTOM;
@WebService(targetNamespace="http://test.com/", name="MTOMAnnotationNoMTOM")

@MTOM(enabled=false)
public interface MTOMAnnotationNoMTOMIF {   
    
    public abstract byte [] echobyte (byte [] b);

}