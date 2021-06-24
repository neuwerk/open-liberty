/**
 * autoFVT/src/jaxwsejb30/ejbapps/annotations/resource/ejbrefchecker/ResourceRefChecker.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * 02-21-2008  samerrell    LIDB4511.45.01       New File
 * 07/31/2008  samerrel     538865               Fixed wsdl location
 *                                               problems
 *
 */
package jaxwsejb30.ejbapps.annotations.resource.ejbrefchecker;

import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.NamingException;

import jaxwsejb30.ejbapps.annotations.resource.ejbservice.HelloRefSupplier;
import jaxwsejb30.ejbapps.annotations.resource.ejbservice.HelloRefSupplierService;
import jaxwsejb30.ejbapps.annotations.resource.warservice.WarRefSupplier;
import jaxwsejb30.ejbapps.annotations.resource.warservice.WarRefSupplierService;

@Stateless
@WebService(wsdlLocation = "META-INF/wsdl/ResourceRefCheckerService.wsdl")
@Resources( {
             @Resource(name = "service/HelloRefService", type = jaxwsejb30.ejbapps.annotations.resource.ejbservice.HelloRefSupplierService.class),
             @Resource(name = "service/WarRefService", type = jaxwsejb30.ejbapps.annotations.resource.warservice.WarRefSupplierService.class) })
@Resource(name = "service/classHelloRefService", type = jaxwsejb30.ejbapps.annotations.resource.ejbservice.HelloRefSupplierService.class)
public class ResourceRefChecker {

    public ResourceRefChecker() {
    }

    /*******************************************************************************************************************
     * FIELD INJECTION
     ******************************************************************************************************************/

    // The default JNDI name should be:
    // jaxwsejb30.annotations.resource.server1.HelloRefSupplierService/mySvc1
    @Resource
    private HelloRefSupplierService mySvc1;

    /**
     * Calls an injected EJB-based service using the \@Resource annotation
     * 
     * @param input
     *            - string to be returned in "Hello, input"
     * @return String - "Hello, input"
     */
    public String checkEjbFieldInjection(String input) {
        String result = "";

        System.out.println("[ResourceRefChecker] checkFieldInjection invoked.");
        if (mySvc1 == null) {
            String response = "HelloRefSupplierService mySvc1 reference is null.";
            System.out.println(response);
            return response;
        }
        System.out.println("Getting HelloRefSupplierPort...");
        HelloRefSupplier port = mySvc1.getHelloRefSupplierPort();
        result = port.sayHello(input);
        System.out.println("HelloRefSupplierService mySvc1 returned.");
        return result;
    }

    @Resource
    private WarRefSupplierService mySvc2;

    /**
     * Calls an injected WAR-based service using the \@Resource annotation
     * 
     * @param input
     *            - string to be returned in "Hello, input"
     * @return String - "Hello, input"
     */
    public String checkWarFieldInjection(String input) {
        String result = "";

        System.out
                  .println("[ResourceRefChecker] checkWarFieldInjection invoked.");
        if (mySvc2 == null) {
            result = "WarRefSupplierService mySvc2 reference is null.";
            System.out.println(result);
            return result;
        }
        System.out.println("Getting WarRefSupplierPort...");
        WarRefSupplier wrsPort = mySvc2.getWarRefSupplierPort();
        result = wrsPort.sayHello(input);
        System.out.println("WarRefSupplierPort mySvc2 returned:");
        return result;
    }

    /*******************************************************************************************************************
     * SETTER INJECTION
     ******************************************************************************************************************/

    private HelloRefSupplierService mySvc3;

    @Resource
    private void setMySvc3(HelloRefSupplierService svc) {
        System.out.println("[ResourceRefChecker] setMySvc3 invoked.");
        mySvc3 = svc;
    }

    /**
     * Calls an injected EJB-based service using the \@Resource annotation on a
     * setter method
     * 
     * @param input
     *            - string to be returned in "Hello, input"
     * @return String - "Hello, input"
     */
    public String checkEjbMethodInjection(String input) {
        String result = "";

        System.out
                  .println("[ResourceRefChecker] checkMethodInjection invoked.");
        if (mySvc3 == null) {
            result = "[ResourceRefChecker] HelloRefSupplierService mySvc3 reference is null";
            System.out.println(result);
            return result;
        }
        System.out
                  .println("[ResourceRefChecker] Getting HelloRefSupplierPort...");
        HelloRefSupplier hrs = mySvc3.getHelloRefSupplierPort();
        result = hrs.sayHello(input);
        System.out
                  .println("[ResourceRefChecker] chechMethodInjection returned.");
        return result;
    }

