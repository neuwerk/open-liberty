/**
 * The interface used by the dynamic proxy client.
 */
package jaxws22.mtom.client;
import javax.jws.*;
import javax.xml.ws.soap.MTOM;
@WebService(targetNamespace="http://test.com/", name="MTOMDDOverride")

@MTOM
public interface MTOMDDOverrideIF {   
    
    public abstract byte [] echobyte (byte [] b);

}