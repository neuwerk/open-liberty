package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
// rpc enc wrap on class, should not produce wsdl, enc not supported.
@SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.ENCODED,
                  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class Sbj2w_rewc {
        public String echo(String parm){
                return ("you passed me a string: "+parm);
        }


}
