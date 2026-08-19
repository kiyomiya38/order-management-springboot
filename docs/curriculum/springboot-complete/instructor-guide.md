# Spring Boot完成版コース 講師ガイド

このガイドは、完成版の短いデモと、空プロジェクトからの受講者ハンズオンを運営する講師向けです。

- 操作台本: [講師デモガイド](./03-instructor-demo.md)
- 事前入門: [JavaからWeb・DBへの入門](./00-java-web-database-primer.md)
- 受講者の作業手順: [ハンズオン](./04-handson-guide.md)
- MariaDBへの切り替え: [デプロイ](./05-deployment.md)
- 復旧支援: [トラブルシューティング](./troubleshooting.md)

受講者はJava基礎を習得済みですが、Web、Spring、SQLは初めてである前提です。専門用語を先に並べず、一つの操作、一つの境界、一つの確認結果を結び付けて進めます。

---

## 1. 教材と作業領域

| 場所 | 用途 | 扱い |
| --- | --- | --- |
| [`docs/curriculum/springboot`](../springboot/) | 既存の段階学習教材 | 変更せず残す |
| [`docs/curriculum/springboot-complete`](./README.md) | 完成版コース | 講義資料 |
| [`complete`](../../../complete/) | 完成済み参照実装 | 講師デモと答え合わせ |
| `practice/springboot-complete-handson` | 受講者の実装 | 空から作成 |

`complete` を受講者の作業場所にしません。行き詰まった場合も、完成版は差分を確認する参照先として使います。

---

## 2. 到達目標

### コア

受講者が次を実装し、画面とコードを行き来しながら説明できる状態を目指します。

- Spring BootのStarter、自動設定、DI
- Spring MVCとThymeleaf
- Controller、Service、Repositoryの責務
- JPA EntityとH2の行の対応
- 出勤、退勤、二重操作拒否
- Spring Securityの認証と認可
- Bean Validationと業務例外
- Flywayと `ddl-auto: validate`

最重要の説明課題:

```text
Browser
  -> Controller
  -> Service
  -> Repository
  -> DB
  -> Controller
  -> HTML
```

### 発展

コアを通過した後に扱います。

- 管理者によるユーザー管理と勤怠修正
- REST API、DTO、HTTPステータス、統一エラーJSON
- 実行可能JAR
- dev/H2とprod/MariaDBの切り替え
- Docker ComposeとVolume

「完成版と同じコードになった」だけでは合格にしません。受講者自身が、入力、呼び出すクラス、DBの変化、画面結果を説明できることを確認します。

---

## 3. 講義前チェック

### 3.1 開発ツール

Git Bashで確認します。

```bash
java -version
mvn -version
git --version
curl --version
docker version
docker compose version
```

前提:

- Java 17
- Maven 3.9以降
- VS CodeのJava Runtimeも17
- Docker Desktopが起動済み
- 通常ウィンドウとシークレットウィンドウが使える

Dockerを扱わない回は、Dockerの二コマンドを省略できます。

### 3.2 完成版をビルドする

リポジトリルートから実行します。

```bash
cd complete
pwd
ls pom.xml
mvn clean -DskipTests package
```

確認:

- `BUILD SUCCESS`
- `target/attendance-management-complete-0.0.1-SNAPSHOT.jar` が作成される

ビルド結果だけで完成とは判断せず、この後のブラウザ、curl、H2、MariaDBの結果まで確認します。

### 3.3 dev/H2のリハーサル

毎回別名のDBを使います。

```bash
export DB_URL='jdbc:h2:file:./data/attendance-rehearsal-01;MODE=MariaDB;DB_CLOSE_ON_EXIT=FALSE'
export SHOW_SQL=true
mvn spring-boot:run
```

期待する順序:

1. H2へ接続
2. FlywayがV1、V2を確認
3. JPAがEntityとスキーマを確認
4. Tomcatが8080番で待受
5. `/login` が応答

