package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
public class Sbj2w_rlbm {
        // rpc lit bare on method, rpc is not allowed on methods, so shouuld not produce wsdl
        @SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.LITERAL,
                          parameterStyle=SOAPBinding.ParameterStyle.BARE)
        public String echo(String parm){
                return ("you passed me a string: "+parm);
        }


}
