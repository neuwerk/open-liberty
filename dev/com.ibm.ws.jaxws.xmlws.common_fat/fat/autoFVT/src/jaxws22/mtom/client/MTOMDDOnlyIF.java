/**
 * The interface used by the dynamic proxy client.
 */
package jaxws22.mtom.client;
import javax.jws.*;
import javax.xml.ws.soap.MTOM;
@WebService(targetNamespace="http://test.com/", name="MTOMDDOnly")


public interface MTOMDDOnlyIF {   
    
    public abstract byte [] echobyte (byte [] b);

}