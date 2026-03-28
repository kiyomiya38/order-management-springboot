import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;

class UserController extends BaseController {
    private final UserService userService;

    UserController(AuthService authService, SessionStore sessionStore, UserService userService) {
        super(authService, sessionStore);
        this.userService = userService;
    }

    void list(HttpExchange exchange) throws IOException {
        User admin = requireAdmin(exchange);
        if (admin == null) {
            return;
        }
        FlashData flash = consumeFlash(exchange);
        List<User> users = userService.list();
        StringBuilder rows = new StringBuilder();
        if (users.isEmpty()) {
            rows.append("<tr><td colspan=\"4\" class=\"muted\">ユーザーがいません。</td></tr>");
        } else {
            for (User u : users) {
                rows.append("<tr>")
                        .append("<td>").append(u.getId()).append("</td>")
                        .append("<td>").append(WebUtil.escapeHtml(u.getUsername())).append("</td>")
                        .append("<td>").append(WebUtil.escapeHtml(u.getRole())).append("</td>")
                        .append("<td>")
                        .append("<a href=\"/users/").append(u.getId()).append("/edit\">編集</a>")
                        .append("<form method=\"post\" action=\"/users/").append(u.getId()).append("/delete\" style=\"display:inline\">")
                        .append("<button type=\"submit\" class=\"danger\">削除</button>")
                        .append("</form>")
                        .append("</td>")
                        .append("</tr>");
            }
        }
        String html = """
                <!doctype html>
                <html lang="ja">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>アカウント管理</title>
                  <link rel="stylesheet" href="/styles.css" />
                </head>
                <body>
                  <div class="container">
                    <header>
                      <h1>アカウント管理</h1>
                      <div class="row">
                        <a href="/">トップへ戻る</a>
                        <a href="/users/new">新規作成</a>
                      </div>
                    </header>
                    %s
                    %s
                    <section class="panel">
                      <table>
                        <thead>
                          <tr><th>ID</th><th>ユーザー名</th><th>ロール</th><th>操作</th></tr>
                        </thead>
                        <tbody>%s</tbody>
                      </table>
                    </section>
                  </div>
                </body>
                </html>
                """.formatted(
                flash.getError() == null || flash.getError().isBlank() ? "" : "<div class=\"alert alert-error\">" + WebUtil.escapeHtml(flash.getError()) + "</div>",
                flash.getMessage() == null || flash.getMessage().isBlank() ? "" : "<div class=\"alert alert-info\">" + WebUtil.escapeHtml(flash.getMessage()) + "</div>",
                rows.toString());
        WebUtil.sendHtml(exchange, 200, html);
    }

    void newForm(HttpExchange exchange) throws IOException {
        User admin = requireAdmin(exchange);
        if (admin == null) {
            return;
        }
        UserForm form = new UserForm();
        form.setRole("ROLE_USER");
        renderForm(exchange, "create", null, form, List.of());
    }

    void create(HttpExchange exchange) throws IOException {
        User admin = requireAdmin(exchange);
        if (admin == null) {
            return;
        }
        UserForm form = toForm(WebUtil.parseUrlEncoded(WebUtil.readBody(exchange)));
        Validation validation = validateForm(form, true);
        if (validation.hasErrors()) {
            renderForm(exchange, "create", null, form, validation.errors());
            return;
        }
        try {
            userService.create(form.getUsername(), form.getPassword(), form.getRole());
            putFlash(exchange, "ユーザーを作成しました", null);
            WebUtil.redirect(exchange, "/users");
        } catch (BusinessException ex) {
            renderForm(exchange, "create", null, form, List.of(ex.getMessage()));
        }
    }

    void editForm(HttpExchange exchange, Long id) throws IOException {
        User admin = requireAdmin(exchange);
        if (admin == null) {
            return;
        }
        User user = userService.get(id);
        UserForm form = new UserForm();
        form.setUsername(user.getUsername());
        form.setRole(user.getRole());
        form.setPassword("");
        renderForm(exchange, "edit", id, form, List.of());
    }

