# Day1（7/9）Spring/Mavenハンズオン + 最小MVC画面表示

## Day1のゴール
- Mavenでビルドと依存関係管理を実行し、`pom.xml` を読める
- Spring BootのDIとMVCの最小構成を説明できる
- `Controller -> Template` の流れで画面を表示できる
- Day2以降の開発で詰まらないためのデバッグ手順を持つ

## 前提
- Day0（HTML/Java基礎）を実施済み
- Java 17 / Maven 3.9+ がインストール済み
- このリポジトリ（`order-management-springboot`）直下で作業する

## 進め方（所要: 180分）
| 時間 | セッション | 目的 | 成果物 |
|:---|:---|:---|:---|
| 0:00-0:15 | 0. 環境確認 | 開始前の失敗を潰す | バージョン確認ログ |
| 0:15-0:55 | 1. Mavenハンズオン | Mavenの責務とコマンドを理解 | ビルド成功、依存ツリー確認 |
| 0:55-1:35 | 2. Spring基礎ハンズオン | DIとMVCの最小構成理解 | `HelloController` の動作 |
| 1:35-2:05 | 3. テスト基礎ハンズオン | 単体テストの価値を体験 | JUnitテスト1本 |
| 2:05-2:50 | 4. 本編: 最小MVC画面表示 | Day1本題を完遂 | `/` 画面表示 |
| 2:50-3:00 | 5. ふりかえり | 学習を言語化 | Day1チェック完了 |

---

## 0. 環境確認（15分）
以下を実行し、すべて成功してから次へ進みます。

```bash
java -version
mvn -version
git --version
```

`mvn` が見つからない場合は、PATH設定を確認してから再実行してください。

---

## 1. Mavenハンズオン（40分）

### 1-1. Mavenは何をするツールか
- 依存関係（ライブラリ）を管理する
- ビルド（コンパイル、テスト、成果物作成）を自動化する
- プロジェクト構成を標準化する

### 1-2. 最低限使うコマンド
```bash
mvn clean
mvn test
mvn package
mvn dependency:tree
```

### 1-3. ハンズオン手順
1. リポジトリ直下で `mvn clean test` を実行する
2. `BUILD SUCCESS` を確認する
3. `mvn dependency:tree` を実行し、`spring-boot-starter-web` 配下の依存を1つ説明する

### 1-4. `pom.xml` 読み解きミニ演習
次の項目を自分の言葉で説明してください。
- `groupId` / `artifactId` / `version`
- `<dependencies>` と `<build><plugins>` の違い
- なぜSpringプロジェクトでMaven（またはGradle）がほぼ必須なのか

### 現場コラム: なぜMaven理解が先なのか
実務では「コードが正しいか」より先に「全員が同じ手順で再現できるか」が問われます。Mavenを理解すると、環境差分による不具合を減らせます。

---

## 2. Spring基礎ハンズオン（40分）

### 2-1. Spring Bootの役割
- Spring Frameworkの設定を自動化し、すぐ動く状態を作る
- `@Controller` や `@Service` などの部品を組み合わせてアプリを作る

### 2-2. DI（依存性注入）を体験する
以下の最小コードで、`new` を使わずに連携できることを確認します。

```java
@Service
public class GreetingService {
    public String message() {
        return "Hello, Spring";
    }
}
```

```java
@Controller
public class HelloController {
    private final GreetingService greetingService;

    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return greetingService.message();
    }
}
```

### 2-3. 確認
- `mvn spring-boot:run` で起動
- `http://localhost:8080/hello` にアクセス
- 文字列が表示されることを確認

### 現場コラム: なぜDIが必要か
DIにより、実装の差し替えとテストがしやすくなります。機能追加や保守で効いてくるため、早期に慣れる価値があります。

---

## 3. テスト基礎ハンズオン（30分）

### 3-1. 目的
- 変更で既存機能を壊さないための安全網を作る

### 3-2. 演習
`GreetingService` のテストを1本作成。

```java
class GreetingServiceTest {

    @Test
    void message_returnsExpectedText() {
        GreetingService service = new GreetingService();
        assertEquals("Hello, Spring", service.message());
    }
}
```

### 3-3. 実行
```bash
mvn test
```

### 現場コラム: カバレッジの見方
数値100%が目的ではありません。重要なのは、壊れると困る分岐がテストされていることです。

---

## 4. 本編ハンズオン: 最小MVC画面表示（45分）

### 4-1. 到達目標
- `/` にアクセスして `templates/index.html` が表示される
- Controllerで設定した値がテンプレートに反映される

### 4-2. 実装ステップ
1. `AttendanceManagementApplication` を起動できる状態にする
2. `HomeController` を作成し、`@GetMapping("/")` を実装する
3. `templates/index.html` を作成し、`th:text` でModel値を表示する
4. `static/styles.css` を配置し、最低限の見た目を整える

### 4-3. 完了条件
- `http://localhost:8080/` で画面表示
- タイトル、日付、ステータスが表示される
- 起動ログにエラーがない

---

## 5. Day1チェックリスト（提出前確認）
- `mvn clean test package` を自力で実行できた
- `pom.xml` の `dependencies` と `plugins` を説明できる
- DIを「newしない設計」として説明できる
- `Controller -> Template` のデータ受け渡しを説明できる
- テストを1本追加し、`mvn test` 成功を確認した

---

## よくある詰まりポイント
- `mvn` が見つからない
  - PATH設定を確認し、ターミナル再起動
- `The goal you specified requires a project to execute but there is no POM in this directory`
  - 実行場所がリポジトリ直下か確認
- 画面が表示されない
  - `return "index";` と `templates/index.html` の対応を確認
  - 起動ログのエラー全文を読む

---

## Day1終了時の提出物
- 実行ログ（`mvn clean test package` 成功）
- `/hello` または `/` 画面のスクリーンショット
- 学びメモ（3行）
  - Mavenの理解
  - Springの理解
  - 明日試したいこと
