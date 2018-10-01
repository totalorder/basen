package se.totalorder.basen.testutil.runhook;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class RunHookExtension implements BeforeAllCallback, TestInstancePostProcessor, ExtensionContext.Store.CloseableResource {

    private static boolean started = false;
    private static boolean stopped = false;
    private static Map<RunHookConfig, RunHook> initializedHooks = new HashMap<>();

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        if (!started) {
            started = true;
            stopped = false;
            context.getRoot().getStore(GLOBAL).put(this.getClass().getCanonicalName(), this);
        }

        if (!context.getTestClass().isPresent()) {
            return;
        }

        final Class<?> testClass = context.getTestClass().get();

        final List<RunHookConfig> runHookConfigs = findAnnotations(testClass, new HashSet<>(), null);
        for (final RunHookConfig runHookConfig : runHookConfigs) {
            final RunHook runHookInstance = runHookConfig.provider.hook().getConstructor(runHookConfig.provider.annotation())
                    .newInstance(runHookConfig.annotation);
            runHookInstance.beforeAll(testClass);

            if (initializedHooks.get(runHookConfig) == null) {
                initializedHooks.put(runHookConfig, runHookInstance);
                runHookInstance.setupOnce();
            }
        }
    }

    @Override
    public void close() {
        if (!stopped) {
            initializedHooks.forEach((key, value) -> value.stop());
            initializedHooks.clear();
            stopped = true;
            started = false;
        }
    }

    private List<RunHookConfig> findAnnotations(
        final AnnotatedElement element, final Set<Annotation> visited, final AnnotatedElement superElement) {
        final List<RunHookConfig> configs = new ArrayList<>();

        if (element.isAnnotationPresent(RunHookProvider.class)) {
            final RunHookProvider provider = element.getAnnotationsByType(RunHookProvider.class)[0];
            for (Annotation annotation : superElement.getAnnotationsByType(provider.annotation())) {
                configs.add(new RunHookConfig(provider, annotation));
            }
        }

        for (final Annotation subElementAnnotation : element.getAnnotations()) {
            if (visited.add(subElementAnnotation)) {
                configs.addAll(findAnnotations(subElementAnnotation.annotationType(), visited, element));
            }
        }

        return configs;
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (final RunHook runHook : initializedHooks.values()) {
//            runHook.instanceCreated(testInstance);
        }
    }

    @Data
    private class RunHookConfig {
        public final RunHookProvider provider;
        public final Annotation annotation;
    }

}