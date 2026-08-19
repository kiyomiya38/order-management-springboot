# 講師デモガイド

## このガイドの目的

完成版を最初に短く見せ、その直後に一つの「出勤」操作を最後まで追跡します。受講者はJava基礎を習得済みですが、Web、Spring、SQLは初めてである前提です。

HTTP、HTML、SQLの用語で止まる場合は、先に [JavaからWeb・DBへの入門](./00-java-web-database-primer.md) と [用語集](./glossary.md) の該当項目だけを確認します。

デモの中心は次の一本です。

```text
Browser
  -> Controller
  -> Service
  -> Repository
  -> DB
  -> Controller
  -> HTML
  -> Browser
```

Spring SecurityはControllerへ入る前の門番として、この流れの入口で確認します。最初から多くの機能を並べず、まず一本の処理を理解してから、認可、入力エラー、REST API、MariaDBへ広げます。

このガイドでは、各場面を次の三点で進めます。

1. 講師操作
2. 受講者への問い
3. 期待結果

---

## 1. デモの構成

### コア

必ずこの順序で扱います。

1. 完成版を短く見る
2. 出勤前のDBを確認する
3. ブラウザから出勤する
4. Controller、Service、Repositoryを順に読む
5. DBへ保存された行を確認する
6. redirect後にHTMLが再生成される流れを読む
7. 二重出勤、403、入力エラーを一つずつ確認する
8. H2上のFlyway履歴を確認する

### 発展

コアを説明できるようになってから扱います。

- REST APIの401、403、400、409、201
- 管理者による勤怠修正
- 最後の管理者と勤怠履歴を守るルール
- 実行可能JAR
- Docker Compose、MariaDB、Volume

発展から始めると、画面、HTTP、DB、コンテナの問題が同時に見えてしまいます。最初はdevプロファイルとH2だけに絞ります。

---

## 2. 講師の事前準備

### 2.1 使用するターミナル

本編のコマンドはGit Bashへ統一します。PowerShellと引用符や行継続を混在させません。

リポジトリルートから完成版へ移動します。

```bash
cd complete
pwd
```

次のファイルがあることを確認します。

```bash
ls pom.xml
ls src/main/java/com/shinesoft/attendance
ls src/main/resources/db/migration
```

### 2.2 ツールとビルド

```bash
java -version
mvn -version
curl --version
mvn clean -DskipTests package
```

期待結果:

- Java 17
- Maven 3.9以降
- `BUILD SUCCESS`
- `target/attendance-management-complete-0.0.1-SNAPSHOT.jar` が存在

ビルドが失敗している状態で、ブラウザ操作へ進みません。

### 2.3 毎回新しいH2 DBを使う

前回の出勤行が残っていると、最初の出勤が二重出勤になります。既存DBを削除せず、デモごとに名前を変えます。

```bash
export DB_URL='jdbc:h2:file:./data/attendance-instructor-01;MODE=MariaDB;DB_CLOSE_ON_EXIT=FALSE'
export SHOW_SQL=true
```

再度行う場合は、`attendance-instructor-02` のように末尾を変更します。

### 2.4 起動

```bash
mvn spring-boot:run
```

このターミナルを「起動ログ用」として残します。`Started AttendanceManagementApplication` が表示されてからブラウザを開きます。

別のGit Bashを使う場合も、最初に `complete` へ移動します。

### 2.5 dev用アカウント

| 権限 | ユーザー名 | パスワード |
| --- | --- | --- |
| 管理者 | `admin` | `admin123` |
| 一般ユーザー | `user1` | `password` |

これはdev専用です。Docker Composeでは `.env` に設定した値を使います。

---

## 3. 最初の短い完成版デモ

ここでは完成像だけを見せます。まだコードを詳しく開かず、`user1` の出勤ボタンも押しません。

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| シークレットウィンドウで `http://localhost:8080/` を開く | ログインしていない利用者へ、業務画面を見せてよいですか | `/login` へ移動する |
| `user1 / password` でログインする | 一般ユーザーが行う操作は何ですか | 今日の状態、出勤ボタン、本人の勤怠一覧リンクが見える |
| 出勤ボタンを押さず、`/users` を直接開く | リンクを隠すだけで管理機能を守れますか | 403になり、管理画面を開けない |
| 通常ウィンドウで `admin / admin123` としてログインする | 一般ユーザーとの違いは何ですか | アカウント管理と勤怠管理を開ける |

