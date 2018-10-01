package se.totalorder.basen.testutil.runhook;

public interface RunHook {
    void start() throws Exception;
    void stop();
    void instanceCreated(Object testInstance) throws Exception;
}
