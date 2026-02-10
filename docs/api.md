# 勤怠管理システム（MVP）エンドポイント一覧

ベースURL: `http://localhost:8080`

## 1. 勤怠トップ
- Method: GET
- Path: `/`

## 2. 出勤
- Method: POST
- Path: `/clock-in`

**curl例**
```bash
curl -s -X POST http://localhost:8080/clock-in
```

## 3. 退勤
- Method: POST
- Path: `/clock-out`

**curl例**
```bash
curl -s -X POST http://localhost:8080/clock-out
```

## 4. 勤怠一覧
- Method: GET
- Path: `/attendances`

**ブラウザ**
```
http://localhost:8080/attendances
```

## 5. 画面のエラーメッセージ
業務ルール違反時は画面上部にメッセージが表示されます。
- すでに出勤済みです
- 退勤するには先に出勤してください
- すでに退勤済みです
