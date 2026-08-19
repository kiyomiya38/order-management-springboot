# JavaからWeb・データベースへ進むための基礎

## この章の目的

この章は、変数、分岐、繰り返し、クラス、メソッドを一度学んだ人が、Spring Bootへ進む前に必要なWebとデータベースの基礎を身に付けるための章です。

HTML、HTTP、SQL、Springを初めて見る前提で説明します。最初から用語を暗記する必要はありません。分からない言葉が出たときは、[用語集](./glossary.md)へ戻ってください。

この章を読み終えたら、次を自分の言葉で説明できれば十分です。

- ブラウザとWebサーバーの役割
- HTTPリクエストとHTTPレスポンス
- URL、HTTPメソッド、ステータスコード
- HTMLフォームとJSONの違い
- データベースのテーブル、行、列
- 主キー、外部キー、一意制約
- SQLの`SELECT`、`INSERT`、`UPDATE`、`DELETE`
- JavaオブジェクトとDBの1行の対応
- 出勤ボタンを押してからDBへ1行保存されるまでの全体像

---

## 1. 普通のJavaプログラムとWebアプリケーション

### 1.1 自分で呼び出すプログラム

これまでのJava学習では、`main`メソッドから自分でメソッドを呼ぶプログラムを多く作りました。

次のファイルは、Java 17でそのままコンパイルして実行できます。

```java
public class MethodCallDemo {

    public static void main(String[] args) {
        GreetingService service = new GreetingService();
        String message = service.greet("user1");
        System.out.println(message);
    }

    static class GreetingService {
        String greet(String username) {
            return "こんにちは、" + username + "さん";
        }
    }
}
```

```bash
javac MethodCallDemo.java
java MethodCallDemo
```

このプログラムでは、次の順で処理されます。

1. Java実行環境（JVM）が`main`メソッドを呼ぶ
2. `main`が`GreetingService`を`new`する
3. `main`が`greet`メソッドを呼ぶ
4. 結果を標準出力へ表示する

### 1.2 Webアプリケーションは待ち続ける

WebアプリケーションもJavaのクラスとメソッドで作ります。ただし、起動後すぐ終了せず、ブラウザなどから届く要求を待ち続けます。

```text
Javaアプリケーションを起動
        |
        v
Webサーバーが8080番ポートで待つ
        |
        v
ブラウザから要求が届く
        |
        v
対応するJavaメソッドが呼ばれる
        |
        v
HTMLやJSONを返す
        |
        v
次の要求を待つ
```

Webアプリケーションでは、開発者がすべてのメソッドを`main`から直接呼ぶわけではありません。Springがブラウザから届いた要求を調べ、対応するメソッドを呼びます。

この「自分のコードが仕組み側から呼ばれる」という点が、最初に慣れるべき違いです。

### 1.3 アノテーションは役割を伝える目印

Springのコードには、`@Controller`や`@GetMapping`のように`@`から始まる記述が登場します。これをアノテーションと呼びます。

アノテーションは、それ自体を自分でメソッドのように呼ぶものではありません。クラスやメソッドに役割を付け、Springが起動時やリクエスト処理時に読み取る目印です。

次は役割だけを示す抜粋であり、単独でコンパイルする完全なファイルではありません。

```java
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
```

この例では、Springは次のように読み取ります。

- `@Controller`: このクラスはWebリクエストを受ける
- `@GetMapping("/")`: `GET /`が届いたら、このメソッドを呼ぶ

詳しい仕組みは[Spring Boot概要](./01-spring-boot-overview.md)で扱います。ここでは「Springへ役割を伝える目印」と理解してください。

---

## 2. ブラウザとWebサーバー

### 2.1 クライアントとサーバー

Webでは、要求する側をクライアント、要求を受けて結果を返す側をサーバーと呼びます。

この教材では、主に次の組み合わせを使います。

| 役割 | 具体例 |
| --- | --- |
| クライアント | Chrome、Edgeなどのブラウザ |
| クライアント | `curl`などのAPI操作コマンド |
| サーバー | Spring Bootで作る勤怠管理アプリ |
| データ保存 | H2またはMariaDB |

