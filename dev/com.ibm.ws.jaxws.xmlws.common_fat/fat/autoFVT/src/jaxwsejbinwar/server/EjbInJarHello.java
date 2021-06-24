package jaxwsejbinwar.server;

import javax.ejb.Stateless;
import javax.jws.WebService;

@Stateless
@WebService()
public class EjbInJarHello {
  //  simple echo
  public String hello(String in){
      String msg = "ejbinjarhello hello invoked with arg: " + in;
      System.out.println(msg);
      return msg;
  }
  
  public String callAnotherHello(String servicename) throws Exception {
      return new CommonClient().invoke(servicename);
  }
  
  public String callAnotherUsingServiceRef(String in)throws java.lang.Exception { return in;}
  
  
}  