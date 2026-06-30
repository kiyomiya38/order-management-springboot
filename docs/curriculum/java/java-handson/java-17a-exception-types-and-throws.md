# Java-17A 補講: 例外の分類と `throws`（checked / unchecked）

## 1. この資料のゴール
- checked例外とunchecked例外の違いを説明できる
- `throws` が必要な場面を判断できる
- `RuntimeException` 系の扱い方を説明できる

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
1. checked例外は「コンパイル時に処理強制される例外」
2. unchecked例外は `RuntimeException` 系で、`throws` や `catch` が必須ではない
3. `throws` は「呼び出し側で処理してください」という宣言

---

## 4. ハンズオン

目的:
- 例外分類に応じて `catch` / `throws` を使い分ける

完了条件:
- `ExceptionTypesDemo.java` で checked と unchecked の違いを説明できる

作成ファイル: `~/order-management-springboot/practice/java/handson17a/ExceptionTypesDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson17a
cd ~/order-management-springboot/practice/java/handson17a
```

### Step 1: checked例外（`ClassNotFoundException`）を扱う
`ExceptionTypesDemo.java` を次の内容で作成:

```java
public class ExceptionTypesDemo {
    static void loadDriver() throws ClassNotFoundException { // checked例外を宣言
        Class.forName("com.example.NotExistingDriver"); // 存在しないクラスを読み込む
    }

    public static void main(String[] args) {
        try {
            loadDriver();
        } catch (ClassNotFoundException e) {
            System.out.println("checked例外を捕捉: " + e.getClass().getSimpleName());
        }
    }
}
```

実行:
```bash
javac -encoding UTF-8 ExceptionTypesDemo.java
java ExceptionTypesDemo
```

期待出力例:
```text
checked例外を捕捉: ClassNotFoundException
```

### Step 2: unchecked例外（`NumberFormatException`）を扱う
`ExceptionTypesDemo.java` を次の内容に更新:

```java
public class ExceptionTypesDemo {
    static int parseQuantity(String raw) { // throws 宣言なし
        return Integer.parseInt(raw); // 不正値で NumberFormatException（unchecked）
    }

    public static void main(String[] args) {
        try {
            int q = parseQuantity("abc");
            System.out.println("quantity=" + q);
        } catch (NumberFormatException e) {
            System.out.println("unchecked例外を捕捉: " + e.getClass().getSimpleName());
        }
    }
}
```

実行:
```bash
javac -encoding UTF-8 ExceptionTypesDemo.java
java ExceptionTypesDemo
```

期待出力例:
```text
unchecked例外を捕捉: NumberFormatException
```

### Step 3: `throws` で上位へ伝播する設計を確認（仕上げ）
`ExceptionTypesDemo.java` を次の内容に更新:

```java
class ConfigException extends Exception { // checked例外を自作
    ConfigException(String message) {
        super(message);
    }
}

public class ExceptionTypesDemo {
    static String loadMode(String value) throws ConfigException {
        if (value == null || value.isBlank()) {
            throw new ConfigException("mode が未設定です");
        }
        return value.trim().toUpperCase();
    }

    static int requirePositive(int n) { // RuntimeException系は throws 省略可
        if (n <= 0) {
            throw new IllegalArgumentException("n は正数が必要です");
        }
        return n;
    }

    public static void main(String[] args) {
        try {
            System.out.println("mode=" + loadMode("prod"));
            System.out.println("n=" + requirePositive(3));
        } catch (ConfigException e) {
            System.out.println("設定エラー: " + e.getMessage());
        }
    }
}
```

実行:
```bash
javac -encoding UTF-8 ExceptionTypesDemo.java
java ExceptionTypesDemo
```

期待出力例:
```text
mode=PROD
n=3
```

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `loadMode("")` を呼び、`ConfigException` の発生を確認する。

期待状態:
- checked例外を `catch` できる

### レベル2（拡張）
1. `requirePositive(0)` を呼び、`IllegalArgumentException` を確認する。

期待状態:
- unchecked例外を `catch` できる

### レベル3（実務）
1. `main` に `throws ConfigException` を付け、`catch` しない構成も試す。

期待状態:
- `catch` ではなく `throws` で上位へ委譲できる

---

## 6. つまずきポイント
- checked例外なのに `catch` / `throws` を書かない
  -> コンパイルエラーになる
- unchecked例外を「絶対に処理不要」と誤解
  -> 入力境界では `catch` やバリデーションで制御する
- `throws` を付ける場所を迷う
  -> そのメソッドで回復できない場合に上位へ委譲する
