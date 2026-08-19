# Java初学者 -> Spring Boot 実務導入ロードマップ（既存教材対応）

## 0. このロードマップの目的
- Java初学者が、既存教材を使って Spring Boot まで到達する最短ルートを示す。
- 「何をいつ学ぶか」を週次で固定し、学習の迷いを減らす。
- 既存教材の不足分を明示し、追加教材の優先順位を決める。

---

## 1. 学習ステージ（全9週の目安）

### Week 1: Java基礎の土台
- 対象:
  - `docs/curriculum/java/java-handson/java-01-intro.md`
  - `docs/curriculum/java/java-handson/java-02-program-flow.md`
  - `docs/curriculum/java/java-handson/java-03-variables-and-types.md`
  - `docs/curriculum/java/java-handson/java-04-expressions-and-operators.md`
  - `docs/curriculum/java/java-handson/java-04a-type-conversion-and-cast.md`
  - `docs/curriculum/java/java-handson/java-05-class-libraries.md`
  - `docs/curriculum/java/java-handson/java-06-conditions-and-loops.md`
  - `docs/curriculum/java/java-handson/java-07-arrays.md`
  - `docs/curriculum/java/java-handson/java-08-methods.md`
- 到達目標:
  - `main` から処理を書いて、コンパイル/実行を自力で回せる。
  - 条件分岐と繰り返しで業務ルールを表現できる。

### Week 2: オブジェクト指向と保守性
- 対象:
  - `docs/curriculum/java/java-handson/java-09-instances-and-classes.md`
  - `docs/curriculum/java/java-handson/java-10-multi-class-development.md`
  - `docs/curriculum/java/java-handson/java-11-class-mechanisms.md`
  - `docs/curriculum/java/java-handson/java-12-encapsulation.md`
  - `docs/curriculum/java/java-handson/java-13-inheritance.md`
  - `docs/curriculum/java/java-handson/java-14-advanced-inheritance.md`
  - `docs/curriculum/java/java-handson/java-15-polymorphism.md`
  - `docs/curriculum/java/java-handson/java-16-standard-classes.md`
  - `docs/curriculum/java/java-handson/java-17-exceptions.md`
  - `docs/curriculum/java/java-handson/java-17a-exception-types-and-throws.md`
- 到達目標:
  - クラス分割、責務分離、例外設計の基礎を説明できる。

### Week 3: 実務で多用するAPI読解
- 対象:
  - `docs/curriculum/java/java-handson/java-18-collections.md`
  - `docs/curriculum/java/java-handson/java-19-stream-api.md`
  - `docs/curriculum/java/java-handson/java-20-javadoc-reading.md`
  - `docs/curriculum/java/java-handson/java-20a-record-enum.md`
  - `docs/curriculum/java/java-handson/java-20b-web-api-prep.md`
  - `docs/curriculum/java/java-handson/java-21-junit-basics.md`
- 到達目標:
  - Javadocを読み、仕様を根拠に実装・検証できる。
  - `record` / `enum` / `HttpServer` / HTTPステータスを、Webアプリ前提として説明できる。
  - JUnitで正常系・異常系の最小テストを実行できる。

### Week 4: フロントエンド基礎
- 対象:
  - `docs/curriculum/html_css/html_css.md`
  - `docs/curriculum/javascript/javascript.md`
  - `docs/curriculum/javascript/javascript-fetch-json.md`
- 到達目標:
  - HTMLフォーム、CSS、DOM操作、イベント処理を実装できる。
  - `fetch` / `async` / `await` / JSON通信の最小形を説明できる。

### Week 5: Webの基本（フレームワークなし）前半
- 対象:
  - `docs/curriculum/web-app(簡易版)/README.md`
  - `docs/curriculum/web-app(簡易版)/lesson1.md`
  - `docs/curriculum/web-app(簡易版)/lesson2.md`
  - `docs/curriculum/web-app(簡易版)/lesson3.md`
- 到達目標:
  - HTTP/JSON/ルーティング/CRUDの最小実装を説明できる。

### Week 6: Webの基本（フレームワークなし）後半
- 対象:
  - `docs/curriculum/web-app(簡易版)/lesson4.md`
  - `docs/curriculum/web-app(簡易版)/lesson5.md`
  - `docs/curriculum/web-app(簡易版)/bridge-to-springboot.md`
- 任意:
  - `docs/curriculum/web-app(簡易版)/lesson6-optional-reservation.md`
- 到達目標:
  - バリデーション、状態遷移、排他制御の必要性を説明できる。
  - フレームワークなしの実装が、Spring Bootでは何に置き換わるか説明できる。

### Week 7: Spring Boot導入
- 対象:
  - `docs/curriculum/springboot/README.md`
  - `docs/curriculum/springboot/lesson00/lesson0.md`
  - `docs/curriculum/springboot/lesson01/lesson1.md`
  - `docs/curriculum/springboot/lesson02/lesson2.md`
- 到達目標:
  - Maven、MVC、DI、Entity/Repository/Service の基本構造を理解する。

### Week 8: Spring Boot実装強化
- 対象:
  - `docs/curriculum/springboot/lesson03/lesson3.md`
  - `docs/curriculum/springboot/lesson04/lesson4.md`
  - `docs/curriculum/springboot/lesson05/lesson5.md`
  - `docs/curriculum/springboot/lesson05/lesson5a-authentication.md`
  - `docs/curriculum/springboot/lesson05/lesson5b-management.md`
  - `docs/curriculum/springboot/lesson05/lesson5c-operations.md`
