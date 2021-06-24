package annotations.resource.primitiveval.primitivevalclient;

import annotations.resource.primitiveval.fieldprimitivevalserver.*;


public class PrimitiveValClient {

	private static FieldPrimitiveValImpl portField = 
		new FieldPrimitiveValImplService().getFieldPrimitiveValImplPort();
	        
	/**
	 * @param args
	 */
	public void main(String[] args) {
		System.out.println("The injected number is: "+getInjectedNumber() );

	}
	
	public int getInjectedNumber(){
		return portField.getInjectedNumber();
	}

}
