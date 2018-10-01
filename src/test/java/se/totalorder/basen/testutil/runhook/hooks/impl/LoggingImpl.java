package se.totalorder.basen.testutil.runhook.hooks.impl;

import lombok.extern.slf4j.Slf4j;
import se.totalorder.basen.testutil.runhook.RunHook;
import se.totalorder.basen.testutil.runhook.hooks.Logging;

@Slf4j
public class LoggingImpl implements RunHook {
    private Logging config;

    public LoggingImpl(final Logging config) {
        this.config = config;
    }

    @Override
    public void setupOnce() throws Exception {

    }

    @Override
    public void beforeAll(Class<?> testClass) {
        log.info("Test run started: " + config.value());
    }

    @Override
    public void stop() {
        log.info("Test ended: " + config.value());
    }
}
