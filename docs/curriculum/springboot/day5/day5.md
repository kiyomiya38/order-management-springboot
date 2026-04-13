# Day5（7/15）ログイン + 管理者機能 + テスト（Day4から拡張）

## 目的（Day5でできるようになること）
- ログイン機能（Spring Security）を実装できる
- 一般ユーザー / 管理者でアクセス権が分かれることを確認できる
- 管理者のアカウント管理・勤怠編集ができる
- `mvn test` でServiceテストを実行できる

## 前提
- Day4 を完了している
- `~/order-management-springboot/stages/day4` のトップ/一覧が動作する

---

## 0. 事前確認
```bash
java -version
mvn -version
git --version
```

---

## 1. 作業フォルダを準備（Day4を複製）
```bash
mkdir -p ~/order-management-springboot/stages/day5
cp -r ~/order-management-springboot/stages/day4/* ~/order-management-springboot/stages/day5/
cd ~/order-management-springboot/stages/day5
```

以降の `作成ファイル` は、`~/order-management-springboot` からのフルパスで表記します。  
例: `~/order-management-springboot/stages/day5/src/main/java/...`

---

## 2. ディレクトリを追加
```bash
mkdir -p ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/config
mkdir -p ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/form
mkdir -p ~/order-management-springboot/stages/day5/src/test/java/com/shinesoft/attendance/service
```

---

## 3. `pom.xml` を編集（依存追加）
作成ファイル: `~/order-management-springboot/stages/day5/pom.xml`

この章でやること（具体手順）:
1. `~/order-management-springboot/stages/day5/pom.xml` を開く
2. 実利用側の `<dependencies>`（`spring-boot-starter-web` などが並んでいるブロック）を探す
3. その `</dependencies>` の直前に、以下3つを追記する

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>
```

Day4からの追加依存:
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `spring-boot-starter-test`（testスコープ）

確認コマンド:
```bash
cd ~/order-management-springboot/stages/day5
# 依存が追記されているか確認（Git Bash）
grep -nE "spring-boot-starter-security|spring-boot-starter-validation|spring-boot-starter-test" pom.xml
# コンパイル確認（成功時は BUILD SUCCESS）
mvn compile
```

理解ポイント（10分）:
- この変更の目的:
  - Day5で必要な「認証」「入力検証」「テスト」を有効化する
- 依存の意味:
  - `spring-boot-starter-security`: ログイン/権限制御
  - `spring-boot-starter-validation`: `@Valid` / `@NotBlank` など入力検証
  - `spring-boot-starter-test`: JUnit + Spring Test
- よくあるミス:
  - 依存追加漏れで `org.springframework.security...` や `jakarta.validation...` のコンパイルエラー

---

## 4. Day5差分を手動追記（理解優先の3フェーズ）
Day5は差分が多いため、手動で一気に作ると混乱しやすいです。  
この章では、Day4コードから段階的に追記して「何が増えたか」を理解しながら進めます。

作業量の目安:
- 初学者: 6〜9時間（1日相当）
- 既にSpring経験あり: 3〜5時間

進め方ルール:
1. フェーズごとの対象ファイルだけ触る
2. 各フェーズの最後で `mvn compile` を実行してエラー0を確認
3. 1フェーズ完了ごとに最低1つ動作確認を行う
4. 30分以上詰まったら完成版 `~/order-management-springboot/src` と差分比較して修正

### Phase 1: 認証の土台（ログインと権限制御）
新規作成ファイル（フルパス）:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java`
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/AuthController.java`
- `~/order-management-springboot/stages/day5/src/main/resources/templates/login.html`

既存編集ファイル（フルパス）:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/config/DataSeeder.java`
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/domain/User.java`

コードの意味（このフェーズで理解すること）:
- `SecurityConfig.java`:
  - URLごとのアクセス権限（認可）を定義する設定クラス
  - ログイン画面URL・ログアウトURL・認証必須範囲を決める
- `AuthController.java`:
  - `/login` へアクセスされた時に `login.html` を返す画面制御クラス
- `login.html`:
  - ユーザー名/パスワードを送信するログイン画面
- `DataSeeder.java`:
  - 起動時に学習用ユーザー（`admin`, `user1`）を初期投入する
- `User.java`:
  - ユーザーの基本情報（名前・ロール・パスワード）を保持するドメイン

#### Phase 1-1: `SecurityConfig.java` を1行ずつ理解しながら作る
作成ファイル:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java`

手順:
1. まずは「URLごとのアクセス制御」と「ログイン画面指定」だけを入れる
2. 次に「DBのユーザーを使う設定（`UserDetailsService`）」を追加する
3. 最後に「パスワード暗号化方式（`PasswordEncoder`）」を追加する

