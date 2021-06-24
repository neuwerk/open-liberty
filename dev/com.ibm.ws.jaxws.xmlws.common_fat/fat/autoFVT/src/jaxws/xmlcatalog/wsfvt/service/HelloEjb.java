/**
 * @(#) 1.1 autoFVT/src/jaxws/xmlcatalog/wsfvt/service/HelloEjb.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 6/30/09 10:50:45 [8/8/12 06:09:26] 
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
package jaxws.xmlcatalog.wsfvt.service;

import javax.ejb.Stateless;
import javax.jws.WebService;

/**
 *
 *
 */
@Stateless
@WebService(wsdlLocation = "META-INF/wsdl/HelloEjbService.wsdl")
public class HelloEjb {

    public HelloEjb() {

    }

    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }
}
