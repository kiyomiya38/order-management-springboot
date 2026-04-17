# WebApp-04 ハンズオン: WebアプリからのDB操作

対応参考資料: `J5-04_WebアプリからのDB操作.pdf`

## 1. この資料のゴール
- ServletからDAO/BusinessLogicを呼び出す流れを説明できる
- フォーム入力をDBへ登録する処理を実装できる
- `sendRedirect` による遷移を実装できる

---

## 2. 事前準備
- WebApp-03 まで完了
- `sample-webapp-schema.sql` 実行済み

```sql
SOURCE ~/order-management-springboot/docs/curriculum/java/webapp-handson/sample-webapp-schema.sql;
```

補足:
- `mysql-connector-j-8.x.x.jar` をプロジェクトの `WEB-INF/lib` に配置

---

## 3. 先に覚えるポイント
1. Servletは「流れの制御」、DAOは「DBアクセス」を担当
2. SQL実行は `PreparedStatement` を使う
3. 成功/失敗で遷移先を分けると画面制御が明確になる
4. `sendRedirect` はクライアントに再アクセス指示を返す

---

## 4. ハンズオン

目的:
- 入力フォーム -> DB登録 -> 完了画面遷移の流れを作る

完了条件:
- 登録成功時に完了ページへ遷移し、DBに1件追加される

作業プロジェクト: `webapp_handson04`

### Step 0: Dynamic Web Project作成
Eclipseで以下を作成:
- プロジェクト名: `webapp_handson04`
- パッケージ: `work`

### Step 1: DAO作成
`src/work/SurveyDao.java`

```java
package work;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class SurveyDao {
    private static final String URL =
        "jdbc:mysql://localhost:3306/test_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";
    private static final String USER = "test_user";
    private static final String PASS = "test_pass";

    public int insert(String userName, int satisfactionLevel, String commentText) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String sql = "INSERT INTO survey_response (user_name, satisfaction_level, comment_text) VALUES (?, ?, ?)";

        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.setInt(2, satisfactionLevel);
            ps.setString(3, commentText);
            return ps.executeUpdate();
        }
    }
}
```

### Step 2: ビジネスロジック作成
`src/work/SurveyService.java`

```java
package work;

public class SurveyService {
    private final SurveyDao dao = new SurveyDao();

    public boolean saveSurvey(String userName, int satisfactionLevel, String commentText) throws Exception {
        int updated = dao.insert(userName, satisfactionLevel, commentText);
        return updated == 1;
    }
}
```

### Step 3: Servlet作成（リクエスト受信 -> DB登録 -> リダイレクト）
`src/work/SaveSurveyServlet.java`

```java
package work;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SaveSurveyServlet extends HttpServlet {
    private final SurveyService service = new SurveyService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String userName = request.getParameter("user_name");
        String levelRaw = request.getParameter("satisfaction_level");
        String comment = request.getParameter("comment_text");

        try {
            int level = Integer.parseInt(levelRaw);
            boolean ok = service.saveSurvey(userName, level, comment);
            if (ok) {
                response.sendRedirect("htmls/finish.html");
            } else {
                response.sendRedirect("htmls/error.html");
            }
        } catch (Exception e) {
            response.sendRedirect("htmls/error.html");
        }
    }
}
```

### Step 4: 入力フォームと完了画面を作成
`WebContent/htmls/input-survey.html`

```html
<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title>アンケート入力</title>
</head>
<body>
  <h1>アンケート入力</h1>
  <form action="../save-survey" method="post">
    名前: <input type="text" name="user_name"><br>
    満足度(1-5): <input type="number" name="satisfaction_level" min="1" max="5"><br>
    コメント: <input type="text" name="comment_text"><br>
    <button type="submit">送信</button>
  </form>
</body>
</html>
```

`WebContent/htmls/finish.html`

```html
<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title>完了</title>
</head>
<body>
  <h1>登録完了</h1>
  <a href="input-survey.html">戻る</a>
</body>
</html>
```

`WebContent/htmls/error.html`

```html
<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title>エラー</title>
</head>
<body>
  <h1>登録失敗</h1>
  <a href="input-survey.html">入力画面へ戻る</a>
</body>
</html>
```

### Step 5: `web.xml` 設定と確認（仕上げ）
`WebContent/WEB-INF/web.xml` に `save-survey` をマッピング:

```xml
<servlet>
  <servlet-name>SaveSurveyServlet</servlet-name>
  <servlet-class>work.SaveSurveyServlet</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>SaveSurveyServlet</servlet-name>
  <url-pattern>/save-survey</url-pattern>
</servlet-mapping>
```

アクセス:

```text
http://localhost:8080/webapp_handson04/htmls/input-survey.html
```

---

## 5. ミニ演習（10分）
1. 登録前に `satisfaction_level` が1〜5かをチェックする。
2. 登録後、一覧画面Servletに `sendRedirect` するよう変更する。
3. 失敗時に例外内容をサーバーログへ出力する。

---

## 6. つまずきポイント
- DB接続エラー
  -> URL / ユーザー / パスワード / Connector/J配置を確認
- `NumberFormatException`
  -> 数値項目の未入力や文字入力を考慮する
- リダイレクト先の404
  -> 相対パス（`htmls/...`）と配置先を確認