```text
利用者
  |
  v
ブラウザ --HTTP--> Spring Bootアプリ --SQLなど--> データベース
          <--HTTP--                 <----------
```

ブラウザとSpring BootはHTTPでやり取りします。Spring BootとデータベースはJDBCなどの仕組みを通じてやり取りします。

### 2.2 `localhost:8080`の意味

完成版を起動すると、ブラウザで次のURLを開きます。

```text
http://localhost:8080/
```

URLを分解すると、次の意味になります。

| 部分 | 名前 | 意味 |
| --- | --- | --- |
| `http` | scheme | HTTPで通信する |
| `localhost` | host | 今使っているPC自身 |
| `8080` | port | PC内のどのプログラムへ接続するかを示す番号 |
| `/` | path | アプリ内のどの機能を呼ぶか |

一台のPCでは複数のプログラムが通信を待てます。ポート番号は、その中から接続先を区別する番号です。

たとえば、`http://localhost:8080/users`のpathは`/users`です。同じSpring Bootアプリでも、pathにより呼ばれる機能が変わります。

---

## 3. HTTPリクエストとHTTPレスポンス

### 3.1 一往復の通信

クライアントからサーバーへ送る要求をHTTPリクエスト、サーバーからクライアントへ返す結果をHTTPレスポンスと呼びます。

```text
ブラウザ
  |
  | HTTPリクエスト
  | GET /
  v
Spring Boot
  |
  | HTTPレスポンス
  | 200 OK + HTML
  v
ブラウザ
```

ブラウザの画面上ではURLを入力するだけですが、内部では次のような情報が送られます。

```http
GET / HTTP/1.1
Host: localhost:8080
```

サーバーは、たとえば次のように返します。

```http
HTTP/1.1 200 OK
Content-Type: text/html

<!doctype html>
<html lang="ja">
  <body>
    <h1>勤怠管理</h1>
  </body>
</html>
```

空行より前がレスポンスの説明、空行より後が本文です。

### 3.2 リクエストを構成するもの

| 要素 | 例 | 意味 |
| --- | --- | --- |
| HTTPメソッド | `GET` | 何をしたいか |
| path | `/attendances` | どの機能を呼ぶか |
| header | `Content-Type: application/json` | 本文の種類などの付加情報 |
| body | `{"username":"user1"}` | 送信するデータ |

すべてのリクエストにbodyがあるわけではありません。画面取得に使う`GET`では、通常bodyを送りません。

### 3.3 主なHTTPメソッド

| メソッド | 主な意味 | 勤怠管理アプリでの例 |
| --- | --- | --- |
| `GET` | 取得する | 一覧画面を表示する |
| `POST` | 新しく作る、操作を実行する | ユーザー作成、出勤 |
| `PUT` | 指定したデータを更新する | APIでユーザーを更新する |
| `DELETE` | 削除する | APIでユーザーを削除する |

URLだけでなく、HTTPメソッドとの組み合わせで機能が決まります。

```text
GET  /users   -> ユーザー一覧を取得
POST /users   -> ユーザーを新規作成
```

### 3.4 主なHTTPステータス

レスポンスには、結果を3桁の数字で表すステータスコードがあります。

| ステータス | 意味 | 例 |
| ---: | --- | --- |
| 200 | 成功 | 一覧を取得できた |
| 201 | 作成成功 | ユーザーを作成できた |
| 204 | 成功、本文なし | 削除できた |
| 302 | 別のURLへ移動 | POST後に一覧へ移動する |
| 400 | 入力形式が不正 | 必須項目が空 |
| 401 | 誰か確認できない | ログイン情報がない |
| 403 | 誰かは分かるが権限がない | 一般ユーザーが管理機能を呼ぶ |
| 404 | 対応するpathがない | 存在しないURLを開いた |
| 405 | HTTPメソッドが許可されていない | `GET`専用URLへ`POST`した |
| 409 | 現在の状態と操作が競合 | 二重出勤 |
| 415 | bodyの種類を扱えない | JSON用APIへ別形式を送った |
| 500 | サーバー内部の想定外エラー | プログラム不具合など |

