package com.infilos.abac;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MatchPolicy {
    
    /**
     * resource location: eg. #param-name, #return
     * @PathVariable
     * @RequestHeader
     * @CookieValue
     * @RequestParam
     * @RequestBody
     * return result
     */
    String resource() default "";
    
    /**
     * action: eg. USER_CREATE
     */
    String action() default "";
}
