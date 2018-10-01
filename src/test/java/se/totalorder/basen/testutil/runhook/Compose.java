package se.totalorder.basen.testutil.runhook;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class Compose {
    private final static DockerComposeRule.Builder baseBuilder = DockerComposeRule.builder()
            .file("src/test/resources/docker-compose.yml")
            .projectName(ProjectName.fromString("authcapturetest"));

    private final String service;
    private int mainPort;
    private final DockerComposeRule dockerComposeRule;
    private final static Set<String> started = new HashSet<>();

    private Compose(final String service, int mainPort, final DockerComposeRule dockerComposeRule) {
        this.service = service;
        this.mainPort = mainPort;
        this.dockerComposeRule = dockerComposeRule;
    }

    public static Compose postgres() {
        return new Compose("postgres", 5432,
                baseBuilder.waitingForService("postgres", HealthChecks.toHaveAllPortsOpen()).build());
    }

    public int port() {
        return dockerComposeRule.containers().container(service).port(mainPort).getExternalPort();
    }

    public void beforeAll() {
        log.info("Starting " + service);
        if (!started.contains(service)) {
            try {
                dockerComposeRule.before();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
