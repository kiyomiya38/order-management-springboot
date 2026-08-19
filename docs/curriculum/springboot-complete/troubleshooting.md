# Spring Boot完成版ハンズオン トラブルシューティング

エラーが発生したら、複数の設定を同時に変更しないでください。  
「どの段階まで成功しているか」を確認し、最初に失敗した境界を直します。

## 1. 最初の切り分け

```text
mvn -DskipTests clean package
  |
  +-- 失敗 --> コンパイルまたはパッケージ設定を直す
  |
  v
アプリ起動
  |
  +-- 失敗 --> 最初の Caused by を確認
  |
  v
/login が応答
  |
  +-- 失敗 --> ポート、Controller、Securityを確認
  |
  v
ログインと画面操作
  |
  +-- 失敗 --> 認証、認可、Validation、Serviceを確認
  |
  v
API
  |
  +-- 失敗 --> HTTP method、JSON、Security、例外変換を確認
  |
  v
Docker Compose
     |
     +-- 失敗 --> db health、環境変数、appログを確認
```

## 2. 変更前に証拠を集める

ローカル:

```bash
pwd
java -version
mvn -version
test -f pom.xml && echo "project root: OK"
mvn -DskipTests clean package
```

アプリ起動失敗時:

- エラー全文の先頭ではなく、最初の `Caused by`
- 例外に出ている自分のクラス名と行番号
- 有効なprofile
- 接続先JDBC URL
- 直前に変更したファイル

Docker:

```bash
docker compose config --quiet
docker compose ps
docker compose logs --tail=200 db
docker compose logs --tail=200 app
```

ログを共有する場合は、パスワード、Authorizationヘッダー、Cookieを除いてください。

## 3. JavaとMaven

### `MissingProjectException` または `pom.xml` がない

症状:

```text
The goal you specified requires a project to execute
but there is no POM in this directory
```

原因は、教材フォルダやリポジトリルートなど、Spring Bootプロジェクトではない場所で`mvn`を実行していることです。

確認:

```bash
pwd
ls
test -f pom.xml && echo "project root: OK"
```

受講者のプロジェクトなら`practice/springboot-complete-handson`、完成版なら`complete`へ移動してから再実行します。

### `mvn: command not found`

確認:

```bash
mvn -version
```

原因:

- Mavenが未インストール
- Mavenの `bin` がPATHにない
- インストール後に古いターミナルを使い続けている

対処:

1. Mavenをインストールする
2. PATHを設定する
3. VS CodeとGit Bashを開き直す
4. `mvn -version` で確認する

### `JAVA_HOME` またはJavaバージョンが不正

症状:

- `release version 17 not supported`
- Mavenだけ別のJavaを使用する
- VS Codeでは赤線が出るが、ターミナルでは成功する

確認:

```bash
java -version
mvn -version
```

両方がJava 17を指す必要があります。VS Codeでは `Java: Configure Java Runtime` も確認します。

### 依存関係をダウンロードできない

症状:

- `Could not resolve dependencies`
- Maven Centralへの接続失敗
- 証明書またはproxyエラー

対処:

1. ネットワーク、VPN、proxy設定を確認
2. 同じエラーが全員に出ているか確認
3. 講師が事前取得したMavenキャッシュまたは完成版環境を使う
4. 依存のversionを無作為に変更しない

一部だけ壊れたキャッシュを疑う場合も、`.m2` 全体を削除する前に、エラーに出たartifactだけを特定します。

### コンパイルエラー

よくある表示:

- `cannot find symbol`
- `package ... does not exist`
- `method ... cannot be applied`

確認順:

1. エラーの最初のファイルと行番号
2. package宣言とフォルダ階層
3. import
4. クラス名・メソッド名の大文字小文字
5. 完成版ファイルとの引数と戻り値の差

1件目を直すと、後続の大量エラーも消えることがあります。

## 4. Spring Bootが起動しない

### 8080番ポートが使用中

症状:

```text
Port 8080 was already in use
```

Git Bashで確認:

```bash
netstat -ano | grep ':8080'
```

対処:

- 先に起動していたSpring BootまたはComposeを正常停止する
- 一時的に別ポートで起動する

```bash
SERVER_PORT=8081 mvn spring-boot:run
```

原因が分からないPIDを強制終了する前に、そのプロセスを起動したターミナルやDocker Composeを確認してください。

### Beanが見つからない

症状:

```text
Parameter 0 of constructor ... required a bean of type ... that could not be found
```

確認:

- Serviceに `@Service`
- Controllerに `@Controller` または `@RestController`
- 設定クラスに `@Configuration`
- Repositoryが `JpaRepository` を継承
- 対象クラスが `com.shinesoft.attendance` 以下
- コンストラクタの型と実装した型が一致

`new Service(...)` をController内で直接行わず、コンストラクタインジェクションを使います。

