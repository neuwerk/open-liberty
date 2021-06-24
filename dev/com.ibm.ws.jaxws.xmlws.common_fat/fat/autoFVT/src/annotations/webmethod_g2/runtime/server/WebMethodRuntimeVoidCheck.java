package annotations.webmethod_g2.runtime.server;
import javax.jws.*;
import javax.xml.ws.*;

// checks that we can call a method that returns void,
// and that dynamic gen wsdl works for @WebMethod. 

// have to change the namespace so client doesn't get beans mixed up with other client.
// 1/23/07 WHY? - there are no methods with the same name in both clients.  Reverting.
// 1/24/07 BECAUSE - wsimport will run for each client, if both services are 
// in same namesppace, the last call wins, and the ObjectFactory.class that gets written
// is only valid for one of the two clients. 
@WebService(targetNamespace="http://server2.runtime.webmethod_g2.annotations")

public class WebMethodRuntimeVoidCheck extends WebMethodRuntimeVoidCheckParent {

    // make sure void method works
    @WebMethod
	public void voidReturnMethod(String s){
		System.out.println("voidMethod called with argument: " + s);
	}
    
    // an ambiguious name with method that follows.
    @WebMethod
    public String echo(String s){
        return("echo replies: "+s);
    }
    
    // test disambiguation - i.e. renaming an otherwise overloaded method
    // note that due to a limitation in our imp, we cannot change package of req/resp beans.
    // we may need an @WebResult here too.
    @RequestWrapper(className="annotations.webmethod_g2.runtime.server.EchoInt")
    @ResponseWrapper(className="annotations.webmethod_g2.runtime.server.EchoIntResonse")
    @WebMethod(operationName="echoInt")
    public int echo(int s){
        return(s);
    }
    
    // test operationName
    @WebMethod(operationName="echoModified")
    public String echo2(String s){
        return("echo2 replies: "+s);
    }
    
    // test excluding inherited method
    @WebMethod(exclude=true)
    public String echoInherited(String s){
        return("should not be reachable");
    }    
        
    // test excluding method we just want blocked for some other reason
    @WebMethod(exclude=true)
    public String echoBlocked(String s){
        return("should not be reachable");
    }
    
    // nonpublic that should not be exposed
    public String nonpublic(String s){
        return s;
    }
    
}
