package se.totalorder.basen.testutil.runhook;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class RunHookExtension implements BeforeAllCallback, BeforeEachCallback, ExtensionContext.Store.CloseableResource {

    private static boolean started = false;
    private static boolean stopped = false;
    private static Map<RunHookConfig, RunHook> initializedHooks = new HashMap<>();

    @Override
    public void beforeAll(final ExtensionContext context) {
        if (!started) {
            started = true;
            stopped = false;
            context.getRoot().getStore(GLOBAL).put(this.getClass().getCanonicalName(), this);
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

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        if (!context.getTestClass().isPresent()) {
            return;
        }

        final Class<?> testClass = context.getTestClass().get();

        final List<RunHookConfig> runHookConfigs = findAnnotations(testClass, new HashSet<>(), null);
        for (final RunHookConfig runHookConfig : runHookConfigs) {
            if (initializedHooks.get(runHookConfig) == null) {
                final RunHook runHookInstance = runHookConfig.provider.hook().getConstructor(runHookConfig.provider.config())
                        .newInstance(runHookConfig.annotation);
                initializedHooks.put(runHookConfig, runHookInstance);
                runHookInstance.start();
            }
        }
    }

    private List<RunHookConfig> findAnnotations(final AnnotatedElement element, final Set<Annotation> visited, Annotation annotation) {
        final List<RunHookConfig> configs = new ArrayList<>();

        if (element.isAnnotationPresent(RunHookProvider.class)) {
            final RunHookProvider provider = element.getAnnotationsByType(RunHookProvider.class)[0];
//            for (Annotation annotation : element.getAnnotationsByType(provider.config())) {
                configs.add(new RunHookConfig(provider, annotation));
//            }
        }

        for (final Annotation subElementAnnotation : element.getAnnotations()) {
            if (visited.add(subElementAnnotation)) {
                configs.addAll(findAnnotations(subElementAnnotation.annotationType(), visited, subElementAnnotation));
            }
        }

        return configs;
//        if (configs.size() > 0) {
//            return configs;
//        }

////        final List<RunHookConfig> result = new ArrayList<>();
//        for (final Annotation subElementAnnotation : element.getAnnotations()) {
//
//        }
//
//        return configs;
    }

    @Data
    private class RunHookConfig {
        public final RunHookProvider provider;
        public final Annotation annotation;
    }

}