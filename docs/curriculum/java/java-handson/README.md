# Java ハンズオン（分割版）

関連資料:
- [Java初学者 -> Spring Boot 実務導入ロードマップ](../../java-to-springboot-roadmap.md)
- [カリキュラム評価ガイド](../../curriculum-assessment-guide.md)
- [Java研修 講師事前チェック](./INSTRUCTOR_CHECKLIST.md)

## 実行環境
- この教材のコマンドは Git Bash で実行する前提
- `mkdir -p`、`~/order-management-springboot/...`、`*.java` などは Git Bash の書き方
- PowerShell や cmd では一部のコマンド表記が異なる
- 前提環境は JDK 17（17.x）
- Java-21 の JUnit 演習のみ Maven が必要

## 本編の学習順
1. [Java-01 ハンズオン: Javaをはじめよう](./java-01-intro.md)
2. [Java-02 ハンズオン: プログラムの書き方](./java-02-program-flow.md)
3. [Java-03 ハンズオン: 変数と型](./java-03-variables-and-types.md)
4. [Java-04 ハンズオン: 式と演算子](./java-04-expressions-and-operators.md)
5. [Java-05 ハンズオン: 代表的なクラスライブラリ](./java-05-class-libraries.md)
6. [Java-06 ハンズオン: 条件分岐と繰り返し](./java-06-conditions-and-loops.md)
7. [Java-07 ハンズオン: 配列](./java-07-arrays.md)
8. [Java-08 ハンズオン: メソッド](./java-08-methods.md)
9. [Java-09 ハンズオン: インスタンスとクラス](./java-09-instances-and-classes.md)
10. [Java-10 ハンズオン: 複数クラスを用いた開発](./java-10-multi-class-development.md)
11. [Java-11 ハンズオン: さまざまなクラス機構](./java-11-class-mechanisms.md)
12. [Java-12 ハンズオン: カプセル化](./java-12-encapsulation.md)
13. [Java-13 ハンズオン: 継承](./java-13-inheritance.md)
14. [Java-14 ハンズオン: 高度な継承](./java-14-advanced-inheritance.md)
15. [Java-15 ハンズオン: 多態性](./java-15-polymorphism.md)
16. [Java-16 ハンズオン: Javaを支える標準クラス](./java-16-standard-classes.md)
17. [Java-17 ハンズオン: 例外](./java-17-exceptions.md)
18. [Java-18 ハンズオン: コレクション](./java-18-collections.md)
19. [Java-19 ハンズオン: Stream API](./java-19-stream-api.md)
20. [Java-20 ハンズオン: Oracle Javadocの読み方](./java-20-javadoc-reading.md)

## Spring Bootへ進む場合の追加必修

本編に加えて次を実施します。Java-04AとJava-17Aは、後続教材で実際に使用する型変換とchecked例外を補います。

- Java-04の後: [Java-04A 型変換とキャスト](./java-04a-type-conversion-and-cast.md)
- Java-17の後: [Java-17A 例外の分類とthrows](./java-17a-exception-types-and-throws.md)
- Java-20の後: [Java-20A record / enum 入門](./java-20a-record-enum.md)
- Java-20Aの後: [Java-20B Web API前準備（HttpServer + POST + MessageStore）](./java-20b-web-api-prep.md)
- Java-20Bの後: [Java-21 JUnit 5基礎](./java-21-junit-basics.md)

Spring Bootまで進む場合の順序は、`Java-01〜04 -> 04A -> Java-05〜17 -> 17A -> Java-18〜20 -> 20A -> 20B -> Java-21`です。
Java-09完了後の[Java-09A Stringの参照比較と値比較](./java-09a-string-reference-and-value-comparison.md)も強く推奨します。

`record` / `enum` / `HttpServer` / HTTPステータス / メモリ保存は、Webアプリ教材で前提になります。

### バックエンド短縮コースでの扱い

HTML/CSS、JavaScript、`web-app(簡易版)` の実装演習を省略してSpring Bootへ進む場合も、Java-04A、Java-17A、Java-20A、Java-20B、Java-21は必修です。

- Java-20A: Spring Bootで使用する `enum` とREST APIの値オブジェクトを読む準備
- Java-20B: `GET` / `POST` / HTTPステータス / `curl` を理解するための最小Web準備
- Java-21: MavenとJUnitを使い、Spring Bootのテストへ接続する準備

Java-20BのHTMLは講師提供コードとして配置し、HTML自体の実装は評価しません。
受講者は、HTTPメソッド、URL、ステータス、Java側の処理を説明できる状態にします。

## 補講（不足項目補完 / 任意）
- [Java-06A 補講: switch / do-while / ラベル付き制御](./java-06a-advanced-control-flow.md)
- [Java-07A 補講: 参照型と多次元配列](./java-07a-reference-types-and-multidimensional-arrays.md)
- [Java-11A 補講: コンストラクタ連鎖（this / デフォルトコンストラクタ）](./java-11a-constructor-chaining.md)
- [Java-12A 補講: アクセス修飾子の使い分け](./java-12a-access-modifiers.md)
- [Java-13A 補講: 継承ルールの深掘り（super / 単一継承 / final）](./java-13a-inheritance-rules.md)
- [Java-16A 補講: 正規表現の基礎（メタ文字とエスケープ）](./java-16a-regex-basics.md)

## 読み方のルール
- `次の内容で作成` は、新しいファイルをその内容で作るという意味
- `次の内容に更新` は、前のコード全体を置き換えるという意味
- `期待出力例` は、画面に表示される内容の例
- `期待結果` は、コンパイル成功、エラー確認、挙動の違いなど、画面出力以外も含む確認結果

## 解答例
- Java-01〜Java-19 のミニ演習解答は `ミニ演習解答/` 配下に配置
- Java-20AとJava-20Bのミニ演習解答も `ミニ演習解答/` 配下に配置
- Java-20 と Java-21 は本文内に `解答例` セクションを含むため、別ファイルは作成していない

## 配布前確認

講師は[Java研修 講師事前チェック](./INSTRUCTOR_CHECKLIST.md)に従い、次のコマンドをリポジトリルートで実行します。

```powershell
powershell -ExecutionPolicy Bypass -File scripts/verify-java-handson.ps1
```

## 方針
- `docs/curriculum/java/java.md` と同じ進行スタイル
- 実務で使う内容を優先
- 各章を独立した markdown ファイルで管理
