# Lesson5B ユーザー管理・勤怠管理

[Lesson5共通準備と全体目次](./lesson5.md)および[Lesson5A](./lesson5a-authentication.md)を完了してから実施します。

## 目的

- 管理者によるユーザー作成・更新・削除を実装できる
- 管理者による勤怠一覧・編集を実装できる
- 画面操作をController、Service、Repositoryまで追跡できる

## 前提

- Lesson5Aのログインと権限制御が動作する
- `admin` と `user1` でログイン結果の違いを確認できる

## 学習内容

Lesson5BではPhase 2のユーザー管理と、Phase 3-1〜3-7の管理者勤怠管理を実施します。Phase 3-8以降のテストと運用確認はLesson5Cで実施します。

### Phase 2: 管理者のユーザー管理
新規作成ファイル（フルパス）:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/service/UserService.java`
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/UserController.java`
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/form/UserForm.java`
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/users.html`
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/user-form.html`
- `~/order-management-springboot/stages/lesson05/src/main/resources/static/users.js`

既存編集ファイル（フルパス）:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/HomeController.java`
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/index.html`
- `~/order-management-springboot/stages/lesson05/src/main/resources/static/styles.css`

コードの意味（このフェーズで理解すること）:
- `UserService.java`:
  - ユーザー登録/更新/削除の業務ロジックをまとめる
  - パスワード暗号化、重複チェックなどを担当する
- `UserController.java`:
  - 管理者画面（ユーザー一覧・作成/編集フォーム）へのHTTPリクエストを受ける
- `UserForm.java`:
  - 画面入力値を受け取り、バリデーションするためのフォームクラス
- `users.html`, `user-form.html`:
  - 管理者向けのユーザー一覧・編集画面を表示する
- `HomeController.java`, `index.html`:
  - ログイン中ユーザー情報を表示し、管理者向け導線を追加する

#### Phase 2-1: `UserService.java` を作る（ユーザー管理の業務ロジック）
作成ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/service/UserService.java`

新規作成してください（既にある場合は全文置き換え）。

```java
package com.shinesoft.attendance.service; // Service層のパッケージ

import java.util.List; // 一覧取得で使う

import org.springframework.security.crypto.password.PasswordEncoder; // パスワードハッシュ化に使う
import org.springframework.stereotype.Service; // Serviceクラスとして登録
import org.springframework.transaction.annotation.Transactional; // 更新処理をトランザクション化

import com.shinesoft.attendance.domain.User; // Userエンティティ
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.repository.AttendanceRepository; // 勤怠参照チェック
import com.shinesoft.attendance.repository.UserRepository; // UserのDBアクセス

@Service // Spring管理対象（業務ロジック層）
public class UserService {
    private final UserRepository userRepository; // User保存/検索に使う
    private final AttendanceRepository attendanceRepository; // 削除前の勤怠参照確認
    private final PasswordEncoder passwordEncoder; // パスワード暗号化に使う

    public UserService(UserRepository userRepository,
                       AttendanceRepository attendanceRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository; // 依存注入
        this.attendanceRepository = attendanceRepository; // 依存注入
        this.passwordEncoder = passwordEncoder; // 依存注入
    }

    public User getByUsername(String username) { // ユーザー名で1件取得
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException("ユーザーが存在しません")); // 見つからなければ業務エラー
    }

    public List<User> list() { // 一覧取得
        return userRepository.findAll();
    }

    public User get(Long id) { // IDで1件取得
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("ユーザーが存在しません")); // 見つからなければ業務エラー
    }

    @Transactional // 作成処理を1トランザクションで実行
    public User create(String username, String rawPassword, String role) {
        username = validateUsername(username);
        validatePassword(rawPassword);
        validateRole(role);
        if (userRepository.findByUsername(username).isPresent()) { // ユーザー名重複チェック
            throw new BusinessException("ユーザー名が既に存在します");
        }
        User user = new User(); // 新規エンティティ生成
        user.setUsername(username); // ユーザー名設定
        user.setPassword(passwordEncoder.encode(rawPassword)); // パスワードをハッシュ化して設定
        user.setRole(role); // ロール設定
        return userRepository.save(user); // 保存して返す
    }

    @Transactional // 更新処理を1トランザクションで実行
    public User update(Long id, String username, String rawPassword, String role) {
        username = validateUsername(username);
        if (rawPassword != null && !rawPassword.isBlank()) {
            validatePassword(rawPassword);
        }
        validateRole(role);
        User user = get(id); // 更新対象を取得
        if (!user.getUsername().equals(username) && userRepository.findByUsername(username).isPresent()) { // 他人と重複しないか確認
            throw new BusinessException("ユーザー名が既に存在します");
        }
        user.setUsername(username); // ユーザー名更新
        if (rawPassword != null && !rawPassword.isBlank()) { // パスワード入力がある場合のみ更新
            user.setPassword(passwordEncoder.encode(rawPassword)); // ハッシュ化して更新
        }
        user.setRole(role); // ロール更新
        return userRepository.save(user); // 保存して返す
    }

    @Transactional // 削除処理を1トランザクションで実行
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("ユーザーが存在しません");
        }
        if (attendanceRepository.existsByUser_Id(id)) {
            throw new BusinessException("勤怠履歴があるユーザーは削除できません");
        }
        userRepository.deleteById(id); // 参照が無い場合だけ削除
    }

    private String validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BusinessException("ユーザー名は必須です");
        }
        String normalized = username.trim();
        if (normalized.length() > 30) {
            throw new BusinessException("ユーザー名は30文字以内にしてください");
        }
        return normalized;
    }

    private void validatePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8 || rawPassword.length() > 64) {
            throw new BusinessException("パスワードは8〜64文字にしてください");
        }
    }

    private void validateRole(String role) {
        if (!"ROLE_USER".equals(role) && !"ROLE_ADMIN".equals(role)) {
            throw new BusinessException("ロールが不正です");
        }
    }
}
```

理解ポイント:
- ControllerからDB直操作せず、業務ロジックを `UserService` に集約する
- `create` / `update` で重複チェックを入れて不正データを防ぐ
- パスワードは常にハッシュ化して保存する

#### Phase 2-2: `UserController.java` を作る（管理者画面の入口）
作成ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/UserController.java`

新規作成してください（既にある場合は全文置き換え）。

```java
package com.shinesoft.attendance.web; // Web（Controller）層のパッケージ

import jakarta.validation.Valid; // 入力バリデーションを有効化

import org.springframework.stereotype.Controller; // Controllerとして登録
import org.springframework.ui.Model; // 画面へ値を渡す
import org.springframework.validation.BindingResult; // バリデーション結果を受ける
import org.springframework.web.bind.annotation.*; // Mapping系アノテーション
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // リダイレクト時のメッセージ保持

