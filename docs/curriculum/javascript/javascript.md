# JavaScript 基礎

前提: `docs/curriculum/html_css/html_css.md` の内容を理解していること

## 目的（この資料でできるようになること）
- JavaScriptの基本構文（変数/条件分岐/ループ/関数）を理解し、自分で書ける
- 配列・オブジェクトの基本操作（`map` / `filter` / `find`）を使える
- DOM操作の基本（要素取得・表示更新・イベント処理）を実装できる
- フロント実装でよく使う操作（`dataset` / `confirm` / `preventDefault` / `hidden`）を体験できる
- `src` 配下アプリ作成に向けた「ブラウザ上のJavaScript基礎体力」を作る

この資料は**ブラウザのみ**で進めます。  
Node.js / npm / API通信（`fetch`）は扱いません。

---

## 0. 環境セットアップ（ブラウザのみ）
最初に環境をそろえます。すでに導入済みの場合は確認だけ実施してください。

### 0-1. 必要ツール一覧
- VS Code（編集）
- Git Bash（作業用）
- ブラウザ（Edge / Chrome など）

### 0-2. 作業フォルダ
この資料では次の作業フォルダを使います。

```bash
mkdir -p ~/order-management-springboot/practice/javascript
```

ファイルを先に作っておきます。

```bash
touch ~/order-management-springboot/practice/javascript/index.html
touch ~/order-management-springboot/practice/javascript/script.js
```

VS Codeで開く（GUI）:
1. VS Code を起動
2. `ファイル` -> `フォルダーを開く`
3. `~/order-management-springboot/practice/javascript` を選択

### 0-3. ブラウザの開発者ツール確認
JavaScriptの確認は主に**コンソール**で行います。

1. `index.html` をブラウザで開く
2. `F12`（または右クリック -> 検証）で開発者ツールを開く
3. `Console` タブを表示できることを確認

---

## 1. JavaScript 基礎（午前）

### 1-1. JavaScriptとは（初心者向け）
JavaScriptは、**ページに動きやロジックを与える言語**です。  
HTMLが構造、CSSが見た目、JavaScriptが振る舞いを担当します。

#### JavaScriptの基本ルール
- 文の終わりは `;`（省略可能だが、この資料では付ける）
- 文字列は `"..."` または `'...'` を使う
- 変数は `const`（再代入しない）または `let`（再代入する）で宣言する
- 比較は `===` / `!==` を優先する
- ブラウザで動かすときは `<script>` で読み込む

#### HTMLへの読み込み（最小例）
```html
<!doctype html>
<html lang="ja">
  <head>
    <meta charset="utf-8" />
    <title>JavaScript 基礎</title>
    <script src="./script.js" defer></script>
  </head>
  <body>
    <h1>JavaScript 練習</h1>
  </body>
</html>
```

##### 各行の意味
- `<script src="./script.js" defer></script>`: 外部JavaScriptを読み込む
- `defer`: HTMLを最後まで読み込んでからJavaScriptを実行する

#### 今日のゴール
この章では、まず「値を扱う」「判定する」「繰り返す」「関数化する」を体験します。  
次章でDOM操作（画面の要素操作）につなげます。

### 1-2. 最初のJavaScriptを書く
作業ファイル:
- `~/order-management-springboot/practice/javascript/index.html`
- `~/order-management-springboot/practice/javascript/script.js`

共通作業手順（各Step共通）:
1. コードを編集して保存
2. ブラウザを再読み込み
3. Console の表示を確認

#### Step 0: 骨組みを作る（`index.html`）
`index.html` を次の内容で作成:

```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>JavaScript 基礎</title>
  <script src="./script.js" defer></script>
</head>
<body>
  <h1>JavaScript 基礎</h1>
  <p>Consoleを開いて確認します。</p>
</body>
</html>
```

確認:
- ブラウザに見出しと文章が表示される
- エラーが出ていない

コード解説:
- `defer` を付けると、DOM未読込のタイミング実行を防ぎやすい
- `meta viewport` はスマホ表示での拡大縮小崩れを防ぐ

よくあるミス:
- `script.js` のパス誤り（`./script.js` になっていない）
- `</script>` の閉じ忘れ

#### Step 1: `console.log` で出力する（`script.js`）
`script.js` を次の内容に更新:

```javascript
console.log("JavaScript start");
```

確認:
- Consoleに `JavaScript start` が表示される