講師は最後に次だけを伝えます。

> 同じアプリの中でも、利用者、URL、権限によって入口が変わります。これから `user1` の出勤一回だけに絞り、ブラウザからDBとHTMLまで追います。

---

## 4. 出勤操作を追跡する準備

### 4.1 開くファイル

次をこの順番でタブへ用意します。

1. [`index.html`](../../../complete/src/main/resources/templates/index.html)
2. [`HomeController.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/HomeController.java)
3. [`AttendanceService.java`](../../../complete/src/main/java/com/shinesoft/attendance/service/AttendanceService.java)
4. [`AttendanceRepository.java`](../../../complete/src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java)
5. [`Attendance.java`](../../../complete/src/main/java/com/shinesoft/attendance/domain/Attendance.java)
6. [`V1__create_tables.sql`](../../../complete/src/main/resources/db/migration/V1__create_tables.sql)

最初は一度に一ファイルだけ表示します。

### 4.2 H2 Consoleへ接続する

ブラウザの別タブで次を開きます。

```text
http://localhost:8080/h2-console
```

| 項目 | 値 |
| --- | --- |
| Driver Class | `org.h2.Driver` |
| JDBC URL | 起動時に設定した `DB_URL` と完全に同じ値 |
| User Name | `sa` |
| Password | 空欄 |

今回の例:

```text
jdbc:h2:file:./data/attendance-instructor-01;MODE=MariaDB;DB_CLOSE_ON_EXIT=FALSE
```

### 4.3 出勤前の行を確認する

```sql
SELECT
    u.username,
    a.work_date,
    a.start_time,
    a.end_time,
    a.status
FROM attendances a
JOIN users u ON u.id = a.user_id
WHERE u.username = 'user1'
  AND a.work_date = CURRENT_DATE;
```

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| SQLを実行する | 今日の出勤前なので、何行返るはずですか | 0行 |
| `attendances` の列をV1と見比べる | Javaのオブジェクトを保存するには、DB側に何が必要ですか | user、勤務日、時刻、状態を置く列が必要 |

---

## 5. BrowserからControllerへ

シークレットウィンドウの `user1` 画面へ戻ります。

### 5.1 送信内容を予想する

[`index.html`](../../../complete/src/main/resources/templates/index.html) の出勤フォームを開きます。

```html
<form th:if="${status.name() == 'NOT_STARTED'}"
      method="post"
      th:action="@{/clock-in}">
```

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| フォームの `method` と `th:action` を示す | ボタンを押すと、どのHTTP methodとURLが送られますか | `POST /clock-in` |
| フォームに `userId` がないことを示す | 誰の出勤かを、ブラウザ入力だけで決めてよいですか | 認証中の本人から決める |
| ブラウザで「出勤」を一度押す | 最初に応答するJavaクラスはどれだと思いますか | Securityの確認後、Controller |

画面では次を確認します。

- `出勤しました`
- 状態が「出勤中」
- 出勤時刻が表示
- 出勤ボタンが退勤ボタンへ変更

### 5.2 SecurityはControllerより前

[`SecurityConfig.java`](../../../complete/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java) を短く開きます。

説明は次の二点に限定します。

- `/clock-in` は認証済みユーザーだけが通る
- 認証できた利用者名が `Principal` としてControllerへ渡る

---

## 6. ControllerからServiceへ

[`HomeController.clockIn`](../../../complete/src/main/java/com/shinesoft/attendance/web/HomeController.java) を開きます。

追う順番:

```text
Principal.getName()
  -> UserService.getByUsername(...)
  -> AttendanceService.clockIn(userId)
  -> flash message
  -> redirect:/
```

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| `principal.getName()` を示す | `user1` はフォーム値とログイン情報のどちらから来ますか | ログイン情報 |
| `service.clockIn(user.getId())` を示す | Controller自身がSQLを書く必要はありますか | ない。業務処理をServiceへ依頼する |
| `redirect:/` を示す | POSTの直後にHTMLを直接返さないのはなぜですか | 再読み込みによるPOST再送を避ける |

Controllerの責務は、HTTP入力を受け、認証中の利用者を特定し、Serviceを呼び、次の画面遷移を決めることです。

---

## 7. ServiceからRepositoryへ

[`AttendanceService.clockIn`](../../../complete/src/main/java/com/shinesoft/attendance/service/AttendanceService.java) を上から順に読みます。

```text
今日の日付を取得
  -> 同じ利用者・同じ日の行を検索
  -> 既にあれば拒否
  -> Attendanceを作成
  -> startTimeとWORKINGを設定
  -> Repositoryへ保存を依頼
```

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| 同日検索を示す | 二重出勤をControllerではなくServiceで判断する利点は何ですか | 画面とAPIの両方で同じ規則を使える |
| `Attendance` の生成とsetterを示す | 保存前のJavaオブジェクトには何が入りますか | user、workDate、startTime、WORKING |
| `attendanceRepository.save(...)` を示す | Serviceは接続先DBがH2かMariaDBかを知る必要がありますか | Repositoryと設定へ任せるため不要 |
| `@Transactional` を示す | 検索と保存を一つの業務処理として扱うのはなぜですか | 途中失敗時に不完全な更新を残さないため |

Serviceの責務は業務ルールと状態変更です。

---

## 8. RepositoryからDBへ

[`AttendanceRepository.java`](../../../complete/src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java) を開きます。

```java
Optional<Attendance> findByUser_IdAndWorkDate(
    Long userId,
    LocalDate workDate
);
```

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| 派生クエリのメソッド名を区切る | `User_Id` と `WorkDate` は何を表しますか | Entityの関連先IDと勤務日 |
| `JpaRepository` の継承を示す | `save` の実装を自分で書いていないのに使えるのはなぜですか | Spring Data JPAが実装を提供する |
| 起動ログのSQLを示す | Repositoryの呼び出しはDB側で何になりますか | SELECTとINSERT |

次に [`Attendance.java`](../../../complete/src/main/java/com/shinesoft/attendance/domain/Attendance.java) とV1を並べます。

| Entity | DB |
| --- | --- |
| `user` | `user_id` |
| `workDate` | `work_date` |
| `startTime` | `start_time` |
| `endTime` | `end_time` |
| `status` | `status` |

JPAはEntityと行の対応を担当し、Flywayはテーブル自体の変更履歴を担当します。

---

## 9. DBで保存結果を確認する

出勤前に使ったSQLをもう一度実行します。

```sql
SELECT
    u.username,
    a.work_date,
    a.start_time,
    a.end_time,
    a.status
FROM attendances a
JOIN users u ON u.id = a.user_id
WHERE u.username = 'user1'
  AND a.work_date = CURRENT_DATE;
```

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| SQLを再実行する | 出勤前の0行から何が変わりましたか | 1行追加された |
| `start_time` と `status` を示す | 画面の「出勤中」と対応するDB値は何ですか | 時刻あり、`WORKING` |
| `end_time` を示す | まだ退勤していないので何が入りますか | `NULL` |

V1の次の制約も示します。

```text
UNIQUE (user_id, work_date)
```

Serviceが分かりやすいエラーを返し、DB制約が最後の整合性を守る二段構えです。

---

## 10. DBからHTMLへ戻る

出勤のPOST後は `redirect:/` が返っています。ブラウザは続けて `GET /` を送ります。

```text
redirect:/
  -> BrowserがGET /
  -> HomeController.index
  -> AttendanceService.getTodayAttendance
  -> AttendanceRepository
  -> Model
  -> index.html
  -> HTML
  -> Browser表示
```

[`HomeController.index`](../../../complete/src/main/java/com/shinesoft/attendance/web/HomeController.java) と [`index.html`](../../../complete/src/main/resources/templates/index.html) を開きます。

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| `model.addAttribute(...)` を示す | ControllerはHTML文字列を組み立てていますか | 値をModelへ渡している |
| `th:text` を示す | DB由来の時刻を画面へ埋めるのは何ですか | Thymeleaf |
| `th:if` を示す | 出勤ボタンが退勤ボタンへ変わった条件は何ですか | statusが`WORKING` |
| ブラウザを再読み込みする | 状態が消えないのはなぜですか | DBから再取得してHTMLを作るため |

### 一本の処理を受講者に復唱してもらう

次の空欄を受講者に答えてもらいます。

```text
Browserが POST ______ を送る
  -> ______ Controller
  -> Attendance ______
  -> Attendance ______
  -> ______ テーブル
  -> redirect後に GET /
  -> Model
  -> ______.html
```

期待する答え:

```text
/clock-in
HomeController
Service
Repository
attendances
index
```

ここまで説明できれば、コアの処理追跡は完了です。

---

## 11. コアの追加デモ

### 11.1 二重出勤をServiceが拒否する

`user1` は現在出勤中です。別のGit Bashから実行します。

```bash
curl -i -u user1:password \
  -X POST http://localhost:8080/api/attendances/clock-in
```

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| 同じ出勤をAPIから要求する | 画面のボタンを隠すだけで二重出勤を防げますか | HTTP 409、`BUSINESS_ERROR` |
| Serviceの同日検索へ戻る | 画面とAPIのどちらから来ても拒否できる理由は何ですか | 同じServiceを使うため |
| DBのSQLを再実行する | 行数は増えましたか | 1行のまま |

### 11.2 正常に退勤する

ブラウザで「退勤」を押します。

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| 退勤後の画面を確認する | 状態と時刻はどう変わりますか | `FINISHED`、終了時刻あり |
| H2の同じ行を確認する | 新しい行が増えますか | 同じ行がUPDATEされる |

### 11.3 一般ユーザーと管理者を比較する

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| `user1` で `/users` を直接開く | 認証済みでも拒否されるのはなぜですか | ROLE不足で403 |
| `admin` で `/users` を開く | 認証と認可の違いは何ですか | 管理者は許可される |

### 11.4 入力エラー

`admin` でユーザー新規作成を開き、空欄のまま送信します。

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| 空欄を送信する | DBへ到達する前に判断できることは何ですか | 必須・長さ・形式 |
| H2のusersを確認する | 不正な行は保存されましたか | 保存されていない |

### 11.5 Flyway履歴

H2 Consoleで実行します。

```sql
SELECT
    "installed_rank",
    "version",
    "description",
    "success"
FROM "flyway_schema_history"
ORDER BY "installed_rank";
```

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| V1とV2を示す | テーブル変更の適用順はどこに残りますか | Flyway履歴 |
| EntityとV1を並べる | `ddl-auto: validate` は何をしますか | Entityと既存スキーマの一致を確認 |

---

## 12. 発展デモA: REST API

REST APIは画面と別の入口ですが、ServiceとRepositoryは共有します。

### 12.1 手動確認表

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| 認証なしで `GET /api/users` | 未認証APIがログインHTMLを返さないのはなぜですか | 401とJSON |
| `user1` で `GET /api/users` | 認証済みなのに拒否される理由は何ですか | 403とJSON |
| `admin` で不正JSONをPOST | JSONをJavaへ変換できない場合はどこで止まりますか | 400と統一エラー |
| `admin` で正常なユーザーをPOST | Entityをそのまま返さない理由は何ですか | 201、passwordなしのDTO |
| 同じusernameを再度POST | 入力形式が正しくても失敗する理由は何ですか | 409、業務エラー |

Git Bash:

```bash
curl -i http://localhost:8080/api/users

curl -i -u user1:password \
  http://localhost:8080/api/users

curl -i -u admin:admin123 \
  -H 'Content-Type: application/json' \
  -d '{not-json' \
  http://localhost:8080/api/users

curl -i -u admin:admin123 \
  -H 'Content-Type: application/json' \
  -d '{"username":"api-user-01","password":"apiPass123","role":"ROLE_USER"}' \
  http://localhost:8080/api/users
```

開くコード:

1. [`UserApiController.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/UserApiController.java)
2. [`UserCreateRequest.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/dto/UserCreateRequest.java)
3. [`UserResponse.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/dto/UserResponse.java)
4. [`ApiExceptionHandler.java`](../../../complete/src/main/java/com/shinesoft/attendance/web/api/advice/ApiExceptionHandler.java)

