# Day2（7/10）データ設計 & 勤怠ロジック（出勤/退勤/一覧）

## 目的（Day2でできるようになること）
- Entity / Repository / Service の役割が分かる
- 出勤・退勤の業務ルールがServiceに入っていることを理解できる
- 勤怠一覧を表示できる

## 業務ストーリー（今日の作業イメージ）
1. 出勤する  
2. 退勤する  
3. 勤怠一覧で履歴を見る  

---

## 1. 構成の全体像（読むだけ）
- Controller: 画面遷移・画面表示
- Service: 業務ルール
- Repository: DBアクセス
- Entity: DBのデータ構造

主なファイル:
- Entity: `src/main/java/com/shinesoft/attendance/domain`
- Repository: `src/main/java/com/shinesoft/attendance/repository`
- Service: `src/main/java/com/shinesoft/attendance/service`
- Controller: `src/main/java/com/shinesoft/attendance/web`
- 画面: `src/main/resources/templates`

---

## 2. Entity を確認
### User
- `id`, `username`, `password`, `role`

### Attendance
- `workDate`, `startTime`, `endTime`, `status`
- `user_id + workDate` の一意制約

ファイル:
- `src/main/java/com/shinesoft/attendance/domain/User.java`
- `src/main/java/com/shinesoft/attendance/domain/Attendance.java`

---

## 3. Repository を確認
`AttendanceRepository` に
- `findByUser_IdAndWorkDate`
- `findByUser_IdOrderByWorkDateDesc`

ファイル:
- `src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java`

---

## 4. Service（業務ルール）
`AttendanceService` でルールが強制されていることを確認:
- 同日に複数回の出勤は不可
- 未出勤で退勤不可
- 退勤済みの再退勤不可

ファイル:
- `src/main/java/com/shinesoft/attendance/service/AttendanceService.java`

---

## 5. 画面を確認
- トップ: `src/main/resources/templates/index.html`
- 一覧: `src/main/resources/templates/attendances.html`

---

## 6. 勤怠一覧の確認
ブラウザで:
```
http://localhost:8080/attendances
```

---

## 7. チェックポイント
- トップ画面が表示される
- 出勤/退勤ができる
- 一覧が表示される

---

## 8. つまずきポイント
- `退勤` を押す前に `出勤` が必要
- すでに `退勤済み` の場合はエラーになる

---

## 9. 時間割目安
- 午前: Entity/Repository理解(60分) / Service理解(60分)
- 午後: 画面確認(60分) / まとめ(30分)
