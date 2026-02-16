# Maven Sandbox (Day1)

この教材は、`pom.xml` と `mvn` コマンドを「なぜ必要か」から理解し、最後にSpringの最小MVCまで動かすためのものです。
対象は **Java初学者** を想定しています。

## 0. この教材で到達する状態
- `pom.xml` の主要ブロックを自分の言葉で説明できる
- `mvn validate/test/package/dependency:tree/clean` の目的を説明できる
- `mvn spring-boot:run` で起動し、`Controller -> Service -> Template` の流れを説明できる

---

## 1. まず全体像（概念）

### 1-1. Mavenとは
Mavenは、Javaプロジェクトの作業を自動化するツールです。
主に次を担当します。

- 依存ライブラリの取得
- コンパイル
- テスト実行
- 成果物（jar）の作成

### 1-2. `pom.xml` とは
`pom.xml` は、Mavenが読む設定ファイルです。

- どのJavaバージョンでビルドするか
- どのライブラリを使うか
- どのプラグインでビルド/起動するか

を定義します。

### 1-3. 後続のアプリ開発とのつながり
このSandboxで理解しておくと、後続のアプリ開発で次が読みやすくなります。

- `dependencies`: どの機能を使っているか
- `plugins`: どうやってビルド・起動するか
- `properties`: バージョン管理をどう統一しているか

---

## 2. 具体例（`pom.xml` を読む）

以下は、このSandboxで使う `pom.xml` の例です。

作成ファイル: `~/order-management-springboot/docs/curriculum/day1/maven-sandbox/pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd"> <!-- MavenのPOM定義 -->
  <modelVersion>4.0.0</modelVersion> <!-- POM仕様バージョン（通常4.0.0） -->

  <groupId>com.shinesoft.training</groupId> <!-- 組織識別子（ドメイン逆順が一般的） -->
  <artifactId>day1-maven-sandbox</artifactId> <!-- 成果物名（jar名の元） -->
  <version>1.0.0-SNAPSHOT</version> <!-- 開発中バージョン -->
  <name>day1-maven-sandbox</name> <!-- プロジェクト表示名 -->
  <description>Day1 Maven learning sandbox project</description> <!-- 説明 -->

  <properties> <!-- 共通で使う値をまとめる -->
    <java.version>17</java.version> <!-- Javaバージョン -->
    <maven.compiler.release>${java.version}</maven.compiler.release> <!-- Java 17でコンパイル -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding> <!-- ソース文字コード -->
    <spring.boot.version>3.2.6</spring.boot.version> <!-- Spring Boot基準バージョン -->
    <junit.jupiter.version>5.10.2</junit.jupiter.version> <!-- JUnitバージョン -->
  </properties>

  <dependencyManagement> <!-- 依存バージョンを一括管理する領域 -->
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring.boot.version}</version>
        <type>pom</type> <!-- BOMとして読み込む -->
        <scope>import</scope> <!-- BOMをimportしてversion記述を減らす -->
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies> <!-- 実際に使うライブラリ -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId> <!-- Spring MVC + 組み込みTomcat -->
    </dependency>

    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId> <!-- 単体テスト用 -->
      <version>${junit.jupiter.version}</version>
      <scope>test</scope> <!-- テスト実行時のみ有効 -->
    </dependency>
  </dependencies>

  <build> <!-- ビルドに使うプラグイン設定 -->
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.13.0</version>
        <configuration>
          <release>${maven.compiler.release}</release> <!-- Java 17でコンパイル -->
        </configuration>
      </plugin>

      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.2.5</version> <!-- mvn testでJUnitを実行 -->
      </plugin>

      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>${spring.boot.version}</version> <!-- mvn spring-boot:run を使うために必要 -->
      </plugin>
    </plugins>
  </build>
</project>
```

### 2-1. 最低限ここだけ先に読む
- `<groupId> / <artifactId> / <version>`
- `<dependencies>`
- `<build><plugins>`

---

## 3. 手順（実際に作る）

### 3-1. 作業場所
```bash
cd ~/order-management-springboot/docs/curriculum/day1/maven-sandbox
pwd
ls
```

### 3-2. ディレクトリ作成
```bash
mkdir -p ~/order-management-springboot/docs/curriculum/day1/maven-sandbox/src/main/java/com/shinesoft/sandbox
mkdir -p ~/order-management-springboot/docs/curriculum/day1/maven-sandbox/src/test/java/com/shinesoft/sandbox
```

なぜ長い階層か:
- Maven標準構成を守るため
- Javaの`package`とフォルダ構成を一致させるため

