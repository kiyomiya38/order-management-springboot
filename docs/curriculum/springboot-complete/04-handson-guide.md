# 勤怠管理アプリ完成版ハンズオン

このハンズオンでは、Javaの基礎を学んだ人が、空のSpring Bootプロジェクトから勤怠管理アプリを順番に作ります。Web、Spring、SQLを初めて扱うことを前提にしています。

完成版は [`complete`](../../../complete/) にあります。ただし、最初から完成版をコピーするのではなく、各Phaseのチェックポイントを通過した後に答え合わせとして開きます。

この章で扱う順序は次のとおりです。

```text
最小MVC
  -> DIとService
  -> JPA / H2 / Flyway
  -> 出勤・退勤の業務ルール
  -> Validationと管理画面
  -> Securityとユーザー管理
  -> REST API
```

関連資料:

- [Spring Boot概要](./01-spring-boot-overview.md)
- [アーキテクチャとリクエスト処理](./02-architecture-and-request-flow.md)
- [H2からMariaDBへの切り替えとDocker Compose](./05-deployment.md)
- [トラブルシューティング](./troubleshooting.md)

## 0. 最初に覚える最小用語

### Webの最小用語

| 用語 | この章での意味 |
| --- | --- |
| URL | ブラウザやcurlがアクセスする場所 |
| HTTP | ブラウザとサーバーがリクエスト・レスポンスを交換する規則 |
| GET | 主に画面やデータを取得するリクエスト |
| POST | 主に登録や更新を依頼するリクエスト |
| HTTPステータス | 処理結果を表す番号。例: 200成功、400入力不正、403権限不足 |
| HTML | ブラウザへ表示する文書 |
| JSON | APIで値を交換するためのテキスト形式 |
| Controller | URLとJavaメソッドを対応付ける入口 |
| Model | ControllerからHTMLテンプレートへ渡す値 |
| Thymeleaf | Modelの値をHTMLへ埋め込むテンプレートエンジン |

### DBの最小用語

| 用語 | この章での意味 |
| --- | --- |
| テーブル | 同じ種類のデータを保存する表 |
| 行 | ユーザー1人、勤怠1件などのデータ |
| 主キー | 行を一意に識別するID |
| 外部キー | 別テーブルの行を参照する列 |
| UNIQUE | 同じ値の重複を禁止する制約 |
| Entity | DBの行と対応するJavaクラス |
| Repository | Entityの検索・保存を担当する入口 |
| Migration | DB構造の変更履歴をSQLファイルとして残す仕組み |

### 2つのターミナル

講義中はGit Bashを2つ開きます。

| 名前 | 用途 |
| --- | --- |
| アプリ用ターミナル | `mvn spring-boot:run` を実行し、起動ログを表示する |
| 操作用ターミナル | `pwd`、`mvn compile`、curlなどを実行する |

アプリ用ターミナルでSpring Bootが動いている間、そのターミナルには次のコマンドを入力できません。コードを変更したら、アプリ用ターミナルで `Ctrl + C` を押して停止し、コンパイルしてから再起動します。

---

## Phase 0: 作業場所と空プロジェクトを準備する

### 目的

- Mavenプロジェクトの場所を正しく確認する
- WebとThymeleafだけを持つ最小のSpring Bootプロジェクトを起動する
- 以後の全Phaseで使う停止・起動の手順を覚える

### 新出語

| 用語 | 意味 |
| --- | --- |
| Maven | Javaの依存ライブラリ取得、コンパイル、起動を行うツール |
| `pom.xml` | Mavenプロジェクトの名前、Javaバージョン、依存関係を書くファイル |
| Starter | 関連ライブラリと自動設定をまとめた依存関係 |
| 組み込みTomcat | Spring Bootアプリの中で動くWebサーバー |
| プロジェクトルート | `pom.xml` があるフォルダ |

### 作成ファイル

- `pom.xml`（Spring Initializrが生成）
- `src/main/java/com/shinesoft/attendance/AttendanceManagementApplication.java`
- `src/main/resources/application.yml`
- `.gitignore`

### 重要なコードまたは差分

#### 1. 作業フォルダ

Git Bashで教材リポジトリのルートへ移動してから実行します。

```bash
mkdir -p practice/springboot-complete-handson
cd practice/springboot-complete-handson
pwd
```

#### 2. Spring Initializr

Spring InitializrまたはVS CodeのSpring Initializr機能で次を選びます。

| 項目 | 値 |
| --- | --- |
| Project | Maven |
| Language | Java |
| Spring Boot | 3.5系 |
| Group | `com.shinesoft` |
| Artifact | `attendance-management-complete` |
| Package name | `com.shinesoft.attendance` |
| Packaging | Jar |
| Java | 17 |
| Dependencies | Spring Web、Thymeleaf |

生成されたフォルダの「中身」を `practice/springboot-complete-handson` へ配置します。配置後に確認します。

```bash
pwd
ls
ls pom.xml
```

`pom.xml` が見えず、さらに同名フォルダが見える場合は、1階層深い場所へ展開されています。`pom.xml` がある場所をVS Codeで開き直してください。

#### 3. 起動クラス

Artifact名から `AttendanceManagementCompleteApplication` が生成された場合は、ファイル名とクラス名を次へ揃えます。`@SpringBootApplication` を付けたクラスを2つ残しません。

```java
package com.shinesoft.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AttendanceManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(AttendanceManagementApplication.class, args);
    }
}
```

#### 4. 最初の設定

`src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: attendance-management
  thymeleaf:
    cache: false

server:
  port: 8080
  address: 127.0.0.1
```

`127.0.0.1` は自分のPCからだけ接続できるアドレスです。開発途中の画面を同じLANへ公開しません。

#### 5. Git管理から外すもの

`.gitignore`:

```gitignore
target/
data/
.env
```

### なぜ

最初から全依存関係を追加すると、どのStarterが何を有効にしたか分からなくなります。最初はWebとThymeleafだけにし、必要になったPhaseで依存関係を追加します。

### 停止→起動

初回なので停止操作は不要です。操作用ターミナルで必ず現在位置を確認します。

```bash
pwd
ls pom.xml
mvn compile
```

コンパイル成功後、アプリ用ターミナルで実行します。

```bash
mvn spring-boot:run
```

操作用ターミナル:

```bash
curl -i http://localhost:8080/
```

確認後、アプリ用ターミナルで `Ctrl + C` を押して停止します。

### 期待結果

- Mavenのコンパイルが成功する
- Tomcatが8080番で起動する
- まだControllerがないため、`GET /` は404になる

404は「サーバーが起動しているが、そのURLの処理はまだ作っていない」という意味です。

### つまずき

- `mvn: command not found`
  - MavenのPATHを確認し、Git Bashを開き直します。
- `release version 17 not supported`
  - `java -version` と `mvn -version` の両方がJava 17か確認します。
- `pom.xml` がない
  - Spring Initializrの生成物を展開した階層と `pwd` を確認します。
- `Port 8080 was already in use`
  - 以前のSpring BootまたはDocker Composeを停止します。

### チェックポイント

- [ ] `pwd` で作業フォルダを説明できる
- [ ] 同じ場所に `pom.xml` がある
- [ ] WebとThymeleafのStarterだけを選んだ理由を説明できる
- [ ] 404と「アプリが起動していない」を区別できる
- [ ] 確認後にアプリを停止した

答え合わせ:

- [`AttendanceManagementApplication.java`](../../../complete/src/main/java/com/shinesoft/attendance/AttendanceManagementApplication.java)
- [`application.yml`](../../../complete/src/main/resources/application.yml) は後続Phaseで項目が増えます

---

## Phase 1: 最小MVC画面を表示する

### 目的

- `GET /` とJavaメソッドを結び付ける
- ControllerからModelへ値を渡す
- ThymeleafがHTMLを生成する流れを確認する

### 新出語

| 用語 | 意味 |
| --- | --- |
| Spring MVC | HTTPリクエストをControllerで処理し、画面を返す仕組み |
| `@Controller` | このクラスが画面用Controllerであることを表す |
| `@GetMapping` | GETのURLとメソッドを対応付ける |
| Model | テンプレートへ渡す値の入れ物 |
| `th:text` | Modelの値でHTML要素の文字を置き換える |

