# Java-11 ハンズオン: さまざまなクラス機構（constructor / this / static）

対応参考資料: `Java-11_さまざまなクラス機構.pptx`

## 1. この資料のゴール
- コンストラクタの役割を説明できる
- `this` と `static` の違いを理解できる
- クラス変数とインスタンス変数を使い分けできる

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

---

## 3. 先に覚えるポイント
1. コンストラクタは `new` 時に呼ばれる初期化処理
2. `this` はインスタンス自身、`static` はクラス全体共有
3. `static` メソッドはインスタンスなしで呼べる

---

## 4. ハンズオン

目的:
- クラス機構の基本を実装で理解する

完了条件:
- `ClassMechanismDemo.java` で constructor / this / static を確認できる

作成ファイル: `~/order-management-springboot/practice/java/handson11/ClassMechanismDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson11
cd ~/order-management-springboot/practice/java/handson11
```

### Step 1: コンストラクタで初期化する
`ClassMechanismDemo.java` を次の内容で作成:

```java
class Product {
    String name;
    int price;

    Product(String name, int price) { // コンストラクタ
        this.name = name;
        this.price = price;
    }
}

public class ClassMechanismDemo {
    public static void main(String[] args) {
        Product p = new Product("Laptop", 120000);
        System.out.println(p.name + " / " + p.price);
    }
}
```

実行:
```bash
javac -encoding UTF-8 ClassMechanismDemo.java
java ClassMechanismDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: static フィールドを追加する
`ClassMechanismDemo.java` を次の内容に更新:

```java
class Product {
    static int createdCount = 0; // クラス全体で共有
    String name;
    int price;

    Product(String name, int price) {
        this.name = name;
        this.price = price;
        createdCount++;
    }
}

public class ClassMechanismDemo {
    public static void main(String[] args) {
        new Product("Laptop", 120000);
        new Product("Mouse", 2500);

        System.out.println("生成数: " + Product.createdCount);
    }
}
```

実行:
```bash
javac -encoding UTF-8 ClassMechanismDemo.java
java ClassMechanismDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 3: static メソッドを追加（仕上げ）
`ClassMechanismDemo.java` を次の内容に更新:

```java
class PriceUtil {
    static int calcTaxIncluded(int basePrice) {
        return basePrice * 110 / 100;
    }
}

class Product {
    String name;
    int price;

    Product(String name, int price) {
        this.name = name;
        this.price = price;
    }
}

public class ClassMechanismDemo {
    public static void main(String[] args) {
        Product p = new Product("Keyboard", 5000);
        int taxed = PriceUtil.calcTaxIncluded(p.price);
        System.out.println(p.name + " 税込: " + taxed);
    }
}
```

実行:
```bash
javac -encoding UTF-8 ClassMechanismDemo.java
java ClassMechanismDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


---

## 5. ミニ演習（10分）
1. `Product` に `quantity` を追加し、コンストラクタで初期化
2. `PriceUtil` に割引計算メソッドを追加
3. `createdCount` を表示するサンプルを再追加

---

## 6. つまずきポイント
- コンストラクタ名がクラス名と一致していない
  -> 返り値なし・クラス名一致を確認
- `non-static variable ... cannot be referenced from a static context`
  -> `static` とインスタンス変数の区別を確認
- `this` を `static` メソッドで使ってしまう
  -> `this` はインスタンスメソッド/コンストラクタのみ
