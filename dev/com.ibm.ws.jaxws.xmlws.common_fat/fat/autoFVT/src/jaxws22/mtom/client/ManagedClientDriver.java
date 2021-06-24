package jaxws22.mtom.client;
import java.io.*;
import javax.xml.ws.*;
import javax.xml.ws.soap.*;


/**
 * This class invokes the CommonMTOMClient in a managed client environment.
 * Managed clients (a.k.a. launchclients) are expensive to instantiate, so 
 * for efficiency, we'll just do all the invoking here in a single main method
 * and return the result for the test cases to parse, rather than have each test method 
 * (... please wait .... ) instantiate the managed client repetitively. 
 * 
 * Note that the name of the service in application-client.xml, for example MTOMDDNoMTOM, 
 * reflects the configuration of the service, not necessarily that of the client,
 * however we try to keep them matched up so we don't go nuts.
 *  
 * The client deployment descriptors that we are testing are in 
 * client/mtom/etc/meta-inf/application-client.xml.  
 * 
 * 
 * @author btiffany
 *
 */
public class ManagedClientDriver {
    private static CommonManagedMTOMClient client =  null;
    private static ManagedClientDriver d =  null;
    private MessageCaptureHandler mch = new MessageCaptureHandler();
    private static PrintStream os;
    
    // didn't realize until too late that @webserviceref injection only 
    // works in the main class, so to test it we have to do it here 
    // instead of in the managedclient, and pass the ref in.
    // since the name matches what's in the dd, config should be looked up from there.
    // dd should win over this annotation.
    // this is covered more in WAS unit tests.
    @MTOM(enabled=false)
    @WebServiceRef(name="service/mtom_dd_enabled_return_serviceref")                   
    public static jaxws22.mtom.client.MTOMDDOnlyService MTOMDDOnlyRef;
    