### 3-3. Javaコード作成
作成ファイル: `~/order-management-springboot/docs/curriculum/day1/maven-sandbox/src/main/java/com/shinesoft/sandbox/GreetingCalculator.java`

```java
package com.shinesoft.sandbox; // package宣言（フォルダ構成と一致）

public class GreetingCalculator {
    public int add(int left, int right) { // 2つの整数を足し算する
        return left + right;
    }

    public String greeting(String name) { // 名前から挨拶文を作る
        if (name == null || name.isBlank()) { // 未入力はguest扱い
            return "Hello, guest";
        }
        return "Hello, " + name.trim(); // 前後空白を除去して返す
    }
}
```

### 3-4. テストコード作成
作成ファイル: `~/order-management-springboot/docs/curriculum/day1/maven-sandbox/src/test/java/com/shinesoft/sandbox/GreetingCalculatorTest.java`

```java
package com.shinesoft.sandbox; // テスト対象と同じpackage

import static org.junit.jupiter.api.Assertions.assertEquals; // 期待値比較

import org.junit.jupiter.api.Test; // テストメソッド定義

class GreetingCalculatorTest {
    private final GreetingCalculator calculator = new GreetingCalculator(); // テスト対象インスタンス

    @Test
    void add_returnsSum() {
        assertEquals(5, calculator.add(2, 3)); // 2 + 3 = 5 を確認
    }

    @Test
    void greeting_blankName_returnsGuest() {
        assertEquals("Hello, guest", calculator.greeting(" ")); // 空白入力の挙動を確認
    }
}
```

---

## 4. `mvn` コマンド実行（出力の見方と切り分け）

実行前に必ず確認:
```bash
cd ~/order-management-springboot/docs/curriculum/day1/maven-sandbox
pwd
ls
```
- `pom.xml` が見える状態で実行する

### 4-1. `mvn validate`
目的:
- `pom.xml` が正しく読めるか確認する

```bash
mvn validate
```

成功時の見方:
- `BUILD SUCCESS` が出る

失敗時の切り分け:
- `no POM in this directory` -> 実行場所が違う
- XMLパースエラー -> `pom.xml` のタグ不整合

### 4-2. `mvn test`
目的:
- テストを実行し、ロジックの正しさを確認する

```bash
mvn test
```

成功時の見方:
- `Tests run: ... Failures: 0, Errors: 0`
- `BUILD SUCCESS`

失敗時の切り分け:
- `Compilation failure` -> Java構文エラー
- `Failures: 1` 以上 -> テスト期待値と実装の不一致

### 4-3. `mvn package`
目的:
- 配布可能な`jar`を作る

```bash
mvn package
```

成功時の見方:
- `target/day1-maven-sandbox-1.0.0-SNAPSHOT.jar` が生成される

失敗時の切り分け:
- まず `mvn test` を通す

### 4-4. `mvn dependency:tree`
目的:
- 依存関係を可視化する

```bash
mvn dependency:tree
```

成功時の見方:
- `spring-boot-starter-web` 配下に `spring-webmvc` などが見える

失敗時の切り分け:
- ネットワークエラー -> 再実行
- 依存解決エラー -> `pom.xml` の記述見直し

### 4-5. `mvn clean`
目的:
- `target` を削除してビルド成果物を初期化する

```bash
mvn clean
```

成功時の見方:
- `BUILD SUCCESS`
- `target` が削除される

### 4-6. エラー修正後に `clean` は必須か
結論:
- 必須ではない

推奨手順:
1. 起動中のアプリを `Ctrl + C` で停止
2. コード修正
3. まず `mvn spring-boot:run` を再実行
4. 直らないときだけ `mvn clean` -> `mvn spring-boot:run`

---

## 5. Spring化ハンズオン（DI/MVC理解）

ここからは、同じSandboxを使ってSpring最小MVCを動かします。

### 5-1. `pom.xml` にThymeleaf依存を追加
`<dependencies>` に以下を追加します。

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

意味:
- `spring-boot-starter-web`: URLを処理する基盤（MVC）
- `spring-boot-starter-thymeleaf`: HTMLテンプレート表示

### 5-2. 起動クラスを作る
作成ファイル: `~/order-management-springboot/docs/curriculum/day1/maven-sandbox/src/main/java/com/shinesoft/sandbox/SandboxApplication.java`

