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
- Spring Boot向け追加必修のJava-04A、Java-17A、Java-20A、Java-20B、Java-21を完了している
- `docs/curriculum/html_css/html_css.md` を完了している
- `docs/curriculum/javascript/javascript.md` を完了している
- `docs/curriculum/javascript/javascript-fetch-json.md` を完了している
- `docs/curriculum/web-app(簡易版)/README.md` の必修範囲を完了している
- `docs/curriculum/web-app(簡易版)/bridge-to-springboot.md` を読了している
- Java 17 / Maven 3.9+ / Git Bash を使える

### バックエンド短縮コースの前提

- `docs/curriculum/java/java-handson` の本編を完了している
- `java-04a-type-conversion-and-cast.md` を完了している
- `java-17a-exception-types-and-throws.md` を完了している
- `java-20a-record-enum.md` を完了している
- `java-20b-web-api-prep.md` で `GET` / `POST` / HTTPステータス / `curl` を確認している
- `java-21-junit-basics.md` を完了している
- [HTTP / フォーム / Thymeleaf最小理解](./prerequisites/http-thymeleaf-minimum.md) を完了している
- Lesson1のMaven Sandboxで、Maven操作とコンストラクタ注入（DI）を確認している
- Java 17 / Maven 3.9+ / Git Bash を使える

短縮コースでは、`html_css`、`javascript`、`web-app(簡易版)` の実装演習を研修後の発展学習へ回します。
ただし、フォームの送信先、`Model` とThymeleafの対応、`templates` / `static` の配置規約は必修です。

### バージョン方針
- 教材はSpring Boot `3.5.15` に固定する
- 研修開始前に [Maven Centralの公式メタデータ](https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-dependencies/maven-metadata.xml) で3.x系列の更新を確認する
- メジャーバージョン（4.x）への更新は、教材コードと全テストを別途検証してから行う

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
| 5C | [lesson05/lesson5c-testing-operations.md](./lesson05/lesson5c-testing-operations.md) | 必修 | テスト、プロファイル、参照整合性を確認する |
| 6 | [lesson06/lesson6.md](./lesson06/lesson6.md) | 必修 | `@RestController` でJSON APIを実装する |
| 7 | [lesson07/lesson7.md](./lesson07/lesson7.md) | 必修 | FlywayでDBスキーマ変更を履歴管理する |
| A | [deployment/virtualbox/README.md](./deployment/virtualbox/README.md) | 環境演習（選択） | 実サーバー風の2VM構成へ移行する |
| B | [deployment/docker-compose/README.md](./deployment/docker-compose/README.md) | 環境演習（選択） | Docker ComposeでApp + DB構成を作る |

### バックエンド短縮コースの追加順序

短縮コースでは、上記Lesson順を維持しながら次を追加します。

1. Lesson0の前後で [HTTP / フォーム / Thymeleaf最小理解](./prerequisites/http-thymeleaf-minimum.md) を実施する
2. Lesson1でMaven SandboxのDI範囲を実施する
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
| lesson05（5A〜5C合計） | 10〜11時間（2日） |
| lesson05A | 2.5時間 |
| lesson05B | 5時間 |
| lesson05C | 3.5時間 |
| lesson06 | 3〜3.5時間 |
| lesson07 | 2.5〜3時間 |
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
- 各LessonのMarkdownを正本とし、同じフォルダのPDFは講師説明用スライドとして使用する
- PDFとMarkdownに差がある場合はMarkdownを優先する
- `lesson00/old` 配下はアーカイブであり、現行研修では使用しない
- バックエンド短縮コースでは、`lesson00/additional-topics` のフロントエンド項目は研修後の参考資料とする
- Lesson2前に [SQL・RDB基礎](./lesson02/sql-rdb-basics.md) を必修とする

講師用PDF:
- Lesson1: `lesson01/Spring_Boot_MVC_基礎.pdf`
- Lesson2: `lesson02/Attendance_DB_Integration_and_Layers.pdf`
- Lesson3: `lesson03/Business_Rules_and_State_Management.pdf`
- Lesson4: `lesson04/JPA_Read_and_Database_Check.pdf`
- Lesson5: `lesson05/Application_Security_and_Testing.pdf`（5A〜5Cの導入・要点説明に使用し、実装手順は各Markdownを正本とする）

Lesson6、Lesson7、環境演習A/Bには講師用PDFを用意していません。これらはMarkdownの図とコードを投影し、講師のライブコーディングを併用して進めます。

## Lesson別の合格基準
写経完了ではなく、動作確認と説明で判定します。

| 教材 | 合格基準 |
| --- | --- |
| lesson00 | クラス分割、メソッド引数、`List`保存、業務ルール判定を説明できる |
| lesson01 | `Controller -> Template` の表示の流れと、Mavenで起動する理由を説明できる |
| lesson02 | `Controller -> Service -> Repository -> DB` の流れと、Mockitoで二重出勤を再現する方法を説明できる |
| lesson03 | 勤怠状態遷移、既存Serviceテストを再実行する理由、ログレベルによる出力差と機密情報をログへ出さない理由を説明できる |
| lesson04 | 画面表示データとDB保存データを対応づけて確認できる |
| lesson05 | 共通準備を完了し、5A〜5Cを同じ `stages/lesson05` で進める理由を説明できる |
| lesson05A | 認証・認可の違いとSecurity設定を説明できる |
| lesson05B | ユーザー作成・勤怠更新をControllerからRepositoryまでコードで追跡できる |
| lesson05C | 12テスト、プロファイル差分、参照整合性による削除禁止を説明できる |
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
