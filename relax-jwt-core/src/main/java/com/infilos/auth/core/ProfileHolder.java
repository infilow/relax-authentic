package com.infilos.auth.core;

import com.infilos.auth.profile.ProfileContext;
import com.infilos.auth.profile.ProfileMode;
import com.infilos.auth.profile.context.*;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;

import static com.infilos.auth.profile.ProfileMode.*;

public class ProfileHolder {
    public static final String SYSTEM_PROPERTY = "relax.jwt.profile-mode";
    
    private static ProfileContext context;
    private static int initializeCount = 0;
    private static String contextMode = System.getProperty(SYSTEM_PROPERTY);

    static {
        initialize();
    }

    public static void clearProfile() {
        context.clearProfile();
    }

    public static TokenProfile getProfile() {
        return context.getProfile();
    }

    public static void setProfile(TokenProfile profile) {
        context.setProfile(profile);
    }

    public static void switchProfileMode(String modeName) {
        ProfileHolder.contextMode = modeName;
        initialize();
    }

    public static ProfileContext getProfileContext() {
        return context;
    }

    public static int getInitiateCount() {
        return initializeCount;
    }

    private static void initialize() {
        ProfileMode mode = ProfileMode.of(contextMode);
        if (REQUEST == mode) {
            context = new RequestAttributeProfileContext();
        } else if (THREADLOCAL == mode) {
            context = new ThreadLocalProfileContext();
        } else if (INHERIT_THREADLOCAL == mode) {
            context = new InheritableThreadLocalProfileContext();
        } else {
            // Try to load a custom context
            try {
                Class<?> clazz = Class.forName(contextMode);
                Constructor<?> customStrategy = clazz.getConstructor();
                context = (ProfileContext) customStrategy.newInstance();
            } catch (Exception ex) {
                ReflectionUtils.handleReflectionException(ex);
            }
        }

        initializeCount++;
    }
}
