package annotations.oneway.testdata;
import javax.jws.*;

/*
 * A simple class to test the one way annotation
 * Lacks an @WebMethod annotation so should fail.
 */
@WebService()
public class OneWayImproper2 {	
	@Oneway()		 
	public void doSomething(){}  

}
