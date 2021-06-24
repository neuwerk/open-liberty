package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
// rpc enc bare on class, should not produce wsdl, as it must be wrapped.
@SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.ENCODED,
                  parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class Sbj2w_rebc {
        public String echo(String parm){
                return ("you passed me a string: "+parm);
        }
}
