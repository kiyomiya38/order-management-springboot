# 理解チェックと解答

## この資料の使い方

この資料は、[JavaからWeb・データベースへ進むための基礎](./00-java-web-database-primer.md)と[Spring Boot概要](./01-spring-boot-overview.md)の理解を確認するためのものです。

答えを先に暗記せず、次の順で使ってください。

1. 問いを読む
2. 図や完成版コードを見ながら、自分の言葉で答える
3. 模範回答と比べる
4. 違った部分を本文または[用語集](./glossary.md)で確認する

「必須」は次の工程へ進む前に説明したい内容です。「発展」は実装後に戻ればよく、最初から説明できなくても構いません。

---

## 1. JavaからFrameworkへ

### 必須1: Webアプリも普通のJavaですか

問い:

Webアプリケーションでは、これまで学んだクラスやメソッドを使わなくなるのでしょうか。

模範回答:

使います。Controller、Service、RepositoryなどもJavaのクラスです。違いは、すべてを`main`から自分で呼ぶのではなく、HTTPリクエストなどをきっかけにSpringが対応するメソッドを呼ぶ点です。

### 必須2: アノテーションとは何ですか

問い:

`@Controller`や`@GetMapping`は、通常のメソッド呼出と何が違いますか。

模範回答:

アノテーションはクラスやメソッドへ役割を付ける目印です。開発者が`@Controller()`を呼ぶのではなく、Springが起動時やリクエスト処理時に目印を読み取ります。

### 必須3: Frameworkとライブラリ

問い:

通常のライブラリ利用とFramework利用では、呼出の向きにどのような違いがありますか。

模範回答:

通常のライブラリは自分のコードから呼ぶことが中心です。Frameworkはアプリ全体の実行の流れを持ち、決められた場所で開発者のコードを呼びます。

---

## 2. WebとHTTP

### 必須1: URLを分解する

問い:

次のURLをscheme、host、port、pathに分けてください。

```text
http://localhost:8080/attendances
```

模範回答:

| 項目 | 値 |
| --- | --- |
| scheme | `http` |
| host | `localhost` |
| port | `8080` |
| path | `/attendances` |

`localhost`は現在のPC自身、8080はPC内の接続先プログラムを区別する番号です。

### 必須2: リクエストとレスポンス

問い:

ブラウザでトップ画面を開いたとき、どちらがリクエストを送り、どちらがレスポンスを返しますか。

模範回答:

ブラウザがSpring BootアプリへHTTPリクエストを送り、Spring BootアプリがブラウザへHTTPレスポンスを返します。

### 必須3: `GET`と`POST`

問い:

`GET /users`と`POST /users`は、pathが同じでも同じ処理でしょうか。

模範回答:

異なります。`GET /users`は一覧取得、`POST /users`はユーザー作成のように、HTTPメソッドとpathの組み合わせで機能が決まります。

### 必須4: 401と403

問い:

401と403は何が違いますか。

模範回答:

401は利用者が誰か確認できない状態です。403は利用者が誰か分かっているものの、その操作を行う権限がない状態です。

### 必須5: 302リダイレクト

問い:

出勤を保存した後、なぜすぐHTMLを返さず`302 Location: /`を返すのでしょうか。

模範回答:

ブラウザの再読み込みで同じPOSTを再送しにくくするためです。POSTで更新し、redirect後のGETで最新画面を表示する流れをPRGと呼びます。

### 発展: headerとbody

問い:

`Content-Type: application/json`は何を伝えていますか。

模範回答:

リクエストまたはレスポンスのbodyがJSON形式であることを伝えるheaderです。

---

## 3. HTMLとJSON

### 必須1: HTMLの役割

問い:

HTMLフォームの`action="/clock-in"`と`method="post"`は、それぞれ何を表しますか。

模範回答:

`action`は送信先path、`method`は使用するHTTPメソッドを表します。この組み合わせでは`POST /clock-in`を送ります。

### 必須2: HTMLとJSON

問い:

HTMLとJSONの主な目的の違いを説明してください。

模範回答:

HTMLは人がブラウザで見る画面構造を表します。JSONはプログラム同士で送受信するデータの構造を表します。

### 必須3: JSONの名前と値

