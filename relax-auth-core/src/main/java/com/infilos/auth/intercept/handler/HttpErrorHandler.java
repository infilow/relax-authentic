package com.infilos.auth.intercept.handler;

import com.infilos.auth.core.AuthorityConfig;
import com.infilos.auth.error.RedirectHttpAction;
import com.infilos.auth.intercept.context.WebContext;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.*;

/**
 * Process the HttpAction error interrupted by interceptor.
 */
public interface HttpErrorHandler {

    void handle(AuthorityConfig config, JEEContext context, HttpAction action);
    
    class Default implements HttpErrorHandler {
        
        @Override
        public void handle(AuthorityConfig config, JEEContext context, HttpAction action) {
            if (config.isStateless()) {
                handleStateless(config, context, action);
            }
            handleStateful(config, context, action);
        }

        /**
         * handle stateless
         */
        protected void handleStateless(AuthorityConfig config, JEEContext context, HttpAction action) {
            if (action instanceof RedirectionAction) {
                return;
            }
            throw action;
        }

        /**
         * handle stateful
         */
        protected void handleStateful(AuthorityConfig config, JEEContext context, HttpAction action) {
            if (config.getAjaxRequestResolver().isAjax(context)) {
                handleStatefulAjax(config, context, action);
            } else {
                handleStatefulNonAjax(config, context, action);
            }
        }

        /**
         * handle stateful ajax
         */
        protected void handleStatefulAjax(AuthorityConfig config, JEEContext context, HttpAction action) {
            throw action;
        }

        /**
         * handle stateful non-ajax
         */
        protected void handleStatefulNonAjax(AuthorityConfig config, JEEContext context, HttpAction action) {
            if (action instanceof UnauthorizedAction || action instanceof ForbiddenAction ||
                action instanceof RedirectHttpAction) {
                WebContext.redirectTo(context, config.getLoginPath());
            } else if (action instanceof WithLocationAction) {
                WebContext.redirectTo(context, action.getCode(), ((WithLocationAction) action).getLocation());
            } else if (action instanceof WithContentAction) {
                WebContext.writeResponse(context, action.getCode(), ((WithContentAction) action).getContent());
            } else {
                throw action;
            }
            
        }
    }
}
