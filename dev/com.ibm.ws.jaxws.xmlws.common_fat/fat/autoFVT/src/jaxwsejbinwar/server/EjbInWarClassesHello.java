package jaxwsejbinwar.server;
import javax.jws.*;
import javax.xml.ws.*;
import javax.ejb.Stateless;
import jaxwsejbinwar.generatedclient.*;

@Stateless
//@WebService(serviceName="HelloService", portName="HelloServicePort", name="HelloServicePortType")
@WebService()
public class EjbInWarClassesHello {
    //  simple echo
    public String hello(String in){
        String msg = "ejbinwarclasseshello hello invoked with arg: " + in;
        System.out.println(msg);
        return msg;
    }
    
    public String callAnotherHello(String servicename) throws java.lang.Exception {
        return new CommonClient().invoke(servicename);       
    } 
    
    // test that pure webservice ref injection works, nothing in dd for this one.  
    @WebServiceRef 
    private ServletInWarLibHelloService service;
    
    public String callAnotherUsingServiceRef(String in)throws java.lang.Exception {
        return service.getServletInWarLibHelloPort().hello(in);
    }
}