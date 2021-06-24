/*
 * @(#) 1.1 autoFVT/src/annotations/partialwsdl/serverpartial3/AddNumbersImplPartial3.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 11:37:10 [8/8/12 06:56:01]
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
package annotations.partialwsdl.serverpartial3;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

/*
 * This test method will verify that the server generates the wsdl correctly
 * when a partial wsdl is provided (JSR-224: 5.2.5.6 Application-specified Schema)
 * in the deployment package.
 * 
 * In this scenario, only a schema file and wsdl file only importing that schema are
 * provided.
 * 
 */	

@WebService
public class AddNumbersImplPartial3 {
	
	public int addTwoNumbers(int number1, int number2) {
		return number1 + number2;
	}
}
