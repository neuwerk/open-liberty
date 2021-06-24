/*
 * tests inheritance flattening.  All 4 echo methods should appear in wsdl
 * and be invocable at runtime
 */
package annotations.webservice_g2.inheritance.tooling;
import javax.jws.*;

// all methods should be reachable, including echo1 and echo2 in inh2
@WebService
public class InheritImpl extends Inh2 implements InheritIf2{
	public String echo(String s){
		return ("the server replies: "+s );	
	}
	
	// implmented from If
	public String echoIf(String s){
		return ("the server replies: "+s );	
	}
	
	// implemented from If2
	public String echoIf2(String s){
		return ("the server replies: "+s );  
	}
    
    // echo2 and echo1 should also appear in wsdl as they are inherited from
    // Inh2 and Inh1 respectively

}
