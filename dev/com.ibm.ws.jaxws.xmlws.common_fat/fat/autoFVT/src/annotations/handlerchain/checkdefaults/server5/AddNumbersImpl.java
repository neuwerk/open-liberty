package annotations.handlerchain.checkdefaults.server;

import javax.jws.HandlerChain;
import javax.jws.WebService;

/*
 * same test as server4 but with . and .. in the path.
 */
@WebService
@HandlerChain(file="../server/./handlersImpl.xml", name="")
public class AddNumbersImpl {
    
    public String addTwenty(String number1s) {
        int number2 = 20;
        int number1 = Integer.parseInt(number1s);        
        return Integer.toString(number1 + number2);
    }
 
}
