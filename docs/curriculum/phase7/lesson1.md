# Phase7 Lesson1: 目視確認だけだと何が困るか

## ロードマップ接続
- 対象: `springless-roadmap.md` の Phase7
- このLessonで増える機能: テスト実行基盤を導入
- Phase完了時に到達する機能: 主要業務ルールをテストで固定できる
- 先に読む `src`: test/.../AttendanceServiceTest, service/AttendanceService

## Web先行での確認方法
- 最初に作った完成版UI（見た目のみ）を土台にし、同じコードへ機能を追記する。
- 作業フォルダは固定: `~/order-management-springboot/practice/springless-final-web`
- コンソール確認は中間確認として使い、最終判定はブラウザ操作シナリオで行う。

## 1. 追加する機能（前Lessonからの差分）
- テスト実行基盤を導入

## 2. 実施手順（コマンド）
```bash
cd ~/order-management-springboot
mkdir -p ~/order-management-springboot/practice/springless-final-web
cd ~/order-management-springboot/practice/springless-final-web
```

## 3. 変更対象ファイル
- `App.java`（必要に応じて `AppTest.java` も追加）
- 既存ファイルを編集し、機能を追記する（新規Lessonフォルダは作らない）

## 4. 実装手順
- 前Lessonの動作をブラウザで確認する
- `App.java` に今回の差分を実装する: テスト実行基盤を導入
- コンパイル/起動してブラウザで動作確認する

実行:
```bash
javac -encoding UTF-8 App.java
java App
```

## 5. ブラウザ確認シナリオ（合格条件）
- テストコマンドで結果を確認できる

## 6. このLessonで学ぶ最小文法
- JUnit基本

## 7. 自己チェック
- 今回追加した機能が、どのクラスに置かれているか説明できる
- 失敗時にどこを見るべきか（Controller/Service/Repository）を説明できる
- `src` の対応箇所を1つ示して、差分を説明できる