//
// @(#) 1.1 autoFVT/src/mtom21/threshold/wsfvt/server/VerifyMTOMProxyServerThreshold.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:40:06 [8/8/12 06:41:02]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 03/10/08 jramos      LIDB4418-12.01  New File

package mtom21.threshold.wsfvt.server;

import java.util.Iterator;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.soap.MTOM;

@WebService(
        targetNamespace = "urn://VerifyMTOMProxyServer.mtom21.test.org",
        serviceName = "VerifyMTOMProxyServerThreshold",
        portName = "VerifyMTOMProxyServerThresholdPort",     
        wsdlLocation = "WEB-INF/wsdl/VerifyMTOMProxyServer.wsdl",
        endpointInterface = "mtom21.threshold.wsfvt.server.proxy.ImageServiceInterface")
@MTOM(threshold=20000)

/**
 * This class returns a response using MTOM if the threshold is broken by the input size
 */
public class VerifyMTOMProxyServerThreshold implements mtom21.threshold.wsfvt.server.proxy.ImageServiceInterface {
  
    public mtom21.threshold.wsfvt.server.proxy.ImageDepot invoke(mtom21.threshold.wsfvt.server.proxy.ImageDepot input) {
        return input;
    }
         
}