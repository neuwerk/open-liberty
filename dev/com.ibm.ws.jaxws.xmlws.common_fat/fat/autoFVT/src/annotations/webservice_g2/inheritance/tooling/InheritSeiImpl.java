/*
 * tests inheritance flattening.  All 4 echo methods should appear in wsdl
 * and be invocable at runtime
 * 
 * Added this test at L.E.'s req. 10.18
 */
package annotations.webservice_g2.inheritance.tooling;
import javax.jws.*;

// all methods should be reachable, including echo1 and echo2 in inh2
@WebService(endpointInterface="annotations.webservice_g2.inheritance.tooling.InheritSei")
public class InheritSeiImpl extends Inh2 implements InheritIf2{
    
    //method defined right here
    public String echo(String s){
        return "echo replies" +s;
    }

	// implmented from If
	public String echoIf(String s){
		return ("the server replies: "+s );	
	}
	
	// implemented from If2
	public String echoIf2(String s){
		return ("the server replies: "+s );  
	}
    
    // echo2 and echo1 are implemented by extending Inh2 , so
    // should also appear in wsdl.
    
    // we have to do this, but we should not.  Def 398900
    /*
    
    public String echo1(String s){
       return super.echo1(s);
        
    }
    
    public String echo2(String s){
        return super.echo2(s);        
    }
    */
    

}
