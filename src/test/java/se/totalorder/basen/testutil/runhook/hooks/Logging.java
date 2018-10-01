package se.totalorder.basen.testutil.runhook.hooks;

import se.totalorder.basen.testutil.runhook.RunHookProvider;

import java.lang.annotation.*;
import se.totalorder.basen.testutil.runhook.hooks.impl.LoggingImpl;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@RunHookProvider(annotation = Logging.class, hook = LoggingImpl.class)
@Repeatable(Logging.List.class)
public @interface Logging {
    String value();

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @RunHookProvider(annotation = Logging.class, hook = LoggingImpl.class)
    @interface List {
        Logging[] value();
    }
}

