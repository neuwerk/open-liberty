package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
//rpc lit bare on class, should not produce wsdl, rpc must be wrapped.
@SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.ENCODED,
                  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class Sbj2w_rlbc {

        public String echo(String parm){
                return ("you passed me a string: "+parm);
        }


}
