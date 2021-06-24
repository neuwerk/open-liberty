package annotations.webresult_g2.runtime.server2;

import javax.jws.*;
import javax.xml.ws.*;
import javax.jws.soap.*;

// used to check that a method with fully specified webresult anno can be invoked

// change namespace to avoid collision with beta client.
// this caused all sorts of problems with xjc, 419201, so we refactored the
// service into a differnt package.
//@WebService(targetNamespace="server2.runtime.webresult_g2.annotations")


@WebService()
public class WebResultCheck{
    
   // check defaults. 
  //@ResponseWrapper(className="annotations.webresult_g2.runtime.server2.LocateCustomerResponse")
   public CustomerRecord locateCustomer(
       String firstName, String lastName, String address){
       return new CustomerRecord(firstName, lastName, address);
    }

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
   // our imp. can't demarshal a bare java.lang.anything, so use a bean. 
   
   @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)   
   @WebResult(name="notreturn", header=true)
   public MyString echo( MyString s){
       s.setString("server replies: "+s.getString());
       return s;
   }
   
}
