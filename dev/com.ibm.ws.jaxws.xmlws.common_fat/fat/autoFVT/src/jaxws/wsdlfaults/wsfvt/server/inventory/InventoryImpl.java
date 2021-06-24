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
import java.util.Arrays;

import javax.jws.WebService;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;

import org.w3c.dom.Node;

@WebService(targetNamespace="http://inventory.wsdlfaults.jaxws",
            wsdlLocation="WEB-INF/wsdl/Inventory.wsdl",
            serviceName="InventoryService",
            portName="InventoryPort",
            endpointInterface="jaxws.wsdlfaults.wsfvt.server.inventory.InventoryPortType")

public class InventoryImpl implements InventoryPortType {

    private Hashtable thisInventory = new Hashtable();


    public InventoryImpl() {
        addToInventory("T20", 10);
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
        Integer n = null;

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
