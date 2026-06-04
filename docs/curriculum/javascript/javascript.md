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

次に `docs/curriculum/web-app(簡易版)` へ進む場合は、続けて [javascript-fetch-json.md](./javascript-fetch-json.md) を実施します。
この補講で `fetch` / `async` / `await` / JSON通信の最小形を確認してから、Java API連携に入ります。

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
mkdir -p ~/order-management-springboot/practice/javascript # JavaScript練習用フォルダを作成する
```

ファイルを先に作っておきます。

```bash
touch ~/order-management-springboot/practice/javascript/index.html # 画面の土台になるHTMLファイルを作成する
touch ~/order-management-springboot/practice/javascript/script.js # JavaScriptを書くファイルを作成する
```

Git Bashを使わない場合は、VS Code上で次のように作成しても問題ありません。

1. `practice` フォルダの中に `javascript` フォルダを作る
2. `javascript` フォルダの中に `index.html` を作る
3. 同じ場所に `script.js` を作る

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
<!doctype html> <!-- HTML5形式の文書であることをブラウザに伝える -->
<html lang="ja"> <!-- ページ全体。lang="ja" は日本語ページであることを表す -->
  <head> <!-- 画面には直接表示されない設定を書く領域 -->
    <meta charset="utf-8" /> <!-- 日本語を正しく表示するための文字コード設定 -->
    <title>JavaScript 基礎</title> <!-- ブラウザのタブに表示されるタイトル -->
    <script src="./script.js" defer></script> <!-- script.jsをHTML読み込み後に実行する -->
  </head>
  <body> <!-- ブラウザ画面に表示される内容を書く領域 -->
    <h1>JavaScript 練習</h1> <!-- 画面に表示する見出し -->
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
<!doctype html> <!-- HTML5形式の文書であることをブラウザに伝える -->
<html lang="ja"> <!-- ページ全体。lang="ja" は日本語ページであることを表す -->
<head> <!-- 画面には直接表示されない設定を書く領域 -->
  <meta charset="utf-8" /> <!-- 日本語を正しく表示するための文字コード設定 -->
  <meta name="viewport" content="width=device-width, initial-scale=1" /> <!-- スマホ表示時の幅を調整する設定 -->
  <title>JavaScript 基礎</title> <!-- ブラウザのタブに表示されるタイトル -->
  <script src="./script.js" defer></script> <!-- 同じフォルダのscript.jsをHTML読み込み後に実行する -->
</head>
<body> <!-- ブラウザ画面に表示される内容を書く領域 -->
  <h1>JavaScript 基礎</h1> <!-- 画面に表示する見出し -->
  <p>Consoleを開いて確認します。</p> <!-- 開発者ツールのConsoleを見ることを案内する文章 -->
</body>
</html>
```

確認:
- ブラウザに見出しと文章が表示される
- エラーが出ていない

コード解説:
- `defer` を付けると、HTMLを最後まで読み込んでから `script.js` が実行される
- `meta viewport` はスマホ表示での拡大縮小崩れを防ぐ

よくあるミス:
- `script.js` のパス誤り（`./script.js` になっていない）
- `</script>` の閉じ忘れ

#### Step 1: `console.log` で出力する（`script.js`）
`script.js` を次の内容に更新:

```javascript
console.log("JavaScript start"); // Consoleに文字を表示して、JavaScriptが動いているか確認する
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
const workDate = "2026-02-05"; // const: 後から変更しない日付文字列を用意する
let count = 0; // let: 後から変更する数値を用意する
count += 1; // count に 1 を足して、値を 0 から 1 に更新する

const isWorking = true; // boolean型: 勤務中かどうかを true / false で表す
const breakMinutes = 45; // number型: 休憩時間を分単位の数値で表す

console.log("workDate:", workDate); // workDate の値を表示する
console.log("count:", count); // count の値を表示する
console.log("isWorking:", isWorking); // isWorking の値を表示する
console.log("breakMinutes:", breakMinutes); // breakMinutes の値を表示する

console.log("type of workDate:", typeof workDate); // workDate の型を確認する
console.log("type of isWorking:", typeof isWorking); // isWorking の型を確認する
console.log("type of breakMinutes:", typeof breakMinutes); // breakMinutes の型を確認する
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
const status = "WORKING"; // 勤務状態を表す文字列を用意する

if (status === "NOT_STARTED") { // status が "NOT_STARTED" と同じか判定する
  console.log("まだ出勤していません"); // 未出勤の場合に表示する
} else if (status === "WORKING") { // 上の条件に合わず、status が "WORKING" と同じか判定する
  console.log("勤務中です"); // 勤務中の場合に表示する
} else if (status === "FINISHED") { // さらに、status が "FINISHED" と同じか判定する
  console.log("退勤済みです"); // 退勤済みの場合に表示する
} else { // どの条件にも当てはまらない場合
  console.log("不明な状態です"); // 想定外の値であることを表示する
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
const operations = ["出勤", "休憩開始", "休憩終了", "退勤"]; // 勤怠操作名を配列で用意する

for (const operation of operations) { // 配列から1件ずつ取り出して operation に入れる
  console.log("[for...of]", operation); // 取り出した操作名を表示する
}

operations.forEach((operation, index) => { // 配列の各要素に対して同じ処理を実行する
  console.log(`[forEach] ${index + 1}. ${operation}`); // indexは0始まりなので、表示用に1を足す
});
```

確認:
- 2種類のループ結果が表示される

コード解説:
- `operations` は配列です。配列は、複数の値を順番に並べて持つ入れ物です
- この例では、`"出勤"`、`"休憩開始"`、`"休憩終了"`、`"退勤"` の4つの文字列が入っています

`for...of` の処理の流れ:

```javascript
for (const operation of operations) { // operations から値を1つずつ取り出す
  console.log("[for...of]", operation); // 現在取り出している値を表示する
}
```

- `operations` の中身を、先頭から1つずつ取り出します
- 取り出した値は、一時的に `operation` という変数に入ります
- 1回目は `operation` に `"出勤"` が入ります
- 2回目は `operation` に `"休憩開始"` が入ります
- 3回目は `operation` に `"休憩終了"` が入ります
- 4回目は `operation` に `"退勤"` が入ります
- 配列の最後まで処理すると、ループは自動的に終わります

`forEach` の処理の流れ:

