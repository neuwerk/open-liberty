/**
 * The interface used by the dynamic proxy client.
 */
package jaxws22.mtom.client;
import javax.jws.*;
import javax.xml.ws.soap.MTOM;
@WebService(targetNamespace="http://test.com/", name="MTOMAnnotationOnly")

// the name of this class describes the configuration of the SERVICE, not the client interface

@MTOM(enabled=true, threshold=1024)
public interface MTOMAnnotationOnlyIF {   
    
    public abstract byte [] echobyte (byte [] b);

}