- 到達目標:
  - 業務ルール実装、認証/認可、プロファイル設定、参照整合性を説明できる。

### Week 9: REST APIと実運用寄り（デプロイ/運用）
- 対象:
  - `docs/curriculum/springboot/lesson06/lesson6.md`
  - `docs/curriculum/springboot/lesson07/lesson7.md`
  - `docs/curriculum/springboot/deployment/virtualbox/README.md`
  - `docs/curriculum/springboot/deployment/docker-compose/README.md`
- 到達目標:
  - Spring BootでJSON APIを実装し、`web-app(簡易版)` で学んだAPI通信と接続できる。
  - DBスキーマ変更をFlywayの履歴として管理できる。
  - 実サーバー配置とコンテナ化の差分を説明できる。

---

## 1.5 バックエンド短縮コース

研修期間が限られ、Spring Bootのサーバー側実装を優先する場合は、Week 4〜6のフロントエンド実装と `web-app(簡易版)` の実装演習を研修後へ回します。
既存の9週間コースは削除せず、通常コースとして継続利用します。

### 短縮コースの対象

1. Week 1〜3のJava教材を完了する
2. `java-20b-web-api-prep.md` で `GET` / `POST` / HTTPステータス / `curl` を確認する
3. `docs/curriculum/springboot/prerequisites/http-thymeleaf-minimum.md` を実施する
4. Spring Boot Lesson0を実施する
5. Lesson1とMaven SandboxのDI範囲を実施する
6. `docs/curriculum/springboot/lesson02/sql-rdb-basics.md` を実施する
7. Spring Boot Lesson2〜4、Lesson5共通準備、5A〜5C、Lesson6〜7へ順番に進む
8. 環境演習はA（VirtualBox）またはB（Docker Compose）の一方を選択する

### 短縮コースで講師が提供するもの

- Spring Lesson1〜5で使用するHTML
- Spring Lesson1〜5で使用するCSS
- Lesson5で使用するJavaScript

受講者は、指定されたディレクトリとファイルを作成し、提供コードを内容や説明コメントを削らず配置します。
配置後は、フォーム送信先、Thymeleaf式、Controllerの `Model`、HTTPステータスを確認します。

### 短縮コースで評価しないもの

- HTML/CSSのデザイン実装
- JavaScript文法
- DOM操作
- `fetch` を使ったブラウザ側API実装

これらは「未習得」として扱い、フルスタック開発を修了したとは判定しません。
一方、`curl` を使ったHTTP/API確認、Spring MVC、DB、Securityは通常コースと同じ基準で評価します。

---

## 2. この順序にした理由
1. Java文法とOOPを先に固めないと、Spring Bootの設計意図が理解しづらい。
2. `web-app(簡易版)` でHTTPの素振りを先に入れると、Spring MVCの抽象化価値が見える。
3. 後半で Spring Boot -> 認証/DB -> デプロイへ進むと、実務の開発順に近い。
4. バックエンド短縮コースでも、HTTPとThymeleafの最小理解を残すことで、画面操作とControllerの接続を追跡できる。
5. SQL・RDB基礎をLesson2前に追加することで、JPAアノテーションを暗記ではなくDB構造と対応づけて理解できる。

---

## 3. 既存教材の不足分（優先度つき）

### 高優先（対応済み）
1. Spring Bootでの REST API（`@RestController`）本編演習
- 対応:
  - `docs/curriculum/springboot/lesson06/lesson6.md`（REST CRUD + バリデーション + 例外応答）
- 補足:
  - Lesson1〜5は `@Controller + Thymeleaf`、Lesson6で `@RestController + JSON API` に戻る構成にする。

2. DBスキーマ変更管理（Flyway/Liquibase）の実装演習
- 対応:
  - `docs/curriculum/springboot/lesson07/lesson7.md`（Flyway導入、`V1__`, `V2__` の運用）
- 補足:
  - 選択式の環境演習A/Bへ接続する。

### 中優先
1. エラー応答統一
- API: `lesson06` の `@RestControllerAdvice` とSecurity用401/403ハンドラーで対応済み
- 今後の候補: 画面Controllerの共通例外画面とログ相関ID

2. Maven基礎の前倒し導線が弱い
- 現状:
  - Maven詳説は `springboot/lesson01` で本格登場。
- 影響:
  - Java基礎フェーズで「依存管理・ビルド実行」を体験しにくい。
- 追加提案:
  - `docs/curriculum/java/java-handson/java-22-maven-basics.md`

---

## 4. 追加教材の進捗と次着手（推奨）
完了済み:
1. Spring REST API演習（lesson06）
2. JUnit基礎演習（java-21）
3. Flyway演習（lesson07）

次に着手:
1. 画面Controller共通例外処理

---

## 5. 運用ルール（受講生向け）
1. 各週の対象をすべて写経するのではなく、必修教材と読解教材を分けて進める。
2. 週末に「説明できるか」で合格判定する（実装だけで終わらせない）。
3. 次週へ進む条件は、前週の必須到達目標を口頭説明できること。
4. 迷った場合は、各フォルダのREADMEにある推奨順を優先する。
5. 評価基準は `docs/curriculum/curriculum-assessment-guide.md` を使う。
