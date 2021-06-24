package jaxws.provider.nullreturn.server.wsdlnoresp;
import javax.xml.ws.*;
import javax.jws.*;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.*;
import java.io.*;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

@WebServiceProvider(wsdlLocation="WEB-INF/wsdl/ProvPayloadImplService.wsdl")
@ServiceMode(value=Service.Mode.PAYLOAD)
//@HandlerChain(file="handlers.xml")
public class ProvPayloadImpl implements Provider<DOMSource>{  

      public ProvPayloadImpl(){} 

      public DOMSource invoke(DOMSource request){            
          System.out.println("wsdlnoresp ProvPayload received:"+request);
          
return null;
 
}
}