### 作成ファイル

- `src/main/java/com/shinesoft/attendance/web/HomeController.java`
- `src/main/resources/templates/index.html`

### 重要なコードまたは差分

`HomeController.java`:

```java
package com.shinesoft.attendance.web;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("workDate", LocalDate.now());
        model.addAttribute("message", "Spring Bootで勤怠管理を作ります");
        return "index";
    }
}
```

`templates/index.html`:

```html
<!doctype html>
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="utf-8">
  <title>勤怠管理</title>
</head>
<body>
  <h1>勤怠管理</h1>
  <p>勤務日: <span th:text="${workDate}">2026-01-01</span></p>
  <p th:text="${message}">ここはThymeleafで置き換わります</p>
</body>
</html>
```

処理順:

```text
ブラウザ
  -> GET /
  -> HomeController#index
  -> ModelへworkDateとmessageを追加
  -> templates/index.html
  -> HTMLレスポンス
```

### なぜ

ControllerはHTML文字列を組み立てません。Javaは「どの値を表示するか」をModelへ渡し、Thymeleafは「どのようなHTMLにするか」を担当します。

### 停止→起動

アプリが動いていれば、アプリ用ターミナルで `Ctrl + C` を押します。

操作用ターミナル:

```bash
pwd
ls pom.xml
mvn compile
```

アプリ用ターミナル:

```bash
mvn spring-boot:run
```

ブラウザで `http://localhost:8080/` を開きます。確認後、`Ctrl + C` で停止します。

### 期待結果

- 見出し「勤怠管理」が表示される
- 今日の日付が表示される
- Controllerに書いたメッセージが表示される
- HTTPステータスは200

### つまずき

- 404
  - `@Controller`、`@GetMapping("/")`、packageの位置を確認します。
- `Template ... might not exist`
  - `src/main/resources/templates/index.html` の綴りを確認します。
- `${workDate}` がそのまま見える
  - `th:text` と `xmlns:th` を確認します。
- Javaを変更しても反映されない
  - アプリを停止してから再コンパイル・再起動します。

### チェックポイント

- [ ] `GET /` の入口メソッドを指せる
- [ ] `"index"` が `templates/index.html` を表すと説明できる
- [ ] Modelの属性名と `${...}` が一致している
- [ ] `th:text` を一度変更し、画面の変化を確認した
- [ ] 確認後にアプリを停止した

---

## Phase 2: DIでControllerとServiceを分ける

### 目的

- Controllerから画面表示用の処理をServiceへ移す
- SpringがServiceのインスタンスを作り、Controllerへ渡すことを確認する
- コンストラクタインジェクションをJavaコードとして読む

### 新出語

| 用語 | 意味 |
| --- | --- |
| DI | 必要なオブジェクトを外部から渡す設計 |
| Bean | Springが生成・管理するオブジェクト |
| `@Service` | 業務処理を担当するBeanであることを表す |
| コンストラクタインジェクション | コンストラクタ引数を通して依存オブジェクトを受け取る方法 |

### 作成ファイル

- `src/main/java/com/shinesoft/attendance/service/AppInfoService.java`
- `HomeController.java` を編集

`AppInfoService` はDIを小さく確認するための学習用クラスです。Phase 4で勤怠用Serviceへ置き換えます。

### 重要なコードまたは差分

`AppInfoService.java`:

```java
package com.shinesoft.attendance.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

@Service
public class AppInfoService {

    public String buildMessage(LocalDate workDate) {
        return workDate + " の勤怠を確認します";
    }
}
```

`HomeController.java` の差分:

```java
private final AppInfoService appInfoService;

public HomeController(AppInfoService appInfoService) {
    this.appInfoService = appInfoService;
}
```

`index` メソッド内:

```java
LocalDate workDate = LocalDate.now();
model.addAttribute("workDate", workDate);
model.addAttribute("message", appInfoService.buildMessage(workDate));
```

Controller内で `new AppInfoService()` は書きません。

### なぜ

Controllerが表示処理も業務判断もすべて持つと、クラスが大きくなります。DIを使うと、ControllerはHTTP、Serviceは処理という役割に分けられます。

### 停止→起動

アプリ用ターミナルで動作中なら `Ctrl + C`。

操作用ターミナル:

```bash
pwd
ls pom.xml
mvn compile
```

アプリ用ターミナル:

```bash
mvn spring-boot:run
```

ブラウザで `/` を確認し、その後 `Ctrl + C` で停止します。

### 期待結果

- Phase 1と同じ画面が表示される
- メッセージが「日付 + の勤怠を確認します」になる
- `HomeController` のコンストラクタが自動で呼ばれる

### つまずき

- `AppInfoService` のBeanが見つからない
  - `@Service` とpackageが `com.shinesoft.attendance` 以下か確認します。
- `final` フィールドが初期化されていない
  - コンストラクタで `this.appInfoService = appInfoService` と代入します。
- 画面が古いまま
  - 停止、`mvn compile`、再起動の順をやり直します。

### チェックポイント

- [ ] ControllerとServiceの役割を一文ずつ説明できる
- [ ] Springがどのクラスを生成するか指せる
- [ ] コンストラクタ引数とフィールドの型が一致している
- [ ] `new AppInfoService()` が不要な理由を説明できる
- [ ] 確認後にアプリを停止した

---

## Phase 3: JPA、H2、Flywayでデータを保存する

### 目的

- JavaのEntityとDBテーブルを対応付ける
- Repositoryから検索・保存できる構造を作る
- Flywayでテーブル構造を履歴管理する
- H2 Consoleで実際のテーブルを確認する

### 新出語

| 用語 | 意味 |
| --- | --- |
| JPA | JavaオブジェクトとDBの行を対応付ける標準 |
| Hibernate | このアプリでJPAを実装するライブラリ |
| H2 | 開発時にPC内で使う軽量DB |
| Flyway | SQLを番号順に適用するMigrationツール |
| Repository | Entityを検索・保存するインターフェース |
| `ddl-auto: validate` | EntityとDB構造が一致するか確認し、自動作成はしない設定 |

### 作成ファイル

- `pom.xml` を編集
- `application.yml` を置き換え
- `application-dev.yml`
- `application-prod.yml`
- `src/main/resources/db/migration/V1__create_tables.sql`
- `src/main/resources/db/migration/V2__add_index_to_attendance_work_date.sql`
- `domain/User.java`
- `domain/Attendance.java`
- `domain/AttendanceStatus.java`
- `repository/UserRepository.java`
- `repository/AttendanceRepository.java`

### 重要なコードまたは差分

#### 1. DB用依存関係

`pom.xml` の `<dependencies>` 内へ追加します。

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>org.mariadb.jdbc</groupId>
  <artifactId>mariadb-java-client</artifactId>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-mysql</artifactId>
  <scope>runtime</scope>
</dependency>
```

#### 2. 共通設定

`application.yml` を置き換えます。

```yaml
spring:
  application:
    name: ${APP_NAME:attendance-management}
  profiles:
    default: dev
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: ${SHOW_SQL:false}
    open-in-view: false
    properties:
      hibernate:
        jdbc:
          time_zone: Asia/Tokyo
  thymeleaf:
    cache: false
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: ${SERVER_PORT:8080}
  address: ${SERVER_ADDRESS:127.0.0.1}
  error:
    include-message: never
    include-stacktrace: never

logging:
  level:
    root: ${LOG_LEVEL:INFO}

app:
  name: ${APP_NAME:attendance-management}
```

`application-dev.yml`:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:h2:file:./data/attendance;MODE=MariaDB;DB_CLOSE_ON_EXIT=FALSE}
    username: ${DB_USER:sa}
    password: ${DB_PASSWORD:}
    driver-class-name: ${DB_DRIVER:org.h2.Driver}
  h2:
    console:
      enabled: true
      path: /h2-console
  thymeleaf:
    cache: false
```

`application-prod.yml`:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: ${DB_DRIVER:org.mariadb.jdbc.Driver}
  h2:
    console:
      enabled: false
  thymeleaf:
    cache: true

app:
  seed:
    enabled: ${APP_SEED_ENABLED:false}
    admin-password: ${APP_SEED_ADMIN_PASSWORD:}
    user-password: ${APP_SEED_USER_PASSWORD:}
