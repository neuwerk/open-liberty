package annotations.handlerchain.checkdefaults.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/* doclitbare can't handle primitives, or more than one arg per method, so 
 * need a different animal for this one.
 * 
 * Also, xjc was overwriting the exception class with the generated exception bean,
 * so got rid of exception just for this test. 
 * 
 */

@WebService(targetNamespace = "http://server.checkdefaults.handlerchain.annotations/", name="AddNumbersImpl")
@SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.LITERAL)
public interface AddNumbersIF  {
    public String addTwenty( String number1 ); 

}
