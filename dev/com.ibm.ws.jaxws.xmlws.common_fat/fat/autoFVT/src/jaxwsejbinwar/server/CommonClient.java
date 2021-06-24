package jaxwsejbinwar.server;
import  jaxwsejbinwar.generatedclient.*;
import java.net.*;

import javax.xml.ws.WebServiceRef;

/**
 * A class to call other web services.
 * 
 * 
 * @author btiffany
 *
 */
public class CommonClient {
    /**
     * invoke one of the other services, given it's wsdl url.
     * Based on that, we're expecting to be able to figure out which service to invoke. 
     * @param wsdlurl
     * @return
     */
    public String invoke(String wsdlurlString) throws java.lang.Exception{
        // the wsdl url's for the various services.  Need to abstract this.
        URL wsdlurl  = new URL(wsdlurlString); 

        /*
        URL siwl = new URL("http://localhost:9080/ejbinwar/ServletInWarLibHelloService?wsdl");
        URL siwc = new URL("http://localhost:9080/ejbinwar/ServletInWarClassesHelloService?wsdl");
        URL eiwl = new URL("http://localhost:9080/ejbinwar/EjbInWarLibHelloService?wsdl");
        URL eiwc = new URL("http://localhost:9080/ejbinwar/EjbInWarClassesHelloService?wsdl");
        */
        
        
            
        String s = wsdlurlString.toLowerCase();
        if(s.contains("servlet")){
             if(s.contains("lib")){
                            
                return (new ServletInWarLibHelloService(wsdlurl)).getServletInWarLibHelloPort().hello(
                         "common client call to: " +wsdlurlString);
                 
                
            } else {
                return (new ServletInWarClassesHelloService(wsdlurl)).getServletInWarClassesHelloPort().hello(
                        "common client call to: " + wsdlurlString);
                
            }
            
        } else {
            if(s.contains("lib")){
                return  (new EjbInWarLibHelloService(wsdlurl)).getEjbInWarLibHelloPort().hello(
                        "common client call to: " + wsdlurlString);
                
            } else {
                return (new EjbInWarClassesHelloService(wsdlurl)).getEjbInWarClassesHelloPort().hello(
                        "common client call to: " + wsdlurlString);
                
            }
        }
    }

    /**
     * invoke one of the other services using a reference.  Figure out 
     * which service based upon the type of the reference.
     * @param ref
     * @param in
     * @return
     * @throws java.lang.Exception
     */
 
    public String invokeUsingServiceRef(Object ref, String in) throws java.lang.Exception {
        if (ref== null){
            System.out.println("ref passed to method is null");
            
        }else {
            System.out.println("type of ref passed to method is "+ ref.getClass().toString());
            
        }
        if (ref instanceof   EjbInWarClassesHelloService ){
            return ((EjbInWarClassesHelloService)ref).getEjbInWarClassesHelloPort().hello("common client reference call to ejbinwarclasses returns: "+ in);  
        } 
        if (ref instanceof   EjbInWarLibHelloService ){
            return ((EjbInWarLibHelloService)ref).getEjbInWarLibHelloPort().hello("common client reference call to ejbinwarlib returns: "+ in);  
        } 
        if (ref instanceof   ServletInWarClassesHelloService ){
            return ((ServletInWarClassesHelloService)ref).getServletInWarClassesHelloPort().hello("common client reference call to servletinwarclasses returns: "+ in);  
        } 
        if (ref instanceof   ServletInWarLibHelloService ){
            return ((ServletInWarLibHelloService)ref).getServletInWarLibHelloPort().hello("common client reference call to servletinwarlib returns: "+ in);  
        } 
        if (ref instanceof   EjbInJarHelloService ){
            return ((EjbInJarHelloService)ref).getEjbInJarHelloPort().hello("common client reference call to ejbinjar returns: "+ in);  
        } 
         
        
        return "unsupported call, type of ref is"+ ref.getClass().toString();
        
    }

}
