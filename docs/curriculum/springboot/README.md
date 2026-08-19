# Spring Boot 学習ガイド

このフォルダは、Java / フロントエンド基礎 / `web-app(簡易版)` を終えた後に進む Spring Boot 本編です。
初学者は、まずサーバーサイドMVCを理解し、その後にREST API、DB変更管理、運用寄りの演習へ進みます。

## コース区分

この教材は、研修期間に応じて次の2コースで使用します。

| コース | 対象 | HTML/CSS/JavaScriptの扱い |
| --- | --- | --- |
| 通常コース | フロントエンドからWebアプリ全体を学ぶ研修 | 前提教材で実装し、`web-app(簡易版)` まで完了する |
| バックエンド短縮コース | Java修了後、Spring Bootのサーバー側実装を優先する研修 | 講師提供コードを指定場所へ配置し、Spring MVCとの接続だけ確認する |

短縮コースでは、既存のHTML/CSS/JavaScriptコードや説明コメントを変更・省略せず使用します。
受講者の評価対象は、画面デザインやDOM実装ではなく、HTTPリクエストをController、Service、Repositoryまで追跡することです。

## 前提

通常コースの前提:

- `docs/curriculum/java/java-handson` の本編を完了している
- `docs/curriculum/html_css/html_css.md` を完了している
- `docs/curriculum/javascript/javascript.md` を完了している
- `docs/curriculum/javascript/javascript-fetch-json.md` を完了している
- `docs/curriculum/web-app(簡易版)/README.md` の必修範囲を完了している
- `docs/curriculum/web-app(簡易版)/bridge-to-springboot.md` を読了している
- Java 17 / Maven 3.9+ / Git Bash を使える

### バックエンド短縮コースの前提

- 下記の「Spring Boot向けJava最短ロードマップ」を完了している
- Javaの全章完了ではなく、ロードマップの「必修」と「必要部分のみ」を説明・実装できる
- [HTTP / フォーム / Thymeleaf最小理解](./prerequisites/http-thymeleaf-minimum.md) を完了している
- Lesson1本編の前にMaven Sandboxを実施し、Maven操作とコンストラクタ注入（DI）を確認している
- Java 17 / Maven 3.9+ / Git Bash を使える

短縮コースでは、`html_css`、`javascript`、`web-app(簡易版)` の実装演習を研修後の発展学習へ回します。
ただし、フォームの送信先、`Model` とThymeleafの対応、`templates` / `static` の配置規約は必修です。

### Spring Boot向けJava最短ロードマップ

