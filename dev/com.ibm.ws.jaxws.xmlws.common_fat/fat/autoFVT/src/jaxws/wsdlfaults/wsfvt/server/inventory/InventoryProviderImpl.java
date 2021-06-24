//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId         Defect            Description
// ----------------------------------------------------------------------------
// 10/05/2006  mzheng         LIDB3296-46.01    New File
//

package jaxws.wsdlfaults.wsfvt.server.inventory;

import java.util.Hashtable;
import java.util.ArrayList;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMResult;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

import org.w3c.dom.Node;

@WebServiceProvider(targetNamespace="http://inventory.wsdlfaults.jaxws",
                    wsdlLocation="WEB-INF/wsdl/Inventory.wsdl",
                    serviceName="InventoryProvider",
                    portName="InventoryProviderPort")
@ServiceMode(value=Service.Mode.PAYLOAD)
@BindingType(value=SOAPBinding.SOAP11HTTP_BINDING)
public class InventoryProviderImpl implements Provider<Source> {

    private static final String SCHEMA_NAMESPACE = "http://inventory.wsdlfaults.jaxws/xsd";

    private Hashtable thisInventory = new Hashtable();

    public InventoryProviderImpl() {
        addToInventory("T20", 10);
    }


    public Source invoke(Source request) {

        Source resp = null;
        String reqOper = null;

        try {
            ArrayList<String> reqInfo = parseRequest(request);
            reqOper = reqInfo.get(0);

            String id = reqInfo.get(1);
            int count = 0;

            String reqCount = reqInfo.get(2);
            if (reqCount != null) {
                count = Integer.parseInt(reqCount);
            }

            if (reqOper.equals("addToInventory")) {

                boolean result = addToInventory(id, count);
                resp = createResponse(reqOper, (new Boolean(result)).toString());

            } else if (reqOper.equals("removeFromInventory")) {
                boolean result = removeFromInventory(id, count);
                resp = createResponse(reqOper, (new Boolean(result)).toString());

            } else if (reqOper.equals("queryInventory")) {

                int result = queryInventory(id);
                resp = createResponse(reqOper, (new Integer(result)).toString());

            } else {
                throw new WebServiceException("This operation is not supported: " + reqOper);
            }
        } catch (InventoryOperationException e) {
            throw new ProtocolException("Caught InventoryOperationException in " + reqOper, e);
        }

        return resp;
    }


    /**
     * Parse the operation name and parameters from request Source 
     */
    private ArrayList<String> parseRequest(Source request) {

        try {
            // The ArrayList holds the operation, id, and count
            ArrayList<String> reqInfo = new ArrayList<String>(3);

            DOMResult dom = new DOMResult();
            Transformer transformer =
              TransformerFactory.newInstance().newTransformer();

            transformer.transform(request, dom);

            // This is the document node
            Node node = dom.getNode();

            Node root = node.getFirstChild();
            String reqMethod = root.getLocalName();

            Node child1  = root.getFirstChild();
            String id = child1.getFirstChild().getNodeValue();

            Node child2  = child1.getNextSibling();
            String count = null;
            if(child2 != null) { 
                count = child2.getFirstChild().getNodeValue();
            }

            reqInfo.add(reqMethod);
            reqInfo.add(id);
            reqInfo.add(count);

            return reqInfo;
        } catch (Exception e) {
            throw new WebServiceException("Caught unexpected Exception in parseRequest()", e);
        }
    }


    private Source createResponse(String operation, String result) {

        try {

            String respString = "<" + operation + "Response" + " xmlns=\"" + 
                            SCHEMA_NAMESPACE + "\">" + "<result>" + 
                            result + "</result></" + 
                            operation + "Response" + ">";

            Source retSource = new StreamSource(new StringReader(respString));
            return retSource;
        } catch (Exception e) {
            throw new WebServiceException("Caught unexpected Exception in createResponse()", e);
        }
    }


    /**
     * Adds a number of items to inventory.
     */
    public boolean addToInventory(String id, int count) {

        // to trigger the NPE
        String s = null;

        if (!id.equals("null")) {
            s = id;
        }

        if (s != null && count > 0) {
            Integer n = (Integer) thisInventory.get(s);

            if (n != null) {
                thisInventory.put(s, n.intValue() + count);
            } else {
                thisInventory.put(s, count);
            }
        } else if (s == null) {
            throw new NullPointerException("Null product id");
        }
        return true;
    }


    /**
     * Removes a number of items from inventory.
     * 
     */
    public boolean removeFromInventory(String id, int count) throws 
        InventoryOperationException {
        Integer n;

        try {
            // to trigger the NPE
            String s = null;

            if (!id.equals("null")) {
                s = id;
            }

            n = (Integer) thisInventory.get(s);
        } catch (NullPointerException e) {
            throw createInventoryException("Caught NPE", "Runtime NullPointerException caught in removeFromInventory()", e);
        }

        if (n != null) {
            if (n.intValue() > count) {
                thisInventory.put(id, n.intValue() - count);
                return true;
            } else if (n.intValue() == count) {
                thisInventory.remove(id);
                return false;
            } else {
                throw createInventoryException("Not enough products to remove", "Request to remove: " + count + ", Available: " + n.intValue(), null);
            }
        } else {
            throw new ProtocolException("No product to remove: " + id);
        } 
    }


    /**
     * This function does not catch NPE thrown at runtime, and client 
     * should expect WebServiceException at runtime.
     */
    public int queryInventory(String id) {

        // to trigger the NPE
        String s = null;

        if (!id.equals("null")) {
            s = id;
        }

        /**
         * When id == null, client should expect WebServiceException
         * caused by unchecked NPE
         */
        Integer n = (Integer) thisInventory.get(s);

        if (n != null) {
            return n.intValue();
        } 

        return 0;
    }


    /**
     * Create an application specific exception
     */
    private InventoryOperationException createInventoryException(String msg,
                                                        String detail,
                                                        Throwable cause) {
        OperationFault fault = new OperationFault();
        fault.setDetail(detail);

        if (cause != null) {
            return new InventoryOperationException(msg, fault, cause);
        } else {
            return new InventoryOperationException(msg, fault);
        }

    }
}
