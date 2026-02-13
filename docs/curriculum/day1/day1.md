# Day1（7/9）最小MVCで画面表示（完成版を自分で作成）

## 目的（Day1でできるようになること）
- Spring Boot アプリを「ゼロから」起動できる
- Thymeleafで画面が表示される仕組み（Controller → Template）が分かる

## 前提
- Day0（HTML / Java 基礎）を実施済み
- Java 17 / Maven 3.9+ がインストール済み
- このリポジトリのルートで作業する

## Spring Bootとは（Day1開始前に読む）
Spring Bootは、JavaでWebアプリを作るためのSpringの実行基盤です。  
通常は手作業で必要になる設定を減らし、`mvn spring-boot:run` で起動できる状態を短時間で作れます。

### Springとは（用語の整理）
- Spring:
  - Javaアプリ開発を効率化するための技術群（フレームワーク群）
- Spring Framework:
  - Springの中核。DI、Web MVCなどの基盤機能を提供
- Spring Boot:
  - Spring Frameworkを「すぐ起動できる形」にまとめた仕組み
  - 設定の自動化（Auto Configuration）で初期構築を簡略化
- Spring MVC:
  - Web機能の一部。`@Controller` や `@GetMapping` でURLと処理を結び付ける

関係イメージ:
- Spring（全体）
- Spring Framework（中核）
- Spring Boot（起動・設定を簡単にする）
- Spring MVC（Web画面/HTTPを扱う機能）

Day1では、以下の最小構成を体験します。
- `Controller`:
  - URLリクエストを受け取り、画面に渡すデータを用意する
- `Template`（Thymeleaf）:
  - `Controller`から受け取ったデータをHTMLに埋め込んで表示する
- `Application`:
  - Spring Bootアプリの起動エントリポイント（`main`メソッド）

この研修の目的は、Java文法を深掘りすることではなく、後続のDocker/CI/CD/K8sで扱う「動くアプリ土台」を作ることです。  
そのためDay1は、最小の構成で起動と画面表示に集中します。

## Day1で作るもの（最小MVC）
- 画面: `/`（勤怠トップ画面、表示のみ）
- 機能: まだ出勤/退勤はしない（Day2から）

---

## 0. 事前確認（環境セットアップはDay0）
環境セットアップ手順は `~/order-management-springboot/docs/curriculum/day0/day0.md` の「0. 環境セットアップ」で実施します。  
Day1開始前に、以下だけ確認してください。

```bash
java -version
mvn -version
git --version
```

3つともエラーなしで表示されれば Day1 を開始できます。

---

## 1. Maven超入門（最初に読む）
Day1で使う `mvn` は、Javaプロジェクトの「依存関係管理」と「ビルド自動化」を行うコマンドです。

- `pom.xml`:
  - Mavenが読む設定ファイル
  - 使用ライブラリ（依存関係）やJavaバージョンなどを定義する
- `mvn spring-boot:run`:
  - 必要ライブラリを取得
  - Javaコードをコンパイル
  - Spring Bootアプリを起動
- `mvn clean`:
  - 前回生成物（`target`）を削除して再ビルドしやすくする

Day1で最低限覚えるコマンドは以下の3つです。
- `mvn -version`
- `mvn clean`
- `mvn spring-boot:run`

---

## 2. `mvn`コマンドを先に体験（コード変更なし）
まず、どのフォルダで実行しているかを確認する習慣を付けます。

```bash
pwd
```

この時点では、まだ `~/order-management-springboot/stages/day1` を作っていないため `pom.xml` はありません。  
`mvn -version` だけ先に実行して、Maven自体が動くことを確認します。

```bash
mvn -version
```

---

## 3. 作業フォルダ
Day1は `~/order-management-springboot/stages/day1` に **自分でコードを作成** します。  
まずこのフォルダを作成し、このフォルダの中で以降の作業を行ってください。

```bash
mkdir -p ~/order-management-springboot/stages/day1
cd ~/order-management-springboot/stages/day1
```

以降の `作成ファイル` は、`~/order-management-springboot` からのフルパスで表記します。  
例: `~/order-management-springboot/stages/day1/pom.xml`