コード（コメント付き・完成形）:
```java
package com.shinesoft.attendance.config; // このクラスの所属パッケージ

import org.springframework.context.annotation.Bean; // Spring管理オブジェクト（Bean）を登録する
import org.springframework.context.annotation.Configuration; // 設定クラスであることを示す
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // セキュリティ設定を組み立てる
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Spring Securityを有効化
import org.springframework.security.core.userdetails.UserDetailsService; // ユーザー情報を取得する窓口
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // BCryptでパスワードをハッシュ化
import org.springframework.security.crypto.password.PasswordEncoder; // パスワードエンコーダの共通型
import org.springframework.security.web.SecurityFilterChain; // セキュリティの実行ルール本体

import com.shinesoft.attendance.repository.UserRepository; // DBからユーザーを取得するために使う

@Configuration // このクラスを設定クラスとしてSpringに登録
@EnableWebSecurity // Spring Securityの機能をONにする
public class SecurityConfig {

    @Bean // SecurityFilterChainをBeanとして登録
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth // URLごとのアクセス権ルール開始
                .requestMatchers("/login", "/styles.css").permitAll() // ログイン画面とCSSは未ログインでも許可
                .requestMatchers("/h2-console/**").permitAll() // 学習用にH2コンソールは許可
                .requestMatchers("/users/**").hasRole("ADMIN") // /users配下は管理者だけ許可
                .requestMatchers("/admin/**").hasRole("ADMIN") // /admin配下は管理者だけ許可
                .anyRequest().authenticated() // それ以外はログイン必須
            )
            .formLogin(form -> form // フォームログイン設定
                .loginPage("/login") // 自作ログイン画面のURL
                .defaultSuccessUrl("/", true) // ログイン成功後は常にトップへ
                .permitAll() // ログイン処理そのものは誰でも実行可能
            )
            .logout(logout -> logout // ログアウト設定
                .logoutUrl("/logout") // ログアウト実行URL
                .logoutSuccessUrl("/login?logout") // ログアウト後にログイン画面へ戻す
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")) // H2コンソールだけCSRF除外
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())); // H2画面表示のため同一オリジンiframe許可
        return http.build(); // 設定を確定して返す
    }

    @Bean // ユーザー認証で使うUserDetailsServiceを登録
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> { // ログイン時に入力されたユーザー名を受け取る
            var user = userRepository.findByUsername(username) // DBからユーザー検索
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                    "User not found: " + username)); // 見つからない場合は認証失敗
            return org.springframework.security.core.userdetails.User // Spring Security用ユーザーへ変換
                .withUsername(user.getUsername()) // 認証に使うユーザー名
                .password(user.getPassword()) // DB保存済みのハッシュ化パスワード
                .roles(user.getRole().replace("ROLE_", "")) // ROLE_ADMIN -> ADMIN に変換して設定
                .build(); // UserDetailsを作成して返す
        };
    }

    @Bean // パスワード照合方式をBeanとして登録
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt方式（平文保存しない）
    }
}
```

このファイルで最低限わかってほしいこと:
1. `securityFilterChain`: 「どのURLに誰が入れるか」を決める
2. `userDetailsService`: 「ログイン時にDBから誰を探すか」を決める
3. `passwordEncoder`: 「パスワードをどう照合するか」を決める

よくあるつまずき:
- `hasRole("ADMIN")` なのに、DB側のロールが `ADMIN` だけだと認可が通らない場合がある（`ROLE_ADMIN` 保存を前提にする）
- `PasswordEncoder` 未設定だとログイン時にパスワード照合エラーになる
- `/login` を `permitAll()` し忘れるとログイン画面に到達できない

#### Phase 1-2: `AuthController.java` を作る（ログイン画面の入口）
作成ファイル:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/AuthController.java`

新規作成してください（既にある場合は全文置き換え）。

```java
package com.shinesoft.attendance.web; // 画面制御（Web）層のパッケージ

import org.springframework.stereotype.Controller; // このクラスをControllerとして登録
import org.springframework.ui.Model; // 画面へ値を渡すための箱
import org.springframework.web.bind.annotation.GetMapping; // GETリクエストを受ける
import org.springframework.web.bind.annotation.RequestParam; // クエリパラメータを受ける

