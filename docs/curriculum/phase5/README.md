# Phase5: ログイン/ログアウトと認可

## 1. 位置づけ（最終アプリとの接続）
- 対応: `springless-roadmap.md` の Phase5
- ここで作るものは、最終アプリ `SecurityConfig` / `AuthController` の手動版
- Cookieセッションで「誰がログイン中か」を管理する

## 2. Lesson順と機能追加
1. `lesson1.md`: セッションなしの最小ログイン判定
2. `lesson2.md`: Cookieセッションでログイン状態保持
3. `lesson3.md`: `ADMIN` / `USER` ロール認可
4. `lesson4.md`: 認可付き勤怠機能（USER打刻 / ADMIN一覧）
5. `lesson5.md`: セッション + 認可を統合してPhase5完成

## Lesson開始ルール
- 全Lessonで同じコードベースを使う: `~/order-management-springboot/practice/springless-final-web`
- Lessonごとに既存コードへ追記して機能を増やす（作り直さない）
- コンソール確認は中間確認として使い、最終判定はブラウザ確認で行う

## 3. 先に読む `src`
- `src/main/java/com/shinesoft/attendance/config/SecurityConfig.java`
- `src/main/java/com/shinesoft/attendance/web/AuthController.java`
- `src/main/java/com/shinesoft/attendance/web/HomeController.java`

## 4. ファイル配置ルール
- 手順書: `docs/curriculum/phase5`
- 演習コード: `practice/springless-final-web`
- `docs` 配下に `.java` を作らない

## 5. Phase完了条件
- 未ログインで保護URLへアクセスできない
- 一般ユーザーで管理URLへアクセスできない
- ログアウト後にセッションが無効になる