```javascript
operations.forEach((operation, index) => { // operation は現在の値、index は現在の位置
  console.log(`[forEach] ${index + 1}. ${operation}`); // 1. 出勤 のように番号付きで表示する
});
```

- `forEach` も、配列の中身を先頭から1つずつ処理します
- `(operation, index) => { ... }` は、「1件ずつ処理するときに実行する関数」です
- `operation` には、現在取り出している値が入ります
- `index` には、現在の位置が入ります
- `index` は `0` から始まるため、1件目の `index` は `0` です
- 画面やConsoleに表示するときは `1. 出勤` のように見せたいので、`index + 1` にしています
- テンプレート文字列 `` `${...}` `` を使うと、文字列の中に変数や計算結果を埋め込めます

使い分け:
- 最初は `for...of` のほうが、処理の流れを追いやすい
- `forEach` は、配列の各要素に対して同じ処理をしたいときによく使う
- この後のDOM操作では、複数の行やボタンに同じ処理を設定するために `forEach` を使う

よくあるミス:
- `for (const i in operations)` を使って値ではなく添字を扱ってしまう
- `` `${index + 1}` `` のバッククォートを `'` と取り違える

#### Step 5: 関数を作る
`script.js` を次の内容に更新:

```javascript
function formatUserLabel(user) { // ユーザー情報を受け取って表示用の文字列を作る関数
  return `${user.username} (${user.role})`; // username と role を組み合わせた文字列を返す
}

const user1 = { username: "tanaka", role: "ROLE_USER" }; // 1人目のユーザー情報
const user2 = { username: "admin", role: "ROLE_ADMIN" }; // 2人目のユーザー情報

console.log(formatUserLabel(user1)); // user1 を関数に渡して、戻り値を表示する
console.log(formatUserLabel(user2)); // user2 を関数に渡して、戻り値を表示する
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

#### 補足: アロー関数（`=>`）の基本
JavaScriptでは、`function` 以外にも、アロー関数という書き方で関数を作れます。

この後の `map` / `filter` / `find` やDOM操作では、アロー関数がよく出てきます。
ここで `function` との違いを一度確認しておきます。

まず、Step 5で書いた `function` 版です。

```javascript
function formatUserLabel(user) { // function を使って formatUserLabel という関数を作る
  return `${user.username} (${user.role})`; // 表示用の文字列を返す
}
```

同じ処理は、アロー関数で次のように書けます。

```javascript
const formatUserLabelArrow = (user) => { // formatUserLabelArrow という変数に関数を入れる
  return `${user.username} (${user.role})`; // 表示用の文字列を返す
};
```

このコードは、次のように読みます。

```text
formatUserLabelArrow という名前の変数に、
user を受け取って表示用文字列を返す関数を入れる
```

アロー関数の形:

```javascript
const 関数名 = (引数) => {
  // 実行する処理
};
```

それぞれの意味:
- `const 関数名 =`: 関数を入れる変数を用意する
- `(引数)`: 関数に渡される値を書く
- `=>`: ここから関数の処理を書く、という記号
- `{ ... }`: 関数が実行されたときの処理を書く

実行方法は、`function` 版と同じです。

```javascript
const user = { username: "tanaka", role: "ROLE_USER" }; // 関数に渡すユーザー情報

console.log(formatUserLabel(user)); // function 版の関数を呼び出す
console.log(formatUserLabelArrow(user)); // アロー関数版の関数を呼び出す
```

戻り値をすぐ返すだけなら、さらに短く書くこともできます。

```javascript
const formatUserLabelShort = (user) => `${user.username} (${user.role})`; // return と {} を省略した短い書き方

console.log(formatUserLabelShort(user)); // 短いアロー関数を呼び出す
```

3つの書き方の比較:

| 書き方 | 例 | 特徴 |
|---|---|---|
| `function 関数名(...) { ... }` | `function formatUserLabel(user) { ... }` | 基本形。初学者には処理の範囲が見えやすい |
| `const 関数名 = (...) => { ... };` | `const formatUserLabelArrow = (user) => { ... };` | 関数を変数に入れる書き方 |
| `(user) => user.username` | `users.map((user) => user.username)` | 1行で値を返す短い書き方 |

この後に出てくる書き方:

```javascript
const applyFilter = () => { // applyFilter という変数に、引数なしの関数を入れる
  // 絞り込み処理を書く
};
```

これは、次のように読むと分かりやすいです。

```text
applyFilter という名前で、あとから呼び出せる処理を作っている
```

`function` で書くと、ほぼ同じ意味のコードは次のようになります。

```javascript
function applyFilter() { // function を使って applyFilter という関数を作る
  // 絞り込み処理を書く
}
```

この教材では、配列処理やイベント処理でよく使われる書き方に慣れるため、アロー関数も使います。
まずは、`=>` が出てきたら「関数を書いている」と読めれば問題ありません。

よくあるミス:
- `=>` を比較記号だと思ってしまう
- `const applyFilter = () => { ... };` の最後の `;` を忘れる
- `applyFilter` と `applyFilter()` の違いを混同する
- 複数行の処理なのに `return` を書き忘れる

#### Step 6: 配列とオブジェクトを扱う
`script.js` を次の内容に更新:

```javascript
const users = [ // ユーザー情報を配列で用意する
  { id: 1, username: "tanaka", role: "ROLE_USER" }, // 1件目のユーザー
  { id: 2, username: "suzuki", role: "ROLE_USER" }, // 2件目のユーザー
  { id: 3, username: "admin", role: "ROLE_ADMIN" } // 3件目のユーザー
];

const usernames = users.map((user) => user.username); // 全ユーザーから username だけを取り出した配列を作る
const admins = users.filter((user) => user.role === "ROLE_ADMIN"); // role が ROLE_ADMIN のユーザーだけを残す
const tanaka = users.find((user) => user.username === "tanaka"); // username が tanaka の最初の1件を探す

