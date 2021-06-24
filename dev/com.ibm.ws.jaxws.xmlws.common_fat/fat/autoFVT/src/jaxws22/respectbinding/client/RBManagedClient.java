package jaxws22.respectbinding.client;

import jaxws22.respectbinding.server.*;

import java.io.*;
import java.net.URL;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.ws.*;
import javax.xml.ws.soap.MTOM;

import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * This class will invoke a variety of clients configured
 * with various deployment descriptors and wsdls and
 * produce results for evaluation by a junit test.
 * 
 * Note that there is no corresponding service.  We just see if the wsdl bindings 
 * are validated and we can get as far as "connection refused".  Testing the actual
 * behavior of various required binding extensions (addressing, wssec, etc.) is beyond
 * the scope of this test.  
 * 
 * That will allow it to be packaged in either a thin client jar, a war, or an
 * ejb ear.
 * 
 * @author btiffany
 *
 */
public class RBManagedClient {
    PrintStream os = null;
    
    @RespectBinding(enabled=true)
    @WebServiceRef(wsdlLocation="META-INF/wsdl/ValidRequired.wsdl", value=jaxws22.respectbinding.server.EchoService.class)
    public static jaxws22.respectbinding.server.Echo ValidRequiredInjectedEchoPort;
    
    @RespectBinding(enabled=true)
    @WebServiceRef(wsdlLocation="META-INF/wsdl/InvalidRequiredonBinding.wsdl", value=jaxws22.respectbinding.server.EchoService.class)
    public static jaxws22.respectbinding.server.Echo InvalidRequiredInjectedEchoPort;

    /**
     * @param args
     */
    public static void main(String[] args) throws java.lang.Exception  {
               
        (new RBManagedClient()).performTests(System.out);
    }
    
    public void performTests(PrintStream os) throws java.lang.Exception {
        this.os = os;
        
        // perform each test in sequence. 
        
        // no extension dd true, should invoke
        lookupAndInvoke("NoExtensionDDTrue", true);
        
        // invalid required dd true on binding fail
        lookupAndInvoke("InvalidRequiredonBindingDDTrue", false);
        
        
        // invalid required dd true on operation fail
        lookupAndInvoke("InvalidRequiredonOperationDDTrue", false);
        
        // invalid required dd true on input fail
        lookupAndInvoke("InvalidRequiredonInputDDTrue", false);
        
        // invalid required dd true on output fail         
        lookupAndInvoke("InvalidRequiredonOutputDDTrue", false);
        
        // invalid required dd true on fault fail
        lookupAndInvoke("InvalidRequiredonFaultDDTrue", false);
        
        // invalid required no feature - probably pass
        lookupAndInvoke("InvalidRequiredNoDD", true);
        
        // InvalidRequiredDDFalse - ok
        lookupAndInvoke("InvalidRequiredDDFalse", true);
        
        // ValidRequiredDDTrue - ok
        lookupAndInvoke("ValidRequiredDDTrue", true);
        
        // ValidRequiredDDTrueDispatch - ok , do last
        
       
        
        
        // these next two test  pure injections with the feature
        // set by annotating the injection.
        // validRequiredWSrTrueok        
        if (ValidRequiredInjectedEchoPort != null) {
            lookupAndInvoke("ValidRequiredInjectedTrue", true, ValidRequiredInjectedEchoPort);
        } else {
            os.println("injection failed");
        }

        // invalid req wsr true fail
        if (InvalidRequiredInjectedEchoPort != null){
            lookupAndInvoke("InvalidRequiredInjectedTrue", false, InvalidRequiredInjectedEchoPort);
        } else {
            os.println("injection failed");
        }       
    
        
        //TODO: Testmultiport
        
        // test factory methods.  This may have to be refactored for other containers. 
        testFactoryConfiguration("META-INF/wsdl/ValidRequired.wsdl", true);
        testFactoryConfiguration("META-INF/wsdl/InvalidRequiredonBinding.wsdl", false);
        
        // check multiple bindings - one should not spill over to the other.
        // one is valid and one is not. 
        QName secport = new QName("http://server.respectbinding.jaxws22/", "SecondaryEchoPort");
        QName priport = new QName("http://server.respectbinding.jaxws22/", "EchoPort");
        
        testMultiPortFactoryConfiguration("META-INF/wsdl/ValidAndInvalidMultiBinding.wsdl", priport, true );
        testMultiPortFactoryConfiguration("META-INF/wsdl/ValidAndInvalidMultiBinding.wsdl", secport, false );
        

    } 
    
