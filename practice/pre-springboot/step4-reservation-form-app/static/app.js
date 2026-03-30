document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("reservation-form");
  const nameInput = document.getElementById("name");
  const dateInput = document.getElementById("date");
  const startTimeInput = document.getElementById("start-time");
  const endTimeInput = document.getElementById("end-time");
  const noteInput = document.getElementById("note");
  const message = document.getElementById("message");
  const count = document.getElementById("count");
  const body = document.getElementById("reservation-body");

  if (!(form instanceof HTMLFormElement) ||
      !(nameInput instanceof HTMLInputElement) ||
      !(dateInput instanceof HTMLInputElement) ||
      !(startTimeInput instanceof HTMLInputElement) ||
      !(endTimeInput instanceof HTMLInputElement) ||
      !(noteInput instanceof HTMLInputElement) ||
      !(message instanceof HTMLElement) ||
      !(count instanceof HTMLElement) ||
      !(body instanceof HTMLTableSectionElement)) {
    return;
  }

  const setMessage = (text) => {
    message.textContent = text;
  };

  const formatTime = (value) => value.slice(0, 5);

  const renderReservations = (reservations) => {
    body.innerHTML = "";
    count.textContent = `件数: ${reservations.length}`;

    if (reservations.length === 0) {
      const row = document.createElement("tr");
      row.innerHTML = `<td colspan="6" class="muted">予約がありません。</td>`;
      body.appendChild(row);
      return;
    }

    reservations.forEach((reservation) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${reservation.id}</td>
        <td>${escapeHtml(reservation.name)}</td>
        <td>${reservation.date}</td>
        <td>${formatTime(reservation.startTime)} - ${formatTime(reservation.endTime)}</td>
        <td>${escapeHtml(reservation.note || "-")}</td>
        <td><button type="button" class="cancel">キャンセル</button></td>
      `;

      const cancelButton = row.querySelector("button.cancel");
      if (cancelButton instanceof HTMLButtonElement) {
        cancelButton.addEventListener("click", () => cancelReservation(reservation.id, reservation.name));
      }

      body.appendChild(row);
    });
  };

  const loadReservations = async () => {
    const response = await fetch("/api/reservations");
    if (!response.ok) {
      throw new Error("一覧取得に失敗しました。");
    }
    const reservations = await response.json();
    renderReservations(reservations);
  };

  const createReservation = async (payload) => {
    const response = await fetch("/api/reservations", {
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

  const cancelReservation = async (id, name) => {
    const ok = window.confirm(`「${name}」の予約をキャンセルします。よろしいですか？`);
    if (!ok) {
      return;
    }

    try {
      const response = await fetch(`/api/reservations/${id}`, { method: "DELETE" });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.error || "キャンセルに失敗しました。");
      }
      await loadReservations();
      setMessage("キャンセルしました。");
    } catch (error) {
      setMessage(error.message || "キャンセルに失敗しました。");
    }
  };

  form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const payload = {
      name: nameInput.value.trim(),
      date: dateInput.value,
      startTime: startTimeInput.value,
      endTime: endTimeInput.value,
      note: noteInput.value.trim()
    };

    if (payload.startTime >= payload.endTime) {
      setMessage("終了時刻は開始時刻より後にしてください。");
      return;
    }

    try {
      await createReservation(payload);
      form.reset();
      await loadReservations();
      setMessage("予約を登録しました。");
    } catch (error) {
      setMessage(error.message || "登録に失敗しました。");
    }
  });

  loadReservations().catch((error) => {
    setMessage(error.message || "初期表示に失敗しました。");
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
