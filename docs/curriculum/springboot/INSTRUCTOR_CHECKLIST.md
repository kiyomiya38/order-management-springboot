# Spring Boot研修 講師事前チェック

研修開始前に、講師または教材管理者が実施します。

## 教材整合性の自動確認

PowerShellでリポジトリルートから実行します。

```powershell
powershell -ExecutionPolicy Bypass -File scripts/verify-springboot-curriculum.ps1
```

確認対象:
- ルート完成例のビルドと実行可能Spring Boot Jar
- Maven Sandbox
- Docker Compose構文
- Spring Boot教材内のローカルリンク
- Markdownコードフェンス
- Java短縮版とSpring Boot側の開始条件が一致していること
- Lesson5Aの初期ユーザー設定が`DataSeeder`作成より前にあること
- Lesson6の直前学習、`record` / Stream / `curl`解説が存在すること
- Lesson7の`application.yml`が、プロファイルなどの既存設定を失わない全コードであること

バックエンド短縮コースでは、次も確認します。

- `prerequisites/http-thymeleaf-minimum.md` と `lesson02/sql-rdb-basics.md` のローカルリンク
- Lesson1〜5で提供するHTML/CSS/JavaScriptが教材本文のコードと一致している
- 提供コード内の説明コメントが削除されていない
- 受講者が作成する `templates` / `static` の配置先が明記されている
- フロントエンド実装を評価対象にせず、HTTP、Thymeleaf、Controllerとの対応を評価できる

## Lesson別リハーサル

| Lesson | 講師確認 |
| --- | --- |
| 0 | Java 4ファイルをコンパイルし、正常・異常入力を確認 |
| 1 | `/` のHTML/CSS表示と実行可能Jarを確認 |
| 2 | 出勤成功とcurlによる二重出勤失敗を確認し、画面・Service・DBの対応を説明 |
| 3 | 未出勤退勤、正常退勤、再退勤、INFO/ERROR/DEBUGの出力差を確認 |
| 4 | 一覧とH2コンソールの値を照合 |
| 5 | 共通準備、依存追加、5A〜5Cの実施順を確認 |
| 5A | `application.yml`の`app.seed.enabled=true`を確認してから起動し、`admin` / `user1`の作成、ログイン、URL認可、パスワードハッシュを確認 |
| 5B | ユーザー管理、管理者勤怠、一般403、ControllerからRepositoryまでのコード追跡を確認 |
| 5C | dev/prodプロファイル、コード読解、削除禁止、参照整合性を確認 |
| 6 | 直前にJava-19 / 20Aの指定範囲を実施し、`record` / Stream / `curl`の説明後に、APIのJSON 401/403、本人出勤、正常系・409例外系のコード追跡を確認 |
| 7 | `application.yml`のプロファイル、初期ユーザー、画面、ログ設定が残っていることを確認し、checksum不一致とMariaDB互換DDLを確認 |
| 環境演習A | VMの固定IP、Nginx、localhost待受、MariaDB接続を確認 |
| 環境演習B | `.env`を用意し、Compose起動とVolume永続化を確認 |

バックエンド短縮コースの追加リハーサル:

| 範囲 | 講師確認 |
| --- | --- |
| HTTP最小理解 | GET/POST、フォーム送信先、`templates` / `static` を説明できる進行になっている |
| Lesson1〜5画面 | 提供コードを指定位置へ配置するだけで起動し、既存コメントも保持される |
| SQL・RDB | 主キー、外部キー、一意制約、CRUD、JOINを勤怠テーブルで説明できる |
| Lesson2 Java補足 | ラムダ式、メソッド参照、Repositoryインターフェース、独自unchecked例外を本文だけで追跡できる |
| Lesson5初期ユーザー | Lesson5Aの記載順どおりに進め、Lesson5Cの設定を先取りせずログインできる |
| Lesson6 API | `fetch` を使わず、本文の`curl`オプション説明だけで正常系・401・403・400・409を確認できる |

## 合格判定

- 自動確認がすべて成功する
- 選択する環境演習（AまたはB）を研修用PCで一度完走する
- 講義で使用するMarkdownと完成版の動作に矛盾がないか確認する
- 研修開始時のSpring Boot 3.x最新パッチとCVE情報を確認する

受講者へ配布する前に、実施日・実施者・結果を研修運用記録へ残します。
