/**
 * 
 * autoFVT/src/jaxwsejb30/ejbapps/annotations/wsr/ejbrefchecker/ReferenceChecker.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * 08/01/2008  samerrel     540905               Fixed hard coded wsdl location
 *                                               for WSRefs
 *
 */
package jaxwsejb30.ejbapps.annotations.wsr.ejbrefchecker;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.WebServiceRefs;

import jaxwsejb30.ejbapps.annotations.wsr.ejbservice.ReferenceSupplier;
import jaxwsejb30.ejbapps.annotations.wsr.ejbservice.ReferenceSupplierService;
import jaxwsejb30.ejbapps.annotations.wsr.warservice.WebReferenceSupplier;
import jaxwsejb30.ejbapps.annotations.wsr.warservice.WebReferenceSupplierService;

/*
 * This class is both a web service and a client. It uses webserviceref annotations to obtain references to another
 * service, then acts as a server to accept requests and forwards them on to ReferenceSupplierService, so we can check
 * that the injected references are valid.
 * 
 */
// here we create a new jndi reference for ReferenceSupplier
// need to throw a war ref in here too.
@WebServiceRefs( { @WebServiceRef(name = "service/FooService", type = ReferenceSupplierService.class) })
@Stateless
@WebService(wsdlLocation = "META-INF/wsdl/ReferenceCheckerService.wsdl")
public class ReferenceChecker {

    // Test SEI Type on a field
    @WebServiceRef
    private ReferenceSupplierService mySup2;

    public String checkGenSEIFieldInjection(String input) {
        String result = "";

        System.out.println("checkGenSEIFieldInjection invoked");
        if (mySup2 == null) {
            System.out.println("reference is null");
            return "Null Reference";
        }
        ReferenceSupplier port = mySup2.getReferenceSupplierPort();

        result = port.sayHello(input);
        System.out.println("service returned");
        return result;
    }

    // generated sei type on a method. The type of the input parameter is key.
    // This works.
    private ReferenceSupplierService mySup3;

    @WebServiceRef
    private void setMySupplierGenSEI(ReferenceSupplierService svc) {
        System.out.println("setMySupplier ran");
        mySup3 = svc;
    }

    public String checkGenSEIMethodInjection(String input) {
        String result = "";

        System.out.println("checkGENSEIMethodInjection invoked");

        if (mySup3 == null)
            System.out.println("reference is null");
        ReferenceSupplier port = mySup3.getReferenceSupplierPort();

        result = port.sayHello(input);
        System.out.println("service returned");
        return result;
    }

    // here we inject the service's actual Impl, using value param to say what
    // kind of service it is.
    @WebServiceRef(value = ReferenceSupplierService.class)
    private ReferenceSupplier rsImpl4;

    public String checkImplFieldInjection(String input) {
        String result = "";
        System.out.println("checkSEIFieldInjection invoked");
        if (rsImpl4 == null)
            return "reference is null";

        ReferenceSupplier port = mySup2.getReferenceSupplierPort();

        result = rsImpl4.sayHello(input);
        System.out.println("service returned");
        return result;
    }

    // actual Impl injected on a method.
    private ReferenceSupplier rsImpl5;

    @WebServiceRef(value = ReferenceSupplierService.class)
    private void setMySupplierSEI(ReferenceSupplier svc) {
        System.out.println("setMySupplierSEI ran");
        rsImpl5 = svc;
    }

    public String checkImplMethodInjection(String input) {
        String result = "";
        System.out.println("checkSEIMethodInjection invoked");
        if (rsImpl5 == null)
            return "reference is null";

        ReferenceSupplier port = mySup2.getReferenceSupplierPort();

        result = rsImpl5.sayHello(input);
        System.out.println("service returned");
        return result;
    }

    // jndi field injection of an SEI type
    @WebServiceRef(name = "service/ReferenceSupplierService")
    private ReferenceSupplierService jndiInjectedSEIServiceRef;

    public String checkGenSEIJndiFieldInjection(String input) {
        System.out.println("checkGenSEIJndiFieldInjection invoked");
        if (jndiInjectedSEIServiceRef == null)
            return "reference is null";
        ReferenceSupplier port = jndiInjectedSEIServiceRef
                                                          .getReferenceSupplierPort();

        String result = port.sayHello(input);
        System.out.println("service returned");
        return result;

    }

    // ????? - should we be able to jndi inject service refs as well as genSEI
    // refs? yes
    // jndi field injection of an impl type
    @WebServiceRef(name = "service/ReferenceSupplier", value = ReferenceSupplierService.class)
    private ReferenceSupplier jndiInjectedImplRef;

    public String checkJndiImplFieldInjection(String input) {
        System.out.println("checkGenSEIJndiFieldInjection invoked");
        if (jndiInjectedImplRef == null)
            return "reference is null";
        String result = jndiInjectedImplRef.sayHello(input);
        System.out.println("service returned");
        return result;

    }

    // jndi field injection of an impl, resource is in a war file this time.
    // note that we have to build webref's wsimport artifacts into this ear
    // so we can demarshal the response.
    @WebServiceRef(name = "service/WebReferenceSupplierService", value = WebReferenceSupplierService.class)
    private WebReferenceSupplier jndiInjectedWarImplRef;

    public String checkWarJndiImplFieldInjection(String input) {
        System.out.println("checkWarJndiImplFieldInjection invoked");
        if (jndiInjectedWarImplRef == null) {
            return "reference is null";
        }

        String result = jndiInjectedWarImplRef.sayHello(input);
        System.out.println("service returned");
        return result;

    }

    // check that the class level annotations worked.
    public String checkClassLevelEJBReference(String input) {
        String result = "";
        System.out.println("checkClassLevelEJBReference invoked");
        try {
            // look it up the old fashioned way.
            javax.naming.InitialContext ic = new javax.naming.InitialContext();
            ReferenceSupplierService s = (ReferenceSupplierService) ic
                                                                      .lookup("java:comp/env/service/FooService");
            ReferenceSupplier port = s.getReferenceSupplierPort();
            result = port.sayHello(input);
            System.out.println("service returned");
        } catch (Exception e) {
            result = "Exception occured.";
            e.printStackTrace();
            return result;
        }
        return result;
    }

    // test that we can assign to type Service, cast, and call getPort
    // broken on this, kills all session bean creation.
    // not sure it's required by the standard to work, either.
    // @WebServiceRef( type = ReferenceSupplier.class, value =
    // ReferenceSupplierService.class,
    // )
    // private Service anyService;

    // public String checkSEIFieldInjectionIntoSuperClass( String input ) {
    // System.out.println( "checkSEIFieldInjectionIntoSuperClass invoked" );
    // if ( anyService == null ) return "reference is null";
    // ReferenceSupplier port = (ReferenceSupplier) anyService
    // .getPort( ReferenceSupplier.class );
    // System.out.println( "got reference, invoking service" );
    // String result = port.sayHello( input );
    // System.out.println( "service returned" );
    // return result;
    // }
}