問い:

次のJSONでは、名前と値はどれですか。

```json
{
  "username": "user2",
  "role": "ROLE_USER"
}
```

模範回答:

`username`と`role`が名前、`user2`と`ROLE_USER`がそれぞれの値です。

---

## 4. データベースとSQL

### 必須1: Javaと表の対応

問い:

Javaのクラス、インスタンス、フィールドを、DBのテーブル、行、列へ対応付けてください。

模範回答:

| Java | DB |
| --- | --- |
| クラス | テーブル |
| 一つのインスタンス | 一行 |
| フィールド | 列 |

厳密には設計により異なる場合がありますが、この教材のEntityはこの対応で理解できます。

### 必須2: 主キー

問い:

ユーザー名があるのに、なぜ`id`も必要なのでしょうか。

模範回答:

一行を変更されにくい値で確実に識別するためです。主キーの`id`を使えば、同名や名前変更の影響を受けずに一行を指定できます。

### 必須3: 外部キー

問い:

`attendances.user_id=2`は何を表しますか。

模範回答:

`users.id=2`のユーザーに属する勤怠であることを表します。`user_id`は別テーブルの主キーを参照する外部キーです。

### 必須4: 一意制約

問い:

`(user_id, work_date)`の組み合わせを一意にする理由は何ですか。

模範回答:

同じユーザーについて、同じ勤務日の勤怠を複数保存しないためです。

### 必須5: SQL

問い:

`SELECT`、`INSERT`、`UPDATE`、`DELETE`は、それぞれ何をしますか。

模範回答:

- `SELECT`: 行を取得する
- `INSERT`: 行を追加する
- `UPDATE`: 行を更新する
- `DELETE`: 行を削除する

### 必須6: commitとrollback

問い:

トランザクションのcommitとrollbackは何が違いますか。

模範回答:

commitは一連のDB変更を確定し、rollbackは途中までの変更を取り消します。

---

## 5. Spring Bootの役割

### 必須1: Spring FrameworkとSpring Boot

問い:

Spring BootはSpring Frameworkを置き換えるものですか。

模範回答:

置き換えません。DI、Spring MVC、トランザクションなどの基盤はSpring Frameworkが提供します。Spring Bootは、依存関係、初期設定、起動、外部設定、配布をまとめやすくします。

### 必須2: Starter

問い:

Starterを追加すると何が便利になりますか。

模範回答:

Web画面やDB接続など、一つの目的に必要な関連ライブラリをまとめて導入できます。

### 必須3: 自動設定

問い:

Spring Bootは何を見て自動設定を決めますか。

模範回答:

主に`pom.xml`の依存関係、`application.yml`などの設定値、自作したBeanを見て決めます。

### 必須4: Bean

問い:

Beanとは何ですか。

模範回答:

Springコンテナが作成し、必要な場所へ渡し、管理するオブジェクトです。

### 発展: ApplicationContext

問い:

ApplicationContextは何ですか。

模範回答:

Beanの作成、検索、接続などを担うSpringコンテナの中心的な仕組みです。

---

## 6. MVCとレイヤー

### 必須1: Controller

問い:

Controllerの主な責務は何ですか。

模範回答:

HTTPリクエストを受け、入力を取り出し、Serviceを呼び、画面名やJSONなどのHTTP側の結果を決めます。

### 必須2: Service

問い:

二重出勤の禁止をControllerではなくServiceへ置く理由は何ですか。

模範回答:

同じ業務ルールを画面とREST APIの両方から利用できるためです。入口ごとにルールを重複実装せずに済みます。

### 必須3: Repository

問い:

Repositoryは何を担当しますか。

模範回答:

Entityの検索、保存、削除など、DB操作の入口を担当します。

### 必須4: MVCのModel

問い:

Spring MVCの`Model`とDBへ保存するEntityは同じものですか。

模範回答:

同じではありません。MVCの`Model`はControllerからViewへ表示値を渡す入れ物です。EntityはDBの行と対応する保存対象です。

### 必須5: Template名

問い:

画面用Controllerの`return "index";`は、文字列`index`をそのままブラウザへ返すのでしょうか。

模範回答:

