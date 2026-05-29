# Java-12 ハンズオン: カプセル化

対応参考資料: `Java-12_カプセル化.pptx`

## 1. この資料のゴール
- カプセル化の目的を説明できる
- `private` フィールド + getter/setter を実装できる
- 不正値を setter で防ぐ設計を実装できる

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
1. フィールドを `private` にして直接変更を防ぐ
2. getter/setter でアクセスを制御する
3. setter 内でバリデーションすると不正状態を防げる

### 書式の基本

#### `private` フィールド

```java
public class UserAccount {
    private String username;
    private int age;
}
```

ポイント:
- `private` を付けたフィールドは、同じクラス内からだけ直接アクセスできる
- クラス外から `user.username` のように直接変更できない
- 不正な値を勝手に入れられないようにするための基本形

#### getter / setter

```java
public String getUsername() {
    return username;
}

public void setUsername(String username) {
    this.username = username;
}
```

ポイント:
- getter はフィールドの値を返すメソッド
- setter はフィールドの値を変更するメソッド
- `this.username` はフィールド、右辺の `username` は引数
- クラス外からはメソッド経由で値を扱う

#### setter 内のバリデーション

```java
public void setUsername(String username) {
    if (username == null || username.isBlank()) {
        throw new IllegalArgumentException("username は必須です");
    }
    this.username = username.trim();
}
```

ポイント:
- setter の中で代入前に値をチェックできる
- `throw new IllegalArgumentException(...)` は不正値を呼び出し元へ知らせる
- 妥当な値だけをフィールドへ保存する

#### 利用側の書き方

```java
UserAccount user = new UserAccount();
user.setUsername("tanaka");
System.out.println(user.getUsername());
```

ポイント:
- 値の設定は setter 経由で行う
- 値の取得は getter 経由で行う
- フィールドを直接触らせないことで、クラス内のルールを守れる

---

## 4. ハンズオン

目的:
- データを安全に扱うクラス設計を学ぶ

完了条件:
- `UserAccount` クラスを `private` フィールドで実装し、妥当性チェックできる

作成フォルダ: `~/order-management-springboot/practice/java/handson12`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson12
cd ~/order-management-springboot/practice/java/handson12
```

### Step 1: カプセル化クラスを作る
作成ファイル: `UserAccount.java`

```java
public class UserAccount { // カプセル化したユーザー情報クラス
    private String username; // private: クラス外から直接参照・変更させない
    private int age; // private フィールド

    public String getUsername() { // username の getter
        return username; // 現在値を返す
    }

    public void setUsername(String username) { // username の setter
        this.username = username; // 受け取った値をフィールドへ設定
    }

    public int getAge() { // age の getter
        return age; // 現在値を返す
    }

    public void setAge(int age) { // age の setter
        this.age = age; // 受け取った値をフィールドへ設定
    }
} // クラス定義の終わり
```

コンパイル確認:
```bash
javac -encoding UTF-8 UserAccount.java
```

期待出力例:
```text
(コンパイル成功: 出力なし)
```


### Step 2: 利用側を作る
作成ファイル: `EncapsulationDemo.java`

```java
public class EncapsulationDemo { // UserAccount 利用側の実行クラス
    public static void main(String[] args) {
        UserAccount user = new UserAccount(); // インスタンス生成
        user.setUsername("tanaka"); // setter 経由で値を設定
        user.setAge(25); // setter 経由で値を設定

        System.out.println("username: " + user.getUsername()); // getter 経由で値を取得
        System.out.println("age: " + user.getAge()); // getter 経由で値を取得
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 UserAccount.java EncapsulationDemo.java
java EncapsulationDemo
```

期待出力例:
```text
username: tanaka
age: 25
```



### Step 3: setter にバリデーションを入れる（仕上げ）
`UserAccount.java` を次の内容に更新:

先取り補足:
- `throw new IllegalArgumentException(...)` は、入力値が不正なときに処理を止めて呼び出し元へ知らせる書き方
- 例外処理の詳しい扱いは Java-17 で学ぶため、ここでは「不正値を保存しないためのガード」として読む

```java
public class UserAccount { // バリデーション付きのカプセル化クラス
    private String username; // ユーザー名
    private int age; // 年齢

    public String getUsername() { // username の getter
        return username; // 現在の username を返す
    }

    public void setUsername(String username) { // username の setter
        if (username == null || username.isBlank()) { // ここが不正検知（バリデーション）：null や空白だけの入力を見つける
            throw new IllegalArgumentException("username は必須です"); // ここで例外を発生：この setter の処理を中断し、呼び出し元へエラーを通知
        }
        this.username = username.trim(); // 前後空白を除去して保存
    }

    public int getAge() { // age の getter
        return age; // 現在の age を返す
    }

    public void setAge(int age) { // age の setter
        if (age < 0 || age > 120) { // ここが不正検知（バリデーション）：年齢が 0〜120 の範囲か確認
            throw new IllegalArgumentException("age の範囲が不正です"); // ここで例外を発生：この setter の処理を中断し、呼び出し元へエラーを通知
        }
        this.age = age; // 検証済み値を保存
    }
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 UserAccount.java EncapsulationDemo.java
java EncapsulationDemo
```

期待出力例:
```text
username: tanaka
age: 25
```



学習ポイント:
- setter が「入力チェックの入口」になる
- クラス外から不正データが入りにくくなる

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `setUsername("   ")` を試して例外を確認する。

期待結果:
- `username は必須です` のような例外メッセージが表示される

### レベル2（拡張）
1. `setAge(130)` を試して例外を確認する。

期待結果:
- `age の範囲が不正です` のような例外メッセージが表示される

### レベル3（実務）
1. `email` フィールドを追加し、`@` 含有チェックを実装する。

期待出力例:
```text
email: user@example.com
```

---

## 6. つまずきポイント
- フィールドを `private` にしたら参照できない
  -> getter/setter 経由でアクセス
- setter 内で `this.` を忘れて代入漏れ
  -> フィールド代入は `this.field` を明示
- 例外でアプリが止まる
  -> 呼び出し側の入力値を見直す（後半で例外処理を学習）



