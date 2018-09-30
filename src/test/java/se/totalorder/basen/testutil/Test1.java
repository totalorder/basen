package se.totalorder.basen.testutil;

import org.junit.jupiter.api.Test;
import se.totalorder.basen.testutil.runhook.RunHookProvider;
import se.totalorder.basen.testutil.runhook.hooks.*;


//@LoggingRunHook("Test1")
//@LoggingRunHook("Test2")
//@BaseRunHook(config = DockerRunHook.class, hook = DockerRunHookImpl.class)

@RunHookProvider(config = DockerRunHook.class, hook = DockerRunHookImpl.class)
@DockerRunHook("asd")
//@DockerRunHook("postgres")
//@DockerRunHook("postgres")
//@Mongo
//@Postgres
//@DockerRunHook("korv")
public class Test1 {
    @Test
    void asd() {

    }
}
