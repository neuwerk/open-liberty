package annotations.webservice_g2.inheritance.tooling;

import javax.jws.*;
@WebService
public class Inh2 extends Inh1 {
        @WebMethod
		public String echo2(String s){
			return("echo2 replies: s");
		}
}