    private WarRefSupplierService myWarSvc1;

    @Resource
    private void setMyWarSvc1(WarRefSupplierService svc) {
        System.out.println("setMyWarSvc1 invoked");
        myWarSvc1 = svc;
    }

    /**
     * Calls an injected WAR-based service using the \@Resource annotation on a
     * setter method
     * 
     * @param input
     *            - string to be returned in "Hello, input"
     * @return String - "Hello, input"
     */
    public String checkWarMethodInjection(String input) {
        String result = "";

        System.out.println("checkWarMethodInjection invoked");
        if (myWarSvc1 == null) {
            result = "[ResourceRefChecker] WarRefSupplierService myWarSvc1 reference is null";
            System.out.println(result);
            return result;
        }
        WarRefSupplier wrs = myWarSvc1.getWarRefSupplierPort();
        result = wrs.sayHello(input);
        return result;
    }

    /*******************************************************************************************************************
     * CLASS-LEVEL INJECTION
     ******************************************************************************************************************/

    /**
     * Calls an injected EJB-based service declared in the class-level
     * \@Resource annotation
     * 
     * @param input
     *            - string to be returned in "Hello, input"
     * @return String - "Hello, input"
     */
    public String checkClassLevelEjbResource(String input) {
        String result = "";
        HelloRefSupplierService clHelloRef = null;

        try {
            javax.naming.InitialContext ic = new javax.naming.InitialContext();
            clHelloRef = (HelloRefSupplierService) ic
                                                     .lookup("java:comp/env/service/classHelloRefService");
        } catch (NamingException ex) {
            ex.printStackTrace();
            result = "A naming exception occurred";
            return result;
        }

        if (clHelloRef == null) {
            result = "WarRefSupplierService clHelloRef reference is null.";
            System.out.println(result);
            return result;
        }

        HelloRefSupplier port = clHelloRef.getHelloRefSupplierPort();
        result = port.sayHello(input);
        return result;
    }

    /**
     * Calls an injected EJB-based service declared in the class-level
     * \@Resources annotation
     * 
     * @param input
     *            - string to be returned in "Hello, input"
     * @return String - "Hello, input"
     */
    public String checkClassLevelEjbResources(String input) {
        String result = "";
        HelloRefSupplierService clEjbHelloRef = null;

        try {
            javax.naming.InitialContext ic = new javax.naming.InitialContext();
            clEjbHelloRef = (HelloRefSupplierService) ic
                                                        .lookup("java:comp/env/service/HelloRefService");
        } catch (NamingException ex) {
            ex.printStackTrace();
            result = "A naming exception occurred";
            return result;
        }

        if (clEjbHelloRef == null) {
            result = "WarRefSupplierService clEjbHelloRef reference is null.";
            System.out.println(result);
            return result;
        }

        HelloRefSupplier port = clEjbHelloRef.getHelloRefSupplierPort();
        result = port.sayHello(input);
        return result;
    }

    /**
     * Calls an injected WAR-based service declared in the class-level
     * \@Resources annotation
     * 
     * @param input
     *            - string to be returned in "Hello, input"
     * @return String - "Hello, input"
     */
    public String checkClassLevelWarResources(String input) {
        String result = "";
        WarRefSupplierService clWarHelloRef = null;

        try {
            javax.naming.InitialContext ic = new javax.naming.InitialContext();
            clWarHelloRef = (WarRefSupplierService) ic
                                                      .lookup("java:comp/env/service/WarRefService");
        } catch (NamingException ex) {
            ex.printStackTrace();
            result = "A naming exception occurred";
            return result;
        }

        if (clWarHelloRef == null) {
            result = "WarRefSupplierService clWarHelloRef reference is null.";
            System.out.println(result);
            return result;
        }

        WarRefSupplier port = clWarHelloRef.getWarRefSupplierPort();
        result = port.sayHello(input);
        return result;
    }

}
