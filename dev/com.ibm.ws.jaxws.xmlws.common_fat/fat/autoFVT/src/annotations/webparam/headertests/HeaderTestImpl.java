/*
 * 2.15.07 - rename so junit quits trying to run it. 
 * 2.23.07 - remove wsdlloc attrib
 */


package annotations.webparam.headertests;
import javax.jws.*;


@WebService(endpointInterface="annotations.webparam.headertests.HeaderTestIf")
public class HeaderTestImpl{


   public String inputHeaderOutputBody(   String s ){
      String msg =  "InputHeaderOutputBody received: "+s ;
       System.out.println(msg);
       return msg;
   }

   public String inputBodyOutputBody( String s   ){
       String msg = "InputBodyOutputBody received: "+s ;
       System.out.println(msg);
       return msg;
    }

   public String nextInputBodyOutputBody( String s   ){
       String msg = "nextInputBodyOutputBody received: "+s ;
       System.out.println(msg);
       return msg;
    }

   public String inputBodyOutputHeader( String s ){
       String msg =  "InputBodyOutputHeader received: "+s ;
       System.out.println(msg);
       return msg;
   }

   /*
   public String inputHeaderOutputHeader( String s ){
       String msg =  "InputHeaderOutputHeader received: "+s ;
       System.out.println(msg);
       return msg;
   }
   */
}
