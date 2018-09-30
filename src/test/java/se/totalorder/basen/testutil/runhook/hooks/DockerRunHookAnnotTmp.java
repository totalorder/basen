package se.totalorder.basen.testutil.runhook.hooks;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DockerRunHookAnnotTmp {
    String value();
}