コード解説:
- `console.log(...)` はデバッグの基本。値確認の最速手段

よくあるミス:
- 保存せずに再読み込みしている
- Consoleではなく別タブ（Elements等）を見ている

#### Step 2: 変数と型を扱う
`script.js` を次の内容に更新:

```javascript
const workDate = "2026-02-05";
let count = 0;
count += 1;

const isWorking = true;
const breakMinutes = 45;

console.log("workDate:", workDate);
console.log("count:", count);
console.log("isWorking:", isWorking);
console.log("breakMinutes:", breakMinutes);

console.log("type of workDate:", typeof workDate);
console.log("type of isWorking:", typeof isWorking);
console.log("type of breakMinutes:", typeof breakMinutes);
```

確認:
- それぞれの値が表示される
- `typeof` の結果が `string` / `boolean` / `number` になる

コード解説:
- `const`: 再代入しない値に使う
- `let`: 再代入する値に使う（`count`）
- `typeof`: 値の型を確認できる

よくあるミス:
- `const` で宣言した値を再代入してエラーになる
- `=`（代入）と `===`（比較）を混同する

#### Step 3: 条件分岐（`if` / `else if` / `else`）
`script.js` を次の内容に更新:

```javascript
const status = "WORKING";

if (status === "NOT_STARTED") {
  console.log("まだ出勤していません");
} else if (status === "WORKING") {
  console.log("勤務中です");
} else if (status === "FINISHED") {
  console.log("退勤済みです");
} else {
  console.log("不明な状態です");
}
```

確認:
- Consoleに `勤務中です` が表示される

コード解説:
- `===` は「型も値も同じか」を判定する
- `else if` で分岐を段階的に追加できる

よくあるミス:
- `status = "WORKING"` と書いてしまう（代入）
- `"WORKING"` のスペルミス

#### Step 4: ループ（`for ... of` / `forEach`）
`script.js` を次の内容に更新:

```javascript
const operations = ["出勤", "休憩開始", "休憩終了", "退勤"];

for (const operation of operations) {
  console.log("[for...of]", operation);
}

operations.forEach((operation, index) => {
  console.log(`[forEach] ${index + 1}. ${operation}`);
});
```

確認:
- 2種類のループ結果が表示される

コード解説:
- `for...of`: 配列の要素を順番に取り出す
- `forEach`: コールバックで配列要素を処理する
- テンプレート文字列 `` `${...}` `` で文字列に値を埋め込める

よくあるミス:
- `for (const i in operations)` を使って値ではなく添字を扱ってしまう
- `` `${index + 1}` `` のバッククォートを `'` と取り違える

#### Step 5: 関数を作る
`script.js` を次の内容に更新:

```javascript
function formatUserLabel(user) {
  return `${user.username} (${user.role})`;
}

const user1 = { username: "tanaka", role: "ROLE_USER" };
const user2 = { username: "admin", role: "ROLE_ADMIN" };

console.log(formatUserLabel(user1));
console.log(formatUserLabel(user2));
```

確認:
- `tanaka (ROLE_USER)` などが表示される

コード解説:
- 関数化すると同じ処理を再利用できる
- `return` で関数の戻り値を返す
- オブジェクトのプロパティは `user.username` のように参照する

よくあるミス:
- `return` を書き忘れて `undefined` になる
- `username` と `userName` のように表記揺れする

#### Step 6: 配列とオブジェクトを扱う
`script.js` を次の内容に更新:

```javascript
const users = [
  { id: 1, username: "tanaka", role: "ROLE_USER" },
  { id: 2, username: "suzuki", role: "ROLE_USER" },
  { id: 3, username: "admin", role: "ROLE_ADMIN" }
];

const usernames = users.map((user) => user.username);
const admins = users.filter((user) => user.role === "ROLE_ADMIN");
const tanaka = users.find((user) => user.username === "tanaka");

console.log("usernames:", usernames);
console.log("admins:", admins);
console.log("tanaka:", tanaka);
```

確認:
- `map` / `filter` / `find` の結果が期待通り表示される

コード解説:
- `map`: 要素を別の形に変換した新配列を作る
- `filter`: 条件に合う要素だけを抽出
- `find`: 最初に一致した1件を返す（見つからないと `undefined`）

よくあるミス:
- `filter` と `find` の戻り値の違い（配列か単一要素か）を取り違える
- `===` の比較値が期待データと一致していない

