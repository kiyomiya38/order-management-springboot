# Day1: 気分ルーレットをゼロから作る（自分でファイル作成）

## ロードマップ接続
- 対象: `java-web-1week-beginner-blueprint.md` の Day1
- このDayで行うこと: HTML / CSS / JavaScript だけで「押したら変わる」ミニWebアプリを完成させる
- 到達点: 自分で3ファイルを作成し、ランダム表示 + 履歴表示まで動かせる

## このDayの方針
- 最初から完成コードを貼らない
- Stepごとに動作確認しながら進む
- 「なぜこの文法が必要か」を実装の中で学ぶ

---

## 1. 作業フォルダ
```bash
cd ~/order-management-springboot
mkdir -p ~/order-management-springboot/practice/day01-mood-roulette
cd ~/order-management-springboot/practice/day01-mood-roulette
```

---

## 2. Day1で使うファイル（自分で新規作成）
- `index.html`
  - 画面の骨組み（見出し、ボタン、結果表示、履歴）
- `styles.css`
  - 色、余白、カード見た目
- `app.js`
  - クリック処理、ランダム選択、履歴管理

---

## 3. Step1: HTMLを作る（画面の骨組み）

### このStepで学ぶ文法
- HTMLの基本構造: `<!doctype html>`, `<head>`, `<body>`
- 要素に名前を付ける: `id` と `class`
- ボタンとリスト: `<button>`, `<ul>`, `<li>`

### 3-1. `index.html` を作成
作成ファイル:
- `index.html`

```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Day1 気分ルーレット</title>
  <link rel="stylesheet" href="./styles.css" />
</head>
<body>
  <main class="page">
    <header class="hero">
      <p class="tag">Day1 Hands-on</p>
      <h1>気分ルーレット</h1>
      <p class="lead">ボタンを押して、今日のおすすめ行動を1つ選ぼう。</p>
    </header>

    <section id="resultCard" class="result-card theme-neutral">
      <p class="label">今日の結果</p>
      <h2 id="resultTitle">まだ回していません</h2>
      <p id="resultDescription" class="description">「ルーレットを回す」を押してください。</p>
    </section>

    <section class="actions">
      <button id="spinButton" class="btn btn-primary" type="button">ルーレットを回す</button>
      <button id="resetButton" class="btn btn-ghost" type="button">履歴をクリア</button>
    </section>

    <section class="history-panel">
      <h3>履歴（最新5件）</h3>
      <ul id="historyList" class="history-list"></ul>
    </section>
  </main>

  <script src="./app.js"></script>
</body>
</html>
```

### 3-2. 動作確認
`index.html` をブラウザで開いて、以下が表示されればOK。

- 「気分ルーレット」の見出し
- 2つのボタン
- 履歴エリア

---

## 4. Step2: CSSを作る（見た目を整える）

### このStepで学ぶ文法
- クラスセレクタ: `.panel`, `.btn`
- 変数: `:root` と `var(--bg)`
- レイアウト: `display: flex`

### 4-1. `styles.css` を作成
作成ファイル:
- `styles.css`

```css
:root {
  --bg: #f5f3eb;
  --panel: #ffffff;
  --text: #1f2937;
  --muted: #6b7280;
  --line: #e5e7eb;
}

* { box-sizing: border-box; }

body {
  margin: 0;
  font-family: "Yu Gothic UI", "Segoe UI", sans-serif;
  background: linear-gradient(160deg, #f7f6ef 0%, #eef6f8 100%);
  color: var(--text);
}

.page {
  max-width: 760px;
  margin: 0 auto;
  padding: 28px 20px 40px;
}

.hero { margin-bottom: 16px; }

.tag {
  display: inline-block;
  margin: 0 0 8px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  background: #dbeafe;
  color: #1e3a8a;
}

h1 { margin: 0 0 8px; font-size: 30px; }
.lead { margin: 0; color: var(--muted); }

.result-card {
  border: 1px solid var(--line);
  border-radius: 14px;
  padding: 18px;
  background: var(--panel);
  margin-bottom: 14px;
  transition: background-color 0.25s ease, border-color 0.25s ease;
}

.label {
  margin: 0 0 6px;
  font-size: 12px;
  color: var(--muted);
}

.result-card h2 { margin: 0 0 8px; }
.description { margin: 0; }

.actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.btn {
  border: none;
  border-radius: 8px;
  padding: 10px 14px;
  cursor: pointer;
  font-weight: 600;
}

.btn-primary { background: #0f766e; color: #fff; }
.btn-primary:hover { opacity: 0.92; }
.btn-ghost { background: #e5e7eb; color: #111827; }
.btn-ghost:hover { background: #d1d5db; }

.history-panel {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 14px;
  background: #fff;
}

.history-panel h3 { margin-top: 0; margin-bottom: 10px; }
.history-list { margin: 0; padding-left: 18px; line-height: 1.8; }

.theme-neutral { background: #ffffff; border-color: #e5e7eb; }
.theme-refresh { background: #e0f2fe; border-color: #7dd3fc; }
.theme-focus { background: #ecfccb; border-color: #bef264; }
.theme-chill { background: #f3e8ff; border-color: #d8b4fe; }
.theme-social { background: #ffedd5; border-color: #fdba74; }
```

### 4-2. 動作確認
ブラウザを再読み込みして、カード表示やボタン色が反映されればOK。

---

## 5. Step3: JavaScriptを作る（ランダム表示）

### このStepで学ぶ文法
- 変数宣言: `const`
- 配列とオブジェクト: `[{ title: "...", ... }]`
- 関数: `function`
- イベント: `addEventListener`

### 5-1. `app.js` を作成（まずは最小実装）
作成ファイル:
- `app.js`