    void update(HttpExchange exchange, Long id) throws IOException {
        User admin = requireAdmin(exchange);
        if (admin == null) {
            return;
        }
        UserForm form = toForm(WebUtil.parseUrlEncoded(WebUtil.readBody(exchange)));
        Validation validation = validateForm(form, false);
        if (validation.hasErrors()) {
            renderForm(exchange, "edit", id, form, validation.errors());
            return;
        }
        try {
            userService.update(id, form.getUsername(), form.getPassword(), form.getRole());
            putFlash(exchange, "ユーザーを更新しました", null);
            WebUtil.redirect(exchange, "/users");
        } catch (BusinessException ex) {
            renderForm(exchange, "edit", id, form, List.of(ex.getMessage()));
        }
    }

    void delete(HttpExchange exchange, Long id) throws IOException {
        User admin = requireAdmin(exchange);
        if (admin == null) {
            return;
        }
        userService.delete(id);
        putFlash(exchange, "ユーザーを削除しました", null);
        WebUtil.redirect(exchange, "/users");
    }

    private UserForm toForm(Map<String, String> raw) {
        UserForm form = new UserForm();
        form.setUsername(raw.getOrDefault("username", "").trim());
        form.setPassword(raw.getOrDefault("password", ""));
        form.setRole(raw.getOrDefault("role", "").trim());
        return form;
    }

    private Validation validateForm(UserForm form, boolean create) {
        Validation validation = new Validation();
        if (form.getUsername() == null || form.getUsername().isBlank()) {
            validation.add("must not be blank");
        }
        if (form.getRole() == null || form.getRole().isBlank()) {
            validation.add("must not be blank");
        }
        if (create && (form.getPassword() == null || form.getPassword().isBlank())) {
            validation.add("パスワードは必須です");
        }
        return validation;
    }

    private void renderForm(HttpExchange exchange,
                            String mode,
                            Long id,
                            UserForm form,
                            List<String> errors) throws IOException {
        String formAction = "create".equals(mode) ? "/users" : "/users/" + id;
        String title = "create".equals(mode) ? "ユーザー作成" : "ユーザー編集";
        String button = "create".equals(mode) ? "作成" : "更新";
        String userSelected = "ROLE_USER".equals(form.getRole()) ? "selected" : "";
        String adminSelected = "ROLE_ADMIN".equals(form.getRole()) ? "selected" : "";
        String errorBlock = "";
        if (!errors.isEmpty()) {
            StringBuilder lis = new StringBuilder();
            for (String err : errors) {
                lis.append("<li>").append(WebUtil.escapeHtml(err)).append("</li>");
            }
            errorBlock = "<div class=\"alert alert-error\"><ul>" + lis + "</ul></div>";
        }
        String html = """
                <!doctype html>
                <html lang="ja">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>ユーザー編集</title>
                  <link rel="stylesheet" href="/styles.css" />
                </head>
                <body>
                  <div class="container">
                    <header>
                      <h1>%s</h1>
                      <a href="/users">一覧へ戻る</a>
                    </header>
                    <section class="panel">
                      <form action="%s" method="post">
                        <div class="row">
                          <label>ユーザー名
                            <input type="text" name="username" value="%s" />
                          </label>
                          <label>パスワード
                            <input type="password" name="password" value="%s" placeholder="変更しない場合は空欄" />
                          </label>
                          <label>ロール
                            <select name="role">
                              <option value="ROLE_USER" %s>ROLE_USER</option>
                              <option value="ROLE_ADMIN" %s>ROLE_ADMIN</option>
                            </select>
                          </label>
                        </div>
                        %s
                        <button type="submit">%s</button>
                      </form>
                    </section>
                  </div>
                </body>
                </html>
                """.formatted(
                title,
                formAction,
                WebUtil.escapeHtml(form.getUsername()),
                WebUtil.escapeHtml(form.getPassword()),
                userSelected,
                adminSelected,
                errorBlock,
                button
        );
        WebUtil.sendHtml(exchange, 200, html);
    }
}
