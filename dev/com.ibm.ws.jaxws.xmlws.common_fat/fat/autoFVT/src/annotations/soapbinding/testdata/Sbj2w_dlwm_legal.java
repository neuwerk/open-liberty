package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
// doc lit wrap on a method, should work

public class Sbj2w_dlwm_legal {

        @SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
                          parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
        public String echo(String parm){
                return ("you passed me a string: "+parm);
        }
}