import com.shinesoft.attendance.domain.User; // 編集時に既存ユーザー情報を扱う
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.service.UserService; // 業務ロジック呼び出し先
import com.shinesoft.attendance.web.form.UserForm; // 画面入力フォーム

@Controller // 画面制御クラス
@RequestMapping("/users") // /users 配下を担当
public class UserController {
    private final UserService userService; // ユーザー管理業務を委譲

    public UserController(UserService userService) {
        this.userService = userService; // 依存注入
    }

    @GetMapping // GET /users（一覧画面）
    public String list(Model model,
                       @ModelAttribute("error") String error,
                       @ModelAttribute("message") String message) {
        model.addAttribute("users", userService.list()); // 一覧データ
        model.addAttribute("error", error); // 失敗メッセージ
        model.addAttribute("message", message); // 成功メッセージ
        return "users"; // templates/users.html
    }

    @GetMapping("/new") // GET /users/new（新規作成フォーム）
    public String newForm(@ModelAttribute("userForm") UserForm form, Model model) {
        model.addAttribute("mode", "create"); // 作成モード
        model.addAttribute("formAction", "/users"); // 送信先
        return "user-form"; // templates/user-form.html
    }

    @PostMapping // POST /users（新規作成実行）
    public String create(@Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult binding,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (binding.hasErrors() || form.getPassword() == null || form.getPassword().isBlank()) { // 入力チェック
            if (form.getPassword() == null || form.getPassword().isBlank()) {
                binding.rejectValue("password", "required", "パスワードは必須です"); // 新規時はパスワード必須
            }
            model.addAttribute("mode", "create");
            model.addAttribute("formAction", "/users");
            return "user-form"; // 入力画面へ戻す
        }
        try {
            userService.create(form.getUsername(), form.getPassword(), form.getRole()); // 作成実行
            redirectAttributes.addFlashAttribute("message", "ユーザーを作成しました");
            return "redirect:/users"; // 一覧へ戻す
        } catch (BusinessException ex) {
            binding.reject("business", ex.getMessage()); // 業務エラー表示
            model.addAttribute("mode", "create");
            model.addAttribute("formAction", "/users");
            return "user-form";
        }
    }

    @GetMapping("/{id}/edit") // GET /users/{id}/edit（編集フォーム）
    public String editForm(@PathVariable("id") Long id,
                           @ModelAttribute("userForm") UserForm form,
                           Model model) {
        User user = userService.get(id); // 既存ユーザー取得
        form.setUsername(user.getUsername()); // 初期値セット
        form.setRole(user.getRole()); // 初期値セット
        model.addAttribute("mode", "edit"); // 編集モード
        model.addAttribute("userId", id); // 画面表示補助
        model.addAttribute("formAction", "/users/" + id); // 更新送信先
        return "user-form";
    }

    @PostMapping("/{id}") // POST /users/{id}（更新実行）
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult binding,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (binding.hasErrors()) { // 入力エラー時
            model.addAttribute("mode", "edit");
            model.addAttribute("userId", id);
            model.addAttribute("formAction", "/users/" + id);
            return "user-form";
        }
        try {
            userService.update(id, form.getUsername(), form.getPassword(), form.getRole()); // 更新実行
            redirectAttributes.addFlashAttribute("message", "ユーザーを更新しました");
            return "redirect:/users";
        } catch (BusinessException ex) {
            binding.reject("business", ex.getMessage()); // 業務エラー
            model.addAttribute("mode", "edit");
            model.addAttribute("userId", id);
            model.addAttribute("formAction", "/users/" + id);
            return "user-form";
        }
    }

    @PostMapping("/{id}/delete") // POST /users/{id}/delete（削除実行）
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id); // 削除実行
            redirectAttributes.addFlashAttribute("message", "ユーザーを削除しました");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/users";
    }
}
```

理解ポイント:
- `UserController` は「画面遷移と入力検証」を担当する
- 実際の作成/更新/削除は `UserService` へ委譲する
- `RedirectAttributes` で完了メッセージを一覧画面へ渡す

#### Phase 2-3: `UserForm.java` を作る（入力値を受ける器）
作成ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/form/UserForm.java`

新規作成してください（既にある場合は全文置き換え）。

```java
package com.shinesoft.attendance.web.form; // フォームクラス用パッケージ

import jakarta.validation.constraints.NotBlank; // 必須チェックに使う
import jakarta.validation.constraints.Pattern; // 許可する値を制限する
import jakarta.validation.constraints.Size; // 文字数を制限する

public class UserForm {
    @NotBlank // 空文字・空白のみを禁止
    @Size(max = 30)
    private String username;

    @Size(max = 64)
    private String password; // 更新時は空欄許可にするためNotBlankを付けない

    @NotBlank // ロールは必須
    @Pattern(regexp = "ROLE_ADMIN|ROLE_USER")
    private String role;

    public String getUsername() { // username取得
        return username;
    }

    public String getPassword() { // password取得
        return password;
    }

    public String getRole() { // role取得
        return role;
    }

    public void setUsername(String username) { // username設定
        this.username = username;
    }

    public void setPassword(String password) { // password設定
        this.password = password;
    }

    public void setRole(String role) { // role設定
        this.role = role;
    }
}
```

理解ポイント:
- Formクラスは「画面入力の受け取り専用」
- `@NotBlank` でControllerに来る前に基本バリデーションを実施できる
- 新規と更新でパスワード必須条件が違うため、Controllerで追加判定する

#### Phase 2-4: `users.html` を作る（ユーザー一覧画面）
作成ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/users.html`

新規作成してください（既にある場合は全文置き換え）。

```html
<!doctype html> <!-- HTML5文書宣言 -->
<html lang="ja" xmlns:th="http://www.thymeleaf.org"> <!-- Thymeleaf有効化 -->
<head>
  <meta charset="utf-8" /> <!-- 文字コード -->
  <meta name="viewport" content="width=device-width, initial-scale=1" /> <!-- モバイル表示対応 -->
  <title>アカウント管理</title> <!-- ページタイトル -->
  <link rel="stylesheet" th:href="@{/styles.css}" /> <!-- 共通CSS -->
