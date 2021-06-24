//
// @(#) 1.1 autoFVT/src/jaxws22/customprops/wsfvt/client/CustomPropsClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/15/10 11:56:44 [8/8/12 06:59:00]
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
package jaxws22.customprops.wsfvt.client;


import java.util.Map;
import javax.xml.ws.BindingProvider;

import jaxws22.customprops.wsfvt.server.*;


public class CustomPropsClient {    
    
    private static final String TEST_STRING_SENT = "<invoke>WS FVT F743-23362</invoke>";

    /**
     * Default constructor
     */
    public CustomPropsClient() {	
    }


	public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("Endpoint URL is required.  Example: http://localhost:9080/CustomProps/CustomPropsService"); 
            return;
        }
        
		CustomPropsClient client = new CustomPropsClient();
		String value = client.callProvider(args[0]);
		System.out.println("Response: " + value);				
	}

    
    public String callProvider(String inputURL) {
        
        try {
            CustomProps myPort = (new CustomPropsService()).getCustomPropsPort();
                   
            Map<String, Object> requestContext = ((BindingProvider) myPort).getRequestContext();            
            requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, inputURL);
            
            String result = myPort.echo(TEST_STRING_SENT);
            System.out.println("received result = " + result);
            
            // if it comes here, the test fails
            return result;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return(ex.getMessage());                                                  
        }
    }
    
}
