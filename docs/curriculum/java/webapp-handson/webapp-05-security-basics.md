# WebApp-05 ハンズオン: セキュリティ対策基礎（XSS / CSRF）

対応参考資料: `J5-05_セキュリティ対策基礎ver9.0.pdf`

## 1. この資料のゴール
- Webセキュリティの基本観点（機密性/完全性/可用性）を説明できる
- XSS対策として出力時エスケープを実装できる
- CSRF対策としてトークン照合を実装できる

---

## 2. 事前準備
- WebApp-04 まで完了
- `webapp_handson04` の入力画面/登録処理が動作する

---

## 3. 先に覚えるポイント
1. セキュリティは「機密性」「完全性」「可用性」の3観点で考える
2. XSS対策は「入力時ではなく表示時」にエスケープする
3. CSRF対策は「サーバーが発行したトークン」を検証する
4. 代表的な脅威は複数あるが、まずXSS/CSRFを確実に押さえる

---

## 4. ハンズオン

目的:
- 既存アンケート機能にXSS/CSRF対策を追加する

完了条件:
- スクリプト文字列が実行されず安全に表示される
- トークンなし送信が拒否される

作業プロジェクト: `webapp_handson05`（`webapp_handson04` をコピーして開始してよい）

### Step 0: XSS攻撃文字列を確認
入力例:

```text
<script>alert('xss')</script>
```

この文字列をそのまま出力するとブラウザで実行される可能性がある。

### Step 1: 出力時エスケープ用ユーティリティを作成
`src/work/HtmlEscaper.java`

```java
package work;

public final class HtmlEscaper {
    private HtmlEscaper() {
    }

    public static String escape(String input) {
        if (input == null) {
            return "";
        }
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
}
```

### Step 2: 表示処理でエスケープを適用
例: 確認画面Servletや一覧画面Servletの出力箇所

```java
String safeName = HtmlEscaper.escape(userName);
String safeComment = HtmlEscaper.escape(commentText);
out.println("<p>名前: " + safeName + "</p>");
out.println("<p>コメント: " + safeComment + "</p>");
```

ポイント:
- DB保存前に無理に加工しない
- 画面へ表示する直前にエスケープする

### Step 3: CSRFトークンをフォームへ埋め込む
フォーム表示Servlet（`doGet`）側:

```java
String token = java.util.UUID.randomUUID().toString();
request.getSession().setAttribute("csrfToken", token);

out.println("<form action='../save-survey' method='post'>");
out.println("<input type='hidden' name='csrf_token' value='" + token + "'>");
out.println("名前: <input type='text' name='user_name'><br>");
out.println("満足度(1-5): <input type='number' name='satisfaction_level'><br>");
out.println("コメント: <input type='text' name='comment_text'><br>");
out.println("<button type='submit'>送信</button>");
out.println("</form>");
```

### Step 4: 受信側でトークン照合（仕上げ）
保存Servlet（`doPost`）側:

```java
String requestToken = request.getParameter("csrf_token");
String sessionToken = (String) request.getSession().getAttribute("csrfToken");

if (sessionToken == null || !sessionToken.equals(requestToken)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "不正なリクエストです");
    return;
}
request.getSession().removeAttribute("csrfToken");
```

---

## 5. ミニ演習（10分）
1. 一覧表示画面の全出力項目にエスケープを適用する。
2. CSRFトークンを使い回せないよう、照合後に必ず破棄する。
3. 未ログイン時に更新系Servletへアクセスした場合、ログイン画面へ飛ばす。

---

## 6. つまずきポイント
- 入力時にエスケープしてしまう
  -> データ本来の値が失われるため、出力時エスケープを徹底
- トークン比較で常に失敗する
  -> hidden項目名と `getParameter` のキー名を一致させる
- CSRF対策したのに直接URLで更新できる
  -> 更新処理はPOST限定にし、セッション認可も併用する

