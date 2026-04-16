# Day2: ミニ習慣トラッカーを作る（状態更新と再描画）

## ロードマップ接続
- 対象: `java-web-1week-beginner-blueprint.md` の Day2
- このDayで行うこと: 入力フォーム付きの記録アプリを作り、状態管理を学ぶ
- 到達点: 「追加 / 完了切替 / 削除 / フィルタ」が動く小アプリを完成させる

## Day1とのつながり
- Day1で学んだ「クリックでDOM更新」を、Day2では「配列の状態を更新して再描画」に発展させる
- Day3のJava学習に向けて、`配列 + オブジェクト + 関数分割` を定着させる

---

## 1. 作業フォルダ
```bash
cd ~/order-management-springboot
mkdir -p ~/order-management-springboot/practice/day02-habit-tracker
cd ~/order-management-springboot/practice/day02-habit-tracker
```

---

## 2. Day2で使うファイル（自分で新規作成）
- `index.html`
  - 入力フォーム、フィルタ、一覧表示の骨組み
- `styles.css`
  - カードUI、完了状態の見た目
- `app.js`
  - 状態（配列）管理、追加、完了切替、削除、フィルタ

---

## 3. Step1: HTMLを作る（入力と一覧の枠）

### このStepで学ぶ文法
- フォーム送信: `<form>`
- 入力部品: `<input>`
- カスタム属性: `data-filter`

### 3-1. `index.html` を作成
作成ファイル:
- `index.html`

```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Day2 ミニ習慣トラッカー</title>
  <link rel="stylesheet" href="./styles.css" />
</head>
<body>
  <main class="page">
    <header class="hero">
      <p class="tag">Day2 Hands-on</p>
      <h1>ミニ習慣トラッカー</h1>
      <p class="lead">習慣を追加して、今日の進捗を見える化しよう。</p>
    </header>

    <section class="panel">
      <form id="habitForm" class="row">
        <input id="habitInput" type="text" placeholder="例: 朝に5分ストレッチ" maxlength="40" required />
        <button type="submit" class="btn btn-primary">追加</button>
      </form>
    </section>

    <section class="panel">
      <div class="toolbar">
        <div class="filters">
          <button class="chip active" data-filter="all" type="button">すべて</button>
          <button class="chip" data-filter="active" type="button">未完了</button>
          <button class="chip" data-filter="done" type="button">完了</button>
        </div>
        <p id="summaryText" class="summary">未完了: 0 / 合計: 0</p>
      </div>

      <ul id="habitList" class="habit-list"></ul>
      <p id="emptyState" class="empty">まだ習慣がありません。まず1つ追加してみましょう。</p>
    </section>
  </main>

  <script src="./app.js"></script>
</body>
</html>
```

### 3-2. 動作確認
`index.html` をブラウザで開き、以下が表示されればOK。
- 入力欄と追加ボタン
- フィルタボタン3つ
- 空状態メッセージ

---

## 4. Step2: CSSを作る（見た目を整える）

### このStepで学ぶ文法
- クラスセレクタ
- `display: flex`
- 状態別クラス（`.done` など）

### 4-1. `styles.css` を作成
作成ファイル:
- `styles.css`

```css
:root {
  --bg: #f4f1ea;
  --panel: #ffffff;
  --text: #1f2937;
  --muted: #6b7280;
  --line: #e5e7eb;
  --accent: #0f766e;
}

* { box-sizing: border-box; }

body {
  margin: 0;
  font-family: "Yu Gothic UI", "Segoe UI", sans-serif;
  color: var(--text);
  background: radial-gradient(circle at top right, #edf7f4, #f8f4eb 55%);
}

.page {
  max-width: 760px;
  margin: 0 auto;
  padding: 28px 20px 40px;
}

.hero { margin-bottom: 14px; }

.tag {
  display: inline-block;
  margin: 0 0 8px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  background: #dbeafe;
  color: #1e3a8a;
}

h1 { margin: 0 0 8px; }
.lead { margin: 0; color: var(--muted); }

.panel {
  border: 1px solid var(--line);
  border-radius: 12px;
  background: var(--panel);
  padding: 14px;
  margin-bottom: 12px;
}

.row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

input[type="text"] {
  flex: 1 1 260px;
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
}

.btn {
  border: none;
  border-radius: 8px;
  padding: 10px 14px;
  cursor: pointer;
  font-weight: 600;
}

.btn-primary {
  background: var(--accent);
  color: #fff;
}

.btn-danger {
  background: #ef4444;
  color: #fff;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.filters {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.chip {
  border: 1px solid #cbd5e1;
  background: #fff;
  color: #334155;
  padding: 6px 10px;
  border-radius: 999px;
  cursor: pointer;
}

.chip.active {
  background: #0f766e;
  border-color: #0f766e;
  color: #fff;
}

.summary {
  margin: 0;
  color: var(--muted);
  font-size: 14px;
}

.habit-list {
  margin: 12px 0 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 8px;
}

.habit-item {
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.habit-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.habit-title.done {
  color: #6b7280;
  text-decoration: line-through;
}

.empty {
  color: var(--muted);
  margin: 12px 2px 0;
}
```

### 4-2. 動作確認
ブラウザ再読み込みで、カードUIになればOK。

---

## 5. Step3: JavaScript最小実装（追加 + 再描画）

### このStepで学ぶ文法
- 配列に追加: `unshift`
- フォーム処理: `event.preventDefault()`
- 描画関数分割: `renderHabits()`

### 5-1. `app.js` を作成
作成ファイル:
- `app.js`

