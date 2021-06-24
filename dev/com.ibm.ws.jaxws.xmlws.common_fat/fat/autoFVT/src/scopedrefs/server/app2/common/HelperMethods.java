package scopedrefs.server.app2.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.naming.InitialContext;
import javax.xml.ws.BindingProvider;

/**
 * contains some helper methods for looking up and invoking services that can
 * be used by extending this class.  To avoid the tangle of webservice method inheritance,
 * these methods should be called by a public method in the subclass if needed.
 * @author btiffany
 *
 */
public class HelperMethods {
    
    /**
     * when app2 is installed on a different server, we need to set this so 
     * the generated clients will use the correct port, instead of the burned in port
     * of server 1.  
     */
    protected static String app2HttpPort = null;
    
    /**
     * invoke the identify method on any impl we can get a jndi ref to. 
     * @param jndiref
     * @return
     */
    protected String lookupAndInvokeAnyJndiRef(String jndiref){
        System.out.println("HelperMethods.lookupAndInvokeAnyJndiRef called with argument: "+ jndiref);        
        
        try{
             InitialContext ctx = new InitialContext();        
             Object ref  = ctx.lookup(jndiref);
             
             if (ref  == null){ return "lookup returned null";   }
             if (ref == ""){ return "lookup returned empty string";   }
             String result = castAndInvokeIdentifyMethodOnReference(ref);
             //String result = b2.getB2Port().identify();
             System.out.println("lookupandinvoke returned: " +result);
             return result;
        } catch (Exception e){  // write to sysout and send back to client. 
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            System.out.println("caught unexpected exception:");
            e.printStackTrace(System.out);
            e.printStackTrace(ps);
            ps.close();
            return "Service impl got this exception: \n" + baos.toString();  // send stacktrace back to caller 
        }
    }
    
    /**
     * given something looked up by jndi, cast it to the correct type, 
     * then invoke the identify method of that service.
     * @param jndiref
     * @return the result of jndiref.getsomeport.identify()
     */
    private String castAndInvokeIdentifyMethodOnReference(Object jndiref){
        
        if  (jndiref instanceof scopedrefs.generatedclients.app1.mod1.b1.B1Service){
            return ((scopedrefs.generatedclients.app1.mod1.b1.B1Service)jndiref).getB1Port().identify();
        }
        if  (jndiref instanceof scopedrefs.generatedclients.app1.mod1.b2.B2Service){
            return ((scopedrefs.generatedclients.app1.mod1.b2.B2Service)jndiref).getB2Port().identify();
        }
        if  (jndiref instanceof scopedrefs.generatedclients.app1.mod2.b1.B1Service){
            return ((scopedrefs.generatedclients.app1.mod2.b1.B1Service)jndiref).getB1Port().identify();
        }
        if  (jndiref instanceof scopedrefs.generatedclients.app1.mod2.b2.B2Service){
            return ((scopedrefs.generatedclients.app1.mod2.b2.B2Service)jndiref).getB2Port().identify();
        }
        if  (jndiref instanceof scopedrefs.generatedclients.app1.mod3.b1.B1Service){
            return ((scopedrefs.generatedclients.app1.mod3.b1.B1Service)jndiref).getB1Port().identify();
        }
        if  (jndiref instanceof scopedrefs.generatedclients.app1.mod3.b2.B2Service){
            return ((scopedrefs.generatedclients.app1.mod3.b2.B2Service)jndiref).getB2Port().identify();
        }
        
        // for the multi-server tests, app2 may be installed on a different server,
        // so we will modify the port number of the endpoint if app2HttpPort has been set non-null.  
        
        if  (jndiref instanceof scopedrefs.generatedclients.app2.mod1.b1.B1Service){
            scopedrefs.generatedclients.app2.mod1.b1.B1 port = 
                ((scopedrefs.generatedclients.app2.mod1.b1.B1Service)jndiref).getB1Port();
            BindingProvider bp = (BindingProvider)port;
            String endpoint = (app2HttpPort==null? null: "http://127.0.0.1:" + app2HttpPort + "/scopedrefsapp2mod1/B1Service");
            modifyHttpPort(bp, endpoint);
            return port.identify();
        }
        if  (jndiref instanceof scopedrefs.generatedclients.app2.mod1.b2.B2Service){
            scopedrefs.generatedclients.app2.mod1.b2.B2 port = 
                ((scopedrefs.generatedclients.app2.mod1.b2.B2Service)jndiref).getB2Port();
            String endpoint = (app2HttpPort==null? null:"http://127.0.0.1:" + app2HttpPort + "/scopedrefsapp2mod1/B2Service");
            BindingProvider bp = (BindingProvider)port;
            modifyHttpPort(bp, endpoint);
            return port.identify();            
        }
        
        if  (jndiref instanceof scopedrefs.generatedclients.app2.mod3.b1.B1Service){
            scopedrefs.generatedclients.app2.mod3.b1.B1 port = 
                ((scopedrefs.generatedclients.app2.mod3.b1.B1Service)jndiref).getB1Port();
            String endpoint = (app2HttpPort==null? null:"http://127.0.0.1:" + app2HttpPort + "/scopedrefsapp2mod3/B1Service");
            BindingProvider bp = (BindingProvider)port;
            modifyHttpPort(bp, endpoint);
            return port.identify();            
        }
        if  (jndiref instanceof scopedrefs.generatedclients.app2.mod3.b2.B2Service){
            scopedrefs.generatedclients.app2.mod3.b2.B2 port = 
                ((scopedrefs.generatedclients.app2.mod3.b2.B2Service)jndiref).getB2Port();
            String endpoint = (app2HttpPort==null? null:"http://127.0.0.1:" + app2HttpPort + "/scopedrefsapp2mod3/B2Service");
            BindingProvider bp = (BindingProvider)port;
            modifyHttpPort(bp, endpoint);
            return port.identify();            
        }

        throw new RuntimeException("jndiref could not be cast to any known service type");
        
    }
    
    /**
     * update the port number of an endpoint. 
     * @param bp
     * @param port
     */
    private void modifyHttpPort(BindingProvider bp, String newendpoint){
        if (newendpoint == null){ return; }     
        System.out.println("updating endpoint to: " + newendpoint);
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, newendpoint);
    }

}