---

## 13. 発展デモB: 管理者機能

### 13.1 勤怠の不正な時刻

`admin` で `/admin/attendances` を開き、終了時刻を開始時刻より前にします。

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| 不正な時刻で更新する | 単項目の必須検査だけで前後関係を判断できますか | 更新拒否、業務メッセージ |
| H2の行を確認する | 不正な値は保存されましたか | 元の正常値が残る |

### 13.2 履歴と管理者を守る

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| 勤怠履歴のある `user1` を削除する | 履歴を残す規則はどの層へ置きますか | Serviceが削除拒否 |
| 最後の管理者を降格または削除する | 管理者が0人になると何が起きますか | 操作拒否 |

---

## 14. 発展デモC: JAR

起動中のアプリを `Ctrl + C` で停止します。

```bash
mvn clean -DskipTests package
java -jar target/attendance-management-complete-0.0.1-SNAPSHOT.jar
```

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| `java -jar` で起動する | Tomcatを別途起動していないのに応答するのはなぜですか | 組み込みサーバーを含む実行可能JAR |
| `/login` を開く | Mavenから起動した場合と画面は変わりますか | 同じ画面 |

確認後は `Ctrl + C` で停止し、8080番を空けます。

---

## 15. 発展デモD: Docker ComposeとMariaDB

