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

期待状態:
- `java -version` と `javac -version` の両方で `17` が表示される
- 例: `17.0.x`

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
import java.util.Objects; // null 安全な比較ユーティリティ

class Product { // 商品クラス
    String code; // 商品コード

    Product(String code) { // コンストラクタ
        this.code = code; // フィールド初期化
    }

    @Override
    public String toString() { // 表示用文字列を返す
        return "Product{code='" + code + "'}";
    }

    @Override
    public boolean equals(Object obj) { // 値の同一性比較を定義
        if (this == obj) { // 同じ参照なら true
            return true;
        }
        if (!(obj instanceof Product other)) { // Product 以外は false
            return false;
        }
        return Objects.equals(code, other.code); // code の値を比較
    }
}

public class StandardClassDemo { // 実行クラス
    public static void main(String[] args) {
        Product p1 = new Product("P-001"); // 同じ code のインスタンス1
        Product p2 = new Product("P-001"); // 同じ code のインスタンス2
        Product p3 = new Product("P-999"); // 異なる code のインスタンス

        System.out.println(p1.toString()); // toString の結果表示
        System.out.println("p1 equals p2: " + p1.equals(p2)); // true 期待
        System.out.println("p1 equals p3: " + p1.equals(p3)); // false 期待
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 StandardClassDemo.java
java StandardClassDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する

コード解説:
- `toString` は表示用文字列を返す
- `equals` は「同じ値か」を比較するためにオーバーライドする


### Step 2: ラッパークラスを使う
`StandardClassDemo.java` を次の内容に更新:

```java
public class StandardClassDemo { // ラッパークラス利用例
    public static void main(String[] args) {
        String quantityText = "25"; // 数値文字列
        int quantity = Integer.parseInt(quantityText); // 文字列を int へ変換

        Integer boxed = quantity; // オートボクシング: int -> Integer
        int unboxed = boxed; // アンボクシング: Integer -> int

        System.out.println("quantity: " + quantity); // int 値を表示
        System.out.println("boxed: " + boxed); // Integer 値を表示
        System.out.println("unboxed: " + unboxed); // 再び int 化した値を表示
    } // main メソッドの終わり
} // クラス定義の終わり
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
public class StandardClassDemo { // StringBuilder 利用例
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder(); // 可変文字列バッファを作成
        sb.append("受注ID=").append("ORD-1001").append(", "); // 文字列を連結
        sb.append("数量=").append(3).append(", "); // 数値もそのまま連結できる
        sb.append("状態=").append("PAID"); // 最後の項目を連結

        String logLine = sb.toString(); // 完成した文字列へ変換
        System.out.println(logLine); // ログ1行を表示
    } // main メソッドの終わり
} // クラス定義の終わり
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
3. `Product` に `hashCode` も実装し、`HashSet` で重複判定を確認

---

## 6. つまずきポイント
- `NumberFormatException`
  -> 数値変換前に入力値を確認
- `==` でラッパー比較してしまう
  -> 値比較は `equals`
- `String` の連結が多すぎて読みにくい
  -> `StringBuilder` を利用

