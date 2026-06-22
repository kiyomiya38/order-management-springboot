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

## Lesson別リハーサル

| Lesson | 講師確認 |
| --- | --- |
| 0 | Java 4ファイルをコンパイルし、正常・異常入力を確認 |
| 1 | `/` のHTML/CSS表示と実行可能Jarを確認 |
| 2 | 出勤成功とcurlによる二重出勤失敗を確認 |
| 3 | 未出勤退勤、正常退勤、再退勤、INFO/ERROR/DEBUGの出力差を確認 |
| 4 | 一覧とH2コンソールの値を照合 |
| 5 | 12テスト、一般403、管理者画面、削除禁止、ユーザー作成のコード追跡を確認 |
| 8 | 17テスト、APIのJSON 401/403、本人出勤、正常系・409例外系のコード追跡を確認 |
| 9 | Migrationテスト、checksum不一致、MariaDB互換DDLを確認 |
| 6 | VMの固定IP、Nginx、localhost待受、MariaDB接続を確認 |
| 7 | `.env`を用意し、Compose起動とVolume永続化を確認 |

## 合格判定

- 自動確認がすべて成功する
- 選択する環境演習（Lesson6または7）を研修用PCで一度完走する
- 講師が使用するPDFとMarkdownの説明に矛盾がないか確認する
- 研修開始時のSpring Boot 3.x最新パッチとCVE情報を確認する

受講者へ配布する前に、実施日・実施者・結果を研修運用記録へ残します。
