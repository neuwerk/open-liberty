package jaxws.provider.nullreturn.server.wsdl;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.transform.stream.StreamResult;

@WebServiceProvider(wsdlLocation="WEB-INF/wsdl/ProvPayloadImplService.wsdl")
@ServiceMode(value=Service.Mode.PAYLOAD)
//@HandlerChain(file="handlers.xml")
public class ProvPayloadImpl implements Provider<DOMSource>{  

      public ProvPayloadImpl(){}
      
      public DOMSource invoke(DOMSource request){            
          System.out.println("wsdl ProvPayload received:"+request);   
          return null; 
         
    }
 
}

