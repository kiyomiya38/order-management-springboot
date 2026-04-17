# WebApp-03 ハンズオン: 画面遷移と引数の受け渡し

対応参考資料: `J5-03_Webページの遷移と引数の受け渡し.pdf`

## 1. この資料のゴール
- URLの構造（プロトコル / ホスト / コンテキスト / パターン）を説明できる
- フォームから送信した値を `request.getParameter` で取得できる
- 画面遷移でのGET/POSTの違いを説明できる

---

## 2. 事前準備
- WebApp-02 まで完了
- Dynamic Web Project と Tomcat が利用可能

---

## 3. 先に覚えるポイント
1. URLはアクセス先プログラムを特定する住所
2. フォーム送信値はリクエストパラメータとして受け取る
3. GETはURLに値が見える、POSTはリクエストボディで送る
4. 画面遷移では「どのServletを呼ぶか」を明示する

---

## 4. ハンズオン

目的:
- 2画面構成でフォーム入力値を受け渡す

完了条件:
- 入力画面 -> 確認画面の遷移で値を表示できる

作業プロジェクト: `webapp_handson03`

### Step 0: Dynamic Web Project作成
Eclipseで以下を作成:
- プロジェクト名: `webapp_handson03`
- パッケージ: `work`

### Step 1: 入力画面Servletを作成
`src/work/ProfileInputServlet.java`

```java
package work;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProfileInputServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><body>");
        out.println("<h1>自己紹介入力</h1>");
        out.println("<form action='profile-confirm' method='post'>");
        out.println("名前: <input type='text' name='name'><br>");
        out.println("コメント: <input type='text' name='comment'><br>");
        out.println("<button type='submit'>送信</button>");
        out.println("</form>");
        out.println("</body></html>");
    }
}
```

### Step 2: 確認画面Servletを作成
`src/work/ProfileConfirmServlet.java`

```java
package work;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProfileConfirmServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("name");
        String comment = request.getParameter("comment");

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><body>");
        out.println("<h1>確認画面</h1>");
        out.println("<p>名前: " + name + "</p>");
        out.println("<p>コメント: " + comment + "</p>");
        out.println("<a href='profile-input'>戻る</a>");
        out.println("</body></html>");
    }
}
```

### Step 3: `web.xml` を設定
`WebContent/WEB-INF/web.xml`

```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

  <servlet>
    <servlet-name>ProfileInputServlet</servlet-name>
    <servlet-class>work.ProfileInputServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>ProfileInputServlet</servlet-name>
    <url-pattern>/profile-input</url-pattern>
  </servlet-mapping>

  <servlet>
    <servlet-name>ProfileConfirmServlet</servlet-name>
    <servlet-class>work.ProfileConfirmServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>ProfileConfirmServlet</servlet-name>
    <url-pattern>/profile-confirm</url-pattern>
  </servlet-mapping>
</web-app>
```

### Step 4: 動作確認（仕上げ）
アクセス:

```text
http://localhost:8080/webapp_handson03/profile-input
```

確認ポイント:
- 送信後に確認画面へ遷移する
- 入力値が確認画面に表示される

---

## 5. ミニ演習（10分）
1. 入力項目に `age` を追加して確認画面にも表示する。
2. `method="get"` に変更し、URL上のパラメータ表示を確認する。
3. 未入力時に「未入力です」と表示する分岐を追加する。

---

## 6. つまずきポイント
- 日本語が文字化けする
  -> `request.setCharacterEncoding("UTF-8")` をPOST受信前に呼ぶ
- `null` が表示される
  -> `name` 属性と `getParameter` のキー名が一致しているか確認
- 遷移先が見つからない
  -> `<form action='...'>` と `url-pattern` を一致させる

