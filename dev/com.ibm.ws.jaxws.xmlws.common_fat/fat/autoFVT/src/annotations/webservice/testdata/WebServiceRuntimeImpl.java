package annotations.webservice.testdata;

import javax.jws.*;

/**
 * A trivial java file to test @Webservice annotation.
 * It should be converted successfully into wsdl.
 * Note that in the case of an SEI, implementing the interface is not required.
 * @author btiffany
 *
 */
@WebService(endpointInterface="annotations.webservice.testdata.WebServiceRuntimeIfc" )

public class WebServiceRuntimeImpl {

        public String echo (String parm){ return "the server says: "+parm; }

}