@Controller // Spring MVCのControllerクラス
public class AuthController {
    @GetMapping("/login") // /login へGETアクセスされたときにこのメソッドを実行
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) { // 認証失敗時（?error が付く）
            model.addAttribute("error", "ユーザー名またはパスワードが正しくありません"); // 画面表示用エラー文言
        }
        if (logout != null) { // ログアウト直後（?logout が付く）
            model.addAttribute("message", "ログアウトしました"); // 画面表示用メッセージ
        }
        return "login"; // templates/login.html を表示
    }
}
```

理解ポイント:
- `AuthController` の役割は「ログイン画面を返す」こと
- 認証処理そのものは `SecurityConfig` とSpring Securityが実行する
- Controller側は「画面メッセージを表示するための値」をModelに詰める

#### Phase 1-3: `login.html` を作る（ログインフォーム画面）
作成ファイル:
- `~/order-management-springboot/stages/day5/src/main/resources/templates/login.html`

新規作成してください（既にある場合は全文置き換え）。

```html
<!doctype html> <!-- HTML5文書宣言 -->
<html lang="ja" xmlns:th="http://www.thymeleaf.org"> <!-- Thymeleafを使うため xmlns:th を宣言 -->
<head>
  <meta charset="utf-8" /> <!-- 文字コード -->
  <meta name="viewport" content="width=device-width, initial-scale=1" /> <!-- モバイル表示対応 -->
  <title>ログイン</title> <!-- ブラウザタブ表示名 -->
  <link rel="stylesheet" th:href="@{/styles.css}" /> <!-- 共通CSSを読み込む -->
</head>
<body>
  <div class="container"> <!-- 全体レイアウト枠 -->
    <header>
      <h1>勤怠管理システム（MVP）</h1>
      <p class="subtitle">ログインしてください</p>
    </header>

    <div th:if="${error != null and !#strings.isEmpty(error)}" class="alert alert-error" th:text="${error}"></div> <!-- エラー表示 -->
    <div th:if="${message != null and !#strings.isEmpty(message)}" class="alert alert-info" th:text="${message}"></div> <!-- 通知表示 -->

    <section class="panel">
      <form method="post" th:action="@{/login}"> <!-- POST /login へ送信（認証はSpring Securityが処理） -->
        <div class="row">
          <label>ユーザー名
            <input type="text" name="username" required /> <!-- name=username はSecurity標準パラメータ -->
          </label>
          <label>パスワード
            <input type="password" name="password" required /> <!-- name=password はSecurity標準パラメータ -->
          </label>
        </div>
        <button type="submit">ログイン</button>
      </form>
      <p class="muted">初期ユーザー: admin / admin123, user1 / password</p> <!-- 学習用の初期アカウント -->
    </section>
  </div>
</body>
</html>
```

理解ポイント:
- `th:action="@{/login}"` の送信先をSpring Securityが受ける
- `name="username"` と `name="password"` はデフォルト認証パラメータ名
- 画面上のエラー/通知は `AuthController` がModelへ詰めた値で表示する

#### Phase 1-4: `DataSeeder.java` を編集（初期ユーザー投入）
編集ファイル:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/config/DataSeeder.java`

全文を以下に置き換えてください。

```java
package com.shinesoft.attendance.config; // 設定クラス群のパッケージ

import org.springframework.boot.CommandLineRunner; // 起動直後に処理を実行するためのIF
import org.springframework.stereotype.Component; // Spring管理対象として登録

import com.shinesoft.attendance.domain.User; // Userエンティティ
import com.shinesoft.attendance.repository.UserRepository; // User保存/検索に使うRepository
import org.springframework.security.crypto.password.PasswordEncoder; // パスワードをハッシュ化するために使う

@Component // アプリ起動時に読み込まれるBean
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository; // ユーザー永続化用
    private final PasswordEncoder passwordEncoder; // 平文パスワードをそのまま保存しないための依存

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository; // 依存注入
        this.passwordEncoder = passwordEncoder; // 依存注入
    }

    @Override
    public void run(String... args) { // アプリ起動後に1回実行される
        if (userRepository.count() == 0) { // usersテーブルが空の時だけ初期投入
            User admin = new User(); // 管理者アカウント作成
            admin.setUsername("admin"); // ログインID
            admin.setPassword(passwordEncoder.encode("admin123")); // ハッシュ化して保存
            admin.setRole("ROLE_ADMIN"); // 管理者ロール
            userRepository.save(admin); // DBへ保存

            User user = new User(); // 一般ユーザー作成
            user.setUsername("user1"); // ログインID
            user.setPassword(passwordEncoder.encode("password")); // ハッシュ化して保存
            user.setRole("ROLE_USER"); // 一般ユーザーロール
            userRepository.save(user); // DBへ保存
        }
    }
}
```

理解ポイント:
- 目的は「起動直後にログイン用ユーザーを必ず作る」こと
- `passwordEncoder.encode(...)` で平文保存を回避する
- `count() == 0` で再起動時の重複投入を防ぐ