手動確認:

| 方法 | 操作 | 合格結果 |
| --- | --- | --- |
| ブラウザ | 未認証で `/` | `/login` へ移動 |
| ブラウザ | `user1 / password` | 本人のトップ画面 |
| ブラウザ | `user1` で `/users` | 403 |
| ブラウザ | 出勤 | 画面が「出勤中」へ変化 |
| H2 | usersをSELECT | `admin` と `user1` |
| H2 | attendancesをSELECT | 画面操作に対応する行 |
| H2 | Flyway履歴をSELECT | V1、V2が成功 |
| curl | 認証なしで `/api/users` | 401 JSON |
| curl | `user1` で `/api/users` | 403 JSON |
| curl | `admin` で `/api/users` | 200 JSON |

確認後は `Ctrl + C` で停止します。

### 3.4 Docker ComposeとMariaDBのリハーサル

`.env` が未作成の場合:

```bash
cp .env.example .env
```

講義専用の秘密値へ変更し、画面共有へ映らない状態で実行します。

```bash
git check-ignore .env
docker compose config --quiet
docker compose up -d --build
docker compose ps
docker compose logs --tail=100 app
```

手動確認:

- dbがhealthy
- appがUp
- `.env` のadminパスワードでログイン
- 画面から確認用ユーザーを作成
- MariaDBのusersに同じ行が存在
- Flyway履歴にV1、V2
- `docker compose down` と再起動後も行が残る

終了:

```bash
docker compose down
```

`down -v` は通常の終了に使いません。

### 3.5 講義用の控え

講義前に次を準備します。

- ビルド済みJAR
- 起動確認済みの新しいH2 DB名
- curlコマンドを貼った講師用メモ
- H2で実行するSELECT文
- Docker imageと依存関係を取得済みの講師PC
- 秘密値を隠した状態の `.env`

ネットワーク障害時は、完成版デモとコード追跡を先に行い、取得が必要な作業を後へ回します。

---

## 4. 推奨する進行

| 順番 | 教材・作業 | 受講者の到達状態 |
| ---: | --- | --- |
| 1 | 完成版の短いデモ | ログインと出勤を見て完成像を持つ |
| 2 | [JavaからWeb・DBへの入門](./00-java-web-database-primer.md) | HTTP、HTML、テーブル、SQLの最小語彙を確認 |
| 3 | [Spring Boot概要](./01-spring-boot-overview.md) | デモの動作とSpring Bootの役割を結ぶ |
| 4 | 出勤一回のコード追跡 | Controller、Service、Repository、DB、HTMLを接続 |
| 5 | ハンズオンPhase 0〜1 | 空プロジェクトからMVC画面を表示 |
| 6 | Phase 2 | Serviceを分け、DIで接続 |
| 7 | Phase 3 | Entity、Repository、H2、Flywayを実装 |
| 8 | Phase 4 | 出勤・退勤の業務ルールを実装 |
| 9 | Phase 5 | Validationと管理者向け勤怠画面を実装 |
| 10 | Phase 6 | Security、本人画面、ユーザー管理を実装 |
| 11 | Phase 7 | REST APIとJSONエラーを実装 |
| 12 | Phase 8 | ブラウザ、curl、H2で完成状態を確認 |
| 13 | [デプロイ](./05-deployment.md) | MariaDBとDocker Composeで再現 |

機能を一度に説明せず、各行で「何を送ったか」「どの行が変わったか」「何が表示されたか」を確認してから次へ進みます。

---

## 5. 最初のデモの進め方

### 5.1 短い完成像

この段階ではコードを開きません。

1. 未認証で `/` を開き、ログインへ移動
2. `user1` でトップと本人一覧を見る
3. `user1` で `/users` を直接開き403を確認
4. `admin` で管理画面を見る

`user1` の出勤ボタンはまだ押しません。次のコード追跡で一回だけ使います。

