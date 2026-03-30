# Day6: ToDoアプリを実務寄りに拡張する（検索・編集・バリデーション）

## ロードマップ接続
- 対象: `java-web-1week-beginner-blueprint.md` の Day6
- このDayで行うこと: Day5のCRUDに、検索・並び替え・編集・入力バリデーションを追加する
- 到達点: 「壊れにくく、使いやすいCRUD」を実装し、MVC/DAOの役割を定着させる

## Day5とのつながり
- Day5: 追加 / 一覧 / 完了切替 / 削除
- Day6: それに「検索」「並び替え」「編集」「入力チェック」を追加
- Day7最終課題で必要な実装の土台になる

---

## 1. 作業フォルダ

Day5を残したまま進めるため、フォルダをコピーしてDay6用を作る。

```bash
cd ~/order-management-springboot/practice
cp -r day05-jsp-jdbc-todo day06-crud-plus
cd day06-crud-plus
rm -rf target
```

---

## 2. Day6で変更するファイル

- `src/main/java/com/shinesoft/day5/repository/TodoDao.java`
  - 検索・並び替え・編集用メソッド追加
- `src/main/java/com/shinesoft/day5/web/TodoServlet.java`
  - クエリ受け取り、バリデーション、編集処理追加
- `src/main/webapp/WEB-INF/jsp/todos.jsp`
  - 検索フォーム、編集フォーム、エラーメッセージ追加

※ package名は Day5 のまま進めてOK（研修では一貫性を優先）

---

## 3. Step1: DAOを拡張する（検索・編集）

### このStepで学ぶ文法
- 動的SQL（条件付きWHERE）
- `PreparedStatement` で安全な検索
- `findById` / `updateTitle` の追加

### 3-1. `TodoDao.java` を置き換え
置き換えファイル:
- `src/main/java/com/shinesoft/day5/repository/TodoDao.java`

```java
package com.shinesoft.day5.repository;

import com.shinesoft.day5.model.Todo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TodoDao {
    private static final String JDBC_URL = "jdbc:h2:./data/day5db;MODE=PostgreSQL";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    public TodoDao() {
        initTable();
    }

    public List<Todo> findByCriteria(String keyword, String status, String sort) {
        StringBuilder sql = new StringBuilder("""
                SELECT id, title, done, created_at
                FROM todos
                WHERE 1 = 1
                """);

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND LOWER(title) LIKE ? ");
            params.add("%" + keyword.toLowerCase() + "%");
        }

        if ("active".equals(status)) {
            sql.append(" AND done = FALSE ");
        } else if ("done".equals(status)) {
            sql.append(" AND done = TRUE ");
        }

        if ("old".equals(sort)) {
            sql.append(" ORDER BY id ASC ");
        } else if ("title".equals(sort)) {
            sql.append(" ORDER BY title ASC, id DESC ");
        } else {
            sql.append(" ORDER BY id DESC ");
        }

        List<Todo> result = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    boolean done = rs.getBoolean("done");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    result.add(new Todo(id, title, done, createdAt.toLocalDateTime()));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("ToDo検索に失敗しました", e);
        }
    }

    public Todo findById(int id) {
        String sql = "SELECT id, title, done, created_at FROM todos WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Todo(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getBoolean("done"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("ToDo詳細取得に失敗しました", e);
        }
    }

    public void add(String title) {
        String sql = "INSERT INTO todos(title, done, created_at) VALUES (?, FALSE, CURRENT_TIMESTAMP)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ToDo追加に失敗しました", e);
        }
    }

    public void updateTitle(int id, String title) {
        String sql = "UPDATE todos SET title = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ToDo編集に失敗しました", e);
        }
    }

    public void toggleDone(int id) {
        String sql = "UPDATE todos SET done = CASE WHEN done THEN FALSE ELSE TRUE END WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ToDo更新に失敗しました", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM todos WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ToDo削除に失敗しました", e);
        }
    }

    private void initTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS todos (
                  id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                  title VARCHAR(100) NOT NULL,
                  done BOOLEAN NOT NULL,
                  created_at TIMESTAMP NOT NULL
                )
                """;
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("テーブル初期化に失敗しました", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}
```

---

## 4. Step2: Servletを拡張する（バリデーション・編集）

