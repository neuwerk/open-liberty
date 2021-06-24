package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
// doc lit bare on a method, should work

public class Sbj2w_dlbm_legal {

        @SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
                          parameterStyle=SOAPBinding.ParameterStyle.BARE)
        public String echo(String parm){
                return ("you passed me a string: "+parm);
        }


}
