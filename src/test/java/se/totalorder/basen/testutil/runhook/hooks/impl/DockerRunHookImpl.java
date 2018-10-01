package se.totalorder.basen.testutil.runhook.hooks.impl;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import lombok.extern.slf4j.Slf4j;
import se.totalorder.basen.testutil.runhook.RunHook;
import se.totalorder.basen.testutil.runhook.hooks.DockerRunHook;

@Slf4j
public class DockerRunHookImpl implements RunHook {
    private final DockerRunHook config;

    public DockerRunHookImpl(final DockerRunHook config) {
        this.config = config;
    }

    @Override
    public void start() throws Exception {
        final DockerComposeRule rule = DockerComposeRule.builder()
            .file("src/test/resources/docker-compose.yml")
            .waitingForService(config.value(), HealthChecks.toHaveAllPortsOpen())
            .projectName(ProjectName.fromString("authcapturetest"))
            .build();

        rule.before();
    }

    @Override
    public void stop() {

    }
}
