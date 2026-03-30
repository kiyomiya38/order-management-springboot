document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("entry-form");
  const typeInput = document.getElementById("type");
  const categoryInput = document.getElementById("category");
  const amountInput = document.getElementById("amount");
  const memoInput = document.getElementById("memo");
  const message = document.getElementById("form-message");
  const incomeTotal = document.getElementById("income-total");
  const expenseTotal = document.getElementById("expense-total");
  const balanceTotal = document.getElementById("balance-total");
  const entryBody = document.getElementById("entry-body");
  const entryCount = document.getElementById("entry-count");
  const filterType = document.getElementById("filter-type");
  const filterCategory = document.getElementById("filter-category");

  if (!(form instanceof HTMLFormElement) ||
      !(typeInput instanceof HTMLSelectElement) ||
      !(categoryInput instanceof HTMLInputElement) ||
      !(amountInput instanceof HTMLInputElement) ||
      !(memoInput instanceof HTMLInputElement) ||
      !(message instanceof HTMLElement) ||
      !(incomeTotal instanceof HTMLElement) ||
      !(expenseTotal instanceof HTMLElement) ||
      !(balanceTotal instanceof HTMLElement) ||
      !(entryBody instanceof HTMLTableSectionElement) ||
      !(entryCount instanceof HTMLElement) ||
      !(filterType instanceof HTMLSelectElement) ||
      !(filterCategory instanceof HTMLInputElement)) {
    return;
  }

  let entries = [];

  const yen = (value) => `${value.toLocaleString("ja-JP")}円`;

  const setMessage = (text) => {
    message.textContent = text;
  };

  const renderSummary = async () => {
    try {
      const response = await fetch("/api/summary");
      if (!response.ok) {
        throw new Error("summary");
      }
      const data = await response.json();
      incomeTotal.textContent = yen(data.income);
      expenseTotal.textContent = yen(data.expense);
      balanceTotal.textContent = yen(data.balance);
    } catch (error) {
      incomeTotal.textContent = "-";
      expenseTotal.textContent = "-";
      balanceTotal.textContent = "-";
    }
  };

  const applyFilter = () => {
    const type = filterType.value;
    const categoryKeyword = filterCategory.value.trim().toLowerCase();

    const filtered = entries.filter((entry) => {
      const typeMatch = type === "" || entry.type === type;
      const categoryMatch = categoryKeyword === "" || entry.category.toLowerCase().includes(categoryKeyword);
      return typeMatch && categoryMatch;
    });

    entryCount.textContent = `表示件数: ${filtered.length}件 / 全${entries.length}件`;
    entryBody.innerHTML = "";

    if (filtered.length === 0) {
      const row = document.createElement("tr");
      row.innerHTML = `<td colspan="5" class="muted">データがありません。</td>`;
      entryBody.appendChild(row);
      return;
    }

    filtered.forEach((entry) => {
      const row = document.createElement("tr");
      const typeLabel = entry.type === "INCOME" ? "収入" : "支出";
      const amountClass = entry.type === "INCOME" ? "income amount" : "expense amount";
      row.innerHTML = `
        <td>${entry.id}</td>
        <td>${typeLabel}</td>
        <td>${escapeHtml(entry.category)}</td>
        <td class="${amountClass}">${yen(entry.amount)}</td>
        <td>${escapeHtml(entry.memo || "-")}</td>
      `;
      entryBody.appendChild(row);
    });
  };

  const loadEntries = async () => {
    const response = await fetch("/api/entries");
    if (!response.ok) {
      throw new Error("entries");
    }
    entries = await response.json();
    applyFilter();
    await renderSummary();
  };

  const createEntry = async (payload) => {
    const response = await fetch("/api/entries", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.error || "登録に失敗しました。");
    }
    return data;
  };

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const amount = Number(amountInput.value);

    const payload = {
      type: typeInput.value,
      category: categoryInput.value.trim(),
      amount,
      memo: memoInput.value.trim()
    };

    try {
      await createEntry(payload);
      form.reset();
      typeInput.value = "INCOME";
      await loadEntries();
      setMessage("登録しました。");
    } catch (error) {
      setMessage(error.message || "登録に失敗しました。");
    }
  });

  filterType.addEventListener("change", applyFilter);
  filterCategory.addEventListener("input", applyFilter);

  loadEntries().catch(() => {
    setMessage("初期データの取得に失敗しました。");
  });
});

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("\"", "&quot;")
    .replaceAll("'", "&#39;");
}
