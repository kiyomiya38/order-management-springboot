# Webアプリ簡易版 学習ガイド

このフォルダは、Spring Boot に入る前に「フレームワークなし」で Webアプリの仕組みを体験する教材です。
Java / HTML / CSS / JavaScript を別々に学んだ後、それらを1つのアプリとして接続します。

## 前提
- `docs/curriculum/java/java-handson` の本編を完了している
- `java-20a-record-enum.md` を完了している
- `java-20b-web-api-prep.md` を完了している
- `docs/curriculum/html_css/html_css.md` を完了している
- `docs/curriculum/javascript/javascript.md` を完了している
- `docs/curriculum/javascript/javascript-fetch-json.md` を完了している
- Git Bash / JDK 17 / ブラウザの DevTools を使える

## 学習順
初学者は次の順で進めます。

| 順番 | 教材 | 扱い | 目的 |
| --- | --- | --- | --- |
| 1 | [lesson1.md](./lesson1.md) | 必修 | 最小Webアプリで、画面入力 -> Java API -> 画面表示を確認する |
| 2 | [lesson2.md](./lesson2.md) | 必修 | `GET` / `POST` / API状態 / 一覧取得 / メモリ保存を整理する |
| 3 | [lesson3.md](./lesson3.md) | 必修 | CRUD、`PATCH`、`DELETE`、画面再描画を体験する |
| 4 | [lesson4.md](./lesson4.md) | 準必修 | 集計、絞り込み、サーバー側バリデーションを理解する |
| 5 | [lesson5.md](./lesson5.md) | 準必修 | 勤怠の状態遷移、履歴、管理画面の考え方を Spring Boot 前に見る |
| 6 | [lesson6-optional-reservation.md](./lesson6-optional-reservation.md) | 任意 | 日付・時刻入力、予約重複チェックを追加で練習する |
| 7 | [bridge-to-springboot.md](./bridge-to-springboot.md) | 必修 | フレームワークなし実装が Spring Boot で何に置き換わるか確認する |

## なぜ Lesson2 をAPI補強にするか
[lesson2.md](./lesson2.md) は `record` / `enum` / `AtomicLong` / `synchronized` / 一覧APIまで扱うため、最初のWebアプリとしては情報量が多いです。
まず [lesson1.md](./lesson1.md) で最小構成を動かし、その後 Lesson2 で API通信と保存処理を整理します。

## 次へ進む条件
Spring Boot へ進む前に、次を説明できる状態にします。

1. HTMLフォームの入力値が JavaScript に渡る流れ
2. `fetch` で Java API を呼び出す流れ
3. `GET` / `POST` / `PATCH` / `DELETE` の使い分け
4. API更新と DOM再描画が別処理である理由
5. サーバー側バリデーションが必要な理由
6. メモリ保存とDB保存の違い
7. `HttpServer` で手書きした処理が Spring Boot で何に置き換わるか

## Lesson別の合格基準
写経が終わったかではなく、次の説明と確認ができるかで判定します。

| 教材 | 合格基準 |
| --- | --- |
| lesson1 | 画面入力が `fetch` で `POST /api/greeting` に送られ、JSONレスポンスで画面が変わる流れを説明できる |
| lesson2 | `GET` / `POST` / HTTPステータス / メモリ保存の役割を説明できる |
| lesson3 | CRUDのうち、作成・一覧取得・更新・削除がそれぞれどのHTTPメソッドに対応するか説明できる |
| lesson4 | サーバー側バリデーションとクライアント側絞り込みの責務の違いを説明できる |
| lesson5 | 勤怠状態遷移と、サーバー側で不正操作を止める理由を説明できる |
| bridge-to-springboot | 手書き実装のどの処理が Spring Boot の Controller / Service / Repository / Jackson に置き換わるか説明できる |

## 注意
- このフォルダでは学習用に、JSONを文字列として組み立てたり、正規表現で最小限だけ読み取ったりします。
- 実務や Spring Boot では、Jackson などのライブラリがJSON変換を担当します。
- ここで覚えるべき中心は「通信の流れ」「役割分担」「業務ルールをどこで守るか」です。
- 共通のエラー対応は [troubleshooting.md](./troubleshooting.md) を確認します。

