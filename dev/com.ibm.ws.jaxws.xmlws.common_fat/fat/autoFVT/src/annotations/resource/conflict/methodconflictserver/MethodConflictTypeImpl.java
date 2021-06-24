/*
 * @(#) 1.1 autoFVT/src/annotations/resource/conflict/methodconflictserver/MethodConflictTypeImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 11:43:03 [8/8/12 06:56:03]
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
package annotations.resource.conflict.methodconflictserver;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/*
 * This SEI presents the test case for type consistency.
 * at the method injection level. When a resource is declared 
 * types specified in the annotation and the declaration 
 * itself should be consistent. 
 * This testcase models a positive test case.
 */
@WebService
public class MethodConflictTypeImpl {

	private WebServiceContext wsc;
	
	private MessageContext mc;
	
	@Resource(type=java.lang.Object.class)
	private void setMyDB(WebServiceContext ds) {
		wsc = ds;
	}
	
	@WebMethod
	public String getServletContextName() {
		mc = wsc.getMessageContext();
		ServletContext sc = (ServletContext)mc.get(MessageContext.SERVLET_CONTEXT);

		return sc.getServletContextName();
	}
}
