/**
 * @(#) 1.1 autoFVT/src/jaxws/xmlcatalog/wsfvt/client/HelloEjbClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 6/30/09 10:50:42 [8/8/12 06:09:26] 
 * 
 * IBM Confidential OCO Source Material
 * (C) COPYRIGHT International Business Machines Corp. 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date       UserId        Feature/Defect  Description
 * ----------------------------------------------------------------------------
 * 06/29/2009 samerrel      552420          New File
 * 
 */
package jaxws.xmlcatalog.wsfvt.client;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.WebServiceRef;

import jaxws.xmlcatalog.wsfvt.service.HelloEjb;
import jaxws.xmlcatalog.wsfvt.service.HelloEjbService;
import jaxws.xmlcatalog.wsfvt.service.HelloWar;
import jaxws.xmlcatalog.wsfvt.service.HelloWarService;

/**
 * A simple EJB3 Web service testing the XML Catalog resolution of the
 * WebServiceRef's wsdlLocation attribute.
 * 
 */
@Stateless
@WebService(wsdlLocation = "META-INF/wsdl/HelloEjbClientService.wsdl")
public class HelloEjbClient {

    @WebServiceRef(wsdlLocation = "http://123456789/HelloEjbService?wsdl")
    private HelloEjbService helloEjbSvcBadWsdlloc;

    @WebServiceRef(wsdlLocation = "http://123456789/HelloWarService?wsdl")
    private HelloWarService helloWarSvcBadWsdlloc;

    public HelloEjbClient() {
        // Default constructor
    }

    /**
     * Calls the HelloWar service from the HelloWarService injected by the
     * WebServiceRef annotation.
     * 
     * @param msg
     *            - Message to be send to the HelloWar service.
     * @return
     */
    public String callHelloWarClientBadWsdlAnnotation(String msg) {
        HelloWar helloWar1 = helloWarSvcBadWsdlloc.getHelloWarPort();
        return helloWar1.sayHello(msg);
    }

    /**
     * Calls the HelloEjb service from the HelloEjbService injected by the
     * WebServiceRef annotation.
     * 
     * @param msg
     *            - Message to be send to the HelloEjb service.
     * @return
     */
    public String callHelloEjbClientBadWsdlAnnotation(String msg) {
        HelloEjb helloEjb1 = helloEjbSvcBadWsdlloc.getHelloEjbPort();
        return helloEjb1.sayHello(msg);
    }

}
