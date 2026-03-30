document.addEventListener("DOMContentLoaded", () => {
  const userSelect = document.getElementById("active-user-select");
  const activeUserRole = document.getElementById("active-user-role");
  const globalMessage = document.getElementById("global-message");
  const todayDate = document.getElementById("today-date");
  const todayStatus = document.getElementById("today-status");
  const todayStart = document.getElementById("today-start");
  const todayEnd = document.getElementById("today-end");
  const clockInButton = document.getElementById("clock-in-btn");
  const clockOutButton = document.getElementById("clock-out-btn");
  const historyBody = document.getElementById("history-body");
  const historyCount = document.getElementById("history-count");
  const userBody = document.getElementById("user-body");
  const userCount = document.getElementById("user-count");
  const userSearchInput = document.getElementById("user-search-input");
  const roleFilterSelect = document.getElementById("role-filter-select");

  if (!(userSelect instanceof HTMLSelectElement) ||
      !(activeUserRole instanceof HTMLElement) ||
      !(globalMessage instanceof HTMLElement) ||
      !(todayDate instanceof HTMLElement) ||
      !(todayStatus instanceof HTMLElement) ||
      !(todayStart instanceof HTMLElement) ||
      !(todayEnd instanceof HTMLElement) ||
      !(clockInButton instanceof HTMLButtonElement) ||
      !(clockOutButton instanceof HTMLButtonElement) ||
      !(historyBody instanceof HTMLTableSectionElement) ||
      !(historyCount instanceof HTMLElement) ||
      !(userBody instanceof HTMLTableSectionElement) ||
      !(userCount instanceof HTMLElement) ||
      !(userSearchInput instanceof HTMLInputElement) ||
      !(roleFilterSelect instanceof HTMLSelectElement)) {
    return;
  }

  let users = [];
  let activeUserId = null;
  let activeTodayStatus = "NOT_STARTED";

  const setMessage = (text) => {
    globalMessage.textContent = text;
  };

  const safeText = (value) => {
    if (value == null || value === "") {
      return "-";
    }
    return value;
  };

  const parseActiveUserId = () => {
    const parsed = Number(userSelect.value);
    return Number.isInteger(parsed) ? parsed : null;
  };

  const refreshClockButtons = () => {
    clockInButton.disabled = activeUserId == null || activeTodayStatus !== "NOT_STARTED";
    clockOutButton.disabled = activeUserId == null || activeTodayStatus !== "WORKING";
  };

  const loadToday = async () => {
    if (activeUserId == null) {
      todayDate.textContent = "-";
      todayStatus.textContent = "-";
      todayStart.textContent = "-";
      todayEnd.textContent = "-";
      activeTodayStatus = "NOT_STARTED";
      refreshClockButtons();
      return;
    }
    const response = await fetch(`/api/attendance/today?userId=${activeUserId}`);
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.error || "本日の勤怠取得に失敗しました。");
    }
    todayDate.textContent = data.date || "-";
    todayStatus.textContent = data.statusLabel || data.status;
    todayStart.textContent = safeText(data.startTime);
    todayEnd.textContent = safeText(data.endTime);
    activeTodayStatus = data.status || "NOT_STARTED";
    refreshClockButtons();
  };

  const loadHistory = async () => {
    if (activeUserId == null) {
      historyBody.innerHTML = `<tr><td colspan="4" class="muted">ユーザーがいません。</td></tr>`;
      historyCount.textContent = "件数: 0";
      return;
    }
    const response = await fetch(`/api/attendance/history?userId=${activeUserId}`);
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.error || "履歴取得に失敗しました。");
    }
    historyCount.textContent = `件数: ${data.length}`;
    historyBody.innerHTML = "";
    if (data.length === 0) {
      historyBody.innerHTML = `<tr><td colspan="4" class="muted">履歴がありません。</td></tr>`;
      return;
    }
    data.forEach((record) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${record.date}</td>
        <td>${safeText(record.startTime)}</td>
        <td>${safeText(record.endTime)}</td>
        <td>${record.statusLabel}</td>
      `;
      historyBody.appendChild(row);
    });
  };

  const renderUserSelect = () => {
    const previous = activeUserId;
    userSelect.innerHTML = "";
    users.forEach((user) => {
      const option = document.createElement("option");
      option.value = String(user.id);
      option.textContent = `${user.username} (${user.role})`;
      userSelect.appendChild(option);
    });

    if (users.length === 0) {
      activeUserId = null;
      activeUserRole.textContent = "";
      refreshClockButtons();
      return;
    }

    const hasPrevious = users.some((user) => user.id === previous);
    activeUserId = hasPrevious ? previous : users[0].id;
    userSelect.value = String(activeUserId);
    const active = users.find((user) => user.id === activeUserId);
    activeUserRole.textContent = active ? `ロール: ${active.role}` : "";
  };

  const applyUserFilter = () => {
    const keyword = userSearchInput.value.trim().toLowerCase();
    const role = roleFilterSelect.value;

    const filtered = users.filter((user) => {
      const nameMatch = keyword === "" || user.username.toLowerCase().includes(keyword);
      const roleMatch = role === "" || user.role === role;
      return nameMatch && roleMatch;
    });

    userCount.textContent = `表示件数: ${filtered.length}件 / 全${users.length}件`;
    userBody.innerHTML = "";

    if (filtered.length === 0) {
      userBody.innerHTML = `<tr><td colspan="4" class="muted">条件に一致するユーザーがいません。</td></tr>`;
      return;
    }

    filtered.forEach((user) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${user.id}</td>
        <td>${escapeHtml(user.username)}</td>
        <td>${user.role}</td>
        <td><button type="button" class="delete">削除</button></td>
      `;

      const deleteButton = row.querySelector("button.delete");
      if (deleteButton instanceof HTMLButtonElement) {
        deleteButton.addEventListener("click", () => deleteUser(user));
      }
      userBody.appendChild(row);
    });
  };

  const loadUsers = async () => {
    const response = await fetch("/api/users");
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.error || "ユーザー取得に失敗しました。");
    }
    users = data;
    renderUserSelect();
    applyUserFilter();
  };

  const postAttendance = async (endpoint) => {
    if (activeUserId == null) {
      return;
    }
    const response = await fetch(endpoint, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ userId: activeUserId })
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.error || "処理に失敗しました。");
    }
    setMessage(data.message || "更新しました。");
    await loadToday();
    await loadHistory();
  };

  const deleteUser = async (user) => {
    const ok = window.confirm(`ユーザー「${user.username}」を削除します。よろしいですか？`);
    if (!ok) {
      return;
    }

    try {
      const response = await fetch(`/api/users/${user.id}`, { method: "DELETE" });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.error || "削除に失敗しました。");
      }
      setMessage("ユーザーを削除しました。");
      await loadUsers();
      await loadToday();
      await loadHistory();
    } catch (error) {
      setMessage(error.message || "削除に失敗しました。");
    }
  };

  userSelect.addEventListener("change", async () => {
    activeUserId = parseActiveUserId();
    const active = users.find((user) => user.id === activeUserId);
    activeUserRole.textContent = active ? `ロール: ${active.role}` : "";
    await loadToday();
    await loadHistory();
  });

  clockInButton.addEventListener("click", async () => {
    try {
      await postAttendance("/api/attendance/clock-in");
    } catch (error) {
      setMessage(error.message || "出勤に失敗しました。");
    }
  });

  clockOutButton.addEventListener("click", async () => {
    try {
      await postAttendance("/api/attendance/clock-out");
    } catch (error) {
      setMessage(error.message || "退勤に失敗しました。");
    }
  });

  userSearchInput.addEventListener("input", applyUserFilter);
  roleFilterSelect.addEventListener("change", applyUserFilter);

  (async () => {
    try {
      await loadUsers();
      await loadToday();
      await loadHistory();
      setMessage("読み込み完了");
    } catch (error) {
      setMessage(error.message || "初期化に失敗しました。");
    }
  })();
});

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("\"", "&quot;")
    .replaceAll("'", "&#39;");
}
