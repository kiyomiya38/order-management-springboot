# 予約フォームアプリ

Java標準のHTTPサーバー（`HttpServer`）で動かす、予約管理の学習用Webアプリです。

## 機能
- 予約作成（名前・日付・開始/終了時刻・メモ）
- 予約一覧表示
- 予約キャンセル（確認ダイアログ付き）
- 同日同時間帯の重複予約禁止（サーバー側ルール）

## 起動方法（絶対パス）
```bash
cd /c/Users/Shinesoft/order-management-springboot/practice/pre-springboot/step4-reservation-form-app
javac -encoding UTF-8 App.java
java App
```

起動後:
- `http://localhost:8093/`

## API
- `GET /api/reservations`
- `POST /api/reservations`
- `DELETE /api/reservations/{id}`
