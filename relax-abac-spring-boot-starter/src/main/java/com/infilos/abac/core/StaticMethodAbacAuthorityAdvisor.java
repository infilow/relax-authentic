package com.infilos.abac.core;

import com.infilos.abac.MatchPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

@Slf4j
public class StaticMethodAbacAuthorityAdvisor extends StaticMethodMatcherPointcutAdvisor {

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

    private boolean hasMarkAuthority(Class<?> targetClass) {
        return AnnotationUtils.findAnnotation(targetClass, MatchPolicy.class) != null;
    }

    private boolean hasMarkAuthority(Method method) {
        return AnnotationUtils.findAnnotation(method, MatchPolicy.class) != null;
    }
}
