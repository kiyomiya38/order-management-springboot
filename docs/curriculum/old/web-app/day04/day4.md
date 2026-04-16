# Day4: Servletで応援メッセージ投稿アプリを作る（GET/POST）

## ロードマップ接続
- 対象: `java-web-1week-beginner-blueprint.md` の Day4
- このDayで行うこと: Javaロジック（Day3）をWeb化し、ブラウザから投稿できるようにする
- 到達点: Servletで `投稿 -> 一覧表示` が動くアプリを完成させる

## Day3とのつながり
- Day3の `QuestService` の考え方を、Day4では `MessageStore` として再利用する
- Day3のコンソール入出力を、Day4では `HttpServlet#doGet/doPost` に置き換える

---

## 1. 作業フォルダ

```bash
cd ~/order-management-springboot
mkdir -p ~/order-management-springboot/practice/day04-servlet-messageboard
cd ~/order-management-springboot/practice/day04-servlet-messageboard
```

---

## 2. Day4で使うファイル（自分で新規作成）

- `pom.xml`
  - Maven設定（Servlet API、Jetty実行）
- `src/main/java/com/shinesoft/day4/model/Message.java`
  - 1件分の投稿データ
- `src/main/java/com/shinesoft/day4/repository/MessageStore.java`
  - メモリ上で投稿を保持
- `src/main/java/com/shinesoft/day4/web/MessageServlet.java`
  - GET/POST処理と画面HTML生成
- `src/main/webapp/WEB-INF/web.xml`
  - ServletのURLマッピング

---

## 3. Step1: Mavenプロジェクトを作る

### このStepで学ぶ文法
- `pom.xml`
- 依存関係 (`dependency`)
- Mavenプラグイン (`jetty-maven-plugin`)

### 3-1. ディレクトリ作成

```bash
mkdir -p \
  src/main/java/com/shinesoft/day4/model \
  src/main/java/com/shinesoft/day4/repository \
  src/main/java/com/shinesoft/day4/web \
  src/main/webapp/WEB-INF
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
  <artifactId>day04-servlet-messageboard</artifactId>
  <version>1.0.0</version>
  <packaging>war</packaging>

  <properties>
    <maven.compiler.release>17</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <dependencies>
    <dependency>
      <groupId>jakarta.servlet</groupId>
      <artifactId>jakarta.servlet-api</artifactId>
      <version>6.0.0</version>
      <scope>provided</scope>
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
        <version>11.0.20</version>
      </plugin>
    </plugins>
  </build>
</project>
```

---

## 4. Step2: モデルと保存クラスを作る

### このStepで学ぶ文法
- Javaクラス分割
- `List` と `ArrayList`
- `List.copyOf(...)`

### 4-1. `Message.java` を作成
作成ファイル:
- `src/main/java/com/shinesoft/day4/model/Message.java`

```java
package com.shinesoft.day4.model;

import java.time.LocalDateTime;

public class Message {
    private final int id;
    private final String author;
    private final String body;
    private final LocalDateTime postedAt;

    public Message(int id, String author, String body, LocalDateTime postedAt) {
        this.id = id;
        this.author = author;
        this.body = body;
        this.postedAt = postedAt;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }
}
```

### 4-2. `MessageStore.java` を作成
作成ファイル:
- `src/main/java/com/shinesoft/day4/repository/MessageStore.java`

```java
package com.shinesoft.day4.repository;

import com.shinesoft.day4.model.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageStore {
    private final List<Message> messages = new ArrayList<>();
    private int nextId = 1;

    public void add(String author, String body) {
        messages.add(0, new Message(nextId, author, body, LocalDateTime.now()));
        nextId++;
    }

    public List<Message> findAll() {
        return List.copyOf(messages);
    }
}
```

---

## 5. Step3: Servletを作る（GET/POST）

### このStepで学ぶ文法
- `HttpServlet`
- `doGet` / `doPost`
- `request.getParameter(...)`
- `response.sendRedirect(...)`

### 5-1. `MessageServlet.java` を作成
作成ファイル:
- `src/main/java/com/shinesoft/day4/web/MessageServlet.java`

