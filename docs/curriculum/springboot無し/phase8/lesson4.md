# Phase8 Lesson4: AppConfig + Router で全体配線を見える化する

## ロードマップ接続
- 対象: `springless-roadmap.md` の Phase8
- このLessonで増える機能: 最終リファクタリング
- Phase完了時に到達する機能: 永続化・最終整理・Spring対応づけを説明できる
- 先に読む `src`: application.yml, repository/*, domain/*

## Web先行での確認方法
- 最初に作った完成版UI（見た目のみ）を土台にし、同じコードへ機能を追記する。
- 作業フォルダは固定: `~/order-management-springboot/practice/springless-final-web`
- コンソール確認は中間確認として使い、最終判定はブラウザ操作シナリオで行う。

## 1. 追加する機能（前Lessonからの差分）
- 最終リファクタリング

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
- `App.java` に今回の差分を実装する: 最終リファクタリング
- コンパイル/起動してブラウザで動作確認する

実行:
```bash
javac -encoding UTF-8 App.java
java App
```

## 5. ブラウザ確認シナリオ（合格条件）
- 重複削減後もブラウザシナリオが通る

## 6. このLessonで学ぶ最小文法
- コード整理

## 7. 自己チェック
- 今回追加した機能が、どのクラスに置かれているか説明できる
- 失敗時にどこを見るべきか（Controller/Service/Repository）を説明できる
- `src` の対応箇所を1つ示して、差分を説明できる