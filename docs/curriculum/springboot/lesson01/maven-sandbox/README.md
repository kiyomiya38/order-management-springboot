# Maven Sandbox (Lesson1)

> バックエンド短縮コースでの扱い
>
> このSandboxは短縮コースでも必修です。Maven操作を確認した後、特に「5. Spring化ハンズオン（DI/MVC理解）」で、ServiceをControllerへコンストラクタ注入する流れを確認します。
> HTMLテンプレートは提供コードとして指定位置へ配置し、HTML自体の実装は評価対象にしません。既存コード内の説明コメントは削除せず使用します。

この教材は、`pom.xml` と `mvn` コマンドを「なぜ必要か」から理解し、最後にSpringの最小MVCまで動かすためのものです。
対象は **Java初学者** を想定しています。

このREADMEと同じ`maven-sandbox`フォルダには、講師確認用の完成例が入っています。
受講者は完成例を直接編集せず、`practice/springboot/maven-sandbox`へ自分の作業用プロジェクトを作ります。
これにより、Step 5で「Thymeleafを追加する前」と「追加した後」を混同せず確認できます。

## 0. この教材で到達する状態
- `pom.xml` の主要ブロックを自分の言葉で説明できる
- `mvn validate/package/dependency:tree/clean` の目的を説明できる
- `@SpringBootApplication`や`@Controller`などのアノテーションが、Springへ役割を伝える目印だと説明できる
- `mvn spring-boot:run` で起動し、`Controller -> Service -> Template` の流れを説明できる

---

## 1. まず全体像（概念）

### 1-1. Maven（メイヴン）とは
Mavenは、Javaプロジェクトの作業を自動化するツールです。
主に次を担当します。

- 依存ライブラリの取得
- コンパイル
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

作成ファイル: `~/order-management-springboot/practice/springboot/maven-sandbox/pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd"> <!-- POM全体のルート要素 -->
  <!-- xmlns: このXMLがMaven POMのタグ体系であることを示す名前空間 -->
  <!-- xmlns:xsi: xsi:schemaLocation のような xsd検証用属性を使うための宣言 -->
  <!-- xsi:schemaLocation: どのXSD（定義ファイル）でこのPOMを検証するかを示す -->
  <modelVersion>4.0.0</modelVersion> <!-- POM仕様バージョン（通常4.0.0） -->

  <groupId>com.shinesoft.training</groupId> <!-- 組織識別子（ドメイン逆順が一般的） -->
  <artifactId>day1-maven-sandbox</artifactId> <!-- 成果物名（jar名の元） -->
  <version>1.0.0-SNAPSHOT</version> <!-- 開発中バージョン -->
  <name>day1-maven-sandbox</name> <!-- プロジェクト表示名 -->
  <description>Lesson1 Maven learning sandbox project</description> <!-- 説明 -->

  <properties> <!-- 共通で使う値をまとめるセクション -->
    <java.version>17</java.version> <!-- Javaバージョン -->
    <maven.compiler.release>${java.version}</maven.compiler.release> <!-- Java 17でコンパイル -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding> <!-- ソース文字コード -->
    <spring.boot.version>3.5.15</spring.boot.version> <!-- Spring Boot基準バージョン -->
  </properties> <!-- propertiesセクション終了 -->

  <dependencyManagement> <!-- 依存バージョンを一括管理する領域 -->
    <dependencies> <!-- 依存管理対象の一覧 -->
      <dependency> <!-- Spring Boot BOM（依存バージョン表）を取り込む -->
        <groupId>org.springframework.boot</groupId> <!-- Spring Boot系ライブラリのグループ -->
        <artifactId>spring-boot-dependencies</artifactId> <!-- BOM本体 -->
        <version>${spring.boot.version}</version> <!-- BOMの基準バージョン -->
        <type>pom</type> <!-- BOMとして読み込む -->
        <scope>import</scope> <!-- BOMをimportしてversion記述を減らす -->
      </dependency> <!-- BOM依存定義の終了 -->
    </dependencies> <!-- dependencyManagement内のdependencies終了 -->
  </dependencyManagement> <!-- dependencyManagementセクション終了 -->

  <dependencies> <!-- 実際に使うライブラリ -->
    <dependency> <!-- Web機能を使うための依存 -->
      <groupId>org.springframework.boot</groupId> <!-- Spring Boot系ライブラリのグループ -->
      <artifactId>spring-boot-starter-web</artifactId> <!-- Spring MVC + 組み込みTomcat -->
    </dependency> <!-- Web依存の終了 -->

  </dependencies> <!-- dependenciesセクション終了 -->

  <build> <!-- ビルドに使うプラグイン設定 -->
    <plugins> <!-- 使用するMavenプラグインの一覧 -->
      <plugin> <!-- Javaコンパイル用プラグイン -->
        <groupId>org.apache.maven.plugins</groupId> <!-- 公式Mavenプラグイングループ -->
        <artifactId>maven-compiler-plugin</artifactId> <!-- Javaコンパイラ設定 -->
        <version>3.13.0</version> <!-- プラグインバージョン -->
        <configuration> <!-- プラグイン固有設定 -->
          <release>${maven.compiler.release}</release> <!-- Java 17でコンパイル -->
        </configuration> <!-- compiler plugin設定終了 -->
      </plugin> <!-- maven-compiler-pluginの終了 -->

      <plugin> <!-- Spring Boot起動/パッケージ化用プラグイン -->
        <groupId>org.springframework.boot</groupId> <!-- Spring Boot系プラグイングループ -->
        <artifactId>spring-boot-maven-plugin</artifactId> <!-- spring-boot:run などを提供 -->
        <version>${spring.boot.version}</version> <!-- mvn spring-boot:run を使うために必要 -->
        <executions>
          <execution>
            <goals>
              <goal>repackage</goal> <!-- mvn packageで実行可能Jarを作る -->
            </goals>
          </execution>
        </executions>
      </plugin> <!-- spring-boot-maven-pluginの終了 -->
    </plugins> <!-- pluginsセクション終了 -->
  </build> <!-- buildセクション終了 -->
</project> <!-- projectルート終了 -->
```

