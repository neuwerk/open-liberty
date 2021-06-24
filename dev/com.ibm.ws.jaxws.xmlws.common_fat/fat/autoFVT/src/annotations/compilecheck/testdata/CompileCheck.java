package annotations.compilecheck.testdata;

/**
 * A test Class  to make sure all the annotations compile. 
 * 
 * If it compiles, we pass. 
 */

// leave the *'s on the import statements - it helps expose any duplicates. 
import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.*;
import junit.framework.*;

@ServiceMode()
@WebFault()
@WebServiceClient()
@WebServiceProvider()
@BindingType()
@WebServiceRef()
@WebService()
@SOAPBinding()
@WebServiceRefs({})
@HandlerChain(file="")
public class CompileCheck  extends com.ibm.ws.wsfvt.test.framework.FVTTestCase
{
	@RequestWrapper()
	@ResponseWrapper()
	@WebEndpoint(name = "")
	@WebMethod()
	@Oneway()
	@WebResult()
	@SOAPBinding()
	public void method1(@WebParam() int a)
	{
		
	}
	
	@RequestWrapper(localName = "a", targetNamespace = "b",
			className = "c")
    @ResponseWrapper(localName = "d", targetNamespace = "e",
			 className = "f")
    @WebEndpoint(name = "h")
    
    @WebMethod(operationName = "i", action = "j", 
    		   exclude = true)
    @Oneway()
    @WebResult(name = "k", targetNamespace = "l", 
    		   header = true, partName = "m")
    @SOAPBinding(style = SOAPBinding.Style.RPC,    	
    			use = SOAPBinding.Use.ENCODED,
    			parameterStyle=SOAPBinding.ParameterStyle.BARE )
    public void method2(@WebParam() int a)
    {

    }
}

@ServiceMode(value = Service.Mode.MESSAGE)
@WebFault(name = "n", targetNamespace = "o", faultBean = "p")
@WebServiceClient(name = "q", targetNamespace = "r", 
				  wsdlLocation= "s")

@WebServiceProvider(wsdlLocation = "t", serviceName = "u",
					portName = "v", targetNamespace = "w")
@BindingType(value = "x")
@WebServiceRef(name = "y", wsdlLocation = "z", 
			   type = Object.class, value = Service.class,
			   mappedName = "1")
@WebService(name = "2", targetNamespace = "3", serviceName = "4",
			wsdlLocation = "5", endpointInterface = "6", 
			portName = "7")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.ENCODED, 
			 parameterStyle = SOAPBinding.ParameterStyle.BARE)
@HandlerChain(file = "2", name = "3")
class AnnotationTest2
{
}
