package com.infilos.auth.aop;

import com.infilos.auth.api.MatchPermit;
import com.infilos.auth.api.MatchRole;
import com.infilos.auth.core.*;
import com.infilos.auth.error.AuthorizeException;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.pac4j.core.exception.http.*;
import org.pac4j.core.util.InitializableObject;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * Main authority interceptor, intercept all methods and check authority if marked specific annotation.
 */
@Slf4j
public class MethodAuthorityInterceptor extends InitializableObject implements MethodInterceptor, ApplicationContextAware {
    protected ApplicationContext context;
    protected AuthorityManager authorityManager;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    protected void internalInit() {
        if (Objects.isNull(authorityManager)) {
            this.authorityManager = context.getBean(AuthorityManager.class);
        }
    }

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        init();

        // intercept and throw error if failed
        Optional<HttpAction> httpAction = intercept(invocation);

        if (httpAction.isPresent()) {
            throw httpAction.get();
        }

        return invocation.proceed();
    }

    protected Optional<HttpAction> intercept(MethodInvocation invocation) {
        Object targetObject = invocation.getThis();
        Class<?> targetClass = null;

        if (Objects.nonNull(targetObject)) {
            targetClass = targetObject instanceof Class<?> ? (Class<?>) targetObject : AopProxyUtils.ultimateTargetClass(targetObject);
        }

        MatchRole matchRole = findAnnotation(invocation.getMethod(), targetClass, MatchRole.class);
        if (Objects.nonNull(matchRole)) {
            return checkAuthority(true, matchRole.allOf(), matchRole.anyOf(), authorityManager::resolveRoles);
        }

        MatchPermit matchPermit = findAnnotation(invocation.getMethod(), targetClass, MatchPermit.class);
        if (Objects.nonNull(matchPermit)) {
            return checkAuthority(false, matchPermit.allOf(), matchPermit.anyOf(), authorityManager::resolvePermissions);
        }

        return Optional.empty();
    }

    protected <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass, Class<A> annotationClass) {
        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        A annotation = AnnotationUtils.findAnnotation(specificMethod, annotationClass);

        if (annotation != null) {
            log.debug("Method {} has marked: {}", specificMethod, annotation);
            return annotation;
        }

        // Check the original (e.g. interface) method
        if (specificMethod != method) {
            annotation = AnnotationUtils.findAnnotation(method, annotationClass);

            if (annotation != null) {
                log.debug("Method {} has marked: {}", method, annotation);
                return annotation;
            }
        }

        // Check the class-level (note declaringClass, not targetClass which may not actually implement the method)
        annotation = AnnotationUtils.findAnnotation(specificMethod.getDeclaringClass(), annotationClass);

        if (annotation != null) {
            log.debug("Method {} has marked: {}", specificMethod.getDeclaringClass().getName(), annotation);
            return annotation;
        }

        return null;
    }

    protected Optional<HttpAction> checkAuthority(final boolean isRole,
                                                  final String[] requireAll,
                                                  final String[] requireAny,
                                                  final Function<TokenProfile, Set<String>> providesResolver) {
        final TokenProfile profile = ProfileHolder.getProfile();

        if (profile == null) {
            log.debug("TokenProfile not found, authorize failed!");
            return Optional.of(UnauthorizedAction.INSTANCE);
        }
        if (authorityManager.shouldSkipAuthorize(profile)) {
            return Optional.empty();
        }

        if (isEmpty(requireAll) && isEmpty(requireAny)) {
            throw new AuthorizeException("Neither required all nor any defined!");
        }
        if (nonEmpty(requireAll) && nonEmpty(requireAny)) {
            throw new AuthorizeException("Both required all and any defined!");
        }
        log.debug("Resource require roles: all-{}, any-{}", requireAll, requireAny);

        boolean isRequireAll = nonEmpty(requireAll);
        Set<String> requires = isRequireAll ? setOf(requireAll) : setOf(requireAny);

        return checkAuthority(profile, isRole, isRequireAll, requires, providesResolver);
    }

    protected Optional<HttpAction> checkAuthority(final TokenProfile profile,
                                                  final boolean isRole,
                                                  final boolean isRequireAll,
                                                  final Set<String> requires,
                                                  final Function<TokenProfile, Set<String>> providesResolver) {
        if (isRole && authorityManager.checkUserRoles(requires, providesResolver.apply(profile), isRequireAll)) {
            return Optional.empty();
        }
        if (!isRole && authorityManager.checkUserPermissions(requires, providesResolver.apply(profile), isRequireAll)) {
            return Optional.empty();
        }

        return Optional.of(ForbiddenAction.INSTANCE);
    }

    protected boolean isEmpty(String[] items) {
        return items.length == 0;
    }

    protected boolean nonEmpty(String[] items) {
        return items.length != 0;
    }

    protected Set<String> setOf(String[] items) {
        return new HashSet<>(Arrays.asList(items));
    }
}
