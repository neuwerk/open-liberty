/**
 * This is what webEndpointTestImplService.wsdl represents.
 * Included for reference, Not required for any tests.
 */
package annotations.webendpoint.testdata;
import javax.jws.*;
import javax.xml.ws.*;

@WebService
public class _WebEndpointTestImpl {
	public String echo(String s){
		System.out.println("you said:"+s);
		return "you said: "+s;
	}
	
	public int echoi(int s){
		System.out.println("you supplied integer:" + s);
		return s*2;		
	}


}