#### Step 7: 文字列検索と組み合わせる
`script.js` を次の内容に更新:

```javascript
const users = [
  { id: 1, username: "tanaka", role: "ROLE_USER" },
  { id: 2, username: "suzuki", role: "ROLE_USER" },
  { id: 3, username: "sato", role: "ROLE_ADMIN" },
  { id: 4, username: "yamada", role: "ROLE_USER" }
];

const keyword = "sa";
const selectedRole = "ROLE_ADMIN";

const filtered = users.filter((user) => {
  const matchedKeyword = keyword === "" || user.username.includes(keyword);
  const matchedRole = selectedRole === "" || user.role === selectedRole;
  return matchedKeyword && matchedRole;
});

console.log("filtered:", filtered);
```

確認:
- `keyword` と `selectedRole` 条件に一致する要素だけ表示される

コード解説:
- `includes` で部分一致検索ができる
- 条件を分けて変数化すると読みやすい
- 実アプリの検索/絞り込み処理の基本パターン

よくあるミス:
- 大文字小文字を意識せず比較して一致しない
- `&&` と `||` の意味を逆に使う

#### Step 8: 午前の最終形（`script.js`）
`script.js` を次の内容にして、午前の学習を締めます。

```javascript
const users = [
  { id: 1, username: "tanaka", role: "ROLE_USER" },
  { id: 2, username: "suzuki", role: "ROLE_USER" },
  { id: 3, username: "sato", role: "ROLE_ADMIN" },
  { id: 4, username: "yamada", role: "ROLE_USER" }
];

function filterUsers(keyword, selectedRole) {
  const normalizedKeyword = keyword.trim().toLowerCase();

  return users.filter((user) => {
    const username = user.username.toLowerCase();
    const matchedKeyword = normalizedKeyword === "" || username.includes(normalizedKeyword);
    const matchedRole = selectedRole === "" || user.role === selectedRole;
    return matchedKeyword && matchedRole;
  });
}

const result = filterUsers("sa", "ROLE_ADMIN");
console.log("result:", result);
```

確認:
- 正規化（`trim` / `toLowerCase`）込みで検索できる

---

## 2. DOM / イベント基礎（午後）

### 2-1. DOMとは
DOMは、HTMLをJavaScriptから扱うための仕組みです。  
要素を取得して、テキスト変更、表示/非表示、イベント処理を実装できます。

この章では、次を実装します。
- 検索入力 + ロール選択によるテーブル絞り込み
- 一致件数表示
- 該当なし行の表示切り替え
- 削除ボタン押下時の確認ダイアログ

### 2-2. 画面の土台HTMLを作る（`index.html`）
`index.html` を次の内容に更新:

```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>JavaScript DOM演習</title>
  <style>
    body { font-family: sans-serif; margin: 24px; }
    .container { max-width: 920px; margin: 0 auto; }
    .row { display: flex; gap: 12px; align-items: end; flex-wrap: wrap; }
    label { display: flex; flex-direction: column; gap: 6px; }
    table { width: 100%; border-collapse: collapse; margin-top: 12px; }
    th, td { border-bottom: 1px solid #ddd; text-align: left; padding: 8px; }
    .muted { color: #666; }
  </style>
  <script src="./script.js" defer></script>
</head>
<body>
  <div class="container">
    <h1>ユーザー管理（JavaScript練習）</h1>

    <section class="row">
      <label>
        ユーザー名で検索
        <input id="user-search-input" type="search" placeholder="例: tanaka" autocomplete="off" />
      </label>

      <label>
        ロールで絞り込み
        <select id="role-filter-select">
          <option value="">すべて</option>
          <option value="ROLE_USER">ROLE_USER</option>
          <option value="ROLE_ADMIN">ROLE_ADMIN</option>
        </select>
      </label>

      <p id="user-filter-result" class="muted"></p>
    </section>

    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>ユーザー名</th>
          <th>ロール</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr class="js-user-row" data-username="tanaka" data-role="ROLE_USER">
          <td>1</td>
          <td>tanaka</td>
          <td>ROLE_USER</td>
          <td>
            <form class="js-delete-user-form" data-username="tanaka">
              <button type="submit">削除</button>
            </form>
          </td>
        </tr>
        <tr class="js-user-row" data-username="suzuki" data-role="ROLE_USER">
          <td>2</td>
          <td>suzuki</td>
          <td>ROLE_USER</td>
          <td>
            <form class="js-delete-user-form" data-username="suzuki">
              <button type="submit">削除</button>
            </form>
          </td>
        </tr>
        <tr class="js-user-row" data-username="admin" data-role="ROLE_ADMIN">
          <td>3</td>
          <td>admin</td>
          <td>ROLE_ADMIN</td>
          <td>
            <form class="js-delete-user-form" data-username="admin">
              <button type="submit">削除</button>
            </form>
          </td>
        </tr>
        <tr id="no-match-row" hidden>
          <td colspan="4" class="muted">条件に一致するユーザーがいません。</td>
        </tr>
      </tbody>
    </table>
  </div>
</body>
</html>
```

