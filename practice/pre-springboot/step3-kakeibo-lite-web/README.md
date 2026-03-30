# 家計簿Lite Web

Java標準のHTTPサーバー（`HttpServer`）で動かす、家計簿学習用Webアプリです。

## 機能
- 収入/支出の登録
- 一覧表示
- 収入合計・支出合計・差引表示
- 種別/カテゴリでの絞り込み
- サーバー側バリデーション

## 起動方法
```bash
cd /c/Users/Shinesoft/order-management-springboot/practice/pre-springboot/step3-kakeibo-lite-web
javac -encoding UTF-8 App.java
java App
```

起動後:
- `http://localhost:8092/`

## API
- `GET /api/entries`
- `POST /api/entries` body: `{"type":"INCOME","category":"給料","amount":300000,"memo":"4月分"}`
- `GET /api/summary`
