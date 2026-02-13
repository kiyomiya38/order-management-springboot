# Day2（7/10）出勤機能の実装（Entity / Repository / Service）

## 目的（Day2でできるようになること）
- 出勤ボタン押下で DB に勤怠レコードを登録できる
- `Controller -> Service -> Repository -> DB` の流れを追える
- 同日の二重出勤を業務ルールとして弾ける

## 前提
- Day1 を完了している
- `java -version` と `mvn -version` が通る
- このリポジトリのルートで作業する

## Day2で作るもの
- 画面: `/`（トップ画面）
- 機能:
  - `POST /clock-in` で出勤登録
  - 当日の状態表示（未出勤 / 出勤中）
  - 二重出勤時にエラーメッセージ表示

---

## 0. 事前確認
```bash
java -version
mvn -version
git --version
```

---

## 1. 作業フォルダ
Day2 は `~/order-management-springboot/stages/day2` に自分でコードを作成します。

```bash
mkdir -p ~/order-management-springboot/stages/day2
cd ~/order-management-springboot/stages/day2
```

以降の `作成ファイル` は、`~/order-management-springboot` からのフルパスで表記します。  
例: `~/order-management-springboot/stages/day2/pom.xml`

### VS Codeでフォルダを開く（GUI）
1. VS Code を起動
2. `ファイル` -> `フォルダーを開く`
3. `~/order-management-springboot/stages/day2` を選択

---

## 2. ディレクトリ構成を作成
```bash
mkdir -p ~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance
mkdir -p ~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/web
mkdir -p ~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/service
mkdir -p ~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/domain
mkdir -p ~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/repository
mkdir -p ~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/exception
mkdir -p ~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/config
mkdir -p ~/order-management-springboot/stages/day2/src/main/resources/templates
mkdir -p ~/order-management-springboot/stages/day2/src/main/resources/static
```

---

## 3. `pom.xml` を作成（Maven設定）
作成ファイル: `~/order-management-springboot/stages/day2/pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <!-- Mavenが読むPOM仕様バージョン（通常は4.0.0） -->
  <modelVersion>4.0.0</modelVersion>

  <!-- プロジェクトの識別情報 -->
  <!-- groupId: 組織名/ドメイン逆順、artifactId: 成果物名、version: 開発版番号 -->
  <groupId>com.shinesoft</groupId>
  <artifactId>attendance-management</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <!-- name/description は人が見て判別しやすくするための情報 -->
  <name>attendance-management</name>
  <description>Attendance Management MVP Day2</description>

  <!-- 共通値を変数化。${...} で再利用できる -->
  <properties>
    <!-- Javaの使用バージョン -->
    <java.version>17</java.version>
    <!-- Spring Boot関連の基準バージョン -->
    <spring-boot.version>3.2.6</spring-boot.version>
    <!-- 文字コード設定（文字化け防止） -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
    <!-- Java 17としてコンパイルする -->
    <maven.compiler.release>17</maven.compiler.release>
  </properties>

  <!-- 依存ライブラリの推奨バージョン表（BOM）を読み込む -->
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring-boot.version}</version>
        <!-- BOMをimportして、依存のversion記述を省略できるようにする -->
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <!-- このアプリで実際に使うライブラリ一覧 -->
  <dependencies>
    <!-- Web機能（Spring MVC、組み込みTomcat、JSON変換など） -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- Thymeleafテンプレート機能（サーバーサイドHTML描画） -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <!-- JPA機能（Entity/RepositoryでDB操作を行うため） -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <!-- H2データベース（研修用の軽量DB） -->
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <!-- 実行時のみ必要。コンパイル時には不要 -->
      <scope>runtime</scope>
    </dependency>
  </dependencies>

  <!-- Mavenのビルド処理を拡張するプラグイン -->
  <build>
    <plugins>
      <!-- mvn spring-boot:run を使えるようにする -->
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>${spring-boot.version}</version>
      </plugin>
      <!-- Javaのコンパイル設定（バージョン/文字コード） -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.13.0</version>
        <configuration>
          <!-- Java 17としてビルド -->
          <release>${maven.compiler.release}</release>
          <!-- UTF-8でソースを読む -->
          <encoding>${maven.compiler.encoding}</encoding>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

理解ポイント（10分）:
- このファイルの役割:
  - Day2のビルド設定を管理する（Web + 画面 + DB）
- Day1からの追加点:
  - `spring-boot-starter-data-jpa`（DBアクセス）
  - `h2`（研修用のローカルDB）
- まず見る場所:
  - `<dependencies>` の4つの依存関係
  - `<description>`（Day2用に識別）
- 変更して試す:
  - `<description>` を変更してもアプリが起動できることを確認
- よくあるミス:
  - `data-jpa` や `h2` の追加漏れでDB関連クラスが動かない

---

## 4. `application.yml` を作成
作成ファイル: `~/order-management-springboot/stages/day2/src/main/resources/application.yml`

```yaml
# Spring Framework全体の設定
spring:
  application:
    # アプリ名。環境変数 APP_NAME が無ければ attendance-management を使う
    name: ${APP_NAME:attendance-management}
  datasource:
    # H2の接続先（インメモリDB）。アプリ停止でデータは消える
    url: jdbc:h2:mem:attendance;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    # JDBCドライバ
    driver-class-name: org.h2.Driver
    # Day2では簡易構成として固定ユーザーを使用
    username: sa
    password:
  jpa:
    hibernate:
      # Entity定義に合わせてテーブルを自動更新（学習用設定）
      ddl-auto: update
    # 画面描画中の遅延読み込みを防ぐためfalse
    open-in-view: false
  thymeleaf:
    # 学習中はキャッシュ無効（HTML変更を反映しやすくする）
    cache: false
  h2:
    console:
      # H2コンソール（ブラウザでDB確認）を有効化
      enabled: true
      # H2コンソールのアクセスパス
      path: /h2-console