```

#### 3. Migration SQL

`V1__create_tables.sql`:

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(30) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT uk_users_username UNIQUE (username)
);

CREATE TABLE attendances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_attendances_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_attendances_user_work_date
        UNIQUE (user_id, work_date)
);
```

`V2__add_index_to_attendance_work_date.sql`:

```sql
CREATE INDEX idx_attendances_work_date
    ON attendances(work_date);
```

`V1` が最初の構造、`V2` が後から追加した変更です。一度適用されたV1を編集せず、新しい変更は次の番号へ追加します。

#### 4. Entity

`User.java` の中心:

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 20)
    private String role;

    // id、username、password、roleのgetter / setter
}
```

`AttendanceStatus.java`:

```java
package com.shinesoft.attendance.domain;

public enum AttendanceStatus {
    NOT_STARTED("未出勤"),
    WORKING("出勤中"),
    FINISHED("退勤済み");

    private final String label;

    AttendanceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
```

`Attendance.java` のフィールド対応:

```java
@Entity
@Table(
    name = "attendances",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "work_date"})
)
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

保存・更新時刻は次で設定します。

```java
@PrePersist
public void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    if (status == null) {
        status = AttendanceStatus.NOT_STARTED;
    }
    if (createdAt == null) {
        createdAt = now;
    }
    updatedAt = now;
}

@PreUpdate
public void preUpdate() {
    updatedAt = LocalDateTime.now();
}
```

すべてのフィールドへgetter / setterを追加します。まず自分で作成し、コンパイル後に完成版と比較します。

#### 5. Repository

`UserRepository.java`:

```java
package com.shinesoft.attendance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinesoft.attendance.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    long countByRole(String role);
}
```

`AttendanceRepository.java` の最初の形:

```java
package com.shinesoft.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shinesoft.attendance.domain.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUser_IdAndWorkDate(Long userId, LocalDate workDate);
    List<Attendance> findByUser_IdOrderByWorkDateDesc(Long userId);

    @EntityGraph(attributePaths = "user")
    List<Attendance> findAllByOrderByWorkDateDesc();

    @EntityGraph(attributePaths = "user")
    Optional<Attendance> findWithUserById(Long id);

    boolean existsByUser_Id(Long userId);
}
```

### なぜ

FlywayがDBの構造を作り、JPAはJavaとDBを対応付けます。`ddl-auto: validate` により、列名などがずれていれば起動時に失敗します。失敗を隠すのではなく、EntityとSQLを一致させます。

### 停止→起動

アプリ用ターミナルで動作中なら `Ctrl + C`。

操作用ターミナル:

```bash
pwd
ls pom.xml
mvn compile
```

アプリ用ターミナル:

```bash
mvn spring-boot:run
```

ブラウザで `http://localhost:8080/h2-console` を開きます。

| 項目 | 値 |
| --- | --- |
| JDBC URL | `jdbc:h2:file:./data/attendance;MODE=MariaDB;DB_CLOSE_ON_EXIT=FALSE` |
| User Name | `sa` |
| Password | 空欄 |

SQL:

```sql
SELECT * FROM users;
SELECT * FROM attendances;

SELECT "installed_rank", "version", "description", "success"
FROM "flyway_schema_history"
ORDER BY "installed_rank";
```

確認後、アプリ用ターミナルで `Ctrl + C`。

### 期待結果

- `users` と `attendances` が存在する
- どちらもまだ0件
- Flyway履歴にV1、V2が記録される
- アプリを再起動してもMigrationが重複実行されない

### つまずき

- `Schema-validation: missing table`
  - Migrationの場所とファイル名、起動ログを確認します。
- `Schema-validation: missing column`
  - Entityの `@Column` とV1の列名を比較します。
- H2 Consoleで空の別DBが開く
  - JDBC URLを1文字ずつ `application-dev.yml` と比較します。
- `flyway_schema_history` が見つからない
  - H2では上記のように小文字名を二重引用符で囲みます。
- 適用済みV1を変更してchecksumエラー
  - V1を元へ戻し、新しい変更は次の番号へ追加します。

### チェックポイント

- [ ] Entityとテーブルを1対1で対応付けられる
- [ ] 主キー、外部キー、UNIQUEをV1から指せる
- [ ] Repositoryの型引数 `Attendance, Long` を説明できる
- [ ] V1とV2が成功している
- [ ] `ddl-auto: validate` の役割を説明できる
- [ ] 確認後にアプリを停止した

答え合わせ:

- [`User.java`](../../../complete/src/main/java/com/shinesoft/attendance/domain/User.java)
- [`Attendance.java`](../../../complete/src/main/java/com/shinesoft/attendance/domain/Attendance.java)
- [`AttendanceStatus.java`](../../../complete/src/main/java/com/shinesoft/attendance/domain/AttendanceStatus.java)
- [`UserRepository.java`](../../../complete/src/main/java/com/shinesoft/attendance/repository/UserRepository.java)
- [`AttendanceRepository.java`](../../../complete/src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java)
- [`V1__create_tables.sql`](../../../complete/src/main/resources/db/migration/V1__create_tables.sql)
- [`V2__add_index_to_attendance_work_date.sql`](../../../complete/src/main/resources/db/migration/V2__add_index_to_attendance_work_date.sql)

---

## Phase 4: 出勤・退勤の業務ルールを作る

### 目的

- 業務ルールをServiceへ実装する
- 状態遷移をJavaの分岐として表現する
- 一時的な確認用ControllerからServiceを実行する

### 新出語

| 用語 | 意味 |
| --- | --- |
| 業務ルール | 利用者の操作として許可する条件 |
| `@Transactional` | まとまったDB操作を成功または失敗の単位にする |
| ロールバック | 処理途中の失敗時にDB変更を戻すこと |
| 状態遷移 | 未出勤、出勤中、退勤済みの変化 |
| `BusinessException` | 利用者の操作が業務ルールに反したことを表す例外 |

### 作成ファイル

- `exception/BusinessException.java`
- `service/AttendanceService.java`
- `web/BusinessDemoController.java`（このPhaseだけの確認用）

Phase 2の `AppInfoService.java` は役割を終えます。VS Codeで削除し、`HomeController` からの参照も外します。

### 重要なコードまたは差分

`BusinessException.java`:

```java
package com.shinesoft.attendance.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

`AttendanceService.java` は、まず本人用のメソッドを実装します。

```java
@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    public Attendance getTodayAttendance(Long userId) {
        return attendanceRepository
            .findByUser_IdAndWorkDate(userId, LocalDate.now())
            .orElse(null);
    }

    public List<Attendance> listAttendances(Long userId) {
        return attendanceRepository.findByUser_IdOrderByWorkDateDesc(userId);
    }

    @Transactional
    public Attendance clockIn(Long userId) {
        LocalDate today = LocalDate.now();
        var existing =
            attendanceRepository.findByUser_IdAndWorkDate(userId, today).orElse(null);
        if (existing != null) {
            throw new BusinessException("すでに出勤済みです");
        }

        User user = getUser(userId);
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setWorkDate(today);
        attendance.setStartTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.WORKING);

        try {
            return attendanceRepository.saveAndFlush(attendance);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("すでに出勤済みです");
        }
    }

    @Transactional
    public Attendance clockOut(Long userId) {
        Attendance attendance = attendanceRepository
            .findByUser_IdAndWorkDate(userId, LocalDate.now())
            .orElseThrow(() ->
                new BusinessException("退勤するには先に出勤してください"));

        if (attendance.getStatus() == AttendanceStatus.FINISHED) {
            throw new BusinessException("すでに退勤済みです");
        }
        if (attendance.getStatus() != AttendanceStatus.WORKING) {
            throw new BusinessException("退勤するには先に出勤してください");
        }

        attendance.setEndTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.FINISHED);
        return attendanceRepository.save(attendance);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("ユーザーが存在しません"));
    }
}
```

状態遷移:

| 現在 | 操作 | 結果 |
| --- | --- | --- |
| 当日データなし | 出勤 | `WORKING`、開始時刻あり |
| 当日データなし | 退勤 | 業務エラー |
| `WORKING` | 出勤 | 業務エラー |
| `WORKING` | 退勤 | `FINISHED`、終了時刻あり |
| `FINISHED` | 退勤 | 業務エラー |

Serviceを画面認証より先に確認するため、一時的な `BusinessDemoController.java` を作ります。

```java
package com.shinesoft.attendance.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinesoft.attendance.domain.Attendance;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.repository.UserRepository;
import com.shinesoft.attendance.service.AttendanceService;