</head>
<body>
  <div class="container"> <!-- 全体レイアウト -->
    <header>
      <h1>アカウント管理</h1>
      <div class="row">
        <a th:href="@{/}">トップへ戻る</a> <!-- トップへ戻る -->
        <a th:href="@{/users/new}">新規作成</a> <!-- 新規作成画面へ -->
      </div>
    </header>

    <div th:if="${error != null and !#strings.isEmpty(error)}" class="alert alert-error" th:text="${error}"></div> <!-- エラー -->
    <div th:if="${message != null and !#strings.isEmpty(message)}" class="alert alert-info" th:text="${message}"></div> <!-- 成功通知 -->

    <section class="panel">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>ユーザー名</th>
            <th>ロール</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr th:if="${#lists.isEmpty(users)}"> <!-- データ0件時の表示 -->
            <td colspan="4" class="muted">ユーザーがいません。</td>
          </tr>
          <tr th:each="u : ${users}"> <!-- users一覧を繰り返し描画 -->
            <td th:text="${u.id}">1</td>
            <td th:text="${u.username}">user1</td>
            <td th:text="${u.role}">ROLE_USER</td>
            <td>
              <a th:href="@{|/users/${u.id}/edit|}">編集</a> <!-- 編集画面 -->
              <form method="post" th:action="@{|/users/${u.id}/delete|}" style="display:inline"> <!-- 削除実行 -->
                <button type="submit" class="danger">削除</button>
              </form>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</body>
</html>
```

理解ポイント:
- `users` モデル属性をテーブル描画に使う
- 編集はリンク、削除はPOSTフォームで送る
- 画面上部で成功/失敗メッセージを表示する

#### Phase 2-4A: `users.html` / `users.js` / `styles.css` を編集（削除確認 + 検索/絞り込み）
このステップで追加すること:
1. ユーザー削除前に確認ダイアログを表示する
2. ユーザー一覧を「ユーザー名」「ロール」で画面遷移なしで絞り込めるようにする

編集ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/users.html`
- `~/order-management-springboot/stages/lesson05/src/main/resources/static/users.js`
- `~/order-management-springboot/stages/lesson05/src/main/resources/static/styles.css`

1) `users.html` を以下に置き換えてください。

```html
<!doctype html> <!-- HTML5文書宣言 -->
<html lang="ja" xmlns:th="http://www.thymeleaf.org"> <!-- Thymeleaf有効化 -->
<head>
  <meta charset="utf-8" /> <!-- 文字コード -->
  <meta name="viewport" content="width=device-width, initial-scale=1" /> <!-- モバイル表示対応 -->
  <title>アカウント管理</title> <!-- ページタイトル -->
  <link rel="stylesheet" th:href="@{/styles.css}" /> <!-- 共通CSS -->
</head>
<body>
  <div class="container"> <!-- 全体レイアウト -->
    <header>
      <h1>アカウント管理</h1>
      <div class="row">
        <a th:href="@{/}">トップへ戻る</a> <!-- トップへ戻る -->
        <a th:href="@{/users/new}">新規作成</a> <!-- 新規作成画面へ -->
      </div>
    </header>

    <div th:if="${error != null and !#strings.isEmpty(error)}" class="alert alert-error" th:text="${error}"></div> <!-- エラー -->
    <div th:if="${message != null and !#strings.isEmpty(message)}" class="alert alert-info" th:text="${message}"></div> <!-- 成功通知 -->

    <section class="panel">
      <div class="table-tools row"> <!-- 検索/絞り込み -->
        <label>ユーザー名検索
          <input id="user-search-input" type="search" placeholder="例: tanaka" autocomplete="off" />
        </label>
        <label>ロール絞り込み
          <select id="role-filter-select">
            <option value="">すべて</option>
            <option value="ROLE_USER">ROLE_USER</option>
            <option value="ROLE_ADMIN">ROLE_ADMIN</option>
          </select>
        </label>
        <p id="user-filter-result" class="muted filter-result"></p> <!-- 表示件数 -->
      </div>

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>ユーザー名</th>
            <th>ロール</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr th:if="${#lists.isEmpty(users)}"> <!-- データ0件時の表示 -->
            <td colspan="4" class="muted">ユーザーがいません。</td>
          </tr>
          <tr th:each="u : ${users}"
              class="js-user-row"
              th:attr="data-username=${#strings.toLowerCase(u.username)},data-role=${u.role}"> <!-- JS絞り込み用データ -->
            <td th:text="${u.id}">1</td>
            <td th:text="${u.username}">user1</td>
            <td th:text="${u.role}">ROLE_USER</td>
            <td>
              <a th:href="@{|/users/${u.id}/edit|}">編集</a> <!-- 編集画面 -->
              <form method="post"
                    th:action="@{|/users/${u.id}/delete|}"
                    style="display:inline"
                    class="js-delete-user-form"
                    th:attr="data-username=${u.username}"> <!-- 削除確認用データ -->
                <button type="submit" class="danger">削除</button>
              </form>
            </td>
          </tr>
          <tr id="no-match-row" hidden> <!-- 絞り込み結果0件 -->
            <td colspan="4" class="muted">条件に一致するユーザーがいません。</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
  <script th:src="@{/users.js}" defer></script> <!-- users一覧画面専用JS -->
</body>
</html>
```

2) `users.js` を新規作成してください。

```javascript
document.addEventListener("DOMContentLoaded", () => {
  setupDeleteConfirmation(); // 削除確認
  setupUserTableFilter(); // 一覧絞り込み
});

function setupDeleteConfirmation() {
  const deleteForms = document.querySelectorAll("form.js-delete-user-form");

  deleteForms.forEach((form) => {
    form.addEventListener("submit", (event) => {
      const username = form.dataset.username || "このユーザー";
      const accepted = window.confirm(`ユーザー「${username}」を削除します。よろしいですか？`);
      if (!accepted) {
        event.preventDefault(); // キャンセル時は送信しない
      }
    });
  });
}

function setupUserTableFilter() {
  const searchInput = document.getElementById("user-search-input");
  const roleSelect = document.getElementById("role-filter-select");
  const resultText = document.getElementById("user-filter-result");
  const noMatchRow = document.getElementById("no-match-row");
  const rows = Array.from(document.querySelectorAll("tr.js-user-row"));

  if (!(searchInput instanceof HTMLInputElement) ||
      !(roleSelect instanceof HTMLSelectElement) ||
      !(resultText instanceof HTMLElement) ||
      !(noMatchRow instanceof HTMLTableRowElement) ||
      rows.length === 0) {
    return;
  }

  const applyFilter = () => {
    const keyword = searchInput.value.trim().toLowerCase();
    const selectedRole = roleSelect.value;
    let visibleCount = 0;

    rows.forEach((row) => {
      const username = (row.dataset.username || "").toLowerCase();
      const role = row.dataset.role || "";
      const matchedKeyword = keyword === "" || username.includes(keyword);
      const matchedRole = selectedRole === "" || role === selectedRole;
      const visible = matchedKeyword && matchedRole;
      row.hidden = !visible;

      if (visible) {
        visibleCount += 1;
      }
    });

    noMatchRow.hidden = visibleCount > 0;
    resultText.textContent = `表示件数: ${visibleCount}件 / ${rows.length}件`;
  };

  searchInput.addEventListener("input", applyFilter);
  roleSelect.addEventListener("change", applyFilter);
  applyFilter(); // 初期表示
}
```

