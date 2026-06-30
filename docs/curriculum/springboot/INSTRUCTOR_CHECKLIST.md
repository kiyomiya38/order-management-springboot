# Spring Boot研修 講師事前チェック

研修開始前に、講師または教材管理者が実施します。

## 自動確認

PowerShellでリポジトリルートから実行します。

```powershell
powershell -ExecutionPolicy Bypass -File scripts/verify-springboot-curriculum.ps1
```

確認対象:
- ルート完成例の全テスト
- 実行可能Spring Boot Jar
- Maven Sandbox
- Docker Compose構文
- Spring Boot教材内のローカルリンク
- Markdownコードフェンス

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
| 2 | 出勤成功、curlによる二重出勤失敗、最小Serviceテストの成功を確認 |
| 3 | 未出勤退勤、正常退勤、再退勤、Lesson2のServiceテスト再実行、INFO/ERROR/DEBUGの出力差を確認 |
| 4 | 一覧とH2コンソールの値を照合 |
| 5 | 共通準備、依存追加、5A〜5Cの実施順を確認 |
| 5A | ログイン、一般/管理者の認証、URL認可、パスワードハッシュを確認 |
| 5B | ユーザー管理、管理者勤怠、一般403、ControllerからRepositoryまでのコード追跡を確認 |
| 5C | 12テスト、dev/prodプロファイル、削除禁止、参照整合性を確認 |
| 6 | 17テスト、APIのJSON 401/403、本人出勤、正常系・409例外系のコード追跡を確認 |
| 7 | Migrationテスト、checksum不一致、MariaDB互換DDLを確認 |
| 環境演習A | VMの固定IP、Nginx、localhost待受、MariaDB接続を確認 |
| 環境演習B | `.env`を用意し、Compose起動とVolume永続化を確認 |

バックエンド短縮コースの追加リハーサル:

| 範囲 | 講師確認 |
| --- | --- |
| HTTP最小理解 | GET/POST、フォーム送信先、`templates` / `static` を説明できる進行になっている |
| Lesson1〜5画面 | 提供コードを指定位置へ配置するだけで起動し、既存コメントも保持される |
| SQL・RDB | 主キー、外部キー、一意制約、CRUD、JOINを勤怠テーブルで説明できる |
| Lesson6 API | `fetch` を使わず、`curl` だけで正常系・401・403・400・409を確認できる |

## 合格判定

- 自動確認がすべて成功する
- 選択する環境演習（AまたはB）を研修用PCで一度完走する
- 講師が使用するPDFとMarkdownの説明に矛盾がないか確認する
- 研修開始時のSpring Boot 3.x最新パッチとCVE情報を確認する

受講者へ配布する前に、実施日・実施者・結果を研修運用記録へ残します。
