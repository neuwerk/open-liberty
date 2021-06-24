/*
 * @(#) 1.3 autoFVT/src/annotations/webmethod_g2/runtime/lwmclient/WebMethodLegacyCheckClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/4/10 17:06:17 [8/8/12 06:57:37]
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 03/10/2010  btiffany    595458.5           New File
 * ..
 * 08/04/2010  jtnguyen    664197             add tests for static and final methods
 */

package annotations.webmethod_g2.runtime.lwmclient;
/* need client and generated support classes 
 * in a different package so we don't collide with objectfactory from other tests. 
*/

import javax.xml.ws.BindingProvider;

public class WebMethodLegacyCheckClient {        
    
    
    public String invokeanno(String posturl, String msg) throws Exception {
        annotations.webmethod_g2.runtime.lwmclient.WebMethodLegacyCheckService s = 
            new annotations.webmethod_g2.runtime.lwmclient.WebMethodLegacyCheckService();
        WebMethodLegacyCheck port =  s.getWebMethodLegacyCheckPort();
        // we built against a wsdl in the src tree, so have to set url this way. 
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, posturl);
        return port.echoanno(msg);
    }
    
    public String invokenoanno(String posturl, String msg) throws Exception {
//        URL u = new URL(posturl);
        WebMethodLegacyCheckService s = new WebMethodLegacyCheckService();
        WebMethodLegacyCheck port =  s.getWebMethodLegacyCheckPort();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, posturl);
        return port.echonoanno(msg);
    }

        
    public String invokeStaticMethod(String posturl, String msg) throws Exception {
    WebMethodLegacyCheckService s = new WebMethodLegacyCheckService();
    WebMethodLegacyCheck port =  s.getWebMethodLegacyCheckPort();
    ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, posturl);
    return port.webMethodStatic(msg);
    }

    public String invokeFinalMethod(String posturl, String msg) throws Exception {
    WebMethodLegacyCheckService s = new WebMethodLegacyCheckService();
    WebMethodLegacyCheck port =  s.getWebMethodLegacyCheckPort();
    ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, posturl);
    return port.webMethodFinal(msg);
    }
    
}