### 5.2 出勤一回を深く追う

[講師デモガイド](./03-instructor-demo.md) の順序を崩しません。

```text
出勤前のH2
  -> Browserのフォーム
  -> Security
  -> HomeController
  -> AttendanceService
  -> AttendanceRepository
  -> H2の行
  -> redirect
  -> HomeController.index
  -> Model
  -> index.html
  -> Browser
```

各境界で、講師は次の三点だけを確認します。

| 種類 | 例 |
| --- | --- |
| 講師操作 | `service.clockIn(...)` を指す |
| 受講者への問い | Controllerは自分でSQLを書きますか |
| 期待結果 | Serviceへ業務処理を依頼する |

### 5.3 コア追加デモ

出勤の流れを復唱できた後に、次を追加します。

1. APIから二重出勤を要求して409
2. ブラウザから正常退勤
3. 一般ユーザーの403
4. 空欄フォームの入力エラー
5. H2のV1、V2

### 5.4 発展デモ

次を別ブロックとして扱います。

1. REST APIの401、403、400、409、201
2. 管理者の勤怠修正
3. 削除制約と最後の管理者
4. 実行可能JAR
5. MariaDBとVolume

発展中も、入口が変わっただけなのか、Serviceの規則が変わったのかを区別させます。

---

## 6. ハンズオンの基本サイクル

各機能を次の順で進めます。

1. 目的を一文で示す
2. Browserまたはcurlが送る内容を予想
3. 受講者が一つの責務を実装
4. `mvn -DskipTests package` でコンパイルとJAR作成を確認
5. アプリを起動
6. ブラウザ、curl、SQLのいずれかで手動確認
7. ControllerからDBまたはHTMLまで指で追う
8. チェックポイントを記録

最後まで実装してから一度だけ起動すると、原因範囲が広くなります。一つのURLまたは一つの状態変更ごとに確認します。

### 完成版を見せる順番

1. エラーメッセージと直前の変更を受講者が読む
2. URL、HTTP method、package、アノテーションを確認
3. 完成版との差分を一か所だけ確認
4. 必要なら該当メソッドだけ提示
5. 復旧後、処理経路を受講者が説明

ファイル全体を渡して終わりにしません。

---

## 7. フェーズ別の問い

### MVC

- BrowserはどのURLとmethodを送ったか
- Controllerの戻り値はHTMLか、テンプレート名か
- Modelの値はどこでHTMLへ変換されるか
- POST後にredirectする理由は何か

### DI

- ControllerのServiceは誰が作るか
- Controller内でServiceを `new` しない理由は何か
- コンストラクタで必要な部品を受け取る利点は何か

### JPAとDB

- EntityのどのfieldがDBのどの列か
- Repositoryのメソッド名から何を検索するか
- Serviceの重複確認に加えてDB一意制約を置く理由は何か
- H2で今変わった行はどれか

### 業務ルール

- 二重出勤をControllerではなくServiceで拒否する理由は何か
- `NOT_STARTED -> FINISHED` を拒否する場所はどこか
- 不正な時刻でDB行が変わらない理由は何か

### Security

- 認証と認可の違いは何か
- SecurityがControllerより前に動く理由は何か
- 誰の出勤かを `Principal` から決める理由は何か
- 画面でリンクを隠すだけでは不十分なのはなぜか

### API

- 画面ControllerとREST Controllerが共有するものは何か
- EntityではなくDTOを返す理由は何か
- 400、401、403、409をどう使い分けるか

### DB運用

- FlywayとJPA `validate` の役割は何か
- 適用済みV1を編集しない理由は何か
- AppからMariaDBへ `localhost` で接続しない理由は何か

---

## 8. フェーズ別チェックポイント

### Phase 0〜1

- `mvn -DskipTests package` が成功
- `GET /` でThymeleaf画面が表示
- Controller、Model、templateの対応を説明

