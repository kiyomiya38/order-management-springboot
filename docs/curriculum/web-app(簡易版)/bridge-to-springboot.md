# Webアプリ簡易版からSpring Bootへの対応表

この資料は、`web-app(簡易版)` で手作業した処理が、Spring Boot では何に置き換わるかを確認する橋渡しです。
Spring Boot に入る前に必ず読みます。

## 目的
- フレームワークなし実装と Spring Boot 実装の対応を説明できる
- Spring Boot が何を自動化してくれるか分かる
- `fetch + JSON API` と `Controller + Thymeleaf` の違いを整理できる

---

## 1. 全体対応
| web-app(簡易版) | Spring Boot | 何が変わるか |
| --- | --- | --- |
| `HttpServer.create(...)` | 組み込みTomcat | サーバー起動をSpring Bootが担当する |
| `server.createContext(...)` | `@GetMapping` / `@PostMapping` | URLと処理メソッドの対応をアノテーションで書く |
| `HttpExchange` | Controllerメソッドの引数 / `Model` / Form | リクエスト情報をSpring MVCが扱いやすい形で渡す |
| `sendJson(...)` | Controllerの戻り値 / `ResponseEntity` | ステータスやレスポンス本文をSpring MVCが返す |
| 手書きJSON文字列 | Jackson / Thymeleaf | JSON変換やHTML描画をライブラリが担当する |
| `static/index.html` | `src/main/resources/templates` / `static` | テンプレートと静的ファイルの置き場が規約化される |
| `MessageStore` / `TodoStore` | `Service` | 業務ロジックを担当する層へ移す |
| `ArrayList` に保存 | `Repository` / DB | アプリ再起動後もデータが残る |
| `synchronized` | DB制約 / トランザクション | 同時更新の整合性をDBとSpringの仕組みで守る |
| `javac` / `java` | Maven / `mvn spring-boot:run` | 依存解決、コンパイル、起動をMavenでまとめる |

---

## 2. ルーティングの置き換え
`web-app(簡易版)` では、URLごとに `createContext` を登録しました。

```java
server.createContext("/api/todos", App::handleTodos);
```

Spring Boot では、Controllerにアノテーションを書きます。

```java
@Controller
public class HomeController {
    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }
}
```

APIとしてJSONを返す場合は `@RestController` を使います。

```java
@RestController
public class TodoApiController {
    @GetMapping("/api/todos")
    public List<TodoResponse> list() {
        return service.list();
    }
}
```

覚えること:
- 画面を返す入口は `@Controller`
- JSONを返す入口は `@RestController`
- URLとの対応は `@GetMapping` / `@PostMapping` などで書く

---

## 3. JSONの置き換え
`web-app(簡易版)` では、学習用にJSON文字列を手作業で作りました。

```java
return "{\"message\":\"" + escapeJson(message) + "\"}";
```

Spring Boot のJSON APIでは、Javaオブジェクトを返すと Jackson がJSONへ変換します。

```java
return new MessageResponse("こんにちは、Taroさん");
```

覚えること:
- 手書きJSONは、通信の仕組みを理解するための学習用
- Spring Bootでは、JSON変換は Jackson に任せる
- 実務では、文字列連結でJSONを作らない

---

## 4. データ保存の置き換え
`web-app(簡易版)` では、メモリ上の `List` に保存しました。

```java
private final List<Todo> todos = new ArrayList<>();
```

Spring Boot では、Entity / Repository / DB を使います。

```java
@Entity
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

```java
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
```

覚えること:
- `List` 保存はアプリを止めると消える
- DB保存はアプリを止めても残る
- RepositoryはDBアクセスの窓口

---

## 5. 業務ロジックの置き換え
`web-app(簡易版)` では、`Store` クラスに保存と判定をまとめました。

```java
public synchronized Todo create(String title) {
    Todo todo = new Todo(sequence.incrementAndGet(), title, false);
    todos.add(todo);
    return todo;
}
```

Spring Boot では、業務判断を `Service` に置きます。

```java
@Service
public class AttendanceService {
    public Attendance clockIn(Long userId) {
        // 二重出勤禁止などの業務ルールを書く
    }
}
```

覚えること:
- Controllerは画面/HTTPの入口
- Serviceは業務ルール
- RepositoryはDBアクセス
- Spring Bootでは役割を分けることで、修正しやすくする

---

## 6. 画面表示方式の違い
`web-app(簡易版)` は、主に `fetch + JSON API + DOM更新` で画面を変えました。

```javascript
const response = await fetch("/api/todos");
const todos = await response.json();
renderTodos(todos);
```

Spring Boot Lesson1〜5 は、主に `Controller + Model + Thymeleaf` でHTMLを作ります。

```java
model.addAttribute("statusLabel", "未出勤");
return "index";
```

```html
<span th:text="${statusLabel}">未出勤</span>
```

違い:
- `fetch + JSON`: ブラウザ側JavaScriptがAPIを呼び、DOMを更新する
- `Controller + Thymeleaf`: サーバー側でHTMLを作ってブラウザへ返す

この研修では、先に `Controller + Thymeleaf` でSpring MVCの基本を学び、後で `@RestController` のJSON APIへ進みます。

---

## 7. Spring Bootに入る前のチェック
次を口頭で説明できれば、Spring Boot Lesson1へ進めます。

1. `createContext` と `@GetMapping` の対応
2. `sendJson` と Controller戻り値の対応
3. `Store` と `Service` の対応
4. `ArrayList` と DB / Repository の違い
5. `fetch + JSON` と `Thymeleaf + Model` の違い
6. Spring Bootを使うと、どの手作業が減るか
