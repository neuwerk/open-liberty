/*
 * A class for testing dynamic generation of wrapper beans, 
 *  a jax-ws conformance requirement, 3.6.2.1.
 *  
 * Each method can throw an exception so we can obtain the 
 * stack trace and see what marshalling method is being used,
 * to assure the code is flowing correctly. 
 * 
 *   
 */
package jaxws.dynamicbeangen.server;
import javax.jws.*;
import javax.xml.ws.*;
import java.util.*;
import java.io.*;

// annotate in such a way that multiple classes use the same wsdl and client
@WebService(name="DynamicBeans", portName="DynamicBeansPort", serviceName="DynamicBeansService")
public class DynamicNoWsdl {
    public static boolean collectStackTraceOnInvokes = true;  // save a captured stack trace
    public static String stackTraceBuffer = null;             // save it here. 

    public String getStackTrace(){                            // return the stack trace we captured on previous call
        return stackTraceBuffer;      
    }
    
    // throw then catch an exception to gather up the stack trace
    // and determine which marshaller we were using. 
    // The stack trace can be obtained by calling  getStackTrace
    private void throwExceptionifRequested(){
        if(collectStackTraceOnInvokes){ 
            stackTraceBuffer = null;
            try{  
                throw new WebServiceException("let us discover the stack trace");
            } catch (Exception e){
               StringWriter sw = new StringWriter();
               e.printStackTrace(new PrintWriter(sw));
               stackTraceBuffer = sw.toString();             
            }            
        }       
    }
    
    // test polymorphism
    public ExtClass echoPolyMorphExtClass(ExtClass in){
        throwExceptionifRequested();
        return in;
    }
    
    // test anytype
    public Object echoAnyTypeObject(Object in){
        throwExceptionifRequested();
        return in;
    }
    
    // test list of strings
    public List<String> echoListofStrings(List<String> in){
        throwExceptionifRequested();
        return in;
    }
    
    // test array of strings
    public String[] echoArrayofStrings(String[] in){
        throwExceptionifRequested();
        return in;
    }
    
    // test primitive
    public int echoInt(int in){
        throwExceptionifRequested();
        return in;
    }
    
    // test non-default values for @request, responsewrapper
    @RequestWrapper(className="jaxws.dynamicbeangen.AnotherRequestWrapper")
    @ResponseWrapper(className="jaxws.dynamicbeangen.server.AnotherResponseWrapper")
    public String echoNonDefaultBeanNames(String in){
        throwExceptionifRequested();
        return in;
    }
    
}
