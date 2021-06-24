/**
 * autoFVT/src/jaxwsejb30/ejbapps/annotations/resource/warservice/WarRefSupplier.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 *
 */
package jaxwsejb30.ejbapps.annotations.resource.warservice;

import javax.jws.WebService;

/**
 * @author samerrel
 * 
 */
@WebService
public class WarRefSupplier {

    public String sayHello( String name ) {
        System.out.println( "WarRefSupplier: in sayHello" );
        return "Hello, " + name;
    }
}