確認:
- 検索欄・選択欄・テーブルが表示される
- この時点では検索しても何も起きない（正常）

コード解説:
- `data-username` / `data-role` はJavaScript用のデータ置き場
- `id` は1件取得、`class` は複数取得に使う

よくあるミス:
- `id` 重複（同じ `id` を複数要素につける）
- `data-role` の値と絞り込みの比較値が一致していない

### 2-3. JavaScriptで画面を操作する（`script.js`）

#### Step 0: `DOMContentLoaded` を確認
`script.js` を次の内容に更新:

```javascript
document.addEventListener("DOMContentLoaded", () => {
  console.log("DOM読み込み完了");
});
```

確認:
- Consoleに `DOM読み込み完了` が表示される

コード解説:
- HTMLの要素取得はDOM読み込み後に行うと安全

#### Step 1: 要素取得とガード節
`script.js` を次の内容に更新:

```javascript
document.addEventListener("DOMContentLoaded", () => {
  const searchInput = document.getElementById("user-search-input");
  const roleSelect = document.getElementById("role-filter-select");
  const resultText = document.getElementById("user-filter-result");
  const noMatchRow = document.getElementById("no-match-row");
  const rows = Array.from(document.querySelectorAll("tr.js-user-row"));

  if (!(searchInput instanceof HTMLInputElement) ||
      !(roleSelect instanceof HTMLSelectElement) ||
      !(resultText instanceof HTMLElement) ||
      !(noMatchRow instanceof HTMLTableRowElement) ||
      rows.length === 0) {
    console.log("必要要素が見つからないため終了");
    return;
  }

  console.log("要素取得OK", { rowCount: rows.length });
});
```

確認:
- Consoleに `要素取得OK` と件数が表示される

コード解説:
- `instanceof` で型を確認し、安全に処理できる
- ガード節（早期 `return`）でエラー連鎖を防ぐ

よくあるミス:
- `querySelectorAll` のセレクタが間違って `rows.length === 0`
- 要素 `id` のタイプミス

#### Step 2: 絞り込み関数 `applyFilter` を作る
`script.js` を次の内容に更新:

```javascript
document.addEventListener("DOMContentLoaded", () => {
  const searchInput = document.getElementById("user-search-input");
  const roleSelect = document.getElementById("role-filter-select");
  const resultText = document.getElementById("user-filter-result");
  const noMatchRow = document.getElementById("no-match-row");
  const rows = Array.from(document.querySelectorAll("tr.js-user-row"));

  if (!(searchInput instanceof HTMLInputElement) ||
      !(roleSelect instanceof HTMLSelectElement) ||
      !(resultText instanceof HTMLElement) ||
      !(noMatchRow instanceof HTMLTableRowElement) ||
      rows.length === 0) {
    return;
  }

  const applyFilter = () => {
    const keyword = searchInput.value.trim().toLowerCase();
    const selectedRole = roleSelect.value;
    let visibleCount = 0;

    rows.forEach((row) => {
      const username = (row.dataset.username || "").toLowerCase();
      const role = row.dataset.role || "";
      const matchedKeyword = keyword === "" || username.includes(keyword);
      const matchedRole = selectedRole === "" || role === selectedRole;
      const visible = matchedKeyword && matchedRole;

      row.hidden = !visible;
      if (visible) {
        visibleCount += 1;
      }
    });

    noMatchRow.hidden = visibleCount > 0;
    resultText.textContent = `表示件数: ${visibleCount}件 / ${rows.length}件`;
  };

  applyFilter();
});
```

確認:
- 初期表示で `表示件数: 3件 / 3件` と表示される
- 入力欄/選択欄を変えても、この時点では未連動（次Stepで対応）