### 循環参照

症状:

```text
The dependencies of some of the beans in the application context form a cycle
```

Service同士が互いをコンストラクタで要求していないか確認します。`@Lazy` で隠す前に、責務を分割するか呼び出し方向を一方向にします。

### 初期ユーザー投入で停止する

症状:

- `DataSeeder` の `IllegalStateException`
- 初期パスワードが必要というメッセージ

確認:

- devでは [`application-dev.yml`](../../../complete/src/main/resources/application-dev.yml) が有効か
- prodで `APP_SEED_ENABLED=true` にしたなら、管理者用と一般ユーザー用の両パスワードを渡したか

prodで初期投入が不要なら、`APP_SEED_ENABLED=false` のままにします。

## 5. H2、JPA、Flyway

### H2 Consoleへ接続できない

確認:

- 有効profileがdev
- URLが `http://localhost:8080/h2-console`
- JDBC URLが `jdbc:h2:file:./data/attendance;MODE=MariaDB;DB_CLOSE_ON_EXIT=FALSE`
- User Nameが `sa`
- Passwordはdev既定では空

別のJDBC URLを入力すると、空の別DBへ接続して「テーブルがない」ように見えることがあります。

### H2で `FLYWAY_SCHEMA_HISTORY` が見つからない

Flyway 11が作る履歴テーブルと列は、H2上ではquoted lowercase名です。二重引用符を付けて確認します。

```sql
SELECT "installed_rank", "version", "description", "success"
FROM "flyway_schema_history"
ORDER BY "installed_rank";
```

MariaDBでは通常のunquoted名で照会できます。H2用SQLとMariaDB用SQLを混同しないでください。

### `Schema-validation: missing table`

原因:

- Flywayが無効
- migrationの配置が違う
- 接続先DBが想定と違う
- Entityのテーブル名とSQLが不一致

確認:

- `src/main/resources/db/migration`
- ファイル名が `V1__...sql` のようにアンダースコア2個
- `spring.flyway.enabled=true`
- `ddl-auto=validate`
- 起動ログのJDBC URLとprofile

Hibernateに作らせるため `ddl-auto=update` へ変えると、原因を隠してしまいます。

### Flyway checksum mismatch

原因:

- 一度適用したV1またはV2を後から編集した

対処:

1. Git差分で変更箇所を確認
2. 適用済みmigrationを元へ戻す
3. 必要な変更はV3として追加

学習用H2だけを完全に作り直す場合は、アプリを停止し、現在位置を確認してから `data` を削除せず退避します。

```bash
pwd
mv data "data.backup-$(date +%Y%m%d-%H%M%S)"
mvn spring-boot:run
```

本番相当MariaDBで履歴やデータを初期化してはいけません。

### `Table already exists`

確認:

- 過去に `ddl-auto=create/update` で作ったDBへFlywayを後付けしていないか
- 同じCREATE TABLEを複数migrationへ書いていないか
- 想定外のschemaへ接続していないか

研修開始時からFlywayを使えば、baselineの判断を避けられます。既存DBへ導入する場合は、勝手にテーブルを消さず、講師またはDB管理者とbaseline方針を決めます。

### EntityとSQLが一致しない

比較する項目:

| Entity | Migration SQL |
| --- | --- |
| `@Table(name=...)` | `CREATE TABLE` |
| `@Column(name=...)` | 列名 |
| `nullable=false` | `NOT NULL` |
| `@Enumerated(EnumType.STRING)` | 文字列列 |
| `@JoinColumn` | 外部キー列 |
| `GenerationType.IDENTITY` | `AUTO_INCREMENT` |

完成版の [`Attendance.java`](../../../complete/src/main/java/com/shinesoft/attendance/domain/Attendance.java) と [`V1__create_tables.sql`](../../../complete/src/main/resources/db/migration/V1__create_tables.sql) を対で確認します。

## 6. ControllerとThymeleaf

### 404

確認:

- URLとHTTP method
- `@GetMapping` / `@PostMapping`
- クラスレベルの `@RequestMapping`
- Controllerがコンポーネントスキャン対象

例: `@RequestMapping("/users")` と `@GetMapping("/new")` の組み合わせは `/users/new` です。

### `TemplateInputException` / template not found

確認:

- 戻り値 `"index"` に対して `templates/index.html` が存在
- 大文字小文字とハイフン
- `src/main/resources/templates` 配下
- `.html` をControllerの戻り値へ書いていない

### Thymeleaf式のエラー

症状:

- `Exception evaluating SpringEL expression`
- 画面表示中に500

確認:

- Modelへ追加した属性名と `${...}` が一致
- nullになり得る値を条件分岐している
- Formの `th:object` と `th:field` が一致
- Enumに画面から参照するgetterがある

### CSSまたはJavaScriptが404

配置:

