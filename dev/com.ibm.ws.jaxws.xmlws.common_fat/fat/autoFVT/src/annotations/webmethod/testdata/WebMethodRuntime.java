package annotations.webmethod.testdata;
import javax.jws.*;
// a trivial webservice with all parms defined for webmethod annotation.
// we will check that the runtime respects the annotation parameters.
// rev 9.5.06 - add wsdlocation param for beta.
// rev 12.1.06 - remove throws exception to workaround 409192


@WebService(wsdlLocation="WEB-INF/wsdl/WebMethodRuntimeService.wsdl")
public class WebMethodRuntime {
	
	// this one should NOT get mapped  - JSR 181 sec 3.1 refers
    // to JSR 250 2.1.2, which explains this. 
	public String noAnno(String in) {
		return "noAnno responds with you said: "+in;
	}
	
	// this should get mapped.
	@WebMethod() public String defaultAnno(String in){
		return "defaultAnno responds with you said: "+in;
	}
	
	// this one should reflect the name spec'd. in the anno.
    // and the soap:action field should be in the wsdl and not break things.
	@WebMethod(operationName="renamedAnno", action="idunno", exclude=false)
    //@WebMethod(operationName="renamedAnno", exclude=false)
	public String fullAnno(String in){
		return "renamedAnno responds with you said: "+in;
	}
	
	// this public method should not be visible in the wsdl or the client. 
	@WebMethod(exclude=true)
	public String excludeAnno(String in) {
		return "you said: "+in;
	}
}