    /**
     * this tests a factory method, not a deployment descriptor. 
     * @param wsdl - wsdl file to use in src/jaxws22/respectbinding/client/wsdl directory.
     * @param shouldInvoke - true if service should invoke 
     * @throws java.lang.Exception
     */
    public void testMultiPortFactoryConfiguration(String wsdlFile, QName qn, boolean shouldInvoke) throws java.lang.Exception {
        boolean invoked = false;    
       String result  = "*** FAILURE ***";
       // System.out.println("classpath is: "+ System.getProperty("java.class.path"));
        QName q = new QName("http://server.respectbinding.jaxws22/",
                "EchoService");
        URL u = new URL("file:" + wsdlFile);
        Service s = Service.create(u, q);
        // add the feature on getport. Unlike service.create, which should not
        // work, this should.
        Echo port = s.getPort(qn, Echo.class, new RespectBindingFeature(true));
        // Echo port = s.getPort(Echo.class, new RespectBindingFeature(true));
        try {
            port.echo("hello"); // this should make it onto the wire.
        } catch (WebServiceException e) {
            invoked= wasConnectionRefused(e);              
            if((invoked && shouldInvoke) || (!invoked && !shouldInvoke)){ result = "--- passed ----"; }
            
            // future: test for meaningful exception here if we do NOT invoke.
            
            os.println("\n"+ wsdlFile + " port:"+qn.getLocalPart() + " result:" + result + " invoke_expected: "+shouldInvoke +" invoke_actual: "+ invoked +"\n");
            os.flush();
            return;
   
        }
        // print failure msg that exception wasn't caught.
        os.println(wsdlFile + " result:" + result + " invoked: NO EXCEPTION CAUGHT");
        os.flush();   
    }    
    
    
    /**
     * this tests a factory method, not a deployment descriptor. 
     * @param wsdl - wsdl file to use in src/jaxws22/respectbinding/client/wsdl directory.
     * @param shouldInvoke - true if service should invoke 
     * @throws java.lang.Exception
     */
    public void testFactoryConfiguration(String wsdlFile, boolean shouldInvoke) throws java.lang.Exception {
        boolean invoked = false;    
                String result  = "*** FAILURE ***";
       // System.out.println("classpath is: "+ System.getProperty("java.class.path"));
        QName q = new QName("http://server.respectbinding.jaxws22/",
                "EchoService");
        URL u = new URL("file:" + wsdlFile);
        Service s = Service.create(u, q);
        // add the feature on getport. Unlike service.create, which should not
        // work, this should.
        Echo port = s.getPort(Echo.class, new RespectBindingFeature(true));
        try {
            port.echo("hello"); // this should make it onto the wire.
        } catch (WebServiceException e) {
            invoked= wasConnectionRefused(e);              
            if((invoked && shouldInvoke) || (!invoked && !shouldInvoke)){ result = "--- passed ----"; }
            
            // future: test for meaningful exception here if we do NOT invoke.
            
            os.println("\n"+ wsdlFile + " result:" + result + " invoke_expected: "+shouldInvoke +" invoke_actual: "+ invoked +"\n");
            os.flush();
            return;
   
        }
        // print failure msg that exception wasn't caught.
        os.println(wsdlFile + " result:" + result + " invoked: NO EXCEPTION CAUGHT");
        os.flush();   
    }
    
    private void lookupAndInvoke(String testname, boolean shouldInvoke) throws java.lang.Exception {
        lookupAndInvoke(testname, shouldInvoke, null);
    }
    
    /**
     * for a given client, look it up from  deployment descriptor file, attempt
     * to invoke it, and see if we get as far as connection refused, which
     * would mean the runtime decided it was valid to invoke.
     * 
     * Print results to the outoput stream. 
     * @param testname - name of test which matches jndi name in deployment descriptor
     * @param shouldInvoke - true if client should attempt to invoke service
     * @pram  port - port of service.  If null, we'll look it up, otherwise skip lookup and just use it.
     */
    private void lookupAndInvoke(String testname, boolean shouldInvoke, Echo port) throws java.lang.Exception {  
        boolean invoked = false;     
        String result  = "*** FAILURE ***";
        if (port == null){
            InitialContext ctx = new InitialContext();
            port = (Echo)ctx.lookup("java:comp/env/service/"+ testname);
        }    
        try {
            port.echo("FAT test hello for "+testname); 
        } catch (WebServiceException e) {               
               invoked= wasConnectionRefused(e);              
               if((invoked && shouldInvoke) || (!invoked && !shouldInvoke)){ result = "--- passed ----"; }
               
               // future: test for meaningful exception here if we do NOT invoke.
               
               os.println("\n"+testname + " result:" + result + " invoke_expected: "+shouldInvoke +" invoke_actual: "+ invoked +"\n");
               os.flush();
               return;
        }
        // print failure msg that exception wasn't caught.
        os.println(testname + " result:" + result + " invoked: NO EXCEPTION CAUGHT");
        os.flush();  
    }
    
    /**
     * expect exception to see if it was due to connection refused.
     * 
     * @param e
     * @return
     */
    private boolean wasConnectionRefused(java.lang.Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        // System.out.println(sw.toString());
        if (sw.toString().contains("ConnectException")) {
            return true;
        } else {
            return false;
        }

    }    

}