```text
src/main/resources/static/styles.css
src/main/resources/static/users.js
```

Thymeleaf:

```html
<link rel="stylesheet" th:href="@{/styles.css}">
<script th:src="@{/users.js}" defer></script>
```

SecurityConfigでも `/styles.css` をpermitしています。新しい公開assetを追加した場合は認可規則も確認します。

### POST後に同じ処理が再実行される

Controllerが処理後にテンプレートを直接返していないか確認します。

```java
return "redirect:/";
```

成功・失敗メッセージは `RedirectAttributes` のflash attributeで渡します。

## 7. Validationと業務例外

### `BindingResult` が働かず例外になる

引数の順序を確認します。

```java
@Valid @ModelAttribute("userForm") UserForm form,
BindingResult binding
```

`BindingResult` は対象の直後に置きます。

### 入力エラーが画面へ出ない

確認:

- Formに制約アノテーションがある
- Controllerに `@Valid`
- 同じ画面を返す際に必要な `mode`、`formAction`、IDを再設定
- テンプレートに `#fields.hasErrors` と `#fields.errors`

### 期待した409ではなく500

確認:

- Serviceが `BusinessException` を投げている
- API Controllerが `com.shinesoft.attendance.web.api` 以下
- [`ApiExceptionHandler`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/advice/ApiExceptionHandler.java) の対象package
- 想定外のNullPointerExceptionなどではない

画面Controllerでは業務例外を捕捉してFormまたはflash messageへ変換し、APIではAdviceが409へ変換します。

## 8. Spring Security

### ログインできない

確認:

```sql
SELECT id, username, password, role FROM users;
```

- usernameが存在する
- passwordがBCryptハッシュ
- `PasswordEncoder` が登録されている
- devの初期パスワードと、Dockerの `.env` の初期パスワードを混同していない

DBへ平文パスワードを直接INSERTしても、BCrypt照合には成功しません。

### ログイン画面へ何度も戻る

確認:

- Login formのinput名が `username` と `password`
- actionが `/login`
- `/login` が `permitAll`
- DBユーザーが存在
- Cookieを禁止していない

ブラウザのシークレットウィンドウで古いセッションを除外します。

### 一般ユーザーが403

管理画面の403は正常です。アクセス先を確認します。

- 一般ユーザー: `/`, `/attendances`, `/api/attendances/**`
- 管理者: 上記に加えて `/users/**`, `/admin/**`, `/api/users/**`

### Form POSTが403

画面用POSTではCSRF tokenが必要です。ThymeleafとSpring Securityを使った同一アプリ内のformには、自動的にhidden tokenが追加されます。

確認:

- formに `method="post"`
- actionを `th:action` で指定
- curlで画面用POSTを直接呼んでいない
- API用chainだけでCSRFを無効にし、画面用chainではH2 Console以外を保護している

CSRFを全体で無効にして解決しないでください。

### API未認証がHTMLになる

期待は401 JSONです。確認:

- URLが正確に `/api` または `/api/...`
- API用chainに `securityMatcher("/api/**")` がある
- API用 `AuthenticationEntryPoint` が設定されている
- 画面用のログインリダイレクトより適切にmatcherが選ばれる

## 9. REST APIとcurl

### 400 Bad Request

候補:

- JSON文法エラー
- DTOのValidation違反
- path variableの型が不正

Git BashではJSON全体を単一引用符で囲みます。

次の`admin:admin123`はdev/H2専用です。Docker Composeのprod環境では、`.env`の`APP_SEED_ADMIN_PASSWORD`へ設定した値を使います。

```bash
curl -i -u admin:admin123 \
  -H 'Content-Type: application/json' \
  -d '{"username":"user2","password":"password123","role":"ROLE_USER"}' \
  http://localhost:8080/api/users
```

PowerShellへそのまま貼ると引用符の解釈が異なります。この教材のcurlはGit Bashで実行します。

JSON文法エラーは `HttpMessageNotReadableException` として、400の統一JSONへ変換します。

```json
{"code":"VALIDATION_ERROR","message":"JSONの形式が不正です"}
```

500になる場合は、[`ApiExceptionHandler`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/advice/ApiExceptionHandler.java) にこの例外専用の `@ExceptionHandler` があるか確認します。

### 401、403、409の違い

| HTTP | 意味 | 最初に確認 |
| ---: | --- | --- |
| 401 | 認証情報なし、または不正 | `-u`、ユーザー名、パスワード |
| 403 | 認証済みだが権限不足 | roleとURL規則 |
| 409 | 認証・入力は通ったが業務上実行不可 | Serviceの業務ルール |

誤ったBasic認証でも、匿名時と同じ `{"code":"UNAUTHORIZED",...}` を返します。Spring Boot既定の `timestamp` / `status` / `error` 形式になる場合は、`httpBasic` とAPI用chainの両方へ同じ `AuthenticationEntryPoint` が設定されているか確認します。

