
package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

/**
 * a class with multiple legal soap bindings on multiple methods. *
 * @author btiffany
 * 
 * used by SoapBindingTestCase
 *
 */
@WebService

@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
                  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class SoapBindAnno2 {

        // should be doc lit wrap
        public String echodlw(String parm){
                return ("echodlw replies: "+parm);
        }

        @SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
                          parameterStyle=SOAPBinding.ParameterStyle.BARE)
        public String echodlb(String parm){
                return ("echodlb replies: "+parm);
        }



}
