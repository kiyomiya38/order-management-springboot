# 勤怠ミニ

Java標準のHTTPサーバー（`HttpServer`）で動かす、最終アプリ前の橋渡し用Webアプリです。

## 機能
- 出勤 / 退勤（状態遷移）
- 本日の状態表示
- 勤怠履歴一覧
- ユーザー一覧の検索・絞り込み
- ユーザー削除（確認ダイアログ）

## 起動方法（絶対パス）
```bash
cd /c/Users/Shinesoft/order-management-springboot/practice/pre-springboot/step5-attendance-mini
javac -encoding UTF-8 App.java
java App
```

起動後:
- `http://localhost:8094/`

## API
- `GET /api/users`
- `DELETE /api/users/{id}`
- `GET /api/attendance/today?userId={id}`
- `GET /api/attendance/history?userId={id}`
- `POST /api/attendance/clock-in`
- `POST /api/attendance/clock-out`