### 415 Unsupported Media Type

JSON送信時に次を付けます。

```bash
-H 'Content-Type: application/json'
```

### APIがHTMLを返す

アクセス先が `/api/...` か確認します。画面ControllerはThymeleaf、API ControllerはJSONを返します。未認証APIがログインHTMLへredirectされる場合はSecurityのAPI matcherを確認します。

## 10. Docker ComposeとMariaDB

### Docker daemonへ接続できない

症状:

```text
Cannot connect to the Docker daemon
```

Docker Desktopを起動し、準備完了後に次を確認します。

```bash
docker version
```

### `docker compose config` が失敗

よくある原因:

- `.env` がない
- 必須変数が空
- YAMLのインデント
- タブ文字
- `:` を含む値の引用不足

完成版の [`.env.example`](../../../complete/.env.example) から `.env` を作り、値を設定します。

### image buildでMaven packageが失敗

DockerfileはMavenで実行可能JARを作ります。ローカルでも先に同じpackage処理を確認します。

```bash
mvn -DskipTests clean package
```

ローカルとコンテナで違う場合は、未保存ファイル、JDK、build context、`.dockerignore` を確認します。

### `no main manifest attribute`

原因:

- Spring Bootのrepackageが実行されていない
- 通常JARをコピーしている

確認:

- [`pom.xml`](../../../complete/pom.xml) の `spring-boot-maven-plugin`
- DockerfileのJAR名
- `mvn -DskipTests clean package` 後の `target`

### dbがhealthyにならない

```bash
docker compose ps
docker compose logs --tail=200 db
```

候補:

- rootパスワードが空
- 既存volumeと現在の `.env` が不一致
- ディスク容量不足
- MariaDBの初期化中

### appがDBへ接続できない

症状:

- `Connection refused`
- `Unknown host`
- `Access denied for user`

確認:

```bash
docker compose ps
docker compose logs --tail=200 app
docker compose logs --tail=200 db
```

設定:

- hostは `db`
- portは `3306`
- DB名、ユーザー名、パスワードがdbとappで一致
- driverは `org.mariadb.jdbc.Driver`
- `SPRING_PROFILES_ACTIVE=prod`

appの `DB_URL` に `localhost` を指定しません。

### `.env` を変えたのにMariaDBのパスワードが変わらない

MariaDB imageの初期化用環境変数は、空のvolumeを最初に作るときだけ使われます。既存の `db_data` がある場合、`.env` を変更してもDB内ユーザーのパスワードは自動更新されません。

`APP_SEED_ADMIN_PASSWORD`と`APP_SEED_USER_PASSWORD`も、同名ユーザーを新規作成するときだけ使われます。既存ユーザーのパスワードは、アプリを再起動しても自動更新されません。

対処:

1. 以前の `.env` へ戻して接続する
2. DB内で正式にパスワードを変更する
3. 破棄可能な研修データだけなら、必要なデータを退避したうえでvolumeを明示的に作り直す

`docker compose down -v` はDBデータを削除します。原因確認の最初の手段にはしません。

### ソース変更が反映されない

```bash
docker compose up -d --build
docker compose logs --tail=100 app
```

それでも古い場合だけ:

```bash
docker compose build --no-cache app
docker compose up -d
```

### 8080へアクセスできない

確認:

- `docker compose ps` でappがUp
- appが再起動を繰り返していない
- `8080:8080` がある
- ホストの8080を別アプリが使用していない

### コンテナだけ勤務日・打刻時刻がずれる

ホストが日本時間でも、コンテナやJVMがUTCだと深夜帯に `LocalDate.now()` の日付がずれることがあります。

[`Dockerfile`](../../../complete/Dockerfile) に次があるか確認します。

```dockerfile
ENV TZ=Asia/Tokyo
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Tokyo"
```

変更後はimageを再ビルドします。

```bash
docker compose up -d --build
```

## 11. 安全な復旧順

問題が複数重なった場合は、次の順に戻します。

1. `git diff` で直前の変更を確認
2. `mvn -DskipTests clean package`
3. dev/H2で `mvn spring-boot:run`
4. `/login`、画面、APIを確認
5. `docker compose config --quiet`
6. `docker compose up -d --build`
7. dbログ、次にappログ
8. MariaDBのFlyway履歴を確認

DB削除、migration履歴の修正、Security全無効化は、通常の復旧手順ではありません。

## 12. 講師へ共有する情報

次をまとめると原因を早く特定できます。

```text
実行したコマンド:
期待した結果:
実際の結果:
最初の Caused by:
有効profile:
直前に変更したファイル:
Maven package の成否:
docker compose ps の状態:
```

スクリーンショットだけでなく、コピー可能なエラーテキストも共有してください。秘密値は伏せます。
