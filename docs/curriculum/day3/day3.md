# Day3（7/13）退勤機能と業務ルール実装（Day2から拡張）

## 目的（Day3でできるようになること）
- 退勤 (`POST /clock-out`) を実装できる
- 状態遷移（未出勤 -> 出勤中 -> 退勤済み）をコードで説明できる
- 業務ルール違反時に画面へエラーメッセージを表示できる
- 出勤/退勤時の INFO ログを確認できる

## 前提
- Day2 を完了している
- `~/order-management-springboot/stages/day2` のアプリが起動・動作確認できている

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
Day3 は Day2 を土台に進めます。`~/order-management-springboot/stages/day3` を作成して Day2 の内容をコピーしてください。

```bash
mkdir -p ~/order-management-springboot/stages/day3
cp -r ~/order-management-springboot/stages/day2/* ~/order-management-springboot/stages/day3/
cd ~/order-management-springboot/stages/day3
```

以降の `作成ファイル` は、`~/order-management-springboot` からのフルパスで表記します。  
例: `~/order-management-springboot/stages/day3/src/main/java/...`

---

## 2. `AttendanceService` を編集（退勤ロジック追加）
作成ファイル: `~/order-management-springboot/stages/day3/src/main/java/com/shinesoft/attendance/service/AttendanceService.java`

全文を以下に置き換えてください。

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
    // 操作履歴を出力するロガー
    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    // DBアクセス層（依存注入）
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

    // 出勤処理（Day2から継続）
    public Attendance clockIn(Long userId) {
        // 1. 今日の日付を取得
        LocalDate today = LocalDate.now();
        // 2. 同日レコードが既にあるか確認
        Optional<Attendance> existing = attendanceRepository.findByUserIdAndWorkDate(userId, today);
        if (existing.isPresent()) {
            // 3. 既存レコードがあれば二重出勤として業務エラー
            throw new BusinessException("すでに出勤済みです");
        }

        // 4. 対象ユーザー取得（存在しなければシステムエラー）
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("研修ユーザーが存在しません"));

        // 5. 新規勤怠レコード作成
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setWorkDate(today);
        // 出勤時刻は現在時刻
        attendance.setStartTime(LocalDateTime.now());
        // 出勤後の状態
        attendance.setStatus(AttendanceStatus.WORKING);

        // 6. 保存してログ出力
        Attendance saved = attendanceRepository.save(attendance);
        log.info("clock-in userId={} date={} time={}", userId, saved.getWorkDate(), saved.getStartTime());
        return saved;
    }

    // 退勤処理（Day3で追加）
    public Attendance clockOut(Long userId) {
        // 1. 今日の日付で当日レコードを取得
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserIdAndWorkDate(userId, today)
            // レコードが無い = まだ出勤していない
            .orElseThrow(() -> new BusinessException("退勤するには先に出勤してください"));

        // 2. すでに退勤済みなら再退勤は禁止
        if (attendance.getStatus() == AttendanceStatus.FINISHED) {
            throw new BusinessException("すでに退勤済みです");
        }
        // 3. 出勤中以外の状態では退勤不可
        if (attendance.getStatus() != AttendanceStatus.WORKING) {
            throw new BusinessException("退勤するには先に出勤してください");
        }

        // 4. 退勤時刻と状態を更新
        attendance.setEndTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.FINISHED);

        // 5. 保存してログ出力
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

理解ポイント（20分）:
- この変更の目的:
  - Day2の出勤のみ機能に「退勤」と状態遷移を追加する
- 重要ポイント:
  - `findByUserIdAndWorkDate(...)` で当日レコードを取得
  - レコード未作成時は `退勤するには先に出勤してください`
  - `FINISHED` の再退勤を明示的に禁止
  - 正常時のみ `endTime` と `status` を更新
- ログ観点:
  - `clock-out ...` のINFOログで操作記録を追える
- よくあるミス:
  - `WORKING` 判定を入れ忘れて不正退勤を許してしまう

---

## 3. `HomeController` を編集（退勤エンドポイント追加）
作成ファイル: `~/order-management-springboot/stages/day3/src/main/java/com/shinesoft/attendance/web/HomeController.java`

