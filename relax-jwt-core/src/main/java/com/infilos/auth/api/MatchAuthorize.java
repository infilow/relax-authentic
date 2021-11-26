package com.infilos.auth.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MatchAuthorize {
    
    MatchRole role();

    MatchPermit permit();
    
    boolean any() default true;

    boolean all() default false;
}
