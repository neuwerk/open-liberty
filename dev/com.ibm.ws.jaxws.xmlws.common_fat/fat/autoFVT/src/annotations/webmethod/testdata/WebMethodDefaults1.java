package annotations.webmethod.testdata;
import javax.jws.*;
import java.lang.Exception;
// a trivial webservice with all parms defined for webmethod annotation.
// Implementation should be able to form this into proper wsdl, which we will check.  

@WebService()
public class WebMethodDefaults1 {
	
	// this one should not be mapped to wsdl since an annotatoin is present within
	// the class.
	public String noAnno(String in) throws java.lang.Exception{
		return "you said: "+in;
	}
	
	@WebMethod() public String defaultAnno(String in) throws Exception{
		return "you said: "+in;
	}
	
	// the method name in the wsdl should be overriden by the annotation.
	@WebMethod(operationName="annotatedfullAnno", action="idunno", exclude=false)
	public String fullAnno(String in) throws Exception {
		return "you said: "+in;
	}
	
	// the methodname should come through intact
	// the "action" parm apparently gets put into the http header.
	// it's pretty obscure, but some apps could use it to filter requests. 
	
	@WebMethod( action="idunno", exclude=false)
	public String someAnno(String in) throws Exception {
		return "you said: "+in;
	}
	
	// this one should not appear in wsdl. 
	@WebMethod(exclude=true)
	public String excludeme(String in) throws Exception {
		return "you said: "+in;
	}
	
	// in w2j, this one should be renamed, "get" should be removed
	@WebMethod()
	public String getsomething(String in) throws Exception {
		return "";
	}
	
	//	 in w2j, this one should be renamed, "put" should be removed
	@WebMethod()
	public String putsomething(String in) throws Exception {
		return "";
	}
	
	// in w2j, this one should be renamed as it conflicts with a method in javax.xml.ws.Binding
	// should be prepended with _
	
	@WebMethod()
	public String getBinding(String in) throws Exception {
		return "";
	}

}
