# JavaScript DOM Step 4: 削除確認ダイアログ 解説

対象資料: `docs/curriculum/javascript/javascript.md`

## このStepの目的

Step 4では、削除ボタンを押したときに確認ダイアログを表示します。

このStepで行うこと:
- 削除フォームをすべて取得する
- フォーム送信時の `submit` イベントを受け取る
- `window.confirm(...)` で確認ダイアログを表示する
- キャンセルされた場合は `event.preventDefault()` で送信を止める
- 検索・絞り込み処理を関数に分ける

Step 3までは、検索と絞り込みだけを扱いました。  
Step 4では、それに加えて削除ボタンの操作を扱います。

## Step 4のコード

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

## Step 3から増えた考え方

Step 3では、検索・絞り込みだけを1つの大きな処理として書いていました。

Step 4では、処理を2つの関数に分けています。

```javascript
setupDeleteConfirmation();
setupUserTableFilter();
```

意味:
- `setupDeleteConfirmation()` は、削除確認ダイアログを設定する
- `setupUserTableFilter()` は、ユーザー一覧の検索・絞り込みを設定する

関数を分ける理由:
- 削除確認の処理と、検索・絞り込みの処理を混ぜないため
- どこに何の処理があるか分かりやすくするため
- 後から修正しやすくするため

## 最初に実行される部分

```javascript
document.addEventListener("DOMContentLoaded", () => {
  setupDeleteConfirmation();
  setupUserTableFilter();
});
```

意味:

```text
HTMLの読み込みが終わったら、
削除確認の設定と、
ユーザー一覧の絞り込み設定を行う
```

この時点では、関数を定義しているだけでは動きません。  
`setupDeleteConfirmation();` と `setupUserTableFilter();` を呼び出すことで、実際に設定が行われます。

## 削除フォームを取得する

```javascript
function setupDeleteConfirmation() {
```

削除確認を設定する関数です。

```javascript
const deleteForms = document.querySelectorAll("form.js-delete-user-form");
```

HTMLの次のようなフォームをすべて取得しています。

```html
<form class="js-delete-user-form" data-username="tanaka">
```

`querySelectorAll("form.js-delete-user-form")` の意味:

```text
class="js-delete-user-form" が付いた form 要素をすべて探す
```

今回のHTMLでは、ユーザー行ごとに削除フォームがあります。

```text
tanaka の削除フォーム
suzuki の削除フォーム
admin の削除フォーム
```

## 各フォームにsubmitイベントを設定する

```javascript
deleteForms.forEach((form) => {
```

取得した削除フォームを1つずつ処理します。

```text
1回目: tanaka の削除フォーム
2回目: suzuki の削除フォーム
3回目: admin の削除フォーム
```

```javascript
form.addEventListener("submit", (event) => {
```

意味:

```text
このフォームが送信されるときに、中の処理を実行する
```

削除ボタンは次のようになっています。

```html
<button type="submit">削除</button>
```

`type="submit"` のボタンを押すと、フォームの `submit` イベントが発生します。

## eventとは

```javascript
form.addEventListener("submit", (event) => {
```

`event` は、発生したイベントの情報が入る仮引数です。

ここでは、フォーム送信イベントの情報が `event` に入ります。

`event` を使うと、例えば次のようなことができます。

```text
フォーム送信を止める
クリックされた対象を調べる
イベントの種類を調べる
```

このStepでは、フォーム送信を止めるために使います。

## data-usernameからユーザー名を取得する

```javascript
const username = form.dataset.username || "このユーザー";
```

HTMLの次の部分を読んでいます。

```html
<form class="js-delete-user-form" data-username="tanaka">
```

`form.dataset.username` で、`data-username` の値を取得できます。

```text
data-username="tanaka" -> form.dataset.username は "tanaka"
```

`|| "このユーザー"` は、値が取れなかった場合の保険です。

もし `data-username` がなければ、確認メッセージでは `"このユーザー"` を使います。

## confirmで確認ダイアログを出す

```javascript
const accepted = window.confirm(`ユーザー「${username}」を削除します。よろしいですか？`);
```

`window.confirm(...)` は、ブラウザの確認ダイアログを表示します。

表示例:

```text
ユーザー「tanaka」を削除します。よろしいですか？
```

ダイアログには、OKとキャンセルがあります。

```text
OK        -> true
キャンセル -> false
```

その結果を `accepted` に入れています。

```text
accepted = true  -> OKされた
accepted = false -> キャンセルされた
```

## キャンセルされたら送信を止める

```javascript
if (!accepted) {
  event.preventDefault();
}
```

