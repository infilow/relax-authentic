package com.infilos.auth.api;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MatchPermit {

    @AliasFor("anyOf")
    String[] value() default {};

    String[] anyOf() default {};

    String[] allOf() default {};
}
