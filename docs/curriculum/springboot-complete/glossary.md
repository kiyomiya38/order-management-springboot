# Spring Boot完成版コース 用語集

この用語集は、本文で分からない言葉が出たときに参照するためのものです。最初からすべて暗記する必要はありません。

説明は、この勤怠管理アプリでの使い方に合わせて簡略化しています。

## JavaとSpring

| 用語 | 説明 |
| --- | --- |
| アノテーション | `@Controller`のように、クラスやメソッドへ役割や追加情報を付ける目印。Springが読み取る |
| Framework | アプリの大きな実行の流れを持ち、決められた場所で開発者のコードを呼ぶ仕組み |
| Spring Framework | DI、Web処理、トランザクションなど、Springアプリの基盤を提供するFramework |
| Spring Boot | Spring Frameworkを使うための依存関係、初期設定、起動、配布をまとめやすくする仕組み |
| Springコンテナ | アプリで使うオブジェクトを作り、必要な場所へ渡すSpringの仕組み |
| ApplicationContext | SpringコンテナをJava上で表す中心的な仕組み。この教材の最初の段階ではSpringコンテナとほぼ同じものとしてよい |
| Bean | Springコンテナが作成して管理するオブジェクト |
| 依存関係 | あるクラスが動くために、別のクラスやインターフェースを必要とする関係 |
| DI | Dependency Injectionの略。必要なオブジェクトをクラス内で`new`せず、外から受け取る考え方 |
| IoC | Inversion of Controlの略。オブジェクト作成や呼出の一部を、アプリ側ではなくFramework側が管理する考え方 |
| コンストラクタ注入 | 必要な依存関係をコンストラクタの引数として受け取るDIの方法 |
| Component | SpringがBeanとして管理できる部品の総称 |
| Component Scan | 指定されたパッケージ以下から、Spring管理対象のクラスを探す処理 |
| `@SpringBootApplication` | 起動用設定、自動設定、Component Scanをまとめて有効にするアノテーション |
| `@Controller` | HTML画面用のWebリクエストを受けるクラスを示すアノテーション |
| `@RestController` | 主にJSONを返すWeb API用クラスを示すアノテーション |
| `@Service` | 業務処理を担当するクラスを示すアノテーション |
| `@Configuration` | Springの設定やBean定義を持つクラスを示すアノテーション |
| classpath | Javaがクラスや設定ファイルを探す場所の集まり |
| package | Javaクラスを分類し、同名クラスを区別するための名前空間 |
| interface | 利用できるメソッドの形を定め、具体的な処理方法と利用側を分けるJavaの仕組み |
| enum | 決められた候補だけを値として扱うJavaの型。勤怠状態に利用する |
| record | 主に変更しないデータのまとまりを簡潔に定義するJavaの仕組み。APIのDTOに利用する |
| `Optional<T>` | 値が存在する場合と存在しない場合を明示するJavaの型 |

## 依存関係と自動設定

| 用語 | 説明 |
| --- | --- |
| Maven | Javaの依存ライブラリ取得、コンパイル、JAR作成などを行うツール |
| `pom.xml` | Mavenプロジェクトの名前、Javaバージョン、依存関係、Pluginなどを定義するファイル |
| 依存ライブラリ | 自分のプログラムが利用する外部のクラス群 |
| Starter | 目的ごとに必要なSpring関連ライブラリをまとめて導入する入口 |
| 自動設定 | classpath、設定値、自作Beanを調べ、Spring Bootが一般的な初期構成を準備する仕組み |
| 組み込みWebサーバー | JARの中から起動できるWebサーバー。完成版ではTomcatを使う |
| Tomcat | HTTPリクエストを待ち受け、Spring MVCへ渡すWebサーバー |
| JAR | Javaのクラスやリソースをまとめたファイル。Spring Bootでは依存ライブラリと起動情報も含む実行可能JARを作れる |
| Plugin | Mavenなどのツールへ処理を追加する仕組み |
| BOM | 発展。関連ライブラリの互換性が取れたバージョン組をまとめて管理する仕組み |

## WebとHTTP

