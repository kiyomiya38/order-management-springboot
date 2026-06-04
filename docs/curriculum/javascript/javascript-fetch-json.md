# JavaScript 補講: fetch / async / await / JSON通信

前提:
- `docs/curriculum/javascript/javascript.md` を完了している
- `docs/curriculum/java/java-handson/java-20b-web-api-prep.md` を完了している
- Git Bash とブラウザの DevTools（Console / Network）を使える

## 目的（この資料でできるようになること）
- ブラウザの JavaScript から Java API を呼び出せる
- `fetch` / `async` / `await` の最小形を読める
- `GET` と `POST` の違いを、JavaScriptコード上で説明できる
- `response.ok` / `response.json()` / `JSON.stringify(...)` の役割を説明できる

この資料は `web-app(簡易版)` に入る直前の橋渡しです。
画面作成そのものではなく、「ブラウザからAPIを呼ぶ流れ」を短く確認します。

---

## 0. 先に覚えるポイント
1. `fetch(url)` は、ブラウザからHTTPリクエストを送る関数
2. `await fetch(...)` は、レスポンスが返るまで待つ書き方
3. `response.json()` は、レスポンス本文のJSONをJavaScriptの値へ変換する
4. `JSON.stringify(...)` は、JavaScriptの値をJSON文字列へ変換する
5. `response.ok` は、HTTPステータスが `200` 番台なら `true`

対応イメージ:

| JavaScript | HTTP上の意味 | Java側で対応する処理 |
| --- | --- | --- |
| `fetch("/api/health")` | `GET /api/health` | 起動状態を返す |
| `fetch("/api/messages")` | `GET /api/messages` | 一覧を返す |
| `fetch("/api/messages", { method: "POST", ... })` | `POST /api/messages` | 新規登録する |

---

## 1. Java APIを起動する
Java-20Bで作ったフォルダへ移動して、APIを起動します。

```bash
cd ~/order-management-springboot/practice/java/handson20b
javac WebApiPrepDemo.java
java WebApiPrepDemo
```

期待表示:

```text
started: http://localhost:8091
```

ブラウザで `http://localhost:8091/` を開き、Java-20Bの画面が表示されることを確認します。

---

## 2. `static/index.html` を fetch確認用に変更する
編集ファイル:
- `~/order-management-springboot/practice/java/handson20b/static/index.html`

次の内容に更新:

```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title>fetch JSON Bridge</title>
  <style>
    body {
      font-family: system-ui, sans-serif;
      max-width: 720px;
      margin: 40px auto;
      line-height: 1.6;
    }

    form,
    section {
      border: 1px solid #ddd;
      padding: 16px;
      margin-top: 16px;
    }

    button {
      margin-left: 8px;
    }

    pre {
      background: #f6f8fa;
      padding: 12px;
      overflow: auto;
    }
  </style>
</head>
<body>
  <h1>fetch JSON Bridge</h1>

  <section>
    <button id="health-button" type="button">API状態確認</button>
    <button id="list-button" type="button">一覧取得</button>
  </section>

  <form id="message-form">
    <label>
      名前
      <input id="name-input" type="text" placeholder="Taro">
    </label>
    <button type="submit">POST登録</button>
  </form>

  <pre id="result">ここに結果を表示します。</pre>

  <script>
    const result = document.getElementById("result");
    const healthButton = document.getElementById("health-button");
    const listButton = document.getElementById("list-button");
    const form = document.getElementById("message-form");
    const nameInput = document.getElementById("name-input");

    const show = (value) => {
      result.textContent = JSON.stringify(value, null, 2);
    };

    const getHealth = async () => {
      const response = await fetch("/api/health");
      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || "API状態確認に失敗しました");
      }

      show(data);
    };

    const getMessages = async () => {
      const response = await fetch("/api/messages");
      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || "一覧取得に失敗しました");
      }

      show(data);
    };

    const createMessage = async (name) => {
      const response = await fetch("/api/messages", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ name })
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || "登録に失敗しました");
      }

      show(data);
    };

    healthButton.addEventListener("click", async () => {
      try {
        await getHealth();
      } catch (error) {
        show({ error: error.message });
      }
    });

    listButton.addEventListener("click", async () => {
      try {
        await getMessages();
      } catch (error) {
        show({ error: error.message });
      }
    });

    form.addEventListener("submit", async (event) => {
      event.preventDefault();

      try {
        await createMessage(nameInput.value.trim());
        nameInput.value = "";
      } catch (error) {
        show({ error: error.message });
      }
    });
  </script>
</body>
</html>
```

ポイント:
- このHTMLは Java-20B の `HttpServer` から配信されます。
- `http://localhost:8091/` で開くため、`fetch("/api/...")` が同じサーバーへ送られます。
- `file://` で直接HTMLを開かないでください。API通信の確認が不安定になります。

---

## 3. ブラウザで確認する
`http://localhost:8091/` を再読み込みします。

確認:
1. `API状態確認` を押すと `status` / `message` が表示される
2. 名前を入力して `POST登録` を押すと、登録結果JSONが表示される
3. `一覧取得` を押すと、登録済みメッセージ配列が表示される
4. DevTools の Network で `GET` / `POST` とステータスを確認する

---

## 4. 理解確認
次を説明できれば完了です。

1. `fetch("/api/health")` が Java側の `/api/health` に届く理由
2. `POST` のときだけ `method` / `headers` / `body` を指定している理由
3. `JSON.stringify({ name })` の役割
4. `await response.json()` の役割
5. `response.ok` が `false` になる代表例

---

## 5. つまずきポイント
- 画面は出るがボタンが動かない:
  - Console にエラーが出ていないか確認
  - `java WebApiPrepDemo` が起動中か確認
- `fetch` が失敗する:
  - `file://` で開いていないか確認
  - `http://localhost:8091/` で開く
- 登録が400になる:
  - 名前が空でないか確認
  - Java側は空文字を `400 Bad Request` として返す
- 一覧に反映されない:
  - `POST登録` 後に `一覧取得` を押して確認
  - Javaプロセスを再起動するとメモリ上のデータは消える

---

## 次へ
次は `docs/curriculum/web-app(簡易版)/README.md` を読み、`lesson1.md` から進めます。
