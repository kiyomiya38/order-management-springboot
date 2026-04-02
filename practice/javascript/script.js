document.addEventListener("DOMContentLoaded", () => {
  setupDeleteConfirmation();
  setupUserTableFilter();
});

function setupDeleteConfirmation() {
  const deleteForms = document.querySelectorAll("form.js-delete-user-form");

  deleteForms.forEach((form) => {
    form.addEventListener("submit", (event) => {
      const username = form.dataset.username || "このユーザー";
      const accepted = window.confirm(`ユーザー「${username}」を削除します。よろしいですか？`);
      if (!accepted) {
        event.preventDefault();
      }
    });
  });
}

function setupUserTableFilter() {
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
}