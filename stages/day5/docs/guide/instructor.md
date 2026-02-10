# 講師向けガイド

## 進め方のポイント
- Java文法の深追いは避け、Spring Bootの流れと設定変更を重視
- 後続のDocker/CI/CD/K8sで使い回すことを前提に、設定キーとログ出力を意識
- Day5は「コードを読む」ことが主目的なので、正解をすぐ見せずに追わせる

## よくある詰まり
- Java 17のPATH/JAVA_HOME設定
- 8080ポート競合
- `@Valid` の付け忘れ
- `application.yml` のインデント
- curlのJSON書式ミス

## 補足説明の例
- `application.yml` の ${VAR:default} は環境変数で上書きできる
- profile切替は `SPRING_PROFILES_ACTIVE` を使う
- H2は後でRDS等へ差し替え可能な設計にしている

## 進捗が早い受講者向け追加課題
- update/delete APIを追加
- Orderにstatus更新API追加
- エラーレスポンスにエラーコードを追加

## 評価観点・採点基準（例）

### 必須達成
- Spring Bootが起動し、`/hello`が応答する
- `POST /orders`で登録できる
- `GET /orders`と`GET /orders/{id}`が動く
- バリデーションエラーで400が返る
- NotFoundで404が返る
- 環境変数でAPP_NAMEが切り替えられる

### 加点項目
- エラーレスポンスの説明ができる
- API一覧を自分で整理できる
- 設定の差分（dev/prod）を理解している
- ログレベル変更の効果を説明できる

### 参考配点（20点満点）
- 起動/Hello: 3点
- 登録/一覧/取得: 6点
- バリデーション/例外: 5点
- 設定/環境変数: 4点
- ログ/理解度: 2点

## 採点チェックシート（例）

### 受講者情報
- 氏名：
- 日付：

### 基本機能（必須）
- [ ] Spring Boot起動（/hello応答）
- [ ] 受注登録（POST /orders）
- [ ] 受注一覧（GET /orders）
- [ ] 受注取得（GET /orders/{id}）

### エラー/品質
- [ ] バリデーションで400
- [ ] NotFoundで404
- [ ] ログ出力（INFO/ERROR）

### 設定
- [ ] APP_NAMEを環境変数で変更できる
- [ ] profile切替で挙動が変わる

### 講師コメント
- 