### このStepで学ぶ文法
- `doGet` でクエリ受け取り
- `doPost` の action分岐拡張
- バリデーション（文字数と空文字）

### 4-1. `TodoServlet.java` を置き換え
置き換えファイル:
- `src/main/java/com/shinesoft/day5/web/TodoServlet.java`

```java
package com.shinesoft.day5.web;

import com.shinesoft.day5.model.Todo;
import com.shinesoft.day5.repository.TodoDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TodoServlet extends HttpServlet {
    private final TodoDao todoDao = new TodoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String q = safeTrim(request.getParameter("q"));
        String status = safeTrim(request.getParameter("status"));
        String sort = safeTrim(request.getParameter("sort"));
        String error = safeTrim(request.getParameter("error"));
        String editingId = safeTrim(request.getParameter("editId"));

        request.setAttribute("todos", todoDao.findByCriteria(q, status, sort));
        request.setAttribute("q", q);
        request.setAttribute("status", status);
        request.setAttribute("sort", sort);
        request.setAttribute("error", error);

        if (!editingId.isEmpty()) {
            int id = parseIntOrMinusOne(editingId);
            if (id > 0) {
                Todo editingTodo = todoDao.findById(id);
                request.setAttribute("editingTodo", editingTodo);
            }
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/todos.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");

        String action = safeTrim(request.getParameter("action"));
        String q = safeTrim(request.getParameter("q"));
        String status = safeTrim(request.getParameter("status"));
        String sort = safeTrim(request.getParameter("sort"));

        if ("add".equals(action)) {
            String title = safeTrim(request.getParameter("title"));
            String error = validateTitle(title);
            if (!error.isEmpty()) {
                redirectWithError(response, request, q, status, sort, error);
                return;
            }
            todoDao.add(title);
        } else if ("update".equals(action)) {
            int id = parseIntOrMinusOne(request.getParameter("id"));
            String title = safeTrim(request.getParameter("title"));
            String error = validateTitle(title);
            if (id <= 0 || !error.isEmpty()) {
                redirectWithError(response, request, q, status, sort, "更新データが不正です");
                return;
            }
            todoDao.updateTitle(id, title);
        } else if ("toggle".equals(action)) {
            int id = parseIntOrMinusOne(request.getParameter("id"));
            if (id > 0) {
                todoDao.toggleDone(id);
            }
        } else if ("delete".equals(action)) {
            int id = parseIntOrMinusOne(request.getParameter("id"));
            if (id > 0) {
                todoDao.delete(id);
            }
        }

        response.sendRedirect(request.getContextPath() + "/todos" + buildQuery(q, status, sort));
    }

    private String validateTitle(String title) {
        if (title.isEmpty()) {
            return "タイトルは必須です";
        }
        if (title.length() > 100) {
            return "タイトルは100文字以内で入力してください";
        }
        return "";
    }

    private void redirectWithError(HttpServletResponse response, HttpServletRequest request,
                                   String q, String status, String sort, String error) throws IOException {
        String encoded = URLEncoder.encode(error, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/todos" + buildQuery(q, status, sort) + "&error=" + encoded);
    }

    private String buildQuery(String q, String status, String sort) {
        StringBuilder sb = new StringBuilder("?");
        sb.append("q=").append(URLEncoder.encode(q == null ? "" : q, StandardCharsets.UTF_8));
        sb.append("&status=").append(URLEncoder.encode(status == null ? "" : status, StandardCharsets.UTF_8));
        sb.append("&sort=").append(URLEncoder.encode(sort == null ? "" : sort, StandardCharsets.UTF_8));
        return sb.toString();
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private int parseIntOrMinusOne(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
```

---

## 5. Step3: JSPを拡張する（検索・並び替え・編集フォーム）

### このStepで学ぶ文法
- request属性を使ったフォーム値保持
- 条件分岐で編集フォーム表示
- 一覧操作で検索条件を引き継ぐhidden項目

### 5-1. `todos.jsp` を置き換え
置き換えファイル:
- `src/main/webapp/WEB-INF/jsp/todos.jsp`

