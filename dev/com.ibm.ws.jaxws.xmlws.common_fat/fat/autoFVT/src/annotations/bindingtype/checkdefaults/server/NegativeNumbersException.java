package annotations.bindingtype.checkdefaults.server@REPLACE_WITH_PACKAGE_IDENTIFIER@;

import javax.xml.ws.WebFault;

@WebFault(name="AddNumbersException", targetNamespace="http://server@REPLACE_WITH_PACKAGE_IDENTIFIER@.checkdefaults.bindingtype.annotations/")
public class NegativeNumbersException extends Exception {
    String info;

    public NegativeNumbersException(String message, String detail) {
      super(message);
        this.info = detail;
    }

    public String getFaultInfo() {
        return info;
    }
}
