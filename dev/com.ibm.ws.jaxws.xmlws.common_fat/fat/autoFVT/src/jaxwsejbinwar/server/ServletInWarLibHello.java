package jaxwsejbinwar.server;
import javax.jws.*;
import javax.xml.ws.*;
import javax.naming.InitialContext;
import jaxwsejbinwar.generatedclient.*;

//@WebService(serviceName="HelloService", portName="HelloServicePort", name="HelloServicePortType")
@WebService()
public class ServletInWarLibHello {
    //  simple echo
    public String hello(String in){
        String msg = "servletinwarlibhello hello invoked with arg: " + in;
        System.out.println(msg);
        return msg;
    }
    
    public String callAnotherHello(String servicename) throws java.lang.Exception {
        return new CommonClient().invoke(servicename);
    }
    
   // @WebServiceRef(type= jaxwsejbinwar.generatedclient.EjbInJarHelloService.class,
   //         name = "service/metainf_EjbInJarHelloService")
   // private Object  ejbjref;
    
    public String callAnotherUsingServiceRef(String in) throws java.lang.Exception {
        InitialContext ctx = new InitialContext();
        
        EjbInWarClassesHelloService service = 
            (EjbInWarClassesHelloService)ctx.lookup("java:comp/env/service/"+ "metainf_ejbInWarClassesRef");
        return new CommonClient().invokeUsingServiceRef(service, in);
    }

   
   
}
