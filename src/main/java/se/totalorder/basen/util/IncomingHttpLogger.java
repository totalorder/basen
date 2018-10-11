package se.totalorder.basen.util;

import io.javalin.Context;
import io.javalin.RequestLogger;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;

@Slf4j
public class IncomingHttpLogger implements RequestLogger {

  @Override
  public void handle(final Context ctx, final Float executionTimeMs) throws Exception {
    log.info("HTTP << " + ctx.method() + " " + ctx.url() + " [" + ctx.status() + " " + HttpStatus.getMessage(ctx.status()) + "] " + executionTimeMs.intValue() + "ms");
    if (ctx.resultString() != null && !ctx.resultString().isEmpty()) {
      log.info(ctx.resultString());
    }
  }

  public void before(final Context ctx) {
    log.info("HTTP << " + ctx.method() + " " + ctx.url());
    if (!ctx.body().isEmpty()) {
      log.info(ctx.body());
    }
  }
}
