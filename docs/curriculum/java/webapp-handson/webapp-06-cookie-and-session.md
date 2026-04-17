# WebApp-06 ハンズオン: Cookieとセッション

対応参考資料: `J5-06_Cookieとセッション.pdf`

## 1. この資料のゴール
- CookieとSessionの役割の違いを説明できる
- セッションでログイン状態を保持できる
- ログイン必須画面への未認証アクセスを制御できる

---

## 2. 事前準備
- WebApp-04 まで完了
- `sample-webapp-schema.sql` の `user_info` テーブル作成済み

---

## 3. 先に覚えるポイント
1. Cookieはブラウザ側保存、Sessionはサーバー側保存
2. ログイン管理はSession中心で実装する
3. 画面表示前に「ログイン済みか」を必ず判定する
4. ログアウト時は `session.invalidate()` で情報を破棄する

---

## 4. ハンズオン

目的:
- ログイン/ホーム/ログアウトの最小機能を作る

完了条件:
- ログイン後のみホーム画面が見られる
- ログアウト後はホーム画面が見られない

作業プロジェクト: `webapp_handson06`

### Step 0: ログイン画面Servlet作成
`src/work/LoginServlet.java`

```java
package work;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html><body>");
        out.println("<h1>ログイン</h1>");
        out.println("<form action='execute-login' method='post'>");
        out.println("ID: <input type='text' name='login_id'><br>");
        out.println("PW: <input type='password' name='login_password'><br>");
        out.println("<button type='submit'>ログイン</button>");
        out.println("</form>");
        out.println("</body></html>");
    }
}
```

### Step 1: 認証Servlet作成（Session保存）
`src/work/ExecuteLoginServlet.java`

```java
package work;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ExecuteLoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String loginId = request.getParameter("login_id");
        String password = request.getParameter("login_password");

        // 学習用の最小実装（本番ではDB照合+ハッシュ化必須）
        boolean success = "moco".equals(loginId) && "pass123".equals(password);

        if (!success) {
            response.sendRedirect("login");
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("loginUserName", "MOCO");

        Cookie lastLoginId = new Cookie("last_login_id", loginId);
        lastLoginId.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(lastLoginId);

        response.sendRedirect("home");
    }
}
```

### Step 2: ログイン必須のホーム画面Servlet作成
`src/work/HomeServlet.java`

```java
package work;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUserName") == null) {
            response.sendRedirect("login");
            return;
        }

        String userName = (String) session.getAttribute("loginUserName");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html><body>");
        out.println("<h1>ホーム</h1>");
        out.println("<p>ようこそ " + userName + " さん</p>");
        out.println("<a href='execute-logout'>ログアウト</a>");
        out.println("</body></html>");
    }
}
```

### Step 3: ログアウトServlet作成
`src/work/ExecuteLogoutServlet.java`

```java
package work;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ExecuteLogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect("login");
    }
}
```

### Step 4: `web.xml` マッピングと確認（仕上げ）
`WebContent/WEB-INF/web.xml` に以下を設定:
- `/login`
- `/execute-login`
- `/home`
- `/execute-logout`

確認:
1. `login` からログイン成功で `home` へ遷移
2. 未ログインで `home` へ直接アクセスすると `login` へ戻る
3. ログアウト後に `home` へ直接アクセスすると `login` へ戻る

---

## 5. ミニ演習（10分）
1. `ExecuteLoginServlet` の認証を `user_info` テーブル照合に差し替える。
2. `login` 画面でCookie `last_login_id` を読み、ID欄に初期表示する。
3. セッションにログイン時刻を入れてホーム画面に表示する。

---

## 6. つまずきポイント
- セッション取得時に毎回新規生成してしまう
  -> 判定時は `getSession(false)` を使う
- ログアウト後も画面が見える
  -> `invalidate()` 呼び出し漏れを確認
- Cookieに機密情報を入れてしまう
  -> パスワードや認可情報はCookieへ保存しない

