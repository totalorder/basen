package se.totalorder.basen;


import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Context;
import io.javalin.Handler;
import io.javalin.Javalin;
import java.util.function.Function;
import javax.sql.DataSource;
import se.totalorder.basen.api.UserApi;
import se.totalorder.basen.config.DatabaseConf;
import se.totalorder.basen.tx.TxMan;

public class Application {
  public static void main(String[] args) {
    final DataSource dataSource = new HikariDataSource(DatabaseConf.get("test"));
    final TxMan transactionManager = new TxMan(dataSource);
    final UserApi numberApi = new UserApi(transactionManager);

    Javalin.create()
        .get("/", ctx -> ctx.result("Hello World"))
        .get("/user", json(ctx -> numberApi.get()))
        .get("/user/:id", json(ctx -> numberApi.get(ctx.pathParam("id"))))
        .post("/user/:id", json(ctx -> numberApi.create(ctx.pathParam("id"), ctx.body())))
        .put("/user/:id", json(ctx -> numberApi.put(ctx.pathParam("id"), ctx.body())))
        .delete("/user/:id", json(ctx -> numberApi.delete(ctx.pathParam("id"))))
        .disableStartupBanner()
        .start(8080);
  }

  private static Handler json(final Function<Context, ?> handler) {
    return (ctx) -> {
      final Object result = handler.apply(ctx);
      if (result != null) {
        ctx.json(result);
      } else {
        ctx.result("");
        ctx.status(404);
      }
    };
  }
}
