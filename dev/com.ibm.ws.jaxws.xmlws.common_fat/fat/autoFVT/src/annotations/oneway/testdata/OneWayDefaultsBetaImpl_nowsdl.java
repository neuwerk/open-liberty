package annotations.oneway.testdata;
import javax.jws.*;

/*
 * A simple class to test the one way annotation
 * Note that we customize servicename and portname so we can replace
 * another class with this one and recycle the wsdl and clients - hey, these
 * annotations really are useful after all! 
 * 
 */
@WebService(endpointInterface="annotations.oneway.testdata.OneWayDefaults",
            serviceName="OneWayDefaultsService",
            portName="OneWayDefaultsPort")
            
public class OneWayDefaultsBetaImpl  {
    static String oneWayStatus = "";
	
	// this should be legal and convert to wsdl
	public void doSomething(String input)  {
		// hopefully this will land in some log that we can check
		System.out.println(input+ " OneWayDefaults invoked");
        oneWayStatus = "invoked";
        
        // if requested, sleep for two minutes
        try{
            if (input.contains("delay")){
                oneWayStatus = "sleeping";
                System.out.println(input+" OneWayDefaults starting 1 minute sleep");
                if (input.contains("delay")) Thread.sleep(60000);
                System.out.println(input + " OneWayDefaults ending 1 minute sleep");
            }
            oneWayStatus = "done";            
        } catch( InterruptedException e) {
            throw new RuntimeException("sleep interrupted unexpectedly");
        }
		
		if (input.contains("exception")){
			throw new RuntimeException("here is the exception you requested");
		}
		
	}
    
    public String getOneWayStatus(){
        return oneWayStatus;        
    }    

}