    /**
     * Usage: host port 
     * returns: String indicating success of each case.
     */
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        String port = "9080";
        if( args.length== 0){
            System.out.println("usage: managedclientdriver [host] [port] ");                    
        } else{
            host = args[0];
            port = args[1];
        }
        (new ManagedClientDriver()).performTests(System.out, host, port);
   }
    
   public void performTests(PrintStream outstream, String host, String port) throws Exception {
       os = outstream;
       os.println("ManagedClientDriver is starting.");
     
       os.println("host ="+host + " port="+port);
       client = new CommonManagedMTOMClient();
       client.hostAndPort = "http://"+ host + ":" + port;
       
       d = this;
       
       if(MTOMDDOnlyRef == null){
           System.out.println("MTOMDDOnlyRef is null");           
       }
       
       // check that an injected ref overridden with an annotation work, dd should still trump anno. 
       d.checkClient("mtom_dd_enabled_return_serviceref_injected", MTOMDDOnlyRef, true, 2050);
       
      
       // next 3 check dd only around the threshold.      
       d.checkClient("mtom_dd_enabled_2048_no_annotation", false, 1000);  //failing with mtom enabled
       //System.out.println("outbound msg: "+client.getOutboundMsgAsString());       
       d.checkClient("mtom_dd_enabled_2048_no_annotation", false, 2048);
       d.checkClient("mtom_dd_enabled_2048_no_annotation", true, 2049);
       //System.out.println(d.client.getOutboundMsgAsString());
       
       
       // no dd or annotation, should not see mtom behavior. 
       d.checkClient("mtom_dd_empty_no_annotation", false, 2050);
       
       // same as mtom_dd_enabled_2048 but we get the service as a ref, not the port
       d.checkClient("mtom_dd_enabled_return_serviceref", true, 2050);
       d.checkClient("mtom_dd_enabled_return_serviceref", false, 2000);
       
       // check corner cases of no threshold and very small messages
       d.checkClient("mtom_dd_enabled_no_threshold", true, 1);
       // zero byte message should work
       d.checkClient("mtom_dd_empty_no_annotation", false, 0); // surprise, this fails.
       //System.out.println(d.client.getOutboundMsgAsString());
       //System.out.println(d.client.getInboundMsgAsString());  
   
       d.checkClient("mtom_dd_enabled_no_threshold", true, 0);
       System.out.println(d.client.getOutboundMsgAsString());
       System.out.println(d.client.getInboundMsgAsString());
        
       /**--------------------------------------------------------
        *  The following tests are ALL INVALID 
        *  because @MTOM is only valid on the webservice
        *  implementation class.  Ref. the jaxws 2.2 api.
        *  -----------------------------------------------------*/ 
        
      /*----------------- 
       // check dd can override default annotation threshold 
       d.checkClient("mtom_dd_2048_annotation_true", true, 2048);
       d.checkClient("mtom_dd_2048_annotation_true", true, 2049);
       d.checkClient("mtom_dd_2048_annotation_true", false, 2047);
       
        // check annotation only, annotation should be ignored. 
       d.checkClient("mtom_dd_empty_annotation_true", false, 512);
       
       // check dd can override annotation's true/false attribute
       d.checkClient("mtom_dd_false_annotation_true", false, 4096);     
       d.checkClient("mtom_dd_true_annotation_false", true, 4096);
       ------------------*/
    
       os.println("ManagedClientDriver is ending normally.");

   }
   
   
   private void checkClient(String service, boolean mtomExpected, int messageSize) throws Exception {
       checkClient(service, null, mtomExpected, messageSize);
       
   }
    
    /**
     * Invoke one of the client/service pairs and see if mtom usage on the outbound request is what we were expecting.
     * Print results to sysout. 
     * The testcase will be looking for the word FAILURE. 
     * @param service  - string name of service.
     * @param injectedref - an injected resource that the client will use to look up the service.
     * @param mtomExpected
     * @param messagseSize
     */
   private void checkClient(String service, Object injectedref, boolean mtomExpected, int messageSize) throws Exception {
        boolean echoOk = false;
        boolean mtomUsed = false;        
        String result = "**FAILURE**";
     
        byte [] b = d.genByteArray(messageSize);
        try{
            byte[] c = client.testProxy(service, b, injectedref);
            echoOk = d.compareByteArrays(b, c);
        } catch( Exception e){
            os.println(service +" threw unexpected Exception ");
            e.printStackTrace(os);            
            e.getCause().printStackTrace(os);
            os.flush();
            return;
        }
        mtomUsed = d.checkRequestforMTOMUsage();
        if(echoOk && mtomUsed == mtomExpected){ result = "OK"; }
        os.println(service + "_requestsize_" + messageSize + " result:"+result+ " echoOk:" +echoOk + " mtomUsed:"+mtomUsed + " mtomExpected:"+mtomExpected   );
        
    }
    
    public byte [] genByteArray(int size){
        byte [] ba = new byte[size];
        int i = 0;
        byte j = 0;
        for(i=0; i < size; i++){
            ba[i]=j++;
            if( j>250 ){ j=0;}
        }
      // System.out.println("request: "+size+" actual: "+ba.length);
       return ba;
    }
    
    public boolean compareByteArrays(byte[] expect, byte [] actual){
        boolean expectnul = false;
        boolean actualnul = false;
        if (expect== null){
            System.out.println("sent byte array was null");   
            expectnul = true;
        }
        
        if (actual== null){
            System.out.println("received byte array was null");
            actualnul = true;
            
        }
        if (expectnul && actualnul) {return true; }    // both null       
        if (expectnul | actualnul ) { return false; }  // only one is null
        
        if (expect.length != actual.length ){
            System.out.println("length mismatch, expect =" +expect.length + 
                    " actual = " + actual.length);
            return false;
        }
        int max = expect.length;
        for(int i=0; i < max; i++){
            if (expect[i] != actual [i]){
                System.out.println("content mismatch at offset "+i);
                return false;
            }            
        }
        return true;
    }
    
    /**
     * look for the mime boundary in the soap message, which would indicate mtom
     * was probably used.   (No way to tell for sure without a wire monitor)
     * @return
     */
    private boolean checkRequestforMTOMUsage(){
        if (mch.getOutboundMsgAsString().contains("_Part_")){ return true; }
        return false;
    }
    

}