最初から番号を暗記する必要はありません。正常、入力不正、認証・権限、業務上の競合を区別できれば十分です。

---

## 4. HTMLフォームとJSON

### 4.1 HTMLはブラウザ画面を組み立てる

HTMLは、見出し、段落、リンク、入力欄、ボタンなど、ブラウザへ表示する構造を表します。

```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8">
  <title>勤怠管理</title>
</head>
<body>
  <h1>勤怠管理</h1>
  <p>現在の状態: 未出勤</p>

  <form action="/clock-in" method="post">
    <button type="submit">出勤</button>
  </form>
</body>
</html>
```

このフォームで出勤ボタンを押すと、ブラウザは`POST /clock-in`を送ります。

```text
action="/clock-in" -> 送信先path
method="post"      -> HTTPメソッド
```

### 4.2 JSONはデータの形を表す

JSONは、画面の見た目ではなく、名前と値の組み合わせでデータを表します。

```json
{
  "username": "user2",
  "password": "password123",
  "role": "ROLE_USER"
}
```

同じユーザー情報でも、HTMLは人がブラウザで操作する画面に向き、JSONはプログラム同士のやり取りに向きます。

| 観点 | HTML | JSON |
| --- | --- | --- |
| 主な利用者 | 人 | プログラム |
| 主な目的 | 画面を表示・操作する | データを送受信する |
| Spring側の入口 | 画面用Controller | REST API用Controller |

### 4.3 リダイレクト

データを更新した直後に同じHTMLを返すと、ブラウザの再読み込みで同じ`POST`が再送されることがあります。

そこで、更新後は302レスポンスで別の`GET`へ移動させます。

```text
POST /clock-in
  |
  | 出勤を保存
  v
302 Location: /
  |
  | ブラウザが移動
  v
GET /
  |
  v
200 OK + 更新後のHTML
```

この流れをPost/Redirect/Get、略してPRGと呼びます。

---

## 5. ログイン状態の基礎

HTTPの各リクエストは、そのままでは独立しています。サーバーは、前のリクエストと次のリクエストが同じ利用者から来たとは判断できません。

画面ログインでは、一般に次の流れでログイン状態を維持します。

1. ブラウザがユーザー名とパスワードを送る
2. サーバーが正しいか確認する
3. サーバーがセッションを作る
4. ブラウザがセッションを識別するCookieを保存する
5. 以後のリクエストでCookieを送る

```text
POST /login
  username=user1
  password=password
        |
        v
認証成功
        |
        v
Set-Cookie: JSESSIONID=...
        |
        v
次のGET /でCookieを送信
```

- 認証: 利用者が誰か確認する
- 認可: その利用者が操作してよいか確認する

Spring Securityは、この確認をControllerより前に行います。

パスワードはDBへそのまま保存しません。元のパスワードへ簡単に戻せないハッシュ値へ変換して保存します。完成版ではBCryptという方式を使用します。

CSRFなどの詳しい防御は[Spring Boot概要](./01-spring-boot-overview.md)で扱います。

---

## 6. データベースの基本

### 6.1 テーブル、行、列

この教材で使うH2とMariaDBは、データを表の形で管理するリレーショナルデータベースです。RDBと略すことがあります。

ユーザーを保存する`users`テーブルの例:

| id | username | password | role |
| ---: | --- | --- | --- |
| 1 | admin | BCryptのハッシュ値 | ROLE_ADMIN |
| 2 | user1 | BCryptのハッシュ値 | ROLE_USER |

- テーブル: 同じ種類のデータを集めた表
- 行: 一件のデータ
- 列: データの項目
- スキーマ: テーブル、列、型、制約など、DB構造の定義

Javaと対応付けると、次のように考えられます。

| Java | DB |
| --- | --- |
| `User`クラス | `users`テーブル |
| `User`の一つのインスタンス | `users`の一行 |
| `username`フィールド | `username`列 |

### 6.2 主キーと一意制約

同じユーザー名の人が複数いても、名前だけでは一件を確実に指定できません。そのため、各行を識別する主キーを持たせます。

`users`では`id`が主キーです。

```text
id=1 -> admin
id=2 -> user1
```

