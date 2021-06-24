/*
 * tests inheritance flattening.  All 4 echo methods should appear in wsdl
 * and be invocable at runtime
 */
package annotations.webservice_g2.inheritance.tooling;
import javax.jws.*;

// this time, we use and endpointinterface anno element instead of 
// implements statement.
//
@WebService(endpointInterface="annotations.webservice_g2.inheritance.server.SEI4")
public class SEI4Impl extends Inh2 {
    
    // this should not be mapped, since it's not in either epi
	public String echo(String s){
		return ("the server replies: "+s );	
	}
	
	// implmented from If
    // we tested manually: Omit this method and make sure wsgen catches it.
	public String echoIf(String s){
		return ("the server replies: "+s );	
	}
	
	// implemented from If2
	public String echoIf2(String s){
		return ("the server replies: "+s );  
	}
    

}