### VS Codeでフォルダを開く（GUI）
1. VS Code を起動
2. `ファイル` → `フォルダーを開く`  
3. `~/order-management-springboot/stages/day1` を選択  
   - フォルダが無い場合は、エクスプローラーで `~/order-management-springboot/stages/day1` を作成してから開く

---

## 4. ディレクトリ構成を作成
```bash
mkdir -p ~/order-management-springboot/stages/day1/src/main/java/com/shinesoft/attendance
mkdir -p ~/order-management-springboot/stages/day1/src/main/java/com/shinesoft/attendance/web
mkdir -p ~/order-management-springboot/stages/day1/src/main/resources/templates
mkdir -p ~/order-management-springboot/stages/day1/src/main/resources/static
```

### VS Codeでディレクトリを作る（GUI）
1. 左側のエクスプローラーで `src` を右クリック → `新しいフォルダー`
2. 以下を順に作成  
   - `~/order-management-springboot/stages/day1/src/main/java/com/shinesoft/attendance`  
   - `~/order-management-springboot/stages/day1/src/main/java/com/shinesoft/attendance/web`  
   - `~/order-management-springboot/stages/day1/src/main/resources/templates`  
   - `~/order-management-springboot/stages/day1/src/main/resources/static`

---

## 5. `pom.xml` を作成（Maven設定）
作成ファイル: `~/order-management-springboot/stages/day1/pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <!-- Mavenがこのファイルをどう読むかを示すPOM仕様バージョン（通常は4.0.0固定） -->
  <modelVersion>4.0.0</modelVersion>

  <!-- このプロジェクトを一意に識別する情報 -->
  <!-- groupId: 組織名/ドメインを逆順で書くのが一般的 -->
  <groupId>com.shinesoft</groupId>
  <!-- artifactId: 成果物（Jar）の名前になる -->
  <artifactId>attendance-management</artifactId>
  <!-- version: SNAPSHOTは「開発中バージョン」の意味 -->
  <version>0.0.1-SNAPSHOT</version>
  <!-- name/description: 人が読むための表示名・説明 -->
  <name>attendance-management</name>
  <description>Attendance Management MVP</description>

  <!-- 共通で使う値を変数化。1か所変更で全体へ反映できる -->
  <properties>
    <!-- 使用するJavaのバージョン -->
    <java.version>17</java.version>
    <!-- Spring Boot関連ライブラリの基準バージョン -->
    <spring-boot.version>3.2.6</spring-boot.version>
    <!-- 文字コード（日本語の文字化け防止のためUTF-8に統一） -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
    <!-- コンパイル後の互換バージョン（Java 17としてビルド） -->
    <maven.compiler.release>17</maven.compiler.release>
  </properties>

  <!-- 依存ライブラリの「バージョン表」を取り込む場所（ここではSpring BootのBOMを利用） -->
  <!-- ポイント: ここは実際に使うライブラリ一覧ではなく、バージョン管理用 -->
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring-boot.version}</version>
        <!-- type=pom + scope=import でBOM（部品の推奨バージョンセット）を読み込む -->
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <!-- このアプリで「実際に使う」ライブラリ一覧 -->
  <dependencies>
    <!-- Webアプリ機能（Spring MVC, 組み込みTomcat, JSON変換など） -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- サーバーサイドHTMLテンプレート機能（Thymeleaf） -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
  </dependencies>

  <!-- Mavenのビルド処理を拡張するプラグイン設定 -->
  <build>
    <plugins>
      <!-- Spring Boot起動用プラグイン。mvn spring-boot:run を使えるようにする -->
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>${spring-boot.version}</version>
      </plugin>
      <!-- Javaコンパイル設定プラグイン -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.13.0</version>
        <configuration>
          <!-- Java 17としてコンパイル -->
          <release>${maven.compiler.release}</release>
          <!-- ソースコードの文字コード -->
          <encoding>${maven.compiler.encoding}</encoding>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

理解ポイント（5分）:
- このファイルの役割:
  - Mavenがプロジェクトをビルド/起動するための設定ファイル
- 今日見るキーワード:
  - `<dependencies>`（使うライブラリ）
  - `spring-boot-starter-web`（Web機能）
  - `spring-boot-starter-thymeleaf`（画面テンプレート機能）
- まず見る場所:
  - `<groupId>`, `<artifactId>`, `<version>`（プロジェクト識別）
  - `<dependencies>`（必要機能の宣言）
  - `spring-boot-maven-plugin`（`mvn spring-boot:run` を実行するためのプラグイン）
- 変更して試す:
  - `<description>` を任意の文字列に変えても起動できることを確認
- よくあるミス:
  - XMLタグの閉じ忘れでビルド失敗
  - `<dependency>` の入れ子崩れで依存解決失敗

---

## 6. `application.yml` を作成
作成ファイル: `~/order-management-springboot/stages/day1/src/main/resources/application.yml`

```yaml
# Spring Framework全体の設定
spring:
  application:
    # アプリ名。${環境変数:デフォルト値} の形式
    # APP_NAMEが未設定なら attendance-management を使う
    name: ${APP_NAME:attendance-management}
  thymeleaf:
    # テンプレートキャッシュ設定
    # false: 画面HTMLを修正した時に反映されやすい（学習中に便利）
    cache: false

