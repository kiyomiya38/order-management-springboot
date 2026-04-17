# WebApp-07 ハンズオン: JavaScript入門（Webアプリ向け）

対応参考資料: `J5-07_JavaScript入門.pdf`

## 1. この資料のゴール
- JavaScriptの役割（ページ内の動き・入力補助）を説明できる
- フォーム入力チェックをJavaScriptで実装できる
- DOM操作で画面表示を動的に変更できる

---

## 2. 事前準備
- WebApp-06 まで完了
- ブラウザのデベロッパーツール（Console / Elements）が使える

---

## 3. 先に覚えるポイント
1. JavaScriptはブラウザ上で動き、サーバー通信なしで即時反応できる
2. ページ遷移やDB処理はServlet、ページ内の挙動はJavaScriptが得意
3. 変数宣言は `let` / `const` を基本にする
4. DOMを操作すると画面表示に即時反映される

---

## 4. ハンズオン

目的:
- ログイン画面に入力チェックとDOM反映を追加する

完了条件:
- 未入力送信をJavaScript側で止められる
- 入力値プレビューを画面に表示できる

作業プロジェクト: `webapp_handson07`

### Step 0: HTML作成
`WebContent/htmls/login.html`

```html
<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title>Login</title>
  <script src="../js/login-check.js" defer></script>
</head>
<body>
  <h1>ログイン</h1>
  <form id="loginForm" action="../execute-login" method="post">
    ID: <input type="text" id="loginId" name="login_id"><br>
    PW: <input type="password" id="loginPassword" name="login_password"><br>
    <button type="submit">ログイン</button>
  </form>

  <p id="preview"></p>
</body>
</html>
```

### Step 1: 外部JavaScript作成（入力チェック）
`WebContent/js/login-check.js`

```javascript
const form = document.getElementById("loginForm");
const loginId = document.getElementById("loginId");
const loginPassword = document.getElementById("loginPassword");
const preview = document.getElementById("preview");

form.addEventListener("submit", (event) => {
  const id = loginId.value.trim();
  const pw = loginPassword.value.trim();

  if (id === "" || pw === "") {
    alert("IDとパスワードは必須です。");
    event.preventDefault();
  }
});
```

### Step 2: DOM操作で入力値プレビューを表示
`login-check.js` に追記:

```javascript
loginId.addEventListener("input", () => {
  const id = loginId.value.trim();
  preview.textContent = id === "" ? "" : `入力中のID: ${id}`;
});
```

### Step 3: ブラウザで確認（仕上げ）
アクセス:

```text
http://localhost:8080/webapp_handson07/htmls/login.html
```

確認ポイント:
- 未入力で送信するとアラートが出て送信されない
- ID入力に合わせてプレビューが更新される
- ConsoleにJavaScriptエラーが出ていない

---

## 5. ミニ演習（10分）
1. パスワード8文字未満なら送信を止めるルールを追加する。
2. エラーメッセージを `alert` ではなく画面内表示に変更する。
3. `const` / `let` の使い分け理由を説明する。

---

## 6. つまずきポイント
- JavaScriptが読み込まれない
  -> `<script src=... defer>` のパスと配置先を確認
- `null` 参照エラーが出る
  -> `id` 名の不一致やDOM読み込みタイミングを確認
- クライアント側チェックだけで安心してしまう
  -> サーバー側（Servlet）でも必ず検証する

