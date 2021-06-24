package jaxws.provider.nullreturn.server.nowsdl;
import javax.xml.ws.*;
import java.io.*;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

@WebServiceProvider()
@ServiceMode(value=Service.Mode.MESSAGE)
//@HandlerChain(file="handlers.xml")
public class ProvMessageImpl implements Provider<SOAPMessage>{ 

      public ProvMessageImpl(){} 

      public SOAPMessage invoke(SOAPMessage request){            
            System.out.println("nowsdl ProvMsg received:"+request);
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

           
            if(request.getSOAPBody().toString().contains("not_null")){
                System.out.println("returning a non-null response");
                return notNullResponse;
            } else{
                System.out.println("returning a null response");
                return null;
            }
            } catch (IOException e) {				
				e.printStackTrace();
			} catch (SOAPException e) {				
				e.printStackTrace();
			}
            
            return null;
      }
}
