package annotations.oneway.testdata;
import javax.jws.*;

/*
 * A simple interface to test the one way annotation
 * We will discard this and replace it with the one produced by
 * wsimport.  This is just to generate a wsdl to get us started.
 * 
 * To be sneaky and reuse the client, we rename it right before we compile.
 * 
 */
@WebService()
public interface OneWayDefaults  {
	@WebMethod()
	@Oneway()		
	public void doSomething(String x);
    
    @WebMethod()
    public String getOneWayStatus();
}
