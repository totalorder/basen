package se.totalorder.basen;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

class HikariTest {
  static DataSource dataSource;
  static TransactionManager transactionManager;

  @BeforeAll
  static void beforeAll() {
    dataSource = createDataSource();

    // Create the Flyway instance
    final Flyway flyway = new Flyway();

    // Point it to the database
    flyway.setDataSource(dataSource);

    // Start the migration
    flyway.migrate();

    transactionManager = new TransactionManager(dataSource);
  }

  @Test
  void basicJdbc() {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      final Statement statement = connection.createStatement();
      statement.executeUpdate("DELETE FROM basen;");
      assertThat(statement.executeUpdate("INSERT INTO basen (data) VALUES ('asd');"), is(1));
      final ResultSet rs = statement.executeQuery("SELECT * FROM basen;");

      final List<String> datas = new ArrayList<>();
      while (rs.next()) {
        final int id = rs.getInt("id");
        assertThat(id, greaterThanOrEqualTo(1));

        final String data = rs.getString("data");
        datas.add(data);
      }

      final List<String> expectedDatas = new ArrayList<>();
      expectedDatas.add("asd");
      assertThat(datas, is(expectedDatas));

    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  @Test
  void basicTransactionManager() {
    final Tx tx = transactionManager.begin();
    tx.update("DELETE FROM basen;");
    assertThat(tx.update("INSERT INTO basen (data) VALUES ('asd');"), is(1));
    final List<String> datas = tx.query("SELECT * FROM basen;", ((ResultSet rs) -> {
      final int id = rs.getInt("id");
      assertThat(id, greaterThanOrEqualTo(1));

      return rs.getString("data");
    }));

    tx.commit();

    final List<String> expectedDatas = new ArrayList<>();
    expectedDatas.add("asd");
    assertThat(datas, is(expectedDatas));
  }

  @Test
  void lambdaTransactionManager() {
    final List<String> datas = transactionManager.begin((final Tx tx) -> {
      tx.update("DELETE FROM basen;");
      assertThat(tx.update("INSERT INTO basen (data) VALUES ('asd');"), is(1));
      return tx.query("SELECT * FROM basen;", ((ResultSet rs) -> {
        final int id = rs.getInt("id");
        assertThat(id, greaterThanOrEqualTo(1));

        return rs.getString("data");
      }));
    });

    final List<String> expectedDatas = new ArrayList<>();
    expectedDatas.add("asd");
    assertThat(datas, is(expectedDatas));
  }

  @Test
  void lambdaReadonlyTransactionManager() {
    transactionManager.begin((final Tx tx) -> {
      tx.update("DELETE FROM basen;");
      assertThat(tx.update("INSERT INTO basen (data) VALUES ('asd');"), is(1));
      return null;
    });

    final List<String> datas = transactionManager.beginReadonly((final Tx tx) -> {
      return tx.query("SELECT * FROM basen;", ((ResultSet rs) -> {
        final int id = rs.getInt("id");
        assertThat(id, greaterThanOrEqualTo(1));

        return rs.getString("data");
      }));
    });

    final List<String> expectedDatas = new ArrayList<>();
    expectedDatas.add("asd");
    assertThat(datas, is(expectedDatas));
  }

  private static DataSource createDataSource() {
    final HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:postgresql://localhost:5432/basen");
    config.setAutoCommit(false);
    config.setUsername("basen");
    config.setPassword("basen");
    config.setMaximumPoolSize(10);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");

    return new HikariDataSource(config);
  }
}