```java
package com.shinesoft.sandbox; // アプリ起点のpackage

import org.springframework.boot.SpringApplication; // 起動ユーティリティ
import org.springframework.boot.autoconfigure.SpringBootApplication; // 起点アノテーション

@SpringBootApplication // 自動設定 + コンポーネントスキャン有効化
public class SandboxApplication {
    public static void main(String[] args) { // Java実行開始点
        SpringApplication.run(SandboxApplication.class, args); // Spring Boot起動
    }
}
```

### 5-3. Serviceを作る（DI対象）
```bash
mkdir -p ~/order-management-springboot/docs/curriculum/day1/maven-sandbox/src/main/java/com/shinesoft/sandbox/service
```

作成ファイル: `~/order-management-springboot/docs/curriculum/day1/maven-sandbox/src/main/java/com/shinesoft/sandbox/service/GreetingService.java`

```java
package com.shinesoft.sandbox.service; // サービス層

import org.springframework.stereotype.Service; // Service登録用アノテーション

@Service // Spring管理対象にする
public class GreetingService {
    public String createMessage(String name) { // 画面表示用メッセージを作る
        if (name == null || name.isBlank()) { // 未入力分岐
            return "Hello, guest";
        }
        return "Hello, " + name.trim(); // 正常系
    }
}
```

### 5-4. Controllerを作る（MVC）
```bash
mkdir -p ~/order-management-springboot/docs/curriculum/day1/maven-sandbox/src/main/java/com/shinesoft/sandbox/web
```

作成ファイル: `~/order-management-springboot/docs/curriculum/day1/maven-sandbox/src/main/java/com/shinesoft/sandbox/web/GreetingController.java`

```java
package com.shinesoft.sandbox.web; // Web層

import com.shinesoft.sandbox.service.GreetingService; // Service利用
import org.springframework.stereotype.Controller; // 画面返却Controller
import org.springframework.ui.Model; // 画面へ値を渡す
import org.springframework.web.bind.annotation.GetMapping; // GETルーティング
import org.springframework.web.bind.annotation.RequestParam; // クエリパラメータ受け取り

@Controller // Spring MVCのControllerとして登録
public class GreetingController {
    private final GreetingService greetingService; // DIされる依存

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService; // コンストラクタ注入（DI）
    }

    @GetMapping("/hello") // GET /hello を処理
    public String hello(@RequestParam(name = "name", required = false) String name, Model model) {
        model.addAttribute("message", greetingService.createMessage(name)); // テンプレートへ値を渡す
        return "hello"; // templates/hello.html を返す
    }
}
```

### 5-5. テンプレートを作る
```bash
mkdir -p ~/order-management-springboot/docs/curriculum/day1/maven-sandbox/src/main/resources/templates
```

作成ファイル: `~/order-management-springboot/docs/curriculum/day1/maven-sandbox/src/main/resources/templates/hello.html`

```html
<!DOCTYPE html> <!-- HTML5宣言 -->
<html lang="ja" xmlns:th="http://www.thymeleaf.org"> <!-- 日本語 + Thymeleaf有効化 -->
<head>
  <meta charset="UTF-8"> <!-- 文字化け防止 -->
  <title>Greeting Sandbox</title> <!-- タブ表示名 -->
</head>
<body>
  <h1 th:text="${message}">Hello, guest</h1> <!-- Controllerのmessageを表示 -->
</body>
</html>
```

### 5-6. 起動と確認
```bash
mvn dependency:tree
mvn test
mvn spring-boot:run
```

ブラウザ確認:
- `http://localhost:8080/hello`
- `http://localhost:8080/hello?name=Shinesoft`

停止:
- ターミナルで `Ctrl + C`

---

## 6. よくあるエラー（原因と対処）

### 6-1. `no POM in this directory`
原因:
- 実行ディレクトリが違う

対処:
```bash
cd ~/order-management-springboot/docs/curriculum/day1/maven-sandbox
ls
```

### 6-2. `/hello` で500、`-parameters`関連エラー
症状例:
- `Name for argument of type [java.lang.String] not specified...`

原因:
- `@RequestParam` の引数名を省略し、パラメータ名を解決できない

対処:
- 次のように `name` を明示する

```java
public String hello(@RequestParam(name = "name", required = false) String name, Model model)
```

### 6-3. `\ufeff` は不正な文字
原因:
- JavaファイルにBOM付きUTF-8が混入

対処:
- UTF-8（BOMなし）で再保存する

---

## 7. 理解確認チェック
- `pom.xml` の `dependencies` と `plugins` の違いを説明できる
- `mvn test` と `mvn package` の違いを説明できる
- DIとは「newしないこと」だけでなく「注入で依存を渡す設計」だと説明できる
- MVCの流れを `GET /hello -> Controller -> Service -> Template` で説明できる
