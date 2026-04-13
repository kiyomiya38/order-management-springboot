# Java-16 ハンズオン: Javaを支える標準クラス

対応参考資料: `Java-16_Javaを支える標準クラス.pptx`

## 1. この資料のゴール
- `Object` の基本メソッド（`toString`, `equals`）を理解する
- ラッパークラス（`Integer`, `Double`）を使える
- `StringBuilder` で文字列連結を効率化できる

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

---

## 3. 先に覚えるポイント
1. 全クラスは `Object` を継承する
2. 基本型とラッパー型は相互変換される（オートボクシング）
3. 文字列連結が多いときは `StringBuilder` が有効

---

## 4. ハンズオン

目的:
- 標準クラスの実務利用を体験する

完了条件:
- `StandardClassDemo.java` で `Object` / ラッパー / `StringBuilder` を確認できる

作成ファイル: `~/order-management-springboot/practice/java/handson16/StandardClassDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson16
cd ~/order-management-springboot/practice/java/handson16
```

### Step 1: Objectメソッドを使う
`StandardClassDemo.java` を次の内容で作成:

```java
class Product {
    String code;

    Product(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Product{code='" + code + "'}";
    }
}

public class StandardClassDemo {
    public static void main(String[] args) {
        Product p = new Product("P-001");
        System.out.println(p.toString());
    }
}
```

実行:
```bash
javac -encoding UTF-8 StandardClassDemo.java
java StandardClassDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: ラッパークラスを使う
`StandardClassDemo.java` を次の内容に更新:

```java
public class StandardClassDemo {
    public static void main(String[] args) {
        String quantityText = "25";
        int quantity = Integer.parseInt(quantityText); // 文字列 -> int

        Integer boxed = quantity; // オートボクシング
        int unboxed = boxed;       // アンボクシング

        System.out.println("quantity: " + quantity);
        System.out.println("boxed: " + boxed);
        System.out.println("unboxed: " + unboxed);
    }
}
```

実行:
```bash
javac -encoding UTF-8 StandardClassDemo.java
java StandardClassDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 3: StringBuilder を使う（仕上げ）
`StandardClassDemo.java` を次の内容に更新:

```java
public class StandardClassDemo {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("受注ID=").append("ORD-1001").append(", ");
        sb.append("数量=").append(3).append(", ");
        sb.append("状態=").append("PAID");

        String logLine = sb.toString();
        System.out.println(logLine);
    }
}
```

実行:
```bash
javac -encoding UTF-8 StandardClassDemo.java
java StandardClassDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


---

## 5. ミニ演習（10分）
1. `Integer.parseInt` に不正文字列を渡したときの挙動を確認
2. `StringBuilder` で3行分のログを作る
3. `Product` に `equals` を実装して比較結果を確認

---

## 6. つまずきポイント
- `NumberFormatException`
  -> 数値変換前に入力値を確認
- `==` でラッパー比較してしまう
  -> 値比較は `equals`
- `String` の連結が多すぎて読みにくい
  -> `StringBuilder` を利用
