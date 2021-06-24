package annotations.handlerchain.checkdefaults.server;

import javax.jws.HandlerChain;
import javax.jws.WebParam;
import javax.jws.WebService;


@WebService(endpointInterface="annotations.handlerchain.checkdefaults.server.AddNumbersIF")
@HandlerChain(file="handlersSEI.xml", name="")
public class AddNumbersImpl {
    
    public String addTwenty(String number1s) {
        int number2 = 20;
        int number1 = Integer.parseInt(number1s);        
        return Integer.toString(number1 + number2);
    }
    
}
