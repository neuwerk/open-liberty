//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId       Defect          Description
// ----------------------------------------------------------------------------
// 04/20/2007  mzheng       LIDB3296-40.01  New File
//

package jaxws.clienthandlers.wsfvt.client;

import jaxws.clienthandlers.wsfvt.common.WASSOAPHandler;

public class ClientSOAP11Handler extends WASSOAPHandler {
    public ClientSOAP11Handler() {
        super();
        super.setParams("ClientSOAP11Handler", "Client");
    }
}
