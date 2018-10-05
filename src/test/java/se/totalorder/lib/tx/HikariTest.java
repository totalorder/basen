package se.totalorder.lib.tx;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import se.deadlock.composed.Composed;
import se.totalorder.basen.config.DatabaseConf;
import se.totalorder.basen.testutil.ComposedService;
import se.totalorder.basen.testutil.TestUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

class HikariTest {
  @RegisterExtension
  static Composed postgres = ComposedService.postgres;

  static DataSource dataSource;
  static TxMan transactionManager;

  @BeforeAll
  static void beforeAll() {
    dataSource = new HikariDataSource(DatabaseConf.get("test", postgres.externalPort(5432)));
    TestUtil.migrateDatabase(dataSource);

    transactionManager = new TxMan(dataSource);
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
}