```javascript
const habits = [];
let nextId = 1;

const habitForm = document.getElementById("habitForm");
const habitInput = document.getElementById("habitInput");
const habitList = document.getElementById("habitList");
const emptyState = document.getElementById("emptyState");
const summaryText = document.getElementById("summaryText");

function addHabit(title) {
  habits.unshift({
    id: nextId++,
    title: title,
    done: false
  });
}

function renderHabits() {
  habitList.innerHTML = "";

  habits.forEach((habit) => {
    const li = document.createElement("li");
    li.className = "habit-item";
    li.innerHTML = `
      <div class="habit-left">
        <input type="checkbox" disabled />
        <span class="habit-title">${habit.title}</span>
      </div>
      <button class="btn btn-danger" disabled>削除</button>
    `;
    habitList.appendChild(li);
  });

  emptyState.style.display = habits.length === 0 ? "block" : "none";
  summaryText.textContent = `未完了: ${habits.length} / 合計: ${habits.length}`;
}

habitForm.addEventListener("submit", (event) => {
  event.preventDefault();

  const title = habitInput.value.trim();
  if (title === "") {
    return;
  }

  addHabit(title);
  habitInput.value = "";
  renderHabits();
});

renderHabits();
```

### 5-2. 動作確認
- 入力して「追加」を押すと一覧に増える
- 入力が空なら追加されない

---

## 6. Step4: 完了切替と削除を実装する

### このStepで学ぶ文法
- 要素検索: `find`
- 条件反転: `habit.done = !habit.done`
- 配列除外: `filter`

### 6-1. `app.js` を置き換え
`app.js` 全体を以下へ置き換える。

```javascript
const habits = [];
let nextId = 1;
let currentFilter = "all";

const habitForm = document.getElementById("habitForm");
const habitInput = document.getElementById("habitInput");
const habitList = document.getElementById("habitList");
const emptyState = document.getElementById("emptyState");
const summaryText = document.getElementById("summaryText");
const filterButtons = document.querySelectorAll("[data-filter]");

function addHabit(title) {
  habits.unshift({
    id: nextId++,
    title: title,
    done: false
  });
}

function toggleHabit(id) {
  const habit = habits.find((item) => item.id === id);
  if (!habit) {
    return;
  }
  habit.done = !habit.done;
}

function removeHabit(id) {
  const next = habits.filter((item) => item.id !== id);
  habits.length = 0;
  habits.push(...next);
}

function getVisibleHabits() {
  if (currentFilter === "active") {
    return habits.filter((item) => !item.done);
  }
  if (currentFilter === "done") {
    return habits.filter((item) => item.done);
  }
  return habits;
}

function renderSummary() {
  const total = habits.length;
  const active = habits.filter((item) => !item.done).length;
  summaryText.textContent = `未完了: ${active} / 合計: ${total}`;
}

function renderHabits() {
  const visibleHabits = getVisibleHabits();
  habitList.innerHTML = "";

  visibleHabits.forEach((habit) => {
    const li = document.createElement("li");
    li.className = "habit-item";

    const left = document.createElement("div");
    left.className = "habit-left";

    const checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.checked = habit.done;
    checkbox.addEventListener("change", () => {
      toggleHabit(habit.id);
      render();
    });

    const title = document.createElement("span");
    title.className = "habit-title";
    if (habit.done) {
      title.classList.add("done");
    }
    title.textContent = habit.title;

    left.appendChild(checkbox);
    left.appendChild(title);

    const removeButton = document.createElement("button");
    removeButton.className = "btn btn-danger";
    removeButton.type = "button";
    removeButton.textContent = "削除";
    removeButton.addEventListener("click", () => {
      removeHabit(habit.id);
      render();
    });

    li.appendChild(left);
    li.appendChild(removeButton);
    habitList.appendChild(li);
  });

  emptyState.style.display = visibleHabits.length === 0 ? "block" : "none";
}

function renderFilterButtons() {
  filterButtons.forEach((button) => {
    if (button.dataset.filter === currentFilter) {
      button.classList.add("active");
    } else {
      button.classList.remove("active");
    }
  });
}

function render() {
  renderHabits();
  renderSummary();
  renderFilterButtons();
}

habitForm.addEventListener("submit", (event) => {
  event.preventDefault();

  const title = habitInput.value.trim();
  if (title === "") {
    return;
  }

  addHabit(title);
  habitInput.value = "";
  render();
});

filterButtons.forEach((button) => {
  button.addEventListener("click", () => {
    currentFilter = button.dataset.filter;
    render();
  });
});

render();
```

### 6-2. 動作確認
- チェックで完了/未完了が切り替わる
- 削除ボタンで1件消える
- フィルタボタンで表示が切り替わる

---

## 7. Step5: 仕組み理解（入力・処理・出力）

1. 入力
- フォーム送信（追加）
- チェックボックス変更（完了切替）
- 削除ボタン押下（削除）
- フィルタボタン押下（表示切替）

2. 処理
- `habits` 配列を更新
- `render()` で再描画

3. 出力
- 一覧表示の更新
- 集計テキスト（未完了/合計）の更新
- 空状態表示のON/OFF

---

## 8. よくあるエラー

- `querySelectorAll(...) is not defined` のようなエラー
  - 綴りミス（`querySelecotrAll` など）を確認

- ボタン押下で反応しない
  - `type="button"` の付け忘れでフォーム送信になっていないか確認

- チェックしても戻る
  - `toggleHabit` の後に `render()` を呼んでいるか確認

---

## 9. 最終確認（Day2完了条件）

- 追加、完了切替、削除が動く
- 「すべて / 未完了 / 完了」で表示切替できる
- 未完了件数と合計件数が正しく表示される
- コードを「入力 -> 状態更新 -> 再描画」で説明できる

## 10. Day3への引き継ぎ

- Day3では、この状態管理の考え方をJavaのコンソールアプリへ移植する
- `配列 + オブジェクト + 関数分割` がそのままJavaの `List + class + method` に対応する

