package annotations.resource.primitiveval.fieldprimitivevalserver;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class FieldPrimitiveValImpl {

	@Resource(name="port", type=java.lang.Integer.class)
	private int port;
	
	@WebMethod
	public int getInjectedNumber(){
		return port;
	}
	
}
