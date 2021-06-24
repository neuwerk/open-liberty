/**
 * @(#) 1.1 autoFVT/src/jaxwsejb31Singleton/depdescRef/ejb2/wsfvt/server/Ejb2Add.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/20/09 12:48:04 [8/8/12 06:58:47]
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
 * 11/18/09   jtnguyen      F743-17947-02        New file for Singleton and WebServiceRef
 */

package jaxwsejb31Singleton.depdescRef.ejb2.wsfvt.server;

import javax.jws.WebService;

/**
 * The bean is a Stateless bean defined by Stateless element in the ejb-jar.xml. 
 */
@WebService(wsdlLocation = "META-INF/wsdl/Ejb2AddService.wsdl")
public class Ejb2Add {

    public Ejb2Add() {
    }

	/**
     * The method returns the sum of num1 and num2
     */
    public int addNumbers(int num1, int num2) {

        return (num1 + num2);

    }
      
   
}
