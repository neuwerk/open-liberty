//
// @(#) 1.1 autoFVT/src/jaxws22/customprops/wsfvt/server/CustomProps.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/15/10 11:56:55 [8/8/12 06:59:00]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date         UserId     Defect                   Description
// ----------------------------------------------------------------------
// 12/07/2010  jtnguyen    F743-23362           new file
//

package jaxws22.customprops.wsfvt.server;
import javax.jws.*;

@WebService()
public class CustomProps {
    
    public String echo(String s) {
            return s;
    }
}
