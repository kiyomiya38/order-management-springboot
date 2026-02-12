# Day3（7/13）退勤機能と業務ルール実装（Day2から拡張）

## 目的（Day3でできるようになること）
- 退勤 (`POST /clock-out`) を実装できる
- 状態遷移（未出勤 -> 出勤中 -> 退勤済み）をコードで説明できる
- 業務ルール違反時に画面へエラーメッセージを表示できる
- 出勤/退勤時の INFO ログを確認できる

## 前提
- Day2 を完了している
- `stages/day2` のアプリが起動・動作確認できている

## Day3で作るもの
- 画面: `/`
- 機能:
  - 出勤（Day2の継続）
  - 退勤（新規）
  - ルール違反表示
    - 未出勤で退勤不可
    - 退勤済みで再退勤不可

---

## 0. 事前確認
```bash
java -version
mvn -version
git --version
```

---

## 1. 作業フォルダを準備（Day2を複製）
Day3 は Day2 を土台に進めます。`stages/day3` を作成して Day2 の内容をコピーしてください。

```bash
mkdir -p stages/day3
cp -r stages/day2/* stages/day3/
cd stages/day3
```

以降の `作成ファイル` は、リポジトリルート (`order-management-springboot`) からのパスで表記します。  
例: `stages/day3/src/main/java/...`

---

## 2. `AttendanceService` を編集（退勤ロジック追加）
作成ファイル: `stages/day3/src/main/java/com/shinesoft/attendance/service/AttendanceService.java`

全文を以下に置き換えてください。

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

    public Attendance clockOut(Long userId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserIdAndWorkDate(userId, today)
            .orElseThrow(() -> new BusinessException("退勤するには先に出勤してください"));

        if (attendance.getStatus() == AttendanceStatus.FINISHED) {
            throw new BusinessException("すでに退勤済みです");
        }
        if (attendance.getStatus() != AttendanceStatus.WORKING) {
            throw new BusinessException("退勤するには先に出勤してください");
        }

        attendance.setEndTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.FINISHED);
        Attendance saved = attendanceRepository.save(attendance);
        log.info("clock-out userId={} date={} time={}", userId, saved.getWorkDate(), saved.getEndTime());
        return saved;
    }
}
```

ポイント:
- `clockOut` は「当日レコードが無ければエラー」
- `FINISHED` の再退勤を禁止
- 成功時は `endTime` と `status=FINISHED` を更新

---

## 3. `HomeController` を編集（退勤エンドポイント追加）
作成ファイル: `stages/day3/src/main/java/com/shinesoft/attendance/web/HomeController.java`

全文を以下に置き換えてください。

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
    public String index(Model model, String message, String error) {
        Optional<Attendance> today = attendanceService.findToday(TRAINING_USER_ID);

        model.addAttribute("workDate", LocalDate.now());
        model.addAttribute("statusLabel", toStatusLabel(today));
        model.addAttribute("startTime", format(today.map(Attendance::getStartTime).orElse(null)));
        model.addAttribute("endTime", format(today.map(Attendance::getEndTime).orElse(null)));
        model.addAttribute("canClockIn", today.isEmpty());
        model.addAttribute("canClockOut", today.isPresent() && today.get().getStatus() == AttendanceStatus.WORKING);
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

    @PostMapping("/clock-out")
    public String clockOut(RedirectAttributes redirectAttributes) {
        try {
            attendanceService.clockOut(TRAINING_USER_ID);
            redirectAttributes.addAttribute("message", "退勤を記録しました");
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

ポイント:
- `@PostMapping("/clock-out")` を追加
- 画面表示のため `canClockOut` を Model に追加

---

## 4. `index.html` を編集（退勤ボタン表示）
作成ファイル: `stages/day3/src/main/resources/templates/index.html`

全文を以下に置き換えてください。

```html
<!doctype html>
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>勤怠管理（Day3）</title>
  <link rel="stylesheet" th:href="@{/styles.css}" />
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理システム（MVP）</h1>
      <p class="subtitle">Day3: 退勤機能と業務ルール</p>
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

      <div class="row">
        <form th:if="${canClockIn}" method="post" th:action="@{/clock-in}">
          <button type="submit">出勤</button>
        </form>

        <form th:if="${canClockOut}" method="post" th:action="@{/clock-out}">
          <button type="submit" class="danger">退勤</button>
        </form>
      </div>

      <p th:if="${!canClockIn and !canClockOut}" class="muted">
        本日の勤怠は確定済みです（退勤済み）。
      </p>
    </section>
  </div>
</body>
</html>
```

---

## 5. `styles.css` を編集（退勤ボタン用クラス）
作成ファイル: `stages/day3/src/main/resources/static/styles.css`

`styles.css` に以下があることを確認してください（無ければ追加）。

```css
.row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.danger {
  background: #ef4444;
}
```

---

## 6. 起動
```bash
cd stages/day3
mvn spring-boot:run
```

---

## 7. 動作確認（必須）
1. `http://localhost:8080/` を開く
2. 初期状態で「未出勤」を確認
3. 「退勤」URLへ直接 POST（未出勤時）するとエラーになることを確認

```bash
curl -X POST http://localhost:8080/clock-out -i
```

4. 画面で「出勤」ボタンを押す（状態: 出勤中）
5. 画面で「退勤」ボタンを押す（状態: 退勤済み）
6. 再度 `curl -X POST http://localhost:8080/clock-out -i` を実行し、`すでに退勤済みです` を確認

---

## 8. ログ確認（必須）
起動ターミナルで以下のような INFO ログが出ることを確認してください。

- `clock-in userId=1 ...`
- `clock-out userId=1 ...`

---

## 9. コード確認ポイント
- `AttendanceService#clockOut` で業務ルールを集約している
- `HomeController` は画面入出力に集中し、`BusinessException` を画面メッセージに変換している
- `index.html` は `canClockIn / canClockOut` でボタンの表示を制御している

---

## 10. つまずきポイント
- 退勤ボタンが出ない:
  - `canClockOut` の条件式が `WORKING` 判定になっているか確認
- `Request method 'POST' is required`:
  - 退勤は `GET` ではなく `POST /clock-out`
- 何度も同じ結果になる:
  - 前日の状態ではなく「当日レコード」を見ているか (`LocalDate.now()`)

---

## 11. 時間割目安
- 午前: Day2コードの複製と差分実装（90分）
- 午後: 業務ルール検証とログ確認（90分）+ まとめ（30分）
