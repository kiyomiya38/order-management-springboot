# Phase5 Lesson5: 総まとめ（AppConfig + SessionStore + 認可）

## ロードマップ接続
- 対象: `springless-roadmap.md` の Phase5
- このLessonで増える機能: Phase5統合（認証認可完成）
- Phase完了時に到達する機能: ログイン/ログアウトと認可を画面で確認できる
- 先に読む `src`: config/SecurityConfig, web/AuthController, web/HomeController

## Web先行での確認方法
- 最初に作った完成版UI（見た目のみ）を土台にし、同じコードへ機能を追記する。
- 作業フォルダは固定: `~/order-management-springboot/practice/springless-final-web`
- コンソール確認は中間確認として使い、最終判定はブラウザ操作シナリオで行う。

## 1. 追加する機能（前Lessonからの差分）
- Phase5統合（認証認可完成）

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
- `App.java` に今回の差分を実装する: Phase5統合（認証認可完成）
- コンパイル/起動してブラウザで動作確認する

実行:
```bash
javac -encoding UTF-8 App.java
java App
```

## 5. ブラウザ確認シナリオ（合格条件）
- ログイン -> 利用 -> ログアウトが一連で通る

## 6. このLessonで学ぶ最小文法
- セキュリティフロー整理

## 7. 自己チェック
- 今回追加した機能が、どのクラスに置かれているか説明できる
- 失敗時にどこを見るべきか（Controller/Service/Repository）を説明できる
- `src` の対応箇所を1つ示して、差分を説明できる