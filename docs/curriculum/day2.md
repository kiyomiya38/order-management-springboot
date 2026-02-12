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
Day2 は `stages/day2` に自分でコードを作成します。

```bash
mkdir -p stages/day2
cd stages/day2
```

以降の `作成ファイル` は、リポジトリルート (`order-management-springboot`) からのパスで表記します。  
例: `stages/day2/pom.xml`（絶対パス例: `C:\Users\<ユーザー名>\order-management-springboot\stages\day2\pom.xml`）

### VS Codeでフォルダを開く（GUI）
1. VS Code を起動
2. `ファイル` -> `フォルダーを開く`
3. `.../order-management-springboot/stages/day2` を選択

---

## 2. ディレクトリ構成を作成
```bash
mkdir -p src/main/java/com/shinesoft/attendance
mkdir -p src/main/java/com/shinesoft/attendance/web
mkdir -p src/main/java/com/shinesoft/attendance/service
mkdir -p src/main/java/com/shinesoft/attendance/domain
mkdir -p src/main/java/com/shinesoft/attendance/repository
mkdir -p src/main/java/com/shinesoft/attendance/exception
mkdir -p src/main/java/com/shinesoft/attendance/config
mkdir -p src/main/resources/templates
mkdir -p src/main/resources/static
```

---

## 3. `pom.xml` を作成（Maven設定）
作成ファイル: `stages/day2/pom.xml`

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
  <description>Attendance Management MVP Day2</description>

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
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>runtime</scope>
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

## 4. `application.yml` を作成
作成ファイル: `stages/day2/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: ${APP_NAME:attendance-management}
  datasource:
    url: jdbc:h2:mem:attendance;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    open-in-view: false
  thymeleaf:
    cache: false
  h2:
    console:
      enabled: true
      path: /h2-console

server:
  port: ${SERVER_PORT:8080}

logging:
  level:
    root: ${LOG_LEVEL:INFO}
```

---

## 5. Applicationクラス
作成ファイル: `stages/day2/src/main/java/com/shinesoft/attendance/AttendanceManagementApplication.java`

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

---

## 6. Domain（Entity / Enum）を作成

### 6-1. 勤怠ステータス
作成ファイル: `stages/day2/src/main/java/com/shinesoft/attendance/domain/AttendanceStatus.java`

```java
package com.shinesoft.attendance.domain;

public enum AttendanceStatus {
    NOT_STARTED,
    WORKING,
    FINISHED
}
```

### 6-2. ユーザー
作成ファイル: `stages/day2/src/main/java/com/shinesoft/attendance/domain/User.java`

```java
package com.shinesoft.attendance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

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
作成ファイル: `stages/day2/src/main/java/com/shinesoft/attendance/domain/Attendance.java`

```java
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

@Entity
@Table(
    name = "attendances",
    uniqueConstraints = @UniqueConstraint(name = "uk_attendance_user_date", columnNames = {"user_id", "work_date"})
)
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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

---

## 7. Repositoryを作成

### 7-1. `UserRepository`
作成ファイル: `stages/day2/src/main/java/com/shinesoft/attendance/repository/UserRepository.java`

```java
package com.shinesoft.attendance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinesoft.attendance.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

### 7-2. `AttendanceRepository`
作成ファイル: `stages/day2/src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java`

```java
package com.shinesoft.attendance.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinesoft.attendance.domain.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUserIdAndWorkDate(Long userId, LocalDate workDate);
}
```

---

## 8. 例外とServiceを作成

### 8-1. `BusinessException`
作成ファイル: `stages/day2/src/main/java/com/shinesoft/attendance/exception/BusinessException.java`

```java
package com.shinesoft.attendance.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

### 8-2. `AttendanceService`
作成ファイル: `stages/day2/src/main/java/com/shinesoft/attendance/service/AttendanceService.java`

