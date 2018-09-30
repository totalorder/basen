package se.totalorder.basen.testutil.runhook.hooks;

import se.totalorder.basen.testutil.runhook.RunHook;
import se.totalorder.basen.testutil.runhook.RunHookProvider;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@RunHookProvider(config = BaseRunHook.class, hook = BaseRunHookImpl.class)
public @interface BaseRunHook {
    Class<? extends Annotation> config();
    Class<? extends RunHook> hook();

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @RunHookProvider(config = BaseRunHook.class, hook = BaseRunHookImpl.class)
    @Inherited
    @interface List {
        BaseRunHook[] value();
    }
}