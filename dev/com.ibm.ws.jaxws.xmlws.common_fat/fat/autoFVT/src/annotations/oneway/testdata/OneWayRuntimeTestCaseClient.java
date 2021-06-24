package annotations.oneway.testdata;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

/**
 * test the one way service.  if input argument is not null,
 * service should throw runtime exception
 * @author btiffany
 *
 */
public class OneWayRuntimeTestCaseClient{
	// reflection can't see the main method.
    static OneWayDefaults port = null;   
    public static void main( String [] args ){
        if ( args.length==0 ){
            System.out.println("Usage options: \n"+
                                "classname [url] [message]  [cycles] \n"+ 
                                "url = wsdl location \n"+
                                "message - some message to write to sysout, like the date and time \n" +
                                "  if message contains the string \'delay\', service will sleep for 60 sec.\n"+
                                "cycles - how many times to invoke the service \n\n"+
                                " ex: OneWayRuntimeTestCaseClient http://somewhere?blah.wsdl delay 3 \n\n\n");
        }
    	String urlString = "";
        String cycles= null; 
        int icycles = 1;
        String input = "Hello Mr. one way service, how are you?" ;        
    	if (args.length >0 ) urlString = args[0];
        if (args.length >1  ) input =  args[1];
        if (args.length >2  ) cycles = args[2]; 
        
        
        if(cycles != null) icycles = Integer.parseInt(cycles);
        
    	System.exit(runtest(urlString, input, icycles));
    	
    }
    public static int runtest(String urlString, String input, int cycles){      
	   
	   if( urlString.length()==0 ){
           System.out.println("using default url");
		   port = new OneWayDefaultsService().getOneWayDefaultsPort();		  
	   }
	   else{
         System.out.println("using supplied wsdl url: "+urlString);
         URL url = null;
         try {
             url = new URL(urlString);
         } catch (MalformedURLException e) {
             e.printStackTrace();
         }
         QName qn = new QName("http://testdata.oneway.annotations/", "OneWayDefaultsService");

         port = new OneWayDefaultsService(url, qn).getOneWayDefaultsPort();
	    
	   }
		   
       System.out.println("will invoke the service "+ cycles + "  times");
       
       // call the method in the service class
       try{
           for(int i=0; i<cycles; i++){
               System.out.println("invoking service with argument: "+ input);
               port.doSomething(input);               
               System.out.println("doSomething method returned.");
           }    
       } catch (Exception e){
    	   e.printStackTrace();
    	   return 12;   	   
       }
       return 5;       
    }
    
    public static String getOneWayStatus(){
        return port.getOneWayStatus();
    }
 }
