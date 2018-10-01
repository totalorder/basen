package se.totalorder.basen.testutil.runhook;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;


@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DockerPort {
    String value();
}
