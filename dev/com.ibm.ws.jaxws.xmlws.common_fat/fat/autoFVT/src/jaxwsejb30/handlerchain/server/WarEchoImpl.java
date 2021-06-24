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
@WebService()
//@HandlerChain(file="handlerchain.xml")
public class WarEchoImpl {
    public String echo(String in){
        return in;
    }

}