### Phase 2

- ControllerがServiceをコンストラクタで受け取る
- ControllerとServiceの責務を説明できる
- SpringがServiceを作って渡すことを説明できる

### Phase 3

- H2にusersとattendancesが存在
- Flyway履歴にV1、V2
- EntityとSQLの列を対応付けられる
- Repositoryの派生クエリを読める

### Phase 4

- 出勤前は今日の行が0件
- 出勤後は1件で `WORKING`
- 退勤後は同じ行が `FINISHED`
- 二重出勤、出勤前退勤、二重退勤を拒否
- BrowserからHTMLまでの流れを説明

### Phase 5

- Formの入力エラーでは不正なDB行が増えない
- 管理者向け勤怠画面から状態と時刻を修正
- 不正な時刻の更新を拒否

### Phase 6

- `admin` と `user1` でログイン
- roleの違いをDBと画面で確認
- 未認証、認証済み、権限不足を区別
- 一般ユーザーは管理URLへ入れない
- adminがユーザーを管理
- 最後の管理者を維持

### Phase 7

- 未認証APIは401 JSON
- 一般ユーザーの管理APIは403 JSON
- 不正入力は400 JSON
- 業務違反は409 JSON
- 成功応答にpasswordを含めない
- 打刻対象を認証中の本人から決定

### Phase 8

- ブラウザで一般ユーザーと管理者の主要操作を確認
- curlで401、403、400、409、2xxを確認
- H2の行と画面・APIの結果を対応付ける

### JARとMariaDB

- `mvn clean -DskipTests package` が成功
- `java -jar` で `/login` が応答
- MariaDBにV1、V2と画面操作の行が存在
- Compose再作成後もデータが残る
- `.env` がGit管理外

---

## 9. 手動評価

### 9.1 必須証拠

受講者は次を講師へ見せます。

| 証拠 | 内容 |
| --- | --- |
| ビルド | `mvn clean -DskipTests package` の `BUILD SUCCESS` |
| Browser | ログイン、出勤、退勤、403、入力エラー |
| curl | 401、403、400、409、成功応答 |
| H2 | users、attendances、Flyway履歴 |
| コード説明 | 出勤一回をBrowserからHTMLまで追跡 |
| MariaDB | 画面操作の行とFlyway履歴 |
| Volume | Compose再作成後も行が残る |

スクリーンショットだけではなく、実際のURL、HTTPステータス、SQL結果、該当ソースをその場で開いてもらいます。

### 9.2 必須の口頭説明

全員が次の一本を説明します。

- 画面から出勤し、DBへ保存され、出勤中のHTMLが表示されるまで

追加で次から二本を選びます。

- 二重出勤が409になるまで
- 一般ユーザーが管理画面で403になるまで
- 不正なユーザー作成が400になるまで
- 重複ユーザー作成が409になるまで
- アプリ起動時にFlywayがV1、V2を確認するまで
- prodプロファイルでMariaDBへ接続するまで

### 9.3 判定

| レベル | 判定 |
| --- | --- |
| 未達 | 完成版を起動できるが、操作とコードを対応付けられない |
| コア達成 | H2で出退勤を実装し、BrowserからHTMLまで説明できる |
| 標準達成 | Security、Validation、管理画面、APIを手動確認できる |
| 完了 | JAR、Flyway、MariaDB、Docker Composeまで再現できる |
| 発展 | 新しい業務規則を追加し、画面、API、DBの結果を説明できる |

入力速度ではなく、再現性、説明、手動確認を重視します。

---

## 10. レビュー観点

| 観点 | 確認内容 |
| --- | --- |
| 動作 | 正常系だけでなく拒否結果も確認 |
| レイヤー | HTTPはController、業務規則はService、DBはRepository |
| DI | コンストラクタ注入を使用 |
| DB | Entityとmigrationが一致、一意制約と外部キーあり |
| Security | URL、role、本人性、CSRFの範囲 |
| Validation | 単項目検査と業務規則を区別 |
| API | DTO、ステータス、Content-Type、統一エラー |
| 設定 | devとprodをコード変更なしで切り替え |
| 配布 | 秘密値をJAR、image、Gitへ含めない |

