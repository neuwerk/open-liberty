package jaxwsejbinwar.server;
import javax.jws.*;
import javax.naming.InitialContext;
import javax.xml.ws.*;

import jaxwsejbinwar.generatedclient.*;

//@WebService(serviceName="HelloService", portName="HelloServicePort", name="HelloServicePortType")
@WebService
public class ServletInWarClassesHello {
    //  simple echo
    public String hello(String in){
        String msg = "servletinwarclasseshello hello invoked with arg: " + in;
        System.out.println(msg);
        return msg;
    }
    public String callAnotherHello(String servicename) throws java.lang.Exception {
        return new CommonClient().invoke(servicename);
    }    
    
    
    public String callAnotherUsingServiceRef(String in)throws java.lang.Exception {
        InitialContext ctx = new InitialContext();
        if(in.contains("web.xml servlet reference") ){            
            jaxwsejbinwar.generatedclient.ServletInWarLibHelloService service =  
                (ServletInWarLibHelloService)ctx.lookup("java:comp/env/service/"+ "metainf_webxmlref_ServletWarLibHelloService"); 
            return new CommonClient().invokeUsingServiceRef(service, "from servletinwarclasses:" +in);
            
        }
        
        if(in.contains("web.xml ejb reference") ){            
            jaxwsejbinwar.generatedclient.EjbInWarClassesHelloService service =  
                (EjbInWarClassesHelloService)ctx.lookup("java:comp/env/service/"+ "metainf_webxmlref_WarClassesHelloService"); 
            return new CommonClient().invokeUsingServiceRef(service, "from servletinwarclasses:" +in);
            
        }
        
        // else....
        jaxwsejbinwar.generatedclient.EjbInWarClassesHelloService service = 
            (EjbInWarClassesHelloService)ctx.lookup("java:comp/env/service/"+ "metainf_ejbInWarClassesRef");        
        return new CommonClient().invokeUsingServiceRef(service,"from a servlet using a ejb serviceref: "+ in);
       
   }
}