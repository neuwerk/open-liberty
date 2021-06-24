package annotations.webparam.headertests;
import javax.jws.*;
import javax.xml.namespace.QName;
import java.net.*;


public class HeaderTestClient{
    public static void main (String [] args){
       String url=null;
       if (args.length > 0 ) url = args[0];
       HeaderTestClient c = new HeaderTestClient();
       String result = null;
       
       System.out.println("client classpath is: "+ System.getProperty("java.class.path"));
       
       System.out.println(result = c.run(url));
       
       // if we've been given an expected result, compare to it.
       int finalrc = 0;
       if(args.length >1){
           if (result.compareTo(args[1])!=0){
               System.out.println("expected result= "+args[1]);
               System.out.println("actual result= "+result);
               System.out.println("WRONG RESULT");
               finalrc = 12;
           }
       }
       else {
           String expected = "InputHeaderOutputBody received: null   ";
           // for negative tests, we'd better not get any responses
           System.out.println("expected result is >"+expected+"<");
           System.out.println("actual result is   >"+result+"<");
          // if (result.length()!=3) finalrc = 10;
           if(!(result.compareTo(expected)==0) ) { finalrc = 10; }
           
           
       }
       System.exit(finalrc);
    }

     public String run(String urlString){

        annotations.webparam.headertests.HeaderTestIf s = null;
        if (urlString == null ){
            System.out.println("using default wsdl url");
            s = new HeaderTestImplService().getHeaderTestImplPort();
        } else {
            
            //urlString = "file:///c:/tmp/webparam/HeaderTestImplService.wsdl";
            System.out.println("using supplied wsdl url: "+urlString);
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            QName qn = new QName("http://headertests.webparam.annotations/", "HeaderTestImplService");

            s = new HeaderTestImplService(url, qn).getHeaderTestImplPort();
        }
        String result = "";
        String msg = "Hello there service";
        String response= "";

        System.out.println("invoking inputHeaderOutputBody "+msg);
        try{
            response = s.inputHeaderOutputBody(msg);
        } catch( Exception e ){
            // 647521 - we get back a null now. 
            System.out.println("caught -un-expected exception");
            e.printStackTrace(System.out);
            System.out.println("\n\n");
            
        }
        System.out.println("received response: **"+response+"**");
        result += response+" ";
        response="";


        System.out.println("invoking inputBodyOutputBody "+msg);
        try{
            response = s.inputBodyOutputBody(msg);
        } catch( Exception e ){
            System.out.println("caught expected exception");
            e.printStackTrace(System.out);
            System.out.println("\n\n");
        }
        System.out.println("received response: **"+response+"**");
        
        result += response+" ";
        response="";


        System.out.println("invoking inputBodyOutputHeader "+msg);
        try{
            response = s.inputBodyOutputHeader(msg);
        } catch( Exception e ){
            System.out.println("caught expected exception");
            e.printStackTrace(System.out);
            System.out.println("\n\n");
        }
        System.out.println("received response: **"+response+"**");        
        result += response+" ";

        return result;
    }

}

