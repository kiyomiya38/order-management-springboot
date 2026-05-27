# HTML / CSS ミニ演習A（HTML）解答

対象資料: `docs/curriculum/html_css/html_css.md`

## ミニ演習A（HTML）解答

変更内容:
1. `<header>` を `<div>` に置き換える
2. `<section class="panel">` を `<article class="panel">` に置き換える
3. フォームに `input type="date"` を追加する
4. テーブルに「状態」列を追加する
5. `ul` の項目を1つ削除する

`index.html`:
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>タグ演習</title>
  <link rel="stylesheet" href="styles.css" />
</head>
<body>
  <div class="container">
    <div>
      <h1>勤怠管理（HTML/CSS練習）</h1>
      <p class="subtitle">このページはタグを理解するための練習用です。</p>
    </div>

    <article class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <span class="status-badge">未出勤</span>
      </div>
      <p>日付: 2026-02-05</p>
      <p>出勤時刻: -</p>
      <p>退勤時刻: -</p>
    </article>

    <article class="panel">
      <h2>簡単なフォーム</h2>
      <label>
        名前:
        <input type="text" name="username" />
      </label>
      <label>
        対象日:
        <input type="date" name="workDate" />
      </label>
      <button>送信</button>
    </article>

    <h2>やること</h2>
    <ul>
      <li>出勤する</li>
      <li>退勤する</li>
    </ul>

    <h2>勤怠サンプル一覧</h2>
    <table>
      <tr>
        <th>日付</th>
        <th>出勤</th>
        <th>退勤</th>
        <th>状態</th>
      </tr>
      <tr>
        <td>2026-02-05</td>
        <td>09:00</td>
        <td>18:00</td>
        <td>退勤済み</td>
      </tr>
    </table>
  </div>
</body>
</html>
```

確認ポイント:
- `<header>` を単純な `<div>` に変えると、HTML上の意味は「ヘッダー領域」ではなく汎用の箱になる。
- CSSに `header { ... }` がある場合、`<div>` へ置き換えるとそのスタイルは当たらなくなる。
- `<section>` と `<article>` はどちらもまとまりを表すが、`article` は「単体でも意味が通じる内容」に使う。
- `input type="date"` はブラウザによって日付入力UIになる。
- `table` は `th` と `td` の数を行ごとに揃える。
