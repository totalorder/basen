package se.totalorder.basen.testutil.runhook.hooks.impl;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.Cluster;
import com.palantir.docker.compose.connection.Container;
import com.palantir.docker.compose.connection.waiting.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Duration;
import se.totalorder.basen.testutil.runhook.RunHook;
import se.totalorder.basen.testutil.runhook.hooks.Docker;
import se.totalorder.basen.testutil.runhook.hooks.DockerPort;

@Slf4j
public class DockerImpl implements RunHook {
  private final Docker config;
  private static final DockerComposeRule rule = DockerComposeRule.builder()
      .file("src/test/resources/docker-compose.yml")
        .projectName(ProjectName.fromString("basentest"))
      .build();

  private static final Cluster cluster = rule.containers();
  private static Set<String> started = new HashSet<>();

  public DockerImpl(final Docker config) {
    this.config = config;
  }

  @Override
  public void setupOnce() throws Exception {
    if (started.add(config.value())) {
      log.info("Starting service: " + config.value());
      final Container container = cluster.container(config.value());
      container.up();

      final HealthCheck<Container> containerHealthCheck = container.ports().stream()
          .filter(dockerPort -> dockerPort.getInternalPort() == 8080).findFirst()
          .map(dockerPort -> HealthChecks.toRespond2xxOverHttp(
              8080,
              (actualPort) -> "http://localhost:" + actualPort.getExternalPort() + "/"))
          .orElse(HealthChecks.toHaveAllPortsOpen());

      new ClusterWait(
          cluster -> containerHealthCheck.isHealthy(container),
          Duration.standardSeconds(5)).waitUntilReady(null);
    }
  }

  @Override
  public void beforeAll(final Class<?> testClass) throws Exception {
    processFieldAnnotations(testClass);
  }

  @Override
  public void stop() {

  }

  private void processFieldAnnotations(final Class<?> testClass) throws Exception {
    for (final Field field : testClass.getDeclaredFields()) {
      final Set<Annotation> visisted = new HashSet<>();
      for (final Annotation annotation : field.getAnnotations()) {
        processAnnotations(annotation, testClass, field, visisted);
      }
    }
  }

  private void processAnnotations(
      final Annotation annotation,
      final Class<?> testClass,
      final Field field,
      final Set<Annotation> visited) throws Exception {
    if (annotation.annotationType().equals(DockerPort.class)) {
      final DockerPort dockerPort = (DockerPort) annotation;
      if (dockerPort.value().equals(config.value())) {
        field.setAccessible(true);
        final com.palantir.docker.compose.connection.DockerPort actualDockerPort =
            rule.containers().container(dockerPort.value()).ports()
                .stream().findFirst().orElse(null);
        if (actualDockerPort != null) {
          field.set(testClass, actualDockerPort.getExternalPort());
        } else {
          throw new RuntimeException("No port reachable for service " + dockerPort.value() + " required by "
              + testClass.getCanonicalName() + "." + field.getName());
        }
      }
    }

    for (final Annotation subElementAnnotation : annotation.annotationType().getAnnotations()) {
      if (visited.add(subElementAnnotation)) {
        processAnnotations(subElementAnnotation, testClass, field, visited);
      }
    }
  }
}
