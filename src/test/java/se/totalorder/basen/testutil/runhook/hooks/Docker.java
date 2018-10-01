package se.totalorder.basen.testutil.runhook.hooks;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import se.totalorder.basen.testutil.runhook.RunHookProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.totalorder.basen.testutil.runhook.hooks.impl.DockerImpl;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Docker.List.class)
@Inherited
@RunHookProvider(annotation = Docker.class, hook = DockerImpl.class)
public @interface Docker {
    String value();

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @RunHookProvider(annotation = Docker.class, hook = DockerImpl.class)
    @interface List {
        Docker[] value();
    }
}
