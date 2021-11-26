package com.infilos.auth.intercept.context;

import com.infilos.auth.error.AuthorizeException;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.JEESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Request context utils.
 */
public class WebContext {

    public static ServletRequestAttributes getServletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    public static HttpServletRequest getHttpServletRequest() {
        return getServletRequestAttributes().getRequest();
    }

    public static HttpServletResponse getHttpServletResponse() {
        return getServletRequestAttributes().getResponse();
    }

    public static void redirectTo(JEEContext context, String url) {
        redirectTo(context, HttpConstants.FOUND, url);
    }

    /**
     * Redirect http request to another url.
     */
    public static void redirectTo(JEEContext context, int code, String url) {
        final HttpServletResponse response = context.getNativeResponse();
        response.setHeader(HttpConstants.LOCATION_HEADER, url);
        response.setStatus(code);
    }

    public static void writeResponse(JEEContext context, String content) {
        writeResponse(context, HttpConstants.OK, content);
    }

    public static void writeResponse(JEEContext context, int code, String content) {
        final HttpServletResponse response = context.getNativeResponse();
        response.setStatus(code);
        if (content != null) {
            try (OutputStream os = response.getOutputStream()) {
                os.write(content.getBytes(StandardCharsets.UTF_8));
                os.flush();
            } catch (IOException e) {
                throw new AuthorizeException(e);
            }
        }
    }

    public static JEEContext getJEEContext(boolean session) {
        ServletRequestAttributes sra = getServletRequestAttributes();
        return getJEEContext(sra.getRequest(), sra.getResponse(), session);
    }

    public static JEEContext getJEEContext(HttpServletRequest request, HttpServletResponse response, boolean session) {
        return new JEEContext(request, response, session ? JEESessionStore.INSTANCE : NoopSessionStoreInstance);
    }
    
    private static final NoopSessionStore NoopSessionStoreInstance = new NoopSessionStore();

    /**
     * No-operation session store if session disabled.
     */
    private static class NoopSessionStore implements SessionStore<JEEContext> {
        @Override
        public String getOrCreateSessionId(JEEContext context) {
            return null;
        }

        @Override
        public Optional<Object> get(JEEContext context, String key) {
            return Optional.empty();
        }

        @Override
        public void set(JEEContext context, String key, Object value) {
        }

        @Override
        public boolean destroySession(JEEContext context) {
            return true;
        }

        @Override
        public Optional<?> getTrackableSession(JEEContext context) {
            return Optional.empty();
        }

        @Override
        public Optional<SessionStore<JEEContext>> buildFromTrackableSession(JEEContext context, Object trackableSession) {
            return Optional.empty();
        }

        @Override
        public boolean renewSession(JEEContext context) {
            return false;
        }
    }
}
