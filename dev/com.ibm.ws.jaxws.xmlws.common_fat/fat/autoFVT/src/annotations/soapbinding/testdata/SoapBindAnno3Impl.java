package annotations.soapbinding.testdata.server;

import javax.jws.*;

// used by SoapBindingRuntimeTestCase for beta test.
// where's the @Soap... anno?  - on the interface. 
@WebService(endpointInterface="annotations.soapbinding.testdata.server.SoapBindAnno3",
			wsdlLocation="WEB-INF/wsdl/SoapBindAnno3ImplService.wsdl")
            
public class SoapBindAnno3Impl implements SoapBindAnno3{
	
    public String echodlw(String inp){
        return("echodlw replies: "+inp);
    }

    public String echodlb(String inp){
        return("echodlb replies: "+inp);
    }
}
