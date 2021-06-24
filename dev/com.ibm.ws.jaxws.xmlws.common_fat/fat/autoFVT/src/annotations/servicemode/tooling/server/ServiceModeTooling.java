/*
 * @(#) 1.1 autoFVT/src/annotations/servicemode/tooling/server/ServiceModeTooling.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 11:24:08 [8/8/12 06:55:57]
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
 * 06/16/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.servicemode.tooling.server;

import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;

@WebService
@ServiceMode(value = Service.Mode.PAYLOAD)
public class ServiceModeTooling {
	
	public int addNumbers(int number1, int number2) {
		return number1 * number2;
	}
}
