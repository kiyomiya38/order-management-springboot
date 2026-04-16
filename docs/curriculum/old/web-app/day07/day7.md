# Day7: 最終プロジェクトを完成させる（クエスト管理Web）

## ロードマップ接続
- 対象: `java-web-1week-beginner-blueprint.md` の Day7
- このDayで行うこと: Day6のCRUD基盤を「最終プロジェクト」として完成させる
- 到達点: Servlet / JSP / JDBC で実用的なCRUDアプリを1本仕上げる

## 今日のテーマ
- テーマ名: クエスト管理アプリ（RPG風ToDo）
- 必須機能:
  - クエスト追加
  - 一覧表示
  - 完了切替
  - 編集
  - 削除
  - 検索 / 絞り込み / 並び替え
- 追加要素:
  - カテゴリ（学習 / 健康 / 仕事 / 趣味）
  - 進捗率表示（完了数 / 全件数）

---

## 1. 作業フォルダ

Day6の成果を残すため、コピーしてDay7用を作る。

```bash
cd ~/order-management-springboot/practice
cp -r day06-crud-plus day07-final-quest
cd day07-final-quest
rm -rf target
```

---

## 2. Day7で変更するファイル

- `src/main/java/com/shinesoft/day5/model/Todo.java`
- `src/main/java/com/shinesoft/day5/repository/TodoDao.java`
- `src/main/java/com/shinesoft/day5/web/TodoServlet.java`
- `src/main/webapp/WEB-INF/jsp/todos.jsp`

※ `pom.xml` と `web.xml` は Day6 のものをそのまま利用する

---

## 3. Step1: モデルを拡張する（カテゴリ追加）

### このStepで学ぶ文法
- フィールド追加
- コンストラクタ引数追加
- getter追加

### 3-1. `Todo.java` を置き換え
置き換えファイル:
- `src/main/java/com/shinesoft/day5/model/Todo.java`

```java
package com.shinesoft.day5.model;

import java.time.LocalDateTime;

public class Todo {
    private final int id;
    private final String title;
    private final String category;
    private final boolean done;
    private final LocalDateTime createdAt;

    public Todo(int id, String title, String category, boolean done, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.done = done;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public boolean isDone() {
        return done;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
```

---

## 4. Step2: DAOを拡張する（検索・編集・集計）

### このStepで学ぶ文法
- 動的SQL
- 複数条件検索
- `ALTER TABLE ... ADD COLUMN IF NOT EXISTS`
- 集計SQL（`COUNT(*)`）

