package annotations.webmethod.testdata;
import javax.jws.*;
// a trivial webservice with all parms defined for webmethod annotation.
// This one has no public methods, nothing should be exposed.  

@WebService()
public class WebMethodNopub {
	
	// this one may or may not be mapped to wsdl. 224 sec 2.3.
	// jsr181 suggests that it should be mapped, ref sec 3.2
	String noAnno(String in) {
		return "you said: "+in;
	}
	
	@WebMethod() String defaultAnno(String in) {
		return "you said: "+in;
	}
	
	// the method name in the wsdl should be overriden by the annotation.
	@WebMethod(operationName="annotatedfullAnno", action="idunno", exclude=false)
	String fullAnno(String in)  {
		return "you said: "+in;
	}
	
	// the methodname should come through intact
	// the "action" parm apparently gets put into the http header.
	// it's pretty obscure, but some apps could use it to filter requests. 
	
	@WebMethod( action="idunno", exclude=false)
	String someAnno(String in)  {
		return "you said: "+in;
	}
	
	// this one should not appear in wsdl. 
	@WebMethod(exclude=true)
	String excludeme(String in)  {
		return "you said: "+in;
	}
	
	// in w2j, this one should be renamed, "get" should be removed
	@WebMethod()
	String getsomething(String in)  {
		return "";
	}
	
	//	 in w2j, this one should be renamed, "put" should be removed
	@WebMethod()
	String putsomething(String in)  {
		return "";
	}
	
	// in w2j, this one should be renamed as it conflicts with a method in javax.xml.ws.Binding
	// should be prepended with _
	
	@WebMethod()
	String getBinding(String in)  {
		return "";
	}

}
