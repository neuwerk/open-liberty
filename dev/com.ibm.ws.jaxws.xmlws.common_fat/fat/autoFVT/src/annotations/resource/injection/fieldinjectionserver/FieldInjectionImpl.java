/*
 * @(#) 1.2 autoFVT/src/annotations/resource/injection/fieldinjectionserver/FieldInjectionImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/10/07 13:53:13 [8/8/12 06:56:03]
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
package annotations.resource.injection.fieldinjectionserver;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/*
 * This SEI models the test case for injecting WebServiceContext
 * at the field level. It also test correct execution of PostConstruct
 * method. Exceution of postconstructer is controlled by a static variable.
 * Injection of WebServiceContext is tested by extracting the ServletContextName
 * from the MessageContext. Both results are accessible via respective get
 * methods.
 * Although the class implements a PreDestroy method, this case will be tested
 * manually by analyzing the server log files.
 */
@WebService
public class FieldInjectionImpl {
	@Resource
	private WebServiceContext wsc;
	
	private MessageContext mc;
	private String _msg;
	
	private static boolean visited = false;
	
	@PostConstruct
	@WebMethod(exclude=true)
	public void initialize() {
		if(!visited){
			_msg = "postConstruct";
			visited = true;
			System.out.println("postConstruct: " + _msg);
		}
	}

	/**
     * Predestroy will not be implemented for wsfp because it is not
     * required by the jaxws standard, and is dependent upon jsr109
     * implementation by the container. 
     * 
	 *  
	 */
	@PreDestroy
	@WebMethod(exclude=true)
	public void finalize() {
		_msg = _msg + "preDestroy:";
		System.out.println("preDestroy: " + _msg);
	}

	@WebMethod
	public String getServletContextName() {
		mc = wsc.getMessageContext();
		ServletContext sc = (ServletContext)mc.get(MessageContext.SERVLET_CONTEXT);

		return sc.getServletContextName();
	}
	
	@WebMethod
	public boolean isPCvisited() {
		return visited;
	}
}
