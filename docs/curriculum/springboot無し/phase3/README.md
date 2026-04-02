# Phase3: Web拡張（ルーティング + 一覧 + PRG）

## 1. 位置づけ（最終アプリとの接続）
- 対応: `springless-roadmap.md` の Phase3
- ここで作るものは、最終アプリ `HomeController` / `AttendanceController` の手動版
- Phase2で動かしたWeb打刻機能を、一覧表示・PRG・責務分離まで拡張する

## 2. Lesson順と機能追加
1. `lesson1.md`: 既存 `HttpServer` をベースにルーティングを整理
2. `lesson2.md`: HTML画面 + POST打刻を追加
3. `lesson3.md`: PRG（POST-Redirect-GET）を導入
4. `lesson4.md`: Service/RepositoryをWebへ接続
5. `lesson5.md`: Controller分割版でPhase3を完成

## Lesson開始ルール
- 全Lessonで同じコードベースを使う: `~/order-management-springboot/practice/springless-final-web`
- Lessonごとに既存コードへ追記して機能を増やす（作り直さない）
- コンソール確認は中間確認として使い、最終判定はブラウザ確認で行う

## 3. 先に読む `src`
- `src/main/java/com/shinesoft/attendance/web/HomeController.java`
- `src/main/java/com/shinesoft/attendance/web/AttendanceController.java`
- `src/main/resources/templates/index.html`
- `src/main/resources/templates/attendances.html`

## 4. ファイル配置ルール
- 手順書: `docs/curriculum/phase3`
- 演習コード: `practice/springless-final-web`
- `docs` 配下に `.java` を作らない

## 5. Phase完了条件
- ブラウザで出勤/退勤できる
- `/attendances` で履歴を表示できる
- PRGの必要性を説明できる
