/**
 * autoFVT/src/jaxwsejb30/ejbapps/serverdepdesc/server/echowar/WarEcho.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
 * 
 * IBM Confidential OCO Source Material
 * (C) COPYRIGHT International Business Machines Corp. 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date       UserId        Defect                Description
 * ----------------------------------------------------------------------------
 * 07/11/2008 samerrel      LIDB4511.46           New File
 * 
 */
package jaxwsejb30.ejbapps.serverdepdesc.server.echowar;

import javax.jws.WebService;

/**
 * A simple WAR-based JAX-WS service with all web service annotation parameters
 * set. These parameters will be overridden in the service's webservices.xml
 * file
 * 
 */
@WebService(name = "WarEcho", targetNamespace = "http://echowar.server.serverdepdesc.ejbapps.jaxwsejb30/", serviceName = "WarEchoService", portName = "WarEchoPort", wsdlLocation = "WEB-INF/wsdl/WarEchoService.wsdl")
public class WarEcho {

    /**
     * Echoes the message passed to it.
     * 
     * @param echoMsg
     *            - message to be echoed
     * @return - ECHO: (echoMsg)
     */
    public String echo(String echoMsg) {
        System.out.println("WarEcho.echo recieved: " + echoMsg);
        return "ECHO: " + echoMsg;
    }

}
