# Spring Boot 学習ガイド

このフォルダは、Java / フロントエンド基礎 / `web-app(簡易版)` を終えた後に進む Spring Boot 本編です。
初学者は、まずサーバーサイドMVCを理解し、その後にREST API、DB変更管理、運用寄りの演習へ進みます。

## 前提
- `docs/curriculum/java/java-handson` の本編を完了している
- `docs/curriculum/html_css/html_css.md` を完了している
- `docs/curriculum/javascript/javascript.md` を完了している
- `docs/curriculum/javascript/javascript-fetch-json.md` を完了している
- `docs/curriculum/web-app(簡易版)/README.md` の必修範囲を完了している
- `docs/curriculum/web-app(簡易版)/bridge-to-springboot.md` を読了している
- Java 17 / Maven 3.9+ / Git Bash を使える

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
| 5 | [lesson05/lesson5.md](./lesson05/lesson5.md) | 必修 | Spring Security、管理者機能、Serviceテストを扱う |
| 6 | [lesson08/lesson8.md](./lesson08/lesson8.md) | 必修 | `@RestController` でJSON APIを実装する |
| 7 | [lesson09/lesson9.md](./lesson09/lesson9.md) | 必修 | FlywayでDBスキーマ変更を履歴管理する |
| 8 | [lesson06/lesson6.md](./lesson06/lesson6.md) | 環境演習 | 実サーバー風の2VM構成へ移行する |
| 9 | [lesson07/lesson7.md](./lesson07/lesson7.md) | 環境演習 | Docker ComposeでApp + DB構成を作る |

## 標準所要時間

新人がコード読解・実装・動作確認・説明レビューまで行う場合の目安です。

| 教材 | 目安 |
| --- | ---: |
| lesson00 | 1〜1.5時間 |
| lesson01 | 4時間 |
| lesson02 | 4〜5時間 |
| lesson03 | 3〜4時間 |
| lesson04 | 3〜4時間 |
| lesson05 | 10〜11時間（2日） |
| lesson08 | 3〜3.5時間 |
| lesson09 | 2.5〜3時間 |
| lesson06 | 4〜6時間（VM準備状況による） |
| lesson07 | 3〜4時間 |

Spring Boot範囲全体は、レビューと予備時間を含めて7〜9研修日を確保します。環境演習はLesson6/7の片方だけを選択しても構いません。

## なぜ Lesson1〜5 は Thymeleaf か
`web-app(簡易版)` では、主に `fetch + JSON API + DOM更新` で画面を動かしました。
Spring Boot Lesson1〜5では、まずSpring MVCの基本を理解するため、`Controller + Model + Thymeleaf` を中心にします。

この順序にすると、次を分けて理解できます。

| 観点 | Lesson1〜5 | Lesson8 |
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

## 補助教材の扱い

- Maven操作に不安がある受講者は、Lesson1の前に [Maven Sandbox](./lesson01/maven-sandbox/README.md) を実施する
- 各LessonのMarkdownを正本とし、同じフォルダのPDFは講師説明用スライドとして使用する
- PDFとMarkdownに差がある場合はMarkdownを優先する
- `lesson00/old` 配下はアーカイブであり、現行研修では使用しない

講師用PDF:
- Lesson1: `lesson01/Spring_Boot_MVC_基礎.pdf`
- Lesson2: `lesson02/Attendance_DB_Integration_and_Layers.pdf`
- Lesson3: `lesson03/Business_Rules_and_State_Management.pdf`
- Lesson4: `lesson04/JPA_Read_and_Database_Check.pdf`
- Lesson5: `lesson05/Application_Security_and_Testing.pdf`

## Lesson別の合格基準
写経完了ではなく、動作確認と説明で判定します。

| 教材 | 合格基準 |
| --- | --- |
| lesson00 | クラス分割、メソッド引数、`List`保存、業務ルール判定を説明できる |
| lesson01 | `Controller -> Template` の表示の流れと、Mavenで起動する理由を説明できる |
| lesson02 | `Controller -> Service -> Repository -> DB` の流れを説明できる |
| lesson03 | 勤怠状態遷移に加え、ログレベルによる出力差と機密情報をログへ出さない理由を説明できる |
| lesson04 | 画面表示データとDB保存データを対応づけて確認できる |
| lesson05 | 認証・認可の違いを説明し、画面操作をControllerからRepositoryまでコードで追跡できる |
| lesson08 | DTOとJSONエラー応答を説明し、APIの正常系・例外系をコードと実行結果で追跡できる |
| lesson09 | Flywayの `V1__` / `V2__` と `ddl-auto: validate` の役割を説明できる |
| lesson06 | app-vm / db-vm / Nginx / systemd / MariaDB の通信経路を説明できる |
| lesson07 | Dockerfile、Compose、Volume、環境変数によるDB接続を説明できる |

## 進め方
- Lesson1〜5は順番を飛ばさない
- Lesson8は、Lesson1〜5でMVCとService層を理解してから実施する
- Lesson6/7は実行環境の準備が重いため、教室環境に合わせて片方だけ実施してもよい
- 迷った場合は `docs/curriculum/java-to-springboot-roadmap.md` と `docs/curriculum/curriculum-assessment-guide.md` を確認する
- 講師は配布前に [INSTRUCTOR_CHECKLIST.md](./INSTRUCTOR_CHECKLIST.md) を実施する