| 用語 | 説明 |
| --- | --- |
| Webアプリケーション | HTTPリクエストを受け、HTMLやJSONなどのレスポンスを返すアプリケーション |
| クライアント | サーバーへ要求を送る側。ブラウザや`curl`など |
| サーバー | クライアントからの要求を待ち、処理結果を返す側 |
| HTTP | Webのクライアントとサーバーがやり取りするための決まり |
| HTTPリクエスト | クライアントからサーバーへ送る要求 |
| HTTPレスポンス | サーバーからクライアントへ返す結果 |
| URL | 接続方法、host、port、pathなどを組み合わせたWeb上の宛先 |
| host | 接続先のPCやサーバーを示す名前 |
| `localhost` | 現在操作しているPC自身を表すhost名 |
| port | 一台のPC内で接続先プログラムを区別する番号 |
| path | Webアプリ内の機能を区別するURLの部分。例: `/users` |
| query parameter | URLの`?`以降へ付ける追加条件。例: `?role=ADMIN` |
| endpoint | HTTPメソッドとpathで特定される、Webアプリの入口 |
| HTTPメソッド | `GET`、`POST`など、リクエストの目的を表す値 |
| HTTPステータス | 200、404など、レスポンスの結果を表す3桁の番号 |
| header | リクエストやレスポンスへ付ける補助情報 |
| body | リクエストやレスポンスで送る本文 |
| Content-Type | bodyがHTML、JSONなど、どの形式かを示すheader |
| HTML | ブラウザへ表示する文書の構造を表す言語 |
| HTML form | ブラウザから入力値や操作を送信するHTML要素 |
| JSON | 名前と値の組み合わせでデータを表すテキスト形式 |
| redirect | サーバーがクライアントへ別URLへの移動を指示すること |
| PRG | Post/Redirect/Getの略。更新POSTの後にredirectし、GETで画面を表示する流れ |
| Cookie | ブラウザが保存し、後のリクエストでも送る小さなデータ |
| session | 複数のHTTPリクエストを同じ利用者の一連の操作として扱うサーバー側の仕組み |
| `Principal` | Spring Securityが保持する、認証済み利用者を表す情報 |
| APIクライアント | HTML画面ではなく、主にJSONなどでAPIを利用するプログラム |
| `curl` | コマンドラインからHTTPリクエストを送るツール |

## MVCと画面

| 用語 | 説明 |
| --- | --- |
| MVC | Model、View、Controllerへ役割を分ける考え方 |
| Controller | HTTPリクエストを受け、Serviceを呼び、画面名やJSONを返す入口 |
| `Model` | Spring MVCで、ControllerからViewへ渡す名前と値の入れ物。DBのEntityとは別物 |
| View | 利用者へ見せる画面。この教材ではThymeleaf Templateから作るHTML |
| Template | データを埋め込んで最終的な文書を作るひな形 |
| Thymeleaf | Spring MVCでHTML TemplateへModelの値を埋め込む仕組み |
| `@GetMapping` | 指定したpathへのGETを受けるメソッドを示す |
| `@PostMapping` | 指定したpathへのPOSTを受けるメソッドを示す |
| `@RequestMapping` | Controller全体やメソッドのpathなどを設定する |
| Flash Attribute | redirect先の次の一回だけ使うメッセージなどを保存する仕組み |

## レイヤーとデータ受渡し

| 用語 | 説明 |
| --- | --- |
| レイヤー | 責務ごとにクラスを分けた層 |
| Service | 出勤条件や重複禁止など、業務ルールを担当する層 |
| Repository | Entityの検索、保存、削除など、DB操作の入口を担当する層 |
| domain | 業務で扱うデータや状態を表す領域。この教材ではEntityやEnumを置く |
| Entity | DBテーブルの行と対応する、保存対象のJavaオブジェクト |
| DTO | Data Transfer Objectの略。外部との入出力に必要な項目だけを持つデータ |
| Form object | HTMLフォームの入力を受け取るJavaオブジェクト |
| Request DTO | APIリクエストのJSONを受け取るDTO |
| Response DTO | APIレスポンスとして公開するDTO |
| 業務ルール | 「一日一回だけ出勤できる」など、アプリが守るべき決まり |
| 業務例外 | 入力形式は正しいが、現在の業務状態では操作できないことを表す例外 |
| シリアライズ | JavaオブジェクトをJSONなどの形式へ変換すること |
| デシリアライズ | JSONなどをJavaオブジェクトへ変換すること |
| Jackson | Spring BootでJSONとJavaオブジェクトの相互変換に使われるライブラリ |

