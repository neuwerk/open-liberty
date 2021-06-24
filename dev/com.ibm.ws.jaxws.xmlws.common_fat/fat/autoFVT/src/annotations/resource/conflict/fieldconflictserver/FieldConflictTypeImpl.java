/*
 * @(#) 1.1 autoFVT/src/annotations/resource/conflict/fieldconflictserver/FieldConflictTypeImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 11:43:00 [8/8/12 06:56:02]
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
 * 10/23/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.resource.conflict.fieldconflictserver;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/*
 * This SEI presents the test case for type consistency.
 * at the field injection level. When a resource is declared 
 * types specified in the annotation and the declaration 
 * itself should be consistent. 
 * This testcase models a positive test case.
 */
@WebService
public class FieldConflictTypeImpl {
	
	@Resource(type=javax.xml.ws.WebServiceContext.class)
	private Object obj;
	
	private MessageContext mc;
	
	private static boolean visited = false;
	
	@WebMethod
	public String getServletContextName() {
		WebServiceContext wsc = (WebServiceContext) obj;
		mc = wsc.getMessageContext();
		ServletContext sc = (ServletContext)mc.get(MessageContext.SERVLET_CONTEXT);

		return sc.getServletContextName();
	}
}
