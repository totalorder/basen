package se.totalorder.basen.testutil;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;

public class TestUtil {
  public static void migrateDatabase(final DataSource dataSource) {
    final Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    flyway.migrate();
  }
}