### 2-1. 最低限ここだけ先に読む
- `<groupId> / <artifactId> / <version>`
- `<dependencies>`
- `<build><plugins>`

---

## 3. 手順（実際に作る）

### 3-1. 作業場所
```bash
mkdir -p ~/order-management-springboot/practice/springboot/maven-sandbox
cd ~/order-management-springboot/practice/springboot/maven-sandbox
pwd
ls
```

この時点の作業フォルダは空で構いません。Section 2の`pom.xml`を作成してから、以降のファイルを追加します。

### 3-2. Java用ディレクトリ作成
```bash
mkdir -p ~/order-management-springboot/practice/springboot/maven-sandbox/src/main/java/com/shinesoft/sandbox
```

なぜ長い階層か:
- Maven標準構成を守るため
- Javaの`package`とフォルダ構成を一致させるため

---

## 4. `mvn` コマンド実行（出力の見方と切り分け）

実行前に必ず確認:
```bash
cd ~/order-management-springboot/practice/springboot/maven-sandbox
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

### 4-2. `mvn dependency:tree`
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

### 4-3. `mvn clean`
目的:
- `target` を削除してビルド成果物を初期化する

```bash
mvn clean
```

成功時の見方:
- `BUILD SUCCESS`
- `target` が削除される

### 4-4. エラー修正後に `clean` は必須か
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

バックエンド短縮コースの重点:

- `GreetingService` を `new` せず、Controllerのコンストラクタで受け取る
- `private final` フィールドへ依存を保持する
- `GET /hello -> Controller -> Service -> Template` の順で処理を追跡する
- テンプレートは提供コードを配置し、`message` と `${message}` の対応を確認する

通常のJavaでは、利用側が必要なオブジェクトを自分で作れます。

```java
GreetingService service = new GreetingService();
```

Springでは、`@Service`が付いた`GreetingService`をSpringが作成します。
Controllerは、作成済みのオブジェクトをコンストラクタで受け取ります。

```java
private final GreetingService greetingService;

public GreetingController(GreetingService greetingService) {
    this.greetingService = greetingService;
}
```

この「必要なオブジェクトを外部から渡してもらう」方法をDI（依存性注入）と呼びます。
このSandboxでは、まず`new`との違いを確認できれば十分です。

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
作成ファイル: `~/order-management-springboot/practice/springboot/maven-sandbox/src/main/java/com/shinesoft/sandbox/SandboxApplication.java`

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
mkdir -p ~/order-management-springboot/practice/springboot/maven-sandbox/src/main/java/com/shinesoft/sandbox/service
```

作成ファイル: `~/order-management-springboot/practice/springboot/maven-sandbox/src/main/java/com/shinesoft/sandbox/service/GreetingService.java`

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
mkdir -p ~/order-management-springboot/practice/springboot/maven-sandbox/src/main/java/com/shinesoft/sandbox/web
```

作成ファイル: `~/order-management-springboot/practice/springboot/maven-sandbox/src/main/java/com/shinesoft/sandbox/web/GreetingController.java`

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
mkdir -p ~/order-management-springboot/practice/springboot/maven-sandbox/src/main/resources/templates
```

作成ファイル: `~/order-management-springboot/practice/springboot/maven-sandbox/src/main/resources/templates/hello.html`

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
mvn package
mvn spring-boot:run
```

`mvn package`でコンパイルとリソース配置を行い、`target/day1-maven-sandbox-1.0.0-SNAPSHOT.jar`を作成します。
`BUILD SUCCESS`を確認してからアプリを起動します。

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
cd ~/order-management-springboot/practice/springboot/maven-sandbox
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
- `mvn validate` と `mvn package` の役割の違いを説明できる
- DIとは「newしないこと」だけでなく「注入で依存を渡す設計」だと説明できる
- MVCの流れを `GET /hello -> Controller -> Service -> Template` で説明できる