ユーザー名も重複してはいけないため、`username`には一意制約を付けます。

| 制約 | 守ること |
| --- | --- |
| 主キー | 行を一意に識別する |
| 一意制約 | 指定した値の重複を禁止する |
| NOT NULL | 値の欠落を禁止する |

### 6.3 外部キーと一対多

勤怠を保存する`attendances`テーブルは、`user_id`でユーザーを参照します。

| id | user_id | work_date | start_time | end_time | status |
| ---: | ---: | --- | --- | --- | --- |
| 101 | 2 | 2026-07-27 | 09:00:00 | 18:00:00 | FINISHED |
| 102 | 2 | 2026-07-28 | 08:55:00 |  | WORKING |

`user_id=2`は、`users.id=2`の`user1`を表します。このように別テーブルの主キーを参照する列を外部キーと呼びます。

一人のユーザーは複数日の勤怠を持てます。

```text
users 1行
   |
   +---- attendances 2026-07-27
   |
   +---- attendances 2026-07-28
```

これを一対多の関係と呼びます。

### 6.4 SQL

SQLは、RDBへデータの取得や更新を依頼する言語です。

取得:

```sql
SELECT id, username, role
FROM users
WHERE username = 'user1';
```

追加:

```sql
INSERT INTO attendances (
    user_id,
    work_date,
    start_time,
    status,
    created_at,
    updated_at
) VALUES (
    2,
    '2026-07-28',
    '2026-07-28 08:55:00',
    'WORKING',
    '2026-07-28 08:55:00',
    '2026-07-28 08:55:00'
);
```

更新:

```sql
UPDATE attendances
SET
    end_time = '2026-07-28 18:03:00',
    status = 'FINISHED',
    updated_at = '2026-07-28 18:03:00'
WHERE id = 102;
```

削除:

```sql
DELETE FROM users
WHERE id = 3;
```

完成版では、通常これらのSQLをControllerへ直接書きません。Spring Data JPAのRepositoryを通してJavaのメソッドとして保存や検索を行います。

### 6.5 トランザクション

複数のDB操作を一つのまとまりとして扱う仕組みをトランザクションと呼びます。

```text
処理開始
  |
  +-- 現在の勤怠を確認
  |
  +-- 出勤データを保存
  |
  +-- すべて成功 -> commit
  |
  +-- 途中で失敗 -> rollback
```

- commit: 一連の変更を確定する
- rollback: 一連の変更を取り消す

「確認だけ成功し、保存は失敗した」といった途中状態を残さないために使います。

---

## 7. JavaとDBをつなぐ考え方

JavaからDBへ接続する基本技術はJDBCです。JDBCでは、接続、SQL、結果の読み取りを開発者が細かく記述できます。

Spring Boot完成版では、JPA、Hibernate、Spring Data JPAを使い、Javaオブジェクトとテーブルを対応付けます。

| 名前 | この段階での理解 |
| --- | --- |
| JPA | JavaクラスとDBテーブルを対応付けるためのルール |
| Hibernate | JPAのルールに従って実際にDB操作する仕組み |
| Spring Data JPA | Repositoryの定型的な実装を自動生成する仕組み |

```text
JavaのUser
   |
   | JPAの対応情報
   v
Hibernate
   |
   | SQLを実行
   v
usersテーブル
```

`@Entity`は「このクラスをDBへ保存する対象として扱う」という目印です。

```java
@Entity
@Table(name = "users")
public class User {
    // フィールドとgetter/setter
}
```

このコードは対応関係だけを示した抜粋であり、単独でコンパイルする完全なクラスではありません。完成版は[`User.java`](../../../complete/src/main/java/com/shinesoft/attendance/domain/User.java)で確認できます。

---

## 8. 出勤1件を端から端まで追う

ここまでの内容を、`user1`が出勤ボタンを一回押す例でつなぎます。

### 8.1 操作前

`users`テーブルには、次のユーザーが存在します。

| id | username | role |
| ---: | --- | --- |
| 2 | user1 | ROLE_USER |

当日の`attendances`行はまだありません。

### 8.2 ブラウザがリクエストを送る