コード解説:
- `row.dataset.username` で `data-username` にアクセスできる
- `hidden` を `true/false` で切り替えて表示制御できる
- `.trim().toLowerCase()` で入力ゆれを吸収できる

よくあるミス:
- `row.dataSet` と書いてしまう（正しくは `dataset`）
- `rows` が `NodeList` のままで配列メソッド制限に引っかかる

#### Step 3: イベント連動を追加（`input` / `change`）
`script.js` を次の内容に更新:

```javascript
document.addEventListener("DOMContentLoaded", () => {
  const searchInput = document.getElementById("user-search-input");
  const roleSelect = document.getElementById("role-filter-select");
  const resultText = document.getElementById("user-filter-result");
  const noMatchRow = document.getElementById("no-match-row");
  const rows = Array.from(document.querySelectorAll("tr.js-user-row"));

  if (!(searchInput instanceof HTMLInputElement) ||
      !(roleSelect instanceof HTMLSelectElement) ||
      !(resultText instanceof HTMLElement) ||
      !(noMatchRow instanceof HTMLTableRowElement) ||
      rows.length === 0) {
    return;
  }

  const applyFilter = () => {
    const keyword = searchInput.value.trim().toLowerCase();
    const selectedRole = roleSelect.value;
    let visibleCount = 0;

    rows.forEach((row) => {
      const username = (row.dataset.username || "").toLowerCase();
      const role = row.dataset.role || "";
      const matchedKeyword = keyword === "" || username.includes(keyword);
      const matchedRole = selectedRole === "" || role === selectedRole;
      const visible = matchedKeyword && matchedRole;
      row.hidden = !visible;
      if (visible) {
        visibleCount += 1;
      }
    });

    noMatchRow.hidden = visibleCount > 0;
    resultText.textContent = `表示件数: ${visibleCount}件 / ${rows.length}件`;
  };

  searchInput.addEventListener("input", applyFilter);
  roleSelect.addEventListener("change", applyFilter);
  applyFilter();
});
```

確認:
- 検索欄入力で即時に絞り込みされる
- ロール選択で絞り込みされる
- 一致なしなら「条件に一致するユーザーがいません。」が表示される

コード解説:
- `input`: 文字入力のたびに発火
- `change`: 選択値が変わったタイミングで発火

#### Step 4: 削除確認ダイアログを追加（`submit` + `confirm`）
`script.js` を次の内容に更新:

```javascript
document.addEventListener("DOMContentLoaded", () => {
  setupDeleteConfirmation();
  setupUserTableFilter();
});

function setupDeleteConfirmation() {
  const deleteForms = document.querySelectorAll("form.js-delete-user-form");

  deleteForms.forEach((form) => {
    form.addEventListener("submit", (event) => {
      const username = form.dataset.username || "このユーザー";
      const accepted = window.confirm(`ユーザー「${username}」を削除します。よろしいですか？`);
      if (!accepted) {
        event.preventDefault();
      }
    });
  });
}

function setupUserTableFilter() {
  const searchInput = document.getElementById("user-search-input");
  const roleSelect = document.getElementById("role-filter-select");
  const resultText = document.getElementById("user-filter-result");
  const noMatchRow = document.getElementById("no-match-row");
  const rows = Array.from(document.querySelectorAll("tr.js-user-row"));

  if (!(searchInput instanceof HTMLInputElement) ||
      !(roleSelect instanceof HTMLSelectElement) ||
      !(resultText instanceof HTMLElement) ||
      !(noMatchRow instanceof HTMLTableRowElement) ||
      rows.length === 0) {
    return;
  }

  const applyFilter = () => {
    const keyword = searchInput.value.trim().toLowerCase();
    const selectedRole = roleSelect.value;
    let visibleCount = 0;

    rows.forEach((row) => {
      const username = (row.dataset.username || "").toLowerCase();
      const role = row.dataset.role || "";
      const matchedKeyword = keyword === "" || username.includes(keyword);
      const matchedRole = selectedRole === "" || role === selectedRole;
      const visible = matchedKeyword && matchedRole;
      row.hidden = !visible;
      if (visible) {
        visibleCount += 1;
      }
    });

    noMatchRow.hidden = visibleCount > 0;
    resultText.textContent = `表示件数: ${visibleCount}件 / ${rows.length}件`;
  };

  searchInput.addEventListener("input", applyFilter);
  roleSelect.addEventListener("change", applyFilter);
  applyFilter();
}
```

