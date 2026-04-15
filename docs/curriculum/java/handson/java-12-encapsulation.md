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

期待結果:
- コンパイルが成功する（エラーなし）
- `.class` ファイルが生成される


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

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 3: setter にバリデーションを入れる（仕上げ）
`UserAccount.java` を次の内容に更新:

```java
public class UserAccount { // バリデーション付きのカプセル化クラス
    private String username; // ユーザー名
    private int age; // 年齢

    public String getUsername() { // username の getter
        return username; // 現在の username を返す
    }

    public void setUsername(String username) { // username の setter
        if (username == null || username.isBlank()) { // null または空白のみは不正
            throw new IllegalArgumentException("username は必須です"); // 不正値を例外で通知
        }
        this.username = username.trim(); // 前後空白を除去して保存
    }

    public int getAge() { // age の getter
        return age; // 現在の age を返す
    }

    public void setAge(int age) { // age の setter
        if (age < 0 || age > 120) { // 年齢範囲チェック
            throw new IllegalArgumentException("age の範囲が不正です"); // 不正値を例外で通知
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

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


学習ポイント:
- setter が「入力チェックの入口」になる
- クラス外から不正データが入りにくくなる

---

## 5. ミニ演習（10分）
1. `setUsername("   ")` を試して例外を確認
2. `setAge(130)` を試して例外を確認
3. `email` フィールドを追加し、`@` 含有チェックを実装

---

## 6. つまずきポイント
- フィールドを `private` にしたら参照できない
  -> getter/setter 経由でアクセス
- setter 内で `this.` を忘れて代入漏れ
  -> フィールド代入は `this.field` を明示
- 例外でアプリが止まる
  -> 呼び出し側の入力値を見直す（後半で例外処理を学習）

