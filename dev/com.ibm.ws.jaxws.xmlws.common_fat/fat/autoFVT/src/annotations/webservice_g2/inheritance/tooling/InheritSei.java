/*
 * tests inheritance flattening.  All 4 echo methods should appear in wsdl
 * and be invocable at runtime
 * This interface is used by InheritSeiImpl.java
 */
package annotations.webservice_g2.inheritance.tooling;
import javax.jws.*;

// 
@WebService
public interface InheritSei { 
	public String echo(String s);
    
	// implmented from If
	public String echoIf(String s);
    
	// implemented from If2
	public String echoIf2(String s);  
   
    
    // echo2 and echo1 should also appear in wsdl as they are inherited from
    // Inh2 and Inh1 respectively
    public String echo1( String s);
    public String echo2( String s);

}
