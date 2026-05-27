# JavaScript DOM Step 2: 絞り込み関数 applyFilter 解説

対象資料: `docs/curriculum/javascript/javascript.md`

## このStepの目的

Step 2では、Step 1で取得したHTML要素を使って、ユーザー一覧を絞り込む関数を作ります。

このStepで行うこと:
- 検索欄の値を取得する
- ロール選択欄の値を取得する
- ユーザー行を1行ずつ確認する
- 条件に合う行だけ表示する
- 表示件数を画面に出す
- 0件の場合だけメッセージ行を表示する

ただし、Step 2ではまだイベント連動をしていません。  
そのため、検索欄に文字を入力しても、入力した瞬間には絞り込みは動きません。  
ユーザー操作と連動するのは、次のStep 3です。

## Step 2のコード

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

## Step 1と同じ部分

次の部分は、Step 1と同じです。

```javascript
const searchInput = document.getElementById("user-search-input");
const roleSelect = document.getElementById("role-filter-select");
const resultText = document.getElementById("user-filter-result");
const noMatchRow = document.getElementById("no-match-row");
const rows = Array.from(document.querySelectorAll("tr.js-user-row"));
```

意味:
- 検索欄を取得する
- ロール選択欄を取得する
- 件数表示欄を取得する
- 0件メッセージ行を取得する
- ユーザー行をすべて取得する

次のガード節も、Step 1と同じ考え方です。

```javascript
if (!(searchInput instanceof HTMLInputElement) ||
    !(roleSelect instanceof HTMLSelectElement) ||
    !(resultText instanceof HTMLElement) ||
    !(noMatchRow instanceof HTMLTableRowElement) ||
    rows.length === 0) {
  return;
}
```

意味:
- 必要なHTML要素が正しく取得できていなければ処理を止める
- 存在しない要素を操作してエラーになるのを防ぐ

Step 2では、異常時の `console.log` は省略しています。  
必要な要素がない場合は、何もせずに `return` で終了します。

## applyFilterとは

```javascript
const applyFilter = () => {
```

`applyFilter` は、ユーザー一覧を絞り込むための関数です。

名前の意味:

```text
apply = 適用する
filter = 絞り込み
```

つまり、`applyFilter` は「現在の検索条件を一覧に適用する」という意味の関数名です。

## 検索条件を取得する

```javascript
const keyword = searchInput.value.trim().toLowerCase();
```

意味:
- `searchInput.value` で、検索欄に入力された文字を取得する
- `trim()` で、前後の空白を取り除く
- `toLowerCase()` で、小文字にそろえる

例えば、検索欄に次のように入力されていた場合:

```text
 SA 
```

この処理によって、次のようになります。

```text
sa
```

```javascript
const selectedRole = roleSelect.value;
```

意味:
- ロール選択欄で選ばれている値を取得する

選択状態の例:

```text
すべて      -> ""
ROLE_USER  -> "ROLE_USER"
ROLE_ADMIN -> "ROLE_ADMIN"
```

```javascript
let visibleCount = 0;
```

意味:
- 画面に表示する行数を数えるための変数
- 最初はまだ1件も数えていないため、`0` にする
- 条件に合う行が見つかるたびに `1` ずつ増やす

## ユーザー行を1行ずつ確認する

```javascript
rows.forEach((row) => {
```

`rows` には、ユーザー行が3件入っています。

```text
1件目: tanaka の行
2件目: suzuki の行
3件目: admin の行
```

`forEach` によって、1行ずつ `row` に入れて処理します。

## data-* から判定用の値を取り出す

```javascript
const username = (row.dataset.username || "").toLowerCase();
```

HTMLの次の部分を読んでいます。

```html
<tr class="js-user-row" data-username="tanaka" data-role="ROLE_USER">
```

`row.dataset.username` で、`data-username` の値を取得できます。

```text
data-username="tanaka" -> row.dataset.username は "tanaka"
```

`|| ""` は、値が取れなかった場合の保険です。  
もし `data-username` がなければ、空文字 `""` として扱います。

最後に `toLowerCase()` で小文字にそろえています。

```javascript
const role = row.dataset.role || "";
```

HTMLの `data-role` を取得しています。

```text
data-role="ROLE_USER"  -> row.dataset.role は "ROLE_USER"
data-role="ROLE_ADMIN" -> row.dataset.role は "ROLE_ADMIN"
```

## ユーザー名条件を判定する

```javascript
const matchedKeyword = keyword === "" || username.includes(keyword);
```

これは、次の意味です。

```text
検索キーワードが空ならOK
または
ユーザー名にキーワードが含まれていればOK
```

例えば `keyword` が `"ta"` の場合:

```text
tanaka -> "ta" を含むため true
suzuki -> "ta" を含まないため false
admin  -> "ta" を含まないため false
```