@Controller
public class BusinessDemoController {
    private final AttendanceService attendanceService;
    private final UserRepository userRepository;

    public BusinessDemoController(AttendanceService attendanceService,
                                  UserRepository userRepository) {
        this.attendanceService = attendanceService;
        this.userRepository = userRepository;
    }

    @GetMapping("/demo/status")
    @ResponseBody
    public String status() {
        var user = demoUser();
        Attendance attendance = attendanceService.getTodayAttendance(user.getId());
        return attendance == null ? "NOT_STARTED" : attendance.getStatus().name();
    }

    @PostMapping("/demo/clock-in")
    @ResponseBody
    public String clockIn() {
        try {
            return attendanceService.clockIn(demoUser().getId()).getStatus().name();
        } catch (BusinessException ex) {
            return ex.getMessage();
        }
    }

    @PostMapping("/demo/clock-out")
    @ResponseBody
    public String clockOut() {
        try {
            return attendanceService.clockOut(demoUser().getId()).getStatus().name();
        } catch (BusinessException ex) {
            return ex.getMessage();
        }
    }

    private com.shinesoft.attendance.domain.User demoUser() {
        return userRepository.findByUsername("demo-user")
            .orElseThrow(() -> new BusinessException(
                "H2 Consoleでdemo-userを作成してください"));
    }
}
```

これはService確認用の一時入口です。Phase 6で認証ユーザーを使う正式なControllerへ置き換えます。

### なぜ

ボタンを隠すだけでは、不正なリクエストを防げません。どの入口から呼ばれても同じ規則になるよう、二重出勤や出勤前退勤の判断をServiceへ置きます。

`saveAndFlush` とDBのUNIQUE制約は、ほぼ同時に2件の出勤が来た場合の最後の防御です。

### 停止→起動

アプリ用ターミナルで動作中なら `Ctrl + C`。

操作用ターミナル:

```bash
pwd
ls pom.xml
mvn compile
```

アプリ用ターミナル:

```bash
mvn spring-boot:run
```

H2 Consoleで確認用ユーザーを1件作成します。

```sql
INSERT INTO users (username, password, role)
VALUES ('demo-user', 'temporary', 'ROLE_USER');
```

操作用ターミナル:

```bash
curl -i http://localhost:8080/demo/status
curl -i -X POST http://localhost:8080/demo/clock-in
curl -i -X POST http://localhost:8080/demo/clock-in
curl -i -X POST http://localhost:8080/demo/clock-out
curl -i -X POST http://localhost:8080/demo/clock-out
```

確認後、アプリ用ターミナルで `Ctrl + C`。

### 期待結果

順に次が返ります。

```text
NOT_STARTED
WORKING
すでに出勤済みです
FINISHED
すでに退勤済みです
```

H2:

```sql
SELECT u.username, a.work_date, a.status, a.start_time, a.end_time
FROM attendances a
JOIN users u ON u.id = a.user_id;
```

`demo-user` の当日勤怠が1件だけ存在します。

### つまずき

- `demo-userを作成してください`
  - H2 ConsoleのINSERTと接続先URLを確認します。
- 最初から「すでに出勤済み」
  - ファイルDBに今日のデータが残っています。勤務行を確認します。
- `cannot find symbol`
  - `LocalDate`、`LocalDateTime`、`List`、`DataIntegrityViolationException` のimportを確認します。
- 500になる
  - 起動ログの最初の `Caused by` と、自分のクラスの行番号を確認します。

### チェックポイント

- [ ] 5つの状態遷移を実際に確認した
- [ ] ControllerではなくServiceに規則を書く理由を説明できる
- [ ] `@Transactional` の範囲を指せる
- [ ] Serviceの重複確認とDBのUNIQUE制約の違いを説明できる
- [ ] H2に勤怠が1件だけある
- [ ] 確認後にアプリを停止した

答え合わせ:

- [`BusinessException.java`](../../../complete/src/main/java/com/shinesoft/attendance/exception/BusinessException.java)
- [`AttendanceService.java`](../../../complete/src/main/java/com/shinesoft/attendance/service/AttendanceService.java)

---

## Phase 5: Validationと管理者向け勤怠画面を作る

### 目的

- フォーム入力をJavaオブジェクトとして受け取る
- 単項目の入力検証と、複数項目を使う業務検証を分ける
- 管理者が全勤怠を一覧・修正できる画面を作る

このPhaseでは認証をまだ追加していないため、管理URLは一時的に誰でも開けます。Phase 6で管理者だけに制限します。

### 新出語

| 用語 | 意味 |
| --- | --- |
| Bean Validation | アノテーションで必須・長さ・形式を検証する仕組み |
| `@Valid` | 受け取ったFormの検証を実行する |
| `BindingResult` | 入力エラーの一覧 |
| Form | HTMLフォームから受け取る値をまとめるクラス |
| PRG | POST後にredirectし、再読み込みによる再送を防ぐ流れ |

### 作成ファイル

- `pom.xml` を編集
- `web/form/AdminAttendanceForm.java`
- `web/AdminAttendanceController.java`
- `templates/admin-attendances.html`
- `templates/admin-attendance-form.html`
- `AttendanceService.java` を編集
- `static/styles.css`

### 重要なコードまたは差分

#### 1. Validation依存

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### 2. 管理用メソッド

`AttendanceService` へ追加します。

```java
public Attendance getAttendance(Long id) {
    return attendanceRepository.findWithUserById(id)
        .orElseThrow(() -> new BusinessException("勤怠が存在しません"));
}

public List<Attendance> listAllAttendances() {
    return attendanceRepository.findAllByOrderByWorkDateDesc();
}
```

更新メソッドの入口:

```java
@Transactional
public Attendance updateAttendance(Long attendanceId,
                                   Long userId,
                                   LocalDate workDate,
                                   LocalDateTime startTime,
                                   LocalDateTime endTime,
                                   AttendanceStatus status) {
    if (userId == null || workDate == null || status == null) {
        throw new BusinessException("ユーザー、勤務日、状態は必須です");
    }

    Attendance attendance = attendanceRepository.findById(attendanceId)
        .orElseThrow(() -> new BusinessException("勤怠が存在しません"));

    User user = getUser(userId);
    Attendance sameDay =
        attendanceRepository.findByUser_IdAndWorkDate(userId, workDate).orElse(null);
    if (sameDay != null && !sameDay.getId().equals(attendanceId)) {
        throw new BusinessException("同じ日付の勤怠が既に存在します");
    }

    validateStatusAndTimes(workDate, status, startTime, endTime);

    attendance.setUser(user);
    attendance.setWorkDate(workDate);
    attendance.setStartTime(startTime);
    attendance.setEndTime(endTime);
    attendance.setStatus(status);
    return attendanceRepository.save(attendance);
}
```

状態と時刻の検証:

```java
private void validateStatusAndTimes(LocalDate workDate,
                                    AttendanceStatus status,
                                    LocalDateTime startTime,
                                    LocalDateTime endTime) {
    switch (status) {
        case NOT_STARTED -> {
            if (startTime != null || endTime != null) {
                throw new BusinessException("未出勤の時刻は空にしてください");
            }
        }
        case WORKING -> {
            if (startTime == null || endTime != null) {
                throw new BusinessException("出勤中は開始時刻のみ必要です");
            }
            if (!startTime.toLocalDate().equals(workDate)) {
                throw new BusinessException(
                    "開始時刻の日付は勤務日と一致させてください");
            }
        }
        case FINISHED -> {
            if (startTime == null || endTime == null) {
                throw new BusinessException(
                    "退勤済みは開始・終了時刻が必要です");
            }
            if (!startTime.toLocalDate().equals(workDate)) {
                throw new BusinessException(
                    "開始時刻の日付は勤務日と一致させてください");
            }
            if (endTime.isBefore(startTime)) {
                throw new BusinessException(
                    "終了時刻は開始時刻以降にしてください");
            }
        }
    }
}
```

#### 3. Form

`AdminAttendanceForm` の中心:

```java
public class AdminAttendanceForm {
    @NotNull
    private Long userId;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    @NotNull
    private AttendanceStatus status;