3) `styles.css` の `.row` 定義の直後に、以下を追記してください。

```css
.table-tools {
  justify-content: space-between;
  margin-bottom: 12px;
}

.table-tools label {
  min-width: 220px;
}

.filter-result {
  margin: 0;
  margin-left: auto;
}

@media (max-width: 640px) {
  .table-tools {
    align-items: stretch;
  }

  .table-tools label {
    min-width: 100%;
  }

  .filter-result {
    margin-left: 0;
  }
}
```

理解ポイント:
- 削除確認ダイアログは「誤操作防止」の最小UI改善
- 一覧絞り込みはサーバーへ再リクエストせず、クライアント側で表示だけ切り替える
- `data-*` 属性を使うと、テンプレートの値をJavaScriptで安全に参照できる

#### Phase 2-5: `user-form.html` を作る（ユーザー作成/編集画面）
作成ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/user-form.html`

新規作成してください（既にある場合は全文置き換え）。

```html
<!doctype html> <!-- HTML5文書宣言 -->
<html lang="ja" xmlns:th="http://www.thymeleaf.org"> <!-- Thymeleaf有効化 -->
<head>
  <meta charset="utf-8" /> <!-- 文字コード -->
  <meta name="viewport" content="width=device-width, initial-scale=1" /> <!-- モバイル表示対応 -->
  <title>ユーザー編集</title> <!-- ページタイトル -->
  <link rel="stylesheet" th:href="@{/styles.css}" /> <!-- 共通CSS -->
</head>
<body>
  <div class="container">
    <header>
      <h1 th:text="${mode == 'create' ? 'ユーザー作成' : 'ユーザー編集'}">ユーザー作成</h1> <!-- モード別タイトル -->
      <a th:href="@{/users}">一覧へ戻る</a> <!-- 一覧へ戻る -->
    </header>

    <section class="panel">
      <form th:action="@{${formAction}}" method="post" th:object="${userForm}"> <!-- 作成/更新共通フォーム -->
        <div class="row">
          <label>ユーザー名
            <input type="text" th:field="*{username}" /> <!-- userForm.username -->
          </label>
          <label>パスワード
            <input type="password" th:field="*{password}" placeholder="変更しない場合は空欄" /> <!-- 更新時は空欄維持を許可 -->
          </label>
          <label>ロール
            <select th:field="*{role}"> <!-- userForm.role -->
              <option value="ROLE_USER">ROLE_USER</option>
              <option value="ROLE_ADMIN">ROLE_ADMIN</option>
            </select>
          </label>
        </div>
        <div th:if="${#fields.hasErrors('*')}" class="alert alert-error"> <!-- バリデーション/業務エラー -->
          <ul>
            <li th:each="err : ${#fields.errors('*')}" th:text="${err}">error</li>
          </ul>
        </div>
        <button type="submit" th:text="${mode == 'create' ? '作成' : '更新'}">作成</button> <!-- モード別ボタン -->
      </form>
    </section>
  </div>
</body>
</html>
```

理解ポイント:
- `th:object="${userForm}"` でフォームとJavaオブジェクトを結びつける
- `formAction` を切り替えて新規/更新を同一テンプレートで使い回す
- `#fields.errors('*')` で入力エラーをまとめて表示する

#### Phase 2-6: `HomeController.java` を編集（ログインユーザー情報を表示）
編集ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/HomeController.java`

全文を以下に置き換えてください。

```java
package com.shinesoft.attendance.web; // Web（Controller）層のパッケージ

import java.security.Principal; // ログイン中ユーザー名を取得するために使う
import java.time.LocalDate; // 今日の日付表示に使う

import org.springframework.stereotype.Controller; // Controllerとして登録
import org.springframework.ui.Model; // 画面へ値を渡す
import org.springframework.web.bind.annotation.GetMapping; // GETマッピング
import org.springframework.web.bind.annotation.ModelAttribute; // フラッシュ属性の受け取りに使う
import org.springframework.web.bind.annotation.PostMapping; // POSTマッピング
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // リダイレクト時メッセージ

import com.shinesoft.attendance.domain.Attendance; // 今日の勤怠データ
import com.shinesoft.attendance.domain.AttendanceStatus; // 勤怠状態
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.service.AttendanceService; // 勤怠業務
import com.shinesoft.attendance.service.UserService; // ユーザー業務

@Controller // 画面制御クラス
public class HomeController {
    private final AttendanceService service; // 勤怠処理
    private final UserService userService; // ユーザー処理

    public HomeController(AttendanceService service, UserService userService) {
        this.service = service; // 依存注入
        this.userService = userService; // 依存注入
    }

    @GetMapping("/") // トップ画面
    public String index(Model model,
                        @ModelAttribute("error") String error,
                        @ModelAttribute("message") String message,
                        Principal principal) {
        var user = userService.getByUsername(principal.getName()); // ログイン中ユーザーを取得
        Attendance today = service.getTodayAttendance(user.getId()); // 当日の勤怠データ
        AttendanceStatus status = today == null ? AttendanceStatus.NOT_STARTED : today.getStatus(); // 状態決定

        model.addAttribute("workDate", LocalDate.now()); // 日付
        model.addAttribute("username", user.getUsername()); // 画面表示用ユーザー名
        model.addAttribute("isAdmin", "ROLE_ADMIN".equals(user.getRole())); // 管理者判定（メニュー表示に使用）
        model.addAttribute("status", status); // 状態本体
        model.addAttribute("statusLabel", statusLabel(status)); // 状態表示文字
        model.addAttribute("statusClass", statusClass(status)); // 状態バッジCSS
        model.addAttribute("startTime", today != null ? today.getStartTime() : null); // 出勤時刻
        model.addAttribute("endTime", today != null ? today.getEndTime() : null); // 退勤時刻
        model.addAttribute("error", error); // 失敗メッセージ
        model.addAttribute("message", message); // 成功メッセージ

        return "index"; // templates/index.html
    }

