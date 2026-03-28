# Phase6: 入力バリデーションと管理機能拡張

## 1. 位置づけ（最終アプリとの接続）
- 対応: `springless-roadmap.md` の Phase6
- ここで作るものは、最終アプリ `UserController` / `AdminAttendanceController` の入力検証思想
- 不正入力を受けた時に「止める・理由を返す」を実装する

## 2. Lesson順と機能追加
1. `lesson1.md`: 必須チェック・文字数チェック
2. `lesson2.md`: `Validator` クラスで検証を分離
3. `lesson3.md`: 重複チェック + PRGを導入
4. `lesson4.md`: ADMIN専用ユーザー登録の検証
5. `lesson5.md`: AppConfigに統合してバリデーション総仕上げ

## Lesson開始ルール
- 全Lessonで同じコードベースを使う: `~/order-management-springboot/practice/springless-final-web`
- Lessonごとに既存コードへ追記して機能を増やす（作り直さない）
- コンソール確認は中間確認として使い、最終判定はブラウザ確認で行う

## 3. 先に読む `src`
- `src/main/java/com/shinesoft/attendance/web/UserController.java`
- `src/main/java/com/shinesoft/attendance/web/form/UserForm.java`
- `src/main/java/com/shinesoft/attendance/web/form/AdminAttendanceForm.java`
- `src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java`

## 4. ファイル配置ルール
- 手順書: `docs/curriculum/phase6`
- 演習コード: `practice/springless-final-web`
- `docs` 配下に `.java` を作らない

## 5. Phase完了条件
- 必須/文字数/形式/重複チェックができる
- エラー時に入力値を保持して再表示できる
- 管理機能の入力を安全に扱える