# 組み込みWebサーバー（Tomcat）の設定
server:
  # 待受ポート。SERVER_PORTが未設定なら8080で起動
  port: ${SERVER_PORT:8080}

# ログ出力設定
logging:
  level:
    # アプリ全体(root)のログレベル（INFO/DEBUG/WARN/ERROR など）
    root: ${LOG_LEVEL:INFO}

# 独自設定（spring配下ではない任意キー）
# ControllerやServiceで参照するためのアプリ固有値を置ける
app:
  # 画面表示などで使うアプリ名（今回は spring.application.name と同じ値）
  name: ${APP_NAME:attendance-management}
```

理解ポイント（5分）:
- このファイルの役割:
  - アプリの設定値をまとめるファイル
- 今日見るキーワード:
  - `server.port`（待受ポート）
  - `spring.application.name`（アプリ名）
  - `logging.level.root`（ログ出力レベル）
- まず見る場所:
  - `server.port: ${SERVER_PORT:8080}`（環境変数未指定時は8080）
  - `app.name: ${APP_NAME:attendance-management}`（画面表示などで利用可能）
- 変更して試す:
  - `server.port` のデフォルトを `8081` に変更して起動確認
- よくあるミス:
  - YAMLのインデントずれ（スペース数不一致）
  - `:` の前後を崩して起動失敗

---

## 7. Applicationクラスを作成
作成ファイル: `~/order-management-springboot/stages/day1/src/main/java/com/shinesoft/attendance/AttendanceManagementApplication.java`

```java
// このクラスが属するパッケージ。
// フォルダ構成 `com/shinesoft/attendance` と一致させる必要がある。
package com.shinesoft.attendance;

// Spring Bootアプリを起動するためのクラス
import org.springframework.boot.SpringApplication;
// 「このクラスがSpring Bootの起点である」ことを示すアノテーション
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication は以下をまとめた便利アノテーション:
// - 設定クラスとして扱う
// - コンポーネントスキャンを有効化
// - 自動設定を有効化
@SpringBootApplication
public class AttendanceManagementApplication {
    // Javaプログラムの開始地点（エントリポイント）
    public static void main(String[] args) {
        // Spring Bootアプリを起動する
        // 第1引数: 起動クラス
        // 第2引数: コマンドライン引数
        SpringApplication.run(AttendanceManagementApplication.class, args);
    }
}
```

ポイント:
- `@SpringBootApplication` が起点
- `main` から Spring Boot を起動する

理解ポイント（5分）:
- このファイルの役割:
  - Java実行時の「開始地点」
- 今日見るキーワード:
  - `@SpringBootApplication`
  - `SpringApplication.run(...)`
  - `main(String[] args)`
- 変更して試す:
  - クラス名を変えずに `main` の中へ `System.out.println("start");` を追加し、起動時に表示されることを確認
- よくあるミス:
  - ファイル名とクラス名の不一致
  - `package` 宣言と配置パスの不一致

---

## 8. Controllerを作成
作成ファイル: `~/order-management-springboot/stages/day1/src/main/java/com/shinesoft/attendance/web/HomeController.java`

```java
// Controllerクラスの配置先パッケージ。
// `web` は「画面/HTTPリクエストを扱う層」という意味で分けている。
package com.shinesoft.attendance.web;

