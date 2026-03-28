import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

abstract class BaseController {
    protected final AuthService authService;
    protected final SessionStore sessionStore;

    protected BaseController(AuthService authService, SessionStore sessionStore) {
        this.authService = authService;
        this.sessionStore = sessionStore;
    }

    protected User requireLogin(HttpExchange exchange) throws IOException {
        User user = authService.currentUser(exchange);
        if (user == null) {
            WebUtil.redirect(exchange, "/login");
            return null;
        }
        return user;
    }

    protected User requireAdmin(HttpExchange exchange) throws IOException {
        User user = requireLogin(exchange);
        if (user == null) {
            return null;
        }
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            WebUtil.sendText(exchange, 403, "Forbidden");
            return null;
        }
        return user;
    }

    protected String currentSid(HttpExchange exchange) {
        return WebUtil.getCookie(exchange, "sid");
    }

    protected void putFlash(HttpExchange exchange, String message, String error) {
        String sid = currentSid(exchange);
        if (sid != null) {
            sessionStore.putFlash(sid, message, error);
        }
    }

    protected FlashData consumeFlash(HttpExchange exchange) {
        String sid = currentSid(exchange);
        if (sid == null) {
            return new FlashData();
        }
        return sessionStore.consumeFlash(sid);
    }
}