console.log("usernames:", usernames); // username一覧を表示する
console.log("admins:", admins); // 管理者ユーザーの配列を表示する
console.log("tanaka:", tanaka); // tanakaユーザー1件を表示する
```

確認:
- `map` / `filter` / `find` の結果が期待通り表示される

実行結果のイメージ:

```text
usernames: ["tanaka", "suzuki", "admin"]
admins: [{ id: 3, username: "admin", role: "ROLE_ADMIN" }]
tanaka: { id: 1, username: "tanaka", role: "ROLE_USER" }
```

ブラウザによって、配列やオブジェクトの表示形式は少し異なる場合があります。

コード解説:
- `users` は「ユーザー情報のオブジェクト」が複数入った配列です
- `(user) => ...` の `user` は仮引数です
- `users` 配列から1件ずつ取り出されたデータが、一時的に `user` に入ります
- `user.username` は、1件分のユーザーから `username` の値を取り出しています

`user` に入る値のイメージ:

```text
1回目: user = { id: 1, username: "tanaka", role: "ROLE_USER" }
2回目: user = { id: 2, username: "suzuki", role: "ROLE_USER" }
3回目: user = { id: 3, username: "admin", role: "ROLE_ADMIN" }
```

`user` という名前は決まりではありません。
ただし、配列の中身がユーザー情報なので、`user` と書くと読みやすくなります。

「返す」の意味:
- `const usernames = ...` の右側で処理した結果が、`usernames` に入ります
- `const admins = ...` の右側で処理した結果が、`admins` に入ります
- `const tanaka = ...` の右側で処理した結果が、`tanaka` に入ります

`map` / `filter` / `find` の違い:
- `map`: 全件を別の形に変換して、配列を返す
- `filter`: 条件に合うものを集めて、配列を返す
- `find`: 条件に合う最初の1件だけを返す

このコードでは、各変数に次のような値が入ります。

```javascript
const usernames = users.map((user) => user.username); // username だけの配列を作る
// 結果: ["tanaka", "suzuki", "admin"]

const admins = users.filter((user) => user.role === "ROLE_ADMIN"); // 管理者だけを残す
// 結果: [{ id: 3, username: "admin", role: "ROLE_ADMIN" }]

const tanaka = users.find((user) => user.username === "tanaka"); // tanaka というユーザーを1件探す
// 結果: { id: 1, username: "tanaka", role: "ROLE_USER" }
```

`filter` と `find` の使い方の違い:

```javascript
console.log(admins[0].username); // filter は配列を返すため、先頭要素を [0] で取り出してから username を読む
console.log(tanaka.username); // find は1件のオブジェクトを返すため、そのまま username を読める
```

`filter` は1件しか見つからなくても、結果は配列です。
`find` は配列ではなく、見つかった1件のオブジェクトを返します。

見つからない場合:
- `filter` は空配列 `[]` を返します
- `find` は `undefined` を返します

よくあるミス:
- `filter` と `find` の戻り値の違い（配列か単一要素か）を取り違える
- `===` の比較値が期待データと一致していない
- `find` の結果を配列だと思って `tanaka[0]` のように書いてしまう

#### Step 7: 文字列検索と組み合わせる
`script.js` を次の内容に更新:

```javascript
const users = [ // 検索対象になるユーザー一覧
  { id: 1, username: "tanaka", role: "ROLE_USER" }, // 一般ユーザー
  { id: 2, username: "suzuki", role: "ROLE_USER" }, // 一般ユーザー
  { id: 3, username: "sato", role: "ROLE_ADMIN" }, // 管理者ユーザー
  { id: 4, username: "yamada", role: "ROLE_USER" } // 一般ユーザー
];

const keyword = "sa"; // ユーザー名に含まれるか調べる検索キーワード
const selectedRole = "ROLE_ADMIN"; // 絞り込みたいロール

const filtered = users.filter((user) => { // users から条件に合うユーザーだけを残す
  const matchedKeyword = keyword === "" || user.username.includes(keyword); // キーワード未指定、または名前にキーワードを含むなら true
  const matchedRole = selectedRole === "" || user.role === selectedRole; // ロール未指定、またはロールが一致すれば true
  return matchedKeyword && matchedRole; // 両方の条件を満たしたユーザーだけを残す
});

console.log("filtered:", filtered); // 絞り込み結果を表示する
```

確認:
- `keyword` と `selectedRole` 条件に一致する要素だけ表示される

コード解説:
- `includes` で部分一致検索ができる
- 条件を分けて変数化すると読みやすい
- 実アプリの検索/絞り込み処理の基本パターン

`includes` の基本:
- `includes` は、文字列の中に指定した文字が含まれているかを調べる
- 含まれている場合は `true`、含まれていない場合は `false` になる

```javascript
console.log("sato".includes("sa")); // true: "sato" に "sa" が含まれる
console.log("tanaka".includes("sa")); // false: "tanaka" に "sa" は含まれない
console.log("sato".includes("to")); // true: "sato" に "to" が含まれる
```

今回のコードでは、`user.username.includes(keyword)` によって、ユーザー名に検索キーワードが含まれているかを確認しています。

条件式の読み方:
- `keyword === ""` は「検索キーワードが空かどうか」を判定している
- `username.includes(keyword)` は「ユーザー名にキーワードが含まれるか」を判定している
- `keyword === "" || username.includes(keyword)` は、「キーワードが空ならOK、または、ユーザー名にキーワードが含まれればOK」という意味
- `selectedRole === "" || user.role === selectedRole` は、「ロール未選択ならOK、または、ユーザーのロールが選択値と一致すればOK」という意味
- `matchedKeyword && matchedRole` は、「キーワード条件とロール条件の両方を満たす」という意味

よくあるミス:
- 大文字小文字を意識せず比較して一致しない
- `&&` と `||` の意味を逆に使う

#### Step 8: 午前の最終形（`script.js`）
`script.js` を次の内容にして、午前の学習を締めます。

```javascript
const users = [ // 検索対象になるユーザー一覧
  { id: 1, username: "tanaka", role: "ROLE_USER" }, // 一般ユーザー
  { id: 2, username: "suzuki", role: "ROLE_USER" }, // 一般ユーザー
  { id: 3, username: "sato", role: "ROLE_ADMIN" }, // 管理者ユーザー
  { id: 4, username: "yamada", role: "ROLE_USER" } // 一般ユーザー
];

function filterUsers(keyword, selectedRole) { // キーワードとロールでユーザーを絞り込む関数
  const normalizedKeyword = keyword.trim().toLowerCase(); // 前後の空白を消し、小文字にそろえる

  return users.filter((user) => { // 条件に合うユーザーだけを配列として返す
    const username = user.username.toLowerCase(); // 比較しやすいようにユーザー名も小文字にする
    const matchedKeyword = normalizedKeyword === "" || username.includes(normalizedKeyword); // キーワード条件に合うか判定する
    const matchedRole = selectedRole === "" || user.role === selectedRole; // ロール条件に合うか判定する
    return matchedKeyword && matchedRole; // 両方の条件に合うユーザーだけを残す
  });
}

