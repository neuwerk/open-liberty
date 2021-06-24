/**
 * @(#) 1.1 autoFVT/src/jaxwsejb31Singleton/depdescRef/ejb1/wsfvt/server/Ejb1.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/20/09 12:48:00 [8/8/12 06:58:46]
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
 * 11/18/09   jtnguyen            F743-17947-02           New file for Singleton and WebServiceRef
 */

package jaxwsejb31Singleton.depdescRef.ejb1.wsfvt.server;

import javax.jws.WebService;
import javax.naming.InitialContext;

import jaxwsejb31Singleton.depdescRef.ejb2.wsfvt.server.Ejb2Add;
import jaxwsejb31Singleton.depdescRef.ejb2.wsfvt.server.Ejb2AddService;
/**
 * The bean Singleton and WebServiceRef defined in the ejb-jar.xml. 
 */
@WebService(wsdlLocation = "META-INF/wsdl/Ejb1Service.wsdl")
public class Ejb1 {

	 public static int count;
	 public static int totalSum;

	 
    public Ejb1() {
    	count = 0;
    	totalSum = 0;
    }
           
    /**
     * The method returns the totalSum 
     */    
    public int lastTotalSum() {
    	
        //System.out.println("-- In lastCount. count = " + count);
        return totalSum;
    }

    /**
     * The method increases the totalSum and returns the sum of num1 and num2 
     */

    public int doSum(int num1, int num2) {
 
    	/*
            <service-ref>
                <service-ref-name>service/Ejb2AddSvc</service-ref-name>
                <service-interface>jaxwsejb31Singleton.depdescRef.ejb2.wsfvt.server.Ejb2AddService</service-interface>
            </service-ref>

        */
    	
 
        InitialContext initCtx = null;
        Ejb2AddService ejb2Add = null;
        Ejb2Add port = null;
        int result = 0;

        try {
            initCtx = new InitialContext();
            ejb2Add = (Ejb2AddService) initCtx.lookup("java:comp/env/service/Ejb2AddSvc");
            port = ejb2Add.getEjb2AddPort();
            
            result = port.addNumbers(num1, num2);
            System.out.println("-- result = " + result);
            
            // add to totalSum
            totalSum = totalSum + result;
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    
   
   
}
