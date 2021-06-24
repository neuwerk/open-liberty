package annotations.webservice.testdata;
// test the webservice annotation with all defaults.
// the package name has to match it's target location, or classloader won't be able to load it.
import javax.jws.*;
//@Bogus()
@WebService()
public class WebServiceMatchesWsdl1{
    @WebMethod()
    public String echo(String e){
        return e;
    }
}
