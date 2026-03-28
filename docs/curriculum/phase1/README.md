# Phase1: 完成版UI先行 + ドメイン骨格

## 1. 位置づけ（最終アプリとの接続）
- 対応: `springless-roadmap.md` の Phase1
- ここでは「見た目は最終形、処理はダミー」の状態を先に作る
- 最終アプリの `domain` / `exception` を読む入口を作る

## 2. Lesson順と機能追加
1. `lesson1.md`: 完成版UIを静的表示（トップ/ログイン/一覧/管理画面）
2. `lesson2.md`: `User`/`Attendance` を導入し、画面表示値をドメインから作る
3. `lesson3.md`: コンストラクタ + `private final` で値の持ち方を固める
4. `lesson4.md`: `AttendanceStatus` と `BusinessException` を導入する
5. `lesson5.md`: ドメイン最小構成をまとめ、Phase2の打刻処理接続準備を完了する

## 3. Lesson開始ルール
- 全Lessonで同じコードベースを使う: `~/order-management-springboot/practice/springless-final-web`
- Lessonごとに既存コードへ追記して機能を増やす（作り直さない）
- コンソール確認は中間確認として使い、最終判定はブラウザ確認で行う

## 4. 先に読む `src`
- `src/main/java/com/shinesoft/attendance/domain/User.java`
- `src/main/java/com/shinesoft/attendance/domain/Attendance.java`
- `src/main/java/com/shinesoft/attendance/domain/AttendanceStatus.java`
- `src/main/java/com/shinesoft/attendance/exception/BusinessException.java`
- `src/main/resources/templates/*.html`

## 5. ファイル配置ルール
- 手順書: `docs/curriculum/phase1`
- 演習コード: `practice/springless-final-web`
- `docs` 配下に `.java` を作らない

## 6. Phase完了条件
- 5画面がブラウザで表示できる
- 画面表示値の一部をドメインクラスから生成できる
- `BusinessException` の用途を説明できる
