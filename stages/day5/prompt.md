あなたは「新人研修（コンテナ・CI/CD）」の教材作成を支援するAIです。
私は研修全体の教材は作成済みですが、7/9〜7/15の「アプリケーション開発（Java）」パートだけ未作成です。
このパートのカリキュラムを確定し、そのまま教材（README/演習手順/サンプルコード）まで落とし込みたいです。
以下の前提と要件に従って、成果物を作ってください。

# 1. 目的（最重要）
このアプリ開発パートの目的は「Java研修を深くやる」ことではなく、
後続の Docker / CI / CD / Kubernetes 研修で “同じアプリ” をコンテナ化・自動化・デプロイするための土台を作ること。
受講者が以下をできる状態にする：
- Spring Bootアプリを起動できる
- APIを叩いて動作確認できる（curl/Postman）
- 設定（application.yml、環境変数、profile）を触れる
- バリデーション/例外処理/ログの基本が分かる
- 既存コードを読んで挙動を追える
※ Java文法網羅や高度な設計パターンの講義は不要

# 2. 研修期間（このパート）
7/9(木)〜7/15(水)の5営業日相当（7/11-12は休み）
各日 9:00-18:00 想定
Day1: 7/9
Day2: 7/10
Day3: 7/13
Day4: 7/14
Day5: 7/15

# 3. 採用するアプリ仕様（固定）
題材：受注管理ミニシステム（REST API）
言語：Java 17
FW：Spring Boot
ビルド：Maven
DB：H2（インメモリ）※後で外部DBに差し替え可能な設計が望ましい
UI：不要（RESTのみ）
ログ：標準ロギング（logback想定でOK）
テスト：最低限でOK（任意）

データモデル（最小）：
Order
- id
- customerName
- productName
- quantity
- price
- status
- createdAt

# 4. Day別のカリキュラム確定案（草案）
Day1（7/9）：Java & Spring Boot最低限
- Java基礎（クラス/メソッド/List/Map）
- Mavenプロジェクト構成
- Spring Boot起動
- @RestControllerでHello API
成果物：GET /hello

Day2（7/10）：Spring Boot基本構造
- Controller/Service/Repository
- DTO
- 受注登録API（POST /orders）
成果物：POST /orders

Day3（7/13）：業務アプリらしさ
- バリデーション（@NotNullなど）
- 例外処理（@ControllerAdvice）
- ログ出力（INFO/ERROR）
成果物：バリデーションエラーと例外ハンドリングができる

Day4（7/14）：設定と環境差分
- application.yml
- 環境変数
- profile（dev/prod）
- DB設定の外出し（この時点はH2でも良い）
成果物：環境変数で挙動変更

Day5（7/15）：コードを読む & API実行
- 受注管理ミニシステムのコード解読
- API一覧確認
- curl/Postmanで動作確認
成果物：API一式が動作し、後続のDocker化に入れる状態

# 5. Codexへの依頼（成果物）
以下を「そのまま教材として使える」品質で作ってください。

(1) カリキュラム確定版
- 各日：到達目標 / 講義内容 / 演習 / チェックポイント / つまずきポイント
- 1日8時間想定の時間割目安（午前/午後）も付ける

(2) サンプルアプリの成果物（コード一式）
- Mavenプロジェクト構成
- Order CRUD（最低：create/list/get。update/deleteは余裕があれば）
- Validation, Exception handler, Logging
- application.yml + profile（application-dev.yml / application-prod.yml など）
- 環境変数で切り替える例（例：LOG_LEVEL、APP_NAME、DB設定など）

(3) 演習手順書（README）
- 受講者向け：セットアップ、起動、API実行例（curlコマンド）、確認方法
- 講師向け：進め方、よくある詰まり、補足説明

(4) 後続（Docker/CI/CD/K8s）につながる“仕込み”
- Docker化しやすいようにポート/設定/ログの設計を意識
- 「後でConfigMap/Secretに載せ替える」前提のキー設計
- 依存しすぎない最小構成

# 6. 出力形式
リポジトリ直下に置く想定で、以下のように “ファイル単位” で出力してください。
例：
/README.md
/docs/day1.md ... day5.md
/pom.xml
/src/main/java/...
/src/main/resources/application.yml
/src/main/resources/application-dev.yml
...

重要：省略せず、コピペでファイル作成できる形で全文を出力してください。

# 7. 注意
- 外部サービス（RDS等）は使わない。ローカルで完結。
- ただし後で外部DBに差し替えしやすい説明は入れる。
- 研修の主役はコンテナ・CI/CDなので、アプリ側は“最小で実務っぽく”。

以上の条件で作業を開始してください。
まず(1)カリキュラム確定版と、(2)サンプルアプリの全ファイルを出力してください。
