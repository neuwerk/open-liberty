package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
// rpc lit wrapped on a class, should work

@SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.LITERAL,
                  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class Sbj2w_rlwc_legal {


        public String echo(String parm){
                return ("you passed me a string: "+parm);
        }


}
