package annotations.handlerchain.checkdefaults.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://server.checkdefaults.handlerchain.annotations/", name="AddNumbersImpl")
@SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.LITERAL)
@HandlerChain(file="handlersSEI.xml", name="")
public interface AddNumbersIF extends Remote {
    
    @WebMethod(operationName="addNumbers", action="urn:addNumbers")
    @WebResult(name="return")
    public int addNumbers(
        @WebParam(name="num1")int number1, 
        @WebParam(name="num2")int number2) throws RemoteException, AddNumbersException;

}