// 日付を扱うJava標準クラス（今日の日付を表示するために使う）
import java.time.LocalDate;

// このクラスが画面表示用Controllerであることを示す
import org.springframework.stereotype.Controller;
// Controllerからテンプレートへ値を渡す入れ物
import org.springframework.ui.Model;
// HTTP GETのURLとメソッドを対応付ける
import org.springframework.web.bind.annotation.GetMapping;

// 「画面を返すController」としてSpringに登録される
@Controller
public class HomeController {

    // ブラウザが "/" にGETアクセスした時に、このメソッドが呼ばれる
    @GetMapping("/")
    public String index(Model model) {
        // テンプレートへ渡す値を追加する（HTML側では ${workDate} で参照）
        model.addAttribute("workDate", LocalDate.now());
        // 画面上の状態ラベル（HTML側: ${statusLabel}）
        model.addAttribute("statusLabel", "未出勤");
        // Day1では未使用のため null を渡している（HTMLでは "-" 表示）
        model.addAttribute("startTime", null);
        model.addAttribute("endTime", null);
        // `templates/index.html` を表示する、という意味
        // 注意: 先頭に "/" は付けない（`return "index"` が基本）
        return "index";
    }
}
```

ポイント:
- `/` にアクセスしたら `index.html` を返す
- `Model` で画面にデータを渡している

理解ポイント（10分）:
- このファイルの役割:
  - ブラウザからのリクエストを受け、画面に渡すデータを準備する
- 今日見るキーワード:
  - `@Controller`（画面表示の制御クラス）
  - `@GetMapping("/")`（URLとメソッドの対応）
  - `model.addAttribute(...)`（テンプレートへ値を渡す）
- 変更して試す:
  - `statusLabel` を `"未出勤"` から `"出勤前（確認用）"` に変更し、画面反映を確認
- よくあるミス:
  - `return "index"` を `return "/index"` にしてテンプレート解決に失敗
  - `addAttribute` のキー名とHTML側 `${...}` の不一致

---

## 8.5 Spring Boot + Thymeleaf の表示の流れ（Controller -> Template）

### 1) リクエストの流れ
1. ブラウザで `http://localhost:8080/` にアクセスする
2. Spring Boot の DispatcherServlet が `@GetMapping("/")` のメソッドを探す
3. `HomeController#index` が実行される
4. `model.addAttribute(...)` で画面に渡す値を詰める
5. `return "index"` で `templates/index.html` を表示対象として返す
6. Thymeleaf が `index.html` 内の `th:text` などを評価して HTML を生成する
7. 生成されたHTMLがブラウザに返る

### 2) 対応関係（重要）
- `@GetMapping("/")` -> URL `/` を処理
- `return "index"` -> `~/order-management-springboot/stages/day1/src/main/resources/templates/index.html`
- `model.addAttribute("statusLabel", "未出勤")` -> HTML側 `${statusLabel}` に表示

### 3) 3分ハンズオン（理解確認）
1. `HomeController` の `statusLabel` を `"未出勤"` から `"出勤中(テスト)"` に変更
2. 再起動して `/` を開き、表示が変わることを確認
3. 元に戻す

### 4) よくあるミス
- `return "index"` のスペルミス -> テンプレートが見つからずエラー
- `addAttribute` のキー名と `${...}` が不一致 -> 値が表示されない
- `@Controller` / `@GetMapping` を付け忘れる -> URLにアクセスできない

---

## 9. テンプレート（画面）を作成
作成ファイル: `~/order-management-springboot/stages/day1/src/main/resources/templates/index.html`