#### Phase 1-5: `User.java` を編集（認証に必要な項目を持つ）
編集ファイル:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/domain/User.java`

全文を以下に置き換えてください。

```java
package com.shinesoft.attendance.domain; // ドメイン（Entity）層のパッケージ

import jakarta.persistence.*; // JPAアノテーション一式

@Entity // DBテーブルと対応するエンティティ
@Table(name = "users") // テーブル名を users に指定
public class User {
    @Id // 主キー
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DBの自動採番を利用
    private Long id;

    @Column(nullable = false, unique = true) // 必須 + 重複不可（ログインID）
    private String username;

    @Column(nullable = false) // 必須（ハッシュ化パスワード）
    private String password;

    @Column(nullable = false) // 必須（ROLE_ADMIN / ROLE_USER など）
    private String role;

    public Long getId() { // id取得
        return id;
    }

    public String getUsername() { // username取得
        return username;
    }

    public String getPassword() { // password取得
        return password;
    }

    public String getRole() { // role取得
        return role;
    }

    public void setId(Long id) { // id設定
        this.id = id;
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
- `username` はログインIDなので `unique=true` が必要
- `password` はハッシュ化済み文字列を保持する
- `role` は認可判定（`hasRole("ADMIN")` など）に使う

完了チェック:
```bash
cd ~/order-management-springboot/stages/day5
mvn compile
```

### Phase 2: 管理者のユーザー管理
新規作成ファイル（フルパス）:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/service/UserService.java`
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/UserController.java`
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/form/UserForm.java`
- `~/order-management-springboot/stages/day5/src/main/resources/templates/users.html`
- `~/order-management-springboot/stages/day5/src/main/resources/templates/user-form.html`
- `~/order-management-springboot/stages/day5/src/main/resources/static/users.js`

既存編集ファイル（フルパス）:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/HomeController.java`
- `~/order-management-springboot/stages/day5/src/main/resources/templates/index.html`
- `~/order-management-springboot/stages/day5/src/main/resources/static/styles.css`

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
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/service/UserService.java`

新規作成してください（既にある場合は全文置き換え）。

```java
package com.shinesoft.attendance.service; // Service層のパッケージ

import java.util.List; // 一覧取得で使う

import org.springframework.security.crypto.password.PasswordEncoder; // パスワードハッシュ化に使う
import org.springframework.stereotype.Service; // Serviceクラスとして登録
import org.springframework.transaction.annotation.Transactional; // 更新処理をトランザクション化

import com.shinesoft.attendance.domain.User; // Userエンティティ
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.repository.UserRepository; // UserのDBアクセス

@Service // Spring管理対象（業務ロジック層）
public class UserService {
    private final UserRepository userRepository; // User保存/検索に使う
    private final PasswordEncoder passwordEncoder; // パスワード暗号化に使う

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository; // 依存注入
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
        userRepository.deleteById(id); // 指定IDを削除
    }
}
```

理解ポイント:
- ControllerからDB直操作せず、業務ロジックを `UserService` に集約する
- `create` / `update` で重複チェックを入れて不正データを防ぐ
- パスワードは常にハッシュ化して保存する

#### Phase 2-2: `UserController.java` を作る（管理者画面の入口）
作成ファイル:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/UserController.java`

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
        userService.delete(id); // 削除実行
        redirectAttributes.addFlashAttribute("message", "ユーザーを削除しました");
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
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/form/UserForm.java`

新規作成してください（既にある場合は全文置き換え）。

```java
package com.shinesoft.attendance.web.form; // フォームクラス用パッケージ

import jakarta.validation.constraints.NotBlank; // 必須チェックに使う

public class UserForm {
    @NotBlank // 空文字・空白のみを禁止
    private String username;

    private String password; // 更新時は空欄許可にするためNotBlankを付けない

