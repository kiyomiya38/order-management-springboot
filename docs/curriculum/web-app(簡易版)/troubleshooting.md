# Webアプリ簡易版 共通つまずき対応

この資料は、`web-app(簡易版)` のLesson1〜6で共通して起きやすい問題をまとめたものです。
各Lessonの「つまずきポイント」とあわせて確認します。

## まず見る場所
問題が起きたら、次の順で確認します。

1. ターミナルのJavaエラー
2. ブラウザのConsole
3. ブラウザのNetwork
4. `fetch` のURL
5. Java側の `server.createContext(...)`

## よくある症状

| 症状 | 主な原因 | 確認すること |
| --- | --- | --- |
| 画面が開かない | Javaサーバーが起動していない | `java App` の起動ログとポート番号を見る |
| `fetch` が失敗する | `file://` でHTMLを開いている | `http://localhost:8091/` などサーバーURLで開く |
| `404 Not Found` | URLまたは静的ファイル名が違う | NetworkのRequest URLとファイル名を確認する |
| `405 Method Not Allowed` | HTTPメソッドが違う | `GET` / `POST` / `PATCH` / `DELETE` がJava側と一致しているか確認する |
| `400 Bad Request` | 入力値またはJSON本文が不正 | 送信JSONとサーバー側バリデーションを確認する |
| `409 Conflict` | 業務ルール違反 | 予約重複、打刻状態、削除制約など仕様通りか確認する |
| `Unexpected token` | JSONではないHTMLやエラー文字列を `response.json()` している | NetworkのResponse本文を見る |
| 登録したのに画面が変わらない | DOM再描画を呼んでいない | 登録後に一覧再取得関数を呼んでいるか確認する |
| 日本語が文字化けする | コンパイル時エンコーディング不一致 | `javac -encoding UTF-8 App.java` を使う |
| ポート競合 | 別のLessonが同じポートで起動中 | 先に起動しているJavaプロセスを停止する |

## Networkタブで見る項目
DevToolsのNetworkでは、最低限次を見ます。

| 項目 | 見る理由 |
| --- | --- |
| Request URL | `fetch` のURLが想定APIに向いているか確認する |
| Request Method | Java側が受け付けるHTTPメソッドと一致しているか確認する |
| Status Code | `200` / `201` / `400` / `404` / `405` / `409` を切り分ける |
| Request Payload | JavaScriptから送ったJSON本文を確認する |
| Response | サーバーが返したJSONまたはエラー本文を確認する |

## 切り分け例

### 登録ボタンを押しても一覧が増えない
1. Networkで `POST` が送信されているか確認する
2. `POST` のStatus Codeが `201` または `200` か確認する
3. 登録後に `loadTodos()` などの再取得関数を呼んでいるか確認する
4. `GET` のResponseに登録済みデータが含まれるか確認する

### `response.json()` でエラーになる
1. NetworkのResponse本文を見る
2. HTMLや空文字が返っている場合は、URL違いまたはサーバー側エラーを疑う
3. Java側の `Content-Type` が `application/json; charset=UTF-8` になっているか確認する

### `405` になる
1. JavaScript側の `fetch` の `method` を見る
2. Java側の `if ("POST".equals(method))` などの分岐を見る
3. URLが同じでも、メソッドが違うと別の処理として扱われることを確認する

## 直す前に残すメモ
エラーを直す前に、次を短く残します。

```text
起きたこと:
HTTPステータス:
Consoleエラー:
NetworkのRequest URL:
原因:
直した内容:
```

このメモを残すと、Spring Bootのデバッグでも同じ切り分けが使えます。

