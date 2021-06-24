package annotations.webservice_g2.inheritance.server;
import javax.jws.*;

@WebService
public interface If1 {
    public String impl1echo(String s); 
    public String impl1echo2(String s);
    
    // it makes no sense to exclude something in an interface - 
    // the definition of an interface is you have to imp ALL of it.
    // inherited and excluded
    //public String impl1surpriseexclude(String s);

}
