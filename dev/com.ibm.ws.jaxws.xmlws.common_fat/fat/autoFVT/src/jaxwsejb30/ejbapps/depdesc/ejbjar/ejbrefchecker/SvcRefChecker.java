/**
 * autoFVT/src/jaxwsejb30/ejbapps/depdesc/ejbjar/ejbrefchecker/SvcRefChecker.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * [ddmmyyyy]  samerrell    [defect]             [description]
 * 07/31/2008  samerrel     538865               Fixed wsdl location
 *                                               problems
 *
 */
package jaxwsejb30.ejbapps.depdesc.ejbjar.ejbrefchecker;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.xml.ws.WebServiceRef;

import jaxwsejb30.ejbapps.depdesc.ejbjar.warservice.WarRefSupplier;
import jaxwsejb30.ejbapps.depdesc.ejbjar.warservice.WarRefSupplierService;
import jaxwsejb30.ejbapps.depdesc.ejbjar.ejbservice.HelloRefSupplier;
import jaxwsejb30.ejbapps.depdesc.ejbjar.ejbservice.HelloRefSupplierService;

/**
 * @author samerrel
 * 
 *         Checks the service-ref entries override the default JNDI binding.
 * 
 */
@Stateless
@WebService(wsdlLocation = "META-INF/wsdl/SvcRefCheckerService.wsdl")
public class SvcRefChecker {

    public SvcRefChecker() {
    }

    @WebServiceRef(name = "service/myHelloRefSupplierSvc")
    private HelloRefSupplierService ejbSvc1;

    /**
     * Checks to see that the name attribute of the \@WebServiceRef is
     * overridden by the service-ref/service-ref-name element in the
     * SvcRefChecker's ejb-jar.xml file.
     * 
     * @param input
     *            - the message to be passed to {@link HelloRefSupplier}
     * @return output of {@link HelloRefSupplier#sayHello(String)}
     */
    public String checkFieldNameOverride(String input) {
        System.out.println("[SvcRefChecker]: In checkFieldNameOverride");
        String result = "";

        if (ejbSvc1 == null) {
            result = "[SvcRefChecker]: ejbSvc1 is null";
            System.out.println(result);
            return result;
        }

        System.out.println("[SvcRefChecker]: ejbSvc1.getHelloRefSupplierPort");
        HelloRefSupplier port = ejbSvc1.getHelloRefSupplierPort();

        System.out.println("[SvcRefChecker]: port.sayHello");
        result = port.sayHello(input);
        return result;
    }

    @WebServiceRef(name = "service/testWsdl", wsdlLocation = "BAD_WSDL_LOCATION")
    private HelloRefSupplierService ejbSvc2;

    /**
     * Checks to see that the wsdlLocation attribute of the \@WebServiceRef is
     * overridden by the service-ref/wsdl-file element in the SvcRefChecker's
     * ejb-jar.xml file.
     * 
     * @param input
     *            - the message to be passed to {@link HelloRefSupplier}
     * @return output of {@link HelloRefSupplier#sayHello(String)}
     */
    public String checkFieldWsdlLocationOverride(String input) {
        System.out
                  .println("[SvcRefChecker]: In checkFieldWsdlLocationOverride");
        String result = "";

        if (ejbSvc2 == null) {
            result = "[SvcRefChecker]: ejbSvc2 is null";
            System.out.println(result);
            return result;
        }

        System.out.println("[SvcRefChecker]: ejbSvc1.getHelloRefSupplierPort");
        HelloRefSupplier port = ejbSvc2.getHelloRefSupplierPort();

        System.out.println("[SvcRefChecker]: port.sayHello");
        result = port.sayHello(input);
        return result;
    }

    /**
     * Checks to see that {@link HelloRefSupplier} can be called directly using
     * a <code>JNDI Lookup</code>. The <code>JNDI</code> name is defined by the
     * service-ref/service-ref-name element in the SvcRefChecker's ejb-jar.xml
     * file.
     * 
     * @param input
     *            - the message to be passed to {@link HelloRefSupplier}
     * @return output of {@link HelloRefSupplier#sayHello(String)}
     */
    public String checkJndiLookup(String input) {
        InitialContext initCtx = null;
        HelloRefSupplierService ejbSvc = null;
        HelloRefSupplier port = null;
        String result = "";

        try {
            initCtx = new InitialContext();
            ejbSvc = (HelloRefSupplierService) initCtx
                                                      .lookup("java:comp/env/service/myHelloRefSupplierSvc");
            port = ejbSvc.getHelloRefSupplierPort();
            result = port.sayHello(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @WebServiceRef(name = "service/myWarRefSupplierSvc")
    private WarRefSupplierService warSvc1;

    /**
     * Checks to see that the name attribute of the WAR-based \@WebServiceRef is
     * overridden by the service-ref/service-ref-name element in the
     * SvcRefChecker's ejb-jar.xml file.
     * 
     * @param input
     *            - the message to be passed to SvcRefChecker
     * @return output of {@link WarRefSupplier#sayHello(String)}
     */
    public String checkNameOverrideWar(String input) {
        String result = "";

        if (warSvc1 == null) {
            result = "[SvcRefChecker]: warSvc1 is null";
            System.err.println(result);
            return result;
        }

        WarRefSupplier port = warSvc1.getWarRefSupplierPort();
        result = port.sayHello(input);
        return result;
    }
}
