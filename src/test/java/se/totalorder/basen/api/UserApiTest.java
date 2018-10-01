package se.totalorder.basen.api;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.totalorder.basen.config.DatabaseConf;
import se.totalorder.basen.model.User;
import se.totalorder.basen.testutil.runhook.hooks.DockerRunHook;
import se.totalorder.basen.tx.TxMan;

@DockerRunHook("postgres")
class UserApiTest {
  UserApi userApi;
  static DataSource dataSource;

  @BeforeAll
  static void setUpClass() {
    dataSource = new HikariDataSource(DatabaseConf.get("test"));
    final Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    flyway.migrate();
  }

  @BeforeEach
  void setUp() {
    final TxMan txMan = new TxMan(dataSource);
    txMan.begin(tx -> tx.update("DELETE FROM usr;"));

    userApi = new UserApi(txMan);
  }

  @Test
  void crud() {
    final User created = userApi.create("1", "asd");
    assertThat(created, is(new User(1, "asd")));

    final User found = userApi.get("1");
    assertThat(created, is(found));

    final User modified = userApi.put("1", "qwe");
    assertThat(modified, is(new User(1, "qwe")));

    final User foundAgain = userApi.get("1");
    assertThat(foundAgain, is(modified));

    final User deleted = userApi.delete("1");
    assertThat(deleted, is(modified));

    final User gone = userApi.get("1");
    assertThat(gone, nullValue());
  }

  @Test
  void getMultiple() {
    final User first = userApi.create("1", "asd");
    final User second = userApi.create("2", "qwe");

    final List<User> users = userApi.get();
    assertThat(users, is(listOf(first, second)));
  }

  private <T> List<T> listOf(T... elements) {
    return Arrays.asList(elements);
  }
}