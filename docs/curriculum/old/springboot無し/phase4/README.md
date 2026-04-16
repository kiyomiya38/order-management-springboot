# Phase4: 手動DIと層分離

## 1. 位置づけ（最終アプリとの接続）
- 対応: `springless-roadmap.md` の Phase4
- ここで作るものは、最終アプリの「コンストラクタ注入 + 層分離」の手動版
- `new` の散在を止め、依存を `AppConfig` に集中させる

## 2. Lesson順と機能追加
1. `lesson1.md`: Web最小例で `AppConfig` 導入
2. `lesson2.md`: Webアプリへ `AppConfig` を適用
3. `lesson3.md`: `AttendanceController` を追加し分離を進める
4. `lesson4.md`: 実装差し替えを `AppConfig` のみで実施
5. `lesson5.md`: 配線全体を整理してPhase4を完成

## Lesson開始ルール
- 全Lessonで同じコードベースを使う: `~/order-management-springboot/practice/springless-final-web`
- Lessonごとに既存コードへ追記して機能を増やす（作り直さない）
- コンソール確認は中間確認として使い、最終判定はブラウザ確認で行う

## 3. 先に読む `src`
- `src/main/java/com/shinesoft/attendance/web/*Controller.java`
- `src/main/java/com/shinesoft/attendance/service/*Service.java`
- `src/main/java/com/shinesoft/attendance/repository/*Repository.java`

## 4. ファイル配置ルール
- 手順書: `docs/curriculum/phase4`
- 演習コード: `practice/springless-final-web`
- `docs` 配下に `.java` を作らない

## 5. Phase完了条件
- ControllerがRepositoryを直接 `new` しない
- 依存差し替えが `AppConfig` 中心でできる
- Spring DIが自動化しているポイントを説明できる
