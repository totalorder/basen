package se.totalorder.basen.testutil.runhook.hooks;

import se.totalorder.basen.testutil.runhook.RunHookProvider;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@RunHookProvider(config = DockerRunHookTmp.class, hook = DockerRunHookImpl.class)
public @interface DockerRunHookTmp {
    String value();

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @RunHookProvider(config = DockerRunHookTmp.class, hook = DockerRunHookImpl.class)
    @Inherited
    @interface List {
        DockerRunHookTmp[] value();
    }
}