    @NotBlank // ロールは必須
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
- `~/order-management-springboot/stages/day5/src/main/resources/templates/users.html`

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
- `~/order-management-springboot/stages/day5/src/main/resources/templates/users.html`
- `~/order-management-springboot/stages/day5/src/main/resources/static/users.js`
- `~/order-management-springboot/stages/day5/src/main/resources/static/styles.css`

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
- `~/order-management-springboot/stages/day5/src/main/resources/templates/user-form.html`

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
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/HomeController.java`

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
- `~/order-management-springboot/stages/day5/src/main/resources/templates/index.html`

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

完了チェック:
```bash
mvn compile
```

### Phase 3: 管理者の勤怠編集 + テスト
新規作成ファイル（フルパス）:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java`
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/form/AdminAttendanceForm.java`
- `~/order-management-springboot/stages/day5/src/main/resources/templates/admin-attendances.html`
- `~/order-management-springboot/stages/day5/src/main/resources/templates/admin-attendance-form.html`
- `~/order-management-springboot/stages/day5/src/test/java/com/shinesoft/attendance/service/AttendanceServiceTest.java`

既存編集ファイル（フルパス）:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/service/AttendanceService.java`
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/AttendanceController.java`
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java`
- `~/order-management-springboot/stages/day5/src/main/resources/templates/attendances.html`

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
- `AttendanceServiceTest.java`:
  - 主要業務ロジックが壊れていないことを自動確認する

#### Phase 3-1: `AdminAttendanceController.java` を作る（管理者勤怠画面の入口）
作成ファイル:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java`

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
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/form/AdminAttendanceForm.java`

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
- `~/order-management-springboot/stages/day5/src/main/resources/templates/admin-attendances.html`

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
- `~/order-management-springboot/stages/day5/src/main/resources/templates/admin-attendance-form.html`

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
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/service/AttendanceService.java`

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
        return attendanceRepository.findById(id)
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
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new BusinessException("勤怠が存在しません"));

        var user = getUser(userId);

        var existing = attendanceRepository.findByUser_IdAndWorkDate(userId, workDate).orElse(null); // 同日重複チェック
        if (existing != null && !existing.getId().equals(attendanceId)) {
            throw new BusinessException("同じ日付の勤怠が既に存在します");
        }

        validateStatusAndTimes(status, startTime, endTime); // 状態と時刻の整合チェック

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

    private void validateStatusAndTimes(AttendanceStatus status, // 整合性ルール
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
            }
            case FINISHED -> {
                if (startTime == null || endTime == null) {
                    throw new BusinessException("退勤済みは開始・終了時刻が必要です");
                }
            }
            default -> {
            }
        }
    }
}
```

補足（重要）:
- 完成版準拠では、`AttendanceRepository` は次のメソッド構成にします。
- `findById(...)` は `JpaRepository` 標準メソッドをそのまま利用します（追加定義は不要）。

編集ファイル:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java`

```java
Optional<Attendance> findByUser_IdAndWorkDate(Long userId, LocalDate workDate);
List<Attendance> findByUser_IdOrderByWorkDateDesc(Long userId);
List<Attendance> findAllByOrderByWorkDateDesc();
```

理解ポイント:
- Day5追加の核は `updateAttendance(...)` と `validateStatusAndTimes(...)`
- 管理者編集でも「同日重複」「状態と時刻の整合」を強制する
- 既存の出勤/退勤ロジックを壊さずに拡張している

#### Phase 3-6: `AttendanceController.java` を編集（ログインユーザー基準で一覧表示）
編集ファイル:
- `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/AttendanceController.java`

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
- Day4の固定ユーザーID方式から、ログインユーザー基準へ変更している
- 同じ `/attendances` でも「誰の履歴か」が認証連動で決まる

#### Phase 3-7: `attendances.html` を編集（ログインユーザー表示）
編集ファイル:
- `~/order-management-springboot/stages/day5/src/main/resources/templates/attendances.html`

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

#### Phase 3-8: `AttendanceServiceTest.java` を作る（最低限の回帰テスト）
作成ファイル:
- `~/order-management-springboot/stages/day5/src/test/java/com/shinesoft/attendance/service/AttendanceServiceTest.java`

このファイルを作る理由（先に読む）:
1. 目的:
   - `AttendanceService` の業務ルールが今後の修正で壊れていないかを自動確認するため
2. 何を守るテストか:
   - 1回目の出勤は成功する
   - 同日に2回目の出勤は失敗する（業務エラーになる）
3. いつ効くか:
   - `mvn test` を実行したときに毎回チェックされる
   - 手作業で毎回ブラウザ確認しなくても、最低限の品質を機械的に守れる
4. 初学者向けの理解:
   - テストは「機能を増やした後に、前に動いていたルールが壊れていないか」を確認する安全網

重要:
- 配置先は必ず `src/test/java` 配下にする（`src/main/java` ではない）
- `src/main/java` に置くと本番コード側として扱われ、テスト用として正しく運用しづらくなる

新規作成してください（既にある場合は全文置き換え）。

```java
package com.shinesoft.attendance.service; // テスト対象Serviceのパッケージ

import static org.junit.jupiter.api.Assertions.assertEquals; // 値一致検証
import static org.junit.jupiter.api.Assertions.assertNotNull; // nullでないことの検証
import static org.junit.jupiter.api.Assertions.assertThrows; // 例外検証

import org.junit.jupiter.api.BeforeEach; // 各テスト前処理
import org.junit.jupiter.api.Test; // テストメソッド
import org.springframework.beans.factory.annotation.Autowired; // 依存注入
import org.springframework.boot.test.context.SpringBootTest; // Spring統合テスト
import org.springframework.security.crypto.password.PasswordEncoder; // テストデータ作成時に利用
import org.springframework.transaction.annotation.Transactional; // テスト後ロールバック

import com.shinesoft.attendance.domain.Attendance; // 勤怠エンティティ
import com.shinesoft.attendance.domain.AttendanceStatus; // 状態Enum
import com.shinesoft.attendance.domain.User; // ユーザーエンティティ
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.repository.AttendanceRepository; // テスト前掃除で使う
import com.shinesoft.attendance.repository.UserRepository; // テストユーザー取得で使う

@SpringBootTest // Springコンテナを立ち上げて実行
@Transactional // 各テストをトランザクションで実行
class AttendanceServiceTest {