    @PostMapping("/clock-in") // 出勤
    public String clockIn(RedirectAttributes redirectAttributes, Principal principal) {
        var user = userService.getByUsername(principal.getName()); // ログイン中ユーザー
        try {
            service.clockIn(user.getId()); // 出勤処理
            redirectAttributes.addFlashAttribute("message", "出勤しました");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage()); // 業務エラー表示
        }
        return "redirect:/"; // トップへ戻る
    }

    @PostMapping("/clock-out") // 退勤
    public String clockOut(RedirectAttributes redirectAttributes, Principal principal) {
        var user = userService.getByUsername(principal.getName()); // ログイン中ユーザー
        try {
            service.clockOut(user.getId()); // 退勤処理
            redirectAttributes.addFlashAttribute("message", "退勤しました");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage()); // 業務エラー表示
        }
        return "redirect:/"; // トップへ戻る
    }

    private String statusClass(AttendanceStatus status) { // 状態に応じたCSSクラスを返す
        return switch (status) {
            case WORKING -> "status-badge status-working";
            case FINISHED -> "status-badge status-finished";
            default -> "status-badge";
        };
    }

    private String statusLabel(AttendanceStatus status) { // 状態に応じた表示ラベル
        return switch (status) {
            case WORKING -> "出勤中";
            case FINISHED -> "退勤済み";
            default -> "未出勤";
        };
    }
}
```

理解ポイント:
- `Principal` で「誰がログイン中か」を取得できる
- `isAdmin` をModelに渡して画面側で管理者メニューを出し分ける
- 出勤/退勤処理は従来どおりServiceに委譲する

#### Phase 2-7: `index.html` を編集（管理者メニューを表示）
編集ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/index.html`

全文を以下に置き換えてください。

```html
<!doctype html> <!-- HTML5文書宣言 -->
<html lang="ja" xmlns:th="http://www.thymeleaf.org"> <!-- Thymeleaf有効化 -->
<head>
  <meta charset="utf-8" /> <!-- 文字コード -->
  <meta name="viewport" content="width=device-width, initial-scale=1" /> <!-- モバイル表示対応 -->
  <title>勤怠管理（MVP）</title> <!-- ページタイトル -->
  <link rel="stylesheet" th:href="@{/styles.css}" /> <!-- 共通CSS -->
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理システム（MVP）</h1>
      <p class="subtitle">研修用 / ログインあり</p>
      <div class="row">
        <span class="muted">ログイン中: <strong th:text="${username}">user1</strong></span> <!-- ログイン名表示 -->
        <a th:href="@{/attendances}">勤怠一覧</a> <!-- 一般一覧リンク -->
        <a th:if="${isAdmin}" th:href="@{/users}">アカウント管理</a> <!-- 管理者のみ表示 -->
        <a th:if="${isAdmin}" th:href="@{/admin/attendances}">勤怠管理</a> <!-- 管理者のみ表示 -->
        <form method="post" th:action="@{/logout}"> <!-- ログアウト -->
          <button type="submit" class="danger">ログアウト</button>
        </form>
      </div>
    </header>

    <div th:if="${error != null and !#strings.isEmpty(error)}" class="alert alert-error" th:text="${error}"></div> <!-- エラー -->
    <div th:if="${message != null and !#strings.isEmpty(message)}" class="alert alert-info" th:text="${message}"></div> <!-- 成功通知 -->

    <section class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <span th:class="${statusClass}" th:text="${statusLabel}">未出勤</span> <!-- 状態表示 -->
      </div>
      <p>日付: <span th:text="${workDate}">2026-02-05</span></p>
      <p>出勤時刻: <span th:text="${startTime != null ? #temporals.format(startTime, 'HH:mm:ss') : '-'}">-</span></p>
      <p>退勤時刻: <span th:text="${endTime != null ? #temporals.format(endTime, 'HH:mm:ss') : '-'}">-</span></p>

      <div class="row">
        <form th:if="${status.name() == 'NOT_STARTED'}" method="post" th:action="@{/clock-in}"> <!-- 未出勤なら出勤ボタン -->
          <button type="submit">出勤</button>
        </form>
        <form th:if="${status.name() == 'WORKING'}" method="post" th:action="@{/clock-out}"> <!-- 出勤中なら退勤ボタン -->
          <button type="submit">退勤</button>
        </form>
      </div>
    </section>

    <section class="panel">
      <h2>業務ルール（抜粋）</h2>
      <ul>
        <li>同日に複数回の出勤は不可</li>
        <li>未出勤で退勤は不可</li>
        <li>退勤後に再度退勤は不可</li>
      </ul>
      <p class="muted">※ エラーは画面上部に表示されます。</p>
    </section>
  </div>
</body>
</html>
```

理解ポイント:
- `th:if="${isAdmin}"` で管理者メニューを表示制御する
- 出勤/退勤ボタンも状態に応じて出し分ける
- ログアウトは `POST /logout` で実行する

#### Phase 2完了チェック: ユーザー管理

```bash
mvn compile
```

- 管理者でユーザー一覧を表示できる
- ユーザーを1件作成し、入力値が保存される
- ユーザー作成を `UserController -> UserService -> UserRepository` の順で追跡できる
- ここまで確認してからPhase 3へ進む

### Phase 3: 管理者の勤怠編集
新規作成ファイル（フルパス）:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java`
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/form/AdminAttendanceForm.java`
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/admin-attendances.html`
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/admin-attendance-form.html`

既存編集ファイル（フルパス）:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/service/AttendanceService.java`
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/AttendanceController.java`
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java`
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/attendances.html`

コードの意味（このフェーズで理解すること）:
- `AdminAttendanceController.java`:
  - 管理者だけが勤怠履歴を検索・編集できる画面制御
- `AdminAttendanceForm.java`:
  - 勤怠編集フォーム入力（日時・状態）を受け取る
- `admin-attendances.html`, `admin-attendance-form.html`:
  - 管理者用の勤怠一覧・編集画面を表示する
- `AttendanceService.java`:
  - 管理者編集用の業務処理（更新時の整合性チェック）を追加する
- `AttendanceController.java`, `attendances.html`:
  - 一般ユーザー向け勤怠一覧との整合を保つ
- `AttendanceRepository.java`:
  - 管理者画面で `user` を同時取得するクエリを提供する

`AttendanceServiceTest.java`の発展版はLesson5Cで作成します。Lesson5Bでは管理機能の実装とコード追跡に集中します。

#### Phase 3-1: `AdminAttendanceController.java` を作る（管理者勤怠画面の入口）
作成ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java`

新規作成してください（既にある場合は全文置き換え）。

```java
package com.shinesoft.attendance.web; // 管理者向け画面制御を置くパッケージ

import java.util.List; // 一覧表示で使用

import jakarta.validation.Valid; // 入力バリデーションを有効化

import org.springframework.stereotype.Controller; // Controller登録
import org.springframework.ui.Model; // 画面へ値を渡す
import org.springframework.validation.BindingResult; // バリデーション結果を受ける
import org.springframework.web.bind.annotation.*; // Mapping系アノテーション
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // リダイレクト時メッセージ

import com.shinesoft.attendance.domain.Attendance; // 勤怠エンティティ
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.service.AttendanceService; // 勤怠業務ロジック
import com.shinesoft.attendance.service.UserService; // ユーザー情報取得に使う
import com.shinesoft.attendance.web.form.AdminAttendanceForm; // 管理者編集フォーム