```javascript
const ACTIONS = [
  { title: "5分だけ散歩する", description: "外の空気を吸って頭を切り替える。", theme: "theme-refresh" },
  { title: "机を2分だけ片づける", description: "視界を整えると集中しやすくなる。", theme: "theme-focus" },
  { title: "好きな音楽を1曲聴く", description: "短時間で気分をリセットする。", theme: "theme-chill" },
  { title: "誰かに一言メッセージする", description: "軽いやり取りで気持ちを前向きにする。", theme: "theme-social" },
  { title: "水を1杯飲む", description: "体を整えてから次の作業へ進む。", theme: "theme-refresh" }
];

const resultCard = document.getElementById("resultCard");
const resultTitle = document.getElementById("resultTitle");
const resultDescription = document.getElementById("resultDescription");
const spinButton = document.getElementById("spinButton");

function getRandomAction() {
  const index = Math.floor(Math.random() * ACTIONS.length);
  return ACTIONS[index];
}

function showAction(action) {
  resultTitle.textContent = action.title;
  resultDescription.textContent = action.description;
}

spinButton.addEventListener("click", () => {
  const action = getRandomAction();
  showAction(action);
});
```

### 5-2. 動作確認
- 「ルーレットを回す」を押すたびに、タイトルと説明が変わればOK

---

## 6. Step4: テーマ色切り替えを追加する

### このStepで学ぶ文法
- 配列の展開: `...THEME_CLASSES`
- クラス操作: `classList.remove`, `classList.add`

### 6-1. `app.js` を追記
`const spinButton = ...` の下に追加:

```javascript
const THEME_CLASSES = [
  "theme-neutral",
  "theme-refresh",
  "theme-focus",
  "theme-chill",
  "theme-social"
];
```

`showAction(action)` を置き換え:

```javascript
function showAction(action) {
  resultTitle.textContent = action.title;
  resultDescription.textContent = action.description;

  resultCard.classList.remove(...THEME_CLASSES);
  resultCard.classList.add(action.theme);
}
```

### 6-2. 動作確認
- ボタンを押すたびに、結果カードの背景色が変わればOK

---

## 7. Step5: 履歴機能を追加する

### このStepで学ぶ文法
- 配列先頭追加: `unshift`
- 繰り返し: `forEach`
- DOM要素生成: `createElement`, `appendChild`

### 7-1. `app.js` を追記
`const spinButton = ...` の下に追加:

```javascript
const historyList = document.getElementById("historyList");
const history = [];
```

`showAction` の下に追加:

```javascript
function appendHistory(action) {
  history.unshift(action.title);
  if (history.length > 5) {
    history.length = 5;
  }
}

function renderHistory() {
  historyList.innerHTML = "";
  history.forEach((item) => {
    const li = document.createElement("li");
    li.textContent = item;
    historyList.appendChild(li);
  });
}
```

`spinButton.addEventListener` を置き換え:

```javascript
spinButton.addEventListener("click", () => {
  const action = getRandomAction();
  showAction(action);
  appendHistory(action);
  renderHistory();
});
```

### 7-2. 動作確認
- 3回回すと履歴が3件になる
- 新しい結果が上に表示される

---

## 8. Step6: リセット機能を追加する

### このStepで学ぶ文法
- 配列の初期化: `history.length = 0`
- 関数分割: `resetResult`

### 8-1. `app.js` を追記
`const spinButton = ...` の下に追加:

```javascript
const resetButton = document.getElementById("resetButton");
```

`renderHistory` の下に追加:

```javascript
function resetResult() {
  resultTitle.textContent = "まだ回していません";
  resultDescription.textContent = "「ルーレットを回す」を押してください。";
  resultCard.classList.remove(
    "theme-refresh",
    "theme-focus",
    "theme-chill",
    "theme-social"
  );
  resultCard.classList.add("theme-neutral");
}
```

`spinButton` のイベント定義の下に追加:

```javascript
resetButton.addEventListener("click", () => {
  history.length = 0;
  renderHistory();
  resetResult();
});
```

### 8-2. 動作確認
- 「履歴をクリア」で履歴が空になる
- タイトル・説明・カード色が初期状態へ戻る

---

## 9. Day1コードの説明（入力・処理・出力）

1. `getRandomAction()`
- 入力: なし
- 処理: `ACTIONS` 配列の中からランダムに1件選ぶ
- 出力: 行動オブジェクト1件

2. `showAction(action)`
- 入力: 選ばれた行動オブジェクト
- 処理: タイトル・説明・テーマ色をDOMへ反映
- 出力: 画面更新

3. `appendHistory(action)` + `renderHistory()`
- 入力: 行動オブジェクト
- 処理: 履歴配列を更新し、`<ul>` を再描画
- 出力: 履歴リスト表示（最大5件）

---

## 10. よくあるエラー

- `Cannot read properties of null`
  - `index.html` 側の `id` 名と `app.js` 側の `getElementById(...)` が一致しているか確認

- ボタンを押しても何も起きない
  - `app.js` の保存漏れ
  - `index.html` の最後に `<script src="./app.js"></script>` があるか確認

- スタイルが反映されない
  - `<link rel="stylesheet" href="./styles.css" />` の記述漏れ
  - ファイル名の綴りミス（`style.css` と `styles.css` の不一致）

---

## 11. 最終確認（Day1完了条件）

- 3ファイルを自分で新規作成できた
- クリックごとに結果がランダム表示される
- 結果に応じてカード色が切り替わる
- 履歴が最大5件で表示される
- リセットで初期状態に戻る

## 12. 次Dayへの引き継ぎ

- Day2では、この「配列の状態更新 -> 再描画」を使って入力フォーム付きアプリへ発展させる

