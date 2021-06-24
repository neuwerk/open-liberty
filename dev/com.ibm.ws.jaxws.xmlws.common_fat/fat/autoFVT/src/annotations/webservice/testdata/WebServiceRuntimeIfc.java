
package annotations.webservice.testdata;

import javax.jws.*;

/**
 * A trivial java interface to test @Webservice annotation.
 * It should be converted successfully into wsdl.
 * @author btiffany
 *
 */
// note that some of the annotation params are picked up from the impl class. 
@WebService(wsdlLocation="WEB-INF/wsdl/WebServiceIfc.wsdl")
public interface WebServiceRuntimeIfc {
        @WebMethod
        @WebResult(name="return")  // required to work around beta limitation - can't be keyword
        public String echo (String parm);

}

