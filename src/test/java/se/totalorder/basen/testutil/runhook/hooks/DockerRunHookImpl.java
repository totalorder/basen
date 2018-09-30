package se.totalorder.basen.testutil.runhook.hooks;

import lombok.extern.slf4j.Slf4j;
import se.totalorder.basen.testutil.runhook.RunHook;

@Slf4j
public class DockerRunHookImpl implements RunHook {
    public DockerRunHookImpl(final DockerRunHook config) {
        log.info(config.value());
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