## データベースとSQL

| 用語 | 説明 |
| --- | --- |
| DB | Databaseの略。データを保存し、検索や更新を行う仕組み |
| RDB | Relational Databaseの略。関連するデータを表の形で管理するDB |
| テーブル | 同じ種類のデータを行と列で保存する表 |
| 行 | 一件のデータ。レコードとも呼ぶ |
| 列 | データの項目。カラムとも呼ぶ |
| データ型 | 数値、文字列、日付など、列へ保存できる値の種類 |
| スキーマ | テーブル、列、型、制約など、DB構造の定義 |
| SQL | RDBへ取得や更新を依頼する言語 |
| `SELECT` | DBから行を取得するSQL |
| `INSERT` | DBへ行を追加するSQL |
| `UPDATE` | DBの行を更新するSQL |
| `DELETE` | DBの行を削除するSQL |
| 主キー | 一行を一意に識別する列。PKと略す |
| 外部キー | 別テーブルの行を参照する列。FKと略す |
| 一意制約 | 指定した列や列の組み合わせの重複を禁止する制約 |
| NOT NULL | 値がない状態を禁止する制約 |
| 一対多 | 一件のデータに、別テーブルの複数行が対応する関係 |
| index | 発展。検索を速くするためにDBが持つ構造 |
| トランザクション | 複数のDB操作を一つのまとまりとして扱う仕組み |
| commit | トランザクション内の変更を確定すること |
| rollback | トランザクション内の変更を取り消すこと |
| JDBC | JavaからRDBへ接続してSQLを実行するための標準API |
| JDBC Driver | JDBCの呼出を、H2やMariaDB固有の通信へ変換するライブラリ |

## JPAとDB変更管理

| 用語 | 説明 |
| --- | --- |
| JPA | JavaオブジェクトとRDBテーブルを対応付けるための仕様 |
| Hibernate | 完成版で使うJPAの実装。Entityの情報からSQLを実行する |
| Spring Data JPA | Repositoryインターフェースから一般的なDB操作を提供するSpringの仕組み |
| `JpaRepository` | 保存、全件取得、id検索、削除などを提供するRepositoryの基礎インターフェース |
| 派生クエリ | `findByUsername`のようなメソッド名からSpring Data JPAが検索条件を作る仕組み |
| `@Entity` | DBへ保存する対象のクラスを示すアノテーション |
| `@Id` | Entityの主キーに対応するフィールドを示す |
| `@ManyToOne` | 多数のEntityから一つのEntityを参照する関連を示す |
| `@Transactional` | メソッドをトランザクションの範囲として扱うアノテーション |
| Migration | DB構造をある版から次の版へ変更すること |
| Flyway | バージョン付きSQLを順番に適用し、DB構造の変更履歴を管理する仕組み |
| H2 | ファイルまたはメモリで手軽に動かせるRDB。完成版では開発用に使う |
| MariaDB | 完成版の本番相当環境で使うRDB |
| `ddl-auto: validate` | EntityとDBの基本構造に不一致がないか起動時に確認する設定 |
| `open-in-view` | 発展。Webリクエスト中にJPAの取得範囲をどこまで保つかに関係する設定 |
| 遅延ロード | 発展。関連データが実際に必要になった時点で追加取得する仕組み |
| N+1問題 | 発展。一覧一回に加え、各行の関連取得で多数のSQLが発生する問題 |
| `@EntityGraph` | 発展。取得時に一緒に読み込む関連を指定する方法 |

## 入力検証とエラー

| 用語 | 説明 |
| --- | --- |
| Validation | 入力値が必須、文字数、形式などの条件を満たすか確認すること |
| Bean Validation | アノテーションでJavaオブジェクトの入力条件を表す仕組み |
| `@Valid` | 対象オブジェクトのValidationを実行するようSpringへ伝える |
| `@NotBlank` | null、空文字、空白だけの文字列を拒否する制約 |
| `@Size` | 文字数や要素数の範囲を指定する制約 |
| `BindingResult` | 画面入力のValidation結果を受け取るSpring MVCの型 |
| 400 | リクエストの形式や入力が不正 |
| 404 | 対応するURLやデータが存在しない |
| 409 | 入力形式は正しいが、現在状態や業務ルールと競合 |
| 500 | サーバー内で想定外の問題が起きた |
| 例外Handler | 発生した例外を画面表示やHTTPステータス、JSONへ変換する処理 |

