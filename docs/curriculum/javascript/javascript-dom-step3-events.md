# JavaScript DOM Step 3: イベント連動 解説

対象資料: `docs/curriculum/javascript/javascript.md`

## このStepの目的

Step 3では、Step 2で作った `applyFilter` 関数を、ユーザー操作と連動させます。

Step 2では、ページを開いた直後に1回だけ `applyFilter()` を実行していました。  
そのため、検索欄に文字を入力しても、入力した瞬間には絞り込みが動きませんでした。

Step 3では、次の操作が起きたときに `applyFilter` を実行します。

- 検索欄に文字が入力されたとき
- ロール選択欄の値が変わったとき

これにより、画面操作に合わせてユーザー一覧が絞り込まれるようになります。

## Step 3のコード

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

## Step 2から増えた部分

Step 3で新しく増えた重要な行は、次の2行です。

```javascript
searchInput.addEventListener("input", applyFilter);
roleSelect.addEventListener("change", applyFilter);
```

この2行によって、ユーザー操作と `applyFilter` がつながります。

## addEventListenerとは

```javascript
searchInput.addEventListener("input", applyFilter);
```

`addEventListener` は、HTML要素にイベント処理を登録する命令です。

読み方:

```text
searchInput で input イベントが起きたら、applyFilter を実行する
```

分解すると、次の意味になります。

```javascript
searchInput
```

対象になるHTML要素です。  
ここでは、ユーザー名検索欄です。

```javascript
"input"
```

どのイベントを待つかを指定しています。  
`input` イベントは、入力欄の文字が変わるたびに発生します。

```javascript
applyFilter
```

イベントが起きたときに実行する関数です。

注意:

```javascript
searchInput.addEventListener("input", applyFilter);
```

ここでは `applyFilter` と書きます。  
`applyFilter()` とは書きません。

理由:
- `applyFilter` は「あとで実行する関数を渡す」
- `applyFilter()` は「今すぐ実行する」

イベント登録では、後で実行してほしいので `applyFilter` と書きます。

## inputイベント

```javascript
searchInput.addEventListener("input", applyFilter);
```

これは、次の意味です。

```text
検索欄に文字が入力されるたびに、applyFilterを実行する
```

例えば、検索欄に `ta` と入力する場合:

```text
t を入力 -> inputイベント発生 -> applyFilter実行
a を入力 -> inputイベント発生 -> applyFilter実行
```

そのため、入力中にすぐ一覧が絞り込まれます。

## changeイベント

```javascript
roleSelect.addEventListener("change", applyFilter);
```

これは、次の意味です。

```text
ロール選択欄の選択値が変わったら、applyFilterを実行する
```

例えば、プルダウンで `ROLE_ADMIN` を選ぶと:

```text
選択値が ROLE_ADMIN に変わる
changeイベント発生
applyFilter実行
ROLE_ADMIN の行だけ表示される
```

`select` では、選択が変わったタイミングで `change` イベントを使うことが多いです。

## 最後の applyFilter()

```javascript
applyFilter();
```

この行は、ページを開いた直後に1回だけ絞り込み処理を実行します。

この行がある理由:
- 初期表示時に件数を表示するため
- `表示件数: 3件 / 3件` を最初から出すため
- 0件メッセージの表示状態を初期化するため

この行がない場合:
- ページを開いた直後は件数表示が空のままになる
- 入力や選択を変更したときに初めて件数が表示される

## Step 3での処理の流れ

ページを開いた直後:

```text
1. HTMLの読み込みが終わる
2. 必要な要素を取得する
3. applyFilter関数を作る
4. inputイベントを検索欄に登録する
5. changeイベントをロール選択欄に登録する
6. applyFilter() を1回実行する
7. 表示件数が出る
```

検索欄に入力したとき:

```text
1. 検索欄の文字が変わる
2. inputイベントが発生する
3. applyFilterが実行される
4. 検索欄の値を読み直す
5. ユーザー行を1行ずつ判定する
6. 条件に合う行だけ表示する
7. 表示件数を更新する
```

ロールを変更したとき:

```text
1. selectの選択値が変わる
2. changeイベントが発生する
3. applyFilterが実行される
4. 選択されたロールを読み直す
5. ユーザー行を1行ずつ判定する
6. 条件に合う行だけ表示する
7. 表示件数を更新する
```

## 画面で確認すること

Step 3を実行したら、次を確認します。

1. 初期表示で `表示件数: 3件 / 3件` が出る
2. 検索欄に `ta` と入力する
3. `tanaka` だけが表示される
4. 件数が `表示件数: 1件 / 3件` になる
5. 検索欄を空に戻す
6. ロールで `ROLE_ADMIN` を選ぶ
7. `admin` だけが表示される
8. 件数が `表示件数: 1件 / 3件` になる
9. 一致しない条件にすると、0件メッセージが表示される

## 理解用の小実験

イベントの違いを確認するために、次の行を一時的にコメントアウトしてみます。

```javascript
// searchInput.addEventListener("input", applyFilter);
```

この状態で検索欄に文字を入力しても、一覧は変わりません。  
これは、検索欄の入力と `applyFilter` のつながりを外したためです。

確認後は、元に戻してください。

```javascript
searchInput.addEventListener("input", applyFilter);
```

次に、ロール選択側も一時的にコメントアウトできます。

```javascript
// roleSelect.addEventListener("change", applyFilter);
```

この状態では、プルダウンを変更しても一覧は変わりません。  
確認後は、元に戻してください。

```javascript
roleSelect.addEventListener("change", applyFilter);
```

## よくあるミス

- `applyFilter` ではなく `applyFilter()` と書いてしまう
- `"input"` のスペルを間違える
- `"change"` のスペルを間違える
- `searchInput` や `roleSelect` の取得に失敗している
- 最後の `applyFilter();` を書き忘れて、初期表示の件数が出ない

## このStepのまとめ

Step 3でやっていること:

1. Step 2と同じ絞り込み関数を用意する
2. 検索欄の `input` イベントに `applyFilter` を登録する
3. ロール選択欄の `change` イベントに `applyFilter` を登録する
4. ページ表示直後にも `applyFilter()` を1回実行する

Step 2では、絞り込み関数を作っただけでした。  
Step 3では、その関数をユーザー操作とつなげています。

つまり、Step 3は「画面操作に反応するようにするStep」です。
