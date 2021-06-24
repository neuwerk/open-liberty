package jaxwsejbinwar.server;
import javax.jws.*;
import javax.xml.ws.*;
import javax.ejb.Stateless;
import jaxwsejbinwar.generatedclient.*;  // client classes supplied by wsimport
import javax.naming.InitialContext;


@Stateless
@WebService()

public class EjbInWarLibHello {
    //  simple echo
    public String hello(String in){
        String msg = "ejbinwarlibhello hello invoked with arg: " + in;
        System.out.println(msg);
        return msg;
    }
    
    public String callAnotherHello(String servicename) throws java.lang.Exception {
        return new CommonClient().invoke(servicename);
    }  
    
    
   //@WebServiceRef(type= jaxwsejbinwar.generatedclient.EjbInWarClassesHelloService.class,  name = "service/ejbInWarClassesRef")
    
    //private Object  warSvc1;
    public String callAnotherUsingServiceRef(String in) throws java.lang.Exception {
        InitialContext ctx = new InitialContext();
        
        // checking a ref in web.xml. This should work. 
        if(in.contains("web.xml ejb reference")){
            System.out.println("web.xml ejb reference");
            jaxwsejbinwar.generatedclient.EjbInWarClassesHelloService service =                                                                                   
                (EjbInWarClassesHelloService)ctx.lookup("java:comp/env/service/"+ "metainf_webxmlref_WarClassesHelloService");        
           return new CommonClient().invokeUsingServiceRef(service, "using web.xml ejb reference " + in);
            
        }
        
        // try to use the service-ref in web-fragment.xml 
        if(in.contains("web-fragment.xml ejb reference")){
            System.out.println("web-fragment.xml ejb reference");
            jaxwsejbinwar.generatedclient.EjbInWarClassesHelloService service =                                                                                   
                (EjbInWarClassesHelloService)ctx.lookup("java:comp/env/service/"+ "metainf_webfragmentref_WarClassesHelloService");        
           return new CommonClient().invokeUsingServiceRef(service, "using web-fragment.xml ejb reference " + in);
            
        }
        
        if(in.contains("web.xml servlet reference")){
            System.out.println("web.xml servlet reference");
            jaxwsejbinwar.generatedclient.ServletInWarLibHelloService service =                                                                                   
                (ServletInWarLibHelloService)ctx.lookup("java:comp/env/service/"+ "metainf_webxmlref_ServletWarLibHelloService");        
           return new CommonClient().invokeUsingServiceRef(service, "using web.xml servlet reference " + in);
            
        }
        
        // in the second reference, we'll try a ref that's in ejb-jar that's inside a jar inside a war.
        // that should not work. 
        if(in.contains("second reference")){
            System.out.println("second reference");
            jaxwsejbinwar.generatedclient.EjbInWarClassesHelloService service = 
                (EjbInWarClassesHelloService)ctx.lookup("java:comp/env/service/"+ "metainf_EjbXmlInaJarInaWar_WarClassesHelloService");        
           return new CommonClient().invokeUsingServiceRef(service, "using second reference " + in);
            
        }
        
        // else...
        jaxwsejbinwar.generatedclient.EjbInWarClassesHelloService service = 
             (EjbInWarClassesHelloService)ctx.lookup("java:comp/env/service/"+ "metainf_ejbInWarClassesRef");        
        return new CommonClient().invokeUsingServiceRef(service, in);
    }

}