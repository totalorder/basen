package se.totalorder.basen.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import se.deadlock.composed.Composed;
import se.deadlock.okok.Client;
import se.deadlock.txman.TxMan;
import se.totalorder.basen.config.DatabaseConf;
import se.totalorder.basen.model.User;
import se.totalorder.basen.testutil.ComposedService;
import se.totalorder.basen.testutil.TestUtil;

class UserApiTest {
  @RegisterExtension
  static Composed postgres = ComposedService.postgres;
  UserApi userApi;
  static DataSource dataSource;

  @BeforeAll
  static void setUpClass() {
    final Config config = TestUtil.testConfigWithDbPort(postgres.externalPort(5432));
    dataSource = new HikariDataSource(DatabaseConf.get(config));
    final Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    flyway.migrate();
  }

  @BeforeEach
  void setUp() {
    final TxMan txMan = new TxMan(dataSource);
    txMan.begin(tx -> tx.update("DELETE FROM usr;"));
    Client client = mock(Client.class);
    userApi = new UserApi(client, txMan);
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