利用者がHTMLフォームの出勤ボタンを押します。

```http
POST /clock-in HTTP/1.1
Host: localhost:8080
Cookie: JSESSIONID=...
```

Cookieにより、サーバーはログイン中の利用者が`user1`だと判断できます。

### 8.3 Spring Securityが入口を確認する

Controllerへ進む前に、Spring Securityが次を確認します。

1. ログイン済みか
2. このURLを操作してよいか
3. 画面フォームが正しい画面から送られたか

問題がなければ、Controllerへ進みます。

### 8.4 Javaのメソッドが順番に呼ばれる

```text
HomeController.clockIn
        |
        | ログイン名 user1 を取得
        v
UserService.getByUsername
        |
        | user1 の id=2 を取得
        v
AttendanceService.clockIn
        |
        | 当日の勤怠がないことを確認
        v
AttendanceRepository.save
```

各クラスの役割:

| クラス | この処理で行うこと |
| --- | --- |
| Controller | HTTP入力を受け、処理結果の画面遷移を決める |
| Service | 二重出勤を禁止し、保存する勤怠を組み立てる |
| Repository | 勤怠をDBへ保存する |

### 8.5 DBへ一行追加される

Repositoryを通じて、考え方としては次のような`INSERT`が行われます。

```sql
INSERT INTO attendances (
    user_id,
    work_date,
    start_time,
    status,
    created_at,
    updated_at
) VALUES (
    2,
    '2026-07-28',
    '2026-07-28 08:55:00',
    'WORKING',
    '2026-07-28 08:55:00',
    '2026-07-28 08:55:00'
);
```

保存後:

| id | user_id | work_date | start_time | end_time | status |
| ---: | ---: | --- | --- | --- | --- |
| 102 | 2 | 2026-07-28 | 08:55:00 |  | WORKING |

実際の時刻とidは実行時に決まります。

### 8.6 ブラウザへ結果を返す

Controllerは更新後の画面へ直接HTMLを返さず、トップ画面へリダイレクトします。

```http
HTTP/1.1 302 Found
Location: /
```

ブラウザは続けて`GET /`を送り、DBの新しい状態を使ったHTMLを受け取ります。

```text
出勤ボタン
  -> POST /clock-in
  -> Security
  -> Controller
  -> Service
  -> Repository
  -> INSERT
  -> 302 /
  -> GET /
  -> 「出勤中」のHTML
```

これが、このコース全体で繰り返し追跡する一本の処理経路です。

---

## 9. Spring Bootで登場するDB関連ツール

完成版では、目的の異なる三つの仕組みを組み合わせます。

| 名前 | 役割 |
| --- | --- |
| H2 | PC上で手軽に使う開発用DB |
| MariaDB | コンテナで動かす本番相当DB |
| Flyway | テーブル作成や変更を、順番付きSQLとして管理する |

Flywayは、たとえば次のファイルを順番に適用します。

```text
V1__create_tables.sql
V2__add_index_to_attendance_work_date.sql
```

JPAはJavaクラスとDBを対応付けますが、完成版ではJPAへテーブル作成を任せません。FlywayがSQLで構造を作り、JPAはJava側の定義と大きな不一致がないか確認します。

---

## 10. 次へ進む前の確認

次の問いへ、資料を見ながら答えてください。

1. `localhost`と8080は、それぞれ何を表しますか。
2. `GET /users`と`POST /users`は何が違いますか。
3. 200、302、400、401、403、409は、どのような結果ですか。
4. HTMLとJSONは、主な利用者と目的がどう違いますか。
5. テーブル、行、列をJavaのクラス、インスタンス、フィールドへ対応付けられますか。
6. 主キーと外部キーは何を守りますか。
7. `SELECT`と`INSERT`は何を行いますか。
8. commitとrollbackは何が違いますか。
9. 出勤ボタンからDBの一行まで、処理を順番に説明できますか。

模範回答は[理解チェックと解答](./checkpoints-and-answers.md)にあります。

次は[Spring Boot概要](./01-spring-boot-overview.md)へ進み、ここで確認したWebとDBの流れをSpringのクラスへ対応付けます。