詳細手順は [H2からMariaDBへの切り替え](./05-deployment.md) を併用します。

### 15.1 準備

```bash
cp .env.example .env
```

`.env` の全パスワードを講義専用値へ変更します。秘密値をプロジェクターへ表示しません。

```bash
git check-ignore .env
docker compose config --quiet
docker compose up -d --build
docker compose ps
```

### 15.2 手動確認

| 講師操作 | 受講者への問い | 期待結果 |
| --- | --- | --- |
| `docker compose ps` を見る | appより先に利用可能になる必要があるサービスは何ですか | dbがhealthy、appがUp |
| `.env` のadminパスワードでログインする | devのパスワードと異なるのはなぜですか | prodは外部設定を使う |
| 画面から確認用ユーザーを作る | H2ではなくMariaDBへ保存された根拠は何ですか | prodログとMariaDBの行 |
| Composeを再作成する | コンテナを消すとDB行も消えますか | Volumeにより残る |

MariaDBの行とFlyway履歴:

```bash
docker compose exec db mariadb -uattendance_app -p attendance
```

```sql
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

SELECT id, username, role
FROM users
ORDER BY id;
```

永続化:

```bash
docker compose down
docker compose up -d
docker compose ps
```

再ログインし、作成したユーザーが残ることを確認します。