@Controller // Spring MVC Controller
@RequestMapping("/admin/attendances") // 管理者勤怠URLを担当
public class AdminAttendanceController {
    private final AttendanceService attendanceService; // 勤怠処理
    private final UserService userService; // ユーザー処理

    public AdminAttendanceController(AttendanceService attendanceService, UserService userService) {
        this.attendanceService = attendanceService; // 依存注入
        this.userService = userService; // 依存注入
    }

    @GetMapping // GET /admin/attendances（一覧）
    public String list(Model model,
                       @ModelAttribute("error") String error,
                       @ModelAttribute("message") String message) {
        List<Attendance> attendances = attendanceService.listAllAttendances(); // 全ユーザー勤怠を取得
        model.addAttribute("attendances", attendances); // 画面へ一覧を渡す
        model.addAttribute("error", error); // エラー表示
        model.addAttribute("message", message); // 成功表示
        return "admin-attendances"; // templates/admin-attendances.html
    }

    @GetMapping("/{id}/edit") // GET /admin/attendances/{id}/edit（編集画面）
    public String edit(@PathVariable("id") Long id,
                       @ModelAttribute("form") AdminAttendanceForm form,
                       Model model) {
        Attendance attendance = attendanceService.getAttendance(id); // 編集対象を取得
        form.setUserId(attendance.getUser().getId()); // userIdをフォームへ
        form.setUsername(attendance.getUser().getUsername()); // 表示用ユーザー名
        form.setWorkDate(attendance.getWorkDate()); // 勤務日
        form.setStartTime(attendance.getStartTime()); // 出勤時刻
        form.setEndTime(attendance.getEndTime()); // 退勤時刻
        form.setStatus(attendance.getStatus()); // 状態

        model.addAttribute("attendanceId", id); // フォーム送信先組み立てに使う
        return "admin-attendance-form"; // templates/admin-attendance-form.html
    }

    @PostMapping("/{id}") // POST /admin/attendances/{id}（更新実行）
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("form") AdminAttendanceForm form,
                         BindingResult binding,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (binding.hasErrors()) { // 入力チェックエラー
            if (form.getUserId() != null) {
                form.setUsername(userService.get(form.getUserId()).getUsername()); // 再表示用ユーザー名を復元
            }
            model.addAttribute("attendanceId", id);
            return "admin-attendance-form";
        }

        try {
            attendanceService.updateAttendance(id,
                form.getUserId(),
                form.getWorkDate(),
                form.getStartTime(),
                form.getEndTime(),
                form.getStatus()); // 業務更新処理
            redirectAttributes.addFlashAttribute("message", "勤怠を更新しました");
            return "redirect:/admin/attendances";
        } catch (BusinessException ex) {
            binding.reject("business", ex.getMessage()); // 業務ルール違反
            if (form.getUserId() != null) {
                form.setUsername(userService.get(form.getUserId()).getUsername()); // 再表示時に復元
            }
            model.addAttribute("attendanceId", id);
            return "admin-attendance-form";
        }
    }
}
```

理解ポイント:
- 管理者の勤怠一覧と編集入口を `AdminAttendanceController` に集約する
- 入力値の形式チェックは `@Valid + BindingResult`
- 業務ルール違反は `BusinessException` で返して画面表示する

#### Phase 3-2: `AdminAttendanceForm.java` を作る（管理者勤怠編集フォーム）
作成ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/form/AdminAttendanceForm.java`

新規作成してください（既にある場合は全文置き換え）。

```java
package com.shinesoft.attendance.web.form; // 管理者フォームのパッケージ

import java.time.LocalDate; // 勤務日
import java.time.LocalDateTime; // 出勤/退勤時刻

import org.springframework.format.annotation.DateTimeFormat; // 文字列->日時変換

import jakarta.validation.constraints.NotNull; // 必須入力チェック

import com.shinesoft.attendance.domain.AttendanceStatus; // 状態Enum

public class AdminAttendanceForm {
    @NotNull // ユーザーID必須
    private Long userId;

    @NotNull // 勤務日必須
    @DateTimeFormat(pattern = "yyyy-MM-dd") // date input形式
    private LocalDate workDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") // datetime-local input形式
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") // datetime-local input形式
    private LocalDateTime endTime;

    @NotNull // 状態必須
    private AttendanceStatus status;

    private String username; // 画面表示専用（保存対象ではない）

    public Long getUserId() { return userId; }
    public LocalDate getWorkDate() { return workDate; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public AttendanceStatus getStatus() { return status; }
    public String getUsername() { return username; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public void setUsername(String username) { this.username = username; }
}
```

理解ポイント:
- フォームは「画面入力の受け取り専用オブジェクト」
- `@DateTimeFormat` で `<input type="date/datetime-local">` の値を変換する
- `username` は表示用で、更新処理自体には `userId` を使う

#### Phase 3-3: `admin-attendances.html` を作る（管理者勤怠一覧）
作成ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/admin-attendances.html`

新規作成してください（既にある場合は全文置き換え）。

```html
<!doctype html> <!-- HTML5文書宣言 -->
<html lang="ja" xmlns:th="http://www.thymeleaf.org"> <!-- Thymeleaf有効化 -->
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>勤怠管理（管理者）</title>
  <link rel="stylesheet" th:href="@{/styles.css}" />
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理（管理者）</h1>
      <div class="row">
        <a th:href="@{/}">トップへ戻る</a> <!-- トップへ戻る -->
        <a th:href="@{/users}">アカウント管理</a> <!-- ユーザー管理へ -->
      </div>
    </header>

    <div th:if="${error != null and !#strings.isEmpty(error)}" class="alert alert-error" th:text="${error}"></div>
    <div th:if="${message != null and !#strings.isEmpty(message)}" class="alert alert-info" th:text="${message}"></div>

    <section class="panel">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>ユーザー</th>
            <th>日付</th>
            <th>出勤時刻</th>
            <th>退勤時刻</th>
            <th>状態</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr th:if="${#lists.isEmpty(attendances)}"> <!-- データ0件 -->
            <td colspan="7" class="muted">勤怠データがありません。</td>
          </tr>
          <tr th:each="att : ${attendances}"> <!-- 一覧表示 -->
            <td th:text="${att.id}">1</td>
            <td th:text="${att.user.username}">user1</td>
            <td th:text="${att.workDate}">2026-02-06</td>
            <td th:text="${att.startTime != null ? #temporals.format(att.startTime, 'HH:mm') : '-'}">-</td>
            <td th:text="${att.endTime != null ? #temporals.format(att.endTime, 'HH:mm') : '-'}">-</td>
            <td th:text="${att.status == T(com.shinesoft.attendance.domain.AttendanceStatus).WORKING ? '出勤中' : (att.status == T(com.shinesoft.attendance.domain.AttendanceStatus).FINISHED ? '退勤済み' : '未出勤')}">未出勤</td>
            <td>
              <a th:href="@{|/admin/attendances/${att.id}/edit|}">編集</a> <!-- 編集画面へ -->
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</body>
</html>
```

理解ポイント:
- 全ユーザーの勤怠を管理者が横断で見られる画面
- `att.user.username` のように関連エンティティの値を表示できる
- 行ごとに編集リンクを置いて詳細編集へ遷移する

#### Phase 3-4: `admin-attendance-form.html` を作る（管理者勤怠編集画面）
作成ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/admin-attendance-form.html`

