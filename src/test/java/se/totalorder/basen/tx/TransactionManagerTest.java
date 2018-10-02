package se.totalorder.basen.tx;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.totalorder.basen.config.DatabaseConf;
import se.totalorder.basen.testutil.runhook.hooks.Postgres;
import se.totalorder.basen.testutil.runhook.hooks.PostgresPort;

@Postgres
class TransactionManagerTest {
  @PostgresPort
  static int postgresPort;

  static DataSource dataSource;
  static TxMan txMan;

  @BeforeAll
  static void beforeAll() {
    dataSource = new HikariDataSource(DatabaseConf.get("test", postgresPort));
    txMan = new TxMan(dataSource);
    txMan.begin(tx -> {
      tx.update("DROP TABLE IF EXISTS record;");
      tx.update("CREATE TABLE record (key INT PRIMARY KEY, value TEXT NOT NULL);");
      return null;
    });
  }

  @BeforeEach
  void setUp() {
    txMan.begin(tx -> {
      tx.update("DELETE FROM record;");
      return null;
    });
  }

  @Test
  void execute() {
    final List<Record> records = txMan.begin(tx -> {
      assertThat(tx.update("INSERT INTO record (key, value) VALUES (?, ?);", 123, "abc"), is(1));
      return tx.execute("SELECT * FROM record;", recordMapper);
    });

    assertThat(records, is(listOf(new Record(123, "abc"))));
  }

  @Test
  void executeReadOnly() {
    txMan.begin(tx -> tx.update("INSERT INTO record (key, value) VALUES (?, ?);", 123, "abc"));

    final List<Record> records = txMan.beginReadonly(tx ->
        tx.execute("SELECT * FROM record;", recordMapper));

    assertThat(records, is(listOf(new Record(123, "abc"))));
  }

  @Test
  void executeRollback() {
    final List<Record> records = txMan.begin(tx -> {
      assertThat(tx.update("INSERT INTO record (key, value) VALUES (?, ?);", 123, "abc"), is(1));
      final List<Record> result = tx.execute("SELECT * FROM record;", recordMapper);
      tx.setRollback();
      return result;
    });

    assertThat(records, is(listOf(new Record(123, "abc"))));

    final List<Record> recordsFromNewTx = txMan.beginReadonly(tx ->
        tx.execute("SELECT * FROM record;", recordMapper));
    assertThat(recordsFromNewTx, is(listOf()));
  }

  class Record {
    private final int key;
    private final String value;

    Record(final int key, final String value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      final Record record = (Record) o;
      return key == record.key &&
          Objects.equals(value, record.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, value);
    }
  }

  private RowMapper<Record> recordMapper = (resultSet) -> new Record(
      resultSet.getInt("key"),
      resultSet.getString("value")
  );

  private <T> List<T> listOf(T... elements) {
    return Arrays.asList(elements);
  }
}