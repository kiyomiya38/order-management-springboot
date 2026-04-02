# Phase7: テスト導入（ユニットテスト）

## 1. 位置づけ（最終アプリとの接続）
- 対応: `springless-roadmap.md` の Phase7
- ここで作るものは、最終アプリ `AttendanceServiceTest` の手動版発想
- 目視確認に頼らず、業務ルールをテストで固定する

## 2. Lesson順と機能追加
1. `lesson1.md`: 目視確認だけの弱点を体感
2. `lesson2.md`: ミニassert関数でテストの基本を理解
3. `lesson3.md`: 例外テスト（メッセージ検証）を追加
4. `lesson4.md`: 依存差し替えでService単体を検証
5. `lesson5.md`: 回帰テストスイートとして統合

## Lesson開始ルール
- 全Lessonで同じコードベースを使う: `~/order-management-springboot/practice/springless-final-web`
- Lessonごとに既存コードへ追記して機能を増やす（作り直さない）
- コンソール確認は中間確認として使い、最終判定はブラウザ確認で行う

## 3. 先に読む `src`
- `src/test/java/com/shinesoft/attendance/service/AttendanceServiceTest.java`
- `src/main/java/com/shinesoft/attendance/service/AttendanceService.java`
- `src/main/java/com/shinesoft/attendance/service/UserService.java`

## 4. ファイル配置ルール
- 手順書: `docs/curriculum/phase7`
- 演習コード: `practice/springless-final-web`
- `docs` 配下に `.java` を作らない

## 5. Phase完了条件
- 正常系・異常系テストが動く
- 仕様を壊す変更でテストが失敗する
- テストが「仕様書」として機能することを説明できる