    @Autowired
    private AttendanceService service; // テスト対象

    @Autowired
    private UserRepository userRepository; // テストデータ準備に使う

    @Autowired
    private AttendanceRepository attendanceRepository; // テーブル掃除に使う

    @Autowired
    private PasswordEncoder passwordEncoder; // ユーザー作成時の暗号化

    private Long userId; // テスト対象ユーザーID

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll(); // 勤怠を毎回空にする
        User user = userRepository.findByUsername("user1").orElseGet(() -> {
            User u = new User();
            u.setUsername("user1");
            u.setPassword(passwordEncoder.encode("password"));
            u.setRole("ROLE_USER");
            return userRepository.save(u);
        });
        userId = user.getId();
    }

    @Test
    void clockIn_success() { // 正常系: 出勤できる
        Attendance attendance = service.clockIn(userId);
        assertEquals(AttendanceStatus.WORKING, attendance.getStatus());
        assertNotNull(attendance.getStartTime());
    }

    @Test
    void clockIn_twice_shouldFail() { // 異常系: 二重出勤は禁止
        service.clockIn(userId);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.clockIn(userId));
        assertEquals("すでに出勤済みです", ex.getMessage());
    }
}
```

理解ポイント:
- 「壊れてはいけない業務ルール」をテストで固定する
- Day5ではまず2本（正常系/異常系）の最小セットを確実に通す

完了チェック:
```bash
mvn compile
mvn test
```

詰まった時の比較方法:
- 例: `stages/day5` のファイルと完成版 `src` の同名ファイルを開き、差分を確認する
- 比較対象の優先度:
  1. `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java`
  2. `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/service/UserService.java`
  3. `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java`

---

## 5. ファイル作成チェック（必須）
```bash
find ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance -type f | sort
find ~/order-management-springboot/stages/day5/src/main/resources/templates -type f | sort
find ~/order-management-springboot/stages/day5/src/main/resources/static -type f | sort
find ~/order-management-springboot/stages/day5/src/test/java/com/shinesoft/attendance -type f | sort
```

期待する追加カテゴリ:
- Security: `config/SecurityConfig.java`
- Auth: `web/AuthController.java`
- User管理: `web/UserController.java`, `service/UserService.java`, `web/form/UserForm.java`
- 管理者勤怠編集: `web/AdminAttendanceController.java`, `web/form/AdminAttendanceForm.java`
- 画面: `login.html`, `users.html`, `user-form.html`, `admin-attendances.html`, `admin-attendance-form.html`
- JavaScript: `static/users.js`
- テスト: `AttendanceServiceTest.java`

理解ポイント（5分）:
- このチェックの目的:
  - 必須ファイルの作成漏れを起動前に潰す
- 重要ポイント:
  - Day5は「依存追加 + 多数ファイル追加」のため、フェーズ完了ごとのチェックが最短ルート

---

## 6. 起動
```bash
cd ~/order-management-springboot/stages/day5
mvn spring-boot:run
```

---

## 7. 動作確認

### 7-1. ログイン
1. `http://localhost:8080/login`
2. `user1 / password` でログイン
3. トップ画面表示を確認

### 7-2. 一般ユーザー権限
1. `user1` ログイン中に `http://localhost:8080/users` へアクセス
2. 403（アクセス不可）になることを確認

### 7-3. 管理者権限
1. ログアウト
2. `admin / admin123` でログイン
3. `http://localhost:8080/users` で新規作成/編集/削除を確認
4. 削除ボタン押下時に確認ダイアログが出ることを確認（キャンセル時は削除されない）
5. `users` 画面の検索欄とロール選択で、一覧が画面遷移なしで絞り込まれることを確認
6. `http://localhost:8080/admin/attendances` で勤怠編集を確認

理解ポイント（15分）:
- この確認の目的:
  - 「認証」と「認可（権限）」を実際の挙動で区別して理解する
- 見るべき結果:
  - `user1` は管理URLが403
  - `admin` は管理URLへアクセス可能
