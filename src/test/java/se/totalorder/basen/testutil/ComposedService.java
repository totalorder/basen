package se.totalorder.basen.testutil;

import com.palantir.docker.compose.connection.waiting.HealthChecks;
import se.deadlock.composed.Composed;

public class ComposedService {
  private static Composed.ComposedBuilder builder = Composed.builder()
      .projectName("basentest")
      .dockerComposeFilePath("docker-compose.yml");

  public static Composed postgres = builder
      .serviceName("postgres")
      .build();

  public static Composed app = builder
      .serviceName("app")
      .healtCheck(HealthChecks.toRespond2xxOverHttp(8080, dockerPort ->
          "http://localhost:" + dockerPort.getExternalPort() + "/"))
      .build();
}
