import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

class AuthController {
    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    void getLogin(HttpExchange exchange) throws IOException {
        Map<String, String> query = WebUtil.parseQuery(exchange.getRequestURI().getRawQuery());
        String error = query.containsKey("error") ? "ユーザー名またはパスワードが正しくありません" : "";
        String message = query.containsKey("logout") ? "ログアウトしました" : "";

        String html = """
                <!doctype html>
                <html lang="ja">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>ログイン</title>
                  <link rel="stylesheet" href="/styles.css" />
                </head>
                <body>
                  <div class="container">
                    <header>
                      <h1>勤怠管理システム（MVP）</h1>
                      <p class="subtitle">ログインしてください</p>
                    </header>
                    %s
                    %s
                    <section class="panel">
                      <form method="post" action="/login">
                        <div class="row">
                          <label>ユーザー名
                            <input type="text" name="username" required />
                          </label>
                          <label>パスワード
                            <input type="password" name="password" required />
                          </label>
                        </div>
                        <button type="submit">ログイン</button>
                      </form>
                      <p class="muted">初期ユーザー: admin / admin123, user1 / password</p>
                    </section>
                  </div>
                </body>
                </html>
                """.formatted(
                error.isBlank() ? "" : "<div class=\"alert alert-error\">" + WebUtil.escapeHtml(error) + "</div>",
                message.isBlank() ? "" : "<div class=\"alert alert-info\">" + WebUtil.escapeHtml(message) + "</div>");
        WebUtil.sendHtml(exchange, 200, html);
    }

    void postLogin(HttpExchange exchange) throws IOException {
        Map<String, String> form = WebUtil.parseUrlEncoded(WebUtil.readBody(exchange));
        String sid = authService.login(form.getOrDefault("username", ""), form.getOrDefault("password", ""));
        if (sid == null) {
            WebUtil.redirect(exchange, "/login?error");
            return;
        }
        WebUtil.setSessionCookie(exchange, sid);
        WebUtil.redirect(exchange, "/");
    }

    void postLogout(HttpExchange exchange) throws IOException {
        authService.logoutBySessionId(WebUtil.getCookie(exchange, "sid"));
        WebUtil.clearSessionCookie(exchange);
        WebUtil.redirect(exchange, "/login?logout");
    }
}