```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.shinesoft.day5.model.Todo" %>
<%
  List<Todo> todos = (List<Todo>) request.getAttribute("todos");
  if (todos == null) {
    todos = java.util.Collections.emptyList();
  }
  String ctx = request.getContextPath();
  String q = String.valueOf(request.getAttribute("q") == null ? "" : request.getAttribute("q"));
  String status = String.valueOf(request.getAttribute("status") == null ? "" : request.getAttribute("status"));
  String sort = String.valueOf(request.getAttribute("sort") == null ? "" : request.getAttribute("sort"));
  String error = String.valueOf(request.getAttribute("error") == null ? "" : request.getAttribute("error"));
  Todo editingTodo = (Todo) request.getAttribute("editingTodo");
%>
<!doctype html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Day6 ToDo+</title>
  <style>
    body { margin: 0; font-family: "Yu Gothic UI", sans-serif; background: #f5f5f5; }
    .container { max-width: 980px; margin: 0 auto; padding: 24px; }
    .panel { background: #fff; border: 1px solid #ddd; border-radius: 10px; padding: 16px; margin-bottom: 12px; }
    .row { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 10px; align-items: center; }
    input[type=text], select { border: 1px solid #ccc; border-radius: 8px; padding: 8px; }
    .grow { flex: 1 1 280px; }
    button, .link-btn {
      border: none; border-radius: 8px; padding: 8px 12px; color: #fff; cursor: pointer; text-decoration: none;
      display: inline-block; font-size: 14px;
    }
    .add { background: #0f766e; }
    .toggle { background: #2563eb; }
    .delete { background: #dc2626; }
    .search { background: #334155; }
    .edit { background: #7c3aed; }
    .save { background: #0f766e; }
    table { width: 100%; border-collapse: collapse; }
    th, td { border-bottom: 1px solid #ddd; text-align: left; padding: 8px; vertical-align: middle; }
    .done { text-decoration: line-through; color: #6b7280; }
    form.inline { display: inline; margin-right: 6px; }
    .error { color: #b91c1c; font-weight: 600; margin-top: 0; }
  </style>
</head>
<body>
<main class="container">
  <section class="panel">
    <h1>やりたいことリスト（Day6）</h1>
    <p>検索・並び替え・編集・バリデーション対応版</p>

    <% if (!error.isBlank()) { %>
      <p class="error"><%= error %></p>
    <% } %>

    <form method="get" action="<%= ctx %>/todos">
      <div class="row">
        <input class="grow" type="text" name="q" value="<%= q %>" placeholder="タイトル検索（部分一致）" />
        <select name="status">
          <option value="" <%= "".equals(status) ? "selected" : "" %>>すべて</option>
          <option value="active" <%= "active".equals(status) ? "selected" : "" %>>未完了</option>
          <option value="done" <%= "done".equals(status) ? "selected" : "" %>>完了</option>
        </select>
        <select name="sort">
          <option value="" <%= "".equals(sort) ? "selected" : "" %>>新しい順</option>
          <option value="old" <%= "old".equals(sort) ? "selected" : "" %>>古い順</option>
          <option value="title" <%= "title".equals(sort) ? "selected" : "" %>>タイトル順</option>
        </select>
        <button type="submit" class="search">絞り込み</button>
      </div>
    </form>

    <form method="post" action="<%= ctx %>/todos">
      <input type="hidden" name="action" value="add" />
      <input type="hidden" name="q" value="<%= q %>" />
      <input type="hidden" name="status" value="<%= status %>" />
      <input type="hidden" name="sort" value="<%= sort %>" />
      <div class="row">
        <input class="grow" type="text" name="title" maxlength="100" placeholder="新しいToDoを入力（100文字以内）" />
        <button type="submit" class="add">追加</button>
      </div>
    </form>
  </section>

  <% if (editingTodo != null) { %>
  <section class="panel">
    <h2>ToDo編集（ID: <%= editingTodo.getId() %>）</h2>
    <form method="post" action="<%= ctx %>/todos">
      <input type="hidden" name="action" value="update" />
      <input type="hidden" name="id" value="<%= editingTodo.getId() %>" />
      <input type="hidden" name="q" value="<%= q %>" />
      <input type="hidden" name="status" value="<%= status %>" />
      <input type="hidden" name="sort" value="<%= sort %>" />
      <div class="row">
        <input class="grow" type="text" name="title" maxlength="100" value="<%= editingTodo.getTitle() %>" />
        <button type="submit" class="save">保存</button>
        <a class="link-btn search" href="<%= ctx %>/todos?q=<%= java.net.URLEncoder.encode(q, "UTF-8") %>&status=<%= status %>&sort=<%= sort %>">キャンセル</a>
      </div>
    </form>
  </section>
  <% } %>

  <section class="panel">
    <h2>一覧</h2>
    <table>
      <thead>
      <tr>
        <th>ID</th>
        <th>タイトル</th>
        <th>状態</th>
        <th>作成時刻</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <% if (todos.isEmpty()) { %>
        <tr>
          <td colspan="5">条件に一致するToDoがありません。</td>
        </tr>
      <% } else { %>
        <% for (Todo todo : todos) { %>
          <tr>
            <td><%= todo.getId() %></td>
            <td class="<%= todo.isDone() ? "done" : "" %>"><%= todo.getTitle() %></td>
            <td><%= todo.isDone() ? "完了" : "未完了" %></td>
            <td><%= todo.getCreatedAt() %></td>
            <td>
              <form class="inline" method="post" action="<%= ctx %>/todos">
                <input type="hidden" name="action" value="toggle" />
                <input type="hidden" name="id" value="<%= todo.getId() %>" />
                <input type="hidden" name="q" value="<%= q %>" />
                <input type="hidden" name="status" value="<%= status %>" />
                <input type="hidden" name="sort" value="<%= sort %>" />
                <button type="submit" class="toggle">完了切替</button>
              </form>

              <a class="link-btn edit"
                 href="<%= ctx %>/todos?editId=<%= todo.getId() %>&q=<%= java.net.URLEncoder.encode(q, "UTF-8") %>&status=<%= status %>&sort=<%= sort %>">編集</a>

              <form class="inline" method="post" action="<%= ctx %>/todos">
                <input type="hidden" name="action" value="delete" />
                <input type="hidden" name="id" value="<%= todo.getId() %>" />
                <input type="hidden" name="q" value="<%= q %>" />
                <input type="hidden" name="status" value="<%= status %>" />
                <input type="hidden" name="sort" value="<%= sort %>" />
                <button type="submit" class="delete">削除</button>
              </form>
            </td>
          </tr>
        <% } %>
      <% } %>
      </tbody>
    </table>
  </section>
</main>
</body>
</html>
```

