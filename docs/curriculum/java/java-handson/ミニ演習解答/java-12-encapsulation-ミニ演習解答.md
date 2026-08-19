# Java-12 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-12-encapsulation.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 2で作成し、Step 3でもそのまま使用する`EncapsulationDemo`の呼び出し値だけを一時的に変更する:

```java
user.setUsername("   "); // 一時的な確認値
```

`username は必須です`という例外を確認したら、Step 2で設定した正常値へ戻してレベル2へ進む:

```java
user.setUsername("tanaka"); // Step 2で設定した正常値へ戻す
```

## レベル2（拡張）解答
レベル1で戻した正常なユーザー名はそのままにし、年齢の呼び出し値だけを一時的に変更する:

```java
user.setAge(130); // 一時的な確認値
```

`age の範囲が不正です`という例外を確認したら、Step 2で設定した正常値へ戻してレベル3へ進む:

```java
user.setAge(25); // Step 2で設定した正常値へ戻す
```

## レベル3（実務）解答
レベル1・2で確認した既存の検証処理を残したまま、`UserAccount`へ次のコードを追記する:

```java
// レベル3で追記
private String email;

public void setEmail(String email) {
    // contains("@")は、emailに「@」が含まれているかを確認する
    // 先頭の!で結果を反転するため、「@を含まない」という条件になる
    if (email == null || !email.contains("@")) {
        throw new IllegalArgumentException("email 形式が不正です: " + email);
    }
    this.email = email;
}

public String getEmail() {
    return email;
}
```

`EncapsulationDemo`の既存表示処理の後へ、次のコードを追記する:

```java
// レベル3で追記
user.setEmail("user@example.com");
System.out.println("email: " + user.getEmail());
```

### レベル3完了時の全コード

`UserAccount.java`:

```java
public class UserAccount {
    private String username;
    private int age;

    // ===== レベル3で追加: emailもクラス内部で管理する =====
    private String email;
    // ===== レベル3で追加ここまで =====

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username は必須です");
        }
        this.username = username.trim();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0 || age > 120) {
            throw new IllegalArgumentException("age の範囲が不正です");
        }
        this.age = age;
    }

    // ===== レベル3で追加: emailの検証と取得窓口 =====
    public void setEmail(String email) {
        // contains("@")は、emailに「@」が含まれているかを確認する
        // 先頭の!で結果を反転するため、「@を含まない」という条件になる
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("email 形式が不正です: " + email);
        }
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    // ===== レベル3で追加ここまで =====
}
```

`EncapsulationDemo.java`:

```java
public class EncapsulationDemo {
    public static void main(String[] args) {
        UserAccount user = new UserAccount();
        user.setUsername("tanaka"); // レベル1の一時変更は正常値へ戻す
        user.setAge(25); // レベル2の一時変更は正常値へ戻す

        System.out.println("username: " + user.getUsername());
        System.out.println("age: " + user.getAge());

        // ===== レベル3で追加 =====
        user.setEmail("user@example.com");
        System.out.println("email: " + user.getEmail());
        // ===== レベル3で追加ここまで =====
    }
}
```
