package se.totalorder.basen;

import io.javalin.Javalin;

public class JavalinApp {
  private final Javalin app = Javalin.create();

  public void start() {
    app.get("/", ctx -> ctx.result("Hello World"));
    app.disableStartupBanner();
    app.start(8080);
  }
}