```html
<!-- HTML5の文書宣言 -->
<!doctype html>
<!-- lang="ja": 文章言語を日本語として宣言 -->
<!-- xmlns:th: Thymeleaf属性（th:*）を使うための名前空間宣言 -->
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
<head>
  <!-- 文字コード。日本語を正しく表示するためにUTF-8 -->
  <meta charset="utf-8" />
  <!-- スマホ表示時の拡大率設定（レスポンシブ対応の基本） -->
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>勤怠管理（MVP）</title>
  <!-- @{} はURL生成式。/static 配下の styles.css を参照する -->
  <link rel="stylesheet" th:href="@{/styles.css}" />
</head>
<body>
  <!-- 画面全体のラッパー -->
  <div class="container">
    <header>
      <h1>勤怠管理システム（MVP）</h1>
      <p class="subtitle">研修用 / Day1（画面表示のみ）</p>
    </header>

    <!-- 1つ目の情報パネル -->
    <section class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <!-- ${statusLabel}: Controllerで addAttribute した値を表示 -->
        <!-- 右側の「未出勤」はフォールバック文字（Thymeleaf未評価時に見える値） -->
        <span class="status-badge" th:text="${statusLabel}">未出勤</span>
      </div>
      <!-- ${workDate}: Controllerの workDate を表示 -->
      <p>日付: <span th:text="${workDate}">2026-02-05</span></p>
      <p>出勤時刻: -</p>
      <p>退勤時刻: -</p>
    </section>

    <!-- 2つ目の情報パネル -->
    <section class="panel">
      <h2>業務ルール（抜粋）</h2>
      <ul>
        <li>同日に複数回の出勤は不可</li>
        <li>未出勤で退勤は不可</li>
        <li>退勤後に再度退勤は不可</li>
      </ul>
      <p class="muted">※ Day2 以降でボタンや業務ルールを実装します。</p>
    </section>
  </div>
</body>
</html>
```

理解ポイント（10分）:
- このファイルの役割:
  - Controllerから渡された値をHTMLとして表示するテンプレート
- 今日見るキーワード:
  - `xmlns:th="http://www.thymeleaf.org"`（Thymeleaf有効化）
  - `th:text="${statusLabel}"`（値の差し込み）
  - `th:href="@{/styles.css}"`（静的CSSの参照）
- 変更して試す:
  - `<title>` を変更してブラウザタブ名が変わることを確認
  - `h1` の文言を変更して画面に反映されることを確認
- よくあるミス:
  - `th:text` の`${}`忘れ
  - `templates` 以外に置いてテンプレートが見つからない

---

## 10. CSSを作成
作成ファイル: `~/order-management-springboot/stages/day1/src/main/resources/static/styles.css`

