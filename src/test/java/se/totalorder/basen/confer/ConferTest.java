package se.totalorder.basen.confer;

import com.typesafe.config.Config;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ConferTest {
  final Confer.ConfigBuilder builder = Confer.builder()
      .directory("confer")
      .name("app");

  @Test
  void noEnv() {
    final Config config = builder.build();

    assertThat(config.getInt("port"), is(8080));
    assertThat(config.getString("db.username"), is("postgres"));
    assertThat(config.getString("db.password"), is("devpass"));
  }

  @Test
  void stagingEnv() {
    final Config config = builder.env("staging").build();

    assertThat(config.getInt("port"), is(8080));
    assertThat(config.getString("db.username"), is("postgres"));
    assertThat(config.getString("db.password"), is("stagingpass"));
  }

  @Test
  void productionEnv() {
    final Config config = builder.env("production").build();

    assertThat(config.getInt("port"), is(8080));
    assertThat(config.getString("db.username"), is("postgres-readonly"));
    assertThat(config.getString("db.password"), is("productionpass"));
  }

  @Test
  void stagingProductionEnv() {
    final Config config = builder.env("staging").env("production").build();

    assertThat(config.getInt("port"), is(8080));
    assertThat(config.getString("db.username"), is("postgres-readonly"));
    assertThat(config.getString("db.password"), is("productionpass"));
  }

  @Test
  void productionStagingEnv() {
    final Config config = builder.env("production").env("staging").build();

    assertThat(config.getInt("port"), is(8080));
    assertThat(config.getString("db.username"), is("postgres-readonly"));
    assertThat(config.getString("db.password"), is("stagingpass"));
  }
}