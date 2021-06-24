/**
 * autoFVT/src/jaxwsejb30/ejbapps/serverdepdesc/server/echoejb/EjbEcho.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date       UserId       Feature/Defect       Description
 * -----------------------------------------------------------------------------
 * 06/28/2008 Samerrel     LIDB4511.45.01       New File
 *
 */
package jaxwsejb30.ejbapps.serverdepdesc.server.echoejb;

import javax.ejb.Stateless;
import javax.jws.WebService;

/**
 * A simple EJB-based JAX-WS service with all web service annotation parameters
 * set. These parameters will be overridden in the service's webservices.xml
 * file
 * 
 */
@Stateless
@WebService(name = "EjbEcho", targetNamespace = "http://echoejb.server.serverdepdesc.ejbapps.jaxwsejb30/", serviceName = "EjbEchoService", portName = "EjbEchoPort", wsdlLocation = "META-INF/wsdl/EjbEchoService.wsdl")
public class EjbEcho {

    public EjbEcho() {
        // Default constructor
    }

    /**
     * Echoes the message passed to it.
     * 
     * @param echoMsg
     *            - message to be echoed
     * @return - ECHO: (echoMsg)
     */
    public String echo(String echoMsg) {
        System.out.println("EjbEcho.echo recieved: " + echoMsg);
        return "ECHO: " + echoMsg;
    }

}
