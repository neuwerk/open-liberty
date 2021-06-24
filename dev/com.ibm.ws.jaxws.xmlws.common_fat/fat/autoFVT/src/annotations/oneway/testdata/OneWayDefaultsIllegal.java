package annotations.oneway.testdata;
import javax.jws.*;

/*
 * A simple class to test the one way annotation
 * @WebMethod is missing on doSomething, so it should not be exposed. 
 * (The missing @WebMethod will slip thorugh wsgen, 386252)
 */
@WebService()
public class OneWayDefaults {
	
	// missing @WebMethod
	
	@Oneway()		// yes, it's lower case in the early r.i. 
	public void doSomething(String input)  {
		// hopefully this will land in some log that we can check
		System.out.println("OneWayDefaults invoked with: "+input);
		
		if (input.contains("exception")){
			throw new RuntimeException("here is the exception you requested");
		}
		
	}
    
    
    @WebMethod()
    public void doNothing(String input){
        System.out.println("OneWayDefaults.donothing invoked with: "+input);
    }

}
