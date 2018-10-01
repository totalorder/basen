package se.totalorder.basen.testutil.runhook.hooks;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import se.totalorder.basen.testutil.runhook.RunHookProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import se.totalorder.basen.testutil.runhook.hooks.impl.DockerRunHookImpl;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DockerRunHook.List.class)
@Inherited
@RunHookProvider(config = DockerRunHook.class, hook = DockerRunHookImpl.class)
public @interface DockerRunHook {
    String value();

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @RunHookProvider(config = DockerRunHook.class, hook = DockerRunHookImpl.class)
    @interface List {
        DockerRunHook[] value();
    }
}
