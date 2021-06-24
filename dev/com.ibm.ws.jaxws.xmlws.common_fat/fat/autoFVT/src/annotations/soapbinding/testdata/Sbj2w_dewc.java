
package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

// doc enc wrap on class, should not produce wsdl, as enc style not supported.
@WebService
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.ENCODED, 
		  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)

			  
public class Sbj2w_dewc {

	public String echo(String parm){
		return ("you passed me a string: "+parm);
	}	


}