終了:

```bash
docker compose down
```

`docker compose down -v` はDB Volumeを削除するため、通常の終了では使いません。

---

## 16. デモ中の問いかけ一覧

| 境界 | 問い | 期待する短い答え |
| --- | --- | --- |
| Browser | 出勤ボタンは何を送るか | `POST /clock-in` |
| Security | Controllerより前に何を確認するか | 認証とURL権限 |
| Controller | 誰の出勤かをどこから決めるか | `Principal` |
| Service | 二重出勤をどこで拒否するか | 業務ルール |
| Repository | SQLを手書きせず検索できる理由 | Spring Data JPA |
| DB | 同日二重行を最後に防ぐもの | 一意制約 |
| HTML | DBの状態を画面へ埋めるもの | ModelとThymeleaf |
| API | 画面と共有するもの | Service |
| Flyway | DB変更の順序を残す場所 | migration履歴 |
| Docker | AppからDBを `db` で呼ぶ理由 | Composeのサービス名 |

答えが出ない場合、講師がすぐ説明を完成させず、該当ファイルの一行を指してもう一度問いかけます。

---

## 17. デモ完了チェックリスト

### コア

- [ ] 新しいH2 DB名で起動した
- [ ] 最初の短い完成像を見せた
- [ ] `user1` の出勤前DBが0行であることを確認した
- [ ] BrowserからControllerへ入る箇所を確認した
- [ ] ControllerからServiceへの引数を確認した
- [ ] Serviceの業務判断と状態変更を確認した
- [ ] Repositoryの検索と保存を確認した
- [ ] H2に1行保存されたことを確認した
- [ ] redirect後にModelとThymeleafでHTMLが変わることを確認した
- [ ] 二重出勤、403、入力エラーを一つずつ確認した
- [ ] FlywayのV1、V2を確認した
- [ ] 受講者が処理順を自分の言葉で復唱した

### 発展

- [ ] REST APIの主要ステータスとJSONを確認した
- [ ] 管理者の勤怠修正と削除制約を確認した
- [ ] `mvn clean -DskipTests package` でJARを作成した
- [ ] `java -jar` で起動した
- [ ] MariaDB上の行とFlyway履歴を確認した
- [ ] Compose再作成後もデータが残ることを確認した
- [ ] `docker compose down` で終了した

---

## 18. ハンズオンへの接続

デモ後は [空プロジェクトからのハンズオン](./04-handson-guide.md) へ進みます。

受講者へ、今から作る順番を次の地図で示します。

```text
最小画面
  -> DBとEntity
  -> ログイン
  -> 出勤Service
  -> 管理画面
  -> REST API
  -> Flyway
  -> MariaDB
```

完成版のコードを暗記することが目的ではありません。各Phaseで「BrowserからHTMLまでのどこを今作っているか」を説明できることを確認します。