    private String username;

    // 全フィールドのgetter / setter
}
```

#### 4. Controllerのルート

| Method | URL | 処理 | テンプレート |
| --- | --- | --- | --- |
| GET | `/admin/attendances` | 全勤怠一覧 | `admin-attendances` |
| GET | `/admin/attendances/{id}/edit` | 編集初期値 | `admin-attendance-form` |
| POST | `/admin/attendances/{id}` | 検証して更新 | 成功時は一覧へredirect |

Controllerでは `BindingResult` を `@Valid` の直後に置きます。

```java
public String update(
        @PathVariable("id") Long id,
        @Valid @ModelAttribute("form") AdminAttendanceForm form,
        BindingResult binding,
        RedirectAttributes redirectAttributes,
        Model model) {
    if (binding.hasErrors()) {
        form.setUsername(
            attendanceService.getAttendance(id).getUser().getUsername());
        model.addAttribute("attendanceId", id);
        return "admin-attendance-form";
    }

    try {
        attendanceService.updateAttendance(
            id,
            form.getUserId(),
            form.getWorkDate(),
            form.getStartTime(),
            form.getEndTime(),
            form.getStatus());
        redirectAttributes.addFlashAttribute("message", "勤怠を更新しました");
        return "redirect:/admin/attendances";
    } catch (BusinessException ex) {
        binding.reject("business", ex.getMessage());
        form.setUsername(
            attendanceService.getAttendance(id).getUser().getUsername());
        model.addAttribute("attendanceId", id);
        return "admin-attendance-form";
    }
}
```

一覧ControllerとGET編集Controllerは、上のルート表と完成版を答え合わせしながら実装します。この時点では `UserService` をまだ作っていないため、入力エラー時のユーザー名は `AttendanceService#getAttendance` から取得します。Phase 6で最終形へ揃えます。

#### 5. HTML

テンプレートへ必要なModel属性:

| テンプレート | 属性 |
| --- | --- |
| `admin-attendances.html` | `attendances`, `error`, `message` |
| `admin-attendance-form.html` | `form`, `attendanceId` |

編集フォームの中心:

```html
<form th:action="@{|/admin/attendances/${attendanceId}|}"
      method="post"
      th:object="${form}">
  <input type="hidden" th:field="*{userId}">

  <input type="date" th:field="*{workDate}">
  <input type="datetime-local" th:field="*{startTime}">
  <input type="datetime-local" th:field="*{endTime}">

  <select th:field="*{status}">
    <option value="NOT_STARTED">未出勤</option>
    <option value="WORKING">出勤中</option>
    <option value="FINISHED">退勤済み</option>
  </select>

  <ul th:if="${#fields.hasErrors('*')}">
    <li th:each="err : ${#fields.errors('*')}" th:text="${err}">error</li>
  </ul>

  <button type="submit">更新</button>
</form>
```

HTML全体とCSSは表示用の定型部分が多いため、Model属性と `th:field` の対応を先に確認してから完成版と比較します。

### なぜ

`@NotNull` は値があるかを1項目だけで判断できます。一方、「退勤時刻が出勤時刻より後か」は複数項目を使うため、Serviceの業務ルールとして検証します。

### 停止→起動

アプリ用ターミナルで動作中なら `Ctrl + C`。

操作用ターミナル:

```bash
pwd
ls pom.xml
mvn compile
```

アプリ用ターミナル:

```bash
mvn spring-boot:run
```

ブラウザで `http://localhost:8080/admin/attendances` を開きます。Phase 4の `demo-user` の勤怠を編集します。

確認後、アプリ用ターミナルで `Ctrl + C`。

### 期待結果

- 全勤怠の一覧が表示される
- 編集画面に現在値が入る
- 終了時刻を開始時刻より前にすると更新されない
- 正しい値へ直すと一覧へredirectする
- ブラウザ更新でPOSTが再送されない

### つまずき

- 日付・時刻がnullになる
  - `@DateTimeFormat` とHTMLのinput typeを確認します。
- 入力エラー時に500になる
  - `BindingResult` が `@Valid` の直後か確認します。
- `Neither BindingResult nor plain target object`
  - `th:object="${form}"` とControllerの属性名を比較します。
- 一覧で `att.user.username` を読むと例外になる
  - Repositoryの `@EntityGraph` と `open-in-view: false` を確認します。

### チェックポイント

- [ ] 単項目の検証と業務検証を1例ずつ説明できる
- [ ] 状態ごとの開始・終了時刻の条件を説明できる
- [ ] `BindingResult` の置き場所を説明できる
- [ ] 不正な終了時刻がDBへ保存されない
- [ ] 正常更新後にredirectする
- [ ] 確認後にアプリを停止した

答え合わせ:

- [`AdminAttendanceForm.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/form/AdminAttendanceForm.java)
- [`AdminAttendanceController.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java)
- [`admin-attendances.html`](../../../complete/src/main/resources/templates/admin-attendances.html)
- [`admin-attendance-form.html`](../../../complete/src/main/resources/templates/admin-attendance-form.html)
- [`styles.css`](../../../complete/src/main/resources/static/styles.css)

---

## Phase 6: Security、本人画面、ユーザー管理を完成させる

### 目的

- DBのユーザー名とハッシュ化パスワードでログインする
- 一般ユーザーと管理者のアクセス範囲を分ける
- ログイン本人の出勤・退勤画面を完成させる
- 管理者のユーザー作成・編集・削除を完成させる

### 新出語

| 用語 | 意味 |
| --- | --- |
| 認証 | 利用者が誰か確認すること |
| 認可 | 認証済み利用者がその操作を行えるか判断すること |
| Security Filter Chain | Controllerより前で認証・認可を処理する設定 |
| BCrypt | 同じ平文へ戻せない形でパスワードを保存する方式 |
| Principal | ログイン中のユーザー名を得る標準インターフェース |
| CSRF | ログイン中のブラウザへ意図しないPOSTを送らせる攻撃 |

### 作成ファイル

- `pom.xml` を編集
- `config/SecurityConfig.java`
- `config/DataSeeder.java`
- `service/UserService.java`
- `web/AuthController.java`
- `web/HomeController.java` を最終形へ置き換え
- `web/AttendanceController.java`
- `web/form/UserForm.java`
- `web/UserController.java`
- `web/AdminAttendanceController.java` を最終形へ置き換え
- `templates/login.html`
- `templates/index.html` を最終形へ置き換え
- `templates/attendances.html`
- `templates/users.html`
- `templates/user-form.html`
- `static/users.js`
- `application-dev.yml` を編集

ファイル数が多いため、6-A、6-B、6-Cの順に作り、各区切りで停止・コンパイル・起動します。

### 重要なコードまたは差分

### 6-A: ログインまで

#### 1. 依存関係

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

#### 2. 画面用Security設定

