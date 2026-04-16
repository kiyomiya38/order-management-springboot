# Java初学者 -> Spring Boot 実務導入ロードマップ（既存教材対応）

## 0. このロードマップの目的
- Java初学者が、既存教材を使って Spring Boot まで到達する最短ルートを示す。
- 「何をいつ学ぶか」を週次で固定し、学習の迷いを減らす。
- 既存教材の不足分を明示し、追加教材の優先順位を決める。

---

## 1. 学習ステージ（全8週の目安）

### Week 1: Java基礎の土台
- 対象:
  - `docs/curriculum/java/handson/java-01-intro.md`
  - `docs/curriculum/java/handson/java-02-program-flow.md`
  - `docs/curriculum/java/handson/java-03-variables-and-types.md`
  - `docs/curriculum/java/handson/java-04-expressions-and-operators.md`
  - `docs/curriculum/java/handson/java-05-class-libraries.md`
  - `docs/curriculum/java/handson/java-06-conditions-and-loops.md`
  - `docs/curriculum/java/handson/java-07-arrays.md`
  - `docs/curriculum/java/handson/java-08-methods.md`
- 到達目標:
  - `main` から処理を書いて、コンパイル/実行を自力で回せる。
  - 条件分岐と繰り返しで業務ルールを表現できる。

### Week 2: オブジェクト指向と保守性
- 対象:
  - `docs/curriculum/java/handson/java-09-multi-class-development.md`
  - `docs/curriculum/java/handson/java-10-instances-and-classes.md`
  - `docs/curriculum/java/handson/java-11-class-mechanisms.md`
  - `docs/curriculum/java/handson/java-12-encapsulation.md`
  - `docs/curriculum/java/handson/java-13-inheritance.md`
  - `docs/curriculum/java/handson/java-14-advanced-inheritance.md`
  - `docs/curriculum/java/handson/java-15-polymorphism.md`
  - `docs/curriculum/java/handson/java-16-standard-classes.md`
  - `docs/curriculum/java/handson/java-17-exceptions.md`
- 到達目標:
  - クラス分割、責務分離、例外設計の基礎を説明できる。

### Week 3: 実務で多用するAPI読解
- 対象:
  - `docs/curriculum/java/handson/java-18-collections.md`
  - `docs/curriculum/java/handson/java-19-stream-api.md`
  - `docs/curriculum/java/handson/java-20-javadoc-reading.md`
- 到達目標:
  - Javadocを読み、仕様を根拠に実装・検証できる。

### Week 4: Webの基本（フレームワークなし）前半
- 対象:
  - `docs/curriculum/web-app(簡易版)/lesson1.md`
  - `docs/curriculum/web-app(簡易版)/lesson2.md`
- 到達目標:
  - HTTP/JSON/ルーティング/CRUDの最小実装を説明できる。

### Week 5: Webの基本（フレームワークなし）後半
- 対象:
  - `docs/curriculum/web-app(簡易版)/lesson3.md`
  - `docs/curriculum/web-app(簡易版)/lesson4.md`
  - `docs/curriculum/web-app(簡易版)/lesson5.md`
- 到達目標:
  - バリデーション、状態遷移、排他制御の必要性を説明できる。

### Week 6: Spring Boot導入
- 対象:
  - `docs/curriculum/springboot/lesson00/lesson0.md`
  - `docs/curriculum/springboot/lesson01/lesson1.md`
  - `docs/curriculum/springboot/lesson02/lesson2.md`
- 到達目標:
  - Maven、MVC、DI、Entity/Repository/Service の基本構造を理解する。

### Week 7: Spring Boot実装強化
- 対象:
  - `docs/curriculum/springboot/lesson03/lesson3.md`
  - `docs/curriculum/springboot/lesson04/lesson4.md`
  - `docs/curriculum/springboot/lesson05/lesson5.md`
- 到達目標:
  - 業務ルール実装、認証/認可、最低限のテストを実装できる。

