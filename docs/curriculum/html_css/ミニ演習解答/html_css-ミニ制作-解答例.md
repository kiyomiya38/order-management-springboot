# HTML / CSS ミニ制作 解答例

対象資料: `docs/curriculum/html_css/html_css.md`

## ミニ制作（30〜45分）解答例

要件:
- 「日付 / 出勤時刻 / 退勤時刻」を表示する
- 「状態バッジ」を表示する
- 「出勤」「退勤」ボタンのダミーを置く
- 一覧（表）を1行だけ用意する

### `index.html`
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>勤怠トップ画面</title>
  <link rel="stylesheet" href="styles.css" />
</head>
<body>
  <div class="container">
    <header class="page-header">
      <div>
        <h1>勤怠トップ画面</h1>
        <p class="subtitle">HTML/CSSだけで作る静的な勤怠画面です。</p>
      </div>
      <span class="status-badge">未出勤</span>
    </header>

    <section class="panel">
      <div class="panel-header">
        <h2>本日の勤怠</h2>
        <span class="date-label">2026-02-05</span>
      </div>

      <div class="summary-grid">
        <div class="summary-item">
          <span class="summary-label">日付</span>
          <strong>2026-02-05</strong>
        </div>
        <div class="summary-item">
          <span class="summary-label">出勤時刻</span>
          <strong>-</strong>
        </div>
        <div class="summary-item">
          <span class="summary-label">退勤時刻</span>
          <strong>-</strong>
        </div>
      </div>

      <div class="actions">
        <button type="button">出勤</button>
        <button type="button" class="secondary-button">退勤</button>
      </div>
    </section>

    <section class="panel">
      <h2>勤怠一覧</h2>
      <table>
        <tr>
          <th>日付</th>
          <th>出勤</th>
          <th>退勤</th>
          <th>状態</th>
        </tr>
        <tr>
          <td>2026-02-04</td>
          <td>09:00</td>
          <td>18:00</td>
          <td>退勤済み</td>
        </tr>
      </table>
    </section>
  </div>
</body>
</html>
```

### `styles.css`
```css
:root {
  --bg: #f6f7fb;
  --panel: #ffffff;
  --text: #202124;
  --muted: #6b7280;
  --accent: #0ea5e9;
  --border: #e5e7eb;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: "Segoe UI", Tahoma, sans-serif;
  color: var(--text);
  background: var(--bg);
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

h1,
h2 {
  margin: 0;
}

.subtitle {
  color: var(--muted);
  margin: 8px 0 0;
}

.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  background: #e0f2fe;
  color: #0369a1;
  font-size: 12px;
}

.date-label,
.summary-label {
  color: var(--muted);
  font-size: 14px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.summary-item {
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 12px;
  background: #f9fafb;
}

.summary-item strong {
  display: block;
  margin-top: 6px;
}

.actions {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

button {
  padding: 8px 12px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.secondary-button {
  background: #64748b;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  border-bottom: 1px solid var(--border);
  text-align: left;
  padding: 8px;
}
```

確認ポイント:
- `summary-grid` で「日付 / 出勤時刻 / 退勤時刻」を横並びにしている。
- `status-badge` で状態を目立たせている。
- ボタンはまだJavaScriptとつながっていないため、押しても処理は実行されない。
- 表は1行だけだが、`tr` を増やせば履歴を増やせる。
