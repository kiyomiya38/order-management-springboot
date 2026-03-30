# Greeting Web

Java標準のHTTPサーバー（`HttpServer`）で動かす、最小構成のWebアプリです。

## 使い方
```bash
cd practice/pre-springboot/step1-greeting-web
javac -encoding UTF-8 App.java
java App
```

起動後に以下へアクセス:
- `http://localhost:8090/`

## 仕様
- 画面の名前入力フォームから送信
- JavaScriptが `POST /api/greeting` を呼び出す
- Javaが `{"message":"こんにちは、...さん"}` を返し、画面表示する