返しません。Thymeleafが`templates/index.html`を選ぶための論理名として扱います。

---

## 7. DIとSpringコンテナ

### 必須1: コンストラクタ注入

問い:

Controller内でServiceを`new`せず、コンストラクタから受け取る理由は何ですか。

模範回答:

ControllerをServiceの具体的な作り方から分離し、必要な依存関係を明確にするためです。実際のオブジェクト作成と受渡しはSpringコンテナが行います。

### 必須2: IoC

問い:

IoCでは何の制御がSpring側へ移りますか。

模範回答:

オブジェクトの作成、接続、一部の呼出タイミングなどが、開発者の`main`や`new`からSpring側へ移ります。

### 必須3: Component Scan

問い:

Controllerを`@SpringBootApplication`のあるpackage以下へ置く理由は何ですか。

模範回答:

標準設定のComponent Scanが、そのpackage以下からSpring管理対象を探すためです。

---

## 8. JPA、Repository、Flyway

### 必須1: JPA、Hibernate、Spring Data JPA

問い:

三者の役割を一文ずつ説明してください。

模範回答:

- JPA: Javaオブジェクトとテーブルを対応付けるルール
- Hibernate: JPAのルールに従って実際にSQLを実行する仕組み
- Spring Data JPA: Repositoryの一般的な処理を提供する仕組み

### 必須2: Entity

問い:

`@Entity`を付けた`User`は何と対応しますか。

模範回答:

`users`テーブルの一行と対応する保存対象になります。

### 必須3: 派生クエリ

問い:

`findByUsername`というRepositoryメソッド名から、Spring Data JPAは何を読み取りますか。

模範回答:

`username`列を条件に一件検索する処理だと読み取ります。

### 必須4: Flyway

問い:

Flywayを使う理由は何ですか。

模範回答:

テーブル作成や変更を、`V1`、`V2`のような順番付きSQLとして記録し、どのDBにも同じ順で適用するためです。

### 必須5: `ddl-auto: validate`

問い:

完成版でHibernateにテーブル作成を任せず、`validate`を使う理由は何ですか。

模範回答:

DB構造の正本をFlywayのSQL履歴にし、JPA側の定義と基本構造が合わない場合は起動時に検出するためです。

### 発展: DB制約との二段構え

問い:

Serviceで重複確認しても、DBへ一意制約を置く理由は何ですか。

模範回答:

ほぼ同時に複数のリクエストが来ると、両方がServiceの事前確認を通る可能性があるためです。DB制約が最後の整合性を守ります。

---

## 9. 入力検証と業務ルール

### 必須1: Validation

問い:

空のユーザー名と、すでに存在するユーザー名は、同じ場所で確認すべきでしょうか。

模範回答:

分けます。空や文字数はFormまたはDTOのValidationで確認できます。既存ユーザーとの重複はDB状態が必要なためServiceで確認します。

### 必須2: DB制約

問い:

ValidationとServiceがあれば、DB制約は不要でしょうか。

模範回答:

必要です。Validationは入力形式、Serviceは業務ルール、DB制約は並行処理を含む最終的なデータ整合性を担当します。

### 必須3: エラーの公開

問い:

予期しない例外のスタックトレースやSQLを、そのまま利用者へ返さない理由は何ですか。

模範回答:

内部構造や機密情報を公開する危険があり、利用者にとっても必要以上に詳細だからです。詳細はサーバー側のログで確認します。

---

## 10. 認証と認可

### 必須1: 認証と認可

問い:

ログイン成功と、管理画面を開けることは同じ確認ですか。

模範回答:

異なります。ログインで誰か確認するのが認証です。その人が管理画面を使ってよいか確認するのが認可です。

### 必須2: Securityの位置

問い:

一般ユーザーが`/users`を開いたとき、なぜ`UserController`まで到達しないのでしょうか。

模範回答:

Spring SecurityのFilter ChainがControllerより前にroleを確認し、権限不足として403を返すためです。

### 必須3: 本人性

問い:

出勤対象のユーザーidをブラウザの入力値から決めず、`Principal`から決める理由は何ですか。

模範回答:

ブラウザから送るidは利用者が変更できるためです。認証済みの利用者名からDBのユーザーを取得し、本人の勤怠だけを操作します。

