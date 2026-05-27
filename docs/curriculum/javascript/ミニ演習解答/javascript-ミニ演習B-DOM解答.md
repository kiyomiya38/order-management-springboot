# JavaScript ミニ演習B DOM 解答

対象資料: `docs/curriculum/javascript/javascript.md`

## ミニ演習B（DOM）解答

`index.html` と `script.js` を、次のように変更します。

## index.html

```html
<!DOCTYPE html>
<html lang="ja">
  <head>
    <meta charset="UTF-8" />
    <title>JavaScript DOM練習</title>
    <style>
      body {
        font-family: sans-serif;
        background-color: #f5f5f5;
        margin: 40px;
      }

      h1 {
        color: #2c3e50;
      }

      .toolbar {
        margin-bottom: 16px;
      }

      input,
      select,
      button {
        padding: 8px;
        margin-right: 8px;
      }

      table {
        width: 100%;
        border-collapse: collapse;
        background-color: #ffffff;
      }

      th,
      td {
        border: 1px solid #cccccc;
        padding: 10px;
        text-align: left;
      }

      th {
        background-color: #2c3e50;
        color: #ffffff;
      }
    </style>
    <script src="script.js" defer></script>
  </head>
  <body>
    <h1 id="page-title">ユーザー一覧</h1>

    <div class="toolbar">
      <input
        type="text"
        id="user-search-input"
        placeholder="ユーザー名で検索"
      />

      <select id="role-filter-select">
        <option value="">すべてのロール</option>
        <option value="ROLE_ADMIN">ROLE_ADMIN</option>
        <option value="ROLE_USER">ROLE_USER</option>
      </select>

      <select id="status-filter-select">
        <option value="">すべての勤務状態</option>
        <option value="WORKING">WORKING</option>
        <option value="FINISHED">FINISHED</option>
      </select>
    </div>

    <p id="user-filter-result">表示件数: 3件 / 3件（条件なし）</p>

    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>ユーザー名</th>
          <th>ロール</th>
          <th>勤務状態</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr
          class="js-user-row"
          data-username="tanaka"
          data-role="ROLE_ADMIN"
          data-status="WORKING"
        >
          <td>1</td>
          <td>tanaka</td>
          <td>ROLE_ADMIN</td>
          <td>WORKING</td>
          <td>
            <form class="js-delete-form">
              <button type="submit">削除</button>
            </form>
          </td>
        </tr>
        <tr
          class="js-user-row"
          data-username="suzuki"
          data-role="ROLE_USER"
          data-status="FINISHED"
        >
          <td>2</td>
          <td>suzuki</td>
          <td>ROLE_USER</td>
          <td>FINISHED</td>
          <td>
            <form class="js-delete-form">
              <button type="submit">削除</button>
            </form>
          </td>
        </tr>
        <tr
          class="js-user-row"
          data-username="sato"
          data-role="ROLE_USER"
          data-status="WORKING"
        >
          <td>3</td>
          <td>sato</td>
          <td>ROLE_USER</td>
          <td>WORKING</td>
          <td>
            <form class="js-delete-form">
              <button type="submit">削除</button>
            </form>
          </td>
        </tr>
        <tr id="no-match-row" hidden>
          <td colspan="5">該当するユーザーはいません。</td>
        </tr>
      </tbody>
    </table>
  </body>
</html>
```

## script.js

```javascript
// HTMLの読み込みが終わってから、DOM操作の処理を実行します。
document.addEventListener("DOMContentLoaded", () => {
  setupDeleteConfirmation();
  setupUserTableFilter();
});

// 削除ボタンを押したときに確認ダイアログを表示します。
function setupDeleteConfirmation() {
  const deleteForms = document.querySelectorAll(".js-delete-form");

  deleteForms.forEach((form) => {
    form.addEventListener("submit", (event) => {
      const result = window.confirm("このユーザーを削除しますか？");

      if (!result) {
        event.preventDefault();
      }
    });
  });
}

// ユーザー名・ロール・勤務状態でテーブルを絞り込みます。
function setupUserTableFilter() {
  const searchInput = document.getElementById("user-search-input");
  const roleSelect = document.getElementById("role-filter-select");
  const statusSelect = document.getElementById("status-filter-select");
  const resultText = document.getElementById("user-filter-result");
  const noMatchRow = document.getElementById("no-match-row");
  const userRows = Array.from(document.querySelectorAll(".js-user-row"));

  if (
    !(searchInput instanceof HTMLInputElement) ||
    !(roleSelect instanceof HTMLSelectElement) ||
    !(statusSelect instanceof HTMLSelectElement) ||
    !(resultText instanceof HTMLElement) ||
    !(noMatchRow instanceof HTMLTableRowElement)
  ) {
    return;
  }

  const applyFilter = () => {
    const keyword = searchInput.value.trim().toLowerCase();
    const selectedRole = roleSelect.value;
    const selectedStatus = statusSelect.value;
    let visibleCount = 0;

    userRows.forEach((row) => {
      const username = (row.dataset.username || "").toLowerCase();
      const role = row.dataset.role || "";
      const status = row.dataset.status || "";

      const matchedKeyword = keyword === "" || username.includes(keyword);
      const matchedRole = selectedRole === "" || role === selectedRole;
      const matchedStatus = selectedStatus === "" || status === selectedStatus;
      const isVisible = matchedKeyword && matchedRole && matchedStatus;

      row.hidden = !isVisible;

      if (isVisible) {
        visibleCount += 1;
      }
    });

    noMatchRow.hidden = visibleCount > 0;

    const conditions = [];

    if (selectedRole !== "") {
      conditions.push(selectedRole);
    }

    if (selectedStatus !== "") {
      conditions.push(selectedStatus);
    }

    const conditionText =
      conditions.length === 0 ? "条件なし" : conditions.join(", ");

    resultText.textContent = `表示件数: ${visibleCount}件 / ${userRows.length}件（${conditionText}）`;
  };

  searchInput.addEventListener("input", applyFilter);
  roleSelect.addEventListener("change", applyFilter);
  statusSelect.addEventListener("change", applyFilter);

  applyFilter();
}
```

## 確認ポイント

- `data-status` を使って、勤務状態をJavaScriptから参照できている
- ユーザー名・ロール・勤務状態の3条件で絞り込みできている
- 条件に合うデータがない場合に、0件メッセージが表示される
- 現在の条件が `表示件数: 1件 / 3件（ROLE_ADMIN, WORKING）` のように表示される
