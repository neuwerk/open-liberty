package annotations.webmethod.testdata;
import javax.jws.*;
// a trivial webservice with all parms defined for webmethod annotation.
// This one has a method renamed to force a wsdl mismatch 

@WebService()
public class WebMethodRuntime {
	
	// this one should get mapped. 224 sec 2.3.
	public String noAnno(String in) throws java.lang.Exception{
		return "noAnno responds with you said: "+in;
	}
	
	// this should get mapped.
	@WebMethod() public String defaultAnno(String in) throws java.lang.Exception{
		return "defaultAnno responds with you said: "+in;
	}
	
	// this one should reflect the name spec'd. in the anno.
	@WebMethod(operationName="twicere___named__Anno", action="idunno", exclude=false)
	public String fullAnno(String in) throws Exception {
		return "renamedAnno responds with you said: "+in;
	}
	
	// this public method should not be visible in the wsdl or the client. 
	@WebMethod(exclude=true)
	public String excludeAnno(String in) throws java.lang.Exception {
		return "you said: "+in;
	}
}
