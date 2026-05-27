# JavaScript ミニ制作 解答例

対象資料: `docs/curriculum/javascript/javascript.md`

## ミニ制作（30〜45分）解答例

この解答例では、サーバーへ送信せず、ブラウザ上だけでユーザーの追加・検索・絞り込み・削除を行います。

## index.html

```html
<!DOCTYPE html>
<html lang="ja">
  <head>
    <meta charset="UTF-8" />
    <title>ユーザー管理ミニ画面</title>
    <style>
      body {
        font-family: sans-serif;
        background-color: #f5f5f5;
        margin: 40px;
      }

      h1 {
        color: #2c3e50;
      }

      .section {
        margin-bottom: 24px;
      }

      .form-row,
      .toolbar {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
        align-items: center;
      }

      input,
      select,
      button {
        padding: 8px;
      }

      button {
        cursor: pointer;
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

      .empty-message {
        text-align: center;
        color: #666666;
      }
    </style>
    <script src="script.js" defer></script>
  </head>
  <body>
    <h1>ユーザー管理ミニ画面</h1>

    <section class="section">
      <h2>ユーザー追加</h2>
      <form id="user-create-form" class="form-row">
        <input
          type="text"
          id="new-username-input"
          placeholder="ユーザー名"
          autocomplete="off"
        />

        <select id="new-role-select">
          <option value="ROLE_USER">ROLE_USER</option>
          <option value="ROLE_ADMIN">ROLE_ADMIN</option>
        </select>

        <button type="submit">追加</button>
      </form>
    </section>

    <section class="section">
      <h2>検索・絞り込み</h2>
      <div class="toolbar">
        <input
          type="text"
          id="user-search-input"
          placeholder="ユーザー名で検索"
          autocomplete="off"
        />

        <select id="role-filter-select">
          <option value="">すべてのロール</option>
          <option value="ROLE_ADMIN">ROLE_ADMIN</option>
          <option value="ROLE_USER">ROLE_USER</option>
        </select>
      </div>

      <p id="user-filter-result">表示件数: 0件 / 0件</p>
    </section>

    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>ユーザー名</th>
          <th>ロール</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody id="user-table-body"></tbody>
    </table>
  </body>
</html>
```

## script.js

```javascript
// HTMLの読み込みが終わってから、画面操作の処理を設定します。
document.addEventListener("DOMContentLoaded", () => {
  const createForm = document.getElementById("user-create-form");
  const newUsernameInput = document.getElementById("new-username-input");
  const newRoleSelect = document.getElementById("new-role-select");
  const searchInput = document.getElementById("user-search-input");
  const roleFilterSelect = document.getElementById("role-filter-select");
  const resultText = document.getElementById("user-filter-result");
  const tableBody = document.getElementById("user-table-body");

  if (
    !(createForm instanceof HTMLFormElement) ||
    !(newUsernameInput instanceof HTMLInputElement) ||
    !(newRoleSelect instanceof HTMLSelectElement) ||
    !(searchInput instanceof HTMLInputElement) ||
    !(roleFilterSelect instanceof HTMLSelectElement) ||
    !(resultText instanceof HTMLElement) ||
    !(tableBody instanceof HTMLTableSectionElement)
  ) {
    return;
  }

  // 初期表示用のユーザーデータです。
  const users = [
    { id: 1, username: "tanaka", role: "ROLE_ADMIN" },
    { id: 2, username: "suzuki", role: "ROLE_USER" },
    { id: 3, username: "sato", role: "ROLE_USER" }
  ];

  let nextId = 4;

  // 現在の検索条件に合うユーザーだけを返します。
  const getFilteredUsers = () => {
    const keyword = searchInput.value.trim().toLowerCase();
    const selectedRole = roleFilterSelect.value;

    return users.filter((user) => {
      const matchedKeyword =
        keyword === "" || user.username.toLowerCase().includes(keyword);
      const matchedRole = selectedRole === "" || user.role === selectedRole;

      return matchedKeyword && matchedRole;
    });
  };

  // ユーザー一覧の表示を作り直します。
  const renderUsers = (displayUsers) => {
    tableBody.innerHTML = "";

    if (displayUsers.length === 0) {
      const row = document.createElement("tr");
      const cell = document.createElement("td");

      cell.colSpan = 4;
      cell.className = "empty-message";
      cell.textContent = "該当するユーザーはいません。";

      row.appendChild(cell);
      tableBody.appendChild(row);
      return;
    }

    displayUsers.forEach((user) => {
      const row = document.createElement("tr");

      const idCell = document.createElement("td");
      idCell.textContent = String(user.id);

      const usernameCell = document.createElement("td");
      usernameCell.textContent = user.username;

      const roleCell = document.createElement("td");
      roleCell.textContent = user.role;

      const actionCell = document.createElement("td");
      const deleteButton = document.createElement("button");

      deleteButton.type = "button";
      deleteButton.textContent = "削除";
      deleteButton.addEventListener("click", () => {
        deleteUser(user.id);
      });

      actionCell.appendChild(deleteButton);
      row.appendChild(idCell);
      row.appendChild(usernameCell);
      row.appendChild(roleCell);
      row.appendChild(actionCell);
      tableBody.appendChild(row);
    });
  };

  // 検索条件を反映して、件数表示とテーブルを更新します。
  const applyFilter = () => {
    const displayUsers = getFilteredUsers();

    resultText.textContent = `表示件数: ${displayUsers.length}件 / ${users.length}件`;
    renderUsers(displayUsers);
  };

  // ユーザーを追加します。
  createForm.addEventListener("submit", (event) => {
    event.preventDefault();

    const username = newUsernameInput.value.trim();
    const role = newRoleSelect.value;

    if (username === "") {
      window.alert("ユーザー名を入力してください。");
      return;
    }

    users.push({
      id: nextId,
      username: username,
      role: role
    });

    nextId += 1;
    newUsernameInput.value = "";
    newUsernameInput.focus();

    applyFilter();
  });

  // ユーザーを削除します。
  function deleteUser(id) {
    const targetUser = users.find((user) => {
      return user.id === id;
    });

    if (!targetUser) {
      return;
    }

    const result = window.confirm(`${targetUser.username} を削除しますか？`);

    if (!result) {
      return;
    }

    const targetIndex = users.findIndex((user) => {
      return user.id === id;
    });

    if (targetIndex !== -1) {
      users.splice(targetIndex, 1);
      applyFilter();
    }
  }

  searchInput.addEventListener("input", applyFilter);
  roleFilterSelect.addEventListener("change", applyFilter);

  applyFilter();
});
```

## 確認ポイント

- フォーム送信時に画面が再読み込みされない
- 入力したユーザー名とロールで、テーブルに行が追加される
- 検索欄に入力すると、ユーザー名で絞り込みできる
- ロールのプルダウンで、`ROLE_ADMIN` / `ROLE_USER` を絞り込みできる
- 削除ボタンを押すと確認ダイアログが表示される
- 検索条件に一致するユーザーがいない場合、0件メッセージが表示される
- 開発者ツールの Console にエラーが出ていない
