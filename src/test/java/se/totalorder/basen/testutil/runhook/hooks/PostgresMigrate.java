package se.totalorder.basen.testutil.runhook.hooks;

import se.totalorder.basen.testutil.runhook.RunHookProvider;
import se.totalorder.basen.testutil.runhook.hooks.impl.DockerImpl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@RunHookProvider(annotation = Docker.class, hook = DockerImpl.class)
@Docker("postgres-migrate")
public @interface PostgresMigrate {
}