全文を以下に置き換えてください。

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
    // Day3は固定ユーザーで進める（認証はDay5で実装）
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
                        // リダイレクト時に受け取る成功メッセージ（任意）
                        @RequestParam(value = "message", required = false) String message,
                        // リダイレクト時に受け取るエラーメッセージ（任意）
                        @RequestParam(value = "error", required = false) String error) {
        // 当日の勤怠を取得
        Optional<Attendance> today = attendanceService.findToday(TRAINING_USER_ID);

        // テンプレートに渡す表示データを準備
        model.addAttribute("workDate", LocalDate.now());
        model.addAttribute("statusLabel", toStatusLabel(today));
        model.addAttribute("startTime", format(today.map(Attendance::getStartTime).orElse(null)));
        model.addAttribute("endTime", format(today.map(Attendance::getEndTime).orElse(null)));
        // 出勤ボタン: 当日レコードがまだ無い時のみ表示
        model.addAttribute("canClockIn", today.isEmpty());
        // 退勤ボタン: 当日レコードがあり、状態がWORKINGの時のみ表示
        model.addAttribute("canClockOut", today.isPresent() && today.get().getStatus() == AttendanceStatus.WORKING);
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        // templates/index.html を表示
        return "index";
    }

    // 出勤ボタン押下時の処理
    @PostMapping("/clock-in")
    public String clockIn(RedirectAttributes redirectAttributes) {
        try {
            // 出勤処理を実行
            attendanceService.clockIn(TRAINING_USER_ID);
            redirectAttributes.addAttribute("message", "出勤を記録しました");
        } catch (BusinessException e) {
            // 業務エラーは画面向けメッセージとして返す
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        // POST後はGETへリダイレクト（PRGパターン）
        return "redirect:/";
    }

    // 退勤ボタン押下時の処理（Day3で追加）
    @PostMapping("/clock-out")
    public String clockOut(RedirectAttributes redirectAttributes) {
        try {
            // 退勤処理を実行
            attendanceService.clockOut(TRAINING_USER_ID);
            redirectAttributes.addAttribute("message", "退勤を記録しました");
        } catch (BusinessException e) {
            // 業務エラーは画面向けメッセージとして返す
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        // POST後はGETへリダイレクト（PRGパターン）
        return "redirect:/";
    }

    // 勤怠状態を画面表示用文字列へ変換
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
            // 値が無い時は "-" を表示
            return "-";
        }
        return value.format(FMT);
    }
}
```

ポイント:
- `@PostMapping("/clock-out")` を追加
- 画面表示のため `canClockOut` を Model に追加

理解ポイント（15分）:
- この変更の目的:
  - 退勤APIを画面から呼び出せるようにする
- 重要ポイント:
  - `clockOut(...)` でServiceの退勤処理を実行
  - 例外時は `error`、成功時は `message` をリダイレクトで返す
  - `canClockOut` で「退勤ボタンを表示してよい状態」を判定
- 表示制御の考え方:
  - ボタン表示条件はControllerで作り、HTMLは受けて描画する
- よくあるミス:
  - `@PostMapping` ではなく `@GetMapping` にしてしまう

---

## 4. `index.html` を編集（退勤ボタン表示）
作成ファイル: `~/order-management-springboot/stages/day3/src/main/resources/templates/index.html`

全文を以下に置き換えてください。

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
  <title>勤怠管理（Day3）</title>
  <!-- /static 配下のCSSを読み込む -->
  <link rel="stylesheet" th:href="@{/styles.css}" />
</head>
<body>
  <!-- 画面全体コンテナ -->
  <div class="container">
    <header>
      <h1>勤怠管理システム（MVP）</h1>
      <p class="subtitle">Day3: 退勤機能と業務ルール</p>
    </header>

    <!-- message がある時のみ成功通知表示 -->
    <div th:if="${message}" class="alert alert-info" th:text="${message}"></div>
    <!-- error がある時のみエラー通知表示 -->
    <div th:if="${error}" class="alert alert-error" th:text="${error}"></div>

    <!-- 今日の勤怠表示パネル -->
    <section class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <!-- Controllerで作った statusLabel を表示 -->
        <span class="status-badge" th:text="${statusLabel}">未出勤</span>
      </div>
      <!-- 値があれば差し込み、無ければフォールバック値を表示 -->
      <p>日付: <span th:text="${workDate}">2026-02-05</span></p>
      <p>出勤時刻: <span th:text="${startTime}">-</span></p>
      <p>退勤時刻: <span th:text="${endTime}">-</span></p>

      <!-- 出勤・退勤ボタンを横並びにする -->
      <div class="row">
        <!-- canClockIn=true の時だけ出勤ボタンを表示 -->
        <form th:if="${canClockIn}" method="post" th:action="@{/clock-in}">
          <button type="submit">出勤</button>
        </form>

        <!-- canClockOut=true の時だけ退勤ボタンを表示 -->
        <form th:if="${canClockOut}" method="post" th:action="@{/clock-out}">
          <!-- danger クラスで退勤ボタンを強調 -->
          <button type="submit" class="danger">退勤</button>
        </form>
      </div>

      <!-- 退勤済み（両ボタン非表示）時の案内 -->
      <p th:if="${!canClockIn and !canClockOut}" class="muted">
        本日の勤怠は確定済みです（退勤済み）。
      </p>
    </section>
  </div>
</body>
</html>
```

理解ポイント（15分）:
- この変更の目的:
  - 画面に「退勤」ボタンと完了表示を追加する
- 重要ポイント:
  - `th:if="${canClockOut}"` で退勤ボタンを制御
  - `th:if="${!canClockIn and !canClockOut}"` で退勤済み文言を表示
  - `class="danger"` で退勤ボタンを視覚的に区別
- 変更して試す:
  - 退勤前/退勤後でボタン表示がどう変わるか確認
- よくあるミス:
  - `th:if` 条件式の否定や `and` の書き間違い

---

## 5. `styles.css` を編集（退勤ボタン用クラス）
作成ファイル: `~/order-management-springboot/stages/day3/src/main/resources/static/styles.css`

`styles.css` に以下があることを確認してください（無ければ追加）。

```css
/* 出勤ボタンと退勤ボタンを横並びにする */
.row {
  /* 子要素を横方向に並べる */
  display: flex;
  /* ボタン間の余白 */
  gap: 8px;
  /* 幅が足りない時は折り返す */
  flex-wrap: wrap;
  /* 高さ方向を中央揃え */
  align-items: center;
}

/* 危険操作（退勤）を目立たせる色 */
.danger {
  background: #ef4444;
}
```

理解ポイント（5分）:
- この変更の目的:
  - 退勤ボタンを強調表示して誤操作を減らす
- 重要ポイント:
  - `.row` で出勤/退勤フォームを横並びにする
  - `.danger` で破壊系アクションの色を分ける
- よくあるミス:
  - HTML側に `class="danger"` を付け忘れる

---

## 6. 起動
```bash
cd ~/order-management-springboot/stages/day3
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
