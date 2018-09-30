package se.totalorder.basen.testutil.runhook;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;


@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ExtendWith(RunHookExtension.class)
public @interface RunHookProvider {
    Class<? extends Annotation> config();
    Class<? extends RunHook> hook();
}