# 組み込みWebサーバーの設定
server:
  # 環境変数 SERVER_PORT が無い時は 8080
  port: ${SERVER_PORT:8080}

# ログ設定
logging:
  level:
    # ルートロガーの出力レベル
    root: ${LOG_LEVEL:INFO}
```

理解ポイント（10分）:
- このファイルの役割:
  - DB接続や起動時設定をまとめる
- Day2で重要な設定:
  - `spring.datasource.*`（H2接続先）
  - `spring.jpa.hibernate.ddl-auto: update`（テーブル自動更新）
  - `spring.h2.console.enabled: true`（H2コンソール有効化）
- まず見る場所:
  - `url: jdbc:h2:mem:attendance...`
  - `open-in-view: false`（画面描画時の不要なDBアクセス抑制）
- 変更して試す:
  - `server.port` のデフォルトを `8081` にして起動確認
- よくあるミス:
  - `jdbc:h2:mem:attendance` のスペルミス
  - YAMLインデント崩れ

---

## 5. Applicationクラス
作成ファイル: `~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/AttendanceManagementApplication.java`

```java
// このクラスが属するパッケージ（フォルダ構成と一致させる）
package com.shinesoft.attendance;

// Spring Boot起動用クラス
import org.springframework.boot.SpringApplication;
// このクラスをSpring Bootの起点として扱う
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 設定読み込み・コンポーネントスキャン・自動設定を有効化
@SpringBootApplication
public class AttendanceManagementApplication {
    // Java実行時の開始地点
    public static void main(String[] args) {
        // Spring Bootアプリを起動する
        SpringApplication.run(AttendanceManagementApplication.class, args);
    }
}
```

理解ポイント（5分）:
- このファイルの役割:
  - Spring Bootの起動エントリポイント
- Day1との関係:
  - Day2でもこのクラスは同じ（起点は変わらない）
- 確認すること:
  - `@SpringBootApplication` と `main` があること

---

## 6. Domain（Entity / Enum）を作成

### 6-1. 勤怠ステータス
作成ファイル: `~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/domain/AttendanceStatus.java`

```java
// 勤怠状態を固定値で管理する列挙型（Enum）
// 文字列の打ち間違いを防ぎ、状態の取り得る値を明確にする
package com.shinesoft.attendance.domain;