### 必須4: パスワード

問い:

パスワードを平文でDBへ保存しない理由は何ですか。

模範回答:

DBの内容が漏れた場合に、元のパスワードをそのまま知られないようにするためです。完成版ではBCryptのハッシュ値を保存します。

### 発展: CSRF

問い:

画面の更新フォームでCSRF Tokenを使う目的は何ですか。

模範回答:

利用者のログイン済みブラウザを悪用し、別サイトから意図しない更新を送らせる攻撃を防ぐためです。

---

## 11. REST API

### 必須1: 画面ControllerとREST Controller

問い:

両者の主な出力は何ですか。

模範回答:

画面ControllerはThymeleaf Template名を返してHTMLを作ります。REST ControllerはDTOなどを返し、SpringがJSONへ変換します。

### 必須2: DTO

問い:

User EntityをそのままJSONへ返さず、`UserResponse`へ変換する理由は何ですか。

模範回答:

パスワードなど内部項目を公開せず、DB構造と外部へ公開する形式を分離するためです。

### 必須3: 400と409

問い:

短すぎるパスワードと、二重出勤はどちらも400でしょうか。

模範回答:

完成版では異なります。入力形式の不正は400、入力形式は正しいが現在状態と業務ルールが競合する二重出勤は409です。

### 必須4: JSON変換

問い:

APIへ送ったJSONは、Controllerの引数へ届くまでに何へ変換されますか。

模範回答:

JacksonがJSONをRequest DTOのJavaオブジェクトへ変換します。レスポンスでは逆にResponse DTOをJSONへ変換します。

---

## 12. 設定と実行環境

### 必須1: profile

問い:

devとprodでJavaコードを書き換えずにDBを切り替えられるのはなぜですか。

模範回答:

同じJARに対して、profile別設定と環境変数から接続先などを渡すためです。

### 必須2: H2とMariaDB

問い:

完成版ではH2とMariaDBをどう使い分けますか。

模範回答:

H2はPC上で手軽に動かす開発用、MariaDBはDocker Composeで動かす本番相当環境用です。

### 必須3: 外部設定

問い:

DBパスワードをJavaソースやJARへ直接書かない理由は何ですか。

模範回答:

秘密情報の混入を避け、環境ごとにJARを作り直さず設定を変更できるようにするためです。

### 発展: Docker Volume

問い:

DBコンテナを作り直してもデータを残すには何を使いますか。

模範回答:

Docker Volumeを使います。DBファイルをコンテナ本体から分離して保存します。

---

## 13. 総合問題: 出勤1件を端から端まで説明する

問い:

`user1`がブラウザで出勤ボタンを一回押してから、「出勤中」の画面を見るまでを説明してください。

模範回答:

1. HTMLフォームが`POST /clock-in`を送る
2. ブラウザはログインsessionを示すCookieとCSRF Tokenも送る
3. Spring Securityが認証済みで正しい画面操作か確認する
4. `HomeController.clockIn`が呼ばれる
5. `Principal`から`user1`を取得する
6. `UserService`が`user1`のidを取得する
7. `AttendanceService`が当日の勤怠がないことを確認する
8. `AttendanceRepository`が`WORKING`状態の勤怠をDBへ保存する
9. トランザクションがcommitされる
10. Controllerが`302 Location: /`を返す
11. ブラウザが`GET /`を送る
12. Controllerが当日の勤怠を取得し、Modelへ入れる
13. Thymeleafが「出勤中」のHTMLを作り、ブラウザへ返す

短くまとめる場合:

```text
POST /clock-in
  -> Security
  -> Controller
  -> Service
  -> Repository
  -> DB
  -> 302 /
  -> GET /
  -> Thymeleaf
  -> HTML
```

---

## 14. 次の資料

- 基礎を確認し直す: [JavaからWeb・データベースへ進むための基礎](./00-java-web-database-primer.md)
- Springの全体像を読む: [Spring Boot概要](./01-spring-boot-overview.md)
- 完成版の詳細な処理順を追う: [アーキテクチャとリクエスト処理](./02-architecture-and-request-flow.md)
- 実装へ進む: [完成版ハンズオン](./04-handson-guide.md)
