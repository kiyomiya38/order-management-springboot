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

## 標準コースの学習順

Java全般を順番に学ぶ場合は、次の本編をすべて実施します。
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

## Spring Boot向け最短コース

Spring Boot教材を開始するために、Javaの全機能を先に終える必要はありません。この最短コースでは、[Spring Boot Lesson 0〜7](../../springboot/README.md#学習順)に実際に登場するコードを確認し、次の基準で学習時期を分けています。

- Lesson 0〜2を読むために繰り返し使うJava基礎は、Spring Boot開始前に学ぶ
- Spring固有の仕組みと一緒に学んだ方が分かりやすい内容は、各Lesson内で補完する
- Lesson 5・6で初めて必要になる内容は、Spring Boot開始条件にせず、そのLessonの直前に学ぶ
- Java一般として有用でも、Spring Boot教材に直接必要ない発展演習は後へ回す

HTTP、Thymeleaf、Maven Sandboxを含むSpring Boot側の進行条件は、[Spring Boot学習ガイド](../../springboot/README.md)で確認します。この節では「Spring Bootを始める前に必要なJava知識」だけを定めます。

### 1. Spring Boot開始前の学習順

「全編」はハンズオンまで実施します。「必要部分のみ」は表に書かれた範囲だけ確認し、ハンズオンとミニ演習の残りは後へ回せます。

| 順番 | Java教材 | 実施範囲 | Spring Boot開始前に必要な理由 |
| ---: | --- | --- | --- |
| 1 | [Java-01](./java-01-intro.md) | 全編 | JDK 17、VS Code、Git BashでJavaを実行する |
| 2 | [Java-02](./java-02-program-flow.md) | 必要部分のみ | Javaソース、`main()`、コンパイルエラーと実行時エラーを区別する |
| 3 | [Java-03](./java-03-variables-and-types.md) | 全編 | 変数、基本型、`String`、初期化を読む |
| 4 | [Java-04](./java-04-expressions-and-operators.md) | 全編 | 業務条件で使う比較・論理演算子を読む |
| 5 | [Java-05](./java-05-class-libraries.md) | 全編 | 勤怠処理で使う`String`、`LocalDate`、`LocalDateTime`を扱う |
| 6 | [Java-06](./java-06-conditions-and-loops.md) | 全編 | 業務ルールの`if` / `else`と一覧処理の繰り返しを読む |
| 7 | [Java-08](./java-08-methods.md) | 全編 | Controller、Service、Repositoryのメソッド呼び出しを追う |
| 8 | [Java-09](./java-09-instances-and-classes.md) | 全編 | EntityやServiceをクラスとインスタンスとして読む |
| 9 | [Java-09A](./java-09a-string-reference-and-value-comparison.md) | 必要部分のみ | `String`の業務条件を`equals()`で比較する |
| 10 | [Java-10](./java-10-multi-class-development.md) | 全編 | クラス分割、`package`、`import`を読む |
| 11 | [Java-11](./java-11-class-mechanisms.md) | 全編 | コンストラクタ注入、`this`、`static final`を読む |
| 12 | [Java-12](./java-12-encapsulation.md) | 全編 | Entityの`private`フィールド、getter / setterを読む |
| 13 | [Java-16](./java-16-standard-classes.md) | 必要部分のみ | Entity IDで使う`Long`などのラッパークラスを読む |
| 14 | [Java-17](./java-17-exceptions.md) | 全編 | Serviceの`throw`とControllerの`try` / `catch`を追う |
| 15 | [Java-18](./java-18-collections.md) | 全編 | `List<T>`、`Map<K, V>`、ジェネリクスを読む |

必要部分だけ実施する教材の範囲:

| 教材 | 開始前に確認する範囲 | 後へ回せる範囲 |
| --- | --- | --- |
| [Java-02](./java-02-program-flow.md) | Javaソースの基本構造、`main()`、コンパイルと実行、2種類のエラー | `javac`操作を繰り返す発展演習 |
| [Java-09A](./java-09a-string-reference-and-value-comparison.md) | 「先に覚えるポイント」とStep 1・2。`==`は参照、`equals()`は文字列の値を比較すること | Step 3・4とミニ演習 |
| [Java-16](./java-16-standard-classes.md) | 「ラッパークラス」とStep 5。`long`と`Long`、ジェネリクスには基本型を直接書けないこと | `toString()`のオーバーライド、`StringBuilder`、`Path`、`Pattern` |

### 2. Spring Boot教材との対応

開始前にすべてを詰め込まず、実際に使用する場所で次の内容を補完します。

| 学習時期 | Spring Bootで登場する内容 | 学び方 |
| --- | --- | --- |
| Lesson 0 | `enum`、`List`、`Map`、業務条件 | `enum`はLesson 0の`LedgerType`で最小構文を確認する。Java-20A全編はまだ不要 |
| Lesson 1 | Maven、コンストラクタ注入 | 必修の[Maven Sandbox](../../springboot/lesson01/maven-sandbox/README.md)で実行する |
| Lesson 2 | `Optional`、ラムダ式、メソッド参照、独自unchecked例外、Repositoryインターフェース | Lesson 2開始前に[Java-19](./java-19-stream-api.md)の「先取り補足」だけ読み、ラムダ式とメソッド参照の形を確認する。`Optional`と`BusinessException extends RuntimeException`はLesson 2内で学ぶ |
| Lesson 3・4 | 状態遷移、`LocalDateTime`、`List<Attendance>` | 開始前に学んだJava-05、Java-06、Java-18を実コードへ結び付ける |
| Lesson 5 | インターフェース、継承、`throws Exception` | Spring Securityのコードと一緒に最小概念を確認する。必要ならJava-13〜15、Java-17Aを復習する |
| Lesson 6 | `record`、Stream API、REST、JSON、HTTPステータス、`curl` | Lesson 6へ進む直前にJava-19とJava-20Aの必要部分を実施する。HTTP操作はLesson 6内で確認する |
| Lesson 7 | `Integer`などのラッパークラス、DB操作 | Java-16で確認した基本型とラッパークラスの違いを再確認する |

### 3. 開始前必修から外す教材

次の教材は有用ですが、Spring Bootを開始する最低条件には含めません。

| 教材 | 開始前必修から外す理由 | 推奨時期 |
| --- | --- | --- |
| [Java-04A](./java-04a-type-conversion-and-cast.md) | Spring Boot Lesson 0〜7では、文字列の手動変換や明示キャストをほぼ使用しない | 型変換が必要になった時点 |
| [Java-07](./java-07-arrays.md) / [Java-07A](./java-07a-reference-types-and-multidimensional-arrays.md) | Spring Boot教材では配列より`List`を中心に使用する | Java一般の復習時 |
| [Java-06A](./java-06a-advanced-control-flow.md) | ラベル付き制御などをSpring Boot教材で使用しない | Java一般の復習時 |
| [Java-11A](./java-11a-constructor-chaining.md) / [Java-12A](./java-12a-access-modifiers.md) | コンストラクタ連鎖や詳細なアクセス範囲は開始時点では不要 | Entity設計を深める時点 |
| [Java-13](./java-13-inheritance.md) / [Java-13A](./java-13a-inheritance-rules.md) / [Java-14](./java-14-advanced-inheritance.md) / [Java-15](./java-15-polymorphism.md) | Springで必要な継承・インターフェースの最小概念は各Lesson内で説明する | Lesson 5前後の復習 |
| [Java-16A](./java-16a-regex-basics.md) | Spring Boot教材の入力検証はBean Validationを中心に扱う | 独自形式を検証する時点 |
| [Java-17A](./java-17a-exception-types-and-throws.md) | Lesson 2で必要なのはunchecked例外。独自checked例外の複数段階伝播は開始前には不要 | `throws Exception`が増えるLesson 5前 |
| [Java-19](./java-19-stream-api.md) | Streamを本格的に使用するのはLesson 6。Lesson 2のラムダ式・メソッド参照は本文内で補足する | Lesson 6直前 |
| [Java-20](./java-20-javadoc-reading.md) | API仕様の調べ方は有用だが、Spring Boot開始を妨げる前提知識ではない | APIを自分で調査する段階 |
| [Java-20A](./java-20a-record-enum.md) | `enum`はLesson 0で最小形を説明し、`record`を使うのはLesson 6 | Lesson 6直前 |
| [Java-20B](./java-20b-web-api-prep.md) | 生の`HttpServer`実装はSpring MVCの開始条件ではない。GET / POSTはSpring側のHTTP前提教材で扱う | `web-app（簡易版）`を実装する標準コース、または発展学習 |
| [Java-21](./java-21-junit-basics.md) | Spring Boot研修の必修範囲では使用しない | JUnitを別途学習する場合 |

### 4. 最短コースの完了条件

次をコードまたは口頭で説明できれば、後回し対象の章やミニ演習が未完了でもSpring Boot Lesson 0へ進めます。

1. Javaソースをコンパイル・実行し、コンパイルエラーと実行時エラーを区別できる
2. 複数クラスを`package`で分け、コンストラクタから依存オブジェクトを受け取れる
3. `private`フィールドをgetter / setter経由で扱える
4. 引数・戻り値・`LocalDate`・`LocalDateTime`を含むメソッドを読める
5. `if` / `else`、繰り返し、`List<T>`、`Map<K, V>`を使った処理を追える
6. `String`を`==`ではなく`equals()`で比較できる
7. `long`と`Long`の違いを最低限説明し、`List<Long>`のような型を読める
8. unchecked例外を`throw`し、呼び出し側の`try` / `catch`で受け取る流れを説明できる

checked例外の詳細、複数段階の`throws`、`record`、Stream API、生のHTTPサーバーは、Spring Boot開始前の完了条件には含めません。

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
- 各Stepで更新するJavaファイルは、原則として省略せず全コードを掲載する
- 前のStepから追加・変更した主な範囲は、`// ===== Step Xで追加・変更 =====` のコメントで示す
- 複数ファイルを使う章では、そのStepで変更するファイルだけ全コードを掲載し、変更しないファイルは再掲しない
- コンパイルエラーの確認、コマンド実行、Javadoc読解など、コード全体の再掲が学習目的にならないStepは例外とする
- `期待出力例` は、そのStepまたはレベルで画面に表示される出力全体の例
- `確認対象の出力（抜粋）` は、既存出力を省略し、その課題で特に確認する行だけを示す
- `期待結果` は、コンパイル成功、エラー確認、挙動の違いなど、画面出力以外も含む確認結果

### 初学者向けの説明基準

各章は、文章量を一律に増やすのではなく、次の順序で「何を変え、なぜ変えるか」を確認できる構成にします。

1. その章で初めて使う書式を、コードより先に分解して説明する
2. 可能な場合は、学習前の書き方と学習後の書き方を比較する
3. Stepの冒頭で、前のStepから何を追加・変更するかを説明する
4. 完成コード内のコメントで、追加・変更した主な範囲を示す
5. 実行前に注目する値や処理を示し、実行後に結果から分かることを説明する
6. 新しい書式が常に優れているとは限らない場合は、使い分けも説明する

コードを読むときは、次の順番で確認します。

```text
前のStepでできていたこと
  ↓
今回追加・変更する箇所
  ↓
実行すると何が変わるか
  ↓
その書き方を使う理由と使い分け
```

## 解答例
- Java-01〜Java-19 のミニ演習解答は `ミニ演習解答/` 配下に配置
- Java-20AとJava-20Bのミニ演習解答も `ミニ演習解答/` 配下に配置
- Java-20 と Java-21 は本文内に `解答例` セクションを含むため、別ファイルは作成していない
- ミニ演習解答は、各レベルについて「引き継ぐ内容」「変更・追記位置」「確認出力」を明記する
- 1ファイルで完結する演習は、原則として各レベルの解答にそのレベル完了時の全コードを掲載する
- コードが長く変更箇所が少ない場合は、「変更するファイル」「追加・変更位置」「前レベルから残す内容」を明記し、最終レベルの全コードまたは独立して適用できる変更コードを掲載する
- 複数ファイルの演習は、そのレベルで変更するファイルの全コードを優先し、変更しないファイルは再掲しない
- 前のレベルから追加・変更した主な範囲は、コード内のコメントで示す
- 一時的にエラーを確認するレベルは、変更前へ戻す箇所と、次のレベルが開始できる状態を明記する

## 配布前確認

講師は[Java研修 講師事前チェック](./INSTRUCTOR_CHECKLIST.md)に従い、次のコマンドをリポジトリルートで実行します。

```powershell
powershell -ExecutionPolicy Bypass -File scripts/verify-java-handson.ps1
```

## 方針
- `docs/curriculum/java/java.md` と同じ進行スタイル
- 実務で使う内容を優先
- 各章を独立した markdown ファイルで管理
