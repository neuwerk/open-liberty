package annotations.webservice_g2.inheritance.server;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

// testing if we can use an sei that was composed with inheritance.
// change servicename and portname so we can reuse bottomimpl's client for this test.    
@WebService(endpointInterface="annotations.webservice_g2.inheritance.server.If2",            
            serviceName="BottomImplService", portName="BottomImplPort")

public class BottomImplWithSei {
    // 1.19.07 changed return signatures to be distinct from BottomImpl
    
    public String impl1echo(String s) { return "SEIimpl1echo:"+ s ; } 
    public String impl1echo2(String s) { return "SEIimpl1echo2:"+ s ; } 
    public String impl2echo (String s) { return "SEIimpl2echo:"+ s ; }   
    public String echoBottom(String s ) {return "SEIechoBottom:" + s; }    
}
