package se.totalorder.basen.testutil.runhook.hooks.impl;

import lombok.extern.slf4j.Slf4j;
import se.totalorder.basen.testutil.runhook.RunHook;
import se.totalorder.basen.testutil.runhook.hooks.LoggingRunHook;

@Slf4j
public class LoggingRunHookImpl implements RunHook {
    private LoggingRunHook config;

    public LoggingRunHookImpl(final LoggingRunHook config) {
        this.config = config;
    }

    @Override
    public void start() {
        log.info("Test run started: " + config.value());
    }

    @Override
    public void stop() {
        log.info("Test ended: " + config.value());
    }
}