新規作成してください（既にある場合は全文置き換え）。

```html
<!doctype html> <!-- HTML5文書宣言 -->
<html lang="ja" xmlns:th="http://www.thymeleaf.org"> <!-- Thymeleaf有効化 -->
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>勤怠編集（管理者）</title>
  <link rel="stylesheet" th:href="@{/styles.css}" />
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠編集（管理者）</h1>
      <a th:href="@{/admin/attendances}">一覧へ戻る</a>
    </header>

    <section class="panel">
      <form th:action="@{|/admin/attendances/${attendanceId}|}" method="post" th:object="${form}">
        <div class="row">
          <label>ユーザー
            <input type="text" th:value="${form.username}" readonly /> <!-- 表示のみ -->
          </label>
          <label>日付
            <input type="date" th:field="*{workDate}" />
          </label>
          <label>出勤時刻
            <input type="datetime-local" th:field="*{startTime}" />
          </label>
          <label>退勤時刻
            <input type="datetime-local" th:field="*{endTime}" />
          </label>
          <label>状態
            <select th:field="*{status}">
              <option value="NOT_STARTED">未出勤</option>
              <option value="WORKING">出勤中</option>
              <option value="FINISHED">退勤済み</option>
            </select>
          </label>
        </div>
        <input type="hidden" th:field="*{userId}" /> <!-- 更新対象ユーザーID -->

        <div th:if="${#fields.hasErrors('*')}" class="alert alert-error"> <!-- 入力/業務エラー -->
          <ul>
            <li th:each="err : ${#fields.errors('*')}" th:text="${err}">error</li>
          </ul>
        </div>
        <button type="submit">更新</button>
      </form>
      <p class="muted">※ 状態と時刻の整合が取れていない場合はエラーになります。</p>
    </section>
  </div>
</body>
</html>
```

理解ポイント:
- 管理者は状態と時刻を直接修正できる
- `userId` は hidden で保持し、更新処理で利用する
- 整合性チェック（未出勤なのに時刻あり等）はService側で実施する

#### Phase 3-5: `AttendanceService.java` を編集（管理者更新ロジック追加）
編集ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/service/AttendanceService.java`

全文を以下に置き換えてください。

```java
package com.shinesoft.attendance.service; // Service層パッケージ

import java.time.LocalDate; // 日付
import java.time.LocalDateTime; // 日時
import java.util.List; // 一覧取得

import org.slf4j.Logger; // ログ出力
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service; // Service登録
import org.springframework.transaction.annotation.Transactional; // 更新処理をトランザクション化

import com.shinesoft.attendance.domain.Attendance; // 勤怠エンティティ
import com.shinesoft.attendance.domain.AttendanceStatus; // 勤怠状態Enum
import com.shinesoft.attendance.domain.User; // ユーザーエンティティ
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.repository.AttendanceRepository; // 勤怠DBアクセス
import com.shinesoft.attendance.repository.UserRepository; // ユーザーDBアクセス

@Service
public class AttendanceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class); // ロガー

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    public Attendance getTodayAttendance(Long userId) { // 当日勤怠取得（無ければnull）
        return attendanceRepository.findByUser_IdAndWorkDate(userId, LocalDate.now())
                .orElse(null);
    }

    public Attendance getAttendance(Long id) { // ID指定取得（管理者編集で使用）
        return attendanceRepository.findWithUserById(id)
            .orElseThrow(() -> new BusinessException("勤怠が存在しません"));
    }

    public List<Attendance> listAttendances(Long userId) { // ユーザー本人向け一覧
        return attendanceRepository.findByUser_IdOrderByWorkDateDesc(userId);
    }

    public List<Attendance> listAllAttendances() { // 管理者向け全件一覧
        return attendanceRepository.findAllByOrderByWorkDateDesc();
    }

    @Transactional
    public Attendance clockIn(Long userId) { // 出勤処理
        LocalDate today = LocalDate.now();
        Attendance existing = attendanceRepository.findByUser_IdAndWorkDate(userId, today).orElse(null);
        if (existing != null) {
            throw new BusinessException("すでに出勤済みです");
        }

        User user = getUser(userId);
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setWorkDate(today);
        attendance.setStartTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.WORKING);

        Attendance saved = attendanceRepository.save(attendance);
        log.info("Clock in userId={}, date={}, time={}", userId, today, saved.getStartTime());
        return saved;
    }

    @Transactional
    public Attendance clockOut(Long userId) { // 退勤処理
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUser_IdAndWorkDate(userId, today)
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
        log.info("Clock out userId={}, date={}, time={}", userId, today, saved.getEndTime());
        return saved;
    }

    @Transactional
    public Attendance updateAttendance(Long attendanceId, // 管理者編集処理
                                       Long userId,
                                       LocalDate workDate,
                                       LocalDateTime startTime,
                                       LocalDateTime endTime,
                                       AttendanceStatus status) {
        if (userId == null || workDate == null || status == null) {
            throw new BusinessException("ユーザー、勤務日、状態は必須です");
        }

        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new BusinessException("勤怠が存在しません"));

        var user = getUser(userId);

        var existing = attendanceRepository.findByUser_IdAndWorkDate(userId, workDate).orElse(null); // 同日重複チェック
        if (existing != null && !existing.getId().equals(attendanceId)) {
            throw new BusinessException("同じ日付の勤怠が既に存在します");
        }

        validateStatusAndTimes(workDate, status, startTime, endTime); // 状態と時刻の整合チェック

        attendance.setUser(user);
        attendance.setWorkDate(workDate);
        attendance.setStartTime(startTime);
        attendance.setEndTime(endTime);
        attendance.setStatus(status);
        return attendanceRepository.save(attendance);
    }

    private User getUser(Long userId) { // 共通ユーザー取得
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("ユーザーが存在しません"));
    }

    private void validateStatusAndTimes(LocalDate workDate,
                                        AttendanceStatus status, // 整合性ルール
                                        LocalDateTime startTime,
                                        LocalDateTime endTime) {
        switch (status) {
            case NOT_STARTED -> {
                if (startTime != null || endTime != null) {
                    throw new BusinessException("未出勤の時刻は空にしてください");
                }
            }
            case WORKING -> {
                if (startTime == null || endTime != null) {
                    throw new BusinessException("出勤中は開始時刻のみ必要です");
                }
                if (!startTime.toLocalDate().equals(workDate)) {
                    throw new BusinessException("開始時刻の日付は勤務日と一致させてください");
                }
            }
            case FINISHED -> {
                if (startTime == null || endTime == null) {
                    throw new BusinessException("退勤済みは開始・終了時刻が必要です");
                }
                if (!startTime.toLocalDate().equals(workDate)) {
                    throw new BusinessException("開始時刻の日付は勤務日と一致させてください");
                }
                if (endTime.isBefore(startTime)) {
                    throw new BusinessException("終了時刻は開始時刻以降にしてください");
                }
            }
            default -> {
            }
        }
    }
}
```

補足（重要）:
- `AttendanceRepository.java` は、下記の完成形に「全文置き換え」してください。
- 更新対象の取得は標準 `findById(...)`、画面表示でユーザーも必要な取得は `@EntityGraph` 付き `findWithUserById(...)` を使い分けます。

編集ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java`

