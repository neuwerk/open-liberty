/* sei for beta test */
package annotations.webparam.testdata;
import javax.jws.*;

@WebService
public interface WPBIf {
    
    @WebResult(name="notreturn")
    public String echo(String s); 
    
    @WebResult(name="notreturn")
    public String echo2(@WebParam(name="s2") String s);
    
    @WebResult(name="notreturn")
    public String echo3(@WebParam(targetNamespace="annotations.webparam.testdata2") String s);

}
