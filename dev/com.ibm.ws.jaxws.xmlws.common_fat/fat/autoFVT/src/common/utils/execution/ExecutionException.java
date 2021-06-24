package common.utils.execution;

import common.utils.ACUTEException;

public class ExecutionException extends ACUTEException {

    private static final long serialVersionUID = 2836709853229462849L;
    private int returnCode = -1;

    public ExecutionException() {
        super();
    }

    public ExecutionException(String s) {
        super(s);
    }

    public ExecutionException(Exception e) {
        super(e);
    }

    public ExecutionException(String s, Exception e) {
        super(s, e);
    }

    public ExecutionException(int returnCode, String returnString) {
        super(returnString);
        this.returnCode = returnCode;
    }

    public int getReturnCode() {
        return this.returnCode;
    }
}