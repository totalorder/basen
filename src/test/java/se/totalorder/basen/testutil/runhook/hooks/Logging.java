package se.totalorder.basen.testutil.runhook.hooks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import se.totalorder.basen.testutil.runhook.RunHookProvider;
import se.totalorder.basen.testutil.runhook.hooks.impl.LoggingImpl;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@RunHookProvider(annotation = Logging.class, hook = LoggingImpl.class)
@Repeatable(Logging.List.class)
public @interface Logging {
  String value();

  @Target({ElementType.TYPE, ElementType.METHOD})
  @Retention(RetentionPolicy.RUNTIME)
  @Inherited
  @RunHookProvider(annotation = Logging.class, hook = LoggingImpl.class)
  @interface List {
    Logging[] value();
  }
}

