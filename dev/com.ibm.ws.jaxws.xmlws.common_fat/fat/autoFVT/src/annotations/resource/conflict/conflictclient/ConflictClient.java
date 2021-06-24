/*
 * @(#) 1.1 autoFVT/src/annotations/resource/conflict/conflictclient/ConflictClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/22/06 11:42:56 [8/8/12 06:56:02]
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
package annotations.resource.conflict.conflictclient;

import annotations.resource.conflict.fieldconflictserver.*;
import annotations.resource.conflict.methodconflictserver.*;
       
public class ConflictClient {
	
	private static FieldConflictTypeImpl portField = 
		new FieldConflictTypeImplService().getFieldConflictTypeImplPort();
	private static MethodConflictTypeImpl portMethod = 
		new MethodConflictTypeImplService().getMethodConflictTypeImplPort();
	
	public ConflictClient(){
		//Empty body
	}
	
	public void main(String[] args) {

		System.out.println("The result from FieldConflictServer is: " + 
				getServletContextNameFromFieldConflict());

		System.out.println("\nThe result MethodInjectionServer is: " + 
				getServletContextNameFromMethodConflict());		
	}
	
    public String getServletContextNameFromFieldConflict(){    	
    	return portField.getServletContextName();
    }
    
    public String getServletContextNameFromMethodConflict(){    	
    	return portMethod.getServletContextName();
    }    
}
