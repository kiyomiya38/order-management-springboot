# Java-12A 補講: アクセス修飾子の使い分け（`public` / `private` / `protected` / 無指定）

## 1. この資料のゴール
- 4種類のアクセス範囲を説明できる
- 同一パッケージ内で `public` / `protected` / 無指定 / `private` の違いを確認できる
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
- この資料では、継承を使った確認は行わない。
- 他パッケージの子クラスから `protected` にアクセスする確認は、継承を学んだ後に扱う内容として分ける。

### 変数やメソッドを使う側から考える

アクセス修飾子は、宣言だけを見ても違いを実感しにくい機能です。この章では、`Account`を「定義する側」、`AccountInspector`を「使う側」に分けて確認します。

```text
Account
  ├─ public       : AccountInspectorから使える
  ├─ protected    : 同じmodelパッケージなので使える
  ├─ 無指定       : 同じmodelパッケージなので使える
  └─ private      : Account自身からだけ使える
```

重要なのは、`private`を付けた値が消えるわけではないことです。クラス外から直接触れなくなるだけで、`Account`が用意した`public`メソッドを通して安全に利用できます。

```java
// 直接アクセスは不可
// System.out.println(account.secret);

// Account自身が用意したpublicメソッド経由なら利用可能
System.out.println(account.debugSecret());
```

この章ではまず「同じパッケージ」を確認します。別パッケージと継承を組み合わせた`protected`の確認は、Java-13で継承を学んだ後に行うと理解しやすくなります。

---

## 4. ハンズオン

目的:
- 同一パッケージ内で、アクセス修飾子ごとの見え方を確認する

完了条件:
- 同一パッケージでの `public` / `protected` / 無指定アクセスと、`private` への直接アクセス不可を確認できる
- 無指定クラス（package-private class）が同一パッケージから使えることを確認できる

作業フォルダ: `~/order-management-springboot/practice/java/handson12a`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson12a/src/model ~/order-management-springboot/practice/java/handson12a/out
cd ~/order-management-springboot/practice/java/handson12a
```

`mkdir -p`に2つのパスを指定すると、`handson12a`、`src/model`、`out`までの必要なディレクトリをこの時点でまとめて作成できます。各Javaファイルは、以降のStepで説明に従って作成します。

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

期待出力例:
```text
A-001
100
ACTIVE
public
protected
package-private
private:internal
```

任意確認: `private` へ直接アクセスできないことを確認する

1. `AccountInspector.java` の次の行のコメントアウトを一時的に外す。

```java
System.out.println(a.secret);
```

2. もう一度コンパイルする。

```bash
javac -encoding UTF-8 -d out src/model/Account.java src/model/AccountInspector.java
```

期待状態:
- `secret has private access in model.Account` のようなコンパイルエラーになる
- `private` フィールドは、同じパッケージでもクラス外から直接アクセスできないことが分かる

3. 確認後は、次のように再びコメントアウトして元に戻す。

```java
// System.out.println(a.secret); // private なので不可
```

### Step 2: クラスの無指定アクセスを確認（仕上げ）
作成ファイル: `src/model/InternalRule.java`

```java
package model;

class InternalRule { // 無指定クラス: package-private
    static String value() {
        return "internal-rule";
    }
}
```

`src/model/AccountInspector.java`を次の内容に更新します。Step 1から追加した行をコメントで示しています。

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

        // ===== Step 2で追加: 同じパッケージの無指定クラスを利用する =====
        System.out.println(InternalRule.value()); // 同一パッケージなので可
        // ===== Step 2で追加ここまで =====
    }
}
```

実行:
```bash
javac -encoding UTF-8 -d out src/model/*.java
java -cp out model.AccountInspector
```

期待出力例:
```text
A-001
100
ACTIVE
public
protected
package-private
private:internal
internal-rule
```

期待状態:
- 無指定クラス `InternalRule` を同一パッケージ内から使えることが分かる
- 別パッケージや継承を組み合わせた確認は、この資料では扱わない

---

## 5. ミニ演習（10分）
各レベルは前のレベルで追加したコードを引き継いで実施します。レベル1はStep 2から開始してください。コンパイルエラー確認用のアクセス変更だけは確認後に戻します。

### レベル1（基本）
1. `Account`に`private int loginFailures = 0;`を追加する。
2. `loginFailures`を1増やす`public void recordFailure()`を追加する。
3. 現在の`loginFailures`を返す`public int getLoginFailures()`を追加する。
4. `AccountInspector.main(...)`の既存処理より後で、`a.recordFailure();`を1回呼び出す。
5. `System.out.println("loginFailures: " + a.getLoginFailures());`で値を表示する。
6. `AccountInspector`から`a.loginFailures`を直接変更するコードは書かない。

確認対象の出力（抜粋）:
```text
loginFailures: 1
```

### レベル2（拡張）
1. `AccountInspector` から `a.privateInfo()` を直接呼び出し、エラーを確認する。

期待状態:
- `private` メソッドへクラス外から直接アクセスできないことがコンパイルエラーで確認できる

### レベル3（実務）
1. `InternalRule` の `value()` を `private` に変え、`AccountInspector` から呼べなくなることを確認する。

期待状態:
- 同じパッケージ内でも、`private` メソッドはクラス外から呼び出せないことを確認できる

---

## 6. つまずきポイント
- `protected` ならどこからでも参照できると誤解
  -> `public` とは違う。継承を使った詳しい確認は、この資料では扱わない
- 無指定メンバを `public` と同じ扱いで使ってしまう
  -> パッケージ外からは見えない
- `private` フィールドへ直接アクセスしようとしてエラー
  -> クラス内部メソッド経由で扱う
