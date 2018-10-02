package se.totalorder.basen.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import javax.sql.DataSource;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.totalorder.basen.config.DatabaseConf;
import se.totalorder.basen.model.User;
import se.totalorder.basen.testutil.TestUtil;
import se.totalorder.basen.testutil.runhook.hooks.App;
import se.totalorder.basen.testutil.runhook.hooks.AppPort;
import se.totalorder.basen.testutil.runhook.hooks.Postgres;
import se.totalorder.basen.testutil.runhook.hooks.PostgresPort;
import se.totalorder.basen.tx.TxMan;

@Postgres
@App
class UserApiIT {
  @AppPort
  static int appPort;

  @PostgresPort
  static int postgresPort;

  static OkHttpClient client = new OkHttpClient();
  static ObjectMapper objectMapper = new ObjectMapper();
  private static TxMan txMan;

  @BeforeAll
  static void beforeAll() {
    final DataSource dataSource = new HikariDataSource(DatabaseConf.get("test", postgresPort));
    TestUtil.migrateDatabase(dataSource);
    txMan = new TxMan(dataSource);
  }

  @BeforeEach
  void setUp() {
    txMan.begin(tx -> tx.update("DELETE FROM usr;"));
  }

  Request.Builder request(final String url) {
    return new Request.Builder().url("http://localhost:" + appPort + url);
  }

  <T> T post(final String url, final String body, final Class<T> responseClass) {
    return send(request(url).post(RequestBody.create(MediaType.parse("text/plain"), body)).build(), responseClass);
  }

  <T> T put(final String url, final String body, final Class<T> responseClass) {
    return send(request(url).put(RequestBody.create(MediaType.parse("text/plain"), body)).build(), responseClass);
  }

  <T> T get(final String url, final Class<T> responseClass) {
    return send(request(url).get().build(), responseClass);
  }

  <T> T delete(final String url, final Class<T> responseClass) {
    return send(request(url).delete().build(), responseClass);
  }

  <T> T send(final Request request, final Class<T> responseClass) {
    try {
      final String body = client.newCall(request).execute().body().string();
      if (body.isEmpty()) {
          return null;
      }
      return objectMapper.readValue(body, responseClass);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void crud() {
    final User created = post("/user/1", "asd", User.class);
    assertThat(created, is(new User(1, "asd")));

    final User found = get("/user/1", User.class);
    assertThat(created, is(found));

    final User modified = put("/user/1", "qwe", User.class);
    assertThat(modified, is(new User(1, "qwe")));

    final User foundAgain = get("/user/1", User.class);
    assertThat(foundAgain, is(modified));

    final User deleted = delete("/user/1", User.class);
    assertThat(deleted, is(modified));

    final User gone = get("/user/1", User.class);
    assertThat(gone, nullValue());
  }

  @Test
  void getMultiple() {
    final User first = post("/user/1", "asd", User.class);
    final User second = post("/user/2", "qwe", User.class);

    final User[] users = get("/user", User[].class);
    assertThat(users, is(new User[]{first, second}));
  }
}