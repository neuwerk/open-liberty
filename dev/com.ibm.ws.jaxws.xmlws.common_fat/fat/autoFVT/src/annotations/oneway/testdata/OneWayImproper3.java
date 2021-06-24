package annotations.oneway.testdata;
import javax.jws.*;
import java.io.*;

/*
 * A simple class to test the one way annotation
 * throws an exception, so should fail.
 */
@WebService()
public class OneWayImproper3 {
	@WebMethod
	@Oneway()		// yes, it's lower case in the early r.i. 
	public void doSomething() throws FileNotFoundException {
		
	}

}
