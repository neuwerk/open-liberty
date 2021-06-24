package annotations.oneway.testdata;
import javax.jws.*;

/*
 * A simple class to test the one way annotation
 */
//@WebService(portName="OneWayDefaultsPort")
@WebService()
public class OneWayDefaults  {
    static String oneWayStatus = "";
	
	// this should be legal and convert to wsdl	
	@Oneway()		
	public void doSomething(String input)  {
		// hopefully this will land in some log that we can check
		System.out.println(input +" OneWayDefaults invoked");

        
        // if requested, sleep for 1 minute
        try{
            // don't want multiple invokes getting each others' status mixed up.
            synchronized(this){
                oneWayStatus = "invoked";
                if (input.contains("delay")){
                        oneWayStatus = "running";
                        System.out.println(input + " OneWayDefaults starting 1 minute sleep");            
                        Thread.sleep(60000);
                        System.out.println(input + " OneWayDefaults ending 1 minute sleep");
                    }
                oneWayStatus = "done";
            } // end sync block
        } catch( InterruptedException e) {
            throw new RuntimeException("sleep interrupted unexpectedly");
        }
		
		if (input.contains("exception")){
			throw new RuntimeException("here is the exception you requested");
		}
		
	}
    
    /*
     * A method to help us run the test. On Z, it is diffult to look at the 
     * server log to see if the method ran synchronously or not, so we
     * will get the method state here.  If we can get control back on the client and
     * call this method while
     * the oneway method is still running, then it must be running asynchronously
     * and the test should pass.
     */
    public String getOneWayStatus(){
        return oneWayStatus;        
    }

}