```css
/* :root は「全体で使えるCSS変数」を定義する場所 */
:root {
  /* ページ全体の背景色 */
  --bg: #f6f6f2;
  /* パネル（カード）の背景色 */
  --panel: #ffffff;
  /* 基本文字色 */
  --text: #202124;
  /* 補助文字色（少し薄い文字） */
  --muted: #6b7280;
  /* 強調色（ボタンなど） */
  --accent: #0ea5e9;
  /* 枠線色 */
  --border: #e5e7eb;
}

/* すべての要素で、幅計算に border/padding を含める */
* { box-sizing: border-box; }

/* ページ全体の基本スタイル */
body {
  /* ブラウザ既定の余白をリセット */
  margin: 0;
  /* 文字フォント */
  font-family: "Segoe UI", Tahoma, sans-serif;
  /* 基本文字色（CSS変数を参照） */
  color: var(--text);
  /* 背景色（CSS変数を参照） */
  background: var(--bg);
}

/* 画面中央に内容を寄せるコンテナ */
.container {
  /* 横幅の上限（これ以上は広がらない） */
  max-width: 920px;
  /* 左右を自動余白にして中央寄せ */
  margin: 0 auto;
  /* 内側の余白 */
  padding: 24px;
}

/* ヘッダー下に少し余白 */
header {
  margin-bottom: 16px;
}

/* タイトルの余白調整（上0 / 右0 / 下4 / 左0） */
h1 {
  margin: 0 0 4px;
}

/* サブタイトルは薄い色 */
.subtitle {
  color: var(--muted);
  margin: 0 0 16px;
}

/* 情報ブロック（白いカード） */
.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  /* 角を丸くする */
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

/* パネル見出しの左右配置（見出し + ステータス） */
.panel-header {
  /* 子要素を横並びにする */
  display: flex;
  /* 垂直方向の中央揃え */
  align-items: center;
  /* 左右端に配置 */
  justify-content: space-between;
}

/* 「未出勤」などのバッジ */
.status-badge {
  display: inline-block;
  padding: 4px 10px;
  /* 999pxでカプセル形にする */
  border-radius: 999px;
  background: #e0f2fe;
  color: #0369a1;
  font-size: 12px;
}

/* 状態別の色（Day2以降で利用） */
.status-working { background: #fef9c3; color: #854d0e; }
.status-finished { background: #dcfce7; color: #166534; }

/* 入力項目などを横並びにする共通クラス */
.row {
  display: flex;
  /* 子要素間の間隔 */
  gap: 8px;
  /* 幅不足時は折り返し */
  flex-wrap: wrap;
  align-items: center;
}

/* ラベルと入力欄を縦並びにする */
label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
}

/* 入力欄とセレクトボックスの共通見た目 */
input, select {
  padding: 8px;
  border: 1px solid var(--border);
  border-radius: 6px;
}

/* ボタンの基本スタイル */
button {
  padding: 8px 12px;
  background: var(--accent);
  color: #fff;
  /* ブラウザ既定の枠線を消す */
  border: none;
  border-radius: 6px;
  /* マウスカーソルを手の形に */
  cursor: pointer;
}

/* ホバー時に少し薄くして押せる感を出す */
button:hover { opacity: 0.9; }

/* 危険操作ボタン（削除など）の色 */
.danger { background: #ef4444; }

/* 表の基本設定 */
table {
  /* 横幅いっぱい */
  width: 100%;
  /* セルの境界線を1本にまとめる */
  border-collapse: collapse;
  font-size: 14px;
}

/* 表ヘッダーとセルの共通設定 */
th, td {
  border-bottom: 1px solid var(--border);
  text-align: left;
  padding: 8px;
}

/* 補足文字用の薄い色 */
.muted { color: var(--muted); }

/* 通知メッセージの共通枠 */
.alert {
  padding: 10px 12px;
  border-radius: 6px;
  margin-bottom: 12px;
}

/* エラー通知の色 */
.alert-error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

/* 情報通知の色 */
.alert-info {
  background: #e0f2fe;
  color: #075985;
  border: 1px solid #bae6fd;
}
```

理解ポイント（10分）:
- このファイルの役割:
  - HTMLの見た目（色・余白・配置）を制御する
- 今日見るキーワード:
  - `:root`（共通変数の定義）
  - `--bg` / `var(--bg)`（CSS変数の定義と参照）
  - `.panel`（クラスセレクタ）
- 変更して試す:
  - `--bg` を別の色に変えて背景色が変わることを確認
  - `h1` の `margin` を調整して見た目の変化を確認
- よくあるミス:
  - `{}` の閉じ忘れで後続のスタイルが無効化
  - HTML側のクラス名とCSS側のクラス名不一致

---

## 11. 起動
`~/order-management-springboot/stages/day1` で実行していることを先に確認してください。

```bash
pwd
ls
```

`pom.xml` が見えることを確認したら起動します。

```bash
mvn spring-boot:run
```

---

## 12. 画面確認
ブラウザで:
```
http://localhost:8080/
```

確認ポイント:
- 「勤怠トップ」が表示される
- 状態が「未出勤」と表示される
- 時刻は「-」になっている

---

## 13. 今日のゴール
- MVCの最低限構成（Controller → Template）が動くことを確認
- Day2から「出勤ボタン」を実装する準備ができた

---

## 14. つまずきポイント
- `mvn` が通らない → 環境変数を確認
- `The goal you specified requires a project to execute but there is no POM in this directory`:
  - 実行場所が `~/order-management-springboot/stages/day1` になっているか確認
  - `ls` で `pom.xml` が存在するか確認
- 画面が出ない → 起動ターミナルのエラーを見る

---

## 15. 時間割目安
- 午前: Java/Maven導入(60分) / 作成(90分)
- 午後: 起動・画面確認(60分) / まとめ(30分)
