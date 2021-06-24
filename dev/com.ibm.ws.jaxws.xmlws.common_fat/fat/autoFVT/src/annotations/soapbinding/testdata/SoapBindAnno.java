
package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

/**
 * a class with multiple legal soap bindings on multiple methods.
 * However it is not legal to support both RPC and Doc bindings, so it should fail.
 * @author btiffany
 *
 */
@WebService
// doc encoded bare on a class, should not produce wsdl, as enc. style is not supported.
@SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.LITERAL,
                  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class SoapBindAnno {

        // should be rpc lit wrap
        public String echorlw(String parm){
                return ("echorlw replies: "+parm);
        }

        @SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
                          parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
        public String echodlw(String parm){
                return ("echodlw replies: "+parm);
        }

        @SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
                          parameterStyle=SOAPBinding.ParameterStyle.BARE)
        public String echodlb(String parm){
                return ("echodlb replies: "+parm);
        }

}
