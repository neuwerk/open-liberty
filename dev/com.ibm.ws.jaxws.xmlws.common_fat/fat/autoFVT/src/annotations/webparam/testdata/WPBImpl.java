/* impl class for beta test */

package annotations.webparam.testdata;
import javax.jws.*;


@WebService(wsdlLocation="WEB-INF/wsdl/WPBImplService.wsdl",
            endpointInterface="annotations.webparam.testdata.WPBIf",
            serviceName="WPBImplService",
            targetNamespace = "http://testdata.webparam.annotations/")
public class WPBImpl {
    
    public String echo(String s){ return("server echo method returns: "+s); }
    
    public String echo2(String s){ return("server echo method returns: "+s); } 
    
    public String echo3(String s){ return("server echo method returns: "+s); } 

}