---

## 6. 実行して動作確認

```bash
cd ~/order-management-springboot/practice/day06-crud-plus
mvn jetty:run
```

確認項目:
1. タイトル空で追加するとエラー表示される
2. `q` で部分一致検索できる
3. `status` で未完了/完了を絞り込める
4. `sort` で並び順が変わる
5. 編集ボタンからタイトル更新できる
6. 完了切替・削除後も検索条件が維持される

---

## 7. Day6コードの説明（入力・処理・出力）

1. 入力
- GETクエリ: `q`, `status`, `sort`, `editId`
- POSTフォーム: `action`, `id`, `title`

2. 処理
- Servletが入力チェックを実施
- DAOが検索条件に応じたSQLを発行
- 編集/削除/完了切替をDBへ反映

3. 出力
- JSPが検索フォーム・編集フォーム・一覧を表示
- 条件に応じて表示内容が変わる

---

## 8. よくあるエラー

- `java.net.URLEncoder` が見つからない（JSP）
  - `java.net.URLEncoder.encode(..., "UTF-8")` の綴りを確認

- 検索条件が操作後に消える
  - hiddenの `q`, `status`, `sort` をフォームに入れているか確認

- 編集ボタンを押しても編集フォームが出ない
  - `doGet` で `editId` を読み取り、`editingTodo` をrequestへ詰めているか確認

- SQLエラー（ORDER BY）
  - 並び順は固定文字列で分岐し、ユーザー入力を直接SQL連結しない

---

## 9. 最終確認（Day6完了条件）

- バリデーション、検索、並び替え、編集を実装できた
- Servletは処理、JSPは表示、DAOはDBアクセスと説明できる
- Day7の最終プロジェクトに使えるCRUD基盤ができた

## 10. Day7への引き継ぎ

- Day7では、このDay6コードをベースにテーマ別最終プロジェクトへ発展させる

