# Java-17 ハンズオン: 例外

対応参考資料: `Java-17_例外.pptx`

## 1. この資料のゴール
- 例外の基本（`try-catch-finally`）を実装できる
- `throw` を使って入力不正を通知できる
- 例外を握りつぶさず、原因を表示する習慣を身につける

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

---

## 3. 先に覚えるポイント
1. 例外は「通常フローでは処理できない異常」を表す
2. `try` で実行、`catch` で捕捉、`finally` で後処理
3. 不正入力は `throw` で呼び出し元へ通知する

---

## 4. ハンズオン

目的:
- 例外の発生・捕捉・再通知を体験する

完了条件:
- `ExceptionDemo.java` で複数の例外ケースを確認できる

作成ファイル: `~/order-management-springboot/practice/java/handson17/ExceptionDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson17
cd ~/order-management-springboot/practice/java/handson17
```

### Step 1: try-catch-finally を使う
`ExceptionDemo.java` を次の内容で作成:

```java
public class ExceptionDemo {
    public static void main(String[] args) {
        try {
            int value = 10 / 0;
            System.out.println(value);
        } catch (ArithmeticException e) {
            System.out.println("計算エラー: " + e.getMessage());
        } finally {
            System.out.println("後処理を実行");
        }
    }
}
```

実行:
```bash
javac -encoding UTF-8 ExceptionDemo.java
java ExceptionDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: 文字列変換エラーを処理
`ExceptionDemo.java` を次の内容に更新:

```java
public class ExceptionDemo {
    public static void main(String[] args) {
        String input = "abc";
        try {
            int quantity = Integer.parseInt(input);
            System.out.println(quantity);
        } catch (NumberFormatException e) {
            System.out.println("入力値が数値ではありません: " + input);
        }
    }
}
```

実行:
```bash
javac -encoding UTF-8 ExceptionDemo.java
java ExceptionDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 3: throw で不正入力を通知（仕上げ）
`ExceptionDemo.java` を次の内容に更新:

```java
public class ExceptionDemo {
    static int validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity は 1 以上である必要があります");
        }
        return quantity;
    }

    public static void main(String[] args) {
        try {
            int q = validateQuantity(0);
            System.out.println("数量: " + q);
        } catch (IllegalArgumentException e) {
            System.out.println("入力エラー: " + e.getMessage());
        }
    }
}
```

実行:
```bash
javac -encoding UTF-8 ExceptionDemo.java
java ExceptionDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


---

## 5. ミニ演習（10分）
1. `validateQuantity` を `quantity > 1000` もエラーにする
2. `validatePrice(int price)` を追加して0未満を弾く
3. 例外メッセージに入力値を含める

---

## 6. つまずきポイント
- 例外を `catch` しないで終了する
  -> 初学段階ではまず `catch` で可視化する
- `catch (Exception e)` の乱用
  -> まずは具体例外を捕まえる
- 例外メッセージが曖昧
  -> どの値が不正かを明示
