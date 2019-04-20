package se.totalorder.basen;


import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Context;
import io.javalin.Handler;
import io.javalin.Javalin;

import java.util.*;
import java.util.function.Function;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import se.deadlock.okok.Client;
import se.totalorder.basen.api.UserApi;
import se.totalorder.basen.confer.Confer;
import se.totalorder.basen.config.ClientConf;
import se.totalorder.basen.config.DatabaseConf;
import se.deadlock.txman.TxMan;
import se.totalorder.basen.util.IncomingHttpLogger;

@Slf4j
public class Application {
  public static void main(String[] args) {
    final String envString = System.getenv("ENVIRONMENTS");
    final List<String> envs = envString != null ? Arrays.asList(envString.split(",")) : Collections.emptyList();
    log.info("Environments: " + envs);
    final Config config = Confer.builder().envs(envs).build();

    final DataSource dataSource = new HikariDataSource(DatabaseConf.get(config));

    log.info("Migrating database...");
    final Flyway flyway = Flyway.configure()
        .dataSource(dataSource)
        .load();
    flyway.migrate();

    final TxMan transactionManager = new TxMan(dataSource);

    final Client.Builder clientBuilder = ClientConf.createClient();
    final Client localhostClient = clientBuilder.baseUrl("http://localhost:8080").build();
    final UserApi userApi = new UserApi(localhostClient, transactionManager);
    final IncomingHttpLogger incomingHttpLogger = new IncomingHttpLogger();

    Javalin.create()
        .before(incomingHttpLogger::before)
        .requestLogger(incomingHttpLogger)
        .get("/", ctx -> ctx.result("Hello World"))
        .get("/user", json(ctx -> userApi.get()))
        .get("/user/:id", json(ctx -> userApi.get(ctx.pathParam("id"))))
        .post("/user/:id", json(ctx -> userApi.create(ctx.pathParam("id"), ctx.body())))
        .put("/user/:id", json(ctx -> userApi.put(ctx.pathParam("id"), ctx.body())))
        .delete("/user/:id", json(ctx -> userApi.delete(ctx.pathParam("id"))))
        .get("/proxy-user/:id", json(ctx -> userApi.proxyGet(ctx.pathParam("id"))))
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
