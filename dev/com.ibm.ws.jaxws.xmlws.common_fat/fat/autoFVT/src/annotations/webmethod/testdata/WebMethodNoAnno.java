package annotations.webmethod.testdata;
import javax.jws.*;
// all public methods should be exposed.  Non public shouldn't.
@WebService
public class WebMethodNoAnno {	
	public String echo(String s){return s;}
	public String echo2(String s){return s;}
	String echo3(String s){return s;}	
}
