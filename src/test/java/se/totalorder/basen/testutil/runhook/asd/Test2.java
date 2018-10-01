package se.totalorder.basen.testutil.runhook.asd;

import com.palantir.docker.compose.DockerComposeRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.totalorder.basen.testutil.runhook.Compose;
import se.totalorder.basen.testutil.runhook.hooks.DockerRunHook;

import java.lang.annotation.Annotation;

@Slf4j
class Test2 {
    @ClassRule
    public static Compose postgres = Compose.postgres();

//    @BeforeAll
//    static void beforeAll() {
//        postgres.beforeAll();
//    }

//    @BeforeAll
//    static void beforeAll() {
//        log.info("beforeAll: " + postgres.port());
//    }

    @Test
    void woot() {
        log.info("postgresPort: " + postgres.port());
    }
}
