# WebApp-02 ハンズオン: Java Servlet基礎とWebページ表示

対応参考資料: `J5-02_Java Servletの基本とWebページの表示.pdf`

## 1. この資料のゴール
- Servletの役割（リクエスト処理とレスポンス生成）を説明できる
- `doGet` / `doPost` の違いを説明できる
- `web.xml` でURLとServletをマッピングできる

---

## 2. 事前準備
- WebApp-01 まで完了
- Eclipse（Dynamic Web Project が作成できる）
- Tomcat（Servlet実行環境）

注意:
- Tomcat 9 系は `javax.servlet.*`
- Tomcat 10 系は `jakarta.servlet.*`
- 本資料のコードは `javax.servlet.*` で記載

---

## 3. 先に覚えるポイント
1. Servletは `HttpServlet` を継承して作る
2. ブラウザからURLアクセス時は基本 `doGet` が呼ばれる
3. フォーム送信（`method="post"`）は `doPost` が呼ばれる
4. `web.xml` の `servlet-mapping` でURLパターンを決める

---

## 4. ハンズオン

目的:
- 最小のServletでHTMLレスポンスを返す

完了条件:
- URLアクセスでServletが動き、HTMLが表示される

作業プロジェクト: `webapp_handson02`

### Step 0: Dynamic Web Project作成
Eclipseで以下を作成:
- プロジェクト名: `webapp_handson02`
- パッケージ: `work`

### Step 1: Servlet作成
`src/work/HelloServlet.java`

```java
package work;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><body>");
        out.println("<h1>Hello Servlet</h1>");
        out.println("<p>doGetが実行されました。</p>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><body>");
        out.println("<h1>Hello Servlet</h1>");
        out.println("<p>doPostが実行されました。</p>");
        out.println("</body></html>");
    }
}
```

### Step 2: `web.xml` にマッピング設定
`WebContent/WEB-INF/web.xml`

```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

  <servlet>
    <servlet-name>HelloServlet</servlet-name>
    <servlet-class>work.HelloServlet</servlet-class>
  </servlet>

  <servlet-mapping>
    <servlet-name>HelloServlet</servlet-name>
    <url-pattern>/hello</url-pattern>
  </servlet-mapping>
</web-app>
```

### Step 3: ブラウザで動作確認
Tomcat起動後にアクセス:

```text
http://localhost:8080/webapp_handson02/hello
```

期待結果:
- `doGetが実行されました。` が表示される

### Step 4: POSTの起動確認（仕上げ）
`WebContent/hello-form.html` を作成:

```html
<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title>Hello Form</title>
</head>
<body>
  <form action="hello" method="post">
    <button type="submit">POSTで送信</button>
  </form>
</body>
</html>
```

アクセス:

```text
http://localhost:8080/webapp_handson02/hello-form.html
```

---

## 5. ミニ演習（10分）
1. `doGet` 側に現在時刻を表示する。
2. `doPost` 側に `request.getMethod()` の結果を表示する。
3. URLパターンを `/hello` から `/self-introduction` に変更して確認する。

---

## 6. つまずきポイント
- 404エラーになる
  -> コンテキスト名 / URLパターン / サーバー起動状態を確認
- `ClassNotFoundException` が出る
  -> `servlet-class` のパッケージ名を含めて正しく記述する
- `javax` / `jakarta` の不一致エラー
  -> Tomcatのバージョンに合わせてimportを揃える

