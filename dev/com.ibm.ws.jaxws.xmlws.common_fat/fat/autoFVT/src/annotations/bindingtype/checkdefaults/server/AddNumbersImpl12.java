/*
 * @(#) 1.1 autoFVT/src/annotations/bindingtype/checkdefaults/server/AddNumbersImpl12.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/10/07 15:28:14 [8/8/12 06:56:42]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 07/10/2006  euzunca     LIDB3296.31.01     new file
 * 
 */
package annotations.bindingtype.checkdefaults.server12;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

@WebService(wsdlLocation="WEB-INF/wsdl/AddNumbersImplService.wsdl")
@BindingType(value=SOAPBinding.SOAP12HTTP_BINDING)
public class AddNumbersImpl {
	
	public int addTwoNumbers(int number1, int number2) {
		return number1 + number2;
	}
}
