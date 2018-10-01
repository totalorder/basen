package se.totalorder.basen.testutil.runhook.hooks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import se.totalorder.basen.testutil.runhook.RunHookProvider;
import se.totalorder.basen.testutil.runhook.hooks.impl.DockerRunHookImpl;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@RunHookProvider(config = DockerRunHook.class, hook = DockerRunHookImpl.class)
@DockerRunHook("postgres")
public @interface Postgres {
}
