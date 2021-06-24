package common.utils;

public abstract class ACUTEException extends RuntimeException {
    
    public ACUTEException() {
        super();
    }
    
    public ACUTEException(String message) {
        super(message);
    }
    
    public ACUTEException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ACUTEException(Throwable cause) {
        super(cause);
    }
}