```java
package com.shinesoft.day4.web;

import com.shinesoft.day4.model.Message;
import com.shinesoft.day4.repository.MessageStore;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessageServlet extends HttpServlet {
    private static final MessageStore STORE = new MessageStore();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");

        String error = request.getParameter("error");
        String html = buildPageHtml(request.getContextPath(), error, STORE.findAll());
        response.getWriter().write(html);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");

        String author = safeTrim(request.getParameter("author"));
        String body = safeTrim(request.getParameter("body"));

        if (author.isEmpty() || body.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/messages?error=1");
            return;
        }

        STORE.add(author, body);
        response.sendRedirect(request.getContextPath() + "/messages");
    }

    private String buildPageHtml(String contextPath, String errorFlag, List<Message> messages) {
        StringBuilder rows = new StringBuilder();
        for (Message message : messages) {
            rows.append("""
                    <tr>
                      <td>%d</td>
                      <td>%s</td>
                      <td>%s</td>
                      <td>%s</td>
                    </tr>
                    """.formatted(
                    message.getId(),
                    escapeHtml(message.getAuthor()),
                    escapeHtml(message.getBody()),
                    message.getPostedAt().format(FORMATTER)
            ));
        }

        if (rows.length() == 0) {
            rows.append("""
                    <tr>
                      <td colspan="4">まだ投稿がありません。</td>
                    </tr>
                    """);
        }

        String errorHtml = "1".equals(errorFlag)
                ? "<p class=\"error\">名前とメッセージの両方を入力してください。</p>"
                : "";

        return """
                <!doctype html>
                <html lang="ja">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>Day4 メッセージ投稿</title>
                  <style>
                    body { font-family: "Yu Gothic UI", sans-serif; margin: 0; background: #f5f5f5; }
                    .container { max-width: 860px; margin: 0 auto; padding: 24px; }
                    .panel { background: #fff; border: 1px solid #ddd; border-radius: 10px; padding: 16px; margin-bottom: 12px; }
                    .row { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 8px; }
                    input, textarea { width: 100%%; border: 1px solid #ccc; border-radius: 8px; padding: 8px; }
                    .half { flex: 1 1 240px; }
                    button { border: none; border-radius: 8px; padding: 10px 12px; background: #0f766e; color: #fff; cursor: pointer; }
                    table { width: 100%%; border-collapse: collapse; }
                    th, td { border-bottom: 1px solid #ddd; text-align: left; padding: 8px; }
                    .error { color: #b91c1c; font-weight: 600; }
                  </style>
                </head>
                <body>
                  <main class="container">
                    <section class="panel">
                      <h1>応援メッセージ投稿</h1>
                      <p>Day4: ServletのGET/POSTを体験する</p>
                      %s
                      <form method="post" action="%s/messages">
                        <div class="row">
                          <div class="half">
                            <label>名前</label>
                            <input type="text" name="author" maxlength="30" />
                          </div>
                        </div>
                        <div class="row">
                          <label>メッセージ</label>
                          <textarea name="body" rows="3" maxlength="120"></textarea>
                        </div>
                        <button type="submit">投稿する</button>
                      </form>
                    </section>

                    <section class="panel">
                      <h2>投稿一覧</h2>
                      <table>
                        <thead>
                          <tr>
                            <th>ID</th>
                            <th>名前</th>
                            <th>メッセージ</th>
                            <th>投稿時刻</th>
                          </tr>
                        </thead>
                        <tbody>
                          %s
                        </tbody>
                      </table>
                    </section>
                  </main>
                </body>
                </html>
                """.formatted(errorHtml, contextPath, rows.toString());
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
```

---

## 6. Step4: URLマッピングを作る

### このStepで学ぶ文法
- `web.xml`
- Servletマッピング（`/` と `/messages`）

### 6-1. `web.xml` を作成
作成ファイル:
- `src/main/webapp/WEB-INF/web.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">

  <servlet>
    <servlet-name>messageServlet</servlet-name>
    <servlet-class>com.shinesoft.day4.web.MessageServlet</servlet-class>
  </servlet>

  <servlet-mapping>
    <servlet-name>messageServlet</servlet-name>
    <url-pattern>/</url-pattern>
    <url-pattern>/messages</url-pattern>
  </servlet-mapping>
</web-app>
```

---

## 7. 実行して動作確認

```bash
cd ~/order-management-springboot/practice/day04-servlet-messageboard
mvn jetty:run
```

ブラウザ確認:
- `http://localhost:8080/messages`
- 名前とメッセージを入力して投稿
- 投稿後に一覧へ表示される

補足:
- 初回の `mvn jetty:run` は依存ダウンロードで時間がかかる

---

## 8. Day4コードの説明（入力・処理・出力）

1. 入力
- フォームの `author`, `body`（POST）

2. 処理
- `doPost` でバリデーション
- `MessageStore.add(...)` でメモリに保存
- `sendRedirect` で再読み込み（PRGパターン）
- `doGet` で一覧をHTML化

3. 出力
- 投稿フォーム + 投稿一覧HTML

---

## 9. よくあるエラー

- `mvn: command not found`
  - MavenインストールとPATH設定を確認

- `Address already in use: bind`
  - 8080番を使っている別プロセスを停止して再実行

- `404 Not Found`
  - `web.xml` の `servlet-class` と実際のpackage/class名が一致しているか確認

- 投稿しても一覧が増えない
  - `doPost` の末尾で `STORE.add(author, body);` を呼んでいるか確認

---

## 10. 最終確認（Day4完了条件）

- Mavenプロジェクトを自分で作成できた
- GETでフォーム表示、POSTで投稿追加が動いた
- 投稿後に一覧へ反映される
- `doGet` と `doPost` の役割を説明できる

## 11. Day5への引き継ぎ

- Day5では、HTML生成をServlet直書きからJSPへ分離し、JDBCでDB永続化へ進む
