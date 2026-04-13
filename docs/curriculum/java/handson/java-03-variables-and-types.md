# Java-03 ハンズオン: 変数と型（宣言→代入→参照）

対応参考資料: `Java-03_変数と型.pptx`

## 1. この資料のゴール
- 変数の基本（宣言・代入・参照）を説明できる
- 主要な型（`String`, `int`, `long`, `double`, `boolean`）を使い分けできる
- 実務で通用する変数名（lowerCamelCase）で記述できる

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

---

## 3. 先に覚えるルール
1. 変数は「宣言→代入→参照」の順で使う
2. 変数名は lowerCamelCase（例: `orderCount`, `unitPrice`）
3. 型に合わない値は代入できない

---

## 4. ハンズオン

目的:
- 型と変数を使って実務に近い情報を扱う

完了条件:
- `VariableTypeDemo.java` を実行し、注文情報を表示できる
- `int` と `long` の使い分けを説明できる

作成ファイル: `~/order-management-springboot/practice/java/handson03/VariableTypeDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson03
cd ~/order-management-springboot/practice/java/handson03
```

### Step 1: `int` の宣言→代入→参照
`VariableTypeDemo.java` を次の内容で作成:

```java
public class VariableTypeDemo {
    public static void main(String[] args) {
        int quantity;         // 宣言
        quantity = 3;         // 代入
        System.out.println(quantity); // 参照
    }
}
```

実行:
```bash
javac -encoding UTF-8 VariableTypeDemo.java
java VariableTypeDemo
```

期待出力:
```text
3
```

### Step 2: 実務で使う変数を追加
`VariableTypeDemo.java` を次の内容に更新:

```java
public class VariableTypeDemo {
    public static void main(String[] args) {
        String orderCode = "ORD-2026-0001";
        int quantity = 3;
        int unitPrice = 1200;
        int totalPrice = quantity * unitPrice;
        boolean paid = false;

        System.out.println("注文番号: " + orderCode);
        System.out.println("数量: " + quantity);
        System.out.println("単価: " + unitPrice);
        System.out.println("合計: " + totalPrice);
        System.out.println("支払済み: " + paid);
    }
}
```

実行:
```bash
javac -encoding UTF-8 VariableTypeDemo.java
java VariableTypeDemo
```

期待出力:
```text
注文番号: ORD-2026-0001
数量: 3
単価: 1200
合計: 3600
支払済み: false
```

コード解説:
- `String` は文字列
- `int` は整数（数量・単価・件数など）
- `boolean` は真偽値（状態フラグ）

### Step 3: `long` と `double` を追加
`VariableTypeDemo.java` を次の内容に更新:

```java
public class VariableTypeDemo {
    public static void main(String[] args) {
        String orderCode = "ORD-2026-0001";
        long orderId = 10000000001L; // long は末尾 L を付ける
        int quantity = 3;
        int unitPrice = 1200;
        int totalPrice = quantity * unitPrice;
        double taxRate = 0.10;
        double taxAmount = totalPrice * taxRate;
        boolean paid = false;

        System.out.println("注文番号: " + orderCode);
        System.out.println("注文ID: " + orderId);
        System.out.println("合計: " + totalPrice);
        System.out.println("税額: " + taxAmount);
        System.out.println("支払済み: " + paid);
    }
}
```

実行:
```bash
javac -encoding UTF-8 VariableTypeDemo.java
java VariableTypeDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


コード解説:
- `long` は `int` より大きな整数を扱える（ID向け）
- `double` は小数を扱える（税率など）

### Step 4: 初期化と再代入を確認
`VariableTypeDemo.java` を次の内容に更新:

```java
public class VariableTypeDemo {
    public static void main(String[] args) {
        int quantity = 3; // 初期化（宣言と代入を1行）
        System.out.println("初期数量: " + quantity);

        quantity = 5; // 再代入（上書き）
        System.out.println("再設定後数量: " + quantity);
    }
}
```

実行:
```bash
javac -encoding UTF-8 VariableTypeDemo.java
java VariableTypeDemo
```

期待出力:
```text
初期数量: 3
再設定後数量: 5
```

学習ポイント:
- 再代入すると古い値は上書きされる

---

### 補足: 実務でよく使う型（優先）
| 型 | 主な用途 | 変数名例 |
|---|---|---|
| `String` | コード、名称、メール | `orderCode`, `customerName` |
| `int` | 数量、件数、価格（小規模） | `quantity`, `unitPrice` |
| `long` | 大きいID | `orderId`, `userId` |
| `double` | 小数（比率など） | `taxRate`, `score` |
| `boolean` | true/false 状態 | `paid`, `active` |

---

## 5. ミニ演習（10分）
1. `orderCode` を別値に変更
2. `taxRate` を `0.08` に変更して税額の差分を確認
3. `paid` を `true` に変更して出力確認
4. `int totalPrice` を `long totalPrice` に変えてコンパイル・実行

---

## 6. つまずきポイント
- `cannot find symbol`
  -> 変数名のスペルと大文字小文字を確認
- `incompatible types`
  -> 型と代入値が一致しているか確認
- `integer number too large`
  -> 大きな整数は `long` + `L` を使う
