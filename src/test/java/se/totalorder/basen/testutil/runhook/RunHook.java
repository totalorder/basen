package se.totalorder.basen.testutil.runhook;

public interface RunHook {
    void setupOnce() throws Exception;
    void beforeAll(Class<?> testClass) throws Exception;
    void stop();
//    void instanceCreated(Object testInstance) throws Exception;
}
