package annotations.webfault.multipleexceptions.server1.exceptions2;


public class AddNegativeNumbersException extends Exception {
    String info;

    public AddNegativeNumbersException(String message, String detail) {
      super(message);
        this.info = detail + " this is package exceptions2...";
    }

    public String getFaultInfo() {
        return info;
    }
}
