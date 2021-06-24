package annotations.webmethod.testdata;
import javax.jws.*;
// a web service that tries to produce two operations of the same name
// This should be caught by tooling.
 

@WebService()
public class WebMethodNameClash1 {
	
	// this one may or may not be mapped to wsdl. 224 sec 2.3.
	public String noAnno(String in) {
		return "you said: "+in;
	}
	
	@WebMethod() public String defaultAnno(String in) {
		return "you said: "+in;
	}
	
	// the method name in the wsdl should be overriden by the annotation.
	// which will cause a name clash.
	@WebMethod(operationName="defaultAnno", action="idunno", exclude=false)
	public String fullAnno(String in)  {
		return "you said: "+in;
	}
}
