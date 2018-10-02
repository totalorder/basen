package se.totalorder.basen.api;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.totalorder.basen.model.User;
import se.totalorder.basen.testutil.runhook.hooks.App;
import se.totalorder.basen.testutil.runhook.hooks.AppPort;
import se.totalorder.basen.testutil.runhook.hooks.PostgresMigrate;
import se.totalorder.basen.testutil.runhook.hooks.PostgresMigratePort;

import javax.sql.DataSource;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@App
class UserApiIT {
  @AppPort
  static int appPort;
  HttpClient client;

  @BeforeAll
  static void setUpClass() {
      HttpClient client = HttpClient.newHttpClient();

  }

  @BeforeEach
  void setUp() {

  }

  void request(String URL) {
      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create("http://openjdk.java.net/"))
              .build();
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