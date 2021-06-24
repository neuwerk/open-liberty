/**
 * @(#) 1.1 autoFVT/src/jaxwsejb31Singleton/depdesc/wsfvt/server/EjbDepdesc.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/18/09 22:52:59 [8/8/12 06:58:46]
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
 * 11/18/09   jtnguyen            F743-17947-02           New file
 */

package jaxwsejb31Singleton.depdesc.wsfvt.server;

import javax.jws.WebService;

/**
 * The bean is a Singleton bean with Singleton defined in the ejb-jar.xml. 
 */
@WebService(wsdlLocation = "META-INF/wsdl/EjbDepdescService.wsdl")
public class EjbDepdesc {

	 public static int count;
	 
    public EjbDepdesc() {
    	count = 0;
    }
    
    /**
     * The method increases the counter and returns a string 
     */
    public String sayHello(String name) {

        count++;
        //System.out.println("-- In sayHello. count = " + count);

        return "Hello, " + name;
    }
    
    /**
     * The method returns the counter 
     */    
    public int lastCount() {
    	
        //System.out.println("-- In lastCount. count = " + count);
        return count;
    }
   
   
}
