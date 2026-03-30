# ToDo Lite

Java標準のHTTPサーバー（`HttpServer`）で動かす、CRUD学習用のWebアプリです。

## 機能
- タスク追加
- 一覧表示
- 完了/未完了の切り替え
- 削除（確認ダイアログ付き）

## 起動方法
```bash
cd practice/pre-springboot/step2-todo-lite
javac -encoding UTF-8 App.java
java App
```

起動後に以下へアクセス:
- `http://localhost:8091/`

## API
- `GET /api/todos`
- `POST /api/todos` body: `{"title":"買い物"}`
- `PATCH /api/todos/{id}/toggle`
- `DELETE /api/todos/{id}`
