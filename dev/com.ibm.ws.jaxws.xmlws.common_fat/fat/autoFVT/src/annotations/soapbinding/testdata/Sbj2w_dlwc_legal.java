package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
// doc lit wrapped on a class, should work
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
                  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)


public class Sbj2w_dlwc_legal {
        public String echo(String parm){
                return ("you passed me a string: "+parm);
        }


}