`keyword === ""` がある理由:
- 検索欄が空なら、ユーザー名では絞り込まない
- つまり、名前条件は全員OKにする

## ロール条件を判定する

```javascript
const matchedRole = selectedRole === "" || role === selectedRole;
```

これは、次の意味です。

```text
ロール未選択ならOK
または
行のロールが選択されたロールと一致すればOK
```

例えば `selectedRole` が `"ROLE_ADMIN"` の場合:

```text
ROLE_USER  -> false
ROLE_ADMIN -> true
```

`selectedRole === ""` がある理由:
- `すべて` が選択されていると、`selectedRole` は空文字 `""` になる
- 空文字なら、ロールでは絞り込まない
- つまり、ロール条件は全員OKにする

## 表示するかどうかを決める

```javascript
const visible = matchedKeyword && matchedRole;
```

`&&` は「両方trueならtrue」です。

つまり、次の意味です。

```text
ユーザー名条件に合う
かつ
ロール条件にも合う
なら表示する
```

例:

```text
matchedKeyword = true
matchedRole    = true
visible        = true
```

```text
matchedKeyword = true
matchedRole    = false
visible        = false
```

どちらか片方でも `false` なら、`visible` は `false` になります。

## 行を表示・非表示にする

```javascript
row.hidden = !visible;
```

`hidden` は、HTML要素を非表示にするためのプロパティです。

`!` は「反対」を表します。

```text
visible が true  -> !visible は false -> hidden = false -> 表示する
visible が false -> !visible は true  -> hidden = true  -> 非表示にする
```

つまり、条件に合う行は表示し、条件に合わない行は非表示にしています。

## 表示件数を数える

```javascript
if (visible) {
  visibleCount += 1;
}
```

意味:
- `visible` が `true` の行だけ数える
- `visibleCount += 1` は、`visibleCount` を1増やすという意味

例えば、3件中2件が表示対象なら、最後に `visibleCount` は `2` になります。

## 0件メッセージを切り替える

```javascript
noMatchRow.hidden = visibleCount > 0;
```

意味:

```text
表示件数が1件以上なら、0件メッセージを隠す
表示件数が0件なら、0件メッセージを表示する
```

具体的には:

```text
visibleCount = 3 -> visibleCount > 0 は true  -> hidden = true  -> 0件行は非表示
visibleCount = 0 -> visibleCount > 0 は false -> hidden = false -> 0件行は表示
```

## 件数表示を更新する

```javascript
resultText.textContent = `表示件数: ${visibleCount}件 / ${rows.length}件`;
```

`textContent` は、HTML要素の中の文字を変更するプロパティです。

ここでは、次のような文字を画面に表示します。

```text
表示件数: 3件 / 3件
```

意味:
- `visibleCount`: 現在表示している件数
- `rows.length`: ユーザー行の全件数

## 最後に関数を実行する

```javascript
applyFilter();
```

ここで、作った `applyFilter` 関数を実行しています。

この1行があるため、ページを開いた直後に次の処理が行われます。

```text
1. 現在の検索欄の値を読む
2. 現在のロール選択値を読む
3. 行ごとに条件判定する
4. 表示/非表示を設定する
5. 件数表示を更新する
```

## 画面で確認すること

Step 2を実行すると、画面には次の表示が出ます。

```text
表示件数: 3件 / 3件
```

初期状態では、検索欄は空、ロールは `すべて` です。  
そのため、すべてのユーザー行が表示されます。

注意:
- このStepでは、検索欄に入力してもまだ即時には反映されません
- ロールを変更してもまだ即時には反映されません
- 入力や選択に反応させるのはStep 3です

## 理解用の小実験

絞り込みの動きを確認したい場合は、`applyFilter` の中を一時的に次のように変更してみます。

```javascript
const keyword = "ta";
const selectedRole = "";
```

この場合、名前に `"ta"` を含むユーザーだけが表示されます。

確認後は、元のコードに戻してください。

```javascript
const keyword = searchInput.value.trim().toLowerCase();
const selectedRole = roleSelect.value;
```

## このStepのまとめ

Step 2でやっていること:

1. HTMLの必要要素を取得する
2. 必要要素がなければ処理を止める
3. `applyFilter` 関数を作る
4. 検索欄の値を取得する
5. ロール選択欄の値を取得する
6. ユーザー行を1行ずつ確認する
7. 条件に合う行だけ表示する
8. 表示件数を数える
9. 0件メッセージを切り替える
10. 件数表示を更新する

Step 2は、絞り込み処理そのものを作るStepです。  
ただし、ユーザー操作と連動させる処理はまだ書いていません。  
次のStep 3で、入力や選択が変わったときに `applyFilter` を実行するようにします。
