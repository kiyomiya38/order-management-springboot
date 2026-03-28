# Phase2 Lesson1: Repository + Service の最小実装（出勤/退勤ボタンをWeb接続）

## ロードマップ接続
- 対象: `springless-roadmap.md` の Phase2
- このLessonで増える機能: `AttendanceRepository` と `AttendanceService` を導入し、トップ画面の出勤/退勤ボタンを処理へ接続
- 到達点: 「画面 -> Handler -> Service -> Repository」の流れを説明できる

## このLessonの方針
- Phase1の分割構成を維持する
- 既存ファイルを活かしつつ、Repository/Serviceを最小導入する
- DBは使わず、まずはメモリ上で状態遷移を確認する

---

## 1. 事前準備
```bash
cd ~/order-management-springboot/practice/springless-final-web
```

Lesson5完了チェック:
- `App.java`, `PageHandlers.java`, `HtmlLayout.java`, `DashboardService.java` が存在する
- `/` 画面が表示できる

---

## 2. Step1: Repositoryインターフェースを作成する

### このStepで学ぶ文法
- インターフェース定義
- 抽象メソッド

新規作成:
- `AttendanceRepository.java`

```java
public interface AttendanceRepository {
    Attendance findTodayByUsername(String username);

    void saveToday(String username, Attendance attendance);
}
```

---

## 3. Step2: メモリ実装のRepositoryを作成する

### このStepで学ぶ文法
- `Map<K,V>`
- 実装クラス（`implements`）

新規作成:
- `InMemoryAttendanceRepository.java`

```java
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class InMemoryAttendanceRepository implements AttendanceRepository {
    private final Map<String, Attendance> store = new HashMap<>();

    @Override
    public Attendance findTodayByUsername(String username) {
        Attendance existing = store.get(username);
        if (existing != null) {
            return existing;
        }

        // 初回アクセス時の初期状態
        return new Attendance(
                LocalDate.now().toString(),
                "-",
                "-",
                AttendanceStatus.NOT_CLOCKED_IN
        );
    }

    @Override
    public void saveToday(String username, Attendance attendance) {
        store.put(username, attendance);
    }
}
```

---

## 4. Step3: AttendanceServiceを追加する

### このStepで学ぶ文法
- `private final` フィールド
- コンストラクタ注入
- 業務ルールの `if` 分岐
- `BusinessException` の送出

新規作成:
- `AttendanceService.java`

```java
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Attendance getTodayAttendance(String username) {
        return attendanceRepository.findTodayByUsername(username);
    }

    public void clockIn(String username) {
        Attendance current = attendanceRepository.findTodayByUsername(username);

        if (current.getStatus() != AttendanceStatus.NOT_CLOCKED_IN) {
            throw new BusinessException("すでに出勤済みです");
        }

        Attendance updated = new Attendance(
                current.getWorkDate(),
                nowTime(),
                "-",
                AttendanceStatus.WORKING
        );
        attendanceRepository.saveToday(username, updated);
    }

    public void clockOut(String username) {
        Attendance current = attendanceRepository.findTodayByUsername(username);

        if (current.getStatus() == AttendanceStatus.NOT_CLOCKED_IN) {
            throw new BusinessException("未出勤のため退勤できません");
        }
        if (current.getStatus() == AttendanceStatus.CLOCKED_OUT) {
            throw new BusinessException("すでに退勤済みです");
        }

        Attendance updated = new Attendance(
                current.getWorkDate(),
                current.getClockInTime(),
                nowTime(),
                AttendanceStatus.CLOCKED_OUT
        );
        attendanceRepository.saveToday(username, updated);
    }

    private String nowTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
```

---

## 5. Step4: HttpResponsesにリダイレクト処理を追加する

### このStepで学ぶ文法
- HTTPリダイレクト（303）

変更ファイル:
- `HttpResponses.java`

追加場所:
- `sendText(...)` の下（クラス末尾 `}` の前）

```java
public static void sendRedirect(HttpExchange exchange, String location) throws IOException {
    exchange.getResponseHeaders().set("Location", location);
    exchange.sendResponseHeaders(303, -1);
    exchange.close();
}
```

---

## 6. Step5: AppでRepository/Serviceを生成して注入する

### このStepで学ぶ文法
- 依存オブジェクト生成順
- ハンドラーへの注入

変更ファイル:
- `App.java`

変更ポイント:
1. `DashboardService` 生成の近くに以下を追加
```java
AttendanceRepository attendanceRepository = new InMemoryAttendanceRepository();
AttendanceService attendanceService = new AttendanceService(attendanceRepository);
```

2. `PageHandlers` の生成を変更
変更前:
```java
PageHandlers handlers = new PageHandlers(dashboardService);
```

変更後:
```java
PageHandlers handlers = new PageHandlers(dashboardService, attendanceService);
```

3. ルーティングを追加
```java
server.createContext("/clock-in", handlers::handleClockIn);
server.createContext("/clock-out", handlers::handleClockOut);
```

---

## 7. Step6: PageHandlersでトップ表示とボタン処理を接続する

### このStepで学ぶ文法
- HandlerでService呼び出し
- `try-catch` で業務例外を画面メッセージ化
- クエリ文字列読み取り

変更ファイル:
- `PageHandlers.java`

### 7-1. フィールドとコンストラクタを変更
変更前:
```java
private final DashboardService dashboardService;

public PageHandlers(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
}
```