- 実務との対応:
  - URL単位の権限制御は運用時の基本パターン

---

## 8. テスト
```bash
mvn test
```

確認:
- `AttendanceServiceTest` の2テストが成功する

理解ポイント（10分）:
- この確認の目的:
  - 主要業務ロジックが壊れていないことを自動で確認する
- 最低限見ること:
  - 出勤成功テスト
  - 二重出勤失敗テスト
- よくあるミス:
  - テスト依存漏れ、またはパッケージパス不一致でテストが検出されない

---

## 9. コード読解ポイント（必須）

1. `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java`
- `requestMatchers` でURL別権限制御
- `formLogin` でログイン画面指定

2. `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/service/UserService.java`
- パスワードハッシュ化
- ユーザー名重複チェック

3. `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/HomeController.java`
- `Principal` からログインユーザー取得
- `isAdmin` で画面表示を分岐

4. `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java`
- 管理者だけが勤怠編集できる流れ

5. `~/order-management-springboot/stages/day5/src/main/resources/static/users.js`
- 削除確認ダイアログと一覧絞り込み（クライアント側UI改善）

---

## 10. つまずきポイント
- ログインできない:
  - `DataSeeder` が作成されているか
  - `users` テーブルに `admin` / `user1` があるか（H2で確認）
- 管理画面が403:
  - `admin` でログインしているか
  - `ROLE_ADMIN` が設定されているか
- テストが失敗:
  - `pom.xml` に `spring-boot-starter-test` があるか
- `org.springframework.security...` や `jakarta.validation...` が「存在しません」と出る:
  - 原因は依存キャッシュ不整合の可能性が高い
  - Git Bashで次を実行して依存を再取得する
  - `cd ~/order-management-springboot/stages/day5`
  - `rm -rf ~/.m2/repository/org/springframework/security`
  - `rm -rf ~/.m2/repository/jakarta/validation`
  - `rm -rf ~/.m2/repository/org/hibernate/validator`
  - `mvn -U clean spring-boot:run`

---

## 11. 時間割目安
- 午前: Security + ログイン + 役割分離（120分）
- 午後: 管理者機能 + テスト + まとめ（150分）

---

## 12. 現行`src`との対応（必須）
この章を追加する理由（先に読む）:
- Day5手順が完成版 `src` と同じ命名・構成になっていることを最終確認するため

一致確認（`stages/day5` と 完成版 `src`）:
- `AttendanceService#getTodayAttendance(Long)`（`Attendance` または `null`）
- `AttendanceService#listAttendances(Long)`
- `AttendanceRepository#findByUser_IdAndWorkDate(...)`
- `AttendanceRepository#findByUser_IdOrderByWorkDateDesc(...)`
- `AttendanceRepository#findAllByOrderByWorkDateDesc()`
- `AttendanceService#getAttendance(...)` / `updateAttendance(...)` では `AttendanceRepository#findById(...)` を利用

補足（重要）:
- Day5本文は、上記の完成版命名・構成に合わせて記載済み
- 追加の読み替え作業は不要

確認ポイント:
- 「業務ルール（`clockIn` / `clockOut` / `updateAttendance`）」の本質は同じ
- 命名差分の吸収ではなく、同じ実装をそのまま読解できる状態になっている

---

## 13. `dev` / `prod` プロファイルの読み方（必須）
この章を追加する理由（先に読む）:
- Day5作業ディレクトリを、完成版と同じ「共通設定 + プロファイル分離」に揃えるため

編集ファイル（`stages/day5`）:
- `~/order-management-springboot/stages/day5/src/main/resources/application.yml`
- `~/order-management-springboot/stages/day5/src/main/resources/application-dev.yml`
- `~/order-management-springboot/stages/day5/src/main/resources/application-prod.yml`

手順:
1. `application.yml` を以下に置き換える（共通設定）
```yaml
# 全環境共通の設定
spring:
  application:
    # アプリ名（未指定時は attendance-management）
    name: ${APP_NAME:attendance-management}
  profiles:
    # 起動時プロファイル（未指定時は dev）
    active: ${SPRING_PROFILES_ACTIVE:dev}
  datasource:
    # DB接続情報は環境変数で切替可能（未指定時はH2）
    url: ${DB_URL:jdbc:h2:mem:attendance;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE}
    username: ${DB_USER:sa}
    password: ${DB_PASSWORD:}
    driver-class-name: ${DB_DRIVER:org.h2.Driver}
  jpa:
    hibernate:
      # Entity定義に合わせてテーブルを更新
      ddl-auto: update
    # 必要時のみSQLログを有効化
    show-sql: ${SHOW_SQL:false}
  thymeleaf:
    # 画面キャッシュ設定はdev/prodで上書き
    cache: false

server:
  # ポートは環境変数で切替可能
  port: ${SERVER_PORT:8080}

logging:
  level:
    root: ${LOG_LEVEL:INFO}

app:
  name: ${APP_NAME:attendance-management}
```

