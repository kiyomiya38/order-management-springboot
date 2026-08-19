# Java-12A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-12a-access-modifiers.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 2の3ファイルを引き継ぎます。変更する2ファイルを次の全コードへ更新します。`InternalRule.java`は変更しません。

`src/model/Account.java`:

```java
package model;

public class Account {
    public String id = "A-001";
    protected int points = 100;
    String status = "ACTIVE";
    private String secret = "internal";

    // ===== レベル1で追加: クラス外から直接変更させない失敗回数 =====
    private int loginFailures = 0;
    // ===== レベル1で追加ここまで =====

    public String publicInfo() {
        return "public";
    }

    protected String protectedInfo() {
        return "protected";
    }

    String packageInfo() {
        return "package-private";
    }

    private String privateInfo() {
        return "private";
    }

    public String debugSecret() {
        return privateInfo() + ":" + secret;
    }

    // ===== レベル1で追加: publicメソッドを安全な操作窓口にする =====
    public void recordFailure() {
        loginFailures++;
    }

    public int getLoginFailures() {
        return loginFailures;
    }
    // ===== レベル1で追加ここまで =====
}
```

`src/model/AccountInspector.java`:

```java
package model;

public class AccountInspector {
    public static void main(String[] args) {
        Account a = new Account();
        System.out.println(a.id);
        System.out.println(a.points);
        System.out.println(a.status);
        System.out.println(a.publicInfo());
        System.out.println(a.protectedInfo());
        System.out.println(a.packageInfo());
        System.out.println(a.debugSecret());
        System.out.println(InternalRule.value());

        // ===== レベル1で追加: privateフィールドをpublicメソッド経由で操作する =====
        a.recordFailure();
        System.out.println("loginFailures: " + a.getLoginFailures());
        // ===== レベル1で追加ここまで =====
    }
}
```

## レベル2（拡張）解答
レベル1の追加は残します。`AccountInspector.java`の`main(...)`末尾へ、次の行を一時的に追加します。

```java
// 確認用。一時的にコメントを外す
// System.out.println(a.privateInfo());
```

`a.privateInfo()`はprivateメソッドを別クラスから呼び出すためコンパイルエラーになります。確認後は再びコメントアウトし、レベル1が実行できる状態へ戻します。

## レベル3（実務）解答
レベル1の追加は残す。`InternalRule.value()`を一時的にprivateへ変更する:

```java
package model;

class InternalRule {
    private static String value() {
        return "internal-rule";
    }
}
```

既存の `AccountInspector` 側の呼び出しは、同じパッケージでもprivateメソッドへアクセスするためコンパイルエラーになる:

```java
System.out.println(InternalRule.value());
```

コンパイルエラーを確認したら、`value()`から`private`を外してpackage-privateへ戻す。レベル1で追加したログイン失敗回数の機能は残す。

### レベル3完了時の全コード

`src/model/Account.java`:

```java
package model;

public class Account {
    public String id = "A-001";
    protected int points = 100;
    String status = "ACTIVE";
    private String secret = "internal";
    // ===== レベル1で追加 =====
    private int loginFailures = 0;
    // ===== レベル1で追加ここまで =====

    public String publicInfo() { return "public"; }
    protected String protectedInfo() { return "protected"; }
    String packageInfo() { return "package-private"; }
    private String privateInfo() { return "private"; }
    public String debugSecret() { return privateInfo() + ":" + secret; }

    // ===== レベル1で追加 =====
    public void recordFailure() { loginFailures++; }
    public int getLoginFailures() { return loginFailures; }
    // ===== レベル1で追加ここまで =====
}
```

`src/model/InternalRule.java`:

```java
package model;

class InternalRule {
    static String value() {
        return "internal-rule";
    }
}
```

`src/model/AccountInspector.java`:

```java
package model;

public class AccountInspector {
    public static void main(String[] args) {
        Account a = new Account();
        System.out.println(a.id);
        System.out.println(a.points);
        System.out.println(a.status);
        System.out.println(a.publicInfo());
        System.out.println(a.protectedInfo());
        System.out.println(a.packageInfo());
        System.out.println(a.debugSecret());
        System.out.println(InternalRule.value());

        // ===== レベル1で追加 =====
        a.recordFailure();
        System.out.println("loginFailures: " + a.getLoginFailures());
        // ===== レベル1で追加ここまで =====
    }
}
```