public enum AttendanceStatus {
    // まだ出勤していない
    NOT_STARTED,
    // 出勤中（開始時刻あり、終了時刻なし）
    WORKING,
    // 退勤済み（開始時刻・終了時刻あり）
    FINISHED
}
```

### 6-2. ユーザー
作成ファイル: `~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/domain/User.java`

```java
// Entity（DBテーブル）を置くパッケージ
package com.shinesoft.attendance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// このクラスをJPAの永続化対象（テーブル）として扱う
@Entity
// 対応するテーブル名
@Table(name = "users")
public class User {
    // 主キー
    @Id
    // DB側で自動採番（AUTO_INCREMENT）
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NULL不可、重複不可、最大50文字
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // 以下はアクセサ（getter/setter）
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
```

### 6-3. 勤怠
作成ファイル: `~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/domain/Attendance.java`

```java
// Entityクラスを置くパッケージ
package com.shinesoft.attendance.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// 勤怠テーブルに対応するEntity
@Entity
@Table(
    name = "attendances",
    // DB制約: 1ユーザー1日1件（同日の二重出勤をDBレベルでも防ぐ）
    uniqueConstraints = @UniqueConstraint(name = "uk_attendance_user_date", columnNames = {"user_id", "work_date"})
)
public class Attendance {
    // 主キー
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 多対1: 多数の勤怠レコードが1ユーザーに紐づく
    // LAZY: 必要になるまでuser本体は読み込まない
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // 外部キー列名
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 勤務日（yyyy-MM-dd）
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    // 出勤時刻（未出勤ならnull）
    @Column(name = "start_time")
    private LocalDateTime startTime;

    // 退勤時刻（退勤前ならnull）
    @Column(name = "end_time")
    private LocalDateTime endTime;

    // Enumを文字列として保存（NOT_STARTED / WORKING / FINISHED）
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    // 監査用の作成日時
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 監査用の更新日時
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // INSERT前に自動実行されるコールバック
    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // UPDATE前に自動実行されるコールバック
    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 以下はアクセサ（getter/setter）
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
}
```

理解ポイント（20分）:
- `AttendanceStatus`:
  - 勤怠状態を列挙型で固定（`NOT_STARTED / WORKING / FINISHED`）
- `User`:
  - 研修用ユーザー情報の最小モデル
  - `username` は重複禁止（`unique = true`）
- `Attendance`:
  - 勤怠の中心データ
  - `@UniqueConstraint(user_id, work_date)` で「1ユーザー1日1件」をDB制約化
  - `@PrePersist / @PreUpdate` で作成/更新日時を自動設定
- よくあるミス:
  - `@Entity` / `@Table` の付け忘れ
  - `uniqueConstraints` のカラム名と `@JoinColumn` / `@Column` の不一致

---

## 7. Repositoryを作成

### 7-1. `UserRepository`
作成ファイル: `~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/repository/UserRepository.java`

```java
// Repositoryインターフェースを置くパッケージ
package com.shinesoft.attendance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinesoft.attendance.domain.User;

// UserテーブルのDB操作窓口
// JpaRepository<エンティティ型, 主キー型>
public interface UserRepository extends JpaRepository<User, Long> {
    // ユーザー名で1件検索（存在しない場合があるのでOptional）
    Optional<User> findByUsername(String username);
}
```

### 7-2. `AttendanceRepository`
作成ファイル: `~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java`

```java
// Repositoryインターフェースを置くパッケージ
package com.shinesoft.attendance.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinesoft.attendance.domain.Attendance;

// AttendanceテーブルのDB操作窓口
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // userId + workDate で当日レコードを検索
    // メソッド名からSQL相当の処理が自動生成される
    Optional<Attendance> findByUserIdAndWorkDate(Long userId, LocalDate workDate);
}
```

理解ポイント（10分）:
- このファイルの役割:
  - DB操作（検索・保存）の窓口
- 重要ポイント:
  - `JpaRepository` 継承でCRUDの基本機能を自動取得
  - `findByUserIdAndWorkDate(...)` はメソッド名からSQL相当が生成される
- よくあるミス:
  - メソッド名のプロパティ名をEntityと不一致にする

---

## 8. 例外とServiceを作成

### 8-1. `BusinessException`
作成ファイル: `~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/exception/BusinessException.java`

```java
// 業務ルール違反を表す独自例外
// 例: 二重出勤、未出勤退勤など
package com.shinesoft.attendance.exception;

public class BusinessException extends RuntimeException {
    // 画面に表示するメッセージを受け取る
    public BusinessException(String message) {
        super(message);
    }
}
```

### 8-2. `AttendanceService`
作成ファイル: `~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/service/AttendanceService.java`

```java
// Serviceクラスを置くパッケージ
package com.shinesoft.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shinesoft.attendance.domain.Attendance;
import com.shinesoft.attendance.domain.AttendanceStatus;
import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.repository.AttendanceRepository;
import com.shinesoft.attendance.repository.UserRepository;

