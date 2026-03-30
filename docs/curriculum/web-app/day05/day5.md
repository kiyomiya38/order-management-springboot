# Day5: JSP + JDBCでToDoアプリを作る（DB永続化）

## ロードマップ接続
- 対象: `java-web-1week-beginner-blueprint.md` の Day5
- このDayで行うこと: Day4のServletアプリを「JSP表示 + DB保存」に進化させる
- 到達点: `追加 / 一覧 / 完了切替 / 削除` がDB連携で動くWebアプリを完成させる

## Day4とのつながり
- Day4: Servlet内でHTML文字列を組み立て
- Day5: Servletは処理担当、JSPは表示担当に分離（MVCの入口）
- Day4: メモリ保存
- Day5: H2へ永続化（アプリ再起動後もデータが残る）

---

## 1. 作業フォルダ

```bash
cd ~/order-management-springboot
mkdir -p ~/order-management-springboot/practice/day05-jsp-jdbc-todo
cd ~/order-management-springboot/practice/day05-jsp-jdbc-todo
```

---

## 2. Day5で使うファイル（自分で新規作成）

- `pom.xml`
  - Maven設定（Servlet/JSP/JDBC/H2/Jetty）
- `src/main/java/com/shinesoft/day5/model/Todo.java`
  - 1件分のToDoデータ
- `src/main/java/com/shinesoft/day5/repository/TodoDao.java`
  - JDBCでDBアクセス
- `src/main/java/com/shinesoft/day5/web/TodoServlet.java`
  - 入力受付、DAO呼び出し、JSPへforward
- `src/main/webapp/WEB-INF/web.xml`
  - URLマッピング
- `src/main/webapp/WEB-INF/jsp/todos.jsp`
  - 画面表示

重要:
- `pom.xml` は **プロジェクト直下** に置く（`src/main` に置かない）

---

## 3. Step1: Mavenプロジェクトを作る

### このStepで学ぶ文法
- `pom.xml` の依存設定
- JSP実行に必要なライブラリ追加
- H2 JDBCドライバ追加

### 3-1. ディレクトリ作成

```bash
mkdir -p \
  src/main/java/com/shinesoft/day5/model \
  src/main/java/com/shinesoft/day5/repository \
  src/main/java/com/shinesoft/day5/web \
  src/main/webapp/WEB-INF/jsp
```

### 3-2. `pom.xml` を作成
作成ファイル:
- `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.shinesoft</groupId>
  <artifactId>day05-jsp-jdbc-todo</artifactId>
  <version>1.0.0</version>
  <packaging>war</packaging>

  <properties>
    <maven.compiler.release>17</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <jetty.version>11.0.20</jetty.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>jakarta.servlet</groupId>
      <artifactId>jakarta.servlet-api</artifactId>
      <version>5.0.0</version>
      <scope>provided</scope>
    </dependency>

    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <version>2.2.224</version>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.13.0</version>
        <configuration>
          <release>${maven.compiler.release}</release>
          <encoding>${project.build.sourceEncoding}</encoding>
        </configuration>
      </plugin>
      <plugin>
        <groupId>org.eclipse.jetty</groupId>
        <artifactId>jetty-maven-plugin</artifactId>
        <version>${jetty.version}</version>
        <dependencies>
          <dependency>
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>apache-jsp</artifactId>
            <version>${jetty.version}</version>
          </dependency>
        </dependencies>
      </plugin>
    </plugins>
  </build>
</project>
```

---

## 4. Step2: モデルとDAOを作る（JDBC）

### このStepで学ぶ文法
- JDBC基本セット（`Connection`, `PreparedStatement`, `ResultSet`）
- DAOパターン
- SQL実行（`CREATE TABLE`, `INSERT`, `SELECT`, `UPDATE`, `DELETE`）

### 4-1. `Todo.java` を作成
作成ファイル:
- `src/main/java/com/shinesoft/day5/model/Todo.java`

```java
package com.shinesoft.day5.model;

import java.time.LocalDateTime;

public class Todo {
    private final int id;
    private final String title;
    private final boolean done;
    private final LocalDateTime createdAt;

    public Todo(int id, String title, boolean done, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.done = done;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
```

