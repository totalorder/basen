package se.totalorder.basen.testutil.runhook.hooks.impl;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.ImmutableDockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import lombok.extern.slf4j.Slf4j;
import se.totalorder.basen.testutil.runhook.DockerPort;
import se.totalorder.basen.testutil.runhook.RunHook;
import se.totalorder.basen.testutil.runhook.hooks.DockerRunHook;

import java.lang.reflect.Field;

@Slf4j
public class DockerRunHookImpl implements RunHook {
    private final DockerRunHook config;
    private final ImmutableDockerComposeRule rule;

    public DockerRunHookImpl(final DockerRunHook config) {
        this.config = config;
        rule = DockerComposeRule.builder()
                .file("src/test/resources/docker-compose.yml")
                .waitingForService(config.value(), HealthChecks.toHaveAllPortsOpen())
                .projectName(ProjectName.fromString("authcapturetest"))
                .build();
    }

    @Override
    public void start() throws Exception {
        log.info("Starting docker-compose " + config.value());
        rule.before();
    }

    @Override
    public void stop() {

    }

    @Override
    public void instanceCreated(Object testInstance) throws Exception {
        for (final Field field : testInstance.getClass().getDeclaredFields()) {
            final DockerPort[] dockerPorts = field.getAnnotationsByType(DockerPort.class);
            if (dockerPorts.length > 0) {
                final DockerPort dockerPort = dockerPorts[0];
                if (dockerPort.value().equals(config.value())) {

                    field.setAccessible(true);

                    final com.palantir.docker.compose.connection.DockerPort actualDockerPort =
                            rule.containers().container(dockerPort.value()).ports()
                                    .stream().findFirst().orElse(null);
                    if (actualDockerPort != null) {
                        field.set(testInstance, actualDockerPort.getExternalPort());
                    }
                }
            }
        }
    }
}
