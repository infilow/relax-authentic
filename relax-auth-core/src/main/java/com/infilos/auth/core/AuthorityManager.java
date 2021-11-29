package com.infilos.auth.core;

import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * Execute user authority depends on role & permission.
 */
@RequiredArgsConstructor
public class AuthorityManager {
    private final String skipAuthRolePermission;
    
    /**
     * Mark a user profile would skip all the authorization check.
     */
    public void markSkipAuthorize(TokenProfile profile) {
        profile.addRole(skipAuthRolePermission);
    }
    
    /**
     * Check if a user profile should skip all the authorization check.
     */
    public boolean shouldSkipAuthorize(TokenProfile profile) {
        return profile.getRoles().contains(skipAuthRolePermission);
    }
    
    /**
     * Resolve user roles.
     */
    public Set<String> resolveRoles(TokenProfile profile) {
        return profile.getRoles();
    }

    /**
     * Resolve user permissions.
     */
    public Set<String> resolvePermissions(TokenProfile profile) {
        return profile.getPermissions();
    }

    public boolean checkUserRoles(Set<String> requireRoles, Set<String> provideRoles, boolean requireAll) {
        return checkAccessRequirements(requireRoles, provideRoles, requireAll);
    }

    public boolean checkUserPermissions(Set<String> requirePermissions, Set<String> providePermissions, boolean requireAll) {
        return checkAccessRequirements(requirePermissions, providePermissions, requireAll);
    }

    protected boolean checkAccessRequirements(Set<String> requires, Set<String> provides, boolean requireAll) {
        if (CollectionUtils.isEmpty(requires) || CollectionUtils.isEmpty(provides)) {
            return false;
        }
        
        if (requireAll) {
            for (String require : requires) {
                if (!provides.contains(require)) {
                    return false;
                }
            }
            return true;
        } else {
            for (String require : requires) {
                if (provides.contains(require)) {
                    return true;
                }
            }
            return false;
        }
    }
}
