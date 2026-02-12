# Day4（7/14）勤怠一覧画面とH2確認（Day3から拡張）

## 目的（Day4でできるようになること）
- 勤怠一覧ページ（`GET /attendances`）を実装できる
- Repository -> Service -> Controller で一覧データを取得できる
- H2コンソールで登録済みデータを確認できる

## 前提
- Day3 を完了している
- `~/order-management-springboot/stages/day3` が起動し、出勤/退勤の動作確認が終わっている

## Day4で作るもの
- 画面:
  - `/`（トップ）
  - `/attendances`（勤怠一覧）
- 機能:
  - 勤怠履歴の降順表示（日付, 出勤時刻, 退勤時刻, 状態）
  - トップから一覧への遷移リンク

---

## 0. 事前確認
```bash
java -version
mvn -version
git --version
```

---

## 1. 作業フォルダを準備（Day3を複製）
Day4 は Day3 を土台に進めます。

```bash
mkdir -p ~/order-management-springboot/stages/day4
cp -r ~/order-management-springboot/stages/day3/* ~/order-management-springboot/stages/day4/
cd ~/order-management-springboot/stages/day4
```

以降の `作成ファイル` は、`~/order-management-springboot` からのフルパスで表記します。  
例: `~/order-management-springboot/stages/day4/src/main/java/...`

---

## 2. `AttendanceRepository` を編集（一覧取得クエリ追加）
作成ファイル: `~/order-management-springboot/stages/day4/src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java`

全文を以下に置き換えてください。

```java
package com.shinesoft.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinesoft.attendance.domain.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUserIdAndWorkDate(Long userId, LocalDate workDate);

    List<Attendance> findByUserIdOrderByWorkDateDescStartTimeDesc(Long userId);
}
```

ポイント:
- `findByUserIdOrderByWorkDateDescStartTimeDesc` で履歴を降順表示する

---

## 3. `AttendanceService` を編集（一覧取得メソッド追加）
作成ファイル: `~/order-management-springboot/stages/day4/src/main/java/com/shinesoft/attendance/service/AttendanceService.java`

全文を以下に置き換えてください。

```java
package com.shinesoft.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    public List<Attendance> findAttendances(Long userId) {
        return attendanceRepository.findByUserIdOrderByWorkDateDescStartTimeDesc(userId);
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

---

## 4. 一覧用Controllerを新規作成
作成ファイル: `~/order-management-springboot/stages/day4/src/main/java/com/shinesoft/attendance/web/AttendanceController.java`

新規作成してください。

```java
package com.shinesoft.attendance.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shinesoft.attendance.domain.Attendance;
import com.shinesoft.attendance.service.AttendanceService;

@Controller
public class AttendanceController {
    private static final Long TRAINING_USER_ID = 1L;

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/attendances")
    public String list(Model model) {
        List<Attendance> rows = attendanceService.findAttendances(TRAINING_USER_ID);
        model.addAttribute("rows", rows);
        return "attendances";
    }
}
```

---

## 5. `HomeController` を編集（一覧画面リンク用）
作成ファイル: `~/order-management-springboot/stages/day4/src/main/java/com/shinesoft/attendance/web/HomeController.java`

`HomeController` は Day3 のままで動作します。  
このステップでは変更不要です。

---

## 6. 一覧テンプレートを新規作成
作成ファイル: `~/order-management-springboot/stages/day4/src/main/resources/templates/attendances.html`

新規作成してください。

```html
<!doctype html>
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>勤怠一覧（Day4）</title>
  <link rel="stylesheet" th:href="@{/styles.css}" />
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠履歴一覧</h1>
      <p class="subtitle">Day4: 一覧表示とDB確認</p>
      <p><a href="/">トップへ戻る</a></p>
    </header>

    <section class="panel">
      <table>
        <thead>
          <tr>
            <th>日付</th>
            <th>出勤時刻</th>
            <th>退勤時刻</th>
            <th>状態</th>
          </tr>
        </thead>
        <tbody>
          <tr th:if="${#lists.isEmpty(rows)}">
            <td colspan="4" class="muted">データがありません</td>
          </tr>
          <tr th:each="r : ${rows}">
            <td th:text="${r.workDate}">2026-02-05</td>
            <td th:text="${r.startTime != null ? #temporals.format(r.startTime, 'yyyy-MM-dd HH:mm:ss') : '-'}">-</td>
            <td th:text="${r.endTime != null ? #temporals.format(r.endTime, 'yyyy-MM-dd HH:mm:ss') : '-'}">-</td>
            <td th:text="${r.status == T(com.shinesoft.attendance.domain.AttendanceStatus).WORKING ? '出勤中' : (r.status == T(com.shinesoft.attendance.domain.AttendanceStatus).FINISHED ? '退勤済み' : '未出勤')}">未出勤</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</body>
</html>
```

---

## 7. `index.html` を編集（一覧リンク追加）
作成ファイル: `~/order-management-springboot/stages/day4/src/main/resources/templates/index.html`

`<section class="panel">` の末尾付近に以下の1行を追加してください。

```html
<p><a href="/attendances">勤怠一覧を見る</a></p>
```

---

## 8. `styles.css` を確認
作成ファイル: `~/order-management-springboot/stages/day4/src/main/resources/static/styles.css`

Day3 の CSS でそのまま表示可能です。  
追加変更は必須ではありません。

---

## 9. 起動
```bash
cd ~/order-management-springboot/stages/day4
mvn spring-boot:run
```

---

## 10. 動作確認（必須）
1. `http://localhost:8080/` を開く
2. 出勤 -> 退勤を1回実施
3. `勤怠一覧を見る` を押す
4. `http://localhost:8080/attendances` で履歴行が表示されることを確認

---

## 11. H2コンソール確認（必須）
1. `http://localhost:8080/h2-console` を開く
2. JDBC URL に以下を入力

```text
jdbc:h2:mem:attendance
```

3. ユーザー名 `sa`、パスワード空で接続
4. SQL実行:

```sql
SELECT * FROM ATTENDANCES ORDER BY WORK_DATE DESC;
```

5. 画面の一覧と DB の値が一致することを確認

---

## 12. コード確認ポイント
- `AttendanceRepository` でソート済み取得メソッドを定義している
- `AttendanceService` で一覧取得処理を集約している
- `AttendanceController` は一覧画面表示だけを担当している
- Thymeleaf テーブル描画で `rows` を反復表示している

---

## 13. つまずきポイント
- `TemplateInputException: attendances`:
  - `attendances.html` の配置パスを確認
- 一覧が空:
  - 先にトップ画面で出勤/退勤を実行してレコードを作成
- H2に接続できない:
  - JDBC URL が `jdbc:h2:mem:attendance` になっているか確認

---

## 14. 時間割目安
- 午前: Day3コード複製 + 一覧機能追加（90分）
- 午後: H2確認 + コード読解 + まとめ（120分）
