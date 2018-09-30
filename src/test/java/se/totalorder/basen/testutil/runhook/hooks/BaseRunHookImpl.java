package se.totalorder.basen.testutil.runhook.hooks;

import lombok.extern.slf4j.Slf4j;
import se.totalorder.basen.testutil.runhook.RunHook;

@Slf4j
public class BaseRunHookImpl implements RunHook {
    public BaseRunHookImpl(final BaseRunHook config) throws Exception {
        config.hook().getConstructor(config.config()).newInstance(config);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
