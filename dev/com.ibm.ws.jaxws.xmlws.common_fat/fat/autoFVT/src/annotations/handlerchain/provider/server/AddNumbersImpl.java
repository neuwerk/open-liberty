package annotations.handlerchain.provider.server;

import java.io.ByteArrayInputStream;

import javax.jws.HandlerChain;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceProvider;

import org.w3c.dom.Node;

/*
 * simple service to test that we can put a handler on a provider. 
 */
@WebServiceProvider(wsdlLocation="WEB-INF/wsdl/AddNumbersImplService.wsdl")
@HandlerChain(file="AddNumbersImpl_handler.xml", name="")
public class AddNumbersImpl implements Provider<Source> {
    public Source invoke(Source source) {
        try {
            DOMResult dom = new DOMResult();
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.transform(source, dom);
            Node node = dom.getNode();
            Node root = node.getFirstChild();
            Node first = root.getFirstChild();
            Node num1 = first.getFirstChild();
            Node second = first.getNextSibling();
            Node num2 = second.getFirstChild();
            int number1 = Integer.decode(num1.getNodeValue());
            int number2 = Integer.decode(num2.getNodeValue());
            System.out.println("annotations.handlerchain.provider.server.AddNumbersimpl invoked with" +
                  Integer.toString(number1)+ " " + Integer.toString(number2));
                    
            
            return sendSource(number1, number2);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in provider endpoint", e);
        }
    }
    
    private Source sendSource(int number1, int number2) {
        int sum = number1+number2;

        String body =
            "<ns1:addNumbersResponse xmlns:ns1=\"http://server.provider.handlerchain.annotations/\"><return>"
            +sum
            +"</return></ns1:addNumbersResponse>";
        Source source = new StreamSource(
            new ByteArrayInputStream(body.getBytes()));
        return source;
    }
    
}