確認:
- `削除` ボタン押下で確認ダイアログが表示される
- `キャンセル` で送信が止まる（画面遷移しない）
- `OK` で送信される（今は `action` 未設定なので同ページ再読込）

コード解説:
- `submit` イベントでフォーム送信を捕捉できる
- `event.preventDefault()` で既定動作（送信）を止められる
- `dataset.username` で確認文言を動的に生成できる

よくあるミス:
- `button` が `type="button"` になっていて `submit` が発火しない
- `preventDefault()` を `accepted` 判定の外に書いて常に送信停止してしまう

#### Step 5: DOM章の最終形（`script.js`）
Step 4のコードがそのまま最終形です。  
このパターンは `src/main/resources/static/users.js` でも使える基礎構成です。

### 2-4. ブラウザで確認（最終）
1. ブラウザで `index.html` を開く
2. ユーザー名検索（例: `ta`）を試す
3. ロール絞り込み（`ROLE_ADMIN`）を試す
4. 0件になる条件を作り、`no-match-row` 表示を確認
5. `削除` ボタンで確認ダイアログを確認

---

## 3. ミニ演習A（JavaScript）
編集対象: `script.js`（DOM処理とは別に、末尾に追記して試す）

1. 次の配列を作る
   ```javascript
   const attendances = [
     { username: "tanaka", status: "WORKING", minutes: 120 },
     { username: "suzuki", status: "FINISHED", minutes: 480 },
     { username: "sato", status: "WORKING", minutes: 240 }
   ];
   ```
2. `WORKING` のユーザーだけ `filter` で抽出する
3. `minutes` 合計を計算する
4. `username` 一覧だけを `map` で作る
5. Consoleに「件数」「合計分」「名前一覧」を表示する

確認ポイント:
- `filter` / `map` / ループを組み合わせて書ける
- 配列の中のオブジェクトを安全に扱える

---

## 4. ミニ演習B（DOM）
編集対象: `index.html` / `script.js`

1. テーブル行に「勤務状態」列を追加する（`WORKING` / `FINISHED`）
2. 絞り込みUIに「勤務状態」`select` を追加する
3. JavaScript側で3条件（ユーザー名 / ロール / 状態）で絞り込みする
4. 表示件数に「現在の条件」を文字列で出す  
   例: `表示件数: 1件 / 3件（ROLE_ADMIN, WORKING）`

確認ポイント:
- 要素を追加しても `id` / `class` / `dataset` を崩さず拡張できる
- 既存関数を壊さず条件を追加できる

---

## 5. ミニ制作（30〜45分）
テーマ: **ユーザー管理ミニ画面（ローカル版）**

実装要件:
- ユーザー作成フォーム（ユーザー名 / ロール）
- 追加ボタンでテーブルに新規行を追加（ページ再読込なし）
- 検索とロール絞り込みが動作する
- 削除前に `confirm` を表示する
- 0件時メッセージを表示する

制約:
- バックエンド連携なし（配列データのみで管理）
- `fetch` は使わない

提出チェック:
- Consoleエラーが0件
- 主要操作（追加/絞り込み/削除）がすべて動作
- 関数を分けて可読性を保てている

---

## 6. つまずきやすいポイント（先に読んでおく）
- JavaScriptが動かない  
  `script` の読み込みパス、`defer`、ファイル保存漏れを確認
- 要素が取得できない  
  `id` / `class` のスペルミス、HTML側の記述漏れを確認
- 条件判定がおかしい  
  `===` を使い、比較値の大文字小文字を確認
- 絞り込みが効かない  
  `dataset` の値と比較値が一致しているか確認
- 送信停止できない  
  `submit` イベント内で `event.preventDefault()` が実行されているか確認

---

## 7. 完了条件
次を満たせばJavaScript基礎は完了です。

- `const` / `let` を使い分けて値を扱える
- `if` / ループ / 関数を自力で書ける
- `map` / `filter` / `find` を使って配列処理できる
- DOM要素を取得し、`textContent` / `hidden` を更新できる
- `input` / `change` / `submit` イベントを使い分けられる
- `confirm` + `preventDefault` の流れを説明できる

この状態で、`src` 配下アプリのフロント実装に進む準備ができます。
