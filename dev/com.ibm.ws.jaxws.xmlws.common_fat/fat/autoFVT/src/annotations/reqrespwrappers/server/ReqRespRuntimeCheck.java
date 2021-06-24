package annotations.reqrespwrappers.server;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

/* in sun, doclitbare style just ignores the second method.
 * however it throws exception in doclitwrapped style. 
 */


@WebService
// this is the default soapbinding, just restated here in case someone wants to 
// experiment with other bindings. 
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, 
			  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
/**
 * test class to 
 * verify that we can pass all these parameters out to doclitwrapped
 * wsdl properly.  This class should generate something very close to
 * W2JWrapperCheck.wsdl
 * 
 * 3.7.2007 Failing on s0709.11. Added echo1b, echo3 methods to give a little more granular testing.
 * 
 *
 */			  
public class ReqRespRuntimeCheck {
	// apply annotations with no params to make sure defaults work.
	@RequestWrapper
	@ResponseWrapper
    /*
    @RequestWrapper(localName = "echo1", targetNamespace = "http://server.reqrespwrappers.annotations/", className = "annotations.reqrespwrappers.server.jaxws.Echo1")
    @ResponseWrapper(localName = "echo1Response", targetNamespace = "http://server.reqrespwrappers.annotations/", className = "annotations.reqrespwrappers.server.jaxws.Echo1Response")
    */
	public String echo1(String parm){		
		return ("you passed me a string: "+parm);
	}
    
    // let's see if we can handle an object 
    public String echo1b(Object parm){       
        return ("you passed me an Object: "+parm.toString());
    }
	
	/* adding this would allow echo1 name to be overloaded, 
	 * wsdl to be generated, and maybe even service to work:
	 * @WebMethod(operationName="echoObject")
	 * We're also supposed to be able to overload by specifying classnames,
	 * but that does not work.  However it's not required by the standard. 
	 *
	 *  localname=notresponse will be without a package, we'll see if that works.
	 */
    
    /**
     * Talking with Nikhil T. 1.5.07, we concluded this is a misuse of the annotation that should not be supported.
     * JSR 224 states that in the start from java case, only the classname attribute is intended to be used, and
     * only to resolve overloading conflicts.  Furthermore, the way jaxb works is to look down the package path
     * of the SEI, so if the package of the class is changed here to mismatch both 
     * the sei package and the namespace inferred package, jaxb can't find the bean.  These would be difficult 
     * cases to get to work, and we can't think of any useful purpose for having them work.
     * 
     *  Hmm, I'm not entirely convinced.  A deployment/invoke algorithm that might work is -
     *   deploy:
     *   - register location of all beans by reading request and resp wrapper annotations.
     *   invoke: 
     *   - find operation
     *   - see if beans are in use at all
     *   - look up location of beans
     *   - use beans.
     *   
     *  This same thing happens with fault beans as well. 
     *  
     *  
	@RequestWrapper(localName="notarg0",
					targetNamespace="annotations.reqrespwrappers.server2req",
					className="echo2input")
	@ResponseWrapper(localName="notresponse",
					targetNamespace="annotations.reqrespwrappers.server2resp",
					className="funny.echo2output") 
                    
      2007.03.07 - per jsr224 7.3
      "When staring from java... only the classname element is required in this
      case" -- This test is therefore invalid and the client attempts it
      but does not check it's return code.                                  
    **/                                    
    /* here is a legal use.  Localname is just names inside the xml and shouldn't have any effect on the generated java.  */
    @RequestWrapper(localName="notarg0",            
            className="annotations.reqrespwrappers.server.echo2input")
    @ResponseWrapper(localName="notresponse",            
            className="annotations.reqrespwrappers.server.echo2output")                   
    
	public String echo2(Object parm){
		return ("you passed me an object: "+parm.toString());
	}
    
    // let's see if we can handle just the classname changed:
    @RequestWrapper(className="annotations.reqrespwrappers.server.echo3input")
    @ResponseWrapper(className="annotations.reqrespwrappers.server.echo3output")                   
    
    public String echo3(Object parm){
        return ("you passed me an object: "+parm.toString());
    }

	

}
