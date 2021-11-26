package com.infilos.abac.core;

import com.infilos.abac.MatchPolicy;
import com.infilos.abac.api.PolicyEvaluator;
import com.infilos.auth.core.ProfileHolder;
import com.infilos.auth.core.TokenProfile;
import com.infilos.auth.error.AuthorizeException;
import com.infilos.auth.intercept.context.WebContext;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class MethodAbacAuthorityInterceptor extends InitializableObject implements MethodInterceptor, ApplicationContextAware {
    private ApplicationContext context;
    private PolicyEvaluator policyEvaluator;

    private static final String RETURN = "#return";
    private static final Pattern PARAM_PATTERN = Pattern.compile("#[a-zA-Z][a-zA-Z0-9_]*");

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    protected void internalInit() {
        if (Objects.isNull(policyEvaluator)) {
            policyEvaluator = context.getBean(PolicyEvaluator.class);
        }
    }

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        init();

        // 1. extract annotation from method or class
        Optional<MatchPolicy> matchPolicy = findMatchPolicy(invocation);
        
        // 2.1 check policy with servlet request context
        if (matchPolicy.isPresent() && shouldCheckOnContext(matchPolicy.get())) {
            checkPolicyOnContext(matchPolicy.get().action()).ifPresent(action -> {
                throw action;
            });
        }
        // 2.2 check policy with servlet request parameter as resource
        else if (matchPolicy.isPresent() && shouldCheckOnRequestParam(matchPolicy.get())) {
            checkPolicyOnRequestParam(invocation, matchPolicy.get().resource(), matchPolicy.get().action()).ifPresent(action -> {
                throw action;
            });
        }
        // 2.3 check policy with servlet respond result as resource
        else if (matchPolicy.isPresent() && shouldCheckOnRespondResult(matchPolicy.get())) {
            Object result = invocation.proceed();
            checkPolicyOnRespondResult(result, matchPolicy.get().action()).ifPresent(action -> {
                throw action;
            });
            
            return result;
        }

        // 2.4 invoke without check policy
        return invocation.proceed();
    }

    private boolean shouldCheckOnContext(MatchPolicy matchPolicy) {
        return CommonHelper.isBlank(matchPolicy.resource());
    }

    private boolean shouldCheckOnRequestParam(MatchPolicy matchPolicy) {
        return CommonHelper.isNotBlank(matchPolicy.resource()) && !matchPolicy.resource().equals(RETURN)
            && CommonHelper.isNotBlank(matchPolicy.action());
    }

    private boolean shouldCheckOnRespondResult(MatchPolicy matchPolicy) {
        return CommonHelper.isNotBlank(matchPolicy.resource()) && matchPolicy.resource().equals(RETURN)
            && CommonHelper.isNotBlank(matchPolicy.action());
    }

    private Optional<HttpAction> checkPolicyOnContext(String action) {
        final TokenProfile profile = ProfileHolder.getProfile();    // uid,name,role...
        final HttpServletRequest request = WebContext.getHttpServletRequest(); // should re-construct, eg. uri,query,header,cookie,remote-host..
        final String actualAction = CommonHelper.isNotBlank(action) ? action : request.getMethod();  // REST: GET/POST/UPDATE/DELETE, SQL: SELECT/INSERT/DELETE or DDL..
        final Map<String, Object> environ = EnvironBuilder.create().build();

        boolean ignoredOrAllowed = policyEvaluator.evaluate(profile, request, actualAction, environ);

        if (ignoredOrAllowed) {
            return Optional.empty();
        } else {
            return Optional.of(ForbiddenAction.INSTANCE);
        }
    }

    private Optional<HttpAction> checkPolicyOnRequestParam(MethodInvocation invocation, String paramName, String action) {
        if (!PARAM_PATTERN.matcher(paramName).matches()) {
            throw new AuthorizeException("Illegal MatchPolicy definition of parameter name " + paramName);
        }
        
        List<String> paramNames = Arrays.stream(invocation.getMethod().getParameters()).map(Parameter::getName).collect(Collectors.toList());
        List<Object> paramValues = Arrays.asList(invocation.getArguments());

        if (!paramNames.contains(paramName)) {
            throw new AuthorizeException("Illegal MatchPolicy definition of parameter index -1");
        }
        
        final Object paramValue = paramValues.get(paramNames.indexOf(paramName));
        final TokenProfile profile = ProfileHolder.getProfile();
        final Map<String, Object> environ = EnvironBuilder.create().build();

        boolean ignoredOrAllowed = policyEvaluator.evaluate(profile, paramValue, action, environ);

        if (ignoredOrAllowed) {
            return Optional.empty();
        } else {
            return Optional.of(ForbiddenAction.INSTANCE);
        }
    }

    private Optional<HttpAction> checkPolicyOnRespondResult(Object result, String action) {
        final TokenProfile profile = ProfileHolder.getProfile();
        final Map<String, Object> environ = EnvironBuilder.create().build();

        boolean ignoredOrAllowed = policyEvaluator.evaluate(profile, result, action, environ);

        if (ignoredOrAllowed) {
            return Optional.empty();
        } else {
            return Optional.of(ForbiddenAction.INSTANCE);
        }
    }

    private Optional<MatchPolicy> findMatchPolicy(MethodInvocation invocation) {
        Object targetObject = invocation.getThis();
        Class<?> targetClass = null;

        if (Objects.nonNull(targetObject)) {
            targetClass = targetObject instanceof Class<?> ? (Class<?>) targetObject : AopProxyUtils.ultimateTargetClass(targetObject);
        }

        return Optional.ofNullable(findAnnotation(invocation.getMethod(), targetClass, MatchPolicy.class));
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
}