`!accepted` は、`accepted` の反対です。

```text
accepted が true  -> !accepted は false
accepted が false -> !accepted は true
```

つまり、この `if` は次の意味です。

```text
キャンセルされた場合
```

```javascript
event.preventDefault();
```

これは、ブラウザの標準動作を止める命令です。

今回の標準動作は、フォーム送信です。  
キャンセルされた場合は、フォーム送信を止めています。

## OKされた場合

OKされた場合、`accepted` は `true` です。

そのため、次の `if` には入りません。

```javascript
if (!accepted) {
  event.preventDefault();
}
```

つまり、`event.preventDefault()` は実行されません。

その結果、フォーム送信が通常どおり進みます。

今回の教材では、フォームに `action` を設定していないため、同じページを再読み込みするような動きになります。

## file://の警告について

`index.html` をダブルクリックして `file://` で開いている場合、削除ボタンでOKを押すと、次のような警告がConsoleに出ることがあります。

```text
Unsafe attempt to load URL file:///...
```

これは、JavaScriptの文法エラーではありません。  
ローカルファイルを直接開いている状態でフォーム送信しようとしたため、ブラウザのセキュリティ制約で出る警告です。

Step 4の確認では、次を見ます。

```text
キャンセル -> 画面遷移しない
OK        -> 送信しようとする
```

## setupUserTableFilterについて

```javascript
function setupUserTableFilter() {
```

この関数は、Step 3までに作った検索・絞り込み処理です。

Step 4では、削除確認処理を追加するために、検索・絞り込み処理を `setupUserTableFilter` という関数に分けています。

中身の考え方はStep 3と同じです。

```text
検索欄を取得する
ロール選択欄を取得する
ユーザー行を取得する
applyFilter を作る
input イベントで applyFilter を実行する
change イベントで applyFilter を実行する
初期表示でも applyFilter を実行する
```

## Step 4での処理の流れ

ページを開いた直後:

```text
1. HTMLの読み込みが終わる
2. setupDeleteConfirmation() が実行される
3. 削除フォームすべてに submit イベントを設定する
4. setupUserTableFilter() が実行される
5. 検索・絞り込みイベントを設定する
6. 初期表示の件数を表示する
```

削除ボタンを押したとき:

```text
1. 削除ボタンを押す
2. フォームの submit イベントが発生する
3. data-username からユーザー名を取得する
4. confirm ダイアログを表示する
5. キャンセルなら event.preventDefault() で送信を止める
6. OKなら送信を止めずに進める
```

## 画面で確認すること

Step 4を実行したら、次を確認します。

1. ページを開く
2. `表示件数: 3件 / 3件` が表示される
3. 検索欄やロール絞り込みがStep 3と同じように動く
4. `削除` ボタンを押す
5. 確認ダイアログが表示される
6. キャンセルを押す
7. 画面遷移しない
8. もう一度 `削除` ボタンを押す
9. OKを押す
10. フォーム送信が進もうとする

## 理解用の小実験

キャンセルしても送信される状態を確認したい場合は、次の行を一時的にコメントアウトします。

```javascript
// event.preventDefault();
```

この状態でキャンセルしても、送信が止まりません。  
確認後は、必ず元に戻してください。

```javascript
event.preventDefault();
```

次に、確認メッセージを変えてみることもできます。

```javascript
const accepted = window.confirm(`${username} を本当に削除しますか？`);
```

確認後は、教材のコードに戻してください。

## よくあるミス

- `submit` を `click` と混同する
- `button type="submit"` ではなく `type="button"` にしてしまい、`submit` が発生しない
- `event.preventDefault()` を書き忘れて、キャンセルしても送信される
- `event.preventDefault()` を `if` の外に書いて、OKしても常に送信が止まる
- `data-username` をHTMLに書き忘れて、確認メッセージにユーザー名が出ない
- `setupDeleteConfirmation()` を呼び忘れて、削除確認が動かない

## このStepのまとめ

Step 4でやっていること:

1. 削除確認処理と検索・絞り込み処理を関数に分ける
2. 削除フォームをすべて取得する
3. 各フォームに `submit` イベントを設定する
4. `data-username` からユーザー名を取得する
5. `confirm` で確認ダイアログを出す
6. キャンセルされたら `preventDefault()` で送信を止める
7. 検索・絞り込み処理はStep 3と同じように動かす

Step 4は、フォーム送信の動きをJavaScriptで制御するStepです。  
「削除前に確認する」「キャンセルなら送信しない」という実務でもよく使う流れを学びます。
