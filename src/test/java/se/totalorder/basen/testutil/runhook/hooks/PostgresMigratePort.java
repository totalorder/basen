package se.totalorder.basen.testutil.runhook.hooks;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@DockerPort("postgres-migrate")
public @interface PostgresMigratePort {
}
