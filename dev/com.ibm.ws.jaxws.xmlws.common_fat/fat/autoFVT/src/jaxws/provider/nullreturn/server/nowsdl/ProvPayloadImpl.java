package jaxws.provider.nullreturn.server.nowsdl;
import javax.xml.ws.*;
import javax.jws.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import java.io.*;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;



@WebServiceProvider()
@ServiceMode(value=Service.Mode.PAYLOAD)
// payload mode receives only the content of the body. 
//@HandlerChain(file="handlers.xml")
public class ProvPayloadImpl implements Provider<DOMSource>{  

      public ProvPayloadImpl(){} 

      public DOMSource invoke(DOMSource request){
          System.out.println("nowsdl ProvPayload received:"+request); 
          DOMSource notNullResponse = new DOMSource();
          String notNullResponseString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
                  "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
                  "xmlns:wsdl=\"http://wsdl.server.nullreturn.provider.jaxws/\">" +
                 "<soapenv:Body><wsdl:echoResponse><arg0>heres a possibly invalid but not empty response</arg0></wsdl:echoResponse>" +  
                 "</soapenv:Body></soapenv:Envelope>";
          
           try {
          
        	  Transformer transformer = TransformerFactory.newInstance().newTransformer();
              StreamResult result = new StreamResult(System.out);
              transformer.transform(request, result);
              System.out.println("\n");
              
              InputStream is = new StringBufferInputStream(notNullResponseString);
              
              SOAPMessage notNullResponseMessage = MessageFactory.newInstance().createMessage(null, is);              
              is.close();              
              notNullResponse.setNode(notNullResponseMessage.getSOAPBody().extractContentAsDocument());           
              System.out.println(request.getNode().getTextContent());
             /* if(request.getNode().getNodeValue().contains("not_null")){
                  System.out.println("returning a non-null response");
                  return notNullResponse;
              } else{
                  System.out.println("returning a null response");
                  return null;
              }
              return null;*/
            } catch (Exception e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			} 
              
              return null;             
          }  
}