```java
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

@Service
public class AttendanceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    public Optional<Attendance> findToday(Long userId) {
        return attendanceRepository.findByUserIdAndWorkDate(userId, LocalDate.now());
    }

    public Attendance clockIn(Long userId) {
        LocalDate today = LocalDate.now();
        Optional<Attendance> existing = attendanceRepository.findByUserIdAndWorkDate(userId, today);
        if (existing.isPresent()) {
            throw new BusinessException("すでに出勤済みです");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("研修ユーザーが存在しません"));

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setWorkDate(today);
        attendance.setStartTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.WORKING);

        Attendance saved = attendanceRepository.save(attendance);
        log.info("clock-in userId={} date={} time={}", userId, saved.getWorkDate(), saved.getStartTime());
        return saved;
    }
}
```

---

## 9. 初期データ投入（固定ユーザー）
作成ファイル: `stages/day2/src/main/java/com/shinesoft/attendance/config/DataSeeder.java`

```java
package com.shinesoft.attendance.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.repository.UserRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUser(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("user1").isEmpty()) {
                User user = new User();
                user.setUsername("user1");
                userRepository.save(user);
            }
        };
    }
}
```

---

## 10. Controllerを作成
作成ファイル: `stages/day2/src/main/java/com/shinesoft/attendance/web/HomeController.java`

```java
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

@Controller
public class HomeController {
    private static final Long TRAINING_USER_ID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AttendanceService attendanceService;

    public HomeController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(value = "message", required = false) String message,
                        @RequestParam(value = "error", required = false) String error) {
        Optional<Attendance> today = attendanceService.findToday(TRAINING_USER_ID);

        model.addAttribute("workDate", LocalDate.now());
        model.addAttribute("statusLabel", toStatusLabel(today));
        model.addAttribute("startTime", format(today.map(Attendance::getStartTime).orElse(null)));
        model.addAttribute("endTime", format(today.map(Attendance::getEndTime).orElse(null)));
        model.addAttribute("canClockIn", today.isEmpty());
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        return "index";
    }

    @PostMapping("/clock-in")
    public String clockIn(RedirectAttributes redirectAttributes) {
        try {
            attendanceService.clockIn(TRAINING_USER_ID);
            redirectAttributes.addAttribute("message", "出勤を記録しました");
        } catch (BusinessException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

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

    private String format(LocalDateTime value) {
        if (value == null) {
            return "-";
        }
        return value.format(FMT);
    }
}
```

---

## 11. テンプレートを作成
作成ファイル: `stages/day2/src/main/resources/templates/index.html`

```html
<!doctype html>
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>勤怠管理（Day2）</title>
  <link rel="stylesheet" th:href="@{/styles.css}" />
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理システム（MVP）</h1>
      <p class="subtitle">Day2: 出勤機能（DB保存）</p>
    </header>

    <div th:if="${message}" class="alert alert-info" th:text="${message}"></div>
    <div th:if="${error}" class="alert alert-error" th:text="${error}"></div>

    <section class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <span class="status-badge" th:text="${statusLabel}">未出勤</span>
      </div>
      <p>日付: <span th:text="${workDate}">2026-02-05</span></p>
      <p>出勤時刻: <span th:text="${startTime}">-</span></p>
      <p>退勤時刻: <span th:text="${endTime}">-</span></p>

      <form th:if="${canClockIn}" method="post" th:action="@{/clock-in}">
        <button type="submit">出勤</button>
      </form>
      <p th:if="${!canClockIn}" class="muted">本日はすでに出勤済みです。</p>
    </section>
  </div>
</body>
</html>
```

---

## 12. CSSを作成
作成ファイル: `stages/day2/src/main/resources/static/styles.css`

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

header { margin-bottom: 16px; }

h1 { margin: 0 0 4px; }

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

button {
  padding: 8px 12px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

button:hover { opacity: 0.9; }

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

## 13. 起動
```bash
cd stages/day2
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
