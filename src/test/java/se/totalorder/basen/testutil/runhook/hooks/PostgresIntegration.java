package se.totalorder.basen.testutil.runhook.hooks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import se.totalorder.basen.testutil.runhook.RunHookProvider;
import se.totalorder.basen.testutil.runhook.hooks.impl.DockerImpl;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RunHookProvider(annotation = Docker.class, hook = DockerImpl.class)
@Docker("postgres-integration")
public @interface PostgresIntegration {
}