このPhaseでは画面用chainだけを作ります。API用chainはPhase 7で追加します。

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login", "/error", "/styles.css", "/users.js", "/favicon.ico")
                    .permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/users/**", "/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers ->
                headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found: " + username));
            return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

必要なimportは完成版と比較します。

#### 3. 初期ユーザー

`application-dev.yml` へ追加:

```yaml
app:
  seed:
    enabled: ${APP_SEED_ENABLED:true}
    admin-password: ${APP_SEED_ADMIN_PASSWORD:admin123}
    user-password: ${APP_SEED_USER_PASSWORD:password}
```

`DataSeeder` は `CommandLineRunner` を実装し、次を行います。

```java
if (userRepository.findByUsername("admin").isEmpty()) {
    User admin = new User();
    admin.setUsername("admin");
    admin.setPassword(passwordEncoder.encode(adminPassword));
    admin.setRole("ROLE_ADMIN");
    userRepository.save(admin);
}

if (userRepository.findByUsername("user1").isEmpty()) {
    User user = new User();
    user.setUsername("user1");
    user.setPassword(passwordEncoder.encode(userPassword));
    user.setRole("ROLE_USER");
    userRepository.save(user);
}
```

設定が有効なときだけ動くよう、クラスへ次を付けます。

```java
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
```

#### 4. Login

`AuthController`:

```java
@Controller
public class AuthController {
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute(
                "error", "ユーザー名またはパスワードが正しくありません");
        }
        if (logout != null) {
            model.addAttribute("message", "ログアウトしました");
        }
        return "login";
    }
}
```

`login.html` の中心:

```html
<form method="post" th:action="@{/login}">
  <label>ユーザー名
    <input type="text" name="username" required>
  </label>
  <label>パスワード
    <input type="password" name="password" required>
  </label>
  <button type="submit">ログイン</button>
</form>
```

#### 5. ログイン中のユーザーを取得するService

6-BのControllerから使うため、ここで `UserService` の最小形を作ります。ユーザー管理用のメソッドは6-Cで同じクラスへ追加します。

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            AttendanceRepository attendanceRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.attendanceRepository = attendanceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() ->
                new BusinessException("ユーザーが見つかりません"));
    }
}
```

`AttendanceRepository` と `PasswordEncoder` は6-Cで使うため、最初からコンストラクタで受け取っておきます。

#### 6-A 停止→起動

アプリ用ターミナルで動作中なら `Ctrl + C`。

操作用ターミナル:

```bash
pwd
ls pom.xml
mvn compile
```

アプリ用ターミナル:

```bash
mvn spring-boot:run
```

ブラウザのシークレットウィンドウで `/` を開き、`/login` へ移動することを確認します。`user1 / password` と `admin / admin123` で順にログインします。

確認後、`Ctrl + C`。

### 6-B: 本人の勤怠画面

Phase 4の `BusinessDemoController.java` を削除します。H2 Consoleで一時データも削除できます。

```sql
DELETE FROM attendances
WHERE user_id = (SELECT id FROM users WHERE username = 'demo-user');

DELETE FROM users WHERE username = 'demo-user';
```

`HomeController` を、Principalから本人を決める最終形へ置き換えます。

```java
var user = userService.getByUsername(principal.getName());
Attendance today = attendanceService.getTodayAttendance(user.getId());
```

POSTもリクエストの `userId` を使いません。

```java
@PostMapping("/clock-in")
public String clockIn(
        RedirectAttributes redirectAttributes,
        Principal principal) {
    var user = userService.getByUsername(principal.getName());
    try {
        attendanceService.clockIn(user.getId());
        redirectAttributes.addFlashAttribute("message", "出勤しました");
    } catch (BusinessException ex) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
    }
    return "redirect:/";
}
```

`AttendanceController`:

```java
@Controller
@RequestMapping("/attendances")
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final UserService userService;

    public AttendanceController(AttendanceService attendanceService,
                                UserService userService) {
        this.attendanceService = attendanceService;
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model, Principal principal) {
        var user = userService.getByUsername(principal.getName());
        model.addAttribute(
            "attendances",
            attendanceService.listAttendances(user.getId()));
        model.addAttribute("username", user.getUsername());
        return "attendances";
    }
}
```

テンプレートの契約:

| 画面 | Model属性 | 操作 |
| --- | --- | --- |
| `index.html` | username、status、startTime、endTimeなど | 出勤・退勤POST |
| `attendances.html` | username、attendances | 本人の履歴を表示 |

表示用HTMLは、まずModel属性と `th:if` / `th:each` の対応を確認してから完成版と答え合わせします。

#### 6-B 停止→起動

アプリ用ターミナルで `Ctrl + C`。

操作用ターミナル:

```bash
pwd
ls pom.xml
mvn compile
```

アプリ用ターミナル:

```bash
mvn spring-boot:run
```

`user1` でログインし、出勤、退勤、本人一覧を確認します。その後 `Ctrl + C`。

### 6-C: ユーザー管理

6-Aで作った `UserService` へ、`list`、`get`、`create`、`update`、`delete` と入力検証用のprivateメソッドを追加します。最終的な責務は次のとおりです。

| メソッド | 規則 |
| --- | --- |
| `create` | 名前、パスワード、ロール、重複を検証してBCrypt保存 |
| `update` | 空パスワードなら維持、重複禁止、最後の管理者を保護 |
| `delete` | 最後の管理者と勤怠履歴ありユーザーを拒否 |

作成処理の中心:

```java
@Transactional
public User create(String username, String rawPassword, String role) {
    username = validateUsername(username);
    validatePassword(rawPassword);
    validateRole(role);
    if (userRepository.findByUsername(username).isPresent()) {
        throw new BusinessException("ユーザー名が既に存在します");
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(rawPassword));
    user.setRole(role);
    return userRepository.save(user);
}
```

最後の管理者:

```java
private boolean isLastAdmin(User user) {
    return "ROLE_ADMIN".equals(user.getRole())
        && userRepository.countByRole("ROLE_ADMIN") <= 1;
}
```

`UserForm` の制約:

```java
public class UserForm {
    @NotBlank
    @Size(max = 30)
    private String username;

    @Size(max = 64)
    private String password;

    @NotBlank
    @Pattern(regexp = "ROLE_ADMIN|ROLE_USER")
    private String role;

    // getter / setter
}
```

ユーザー管理のルート:

| Method | URL | 処理 |
| --- | --- | --- |
| GET | `/users` | 一覧 |
| GET | `/users/new` | 新規フォーム |
| POST | `/users` | 作成 |
| GET | `/users/{id}/edit` | 編集フォーム |
| POST | `/users/{id}` | 更新 |
| POST | `/users/{id}/delete` | 削除 |

Controllerで守ること:

- `BindingResult` は `@Valid UserForm` の直後
- 作成時だけパスワード必須
- ログイン中の自分自身は削除・名前変更・権限変更しない
- Serviceの業務例外を画面エラーへ変換
- 成功時は一覧へredirect

`AdminAttendanceController` は、入力エラー時のユーザー名取得を最終形の `UserService#get` へ置き換えます。

テンプレートの契約:

| 画面 | Model属性 |
| --- | --- |
| `users.html` | users、error、message |
| `user-form.html` | userForm、mode、formAction、必要ならuserId |

検索・ロール絞り込みと削除確認は `users.js` が補助します。権限とValidationは必ずサーバー側にも残します。

#### 6-C 停止→起動

アプリ用ターミナルで `Ctrl + C`。

操作用ターミナル:

```bash
pwd
ls pom.xml
mvn compile
```

アプリ用ターミナル:

```bash
mvn spring-boot:run
```

`admin` でログインし、ユーザー作成・編集・削除と管理者勤怠画面を確認します。一般ユーザーで `/users` を直接開き、403になることも確認します。その後 `Ctrl + C`。

### なぜ

認証済みかどうかだけでは、一般ユーザーと管理者を区別できません。URLの認可をSecurityへ集約し、Controllerへ到達する前に拒否します。

パスワードは平文で保存せず、BCryptでハッシュ化します。本人の勤怠対象はフォームのIDではなくPrincipalから決め、他人のIDへの書き換えを防ぎます。

### 期待結果

- 未認証で `/` を開くと `/login` へ移動
- `user1` は本人の出勤・退勤・一覧を利用できる
- `user1` は `/users` と `/admin/**` で403
- `admin` はユーザー管理と全勤怠管理を利用できる
- H2のpassword列は平文ではない
- 最後の管理者とログイン中の自分を危険な操作から保護する

### つまずき

- Springの自動生成パスワードがログに出る
  - 自作 `SecurityConfig` が読み込まれていません。packageと `@Configuration` を確認します。
- 正しい初期パスワードでログインできない
  - H2の既存ユーザーはDataSeederで上書きされません。接続先DBとusers行を確認します。
- 画面POSTが403
  - `th:action` を使ったformか、画面用chainでCSRFを全無効にしていないか確認します。
- H2 Consoleが枠内に表示されない
  - frame optionsの `sameOrigin` を確認します。
- 一般ユーザーが管理画面へ入れる
  - `/users/**` と `/admin/**` の `hasRole("ADMIN")` を確認します。

### チェックポイント

- [ ] 認証と認可を別々に説明できる
- [ ] 一般ユーザーと管理者のURL範囲を確認した
- [ ] password列がハッシュである
- [ ] 打刻対象がPrincipalから決まる
- [ ] 一般ユーザーの本人一覧に他人の勤怠が出ない
- [ ] 最後の管理者を削除・降格できない
- [ ] 各6-A、6-B、6-Cの変更後に停止・コンパイル・再起動した
- [ ] 最終確認後にアプリを停止した

答え合わせ:

- [`SecurityConfig.java`](../../../complete/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java) はPhase 7でAPI用chainも加わります
- [`DataSeeder.java`](../../../complete/src/main/java/com/shinesoft/attendance/config/DataSeeder.java)
- [`UserService.java`](../../../complete/src/main/java/com/shinesoft/attendance/service/UserService.java)
- [`AuthController.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/AuthController.java)
- [`HomeController.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/HomeController.java)
- [`AttendanceController.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/AttendanceController.java)
- [`UserController.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/UserController.java)
- [`UserForm.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/form/UserForm.java)
- [`index.html`](../../../complete/src/main/resources/templates/index.html)
- [`attendances.html`](../../../complete/src/main/resources/templates/attendances.html)
- [`users.html`](../../../complete/src/main/resources/templates/users.html)
- [`user-form.html`](../../../complete/src/main/resources/templates/user-form.html)
- [`users.js`](../../../complete/src/main/resources/static/users.js)

---

## Phase 7: REST APIと統一エラーJSONを作る

### 目的

- HTMLではなくJSONを返す入口を追加する
- Entityを直接公開せずDTOへ変換する
- APIだけをHTTP Basicかつstatelessにする
- 入力・認証・認可・業務エラーをHTTPステータスで区別する

### 新出語

| 用語 | 意味 |
| --- | --- |
| REST API | HTTPとJSONで機能を提供する入口 |
| DTO | APIで受け取る・返す項目だけを持つ型 |
| `@RestController` | 戻り値をJSONなどのレスポンス本文へ変換するController |
| `@RequestBody` | JSONをJavaオブジェクトとして受け取る |
| HTTP Basic | リクエストごとにユーザー名・パスワードを送る認証 |
| stateless | サーバー側のログインセッションへ依存しない |
| Advice | 複数APIの例外処理をまとめる仕組み |

### 作成ファイル

- `web/api/dto/UserCreateRequest.java`
- `web/api/dto/UserUpdateRequest.java`
- `web/api/dto/UserResponse.java`
- `web/api/dto/ErrorResponse.java`
- `web/api/UserApiController.java`
- `web/api/AttendanceApiController.java`
- `web/api/ApiFallbackController.java`
- `web/api/advice/ApiExceptionHandler.java`
- `SecurityConfig.java` を最終形へ編集

### 重要なコードまたは差分

#### 1. DTO

```java
public record UserCreateRequest(
    @NotBlank @Size(max = 30) String username,
    @NotBlank @Size(min = 8, max = 64) String password,
    @NotBlank @Pattern(regexp = "ROLE_ADMIN|ROLE_USER") String role
) {
}
```

```java
public record UserUpdateRequest(
    @NotBlank @Size(max = 30) String username,
    @Size(min = 8, max = 64) String password,
    @NotBlank @Pattern(regexp = "ROLE_ADMIN|ROLE_USER") String role
) {
}
```

```java
public record UserResponse(Long id, String username, String role) {
}
```

```java
public record ErrorResponse(String code, String message) {
}
```

Responseへpasswordを含めません。

#### 2. ユーザーAPI

| Method | URL | 正常時 |
| --- | --- | ---: |
| GET | `/api/users` | 200 |
| GET | `/api/users/{id}` | 200 |
| POST | `/api/users` | 201 |
| PUT | `/api/users/{id}` | 200 |
| DELETE | `/api/users/{id}` | 204 |

Controllerの中心:

```java
@RestController
@RequestMapping("/api/users")
public class UserApiController {
    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> list() {
        return userService.list().stream()
            .map(this::toResponse)
            .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(
            @Valid @RequestBody UserCreateRequest request) {
        return toResponse(userService.create(
            request.username(), request.password(), request.role()));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(), user.getUsername(), user.getRole());
    }
}
```

GET 1件、PUT、DELETEもルート表と完成版を答え合わせしながら追加します。

#### 3. 勤怠API

```java
@RestController
@RequestMapping("/api/attendances")
public class AttendanceApiController {
    private final AttendanceService attendanceService;
    private final UserService userService;

    public AttendanceApiController(AttendanceService attendanceService,
                                   UserService userService) {
        this.attendanceService = attendanceService;
        this.userService = userService;
    }

    @PostMapping("/clock-in")
    public Map<String, String> clockIn(Principal principal) {
        var user = userService.getByUsername(principal.getName());
        attendanceService.clockIn(user.getId());
        return Map.of("message", "出勤しました");
    }

    @PostMapping("/clock-out")
    public Map<String, String> clockOut(Principal principal) {
        var user = userService.getByUsername(principal.getName());
        attendanceService.clockOut(user.getId());
        return Map.of("message", "退勤しました");
    }
}
```

#### 4. 例外からJSON

| 事象 | HTTP | code |
| --- | ---: | --- |
| DTO入力不正 | 400 | `VALIDATION_ERROR` |
| JSON文法不正 | 400 | `VALIDATION_ERROR` |
| 未認証 | 401 | `UNAUTHORIZED` |
| 権限不足 | 403 | `FORBIDDEN` |
| 未知のAPIパス | 404 | `NOT_FOUND` |
| 未対応HTTPメソッド | 405 | `METHOD_NOT_ALLOWED` |
| 未対応Content-Type | 415 | `UNSUPPORTED_MEDIA_TYPE` |
| 業務ルール違反 | 409 | `BUSINESS_ERROR` |
| 想定外 | 500 | `INTERNAL_SERVER_ERROR` |

`ApiExceptionHandler` の例:

```java
@ExceptionHandler(BusinessException.class)
@ResponseStatus(HttpStatus.CONFLICT)
public ErrorResponse handleBusiness(BusinessException ex) {
    return new ErrorResponse("BUSINESS_ERROR", ex.getMessage());
}

@ExceptionHandler(HttpMessageNotReadableException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public ErrorResponse handleUnreadableMessage(
        HttpMessageNotReadableException ex) {
    return new ErrorResponse(
        "VALIDATION_ERROR", "JSONの形式が不正です");
}
```

`ApiFallbackController` は未知の `/api/**` と未対応メソッドも同じ `ErrorResponse` 形式へ揃えます。

#### 5. API用Security chain

`SecurityConfig` へAPI用chainを追加し、画面用chainへ `@Order(2)` を付けます。

```java
@Bean
@Order(1)
public SecurityFilterChain apiSecurityFilterChain(
        HttpSecurity http,
        ObjectMapper objectMapper) throws Exception {
    AuthenticationEntryPoint entryPoint =
        (request, response, exception) ->
            writeApiError(
                response,
                objectMapper,
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "認証が必要です");

    http
        .securityMatcher("/api/**")
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/users/**").hasRole("ADMIN")
            .requestMatchers("/api/attendances/**").authenticated()
            .anyRequest().authenticated()
        )
        .httpBasic(basic ->
            basic.authenticationEntryPoint(entryPoint))
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .requestCache(AbstractHttpConfigurer::disable)
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(entryPoint)
            .accessDeniedHandler((request, response, exception) ->
                writeApiError(
                    response,
                    objectMapper,
                    HttpStatus.FORBIDDEN,
                    "FORBIDDEN",
                    "この操作を行う権限がありません")));
    return http.build();
}
```

JSONを書き込む `writeApiError` と必要importは完成版と比較して追加します。

### なぜ

画面はForm Loginとセッションを使います。APIはHTTP Basicでリクエストごとに認証し、HTMLへredirectせずJSONで結果を返します。そのため2つのSecurity chainをURLごとに分けます。

Entityを直接返すとpasswordなどの内部項目を誤って公開する可能性があります。DTOでAPIの契約を限定します。

### 停止→起動

アプリ用ターミナルで動作中なら `Ctrl + C`。

操作用ターミナル:

```bash
pwd
ls pom.xml
mvn compile
```

アプリ用ターミナル:

```bash
mvn spring-boot:run
```

操作用ターミナルで確認します。

```bash
# 401: 認証情報なし
curl -i http://localhost:8080/api/users

# 401: 誤ったパスワード
curl -i -u admin:wrong-password http://localhost:8080/api/users

# 403: 一般ユーザー
curl -i -u user1:password http://localhost:8080/api/users

# 200: 管理者
curl -i -u admin:admin123 http://localhost:8080/api/users

# 400: 入力不正
curl -i -u admin:admin123 \
  -H 'Content-Type: application/json' \
  -d '{"username":"","password":"short","role":"ROLE_USER"}' \
  http://localhost:8080/api/users

# 400: JSON文法不正
curl -i -u admin:admin123 \
  -H 'Content-Type: application/json' \
  -d '{not-json' \
  http://localhost:8080/api/users

# 201: 作成
curl -i -u admin:admin123 \
  -H 'Content-Type: application/json' \
  -d '{"username":"api-user","password":"password123","role":"ROLE_USER"}' \
  http://localhost:8080/api/users

# 409: 同じユーザーを再作成
curl -i -u admin:admin123 \
  -H 'Content-Type: application/json' \
  -d '{"username":"api-user","password":"password123","role":"ROLE_USER"}' \
  http://localhost:8080/api/users

# 404: 未知のAPI
curl -i -u admin:admin123 http://localhost:8080/api/not-found
```

確認後、アプリ用ターミナルで `Ctrl + C`。

### 期待結果

- すべてのAPIレスポンスがJSON
- 認証情報なしと誤りは同じ401形式
- 一般ユーザーのユーザー管理APIは403
- Validation違反と壊れたJSONは400
- 作成は201
- 重複は409
- 未知のAPIは404
- `UserResponse` にpasswordがない

### つまずき

- APIがログインHTMLを返す
  - API用chainの `securityMatcher("/api/**")` と `@Order(1)` を確認します。
- POSTが403
  - API用chainが選ばれているか、URLが `/api/...` か確認します。
- 415
  - JSON送信時の `Content-Type: application/json` を確認します。
- 401だけ別形式
  - `httpBasic` と `exceptionHandling` が同じAuthenticationEntryPointを使うか確認します。
- 500
  - `ApiExceptionHandler` の対象packageと、起動ログの最初の例外を確認します。
- Git BashのcurlをPowerShellへ貼って失敗
  - この章のcurlはGit Bashで実行します。

### チェックポイント

- [ ] `@Controller` と `@RestController` の戻り値の違いを説明できる
- [ ] Request DTOとResponse DTOを分ける理由を説明できる
- [ ] 400、401、403、404、409を実際に確認した
- [ ] API打刻がPrincipalの本人を対象にする
- [ ] passwordがJSONへ出ない
- [ ] 画面用chainとAPI用chainの違いを説明できる
- [ ] 確認後にアプリを停止した

答え合わせ:

- [`UserCreateRequest.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/dto/UserCreateRequest.java)
- [`UserUpdateRequest.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/dto/UserUpdateRequest.java)
- [`UserResponse.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/dto/UserResponse.java)
- [`ErrorResponse.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/dto/ErrorResponse.java)
- [`UserApiController.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/UserApiController.java)
- [`AttendanceApiController.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/AttendanceApiController.java)
- [`ApiFallbackController.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/ApiFallbackController.java)
- [`ApiExceptionHandler.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/advice/ApiExceptionHandler.java)
- [`SecurityConfig.java`](../../../complete/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java)

---

## Phase 8: ブラウザ、curl、H2で最終確認する

### 目的

- 全機能がつながって動くことを手動で確認する
- 完成版との差分を、理由を説明しながら直す
- H2からMariaDBへ進める状態にする

### 新出語

| 用語 | 意味 |
| --- | --- |
| スモーク確認 | 主要機能が一通り動くことを短時間で確認すること |
| 回帰 | 変更により、以前動いていた機能が壊れること |
| 外部設定 | 同じJavaコードへ環境ごとの値を外から渡すこと |

### 作成ファイル

このPhaseでは新しい機能ファイルを増やしません。`src/main` とruntime依存関係を完成版と比較し、必要な差分だけを反映します。

### 重要なコードまたは差分

最終構成:

```text
practice/springboot-complete-handson/
├── pom.xml
├── .gitignore
└── src/main/
    ├── java/com/shinesoft/attendance/
    │   ├── AttendanceManagementApplication.java
    │   ├── config/
    │   ├── domain/
    │   ├── exception/
    │   ├── repository/
    │   ├── service/
    │   └── web/
    └── resources/
        ├── application.yml
        ├── application-dev.yml
        ├── application-prod.yml
        ├── db/migration/
        ├── static/
        └── templates/
```

比較順:

1. packageとファイル名
2. `pom.xml` のJavaバージョン、runtime依存、Spring Boot plugin
3. `application*.yml` のインデントとprofile差分
4. Entityの列とV1の列
5. Repositoryのメソッド名
6. Serviceの業務ルール
7. SecurityのURLとrole
8. Form/DTOの制約
9. ControllerのURL、Model属性、戻り値
10. テンプレートの属性名

完成版:

- [`complete/pom.xml`](../../../complete/pom.xml)
- [`complete/src/main`](../../../complete/src/main/)

完成版を全文上書きするのではなく、差分ごとに「何が不足していたか」を説明してから直します。

### なぜ

個別機能が動いても、Security、画面、API、DBを組み合わせると別の問題が見つかることがあります。利用者の操作単位で最終確認します。

### 停止→起動

アプリ用ターミナルで動作中なら `Ctrl + C`。

操作用ターミナル:

```bash
pwd
ls pom.xml
mvn compile
```

アプリ用ターミナル:

```bash
mvn spring-boot:run
```

すべての確認後に `Ctrl + C` で停止します。

### 期待結果

#### ブラウザ

- 未認証の `/` は `/login` へ移動
- `user1` は出勤、退勤、本人履歴を操作
- `user1` は管理URLで403
- `admin` はユーザー作成、編集、削除を操作
- `admin` は全勤怠一覧と修正を操作
- 不正入力は同じフォームへエラー表示
- ログアウト後は保護画面へ戻れない

#### curl

- `/api/users` は認証・権限・入力・業務結果に応じたJSON
- `/api/attendances/clock-in` とclock-outは認証本人を操作
- 未知のAPIもJSONの404

#### H2

```sql
SELECT id, username, role FROM users ORDER BY id;

SELECT
    u.username,
    a.work_date,
    a.status,
    a.start_time,
    a.end_time
FROM attendances a
JOIN users u ON u.id = a.user_id
ORDER BY a.work_date DESC, u.username;

SELECT "version", "description", "success"
FROM "flyway_schema_history"
WHERE "version" IS NOT NULL
ORDER BY "installed_rank";
```

- usersと画面のユーザーが一致
- attendancesと画面の勤怠が一致
- V1、V2が成功

### つまずき

- 完成版との差分が多すぎる
  - package、設定、Entity、Service、Controllerの順に1分類ずつ比較します。
- H2の内容と画面が違う
  - 接続URL、ログインユーザー、勤務日を確認します。
- 同日の出勤を最初からやり直せない
  - 管理者画面で状態を確認します。Migrationファイルを書き換えて初期化しません。
- どの層が原因か分からない
  - HTTPステータス、Controller到達、Serviceログ、H2行の順に確認します。

詳細は [トラブルシューティング](./troubleshooting.md) を使います。

### チェックポイント

- [ ] 一般ユーザーと管理者の全操作を確認した
- [ ] 正常系とエラー系を1つずつコードで追跡できる
- [ ] Controller、Service、Repository、DBの役割を説明できる
- [ ] 画面とAPIが同じServiceを使う理由を説明できる
- [ ] FlywayとJPA validateの役割を説明できる
- [ ] devのH2とprodのMariaDBを設定で切り替える準備ができた
- [ ] `.env` と `data/` がGit管理対象外
- [ ] 最後にアプリを停止した

## 次へ進む

H2版の完成後は、[05-deployment.md](./05-deployment.md) で同じアプリをMariaDBとDocker Composeへ接続します。

完成の基準は、コード量ではありません。少なくとも次の2本を、入力値と戻り値を含めて説明できれば完了です。

1. ブラウザで出勤してH2へ保存されるまで
2. APIの二重出勤が409 JSONになるまで
