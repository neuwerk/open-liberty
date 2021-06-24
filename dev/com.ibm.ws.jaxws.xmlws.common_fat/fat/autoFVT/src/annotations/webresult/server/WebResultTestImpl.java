package annotations.webresult.server;
import javax.jws.soap.SOAPBinding;
import javax.jws.*;

@WebService
public class WebResultTestImpl{

    // annotations are missing and this should be allowed since all values are default.
     public String echo(String parm){
        return parm;
    }
    
    // annotation present but should behave like it was missing, assume all defaults
     @WebResult
    public String echomore(String parm){
    	return parm;
    }
    
     // partname should be ignored here
    @WebMethod
    @WebResult(name="echo111InvocationResult", partName="echo111Result")
    public String echo111(String parm){
        return parm;
    }
    @WebMethod
    @WebResult(targetNamespace="echo222ns")
    public String echo222(String parm){
        return parm;
    }
    
    /**----------
     * this is out until it works in wsgen.
     * will need to add coverage back into fromjava and from wsdl cases.
    @WebMethod
    // we have to set partName or it defaults to "return", illegal keyword.
    @WebResult(header=true, partName="xxreturn")
    public String echo333(String parm){
        return parm;
    }
    ------------*/

    // partname is only looked at if rpc or bare
    @SOAPBinding(style=SOAPBinding.Style.DOCUMENT,
                 use=SOAPBinding.Use.LITERAL,
                 parameterStyle=SOAPBinding.ParameterStyle.BARE)
    @WebMethod
    @WebResult(name="echo444InvocationResult", partName="echo444result")
    public String echo444(String parm){
        return parm;
    }
}
