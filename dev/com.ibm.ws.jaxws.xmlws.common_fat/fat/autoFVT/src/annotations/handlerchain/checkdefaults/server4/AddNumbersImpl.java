package annotations.handlerchain.checkdefaults.server;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.BARE)
//@SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
@HandlerChain(file="handlersImpl.xml", name="")
public class AddNumbersImpl {
    public String addTwenty(String number1s) {
        int number2 = 20;
        int number1 = Integer.parseInt(number1s);        
        return Integer.toString(number1 + number2);
    }
    
}
