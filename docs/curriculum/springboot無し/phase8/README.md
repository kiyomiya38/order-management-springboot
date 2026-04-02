# Phase8: 永続化 + リファクタリング + Spring対応づけ

## 1. 位置づけ（最終アプリとの接続）
- 対応: `springless-roadmap.md` の Phase8
- ここで作るものは、最終アプリ運用を意識した仕上げ
- InMemory中心の実装を、永続化可能な構成へ拡張する

## 2. Lesson順と機能追加
1. `lesson1.md`: 重複ロジックの問題を可視化
2. `lesson2.md`: 認証・認可ガードを共通化
3. `lesson3.md`: Form/Request/Validator/Service責務を整理
4. `lesson4.md`: AppConfig + Routerで全体配線を整理
5. `lesson5.md`: Plain Java と Spring の対応表を完成

## Lesson開始ルール
- 全Lessonで同じコードベースを使う: `~/order-management-springboot/practice/springless-final-web`
- Lessonごとに既存コードへ追記して機能を増やす（作り直さない）
- コンソール確認は中間確認として使い、最終判定はブラウザ確認で行う

## 3. 先に読む `src`
- `src/main/resources/application.yml`
- `src/main/java/com/shinesoft/attendance/repository/*`
- `src/main/java/com/shinesoft/attendance/config/*`
- `src/main/java/com/shinesoft/attendance/web/*`

## 4. ファイル配置ルール
- 手順書: `docs/curriculum/phase8`
- 演習コード: `practice/springless-final-web`
- `docs` 配下に `.java` を作らない

## 5. Phase完了条件
- 責務分離と依存関係を説明できる
- Spring版との対応関係（Routing/DI/Validation/Security）を説明できる
- DB永続化へ切り替えるための変更点を列挙できる
