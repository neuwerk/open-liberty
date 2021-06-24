package annotations.handlerchain.checkdefaults.server;

import javax.jws.HandlerChain;
import javax.jws.WebService;

/*
 * same as server4 except uses http url for handler. 
 * We'll transform it in the ant build to the proper host and port.
 * We need to pull the impl from another war, as we have a circular
 * dep. problem - this app won't deploy if it can't read the handler file,
 * and it can't read the handler file until it's deployed! 
 * So we read the handler file from server5.
 */
@WebService
@HandlerChain(file="@HOST_AND_PORT@/HandlerChainCheckDefaults5/handlersImpl.xml", name="")
public class AddNumbersImpl {
    
    public String addTwenty(String number1s) {
        int number2 = 20;
        int number1 = Integer.parseInt(number1s);        
        return Integer.toString(number1 + number2);
    } 
    
}
