document.addEventListener("DOMContentLoaded", () => { // HTML読込完了後に処理を開始
  const healthStatus = document.getElementById("health-status"); // API状態表示要素を取得
  const form = document.getElementById("message-form"); // フォーム要素を取得
  const nameInput = document.getElementById("name"); // 名前入力欄を取得
  const resultMessage = document.getElementById("result-message"); // 結果/エラー表示要素を取得
  const messageList = document.getElementById("message-list"); // メッセージ一覧要素を取得

  // 要素が想定どおり取得できたかを型込みでチェック
  if (!(healthStatus instanceof HTMLElement) ||
      !(form instanceof HTMLFormElement) ||
      !(nameInput instanceof HTMLInputElement) ||
      !(resultMessage instanceof HTMLElement) ||
      !(messageList instanceof HTMLUListElement)) {
    return; // 取得失敗時は安全に処理終了
  }

  const showMessage = (text, isError = false) => { // 結果/エラー表示をまとめて更新する関数
    resultMessage.textContent = text; // 表示文字列を更新
    resultMessage.classList.toggle("error", isError); // エラー時だけerrorクラスを付ける
  };

  const loadHealth = async () => { // API状態を取得する非同期関数
    const response = await fetch("/api/health"); // Java APIへGETリクエスト
    const data = await response.json(); // レスポンスJSONをオブジェクト化

    if (!response.ok) { // HTTPエラー（400/404/405など）の場合
      throw new Error(data.error || "API状態確認に失敗しました"); // 呼び出し元のcatchへエラーを渡す
    }

    healthStatus.textContent = `${data.status}: ${data.message}`; // 画面のAPI状態を更新
  };

  const renderMessages = (messages) => { // メッセージ一覧を画面に描画する関数
    messageList.innerHTML = ""; // 既存の一覧表示を空にする

    if (messages.length === 0) { // 一覧が空の場合
      const emptyItem = document.createElement("li"); // 空表示用のli要素を作成
      emptyItem.textContent = "まだメッセージはありません。"; // 空表示メッセージ
      messageList.appendChild(emptyItem); // ulへliを追加
      return; // ここで描画処理を終了
    }

    messages.forEach((message) => { // メッセージを1件ずつ処理
      const item = document.createElement("li"); // 1件分のli要素を作成
      item.textContent = `#${message.id} ${message.text}`; // IDと本文を表示
      messageList.appendChild(item); // ulへliを追加
    });
  };

  const loadMessages = async () => { // メッセージ一覧をAPIから取得する非同期関数
    const response = await fetch("/api/messages"); // Java APIへGETリクエスト
    const messages = await response.json(); // レスポンスJSON配列をJavaScript配列へ変換

    if (!response.ok) { // HTTPエラーの場合
      throw new Error(messages.error || "一覧取得に失敗しました"); // 呼び出し元のcatchへエラーを渡す
    }

    renderMessages(messages); // 取得した一覧を画面へ描画
  };

  const createMessage = async (name) => { // メッセージを新規登録する非同期関数
    const response = await fetch("/api/messages", { // Java APIへPOSTリクエスト
      method: "GET", // 登録なのでPOSTを指定
      headers: {
        "Content-Type": "application/json" // JSON送信を宣言
      },
      body: JSON.stringify({ name }) // {name: "..."} をJSON文字列化
    });

    const data = await response.json(); // レスポンスJSONをオブジェクト化

    if (!response.ok) { // HTTPエラー（例: 400）の場合
      throw new Error(data.error || "登録に失敗しました"); // 呼び出し元のcatchへエラーを渡す
    }

    return data; // 正常時は作成結果を返す
  };

  form.addEventListener("submit", async (event) => { // フォーム送信イベントを監視
    event.preventDefault(); // ブラウザ既定の画面遷移を止める

    const name = nameInput.value.trim(); // 入力値の前後空白を除去
    showMessage(""); // 前回のメッセージを消す

    try {
      const created = await createMessage(name); // APIへ登録リクエストを送信
      showMessage(created.message); // 登録結果メッセージを表示
      nameInput.value = ""; // 入力欄を空に戻す
      await loadMessages(); // 登録後の一覧を再取得
    } catch (error) { // APIエラーや通信失敗時
      showMessage(error.message, true); // エラーメッセージを表示
    }
  });

  (async () => { // 初期表示用の即時実行非同期関数
    try {
      await loadHealth(); // API状態を取得
      await loadMessages(); // 初期一覧を取得
    } catch (error) { // 初期表示時に失敗した場合
      showMessage(error.message, true); // エラーメッセージを表示
    }
  })();
});