このロードマップは、Java全般を網羅することではなく、このフォルダのSpring Boot教材を読解・実装するための前提を最短で揃えることを目的とします。
正本は[JavaハンズオンのSpring Boot向け最短コース](../java/java-handson/README.md#spring-boot向け最短コース)です。
この節も同じ区分を使用し、Spring Boot側だけに古い開始条件を残さないようにします。

「全編」はハンズオンまで実施します。「必要部分のみ」は指定範囲だけ確認し、残りのハンズオンやミニ演習は後へ回せます。

#### 1. 必修

| 順番 | Java教材 | 実施範囲 | Spring Boot開始前に必要な理由 |
| ---: | --- | --- | --- |
| 1 | [Java-01](../java/java-handson/java-01-intro.md) | 全編 | JDK 17、VS Code、Git BashでJavaを実行する |
| 2 | [Java-02](../java/java-handson/java-02-program-flow.md) | 必要部分のみ | Javaソース、`main()`、コンパイルエラーと実行時エラーを区別する |
| 3 | [Java-03](../java/java-handson/java-03-variables-and-types.md) | 全編 | 変数、基本型、`String`、初期化を読む |
| 4 | [Java-04](../java/java-handson/java-04-expressions-and-operators.md) | 全編 | 業務条件で使う比較・論理演算子を読む |
| 5 | [Java-05](../java/java-handson/java-05-class-libraries.md) | 全編 | 勤怠処理で使う`String`、`LocalDate`、`LocalDateTime`を扱う |
| 6 | [Java-06](../java/java-handson/java-06-conditions-and-loops.md) | 全編 | 業務ルールの`if` / `else`と一覧処理の繰り返しを読む |
| 7 | [Java-08](../java/java-handson/java-08-methods.md) | 全編 | Controller、Service、Repositoryのメソッド呼び出しを追う |
| 8 | [Java-09](../java/java-handson/java-09-instances-and-classes.md) | 全編 | EntityやServiceをクラスとインスタンスとして読む |
| 9 | [Java-09A](../java/java-handson/java-09a-string-reference-and-value-comparison.md) | 必要部分のみ | `String`の業務条件を`equals()`で比較する |
| 10 | [Java-10](../java/java-handson/java-10-multi-class-development.md) | 全編 | クラス分割、`package`、`import`を読む |
| 11 | [Java-11](../java/java-handson/java-11-class-mechanisms.md) | 全編 | コンストラクタ注入、`this`、`static final`を読む |
| 12 | [Java-12](../java/java-handson/java-12-encapsulation.md) | 全編 | Entityの`private`フィールド、getter / setterを読む |
| 13 | [Java-16](../java/java-handson/java-16-standard-classes.md) | 必要部分のみ | Entity IDで使う`Long`などのラッパークラスを読む |
| 14 | [Java-17](../java/java-handson/java-17-exceptions.md) | 全編 | Serviceの`throw`とControllerの`try` / `catch`を追う |
| 15 | [Java-18](../java/java-handson/java-18-collections.md) | 全編 | `List<T>`、`Map<K, V>`、ジェネリクスを読む |

#### 2. 必要部分のみ

| Java教材 | 確認する範囲 | 省略できる範囲 |
| --- | --- | --- |
| [Java-02](../java/java-handson/java-02-program-flow.md) | Javaソースの基本構造、`main`、コンパイルエラーと実行時エラーの違い | `javac`操作の反復演習 |
| [Java-09A](../java/java-handson/java-09a-string-reference-and-value-comparison.md) | 「先に覚えるポイント」とStep 1・2。`==`は参照、`equals()`は文字列の値を比較すること | Step 3・4とミニ演習 |
| [Java-16](../java/java-handson/java-16-standard-classes.md) | 「ラッパークラス」とStep 5。`long`と`Long`、ジェネリクスには基本型を直接書けないこと | `toString()`のオーバーライド、`StringBuilder`、`Path`、`Pattern` |

`LocalDate`と`LocalDateTime`は勤怠データで繰り返し使用します。Java-05で日付操作の基本を確認し、Lesson2で実際のEntityとの対応を確認します。
`Optional<T>`はJava短縮範囲に独立した章がないため、Lesson2の「Java補足: `Optional` の最小理解」で必ず補完します。
Mavenの基本操作は、Lesson1本編の前に実施するMaven Sandboxで補完します。

#### 3. Spring Boot開始後に補完する内容

開始前にすべてを詰め込まず、実際に使用するLessonで次の内容を補完します。

| 学習時期 | 初めて本格的に使う内容 | 実施内容 |
| --- | --- | --- |
| Lesson 0 | `enum` | `LedgerType`のコード前に、定数、`valueOf()`、比較方法を確認する |
| Lesson 1 | Maven、アノテーション、コンストラクタ注入 | 本編前に[Maven Sandbox](./lesson01/maven-sandbox/README.md)を実施する |
| Lesson 2直前 | ラムダ式、メソッド参照 | [Java-19](../java/java-handson/java-19-stream-api.md)の「先取り補足」だけを読む |
| Lesson 2 | `Optional`、Repositoryインターフェース、独自unchecked例外 | Lesson 2本文のJava補足で確認する |
| Lesson 5 | インターフェース、`implements`、`throws Exception` | Spring Securityのコード前に最小構文を確認する。必要ならJava-13〜15、Java-17Aを復習する |
| Lesson 6直前 | `record`、Stream API | Java-19とJava-20Aの本文・ハンズオンの必要部分を実施する |
| Lesson 6 | REST、JSON、HTTPステータス、`curl` | Lesson 6本文でコマンドの読み方から確認する |

#### 4. Spring Boot開始後へ回せる教材

次の教材はJava一般の理解を深めるために有用ですが、このSpring Boot教材を開始する最低条件には含めません。

- Java-04A: 型変換と明示キャスト
- Java-06A: `switch` / `do-while` / ラベル付き制御
- Java-07 / 07A: 配列、参照共有の詳細、多次元配列
- Java-11A: コンストラクタ連鎖の詳細
- Java-12A: `protected`とpackage-privateの使い分け
- Java-13 / 13A: 継承、`super`、継承規則の詳細
- Java-14 / 15: 抽象クラス、インターフェース、多態性の実装演習
- Java-16A: 正規表現
- Java-17A: checked例外と複数段階の`throws`
- Java-19: Stream APIの本格演習（Lesson 6直前に必要部分だけ実施）
- Java-20: Javadocの本格読解
- Java-20A: `record`の本格演習（Lesson 6直前に必要部分だけ実施）
- Java-20B: 生の`HttpServer`によるWeb API実装

ただし、Lesson2で独自例外を作る前に「`RuntimeException`を継承すると独自のunchecked例外を作れること」を講師が補足します。
Repositoryなどのインターフェースは「利用側と実装の契約を分ける仕組み」として各Lesson内で説明し、継承・多態性の本格演習はSpring Boot開始後の復習へ回します。

#### 5. Java範囲の完了条件

次をコードまたは口頭で説明できれば、Java全章や省略対象のミニ演習が未完了でもSpring Bootへ進めます。

1. Javaソースをコンパイル・実行し、コンパイルエラーと実行時エラーを区別できる
2. 複数クラスを`package`で分け、コンストラクタから依存オブジェクトを受け取れる
3. `private`フィールドをgetter / setter経由で扱える
4. 引数・戻り値・`LocalDate`・`LocalDateTime`を含むメソッドを読める
5. `if` / `else`、繰り返し、`List<T>`、`Map<K, V>`を使った処理を追える
6. `String`を`==`ではなく`equals()`で比較できる
7. `long`と`Long`の違いを最低限説明し、`List<Long>`のような型を読める
8. unchecked例外を`throw`し、呼び出し側の`try` / `catch`で受け取る流れを説明できる

`enum`、アノテーション、ラムダ式、メソッド参照、Repositoryインターフェース、`record`、Stream API、HTTP操作は、上表の時期にSpring Boot教材と結び付けて学びます。

### バージョン方針
- 教材はSpring Boot `3.5.15` に固定する
- 研修開始前に [Maven Centralの公式メタデータ](https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-dependencies/maven-metadata.xml) で3.x系列の更新を確認する
- メジャーバージョン（4.x）への更新は、教材コードと主要な画面・API・DB動作を別途確認してから行う

## 学習順
初学者は次の順で進めます。

| 順番 | 教材 | 扱い | 目的 |
| --- | --- | --- | --- |
| 0 | [lesson00/lesson0.md](./lesson00/lesson0.md) | 必修 | Webに入る前に、Javaだけでクラス分割と業務ルールを復習する |
| 1 | [lesson01/lesson1.md](./lesson01/lesson1.md) | 必修 | Maven、起動、Controller、Thymeleafの最小構成を作る |
| 2 | [lesson02/lesson2.md](./lesson02/lesson2.md) | 必修 | Entity / Repository / Service / DB連携を実装する |
| 3 | [lesson03/lesson3.md](./lesson03/lesson3.md) | 必修 | 退勤処理と状態遷移、業務ルール違反表示を実装する |
| 4 | [lesson04/lesson4.md](./lesson04/lesson4.md) | 必修 | 勤怠一覧とH2確認で、保存データを追跡できるようにする |
| 5 | [lesson05/lesson5.md](./lesson05/lesson5.md) | 必修 | Lesson5共通準備と5A〜5Cの進め方を確認する |
| 5A | [lesson05/lesson5a-authentication.md](./lesson05/lesson5a-authentication.md) | 必修 | ログイン、認証、認可を実装する |
| 5B | [lesson05/lesson5b-management.md](./lesson05/lesson5b-management.md) | 必修 | ユーザー管理と勤怠管理を実装する |
| 5C | [lesson05/lesson5c-operations.md](./lesson05/lesson5c-operations.md) | 必修 | プロファイル、コード読解、参照整合性を確認する |
| 6 | [lesson06/lesson6.md](./lesson06/lesson6.md) | 必修 | `@RestController` でJSON APIを実装する |
| 7 | [lesson07/lesson7.md](./lesson07/lesson7.md) | 必修 | FlywayでDBスキーマ変更を履歴管理する |
| A | [deployment/virtualbox/README.md](./deployment/virtualbox/README.md) | 環境演習（選択） | 実サーバー風の2VM構成へ移行する |
| B | [deployment/docker-compose/README.md](./deployment/docker-compose/README.md) | 環境演習（選択） | Docker ComposeでApp + DB構成を作る |

### バックエンド短縮コースの追加順序

短縮コースでは、上記Lesson順を維持しながら次を追加します。

1. Lesson0の前後で [HTTP / フォーム / Thymeleaf最小理解](./prerequisites/http-thymeleaf-minimum.md) を実施する
2. Lesson1本編の前にMaven Sandboxを実施し、Maven操作、アノテーション、DIの最小形を確認する
3. Lesson2へ進む前に [SQL・RDB基礎](./lesson02/sql-rdb-basics.md) を実施する
4. Lesson1〜5のHTML/CSS/JavaScriptは、本文のコードを講師提供コードとして配置する
5. Lesson6はブラウザの `fetch` ではなく、`curl` でJSON APIを確認する

## 標準所要時間

新人がコード読解・実装・動作確認・説明レビューまで行う場合の目安です。

| 教材 | 目安 |
| --- | ---: |
| lesson00 | 1〜1.5時間 |
| lesson01 | 4時間 |
| lesson02 | 4〜5時間 |
| lesson03 | 3〜4時間 |
| lesson04 | 3〜4時間 |
| lesson05（5A〜5C合計） | 9〜9.5時間 |
| lesson05A | 2.5時間 |
| lesson05B | 5時間 |
| lesson05C | 1.5〜2時間 |
| lesson06 | 2.5〜3時間 |
| lesson07 | 2〜2.5時間 |
| 環境演習A（VirtualBox） | 4〜6時間（VM準備状況による） |
| 環境演習B（Docker Compose） | 3〜4時間 |

Spring Boot範囲全体は、レビューと予備時間を含めて7〜9研修日を確保します。環境演習はA/Bの片方だけを選択しても構いません。

## なぜ Lesson1〜5 は Thymeleaf か
`web-app(簡易版)` では、主に `fetch + JSON API + DOM更新` で画面を動かしました。
Spring Boot Lesson1〜5では、まずSpring MVCの基本を理解するため、`Controller + Model + Thymeleaf` を中心にします。

バックエンド短縮コースでは、`web-app(簡易版)` の実装経験を前提にしません。
講師提供のThymeleafテンプレートを配置し、`Model` のキー、フォームの送信先、Controllerの戻り値だけをコード上で対応づけます。

この順序にすると、次を分けて理解できます。

| 観点 | Lesson1〜5 | Lesson6 |
| --- | --- | --- |
| 画面表示 | サーバー側でHTMLを作る | APIはJSONを返す |
| Controller | `@Controller` | `@RestController` |
| 値の受け渡し | `Model` / フォーム送信 | DTO / `@RequestBody` / JSON |
| エラー表示 | 画面メッセージ | JSONエラー応答 |

## Spring Bootへ進む前の確認
次を説明できない場合は、`web-app(簡易版)` の復習に戻ります。

1. `HttpServer` の手書きルーティングが、Spring Bootでは `@GetMapping` / `@PostMapping` になること
2. 手書きJSONが、Spring Bootでは Jackson に置き換わること
3. メモリ保存が、Spring Bootでは Repository / DB に置き換わること
4. 画面を返す `@Controller` と、JSONを返す `@RestController` の違い

### バックエンド短縮コースの確認

短縮コースでは、次を説明できればLesson1へ進みます。

1. `GET` と `POST` の違い
2. フォームの `action` / `method` とControllerマッピングの対応
3. `templates` と `static` の違い
4. `model.addAttribute("name", value)` と `${name}` の対応
5. HTML/CSS/JavaScriptは提供コードを配置し、実装自体は評価対象にしないこと

## 補助教材の扱い

- 通常コースでは、Maven操作に不安がある受講者がLesson1の前に [Maven Sandbox](./lesson01/maven-sandbox/README.md) を実施する
- バックエンド短縮コースでは、Maven Sandboxを必修とし、特にMaven操作とコンストラクタ注入（DI）の範囲を確認する
- 各LessonのMarkdownを正本とし、講義・ハンズオンともMarkdownを使用する
- 同じフォルダのPDFは今回の研修では使用しない
- `lesson00/old` 配下はアーカイブであり、現行研修では使用しない
- バックエンド短縮コースでは、`lesson00/additional-topics` のフロントエンド項目は研修後の参考資料とする
- Lesson2前に [SQL・RDB基礎](./lesson02/sql-rdb-basics.md) を必修とする

講師はMarkdownの図とコードを投影し、完成版のデモとライブコーディングを併用して進めます。

## Lesson別の合格基準
写経完了ではなく、動作確認と説明で判定します。

| 教材 | 合格基準 |
| --- | --- |
| lesson00 | クラス分割、メソッド引数、`List`保存、業務ルール判定を説明できる |
| lesson01 | `Controller -> Template` の表示の流れと、Mavenで起動する理由を説明できる |
| lesson02 | `Controller -> Service -> Repository -> DB` の流れを説明し、画面・curl・H2で二重出勤禁止を確認できる |
| lesson03 | 勤怠状態遷移、ログレベルによる出力差、機密情報をログへ出さない理由を説明できる |
| lesson04 | 画面表示データとDB保存データを対応づけて確認できる |
| lesson05 | 共通準備を完了し、5A〜5Cを同じ `stages/lesson05` で進める理由を説明できる |
| lesson05A | 認証・認可の違いとSecurity設定を説明できる |
| lesson05B | ユーザー作成・勤怠更新をControllerからRepositoryまでコードで追跡できる |
| lesson05C | プロファイル差分、主要コードの責務、参照整合性による削除禁止を説明できる |
| lesson06 | DTOとJSONエラー応答を説明し、APIの正常系・例外系をコードと実行結果で追跡できる |
| lesson07 | Flywayの `V1__` / `V2__` と `ddl-auto: validate` の役割を説明できる |
| 環境演習A | app-vm / db-vm / Nginx / systemd / MariaDB の通信経路を説明できる |
| 環境演習B | Dockerfile、Compose、Volume、環境変数によるDB接続を説明できる |

## 進め方
- Lesson1〜4、Lesson5共通準備、Lesson5A、5B、5Cは順番を飛ばさない
- Lesson6は、Lesson1〜5でMVCとService層を理解してから実施する
- Lesson7はLesson6を完了してから実施する
- 環境演習A/Bは実行環境の準備が重いため、教室環境に合わせて片方だけ実施してもよい
- 迷った場合は `docs/curriculum/java-to-springboot-roadmap.md` と `docs/curriculum/curriculum-assessment-guide.md` を確認する
- 講師は配布前に [INSTRUCTOR_CHECKLIST.md](./INSTRUCTOR_CHECKLIST.md) を実施する