// 業務ロジックを担当するクラス（Controllerから分離）
@Service
public class AttendanceService {
    // 操作履歴をログ出力するためのロガー
    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    // DBアクセス窓口（依存注入される）
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    // コンストラクタインジェクション
    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    // 当日の勤怠を取得（無ければOptional.empty）
    public Optional<Attendance> findToday(Long userId) {
        return attendanceRepository.findByUserIdAndWorkDate(userId, LocalDate.now());
    }

    // 出勤処理（業務ルール: 同日二重出勤禁止）
    public Attendance clockIn(Long userId) {
        // 1. 今日の日付を取得
        LocalDate today = LocalDate.now();
        // 2. 既存データがあるか確認
        Optional<Attendance> existing = attendanceRepository.findByUserIdAndWorkDate(userId, today);
        if (existing.isPresent()) {
            // 3. すでに出勤済みなら業務例外
            throw new BusinessException("すでに出勤済みです");
        }

        // 4. ユーザー取得（研修用固定ユーザーがいない場合はシステム例外）
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("研修ユーザーが存在しません"));

        // 5. 新しい勤怠レコードを作成
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setWorkDate(today);
        // 出勤時刻は「現在時刻」
        attendance.setStartTime(LocalDateTime.now());
        // 状態は「出勤中」
        attendance.setStatus(AttendanceStatus.WORKING);

        // 6. DBへ保存
        Attendance saved = attendanceRepository.save(attendance);
        // 7. 監査しやすいようにログ出力
        log.info("clock-in userId={} date={} time={}", userId, saved.getWorkDate(), saved.getStartTime());
        return saved;
    }
}
```

理解ポイント（20分）:
- このファイルの役割:
  - 業務ルールをまとめる層（Controllerを薄くする）
- 重要ポイント:
  - `clockIn` で二重出勤チェック（同日データがあれば例外）
  - `BusinessException` で画面表示用のエラーを返す
  - `log.info(...)` で操作ログを残す
- 依存関係:
  - `Controller -> Service -> Repository -> DB`
- よくあるミス:
  - Controller側で業務判定を書いてしまい責務が混ざる

---

## 9. 初期データ投入（固定ユーザー）
作成ファイル: `~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/config/DataSeeder.java`

```java
// 設定クラスを置くパッケージ
package com.shinesoft.attendance.config;

// アプリ起動時に1回だけ実行される処理を作るためのインターフェース
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.repository.UserRepository;

// Springの設定クラス
@Configuration
public class DataSeeder {

    // Beanとして登録され、起動時に自動実行される
    @Bean
    CommandLineRunner seedUser(UserRepository userRepository) {
        // argsは起動引数（今回は未使用）
        return args -> {
            // user1 がいない時だけ作成（重複投入を防ぐ）
            if (userRepository.findByUsername("user1").isEmpty()) {
                User user = new User();
                user.setUsername("user1");
                userRepository.save(user);
            }
        };
    }
}
```

理解ポイント（5分）:
- このファイルの役割:
  - アプリ起動時に研修用ユーザー（`user1`）を投入する
- 重要ポイント:
  - `findByUsername("user1")` で重複作成を防止
- よくあるミス:
  - Seederを作っていないため `研修ユーザーが存在しません` で失敗

---

## 10. Controllerを作成
作成ファイル: `~/order-management-springboot/stages/day2/src/main/java/com/shinesoft/attendance/web/HomeController.java`

```java
// 画面（Web）層のクラスを置くパッケージ
package com.shinesoft.attendance.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shinesoft.attendance.domain.Attendance;
import com.shinesoft.attendance.domain.AttendanceStatus;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.service.AttendanceService;

// 画面表示を担当するController
@Controller
public class HomeController {
    // Day2では固定ユーザーで進める（ログイン機能はDay5で実装）
    private static final Long TRAINING_USER_ID = 1L;
    // 日時表示フォーマット
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 業務ロジックはServiceに委譲
    private final AttendanceService attendanceService;

