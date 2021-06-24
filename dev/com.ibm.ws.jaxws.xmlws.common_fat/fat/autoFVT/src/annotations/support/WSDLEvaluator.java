package annotations.support;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A XPath based WSDL evaluator 
 */
public class WSDLEvaluator {

	public static String out = null;
	
    public static String getAttributeValue(Node n, String attrName){
    	String nodeName = null;

		NamedNodeMap attr = n.getAttributes();
		int jx = 0;
		while(jx < attr.getLength()){
			if(attr.item(jx).getNodeName().equals(attrName)){
				nodeName = attr.item(jx).getNodeValue();
				break;
			}
			jx++;
		}
    	return nodeName;
    }
    
    public static boolean checkUniqueness(NodeList n, String attrName){
    	boolean nodes_have_unique_attr = true;
    	
		int num = n.getLength(), 
		ix = 0;	
		String nodeName = "",
		       checkName = "";

		// check uniqueness of the value of the attribiute
		while (ix < num - 1) {
			int check = ix + 1;
			nodeName = getAttributeValue(n.item(ix), attrName);
			
			if(nodeName.equals("")){
				nodes_have_unique_attr = false;
				break;
			}
			
			while (check < num) {
				checkName = getAttributeValue(n.item(check), attrName);
				if (nodeName.equals(checkName) ||
						checkName.equals("")) {
					nodes_have_unique_attr = false;
					break;
				}
				check++;
			}
			if (!nodes_have_unique_attr)
				break;
			ix++;
		}
		
		out = "(" + nodeName + " : " + checkName + ")";
		
    	return nodes_have_unique_attr;    	
    }

	
}