### Week 8: 実運用寄り（デプロイ/運用）
- 対象:
  - `docs/curriculum/springboot/lesson06/lesson6.md`
  - `docs/curriculum/springboot/lesson07/lesson7.md`
- 到達目標:
  - 実サーバー配置とコンテナ化の差分を説明できる。

---

## 2. この順序にした理由
1. Java文法とOOPを先に固めないと、Spring Bootの設計意図が理解しづらい。
2. `web-app(簡易版)` でHTTPの素振りを先に入れると、Spring MVCの抽象化価値が見える。
3. 後半で Spring Boot -> 認証/DB -> デプロイへ進むと、実務の開発順に近い。

---

## 3. 既存教材の不足分（優先度つき）

### 高優先
1. Spring Bootでの REST API（`@RestController`）本編演習が不足
- 現状:
  - Spring Boot本編はサーバーサイド描画中心（`@Controller` + Thymeleaf）。
  - 例: `docs/curriculum/springboot/lesson02/lesson2.md` の冒頭に「JSONは未使用」記載あり。
- 影響:
  - 実務で多い「フロント分離 + REST API」への接続が弱い。
- 追加提案:
  - `docs/curriculum/springboot/lesson08/lesson8.md`（REST CRUD + バリデーション + 例外応答）
  - 追加済み: `docs/curriculum/springboot/lesson08/lesson8.md`

2. DBスキーマ変更管理（Flyway/Liquibase）の実装演習が不足
- 現状:
  - `docs/curriculum/springboot/lesson06/lesson6.md` に「実運用との差分」として言及のみ。
- 影響:
  - DB変更手順が属人化しやすい。
- 追加提案:
  - `docs/curriculum/springboot/lesson09/lesson9.md`（Flyway導入、`V1__`, `V2__` の運用）
  - 追加済み: `docs/curriculum/springboot/lesson09/lesson9.md`

### 中優先
1. テストの段階学習（JUnit -> Spring Test -> MockMvc）が本線で不足
- 現状:
  - テストは `lesson05` で一部実施、Maven sandboxでも実施可能だが、本編で段階化されていない。
- 影響:
  - 「どこを何でテストするか」の設計力が育ちにくい。
- 追加提案:
  - `docs/curriculum/java/handson/java-21-junit-basics.md`
  - `docs/curriculum/springboot/lesson10/lesson10.md`（Service単体 + MockMvc）
  - 追加済み: `docs/curriculum/java/handson/java-21-junit-basics.md`

2. `@ControllerAdvice` によるエラー応答統一の本編演習が不足
- 現状:
  - エラーハンドリングは個別実装中心。
- 影響:
  - API/画面でエラー形式がばらつきやすい。
- 追加提案:
  - `docs/curriculum/springboot/lesson11/lesson11.md`

3. Maven基礎の前倒し導線が弱い
- 現状:
  - Maven詳説は `springboot/lesson01` で本格登場。
- 影響:
  - Java基礎フェーズで「依存管理・テスト実行」を体験しにくい。
- 追加提案:
  - `docs/curriculum/java/handson/java-22-maven-basics.md`

### 低優先
1. CI最小演習（`mvn test` の自動実行）が不足
- 現状:
  - CI/CDは一部資料で言及はあるが、ハンズオン化されていない。
- 追加提案:
  - `docs/curriculum/springboot/lesson12/lesson12.md`（GitHub Actions最小構成）

---

## 4. 追加教材の進捗と次着手（推奨）
完了済み:
1. Spring REST API演習（lesson08）
2. JUnit基礎演習（java-21）
3. Flyway演習（lesson09）

次に着手:
1. MockMvc演習（lesson10）
2. `@ControllerAdvice` 応答統一演習（lesson11）
3. CI最小演習（lesson12）

---

## 5. 運用ルール（受講生向け）
1. 各週で「必須1本 + 任意1本」までに制限する（詰め込み防止）。
2. 週末に「説明できるか」で合格判定する（実装だけで終わらせない）。
3. 次週へ進む条件は、前週の必須到達目標を口頭説明できること。
