package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
public class Sbj2w_rewm {
//       rpc enc wrap on method, should not produce wsdl, rpc not allowed on methods.
        @SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.ENCODED,
                          parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
        public String echo(String parm){
                return ("you passed me a string: "+parm);
        }
}
