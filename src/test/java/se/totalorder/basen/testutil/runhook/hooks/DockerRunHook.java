package se.totalorder.basen.testutil.runhook.hooks;

import se.totalorder.basen.testutil.runhook.RunHookProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@RunHookProvider(config = DockerRunHook.class, hook = DockerRunHookImpl.class)
public @interface DockerRunHook {
    String value();
}
