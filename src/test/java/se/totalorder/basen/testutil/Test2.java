package se.totalorder.basen.testutil;

import org.junit.jupiter.api.Test;
import se.totalorder.basen.testutil.runhook.hooks.LoggingRunHook;

@LoggingRunHook("Test2")
@LoggingRunHook("Test3")
//@DockerRunHook("Korv")
public class Test2 {

    static String woot() {
        return "woot";
    }

    @Test
    void asd() {

    }
}
