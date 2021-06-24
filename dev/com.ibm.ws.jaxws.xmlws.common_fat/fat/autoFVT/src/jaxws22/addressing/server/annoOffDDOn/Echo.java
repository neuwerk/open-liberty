//A test service for addressing configuration

//Package does not match the eclipse directory. Needs to be that way
// so webservice.xml and web.xml can be easily recycled across
// multiple services.
 
package jaxws22.addressing.server;  // don't change this package
import javax.jws.*;
import javax.xml.ws.*;
import javax.xml.ws.soap.*; 

// dd will override all settings if that's working.
@WebService()
@Addressing(enabled=false, required=false, responses=AddressingFeature.Responses.ANONYMOUS)
public class Echo {
    public String echo(String in) throws java.lang.Exception {
        System.out.println("annoOffDDOn echo called with arg:" +in);
        return "server replies: "+ in;
    }
}