    public HomeController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // トップ画面表示
    @GetMapping("/")
    public String index(Model model,
                        // redirect:/ から渡される成功メッセージ（任意）
                        @RequestParam(value = "message", required = false) String message,
                        // redirect:/ から渡されるエラーメッセージ（任意）
                        @RequestParam(value = "error", required = false) String error) {
        // 当日の勤怠を取得
        Optional<Attendance> today = attendanceService.findToday(TRAINING_USER_ID);

        // 画面表示に必要な値をModelへ詰める
        model.addAttribute("workDate", LocalDate.now());
        model.addAttribute("statusLabel", toStatusLabel(today));
        model.addAttribute("startTime", format(today.map(Attendance::getStartTime).orElse(null)));
        model.addAttribute("endTime", format(today.map(Attendance::getEndTime).orElse(null)));
        // trueの時だけ「出勤」ボタンを表示
        model.addAttribute("canClockIn", today.isEmpty());
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        // templates/index.html を表示
        return "index";
    }

    // 出勤ボタン押下時の処理
    @PostMapping("/clock-in")
    public String clockIn(RedirectAttributes redirectAttributes) {
        try {
            // 出勤処理（Serviceで業務判定）
            attendanceService.clockIn(TRAINING_USER_ID);
            // 画面へ成功メッセージを渡してリダイレクト
            redirectAttributes.addAttribute("message", "出勤を記録しました");
        } catch (BusinessException e) {
            // 業務エラー（例: 二重出勤）はエラーメッセージとして返す
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        // PRGパターン: POST後はGETへリダイレクト
        return "redirect:/";
    }

    // 勤怠状態(Enum)を画面表示用文字列へ変換
    private String toStatusLabel(Optional<Attendance> today) {
        if (today.isEmpty()) {
            return "未出勤";
        }
        AttendanceStatus status = today.get().getStatus();
        if (status == AttendanceStatus.WORKING) {
            return "出勤中";
        }
        if (status == AttendanceStatus.FINISHED) {
            return "退勤済み";
        }
        return "未出勤";
    }

    // 日時表示の共通フォーマッタ
    private String format(LocalDateTime value) {
        if (value == null) {
            // 値が無い時は "-" 表示
            return "-";
        }
        return value.format(FMT);
    }
}
```

理解ポイント（20分）:
- このファイルの役割:
  - 画面入力を受け取り、表示データを作ってテンプレートへ渡す
- 重要ポイント:
  - `@GetMapping("/")` でトップ画面表示
  - `@PostMapping("/clock-in")` で出勤処理
  - `RedirectAttributes` でメッセージを画面に戻す
- 画面制御:
  - `canClockIn` により出勤ボタンの表示/非表示を切り替える
- よくあるミス:
  - `@RequestParam(..., required = false)` を外して初回表示でエラー

---

## 11. テンプレートを作成
作成ファイル: `~/order-management-springboot/stages/day2/src/main/resources/templates/index.html`

```html
<!-- HTML5の文書宣言 -->
<!doctype html>
<!-- Thymeleafを使うため xmlns:th を宣言 -->
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
<head>
  <!-- 文字コード -->
  <meta charset="utf-8" />
  <!-- スマホ表示用の基本設定 -->
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>勤怠管理（Day2）</title>
  <!-- static/styles.css を読み込む -->
  <link rel="stylesheet" th:href="@{/styles.css}" />
</head>
<body>
  <!-- 画面全体のコンテナ -->
  <div class="container">
    <header>
      <h1>勤怠管理システム（MVP）</h1>
      <p class="subtitle">Day2: 出勤機能（DB保存）</p>
    </header>

    <!-- messageがある時だけ成功通知を表示 -->
    <div th:if="${message}" class="alert alert-info" th:text="${message}"></div>
    <!-- errorがある時だけエラー通知を表示 -->
    <div th:if="${error}" class="alert alert-error" th:text="${error}"></div>

    <!-- 今日の勤怠パネル -->
    <section class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <!-- Controllerから受け取った statusLabel を表示 -->
        <span class="status-badge" th:text="${statusLabel}">未出勤</span>
      </div>
      <!-- 値がある時はその値、無い時はフォールバック文字が表示される -->
      <p>日付: <span th:text="${workDate}">2026-02-05</span></p>
      <p>出勤時刻: <span th:text="${startTime}">-</span></p>
      <p>退勤時刻: <span th:text="${endTime}">-</span></p>

