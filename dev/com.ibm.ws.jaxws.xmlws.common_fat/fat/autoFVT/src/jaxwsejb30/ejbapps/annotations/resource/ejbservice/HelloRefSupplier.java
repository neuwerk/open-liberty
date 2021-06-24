/**
 * autoFVT/src/jaxwsejb30/ejbapps/annotations/resource/ejbservice/HelloRefSupplier.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId       Feature/Defect       Description
 * -----------------------------------------------------------------------------
 * [ddmmyyyy]  samerrell    [defect]             New file
 * 07/31/2008  samerrel     538865               Fixed wsdl location
 *                                               problems
 *
 */
package jaxwsejb30.ejbapps.annotations.resource.ejbservice;

import javax.ejb.Stateless;
import javax.jws.WebService;

/**
 * @author samerrel
 * 
 */
@Stateless
@WebService(wsdlLocation = "META-INF/wsdl/HelloRefSupplierService.wsdl")
public class HelloRefSupplier {

    public HelloRefSupplier() {
    }

    public String sayHello(String name) {

        System.out.println("HelloRefSupplier: In sayHello.");
        return "Hello, " + name;
    }
}
