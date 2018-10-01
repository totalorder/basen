package se.totalorder.basen.testutil.runhook;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.totalorder.basen.testutil.runhook.hooks.DockerRunHook;

@Slf4j
@DockerRunHook("postgres")
class Test1 {

    @DockerPort("postgres")
    static int postgresPort;

    @BeforeAll
    static void beforeAll() {
        log.info("beforeAll: " + postgresPort);
    }

    @Test
    void woot() {
        log.info("postgresPort: " + postgresPort);
    }
}
