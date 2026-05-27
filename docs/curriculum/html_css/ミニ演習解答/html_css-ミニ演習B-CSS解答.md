# HTML / CSS ミニ演習B（CSS）解答

対象資料: `docs/curriculum/html_css/html_css.md`

## ミニ演習B（CSS）解答

変更内容:
1. 背景色（`--bg`）を変更
2. ボタン色（`--accent`）を変更
3. `.panel` の角丸を `12px` に変更
4. `.status-badge` の背景色を変更

変更例:
```css
:root {
  --bg: #eef2ff;
  --panel: #ffffff;
  --text: #202124;
  --muted: #6b7280;
  --accent: #2563eb;
  --border: #e5e7eb;
}

.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  background: #dcfce7;
  color: #166534;
  font-size: 12px;
}

button {
  margin-top: 8px;
  padding: 8px 12px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
```

確認ポイント:
- `--bg` を変えると `body` の `background: var(--bg);` に反映される。
- `--accent` を変えると `button` の背景色が変わる。
- `.panel` の `border-radius` を大きくすると、パネルの角丸が強くなる。
- `.status-badge` の背景色と文字色は、セットで見やすい組み合わせにする。
