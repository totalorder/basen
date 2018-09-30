package se.totalorder.basen.testutil.runhook.hooks;

import se.totalorder.basen.testutil.runhook.RunHookProvider;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@RunHookProvider(config = LoggingRunHook.class, hook = LoggingRunHookImpl.class)
@Repeatable(LoggingRunHook.List.class)
public @interface LoggingRunHook {
    String value();

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @RunHookProvider(config = LoggingRunHook.class, hook = LoggingRunHookImpl.class)
    @Inherited
    @interface List {
        LoggingRunHook[] value();
    }
}