## 認証と認可

| 用語 | 説明 |
| --- | --- |
| Spring Security | ログイン、認証、認可などを扱うSpringの仕組み |
| Security Filter Chain | Controllerより前で、複数のセキュリティ処理を順番に行う仕組み |
| 認証 | 利用者が誰か確認すること |
| 認可 | 認証済み利用者が、その操作をしてよいか確認すること |
| role | 利用者の役割。完成版では`ROLE_USER`と`ROLE_ADMIN` |
| Form Login | HTMLフォームでユーザー名とパスワードを送り、画面用sessionを作る認証方式 |
| HTTP Basic | リクエストheaderでユーザー名とパスワードを送るHTTPの認証方式 |
| BCrypt | パスワードを元へ戻しにくいハッシュ値へ変換する方式 |
| CSRF | 利用者のブラウザを悪用し、意図しない更新リクエストを送らせる攻撃 |
| CSRF Token | 正しい画面から送られた更新リクエストか確認するための値 |
| CORS | 発展。あるWebサイトのJavaScriptから別の接続先へアクセスできる範囲を制御する仕組み |
| 401 | 認証情報がない、または認証に失敗した |
| 403 | 認証済みだが、その操作を行う権限がない |

## REST API

| 用語 | 説明 |
| --- | --- |
| API | プログラムから機能やデータを利用するための入口 |
| REST API | HTTPメソッドとURLを使い、リソースの取得や更新を表すAPI設計方式 |
| resource | APIが扱う対象。完成版ではユーザーや勤怠 |
| path variable | `/users/3`の`3`のように、path内で対象を指定する値 |
| 200 | 処理成功 |
| 201 | 新しいresourceの作成成功 |
| 204 | 処理成功で、返すbodyがない |
| 405 | そのendpointではHTTPメソッドが許可されていない |
| 415 | 送信されたContent-Typeを扱えない |

## 設定と実行環境

| 用語 | 説明 |
| --- | --- |
| YAML | インデントで階層を表す設定ファイル形式 |
| `application.yml` | Spring Bootの共通設定を置くファイル |
| profile | 同じアプリで環境別の設定を切り替える仕組み |
| dev | 開発用profile。完成版ではH2と学習用初期ユーザーを使う |
| prod | 本番相当profile。完成版ではMariaDBと外部設定を使う |
| 環境変数 | アプリの外側から名前と値を渡すOSの仕組み |
| 外部設定 | JARを書き換えず、設定ファイルや環境変数から動作を変える考え方 |
| `DataSeeder` | 初期ユーザーなど、必要な初期データを投入する完成版のクラス |
| `CommandLineRunner` | Spring Boot起動時に一度実行する処理を定義する仕組み |

## Docker

| 用語 | 説明 |
| --- | --- |
| Docker | アプリと必要な実行環境を、隔離された単位で動かす仕組み |
| image | コンテナを作るための変更されないひな形 |
| container | imageから作られ、実際に動作しているプロセスとファイル環境 |
| Dockerfile | imageの作り方を記述するファイル |
| Docker Compose | 複数のcontainerと接続関係を一つのYAMLで管理する仕組み |
| service | Composeで管理するアプリやDBなどの単位 |
| volume | containerを作り直しても残すデータ領域 |
| port mapping | ホストPCのportをcontainerのportへ接続する設定 |
| healthcheck | container内のサービスが利用可能か定期的に確認する仕組み |
| build context | Docker image作成時にDockerへ渡すファイルの範囲 |
| `.dockerignore` | build contextへ含めないファイルを指定する設定 |
| `.env` | Composeへ環境変数を渡すファイル。実パスワードを含むためGitへ登録しない |
| `.env.example` | 必要な環境変数名と例を共有するひな形。実パスワードは入れない |

## 次に読む資料

- WebとDBの基礎へ戻る: [JavaからWeb・データベースへ進むための基礎](./00-java-web-database-primer.md)
- Spring全体をつかむ: [Spring Boot概要](./01-spring-boot-overview.md)
- 完成版の処理を詳しく追う: [アーキテクチャとリクエスト処理](./02-architecture-and-request-flow.md)
- 理解を確認する: [理解チェックと解答](./checkpoints-and-answers.md)
