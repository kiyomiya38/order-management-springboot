# Phase6 Lesson3: 重複チェックとPRGを導入する

## ロードマップ接続
- 対象: `springless-roadmap.md` の Phase6
- このLessonで増える機能: 重複チェック（社員コードなど）
- Phase完了時に到達する機能: 管理機能と入力バリデーションを画面で運用できる
- 先に読む `src`: web/UserController, web/AdminAttendanceController, web/form/*

## Web先行での確認方法
- 最初に作った完成版UI（見た目のみ）を土台にし、同じコードへ機能を追記する。
- 作業フォルダは固定: `~/order-management-springboot/practice/springless-final-web`
- コンソール確認は中間確認として使い、最終判定はブラウザ操作シナリオで行う。

## 1. 追加する機能（前Lessonからの差分）
- 重複チェック（社員コードなど）

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
- `App.java` に今回の差分を実装する: 重複チェック（社員コードなど）
- コンパイル/起動してブラウザで動作確認する

実行:
```bash
javac -encoding UTF-8 App.java
java App
```

## 5. ブラウザ確認シナリオ（合格条件）
- 重複入力時に登録が拒否される

## 6. このLessonで学ぶ最小文法
- Optional、orElseThrow

## 7. 自己チェック
- 今回追加した機能が、どのクラスに置かれているか説明できる
- 失敗時にどこを見るべきか（Controller/Service/Repository）を説明できる
- `src` の対応箇所を1つ示して、差分を説明できる