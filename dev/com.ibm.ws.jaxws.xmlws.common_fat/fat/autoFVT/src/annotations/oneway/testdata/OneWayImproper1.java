package annotations.oneway.testdata;
import javax.jws.*;

/*
 * A simple class to test the one way annotation
 */
@WebService()
public class OneWayImproper1 {
	
	// this should be legal and convert to wsdl
	@WebMethod()
	@Oneway()		// yes, it's lower case in the early r.i. 
	public int doSomething(){
		return 3;
		
	}

}
