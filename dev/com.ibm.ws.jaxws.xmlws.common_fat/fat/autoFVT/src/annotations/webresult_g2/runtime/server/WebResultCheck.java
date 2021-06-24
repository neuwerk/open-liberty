package annotations.webresult_g2.runtime.server;

import javax.jws.*;
import javax.jws.soap.*;

// used to check that a method with fully specified webresult anno can be invoked
// change namespace to avoid collision with beta client.
@WebService(targetNamespace="server2.runtime.webresult_g2.annotations")
public class WebResultCheck{
    
   // check defaults. 
   public CustomerRecord locateCustomer(
       String firstName, String lastName, String address){
       return new CustomerRecord(firstName, lastName, address);
    }


   // check every parameter except header.
   // on header methods, we can't use multiple parameters because that violates WSI.
   // "multiple part WSDL files are not allowed for a Document/Literal Bare request"
   // (since it's converted to bare, non-wrapped style, the multi-part response
   //  gets reflected as multiple input params on the client side )
   // We'll try to work around this by forcing it to bare style.
   /*
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)   
    @WebResult(name="locateCustomerb",
              partName="locateCutomerb_parameters",
              targetNamespace="annotations.webresult_g2.runtime.newns",
              header=true)
   
   */
   //@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)   
   @WebResult(name="locateCustomerb",
           partName="locateCutomerb_parameters",
           targetNamespace="annotations.webresult_g2.runtime.newns",
           header=false)
   
   public CustomerRecord locateCustomer2(
       String firstName){
       return new CustomerRecord(firstName, "tbd", "mystery");
    }
   
   // check header parameter.
   // this also produces a multi-parameter method call on the client side
   // which we we refuse to invoke because it violates WSI.  So, it looks like
   // we just can't support header=true.  Will open defect for review.
   // 1.3.07 - let's try it in bare style, ref 402362. 
   @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)   
   @WebResult(name="notreturn", header=true)
   public String echo( String s){
       return "The server replies: "+s;
   }
   
}
