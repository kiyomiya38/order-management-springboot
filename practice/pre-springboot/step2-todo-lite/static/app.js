document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("todo-form");
  const titleInput = document.getElementById("todo-title");
  const list = document.getElementById("todo-list");
  const message = document.getElementById("message");
  const count = document.getElementById("count");

  if (!(form instanceof HTMLFormElement) ||
      !(titleInput instanceof HTMLInputElement) ||
      !(list instanceof HTMLUListElement) ||
      !(message instanceof HTMLElement) ||
      !(count instanceof HTMLElement)) {
    return;
  }

  const setMessage = (text) => {
    message.textContent = text;
  };

  const loadTodos = async () => {
    const response = await fetch("/api/todos");
    if (!response.ok) {
      throw new Error("failed to load todos");
    }
    const todos = await response.json();
    renderTodos(todos);
  };

  const renderTodos = (todos) => {
    count.textContent = `件数: ${todos.length}`;
    list.innerHTML = "";

    if (todos.length === 0) {
      const li = document.createElement("li");
      li.className = "muted";
      li.textContent = "タスクがありません。";
      list.appendChild(li);
      return;
    }

    todos.forEach((todo) => {
      const li = document.createElement("li");
      li.className = "todo-item";

      const left = document.createElement("div");
      left.className = "todo-left";

      const checkbox = document.createElement("input");
      checkbox.type = "checkbox";
      checkbox.checked = Boolean(todo.completed);
      checkbox.addEventListener("change", () => toggleTodo(todo.id));

      const title = document.createElement("span");
      title.textContent = todo.title;
      title.className = todo.completed ? "todo-title done" : "todo-title";

      left.appendChild(checkbox);
      left.appendChild(title);

      const deleteButton = document.createElement("button");
      deleteButton.type = "button";
      deleteButton.className = "delete";
      deleteButton.textContent = "削除";
      deleteButton.addEventListener("click", () => deleteTodo(todo.id, todo.title));

      li.appendChild(left);
      li.appendChild(deleteButton);
      list.appendChild(li);
    });
  };

  const addTodo = async (title) => {
    const response = await fetch("/api/todos", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ title })
    });

    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.error || "failed to create todo");
    }
  };

  const toggleTodo = async (id) => {
    try {
      const response = await fetch(`/api/todos/${id}/toggle`, { method: "PATCH" });
      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.error || "failed to toggle");
      }
      await loadTodos();
      setMessage("状態を更新しました。");
    } catch (error) {
      setMessage("状態の更新に失敗しました。");
    }
  };

  const deleteTodo = async (id, title) => {
    const ok = window.confirm(`「${title}」を削除します。よろしいですか？`);
    if (!ok) {
      return;
    }

    try {
      const response = await fetch(`/api/todos/${id}`, { method: "DELETE" });
      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.error || "failed to delete");
      }
      await loadTodos();
      setMessage("削除しました。");
    } catch (error) {
      setMessage("削除に失敗しました。");
    }
  };

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const title = titleInput.value.trim();
    if (title.length === 0) {
      setMessage("タスク名を入力してください。");
      return;
    }

    try {
      await addTodo(title);
      titleInput.value = "";
      await loadTodos();
      setMessage("追加しました。");
    } catch (error) {
      setMessage("追加に失敗しました。");
    }
  });

  loadTodos().catch(() => {
    setMessage("一覧取得に失敗しました。");
  });
});
