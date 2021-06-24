//A test service for the RespectBinding feature

//Package does not match the eclipse directory. Needs to be that way
// so webservice.xml and web.xml can be easily recycled across
// multiple services.
 
package jaxws22.respectbinding.server;  // don't change this package
import javax.jws.*;
import javax.xml.ws.*;

//@RespectBinding()  // should default to enabled, we'll find out. 
@WebService(wsdlLocation="WEB-INF/wsdl/EchoService.wsdl")
public class Echo {
    public String echo(String in) throws Exception {
        System.out.println("Respect Binding validRequiredDDTrue echo called with arg:" +in);
        return in;
    }
}
