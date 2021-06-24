package annotations.reqrespwrappers.server;
import javax.xml.ws.WebFault;

@WebFault(messageName="customizedWebFaultMessage")
public class Jaxws22ImplException  extends Exception {

}
