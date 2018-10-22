package se.totalorder.basen.config;

import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;

public class DatabaseConf {
  public static HikariConfig get(final Config config) {
    final HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl("jdbc:postgresql://" + config.getString("db.hostname") + ":"
        + config.getInt("db.port") + "/" + config.getString("db.name"));
    hikariConfig.setAutoCommit(false);
    hikariConfig.setUsername(config.getString("db.username"));
    hikariConfig.setPassword(config.getString("db.password"));
    hikariConfig.setMaximumPoolSize(10);
    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    return hikariConfig;
  }
}
