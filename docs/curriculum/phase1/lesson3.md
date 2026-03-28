# Lesson3: `private final` と手動DI（Service経由で値を取得する）

## ロードマップ接続
- 対象: `springless-roadmap.md` の Phase1
- このLessonで増える機能: `DashboardService` を追加し、画面表示値の取得を Service 経由に統一する
- 到達点: Controller相当（`PageHandlers`）が「値を作る責務」を持たず、依存クラスへ委譲できる

## このLessonの方針
- Lesson1/2の分割構成を維持する
- 変更対象: `App.java`, `PageHandlers.java` + 新規 `DashboardService.java`
- `HtmlLayout.java`, `User.java`, `Attendance.java`, `HttpResponses.java` は既存を再利用

---

## 1. 事前準備
```bash
cd ~/order-management-springboot/practice/springless-final-web
```

Lesson2完了チェック:
- `http://localhost:8080/`
- `http://localhost:8080/attendances`
- 表示値が `User` / `Attendance` オブジェクト由来になっている

---

## 2. Step1: `DashboardService.java` を新規作成する

### このStepで学ぶ文法
- Serviceクラスの作成
- 値の生成処理を1か所へ集約
- 戻り値としてドメインオブジェクトを返す

作成ファイル:
- `DashboardService.java`

```java
public class DashboardService {
    // ログイン中ユーザー（仮）を返す
    public User getLoginUser() {
        return new User("user1", "ROLE_USER");
    }

    // トップ画面用の当日勤怠（仮）を返す
    public Attendance getTodayAttendanceFor(String username) {
        // 今は固定値。後続Lessonで username や日付に応じた処理へ拡張する。
        return new Attendance("2026-03-26", "-", "-", "未出勤");
    }

    // 勤怠一覧の先頭行（仮）を返す
    public Attendance getLatestAttendanceFor(String username) {
        return new Attendance("2026-03-26", "-", "-", "未出勤");
    }
}
```

---

## 3. Step2: `PageHandlers.java` を手動DI構成へ変更する

### このStepで学ぶ文法
- `private final` フィールド
- コンストラクタ注入（手動DI）
- Service呼び出しで値取得

変更ファイル:
- `PageHandlers.java`

変更方法:
- **ファイル全体を置き換える**（貼り付け先ミス防止のため）

置き換えコード:
```java
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class PageHandlers {
    // 依存先を private final で保持（作成後に差し替え不可）
    private final DashboardService dashboardService;

    // 手動DI: App で作った Service を受け取る
    public PageHandlers(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // トップ画面（/）
    public void handleTop(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        User loginUser = dashboardService.getLoginUser();
        Attendance todayAttendance = dashboardService.getTodayAttendanceFor(loginUser.getUsername());

        HttpResponses.sendHtml(exchange, 200, HtmlLayout.topPageHtml(loginUser, todayAttendance));
    }

    // 監視確認（/health）
    public void handleHealth(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        HttpResponses.sendText(exchange, 200, "OK");
    }

    public void handleLogin(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        HttpResponses.sendHtml(exchange, 200, HtmlLayout.loginPageHtml());
    }

    public void handleAttendances(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        User loginUser = dashboardService.getLoginUser();
        Attendance row = dashboardService.getLatestAttendanceFor(loginUser.getUsername());

        HttpResponses.sendHtml(exchange, 200, HtmlLayout.attendancesPageHtml(loginUser, row));
    }

    public void handleUsers(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        HttpResponses.sendHtml(exchange, 200, HtmlLayout.usersPageHtml());
    }

    public void handleAdminAttendances(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        HttpResponses.sendHtml(exchange, 200, HtmlLayout.adminAttendancesPageHtml());
    }
}
```

---

## 4. Step3: `App.java` で Service と Handlers を生成して注入する

### このStepで学ぶ文法
- オブジェクト生成順
- メソッド参照（インスタンス版）

変更ファイル:
- `App.java`

変更方法:
- `main` メソッド内のサーバー生成後に、次の2行を追加
  - `DashboardService dashboardService = new DashboardService();`
  - `PageHandlers handlers = new PageHandlers(dashboardService);`
- `server.createContext` を `PageHandlers::...` から `handlers::...` へ置換

置き換え後コード（`App.java` 全体）:
```java
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 手動DI: 依存オブジェクトをAppで作って渡す
        DashboardService dashboardService = new DashboardService();
        PageHandlers handlers = new PageHandlers(dashboardService);

        server.createContext("/", handlers::handleTop);
        server.createContext("/health", handlers::handleHealth);
        server.createContext("/login", handlers::handleLogin);
        server.createContext("/attendances", handlers::handleAttendances);
        server.createContext("/users", handlers::handleUsers);
        server.createContext("/admin/attendances", handlers::handleAdminAttendances);

        server.setExecutor(null);
        server.start();

        System.out.println("Server started: http://localhost:8080");
    }
}
```

---

## 5. 反映と実行
```bash
Ctrl + C
javac -encoding UTF-8 App.java PageHandlers.java HtmlLayout.java HttpResponses.java User.java Attendance.java DashboardService.java
java App
```

ブラウザ確認:
- `http://localhost:8080/`
- `http://localhost:8080/attendances`
- `http://localhost:8080/users`

確認ポイント:
- 画面表示は変わらない
- ただし `PageHandlers` 内で `new User(...)` / `new Attendance(...)` を直接書かず、`dashboardService` 経由で取得している

---

## 6. Lesson3コードの説明（入力・処理・出力）

1. `PageHandlers` の `private final DashboardService dashboardService`
- 定義元: 自作（`PageHandlers`）
- 入力: コンストラクタで受け取った `DashboardService`
- 処理: フィールドへ1回だけ代入し保持
- 出力: ハンドラー内で安定して同じ Service を使える

2. `new PageHandlers(dashboardService)`
- 定義元: 自作コンストラクタ呼び出し
- 入力: `DashboardService` インスタンス
- 処理: 依存先を注入して `PageHandlers` を生成
- 出力: Service利用可能な `handlers` オブジェクト

3. `handlers::handleTop`
- 定義元: Javaメソッド参照
- 入力: `HttpExchange`（実行時にサーバーが渡す）
- 処理: `handlers` インスタンスの `handleTop` を呼び出す
- 出力: `/` アクセス時にトップ画面が返る

4. `dashboardService.getLoginUser()`
- 定義元: 自作（`DashboardService`）
- 入力: なし
- 処理: ログインユーザー（仮）を生成して返す
- 出力: `User`

---

## 7. よくあるエラー
- `invalid method reference`（`PageHandlers::handleTop`）:
  - `PageHandlers` をインスタンス化した後は `handlers::handleTop` を使う
- `cannot find symbol handleTop(HttpExchange)`:
  - `PageHandlers` 内のメソッド名/引数が崩れていないか確認
- `constructor PageHandlers in class PageHandlers cannot be applied`:
  - `new PageHandlers(dashboardService)` とコンストラクタ定義が一致しているか確認

## 8. 次Lessonへの引き継ぎ
- Lesson3で「依存先へ処理を委譲する形」ができた
- Lesson4では Service の処理を増やし、状態分岐や一覧拡張へ進める
