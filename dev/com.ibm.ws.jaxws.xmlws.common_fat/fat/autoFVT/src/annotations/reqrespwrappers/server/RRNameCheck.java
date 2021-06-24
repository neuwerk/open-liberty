/*
 * @(#) 1.2 autoFVT/src/annotations/reqrespwrappers/server/RRNameCheck.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/5/07 14:42:55 [8/8/12 06:55:26]
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
 * see cmvc history        LIDB3296.31.01     new file
 *
 */

package annotations.reqrespwrappers.server;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;


/**
 * use req/respwrapper to rename beans and see if it works.
 * Skip the other stuff for beta.
 * Would have tried overload resolution but we already know 
 * WebMethod is busted. 
 * @author btiffany
 *
 */
@WebService(wsdlLocation="WEB-INF/wsdl/rro.wsdl",
            endpointInterface="annotations.reqrespwrappers.server.RRNif")
public class RRNameCheck {   

    public boolean echo(boolean input){ return input; }
    
    public String  echo2(String input){ return "server says:"+input; }
        
}
