package se.totalorder.basen.testutil;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import se.totalorder.basen.confer.Confer;

public class TestUtil {
  public static void migrateDatabase(final DataSource dataSource) {
    final Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    flyway.migrate();
  }

  public static Config testConfig() {
    return Confer.builder().env("test").build();
  }

  public static Config testConfigWithDbPort(final int port) {
    return testConfig().withValue("db.port", ConfigValueFactory.fromAnyRef(port));
  }
}
