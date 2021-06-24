package annotations.webmethod_g2.runtime_beta.server;
import java.net.*;
import javax.xml.namespace.QName;

// dummy class for eclispe.  Don't check in. 
public class InterfaceTestImplService {

   public InterfaceTestImplService(){
       System.out.println("you shouldn't be here");
   }

   public InterfaceTestImplService(URL u, QName q){
       System.out.println("you shouldn't be here");       
   }
   
   public InterfaceTestImpl getInterfaceTestImplPort(){
       System.out.println("you shouldn't be here");      
       return new InterfaceTestImpl();
   }
}
