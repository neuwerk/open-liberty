package jaxws.provider.nullreturn.server.wsdlnoresp;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

@WebServiceProvider(wsdlLocation="WEB-INF/wsdl/ProvMessageImplService.wsdl" )
@ServiceMode(value=Service.Mode.MESSAGE)
//@HandlerChain(file="handlers.xml")
public class ProvMessageImpl implements Provider<SOAPMessage>{  

      public ProvMessageImpl(){}
      
      public SOAPMessage invoke(SOAPMessage request){            
          System.out.println("wsdlnoresp ProvMsg received:"+request);
          SOAPMessage notNullResponse = null;
          String notNullResponseString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
                  "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
                  "xmlns:wsdl=\"http://wsdl.server.nullreturn.provider.jaxws/\">" +
                 "<soapenv:Body><wsdl:echoResponse><arg0>heres a possibly invalid but not empty response</arg0></wsdl:echoResponse>" +  
                 "</soapenv:Body></soapenv:Envelope>";
                 
          try {
          MessageFactory factory = MessageFactory.newInstance();
          InputStream is = new StringBufferInputStream(notNullResponseString);
          
          notNullResponse = factory.createMessage(null, is);
			
          
          if(request.getSOAPBody().getTextContent().contains("not_null")){
              System.out.println("returning a non-null response");
              return notNullResponse;
          } else{
              System.out.println("returning a null response");
              return null;
          }
          } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          
          return null;
    }

}