### 4-1. `TodoDao.java` を置き換え
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
    private static final String JDBC_URL = "jdbc:h2:./data/day7db;MODE=PostgreSQL";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    public TodoDao() {
        initTable();
        ensureCategoryColumn();
    }

    public List<Todo> findByCriteria(String keyword, String status, String categoryFilter, String sort) {
        StringBuilder sql = new StringBuilder("""
                SELECT id, title, category, done, created_at
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

        if (categoryFilter != null && !categoryFilter.isBlank()) {
            sql.append(" AND category = ? ");
            params.add(categoryFilter);
        }

        if ("old".equals(sort)) {
            sql.append(" ORDER BY id ASC ");
        } else if ("title".equals(sort)) {
            sql.append(" ORDER BY title ASC, id DESC ");
        } else if ("category".equals(sort)) {
            sql.append(" ORDER BY category ASC, id DESC ");
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
                    result.add(new Todo(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("category"),
                            rs.getBoolean("done"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("ToDo検索に失敗しました", e);
        }
    }

    public Todo findById(int id) {
        String sql = "SELECT id, title, category, done, created_at FROM todos WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Todo(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getBoolean("done"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("ToDo詳細取得に失敗しました", e);
        }
    }

    public void add(String title, String category) {
        String sql = "INSERT INTO todos(title, category, done, created_at) VALUES (?, ?, FALSE, CURRENT_TIMESTAMP)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, category);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ToDo追加に失敗しました", e);
        }
    }

    public void update(int id, String title, String category) {
        String sql = "UPDATE todos SET title = ?, category = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, category);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ToDo更新に失敗しました", e);
        }
    }

    public void toggleDone(int id) {
        String sql = "UPDATE todos SET done = CASE WHEN done THEN FALSE ELSE TRUE END WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ToDo完了切替に失敗しました", e);
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

    public int countAll() {
        return countBySql("SELECT COUNT(*) FROM todos");
    }

    public int countDone() {
        return countBySql("SELECT COUNT(*) FROM todos WHERE done = TRUE");
    }

    private int countBySql(String sql) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("件数取得に失敗しました", e);
        }
    }

    private void initTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS todos (
                  id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                  title VARCHAR(100) NOT NULL,
                  category VARCHAR(30) NOT NULL DEFAULT '学習',
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

    private void ensureCategoryColumn() {
        String sql = "ALTER TABLE todos ADD COLUMN IF NOT EXISTS category VARCHAR(30) NOT NULL DEFAULT '学習'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("category列追加に失敗しました", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}
```

---

## 5. Step3: Servletを拡張する（編集とバリデーション強化）

### このStepで学ぶ文法
- `Set.of(...)` で許可カテゴリ定義
- 入力検証
- 画面状態（検索条件）の維持

### 5-1. `TodoServlet.java` を置き換え
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
import java.util.Set;

public class TodoServlet extends HttpServlet {
    private static final Set<String> ALLOWED_CATEGORIES = Set.of("学習", "健康", "仕事", "趣味");
    private final TodoDao todoDao = new TodoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String q = safeTrim(request.getParameter("q"));
        String status = safeTrim(request.getParameter("status"));
        String categoryFilter = safeTrim(request.getParameter("categoryFilter"));
        String sort = safeTrim(request.getParameter("sort"));
        String error = safeTrim(request.getParameter("error"));
        String editId = safeTrim(request.getParameter("editId"));

        request.setAttribute("todos", todoDao.findByCriteria(q, status, categoryFilter, sort));
        request.setAttribute("q", q);
        request.setAttribute("status", status);
        request.setAttribute("categoryFilter", categoryFilter);
        request.setAttribute("sort", sort);
        request.setAttribute("error", error);

        int totalCount = todoDao.countAll();
        int doneCount = todoDao.countDone();
        int progress = totalCount == 0 ? 0 : (doneCount * 100 / totalCount);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("doneCount", doneCount);
        request.setAttribute("progress", progress);

        if (!editId.isEmpty()) {
            int id = parseIntOrMinusOne(editId);
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
        String categoryFilter = safeTrim(request.getParameter("categoryFilter"));
        String sort = safeTrim(request.getParameter("sort"));

        if ("add".equals(action)) {
            String title = safeTrim(request.getParameter("title"));
            String category = safeTrim(request.getParameter("category"));
            String error = validate(title, category);
            if (!error.isEmpty()) {
                redirectWithError(response, request, q, status, categoryFilter, sort, error);
                return;
            }
            todoDao.add(title, category);
        } else if ("update".equals(action)) {
            int id = parseIntOrMinusOne(request.getParameter("id"));
            String title = safeTrim(request.getParameter("title"));
            String category = safeTrim(request.getParameter("category"));
            String error = validate(title, category);
            if (id <= 0 || !error.isEmpty()) {
                redirectWithError(response, request, q, status, categoryFilter, sort, "更新データが不正です");
                return;
            }
            todoDao.update(id, title, category);
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

        response.sendRedirect(request.getContextPath() + "/todos" + buildQuery(q, status, categoryFilter, sort));
    }

    private String validate(String title, String category) {
        if (title.isEmpty()) {
            return "タイトルは必須です";
        }
        if (title.length() > 100) {
            return "タイトルは100文字以内で入力してください";
        }
        if (!ALLOWED_CATEGORIES.contains(category)) {
            return "カテゴリを選択してください";
        }
        return "";
    }

    private void redirectWithError(HttpServletResponse response, HttpServletRequest request,
                                   String q, String status, String categoryFilter, String sort,
                                   String error) throws IOException {
        String encoded = URLEncoder.encode(error, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath()
                + "/todos"
                + buildQuery(q, status, categoryFilter, sort)
                + "&error=" + encoded);
    }

    private String buildQuery(String q, String status, String categoryFilter, String sort) {
        StringBuilder sb = new StringBuilder("?");
        sb.append("q=").append(URLEncoder.encode(q == null ? "" : q, StandardCharsets.UTF_8));
        sb.append("&status=").append(URLEncoder.encode(status == null ? "" : status, StandardCharsets.UTF_8));
        sb.append("&categoryFilter=").append(URLEncoder.encode(categoryFilter == null ? "" : categoryFilter, StandardCharsets.UTF_8));
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

## 6. Step4: JSPを仕上げる（最終プロジェクト画面）

### このStepで学ぶ文法
- 検索条件の保持
- 編集フォーム表示
- 進捗率表示

### 6-1. `todos.jsp` を置き換え
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
  String categoryFilter = String.valueOf(request.getAttribute("categoryFilter") == null ? "" : request.getAttribute("categoryFilter"));
  String sort = String.valueOf(request.getAttribute("sort") == null ? "" : request.getAttribute("sort"));
  String error = String.valueOf(request.getAttribute("error") == null ? "" : request.getAttribute("error"));
  Todo editingTodo = (Todo) request.getAttribute("editingTodo");
  int totalCount = request.getAttribute("totalCount") == null ? 0 : (Integer) request.getAttribute("totalCount");
  int doneCount = request.getAttribute("doneCount") == null ? 0 : (Integer) request.getAttribute("doneCount");
  int progress = request.getAttribute("progress") == null ? 0 : (Integer) request.getAttribute("progress");
%>
<!doctype html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Day7 Quest Manager</title>
  <style>
    body { margin: 0; font-family: "Yu Gothic UI", sans-serif; background: #f5f5f5; }
    .container { max-width: 1020px; margin: 0 auto; padding: 24px; }
    .panel { background: #fff; border: 1px solid #ddd; border-radius: 10px; padding: 16px; margin-bottom: 12px; }
    .row { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 10px; align-items: center; }
    input[type=text], select { border: 1px solid #ccc; border-radius: 8px; padding: 8px; }
    .grow { flex: 1 1 280px; }
    .stats { display: flex; gap: 12px; flex-wrap: wrap; }
    .stat { padding: 8px 12px; border-radius: 8px; background: #eef2ff; }
    .bar-wrap { width: 260px; height: 10px; border-radius: 999px; background: #e5e7eb; overflow: hidden; }
    .bar { height: 100%; background: #16a34a; }
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
    <h1>クエスト管理アプリ（Day7 最終プロジェクト）</h1>
    <p>Servlet + JSP + JDBC で作る最終版</p>
    <div class="stats">
      <div class="stat">完了 <strong><%= doneCount %></strong> / 全件 <strong><%= totalCount %></strong></div>
      <div class="stat">進捗率 <strong><%= progress %>%</strong></div>
      <div class="bar-wrap"><div class="bar" style="width:<%= progress %>%"></div></div>
    </div>
  </section>

  <section class="panel">
    <% if (!error.isBlank()) { %>
      <p class="error"><%= error %></p>
    <% } %>

    <form method="get" action="<%= ctx %>/todos">
      <div class="row">
        <input class="grow" type="text" name="q" value="<%= q %>" placeholder="タイトル検索（部分一致）" />
        <select name="status">
          <option value="" <%= "".equals(status) ? "selected" : "" %>>状態: すべて</option>
          <option value="active" <%= "active".equals(status) ? "selected" : "" %>>未完了</option>
          <option value="done" <%= "done".equals(status) ? "selected" : "" %>>完了</option>
        </select>
        <select name="categoryFilter">
          <option value="" <%= "".equals(categoryFilter) ? "selected" : "" %>>カテゴリ: すべて</option>
          <option value="学習" <%= "学習".equals(categoryFilter) ? "selected" : "" %>>学習</option>
          <option value="健康" <%= "健康".equals(categoryFilter) ? "selected" : "" %>>健康</option>
          <option value="仕事" <%= "仕事".equals(categoryFilter) ? "selected" : "" %>>仕事</option>
          <option value="趣味" <%= "趣味".equals(categoryFilter) ? "selected" : "" %>>趣味</option>
        </select>
        <select name="sort">
          <option value="" <%= "".equals(sort) ? "selected" : "" %>>新しい順</option>
          <option value="old" <%= "old".equals(sort) ? "selected" : "" %>>古い順</option>
          <option value="title" <%= "title".equals(sort) ? "selected" : "" %>>タイトル順</option>
          <option value="category" <%= "category".equals(sort) ? "selected" : "" %>>カテゴリ順</option>
        </select>
        <button type="submit" class="search">絞り込み</button>
      </div>
    </form>

    <form method="post" action="<%= ctx %>/todos">
      <input type="hidden" name="action" value="add" />
      <input type="hidden" name="q" value="<%= q %>" />
      <input type="hidden" name="status" value="<%= status %>" />
      <input type="hidden" name="categoryFilter" value="<%= categoryFilter %>" />
      <input type="hidden" name="sort" value="<%= sort %>" />
      <div class="row">
        <input class="grow" type="text" name="title" maxlength="100" placeholder="クエスト名（100文字以内）" />
        <select name="category">
          <option value="学習">学習</option>
          <option value="健康">健康</option>
          <option value="仕事">仕事</option>
          <option value="趣味">趣味</option>
        </select>
        <button type="submit" class="add">クエスト追加</button>
      </div>
    </form>
  </section>

  <% if (editingTodo != null) { %>
  <section class="panel">
    <h2>クエスト編集（ID: <%= editingTodo.getId() %>）</h2>
    <form method="post" action="<%= ctx %>/todos">
      <input type="hidden" name="action" value="update" />
      <input type="hidden" name="id" value="<%= editingTodo.getId() %>" />
      <input type="hidden" name="q" value="<%= q %>" />
      <input type="hidden" name="status" value="<%= status %>" />
      <input type="hidden" name="categoryFilter" value="<%= categoryFilter %>" />
      <input type="hidden" name="sort" value="<%= sort %>" />
      <div class="row">
        <input class="grow" type="text" name="title" maxlength="100" value="<%= editingTodo.getTitle() %>" />
        <select name="category">
          <option value="学習" <%= "学習".equals(editingTodo.getCategory()) ? "selected" : "" %>>学習</option>
          <option value="健康" <%= "健康".equals(editingTodo.getCategory()) ? "selected" : "" %>>健康</option>
          <option value="仕事" <%= "仕事".equals(editingTodo.getCategory()) ? "selected" : "" %>>仕事</option>
          <option value="趣味" <%= "趣味".equals(editingTodo.getCategory()) ? "selected" : "" %>>趣味</option>
        </select>
        <button type="submit" class="save">保存</button>
        <a class="link-btn search" href="<%= ctx %>/todos?q=<%= java.net.URLEncoder.encode(q, "UTF-8") %>&status=<%= status %>&categoryFilter=<%= java.net.URLEncoder.encode(categoryFilter, "UTF-8") %>&sort=<%= sort %>">キャンセル</a>
      </div>
    </form>
  </section>
  <% } %>

  <section class="panel">
    <h2>クエスト一覧</h2>
    <table>
      <thead>
      <tr>
        <th>ID</th>
        <th>タイトル</th>
        <th>カテゴリ</th>
        <th>状態</th>
        <th>作成時刻</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <% if (todos.isEmpty()) { %>
        <tr>
          <td colspan="6">条件に一致するクエストがありません。</td>
        </tr>
      <% } else { %>
        <% for (Todo todo : todos) { %>
          <tr>
            <td><%= todo.getId() %></td>
            <td class="<%= todo.isDone() ? "done" : "" %>"><%= todo.getTitle() %></td>
            <td><%= todo.getCategory() %></td>
            <td><%= todo.isDone() ? "完了" : "未完了" %></td>
            <td><%= todo.getCreatedAt() %></td>
            <td>
              <form class="inline" method="post" action="<%= ctx %>/todos">
                <input type="hidden" name="action" value="toggle" />
                <input type="hidden" name="id" value="<%= todo.getId() %>" />
                <input type="hidden" name="q" value="<%= q %>" />
                <input type="hidden" name="status" value="<%= status %>" />
                <input type="hidden" name="categoryFilter" value="<%= categoryFilter %>" />
                <input type="hidden" name="sort" value="<%= sort %>" />
                <button type="submit" class="toggle">完了切替</button>
              </form>

              <a class="link-btn edit"
                 href="<%= ctx %>/todos?editId=<%= todo.getId() %>&q=<%= java.net.URLEncoder.encode(q, "UTF-8") %>&status=<%= status %>&categoryFilter=<%= java.net.URLEncoder.encode(categoryFilter, "UTF-8") %>&sort=<%= sort %>">編集</a>

              <form class="inline" method="post" action="<%= ctx %>/todos">
                <input type="hidden" name="action" value="delete" />
                <input type="hidden" name="id" value="<%= todo.getId() %>" />
                <input type="hidden" name="q" value="<%= q %>" />
                <input type="hidden" name="status" value="<%= status %>" />
                <input type="hidden" name="categoryFilter" value="<%= categoryFilter %>" />
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

## 7. 実行して確認

```bash
cd ~/order-management-springboot/practice/day07-final-quest
rm -f data/day7db.mv.db data/day7db.trace.db
mvn jetty:run
```

確認URL:
- `http://localhost:8080/todos`

確認項目:
1. クエスト追加時にカテゴリ選択できる
2. 検索・状態・カテゴリ・並び替えが効く
3. 編集でタイトルとカテゴリを更新できる
4. 完了切替・削除後も検索条件が維持される
5. 進捗率が表示される

---

## 8. 発表用チェック（5分）

1. 自分のアプリで「一番工夫した点」を1つ説明する
2. Servlet / JSP / DAO それぞれの役割を説明する
3. 追加したバリデーション内容を説明する
4. 今後改善したい点を1つ挙げる

---

## 9. 最終確認（Day7完了条件）

- Day6基盤を使って最終プロジェクトとして1本完成させた
- CRUD + 検索 + 編集 + バリデーションを実装できた
- MVC/DAOの分離を維持したまま機能追加できた
- Day1〜Day7の学習内容を通して説明できる

## 10. 次の学習（任意）

- Docker化（`app + db` のCompose化）
- PostgreSQLへの切り替え
- Spring Boot版への移植

