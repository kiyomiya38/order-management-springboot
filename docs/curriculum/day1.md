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
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.shinesoft</groupId>
  <artifactId>attendance-management</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <name>attendance-management</name>
  <description>Attendance Management MVP</description>

  <properties>
    <java.version>17</java.version>
    <spring-boot.version>3.2.6</spring-boot.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
    <maven.compiler.release>17</maven.compiler.release>
  </properties>

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring-boot.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>${spring-boot.version}</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.13.0</version>
        <configuration>
          <release>${maven.compiler.release}</release>
          <encoding>${maven.compiler.encoding}</encoding>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

---

## 6. `application.yml` を作成
作成ファイル: `~/order-management-springboot/stages/day1/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: ${APP_NAME:attendance-management}
  thymeleaf:
    cache: false

server:
  port: ${SERVER_PORT:8080}

logging:
  level:
    root: ${LOG_LEVEL:INFO}

app:
  name: ${APP_NAME:attendance-management}
```

---

## 7. Applicationクラスを作成
作成ファイル: `~/order-management-springboot/stages/day1/src/main/java/com/shinesoft/attendance/AttendanceManagementApplication.java`

```java
package com.shinesoft.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AttendanceManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(AttendanceManagementApplication.class, args);
    }
}
```

ポイント:
- `@SpringBootApplication` が起点
- `main` から Spring Boot を起動する

---

## 8. Controllerを作成
作成ファイル: `~/order-management-springboot/stages/day1/src/main/java/com/shinesoft/attendance/web/HomeController.java`

```java
package com.shinesoft.attendance.web;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("workDate", LocalDate.now());
        model.addAttribute("statusLabel", "未出勤");
        model.addAttribute("startTime", null);
        model.addAttribute("endTime", null);
        return "index";
    }
}
```

ポイント:
- `/` にアクセスしたら `index.html` を返す
- `Model` で画面にデータを渡している

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
<!doctype html>
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>勤怠管理（MVP）</title>
  <link rel="stylesheet" th:href="@{/styles.css}" />
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理システム（MVP）</h1>
      <p class="subtitle">研修用 / Day1（画面表示のみ）</p>
    </header>

    <section class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <span class="status-badge" th:text="${statusLabel}">未出勤</span>
      </div>
      <p>日付: <span th:text="${workDate}">2026-02-05</span></p>
      <p>出勤時刻: -</p>
      <p>退勤時刻: -</p>
    </section>

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

---

## 10. CSSを作成
作成ファイル: `~/order-management-springboot/stages/day1/src/main/resources/static/styles.css`

```css
:root {
  --bg: #f6f6f2;
  --panel: #ffffff;
  --text: #202124;
  --muted: #6b7280;
  --accent: #0ea5e9;
  --border: #e5e7eb;
}

* { box-sizing: border-box; }

body {
  margin: 0;
  font-family: "Segoe UI", Tahoma, sans-serif;
  color: var(--text);
  background: var(--bg);
}

.container {
  max-width: 920px;
  margin: 0 auto;
  padding: 24px;
}

header {
  margin-bottom: 16px;
}

h1 {
  margin: 0 0 4px;
}

.subtitle {
  color: var(--muted);
  margin: 0 0 16px;
}

.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  background: #e0f2fe;
  color: #0369a1;
  font-size: 12px;
}

.status-working { background: #fef9c3; color: #854d0e; }
.status-finished { background: #dcfce7; color: #166534; }

.row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
}

input, select {
  padding: 8px;
  border: 1px solid var(--border);
  border-radius: 6px;
}

button {
  padding: 8px 12px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

button:hover { opacity: 0.9; }

.danger { background: #ef4444; }

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

th, td {
  border-bottom: 1px solid var(--border);
  text-align: left;
  padding: 8px;
}

.muted { color: var(--muted); }

.alert {
  padding: 10px 12px;
  border-radius: 6px;
  margin-bottom: 12px;
}

.alert-error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.alert-info {
  background: #e0f2fe;
  color: #075985;
  border: 1px solid #bae6fd;
}
```

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
