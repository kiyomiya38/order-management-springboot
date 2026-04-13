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
public class UserAccount {
    private String username;
    private int age;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
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
public class EncapsulationDemo {
    public static void main(String[] args) {
        UserAccount user = new UserAccount();
        user.setUsername("tanaka");
        user.setAge(25);

        System.out.println("username: " + user.getUsername());
        System.out.println("age: " + user.getAge());
    }
}
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
public class UserAccount {
    private String username;
    private int age;

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
}
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
