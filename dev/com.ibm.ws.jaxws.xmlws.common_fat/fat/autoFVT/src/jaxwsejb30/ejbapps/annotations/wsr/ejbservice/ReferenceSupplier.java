/**
 * 
 * autoFVT/src/jaxwsejb30/ejbapps/annotations/wsr/ejbservice/ReferenceSupplier.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * 04/01/2008  samerrell    LIDB4511.45          New file
 * 07/31/2008  samerrel     538865               Fixed wsdl location
 *                                               problems
 *
 */
package jaxwsejb30.ejbapps.annotations.wsr.ejbservice;

import javax.ejb.Stateless;
import javax.jws.WebService;

/*
 * a trivial service that will cause the ejb container to supply some resources that we'll look up with the webservice
 * ref annotations.
 */
@Stateless
@WebService(wsdlLocation = "META-INF/wsdl/ReferenceSupplierService.wsdl")
public class ReferenceSupplier {
    public String sayHello(String in) {
        return "Hello, " + in;
    }

}
