# JavaScript DOM Step 1: 要素取得とガード節 解説

対象資料: `docs/curriculum/javascript/javascript.md`

## このStepの目的

Step 1では、画面を動かす前に、JavaScriptからHTMLの部品を正しく取得できるか確認します。

このStepで確認すること:
- ユーザー名検索欄を取得できる
- ロール選択欄を取得できる
- 表示件数を書く場所を取得できる
- 0件メッセージ行を取得できる
- ユーザー行をまとめて取得できる

このStepでは、まだ画面の表示は変わりません。  
検索欄に入力しても、まだ絞り込みは動かない状態で正常です。

## Step 1のコード

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

## 1行ずつ読む

```javascript
document.addEventListener("DOMContentLoaded", () => {
```

HTMLの読み込みが終わったら、中の処理を実行するという意味です。

HTMLの読み込みが終わる前に要素を探すと、まだHTML要素が存在せず、取得できない可能性があります。  
そのため、DOM操作は `DOMContentLoaded` の中に書きます。

```javascript
const searchInput = document.getElementById("user-search-input");
```

HTMLの中から、次の要素を探しています。

```html
<input id="user-search-input" ... />
```

意味:
- ユーザー名検索欄を探す
- 見つかった要素を `searchInput` に入れる

```javascript
const roleSelect = document.getElementById("role-filter-select");
```

HTMLの中から、次の要素を探しています。

```html
<select id="role-filter-select">
```

意味:
- ロール選択欄を探す
- 見つかった要素を `roleSelect` に入れる

```javascript
const resultText = document.getElementById("user-filter-result");
```

HTMLの中から、次の要素を探しています。

```html
<p id="user-filter-result" class="muted"></p>
```

意味:
- 表示件数を書く場所を探す
- 見つかった要素を `resultText` に入れる

```javascript
const noMatchRow = document.getElementById("no-match-row");
```

HTMLの中から、次の要素を探しています。

```html
<tr id="no-match-row" hidden>
```

意味:
- 0件メッセージ用の行を探す
- 見つかった要素を `noMatchRow` に入れる

```javascript
const rows = Array.from(document.querySelectorAll("tr.js-user-row"));
```

ここでは、ユーザー行をすべて探しています。

```javascript
document.querySelectorAll("tr.js-user-row")
```

これは、次の意味です。

```text
class="js-user-row" が付いた tr 要素をすべて探す
```

HTMLには、次のような行があります。

```html
<tr class="js-user-row" data-username="tanaka" data-role="ROLE_USER">
<tr class="js-user-row" data-username="suzuki" data-role="ROLE_USER">
<tr class="js-user-row" data-username="admin" data-role="ROLE_ADMIN">
```

そのため、3件の行が見つかります。

`querySelectorAll` の結果は、厳密には普通の配列ではありません。  
そこで、`Array.from(...)` を使って配列に変換しています。

意味:
- ユーザー行を全部探す
- 配列に変換する
- `rows` に入れる

## ガード節を読む

```javascript
if (!(searchInput instanceof HTMLInputElement) ||
```

意味:

```text
searchInput が input 要素ではなかったら
```

`instanceof HTMLInputElement` は、「input要素かどうか」を確認しています。  
前に `!` が付いているため、「input要素ではないか」という判定になります。

```javascript
!(roleSelect instanceof HTMLSelectElement) ||
```

意味:

```text
roleSelect が select 要素ではなかったら
```

```javascript
!(resultText instanceof HTMLElement) ||
```

意味:

```text
resultText が HTML 要素ではなかったら
```

`p`、`div`、`section` など、多くのHTML要素は `HTMLElement` です。

```javascript
!(noMatchRow instanceof HTMLTableRowElement) ||
```

意味:

```text
noMatchRow が tr 要素ではなかったら
```

```javascript
rows.length === 0
```

意味:

```text
ユーザー行が1件も見つからなかったら
```

`||` は「または」です。  
この `if` 全体は、次の意味になります。

```text
必要なHTML要素のうち、どれか1つでも正しく取得できていなければ
```

問題があった場合は、この処理に入ります。

```javascript
console.log("必要要素が見つからないため終了");
return;
```

意味:
- Consoleにメッセージを出す
- `return` で処理をここで終わる
- 存在しない要素を操作してエラーになるのを防ぐ

## 成功時の表示

```javascript
console.log("要素取得OK", { rowCount: rows.length });
```

ここまで来た場合、必要なHTML要素はすべて取得できています。

`rows.length` は、取得できたユーザー行の件数です。  
今回のHTMLではユーザー行が3件あるため、Consoleには次のように表示されます。

```text
要素取得OK
rowCount: 3
```

Consoleに `Object` と表示された場合は、クリックして展開すると `rowCount: 3` を確認できます。

## このStepのまとめ

Step 1でやっていること:

1. HTMLの読み込み完了を待つ
2. 検索欄を探す
3. ロール選択欄を探す
4. 件数表示欄を探す
5. 0件メッセージ行を探す
6. ユーザー行を全部探す
7. 必要な要素が取れているか確認する
8. 問題があれば処理を止める
9. 問題がなければ `要素取得OK` と表示する

このStepは、画面を変える処理ではありません。  
JavaScriptからHTMLの部品を取得できるか確認するStepです。
