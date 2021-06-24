/*
 * @(#) 1.2 autoFVT/src/annotations/reqrespwrappers/server/RRNif.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/5/07 14:42:59 [8/8/12 06:55:26]
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

@WebService()
public interface RRNif {
    
    // add wrappers that should cause beans to be renamed.
    @RequestWrapper(className="annotations.reqrespwrappers.server.Xecho")
    @ResponseWrapper(className="annotations.reqrespwrappers.server.XechoResponse")
    @WebResult(name="notreturn")
    boolean echo(boolean b);
    
    @RequestWrapper(className="annotations.reqrespwrappers.server.Xecho2")
    @ResponseWrapper(className="annotations.reqrespwrappers.server.XechoResponse2")
    @WebResult(name="notreturn")
    @WebMethod String  echo2(String s);
    
}