---

## 11. よくある誤解への対応

| 誤解 | 手動で確認させること |
| --- | --- |
| `@SpringBootApplication` が全処理を作る | 自動設定と自作クラスを分けて指す |
| ControllerがSQLを書く | ControllerからService、Repositoryへ順に移動 |
| EntityがあればDBも自動で正しくなる | Flyway履歴と `ddl-auto: validate` を確認 |
| ボタンを隠せば不正操作を防げる | curlで同じURLを直接呼ぶ |
| `@Valid` が業務ルールも判断する | 重複やDB状態を使う規則をServiceで確認 |
| 例外はすべて500 | curlで400、401、403、409を比較 |
| H2で動けばMariaDBでも同じ | MariaDBの行とFlyway履歴を確認 |
| `.env` は設定なのでコミットしてよい | `git check-ignore .env` を実行 |

---

## 12. つまずいた受講者への支援

次の順に質問します。

1. 何を操作したか
2. 何が起きると予想したか
3. 実際の画面、ステータス、DB行はどうなったか
4. 最後に成功した境界はどこか
5. 直前に変更したファイルは何か
6. 起動ログの最初の `Caused by` は何か

講師がコードを直す前に、受講者へ次を指してもらいます。

- BrowserまたはcurlのURLとHTTP method
- Controllerのmapping
- Serviceの呼び出し
- Repositoryの検索
- H2またはMariaDBの行
- SecurityのURL規則
- templateの `th:text` または `th:if`

その後、[トラブルシューティング](./troubleshooting.md) の該当境界を使います。

---

## 13. データと環境の復旧

### H2

既存DBを消さず、別名で起動します。

```bash
DB_URL='jdbc:h2:file:./data/attendance-retry-01;MODE=MariaDB;DB_CLOSE_ON_EXIT=FALSE' \
  mvn spring-boot:run
```

再試行時は末尾の番号を変えます。

### Docker

通常停止:

```bash
docker compose down
```

既存データを残して別環境を作る場合:

```bash
docker compose -p attendance-demo-02 up -d --build
docker compose -p attendance-demo-02 down
```

`docker compose down -v` は対象projectのDB Volumeを削除します。破棄可能な講義データであることと対象project名を確認した場合だけ実行します。

### Flyway

checksumエラーで履歴テーブルを直接書き換えません。

1. 適用済みSQLを元へ戻す
2. 変更は新しいV3へ追加
3. 使い捨てH2だけを新しいDB名でV1から再現

---

## 14. 終了時チェックリスト

### 講師

- [ ] `complete` を `mvn clean -DskipTests package` でビルドした
- [ ] 新しいH2 DBで短い完成版デモを行った
- [ ] 出勤一回をBrowserからHTMLまで追跡した
- [ ] H2で操作前後の行を確認した
- [ ] コアと発展を混在させず進行した
- [ ] MariaDB版を手動確認した
- [ ] `.env` や実パスワードを共有していない
- [ ] 不要なアプリとComposeを停止した

### 受講者

- [ ] 作業が `practice/springboot-complete-handson` にある
- [ ] `mvn clean -DskipTests package` が成功する
- [ ] Browserで正常操作と拒否結果を確認した
- [ ] curlで主要なHTTPステータスを確認した
- [ ] H2とMariaDBで画面操作に対応する行を確認した
- [ ] Flyway履歴のV1、V2を確認した
- [ ] 出勤一回の処理経路を説明した
- [ ] `.env` をGitへ含めていない

受講者には完成コードだけでなく、手動確認結果と、自分の言葉で説明した処理経路を提出させます。