### 4-2. `TodoDao.java` を作成
作成ファイル:
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

    public List<Todo> findAll() {
        String sql = "SELECT id, title, done, created_at FROM todos ORDER BY id DESC";
        List<Todo> result = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                boolean done = rs.getBoolean("done");
                Timestamp createdAt = rs.getTimestamp("created_at");
                result.add(new Todo(id, title, done, createdAt.toLocalDateTime()));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("ToDo一覧取得に失敗しました", e);
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

## 5. Step3: Servletを作る（処理担当）

### このStepで学ぶ文法
- `RequestDispatcher` でJSPへforward
- `doGet` / `doPost` で責務分離
- `action` パラメータで処理分岐

### 5-1. `TodoServlet.java` を作成
作成ファイル:
- `src/main/java/com/shinesoft/day5/web/TodoServlet.java`

```java
package com.shinesoft.day5.web;

import com.shinesoft.day5.repository.TodoDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class TodoServlet extends HttpServlet {
    private final TodoDao todoDao = new TodoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("todos", todoDao.findAll());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/todos.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");

        String action = safeTrim(request.getParameter("action"));
        if ("add".equals(action)) {
            String title = safeTrim(request.getParameter("title"));
            if (!title.isEmpty()) {
                todoDao.add(title);
            }
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

        response.sendRedirect(request.getContextPath() + "/todos");
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

## 6. Step4: web.xmlを作る（URLマッピング）

### このStepで学ぶ文法
- `servlet` / `servlet-mapping`
- `/` と `/todos` のルーティング

### 6-1. `web.xml` を作成
作成ファイル:
- `src/main/webapp/WEB-INF/web.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"
         version="5.0">

  <servlet>
    <servlet-name>todoServlet</servlet-name>
    <servlet-class>com.shinesoft.day5.web.TodoServlet</servlet-class>
  </servlet>

  <servlet-mapping>
    <servlet-name>todoServlet</servlet-name>
    <url-pattern>/</url-pattern>
    <url-pattern>/todos</url-pattern>
  </servlet-mapping>
</web-app>
```

---

## 7. Step5: JSPを作る（表示担当）

### このStepで学ぶ文法
- JSPの基本 (`<%@ page ... %>`)
- request属性の受け取り
- Javaのfor-eachで一覧表示（JSP内）

### 7-1. `todos.jsp` を作成
作成ファイル:
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
%>
<!doctype html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Day5 ToDo</title>
  <style>
    body { margin: 0; font-family: "Yu Gothic UI", sans-serif; background: #f5f5f5; }
    .container { max-width: 860px; margin: 0 auto; padding: 24px; }
    .panel { background: #fff; border: 1px solid #ddd; border-radius: 10px; padding: 16px; margin-bottom: 12px; }
    .row { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 10px; }
    input[type=text] { flex: 1 1 260px; border: 1px solid #ccc; border-radius: 8px; padding: 8px; }
    button { border: none; border-radius: 8px; padding: 8px 12px; color: #fff; cursor: pointer; }
    .add { background: #0f766e; }
    .toggle { background: #2563eb; }
    .delete { background: #dc2626; }
    table { width: 100%; border-collapse: collapse; }
    th, td { border-bottom: 1px solid #ddd; text-align: left; padding: 8px; vertical-align: middle; }
    .done { text-decoration: line-through; color: #6b7280; }
    form.inline { display: inline; margin-right: 6px; }
  </style>
</head>
<body>
<main class="container">
  <section class="panel">
    <h1>やりたいことリスト（Day5）</h1>
    <p>Servlet + JSP + JDBC + H2</p>
    <form method="post" action="<%= ctx %>/todos">
      <input type="hidden" name="action" value="add" />
      <div class="row">
        <input type="text" name="title" maxlength="100" placeholder="例: JDBCの復習をする" />
        <button type="submit" class="add">追加</button>
      </div>
    </form>
  </section>

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
          <td colspan="5">まだToDoがありません。</td>
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
                <button type="submit" class="toggle">完了切替</button>
              </form>
              <form class="inline" method="post" action="<%= ctx %>/todos">
                <input type="hidden" name="action" value="delete" />
                <input type="hidden" name="id" value="<%= todo.getId() %>" />
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

## 8. 実行して動作確認

```bash
cd ~/order-management-springboot/practice/day05-jsp-jdbc-todo
mvn jetty:run
```

ブラウザ確認:
- `http://localhost:8080/todos`
- 追加、完了切替、削除が動く
- アプリ再起動後もデータが残る

補足:
- 初回起動時に `data/day5db.mv.db` が自動作成される

---

## 9. Day5コードの説明（入力・処理・出力）

1. 入力
- フォームから `action`, `title`, `id`

2. 処理
- `TodoServlet#doPost` で actionを判定
- `TodoDao` が JDBCでSQL実行
- その後 `sendRedirect` で `/todos` へ戻す

3. 出力
- `doGet` で `todos` をrequestへ詰める
- `todos.jsp` が一覧HTMLを生成して表示

---

## 10. よくあるエラー

- `No plugin found for prefix 'jetty'`
  - `pom.xml` がプロジェクト直下にあるか確認
  - `mvn jetty:run` を同じ階層で実行しているか確認

- `Table "TODOS" not found`
  - `TodoDao` のコンストラクタで `initTable()` を呼んでいるか確認

- 文字化けする
  - `request.setCharacterEncoding("UTF-8")` の呼び出しを確認
  - JSP先頭の `contentType`/`pageEncoding` を確認

- 追加ボタンを押しても反映されない
  - `<input type="hidden" name="action" value="add">` の記述漏れを確認

---

## 11. 最終確認（Day5完了条件）

- ServletとJSPの責務を分けて説明できる
- DAO経由でH2へ `INSERT/SELECT/UPDATE/DELETE` できる
- `PreparedStatement` を使っている理由を説明できる
- 再起動後もデータが残ることを確認できる

## 12. Day6への引き継ぎ

- Day6では、バリデーションや検索・並び替えを追加し、CRUDを実務寄りに整える