手順:
1. `AttendanceRepository.java` を開く。
2. ファイル内容を次の完成形に全文置き換える。
3. 保存後に `mvn compile`（可能なら `mvn test` も）を実行してエラーがないことを確認する。

```java
// Repositoryインターフェースを置くパッケージ
package com.shinesoft.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shinesoft.attendance.domain.Attendance;

// AttendanceテーブルのDB操作窓口
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // userId + 勤務日で当日レコードを1件取得（出勤/退勤判定に使う）
    Optional<Attendance> findByUser_IdAndWorkDate(Long userId, LocalDate workDate);

    // 指定ユーザーの履歴を勤務日降順で取得
    // 直近データを上に表示したい一覧画面向け
    List<Attendance> findByUser_IdOrderByWorkDateDesc(Long userId);

    // 管理者向けに全ユーザー分の履歴を勤務日降順で取得
    @EntityGraph(attributePaths = "user")
    List<Attendance> findAllByOrderByWorkDateDesc();

    // 管理者編集でユーザー情報も同時取得
    @EntityGraph(attributePaths = "user")
    Optional<Attendance> findWithUserById(Long id);

    // ユーザー削除前の参照整合チェック
    boolean existsByUser_Id(Long userId);
}
```

理解ポイント:
- Lesson5追加の核は `updateAttendance(...)` と `validateStatusAndTimes(...)`
- 管理者編集でも「同日重複」「状態と時刻の整合」「開始・終了の前後関係」を強制する
- `@EntityGraph` で画面に必要なユーザー情報をServiceのトランザクション外でも参照できる状態にする
- 既存の出勤/退勤ロジックを壊さずに拡張している

#### Phase 3-6: `AttendanceController.java` を編集（ログインユーザー基準で一覧表示）
編集ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/AttendanceController.java`

全文を以下に置き換えてください。

```java
package com.shinesoft.attendance.web; // Web層パッケージ

import org.springframework.stereotype.Controller; // Controller登録
import org.springframework.ui.Model; // 画面へ値を渡す
import org.springframework.web.bind.annotation.GetMapping; // GETマッピング
import org.springframework.web.bind.annotation.RequestMapping; // 共通URL

import com.shinesoft.attendance.service.AttendanceService; // 勤怠業務
import com.shinesoft.attendance.service.UserService; // ユーザー業務

import java.security.Principal; // ログインユーザー名取得

@Controller
@RequestMapping("/attendances") // /attendances を担当
public class AttendanceController {
    private final AttendanceService service;
    private final UserService userService;

    public AttendanceController(AttendanceService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping // GET /attendances
    public String list(Model model, Principal principal) {
        var user = userService.getByUsername(principal.getName()); // ログイン中ユーザー
        model.addAttribute("attendances", service.listAttendances(user.getId())); // 本人の履歴
        model.addAttribute("username", user.getUsername()); // 画面表示用ユーザー名
        return "attendances"; // templates/attendances.html
    }
}
```

理解ポイント:
- Lesson4の固定ユーザーID方式から、ログインユーザー基準へ変更している
- 同じ `/attendances` でも「誰の履歴か」が認証連動で決まる

#### Phase 3-7: `attendances.html` を編集（ログインユーザー表示）
編集ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/attendances.html`

全文を以下に置き換えてください。

```html
<!doctype html> <!-- HTML5文書宣言 -->
<html lang="ja" xmlns:th="http://www.thymeleaf.org"> <!-- Thymeleaf有効化 -->
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>勤怠一覧</title>
  <link rel="stylesheet" th:href="@{/styles.css}" />
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠一覧</h1>
      <p class="subtitle"><span th:text="${username}">user1</span> の履歴（降順）</p> <!-- 誰の履歴か明示 -->
      <a th:href="@{/}">トップへ戻る</a>
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
          <tr th:if="${#lists.isEmpty(attendances)}"> <!-- データ0件 -->
            <td colspan="4" class="muted">まだ勤怠履歴がありません。</td>
          </tr>
          <tr th:each="att : ${attendances}"> <!-- 一覧描画 -->
            <td th:text="${att.workDate}">2026-02-05</td>
            <td th:text="${att.startTime != null ? #temporals.format(att.startTime, 'HH:mm:ss') : '-'}">-</td>
            <td th:text="${att.endTime != null ? #temporals.format(att.endTime, 'HH:mm:ss') : '-'}">-</td>
            <td th:text="${att.status == T(com.shinesoft.attendance.domain.AttendanceStatus).WORKING ? '出勤中' : (att.status == T(com.shinesoft.attendance.domain.AttendanceStatus).FINISHED ? '退勤済み' : '未出勤')}">未出勤</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</body>
</html>
```

理解ポイント:
- 一覧ヘッダでログインユーザー名を表示し、閲覧対象を明確化
- 表示ロジックは `attendances` モデル属性に集約

#### Phase 3完了チェック: 管理者勤怠管理

```bash
mvn compile
```

- 管理者が全ユーザーの勤怠を一覧・編集できる
- 一般ユーザーが管理画面へアクセスすると`403`になる
- 勤怠更新を `AdminAttendanceController -> AttendanceService -> AttendanceRepository` の順で追跡できる
- ここまで確認してからLesson5Cへ進む

---

## Lesson5B 完了条件

- `mvn compile` が成功する
- 管理者がユーザーを作成・更新できる
- 管理者が全ユーザーの勤怠を一覧・編集できる
- 一般ユーザーが管理画面へアクセスすると `403` になる
- ユーザー作成または勤怠更新をControllerからRepositoryまで追跡できる
- 提供されたHTML/CSS/JavaScriptの説明コメントが保持されている

完了後は [Lesson5C テスト・プロファイル・参照整合性](./lesson5c-testing-operations.md) へ進みます。
