# Day2（7/10）出勤の実装（Day2コード使用）

## 目的（Day2でできるようになること）
- Entity/Repository/Service が登場する意味が分かる
- 出勤ボタンでDBに記録されることが分かる

## 使用するコード
`stages/day2` を使う（出勤のみ実装）

---

## 1. 起動
```bash
cd stages/day2
mvn spring-boot:run
```

## 2. 出勤の動作確認
```
http://localhost:8080/
```
1. 「出勤」ボタンを押す  
2. 状態が「出勤中」になり、出勤時刻が表示される  

---

## 3. コード確認ポイント
- Entity: `Attendance`, `User`
- Repository: `AttendanceRepository`
- Service: `AttendanceService#clockIn`
- Controller: `HomeController#clockIn`

---

## 4. チェックポイント
- 出勤ボタンが動く
- 同日に複数回の出勤ができない（エラーが表示される）

---

## 5. 時間割目安
- 午前: Entity/Repository理解(60分)
- 午後: Service/Controller理解(60分) / 動作確認(30分)
