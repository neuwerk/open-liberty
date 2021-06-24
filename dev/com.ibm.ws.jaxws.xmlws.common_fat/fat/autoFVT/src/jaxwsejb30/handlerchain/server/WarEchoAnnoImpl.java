package jaxwsejb30.handlerchain.server;

import javax.xml.ws.*;
import javax.jws.*;
import javax.ejb.Stateless;

/**
 * a simple web service to echo a string, no handler
 * @author btiffany
 *
 */
//@Stateless()
@WebService(name="WarEchoAnnoImpl")
@HandlerChain(file="annohandlerchain.xml")
public class WarEchoAnnoImpl {
    public String echo(String in){
        return in;
    }

}