2. `application-dev.yml` を新規作成する（開発用）
```yaml
# 開発用プロファイル
spring:
  h2:
    console:
      # 開発中はH2コンソールを有効化
      enabled: true
      path: /h2-console
  thymeleaf:
    # 画面確認しやすいようにキャッシュOFF
    cache: false

logging:
  level:
    root: ${LOG_LEVEL:INFO}
```

3. `application-prod.yml` を新規作成する（本番想定）
```yaml
# 本番用プロファイル
spring:
  h2:
    console:
      # 本番ではH2コンソールを無効化
      enabled: false
  thymeleaf:
    # 本番ではキャッシュON
    cache: true

logging:
  level:
    root: ${LOG_LEVEL:INFO}
```

4. プロファイル切替を実行確認する（Git Bash）
```bash
cd ~/order-management-springboot/stages/day5

# 開発モード（未指定でもdev）
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# 本番モード（画面キャッシュON / H2コンソールOFF）
SPRING_PROFILES_ACTIVE=prod mvn spring-boot:run
```

確認ポイント:
- `dev` と `prod` で挙動が変わるのは設定であり、Javaコード分岐ではない
- 本番DB切替は `DB_URL` など環境変数で行う

---

## 14. テスト追加演習（任意）
この章を追加する理由（先に読む）:
- Day5本文の最小2テストは重要だが、完成版で追加された業務ルール（退勤と管理者更新）も自動検証すると理解が深まるため

編集ファイル:
- `~/order-management-springboot/stages/day5/src/test/java/com/shinesoft/attendance/service/AttendanceServiceTest.java`
- （完成版読解のみの場合）`~/order-management-springboot/src/test/java/com/shinesoft/attendance/service/AttendanceServiceTest.java`

追加候補:
1. 退勤前に出勤していない場合は失敗する
2. 退勤を2回実行すると失敗する
3. `NOT_STARTED` に時刻を入れて更新すると失敗する
4. 同一ユーザー・同一日付へ更新すると失敗する

サンプル（1と2）:
```java
@Test
void clockOut_beforeClockIn_shouldFail() {
    BusinessException ex = assertThrows(BusinessException.class, () -> service.clockOut(userId));
    assertEquals("退勤するには先に出勤してください", ex.getMessage());
}

@Test
void clockOut_twice_shouldFail() {
    service.clockIn(userId);
    service.clockOut(userId);

    BusinessException ex = assertThrows(BusinessException.class, () -> service.clockOut(userId));
    assertEquals("すでに退勤済みです", ex.getMessage());
}
```

サンプル（3）:
```java
@Test
void updateAttendance_notStartedWithTimes_shouldFail() {
    Attendance attendance = service.clockIn(userId);

    BusinessException ex = assertThrows(BusinessException.class, () ->
        service.updateAttendance(
            attendance.getId(),
            userId,
            LocalDate.now(),
            LocalDateTime.now(),
            null,
            AttendanceStatus.NOT_STARTED
        )
    );
    assertEquals("未出勤の時刻は空にしてください", ex.getMessage());
}
```

補足:
- 上記を追加する場合は `LocalDate` / `LocalDateTime` の `import` 追加が必要
- 最終確認は `mvn test`

---

## 15. 参照整合とユーザー削除（必須）
この章を追加する理由（先に読む）:
- 完成版では `UserService#delete` が `deleteById` 直実行のため、勤怠があるユーザー削除時の挙動を学習項目として明示する必要があるため

背景:
- `Attendance` は `user_id` の必須参照（`@ManyToOne(optional = false)`）を持つ
- そのため、勤怠が存在するユーザーを削除するとDBの参照整合エラーになる場合がある

確認手順（動作観察）:
1. `user1` でログインして出勤（必要なら退勤）を実行
2. `admin` でログインして `users` 画面から `user1` を削除
3. エラーの有無と内容を確認

学習ポイント:
- 削除APIは「対象データがあるか」だけでなく「関連データが残っていないか」も確認が必要
- 業務要件によって方針は変わる（削除禁止 / 論理削除 / 連鎖削除）

発展（任意）:
1. `AttendanceRepository` に件数確認メソッドを追加する
2. `UserService#delete` で勤怠件数を先にチェックして `BusinessException` を返す
3. `UserController#delete` で例外メッセージを画面表示する

発展時のゴール:
- 参照整合エラーを「想定外のDB例外」ではなく「想定内の業務エラー」として扱えるようにする
