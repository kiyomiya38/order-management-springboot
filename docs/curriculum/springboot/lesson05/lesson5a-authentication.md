# Lesson5A ログイン・認証・認可

[Lesson5共通準備と全体目次](./lesson5.md)を完了してから実施します。

## 目的

- Spring Securityによるログイン処理を実装できる
- 認証と認可の違いを説明できる
- URLごとのアクセス制御とパスワードハッシュ化を説明できる

## 前提

- `lesson5.md` の共通準備を完了している
- `~/order-management-springboot/stages/lesson05` で `mvn compile` が成功する

## 学習内容

### Phase 1: 認証の土台（ログインと権限制御）
新規作成ファイル（フルパス）:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java`
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/AuthController.java`
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/login.html`

既存編集ファイル（フルパス）:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/config/DataSeeder.java`
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/domain/User.java`

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
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java`

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
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/AuthController.java`

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
- `~/order-management-springboot/stages/lesson05/src/main/resources/templates/login.html`

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
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/config/DataSeeder.java`

全文を以下に置き換えてください。

```java
package com.shinesoft.attendance.config; // 設定クラス群のパッケージ

import org.springframework.boot.CommandLineRunner; // 起動直後に処理を実行するためのIF
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty; // 設定で投入可否を切り替える
import org.springframework.beans.factory.annotation.Value; // 設定値を受け取る
import org.springframework.stereotype.Component; // Spring管理対象として登録

import com.shinesoft.attendance.domain.User; // Userエンティティ
import com.shinesoft.attendance.repository.UserRepository; // User保存/検索に使うRepository
import org.springframework.security.crypto.password.PasswordEncoder; // パスワードをハッシュ化するために使う

@Component // アプリ起動時に読み込まれるBean
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository; // ユーザー永続化用
    private final PasswordEncoder passwordEncoder; // 平文パスワードをそのまま保存しないための依存
    private final String adminPassword;
    private final String userPassword;

    public DataSeeder(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.seed.admin-password:}") String adminPassword,
                      @Value("${app.seed.user-password:}") String userPassword) {
        this.userRepository = userRepository; // 依存注入
        this.passwordEncoder = passwordEncoder; // 依存注入
        this.adminPassword = adminPassword;
        this.userPassword = userPassword;
    }

    @Override
    public void run(String... args) { // アプリ起動後に1回実行される
        if (adminPassword.isBlank() || userPassword.isBlank()) {
            throw new IllegalStateException("初期ユーザー投入を有効にする場合は初期パスワードが必要です");
        }

        if (userRepository.findByUsername("admin").isEmpty()) { // adminがいない時だけ作成
            User admin = new User(); // 管理者アカウント作成
            admin.setUsername("admin"); // ログインID
            admin.setPassword(passwordEncoder.encode(adminPassword)); // 設定値をハッシュ化して保存
            admin.setRole("ROLE_ADMIN"); // 管理者ロール
            userRepository.save(admin); // DBへ保存
        }

        if (userRepository.findByUsername("user1").isEmpty()) { // user1がいない時だけ作成
            User user = new User(); // 一般ユーザー作成
            user.setUsername("user1"); // ログインID
            user.setPassword(passwordEncoder.encode(userPassword)); // 設定値をハッシュ化して保存
            user.setRole("ROLE_USER"); // 一般ユーザーロール
            userRepository.save(user); // DBへ保存
        }
    }
}
```

理解ポイント:
- 目的は「起動直後にログイン用ユーザーを必ず作る」こと
- `passwordEncoder.encode(...)` で平文保存を回避する
- ユーザー名ごとに存在確認し、片方だけ削除された場合も必要な初期ユーザーを復元する
- 初期投入は `app.seed.enabled=true` の環境だけで動かし、パスワードは設定値から受け取る

#### Phase 1-5: `User.java` を編集（認証に必要な項目を持つ）
編集ファイル:
- `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/domain/User.java`

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
cd ~/order-management-springboot/stages/lesson05
mvn compile
```

---

## Lesson5A 完了条件

- `mvn compile` が成功する
- 未認証で保護対象URLを開くとログイン画面へ遷移する
- `admin` / `user1` のパスワードが平文ではなくハッシュとしてDBへ保存される
- 認証と認可の違いを説明できる
- `SecurityFilterChain`、`UserDetailsService`、`PasswordEncoder` の役割を説明できる

完了後は [Lesson5B ユーザー管理・勤怠管理](./lesson5b-management.md) へ進みます。
