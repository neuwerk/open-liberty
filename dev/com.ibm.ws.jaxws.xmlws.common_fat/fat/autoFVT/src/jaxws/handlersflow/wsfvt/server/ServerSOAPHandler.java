//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId         Defect          Description
// ----------------------------------------------------------------------------
// 05/10/2007  mzheng         443868          New File
//

package jaxws.handlersflow.wsfvt.server;

import jaxws.handlersflow.wsfvt.common.TestSOAPHandler;

public class ServerSOAPHandler extends TestSOAPHandler {
    public ServerSOAPHandler() {
        super();
        super.setParams("ServerSOAPHandler", "Server");
    }
}
