/*
 * @(#) 1.1 WautoFVT/src/annotations/partialwsdl/serverpartial2/AddNumbersImplPartial2.java, WAS.websvcs.fvt, WSFP.WFVT, a0705.10 12/22/06 11:37:09 [2/7/07 11:56:13]
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
 * 10/26/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.partialwsdl.serverpartial2;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

/*
 * This test method will verify that the server generates the wsdl correctly
 * when a partial wsdl is provided (JSR-224: 5.2.5.5 Application-specified PortType)
 * in the deployment package. 
 * 
 * Here, the wsdl is completely empty except for definitions. 
 * 
 */	

@WebService(wsdlLocation="WEB-INF/wsdl/AddNumbersImplPartial2Service.wsdl")

public class AddNumbersImplPartial2 {
	
	public int addTwoNumbers(int number1, int number2) {
		return number1 + number2;
	}
}
