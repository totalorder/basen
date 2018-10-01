package se.totalorder.basen.config;

import com.zaxxer.hikari.HikariConfig;

public class DatabaseConf {
  public static HikariConfig get(final String env) {
    return get(env, 5432);
  }

  public static HikariConfig get(final String env, final int port) {

    final HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:postgresql://localhost:" + port + "/basen");
    config.setAutoCommit(false);
    config.setUsername("basen");
    config.setPassword("basen");
    config.setMaximumPoolSize(10);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    return config;
  }
}
