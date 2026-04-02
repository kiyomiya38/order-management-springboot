# Phase2: 打刻機能（出勤/退勤）を実装

## 1. 位置づけ（最終アプリとの接続）
- 対応: `springless-roadmap.md` の Phase2
- ここで作るものは、最終アプリ `AttendanceService` の業務ルール部分
- Phase1で作成したWeb画面に打刻機能を接続し、ブラウザで状態遷移を確認する

## 2. Lesson順と機能追加
1. `lesson1.md`: Repository + Service の最小形を作る
2. `lesson2.md`: 複数ユーザーの勤怠一覧を扱う
3. `lesson3.md`: Repositoryインターフェースを導入する
4. `lesson4.md`: ユーザー切り替えメニューを追加する
5. `lesson5.md`: Web打刻機能の業務ロジックを完成させる

## Lesson開始ルール
- 全Lessonで同じコードベースを使う: `~/order-management-springboot/practice/springless-final-web`
- Lessonごとに既存コードへ追記して機能を増やす（作り直さない）
- コンソール確認は中間確認として使い、最終判定はブラウザ確認で行う

## 3. 先に読む `src`
- `src/main/java/com/shinesoft/attendance/service/AttendanceService.java`
- `src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java`
- `src/main/java/com/shinesoft/attendance/repository/UserRepository.java`

## 4. ファイル配置ルール
- 手順書: `docs/curriculum/phase2`
- 演習コード: `practice/springless-final-web`
- `docs` 配下に `.java` を作らない

## 5. Phase完了条件
- ブラウザ操作で未出勤で退勤・二重出勤などの業務エラーを再現できる
- `Repository` と `Service` の責務を説明できる
- Phase3のWeb拡張でも業務ルールを再利用できる状態になっている
