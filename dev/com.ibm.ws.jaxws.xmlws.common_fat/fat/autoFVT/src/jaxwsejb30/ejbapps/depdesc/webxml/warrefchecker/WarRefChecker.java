/**
 * autoFVT/src/jaxwsejb30/ejbapps/depdesc/webxml/warrefchecker/WarRefChecker.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * 05/12/2008  samerrell    LIDB4511.45.01       New file
 * 07/31/2008  samerrel     538865               Fixed wsdl location
 *                                               problems
 *
 */
package jaxwsejb30.ejbapps.depdesc.webxml.warrefchecker;

import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.xml.ws.WebServiceRef;

import jaxwsejb30.ejbapps.depdesc.webxml.ejbservice.HelloRefSupplier;
import jaxwsejb30.ejbapps.depdesc.webxml.ejbservice.HelloRefSupplierService;
import jaxwsejb30.ejbapps.depdesc.webxml.warservice.WarRefSupplier;
import jaxwsejb30.ejbapps.depdesc.webxml.warservice.WarRefSupplierService;

/**
 * 
 * This class is both a web service and a client. It uses webserviceref
 * annotations to obtain references to another service, then acts as a server to
 * accept requests and forwards them on to {@link HelloRefSupplier} or
 * {@link WarRefSupplier} so we can check that the injected references are
 * valid.
 * 
 * @see HelloRefSupplier, WarRefSupplier
 */
@WebService(wsdlLocation = "WEB-INF/wsdl/WarRefCheckerService.wsdl")
public class WarRefChecker {

    @WebServiceRef(name = "service/webxmlHelloRefSupplierSvc")
    private HelloRefSupplierService ejbSvc1;

    /**
     * Checks to see that the name attribute of the \@WebServiceRef is
     * overridden by the service-ref/service-ref-name element in the
     * WarRefChecker web.xml.
     * 
     * @param input
     *            - the message to be passed to {@link HelloRefSupplier}
     * @return output of {@link HelloRefSupplier#sayHello(String)}
     */
    public String checkFieldNameOverride(String input) {
        System.out.println("[WarRefChecker]: In checkFieldNameOverride");
        String result = "";

        if (ejbSvc1 == null) {
            result = "[WarRefChecker]: ejbSvc1 is null";
            System.out.println(result);
            return result;
        }

        System.out.println("[WarRefChecker]: ejbSvc1.getHelloRefSupplierPort");
        HelloRefSupplier port = ejbSvc1.getHelloRefSupplierPort();

        System.out.println("[WarRefChecker]: port.sayHello");
        result = port.sayHello(input);
        return result;
    }

    @WebServiceRef(name = "service/webxmlTestWsdl", wsdlLocation = "BAD_WSDL_LOCATION")
    private HelloRefSupplierService ejbSvc2;

    /**
     * Checks to see that the wsdlLocation attribute of the \@WebServiceRef is
     * overridden by the service-ref/wsdl-file element in the WarRefChecker
     * web.xml.
     * 
     * @param input
     *            - the message to be passed to {@link HelloRefSupplier}
     * @return output of {@link HelloRefSupplier#sayHello(String)}
     */
    public String checkFieldWsdlLocationOverride(String input) {
        System.out
                  .println("[WarRefChecker]: In checkFieldWsdlLocationOverride");
        String result = "";

        if (ejbSvc2 == null) {
            result = "[WarRefChecker]: ejbSvc2 is null";
            System.out.println(result);
            return result;
        }

        System.out.println("[WarRefChecker]: ejbSvc1.getHelloRefSupplierPort");
        HelloRefSupplier port = ejbSvc2.getHelloRefSupplierPort();

        System.out.println("[WarRefChecker]: port.sayHello");
        result = port.sayHello(input);
        return result;
    }

    /**
     * Checks to see that {@link HelloRefSupplier} can be called directly using
     * a <code>JNDI Lookup</code>. The <code>JNDI</code> name is defined by the
     * service-ref/service-ref-name element in the WarRefChecker's web.xml file.
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
                                                      .lookup("java:comp/env/service/webxmlHelloRefSupplierSvc");
            port = ejbSvc.getHelloRefSupplierPort();
            result = port.sayHello(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @WebServiceRef(name = "service/webxmlWarRefSupplierSvc")
    private WarRefSupplierService warSvc1;

    /**
     * Checks to see that the name attribute of the WAR-based \@WebServiceRef is
     * overridden by the service-ref/service-ref-name element in the
     * WarRefChecker web.xml.
     * 
     * @param input
     *            - the message to be passed to {@link WarRefSupplier}
     * @return output of {@link WarRefSupplier#sayHello(String)}
     */
    public String checkNameOverrideWar(String input) {
        String result = "";

        if (warSvc1 == null) {
            result = "[WarRefChecker]: warSvc1 is null";
            System.err.println(result);
            return result;
        }

        WarRefSupplier port = warSvc1.getWarRefSupplierPort();
        result = port.sayHello(input);
        return result;
    }
}
