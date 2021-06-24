/**
 * @(#) 1.1 autoFVT/src/jaxwsejb31Singleton/annotationsRef/ejb1startup/wsfvt/server/Ejb1Startup.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/3/09 13:47:41 [8/8/12 06:58:48]
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
 * 12/03/09    jtnguyen     F743-17947-01        New file for Singleton+WebServiceRef+Startup
 */

package jaxwsejb31Singleton.annotationsRef.ejb1startup.wsfvt.server;

import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.xml.ws.WebServiceRef;
import javax.ejb.Singleton;
import javax.xml.ws.WebServiceRef;
import javax.annotation.PostConstruct;


import jaxwsejb31Singleton.annotationsRef.ejb2.wsfvt.server.Ejb2Add;
import jaxwsejb31Singleton.annotationsRef.ejb2.wsfvt.server.Ejb2AddService;
/**
 * The bean is a Singleton.  The service it uses to inject resource, Ejb2AddService, is a stateless bean.
 */
@Singleton
@WebService(wsdlLocation = "META-INF/wsdl/Ejb1StartupService.wsdl")
public class Ejb1Startup {

	 public static int count;
	 public static int totalSum;

	 
    public Ejb1Startup() {
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

           
    // test element init-on-startup
    @PostConstruct
    private void startup() 
    {
        
        System.out.println("-- anno-Singleton-Startup-WebServiceRef: init-on-startup");
    }

    
    
    /**
     * The method increases the totalSum and returns the sum of num1 and num2 
     */

    
    @WebServiceRef(name = "service/Ejb2AddSvc", type = Ejb2AddService.class)
    private Ejb2AddService svc;
    public int doSum(int num1, int num2) {
 
      /*
        // check JNDI - it works  
        try {
            InitialContext initCtx = null;
            Ejb2AddService ejb2Add = null;
            Ejb2Add port = null;
            int result = 0;

            
            System.out.println("-- check JNDI");
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

     */
        
    	int result = 0;
    	System.out.println("-- check SEI field injection");
        if (svc == null)
            System.out.println( "-- reference is null");
        else {
        
            Ejb2Add port = svc.getEjb2AddPort();
            result = port.addNumbers(num1, num2);
            System.out.println("-- result = " + result);
            
        }

        // add to totalSum
        totalSum = totalSum + result;

        return result;
    }

}
