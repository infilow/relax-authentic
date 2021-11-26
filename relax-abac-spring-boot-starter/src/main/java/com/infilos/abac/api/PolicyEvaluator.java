package com.infilos.abac.api;

public interface PolicyEvaluator {
    
    /**
     * Evaluate policy to check if user is able to apply operate on resource.
     * 
     * @param profile of user
     * @param resource to apply
     * @param action to operate
     * @param environ of current
     */
    boolean evaluate(Object profile, Object resource, Object action, Object environ);
}
