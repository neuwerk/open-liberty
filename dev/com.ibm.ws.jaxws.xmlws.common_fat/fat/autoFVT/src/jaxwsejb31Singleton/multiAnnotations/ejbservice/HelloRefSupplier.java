/**
 * @(#) 1.1 autoFVT/src/jaxwsejb31Singleton/multiAnnotations/ejbservice/HelloRefSupplier.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/19/09 11:16:53 [8/8/12 06:39:43]
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2009
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId       Feature/Defect       Description
 * -----------------------------------------------------------------------------
 * 11/19/2009  varadan      F743-17947-01        New file
 *
 */
package jaxwsejb31Singleton.multiAnnotations.ejbservice;

import javax.ejb.Singleton;
import javax.jws.WebService;

/**
 * @author varadan
 * 
 */
@Singleton
@WebService(wsdlLocation = "META-INF/wsdl/HelloRefSupplierService.wsdl")
public class HelloRefSupplier {

    static int counter = 0;
    
    public HelloRefSupplier() {
    }

    public String sayHello(String name) {
        System.out.println("HelloRefSupplier: In sayHello.");
        return "Hello, " + name;
    }
    
    public String incrementCounter() {
        System.out.println("HelloRefSupplier: In incrementCounter. bean : " + this.toString());
        counter++;

        System.out.println( "counter : " + counter);
        return "Counter Incremented to, " + counter;
    }
    
    public int getCounter() {
        System.out.println("HelloRefSupplier: In getCounter. Returning value : " + counter + " bean : " + this.toString());
        return counter;
    }
    
}
