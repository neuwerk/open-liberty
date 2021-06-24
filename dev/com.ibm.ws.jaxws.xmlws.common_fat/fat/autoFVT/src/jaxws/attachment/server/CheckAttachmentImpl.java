package jaxws.attachment.server;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.ws.Holder;

@WebService (targetNamespace="http://server.attachment.jaxws",
        wsdlLocation="WEB-INF/wsdl/CheckAttachmentService.wsdl",
        serviceName="CheckAttachmentService",
        portName="CheckAttachmentServicePort",
        endpointInterface="jaxws.attachment.server.CheckAttachmentPortType")

public class CheckAttachmentImpl {
	
	public void retrieve( String request,  Holder<String> response,  Holder<DataHandler> mimeAttachment){
			System.out.println("CheckAttachmentImpl.retrieve is invoked with argument: "+ request);
			response.value = "server says hi";
			
			// If the request string contain not_null, then the server creates an attachment that is not null
			if  (request.contains("not_null")) {
				System.out.println("creating non-null datahandler");
				mimeAttachment.value = new DataHandler("sampleyy", "text/xml");
			}
			// for any other request, it creates a null attachment
			else {
				System.out.println("creating null datahandler");
				mimeAttachment = new Holder<DataHandler>((DataHandler)null);
			}	
		
		}
}
