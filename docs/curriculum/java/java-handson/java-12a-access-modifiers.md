# Java-12A 補講: アクセス修飾子の使い分け（`public` / `private` / `protected` / 無指定）

対応参考資料: `J2_03_カプセル化.pdf`, `J2_05_継承②.pdf`

## 1. この資料のゴール
- 4種類のアクセス範囲を説明できる
- `protected` の「継承先からアクセス可能」を実装で確認できる
- `package-private`（無指定）の意味を説明できる

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

期待状態:
- `java -version` と `javac -version` の両方で `17` が表示される
- 例: `17.0.x`

---

## 3. 先に覚えるポイント
1. `private` は「同じクラス内のみ」
2. 無指定（package-private）は「同じパッケージ内のみ」
3. `protected` は「同じパッケージ + 他パッケージのサブクラス」
4. `public` はどこからでも参照可能

比較表（`○` = 使える / `×` = 使えない）:

| 修飾子 | 同じクラス | 同じパッケージ | 他パッケージの子クラス | 他パッケージの通常クラス | 初学者向けの覚え方 |
| --- | --- | --- | --- | --- | --- |
| `private` | ○ | × | × | × | そのクラス専用の「内側だけ」 |
| 無指定（package-private） | ○ | ○ | × | × | 同じフォルダ仲間（同一package）だけ |
| `protected` | ○ | ○ | ○ | × | 仲間 + 継承した子クラスまで |
| `public` | ○ | ○ | ○ | ○ | どこからでも見える公開入口 |

補足:
- この資料で使う `extends` は、`protected` のアクセス範囲を確認するための最小限の利用に限定する。
- 継承の考え方（親子クラスの役割分担・再利用・オーバーライド）は [Java-13 ハンズオン: 継承](./java-13-inheritance.md) で学ぶ。

---

## 4. ハンズオン

目的:
- パッケージ境界と継承の組み合わせでアクセス範囲を確認する

完了条件:
- 同一パッケージでの無指定アクセスと、他パッケージ継承先での `protected` アクセスを確認できる

作業フォルダ: `~/order-management-springboot/practice/java/handson12a`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson12a/src/model
mkdir -p ~/order-management-springboot/practice/java/handson12a/src/app
cd ~/order-management-springboot/practice/java/handson12a
```

### Step 1: 同一パッケージでアクセス確認
作成ファイル: `src/model/Account.java`

```java
package model;

public class Account {
    public String id = "A-001"; // どこからでも可
    protected int points = 100; // 同一パッケージ + サブクラス可
    String status = "ACTIVE"; // 無指定: 同一パッケージのみ
    private String secret = "internal"; // 同一クラスのみ

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

    public String debugSecret() { // 同一クラス内なので private へアクセス可能
        return privateInfo() + ":" + secret;
    }
}
```

作成ファイル: `src/model/AccountInspector.java`

```java
package model;

public class AccountInspector {
    public static void main(String[] args) {
        Account a = new Account();
        System.out.println(a.id); // public
        System.out.println(a.points); // 同一パッケージなので可
        System.out.println(a.status); // 無指定なので可
        System.out.println(a.publicInfo()); // public
        System.out.println(a.protectedInfo()); // 同一パッケージなので可
        System.out.println(a.packageInfo()); // 同一パッケージなので可
        System.out.println(a.debugSecret()); // private情報は公開メソッド経由なら可
        // System.out.println(a.secret); // private なので不可
    }
}
```

実行:
```bash
javac -encoding UTF-8 -d out src/model/Account.java src/model/AccountInspector.java
java -cp out model.AccountInspector
```

期待結果:
- `id/points/status` 等が表示される

### Step 2: 他パッケージの継承先で `protected` を確認
※ここでは `protected` のアクセス可否確認が目的。継承設計そのものの詳細は `java-13-inheritance.md` を参照。

作成ファイル: `src/app/PremiumAccount.java`

```java
package app;

import model.Account;

public class PremiumAccount extends Account {
    public void printInheritedFields() {
        System.out.println(id); // public: 可
        System.out.println(points); // protected: サブクラスなので可
        // System.out.println(status); // package-private: 他パッケージなので不可
    }
}
```

作成ファイル: `src/app/AccessModifierDemo.java`

```java
package app;

import model.Account;

public class AccessModifierDemo {
    public static void main(String[] args) {
        PremiumAccount pa = new PremiumAccount();
        pa.printInheritedFields();

        Account base = new Account();
        System.out.println(base.id); // public: 可
        // System.out.println(base.points); // 他パッケージ + 非サブクラス文脈では不可
        // System.out.println(base.status); // package-private なので不可
    }
}
```

実行:
```bash
javac -encoding UTF-8 -d out src/model/Account.java src/model/AccountInspector.java src/app/PremiumAccount.java src/app/AccessModifierDemo.java
java -cp out app.AccessModifierDemo
```

期待結果:
- `id` と `points`（継承経由）が表示される

### Step 3: クラスの無指定アクセスを確認（仕上げ）
作成ファイル: `src/model/InternalRule.java`

```java
package model;

class InternalRule { // 無指定クラス: package-private
    static String value() {
        return "internal-rule";
    }
}
```

`src/model/AccountInspector.java` の `main` に次を追加:

```java
System.out.println(InternalRule.value()); // 同一パッケージなので可
```

実行:
```bash
javac -encoding UTF-8 -d out src/model/*.java src/app/*.java
java -cp out model.AccountInspector
```

期待結果:
- `internal-rule` が表示される
- `app` パッケージ側から `InternalRule` を使うとコンパイルエラーになる

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `Account` に `private int loginFailures` を追加し、公開メソッド経由でのみ更新できるようにする。

期待出力例:
```text
loginFailures: 1
```

### レベル2（拡張）
1. `app` 側で `Account` を継承しない通常クラスから `protected` へアクセスしてエラーを確認する。

期待結果:
- `protected` メンバへ直接アクセスできないことがコンパイルエラーで確認できる

### レベル3（実務）
1. `InternalRule` を `public` に変えて `app` から参照できることを確認する。

期待出力例:
```text
internal-rule
```

---

## 6. つまずきポイント
- `protected` ならどこからでも参照できると誤解
  -> 他パッケージではサブクラス経由が前提
- 無指定メンバを `public` と同じ扱いで使ってしまう
  -> パッケージ外からは見えない
- `private` フィールドへ直接アクセスしようとしてエラー
  -> クラス内部メソッド経由で扱う
