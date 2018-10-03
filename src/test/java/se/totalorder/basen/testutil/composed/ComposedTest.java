package se.totalorder.basen.testutil.composed;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@Slf4j
class ComposedTest {
  @RegisterExtension
  static Composed postgres = Composed.builder()
      .projectName("basentest")
      .dockerComposeFilePath("docker-compose.yml")
      .serviceName("postgres")
      .build();

  @Test
  void serviceReturnsExternalPort() {
    assertThat(postgres.externalPort(5432), is(greaterThan(0)));
  }
}
