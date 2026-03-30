document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("greeting-form");
  const nameInput = document.getElementById("name");
  const resultMessage = document.getElementById("result-message");

  if (!(form instanceof HTMLFormElement) ||
      !(nameInput instanceof HTMLInputElement) ||
      !(resultMessage instanceof HTMLElement)) {
    return;
  }

  form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const name = nameInput.value.trim();
    if (name.length === 0) {
      resultMessage.textContent = "名前を入力してください。";
      return;
    }

    resultMessage.textContent = "送信中...";

    try {
      const response = await fetch("/api/greeting", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ name })
      });

      const data = await response.json();
      if (!response.ok) {
        resultMessage.textContent = data.error || "エラーが発生しました。";
        return;
      }

      resultMessage.textContent = data.message;
    } catch (error) {
      resultMessage.textContent = "通信に失敗しました。";
    }
  });
});
