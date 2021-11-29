package com.infilos.auth.aop;

import com.infilos.auth.api.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Static method intercept, initiate with an interceptor as advice.
 */
@Slf4j
public class StaticMethodAuthorityAdvisor extends StaticMethodMatcherPointcutAdvisor {

    private static final List<Class<? extends Annotation>> MARK_CLASSES =
        Arrays.asList(MatchRole.class, MatchPermit.class, MatchAuthorize.class);

    protected List<Class<? extends Annotation>> markClasses() {
        return MARK_CLASSES;
    }

    @Override
    public boolean matches(@NonNull Method method, @Nullable Class<?> targetClass) {
        if (hasMarkAuthority(method)) {
            log.debug("Authority annotation provide: {}.{}", targetClass == null ? "$" : targetClass.getSimpleName(), method.getName());
            return true;
        }

        // or check subclass method
        if (targetClass != null) {
            try {
                Method subMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
                
                return hasMarkAuthority(subMethod) || hasMarkAuthority(targetClass);
            } catch (NoSuchMethodException ignore) {
            }
        }

        return false;
    }

    private boolean hasMarkAuthority(Class<?> targetClazz) {
        for (Class<? extends Annotation> annClass : markClasses()) {
            Annotation anno = AnnotationUtils.findAnnotation(targetClazz, annClass);
            if (anno != null) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMarkAuthority(Method method) {
        for (Class<? extends Annotation> annClass : markClasses()) {
            Annotation anno = AnnotationUtils.findAnnotation(method, annClass);
            if (anno != null) {
                return true;
            }
        }
        return false;
    }
}
