
package annotations.webservice.testdata;

import javax.jws.*;

/**
 * A trivial java file to test @Webservice annotation.
 * It should be converted successfully into wsdl.
 * @author btiffany
 *
 */
// note that some of the annotation params are picked up from the impl class. 
@WebService
public interface WebServiceRuntimeif {
        @WebMethod
        public String echo (String parm);

}

