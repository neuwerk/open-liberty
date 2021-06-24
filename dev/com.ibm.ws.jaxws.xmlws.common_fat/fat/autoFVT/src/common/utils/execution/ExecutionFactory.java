package common.utils.execution;

public class ExecutionFactory {

    public static IExecution getExecution() {
        return new IExecution();
    }
}