const result = filterUsers("sa", "ROLE_ADMIN"); // 名前に sa を含む ROLE_ADMIN のユーザーを探す
console.log("result:", result); // 絞り込み結果を表示する
```

確認:
- 正規化（`trim` / `toLowerCase`）込みで検索できる

実行結果のイメージ:

```text
result: [{ id: 3, username: "sato", role: "ROLE_ADMIN" }]
```

ブラウザによって、配列やオブジェクトの表示形式は少し異なる場合があります。

コード解説:
- Step 8は、Step 7の絞り込み処理を `filterUsers` という関数にまとめたコードです
- `filterUsers(keyword, selectedRole)` は、検索キーワードと選択ロールを受け取る関数です
- `const result = filterUsers("sa", "ROLE_ADMIN");` で関数を呼び出しています
- 呼び出した結果は、`result` という変数に入ります

処理の流れ:
1. `filterUsers("sa", "ROLE_ADMIN")` が実行される
2. `keyword` に `"sa"` が入る
3. `selectedRole` に `"ROLE_ADMIN"` が入る
4. `keyword.trim().toLowerCase()` で、検索キーワードを検索しやすい形に整える
5. `users.filter(...)` で、ユーザーを1件ずつ確認する
6. ユーザー名条件とロール条件の両方に合うユーザーだけを残す
7. 条件に合ったユーザー配列が `return` される
8. `return` された配列が `result` に入る

正規化とは:
- `trim()` は、文字列の前後の空白を取り除く
- `toLowerCase()` は、英字を小文字にそろえる
- 例えば `" SA "` は、`trim().toLowerCase()` によって `"sa"` になる
- 入力値とユーザー名の両方を小文字にそろえることで、`"SA"` と入力しても `"sato"` に一致させやすくなる

`return users.filter(...)` の意味:
- `users.filter(...)` は、条件に合うユーザーだけを集めた配列を作る
- `return` は、その配列を関数の呼び出し元へ返す
- そのため、`filterUsers("sa", "ROLE_ADMIN")` の結果は配列になる

条件指定の例:

```javascript
filterUsers("sa", "ROLE_ADMIN"); // 名前に "sa" を含み、ROLE_ADMIN のユーザーだけを返す
filterUsers("sa", ""); // 名前に "sa" を含むユーザーだけを返す
filterUsers("", "ROLE_USER"); // ROLE_USER のユーザーだけを返す
filterUsers("", ""); // 条件を指定しないため、全ユーザーを返す
```

空文字 `""` を渡した場合は、「その条件では絞り込まない」という意味になります。

よくあるミス:
- `return users.filter(...)` の `return` を書き忘れる
- `keyword` だけ小文字にして、`user.username` 側を小文字にし忘れる
- `filterUsers("ROLE_ADMIN", "sa")` のように、引数の順番を逆にしてしまう

---

## 2. DOM / イベント基礎（午後）

### 2-1. DOMとは
DOMは、HTMLをJavaScriptから扱うための仕組みです。  
要素を取得して、テキスト変更、表示/非表示、イベント処理を実装できます。

この章では、HTMLに書いた `id` / `class` / `data-*` を目印にして、JavaScriptから画面の部品を操作します。
最初は「HTMLのどの要素を、JavaScriptのどの変数に入れているか」を意識してください。

この章では、次を実装します。
- 検索入力 + ロール選択によるテーブル絞り込み
- 一致件数表示
- 該当なし行の表示切り替え
- 削除ボタン押下時の確認ダイアログ

### 2-2. 画面の土台HTMLを作る（`index.html`）
このStepでは、JavaScriptで操作する前の画面部品をHTMLで用意します。

作成する部品:
- ユーザー名を入力する検索欄
- ロールを選択する絞り込み欄
- 絞り込み後の表示件数を出す場所
- ユーザー一覧テーブル
- 条件に一致するユーザーがいないときのメッセージ行

この時点では、入力欄や削除ボタンの**見た目だけ**を作ります。
入力に合わせて行を非表示にする処理や、削除確認を出す処理は、次のStepで `script.js` に書きます。

#### このStepでJavaScriptが使うHTMLの目印
入力欄、プルダウン、表の構造は、HTML/CSS基礎の補足演習で確認済みの前提です。
ここでは、次のStepでJavaScriptから操作するための目印に注目します。

- `id` は、検索欄のように「1つだけ取得したい要素」に付ける
- `class` は、ユーザー行のように「複数まとめて取得したい要素」に付ける
- `data-*` は、JavaScriptの判定で使う値をHTML側に持たせる
- `hidden` は、JavaScriptで表示/非表示を切り替える対象に付ける

この4つが、次のStepで書くJavaScriptとHTMLをつなぐ目印になります。

`index.html` を次の内容に更新:

```html
<!doctype html> <!-- HTML5形式の文書であることをブラウザに伝える -->
<html lang="ja"> <!-- HTML文書全体。lang="ja" はページの言語が日本語であることを表す -->
<head> <!-- 画面には直接表示されない、ページの設定を書く領域 -->
  <meta charset="utf-8" /> <!-- 日本語を正しく表示するため、文字コードをUTF-8にする -->
  <meta name="viewport" content="width=device-width, initial-scale=1" /> <!-- スマホでも画面幅に合わせて表示する -->
  <title>JavaScript DOM演習</title> <!-- ブラウザのタブに表示されるタイトル -->

  <style> /* このページにだけ適用するCSSを書く領域 */
    body { font-family: sans-serif; margin: 24px; } /* ページ全体の文字と外側の余白 */
    .container { max-width: 920px; margin: 0 auto; } /* 内容が広がりすぎないよう中央に配置 */
    .row { display: flex; gap: 12px; align-items: end; flex-wrap: wrap; } /* 検索部品を横並びにする */
    label { display: flex; flex-direction: column; gap: 6px; } /* 説明文と入力欄を縦並びにする */
    table { width: 100%; border-collapse: collapse; margin-top: 12px; } /* 表を画面幅に広げる */
    th, td { border-bottom: 1px solid #ddd; text-align: left; padding: 8px; } /* 表の各セルを整える */
    .muted { color: #666; } /* 補助的なメッセージを控えめな色にする */
  </style>

  <script src="./script.js" defer></script> <!-- 同じフォルダのJavaScriptをHTML読込後に実行する -->
</head>
<body> <!-- ブラウザ画面に表示される内容を書く領域 -->
  <div class="container"> <!-- 見出し・検索欄・テーブルをまとめる外枠 -->
    <h1>ユーザー管理（JavaScript練習）</h1> <!-- 画面の見出し -->

    <!-- 検索条件を入力する領域 -->
    <section class="row">
      <label>
        ユーザー名で検索
        <!-- JavaScriptは id を目印にして、この入力欄の値を取得する -->
        <input id="user-search-input" type="search" placeholder="例: tanaka" autocomplete="off" />
      </label>

      <label>
        ロールで絞り込み
        <!-- JavaScriptは id を目印にして、現在選択されている value を取得する -->
        <select id="role-filter-select">
          <option value="">すべて</option> <!-- 空文字は「ロールで絞り込まない」ことを表す -->
          <option value="ROLE_USER">ROLE_USER</option> <!-- 一般ユーザーだけを表示する選択値 -->
          <option value="ROLE_ADMIN">ROLE_ADMIN</option> <!-- 管理者だけを表示する選択値 -->
        </select>
      </label>

      <!-- JavaScriptが「表示件数: 3件 / 3件」のような結果を書き込む -->
      <p id="user-filter-result" class="muted"></p>
    </section> <!-- 検索条件領域の終わり -->

    <!-- ユーザー一覧を表示する表 -->
    <table>
      <thead> <!-- 表の見出し行をまとめる領域 -->
        <tr>
          <th>ID</th> <!-- 1列目: ユーザーを識別する番号 -->
          <th>ユーザー名</th> <!-- 2列目: ログイン名 -->
          <th>ロール</th> <!-- 3列目: 権限 -->
          <th>操作</th> <!-- 4列目: 削除ボタン -->
        </tr>
      </thead>
      <tbody> <!-- 実際のユーザーデータ行をまとめる領域 -->
        <!-- class は取得対象の行である目印、data-* は絞り込み判定に使う値 -->
        <tr class="js-user-row" data-username="tanaka" data-role="ROLE_USER">
          <td>1</td>
          <td>tanaka</td>
          <td>ROLE_USER</td>
          <td>
            <!-- JavaScriptはこの class のフォームすべてに削除確認処理を設定する -->
            <form class="js-delete-user-form" data-username="tanaka">
              <button type="submit">削除</button> <!-- 押すとフォームの submit イベントが発生する -->
            </form>
          </td>
        </tr>

        <!-- 2件目のユーザーデータ行 -->
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

        <!-- 3件目のユーザーデータ行 -->
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

        <!-- 条件に一致するユーザーが0件の場合だけ、JavaScriptで表示する行 -->
        <tr id="no-match-row" hidden>
          <!-- colspan="4" によって、4列分をつなげてメッセージを表示する -->
          <td colspan="4" class="muted">条件に一致するユーザーがいません。</td>
        </tr>
      </tbody>
    </table> <!-- ユーザー一覧表の終わり -->
  </div> <!-- container の終わり -->
</body>
</html>
```

確認:
- 検索欄・選択欄・テーブルが表示される
- この時点では検索しても何も起きない（正常）

画面の構成:

| HTML | 画面での役割 |
|---|---|
| `<input id="user-search-input">` | ユーザー名を入力する検索欄 |
| `<select id="role-filter-select">` | 表示するロールを選ぶ欄 |
| `<p id="user-filter-result">` | 絞り込み後の件数を表示する場所 |
| `<table>` | ユーザー一覧を表示する表 |
| `<form class="js-delete-user-form">` | 行ごとの削除操作 |
| `<tr id="no-match-row" hidden>` | 0件のときに表示するメッセージ行 |

HTMLとJavaScriptをつなぐ目印:

| HTMLの記述 | 意味 | 次のStepでのJavaScript処理 |
|---|---|---|
| `id="user-search-input"` | 検索欄を1件だけ特定する名前 | 入力された文字を取得する |
| `id="role-filter-select"` | ロール選択欄を1件だけ特定する名前 | 選択された `value` を取得する |
| `id="user-filter-result"` | 件数表示欄を1件だけ特定する名前 | 表示件数の文章を書き換える |
| `class="js-user-row"` | ユーザー行に共通で付ける目印 | すべての行を取得し、表示/非表示を切り替える |
| `data-username="tanaka"` | 行に保持するユーザー名データ | 入力されたキーワードと比較する |
| `data-role="ROLE_USER"` | 行に保持するロールデータ | 選択されたロールと比較する |
| `id="no-match-row"` | 0件メッセージ行の目印 | 表示件数が0件なら表示する |

`id` と `class` の違い:
- `id` は、ページ内で1つだけの要素を特定するときに使います
- 検索欄や件数表示欄はそれぞれ1つだけなので、`id` を付けています
- `class` は、複数の要素をまとめて扱うときに使います
- ユーザー行や削除フォームは複数あるため、共通の `class` を付けています

`data-*` の役割:
- `<td>tanaka</td>` は、利用者に見せる文字です
- `data-username="tanaka"` は、JavaScriptが判定で使う値です
- 画面表示とは別に判定用データを持っておくと、JavaScriptで読み取りやすくなります
- 次のStepでは、`data-username` を `row.dataset.username` として取得します
- `data-role` は、`row.dataset.role` として取得します

`hidden` の役割:
- `hidden` が付いた要素は、最初は画面に表示されません
- `no-match-row` は、通常は非表示でよいため、最初から `hidden` を付けています
- 次のStepでは、該当データが0件になったときにJavaScriptで `hidden` を解除して表示します

なぜこの時点では検索が動かないのか:
- HTMLは、検索欄や表などの画面部品を配置しているだけです
- 「入力されたら行を確認する」「一致しない行を隠す」という動きは、まだ書かれていません
- 次の `script.js` のStepで、HTMLの `id` / `class` / `data-*` を使って動きを追加します

よくあるミス:
- `id` 重複（同じ `id` を複数要素につける）
- `user-search-input` などの `id` と、JavaScript側で指定する文字列が一致していない
- `js-user-row` を付け忘れて、絞り込み対象の行を取得できない
- `data-role` の値と絞り込みの比較値が一致していない
- `<option value="">すべて</option>` の `value=""` を別の値に変更して、未選択時の判定が合わなくなる

### 2-3. JavaScriptで画面を操作する（`script.js`）

#### Step 0: `DOMContentLoaded` を確認
ここから、JavaScriptでHTMLを操作する練習に入ります。

最初に `document` を使います。
`document` は自分で定義する変数ではなく、ブラウザが最初から用意しているオブジェクトです。

`document` は、**今ブラウザで開いているHTMLページ全体**を表します。

例えば、次のように考えると分かりやすいです。

```javascript
document.getElementById("user-search-input");
```

これは、次の意味です。

```text
今開いているHTMLページ全体の中から、
id="user-search-input" の要素を探す
```

`console.log(...)` の `console` も、ブラウザが用意しているオブジェクトです。
同じように、`document` もブラウザが用意しているため、自分で `const document = ...` のように定義しなくても使えます。

`script.js` を次の内容に更新:

```javascript
document.addEventListener("DOMContentLoaded", () => { // HTMLの読み込みが終わってから中の処理を実行する
  console.log("DOM読み込み完了"); // DOM操作を始められる状態になったことをConsoleに表示する
});
```

確認:
- Consoleに `DOM読み込み完了` が表示される

コード解説:
- `document` は、ブラウザで開いているHTMLページ全体を表す
- `document.addEventListener(...)` は、HTMLページ全体に対してイベント処理を登録する書き方
- `DOMContentLoaded` は、HTMLの読み込みが終わったタイミングで発火するイベント
- HTMLの要素をJavaScriptで取得する処理は、このイベントの中に書くと安全
- `defer` を付けていても、DOM操作に慣れるまではこの形で書くと処理順を把握しやすい

#### Step 1: 要素取得とガード節
ここでいうガード節とは、本来の処理に入る前に「このまま処理を続けてもよいか」を確認し、問題があれば早めに `return` で終了する書き方です。

今回のようにJavaScriptでHTML要素を操作する場合、HTML側の `id` や `class` を書き間違えると、JavaScriptは目的の要素を取得できません。

例えば、検索欄を取得できなかった場合、`searchInput` にはHTML要素ではなく `null` が入ります。  
その状態で `searchInput.value` のように値を読もうとすると、存在しないものから値を読もうとしてエラーになります。

そのため、Step 1では最初に必要な要素が取得できているか確認します。  
確認に通った場合だけ、Step 2以降の絞り込み処理へ進めるようにします。

`script.js` を次の内容に更新:

```javascript
document.addEventListener("DOMContentLoaded", () => { // HTMLの読み込み完了後にDOM操作を始める
  const searchInput = document.getElementById("user-search-input"); // ユーザー名検索欄を取得する
  const roleSelect = document.getElementById("role-filter-select"); // ロール選択欄を取得する
  const resultText = document.getElementById("user-filter-result"); // 件数表示用のp要素を取得する
  const noMatchRow = document.getElementById("no-match-row"); // 0件メッセージ行を取得する
  const rows = Array.from(document.querySelectorAll("tr.js-user-row")); // ユーザー行をすべて取得し、配列に変換する

  if (!(searchInput instanceof HTMLInputElement) || // 検索欄がinput要素でなければ異常
      !(roleSelect instanceof HTMLSelectElement) || // ロール欄がselect要素でなければ異常
      !(resultText instanceof HTMLElement) || // 件数表示欄がHTML要素でなければ異常
      !(noMatchRow instanceof HTMLTableRowElement) || // 0件行がtr要素でなければ異常
      rows.length === 0) { // ユーザー行が1件も取得できなければ異常
    console.log("必要要素が見つからないため終了"); // 異常時の確認メッセージ
    return; // 以降の処理を実行せずに終了する
  }

  console.log("要素取得OK", { rowCount: rows.length }); // 取得できた行数を表示する
});
```

確認:
- Consoleに `要素取得OK` と件数が表示される

コード解説:
- `document.getElementById("user-search-input")` は、`id="user-search-input"` の要素を1件取得する
- `document.querySelectorAll("tr.js-user-row")` は、`class="js-user-row"` が付いた `tr` 要素をすべて取得する
- `querySelectorAll` の結果は配列そのものではないため、`Array.from(...)` で配列に変換している
- `instanceof HTMLInputElement` は、「取得した要素が input かどうか」を確認している
- `instanceof HTMLSelectElement` は、「取得した要素が select かどうか」を確認している
- ガード節（早期 `return`）は、必要な要素が見つからない場合に処理をそこで止める書き方
- 途中で処理を止めることで、存在しない要素を操作してエラーになるのを防ぐ

ガード節の考え方:
- 要素が正しく取得できた場合: `if` の中には入らず、下の処理へ進む
- 要素が見つからない場合: Consoleにメッセージを出して `return` で終了する

よくあるミス:
- `querySelectorAll` のセレクタが間違って `rows.length === 0`
- 要素 `id` のタイプミス

#### Step 2: 絞り込み関数 `applyFilter` を作る
`script.js` を次の内容に更新:

```javascript
document.addEventListener("DOMContentLoaded", () => { // HTMLの読み込み完了後にDOM操作を始める
  const searchInput = document.getElementById("user-search-input"); // ユーザー名検索欄を取得する
  const roleSelect = document.getElementById("role-filter-select"); // ロール選択欄を取得する
  const resultText = document.getElementById("user-filter-result"); // 件数表示用のp要素を取得する
  const noMatchRow = document.getElementById("no-match-row"); // 0件メッセージ行を取得する
  const rows = Array.from(document.querySelectorAll("tr.js-user-row")); // ユーザー行をすべて取得し、配列に変換する

  if (!(searchInput instanceof HTMLInputElement) || // 検索欄がinput要素でなければ終了する
      !(roleSelect instanceof HTMLSelectElement) || // ロール欄がselect要素でなければ終了する
      !(resultText instanceof HTMLElement) || // 件数表示欄がHTML要素でなければ終了する
      !(noMatchRow instanceof HTMLTableRowElement) || // 0件行がtr要素でなければ終了する
      rows.length === 0) { // ユーザー行がない場合は終了する
    return; // 必要な要素がない状態で処理を続けない
  }

  const applyFilter = () => { // 現在の入力条件でテーブルを絞り込む関数
    const keyword = searchInput.value.trim().toLowerCase(); // 検索欄の値を空白除去・小文字化して取得する
    const selectedRole = roleSelect.value; // 選択中のロール値を取得する
    let visibleCount = 0; // 表示している行数を数える変数

    rows.forEach((row) => { // ユーザー行を1行ずつ確認する
      const username = (row.dataset.username || "").toLowerCase(); // 行のdata-usernameを取得して小文字化する
      const role = row.dataset.role || ""; // 行のdata-roleを取得する
      const matchedKeyword = keyword === "" || username.includes(keyword); // キーワード未入力、または名前に含まれればtrue
      const matchedRole = selectedRole === "" || role === selectedRole; // ロール未選択、または一致すればtrue
      const visible = matchedKeyword && matchedRole; // 両方の条件に合う場合だけ表示対象にする

      row.hidden = !visible; // visible が false の行は hidden で非表示にする
      if (visible) { // 表示対象の行だけ件数に加える
        visibleCount += 1; // 表示件数を1増やす
      }
    });

    noMatchRow.hidden = visibleCount > 0; // 表示件数が0なら0件メッセージを表示する
    resultText.textContent = `表示件数: ${visibleCount}件 / ${rows.length}件`; // 件数表示を更新する
  };

  applyFilter(); // 初期表示時にも件数と表示状態を更新する
});
```

確認:
- 初期表示で `表示件数: 3件 / 3件` と表示される
- 入力欄/選択欄を変えても、この時点では未連動（次Stepで対応）

コード解説:
- `row.dataset.username` で `data-username` にアクセスできる
- `hidden` を `true/false` で切り替えて表示制御できる
- `.trim().toLowerCase()` で入力ゆれを吸収できる

処理の流れ:
1. 検索欄の値を `keyword` に入れる
2. ロール選択欄の値を `selectedRole` に入れる
3. `rows.forEach` で、テーブルの行を1行ずつ確認する
4. その行の `data-username` と `data-role` を取り出す
5. ユーザー名条件に合うかを `matchedKeyword` に入れる
6. ロール条件に合うかを `matchedRole` に入れる
7. 両方に合う場合だけ `visible` が `true` になる
8. `row.hidden = !visible` で、表示するか非表示にするかを切り替える
9. 表示した行の数を `visibleCount` で数える

`row.hidden = !visible` の読み方:
- `visible` が `true` のとき、`!visible` は `false` になるため、行は表示される
- `visible` が `false` のとき、`!visible` は `true` になるため、行は非表示になる

`|| ""` の意味:
- `row.dataset.username` が取得できない場合でも、空文字 `""` として扱う
- これにより、`toLowerCase()` を呼び出したときのエラーを防ぎやすくなる

よくあるミス:
- `row.dataSet` と書いてしまう（正しくは `dataset`）
- `rows` が `NodeList` のままで配列メソッド制限に引っかかる

#### Step 3: イベント連動を追加（`input` / `change`）
`script.js` を次の内容に更新:

```javascript
document.addEventListener("DOMContentLoaded", () => { // HTMLの読み込み完了後にDOM操作を始める
  const searchInput = document.getElementById("user-search-input"); // ユーザー名検索欄を取得する
  const roleSelect = document.getElementById("role-filter-select"); // ロール選択欄を取得する
  const resultText = document.getElementById("user-filter-result"); // 件数表示用のp要素を取得する
  const noMatchRow = document.getElementById("no-match-row"); // 0件メッセージ行を取得する
  const rows = Array.from(document.querySelectorAll("tr.js-user-row")); // ユーザー行をすべて取得し、配列に変換する

  if (!(searchInput instanceof HTMLInputElement) || // 検索欄がinput要素でなければ終了する
      !(roleSelect instanceof HTMLSelectElement) || // ロール欄がselect要素でなければ終了する
      !(resultText instanceof HTMLElement) || // 件数表示欄がHTML要素でなければ終了する
      !(noMatchRow instanceof HTMLTableRowElement) || // 0件行がtr要素でなければ終了する
      rows.length === 0) { // ユーザー行がない場合は終了する
    return; // 必要な要素がない状態で処理を続けない
  }

  const applyFilter = () => { // 現在の入力条件でテーブルを絞り込む関数
    const keyword = searchInput.value.trim().toLowerCase(); // 検索欄の値を空白除去・小文字化して取得する
    const selectedRole = roleSelect.value; // 選択中のロール値を取得する
    let visibleCount = 0; // 表示している行数を数える変数

    rows.forEach((row) => { // ユーザー行を1行ずつ確認する
      const username = (row.dataset.username || "").toLowerCase(); // 行のdata-usernameを取得して小文字化する
      const role = row.dataset.role || ""; // 行のdata-roleを取得する
      const matchedKeyword = keyword === "" || username.includes(keyword); // キーワード条件に合うか判定する
      const matchedRole = selectedRole === "" || role === selectedRole; // ロール条件に合うか判定する
      const visible = matchedKeyword && matchedRole; // 両方の条件に合う場合だけ表示対象にする
      row.hidden = !visible; // 条件に合わない行を非表示にする
      if (visible) { // 表示対象の行だけ件数に加える
        visibleCount += 1; // 表示件数を1増やす
      }
    });

    noMatchRow.hidden = visibleCount > 0; // 表示件数が0なら0件メッセージを表示する
    resultText.textContent = `表示件数: ${visibleCount}件 / ${rows.length}件`; // 件数表示を更新する
  };

  searchInput.addEventListener("input", applyFilter); // 入力欄に文字が入力されるたびに絞り込みを実行する
  roleSelect.addEventListener("change", applyFilter); // ロール選択が変わるたびに絞り込みを実行する
  applyFilter(); // 初期表示時にも件数と表示状態を更新する
});
```

確認:
- 検索欄入力で即時に絞り込みされる
- ロール選択で絞り込みされる
- 一致なしなら「条件に一致するユーザーがいません。」が表示される

コード解説:
- `input`: 文字入力のたびに発火
- `change`: 選択値が変わったタイミングで発火
- `addEventListener("input", applyFilter)` は、「入力されたら `applyFilter` を実行する」という意味
- Step 2では最初の1回だけ `applyFilter()` を実行していた
- Step 3では、ユーザー操作のたびに `applyFilter()` が実行されるようにしている

#### Step 4: 削除確認ダイアログを追加（`submit` + `confirm`）
`script.js` を次の内容に更新:

```javascript
document.addEventListener("DOMContentLoaded", () => { // HTMLの読み込み完了後に初期設定を行う
  setupDeleteConfirmation(); // 削除フォームに確認ダイアログを設定する
  setupUserTableFilter(); // ユーザー一覧の検索・絞り込みを設定する
});

function setupDeleteConfirmation() { // 削除確認ダイアログを設定する関数
  const deleteForms = document.querySelectorAll("form.js-delete-user-form"); // 削除フォームをすべて取得する

  deleteForms.forEach((form) => { // 各削除フォームに同じ処理を設定する
    form.addEventListener("submit", (event) => { // フォーム送信時に実行する処理
      const username = form.dataset.username || "このユーザー"; // data-username からユーザー名を取得する
      const accepted = window.confirm(`ユーザー「${username}」を削除します。よろしいですか？`); // 確認ダイアログを表示する
      if (!accepted) { // キャンセルされた場合
        event.preventDefault(); // フォーム送信を止める
      }
    });
  });
}

function setupUserTableFilter() { // ユーザー一覧の絞り込みを設定する関数
  const searchInput = document.getElementById("user-search-input"); // ユーザー名検索欄を取得する
  const roleSelect = document.getElementById("role-filter-select"); // ロール選択欄を取得する
  const resultText = document.getElementById("user-filter-result"); // 件数表示用のp要素を取得する
  const noMatchRow = document.getElementById("no-match-row"); // 0件メッセージ行を取得する
  const rows = Array.from(document.querySelectorAll("tr.js-user-row")); // ユーザー行をすべて取得し、配列に変換する

  if (!(searchInput instanceof HTMLInputElement) || // 検索欄がinput要素でなければ終了する
      !(roleSelect instanceof HTMLSelectElement) || // ロール欄がselect要素でなければ終了する
      !(resultText instanceof HTMLElement) || // 件数表示欄がHTML要素でなければ終了する
      !(noMatchRow instanceof HTMLTableRowElement) || // 0件行がtr要素でなければ終了する
      rows.length === 0) { // ユーザー行がない場合は終了する
    return; // 必要な要素がない状態で処理を続けない
  }

  const applyFilter = () => { // 現在の入力条件でテーブルを絞り込む関数
    const keyword = searchInput.value.trim().toLowerCase(); // 検索欄の値を空白除去・小文字化して取得する
    const selectedRole = roleSelect.value; // 選択中のロール値を取得する
    let visibleCount = 0; // 表示している行数を数える変数

    rows.forEach((row) => { // ユーザー行を1行ずつ確認する
      const username = (row.dataset.username || "").toLowerCase(); // 行のdata-usernameを取得して小文字化する
      const role = row.dataset.role || ""; // 行のdata-roleを取得する
      const matchedKeyword = keyword === "" || username.includes(keyword); // キーワード条件に合うか判定する
      const matchedRole = selectedRole === "" || role === selectedRole; // ロール条件に合うか判定する
      const visible = matchedKeyword && matchedRole; // 両方の条件に合う場合だけ表示対象にする
      row.hidden = !visible; // 条件に合わない行を非表示にする
      if (visible) { // 表示対象の行だけ件数に加える
        visibleCount += 1; // 表示件数を1増やす
      }
    });

    noMatchRow.hidden = visibleCount > 0; // 表示件数が0なら0件メッセージを表示する
    resultText.textContent = `表示件数: ${visibleCount}件 / ${rows.length}件`; // 件数表示を更新する
  };

  searchInput.addEventListener("input", applyFilter); // 入力欄に文字が入力されるたびに絞り込みを実行する
  roleSelect.addEventListener("change", applyFilter); // ロール選択が変わるたびに絞り込みを実行する
  applyFilter(); // 初期表示時にも件数と表示状態を更新する
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
- `setupDeleteConfirmation()` は、削除フォームに確認ダイアログを設定する関数
- `setupUserTableFilter()` は、検索と絞り込みを設定する関数
- 役割ごとに関数を分けると、どこに何の処理があるか追いやすくなる

削除確認の流れ:
1. 削除ボタンを押す
2. フォームの `submit` イベントが発火する
3. `window.confirm(...)` で確認ダイアログを表示する
4. キャンセルされた場合、`accepted` が `false` になる
5. `event.preventDefault()` で送信を止める
6. OKされた場合は、送信を止めずに通常の送信処理へ進む

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
   const attendances = [ // 勤怠データを配列で用意する
     { username: "tanaka", status: "WORKING", minutes: 120 }, // tanaka は勤務中で120分勤務
     { username: "suzuki", status: "FINISHED", minutes: 480 }, // suzuki は退勤済みで480分勤務
     { username: "sato", status: "WORKING", minutes: 240 } // sato は勤務中で240分勤務
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

### 5-1. 実装前に知っておくこと
ミニ制作では、ここまでの検索・絞り込みに加えて、JavaScriptでテーブル行を追加します。
画面に要素を追加するときは、次の流れで考えます。

1. 配列に新しいユーザーデータを追加する
2. 画面のテーブル表示を作り直す
3. 検索条件に合うユーザーだけを表示する

テーブル行を追加する最小例:

```javascript
const row = document.createElement("tr"); // 新しいテーブル行 tr を作る
const usernameCell = document.createElement("td"); // ユーザー名を表示するセル td を作る

usernameCell.textContent = "tanaka"; // セルに表示する文字を設定する
row.appendChild(usernameCell); // 作成したセルを行の中に追加する
tableBody.appendChild(row); // 作成した行をテーブル本体に追加する
```

コード解説:
- `document.createElement("tr")` で、新しい `tr` 要素を作る
- `document.createElement("td")` で、新しい `td` 要素を作る
- `textContent` で、セルに表示する文字を設定する
- `appendChild` で、親要素の中に子要素を追加する
- 実際の制作では、ユーザー1件につき `tr` を1つ作り、その中に `td` を追加していく

作り方の目安:
- `users` 配列にユーザー一覧を持たせる
- 追加フォームが送信されたら、`users.push(...)` で配列に追加する
- `renderUsers()` のような関数で、配列からテーブルを描画する
- 検索や削除の後も、同じ `renderUsers()` を呼び出して画面を更新する

### 5-2. 実装要件

- ユーザー作成フォーム（ユーザー名 / ロール）
- 追加ボタンでテーブルに新規行を追加（ページ再読込なし）
- 検索とロール絞り込みが動作する
- 削除前に `confirm` を表示する
- 0件時メッセージを表示する

制約:
- バックエンド連携なし（配列データのみで管理）
- このミニ制作では `fetch` は使わない

API通信は次の [javascript-fetch-json.md](./javascript-fetch-json.md) で扱います。

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
