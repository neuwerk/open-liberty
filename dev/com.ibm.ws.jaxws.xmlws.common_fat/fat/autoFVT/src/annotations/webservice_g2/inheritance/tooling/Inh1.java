package annotations.webservice_g2.inheritance.tooling;
import javax.jws.*;

@WebService
public class Inh1  {
		public String echo1(String s){
			return("echo1 replies: s");
		}
}
