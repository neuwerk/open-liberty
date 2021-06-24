/*
 * @(#) 1.1 autoFVT/src/annotations/resource/injection/injectionclient/InjectionClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 11:43:09 [8/8/12 06:56:03]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 10/23/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.resource.injection.injectionclient;

import annotations.resource.injection.fieldinjectionserver.*;
import annotations.resource.injection.methodinjectionserver.*;
       
public class InjectionClient {
	
	private static FieldInjectionImpl portField = 
		new FieldInjectionImplService().getFieldInjectionImplPort();
	private static MethodInjectionImpl portMethod = 
		new MethodInjectionImplService().getMethodInjectionImplPort();
	
	public InjectionClient(){
		//Empty body
	}
	
	public void main(String[] args) {

		System.out.println("The result from FieldInjectionServer is: " + 
				getServletContextNameFromFieldInj());
		System.out.println("@PostConstruct is visited on FieldInjectionServer : " + 
				getPCVisitedFromFieldInj());
		
		System.out.println("\nThe result MethodInjectionServer is: " + 
				getServletContextNameFromMethodInj());
		System.out.println("@PostConstruct is visited on MethodInjectionServer : " + 
				getPCVisitedFromMethodInj());
		
	}
	
    public String getServletContextNameFromFieldInj(){    	
    	return portField.getServletContextName();
    }
    
    public String getServletContextNameFromMethodInj(){    	
    	return portMethod.getServletContextName();
    }    

    public boolean getPCVisitedFromFieldInj(){    	
    	return portField.isPCvisited();
    }
    
    public boolean getPCVisitedFromMethodInj(){    	
    	return portMethod.isPCvisited();
    }    

}
