package annotations.webfault.multipleexceptions.server1.exceptions2;

import javax.xml.ws.WebFault;

@WebFault(name="MultiplyNumbersException", targetNamespace="http://server1.multipleexceptions.webfault.annotations/")
public class MultNegativeNumbersException extends Exception {
    String info;

    public MultNegativeNumbersException(String message, String detail) {
      super(message);
        this.info = detail;
    }

    public String getFaultInfo() {
        return info;
    }
}
