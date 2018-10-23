package se.totalorder.basen.api;

import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import se.deadlock.composed.Composed;
import se.totalorder.basen.config.ClientConf;
import se.totalorder.basen.config.DatabaseConf;
import se.totalorder.basen.model.User;
import se.totalorder.basen.testutil.ComposedService;
import se.totalorder.basen.testutil.TestUtil;
import se.deadlock.okok.Client;
import se.deadlock.txman.TxMan;

import javax.sql.DataSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class UserApiIT {
  @RegisterExtension
  static Composed postgres = ComposedService.postgres;

  @RegisterExtension
  static Composed app = ComposedService.app;
  static Client client;
  private static TxMan txMan;

  @BeforeAll
  static void beforeAll() {
    final Config config = TestUtil.testConfigWithDbPort(postgres.externalPort(5432));
    client = ClientConf.createClient().baseUrl("http://localhost:" + app.externalPort(8080)).build();
    final DataSource dataSource = new HikariDataSource(DatabaseConf.get(config));
    TestUtil.migrateDatabase(dataSource);
    txMan = new TxMan(dataSource);
  }

  @BeforeEach
  void setUp() {
    txMan.begin(tx -> tx.update("DELETE FROM usr;"));
  }


  @Test
  void crud() {
    final User created = client.post("/user/1", "asd").json(User.class);
    assertThat(created, is(new User(1, "asd")));

    final User found = client.get("/user/1").json(User.class);
    assertThat(created, is(found));

    final User modified = client.put("/user/1", "qwe").json(User.class);
    assertThat(modified, is(new User(1, "qwe")));

    final User foundAgain = client.get("/user/1").json(User.class);
    assertThat(foundAgain, is(modified));

    final User deleted = client.delete("/user/1").json(User.class);
    assertThat(deleted, is(modified));

    final User gone = client.get("/user/1").json(User.class);
    assertThat(gone, nullValue());
  }

  @Test
  void getMultiple() {
    final User first = client.post("/user/1", "asd").json(User.class);
    final User second = client.post("/user/2", "qwe").json(User.class);

    final User[] users = client.get("/user").json(User[].class);
    assertThat(users, is(new User[]{first, second}));
  }

  @Test
  void proxy() {
    final User created = client.post("/user/1", "asd").json(User.class);
    assertThat(created, is(new User(1, "asd")));

    final User found = client.get("/proxy-user/1").json(User.class);
    assertThat(created, is(found));
  }
}