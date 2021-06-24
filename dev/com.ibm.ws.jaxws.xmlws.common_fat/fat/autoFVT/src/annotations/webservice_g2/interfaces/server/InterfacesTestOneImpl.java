package annotations.webservice_g2.interfaces.server;

import javax.jws.*;
// since we must use wsimport to gen our sei for beta, 
// the endpointinterface will wind up being the generated sei, not ours.
// That constrains what we can specify for the sei package name. 
@WebService(targetNamespace="server.interfaces.webservice_g2.annotations",
			serviceName="ifimplsvc",
			portName="iftest",
			endpointInterface="annotations.webservice_g2.interfaces.server.InterfaceTestOne"
            )
// note that the implements statement is missing, indeed it's not required. 		
public class InterfacesTestOneImpl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}	
	
	// implemented from interface
	public String bareIfMethod(String s){return  s;}

	// implemented from interface	
	public String annoIfMethod(String s){return s;}
	
	// implemented from interface.
	public String annoIfExcluded(String s){ return s; }
	
	//@WebMethod - anno here illegal due to endpointinterface
	public int implMethod(int x){
		return x + 1;
	}

	// not public, should not be exposed.
	int implNoAnno(int x){
		return x+1 ;
	}

}
