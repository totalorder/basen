package se.totalorder.basen.testutil.runhook.hooks.impl;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.ImmutableDockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import se.totalorder.basen.testutil.runhook.RunHook;
import se.totalorder.basen.testutil.runhook.hooks.Docker;
import se.totalorder.basen.testutil.runhook.hooks.DockerPort;

@Slf4j
public class DockerImpl implements RunHook {
  private final Docker config;
  private final ImmutableDockerComposeRule rule;

  public DockerImpl(final Docker config) {
    this.config = config;
    rule = DockerComposeRule.builder()
        .file("src/test/resources/docker-compose.yml")
        .waitingForService(config.value(), HealthChecks.toHaveAllPortsOpen())
        .projectName(ProjectName.fromString("basentest"))
        .build();
  }

  @Override
  public void setupOnce() throws Exception {
    log.info("Starting docker-compose: " + config.value());
    rule.before();
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
    log.info("Processing annotation " + annotation);
    if (annotation.annotationType().equals(DockerPort.class)) {
      final DockerPort dockerPort = (DockerPort) annotation;
      log.info("Found " + dockerPort + ", related to " + config + "?");
      if (dockerPort.value().equals(config.value())) {
        field.setAccessible(true);
        final com.palantir.docker.compose.connection.DockerPort actualDockerPort =
            rule.containers().container(dockerPort.value()).ports()
                .stream().findFirst().orElse(null);
        log.info("Sure is, actual port: " + actualDockerPort);
        log.info("Containers: " + rule.containers().container("app").ports());
        if (actualDockerPort != null) {
          log.info("Setting " + field.getName() + " to " + actualDockerPort.getExternalPort());
          field.set(testClass, actualDockerPort.getExternalPort());
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
