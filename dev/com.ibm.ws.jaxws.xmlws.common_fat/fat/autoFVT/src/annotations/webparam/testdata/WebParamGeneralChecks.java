package annotations.webparam.testdata;
import javax.jws.soap.SOAPBinding;
import javax.jws.*;
import javax.xml.ws.Holder;
/**
 * check that java annotated with some likely conditions can be generated into
 * useful wsdl, deployed, and invoked.
 * 
 * CAUTION: IF YOU CHANGE THIS FILE, 
 * CAREFULLY PROPAGATE ALL CHANGES INTO THE MUTATED FILES AS WELL.
 * THIS FILE IS  USED BY BOTH THE RUNTIME AND TOOLING CASES.
 * 
 * @author btiffany
 *
 */
@WebService
public class WebParamGeneralChecks {
	/**
	 * take an object and just return it 
	 */
	@WebMethod String sayTime(){
		return "now"; 
	}
	
	@WebMethod public Object echoObject1 (@WebParam() Object obj){
		return obj;
	}
	
	// client should see obj2 as param name
	@WebMethod public Object echoObject2 (@WebParam(name="obj2") Object o){
		return o;
	}
    
    // the IN mode param is superfluous here, use it and make sure it doesn't cause any trouble
	@WebMethod public Object echoObject4 (@WebParam(name="obj", mode= WebParam.Mode.IN) Object o){
        return o;
    }
	
	
	// use a holder so we can check inout mode.
	@WebMethod public void  echoObject3 (@WebParam(name="obj3", mode= WebParam.Mode.INOUT) Holder<Object> o){
        o.value = new String("Holder updated:" +o.value) ;        
	}
	
	
	// an OUT holder makes no sense because that has to be the method return. 
	//@WebMethod public void echoObject5 (@WebParam(name="obj", mode= WebParam.Mode.OUT) Holder<Object> o){		
	//}
	
	// change the namespace
	@WebMethod public Object echoObject6 (@WebParam(name="obj", targetNamespace="foo.myns") Object o){
		return o;
	}
	
    // change the header mode
	@WebMethod public Object echoObject7 (@WebParam(name="obj", header=true) Object o){
		return o;
	}
	
    // change the partName, which is only meaningful in BARE mode, so change that too. 
    @SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
	@WebMethod public Object echoObject8 (@WebParam(name="obj", partName="fobj") Object o){
		return o;
	}
    
    // see if we can invoke when only the partname is changed.
    @SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
    @WebMethod public Object echoObject9 (@WebParam(partName="fobj999") Object o){
        return o;
    }

	

}