      <!-- canClockIn=true の時だけ出勤フォームを表示 -->
      <form th:if="${canClockIn}" method="post" th:action="@{/clock-in}">
        <button type="submit">出勤</button>
      </form>
      <!-- canClockIn=false の時は案内文を表示 -->
      <p th:if="${!canClockIn}" class="muted">本日はすでに出勤済みです。</p>
    </section>
  </div>
</body>
</html>
```

理解ポイント（15分）:
- このファイルの役割:
  - Controllerから渡された値をHTMLに描画する
- 重要ポイント:
  - `th:if` でメッセージ表示制御
  - `th:text` で値を埋め込む
  - `th:action="@{/clock-in}"` でPOST先を指定
- 変更して試す:
  - `subtitle` の文言を変更し、画面反映を確認
- よくあるミス:
  - `${canClockIn}` のキー名ミスでボタン表示が崩れる

---

## 12. CSSを作成
作成ファイル: `~/order-management-springboot/stages/day2/src/main/resources/static/styles.css`

```css
/* 画面全体で使う色変数 */
:root {
  /* 背景色 */
  --bg: #f6f6f2;
  /* パネル背景 */
  --panel: #ffffff;
  /* 基本文字色 */
  --text: #202124;
  /* 補助文字色 */
  --muted: #6b7280;
  /* 強調色（ボタン） */
  --accent: #0ea5e9;
  /* 枠線色 */
  --border: #e5e7eb;
}

/* 幅計算にpadding/borderを含める */
* { box-sizing: border-box; }

/* ページ全体の基本スタイル */
body {
  /* 既定余白をリセット */
  margin: 0;
  font-family: "Segoe UI", Tahoma, sans-serif;
  color: var(--text);
  background: var(--bg);
}

/* 中央寄せコンテナ */
.container {
  max-width: 920px;
  margin: 0 auto;
  padding: 24px;
}

/* ヘッダー余白 */
header { margin-bottom: 16px; }

/* 見出し余白 */
h1 { margin: 0 0 4px; }

/* サブタイトル色 */
.subtitle {
  color: var(--muted);
  margin: 0 0 16px;
}

/* カード風パネル */
.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

/* 見出し行の左右配置 */
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* ステータス表示バッジ */
.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  background: #e0f2fe;
  color: #0369a1;
  font-size: 12px;
}

/* 出勤ボタン */
button {
  padding: 8px 12px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

/* ホバーで視覚フィードバック */
button:hover { opacity: 0.9; }

/* 補助テキスト色 */
.muted { color: var(--muted); }

/* 通知メッセージ共通 */
.alert {
  padding: 10px 12px;
  border-radius: 6px;
  margin-bottom: 12px;
}

/* エラー通知 */
.alert-error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

/* 成功/情報通知 */
.alert-info {
  background: #e0f2fe;
  color: #075985;
  border: 1px solid #bae6fd;
}
```

理解ポイント（10分）:
- このファイルの役割:
  - Day2画面の見た目を整える
- 重要ポイント:
  - `.alert-error` / `.alert-info` で結果表示の見分けを付ける
  - `.status-badge` で状態を視覚化
- 変更して試す:
  - `.status-badge` の背景色を変更して違いを確認
- よくあるミス:
  - HTML側のクラス名とCSS側のクラス名の不一致

---

## 13. 起動
```bash
cd ~/order-management-springboot/stages/day2
mvn spring-boot:run
```

---

## 14. 動作確認
1. ブラウザで `http://localhost:8080/` を開く
2. 初期表示で状態が「未出勤」であることを確認
3. 「出勤」ボタンを押す
4. 状態が「出勤中」に変わり、出勤時刻が表示されることを確認
5. 再度「出勤」しようとして、`すでに出勤済みです` が表示されることを確認

---

## 15. コード確認ポイント
- `Attendance` の `@Table(uniqueConstraints=...)` で「1日1件」を DB 制約にしている
- `AttendanceService#clockIn` で業務ルール（二重出勤不可）を実装している
- `HomeController` は画面入出力に集中し、業務ロジックは Service に寄せている
- `DataSeeder` で固定ユーザー `user1` を初期投入している

---

## 16. つまずきポイント
- `Table "USERS" not found`:
  - 起動直後にアクセスしすぎると発生することがある。起動ログの完了を待つ
- `研修ユーザーが存在しません`:
  - `DataSeeder` が正しく作成されているか確認
- `mvn` が通らない:
  - Day0 の環境セットアップに戻って `JAVA_HOME` / `Path` を確認

---

## 17. 時間割目安
- 午前: JPA/H2導入（60分）+ Entity/Repository作成（90分）
- 午後: Service/Controller/画面実装（90分）+ 動作確認/振り返り（30分）