変更後:
```java
private final DashboardService dashboardService;
private final AttendanceService attendanceService;

public PageHandlers(DashboardService dashboardService, AttendanceService attendanceService) {
    this.dashboardService = dashboardService;
    this.attendanceService = attendanceService;
}
```

### 7-2. `handleTop` を置き換える
```java
public void handleTop(HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
        HttpResponses.sendText(exchange, 405, "Method Not Allowed");
        return;
    }

    User loginUser = dashboardService.getLoginUser();
    Attendance todayAttendance = attendanceService.getTodayAttendance(loginUser.getUsername());

    String message = readQueryParam(exchange, "msg");
    String messageType = readQueryParam(exchange, "type");

    if (message == null || message.isBlank()) {
        message = "メッセージ表示エリア（機能は後続Lessonで実装）";
    }
    if (messageType == null || messageType.isBlank()) {
        messageType = "info";
    }

    HttpResponses.sendHtml(exchange, 200, HtmlLayout.topPageHtml(loginUser, todayAttendance, message, messageType));
}
```

### 7-3. `handleClockIn` と `handleClockOut` を追加
追加場所:
- `handleTop` の下（`handleHealth` の前でも後でも可）

```java
public void handleClockIn(HttpExchange exchange) throws IOException {
    if (!"POST".equals(exchange.getRequestMethod())) {
        HttpResponses.sendText(exchange, 405, "Method Not Allowed");
        return;
    }

    String username = dashboardService.getLoginUser().getUsername();

    try {
        attendanceService.clockIn(username);
        redirectWithMessage(exchange, "info", "出勤しました");
    } catch (BusinessException e) {
        redirectWithMessage(exchange, "error", e.getMessage());
    }
}

public void handleClockOut(HttpExchange exchange) throws IOException {
    if (!"POST".equals(exchange.getRequestMethod())) {
        HttpResponses.sendText(exchange, 405, "Method Not Allowed");
        return;
    }

    String username = dashboardService.getLoginUser().getUsername();

    try {
        attendanceService.clockOut(username);
        redirectWithMessage(exchange, "info", "退勤しました");
    } catch (BusinessException e) {
        redirectWithMessage(exchange, "error", e.getMessage());
    }
}
```

### 7-4. ヘルパーメソッドを追加
追加場所:
- `PageHandlers` クラス末尾（最後の `}` の直前）

```java
private void redirectWithMessage(HttpExchange exchange, String type, String msg) throws IOException {
    String encodedType = java.net.URLEncoder.encode(type, java.nio.charset.StandardCharsets.UTF_8);
    String encodedMsg = java.net.URLEncoder.encode(msg, java.nio.charset.StandardCharsets.UTF_8);
    HttpResponses.sendRedirect(exchange, "/?type=" + encodedType + "&msg=" + encodedMsg);
}

private String readQueryParam(HttpExchange exchange, String key) {
    String query = exchange.getRequestURI().getQuery();
    if (query == null || query.isBlank()) {
        return "";
    }

    String[] pairs = query.split("&");
    for (String pair : pairs) {
        String[] kv = pair.split("=", 2);
        if (kv.length == 2 && key.equals(kv[0])) {
            return java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    return "";
}
```

---

## 8. 反映と実行
```bash
Ctrl + C
javac -encoding UTF-8 App.java PageHandlers.java HtmlLayout.java HttpResponses.java User.java Attendance.java DashboardService.java AttendanceStatus.java BusinessException.java AttendanceRepository.java InMemoryAttendanceRepository.java AttendanceService.java
java App
```

ブラウザ確認:
1. `http://localhost:8080/` を開く（初期状態: 未出勤）
2. 出勤ボタンを押す（状態: 出勤中、メッセージ表示）
3. 退勤ボタンを押す（状態: 退勤済み、メッセージ表示）
4. もう一度退勤を押す（業務エラーメッセージ表示）

合格条件:
- 未出勤 -> 出勤 -> 退勤 の状態遷移が画面で確認できる
- 不正操作時に `BusinessException` の文言が表示される

---

## 9. Lesson1コードの説明（入力・処理・出力）

1. `AttendanceRepository`
- 定義元: 自作インターフェース
- 入力: `username`
- 処理: 当日勤怠の取得/保存を抽象化
- 出力: `Attendance` または保存結果

2. `AttendanceService.clockIn/clockOut`
- 定義元: 自作Service
- 入力: `username`
- 処理: 現在状態を検証 -> 業務ルールに従い更新
- 出力: 正常時は状態更新、不正時は `BusinessException`

3. `PageHandlers.handleClockIn/handleClockOut`
- 定義元: 自作Handler
- 入力: `HttpExchange`
- 処理: POST判定 -> Service呼び出し -> リダイレクト
- 出力: トップ画面へメッセージ付き遷移

---

## 10. よくあるエラー
- `cannot find symbol AttendanceService`:
  - `AttendanceService.java` 作成漏れ
- `constructor PageHandlers(...) cannot be applied`:
  - `App.java` 側の引数数と `PageHandlers` コンストラクタを一致させる
- `405 Method Not Allowed`:
  - `/clock-in` `/clock-out` を `POST` で送っているか確認

## 11. 次Lessonへの引き継ぎ
- Lesson1で打刻の最小動作がWeb接続できた
- Lesson2でユーザー単位の履歴管理を拡張する
